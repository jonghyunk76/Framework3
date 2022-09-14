package kr.yni.frame.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import kr.yni.frame.Constants;
import kr.yni.frame.collection.DataMap;
import kr.yni.frame.util.DataMapHelper;
import kr.yni.frame.util.StringHelper;

/**
 * WebAction 클래스
 * 
 * @author YNI-maker
 *
 */
public class WebAction {
	
	private static Log log = LogFactory.getLog(WebAction.class);
	
	/**
	 * 지정된 URL 페이지로 forwarding
	 * 
	 * @param forwardUrl 포워딩될 URL
	 * @return
	 */
	public static ModelAndView forwarding(String forwardUrl) {
		return forwarding(forwardUrl, null, null);
	}
	
	public static ModelAndView forwarding(String forwardUrl, DataMap dataMap) {
		return forwarding(forwardUrl, dataMap, null);
	}
	
	/**
	 * 메시지를 포함한 페이지 forwarding
	 * 
	 * @param forwardUrl
	 * @param dataMap
	 * @param message
	 *            화면에 표시할 메시지
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ModelAndView forwarding(String forwardUrl, DataMap dataMap, String message) {
		ModelAndView mav = null;
		
		// Program Code 찾기
		String programCode = "";
		
		String path = forwardUrl;
		int idx = 0;
		
		if(idx > 0) {
			programCode = path.substring(idx, path.length());
			if(programCode.length() > 8) {
				programCode = programCode.substring(0, 8).replaceAll("-", ""); // HTML에서 '-'가 붙는 경우 오류가발생하기 때문에 제거했음
			}
		}
		
		if(log.isDebugEnabled()) log.debug("PROGRAM_CODE = " + programCode);
		
		if(dataMap != null && !dataMap.isEmpty()) {
			if (!StringHelper.isNull(message)) {
				dataMap.put("message", message);
			}
		} else {
			dataMap = new DataMap();
		}
		
		dataMap.put("PROGRAM_CODE", programCode);
		
		mav = new ModelAndView(forwardUrl, DataMapHelper.getMap(dataMap));
		
		if(log.isDebugEnabled()) {
			log.debug("Forwarding... (" + forwardUrl + ")");
		}
		
		return mav;
	}
	
	/**
	 * 메시지를 포함한 페이지 forwarding
	 * 
	 * @param forwardUrl
	 * 			화면 틀
	 * @param serviceUrl
	 * 			운영 화면 
	 * @param dataMap
	 * 			데이터 맵
	 * @param message
	 *            화면에 표시할 메시지
	 * @return
	 */
	public static ModelAndView forwarding(String forwardUrl, String serviceUrl, DataMap dataMap, String message) {
		ModelAndView mav = forwarding(forwardUrl, dataMap, message);
		serviceUrl = "../"+serviceUrl +".jsp";
		mav.addObject("serviceUrl", serviceUrl); 
		return mav;
	}
	
	
	/**
	 * HashMap 데이터를 Json타입으로 변환한다.
	 * 
	 * @param map HashMap
	 * @return
	 */
	@SuppressWarnings( "rawtypes" )
	public static ModelAndView returnHashMap(LinkedHashMap map) {
		return reponseJsonView(map, null);
	}
	
	/**
	 * HashMap 데이터를 Json타입으로 변환시키며, 메시지를 포함한다.<br>
	 * 메시지의 key는 "message"이다.
	 * 
	 * @param map
	 * @param message 메시지 key
	 * @return
	 */
	@SuppressWarnings( "rawtypes" )
	public static ModelAndView returnHashMap(LinkedHashMap map, String message) {
		return reponseJsonView(map, message);
	}
	
	/**
	 * Map 데이터를 Json타입으로 변환한다.
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings( "rawtypes" )
	public static ModelAndView returnMap(Map map) {
		return reponseJsonView(map, null);
	}
	
	/**
	 * Map 데이터를 Json타입으로 변환시키며, 메시지를 포함한다.<br>
	 * 메시지의 key는 "message"이다.
	 * 
	 * @param map
	 * @param message 메시지 key
	 * @return
	 */
	@SuppressWarnings( "rawtypes" )
	public static ModelAndView returnMap(Map map, String message) {
		return reponseJsonView(map, message);
	}
	
