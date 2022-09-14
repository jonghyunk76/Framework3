package kr.yni.frame.collection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import kr.yni.frame.util.StringHelper;

/**
*
* PROGRAM ID : DBBasedHashMap.java 
* PROGRAM NAME : DBBasedHashMap 
* DESCRIPTION : DataMap에서 사용하는 HashMap객체. get()메소드가 호출시에 원본 key로 get()한 객체가 null이면 db 컬럼
 *                       Style로 한번더 get메소드 호출. 사용법은 HashMap과 동일하며, 추가적인 기능을 가지고 있음
* 
* PROJECT :
* --------------------------------------------------------------------
* HISTORY VERSION DATE AUTHOR DESCRIPTION
* -------------------------------------------------------------------- 
* 2009. 11. 25. 이동훈 최초작성
**********************************************************************
*
* @author YNI-maker
*
*/
@SuppressWarnings("rawtypes")
public class DBBasedHashMap extends LinkedHashMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9168899778865879029L;

	/**
	 * 디폴트생성자 - initialCapacity : 500
	 */
	public DBBasedHashMap() {
		super(500);
	}

	/**
	 * @param initialCapacity
	 */
	public DBBasedHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Map을 인자로 해서 DataMap 생성
	 * 
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	public DBBasedHashMap(Map map) {
		super(map);
	}

	public Object get(Object key) {
		Object obj = super.get(key);
		// super.containsKey(key);
		if (obj == null && key instanceof String) {
			String keyName = (String) key;
			obj = super.get(StringHelper.toDbStyle(keyName));
		}
		return obj;
	}
}