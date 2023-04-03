package kr.yni.frame.pool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class OrderedProperties extends Properties {
	
	public ArrayList orderedKeys = new ArrayList();
	
	public OrderedProperties() {
		super();
	}
	
	public OrderedProperties(java.util.Properties defaults) {
		super(defaults);
	}
	
	public synchronized Iterator getKeysIterator() {
		return orderedKeys.iterator();
	} 
	
	public static OrderedProperties load(String path)
			throws IOException {
		OrderedProperties props = null;
		InputStream is = null;
		
		try {
			is = Files.newInputStream(new File(path).toPath());
			
			props = new OrderedProperties();
			props.load(is);
		} catch(IOException io) {
			io.printStackTrace();
			throw new IOException("Properties could not be loaded.");
		} finally {
			if(is != null) is.close();
		}
		
		return props;
	}
	
	public synchronized Object put(Object key, Object value) {
		Object obj = super.put(key, value);
		orderedKeys.add(key);
		
		return obj;
	}
	
	public synchronized Object remove(Object key) {
		Object obj = super.remove(key);
		orderedKeys.remove(key);
		
		return obj;
	}
	
}
