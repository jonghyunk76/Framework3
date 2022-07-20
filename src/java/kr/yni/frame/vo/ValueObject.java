package kr.yni.frame.vo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.Constants;
import kr.yni.frame.exception.TooManyRowsException;

/**
 * <p>
 * <strong>ValueObject</strong>는  Data Transfer Object Pattern을 구현한 오브젝트 이며, 각각의 Tier별 Data 전송을 담당한다. 
 * </p>
 * <p>
 * <strong>ValueObject</strong>는 위 Application에서 발생하는 Data 전송의 유연성을 제시하기 위해
 * <code>DataBase</code>의 Table과 유사한 구조를 갖는다. 주요 구성 부분은 아래 그림과 같은 내부 구조를 같는다.
 * </p>
 *
 * <pre>
 *  <blockquote>
 *  &lt;&nbsp;<strong>TABLE</strong>&nbsp;&gt;
 *  <table border=1 cellspacing=3 cellpadding=0>
 *      <tr bgcolor="#ccccff">
 *           <th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *           <th>key1&nbsp;&nbsp;&nbsp;&nbsp;
 *           <th>key2&nbsp;&nbsp;&nbsp;&nbsp;
 *           <th>key3&nbsp;&nbsp;&nbsp;&nbsp;
 *           <th>... &nbsp;&nbsp;&nbsp;&nbsp;
 *      <tr valign=top>
 *           <th bgcolor="#ccccff">row1
 *           <td>value1
 *           <td>value2
 *           <td>value3
 *           <td>...
 *      <tr valign=top>
 *           <th bgcolor="#ccccff">row2
 *           <td>value1
 *           <td>value2
 *           <td>value3
 *           <td>...
 *      <tr valign=top>
 *           <th bgcolor="#ccccff">...
 *           <td>...
 *           <td>...
 *           <td>...
 *           <td>...
 *  </table>
 *  </blockquote>
 * </pre>
 *
 * <p>
 * TABLE 이라는 전체 큰 개념에 각각의 Row라는 개념이 존재하며 각각이 외부 IN/OUT 관련 오브젝트로는 다양하게 지원한다.
 * 각각의 구조에 대한 큰 이해를 돕기 위해 아래 Method의 개념을 정리 하였다. 또한 아래 Method를 이용한 다양한 사용 방식은 밑에 열거된
 * Method 부분 JavaDoc을 참조 바란다.
 * </p>
 * <pre>
 *      <table border=0>
 *          <tr>
 *              <th>
 *              <th>SELECT&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *              <th>INSERT&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *              <th>UPDATE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *          <tr bgcolor="#eeeeff">
 *              <td><strong>TABLE</strong>
 *              <td>getTable
 *              <td>setTable
 *              <td>setTable
 *          <tr>
 *              <td><strong>Row</strong>
 *              <td>getRow
 *              <td>addRow
 *              <td>setRow
 *          <tr bgcolor="#eeeeff">
 *              <td><strong>value</strong>
 *              <td>get, getXXX
 *              <td>set
 *              <td>set
 *      </table>
 *  </pre>
 * 
 * @author YNI-maker
 * @since 2013. 5. 7. 오전 9:58:44
 * @version 1.0
 *
 * @see
 * YNI-maker 2013. 5. 7. Initial version
 *
 */
public class ValueObject implements List<ValueRow>, Serializable {
	
	/**
	 * <p>
	 * 서로 다른 자바 컴파일러 사이에서도 동일한 serialVersionUID 값을 보장하기 위해서 명시적 선언
	 * </p>
	 */
	private static final long serialVersionUID = -5611684802481686195L;
	
	 /**
     * <p>
     * 에러나 이벤트와 관련된 각종 메시지를 로깅하기 위한 Log 오브젝트
     * </p>
     */
    private static final Log log = LogFactory.getLog(ValueObject.class);
    
    /**
     * max dump size
     */
    private static final int MAX_LOG_DUMP_SIZE = 10;

    /**
     * ValueObject의 size가 이 건수 단위가 될 때 로그를 찍는다.
     */
    private static final int CHECK_AMOUNT_UNIT_SIZE = Constants.EXCEL_SHEET_ROWS;


    /**
     * ValueObject에 담을 수 있는 최대 row 수 디폴트 값
     */
    private static final int MAX_ROWS = Constants.EXCEL_MAX_ROWS;
    
