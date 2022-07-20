package kr.yni.frame.resources;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import kr.yni.frame.Constants;
import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorFactory;
import kr.yni.frame.context.ApplicationContextFactory;
import kr.yni.frame.util.JsonUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.util.SystemHelper;

/**
 * MessageResource
 * 
 * @author YNI-maker
 *
 */
public class MessageResource {
	private static final Log log = LogFactory.getLog(MessageResource.class);
	
	/*
	 * ApplicationContext
	 */
	public ApplicationContext applicationContext;

	/*
	 * Message가 담긴 LinkedHashMap
	 */
	private ResourceMap resource;

	/*
	 * 공유 Message instance
	 */
	private static MessageResource message;

	public MessageResource() { }

	/**
	 * 메세지 정보를 초기화 한다. MessageServiceImpl.setApplicationContext 에서 호출됨
	 * 
	 * @param ApplicationContextFactory
	 * @exception
	 * @see
	 */
	public static void initMessageInstance(ApplicationContext applicationContext) {
		initMessageInstance(applicationContext, "message");
	}

	/**
	 * 메세지 정보를 초기화 한다. MessageServiceImpl.setApplicationContext 에서 호출됨
	 * 
	 * @param ApplicationContextFactory
	 * @exception
	 * @see
	 */
	public static void clearMessageResource() {
		if (message != null) {
			message.getResource().clear();
		}
	}

	/**
	 * 메세지 정보를 초기화 한다. MessageServiceImpl.setApplicationContext 에서 호출됨
	 * 
	 * @param ApplicationContextFactory
	 * @exception
	 * @see
	 */
	private static void initMessageInstance(ApplicationContext applicationContext, String resouceName) {
		if (message == null) {
			message = new MessageResource();
		}
		message.setApplicationContext(applicationContext);

		if (message.getResource() == null) {
			message.setResourceMap(new ResourceMap(resouceName));
		}
	}

	/**
	 * 공유 Message instance 를 가져온다.
	 * 
	 * @return MessageResource
	 * @exception
	 * @see
	 */
	public static MessageResource getMessageInstance() {
		if(message == null) {
			initMessageInstance(ApplicationContextFactory.getAppContext());
		}
		
		return message;
	}

	/**
	 * Message instance 메세지 Map을 가져온다.
	 * 
	 * @return ResourceMap
	 * @exception
	 * @see
	 */
	@SuppressWarnings("unused")
	private ResourceMap getResourceInstance() {
		// initMessageInstance();
		return getResource();
	}

	/**
	 * Message 내용을 가져온다.
	 * 
	 * @param messageCode
	 * @return ResourceMap
	 * @exception
	 * @see
	 */
	public String getMessage(String messageCode) {
		return this.getMessage(messageCode, null, null);
	}

	/**
	 * Message 내용을 가져온다
	 * 
	 * @param messageCode
	 * @param massageArgs
	 * @return ResourceMap
	 * @exception
	 * @see
	 */
	public String getMessage(String messageCode, Object[] massageArgs) {
		return this.getMessage(messageCode, massageArgs, null);

	}

