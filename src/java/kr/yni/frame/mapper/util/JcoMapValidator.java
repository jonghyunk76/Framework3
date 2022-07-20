package kr.yni.frame.mapper.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sap.mw.jco.JCO;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.mapper.ParamReader;
import kr.yni.frame.mapper.element.Column;
import kr.yni.frame.mapper.element.Export;
import kr.yni.frame.mapper.element.Jco;
import kr.yni.frame.mapper.element.Parameter;
import kr.yni.frame.mapper.element.Table;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.DateHelper;
import kr.yni.frame.util.JsonUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.util.SystemHelper;

/**
 * <p>
 * 데이터의 유효성을 체크하는 클래스
 * </p>
 * <p>
 * - TYPE이 DATA인 경우 필수여부, 날짜형식 검증 
 *    IS_DATE : 날짜타입 체크(ERROR_INVALID_DATE)
 *    IS_REQUIRED : 필수 여부(ERROR_REQUIRED_YN) 
 * - TYPE이 NUMBER인 경우 필수여부, 숫자형식, 길이 검증
 *    IS_NUMBER : 숫자타입 체크(ERROR_INVALID_NUMBER) 
 *    IS_LENGTH : 문자열 길이 체크(ERROR_INVALID_LENGTH) 
 *    IS_REQUIRED : 필수 여부(ERROR_REQUIRED_YN) 
 * - TYPE이 STRING인 경우 필수여부, 길이 검증 
 *    IS_LENGTH : 문자열 길이 체크(ERROR_INVALID_LENGTH)
 *    IS_REQUIRED : 필수 여부(ERROR_REQUIRED_YN)
 * </p>
 * 
 * @author 김종현
 */
public class JcoMapValidator {
    private static Log log = LogFactory.getLog(JcoMapValidator.class);
    
    /**
     * jco Map과 맵핑되는 source List 생성
     * 
     * @param jcoID      사용자XML의 식별ID
     * @param sourceList 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, List<Object> sourceList, boolean valid) throws Exception {
        return executeCheck(jcoID, sourceList, null, null, SystemHelper.getLocale(null), valid);
    }
    
    /**
     * jco Map과 맵핑되는 source List 생성
     * 
     * @param jcoID      사용자XML의 식별ID
     * @param sourceList 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName  데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, List<Object> sourceList, String paramName, boolean valid) throws Exception {
        return executeCheck(jcoID, sourceList, paramName, null, SystemHelper.getLocale(null), valid);
    }
    
    /**
     * jco Map과 맵핑되는 source List 생성
     * 
     * @param jcoID      사용자XML의 식별ID
     * @param sourceList 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName  데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param basecode   basecode 데이터(basecode와 맵칭될 경우 basecode의 id값으로 변경됨)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, List<Object> sourceList, String paramName, List<Object> basecode, boolean valid) throws Exception {
        return executeCheck(jcoID, sourceList, paramName, basecode, SystemHelper.getLocale(null), valid);
    }
    
    /**
     * jco Map과 맵핑되는 source List 생성
     * 
     * @param jcoID      사용자XML의 식별ID
     * @param sourceList 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName  데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param locale     설정언어
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, List<Object> sourceList, String paramName, Locale locale, boolean valid) throws Exception {
        return executeCheck(jcoID, sourceList, paramName, null, locale, valid);
    }
    
    /**
     * jco Map과 맵핑되는 source List 생성
     * 
     * @param jcoID      사용자XML의 식별ID
     * @param sourceList 실제 값을 담고 있는 List<Map> 객체(column 요소의 name속성과 mapping됨)
     * @param paramName  데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param basecode   basecode 데이터(basecode와 맵칭될 경우 basecode의 id값으로 변경됨)
     * @param locale     설정언어
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<Object> executeCheck(String jcoID, List<Object> sourceList, String paramName, List<Object> basecode, Locale locale, boolean valid) throws Exception {
    	List returnList =  new LinkedList(); 
        
        Jco jco = ParamReader.getJcoParameter(jcoID);
        Table tbl = jco.getFunction(0).getTable();
        
        if(tbl == null) {
            return sourceList;
        }
        
        int listIndex = 0;
        
        for (int i = 0; i < tbl.getParameterCount(); i++) {
            boolean paramChecker = false;
            Parameter param = tbl.getParams(i);
            String pname = param.getName();
            
            // 지정된 parameter가 있다면 해당 parameter의 값만 mapping한 후 리턴한다.
            if(paramName == null || pname.equals(paramName)) {
                paramChecker = true;
            }
            
            if(paramChecker) {
                int pType = param.getType();
                
                if(pType == JCO.TYPE_TABLE) {
                    for (int k = 0; k < sourceList.size(); k++) {
                        Map<String, Object> map = (Map<String, Object>) sourceList.get(k);
                        Map sourceMap = getCheckMapForTable(param, map, basecode, locale, valid);
                        
                        sourceMap.put("TABLE_NAME", param.getName());
                        
                        returnList.add(listIndex, sourceMap);
                        
                        listIndex++;
                    }
                } else if(pType == JCO.TYPE_STRUCTURE) {
                    for (int k = 0; k < sourceList.size(); k++) {
                        Map<String, Object> map = (Map<String, Object>) sourceList.get(k);
                        
                        listIndex = getCheckMapForStructure(param, map, returnList, listIndex, basecode, locale, valid);
                    }
                } else {
                    throw new FrameException("not suppert Parameter Type");
                }
            }
        }
        
        return returnList;
    }
    
    /**
     * <p>
     * jco Map과 맵핑되는 source List 생성
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, Map<String, Object> map, boolean valid) throws Exception {
        return executeCheck(jcoID, map, null, null, SystemHelper.getLocale(null), valid);
    }
    
    /**
     * <p>
     * parameter명에 해당하는 jco Map과 맵핑되는 source List 생성
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName 데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, Map<String, Object> map, String paramName, boolean valid) throws Exception {
        return executeCheck(jcoID, map, paramName, null, SystemHelper.getLocale(null), valid);
    }
    
    /**
     * <p>
     * parameter명에 해당하는 jco Map과 맵핑되는 source List 생성
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName 데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param basecode   basecode 데이터(basecode와 맵칭될 경우 basecode의 id값으로 변경됨)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, Map<String, Object> map, String paramName, List<Object> basecode, boolean valid) throws Exception {
        return executeCheck(jcoID, map, paramName, basecode, SystemHelper.getLocale(null), valid);
    }
    
    /**
     * <p>
     * parameter명에 해당하는 jco Map과 맵핑되는 source List 생성
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName 데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param valid      유효성 체크여부
     * @param locale     설정언어
     * @return
     * @throws Exception
     */
    public static List<Object> executeCheck(String jcoID, Map<String, Object> map, String paramName, Locale locale, boolean valid) throws Exception {
        return executeCheck(jcoID, map, paramName, null, locale, valid);
    }
    