	private List<ValueRow> tbl = new ArrayList<ValueRow>();

	/**
	 * ValueObject의 명칭
	 */
    private String voName = "";

    /**
     * ValueObject에 담을 수 있는 최대 row 수
     */
    private int maxRows = MAX_ROWS;
    
    /**
     * <p>
     * <strong>ValueObject</strong>의 default 컨스트럭터(Constructor).
     * </p>
     */
    public ValueObject() {
    }

    /**
     * 최대 담을 수 있는 row 수를 지정하여 생성하는 컨스트럭터
     * @param max 최대 row 수
     */
    public ValueObject(int max) {
    	maxRows = max;
    }
    
    /**
     * <p>
     * <strong>ValueObject</strong>의 Name을 지정할 수 있는 Constructor
     *
     * @param voName ValueObject의 Name.
     */
    public ValueObject(String name) {
        voName = name;
    }

    /**
     * <p>
     * <strong>ValueObject</strong>의 Name을 지정할 수 있는 Constructor
     *
     * @param name ValueObject의 Name.
     * @param max 최대 row 수
     */
    public ValueObject(String name, int max) {
        voName = name;
        maxRows = max;
    }
    
    /**
     * <p>
     * <strong>ValueObejct</strong>의 Name을 리턴한다
     * </p>
     *
     * @return <strong>ValueObejct</strong>의 Name : default는 "anonymous"이다.
     */
    public String getName() {
        return voName;
    }

    /**
     * <p>
     * Value에 Object(String)값을 저장한다.<br>
     * : default인 0번 Index를 가지는 Row에 저장한다.
     * </p>
     *
     * @param key   저장할 값의 key
     * @param value Object(String) value
     */
    public void set(String key, Object value) {
        set(0, key, value);
    }

    /**
     * <p>
     * Value에 char값을 저장한다.<br>
     * : default인 0번 Index를 가지는 Row에 저장한다.
     * </p>
     *
     * @param key   저장할 값의 key
     * @param value char value
     */
    public void set(String key, char value) {
        set(0, key, value);
    }

    /**
     * <p>
     * Value에 boolean값을 저장한다.<br>
     * : default인 0번 Index를 가지는 Row에 저장한다.
     * </p>
     *
     * @param key   저장할 값의 key
     * @param value boolean value
     */
    public void set(String key, boolean value) {
        set(0, key, value);
    }


    /**
     * <p>
     * Value에 int값을 저장한다.<br>
     * : default인 0번 Index를 가지는 Row에 저장한다.
     * </p>
     *
     * @param key   저장할 값의 key
     * @param value int value
     */
    public void set(String key, int value) {
        set(0, key, value);
    }

    /**
     * <p>
     * Value에 float값을 저장한다.<br>
     * : default인 0번 Index를 가지는 Row에 저장한다.
     * </p>
     *
     * @param key   저장할 값의 key
     * @param value float value
     */
    public void set(String key, float value) {
        set(0, key, value);
    }

    /**
     * <p>
     * Value에 long값을 저장한다.<br>
     * : default인 0번 Index를 가지는 Row에 저장한다.
     * </p>
     *
     * @param key   저장할 값의 key
     * @param value long value
     */
    public void set(String key, long value) {
        set(0, key, value);
    }

    /**
     * <p>
     * Value에 double값을 저장한다.<br>
     * : default인 0번 Index를 가지는 Row에 저장한다.
     * </p>
     *
     * @param key   저장할 값의 key
     * @param value double value
     */
    public void set(String key, double value) {
        set(0, key, value);
    }
    
    /**
     * <p>
     * Value에 Object(String)값을 저장한다.
     * </p>
     * 
     * @param idx
     * @param key
     * @param value
     */
    public void set(int idx, String key, Object value) {
    	ValueRow row = null;
    	
    	if(idx < tbl.size() && tbl.get(idx) != null) {
    		row = tbl.get(idx);
        } else {
            row = this.createRowInstance();
            add(idx, row);
        }
        row.put(key, value);
    }
    
    /**
     * <p>
     * Value에 char값을 저장한다.
     * </p>
     *
     * @param idx   저장할 값의 Row의 인덱스
     * @param key   저장할 값의 key
     * @param value char value
     */
    public void set(int idx, String key, char value) {
        set(idx, key, new Character(value));
    }

