package kr.yni.frame.controller;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import kr.yni.frame.Constants;
import kr.yni.frame.resolver.BaseExceptionResolver;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.util.SystemHelper;

/**
 * 
 * @author YNI-maker
 *
 */
public abstract class YniAbstractController {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private String USER_AUTH = "admin"; // normal:�씪諛� �궗�슜�옄, admin:愿�由ъ옄 沅뚰븳
	
	@Autowired 
	protected Properties properties;
	
	@Resource(name = "messageSource")
	protected MessageSource messageSource;
	
	public YniAbstractController() { }

	/**
	 * message_locale.properties�뿉 �벑濡앸맂 硫붿떆吏�瑜� 援ы븳�떎.
	 * 
	 * @param messageKey 硫붿떆吏� 肄붾뱶
	 * @return String
	 * @exception Exception
	 */
	protected String getMessage(String messageKey)
			throws Exception {
		return messageSource.getMessage(messageKey, null, null);
	}

	/**
	 * message_locale.properties�뿉 �벑濡앸맂 硫붿떆吏�瑜� 援ы븳�떎.
	 * 
	 * @param messageKey 硫붿떆吏� 肄붾뱶
	 * @param messageParameters 留듯븨�븷 諛곗뿴
	 * @return String
	 * @exception Exception
	 */
	protected String getMessage(String messageKey, String messageParameters[])
			throws Exception {
		return messageSource.getMessage(messageKey, messageParameters, null);
	}
	
	/**
	 * message_locale.properties�뿉 �벑濡앸맂 硫붿떆吏�瑜� 援ы븳�떎.
	 * 
	 * @param messageKey 硫붿떆吏� 肄붾뱶
	 * @param messageParameters 留듯븨�븷 諛곗뿴
	 * @param locale �뼵�뼱(KOR, ENG, LOC)
	 * @return String
	 * @exception Exception
	 */
	protected String getMessage(String messageKey, String messageParameters[],
			String locale) throws Exception {
		Locale rLocale = SystemHelper.getLocale(locale);
		
		return messageSource.getMessage(messageKey, messageParameters, rLocale);
	}

	/**
	 * Get Process Message
	 * 
	 * @param parmOne
	 *            messageKey
	 * @param paramTwo
	 *            messageParameters
	 * @return String
	 * @exception Exception
	 */
	protected String getProcessMssage(HttpSession session, String messageArgKey)
			throws Exception {
		return getMessage("MSG_SUCCESS_PROCESS",
				new String[] { getMessage(messageArgKey, null, Constants.DEFAULT_LANGUAGE) },
									  Constants.DEFAULT_LANGUAGE);

	}

	/**
	 * Get Save Message
	 * 
	 * @param parmOne
	 *            messageKey
	 * @param paramTwo
	 *            messageParameters
	 * @return String
	 * @exception Exception
	 */
	protected String getSaveSuccessMsg(HttpSession session) throws Exception {
		return getProcessMssage(session, "MSG_SAVE");
	}

	/**
	 * Get Modify Message
	 * 
	 * @param parmOne
	 *            messageKey
	 * @param paramTwo
	 *            messageParameters
	 * @return String
	 * @exception Exception
	 */
	protected String getModifySuccessMsg(HttpSession session) throws Exception {
		return getProcessMssage(session, "MSG_MODIFY");
	}

	/**
	 * Get Delete Message
	 * 
	 * @param parmOne
	 *            messageKey
	 * @param paramTwo
	 *            messageParameters
	 * @return String
	 * @exception Exception
	 */
	protected String getDeleteSuccessMsg(HttpSession session) throws Exception {
		return getProcessMssage(session, "MSG_DELETE");
	}

	/**
	 * Get Save Message
	 * 
	 * @param parmOne
	 *            messageKey
	 * @param paramTwo
	 *            messageParameters
	 * @return String
	 * @exception Exception
	 */
	protected String getOriginDeterminSuccessMsg(HttpSession session)
			throws Exception {
		return getProcessMssage(session, "MSG_ORIGIN_SUCESS");
	}

	/**
	 * Get Save Message
	 * 
	 * @param parmOne
	 *            messageKey
	 * @param paramTwo
	 *            messageParameters
	 * @return String
	 * @exception Exception
	 */
	protected String getPreDeterminSuccessMsg(HttpSession session)
			throws Exception {
		return getProcessMssage(session, "MSG_PRE_SUCESS");
	}

	/**
	 * Get Save Message
	 * 
	 * @param parmOne
	 *            messageKey
	 * @param paramTwo
	 *            messageParameters
	 * @return String
	 * @exception Exception
	 */
	protected String getSimulationSuccessMsg(HttpSession session)
			throws Exception {
		return getProcessMssage(session, "MSG_SIMULATION_SUCESS");
	}
	

	/**
	 * 
	 * @param session
	 * @param messageArgKey
	 * @return
	 * @throws Exception
	 */
	protected String getReturnMsg(HttpSession session, String messageArgKey)
			throws Exception {
		return getProcessMssage(session, messageArgKey);
	}	
	
	/**
	 * �삁�쇅泥섎━ 硫붿떆吏�瑜� 議고쉶�븳�떎.
	 * 
	 * @param request �슂泥��젙蹂�
	 * @param exception �삁�쇅�궗�빆
	 * @return �삁�쇅硫붿꽭吏�
	 */
	protected String getExceptionMessage(HttpServletRequest request,
			Exception exception) {
		return getExceptionMessage(request, exception, null);
	}
			
	/**
	 * �삁�쇅泥섎━ 硫붿떆吏�瑜� 議고쉶�븳�떎.
	 * 
	 * @param request �슂泥��젙蹂�
	 * @param exception �삁�쇅�궗�빆
	 * @param messageKey 硫붿떆吏� 肄붾뱶
	 * @return �삁�쇅硫붿꽭吏�
	 */
	protected String getExceptionMessage(HttpServletRequest request,
			Exception exception, String messageKey) {
		String returnMessage = null;
		Locale rLocale = SystemHelper.getLocale(null);
		// 硫붿떆吏� 援ы븯湲�
		try {
			if(!StringHelper.isNull(messageKey)) {
				returnMessage = messageSource.getMessage(messageKey, null, rLocale); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(this.USER_AUTH.equals("admin")) {
			// Exception 硫붿떆吏� 援ы븯湲�
			BaseExceptionResolver exceptionResolver = new BaseExceptionResolver();
			String exp = exceptionResolver.createExceptionMessge(request, exception);
			returnMessage = returnMessage + "<br><br>"+ exp;
		}
		return returnMessage;
	}
	
    protected String getURIBaseName(HttpServletRequest p_req) {
    	String url = StringUtils.replace(p_req.getRequestURI() + "^^",".do^^","");
    	url = StringUtils.replace(url,p_req.getContextPath(),"");
    	return url;
    }
    
    protected String getBaseName(HttpServletRequest p_req) {
    	String l_url = p_req.getRequestURI();
    	int l_start = l_url.lastIndexOf("/")+1;
    	return StringUtils.replace(l_url.substring(l_start) + "^^",".do^^","");
    }
}