	/**
	 * 다국어를 지원하는 메시지를 생성한다.
	 * - 스페인어(ES)는 추후 번역작업 시 정상적으로 등록하기 위해 현재는 과거 메시지코드를 그대로 사용함(2020.05.06) 
	 * - 통관 시스템의 메시지는 CC_+메시지로 규정하고 실제 다국어 표시할 경우 CC_를 제거함(메시지 다국어 처리 시 CC_로 검색하면 됨)
	 * 
	 * @param messageCode
	 * @param massageArgs
	 * @param locale
	 * @return ResourceMap
	 * @exception
	 * @see
	 */
	public String getMessage(String messageCode, Object[] massageArgs, Object locale) {
		String orgmsgcode = new String(messageCode);
		String message = "";
		
		try {
			Locale rLocale = null;
			if(locale instanceof String) {
				rLocale = SystemHelper.getLocale(locale.toString());
			} else if(locale instanceof Locale) {
				rLocale = (Locale) locale;
			} else {
				rLocale = SystemHelper.getLocale(null);
			}
//			log.debug("messageCode before = " + messageCode);
			
			if(!("es" == rLocale.getLanguage() || rLocale.getCountry() == "ES") && !messageCode.startsWith("CC_")) { // 스페인어는 나중에 번역작업 후 등록할 예정임
				if(messageCode.startsWith("SS_")) {
					messageCode = StringHelper.replace(messageCode, "SS_", "");
				} else {
					// 메시지 코드를 변환한다.(2020-03-03)
					// 쉼표는 under bar로 표시하고 공백은 제거한다.
					// 특수문자는 under bar + 아스키 코드로 변환한다.
					if(Constants.APPLICATION_CHANGE_MSGCODE) {
						messageCode = StringHelper.replace(StringHelper.replace(messageCode, ",", "_"), " ", "");
						messageCode = StringHelper.changeCode4Message(messageCode);
					}
				}
				
//				log.debug("messageCode after = " + messageCode);
				
				MessageSource messageSource = (MessageSource) applicationContext.getBean("messageSource");
				message = messageSource.getMessage(messageCode, massageArgs, null, rLocale);
			} else if(messageCode.startsWith("CC_")) {
				message = StringHelper.replace(messageCode, "CC_", "");
	    	}
			
			String nation = rLocale.getCountry();
        	if(nation == null || nation.isEmpty()) {
        		if(Locale.ENGLISH == rLocale || rLocale.getCountry() == "EN") {
        			nation = "EN";
        		} else if("es" == rLocale.getLanguage() || rLocale.getCountry() == "ES") {
        			nation = "ES";
        		} else if("vi" == rLocale.getLanguage() || rLocale.getCountry() == "VN") {
        			nation = "VN";
        		} else if(Locale.KOREA == rLocale || Locale.KOREAN == rLocale || rLocale.getCountry() == "KR" || rLocale.getCountry() == "KO") {
        			nation = "KR";
        		}
        	}
        	
			Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			String cword = configurator.getString("CONVERSION_WORD_"+nation);
			
			// 특정 문자열을 변경(김종현, 2019-05-16)
			if(cword != null && !cword.isEmpty()) {
				List clist = JsonUtil.getList(cword);
				
//				log.debug("1.conversion word = " + cword + ", nation = " + nation);
				
				if(clist.size() > 0) {
					for(int i = 0; i < clist.size(); i++) {
						Map cMap = (Map) clist.get(i);
						String name = StringHelper.null2void(cMap.get("name"));
						String word = StringHelper.null2void(cMap.get("word"));
						
						message = message.replace(name, word);
					}
					
//					log.debug("2.conversion message = " + message);
				}
			}
			
			if (message == null) {
				message = "message code is not defined (" + messageCode + ")";
				if(log.isErrorEnabled()) log.error(message);
			}
		} catch (Exception e) {
			message = "Message Resouce Exception " + e.toString();
			if(log.isErrorEnabled()) log.error(e);
		}
		
		return message;
	}
	
	/**
	 * Locale 적용된 Message 내용을 가져온다
	 * 
	 * @param list
	 *            LinkedHashMap
	 * @param messageCode
	 * @param massageArgs
	 * @param locale
	 * @return ResourceMap
	 * @exception
	 * @see
	 */
	public String getLocaleMessage(LinkedHashMap<String, Object> list,
		String messageCode, Object[] massageArgs, String locale) {
		String messageTxt;

		// 한글....
		if ("KR".equals(locale)) {
			messageTxt = StringHelper.null2void(list.get("MESSAGE_KR"));
		} else if ("EN".equals(locale)) {
			messageTxt = StringHelper.null2void(list.get("MESSAGE_EN"));
		} else {
			messageTxt = StringHelper.null2void(list.get("MESSAGE_LOC"));
		}
		
//		if(log.isDebugEnabled()) {
//			log.debug(locale + "'s message = " + messageTxt);
//		}
		return (massageArgs != null) ? new MessageFormat(messageTxt).format(massageArgs) : messageTxt;
	}

	public void setResourceMap(ResourceMap resource) {
		this.resource = resource;
	}

	/**
	 * @return the resource
	 */
	public ResourceMap getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void setResource(ResourceMap resource) {
		this.resource = resource;
	}

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @param applicationContext
	 *            the applicationContext to set
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