	/**
	 * List 데이터를 Json타입으로 변환한다.<br>
	 * 데이터의 key는 "rows"이고 list row수의 key는 "total" 이다.
	 * <br>
	 * <b>JSON 구조</b>
	 * List list = new ArrayList();<br>
	 *<br>
	 * Map map1 = new LinkedHashMap();<br>
	 * <br>
	 * Map map2 = new LinkedHashMap();<br>
	 * <br>
	 * map2.put("productid", "item01");<br>
	 * map2.put("productname", "아이템01");<br>
	 * map2.put("unitcost", "10.00");<br>
	 * map2.put("status", "P");<br>
	 * map2.put("listprice", "36.50");<br>
	 * map2.put("attr1", "Large");<br>
	 * map2.put("itemid", "1234");<br>
	 * <br>
	 * list.add(map2);<br>
	 * <br>
	 * map1.put("rows", list);<br>
	 * map1.put("total", "10");<br>
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static ModelAndView returnDataSet(List list) {
		return returnDataSet(list, null, null);
	}
	
	/**
	 * List 데이터를 Json타입으로 변환시키며, message를 포함한다.<br>
	 * 데이터의 key는 "rows"이고 list row수의 key는 "total" 이다.
	 * * <br>
	 * <b>JSON 구조 : Map<String, List<Map<String, Object>></b><br>
	 * 예시) <br>
	 * List list = new ArrayList();<br>
	 *<br>
	 * Map map1 = new LinkedHashMap();<br>
	 * <br>
	 * Map map2 = new LinkedHashMap();<br>
	 * <br>
	 * map2.put("productid", "item01");<br>
	 * map2.put("productname", "아이템01");<br>
	 * map2.put("unitcost", "10.00");<br>
	 * map2.put("status", "P");<br>
	 * map2.put("listprice", "36.50");<br>
	 * map2.put("attr1", "Large");<br>
	 * map2.put("itemid", "1234");<br>
	 * <br>
	 * list.add(map2);<br>
	 * <br>
	 * map1.put("rows", list);<br>
	 * map1.put("total", "10");<br>
	 * 
	 * @param list
	 * @param message 메시지 key
	 * @return
	 */
	@SuppressWarnings( { "rawtypes" } )
	public static ModelAndView returnDataSet(List list, String message) {
		return returnDataSet(list, message, null);
	}
	
	/**
	 * List 데이터를 Json타입으로 변환시키며, message를 포함한다.<br>
	 * 데이터의 key는 "rows"이고 list row수의 key는 "total", 하위 footer의 key는 "footer" 이다.
	 * 
	 * @param list data list
	 * @param message view meesage
	 * @param flist  footer list
	 * @return
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static ModelAndView returnDataSet(List list, String message, List flist) {
		Map map = new LinkedHashMap();
		
		if(list != null && list.size() < 1) {
			Map emptyMap = new LinkedHashMap();
			
			emptyMap.put("empty", "MSG_NO_SEARCH_RESULT");
			
			list.add(emptyMap);
		}
		
		// easyUI그리드에 대한 json타입의 데이터 생성
		if(list != null && !list.isEmpty()) {
			map.put("rows", list);
			map.put("total", ((Map) list.get(0)).get("TOTALCOUNT"));
			
			// footer(그리드 하위 합계 뷰) 생성
			if(flist != null && !flist.isEmpty()) {
				map.put("footer", flist);
			}
			
		} else {
			map.put("rows", new ArrayList());
			map.put("total", 0);
		}
		
		return reponseJsonView(map, message);
	}
	
	/**
	 * 오브젝트 타입별로 json데이터를 생성 후 응답한다.
	 * 
	 * @param dataSet 
	 * @param message 메시지 key
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ModelAndView reponseJsonView(Object obj, String message) {
		if (log.isDebugEnabled()) log.debug("reponseJsonView.....");
		
		List<Map<String, Object>> list = null;
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		
		if(obj instanceof List) {
			// input type = [{col1=value, col2=value},{col1=value, col2=value}]
			modelMap.put("rows", list);
			
			list = (List) obj;
		} else if(obj instanceof Map) {
			// input type = {rows=[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string},{empty=string}],total=string}
			Map mapData = (Map) obj;
			modelMap.putAll(mapData);
			
			Object objt =  ((Map) obj).get("rows");
			if(objt != null) {
				list = (List) objt;
			}
		} else if(obj instanceof HashMap) {
			// input type = {rows=[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string},{empty=string}],total=string}
			modelMap.putAll((HashMap) obj);
			
			Object objt =  ((HashMap) obj).get("rows");
			if(objt != null) {
				list = (List) objt;
			}
		} else if(obj instanceof LinkedHashMap) {
			// input type = {rows=[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string},{empty=string}],total=string}
			modelMap.putAll((LinkedHashMap) obj);
			
			Object objt =  ((LinkedHashMap) obj).get("rows");
			if(objt != null) {
				list = (List) objt;
			}
		}
		
		// 엑셀파일을 만들기 위한 로직 추가(2013.09.16)
		if(list != null && list.size() > 0) {
			int lastIndex = list.size() - 1;
			
			// 1. 마지막 데이터에 해더정보가 있는지 체크
			if(list.get(lastIndex) != null && list.get(lastIndex) instanceof Map) { 
				Map map = (Map) list.get(lastIndex);
				
				String file = StringHelper.null2void(map.get("file"));
				Object colObj = map.get("headers");
				
				// 2. 해더정보가 있다면 엑셀 다운로드로 응답시킴
				if(colObj != null && colObj instanceof List) {
					List colList = (List) colObj;
					
					if(log.isDebugEnabled()) log.debug("header:" + colList.get(colList.size() - 1));
					
					if(colList != null && colList.size() > 0) {
						return returnExcelView(Arrays.asList(list), file, message);
					} else {
						if(StringHelper.isNull(message)) {
							message = "No search header info.";
						}
					}
				}
			}
		}
		
		modelMap.put("success", true);
		
		if(!StringHelper.isNull(message)) {
			modelMap.put("message", message);
		}
		
		ModelAndView mav = new ModelAndView("json", modelMap);
		
		return mav;
	}
	
	/**
	 * 요청처리 성공여부와 메시지를 리턴한다.
	 * 
	 * @param success
	 * @param message 메시지 key
	 * @return ModelAndView
	 */
	public static ModelAndView resultJsonMsg(boolean success, String message){
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		
		modelMap.put("success", success);
		
		if(!StringHelper.isNull(message)) {
			modelMap.put("message", message);
		}
		
		return new ModelAndView("json", modelMap);
	}
	
