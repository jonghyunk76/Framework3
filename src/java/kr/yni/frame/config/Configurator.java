package kr.yni.frame.config;

public interface Configurator {
	/**
	 * <p>
	 * System에서 Properties정보를 받아 메모리에 적재하는 초기화 로직.
	 * </p>
	 * 
	 * @throws MissingResourceException
	 */
	void doConfigure() throws ConfiguratorException;
	
	/**
	 * <p>
	 * 시스템 프로퍼티 또는 <strong>Configurator</strong>에 의해 메모리에 로드된 configuration
	 * file의 원하고자 하는 특정 key를 인자로 하여 해당 Value를 String형으로 리턴한다.
	 * </p>
	 * 
	 * @param key
	 *            원하고자하는 key값
	 * @return 원하고자하는 key값에 해당하는 value
	 * @throws MissingResourceException
	 *             key값이 configuration file에 존재하지 않을 경우 발생
	 */
	String getString(String key) throws ConfiguratorException;

	/**
	 * <p>
	 * 시스템 프로퍼티 또는 <strong>Configurator</strong>에 의해 메모리에 로드된 configuration
	 * file의 원하고자 하는 특정 key를 인자로 하여 해당 Value를 String형으로 리턴한다.
	 * </p>
	 * 
	 * @param key
	 *            원하고자하는 key값
	 * @param defaultValue
	 *            값이 없을 경우 default값
	 * @return 원하고자하는 key값에 해당하는 value
	 */
	String getString(String key, String defaultValue) throws ConfiguratorException;

	/**
	 * <p>
	 * 시스템 프로퍼티 또는 <strong>Configurator</strong>에 의해 메모리에 로드된 configuration
	 * file의 원하고자 하는 특정 key를 인자로 하여 해당 Value를 int형으로 리턴한다.
	 * </p>
	 * 
	 * @param key
	 *            원하고자하는 key값
	 * @return 원하고자하는 key값에 해당하는 value
	 * @throws MissingResourceException
	 *             key값이 configuration file에 존재하지 않을 경우 발생
	 */
	int getInt(String key) throws ConfiguratorException;

	/**
	 * <p>
	 * 시스템 프로퍼티 또는 <strong>Configurator</strong>에 의해 메모리에 로드된 configuration
	 * file의 원하고자 하는 특정 key를 인자로 하여 해당 Value를 int형으로 리턴한다.
	 * </p>
	 * 
	 * @param key
	 *            원하고자하는 key값
	 * @return 원하고자하는 key값에 해당하는 value
	 * @throws MissingResourceException
	 *             key값이 configuration file에 존재하지 않을 경우 발생
	 */
	int getInt(String key, String defaultValue) throws ConfiguratorException;
	
	/**
     * 설정 정보 clear
     */
    public void clear();
}
