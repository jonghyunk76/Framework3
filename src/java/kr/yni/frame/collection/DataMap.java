package kr.yni.frame.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.exception.FrameException;
import kr.yni.frame.util.StringHelper;

/**
 *요청 파라미터 및 응답 데이타를 담는데 사용되는 유틸리티 클래스
 *                        
 * @author YNI-maker
 *
 */
public class DataMap implements Serializable, Cloneable {
	
	protected static Log log = LogFactory.getLog(DataMap.class);
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -7517688282979333170L;

	private Map<String, Object> session;

	private DBBasedHashMap map;

	public DBBasedHashMap getMap() {
		return map;
	}

	/**
	 * Comment for <code>BEGIN_LOOP</code>
	 */
	public static final String BEGIN_LOOP = "_BeginLoop_";

	/**
	 * Comment for
	 * <code>CONTEXT_NAME 컨텍스트 명. ControllerServlet 레벨에서 셋팅하여야 함.</code>
	 */
	public static final String CONTEXT_NAME = "_CONTEXT_NAME";

	/**
	 * 디폴트생성자 - initialCapacity : 500
	 */
	public DataMap() {
		map = new DBBasedHashMap(500);
	}

	/**
	 * @param initialCapacity
	 */
	public DataMap(int initialCapacity) {
		map = new DBBasedHashMap(initialCapacity);
	}

	/**
	 * Map을 인자로 해서 DataMap 생성
	 * 
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public DataMap(Map map) {
		this.map = new DBBasedHashMap(map);
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@SuppressWarnings("rawtypes")
	public Map getSession() {
		return this.session;
	}

	/**
	 * DataMap에서 key에 해당하는 value 값을 얻어온다.
	 * 
	 * @param name
	 *            DataMap에서 가져올 key값
	 * @return key에 해당하는 value Object
	 */
	public Object get(String name) {
		Object obj = this.map.get(name);
		return obj;
	}

	@SuppressWarnings("unchecked")
	public void put(String name, int value) {
		this.map.put(name, new Integer(value));
	}

	@SuppressWarnings("unchecked")
	public void put(String name, long value) {
		this.map.put(name, new Long(value));
	}

	@SuppressWarnings("unchecked")
	public void put(String name, float value) {
		this.map.put(name, new Float(value));
	}

	@SuppressWarnings("unchecked")
	public void put(String name, double value) {
		this.map.put(name, new Double(value));
	}

	@SuppressWarnings("unchecked")
	public void put(String name, boolean value) {
		this.map.put(name, value);
	}

	@SuppressWarnings("unchecked")
	public void put(String name, Object value) {
		this.map.put(name, value);
	}

	/**
	 * DataMap에서 key에 해당하는 Value값을 String으로 얻어온다.
	 * 
	 * @param paramName
	 *            DataMap에서 가져올 key값
	 * @return key에 해당하는 value String
	 */
	public String getString(String paramName) {
		Object obj = this.map.get(paramName);
		if (obj == null) {
			return null;
		} else {
			return obj.toString();
		}
	}

