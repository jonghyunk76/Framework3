package kr.yni.frame.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.yni.frame.Constants;
import kr.yni.frame.collection.DataMap;
import kr.yni.frame.collection.DBBasedHashMap;
import kr.yni.frame.web.upload.FormFile;

/**
 * DataMap에서 key에 해당하는 값을 찾기 위해 제공되는 클래스
 * 
 * @author YNI-maker
 *
 */
public class DataMapHelper {
	
	private static Log log = LogFactory.getLog(DataMapHelper.class);
	
	public final static int LIMIT = 10;

	public final static int START = 0;

	public final static int MAX_RECORD = 10000;
	
	/**
	 * DataMap에서 key에 해당하는 값을 찾아 Number 타입으로 리턴한다.
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static int getInt(Map map, String key, int defaultValue) {
		int result = defaultValue;
	
		try {
			if(isKeyValue(map, key)) {
				if(map.get(key) instanceof String) {
					result = Integer.parseInt((String) map.get(key));
				} else if(map.get(key) instanceof Number) {
					result = ((Number) map.get(key)).intValue();
				}
			} else {
				result = defaultValue;
			}
			
			
		} catch (NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}
	
	/**
	 * DataMap에서 key해당하는 값을 찾아 BigDecimal 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static BigDecimal getBigDecimal(Map map, String key) {
		BigDecimal result = null;
		
		try {
			if(isKeyValue(map, key)) {
				if(map.get(key) instanceof String) {
					result = (BigDecimal) map.get(key);
				} else if(map.get(key) instanceof Number) {
					result = new BigDecimal(((Number) map.get(key)).longValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * DataMap에서 key에 해당하는 값을 찾아 Number 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static int getInt(Map map, String key) {
		return getInt(map, key, START);
	}
	
	/**
	 * DataMap에서 key에 해당하는 값을 찾아 Boolean 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean getBoolean(Map map, String key) {
		return getBoolean(map, key, false);
	}

	/**
	 * DataMap에서 key에 해당하는 값을 찾아 Number 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean getBoolean(Map map, String key, boolean defaultValue) {
		boolean result = defaultValue;
		
		try {
			if(isKeyValue(map, key)) {
				result = Boolean.parseBoolean((String) map.get(key));
			} else {
				result = defaultValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * DataMap에서 key에 해당하는 값을 찾아 String 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getString(Map map, String key) {
		return getString(map, key, null);
	}
	
	/**
	 * DataMap에서 key에 해당하는 값을 찾아 String 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getString(Map map, String key, String defaultValue) {
		String result = null;
		
		try {
			if(isKeyValue(map, key)) {
				result = (String) map.get(key);
			} else {
				result = defaultValue;
			}
		} catch (Exception e) {
		}

		return result;

	}
	
	/**
	 * DataMap에서 key에 해당하는 값을 찾아 Long 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Long getLong(Map map, String key) {
		return getLong(map, key, null);
	}
	
	/**
	 * DataMap에서 key에 해당하는 값을 찾아 Long 타입으로 리턴한다.
	 * 
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Long getLong(Map map, String key, Long defaultValue) {
		Long result = null;
		
		try {
			if(isKeyValue(map, key)) {
				result = (Long) map.get(key);
			} else {
				result  = defaultValue;
			}
		} catch (Exception e) {
		}

		return result;

	}
	
	/**
	 * DataMap객체에서 Map를 구한다.
	 * 
	 * @param dataMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map getMap(DataMap dataMap) {
		return getMap(dataMap, true);
	}
	
	/**
	 * MultipartRequest에서 "dataMap"에 해당하는 Map를 구한다.
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map getMap(MultipartHttpServletRequest request) {
		return getMap((DataMap) request.getAttribute(Constants.DATA_MAP), true);
	}
	
	/**
	 * 사용자 세션 정보에 요청한 key에 해당하는 값을 포함하고 있는지 여부 리턴
	 * @param dataMap
	 * @param key
	 * @return
	 */
	private static boolean isContainsSession(DataMap dataMap, String key) {
		boolean checker = true;
		
		Map sessionMap = SessionUtil.getUserInfo(dataMap);
		
		if(sessionMap == null) {
			checker = false;
		} else {
			if(!sessionMap.containsKey(key)) {
				checker = false;
			} else {
				if(StringHelper.isNull((String) dataMap.get(key))) {
					checker = true;
				} else {
					checker = false;
				}
			}
		}
		
		return checker;
	}
	
