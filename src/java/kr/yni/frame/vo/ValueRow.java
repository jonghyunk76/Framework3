package kr.yni.frame.vo;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

/**
 * <code>kr.yni.fta.origin.vo.ValueObject</code>의 Row값을 구하는 클래스 
 * 
 * @author YNI-maker
 * @since 2013. 5. 6. 오후 3:52:45
 * @version 1.0
 *
 * @see
 * YNI-maker 2013. 5. 6. Initial version
 *
 */
public class ValueRow {
	private Map<String,Object> row = null;

	public ValueRow() {
		row = new LinkedHashMap<String,Object>();
	}
	
	public ValueRow(Map<String,Object> map) {
		row = map;
	}
	
	public ValueRow(ValueRow r) {
		row = r.getMap();
	}
	
	public ValueRow clone() {
		return new ValueRow(new LinkedHashMap<String,Object>(row));
	}

	public Map<String,Object> getMap() {
		return row;
	}
	
    /**
     * <p>
     * key에 해당하는 value를 가져온다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @return key에 해당하는 value
     */
	public Object get(Object key) {
		return row.get(key);
	}
	
    /**
     * <p>
     * key에 해당하는 value를 가져온다.
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param defaultValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public Object get(String key, Object defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else {
            return obj;
        }
    }

    /**
     * <p>
     * Key에 해당하는 char형의 Data를 가져온다.
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param defaultValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public char getChar(String key, char defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else {
            return ((Character)obj).charValue();
        }
    }

    /**
     * <p>
     * Key에 해당하는 boolean형의 Data를 가져오는데,
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param  defaultValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof Boolean) {
            return ((Boolean)obj).booleanValue();
        } else {
        	String str = String.valueOf(obj);
        	return Boolean.parseBoolean(str);
        }
    }

    /**
     * <p>
     * Key에 해당하는 String형의 Data를 가져온다.
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * value가 배열 타입이라면 하부 데이터를 풀어서 문자열로 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public String getString(String key, String defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof Double || obj instanceof Float) {
        	// 지수 표현식으로 반환되는 것을 막기 위하여 BigDecimal로 중간 변환하여 처리함.(2009/11/18 김형도)
        	return BigDecimal.valueOf(((Number)obj).doubleValue()).toString();
        } else {
            return obj.toString();
        }
    }

    /**
     * <p>
     * Key에 해당하는 int형의 Data를 가져온다.
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public int getInt(String key, int defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof Number){
            return ((Number)obj).intValue();
        } else {
        	String str = String.valueOf(obj);
        	if (str.length() == 0) {
        		return defaultValue;
        	} else {
        		return Integer.parseInt(str);
        	}
        }
    }

    /**
     * <p>
     * Key에 해당하는 float형의 Data를 가져온다.
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public float getFloat(String key, float defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof Number) {
            return ((Number)obj).floatValue();
        } else {
        	String str = String.valueOf(obj);
        	if (str.length() == 0) {
        		return defaultValue;
        	} else {
        		return Float.parseFloat(str);
        	}
        }
    }

    /**
     * <p>
     * Key에 해당하는 long형의 Data를 가져온다.
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public long getLong(String key, long defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof Number) {
            return ((Number)obj).longValue();
        } else {
        	String str = String.valueOf(obj);
        	if (str.length() == 0) {
        		return defaultValue;
        	} else {
        		return Long.parseLong(str);
        	}
        }
    }

    /**
     * <p>
     * Key에 해당하는 double형의 Data를 가져온다.
     * 해당 key가 존재하지 않거나, key의 value가 null일 경우에는 defaultValue를 반환한다.
     * </p>
     *
     * @param key 가져올 값의 key
     * @param defaultNullValue 리턴 값이 NULL일 경우 넘겨줘야 할 값
     * @return key에 해당하는 value
     */
    public double getDouble(String key, double defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof Number) {
            return ((Number)obj).doubleValue();
        } else {
        	String str = String.valueOf(obj);
        	if (str.length() == 0) {
        		return defaultValue;
        	} else {
        		return Double.parseDouble(str);
        	}
        }
    }

	public void clear() {
		row.clear();
	}

	public boolean containsKey(Object key) {
		return row.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return row.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return row.entrySet();
	}

	public boolean isEmpty() {
		return row.isEmpty();
	}

	public Set<String> keySet() {
		return row.keySet();
	}

	public Object put(String key, Object value) {
		return row.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> map) {
		row.putAll(map);
	}

	public Object remove(Object key) {
		return row.remove(key);
	}

	public int size() {
		return row.size();
	}

	public Collection<Object> values() {
		return row.values();
	}
    
    /**
     * <p>
     * 해당 index의 <strong>Row</strong> dump : TRACE Level
     * </p>
     *
     * @param idx    dump 하고자 하는 Row index
     * @param applog commons logging 오브젝트
     */
	public void dump(int idx, Log applog) {
        if (applog.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("(").append(idx).append(")");
            sb.append(toString());
            applog.trace(sb.toString());
        }
    }
	
    /**
     * <p>
     * 해당 index의 <strong>Row</strong> dump : OutputStream으로 출력
     * </p>
     *
     * @param idx dump 하고자 하는 Row index
     * @param out 출력 대상 OutputStream 객체
     */
	public void dump(int idx, OutputStream out) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(idx).append(")");
        sb.append(toString()).append("\n");
        try {
			out.write(sb.toString().getBytes());
		} catch (IOException ignore) {}
    }
	
	/**
	 * <p>
	 * Map에 저장된 데이터를 JSON 포맷으로 변환 후 리턴한다.
	 * </p>
	 * <p>
	 * JSONObject = {"Object Name":[{"name1":"value1", "name2":"value2"}]};
	 * </p>
	 */
	public String toString() {
		Iterator<String> ir = keySet().iterator();

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String keyTmp = null;
        Object value = null;
        
        while(ir.hasNext()) {
            keyTmp = ir.next();
            sb.append("{");
            sb.append(keyTmp);
            sb.append(":");
            value = get(keyTmp);
            if (value == null) {
            	sb.append("null");
            } else if (value instanceof String) {
            	sb.append("\"");
            	sb.append(value);
            	sb.append("\"");
            } else if (value instanceof char[]) {
            	sb.append("'");
            	sb.append((char[])value);
            	sb.append("'");
            } else if (value instanceof Object[]) {
            	Object[] arr = (Object[])value;
            	sb.append("(");
            	for(int i=0;i<arr.length-1;i++) {
            		sb.append(arr[i]);
            		sb.append(",");
            	}
            	if (sb.lastIndexOf(",") == sb.length()-1) {
            		sb.deleteCharAt(sb.length()-1);
            	}
            	sb.append(")");
            } else {
            	sb.append(value);
            }
            sb.append("},");
        }

        // 마지막 ',' 짜르기;
        if (sb.lastIndexOf(",") == sb.length()-1) {
            sb.deleteCharAt(sb.length()-1);
        }

        sb.append("]");
        
        return sb.toString();
	}
}
