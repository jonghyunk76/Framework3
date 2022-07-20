package kr.yni.frame.mapper.type;

import com.sap.mw.jco.JCO;

/**
 * <p>
 * 특정 Data Type를 검증하거나 변환하기 위한 클래스
 * </p>
 * 
 * @author 김종현 (ador218@bluesolution.co.kr)
 * 
 */
public class ColumnTypeCaster {

	/**
	 * RFC Type 정보로 변환하는 매소드
	 * 
	 * @param value
	 * @param id
	 * @return
	 */
	public static int changeRfcType(String type) {
		int rfc = JCO.TYPE_CHAR;
		if (type != null) {
			if ("DATS".equals(type))
				rfc = JCO.TYPE_DATE; // 1
			else if ("CHAR".equals(type))
				rfc = JCO.TYPE_CHAR; // 0
			else if ("UNIT".equals(type))
				rfc = JCO.TYPE_CHAR; // 0
			else if ("NUMC".equals(type))
				rfc = JCO.TYPE_NUM; // 6
			else if ("QUAN".equals(type))
				rfc = JCO.TYPE_FLOAT; // 7
			else if ("CUKY".equals(type))
				rfc = JCO.TYPE_CHAR; // 0
			else if ("CURR".equals(type))
				rfc = JCO.TYPE_FLOAT; // 7
			else if ("DEC".equals(type))
				rfc = JCO.TYPE_NUM; // 6
			else if ("BYTE".equals(type))
				rfc = JCO.TYPE_BYTE; // 4
			else if ("table".equals(type))
				rfc = JCO.TYPE_TABLE; // 99
			else if ("structure".equals(type))
				rfc = JCO.TYPE_STRUCTURE; // 17
			else if ("itable".equals(type))
				rfc = JCO.TYPE_ITAB; // 5
			else if ("json".equals(type))
				rfc = JCO.TYPE_XSTRING; // 30
			else if ("TIME".equals(type))
				rfc = JCO.TYPE_TIME; // 3
			else if ("INT".equals(type))
				rfc = JCO.TYPE_INT; // 8
		}

		return rfc;
	}
}