    /**
     * <p>
     * Value에 boolean값을 저장한다.
     * </p>
     *
     * @param idx   저장할 값의 Row의 인덱스
     * @param key   저장할 값의 key
     * @param value boolean value
     */
    public void set(int idx, String key, boolean value) {
        set(idx, key, new Boolean(value));
    }

    /**
     * <p>
     * Value에 int값을 저장한다.
     * </p>
     *
     * @param idx   저장할 값의 Row의 인덱스
     * @param key   저장할 값의 key
     * @param value int value
     */
    public void set(int idx, String key, int value) {
        set(idx, key, new Integer(value));
    }

    /**
     * <p>
     * Value에 float값을 저장한다.
     * </p>
     *
     * @param idx   저장할 값의 Row의 인덱스
     * @param key   저장할 값의 key
     * @param value float value
     */
    public void set(int idx, String key, float value) {
        set(idx, key, new Float(value));
    }

    /**
     * <p>
     * Value에 long값을 저장한다.
     * </p>
     *
     * @param idx   저장할 값의 Row의 인덱스
     * @param key   저장할 값의 key
     * @param value long value
     */
    public void set(int idx, String key, long value) {
        set(idx, key, new Long(value));
    }

    /**
     * <p>
     * Value에 double값을 저장한다.
     * </p>
     *
     * @param idx   저장할 값의 Row의 인덱스
     * @param key   저장할 값의 key
     * @param value double value
     */
    public void set(int idx, String key, double value) {
        set(idx, key, new Double(value));
    }

    /**
     * <p>
     * Key에 해당하는 Object형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public Object get(String key) {
        return get(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 char형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public char getChar(String key) {
        return getChar(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 boolean형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public boolean getBoolean(String key) {
        return getBoolean(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 Sting형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public String getString(String key) {
        return getString(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 int형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public int getInt(String key) {
        return getInt(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 float형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public float getFloat(String key) {
        return getFloat(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 long형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public long getLong(String key) {
        return getLong(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 double형의 Data를 가져온다.
     * </p>
     *
     * @param key 가져올 value의 key
     * @return key에 해당하는 value
     */
    public double getDouble(String key) {
        return getDouble(0, key);
    }

    /**
     * <p>
     * Key에 해당하는 Object형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public Object get(String key, Object defaultNullValue) {
        return get(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 char형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public char getChar(String key, char defaultNullValue) {
        return getChar(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 boolean형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public boolean getBoolean(String key, boolean defaultNullValue) {
        return getBoolean(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 String형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public String getString(String key, String defaultNullValue) {
        return getString(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 int형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public int getInt(String key, int defaultNullValue) {
        return getInt(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 float형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public float getFloat(String key, float defaultNullValue) {
        return getFloat(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 long형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public long getLong(String key, long defaultNullValue) {
        return getLong(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 double형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.<br>
     * : 0번의 Index를 갖는 Row에서 가져온다.
     * </p>
     *
     * @param key              가져올 value의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public double getDouble(String key, double defaultNullValue) {
        return getDouble(0, key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 Object형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public Object get(int idx, String key, Object defaultNullValue) {
        return getRow(idx).get(key,defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 char형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public char getChar(int idx, String key, char defaultNullValue) {
        return getRow(idx).getChar(key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 boolean형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public boolean getBoolean(int idx, String key, boolean defaultNullValue) {
        return getRow(idx).getBoolean(key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 String형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public String getString(int idx, String key, String defaultNullValue) {
        return getRow(idx).getString(key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 int형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public int getInt(int idx, String key, int defaultNullValue) {
        return getRow(idx).getInt(key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 float형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public float getFloat(int idx, String key, float defaultNullValue) {
        return getRow(idx).getFloat(key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 long형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public long getLong(int idx, String key, long defaultNullValue) {
    	return getRow(idx).getLong(key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 double형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에
     * null을 리턴하는데, 이 Method에서는 default value에 해당하는 값을 parameter로 넣어 주면 value가 null일 경우에
     * default 값을 리턴한다.
     * </p>
     *
     * @param idx              가져올 값의 Row의 인덱스
     * @param key              가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public double getDouble(int idx, String key, double defaultNullValue) {
    	return getRow(idx).getDouble(key, defaultNullValue);
    }

    /**
     * <p>
     * Key에 해당하는 Object형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
	public Object get(int idx, String key) {
        return getRow(idx).get(key);
    }

    /**
     * <p>
     * Key에 해당하는 char형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
    public char getChar(int idx, String key) {
        return getRow(idx).getChar(key, ' ');
    }

    /**
     * <p>
     * Key에 해당하는 boolean형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
    public boolean getBoolean(int idx, String key) {
        return getRow(idx).getBoolean(key, false);
    }

    /**
     * <p>
     * Key에 해당하는 String형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
    public String getString(int idx, String key) {
        return getRow(idx).getString(key, null);
    }

    /**
     * <p>
     * Key에 해당하는 int형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
    public int getInt(int idx, String key) {
        return getRow(idx).getInt(key, 0);
    }

    /**
     * <p>
     * Key에 해당하는 float형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
    public float getFloat(int idx, String key) {
        return getRow(idx).getFloat(key, 0F);
    }

    /**
     * <p>
     * Key에 해당하는 long형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
    public long getLong(int idx, String key) {
        return getRow(idx).getLong(key, 0L);
    }

    /**
     * <p>
     * Key에 해당하는 double형의 Data를 리턴한다.
     * </p>
     *
     * @param idx 가져올 값의 Row의 인덱스
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
    public double getDouble(int idx, String key) {
        return getRow(idx).getDouble(key, 0D);
    }

    /**
     * <p>
     * <code>TABLE</code>내에 마지막 Index로 <code>Row</code>를 증가한다
     * </p>
     *
     * @param row TABLE에 추가할 HashMap형태의 Row Data
     */
	public void addRow(ValueRow row) {
        add(row);
    }