	/**
	 * 메인 페이지로 forwarding
	 * 
	 * @return ModelAndView
	 */
	public static ModelAndView goMain() {
		return new ModelAndView("redirect:" + Constants.MAIN_VIEW);
	}
	
	/**
	 * process 처리 내용을 json 유형으로 return (view.xml)-xmlFileViewResolver
	 * 
	 * @param String
	 *            message
	 * @return ModelAndView
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ModelAndView describeMessage(String message) {
		Map result = new LinkedHashMap();
		
		result.put("message", message);
		
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>(2);
		
		modelMap.put("total", 1);
		modelMap.put("rows", result);

		return new ModelAndView("json", modelMap);

	}

	/**
	 * process 처리 결과를 json 유형으로 return (view.xml)-xmlFileViewResolver
	 * 
	 * @param String
	 *            message
	 * @return ModelAndView
	 */
	public static ModelAndView processResult(String message) {

		Map<String, Object> modelMap = new LinkedHashMap<String, Object>(2);
		modelMap.put("success", true);

		if (!StringHelper.isNull(message)) {

			Map<String, Object> errorMap = new LinkedHashMap<String, Object>(2);
			errorMap.put("message", message);
			modelMap.put("messages", errorMap);
		}
		
		return new ModelAndView("json", modelMap);
	}
	
	/**
	 * <code>List</code>를 기본 json타입으로 변환된 <code>String</code>객체로 응답한다.<br>
	 * 
	 * @param list json타입으로 변환시킬 객체
	 * @return ModelAndView
	 */
	public static ModelAndView returnJsonView(List<Map<String, Object>> list) {
		return returnJsonView(list, null);
	}
	
