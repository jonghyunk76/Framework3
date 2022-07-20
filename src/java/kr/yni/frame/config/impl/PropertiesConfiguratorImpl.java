package kr.yni.frame.config.impl;

import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.Constants;
import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorException;

/**
  * <p>
 * <strong>Configurator</strong>의 Property로 구성된 파일을 처리하는 구현 클래스이다.
 * Property 형태로 구성 파일을 정의할 때 "${" 와 "}" 로 둘러싸인 형태의 참조변수 정의가 가능하다.
 * 참조 변수는 실제 값으로 치환되어 반환된다.
 * </p>
 * 
 * @author YNI-maker
 * @since 2013. 5. 9. 오전 10:38:17
 * @version 1.0
 *
 * @see
 * YNI-maker 2013. 5. 9. Initial version
 *
 */
public class PropertiesConfiguratorImpl implements Configurator {
	
	protected static Log log = LogFactory.getLog(PropertiesConfiguratorImpl.class);

	/**
	 * <p>
     * properties 파일 경로
     * </p>
     */
	private final static String DEFAULT_APP_RESOURCE_PATH = "config";

	/**
	 * <p>
     * JCO 리소스 설정 파일 prefix명<br>
     * (defualt = app_resource)
     * </p>
     */
//	private String APP_RESOURCE_NAME = "app_resource";
	private String APP_RESOURCE_NAME = "app_";
	
	/**
	 * <p>
     * Framework의 구성 파일을 메모리에 적재하기 위한 ResourceBundle 오브젝트
     * </p>
     */
	protected ResourceBundle appResourceBundle;
	
	/**
	 * <p>
	 * properties 소스 경로
	 * </p>
	 */
	private static String complianceResource;
	
	static {
		// 시스템내 환경설정를 검색
		Properties prop = System.getProperties();
		complianceResource = prop.getProperty("complianceResource");
		
		if (complianceResource == null) {
			complianceResource = DEFAULT_APP_RESOURCE_PATH;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Properties's Dectory : " + complianceResource);
		}
	}
	
	public PropertiesConfiguratorImpl(String configName) {
		this.setPropertiesName(configName + Constants.PROPERTIES_PREFIX_NAME);
	}
	
	public void doConfigure() throws ConfiguratorException {
		try {
			this.clear();
			
			if(log.isDebugEnabled()) log.debug("new load file = " + DEFAULT_APP_RESOURCE_PATH + "." + APP_RESOURCE_NAME);
			
			appResourceBundle = ResourceBundle.getBundle(DEFAULT_APP_RESOURCE_PATH + "." + APP_RESOURCE_NAME);
		} catch (MissingResourceException e) {
			throw new ConfiguratorException("There is no properties.");
		}
	}
	
	public void setPropertiesName(String name) {
		this.APP_RESOURCE_NAME = name;
	}
	
	public String getPropertiesName() {
		return this.APP_RESOURCE_NAME;
	}
	
	public String getString(String key) throws ConfiguratorException {
		String retStr = null;
		
		try {
			retStr = appResourceBundle.getString(key);
//			if(log.isInfoEnabled()) log.info("ResourceBundle before = " + retStr);
			retStr = new String(retStr.getBytes("8859_1"), "KSC5601");
//			if(log.isInfoEnabled()) log.info("ResourceBundle after = " + retStr);
		} catch(Exception e) {
			if(log.isErrorEnabled()) log.error("[ exception = " + e.getMessage() + "] can't find " + key);
		}
		
		return retStr;
	}

	public String getString(String key, String defaultValue) throws ConfiguratorException {
		String retStr = null;
		
		try {
			retStr = (appResourceBundle.getString(key) != null) ? this.getString(key) : defaultValue;
//			if(log.isInfoEnabled()) log.info("ResourceBundle before = " + retStr);
			retStr = new String(retStr.getBytes("8859_1"), "KSC5601");
//			if(log.isInfoEnabled()) log.info("ResourceBundle after = " + retStr);
		} catch(Exception e) {
			if(log.isErrorEnabled()) log.error("[ exception = " + e.getMessage() + "] can't find " + key);
		}
		
		return retStr;
	}

	public int getInt(String key) throws ConfiguratorException {
		return new Integer(appResourceBundle.getString(key));
	}

	public int getInt(String key, String defaultValue) throws ConfiguratorException {
		return (appResourceBundle.getString(key) != null) ? this.getInt(key) : Integer.parseInt(defaultValue);
	}
	
	@SuppressWarnings("static-access")
	public void clear() {
		appResourceBundle.clearCache();
    }
}
