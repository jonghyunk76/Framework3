package kr.yni.frame.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.XML;

import kr.yni.frame.collection.DataMap;
import net.sf.json.JSONArray;

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
			Map emptyMap = new LinkedHashMap();
			
			emptyMap.put("empty", "");
			
			list.add(emptyMap);
		}
		
		jsonString = getViewJson(list);
		
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
			Map emptyMap = new LinkedHashMap();
			
			emptyMap.put("empty", "");
			
			list.add(emptyMap);
		}
		
		Gson gson = new Gson();
		jsonString = gson.toJson(list);
		
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
		Gson gson = new Gson();
		String jsonString = gson.toJson(map);
        
        return jsonString;
	}
	
	/***
	 * getList provides a List representation of the JSON Object
	 * 
	 * @param jsonResponse
	 *            The JSON array string
	 * @return List of JsonObject.
	 **/
	public static List<Object> getList(String jsonResponse) throws Exception {
		return getList(jsonResponse, -1);
	}
	
	/***
	 * getList provides a List representation of the JSON Object
	 * 
	 * @param jsonResponse
	 *            The JSON array string
	 * @return List of JsonObject.
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
				GsonBuilder builder = new GsonBuilder();
	            builder.setPrettyPrinting();
	            
	            Gson gson = builder.create();
	            listResponse = gson.fromJson(jsonResponse, List.class);
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
	 * @return Map of JsonObject.
	 **/
	public static Map<String, Object> getMap(String jsonResponse)
			throws Exception {
		Map<String, Object> mapResponse = new LinkedHashMap<String, Object>();

		if (jsonResponse.startsWith("{")) {
			GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            
            Gson gson = builder.create();
            mapResponse = gson.fromJson(jsonResponse, Map.class);
		} else {
			throw new Exception("MalFormed JSON Map Response.");
		}

		return mapResponse;
	}
	
	/**
	 * Object를 Parsing을 통해 JsonObject를 리턴한다.
	 * 
	 * @param obj Map 또는 List 객체
	 * @return
	 * @throws Exception
	 */
	public static JsonObject getJsonObject(Object obj) throws Exception {
		Gson gson = new Gson();
		String jsonString = gson.toJson(obj);
		
		return (JsonObject) JsonParser.parseString(jsonString);
	}
	
	/**
	 * net.sf.json.JSON 배열을 구한다.
	 * 
	 * @param dataSet
	 * @return
	 */
	public static JSONArray fromObject(Object dataSet) {
		return JSONArray.fromObject(dataSet);
	}
	
	/**
	 * XML을 Json으로 변환 후 Map를 리턴
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Map getXMLMap(String xml) throws Exception {
		JSONObject json = XML.toJSONObject(xml);
		
		return getMap(json.toString());
	}
	
}
