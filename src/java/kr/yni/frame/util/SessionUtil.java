package kr.yni.frame.util;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import kr.yni.frame.Constants;
import kr.yni.frame.collection.DataMap;

public class SessionUtil {
	
	/**
	 * 세션 정보(user_info) 조회
	 * 
	 * @param session
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map getUserInfo(HttpSession session) {
		return session.getAttribute(Constants.SESSION_KEY) == null ? null : (LinkedHashMap) session.getAttribute(Constants.SESSION_KEY);
	}

	/**
	 * 세션의 사용자 정보 조회 (String value)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @param defaultValue
	 *            default value
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static String getString(HttpSession session, String key, String defaultValue) {
		Map member = getUserInfo(session);
		return (member != null) ? StringUtils.defaultIfEmpty((String) member.get(key), defaultValue) : "";
	}

	/**
	 * 세션의 사용자 정보 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getString(HttpSession session, String key) {
		return getString(session, key, null);
	}

	/**
	 * 세션의 사용자 Default lanuguage 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getDefaultLanguage(HttpSession session) {
		return getString(session, Constants.KEY_DEFAULT_LANGUAGE);
	}

	/**
	 * 세션의 사용자 company code 조회
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCompanyCode(HttpSession session) {
		return getString(session, Constants.KEY_COMPANY_CD);
	}
	
	/**
	 * 세션의 사용자 company code 조회
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getParentCompanyCode(HttpSession session) {
		return getString(session, Constants.KEY_PARENT_COMPANY_CD);
	}
	
	/**
	 * 세션의 운영상태 조회
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getRunType(HttpSession session) {
		return getString(session, Constants.KEY_RUN_TYPE);
	}
	
	/**
	 * 업체의 사업자 등록번호
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getBusinessno(HttpSession session) {
		return getString(session, Constants.KEY_BUSINESS_NO);
	}
	
	/**
	 * 사용자 권한 그룹 조회
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getAuthGroup(HttpSession session) {
		return getString(session, Constants.KEY_AUTH_GROUP);
	}
	
	/**
	 * 세션의 사용자 company code 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCompanyName(HttpSession session) {
		return getString(session, Constants.KEY_COMPANY_NAME);
	}

	/**
	 * 세션의 사용자 user id 조회
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getUserId(HttpSession session) {
		return getString(session, Constants.KEY_USER_ID);
	}

	/**
	 * 세션의 사용자 user name 조회
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getUserName(HttpSession session) {
		return getString(session, Constants.KEY_USER_NAME);
	}

	/**
	 * 세션의 사용자 pu code 조회
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getPuCode(HttpSession session) {
		return getString(session, Constants.KEY_PU_CODE);
	}

	/**
	 * 세션의 사용자 LOGO 파일 경로 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComLogoPath(HttpSession session) {
		return getString(session, Constants.KEY_COM_LOGO_PATH, Constants.KEY_COM_LOGO_DEFAULT_PATH);
	}

	/**
	 * 세션의 사용자 MAIN 파일 경로 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComMainPath(HttpSession session) {
		return getString(session, Constants.KEY_COM_MAIN_PATH, Constants.KEY_COM_MAIN_DEFAULT_PATH);
	}

	/**
	 * 세션의 사용자 LOGO 파일명 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComLogoFile(HttpSession session) {
		return getString(session, Constants.KEY_COM_LOGO_FILE, Constants.KEY_COM_LOGO_DEFAULT_FILE);
	}

	/**
	 * 세션의 사용자 MAIN 파일명 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComMainFile(HttpSession session) {
		return getString(session, Constants.KEY_COM_MAIN_FILE, Constants.KEY_COM_MAIN_DEFAULT_FILE);
	}

	/**
	 * 세션 정보(user_info) 조회
	 * 
	 * @param session
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map getUserInfo(DataMap dataMap) {
		return dataMap.getSession() == null ? null : (LinkedHashMap) dataMap.getSession();
	}

	/**
	 * 세션의 사용자 정보 조회 (String value)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @param defaultValue
	 *            default value
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static String getString(DataMap dataMap, String key, String defaultValue) {
		Map member = getUserInfo(dataMap);
		return (member != null) ? StringUtils.defaultIfEmpty((String) member.get(key), defaultValue) : "";
	}
	
	/**
	 * 세션의 사용자 정보 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getString(DataMap dataMap, String key) {
		return getString(dataMap, key, null);
	}

	/**
	 * 세션의 사용자 Default lanuguage 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getDefaultLanguage(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_DEFAULT_LANGUAGE);
	}
	
	// 시스템 환경설정을 위한 값 조회
	public static String getSystemConfig01(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_01); }
	public static String getSystemConfig02(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_02); }
	public static String getSystemConfig03(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_03); }
	public static String getSystemConfig04(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_04); }
	public static String getSystemConfig05(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_05); }
	public static String getSystemConfig06(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_06); }
	public static String getSystemConfig07(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_07); }
	public static String getSystemConfig08(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_08); }
	public static String getSystemConfig09(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_09); }
	public static String getSystemConfig10(DataMap dataMap) { return getString(dataMap, Constants.KEY_SYS_CONFIG_10); }
	// 어플리케이션 환경설정을 위한 값 조회
	public static String getApplicationConfig01(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_01); }
	public static String getApplicationConfig02(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_02); }
	public static String getApplicationConfig03(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_03); }
	public static String getApplicationConfig04(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_04); }
	public static String getApplicationConfig05(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_05); }
	public static String getApplicationConfig06(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_06); }
	public static String getApplicationConfig07(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_07); }
	public static String getApplicationConfig08(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_08); }
	public static String getApplicationConfig09(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_09); }
	public static String getApplicationConfig10(DataMap dataMap) { return getString(dataMap, Constants.KEY_APP_CONFIG_10); }
		
	/**
	 * 세션의 사용자 company code 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCompanyCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_COMPANY_CD);
	}
	
	/**
	 * 세션의 사용자 상위 company code 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getParentCompanyCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_PARENT_COMPANY_CD);
	}
	
	/**
	 * 세션의 운영상태 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getRunType(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_RUN_TYPE);
	}
	
	/**
	 * 업체의 사업자 등록번호
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getBusinessNo(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_BUSINESS_NO);
	}
	
	/**
	 * 권한 그룹 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getAuthGroup(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_AUTH_GROUP);
	}
	
	/**
	 * 세션의 사용자 company id 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCompanyId(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_COMPANY_ID);
	}

	/**
	 * 세션의 사용자 company 명 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCompanyName(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_COMPANY_NAME);
	}
	
	/**
	 * 세션의 사용자 company 그룹 id 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getParentCompanyId(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_PARENT_COMPANY_CD);
	}
	
	/**
	 * 세션의 사용자 user id 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getUserId(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_USER_ID);
	}
	
	/**
	 * 세션의 사용자 user info id 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getUserInfoId(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_USER_INFO_ID);
	}
	
	/**
	 * 세션의 사용자 user name 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getUserName(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_USER_NAME);
	}

	/**
	 * 세션의 사용자 pu code 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getPuCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_PU_CODE);
	}
	
	/**
	 * 세션의 생성자 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCreateBy(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_CREATE_BY);
	}
	
	/**
	 * 세션의 수정자 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getUpdateBy(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_UPDATE_BY);
	}
	
	/**
	 * 서버 주소 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getServerAddress(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SERVER_ADDRESS);
	}
	
	/**
	 * 서버 주소 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getServerPort(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SERVER_PORT);
	}
	
	/**
	 * 세션의 사업부 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getDivisionCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SE_DIVISION_CD);
	}
	
	/**
	 * 세션의 자가생산품 자산구분 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getProductAssetsType(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SE_PRODUCT_ASSETS_TYPE);
	}
	
	/**
	 * 세션의 투입자재 자산구분 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getPartAssetsType(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SE_PART_ASSETS_TYPE);
	}
	
	/**
	 * FTA 실행 국가 코드
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getFtaNation(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_FTA_NATION);
	}
	
	/**
	 * 회사통화
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getLocalCurrency(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_LOCAL_CURRENCY);
	}
	
	/**
	 * 환산통화
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getExchangeCurrency(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_EXCHANGE_CURRENCY);
	}
	
	/**
	 * 환율적용타입
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getExchangeCurrencyType(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_EXCHANGE_CURRENCY_TYPE);
	}
	
	/**
	 * 환율
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getExchangeRate(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_EXCHANGE_RATE);
	}
	
	/**
	 * 협력사 확인서 수취대상
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getVendorCoTarget(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_VENDOR_CO_TARGET);
	}
	/////////////////////////////////////
	/**
	 * 협력사 확인서 수취대상 상품 포함여부
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCrtctGoodYn(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_CRTCT_GOOD_YN);
	}
	
	/**
	 * 협력사 확인서 수취대상 수입 포함여부
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCrtctImpYn(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_CRTCT_IMP_YN);
	}
	
	/**
	 * 고객사 중점관리 대상
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCustomerCoTarget(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_CUSTOMER_CO_TARGET);
	}
	
	/**
	 * 고객사 중점관리 대상 유상사급 포함여부
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCrtctSubitemYn(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_CRTCT_SUBITEM_YN);
	}
	
	/**
	 * 고객사 중점관리 대상 수출 포함여부
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCrtctExportYn(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_CRTCT_EXPORT_YN);
	}
	
	/**
	 * 엑셀파일 최대 표시 ROW수(10만건 이상인 경우에는 CSV파일로 전환)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getExcelMaxRownum(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_EXCEL_MAX_ROWNUM);
	}
	
	/**
	 * CSV파일 최대 표시 ROW수(초과시 에러발생)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCsvMaxRownum(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SESSION_CSV_MAX_ROWNUM);
	}
	
	/**
	 * TOMS라이센스 정보
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getTomsftaCertKey(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_TOMS_FTA_CERT_KEY);
	}
	
	/**
	 * TOMS 클라우드 URL
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getTomsCloudSite(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_TOMS_CLOUD_SITE);
	}
	
	/**
	 * 관리자 여부 정보
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getMangerYn(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_MANAGER_YN);
	}
	
	/////////////////////////
	/**
	 * 세션의 제품판매그룹 조회(LGD)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getFamilyCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_FAMILY_CODE);
	}
	
	/**
	 * 세션의 제품판매그룹 조회(LGD)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getFamilyCode(HttpSession session) {
		return getString(session, Constants.KEY_FAMILY_CODE);
	}
	
	/**
	 * 카카오 스크립트 키
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getKakaoScriptKey(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_KAKAO_SCRIPT_KEY);
	}
	
	/**
	 * 카카오 스크립트 키
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getKakaoScriptKey(HttpSession session) {
		return getString(session, Constants.KEY_KAKAO_SCRIPT_KEY);
	}
	
	/**
	 * Product Unit 명칭
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getPuName(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_PU_NAME);
	}
	
	/**
	 * 서브 사업부 코드
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getSeDivisionCode(HttpSession session) {
		return getString(session, Constants.KEY_SE_DIVISION_CD);
	}
	
	/**
	 * 서브 사업부 코드
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getSeDivisionCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SE_DIVISION_CD);
	}
	
	/**
	 * Product Unit 명칭
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getPuName(HttpSession session) {
		return getString(session, Constants.KEY_PU_NAME);
	}
	
	/**
	 * 인증구분 조회
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionCertifyType(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_S_CERTIFY_TYPE);
	}
	
	/**
	 * 사용자ID 조회
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionUserId(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_S_USER_ID);
	}
	
	/**
	 * 사용자명 조회
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionUserName(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_S_USER_NAME);
	}
	
	/**
	 *  대표 고객사 코드
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionCompanyCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_S_COMPANY_CD);
	}
	
	/**
	 * 고객사코드(V_USER_INFO.COMPANY_CD적용, 단, NULL값이 허용 가능함)
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionRepresentCompanyCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SP_COMPANY_CD);
	}
	
	/**
	 * 협력사코드 조회
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionVendorCode(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_S_VENDOR_CD);
	}
	
	/**
	 * 협력사명 조회
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionVendorName(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_S_VENDOR_NAME);
	}
	
	/**
	 * 금액단위에 화폐별 소수점 표시 숫자 조회
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionAmountPointNm(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_AMOUNT_POINT_NM);
	}
	
	/**
	 * 인증구분 조회
	 * 
	 * @param dataMap 인증관련 session정보
	 * @return String
	 */
	public static String getSessionDafaultLanguage(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_S_DEFAULT_LANGUAGE);
	}
	
	/**
	 * 세션의 사용자 LOGO 파일 경로 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComLogoPath(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_COM_LOGO_PATH, Constants.KEY_COM_LOGO_DEFAULT_PATH);
	}

	/**
	 * 세션의 사용자 MAIN 파일 경로 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComMainPath(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_COM_MAIN_PATH, Constants.KEY_COM_MAIN_DEFAULT_PATH);
	}

	/**
	 * 세션의 사용자 LOGO 파일명 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComLogoFile(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_COM_LOGO_FILE, Constants.KEY_COM_LOGO_DEFAULT_FILE);
	}

	/**
	 * 세션의 사용자 MAIN 파일명 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getComMainFile(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_COM_MAIN_FILE, Constants.KEY_COM_MAIN_DEFAULT_FILE);
	}

	/**
	 * 세션 정보(user_info) 조회 to_string
	 * 
	 * @param session
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(HttpSession session) {
		return toString((LinkedHashMap) getUserInfo(session));
	}

	/**
	 * 세션 정보(user_info) 조회 to_string
	 * 
	 * @param session
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(DataMap dataMap) {
		return toString((LinkedHashMap) getUserInfo(dataMap));
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(조회)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getSelAuth(HttpSession session) {
		return getString(session, Constants.KEY_SEL_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(조회)
	 * 
	 * @param session
	 * @return String
	 */
	public static String getSelAuth(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_SEL_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(등록)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getRegAuth(HttpSession session) {
		return getString(session, Constants.KEY_REG_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(등록)
	 * 
	 * @param session
	 * @return String
	 */
	public static String getRegAuth(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_REG_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(수정)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getUpdAuth(HttpSession session) {
		return getString(session, Constants.KEY_UPD_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(수정)
	 * 
	 * @param session
	 * @return String
	 */
	public static String getUpdAuth(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_UPD_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(삭제)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getDelAuth(HttpSession session) {
		return getString(session, Constants.KEY_DEL_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(삭제)
	 * 
	 * @param session
	 * @return String
	 */
	public static String getDelAuth(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_DEL_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(실행)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getExcAuth(HttpSession session) {
		return getString(session, Constants.KEY_EXC_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(실행)
	 * 
	 * @param session
	 * @return String
	 */
	public static String getExcAuth(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_EXC_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(파일)
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getFleAuth(HttpSession session) {
		return getString(session, Constants.KEY_FLE_AUTH);
	}
	
	/**
	 * 세션의 화면별 기능 권한 조회(파일)
	 * 
	 * @param session
	 * @return String
	 */
	public static String getFleAuth(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_FLE_AUTH);
	}
	
	/**
	 * 세션의 현재 선택된 메뉴 URL
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getCurrentMenuLinkUrl(HttpSession session) {
		return getString(session, Constants.KEY_CURRENT_MENU_LINK_URL);
	}

	/**
	 * 세션의 login시 등록된 log seq
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getLogSeq(HttpSession session) {
		return getString(session, Constants.KEY_LOG_SEQ);
	}

	/**
	 * 세션의 사용자 WorkYyyyMn 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getWorkYyyyMn(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_WORK_YYYY_MM);
	}
	
	/**
	 * 세션의 사용자 WorkYyyyMn 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getWorkYyyyMn(HttpSession session) {
		String workYyyyMm = "";
		if(getString(session, Constants.KEY_WORK_YYYY_MM) == null || getString(session, Constants.KEY_WORK_YYYY_MM).equals("")) {
			java.util.Date date = new java.util.Date();
			SimpleDateFormat sDate = new SimpleDateFormat("yyyyMM");
			workYyyyMm = sDate.format(date);
		} else {
			workYyyyMm = getString(session, Constants.KEY_WORK_YYYY_MM);
		}
		return workYyyyMm;
	}
	
	/**
	 * 세션의 작업 조사번호 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getWorkInvestigateId(DataMap dataMap) {
		return getString(dataMap, Constants.KEY_WORK_INVESTIGATE_ID);
	}
	
	/**
	 * 세션의 작업 조사번호 조회
	 * 
	 * @param session
	 * @param key
	 *            Session Key
	 * @return String
	 */
	public static String getWorkInvestigateId(HttpSession session) {
		return getString(session, Constants.KEY_WORK_INVESTIGATE_ID);
	}
	
	/**
	 * 세션 정보(user_info) 조회 to_string
	 * 
	 * @param session
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Map userInfo) {
		if (userInfo == null)
			return null;

		StringBuffer userInfoStr = new StringBuffer();
		Set set = userInfo.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			userInfoStr.append("[" + entry.getKey() + "=" + entry.getValue() + "]\n");
		}
		return userInfoStr.toString();
	}
}
