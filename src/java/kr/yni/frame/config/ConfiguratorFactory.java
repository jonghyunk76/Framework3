package kr.yni.frame.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.Constants;
import kr.yni.frame.config.impl.PropertiesConfiguratorImpl;

/**
 * classes:package.config 경로에 포함된 properties파일을 관리하는 패키지 클래스
 * @author YNI-maker
 *
 */
public class ConfiguratorFactory {
	
	protected static Log log = LogFactory.getLog(ConfiguratorFactory.class);
			
	/**
     * <p>
     * <strong>ConfiguratorFactory</strong> static 인스턴스.
     * </p>
     */
    private static ConfiguratorFactory factorySingleton;
    
	/**
     * <p>
     * <strong>Configurator</strong> 단일(Single) 인스턴스 패턴 구현을 위한 static 인스턴스.
     * </p>
     */
    private static Configurator configurator;
    
    /**
     * <p>
     * <strong>Configurator</strong> 를 담을 Map 객체
     * </p>
     */
    private static Map<String, Configurator> configMap;
	
	 /**
     * <p>
     * 단일(Single) 인스턴스 패턴 구현을 위한 <code>private</code> 컨스트럭터(Constructor).
     * </p>
     */
    private ConfiguratorFactory() throws ConfiguratorException {
        initialize();
    }
    
    /**
     * <p>
     * <strong>ConfiguratorFactory</strong>의 단일(Single) 인스턴스 패턴의 구현을 위한 synchronized static 인스턴스 Method.
     * </p>
     *
     * @param properties 타입명(default=app), 명칭은 xxx_resource.properties형식으로 앞 3자리 문자만 입력할 것
     * @return 단일(Single) 인스턴스 패턴의 <code>ConfiguratorFactory</code> 인스턴스.
     * @throws ConfiguratorException initialize 시 해당 구성파일을 읽을 때 오류가 발생 하는경우:<code>java.io.IOException</code>.
     */
    public synchronized static ConfiguratorFactory getInstance() throws ConfiguratorException {
    	if (factorySingleton == null) {
            factorySingleton = new ConfiguratorFactory();
        }
    	
        return factorySingleton;
    }
    
    /**
     * <p>
     * <strong>ConfiguratorFactory</strong> 단일(Single) 인스턴스 내부에서 <strong>Configurator</strong>의 단일(Single)
     * 인스턴스 패턴의 구현을 위한 initialize Method. 구성 파일의 변경 시 Method를 사용하면 구성 파일을 다시 재로딩 할 수 있다.
     * </p>
     *
     * @throws ConfiguratorException 
     */
    public synchronized void initialize() throws ConfiguratorException {
    	if(configMap == null) {
    		configMap = new LinkedHashMap<String, Configurator>();
    	}
    }
    
    public synchronized static void removeInstance() {
    	if (configMap != null) {
    		configMap.clear();
    	}
    	if (factorySingleton != null) {
    		factorySingleton = null;
    	}
    }
    
    /**
     * <p>
     * <strong>ConfiguratorFactory</strong> 단일(Single) 인스턴스 내부에서 <strong>Configurator</strong>의 단일(Single)
     * 인스턴스을 얻기 위한 Method.
     * </p>
     *
     * @return 단일(Single) 인스턴스 패턴의 <code>Configurator</code> 인스턴스.
     */
    public Configurator getConfigurator(String configName)  throws ConfiguratorException {
    	configurator = configMap.get(configName);
    	
    	if(configurator == null) {
    		configurator = new PropertiesConfiguratorImpl(configName);
    		
    		configurator.doConfigure();
    		
    		configMap.put(configName, configurator);
    		
    		if(log.isDebugEnabled()) log.debug("create new PropertiesConfiguratorImpl class(" + configName + Constants.PROPERTIES_PREFIX_NAME + ")");
    	}
    	
        return configurator;
    }
    
    public Configurator getConfigurator()  throws ConfiguratorException {
    	return this.getConfigurator("app");
    }

}
