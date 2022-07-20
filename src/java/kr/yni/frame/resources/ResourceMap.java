package kr.yni.frame.resources;

import java.util.LinkedHashMap;

/**
 * Resource Map
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings("rawtypes")
public class ResourceMap extends LinkedHashMap {

	private static final long serialVersionUID = 1L;

	public ResourceMap() {
	}

	public ResourceMap(String name) {
		this.name = name;
	}

	/*
	 * resource 명
	 */
	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