    /**
     * <p>
     * parameter명에 해당하는 jco Map과 맵핑되는 source List 생성
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName 데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param basecode   basecode 데이터(basecode와 맵칭될 경우 basecode의 id값으로 변경됨)
     * @param locale     설정언어
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Object> executeCheck(String jcoID, Map<String, Object> map, String paramName, List<Object> basecode, Locale locale, boolean valid) throws Exception {
        List returnList =  new LinkedList();
        
        Jco jco = ParamReader.getJcoParameter(jcoID);
        Table tbl = jco.getFunction(0).getTable();
        
        int listIndex = 0;
        
        // parameter 설정 갯수만큼 loop... 
        for(int i = 0; i < tbl.getParameterCount(); i++) {
            boolean paramChecker = false;
            Parameter param = tbl.getParams(i);
            String pname = param.getName();
            int pType = param.getType();
            
            // 지정된 parameter가 있다면 해당 parameter의 값만 mapping한 후 리턴한다.
            if(paramName == null || pname.equals(paramName)) {
                paramChecker = true;
            }
            
            if(paramChecker) {
                log.debug("get parsing data(parameter name = " + pname + ", type = " + pType + ")");
                
                if(pType == JCO.TYPE_TABLE) {
                    Map sourceMap = getCheckMapForTable(param, map, basecode, locale, valid);
                    
                    sourceMap.put("TABLE_NAME", param.getName());
                    
                    returnList.add(listIndex, sourceMap);
                    
                    listIndex++;
                } else if(pType == JCO.TYPE_STRUCTURE) {
                    listIndex = getCheckMapForStructure(param, map, returnList, listIndex, basecode, locale, valid);
                } else if(pType == JCO.TYPE_ITAB) {
                    returnList = getCheckMapForITable(param, map, basecode, locale, valid);
                } else {
                    throw new FrameException("not support Parameter Type");
                }
            }
        }
        
        return returnList;
    }
    
    /**
     * <p>
     * table타입의 데이터를 List로 변환한 후 리턴(단, TBLE타입만 지원됨)
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName 데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param basecode   basecode 데이터(basecode와 맵칭될 경우 basecode의 id값으로 변경됨)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeTableList(String jcoID, Map<String, Object> map, String paramName, List<Object> basecode, boolean valid) throws Exception {
        return executeTableList(jcoID, map, paramName, basecode, SystemHelper.getLocale(null), valid);
    }
    
    /**
     * <p>
     * table타입의 데이터를 List로 변환한 후 리턴(단, TBLE타입만 지원됨)
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName 데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param basecode   basecode 데이터(basecode와 맵칭될 경우 basecode의 id값으로 변경됨)
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    public static List<Object> executeTableList(String jcoID, Map<String, Object> map, String paramName, Locale locale, boolean valid) throws Exception {
        return executeTableList(jcoID, map, paramName, null, locale, valid);
    }
        
    /**
     * <p>
     * table타입의 데이터를 List로 변환한 후 리턴(단, TBLE타입만 지원됨)
     * </p>
     * 
     * @param jcoID     사용자XML의 식별ID
     * @param sourceMap 실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param paramName 데이터를 맵핑할 파라메터명(null인 경우에는 모든 parameter를 Mapping시킴)
     * @param basecode   basecode 데이터(basecode와 맵칭될 경우 basecode의 id값으로 변경됨)
     * @param locale     설정언어
     * @param valid      유효성 체크여부
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Object> executeTableList(String jcoID, Map<String, Object> map, String paramName, List<Object> basecode, Locale locale, boolean valid) throws Exception {
        List returnList =  new LinkedList();
        
        Jco jco = ParamReader.getJcoParameter(jcoID);
        Table tbl = jco.getFunction(0).getTable();
       
        // parameter 설정 갯수만큼 loop... 
        for(int i = 0; i < tbl.getParameterCount(); i++) {
            boolean paramChecker = false;
            Parameter param = tbl.getParams(i);
            String pname = param.getName();
            
            // 지정된 parameter가 있다면 해당 parameter의 값만 mapping한 후 리턴한다.
            if(paramName == null || pname.equals(paramName)) {
                paramChecker = true;
            }
            
            if(paramChecker) {
                int pType = param.getType();
                
                if(pType == JCO.TYPE_TABLE) {
                    returnList = getCheckListForTable(param, map, basecode, locale, valid);
                } else if(pType == JCO.TYPE_STRUCTURE) {
                    returnList = getCheckListForStructure(param, map, basecode, locale, valid);
                } else if(pType == JCO.TYPE_ITAB) {
                    returnList = getCheckListForITable(param, map, basecode, locale, valid);
                } else {
                    throw new FrameException("not support Parameter Type");
                }
            }
        }
        
        return returnList;
    }
    
    /**
     * <p>
     * JCO XML의 Export되는 parameter중 Structure타입 해당하는 데이터의 유효성 체크와 등록할 값 생성<br>
     * </p>
     * @param jcoID     사용자XML의 식별ID
     * @param map       실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param struName  사용자가 지정한 파라메터 명
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static LinkedHashMap executeStructure(String jcoID, Map map, String struName, boolean valid) throws Exception{
    	LinkedHashMap<String,Object> rstMap = (LinkedHashMap) map;
    	
    	Jco jco = ParamReader.getJcoParameter(jcoID);
        Export exp = jco.getFunction(0).getExports();
    	
        for(int i = 0; i < exp.getParameterCount(); i++) {
        	Parameter param = exp.getParams(i);
            String paramName = param.getName();
            
            if(StringUtils.equals(paramName, struName) && param.getType() == JCO.TYPE_STRUCTURE){
            	List returnList = new LinkedList();
            	int addNum = getCheckMapForStructure(param, map, returnList, 0, null, null, valid);
            	
            	String error = "N";
            	String message = "";
            	
            	if(addNum > 0 && returnList.size() > 0) {
            		for(int t = 0 ; t < returnList.size(); t++) {
	            		Map tmpMap = (Map) returnList.get(t); 
	            		
	            		String status  = StringHelper.null2void(tmpMap.get("ERROR_YN"));
	            		message = StringHelper.null2void(tmpMap.get("ERROR_MESSAGE"));
	            		
	            		if(status.equals("Y")) {
	            			error = status;
	            			message += message + ", ";
	            		}
	            		
	            		for(int p = 0; p < param.getColumnCount(); p++) {
	            			Column col = param.getColumn(p);
	            			
	            			String name = col.getName();
	            			String trans = col.getTrans();
	            			String codeName = StringHelper.null2void(tmpMap.get("NAME_CODE"));
	            			
	            			if(codeName.equals(name)) {
	            				rstMap.put(trans, tmpMap.get(codeName));
	            				break;
	            			}
	            		}
	            			
	            		log.debug("executeStructure result value = " + tmpMap.toString());
            		}
	            	
	            	if(error.equals("Y")) {
	            		rstMap.put("ERROR_YN", "Y");
	            		rstMap.put("ERROR_MESSAGE", message);
	            	} else {
	            		rstMap.put("ERROR_YN", "N");
	            	}
	            }
            }
        }
    	
    	return rstMap;
    }
    
    /**
     * <p>
     * JCO XML의 Table타입의 Parameter Name에 해당하는 데이터의 유효성 체크와 등록할 값 생성<br>
     * </p>
     * @param param    맵핑시킬 파라메터명
     * @param map      실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param codeList 기본코드값을 명칭으로 변경하기 위한 코드리스트
     * @param locale   다국어
     * @param valid    유효성 체크할지 여부
     * @return      Map에 해당하는 생성된 데이터의 에러여부와 에러메시지를 담는다.
     * @throws Exception
     */
    private static Map<String, Object> getCheckMapForTable(Parameter param, Map<String, Object> map, List<Object> codeList, Locale locale, boolean valid) throws Exception {
        StringBuffer resultStr = new StringBuffer();
        Map<String, Object> valiMap = new LinkedHashMap<String, Object>();
        
        for (int j = 0; j < param.getColumnCount(); j++) {
            Column col = param.getColumn(j);
            
            String name = col.getName();
            String defaultval = col.getDefault();
            String trans = col.getTrans();
            String message = col.getMessage();
            String error = "";
            
            String source = StringHelper.null2void(getSourceString(map.get(name), col, codeList));
            if(source.length() > 0) {
            	valiMap.put("MESSAGE_" + name , source);  // name = MESSAGE_+cell번호
            }
            
            if(valid) {
	            if(!source.isEmpty()) {
	            	String chkstr = StringHelper.null2void(getSourceCheckForType(col, source, locale));
	                
	                if(!chkstr.isEmpty()) {
	                    resultStr.append(col.getDesc());
	                    resultStr.append(" : ");
	                    resultStr.append(chkstr);
	                }
	                
	                if (resultStr.length() > 0) {
	                    error = resultStr.substring(0, resultStr.length() - 1);
	                    valiMap.put("ERROR_YN", "Y");
	                } else {
	                    valiMap.put("ERROR_YN", "N");
	                }
	                
	                valiMap.put("ERROR_MESSAGE", error);
	            }
            } else {
            	if(log.isInfoEnabled()) log.info("[Interface Validation Method] Ignore validation....");
            	
            	valiMap.put("ERROR_YN", "N");
            	valiMap.put("ERROR_MESSAGE", error);
            }
            
            if(map.get(name) != null) {
                // 플랜트 코드가 있는 경우에는 DIVISION_CD값을 생성한다.	
                if(name.equals("DIVISION_CD")) {
                    valiMap.put(name, StringHelper.null2string(source, defaultval));
                }
            }
            
            valiMap.put(name, StringHelper.null2string(source, defaultval));
            valiMap.put(trans, StringHelper.null2string(source, defaultval));
            valiMap.put("NAME_CODE", name);
            valiMap.put("TRANS_CODE", trans);
            valiMap.put("XL_VALUE", StringHelper.null2string(source, defaultval));
            
            if(message != null) valiMap.put("MESSAGE_VIEW", message);
            if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
            if(col.getAlert() != null) valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), map));
        }
        
        return valiMap;
    }
    
    
    
    /**
     * <p>
     * JCO XML의 Table타입의 Parameter Name에 해당하는 데이터의 유효성 체크와 등록할 값 생성<br>
     * </p>
     * @param param Mapping 시킬 파라메터명
     * @param map   실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param codeList 기본코드값을 명칭으로 변경하기 위한 코드리스트
     * @param locale   다국어
     * @param valid    유효성 체크할지 여부
     * @return      Map에 해당하는 생성된 데이터의 에러여부와 에러메시지를 담는다.
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List getCheckListForTable(Parameter param, Map<String, Object> map, List<Object> codeList, Locale locale, boolean valid) throws Exception {
        List vlist = new LinkedList();
        
        for (int j = 0; j < param.getColumnCount(); j++) {
            Column col = param.getColumn(j);
            
            String name = col.getName();
            String defaultval = col.getDefault();
            String trans = col.getTrans();
            String message = col.getMessage();
            String error = "";
            StringBuffer resultStr = new StringBuffer();
            Map<String, Object> valiMap = new LinkedHashMap<String, Object>();
            
            String source = StringHelper.null2void(getSourceString(map.get(name), col, codeList));
            if(source.length() > 0) {
            	valiMap.put("MESSAGE_" + name , source);  // name = MESSAGE_+cell번호
            }
            
            if(valid) {
	            if(source != null) {
	                resultStr.append(getSourceCheckForType(col, source, locale));
	                
	                if (resultStr.length() > 0) {
	                    error = resultStr.substring(0, resultStr.length() - 1);
	                    valiMap.put("ERROR_YN", "Y");
	                } else {
	                    valiMap.put("ERROR_YN", "N");
	                }
	                
	                valiMap.put("ERROR_MESSAGE", col.getDesc() + ":" +error);
	            }
            } else {
            	if(log.isInfoEnabled()) log.info("[Interface Validation Method] Ignore validation....");
            	
            	valiMap.put("ERROR_YN", "N");
            	valiMap.put("ERROR_MESSAGE", col.getDesc() + ":" +error);
            }
            
            if(map.get(name) != null) {
                // 플랜트 코드가 있는 경우에는 DIVISION_CD값을 생성한다.
                if(name.equals("DIVISION_CD")) {
                    valiMap.put(name, StringHelper.null2string(source, defaultval));
                }
            }
            
            valiMap.put("DESCRIPTION", col.getDesc());
            valiMap.put("TABLE_NAME", param.getName());
            valiMap.put(name, StringHelper.null2string(source, defaultval));
            valiMap.put(trans, StringHelper.null2string(source, defaultval));
            valiMap.put("NAME_CODE", name);
            valiMap.put("TRANS_CODE", trans);
            valiMap.put("XL_VALUE", StringHelper.null2string(source, defaultval));
            if(message != null) valiMap.put("MESSAGE_VIEW", message);
            if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
            if(col.getAlert() != null) valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), map));
            
            vlist.add(valiMap);
        }
        
        return vlist;
    }
    
    /**
     * <p>
     * JCO XML의 ITable타입의 Parameter Name에 해당하는 데이터의 유효성 체크와 등록할 값 생성<br>
     * row단위의 엑셀의 cell정보를 추가로 저장한다.
     * </p>
     * @param param Mapping 시킬 파라메터명
     * @param map   실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param codeList 기본코드값을 명칭으로 변경하기 위한 코드리스트
     * @param locale   다국어
     * @param valid    유효성 체크할지 여부
     * @return      Map에 해당하는 생성된 데이터의 에러여부와 에러메시지를 담는다.
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List getCheckListForITable(Parameter param,  Map<String, Object> map, List<Object> codeList, Locale locale, boolean valid) throws Exception {
        List vlist = new LinkedList();
        int offset = -1;
        boolean checkout = true;
        
        while(checkout) {
            StringBuffer resultStr = null;
            checkout = false;
            Map<String, Object> valiMap = null;
            
            for (int j = 0; j < param.getColumnCount(); j++) {
                valiMap = new LinkedHashMap<String, Object>();
                resultStr = new StringBuffer();
                
                Column col = param.getColumn(j);
                
                String name = col.getName();
                String defaultval = col.getDefault();
                String trans = col.getTrans();
                String message = col.getMessage();
                if(offset == -1) {
                    offset = col.getOffset();
                }
                
                String vname = name + offset;
                Object obj = map.get(vname);
                
                String error = null;
                String source = StringHelper.null2void(getSourceString(obj, col, codeList));
                if(source != null) {
                    checkout = true;
                }
                
                //log.debug("name = " + vname + ", source = " + source + ", checkout = " + checkout);
                if(valid) {
	                String checkStr = StringHelper.null2void(getSourceCheckForType(col, source, locale));
	                if(checkStr.length() > 0) {
	                	valiMap.put("MESSAGE_" + vname , checkStr);  // name = MESSAGE_+cell번호
	                	checkStr = col.getDesc() + ":" + checkStr;
	                }
	                
	                resultStr.append(checkStr);
	                
	                if (resultStr.length() > 0) {
	                    error = resultStr.substring(0, resultStr.length() - 1);
	                    valiMap.put("ERROR_YN", "Y");
	                } else {
	                    valiMap.put("ERROR_YN", "N");
	                }
	                
	                valiMap.put("ERROR_MESSAGE", error);
                } else {
                	if(log.isInfoEnabled()) log.info("[Interface Validation Method] Ignore validation....");
                	
                	valiMap.put("ERROR_YN", "N");
                	valiMap.put("ERROR_MESSAGE", error);
                }
                
                if(obj != null) {
                    // 플랜트 코드가 있는 경우에는 DIVISION_CD값을 생성한다.
                    if(name.equals("DIVISION_CD")) {
                        valiMap.put(name, StringHelper.null2string(source, defaultval));
                    }
                }
                
                valiMap.put("DESCRIPTION", col.getDesc());
                valiMap.put("TABLE_NAME", param.getName());
                valiMap.put(trans, StringHelper.null2string(source, defaultval));
                valiMap.put(vname, StringHelper.null2string(source, defaultval));
                if(col.getAlert() != null)  valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), map, offset));
                
                // 엑셀정보 생성
                valiMap.put("XL_CELL", vname);
                valiMap.put("XL_SHEET", param.getDesc());
                valiMap.put("XL_FIELD", col.getDesc());
                valiMap.put("UPLOAD_SEQ", (offset-(col.getOffset()-1)));
                valiMap.put("XL_VALUE", StringHelper.null2string(source, defaultval));
                if(message != null) valiMap.put("MESSAGE_VIEW", message);
                if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
                
                if(checkout) {
                    vlist.add(valiMap);
                }
            }
            
            offset++;
        }
        
        return vlist;
    }
    
    /**
     * <p>
     * JCO XML의 ITable타입의 Parameter Name에 해당하는 데이터의 유효성 체크와 등록할 값 생성<br>
     * row단위의 엑셀의 cell정보를 추가로 저장한다. 
     * </p>
     * @param param Mapping 시킬 파라메터명
     * @param map   실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param codeList 기본코드값을 명칭으로 변경하기 위한 코드리스트
     * @param locale   다국어
     * @param valid    유효성 체크할지 여부
     * @return      Map에 해당하는 생성된 데이터의 에러여부와 에러메시지를 담는다.
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List getCheckMapForITable(Parameter param,  Map<String, Object> map, List<Object> codeList, Locale locale, boolean valid) throws Exception {
        List vlist = new LinkedList();
        int offset = -1;
        boolean checkout = true;
        
        while(checkout) {
            StringBuffer resultStr = new StringBuffer();
            checkout = false;
            Map<String, Object> valiMap = new LinkedHashMap<String, Object>();
            
            for (int j = 0; j < param.getColumnCount(); j++) {
                Column col = param.getColumn(j);
                
                String name = col.getName();
                String defaultval = col.getDefault();
                String trans = col.getTrans();
                String message = col.getMessage();
                if(offset == -1) {
                    offset = col.getOffset();
                }
                
                String vname = name + offset;
                Object obj = map.get(vname);
                
                String error = null;
                String source = StringHelper.null2void(getSourceString(obj, col, codeList));
                if(source != null && !source.isEmpty()) {
                    checkout = true;
                }
                
                //log.debug("name = " + vname + ", source = " + source + ", checkout = " + checkout);
                if(valid) {
	                String checkStr = StringHelper.null2void(getSourceCheckForType(col, source, locale));
	                if(checkStr.length() > 0) {
	                	valiMap.put("MESSAGE_" + vname , checkStr);  // name = MESSAGE_+cell번호
	                	checkStr = col.getDesc() + ":" + checkStr;
	                }
	                
	                resultStr.append(checkStr);
	                
	                if (resultStr.length() > 0) {
	                    error = resultStr.substring(0, resultStr.length() - 1);
	                    valiMap.put("ERROR_YN", "Y");
	                } else {
	                    valiMap.put("ERROR_YN", "N");
	                }
	                
	                valiMap.put("ERROR_MESSAGE", error);
                } else {
                	if(log.isInfoEnabled()) log.info("[Interface Validation Method] Ignore validation....");
                	
                	valiMap.put("ERROR_YN", "N");
                	valiMap.put("ERROR_MESSAGE", error);
                }
                
                if(obj != null) {    
                    // 플랜트 코드가 있는 경우에는 DIVISION_CD값을 생성한다.
                    if(name.equals("DIVISION_CD")) {
                        valiMap.put(name, StringHelper.null2string(source, defaultval));
                    }
                }
                
                valiMap.put("DESCRIPTION", col.getDesc());
                valiMap.put("TABLE_NAME", param.getName());
                valiMap.put(trans, StringHelper.null2string(source, defaultval));
                valiMap.put(vname, StringHelper.null2string(source, defaultval));
                if(col.getAlert() != null) valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), map, offset));
                
                // 엑셀정보 생성
                valiMap.put("XL_CELL", vname);
                valiMap.put("XL_SHEET", param.getDesc());
                valiMap.put("XL_FIELD", col.getDesc());
                valiMap.put("XL_VALUE", StringHelper.null2string(source, defaultval));
                valiMap.put("UPLOAD_SEQ", (offset-(col.getOffset()-1)));
                if(message != null) valiMap.put("MESSAGE_VIEW", message);
                if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
            }
            
            if(checkout) {
                vlist.add(valiMap);
            }
            
            offset++;
        }
        
        return vlist;
    }

    /**
     * <p>
     * JCO XML의 Structure타입의 Parameter Name에 해당하는 데이터의 유효성 체크와 등록할 값 생성<br>
     * column타입이 json인 경우 분리해서 validation을 체크하고 각 속성 값을 체크 결과를 List에 추가한다.<br>
     * </p>
     * @param param Mapping 시킬 파라메터명
     * @param map   실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @return      Map에 해당하는 생성된 데이터의 에러여부와 에러메시지를 담는다.
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List getCheckListForStructure(Parameter param, Map<String, Object> map, List<Object> codeList, Locale locale, boolean valid) throws Exception {
        List vlist = new LinkedList();
        
        for (int j = 0; j < param.getColumnCount(); j++) {
            Column col = param.getColumn(j);
            
            String name = col.getName();
            int type = col.getType();
            String trans = StringHelper.null2string(col.getTrans(), "{}");
            String leng = "{}"; // json타입으로 지원되지 않음 - StringHelper.null2string(col.getLength(), "{}");
            String decimals = "{}"; // json타입으로 지원되지 않음 - StringHelper.null2string(col.getDecimals(), "{}");
            String format = StringHelper.null2string(col.getFormat(), "{}");
            String required = StringHelper.null2string(col.getRequired(), "{}");
            String desc = col.getDesc();
            String message = col.getMessage();
            String source = null;
            
            Map<String, Object> valiMap = new LinkedHashMap<String, Object>();
            
            if(type == JCO.TYPE_XSTRING) { // json 타입일 경우
                Map targetMap = JsonUtil.getMap(trans);
                Map lengMap = JsonUtil.getMap(leng);
                Map decimalsMap = JsonUtil.getMap(decimals);
                Map formatMap = JsonUtil.getMap(format);
                Map requiredMap = JsonUtil.getMap(required);
                List messageList = null;
                if(message != null) messageList = JsonUtil.getList(message);
                
                if(targetMap.size() > 0) {
                	Map valiMstMap = new LinkedHashMap<String, Object>();
                    
                    for(Iterator it = targetMap.entrySet().iterator(); it.hasNext();) {
                        Map.Entry entry = (Map.Entry) it.next();
                        String key = StringHelper.null2void(entry.getKey());
                        String value = StringHelper.null2void(targetMap.get(key));
                        
                        if(value.startsWith("#")) {
                            source = value.replaceAll("#", "");
                            valiMstMap.put(key, source);
                        }
                    }
                    
                    int idx = 1;
                    
                    for(Iterator it = targetMap.entrySet().iterator(); it.hasNext();) {
                    	StringBuffer resultStr = new StringBuffer();
                    	valiMap = new LinkedHashMap<String, Object>();
                        
                        valiMap.putAll(valiMstMap);
                        
                        Map.Entry entry = (Map.Entry) it.next();
                        String key = StringHelper.null2void(entry.getKey());
                        String value = StringHelper.null2void(targetMap.get(key));
                        
                        if(!value.startsWith("#")) {
                            source = StringHelper.null2void(getSourceString(map.get(value), col, codeList));
                            
                            // 유효성 체크에 대한 메시지를 등록한다.
                            int leng_atr = -1;
                            int decimals_atr = -1;
                            String format_atr = null;
                            String required_atr = "N"; 
                            
                            // 문자열 길이
                            if(lengMap.size() > 0) {
                                for (Iterator re = lengMap.entrySet().iterator(); re.hasNext();) {
                                    Map.Entry re_entry = (Map.Entry) re.next();
                                    String re_key = StringHelper.null2void(re_entry.getKey());
                                    int re_value = StringHelper.null2zero(lengMap.get(re_key));
                                    
                                    if(key.equals(re_key)) {
                                    	leng_atr = re_value;
                                    	break;
                                    }
                                }
                            }
                            // 소수점
                            if(decimalsMap.size() > 0) {
                                for (Iterator re = decimalsMap.entrySet().iterator(); re.hasNext();) {
                                    Map.Entry re_entry = (Map.Entry) re.next();
                                    String re_key = StringHelper.null2void(re_entry.getKey());
                                    int re_value = StringHelper.null2zero(decimalsMap.get(re_key));
                                    
                                    if(key.equals(re_key)) {
                                    	decimals_atr = re_value;
                                    	break;
                                    }
                                }
                            }
                            // format
                            if(formatMap.size() > 0) {
                                for (Iterator re = formatMap.entrySet().iterator(); re.hasNext();) {
                                    Map.Entry re_entry = (Map.Entry) re.next();
                                    String re_key = StringHelper.null2void(re_entry.getKey());
                                    String re_value = StringHelper.null2void(formatMap.get(re_key));
                                    
                                    if(key.equals(re_key)) {
                                    	format_atr = re_value;
                                    	break;
                                    }
                                }
                            }
                            // 필수여부 체크
                            if(requiredMap.size() > 0) {
                                for (Iterator re = requiredMap.entrySet().iterator(); re.hasNext();) {
                                    Map.Entry re_entry = (Map.Entry) re.next();
                                    String re_key = StringHelper.null2void(re_entry.getKey());
                                    String re_value = StringHelper.null2void(requiredMap.get(re_key));
                                    
                                    if(key.equals(re_key)) {
                                    	required_atr = re_value;
                                    	break;
                                    }
                                }
                            }
                            
                            resultStr.append(StringHelper.null2void(getSourceCheckForColMessage(source, required_atr, leng_atr, decimals_atr, format_atr, desc, locale, messageList)));
                            
                            if(resultStr.length() > 0) {
                            	if(log.isDebugEnabled()) log.debug("check message(getCheckListForStructure) = " + resultStr.toString());
                            	valiMap.put("ERROR_MESSAGE", resultStr.toString().substring(0, resultStr.length() - 1));
                            	valiMap.put("ERROR_YN", "Y");
                            } else {
                            	valiMap.put("ERROR_MESSAGE", "");
                                valiMap.put("ERROR_YN", "N");
                            }
                            valiMap.put(key, source);
                            valiMap.put(name+"_"+idx, source);  // 사용자 화면에서 입력한 값과 맵핑하기 위해 추가
                            valiMap.put("XL_VALUE", source);
                            valiMap.put("DESCRIPTION", desc);
                            valiMap.put("NAME_CODE", name);
                            valiMap.put("TABLE_NAME", param.getName());
                            if(message != null) valiMap.put("MESSAGE_VIEW", message);
                            if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
                            if(col.getAlert() != null) valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), targetMap));
                            
                            vlist.add(valiMap);
                            
                            idx++;
                        }
                    }
                }
            } else {
            	String str = null;
            	source = StringHelper.null2void(getSourceString(map.get(name), col, codeList));
            	
            	if(valid) {
	                String error = getSourceCheckForType(col, source, locale);
	                
	                if (error.length() > 0) {
	                    valiMap.put("ERROR_YN", "Y");
	                    str = error.substring(0, error.length() - 1);
	                } else {
	                    valiMap.put("ERROR_YN", "N");
	                }
	                
	                valiMap.put("ERROR_MESSAGE", str);
            	} else {
                	if(log.isInfoEnabled()) log.info("[Interface Validation Method] Ignore validation....");
                	
                	valiMap.put("ERROR_YN", "N");
                	valiMap.put("ERROR_MESSAGE", str);
                }
                valiMap.put(trans, source);
                valiMap.put(name, source);
                valiMap.put("NAME_CODE", name);
                valiMap.put("XL_VALUE", source);
                if(message != null) valiMap.put("MESSAGE_VIEW", message);
                if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
                if(col.getAlert() != null) valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), map));
                
                vlist.add(valiMap);
            }
        }
        
        return vlist;
    }
    
    /**
     * <p>
     * JCO XML의 Table로 저장되는 parameter중 Structure타입 해당하는 데이터의 유효성 체크와 등록할 값 생성<br>
     * </p>
     * @param param Mapping 시킬 파라메터명
     * @param map   실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @return      Map에 해당하는 생성된 데이터의 에러여부와 에러메시지를 담는다.
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static int getCheckMapForStructure(Parameter param, Map<String, Object> map, List returnList, int listIndex, List<Object> codeList, Locale locale, boolean valid) throws Exception {
        for(int j = 0; j < param.getColumnCount(); j++) {
            Column col = param.getColumn(j);
            
            String name = col.getName();
            int type = col.getType();
            String trans = StringHelper.null2string(col.getTrans(), "{}");
            String leng = "{}"; // json타입으로 지원되지 않음
            String decimals = "{}"; // json타입으로 지원되지 않음
            String format = StringHelper.null2string(col.getFormat(), "{}");
            String required = StringHelper.null2string(col.getRequired(), "{}");
            String desc = col.getDesc();
            String message = col.getMessage();
            String source = null;
            
            Map<String, Object> valiMap = new LinkedHashMap<String, Object>();
            
            if(type == JCO.TYPE_XSTRING) { // json 타입일 경우
            	Map targetMap = JsonUtil.getMap(trans);
                Map lengMap = JsonUtil.getMap(leng);
                Map decimalsMap = JsonUtil.getMap(decimals);
                Map formatMap = JsonUtil.getMap(format);
                Map requiredMap = JsonUtil.getMap(required);
                List messageList = null;
                if(message != null) messageList = JsonUtil.getList(message);
                
                if(targetMap.size() > 0) {
                	StringBuffer resultStr = new StringBuffer();
                    int idx = 1;
                    
                    for(Iterator it = targetMap.entrySet().iterator(); it.hasNext();) {
                        Map.Entry entry = (Map.Entry) it.next();
                        String key = StringHelper.null2void(entry.getKey());
                        String value = StringHelper.null2void(targetMap.get(key));
                        
                        if(value.startsWith("#")) {
                            source = value.replaceAll("#", "");
                        } else {
                            source = StringHelper.null2void(getSourceString(map.get(value), col, codeList));
                            
                            valiMap.put(name+"_"+idx, source);  // 사용자 화면에서 입력한 값과 맵핑하기 위해 추가
                            idx++;
                        }
                        
                        // 유효성 체크에 대한 메시지를 등록한다.
                        int leng_atr = -1;
                        int decimals_atr = -1;
                        String format_atr = null;
                        String required_atr = "N";
                        
                        // 문자열 길이
                        if(lengMap.size() > 0) {
                            for (Iterator re = lengMap.entrySet().iterator(); re.hasNext();) {
                                Map.Entry re_entry = (Map.Entry) re.next();
                                String re_key = StringHelper.null2void(re_entry.getKey());
                                int re_value = StringHelper.null2zero(lengMap.get(re_key));
                                
                                if(key.equals(re_key)) {
                                	leng_atr = re_value;
                                	break;
                                }
                            }
                        }
                        // 소수점
                        if(decimalsMap.size() > 0) {
                            for (Iterator re = decimalsMap.entrySet().iterator(); re.hasNext();) {
                                Map.Entry re_entry = (Map.Entry) re.next();
                                String re_key = StringHelper.null2void(re_entry.getKey());
                                int re_value = StringHelper.null2zero(decimalsMap.get(re_key));
                                
                                if(key.equals(re_key)) {
                                	decimals_atr = re_value;
                                	break;
                                }
                            }
                        }
                        // format
                        if(formatMap.size() > 0) {
                            for (Iterator re = formatMap.entrySet().iterator(); re.hasNext();) {
                                Map.Entry re_entry = (Map.Entry) re.next();
                                String re_key = StringHelper.null2void(re_entry.getKey());
                                String re_value = StringHelper.null2void(formatMap.get(re_key));
                                
                                if(key.equals(re_key)) {
                                	format_atr = re_value;
                                	break;
                                }
                            }
                        }
                        // 필수여부 체크
                        if(requiredMap.size() > 0) {
                            for (Iterator re = requiredMap.entrySet().iterator(); re.hasNext();) {
                                Map.Entry re_entry = (Map.Entry) re.next();
                                String re_key = StringHelper.null2void(re_entry.getKey());
                                String re_value = StringHelper.null2void(requiredMap.get(re_key));
                                
                                if(key.equals(re_key)) {
                                	required_atr = re_value;
                                	break;
                                }
                            }
                        }
                        
                        resultStr.append(StringHelper.null2void(getSourceCheckForColMessage(source, required_atr, leng_atr, decimals_atr, format_atr, desc, locale, messageList)));
                        
                        if(resultStr.length() > 0) {
                        	if(log.isDebugEnabled()) log.debug("check message(getCheckListForStructure) = " + resultStr.toString());
                        	valiMap.put("ERROR_MESSAGE", resultStr.toString().substring(0, resultStr.length() - 1));
                        	valiMap.put("ERROR_YN", "Y");
                        } else {
                        	valiMap.put("ERROR_MESSAGE", "");
                            valiMap.put("ERROR_YN", "N");
                        }
                        valiMap.put(key, source);
                        valiMap.put("XL_VALUE", source);
                        valiMap.put("NAME_CODE", name);
                        if(message != null) valiMap.put("MESSAGE_VIEW", message);
                        if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
                        if(col.getAlert() != null) valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), targetMap));
                    }
                }
            } else {
            	String str = null;
                source = StringHelper.null2void(getSourceString(map.get(name), col, codeList));
                
                if(valid) {
	                String error = getSourceCheckForType(col, source, locale);
	                
	                if (error.length() > 0) {
	                    valiMap.put("ERROR_YN", "Y");
	                    str = error.substring(0, error.length() - 1);
	                } else {
	                    valiMap.put("ERROR_YN", "N");
	                }
	                
	                valiMap.put("ERROR_MESSAGE", str);
                } else {
                	if(log.isInfoEnabled()) log.info("[Interface Validation Method] Ignore validation....");
                	
                	valiMap.put("ERROR_YN", "N");
                	valiMap.put("ERROR_MESSAGE", str);
                }
                
                valiMap.put(trans, source);
                valiMap.put(name, source);
                valiMap.put("NAME_CODE", name);
                valiMap.put("XL_VALUE", source);
                if(message != null) valiMap.put("MESSAGE_VIEW", message);
                if(col.getBaseCode() != null) valiMap.put("XL_BASECODE", col.getBaseCode());
                if(col.getAlert() != null) valiMap.put("ALERT_MESSAGE", getAlertMessage(col.getAlert(), map));
            }
            
            valiMap.put("DESCRIPTION", desc);
            valiMap.put("TABLE_NAME", param.getName());
            
            returnList.add(listIndex, valiMap);
            listIndex++;
        }
        
        return listIndex;
    }
    
    /**
     * 유효성 체크를 수행하여 결과를 문자열로 리턴
     * 
     * @param source = 비교할 문자열 값
     * @param required_atr = 필수항목 여부
     * @param leng_atr = 문자열 길이
     * @param decimals_atr = 소수점 자리수
     * @param format_atr = 문자열 format
     * @param desc = 설명문
     * @param locale = 적용언어
     * @param messageList = XML에 설정된 메시지 항목
     * @return
     */
    @SuppressWarnings("rawtypes")
	private static String getSourceCheckForColMessage(String source, String required_atr, int leng_atr, int decimals_atr, String format_atr, String desc
    		, Locale locale, List messageList) throws Exception {
    	MessageResource msg = MessageResource.getMessageInstance();
    	StringBuffer resultStr = new StringBuffer();
    	 
    	if(messageList != null && messageList.size() > 0) {
			for(int m = 0; m < messageList.size(); m++) {
				Map messageMap = (Map) messageList.get(m);
				
        		String msgtype = StringHelper.null2void(messageMap.get("MESSAGE_TYPE"));
				String msgname = StringHelper.null2void(messageMap.get("MESSAGE_NAME"));
        		String msgarg1 = StringHelper.null2void(messageMap.get("MESSAGE_ARG1"));
        		String msgarg2 = StringHelper.null2void(messageMap.get("MESSAGE_ARG2"));
        		String msgarg3 = StringHelper.null2void(messageMap.get("MESSAGE_ARG3"));
        		String msgarg4 = StringHelper.null2void(messageMap.get("MESSAGE_ARG4"));
        		String msgarg5 = StringHelper.null2void(messageMap.get("MESSAGE_ARG5"));
        		String[] arg = null;
        		int argcount = 0;
        		
        		if(!msgarg1.isEmpty()) argcount++;
        		if(!msgarg2.isEmpty()) argcount++;
        		if(!msgarg3.isEmpty()) argcount++;
        		if(!msgarg4.isEmpty()) argcount++;
        		if(!msgarg5.isEmpty()) argcount++;
        		
        		if(msgname.startsWith("#")) msgname = msgname.substring(1, msgname.length());
        		if(argcount > 0) {
        			arg = new String[argcount];
        			
            		if(!msgarg1.isEmpty()) {
            			if(msgarg1.startsWith("#")) arg[0] = msgarg1.substring(1, msgarg1.length());
            			else arg[0] = StringHelper.null2void(msg.getMessage(msgarg1, null, locale));
            		}
            		if(!msgarg2.isEmpty()) {
            			if(msgarg2.startsWith("#")) arg[1] = msgarg2.substring(1, msgarg2.length());
            			else arg[1] = StringHelper.null2void(msg.getMessage(msgarg2, null, locale));
            		}
            		if(!msgarg3.isEmpty()) {
            			if(msgarg3.startsWith("#")) arg[2] = msgarg3.substring(1, msgarg3.length());
            			else arg[2] = StringHelper.null2void(msg.getMessage(msgarg3, null, locale));
            		}
            		if(!msgarg4.isEmpty()) {
            			if(msgarg4.startsWith("#")) arg[3] = msgarg4.substring(1, msgarg4.length());
            			else arg[3] = StringHelper.null2void(msg.getMessage(msgarg4, null, locale));
            		}
            		if(!msgarg5.isEmpty()) {
            			if(msgarg5.startsWith("#")) arg[4] = msgarg5.substring(1, msgarg5.length());
            			else arg[4] = StringHelper.null2void(msg.getMessage(msgarg5, null, locale));
            		}
        		}
        		
        		if(msgtype.endsWith("IS_REQUIRED") && !DataHelper.compareRequired(source, required_atr)) {
        			if(msgname.isEmpty()) msgname = "ERROR_REQUIRED_YN";
        			resultStr.append(msg.getMessage(msgname, arg, locale) +",");
        		} else if(msgtype.endsWith("IS_DATE") && !DataHelper.isDate(source, format_atr)) {
        			if(msgname.isEmpty()) msgname = "ERROR_INVALID_DATE";
        			resultStr.append(msg.getMessage(msgname, arg, locale) +",");
        		} else if(msgtype.endsWith("IS_NUMBER") && !DataHelper.isNumberValid(source)) {
        			if(msgname.isEmpty()) msgname = "ERROR_INVALID_NUMBER";
        			resultStr.append(msg.getMessage(msgname, arg, locale) +",");
        		} else if(msgtype.endsWith("IS_LENGTH") && !DataHelper.compareLength(source, leng_atr, decimals_atr)) {
        			if(msgname.isEmpty()) msgname = "ERROR_INVALID_LENGTH";
        			resultStr.append(msg.getMessage(msgname, arg, locale) +",");
        		}
			}
    	} else {
    		if(!DataHelper.compareRequired(source, required_atr)) {
    			resultStr.append(msg.getMessage("ERROR_REQUIRED_YN", new Object[]{msg.getMessage(desc, null, locale)}, locale) +",");
    		}
    		if(!DataHelper.isDate(source, format_atr)) {
    			resultStr.append(msg.getMessage("ERROR_INVALID_DATE", new Object[]{msg.getMessage(desc, null, locale)}, locale) +",");
    		}
    		if(!DataHelper.compareLength(source, leng_atr, decimals_atr)) {
    			resultStr.append(msg.getMessage("ERROR_INVALID_LENGTH", new Object[]{msg.getMessage(desc, null, locale)}, locale) +",");
    		}
    	}
    	
		return resultStr.toString();
    }
    
    /**
     * 객체 타입에 맞게 String으로 변환시킴
     * 
     * @param obj value 객체
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getSourceString(Object obj, Column col, List<Object> codeList) {
        String source = null;
        int type = col.getType();
        int offset = col.getOffset();
        
        if(obj != null) {
            // 소스데이터 형식에 맞게 형변환을 한다.(MS-SQL에서는 String을 포함하지 않는 다양한 형식이 지원된다.)
            if(obj instanceof Integer) {
                source = Integer.toString((Integer)obj);
            } else if(obj instanceof Long) {
                source = Long.toString((Long)obj);
            } else if(obj instanceof Float) {
                source = Float.toString((Float)obj);
            } else if(obj instanceof Double) {
                source = Double.toString((Double)obj);
            } else if(obj instanceof Date || type == JCO.TYPE_DATE) {
            	String fm = StringHelper.null2string(col.getFormat(), "yyyyMMdd");
                source = DateHelper.formatter(StringHelper.null2void(obj).replace("-", ""), fm);
            } else if(obj instanceof BigInteger) {
                source = String.valueOf(obj);
            } else if(obj instanceof BigDecimal) {
                source = String.valueOf(obj);
            } else if(obj instanceof BigInteger) {
                source = String.valueOf(obj);
            } else {
            	source = obj.toString();
            	
            	// 인터페이스된 정보는 최대길이로 잘라낸다.(2017.12.13, YNI-Maker)
            	if(source.length() > offset) {
            		source = source.substring(0, offset);
            	}
            }
            
            if(codeList != null && codeList.size() > 0 && source != null) {
            	String basecode = StringHelper.null2void(col.getBaseCode());
            	
            	if(!basecode.isEmpty()) {
	                for(int i = 0; i < codeList.size(); i++) {
	                    Map map = (Map) codeList.get(i);
	                    
	                    String code = StringHelper.null2void(map.get("CODE"));
	                    String name = StringHelper.null2void(map.get("NAME")).trim().replaceAll(" ", "").toUpperCase();
	                    String id = StringHelper.null2void(map.get("ID"));
	                    
	                    if(code.equals(basecode) && name.equals(source.trim().replaceAll(" ", "").toUpperCase())) {
	                        source = id;
	                        break;
	                    }
	                }
            	}
            }
        }
        
        return source;
    }
    
    /**
     * Source값의 유효성을 체크한 후 에러메시지 리턴
     * @param col XML의 컬럼 객체
     * @param source 체크할 대상 값
     * @param locale 설정된 언어
     * @return validation 체크 결과(오류가 없는 경우 빈값을 리턴한다.)
     */
    public static String getSourceCheckForType(Column col, String source, Locale locale) {
        StringBuffer resultStr = new StringBuffer();
        MessageResource msg = MessageResource.getMessageInstance();
        
        int type = col.getType();
        int leng = col.getLength();
        int decimals = col.getDecimals();
        String format = col.getFormat();
        String desc = col.getDesc();
        String required = col.getRequired();
        
        if(source != null && !source.isEmpty()) {
            if (type == JCO.TYPE_DATE) { // 날짜 형식인 경우
                if (!DataHelper.isDate(source, format)) {
                    resultStr.append(msg.getMessage("ERROR_INVALID_DATE", new Object[]{msg.getMessage(desc, null, locale)}, locale) + ",");
                }
            } else if (type == JCO.TYPE_FLOAT || type == JCO.TYPE_NUM) { // 숫자타입인 경우
                if (!DataHelper.isNumberValid(source)) {
                    resultStr.append(msg.getMessage("ERROR_INVALID_NUMBER", new Object[]{msg.getMessage(desc, null, locale)}, locale) + ",");
                }
                if (!DataHelper.compareLength(source, leng, decimals)) {
                    resultStr.append(msg.getMessage("ERROR_INVALID_LENGTH", new Object[]{msg.getMessage(desc, null, locale)}, locale) + "[max:"+leng+" | decimal:"+decimals+"],");
                }
            } else if (type == JCO.TYPE_XSTRING) { // json 타입
                ; // json타입은 체크하지 않음
            } else if (type == JCO.TYPE_BYTE) { // byte 타입
                ; // json타입은 체크하지 않음
            } else { // 기타는 문자타입으로 처리
                if (!DataHelper.compareLength(source, leng)) {
                    resultStr.append(msg.getMessage("ERROR_INVALID_LENGTH", new Object[]{msg.getMessage(desc, null, locale)}, locale) + "[max:"+leng+"],");
                }
            }
        } else {
        	if (!DataHelper.compareRequired(source, required)) {
                resultStr.append(msg.getMessage("ERROR_REQUIRED_YN", new Object[]{msg.getMessage(desc, null, locale)}, locale) + ",");
            }
        }
        
        return resultStr.toString();
    }
    
    /**
     * <p>
     * 알림 메시지를 찾아 리턴한다.<br>
     * </p>
     * @param alert 알림 메시지 또는 key
     * @param map   실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @return      알림 메시지
     * @throws Exception
     */
    private static String getAlertMessage(String alert, Map<String, Object> map) {
        return getAlertMessage(alert, map, 0);
    }
    
    /**
     * <p>
     * 알림 메시지를 찾아 리턴한다.<br>
     * </p>
     * @param alert 알림 메시지 또는 key
     * @param map   실제 값을 담고 있는 Map 객체(column 요소의 name속성과 mapping됨)
     * @param index ITable의 row번호
     * @return      알림 메시지
     * @throws Exception
     */
    private static String getAlertMessage(String alert, Map<String, Object> map, int index) {
        String message = null;
        
        if(alert == null) message = "";
        else {
            if(map == null || map.size() < 1) message = alert;
            else {
            	String strtype = "";
            	StringTokenizer st = null;
            	StringBuffer returnMsg = new StringBuffer();
            	String indexStr = (index <= 0) ? "":Integer.toString(index);
            	
            	if(alert.indexOf("|") > 0) {
		            st = new StringTokenizer(alert, "|");
		            strtype = "OR";
            	} else if(alert.indexOf("&") > 0) {
            	    st = new StringTokenizer(alert, "&");
            	    strtype = "AND";
            	} else {
            		st = new StringTokenizer(alert, "^");
            		strtype = "NONE";
            	}
            	
            	if(st != null) {
            		String msg = null;
            		
	            	while(st.hasMoreTokens()) {
						String key = StringHelper.null2void(st.nextToken());
						msg = StringHelper.null2void(map.get(key+indexStr));
		            	
		            	if(!msg.isEmpty()) {
		            		if(key.startsWith("#")) {
		                        returnMsg.append(key);
		                        returnMsg.append(",");
		                    } else if(!msg.isEmpty()) {
		                        returnMsg.append(msg);
		                        returnMsg.append(",");
		                    }
		            	}
		            	
		            	if(strtype.equals("OR") && !msg.isEmpty()) break;
		            }
            	}
            	
                message = returnMsg.toString();
                
                if(message.length() > 1) message = message.substring(0, returnMsg.length()-1);
            }
        }
        
        return message;
    }
    
    public static class DataHelper {
        /**
         * 필수 여부를 체크 한다.
         * 
         * @param source
         *            문자열
         * @param required
         *            필수여부
         * @return 필수여부 체크결과
         */
        public static boolean compareRequired(String source, String required) {
            if ("Y".equals(required) && (source == null || source.isEmpty())) {
                return false;
            }
            
            return true;
        }

        /**
         * 문자열의  길이 체크 한다. (한글은 2byte 취급)
         * 문자길이를 -1로 설정하면 체크하지 않음
         * @param source 문자열
         * @param leng 문자길이
         * @return 문자열의 길이 체크 결과
         */
        public static boolean compareLength(String source, int leng) {
        	if(leng == -1) return true;
        	
            return compareLength(source, leng, 0);
        }
        
        /**
         * 문자열의  길이 체크 한다. (본래 BYTE는 한글은 2byte 취급하나, DB를 NVARCHAR로 설정했기 때문에, 문자열의 길이만 점검한다.)
         * 문자길이와 소수점 모두를 -1로 설정하면 체크하지 않음
         * @param source 문자열
         * @param leng 문자길이
         * @param decimals 소수점 길이
         * @return
         */
        public static boolean compareLength(String source, int leng, int decimals) {
        	if (source == null || source.isEmpty()) {
                return true;
            }
        	
        	if(leng == -1 && decimals == -1) return true;
        	
            if(decimals == 0) {
                int sourceLength = source.length();
                if (sourceLength > leng) {
                    return false;
                }
            } else { // 숫자 중 실수형인 데이터의 유효성을 검증함(oracle number(??,??)타입의 형식을 따름)
                String[] splitSource = new String[2];
                
                int idx = 0;
                StringTokenizer stk = new StringTokenizer(source, ".");
                while(stk.hasMoreElements()) {
                    splitSource[idx] = stk.nextToken();
                    idx++;
                }
                
                if(splitSource[1] != null) {
                    int integer = splitSource[0].length();
                    if (integer > (leng - decimals)) {
                        return false;
                    }
                    int decimal = splitSource[1].length();
                    if (decimal > decimals) {
                        return false;
                    }
                } else {
                    int sourceLength = source.length();
                    if (sourceLength > leng) {
                        return false;
                    }
                }
            }
            
            return true;
        }
        
        /**
         * 문자열의 Number형 문자열인지 여부 (- 기호나 소수점도 포함)
         * 
         * @param source
         *            검증 하고자 하는 문자열
         * @return 숫자형 문자열 여부 (true : 숫자형)
         */
        public static boolean isNumberValid(String source) {
        	if (source == null || source.isEmpty()) {
                return true;
            }

            try {
                Double db = new Double(source);
                if (db.isNaN()) {
                    return false;
                } else {
                    return true;
                }
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        
        /**
         * 문자열의 Number형 문자열인지 여부 (- 기호나 소수점도 포함)
         * 
         * @param source
         *            검증 하고자 하는 문자열
         * @return 숫자형 문자열 여부 (true : 숫자형)
         */
        public static boolean isNumber(String source) {
        	if (source == null || source.isEmpty()) {
                return true;
            }

            try {
                Double db = new Double(source);
                if (db.isNaN()) {
                    return false;
                } else {
                    return true;
                }
            } catch (NumberFormatException ex) {
                return false;
            }
        }

        /**
         * 해당 문자열이 주어진 일자 형식을 준수하는지 여부를 검사한다.
         * 
         * @param source
         *            검사할 대상 문자열
         * @param format
         *            Date 형식의 표현. 예) "yyyy-MM-dd".
         * @return 형식을 검색한 결과
         */
        public static boolean isDate(String source, String format) {
        	if (format == null) {
                return true;
            }
        	
            if (source == null || source.isEmpty()) {
                if (log.isDebugEnabled()) log.debug("date string to check is null");
                
                return true;
            }

            SimpleDateFormat formatter = new SimpleDateFormat(format, SystemHelper.getLocale(null));
            
            Date date = null;

            try {
                date = formatter.parse(source);
            } catch (ParseException e) {
                if (log.isDebugEnabled()) {
                    log.debug(" wrong date:\"" + source + "\" with format \""
                            + format + "\"");
                }
                return false;
            }

            if (!formatter.format(date).equals(source)) {
                if (log.isDebugEnabled()) {
                    log.debug("Out of bound date:\"" + source
                            + "\" with format \"" + format + "\"");
                }
                return false;
            }

            return true;
        }
    }
	
}
