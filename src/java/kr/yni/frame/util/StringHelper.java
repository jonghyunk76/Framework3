package kr.yni.frame.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  * <p>
 * String(TEXT) 관련 Framework의 Helper Class
 * </p>
 *
 * <p>
 * 아래 Method들은 모두 <code>static</code>으로 선언 되었기 때문에 유념하기 바란다.
 * </p>
 * 
 * @author YNI-maker
 *
 */
public class StringHelper {
	
	private static Log log = LogFactory.getLog(StringHelper.class);
	
    /**
     * <p>
     * 공백문자(blank String)
     * </p>
     */
    private final static char WHITE_SPACE = ' ';

    /**
     * <p>
     * <strong>StringHelper</strong>의 default 컨스트럭터(Constructor).
     * </p>
     */
    protected StringHelper() {
    }

    /**
     * <p>
     * 문자열에서 Property형태의 값을 추출한다. Property 형태란 'key=value'형식으로 되어있는 것을 의미한다.
     * </p>
     *
     * <pre>
     * String source = "key1=value1::key2=value2::key3=value3";
     * String key = "key2";
     * String delim = "::";
     * String result = StringHelper.getParam(source,key, delim, "Default Value");
     * </pre>
     * <code>result</code>는 <code>"value2"</code> 을 가지게 된다.
     *
     * @param source       프로퍼티를 검색할 원본 문자열
     * @param key          검색할 키 문자열
     * @param delim        구분자
     * @param defaultValue 해당 Key에 해당하는 값이 없을때 반환할 기본값
     * @return 검색된 Property의 Value
     */
    public static String getParam(String source, String key, String delim, String defaultValue) {
        if (isNull(source) || isNull(key)) {
            return defaultValue;
        }
        int i = source.indexOf(key + "=");
        if (i < 0) {
            return defaultValue;
        }
        int j = i + key.length() + 1;
        int k = source.indexOf(delim, j);
        if (k < 0) {
            k = source.length();
        }
        try {
            return source.substring(j, k);
        } catch (Exception _ex) {
            return defaultValue;
        }
    }

    /**
     * <p>
     * 여분의 빈 자리 만큼 왼쪽부터 패딩문자를 채운다.
     * </p>
     *
     * <pre>
     * String source = "300";
     * String result = StringHelper.lPad(source, 5, '#');
     * </pre>
     * <code>result</code>는 <code>"##300"</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param len    원하고자 하는 문자열의 길이
     * @param pad    덧붙히고자 하는 문자
     * @return 패딩된 문자열
     */
    public static String lPad(String source, int len, char pad) {
        return lPad(source, len, pad, false);
    }

    /**
     * <p>
     * 여분의 빈 자리 만큼 왼쪽부터 패딩문자를 채운다.
     * </p>
     *
     * <pre>
     * String source = "300";
     * String result = StringHelper.lPad(source, 5, '#');
     * </pre>
     * <code>result</code>는 <code>"##300"</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param len    원하고자 하는 문자열의 길이
     * @param pad    덧붙히고자 하는 문자
     * @param isTrim 문자열 trim 여부
     * @return 패딩된 문자열
     */
    public static String lPad(String source, int len, char pad, boolean isTrim) {

        if (isTrim) {
            source = source.trim();
        }

        for (int i = source.length(); i < len; i++) {
            source = pad + source;
        }
        return source;
    }


    /**
     * <p>
     * 여분의 빈 자리 만큼 오른쪽부터 패딩문자를 채운다.
     * </p>
     *
     * <pre>
     * String source = "300";
     * String result = StringHelper.rPad(source, 5, '#');
     * </pre>
     * <code>result</code>는 <code>"300##"</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param len    원하고자 하는 문자열의 길이
     * @param pad    덧붙히고자 하는 문자
     * @return 패딩된 문자열
     */
    public static String rPad(String source, int len, char pad) {
        return rPad(source, len, pad, false);
    }

    /**
     * <p>
     * 여분의 빈 자리 만큼 오른쪽부터 패딩문자를 채운다.
     * </p>
     *
     * <pre>
     * String source = "300";
     * String result = StringHelper.rPad(source, 5, '#');
     * </pre>
     * <code>result</code>는 <code>"300##"</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param len    원하고자 하는 문자열의 길이
     * @param pad    덧붙히고자 하는 문자
     * @param isTrim 문자열의 trim 여부
     * @return 패딩된 문자열
     */
    public static String rPad(String source, int len, char pad, boolean isTrim) {

        if (isTrim) {
            source = source.trim();
        }

        for (int i = source.length(); i < len; i++) {
            source = source + pad;
        }
        return source;
    }

    /**
     * <p>
     * 해당 문자열의 왼쪽 WhiteSpace({@link java.lang.Character#isWhitespace})를 지운다.
     * </p>
     *
     * @param source 문자열
     * @return 왼쪽 공백이 제거된 문자열
     */
    public static String lTrim(String source) {
        int strIdx = 0;
        char[] val = source.toCharArray();
        int lenIdx = val.length;

        while ((strIdx < lenIdx) && Character.isWhitespace(val[strIdx])) {
            strIdx++;
        }

        return (strIdx >= 0) ? source.substring(strIdx) : source;
    }