	/**
	 * default로 생성할 세션정보를 DataMap에 추가한다. 
	 * - S_CERTIFY_TYPE : 인증타입(internal or external)
	 * - S_USER_ID : 협력사 로그인 아이디
	 * 
	 * @param dataMap
	 * @param insertWhoInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map getMap(DataMap dataMap, boolean insertWhoInfo) {
		
		if(dataMap != null && insertWhoInfo) {
			// 파일 크기 설정
			if(isContainsSession(dataMap, "FILE_MAX_UPLOAD_SIZE")) dataMap.put("FILE_MAX_UPLOAD_SIZE", Constants.FILE_MAX_UPLOAD_SIZE);
			if(isContainsSession(dataMap, "FILE_MAX_MEMORY_SIZE")) dataMap.put("FILE_MAX_MEMORY_SIZE", Constants.FILE_MAX_MEMORY_SIZE);
			
			// 환경설정(시스템)
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_01)) dataMap.put(Constants.KEY_SYS_CONFIG_01, SessionUtil.getSystemConfig01(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_02)) dataMap.put(Constants.KEY_SYS_CONFIG_02, SessionUtil.getSystemConfig02(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_03)) dataMap.put(Constants.KEY_SYS_CONFIG_03, SessionUtil.getSystemConfig03(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_04)) dataMap.put(Constants.KEY_SYS_CONFIG_04, SessionUtil.getSystemConfig04(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_05)) dataMap.put(Constants.KEY_SYS_CONFIG_05, SessionUtil.getSystemConfig05(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_06)) dataMap.put(Constants.KEY_SYS_CONFIG_06, SessionUtil.getSystemConfig06(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_07)) dataMap.put(Constants.KEY_SYS_CONFIG_07, SessionUtil.getSystemConfig07(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_08)) dataMap.put(Constants.KEY_SYS_CONFIG_08, SessionUtil.getSystemConfig08(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_09)) dataMap.put(Constants.KEY_SYS_CONFIG_09, SessionUtil.getSystemConfig09(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_SYS_CONFIG_10)) dataMap.put(Constants.KEY_SYS_CONFIG_10, SessionUtil.getSystemConfig10(dataMap));
			// 환경설정(어플리케이션)
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_01)) dataMap.put(Constants.KEY_APP_CONFIG_01, SessionUtil.getApplicationConfig01(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_02)) dataMap.put(Constants.KEY_APP_CONFIG_02, SessionUtil.getApplicationConfig02(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_03)) dataMap.put(Constants.KEY_APP_CONFIG_03, SessionUtil.getApplicationConfig03(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_04)) dataMap.put(Constants.KEY_APP_CONFIG_04, SessionUtil.getApplicationConfig04(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_05)) dataMap.put(Constants.KEY_APP_CONFIG_05, SessionUtil.getApplicationConfig05(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_06)) dataMap.put(Constants.KEY_APP_CONFIG_06, SessionUtil.getApplicationConfig06(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_07)) dataMap.put(Constants.KEY_APP_CONFIG_07, SessionUtil.getApplicationConfig07(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_08)) dataMap.put(Constants.KEY_APP_CONFIG_08, SessionUtil.getApplicationConfig08(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_09)) dataMap.put(Constants.KEY_APP_CONFIG_09, SessionUtil.getApplicationConfig09(dataMap));
			if(isContainsSession(dataMap, Constants.KEY_APP_CONFIG_10)) dataMap.put(Constants.KEY_APP_CONFIG_10, SessionUtil.getApplicationConfig10(dataMap));
			
			// 인증타입(internal or external)
			if(isContainsSession(dataMap, Constants.KEY_S_CERTIFY_TYPE)) {
				dataMap.put(Constants.KEY_S_CERTIFY_TYPE, SessionUtil.getSessionCertifyType(dataMap));
			}
			// 협력사 사용자 인증정보
			if(isContainsSession(dataMap, Constants.KEY_S_USER_ID)) {
				dataMap.put(Constants.KEY_S_USER_ID, SessionUtil.getSessionUserId(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_S_USER_NAME)) {
				dataMap.put(Constants.KEY_S_USER_NAME, SessionUtil.getSessionUserName(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_S_COMPANY_CD)) {
				dataMap.put(Constants.KEY_S_COMPANY_CD, SessionUtil.getSessionCompanyCode(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_SP_COMPANY_CD)) {
				dataMap.put(Constants.KEY_SP_COMPANY_CD, SessionUtil.getSessionRepresentCompanyCode(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_S_VENDOR_CD)) {
				dataMap.put(Constants.KEY_S_VENDOR_CD, SessionUtil.getSessionVendorCode(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_S_VENDOR_NAME)) {
				dataMap.put(Constants.KEY_S_VENDOR_NAME, SessionUtil.getSessionVendorName(dataMap));
			}
			// 금액단위에 화폐별 소수점 표시 숫자
			if(isContainsSession(dataMap, Constants.KEY_AMOUNT_POINT_NM)) {
				dataMap.put(Constants.KEY_AMOUNT_POINT_NM, SessionUtil.getSessionAmountPointNm(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_S_DEFAULT_LANGUAGE)) {
				dataMap.put(Constants.KEY_S_DEFAULT_LANGUAGE, SessionUtil.getSessionDafaultLanguage(dataMap));
			}
			// CREATE_BY 를 직접 맵핑
			if(isContainsSession(dataMap, Constants.KEY_CREATE_BY)) {
				dataMap.put(Constants.KEY_CREATE_BY, SessionUtil.getCreateBy(dataMap));
			}
			// UPDATE_BY 를 직접 맵핑
			if(isContainsSession(dataMap, Constants.KEY_UPDATE_BY)) {
				dataMap.put(Constants.KEY_UPDATE_BY, SessionUtil.getUpdateBy(dataMap));
			}
			// 서버 주소를 직접 맵핑
			if(isContainsSession(dataMap, Constants.KEY_SERVER_ADDRESS)) {
				dataMap.put(Constants.KEY_SERVER_ADDRESS, SessionUtil.getServerAddress(dataMap));
			}
			// 서버 포트를 직접 맵핑
			if(isContainsSession(dataMap, Constants.KEY_SERVER_PORT)) {
				dataMap.put(Constants.KEY_SERVER_PORT, SessionUtil.getServerPort(dataMap));
			}
			// COMPANY_CD로 변경
			if(isContainsSession(dataMap, Constants.KEY_COMPANY_CD)) {
				dataMap.put(Constants.KEY_COMPANY_CD, SessionUtil.getCompanyCode(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_PARENT_COMPANY_CD)) {
				dataMap.put(Constants.KEY_PARENT_COMPANY_CD, SessionUtil.getParentCompanyCode(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_RUN_TYPE)) {
				dataMap.put(Constants.KEY_AUTH_GROUP, SessionUtil.getRunType(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_AUTH_GROUP)) {
				dataMap.put(Constants.KEY_RUN_TYPE, SessionUtil.getAuthGroup(dataMap));
			}
			if(isContainsSession(dataMap, Constants.KEY_BUSINESS_NO)) {
				dataMap.put(Constants.KEY_BUSINESS_NO, SessionUtil.getBusinessNo(dataMap));
			}
			// OK
			if(isContainsSession(dataMap, Constants.KEY_DEFAULT_LANGUAGE)) {
				dataMap.put(Constants.KEY_DEFAULT_LANGUAGE, SessionUtil.getDefaultLanguage(dataMap));
			}
			// USER_ID
			if(isContainsSession(dataMap, Constants.KEY_USER_ID)) {
				dataMap.put(Constants.KEY_USER_ID, SessionUtil.getUserId(dataMap));
			}
			// OK
			if(isContainsSession(dataMap, Constants.KEY_USER_NAME)) {
				dataMap.put(Constants.KEY_USER_NAME, SessionUtil.getUserName(dataMap));
			}
			// OK
			if(isContainsSession(dataMap, Constants.KEY_COMPANY_NAME)) {
				dataMap.put(Constants.KEY_COMPANY_NAME, SessionUtil.getCompanyName(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_WORK_YYYY_MM)) {
				dataMap.put(Constants.KEY_WORK_YYYY_MM, SessionUtil.getWorkYyyyMn(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_WORK_INVESTIGATE_ID)) {
				dataMap.put(Constants.KEY_WORK_INVESTIGATE_ID, SessionUtil.getWorkInvestigateId(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SE_DIVISION_CD)) {
				dataMap.put(Constants.KEY_SE_DIVISION_CD, SessionUtil.getDivisionCode(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SE_PRODUCT_ASSETS_TYPE)) {
				dataMap.put(Constants.KEY_SE_PRODUCT_ASSETS_TYPE, SessionUtil.getProductAssetsType(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SE_PART_ASSETS_TYPE)) {
				dataMap.put(Constants.KEY_SE_PART_ASSETS_TYPE, SessionUtil.getPartAssetsType(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_FTA_NATION)) {
				dataMap.put(Constants.KEY_FTA_NATION, SessionUtil.getFtaNation(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_LOCAL_CURRENCY)) {
				dataMap.put(Constants.KEY_LOCAL_CURRENCY, SessionUtil.getLocalCurrency(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_EXCHANGE_CURRENCY)) {
				dataMap.put(Constants.KEY_EXCHANGE_CURRENCY, SessionUtil.getExchangeCurrency(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_EXCHANGE_CURRENCY_TYPE)) {
				dataMap.put(Constants.KEY_EXCHANGE_CURRENCY_TYPE, SessionUtil.getExchangeCurrencyType(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_VENDOR_CO_TARGET)) {
				dataMap.put(Constants.KEY_SESSION_VENDOR_CO_TARGET, SessionUtil.getVendorCoTarget(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_CRTCT_GOOD_YN)) {
				dataMap.put(Constants.KEY_SESSION_CRTCT_GOOD_YN, SessionUtil.getCrtctGoodYn(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_CRTCT_IMP_YN)) {
				dataMap.put(Constants.KEY_SESSION_CRTCT_IMP_YN, SessionUtil.getCrtctImpYn(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_CUSTOMER_CO_TARGET)) {
				dataMap.put(Constants.KEY_SESSION_CUSTOMER_CO_TARGET, SessionUtil.getCustomerCoTarget(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_CRTCT_SUBITEM_YN)) {
				dataMap.put(Constants.KEY_SESSION_CRTCT_SUBITEM_YN, SessionUtil.getCrtctSubitemYn(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_CRTCT_EXPORT_YN)) {
				dataMap.put(Constants.KEY_SESSION_CRTCT_EXPORT_YN, SessionUtil.getCrtctExportYn(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_EXCEL_MAX_ROWNUM)) {
				dataMap.put(Constants.KEY_SESSION_EXCEL_MAX_ROWNUM, SessionUtil.getExcelMaxRownum(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SESSION_CSV_MAX_ROWNUM)) {
				dataMap.put(Constants.KEY_SESSION_CSV_MAX_ROWNUM, SessionUtil.getCsvMaxRownum(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_TOMS_FTA_CERT_KEY)) {
				dataMap.put(Constants.KEY_TOMS_FTA_CERT_KEY, SessionUtil.getTomsftaCertKey(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_TOMS_CLOUD_SITE)) {
				dataMap.put(Constants.KEY_TOMS_CLOUD_SITE, SessionUtil.getTomsCloudSite(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_MANAGER_YN)) {
				dataMap.put(Constants.KEY_MANAGER_YN, SessionUtil.getMangerYn(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_EXCHANGE_RATE)) {
				dataMap.put(Constants.KEY_EXCHANGE_RATE, SessionUtil.getExchangeRate(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_FAMILY_CODE)) {
				dataMap.put(Constants.KEY_FAMILY_CODE, SessionUtil.getFamilyCode(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_KAKAO_SCRIPT_KEY)) {
				dataMap.put(Constants.KEY_KAKAO_SCRIPT_KEY, SessionUtil.getKakaoScriptKey(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_PU_NAME)) {
				dataMap.put(Constants.KEY_PU_NAME, SessionUtil.getPuName(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_PU_CODE)) {
				dataMap.put(Constants.KEY_PU_CODE, SessionUtil.getPuCode(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_SE_DIVISION_CD)) {
				dataMap.put(Constants.KEY_SE_DIVISION_CD, SessionUtil.getSeDivisionCode(dataMap));
			}
			
			// 권한설정
			if(isContainsSession(dataMap, Constants.KEY_SEL_AUTH)) {
				dataMap.put(Constants.KEY_SEL_AUTH, SessionUtil.getSelAuth(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_REG_AUTH)) {
				dataMap.put(Constants.KEY_REG_AUTH, SessionUtil.getRegAuth(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_UPD_AUTH)) {
				dataMap.put(Constants.KEY_UPD_AUTH, SessionUtil.getUpdAuth(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_DEL_AUTH)) {
				dataMap.put(Constants.KEY_DEL_AUTH, SessionUtil.getDelAuth(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_EXC_AUTH)) {
				dataMap.put(Constants.KEY_EXC_AUTH, SessionUtil.getExcAuth(dataMap));
			}
			
			if(isContainsSession(dataMap, Constants.KEY_FLE_AUTH)) {
				dataMap.put(Constants.KEY_FLE_AUTH, SessionUtil.getFleAuth(dataMap));
			}
		}
		
		return dataMap.getMap();
	}
	
	/**
	 * Map에 Key가 포함되 있는지 여부 체크
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isKeyValue(Map map, String key) {
		if(map == null) return false;
		else if(!map.containsKey(key)) return false;
		else if(map.get(key) == null) return false;
		else return true;
	}
	
	/**
	 * 일반 Map 데이터를 DataMap에 옴긴다. 
	 * @param dataMap
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private static DataMap joinWithMap(DataMap dataMap, Map map) {
		if(map != null && map.size() > 0) {
			Iterator it = map.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = StringHelper.null2void(entry.getKey());
				String value = "";
				
				if(key != null && !key.isEmpty()) {
					value = StringHelper.null2void(map.get(key));
				}
				
				dataMap.put(key, value);
			}
		}
		
		return dataMap;
	}
	
	/**
	 * Map형식의 데이터를 List로 변환
	 * @param map java.lang.Map
	 * @return java.lang.List
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map<String, Object>> changeListForMap(Map map) {
		List list  = new LinkedList();
		
		if(map != null && map.size() > 0) {
			Iterator it = map.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = StringHelper.null2void(entry.getKey());
				String value = "";
				
				if(key != null && !key.isEmpty()) {
					value = StringHelper.null2void(map.get(key));
					
					Map tempMap = new LinkedHashMap();
					tempMap.put(key, value);
					list.add(tempMap);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Map에서 FormFile를 찾아 List에 담아서 리턴한다.
	 * 
	 * @param map multipart에서 구한 FormFile객체를 포함하고 있는 map
	 * @return FormFile를 포함한 List
	 */
	@SuppressWarnings("rawtypes")
	public static List<FormFile> getFormFile(Map map) {
		List<FormFile> fileList = new LinkedList<FormFile>();
		
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Object obj = map.get(key);
			
			if(obj instanceof FormFile) {
				FormFile file = (FormFile) obj;
				
				fileList.add(file);
			} else if(obj instanceof List) {
				List files = (List) obj;
				
				for(int i = 0; i < files.size(); i++) {
					if(files.get(i) != null && files.get(i) instanceof FormFile) {
						FormFile file = (FormFile) files.get(i);
					
						fileList.add(file);
					}
				}
			}
		}
		
