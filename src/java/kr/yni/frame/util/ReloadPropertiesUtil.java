package kr.yni.frame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;



public class ReloadPropertiesUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadPropertiesUtil.class);

	private static Properties propertyMap = new Properties();

	/** Properties file **/
	private List<String> propertyFiles;

	public void setPropertyFiles(List<String> propertyFiles) {
		this.propertyFiles = propertyFiles;
	}

	/**
	 * 
	 *<pre>
	 * 1.Description: Load Property file when the context is loaded
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @throws Exception
	 */
	public void init() throws Exception {
		generateProperties();
	}

	/**
	 *<pre>
	 * Reload Properties file
	 *</pre>
	 * @throws Exception
	 */
	public void reload() throws Exception {
		synchronized(this) {
			propertyMap.clear();
			generateProperties();
		}
	}

	@SuppressWarnings("unused")
	private void generateProperties() throws Exception {
		for (String fileName : propertyFiles) {
			String path = getClass().getResource("/").getPath() + fileName;
			InputStream fis = new FileInputStream(path);

			if (fis == null) {
				LOGGER.error("{} file does not exist", path);
				continue;
			}

			propertyMap.load(fis);
		}

		if (LOGGER.isDebugEnabled()) {
			propertyMap.list(System.out);	
		}
	}

	public static Object get(String key) {
		return propertyMap.get(key);
	}

	public static String getString(String key) {
		return propertyMap.get(key) != null ? String.valueOf(propertyMap.get(key)) : "";
	}

	public static int getInt(String key) {
		return propertyMap.get(key) != null ? Integer.parseInt(String.valueOf(propertyMap.get(key))) : 0;
	}

	public static long getLong(String key) {
		return propertyMap.get(key) != null ? Long.parseLong(String.valueOf(propertyMap.get(key))) : 0L;
	}

}