    /**
     * <p>
     * 해당 문자열의 오른쪽 WhiteSpace({@link java.lang.Character#isWhitespace})를 지운다.
     * </p>
     *
     * @param source 문자열
     * @return 오른쪽 공백이 제거된 문자열
     */
    public static String rTrim(String source) {
        int strIdx = 0;
        char[] val = source.toCharArray();
        int count = val.length;
        int lenIdx = count;

        while ((strIdx < lenIdx) && Character.isWhitespace(val[lenIdx - 1])) {
            lenIdx--;
        }

        return (lenIdx >= 0) ? source.substring(strIdx, lenIdx) : source;
    }

    /**
     * <p>
     * Byte 잘라내기(한글포함).
     * </p>
     *
     * <pre>
     * String source = "";
     * String result = StringHelper.byteSubString(source, 4);
     * </pre>
     * <code>result</code>는 <code>"?????"</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param len    원하고자 하는 문자열의 길이
     * @return 잘려진 문자열
     */
    public static String byteSubString(String source, int len) {

        if (isNull(source)) {
            return "";
        }

        String tmp = source;

        int slen = 0, blen = 0;
        char c;
        if (getByteLength(tmp) > len) {
            while (blen + 1 < len) {
                c = tmp.charAt(slen);
                blen++;
                slen++;
                if (c > 127) {
                    blen++; //2-byte character..
                }
            }
            tmp = tmp.substring(0, slen);
        }
        return tmp;
    }


    /**
     * <p>
     * 문자열을 좌측 정렬한다. 이때 문자열뒤에 줄임표는 넣지 않는다.
     * </p>
     *
     * <pre>
     * String source = "ABCDEFG";
     * String result = StringHelper.alignLeft(source, 10);
     * </pre>
     * <code>result</code>는 <code>"ABCDEFG  "</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param length 정렬이 이루어질 길이
     * @return 정렬이 이루어진 문자열
     */
    public static String alignLeft(String source, int length) {
        return alignLeft(source, length, false);
    }


    /**
     * <p>
     * 문자열을 좌측 정렬한다.
     * </p>
     *
     * <pre>
     * String source = "ABCDEFG";
     * String result = StringHelper.alignLeft(source, 5, true);
     * </pre>
     * <code>result</code>는 <code>"AB..."</code> 을 가지게 된다.
     *
     * @param source     원본 문자열
     * @param length     정렬이 이루어질 길이
     * @param isEllipsis 마지막에 줄임표("...")의 여부
     * @return 정렬이 이루어진 문자열
     */
    public static String alignLeft(String source, int length, boolean isEllipsis) {

        if (source.length() <= length) {

            StringBuilder temp = new StringBuilder(source);
            for (int i = 0; i < (length - source.length()); i++) {
                temp.append(WHITE_SPACE);
            }
            return temp.toString();
        } else {
            if (isEllipsis) {

                StringBuilder temp = new StringBuilder(length);
                temp.append(source.substring(0, length - 3));
                temp.append("...");
                return temp.toString();
            } else {
                return source.substring(0, length);
            }
        }
    }


    /**
     * <p>
     * 문자열을 우측 정렬한다. 이때 문자열뒤에 줄임표는 넣지 않는다.
     * </p>
     *
     * <pre>
     * String source = "ABCDEFG";
     * String result = StringHelper.alignRight(source, 10);
     * </pre>
     * <code>result</code>는 <code>"   ABCDEFG"</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param length 정렬이 이루어질 길이
     * @return 정렬이 이루어진 문자열
     */
    public static String alignRight(String source, int length) {

        return alignRight(source, length, false);
    }


    /**
     * <p>
     * 문자열을 우측 정렬한다.
     * </p>
     *
     * <pre>
     * String source = "ABCDEFG";
     * String result = StringHelper.alignRight(source, 5, true);
     * </pre>
     * <code>result</code>는 <code>"AB..."</code> 을 가지게 된다.
     *
     * @param source     원본 문자열
     * @param length     정렬이 이루어질 길이
     * @param isEllipsis 마지막에 줄임표("...")의 여부
     * @return 정렬이 이루어진 문자열
     */
    public static String alignRight(String source, int length, boolean isEllipsis) {

        if (source.length() <= length) {

            StringBuilder temp = new StringBuilder(length);
            for (int i = 0; i < (length - source.length()); i++) {
                temp.append(WHITE_SPACE);
            }
            temp.append(source);
            return temp.toString();
        } else {
            if (isEllipsis) {

                StringBuilder temp = new StringBuilder(length);
                temp.append(source.substring(0, length - 3));
                temp.append("...");
                return temp.toString();
            } else {
                return source.substring(0, length);
            }
        }
    }

    /**
     * <p>
     * 문자열을 중앙 정렬한다. 이때 문자열뒤에 줄임표는 넣지 않는다. 만약 공백이 홀수로 남는다면 오른쪽에 들어 간다.
     * </p>
     *
     * <pre>
     * String source = "ABCDEFG";
     * String result = StringHelper.alignCenter(source, 10);
     * </pre>
     * <code>result</code>는 <code>" ABCDEFG  "</code> 을 가지게 된다.
     *
     * @param source 원본 문자열
     * @param length 정렬이 이루어질 길이
     * @return 정렬이 이루어진 문자열
     */
    public static String alignCenter(String source, int length) {
        return alignCenter(source, length, false);
    }


