package kr.yni.frame.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.yni.frame.collection.DBBasedHashMap;

/**
 * Object 타입을 요구하는 형태로 변경하도록 도와주는 클래스
 * @author YNI-maker
 *
 */
@SuppressWarnings("rawtypes")
public class ObjectHelper {
	
	/**
	 * Object타입을 <code>java.util.List</code>로 치환
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static List toList(Object obj) throws Exception {
		List list = null;
		
		if(obj == null) {
			list = new ArrayList();
		} else {
			if(obj instanceof List) {
				list = (List) obj;
			} else {
				list = new ArrayList();
			}
		}
		
		return list;
	}
	
	/**
	 * Object타입을 <code>java.util.Map</code>로 치환
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map toMap(Object obj) throws Exception {
		Map map = null;
		
		if(obj == null) {
			map = new HashMap();
		} else {
			if(obj instanceof Map) {
				map = (Map) obj;
			} else {
				map = new HashMap();
			}
		}
		
		return map;
	}
	
}