	/**
	 * <code>List</code>를 기본 json타입으로 변환된 <code>String</code>객체로 응답한다.<br>
	 * 
	 * @param list json타입으로 변환시킬 객체
	 * @param message 오류 메시지
	 * @return ModelAndView
	 */
	public static ModelAndView returnJsonView(List<Map<String, Object>> list, String message) {
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		String jsonString = null;

		if(!StringHelper.isNull(message)) {
			modelMap.put("success", false);
			modelMap.put("message", message);
			
			return new ModelAndView("json", modelMap);
		}
		
		try {
			JSONArray jsonObject = JSONArray.fromObject(list);
			
			jsonString = jsonObject.toString();
		} catch (Exception e) {
			jsonString = "";
		}
		
		modelMap.put("rows", jsonString);
		
		return new ModelAndView("jrows", modelMap);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map  File 객체
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(File file) {
		return returnFileView(file, null, null);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map File 객체
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(File file, String fname) {
		return returnFileView(file, fname, null);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map File 객체
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @param message 오류 메시지
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(File file, String fname, String message) {
		return returnFileView((Object) file, fname, message);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map  FileInputStream 객체
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(FileInputStream stream) {
		return returnFileView(stream, null, null);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map  FileInputStream 객체
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(FileInputStream stream, String fname) {
		return returnFileView(stream, fname, null);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map  FileInputStream 객체
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @param message 오류 메시지
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(FileInputStream stream, String fname, String message) {
		return returnFileView((Object) stream, fname, message);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map  byte[] 객체
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(byte[] data) {
		return returnFileView(data, null, null);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map  byte[] 객체
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(byte[] data, String fname) {
		return returnFileView(data, fname, null);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map  byte[] 객체
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @param message 오류 메시지
	 * @return ModelAndView
	 */
	public static ModelAndView returnFileView(byte[] data, String fname, String message) {
		return returnFileView((Object) data, fname, message);
	}
	
	/**
	 * <code>File</code>로 다운로드 한다.<br>
	 * 
	 * @param map 파일 생성 객체
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @param message 오류 메시지
	 * @return ModelAndView
	 */
	private static ModelAndView returnFileView(Object obj, String fname, String message) {
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		
		if(!StringHelper.isNull(message)) {
			modelMap.put("success", false);
			modelMap.put("message", message);
			
			return new ModelAndView("json", modelMap);
		}
		
		modelMap.put("file", obj);
		modelMap.put("fileName", fname);
		
		return new ModelAndView("file", modelMap);
	}
	
	/**
	  * <p>
	 * <code>Excel File</code>로 다운로드 한다.<br>
	 * 단, 오류 메시지가 등록되어 있는 경우에는 메시지만 리턴된다.
	 * </p>
	 * 
	 * @param list 실제파일로 생성할 List
	 * @return ModelAndView
	 */
	public static ModelAndView returnExcelView(List<List<Map<String, Object>>> list) {
		return returnExcelView(list, null, null);
	}
	
	/**
	 * <p>
	 * <code>Excel File</code>로 다운로드 한다.<br>
	 * 단, 오류 메시지가 등록되어 있는 경우에는 메시지만 리턴된다.
	 * </p>
	 * 
	 * @param list 실제파일로 생성할 List
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @return ModelAndView
	 */
	public static ModelAndView returnExcelView(List<List<Map<String, Object>>> list, String fname) {
		return returnExcelView(list, fname, null);
	}
	
	/**
	 * <p>
	 * <code>Excel File</code>로 다운로드 한다.<br>
	 * 단, 오류 메시지가 등록되어 있는 경우에는 메시지만 리턴된다.
	 * </p>
	 * 
	 * @param list 실제파일로 생성할 List(List<Map> 또는 List<List<Map>> 형태)<br>
	 *        Map 안에 등록되는 데이터<br>
	 * 	      rows = 엑셀의 row data (type = Map)<br>
	 *       ,headers = 시트의 header 정보 (type = List)<br>
	 *       ,headers.sheet = 시트명 (type = String)<br>
	 * @param fname 다운로드 시 적용될 파일의 명칭
	 * @param message 오류 메시지
	 * @return ModelAndView
	 */
	public static ModelAndView returnExcelView(List<List<Map<String, Object>>> list, String fname, String message) {
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		
		if(!StringHelper.isNull(message)) {
			modelMap.put("success", false);
			modelMap.put("message", message);
			
			return new ModelAndView("json", modelMap);
		}
		
		// list type = [[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string}],
		//			   ,[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string}]]
		modelMap.put("list", list);
		modelMap.put("fileName", StringHelper.null2void(fname));
		
		return new ModelAndView("excel", modelMap);
	}
	
	/**
	 * <p>
	 * IReport app 를 이용하여 보고서를 출력한다.
	 * </p>
	 * @param list
	 * @param message
	 * @return
	 */
	public static ModelAndView returnIReportView(List<JasperPrint> list, String message) {
		return returnIReportView(list, null, message);
	}
	
	/**
	 * <p>
	 * IReport app 를 이용하여 보고서를 출력한다.
	 * </p>
	 * @param list
	 * @param map
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ModelAndView returnIReportView(List<JasperPrint> list, DataMap map, String message) {
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		
		if(!StringHelper.isNull(message)) {
			modelMap.put("success", false);
			modelMap.put("message", message);
			
			return new ModelAndView("json", modelMap);
		}
		
		modelMap.put("data", list);
		if(map != null) {
			modelMap.putAll(map.getMap());
		}
			
		return new ModelAndView("jasper", modelMap);
	}
	
	/**
	 * <p>
	 * XML을 리턴한다.
	 * </p>
	 * @param list
	 * @param message
	 * @return
	 */
	public static ModelAndView returnXMLView(Object obj, String fname) {
		return returnXMLView(obj, null, fname);
	}
	
	/**
	 * <p>
	 * XML을 리턴한다.
	 * </p>
	 * @param list
	 * @param map
	 * @param message
	 * @return
	 */
	public static ModelAndView returnXMLView(Object obj, String fname, String message) {
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		
		if(!StringHelper.isNull(message)) {
			modelMap.put("success", false);
			modelMap.put("message", message);
			
			return new ModelAndView("json", modelMap);
		}
		
		modelMap.put("file", obj);
		modelMap.put("fileName", fname);
		
		return new ModelAndView("xml", modelMap);
	}
	
}