    /**
     * <p>
     * 문자열을 중앙 정렬한다.이때 문자열뒤에 줄임표는 넣지 않는다. 만약 공백이 홀수로 남는다면 오른쪽에 들어 간다.
     * </p>
     *
     * <pre>
     * String source = "ABCDEFG";
     * String result = StringHelper.alignCenter(source, 5,true);
     * </pre>
     * <code>result</code>는 <code>"AB..."</code> 을 가지게 된다.
     *
     * @param source     원본 문자열
     * @param length     정렬이 이루어질 길이
     * @param isEllipsis 마지막에 줄임표("...")의 여부
     * @return 정렬이 이루어진 문자열
     */
    public static String alignCenter(String source, int length, boolean isEllipsis) {
        if (source.length() <= length) {

            StringBuilder temp = new StringBuilder(length);
            int leftMargin = (int) (length - source.length()) / 2;

            int rightMargin;
            if ((leftMargin * 2) == (length - source.length())) {
                rightMargin = leftMargin;
            } else {
                rightMargin = leftMargin + 1;
            }

            for (int i = 0; i < leftMargin; i++) {
                temp.append(WHITE_SPACE);
            }

            temp.append(source);

            for (int i = 0; i < rightMargin; i++) {
                temp.append(WHITE_SPACE);
            }

            return temp.toString();
        } else {
            if (isEllipsis) {

                StringBuilder temp = new StringBuilder(length);
                temp.append(source.substring(0, length - 3));
                temp.append("...");
                return temp.toString();
            } else {
                return source.substring(0, length);
            }
        }

    }


    /**
     * <p>
     * 문자열의 제일 처음글자를 대문자화 한다.
     * </p>
     *
     * <pre>
     * String source = "abcdefg";
     * String result = StringHelper.capitalize(source);
     * </pre>
     * <code>result</code>는 <code>"Abcdefg"</code> 을 가지게 된다.
     *
     * @param source 원본 문자였
     * @return 대문자화 된 문자열
     */
    public static String capitalize(String source) {
        return !isNull(source) ? source.substring(0, 1).toUpperCase() + source.substring(1).toLowerCase() : source;
    }

    /**
     * <p>
     * 대상문자열(source)에서 지정문자열(target)이 검색된 횟수를,
     * 지정문자열이 없으면 0 을 반환한다.
     * </p>
     *
     * <pre>
     * String source = "ar";
     * String target = "StringHelper Class &ar; search";
     * int result = StringHelper.search(source, target);
     * </pre>
     * <code>result</code>는 <code>2</code>을 가지게 된다.
     *
     * @param source 대상문자열
     * @param target 검색할 문자열
     * @return 지정문자열이 검색되었으면 검색된 횟수를, 검색되지 않았으면 0 을 반환한다.
     */
    public static int search(String source, String target) {
        int result = 0;
        String strCheck = new String(source);
        for (int i = 0; i < source.length();) {
            int loc = strCheck.indexOf(target);
            if (loc == -1) {
                break;
            } else {
                result++;
                i = loc + target.length();
                strCheck = strCheck.substring(i);
            }
        }
        return result;
    }
    
    /**
     * <p>
     * 문자열을 잘라 대상이 되는 문자열이 포함되어 있는지 체크한다.
     * </p>
     * 
     * @param source 비교할 문자열
     * @param target 대상문자열
     * @param regex 구분자
     * @return
     */
    public static boolean stringSplitSearch(String source, String target, String regex) {
    	boolean result = false;
    	
    	String[] strs = source.split(StringHelper.null2string(regex,","));
    	
    	for(int i = 0; i < strs.length; i++) {
    		if(strs[i].equals(target)) {
    			result = true;
    			break;
    		}
    	}
    	
    	return result;
    }
    
    /**
     * <p>
     * 배열을 받아 연결될 문자열로 연결한다. 이때 각 엘레멘트 사이에 구분문자열을 추가한다.
     * </p>
     *
     * <pre>
     * String[] source = new String[] {"AAA","BBB","CCC"};
     * String result = StringHelper.join(source,"+");
     * </pre>
     * <code>result</code>는 <code>"AAA+BBB+CCC"</code>를 가지게 된다.
     *
     * @param aryObj 문자열로 만들 배열
     * @param delim  각 엘레멘트의 구분 문자열
     * @return 연결된 문자열
     */
    public static String join(Object aryObj[], String delim) {
        StringBuilder stringbuffer = new StringBuilder();
        int i = aryObj.length;
        if (i > 0) {
            stringbuffer.append(aryObj[0].toString());
        }
        for (int j = 1; j < i; j++) {
            stringbuffer.append(delim);
            stringbuffer.append(aryObj[j].toString());
        }

        return stringbuffer.toString();
    }

    /**
     * <p>
     * 문자열의 byte 길이 체크 한다. (한글은 2byte 취급)
     * </p>
     *
     * <pre>
     * String source = "A123456BB";
     * int result = StringHelper.getByteLength(source);
     * </pre>
     * <code>result</code>는 <code>10</code>을 가지게 된다.
     *
     * @param source 문자열
     * @return 문자열의 길이
     */
    public static int getByteLength(String source) {
        return source.getBytes().length;
    }

    /**
     * <p>
     * 문자열의 Null 이나 공백 문자열 여부를 판단한다.
     * isTrim = true;
     * </p>
     *
     * <pre>
     * String source = "";
     * boolean result = StringHelper.isNull(source);
     * </pre>
     * <code>result</code>는 <code>true</code> 을 가지게 된다.
     *
     * @param source 문자열
     * @return NULL("", null) 여부
     */
    public static boolean isNull(String source) {
        return isNull(source, true);
    }