    /**
     * <p>
     * <code>TABLE</code>내에 해당 Index로 <code>Row</code>를 증가한다
     * </p>
     *
     * @param row TABLE에 추가할 HashMap형태의 Row Data
     */
	public void addRow(int idx, ValueRow row) {
        add(idx, row);
    }
	
	/**
	 * Map으로부터 ValueRow를 생성하여 row를 추가한다.
	 * @param map
	 */
	public void addRow(Map<String,Object> map) {
		add(new ValueRow(map));
	}
	
	/**
	 * Map으로부터 ValueRow를 생성하여 idx위치에 row를 추가한다.
	 * @param m
	 */
	public void addRow(int idx, Map<String,Object> map) {
		addRow(idx,new ValueRow(map));
	}
	
    /**
     * <p>
     * 0번의 Index를 갖는 Row를 리턴한다.
     * </p>
     *
     * @return Row Data
     * @deprecated
     */
	public ValueRow getRow() {
        return getRow(0);
    }

    /**
     * <p>
     * 해당 Index를 갖는 Row를 {@link java.util.HashMap} 형태로 리턴한다.
     * </p>
     *
     * @param idx 가져올 Row의 Index
     * @throws IndexOutOfBoundsException 해당 Index가 기 존재하는 Row의 Index 범위를 벗어 나는 경우
     */
	public ValueRow getRow(int idx) {
        if (tbl.size() <= idx) {
        	throw new IndexOutOfBoundsException("ValueObject index is out of range. [Index:"+idx+", Size:"+tbl.size()+"]");
        }
        return tbl.get(idx);
    }

    /**
     * <p>
     * 해당 Index를 갖는 Row를 복제(shallow copy)하여 {@link java.util.HashMap} 형태로 리턴한다.
     * </p>
     *
     * @param idx 복제하여 가져올 Row의 Index
     * @throws IndexOutOfBoundsException 해당 Index가 기 존재하는 Row의 Index 범위를 벗어 나는 경우
     * @author 김형도(2007.11.14)
     */
	public ValueRow cloneRow(int idx) {
    	return getRow(idx).clone();
    }
    
    /**
     * <p>
     * 0번 Index를 갖는 Row를 복제(shallow copy)하여 {@link java.util.HashMap} 형태로 리턴한다.
     * </p>
     *
     * @param idx 복제하여 가져올 Row의 Index
     * @throws IndexOutOfBoundsException 해당 Index가 기 존재하는 Row의 Index 범위를 벗어 나는 경우
     * @author 김형도(2007.11.14)
     */
	public ValueRow cloneRow() {
    	return getRow(0).clone();
    }
    