		return fileList;
	}
	
	/**
	 * Map에서 파일을 삭제함
	 * 
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public static void removeFormFile(Map map) throws CloneNotSupportedException, Exception {
		// 원본 Map data를 복사한다.(loop 실행 시 원본이 변경될 경우 ConcurrentModificationException이 발생하게 됨)
		Map tempMap = DataMapHelper.toCloneMap(map);
		
		for(Iterator it = tempMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			
			String key = (String) entry.getKey();
			Object obj = tempMap.get(key);
			
			if(obj instanceof FormFile) {
				if(log.isDebugEnabled()) log.debug("Removed files : name = " + key + ", value = " + obj);
				map.remove(key);
			} else if(obj instanceof LinkedList) {
				List files = (LinkedList) obj;
				
				if(0 < files.size()) {
					Object fileObj = files.get(0);
					
					if(fileObj instanceof FormFile) {
						if(log.isDebugEnabled()) log.debug("Removed files : name = " + key + ", value = " + fileObj);
						map.remove(key);
					}
				}
			}
		}
		
		tempMap.clear();
	}
	
	/**
	 * 파라메터의 Map과 동일한 새로운 Map데이터를 copy한다.
	 * - callByValue 처리
	 * @param oldMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map toCloneMap(Map oldMap) throws Exception {
		if(oldMap != null && oldMap.size() > 0) {
			return (Map) ((DBBasedHashMap) oldMap).clone();
		} else {
			return new LinkedHashMap();
		}
	}
	
	/**
	 * Map Key를 대문자로 변환
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Object toUpperCaseObject(Object obj) {
		if(!Constants.APPLICATION_DB_TYPE.equals("POSTGRESQL")) {
			return obj;
		} else {
			if(obj instanceof Map) {
				Map<String, Object> tm = (LinkedHashMap) obj;
				Iterator<String> iteratorKey = tm.keySet().iterator(); // 키값 오름차순
				Map newMap = new LinkedHashMap();
	
				while (iteratorKey.hasNext()) {
					String key = iteratorKey.next();
					newMap.put(key.toUpperCase(), tm.get(key));
				}
				
				return newMap;
			} else if(obj instanceof List) {
				List plist = (List) obj;
				List<Object> newList = new LinkedList<Object>();
	
				for (int i = 0; i < plist.size(); i++) {
					Object attr = plist.get(i);
					
					if(attr instanceof Map) {
						Map<String, Object> tm = (LinkedHashMap) attr;
						Iterator<String> iteratorKey = tm.keySet().iterator(); // 키값 오름차순
						Map newMap = new LinkedHashMap();
		
						while (iteratorKey.hasNext()) {
							String key = iteratorKey.next();
							newMap.put(key.toUpperCase(), tm.get(key));
						}
					
						newList.add(newMap);
					} else {
						newList.add(attr);
					}
				}
				
				return newList;
			} else {
				return obj;
			}
		}
	}
}