    /**
     * <p>
     * 문자열의 Null 이나 공백 문자열 여부를 판단한다.
     * </p>
     *
     * <pre>
     * String source = "";
     * boolean result = StringHelper.isNull(source);
     * </pre>
     * <code>result</code>는 <code>true</code> 을 가지게 된다.
     *
     * @param source 문자열
     * @param isTrim Trim 여부 (default는 true);
     * @return NULL("",null) 여부
     */
    public static boolean isNull(String source, boolean isTrim) {
        boolean isNullString = false;
        
        if (isTrim && source != null && !source.isEmpty()) {
            source = source.trim();
        }
        
        if (source == null || source.isEmpty()) {
            isNullString = true;
        }
        
        return isNullString;
    }
    
    /**
     * <p>
     * 배열을 Vector로 만든다.
     * </p>
     *
     * <pre>
     * String[] source = new String[]{"AAA","BBB","CCC","DDD"};
     * Vector result = StringHelper.toVector(source);
     * </pre> <code>result</code>는 <code>[AAA,BBB,CCC,DDD]</code>
     *
     * @param array 원본 배열
     * @return 배열과 같은 내용을 가지는 Vector
     */
    public static <T> Vector<T> toVector(T[] array) {
        if (array == null) {
            return null;
        }

        Vector<T> vec = new Vector<T>(array.length);

        for (int i = 0; i < array.length; i++) {
            vec.add(i, array[i]);
        }
        return vec;
    }

    /**
     * <p>
     * 문자열의 배열을 정렬한다.
     * </p>
     *
     * <pre>
     * String[] source = new String[]{"CCC","BBB","DDD","AAA"};
     * StringHelper.sortStringArray(source);
     * </pre>
     * <code>source</code>는 <code>[AAA,BBB,CCC,DDD]</code>
     *
     * @param source 정렬할 문자열의 배열
     */
    public static void sortStringArray(String[] source) {

        java.util.Arrays.sort(source);
    }

    /**
     * <p>
     * 문자열의 Enemration을 정렬된 배열로 반환한다.
     * </p>
     *
     * @param source 정렬하고자 하는 Enumeration
     * @return 정렬된 문자열 배열
     */
    public static String[] sortStringArray(Enumeration<String> source) {
        Vector<String> buf = new Vector<String>();
        while (source.hasMoreElements()) {
            buf.add(source.nextElement());
        }
        String[] buf2 = new String[buf.size()];

        for (int i = 0; i < buf.size(); i++) {
            buf2[i] = buf.get(i);
        }
        java.util.Arrays.sort(buf2);
        return buf2;
    }

    /**
     * <p>
     * 문자열을 받아 null이나 문자열이 space로 이뤄진
     * "   " - 문자열 일 경우 ""으로 변환 한다.
     * 해당 사항 없을 경우 원본 문자열을 리턴 한다.
     * </p>
     *
     * <pre>
     * String source = null;
     * String result = StringHelper.null2void(source);
     * </pre>
     * <code>result</code>는 <code>""</code>를 가지게 된다.
     *
     * @param source 문자열
     * @return source 변환된 문자열
     */
    public static String null2void(String source) {
        if (isNull(source)) {
            source = "";
        }
        return source;
    }
    
    /**
   	 * 조회나, 수정form에 뿌려 줄때 값이 null이면 화면에 null이라고 찍히는 것을 없애기 위해사용한다.
   	 * 
   	 * @param obj
   	 *            null 처리할 문자열
   	 * @return null 처리된 문자열
   	 */
   	public static String null2void(Object obj) {
   		if (obj != null)
   			return obj.toString();
   		else
   			return "";
   	}

   	/**
   	 * Object를 boolean 타입으로 변환 후 리턴한다. 
   	 * 
   	 * @param obj
   	 * @param flag
   	 * @return
   	 */
    public static boolean null2boolean(Object obj, boolean flag) {
    	boolean returnVal = false;
    	
        if (obj == null) {
            return flag;
        } else {
        	returnVal = Boolean.parseBoolean(obj.toString());
        }
        
        return returnVal;
    }
    
    /**
     * <p>
     * 파라미터가 null이나 "", " " 이면 0 리턴
     * (Trim 여부 true).
     * </p>
     *
     * <pre>
     * String source = null;
     * int result = StringHelper.null2zero(source);
     * </pre>
     * <code>result</code>는 <code>0</code>를 가지게 된다.
     *
     * @param source 문자열
     * @param val obj가 null 인경우 치환시킬 int값
     * @return 변환된 int형
     */
    
    public static int null2zero(Object obj, int val) {
    	int returnVal = 0;
    	
    	try {
	    	if (obj == null) {
	            return val;
	        } else {
	        	returnVal = Integer.parseInt(obj.toString());
	        }
    	} catch(Exception e) {
    		if(log.isErrorEnabled()) log.error("source = " + obj + ", exception = " + e.getMessage());
    	}
    	
        return returnVal;
    }
    