    /**
     * <p>
     * 해당 Index의 Row Data를 Update 한다.
     * </p>
     *
     * @param idx Update 하고자 하는 Row의 Index
     * @param row Update 하는 Row Data 의 {@link java.util.HashMap} 형태
     * @throws IndexOutOfBoundsException 해당 Index가 기 존재하는 Row의 Index 범위를 벗어 나는 경우
     */
	public void setRow(int idx, ValueRow row) {
        if (tbl.size() <= idx) {
        	throw new IndexOutOfBoundsException("ValueObject index is out of range. [Index:"+idx+", Size:"+tbl.size()+"]");
        }

        if (row == null) {
            row = createRowInstance();
        }

        tbl.set(idx, row);
    }

    /**
     * <p>
     * 0번의 Index의 Row Data를 Update 한다.
     * </p>
     *
     * @param row Update 하는 Row Data 의 {@link java.util.HashMap} 형태
     * @throws IndexOutOfBoundsException 해당 Index가 기 존재하는 Row의 Index 범위를 벗어 나는 경우
     */
	public void setRow(ValueRow row) {
        setRow(0, row);
    }

    /**
     * <p>
     * Table을 {@link java.util.List} 오브젝트로 리턴한다. 리턴된 List Object 내부에 각각의 요소는
     * {@link java.util.HashMap}으로 구성되어 졌다.
     * </p>
     *
     * @return TABLE
     */
	public List<ValueRow> getTable() {
        return tbl;
    }

    /**
     * <p>
     * Table을 {@link java.util.List&lt;ValueRow&gt;}를 사용하여 값을 채운다.<br>
     * </p>
     *
     * @param tbl 소스 데이터
     */
	public void setTable(List<ValueRow> tbl) {
        clear();
        if (tbl != null) {
            for (int i = 0; i < tbl.size(); i++) {
                add(tbl.get(i));
            }
        }
    }

	/**
	 * Table을 Map[]를 사용하여 값을 채운다.
	 * @param mapArray 소스 데이터
	 */
	public void setTable(Map<String,Object>[] mapArray) {
		clear();
		if (mapArray != null) {
			for (int i=0;i<mapArray.length;i++) {
				add(new ValueRow(mapArray[i]));
			}
		}
	}
	
    /**
     * <p>
     * Table에 {@link java.util.List} 오브젝트 data를 이용하여 row들을 추가한다.<br>
     * <strong>단. {@link java.util.List}의 각각의 요소는 {@link java.util.HashMap}으로 구성되어 있어야 한다.</strong>
     * </p>
     *
     * @param tbl TABLE을 지정하는 Data
     */
	public void addTable(List<ValueRow> tbl) {
        if (tbl != null) {
            for (int i = 0; i < tbl.size(); i++) {
                add(tbl.get(i));
            }
        }
    }

    /**
     * <p>
     * <strong>TABLE</strong>의 <strong>Row</strong> 인스턴스 생성한다.
     * </p>
     *
     * @return Row 인스턴스
     */
	private ValueRow createRowInstance() {
        return new ValueRow();
    }

    /**
     * <p>
     * 해당 Index의 Row을 삭제한다.
     * </p>
     *
     * @param idx 삭제하고자 하는 Row의 Index
     * @return 삭세된 row
     */
    public ValueRow remove(int idx) {
        return tbl.remove(idx);
    }

    /**
     * <p>
     * 해당 Index의 Row안에 해당 key를 갖는 value를 삭제 한다.
     * 삭제된 value의 위치는 비워 두지 않는다.
     * </p>
     *
     * @param idx 삭제할 key가 있는 Row의 Index
     * @param key Row안의 삭제할 key값
     * @throws IndexOutOfBoundsException 해당 Index가 기 존재하는 Row의 Index 범위를 벗어 나는 경우
     */
	public Object remove(int idx, String key) throws IndexOutOfBoundsException {
        return getRow(idx).remove(key);
    }

    /**
     * <p>
     * 해당 Index의 Row안의 해당 key 존재 유무 여부 체크
     * </p>
     *
     * @param idx 찾고자하는 Row의 Index
     * @param key Row안의 찾고자 하는 key값
     */
	public boolean isExist(int idx, String key) {
    	ValueRow row = null;
        try {
            row = getRow(idx);
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }

        return row.containsKey(key);
    }

