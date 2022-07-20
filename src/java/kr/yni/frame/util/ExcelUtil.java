package kr.yni.frame.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;

import kr.yni.frame.Constants;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.vo.ValueObject;

/**
 * <p>
 * 엑셀 기능을 제공하는 Util 클래스
 * </p>
 * @author YNI-maker
 */
public class ExcelUtil {
    
    private static Log log = LogFactory.getLog(ExcelUtil.class);
    
    /**
     * <code>List</code>객체를 <code>ValueObject</code>로 변환한다.
     * 
     * @param list
     * @param name
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ValueObject transformVO(List list, String name) {
        ValueObject vo = new ValueObject(name);
        for(int i = 0; i < list.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) list.get(i);
            vo.addRow(i, map);
        }
        if(log.isDebugEnabled()) log.debug("vo size = " + vo.size());
        return vo;
    }
    
    /**
     * Map정보에서 컬럼명을 생성한다.
     * @param map 해더에 출력할 Map정보
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String[] transformColumns(Map<String, Object> map) {
        String[] columns = new String[map.size()];
        
        int i = 0;
        Iterator iter = map.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            
            if(!key.equals("TOTALCOUNT") && !key.equals("PG_CNT")) {
                columns[i] = key;
                i++;
            }
        }
        
        if(log.isDebugEnabled()) log.debug("column 0 check = " + columns[0]);
        
        return columns;
    }
    
    /**
	 * 엑셀파일을 작성하기 위한 해더 정보 set
	 * <br>header정보를 List의 마지막 정보에 기록된다.
	 * 
	 * @param resutList 엑셀에 출력할 data 정보
	 * @param parameterObject json타입의 header 정보
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List setHeaderColumns(List plist, Object parameterObject) {
		List resutList = null;
		
		if(plist == null) {
			resutList = new LinkedList();
		} else {
			resutList = (List) DataMapHelper.toUpperCaseObject(plist);
		}
		
		if(parameterObject instanceof Map) {
			Map map = (Map) parameterObject;
			
			try {
				String columns = StringHelper.null2void(map.get("headers"));
				
				if(!StringHelper.isNull(columns)) {
					JSONArray jsonArray = JSONArray.fromObject(columns);
					int level = jsonArray.size();
					
					if(log.isDebugEnabled()) log.debug("json array size = " + level + " / columns : " + columns);
					
					List list = new LinkedList();
					
					for(int idx = 0 ; idx < level ; idx++) {
						String jsonString = StringHelper.null2void(JsonUtil.fromObject(columns).get(idx));
						
						jsonArray = JsonUtil.fromObject(jsonString);
						
						List colList = new LinkedList();
						
						for(int i = 0 ; i < jsonArray.size(); i++) {
							colList.add(JsonUtil.getList(jsonString, i).get(0));
						}
						
						list.add(colList);
					}
					
					if(list != null && list.size() > 0) {
						Map columMap = new HashMap();
						String file = StringHelper.null2void(map.get("filename"));
						String sheet = StringHelper.null2void(map.get("sheetname"));
						
						columMap.put("headers", list);
						columMap.put("file", file);
						columMap.put("sheet", sheet);
						
						List cols = (List) list.get(0);
						
						if(log.isDebugEnabled()) log.debug("excel file header(total size = "+list.size()+
								", column number=" + cols.size() + "):" + cols.get(0)+
								", file name = " + file + ", sheet name = " + sheet);
						
						resutList.add(columMap);
					}
				}
			} catch(Exception e) {
				if(log.isErrorEnabled()) log.error("json convertion error : " + e);
			}
		}
		
		return resutList;
	}
	
	/**
	 * 엑셀파일 작성 시 hreader정보를 구한다.
	 * 
	 * @param list 엑셀에 출력할 Data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List setHeaderInfo(List<Map<String, Object>> list) {
		if(list == null  || list.size() <= 0) return new LinkedList();
		else return setHeaderInfo(list.get(0), list, null, null);
	}
	
	/**
	 * 엑셀파일 작성 시 hreader정보를 구한다.
	 * 
	 * @param list 엑셀에 출력할 Data
	 * @param fname 엑셀 파일명
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List setHeaderInfo(List<Map<String, Object>> list, String fname) {
		if(list == null  || list.size() <= 0) return new LinkedList();
		else return setHeaderInfo(list.get(0), list, fname, null);
	}
	
	/**
	 * 엑셀파일 작성 시 hreader정보를 구한다.
	 * 
	 * @param list 엑셀에 출력할 Data
	 * @param fname 엑셀 파일명
	 * @param sheet 시트명
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List setHeaderInfo(List<Map<String, Object>> list, String fname, String sheet) {
		if(list == null  || list.size() <= 0) return new LinkedList();
		else return setHeaderInfo(list.get(0), list, fname, sheet);
	}
	
	/**
	 * 엑셀파일 작성 시 hreader정보를 구한다.
	 * - List<Map<String, Object>>의 첫번째 Map의 name을 해더 컬럼명으로 적용(이때, 컬럼명이 용어사전에 있는 경우 다국어로 처리됨)
	 * - 엑셀 속성 지정방법
	 * field = 첫번째 배열의 Map name
	   title = fileId의 다국어 명(다국어에 없는 경우 원래의 명으로 적용됨);
	   width = 각 column data의 max byte * 8
	   hidden = false
	   align = left
	   formatter = null
	   merge_row = "0"  
	   merge_col = "0"
	 * @param map hreader정보를 추출할 data
	 * @param list 엑셀에 출력할 Data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List setHeaderInfo(Object headerObject, List<Map<String, Object>> list, String fname, String sheet) {
		List<Map<String, Object>> header = new LinkedList();
		Map<String, Object> headers = new HashMap(); 
		
		if(headerObject == null) return header;
		else {
			String[] names = null;
			
			if(headerObject instanceof Map) {
				names = transformColumns((Map) headerObject);
			} else if(headerObject instanceof String[]) {
				names = (String[]) headerObject;
			}
			
			for(int i = 0; i < names.length; i++) {
				String name = StringHelper.null2void(names[i]);
				String field = name;
				
				if(name.isEmpty()) continue;
				
				Map<String, Object> column = new HashMap<String, Object>();
				
				// 컬럼의 최대크기 구하기
				int s = 3200;
				for(int l = 0; l < list.size(); l++) {
					Map tMap = list.get(l);
					
					String cdata = StringHelper.null2void(tMap.get(name));
					if(s == 0 || s < (cdata.length() * 188)) {
						s = cdata.length() * 188;
					}
					
					// map정보를 통해 field ID를 구한다.
					if(l == 0) {
						field = transformColumns(tMap)[i];
					}
				}
				
				// 해더정보에서 속성을 구해 엑셀에 적용함
				// 해더정보 : string = Field ID^Field Name^Width Size^Align^halign^Sort^hidden^editor type^checkbox^format^rowspan^colspan^frozen^styler
				
				column.put("field", field);
				column.put("title", MessageResource.getMessageInstance().getMessage(name));
				column.put("width", Integer.toString(s));
				column.put("hidden", "false");
				column.put("halign", "center");
				column.put("align", "left");
				column.put("sortable", "true");
				//column.put("formatter", value);
				
				header.add(column);
			}
			
			headers.put("headers", new ArrayList(Arrays.asList(header)));
			if(fname != null) {
				headers.put("file", fname);
			}
			if(sheet != null) {
				headers.put("sheet", sheet);
			}
			list.add(headers);
		}
		
		return list;
	}
	
	/**
	 * Given a format ID this will check whether the format represents an internal excel date format or not.
	 * 
	 * @param format #isADateFormat(int, java.lang.String)
	 * @return 
	 */
	public static boolean isInternalDateFormat(int format) {
        switch(format) {
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            case 0x2d:
            case 0x2e:
            case 0x2f:
            return true;
        }
        
	    return false;
	}
	
	/**
	 * 엑셀 cell타입이 날짜 포맷인지 체크
	 * 
	 * @param cell
	 * @return
	 */
	public static boolean isADateFormat(Cell cell) {
		CellStyle style = cell.getCellStyle();
		
	    if(style == null) return false;
	    
	    boolean result = false;
	    int formatNo = style.getDataFormat();
	    String formatString = style.getDataFormatString();
	    
//	    log.debug("Data format = " + formatString);
	    
	    if(formatString.indexOf("월") > -1|| formatString.indexOf("일") > -1 || formatString.indexOf("년") > -1) {
	    	result = true;
	    } else {
	    	result = DateUtil.isADateFormat(formatNo, formatString);
	    }
	    
	    return result;
	}
	
}