    /**
     * <p>
     * 파라미터가 null이나 "", " " 이면 0 리턴
     * (Trim 여부 true).
     * </p>
     *
     * <pre>
     * String source = null;
     * int result = StringHelper.null2zero(source);
     * </pre>
     * <code>result</code>는 <code>0</code>를 가지게 된다.
     *
     * @param source 문자열
     * @return 변환된 int형
     */
    public static int null2zero(Object obj) {
    	return null2zero(obj, 0);
    }
    
    /**
     * <p>
     * 파라미터가 null이나 "", " " 이면 0.0F 리턴
     * (Trim 여부 true).
     * </p>
     *
     * <pre>
     * String source = null;
     * float result = StringHelper.null2float(source);
     * </pre>
     * <code>result</code>는 <code>0.0F</code>를 가지게 된다.
     *
     * @param source 문자열
     * @return 변환된 float형
     */
    public static float null2float(String source) {
        if (isNull(source)) {
            return 0.0F;
        }
        return Float.parseFloat(source);
    }

    /**
     * <p>
     * 파라미터가 null이나 "", " " 이면 0.0D 리턴
     * (Trim 여부 true).
     * </p>
     *
     * <pre>
     * String source = null;
     * double result = StringHelper.null2double(source);
     * </pre>
     * <code>result</code>는 <code>0.0D</code>를 가지게 된다.
     *
     * @param source 문자열
     * @return 변환된 double형
     */
    public static double null2double(String source) {
        if (isNull(source)) {
            return 0.0D;
        }
        return Double.parseDouble(source);
    }

    /**
     * <p>
     * 파라미터가 null이나 "", " " 이면 0L 리턴
     * (Trim 여부 true).
     * </p>
     *
     * <pre>
     * String source = null;
     * long result = StringHelper.null2long(source);
     * </pre>
     * <code>result</code>는 <code>0</code>를 가지게 된다.
     *
     * @param source 문자열
     * @return 변환된 long형
     */
    public static long null2long(String source) {
        if (isNull(source)) {
            return 0L;
        }
        return Long.parseLong(source);
    }

        /**
     * <p>
     * 파라미터가 null이나 "", " " 이면 value를 리턴, 아니면 source를 리턴
     * </p>
     *
     * <pre>
     * String source = null;
     * String result = StringHelper.null2string(source,"0");
     * </pre>
     * <code>result</code>는 <code>"0"</code>를 가지게 된다.
     *
     * @param source 문자열
     * @return 변환된 문자열
     */
    public static String null2string(Object source, String value) {
        if (source == null) {
            return value;
        } 
        
        if(source instanceof String) {
        	if(new String(source.toString()).isEmpty()) {
        		return value;
        	}
        }
        
        return new String(source.toString());
    }
	
    /**
     * <p>
     * 문장열의 비교시 사용.
     * 기존 <strong>String</strong> Class의 <code>equals()</code> Method의 여러 문제점을 해결 하기 위함 Method이다.
     * <p/>
     *
     * <p>
     * 아래와 같이 두가지의 빈번하고 복잡한 비교 방식을
     * </p>
     * <pre>
     * 1.
     * String source = null;
     *
     * if(source == null || "".equals(source)){
     *     ...
     * }
     *
     * 2.
     * String target = "?"
     *
     * if(source != null && source.equals(target)) {
     *     ...
     * }
     * </pre>
     *
     * <p>
     * <code>strEquals()</code> Method를 이용하여 쉽게 할 수 있다.
     * </p>
     *
     * <pre>
     * 1.
     * String source = null;
     *
     * if(StringHelper.strEquals(source, "")){
     *     ...
     * }
     *
     * 2.
     * String target = "?";
     *
     * if(StringHelper.strEquals(source, target)) {
     *  ...
     * }
     * </pre>
     *
     * @param source 비교 문자열1
     * @param target 비교 문자열2
     * @return boolean 비교 결과 (true, false)
     */
    public static boolean strEquals(String source, String target) {
        return null2void(source).equals(null2void(target));
    }


    /**
     * <p>
     * 지정된 문자열의 일부분을 리턴함.
     * 기존 <strong>String</strong> Class의 <code>substring()</code>에서 자주 발생하는 <code>NullpointException</code>을
     * 고려하여 만든 Method이다.
     * </p>
     *
     * <pre>
     * String source = "200403"
     * String result = StringHelper.toSubString(source, 4, 8);
     * </pre>
     *
     * <code>result</code>는 <strong>IndexOutOfBoundException</strong>이 발생하지 않고 <code>"03"</code>을 가지게 된다.
     *
     * @param source     원본 문자열
     * @param beginIndex 시작위치
     * @param endIndex   마지막 위치
     * @return 해당 문자열
     */
    public static String toSubString(String source, int beginIndex, int endIndex) {

        if (strEquals(source, "")) {
            return source;
        } else if (source.length() < beginIndex) {
            return "";
        } else if (source.length() < endIndex) {
            return source.substring(beginIndex);
        } else {
            return source.substring(beginIndex, endIndex);
        }

    }

    /**
     * <p>
     * 지정된 문자열의 일부분을 리턴함.
     * 기존 <strong>String</strong> Class의 <code>substring()</code>에서 자주 발생하는 <code>NullpointException</code>을
     * 고려하여 만든 Method이다.
     * </p>
     *
     * <pre>
     * String source = "200403"
     * String result = StringHelper.toSubString(source, 8);
     * </pre>
     * <code>result</code>는 <strong>IndexOutOfBoundException</strong>이 발생하지 않고 <code>""</code>을 가지게 된다.
     *
     * @param source     원본 문자열
     * @param beginIndex 시작위치
     * @return 해당 문자열
     */
    public static String toSubString(String source, int beginIndex) {

        if (strEquals(source, "")) {
            return source;
        } else if (source.length() < beginIndex) {
            return "";
        } else {
            return source.substring(beginIndex);
        }

    }
    