    /**
     * <p>
     * 0번을 갖는 Index의 Row안의 해당 key 존재 유무 여부 체크
     * </p>
     *
     * @param key Row안의 찾고자 하는 key값
     */
    public boolean isExist(String key) {
        return isExist(0, key);
    }

    /**
     * <p>
     * <strong>TABLE</strong> 안의 <strong>Row</strong>의 개수
     * </p>
     *
     * @return Row의 개수
     */
    public int size() {
        return tbl.size();
    }

    /**
     * <p>
     * <strong>TABLE</strong> 안의 모든 Data 삭제
     * </p>
     */
    public void clear() {
        tbl.clear();
    }

    /**
     * <p>
     * <strong>TABLE</strong>의 idx 번째 row를 <strong>Map</strong>객체로 리턴
     * </p>
     *
     * @param idx 리턴 받고자 하는 Row의 index
     * @return Row의 Map 인스턴스
     */
    public Map<String,Object> getRowAsMap(int idx) {
    	return getRow(idx).getMap();
    }
    
    /**
     * <p>
     * <strong>TABLE</strong>의 첫번째 row를 <strong>Map</strong>객체로 리턴
     * </p>
     *
     * @return Row의 Map 인스턴스
     * @deprecated
     */
    public Map<String,Object> getRowAsMap() {
    	return getRowAsMap(0);
    }
    
    /**
     * <p>
     * <strong>TABLE</strong>의 idx 번째 row를 <strong>ValueObject</strong>객체로 리턴
     * </p>
     *
     * @param idx 리턴 받고자 하는 Row의 index
     * @return Row의 ValueObject 인스턴스
     */
	public ValueObject getRowAsVo(int idx) {
        ValueObject vo = new ValueObject();
        //vo.add(getRow(idx).clone());
        vo.add(getRow(idx));
        return vo;
    }

    /**
     * <p>
     * <strong>TABLE</strong>의 첫번째 row를 <strong>ValueObject</strong>객체로 리턴
     * </p>
     *
     * @param idx 리턴 받고자 하는 Row의 index
     * @return Row의 ValueObject 인스턴스
     * @deprecated
     */
	public ValueObject getRowAsVo() {
		return getRowAsVo(0);
	}
	
    /**
     * <p>
     * 0번 index를 가진 <strong>Row</strong>의 dump : TRACE Level
     * </p>
     * @deprecated
     * @param applog commons logging 오브젝트
     */
    public void dumpRow(Log applog) {
        dumpRow(0, applog);
    }

    /**
     * <p>
     * 해당 index의 <strong>Row</strong> dump : TRACE Level
     * </p>
     * @deprecated
     * @param idx dump 하고자 하는 Row index
     * @param applog commons logging 오브젝트
     */
	public void dumpRow(int idx, Log applog) {
        if (applog.isTraceEnabled()) {
        	ValueRow row = getRow(idx);
            if (row == null) {
                return;
            }
            row.dump(idx,applog);
        }
    }

    /**
     * <p>
     * <strong>TABLE</strong> dump : TRACE Level
     * </p>
     *
     * @param applog commons logging 오브젝트
     */
	public void dumpTable(Log applog) {
        dumpTable(applog,MAX_LOG_DUMP_SIZE);
    }

    /**
     * <p>
     * <strong>TABLE</strong> dump : TRACE Level
     * </p>
     *
     * @param applog  commons logging 오브젝트
     * @param maxSize Log를 찍을수 있는 최대 row 수
     */
	public void dumpTable(Log applog, int maxSize) {
        if (applog.isTraceEnabled()) {

            if (tbl == null) {
                return;
            }

            int len;

            if (maxSize < tbl.size()) {
                len = maxSize;
            } else {
                len = tbl.size();
            }

            for (int i = 0; i < len; i++) {
            	tbl.get(i).dump(i,applog);
            }

            if (maxSize < tbl.size()) {
                if (applog.isTraceEnabled()) {
                    log.trace("\n .............. The rest is ommitted. (이하 생략), \t [Row Size:" + tbl.size() + ", MAX Size:" + maxSize + "]");
                }
            }
        }
    }

