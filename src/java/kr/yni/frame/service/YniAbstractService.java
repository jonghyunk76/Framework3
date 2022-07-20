package kr.yni.frame.service;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import kr.yni.frame.util.SystemHelper;

/**
 * Service 구현 시 지원될 기능을 구현한 클래스
 * 
 * @author YNI-maker
 *
 */
public class YniAbstractService {
	protected Log log = LogFactory.getLog(this.getClass());
	
	@Autowired 
	protected Properties properties;
	
	@Resource(name = "messageSource")
	protected MessageSource messageSource;
	
	/**
	 * key에 해당하는 메시지를 구한다.
	 * 
	 * @param messageKey
	 * @return
	 */
	protected String getMessage(String messageKey) {
		return getMessage(messageKey, null, null);
	}
	
	/**
	 * key에 해당하는 메시지를 구한다.
	 * 
	 * @param messageKey
	 * @param messageParameters 맵핑할 인자
	 * @return
	 */
	protected String getMessage(String messageKey, Object messageParameters[]) {
		return getMessage(messageKey, messageParameters, null);
	}
	
	/**
	 * key에 해당하는 메시지를 구한다.
	 * 
	 * @param messageKey
	 * @param messageParameters 맵핑할 인자
	 * @param locale 언어(KOR, ENG, LOC)
	 * @return
	 */
	protected String getMessage(String messageKey, Object messageParameters[], String locale) {
		Locale rLocale = SystemHelper.getLocale(locale);
		
		return messageSource.getMessage(messageKey, messageParameters, null, rLocale);
	}
}