    /**
     * 문자열을 delims를 기준으로 잘라서 List로 반환한다. delims 들 자체는 token으로 반환되지 않는다.
     * 내부적으로 StringTokenizer를 사용한다.
     * @param str
     * @param delims
     * @param returnDelims
     * @return
     */
    public static List<String> tokenize(String str, String delims) {
    	return tokenize(str,delims,false);
    }
    
    /**
     * 문자열을 delims를 기준으로 잘라서 List로 반환한다. delims 들 자체도 token으로 반환시킬지 여부를
     * returnDelims로 지정한다. 내부적으로 StringTokenizer를 사용한다.
     * @param str
     * @param delims
     * @param returnDelims
     * @return
     */
    public static List<String> tokenize(String str, String delims, boolean returnDelims) {
    	if (str == null) {
    		return null;
    	}
    	
    	List<String> tokens = new ArrayList<String>();
    	if (isNull(delims)) {
    		tokens.add(str);
    	} else {
    		StringTokenizer tokenizer = new StringTokenizer(str,delims,returnDelims);
    		while(tokenizer.hasMoreElements()) {
    			tokens.add(tokenizer.nextToken());
    		}
    	}
    	
    	return tokens;
    }
    
    /**
     * <p>
     * str 문자열을 delim을 기준으로 split하여 List에 담아준다.
     * <code>String.split(String regexp)</code>에서는 delimiter를 reqular expression으로
     * 표현하는데 비해 이 함수는 delimiter를 그대로 split하므로 빠르게 동작한다.
     * </p>
     * @param str 분할할  문자열
     * @param delim 문자열을 분할하기 위한 구분자
     * @return List 객체
     */
    public static List<String> split(String str, String delim) {
		if ( str == null ) {
			return null;
		}
		if ( delim == null ) {
			delim = "";
		}
		
		int beginIdx = 0;
		int endIdx = 0;
		List<String> splitList = new ArrayList<String>();
		int strLen = str.length();
		int delimLen = delim.length();
		
		do {
			endIdx = str.indexOf(delim, beginIdx);
			if ( endIdx >= 0 ) {
				splitList.add(str.substring(beginIdx, endIdx));
			} else {
				splitList.add(str.substring(beginIdx,strLen));
			}
			beginIdx = endIdx+delimLen;
		} while(endIdx >= 0);
		
		return splitList;
	}
	
    /**
     * 문자열을 delim 기준으로 split하여 주어진 dest 배열에 담는다. 
     * dest 배열의 크기 만큼 담기게 되며, 그 이상은 무시된다.
     * 만약 배열의 크기 보다 적게 나뉘게 되면 모자라는 부분은 defStr로 채워진다.
     * @param str 분할할 문자
     * @param delim null 이면 공백문자(" ")를 사용한다.
     * @param dest 분할될 문자가 담길 배열 객체
     * @param defStr 분할 개수가 모자랄 경우 채우기 위해 사용할 문자열
     */
    public static void split(String str, String delim, String[] dest, String defStr) {
		if (dest == null || dest.length == 0) {
			return;
		}
		int cnt = dest.length;
		
    	if ( str == null ) {
			str = "";
		}
		if ( delim == null ) {
			delim = " ";
		}
		
		int beginIdx = 0;
		int endIdx = 0;
		
		int strLen = str.length();
		int delimLen = delim.length();
		int count = 0;
		do {
			endIdx = str.indexOf(delim, beginIdx);
			if ( endIdx > 0 ) {
				dest[count] = str.substring(beginIdx, endIdx);
			} else {
				dest[count] = str.substring(beginIdx,strLen);
			}
			beginIdx = endIdx+delimLen;
			count++;
		} while(endIdx > 0 && count<cnt);
		
		for(int i=count;i<cnt;i++) {
			dest[i] = defStr;
		}
    }
    
    /**
     * <p>
     * str 문자열을 delim을 기준으로 split하여 List에 담아준다. 
     * cnt 개수 만큼만 담기며, 그 이상은 무시된다. 만약 cnt 보다 적게 나뉘게 되면 모자라는 부분은
     * defStr로 채워진다.
     * <code>String.split(String regexp)</code>에서는 delimiter를 reqular expression으로
     * 표현하는데 비해 이 함수는 delimiter를 그대로 split하므로 훨씬 빠르게 동작한다.
     * </p> 
     * @param str 분할할 문자열
     * @param delim 문자열을 분할하기 위한 구분자, null 이면 공백문자(" ")를 사용한다.
     * @param cnt 분할 개수
     * @param defStr 분할 개수가 모자랄 경우 채우기 위해 사용할 문자열
     * @return List 객체
     */
	public static List<String> split(String str, String delim, int cnt, String defStr) {
		if ( str == null ) {
			return null;
		}
		if ( delim == null ) {
			delim = " ";
		}
		if ( cnt <= 0 ) {
			return new ArrayList<String>();
		}
		List<String> splitList = new ArrayList<String>(cnt);
		
		int beginIdx = 0;
		int endIdx = 0;
		
		
		int strLen = str.length();
		int delimLen = delim.length();
		int count = 0;
		do {
			endIdx = str.indexOf(delim, beginIdx);
			if ( endIdx > 0 ) {
				splitList.add(str.substring(beginIdx, endIdx));
			} else {
				splitList.add(str.substring(beginIdx,strLen));
			}
			beginIdx = endIdx+delimLen;
			count++;
		} while(endIdx > 0 && count<cnt);
		
		for(int i=count;i<cnt;i++) {
			splitList.add(defStr);
		}
		
		return splitList;
	}
	
