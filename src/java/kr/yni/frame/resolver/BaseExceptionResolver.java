package kr.yni.frame.resolver;

import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import kr.yni.frame.context.ApplicationContextFactory;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.util.StringHelper;

/**
 * Session Exception Resolver
 * 
 * </pre>
 */
public class BaseExceptionResolver implements HandlerExceptionResolver {
	private static final Log log = LogFactory.getLog(BaseExceptionResolver.class);
	
	private String view;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	public void setView(String view) {
		this.view = view;
	}

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object obj, Exception exception) {
		String message = this.createExceptionMessge(request, exception);

		log.debug(" exception start");

		if (exception instanceof Exception) { // 지정된 예외처리
			log.debug("etc.Exception Message = " + message);
			request.setAttribute("exception", message);
		} else { // 지정되지 않은 예외처리
			if(exception instanceof MaxUploadSizeExceededException) {
				message = this.getMessage("MSG_FILE_LIMITE_SIZE", null);
				
				request.setAttribute("exception", message);
			} else {
				request.setAttribute("exception", message); // 메시지를 request객체에 실어서 보내게 수정(김종현, 2011-03-11)
			}
		}

		log.debug("ResolveException exception("+message+") end");
		
		return new ModelAndView(view);
	}

	public String createExceptionMessge(HttpServletRequest request, Exception exception) {
		exception.printStackTrace();

		SessionLocaleResolver localeResolver = (SessionLocaleResolver) ApplicationContextFactory.getAppContext().getBean("localeResolver");
		String message = null;

		if (exception instanceof FrameException) {
			try {
				// locale 정보를 session 에서 찾도록 수정(김종현, 2011-03-11)
				String locale = ((FrameException) exception).getLocale();

				if (locale == null) {
					locale = StringHelper.null2void(localeResolver.resolveLocale(request).getLanguage());
				}
				log.debug("resolveException's locale = " + locale);

				message =  ((FrameException) exception).getMessage();
			} catch (Exception messageException) {
				messageException.printStackTrace();
				message = messageException.getMessage();
			}

			message = StringUtils.defaultIfEmpty(message, exception.getMessage());
		} else {
			if (null == exception.getCause()) {
				message = exception.getMessage();
				if(null == message){
					message = "\n" + exception.getClass().getName();
					StackTraceElement[] elem = exception.getStackTrace();
					message += "\n"+elem[0]; 
				}
			} else {
				message = exception.getCause().toString();
				message = message.replaceAll("\n", " ");
			}
		}

		return message;
	}

	protected String getMessage(String messageKey, Object messageParameters[]) {
		return getMessage(messageKey, messageParameters, null);
	}

	protected String getMessage(String messageKey, Object messageParameters[], Locale locale) {
		return messageSource.getMessage(messageKey, messageParameters, null, locale);
	}
}