	public void dumpTable(OutputStream out, int maxSize) {
        
        if (tbl == null) {
            return;
        }

        int len = 0;
        if (maxSize < tbl.size()) {
            len = maxSize;
        } else {
            len = tbl.size();
        }

        for (int i = 0; i < len; i++) {
        	tbl.get(i).dump(i,out);
        }

        if (maxSize < tbl.size()) {
        	String msg = "\n .............. The rest is ommitted. (이하 생략), \t [Row Size:" + tbl.size() + ", MAX Size:" + maxSize + "]";
        	try {
				out.write(msg.getBytes());
			} catch (IOException ignored) {}  
        }
    }
    /**
     * <p>
     * <strong>ValueObject</strong>의 실제 크기를 KByte 단위로 반환한다.
     * </p>
     *
     * @param applog commons logging 오브젝트
     */
    public void dumpObjectSize(Log applog) {

        if (applog.isTraceEnabled()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(this);
                applog.trace("ValueObject(" + getName() + ")'s size : " + baos.size() / 1024 + "KB");
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("ValueObject.getSize() Error", e);
                }
            }
        }

    }

	public boolean add(ValueRow row) {
    	if (tbl.size() >= maxRows) {
    		try {
				throw new TooManyRowsException("max rows="+maxRows);
			} catch (TooManyRowsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
        if (row == null) {
            row = createRowInstance();
        }
        
        if (log.isDebugEnabled()) {
            if ((tbl.size()+1) % CHECK_AMOUNT_UNIT_SIZE == 0) {
                Throwable th = new Throwable();
                StackTraceElement ste[] = th.getStackTrace();
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < ste.length; i++) {
                    sb.append(ste[i].toString());
                    sb.append("\n");
                    if (i > 20) {
                        sb.append("... omitted");
                        break;
                    }
                }
                log.debug("ValueObject size checker : " + tbl.size() + "'th row added at \n" + sb.toString());
            }
        }
        return tbl.add(row);
    }

	public void add(int idx, ValueRow row) {
        if (size() < idx) {
        	throw new IndexOutOfBoundsException("ValueObject index is out of range. [Index:"+idx+", Size:"+tbl.size()+"]");
        } else if (idx >= maxRows) {
    		try {
				throw new TooManyRowsException("max rows="+maxRows);
			} catch (TooManyRowsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

        if (row == null) {
            row = createRowInstance();
        }
        
    	if (log.isDebugEnabled()) {
            if ((tbl.size()+1) % CHECK_AMOUNT_UNIT_SIZE == 0) {
                Throwable th = new Throwable();
                StackTraceElement ste[] = th.getStackTrace();
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < ste.length; i++) {
                    sb.append(ste[i].toString());
                    sb.append("\n");
                    if (i > 20) {
                        sb.append("... omitted");
                        break;
                    }
                }
                log.debug("ValueObject size checker : " + tbl.size() + "'th row added at \n" + sb.toString());
            }
        }
    	tbl.add(idx, row);
    }
	
	public boolean addAll(Collection<? extends ValueRow> c) {
		return tbl.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends ValueRow> c) {
		return tbl.addAll(c);
	}

	public boolean contains(Object o) {
		return tbl.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return tbl.containsAll(c);
	}

	public ValueRow get(int idx) {
        if (tbl.size() <= idx) {
            throw new IndexOutOfBoundsException("ValueObject index is out of range. [Index:"+idx+", Size:"+tbl.size()+"]");
        }
		return tbl.get(idx);
	}

	public int indexOf(Object o) {
		return tbl.indexOf(o);
	}

	public boolean isEmpty() {
		return tbl.isEmpty();
	}

	public Iterator<ValueRow> iterator() {
		return tbl.iterator();
	}

	public int lastIndexOf(Object o) {
		return tbl.lastIndexOf(o);
	}

	public ListIterator<ValueRow> listIterator() {
		return tbl.listIterator();
	}

	public ListIterator<ValueRow> listIterator(int index) {
		return tbl.listIterator(index);
	}

	public boolean remove(Object o) {
		return tbl.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return tbl.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return tbl.retainAll(c);
	}

	public ValueRow set(int index, ValueRow element) {
		return tbl.set(index, element);
	}

	public List<ValueRow> subList(int fromIndex, int toIndex) {
		return tbl.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return tbl.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return tbl.toArray(a);
	}
}