	/**
     * <p>
     * 해당 문자열의 왼쪽의 WhiteSpace({@link java.lang.Character#isWhitespace})를 해당 문자로 대체.
     * </p>
     *
     * <pre>
     * String source = "  300  ";
     * String repStr = "0";
     * String result  = StringHelper.lSpaceReplace(source, repStr);
     * </pre>
     * <code>result</code>는 <code>"00300  "</code> 을 가지게 된다.
     *
     * @param source 문자열
     * @param repStr 대체 문자열
     * @return 대체된 문자열
     */
    public static String replaceLeftSpace(String source, String repStr) {
        Matcher matcher = Pattern.compile("^\\s*").matcher(source);

        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            byte[] findGroup = matcher.group().getBytes();
            for (int i = 0; i < findGroup.length; i++) {
                if (Character.isWhitespace((char) findGroup[i])) {
                    sb.append(repStr);
                }
            }
        }

        return sb.toString().concat(matcher.replaceAll(""));
    }

    /**
     * <p>
     * 해당 문자열의 오른쪽의 WhiteSpace({@link java.lang.Character#isWhitespace})를 해당 문자로 대체.
     * </p>
     *
     * <pre>
     * String source = "  300  ";
     * String repStr = "0";
     * String result  = StringHelperExt.rSpaceReplace(source, repStr);
     * </pre>
     * <code>result</code>는 <code>"  30000"</code> 을 가지게 된다.
     *
     * @param source 문자열
     * @param repStr 대체 문자열
     * @return 대체된 문자열
     */
    public static String replaceRightSpace(String source, String repStr) {

        Matcher matcher = Pattern.compile("\\s*$").matcher(source);

        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            char[] findGroup = matcher.group().toCharArray();
            for (int i = 0; i < findGroup.length; i++) {
                if (Character.isWhitespace(findGroup[i])) {
                    sb.append(repStr);
                }
            }
        }

        return matcher.replaceAll("").concat(sb.toString());
    }
    
	private static final Pattern unicodePattern = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");

	/**
	 * Unicode 로 표현댄 문자들을 실제 문자로 변경한다.
	 * 예를 들어 "Hello\u003aWorld"와 같이 Unicode 표현이 들어간 문자열을 넘기면
	 * 실제 문자열로 변경이된 "Hello:World"로 변경되어 반환된다.
	 * @param s Unicode 표현이 있는 문자열
	 * @return 실제 문자로 변경된 결과
	 */
	public static String unescapeUnicode(String s) {
		String res = s;
		Matcher m = unicodePattern.matcher(res);
		
		while (m.find()) {
			res = res.replaceAll("\\" + m.group(0), Character
					.toString((char) Integer.parseInt(m.group(1), 16)));
		}
		return res;
	}
	
	public static String toDbStyle(String name) {
		StringBuilder sb = new StringBuilder(name.replace('.', '_'));
		for (int i = 1; i < sb.length() - 1; i++) {
			if (Character.isLowerCase(sb.charAt(i - 1))
					&& Character.isUpperCase(sb.charAt(i))
					&& Character.isLowerCase(sb.charAt(i + 1))) {
				sb.insert(i++, '_');
			}
		}
		return sb.toString().toUpperCase();
	}
	
	public static boolean getBoolean(String value, boolean defaultValue) {
		if (value == null)
			return defaultValue;
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on")
				|| value.equalsIgnoreCase("y") || value.equalsIgnoreCase("t")
				|| value.equalsIgnoreCase("yes"))
			return true;
		return false;
	}
	
	/**
	 * 입력받은 문자열이 null이거나 ""(empty-String) 인지 판단하여 null 이면 true 값이 있으면 false 리턴
	 * 합니다. 주어진 데이터에 대해서 trim() 하지 않습니다.
	 * 
	 * @param str
	 *            체크 대상 문자열
	 * @return null 혹은 empty-String 이면 true, 아니면 false
	 * @since 2005.11.02
	 * @author Helexis
	 */
	public static boolean isNullNotTrim(String str) {
		if (str == null || str.length() == 0)
			return true;
		return false;
	}
	
	/**
	 * 일반 문자열을 xml 안에 넣기 위해 엔티티를 변환 한다.
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeXml(String str) {
		str = str.replaceAll("&", "&amp;"); // 이걸 아래에 쓰면 잘못된다.
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("'", "&apos;");
		return str;
	}
	
	/**
	 * 라인피드와 커리지 리턴을 제거한 한 라인으로 escape문자를 변환한 후 리턴한다.
	 * 
	 * @param str
	 * @return
	 */
	public static String unescapeWithLine(String str) {
		String reStr = str;
		String[] oStr = {"&amp;", "&lt;", "&gt;", "&quot;", "&apos;", "\n", "\r"}; 
		String[] nStr = {"&", "<", ">", "\"", "'", "", ""};
		
		for(int i = 0; i < 5; i++) {
			reStr = replace(reStr, oStr[i], nStr[i]);
		}
		
		return reStr;
	}
	
	/**
	 * escape문자를 변환한 후 리턴한다.
	 * 
	 * @param str
	 * @return
	 */
	public static String unescape(String str) {
		String reStr = str;
		String[] oStr = {"&amp;", "&lt;", "&gt;", "&quot;", "&apos;"};
		String[] nStr = {"&", "<", ">", "\"", "'"};
		
		for(int i = 0; i < oStr.length; i++) {
			reStr = replace(reStr, oStr[i], nStr[i]);
		}
		
		return reStr;
	}
	
	/**
	 * 문자열 내의 특정한 문자열을 모두 지정한 다른 문자열로 바꾼다.
	 * 원본 String 이 null 일 경우에는 null 을 반환한다.
	 * 
	 * @param src = 원본 String
	 * @param oldstr = 원본 String 내의 바꾸기 전 문자열
	 * @param newstr = 바꾼 후 문자열
	 * @return 치환이 끝난 문자열
	 */
	public static String replace(String src, String oldstr, String newstr) {
		if (src == null) return null;
		
		StringBuilder dest = new StringBuilder();
		int len = oldstr.length();
		int srclen = src.length();
		int pos = 0;
		int oldpos = 0;
		
		while ((pos = src.indexOf(oldstr, oldpos)) >= 0) {
			dest.append(src.substring(oldpos, pos));
			dest.append(newstr);
			oldpos = pos + len;
		}
		
		if (oldpos < srclen) dest.append(src.substring(oldpos, srclen));
		
		return dest.toString();
	}
	
	/**
	 * 문자열 인코딩 변환(euc-kr -> 8859_1)
	 * @param text
	 * @return
	 */
	public static String get8859_1(String text) {
        String rtn = new String("");

        if (text == null) {
            return rtn;
        } else {
            try {
                return new String(text.getBytes("euc-kr"),"8859_1");
            } catch (UnsupportedEncodingException UEE) {
                return rtn;
            }
        }
    }
	
	/**
	 * 반각문자로 변환
	 * @param value 변환시킬 값
	 * @return
	 */
	public static String toHalfChar(Object value) {
		return toHalfChar(value, null);
	}
	
	/**
	 * 반각문자로 변환
	 * @param value 변환시킬 값
	 * @param defStr 값이 없는 경우 치환될 값
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String toHalfChar(Object value, String defStr) {
		String src = "";
		if(src != null) src = value.toString();
		else return (defStr != null) ? defStr : src;
		
		StringBuffer buf = new StringBuffer();
		
		char c = 0;
		int srcLeng = src.length();
		
		for(int i = 0; i < srcLeng; i++) {
			c = src.charAt(i);
			
			// 영문이거나 특수문자 처리
			if(c >= '!' && c <= '~') {
				c -= 0xfee0;
			} else if(c == ' ') {
				c = 0x20;
			}
			
			buf.append(c);
		}
		
		return buf.toString();
	}
	
	/**
	 * 전각문자로 변환
	 * @param value 변환시킬 값
	 * @return
	 */
	public static String toFullChar(Object value) {
		return toFullChar(value, null);
	}
	
	/**
	 * 전각문자로 변환
	 * @param value 변환시킬 값
	 * @param defStr 값이 없는 경우 치환될 값
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String toFullChar(Object value, String defStr) {
		String src = "";
		if(src != null) src = value.toString();
		else return (defStr != null) ? defStr : src;
		
		StringBuffer buf = new StringBuffer();
		
		char c = 0;
		int srcLeng = src.length();
		
		for(int i = 0; i < srcLeng; i++) {
			c = src.charAt(i);
			
			// 영문이거나 특수문자 처리
			if(c >= 0x21 && c <= 0x7e) {
				c += 0xfee0;
			} else if(c == 0x20) {
				c = 0x3000;
			}
			
			buf.append(c);
		}
		
		return buf.toString();
	}
	
	/**
	 * 지정된 번호의 알파벳 숫자를 린터한다.
	 * <br>A~Z,AA~AZ
	 * 
	 * @param i
	 * @return
	 */
	public static String nextAlphabet(int i) {
	    return i < 0 ? "" : nextAlphabet((i / 26) - 1) + (char)(65 + i % 26);
	}
	
	/**
     * 메시지 코드에 포함된 특수문자를 아스키 코드로 변환시킨다.
     * 
     * @param code : 메시지 코드
     * @return 변환시킨 메시지 코드
     */
    public static String changeCode4Message(String code) {
    	String reStr = code;
    	
    	try {
			String[] oStr = {"(", ")", "<", ">", "/", "%", "-", "|", "+", "&", ":", "[", "]"};
			String[] nStr = {"_40", "_41", "_60", "_62", "_47", "_37", "_45", "_124", "_43", "_38", "_58", "_91", "_93"};
			
			for(int i = 0; i < oStr.length; i++) {
				reStr = replace(reStr, oStr[i], nStr[i]);
			}
    	} catch(Exception e) {
    		log.error("changeCode4Message error : " + e.getMessage());
    		reStr = code;
    	}
		
		return reStr;
    }
    
}