	/**
	 * DataMap에서 key에 해당하는 Value값을 Int로 얻어온다.
	 * 
	 * @param paramName
	 *            DataMap에서 가져올 key값
	 * @return 0 if value is null, else return the value
	 * @throws NumberFormatException
	 *             value 값이 Integer 타입이 아닐경우
	 */
	public int getInt(String paramName) throws NumberFormatException {
		String value = getString(paramName);
		if (value == null) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	/**
	 * DataMap에서 key에 해당하는 Value값을 Long으로 얻어온다.
	 * 
	 * @param paramName
	 *            DataMap에서 가져올 key값
	 * @return key에 해당하는 value Long
	 * @throws NumberFormatException
	 *             value 값이 Long 타입이 아닐경우
	 */
	public long getLong(String paramName) throws NumberFormatException {
		String value = getString(paramName);
		if (value == null) {
			return 0;
		}
		return Long.parseLong(value);
	}

	/**
	 * y,Y,Yes,yes,T,true,True,On,on모두 true를 리턴한다. 값이 없거나, 위의 값이 아니면 false를
	 * 리턴한다.
	 * 
	 * @param paramName
	 * @return
	 */
	public boolean getBoolean(String paramName) {
		String value = getString(paramName);
		if (value == null) {
			return false;
		}
		return StringHelper.getBoolean(value, false);
	}

	/**
	 * <font color=red> 해당 하는 파라미터 값을 얻어 옵니다. 해당 값이 없을 때 OriginException을 발생 시킵니다.
	 * 따라서 개발자는 해당 값이 파라미터로 전달 된 값이 없을 때 에러 유발을 원치 않는 다면 getParameter(String
	 * paramName, String defaultValue) 메소드를 호출 합니다. 이 메소드는 해당 값이 없으면 default값을
	 * 리턴 합니다. 다시 말해 이 메소드는 필수 입력 값을 얻어 내는데 사용하면 편리 합니다. 즉 필수 입력값이 없으면 파라미터가 없다는
	 * 에러를 내어 알려 줍니다. </font>
	 * 
	 * @param paramName
	 * @return String 파라미터값
	 * @throws FrameException
	 *             값이 없을 경우 (개발자가 매번 null check를 하지 않도록 하기 위해)
	 */
	public String getParameter(String paramName) throws FrameException {
		String str = getString(paramName);
		if (StringHelper.isNullNotTrim(str)) {
			throw new FrameException(paramName);
		} else {
			return str;
		}
	}

	/**
	 * DataMap에서 가져 온다.
	 * 
	 * @param paramName
	 *            DataMap에서 가져올 Key값
	 * @param defaultValue
	 *            paramName이 null일때 설정할 기본값
	 * @return DataMap에서 Key값으로 가져온 Value
	 */
	public String getParameter(String paramName, String defaultValue) {
		String str = getString(paramName);
		if (StringHelper.isNullNotTrim(str)) {
			return defaultValue;
		} else {
			return (String) str;
		}
	}

	/**
	 * DataMap에서 배열 형태로 가져 온다. 값이 없으면 에러가 발생하지 않고 null을 리턴한다.
	 * 
	 * @param paramName
	 *            가져올 Key값
	 * @return DataMap에 DataMap에서 배열로 저장된 Value
	 */
	public String[] getParameterValues(String paramName) {
		String[] strArr = getStringArray(paramName);
		return strArr;
	}

	/**
	 * DataMap에서 int 형태로 가져 온다.
	 * 
	 * @param paramName
	 *            가져올 Key값
	 * @return DataMap에서 Key값으로 가져온 int형 value값
	 * @throws FrameException
	 *             값이 없을 경우 (개발자가 매번 null check를 하지 않도록 하기 위해)
	 */
	public int getIntParameter(String paramName) throws FrameException {
		String str = getParameter(paramName);

		int i = 0;
		try {
			i = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new FrameException("숫자 형식이 아닙니다--" + str);
		}
		return i;
	}

	/**
	 * DataMap에서 int 형태로 가져 온다.
	 * 
	 * @param paramName
	 *            가져올 Key값
	 * @param defaultValue
	 *            Key로 가져온 Value가 null일경우
	 * @return DataMap에서 Key값으로 가져온 int형 value값
	 * @throws FrameException
	 *             값이 없을 경우 (개발자가 매번 null check를 하지 않도록 하기 위해)
	 */
	public int getIntParameter(String paramName, int defaultValue)
			throws FrameException {
		String str = getParameter(paramName, "");
		if (StringHelper.isNull(str)) {
			return defaultValue;
		}
		return getIntParameter(paramName);
	}

	/**
	 * DataMap에서 가져 온다. 필수 입력 정보일때 사용 한다.
	 * 
	 * @param paramName
	 *            DataMap에서 가져올 Key값
	 * @return DataMap에서 Key값으로 가져온 Object형 Value
	 * @throws FrameException
	 *             값이 없을 경우 (개발자가 매번 null check를 하지 않도록 하기 위해)
	 */
	public Object getObjectParameter(String paramName) throws FrameException {
		Object obj = get(paramName);
		if (obj == null) {
			throw new FrameException(paramName);
		} else {
			return obj;
		}
	}

	/**
	 * DataMap에서 가져 온다. 필수 입력 정보일때 사용 한다.
	 * 
	 * @param paramName
	 *            DataMap에서 가져올 Key값
	 * @param defaultObj
	 *            paramName Key로 가져온 데이타가 없을시에 기본 Obfjv
	 * @return DataMap에서 Key값으로 가져온 Object형 Value
	 * @throws FrameException
	 *             값이 없을 경우 (개발자가 매번 null check를 하지 않도록 하기 위해)
	 */
	public Object getObjectParameter(String paramName, Object defaultObj) {
		Object obj = get(paramName);
		if (obj == null) {
			return defaultObj;
		} else {
			return obj;
		}
	}

	/**
	 * DataMap에서 가져 온다.
	 * 
	 * @param DataMap에서
	 *            가져올 Key값
	 * @return DataMap에서 Key로 가져온 doble형 value
	 * @throws FrameException
	 *             값이 없을 경우 (개발자가 매번 null check를 하지 않도록 하기 위해)
	 */
	public double getDoubleParameter(String paramName) throws FrameException {
		String str = getParameter(paramName);

		double i = 0;
		try {
			i = Double.parseDouble(str);
		} catch (NumberFormatException e) {
			throw new FrameException("숫자 형식이 아닙니다--" + str);
		}
		return i;
	}

	/**
	 * DataMap에서 가져 온다.
	 * 
	 * @param DataMap에서
	 *            가져올 Key값
	 * @param defaultValue
	 *            기본
	 * @return DataMap에서 Key로 가져온 doble형 value
	 * @throws FrameException
	 *             값이 없을 경우 (개발자가 매번 null check를 하지 않도록 하기 위해)
	 */
	public double getDoubleParameter(String paramName, double defaultValue) {
		String str = getParameter(paramName, "");
		if (StringHelper.isNull(str)) {
			return defaultValue;
		}
		double i = 0;
		try {
			i = Double.parseDouble(str);
		} catch (NumberFormatException e) {
			throw e;
		}
		return i;
	}

	/**
	 * DataMap안에 있는 객체를 array로 얻어 오는 로직
	 * 
	 * @param paramName
	 * @return
	 * 
	 */
	public String[] getStringArray(String paramName) {
		Object obj = this.map.get(paramName);
		String param = null;
		String[] params = null;
		
		if (obj instanceof String[]) {
			return (String[]) obj;
		} else if (obj instanceof Collection) {
			return null;
		} else if (obj instanceof Object[]) {
			Object[] objArray = (Object[]) obj;
			String[] array = new String[objArray.length];
			
			for (int i = 0; i < objArray.length; i++) {
				try {
					array[i] = objArray[i].toString();
				} catch (NullPointerException e) {
					array[i] = "";
				}
			}
			return array;
		} else if (obj != null) {
			String[] array = new String[1];
			array[0] = obj.toString();
			
			return array;
		} else {
			param = getParameter(paramName, null);
			
			if (param == null) {
				log.debug("responseMap에" + paramName + "으로 등록된 객체가 없습니다.");
				
				return null;
			} else {
				params = new String[1];
				params[0] = param;
				
				return params;
			}
		}
	}

	/**
	 * DataMap을 clone한다.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		DataMap dataMap = new DataMap();
		dataMap.putAll((Map<String, Object>) this.map.clone());
		return dataMap;
	}

	/**
	 * Copies all of the mappings from the specified map to this map These
	 * mappings will replace any mappings that this map had for any of the keys
	 * currently in the specified map.
	 * 
	 * @param m
	 *            mappings to be stored in this map.
	 */
	@SuppressWarnings("unchecked")
	public void putAll(Map<String, Object> m) {
		this.map.putAll(m);
	}

	/**
	 * debugging을 위한 메소드 추가
	 */
	@SuppressWarnings("rawtypes")
	public String toString() {
		String key = null;
		Object item = null;
		
		if (this.map.isEmpty()) {
			return "DataMap is empty.";
		}
		
		StringBuffer buf = new StringBuffer(2000);
		Set keySet = this.map.keySet();
		Iterator i = keySet.iterator();
		
		while (i.hasNext()) {
			try {
				key = i.next().toString();
				
				if ("q".equals(key) || "p".equals(key)) continue;
				
				item = get(key);
				
				if (item == null) {
					buf.append(key + "=null");
				} else if (item instanceof String) {
					if (item == null || ((String) item).length() == 0) {
						item = "";
					}
					
					buf.append(key + "=[" + item + "]");
				} else if (item instanceof Integer || item instanceof Long
						|| item instanceof Double
						|| item instanceof java.lang.Float
						|| item instanceof Boolean)
					buf.append(key + "=[" + item + "]");
				else if (item instanceof String[]) {
					String data[] = (String[]) item;
					buf.append(key + "=[");
					int j;
					for (j = 0; j < data.length; j++) {
						buf.append(data[j]);
					}
					
					if (j < (data.length - 1)) {
						buf.append(",");
					}
					
					buf.append("] Array Size:" + j + " ");
				} else {
					buf.append(key + "=[" + item + "] ClassName:" + item.getClass().getName() + "");
				}
			} catch (Exception ignore) {
				log.error(ignore.getMessage());
			}
		}
		
		if(log.isDebugEnabled()) log.debug("end of DataMap info :" + buf.toString());
		
		return "";
	}

	public String getDataMapInfo() {
		return toString();
	}

	public String getRequestURI() {
		return (String) get("URI");
	}

	@SuppressWarnings("unchecked")
	public void setRequestURI(String uri) {
		this.map.put("URI", uri);
	}

	/**
	 * CONTEXT_NAME 값을 얻어 옵니다.
	 * 
	 * @return 컨텍스트 명
	 */
	public String getContextName() {
		return getString(CONTEXT_NAME);
	}

	/**
	 * CONTEXT_NAME을 세팅합니다.
	 * 
	 * @param 컨텍스트
	 *            명
	 */
	@SuppressWarnings("unchecked")
	public void setContextName(String contextName) {
		this.map.put(CONTEXT_NAME, contextName);
	}

	public void clear() {
		this.map.clear();
	}

	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return this.map.containsValue(value);
	}

	@SuppressWarnings("rawtypes")
	public Set entrySet() {
		return this.map.entrySet();
	}

	public Object get(Object key) {
		return this.map.get(key);
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@SuppressWarnings("rawtypes")
	public Set keySet() {
		return this.map.keySet();
	}

	@SuppressWarnings("unchecked")
	public Object put(Object key, Object value) {
		return this.map.put(key, value);
	}

	public Object remove(Object key) {
		return this.map.remove(key);
	}

	public int size() {
		return this.map.size();
	}

	@SuppressWarnings("rawtypes")
	public Collection values() {
		return this.map.values();
	}

}