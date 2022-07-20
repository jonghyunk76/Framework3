package kr.yni.frame.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.collection.DataMap;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.StringHelper;

/**
 * Json format를 도와주는 클래스
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings("rawtypes")
public class JsonUtil {
	
	private static Log log = LogFactory.getLog(JsonUtil.class);
	
	/**
	 * <code>List</code>를 json형태로 변환 후 <code>DataMap</code>에 등록한다.<br>
	 * 이때, DataMap에 등록되는 "jwors" 이다.
	 * 
	 * @param list
	 * @param dataMap
	 * @throws Exception
	 */
	public static void viewJson(List list, DataMap dataMap) throws Exception {
		viewJson(list, dataMap, null);
	}
	
	/**
	 * json형태의 문자열인지 판단하고 true 또는 false를 리턴한다.
	 * 
	 * @param jsonResponse 문자열
	 * @return ture or false
	 */
	public static boolean isJsonType(String jsonResponse) throws Exception {
		return (jsonResponse.startsWith("[") && jsonResponse.endsWith("]")) ? true : false;
	}
	
	/**
	 * <code>List</code>를 json형태로 변환 후 <code>DataMap</code>에 등록한다.<br>
	 * 이때, DataMap에 등록되는 key는 지정된 key를 사용한다.
	 * 
	 * @param list
	 * @param dataMap
	 * @param keyName
	 * @throws Exception
	 */
	public static void viewJson(List list, DataMap dataMap, String keyName) throws Exception {
		String jsonString = null;
		
		if(list != null && list.size() < 1) {
			Map emptyMap = new HashMap();
			
			emptyMap.put("empty", "");
			
			list.add(emptyMap);
		}
		
		JSONArray jsonObject = JSONArray.fromObject(list);
		jsonString = jsonObject.toString();
		
		if(StringHelper.isNull(keyName)) {
			dataMap.put("jrows", jsonString);
		} else {
			dataMap.put(keyName, jsonString);
		}
	}
	
	/**
	 * <code>List</code>를 json형태로 변환한다.
	 * 
	 * @param list
	 * @return json 데이터
	 * @throws Exception
	 */
	public static String getViewJson(List list) throws Exception {
		String jsonString = null;
		
		if(list != null && list.size() < 1) {
			Map emptyMap = new HashMap();
			
			emptyMap.put("empty", "");
			
			list.add(emptyMap);
		}
		
		JSONArray jsonObject = JSONArray.fromObject(list);
		jsonString = jsonObject.toString();
		
		return jsonString;
	}
	
	/**
	 * <code>List</code>를 json형태로 변환한다.
	 * 
	 * @param list
	 * @return json 데이터
	 * @throws Exception
	 */
	public static String getViewJson(Map<String, Object> map) throws Exception {
		JSONObject jsonObject = new JSONObject();
		
        for( Map.Entry<String, Object> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            jsonObject.put(key, value);
        }
        
        
        return jsonObject.toString();
	}
	
	/***
	 * getList provides a List representation of the JSON Object
	 * 
	 * @param jsonResponse
	 *            The JSON array string
	 * @return List of JSONObject.
	 **/
	public static List<Object> getList(String jsonResponse) throws Exception {
		return getList(jsonResponse, -1);
	}
	
	/***
	 * getList provides a List representation of the JSON Object
	 * 
	 * @param jsonResponse
	 *            The JSON array string
	 * @return List of JSONObject.
	 **/
	public static List<Object> getList(String jsonResponse, int i) throws Exception {
		List<Object> listResponse = new ArrayList<Object>();

		if (jsonResponse == null || jsonResponse.equals("")) {
			jsonResponse = "[]";
		} else {
			// 뉴라인 문자를 제거해야 오류가 발생하지 않음
			jsonResponse = jsonResponse.replace('\n', ' ');
			jsonResponse = jsonResponse.replace('\r', ' ');
		}
		
		if (jsonResponse.startsWith("{")) {
			log.debug("Json data type is Map...");
			
			listResponse.add(JsonUtil.getMap(jsonResponse));
		} else {
			log.debug("Json data type is List...");
			
			if (JsonUtil.isJsonType(jsonResponse)) {
				JSONArray jsonArray = JSONArray.fromObject(jsonResponse);
				
				if(i == -1) {
					for (int a = 0; a < jsonArray.size(); a++) {
						toMultiList(jsonArray, listResponse, a);
					}
				} else {
					toMultiList(jsonArray, listResponse, i);
				}
			} else {
				throw new Exception("MalFormed JSON Array Response.");
			}
		}
		
		return listResponse;
	}

	/***
	 * getMap provides a Map representation of the JSON Object
	 * 
	 * @param jsonResponse
	 *            The JSON object string
	 * @return Map of JSONObject.
	 **/
	public static Map<String, Object> getMap(String jsonResponse)
			throws Exception {
		Map<String, Object> mapResponse = new LinkedHashMap<String, Object>();

		if (jsonResponse.startsWith("{")) {
			JSONObject jsonObj = JSONObject.fromObject(jsonResponse);
			toJavaMap(jsonObj, mapResponse);
		} else {
			throw new Exception("MalFormed JSON Map Response.");
		}

		return mapResponse;
	}

	/***
	 * toJavaMap converts the JSONObject into a Java Map
	 * 
	 * @param o
	 *            JSONObject to be converted to Java Map
	 * @param b
	 *            Java Map to hold converted JSONObject response.
	 **/
	private static void toJavaMap(JSONObject o, Map<String, Object> b) {
		Iterator ji = o.keys();

		while (ji.hasNext()) {
			String key = (String) ji.next();
			Object val = o.get(key);
			
			if (val.getClass() == JSONObject.class) {
				Map<String, Object> sub = new LinkedHashMap<String, Object>();
				
				toJavaMap((JSONObject) val, sub);
				b.put(key, sub);
			} else if (val.getClass() == JSONArray.class) {
				List<Object> l = new ArrayList<Object>();
				JSONArray arr = (JSONArray) val;

				for (int a = 0; a < arr.size(); a++) {
					Map<String, Object> sub = new LinkedHashMap<String, Object>();
					Object element = arr.get(a);
					
					if (element instanceof JSONObject) {
						toJavaMap((JSONObject) element, sub);
						
						l.add(sub);
					} else {
						l.add(element);
					}
				}
				
				b.put(key, l);
			} else {
				b.put(key, val);
			}
		}
	}

	/***
	 * toJavaList converts JSON's array response into Java's List
	 * 
	 * @param ar
	 *            JSONArray to be converted to Java List
	 * @param ll
	 *            Java List to hold the converted JSONArray response
	 **/
	private static void toJavaList(JSONArray ar, List<Object> ll) {
		int i = 0;

		while (i < ar.size()) {
			Object val = ar.get(i);
			
			if (val.getClass() == JSONObject.class) {
				Map<String, Object> sub = new LinkedHashMap<String, Object>();
				toJavaMap((JSONObject) val, sub);
				ll.add(sub);
			} else if (val.getClass() == JSONArray.class) {
				List<Object> l = new ArrayList<Object>();
				JSONArray arr = (JSONArray) val;

				for (int a = 0; a < arr.size(); a++) {
					Map<String, Object> sub = new LinkedHashMap<String, Object>();
					Object element = arr.get(a);
					
					if (element instanceof JSONObject) {
						toJavaMap((JSONObject) element, sub);
						ll.add(sub);
					} else {
						ll.add(element);
					}
				}

				l.add(l);
			} else {
				ll.add(val);
			}

			i++;
		}
	}
	
	/**
	 * 다중배열인 경우 List<Map<String, Object>> 형태로 변환하는 재귀 Method.
	 * 
	 * @param ar JSONArray to be converted to Java List
	 * @param ll  Java List to hold the converted JSONArray response
	 * @param i  index
	 */
	private static void toMultiList(JSONArray ar, List<Object> ll, int i) {
		if(ar.size() > 0) {
			Object val = ar.get(i);
			
			if(val != null) {
				if(val instanceof JSONArray) {
					toMultiList((JSONArray) val, ll, i);
				} else if(val instanceof JSONObject) {
					Map<String, Object> sub = new HashMap<String, Object>();
					toJavaMap((JSONObject) val, sub);
					ll.add(sub);
				} else {
					ll.add(val);
				}
			}
		}
	}
	
	/**
	 * JSON 배열을 구한다.
	 * 
	 * @param dataSet
	 * @return
	 */
	public static JSONArray fromObject(Object dataSet) {
		return net.sf.json.JSONArray.fromObject(dataSet);
	}
	
	/**
	 * JSON의 배열 차원 조회
	 * 
	 * @param jsonResponse
	 * @return
	 */
	public static int getJSONArrayLevel(String jsonResponse) {
		int level = 1;
		StringBuffer arrayFlag = new StringBuffer("");
		
		JSONArray jsonArray = JSONArray.fromObject(jsonResponse);
		
		for(int i = 0 ; i < jsonArray.size() ; i++) {
			arrayFlag.append("[");
		}
		
		if(jsonResponse.startsWith(arrayFlag.toString())) {
			level = jsonArray.size();
		}
		
		return level;
	}
	
	/**
	 * 원산지확인서 등록 화면에서 엑셀다운로드를 위한 별도 JsonUtils
	 * getHMList 현대모비스용 
	 * 원산지확인서 등록 화면에서 엑셀 다운로드시 DB에 저장하지 않고 화면의
	 * 값들을 바로 Excel 다운로드 할수 있게 LinkedHashMap으로 변경
	 * @param jsonResponse
	 * @return
	 * @throws Exception
	 */
	public static List<Object> getHMList(String jsonResponse) throws Exception {
		List<Object> listResponse = new ArrayList<Object>();

		if (jsonResponse == null || jsonResponse.equals("")) {
			jsonResponse = "[]";
		}

		if (jsonResponse.startsWith("[")) {
			JSONArray jsonArray = JSONArray.fromObject(jsonResponse);
			toJavaHMList(jsonArray, listResponse);
		} else {
			throw new Exception("MalFormed JSON Array Response.");
		}

		return listResponse;
	}
	
	/***
	 * toJavaList converts JSON's array response into Java's List
	 * 
	 * @param ar = JSONArray to be converted to Java List
	 * @param ll = Java List to hold the converted JSONArray response
	 **/
	private static void toJavaHMList(JSONArray ar, List<Object> ll) {
		int i = 0;

		while (i < ar.size()) {
			Object val = ar.get(i);

			if (val.getClass() == JSONObject.class) {
				Map<String, Object> sub = new LinkedHashMap<String, Object>();
				toJavaHMMap((JSONObject) val, sub);
				ll.add(sub);
			} else if (val.getClass() == JSONArray.class) {
				List<Object> l = new ArrayList<Object>();
				JSONArray arr = (JSONArray) val;

				for (int a = 0; a < arr.size(); a++) {
					Map<String, Object> sub = new LinkedHashMap<String, Object>();
					Object element = arr.get(a);

					if (element instanceof JSONObject) {
						toJavaHMMap((JSONObject) element, sub);
						ll.add(sub);
					} else {
						ll.add(element);
					}
				}

				l.add(l);
			} else {
				ll.add(val);
			}

			i++;
		}
	}
	
	/***
	 * toJavaMap converts the JSONObject into a Java Map
	 * 
	 * @param o
	 *            JSONObject to be converted to Java Map
	 * @param b
	 *            Java Map to hold converted JSONObject response.
	 **/
	private static void toJavaHMMap(JSONObject o, Map<String, Object> b) {
		Iterator ji = o.keys();

		while (ji.hasNext()) {
			String key = (String) ji.next();
			Object val = o.get(key);
			if (val.getClass() == JSONObject.class) {
				Map<String, Object> sub = new LinkedHashMap<String, Object>();
				toJavaMap((JSONObject) val, sub);
				b.put(key, sub);
			} else if (val.getClass() == JSONArray.class) {
				List<Object> l = new ArrayList<Object>();
				JSONArray arr = (JSONArray) val;

				for (int a = 0; a < arr.size(); a++) {
					Map<String, Object> sub = new LinkedHashMap<String, Object>();
					Object element = arr.get(a);
					if (element instanceof JSONObject) {
						toJavaMap((JSONObject) element, sub);
						l.add(sub);
					} else {
						l.add(element);
					}
				}
				b.put(key, l);
			} else {
				b.put(key, val);
			}
		}
	}
	
}
