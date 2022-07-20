package kr.yni.frame.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**********************************************************************
 * PROGRAM ID : XssUtil.java PROGRAM NAME : XssUtil DESCRIPTION : Xss(Cross site script)방지
 * HISTORY --------------------------------------------------------------------
 * VERSION DATE AUTHOR DESCRIPTION
 * -------------------------------------------------------------------- Ver 1.0
 * 2014. 12. 12. myoungilkim 최초작성
 **********************************************************************/
@SuppressWarnings({ "unchecked", "rawtypes" })
public class XssUtil {

	/**
	 * Xss 방지를 위한 HTML 변환
	 * @param map 쿼리 실행후 결과값
	 * @param entryVal 화면에 나타낼 값의 변수명
	 * @return  
	 */
	public static Map XssChange(Map map, String[] entryVal) throws Exception {
		Map paramMap = new HashMap();
		
		for(Iterator iter = map.entrySet().iterator(); iter.hasNext();){
			
			java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
			
			for(int i = 0 ; i < entryVal.length ; i++){
				if(StringUtils.equals((String)entry.getKey(), entryVal[i])) {
                	paramMap.put(entryVal[i], getHTMLCode(StringHelper.null2void(entry.getValue())));
                }else{
                	paramMap.put(entry.getKey(), getHTMLCode(StringHelper.null2void(entry.getValue())));
                }
			}
		}
		
		return paramMap;
	}
	
	/**
	 * Xss 변환
	 * @param text
	 * @return
	 */
	public static String getHTMLCode(String text) throws Exception {
		if (text == null || text.equals(""))
			return "";

		StringBuffer sb = new StringBuffer(text);
		char ch;

		for (int i = 0; i < sb.length(); i++) {
			ch = sb.charAt(i);
			if (ch == '<') {
				sb.replace(i, i + 1, "&lt;");
				i += 3;
			} else if (ch == '>') {
				sb.replace(i, i + 1, "&gt;");
				i += 3;
			} else if (ch == '&') {
				sb.replace(i, i + 1, "&amp;");
				i += 4;
			} else if (ch == '"') {
				sb.replace(i, i + 1, "&quot;");
				i += 5;
			} else if (ch == '\r' && sb.charAt(i + 1) == '\n') {
				sb.replace(i, i + 2, "<BR>");
				i += 3;
			} else if (ch == '\n') {
				sb.replace(i, i + 1, "<BR>");
				i += 3;
			}
		}
		
		return sb.toString();
	}
	
	public static String getHTML(String str)  throws Exception {
		String convertStr = new String(str);
		
		convertStr = convertStr.replaceAll("&lt;", "<");
		convertStr = convertStr.replaceAll("&gt;", ">");
		convertStr = convertStr.replaceAll("&quot;", "\"");
		convertStr = convertStr.replaceAll("&apos;", "'");
		convertStr = convertStr.replaceAll("&lt;br&gt;", "<br>");
		convertStr = convertStr.replaceAll("&lt;BR&gt;", "<BR>");
		convertStr = convertStr.replaceAll("&lt;p&gt;", "<p>");
		convertStr = convertStr.replaceAll("&lt;P&gt;", "<P>");
		
		return convertStr;
	}
	
}
