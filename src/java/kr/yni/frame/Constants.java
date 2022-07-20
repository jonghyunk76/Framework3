package kr.yni.frame;

import java.net.InetAddress;

import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorFactory;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.util.SystemHelper;

/**
 * 
 * 
 * @author YNI-maker
 * @since 2013. 5. 6. 오전 11:53:30
 * @version 1.0
 *
 * @see
 * YNI-maker 2013. 5. 6. Initial version
 *
 */
public class Constants {
	
	/**
     * <p/>
     * The name of Framework Package.
     * </p>
     */
    public static final String Package = "kr.yni.frame";
    
    /**
     * <p/>
     * 서버 정보
     * </p>
     */
    public static String SERVER_HOST_ADDRESS; // 네트워크 서버 IP
    public static String SERVER_HOST_NAME; // 네트워크 서버명
    
    /**
     * 메시지 코드 변경할지 여부 설정(default : true)
     */
    public static boolean APPLICATION_CHANGE_MSGCODE = true;
    
    /**
     * properties 파일의 prefix 명 
     */
    public static final String PROPERTIES_PREFIX_NAME = "_resource";
    
    public static String SERVLET_CONTEXT_PATH;
    public static String APPLICATION_REAL_PATH;
    public static String APPLICATION_CONTEXT_CHARSET;
    public static String APPLICATION_FILE_ENCODING;
    public static String APPLICATION_LEVEL;
    public static String APPLICATION_TIME_NATION;
    public static String APPLICATION_SERVICE_TYPE;
    public static String APPLICATION_DB_TYPE;
    public static String APPLICATION_SYSTEM_ID; // 시스템 ID(FTA:IF, 통관:CC,  중계서버:RS)
    public static String APPLICATION_BATCH_ID; // 배치처리 시 등록자(수정자) ID
    
    /**
     * <p/>
     * define Configuration path key
     * default : com.kpmg.kr.config.path
     * </p>
     */
    public static final String CONFIGURATION_FILE_PATH_KEY = "Frame.config.path";
    
    public static float GARBAGE_COLLECTION_LIMIT_RATE = 99;
    public static final Integer NULL = 0;

	/**
	 * request에 REQUEST 정보를 담고 있는 MAP 객체를 담는 이름.
	 * dataMap은 Bean으로 적용되므로 Controller 클래스에서 받을 때 dataMap 파라메터를 지정한다. 
	 * <font color=red> 
	 * 이것이 바뀌면 jsp/include/common.jsp에서 &lt;jsp:useBean id="dataMap"부분이 바뀌어야 하므로..주의
	 * </font>
	 */
	public static final String DATA_MAP = "dataMap"; // 변경하지 말 것
	
	/**
	 * 식별 구분자
	 */
	public static final String MUILT_SEARCH_DELIM = ",";
	
	/**
	 * 로그인 제외 URL 필터링 목록
	 */
	public static final String[] URL_FILTER_LIST = {"/main", "/login", "/ServerDigitalSignature", "/logout", "/sessionBlank", "/checkSession"};
	
	/**
	 * 기본 언어 설정
	 */
	public static String DEFAULT_LANGUAGE = "KR";
	
	/**
	 * WebAction 변수 선언
	 */
	public static final String MAIN_VIEW = "/MAIN/index";

	public static final String DUMMY = "DUMMY";
	public static final String ERROR_YN = "ERROR_YN";
	public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
	
	/**
	 * 배치 수행을 위한 클래스의 prefix경로
	 */
	public static final String PREFIX_CLASS_PACKAGE = "com.yni.fta.common.batch";
	
	/**
	 * 테스트 사용자 정보
	 */
	public static String TEST_USER_EAMIL;
	public static String TEST_USER_SMS;
	
	/**
	 * Session Util 변수선언
	 */
	// 세션Key
	public static final String SESSION_KEY = "_MEMBER";
	
	// 세션정보(내부사용자)
	public static final String KEY_COMPANY_ID = "SESSION_COMPANY_ID";
	public static final String KEY_COMPANY_CD = "SESSION_COMPANY_CD";
	public static final String KEY_COMPANY_NAME = "SESSION_COMPANY_NAME";
	public static final String KEY_DEFAULT_LANGUAGE = "SESSION_DEFAULT_LANGUAGE";
	public static final String KEY_USER_ID = "SESSION_USER_ID";
	public static final String KEY_USER_INFO_ID = "SESSION_USER_INFO_ID";
	public static final String KEY_USER_NAME = "SESSION_USER_NAME";
	public static final String KEY_DEPT_NAME = "SESSION_DEPT_NAME";
	public static final String KEY_PU_NAME = "SESSION_PU_NAME";
	public static final String KEY_PU_CODE = "SESSION_PU_CODE";
	public static final String KEY_FAMILY_CODE = "SESSION_FAMILY_CODE"; // LGD추가
	public static final String KEY_KAKAO_SCRIPT_KEY = "SESSION_KAKAO_SCRIPT_KEY"; // 카카오 script key(2021-05-19)
	
	// 세션정보(외부 협력사 사용자)
	public static final String KEY_S_USER_ID = "SESSION_USER_ID";
	public static final String KEY_S_USER_NAME = "SESSION_USER_NAME";
	public static final String KEY_S_COMPANY_CD = "SESSION_COMPANY_CD"; // 고객사코드(V_USER_INFO.COMPANY_CODE적용, 단, NULL값이 허용 가능함)
	public static final String KEY_SP_COMPANY_CD = "SESSION_SP_COMPANY_CODE"; // 대표 고객사 코드
	public static final String KEY_S_VENDOR_CD = "SESSION_VENDOR_CD";
	public static final String KEY_S_VENDOR_NAME = "SESSION_VENDOR_NAME";
	public static final String KEY_S_DEFAULT_LANGUAGE = "SESSION_DEFAULT_LANGUAGE";
	
	// 세션정보(공통)
	public static final String KEY_S_CERTIFY_TYPE = "SESSION_CERTIFY_TYPE";
	public static final String KEY_CREATE_BY = "CREATE_BY";
	public static final String KEY_UPDATE_BY = "UPDATE_BY";
	public static final String KEY_SERVER_ADDRESS = "SERVER_IP";
	public static final String KEY_SERVER_PORT = "SERVER_PORT";
	public static final String KEY_PARENT_COMPANY_CD = "SESSION_PARENT_COMPANY_CD";
	public static final String KEY_RUN_TYPE = "SESSION_RUN_TYPE";
	public static final String KEY_AUTH_GROUP = "SESSION_AUTH_GROUP";
	public static final String KEY_BUSINESS_NO = "SESSION_BUSINESS_NO";
	public static final String KEY_TOMS_FTA_CERT_KEY = "SESSION_TOMS_FTA_CERT_KEY";
	public static final String KEY_TOMS_CLOUD_SITE = "SESSION_TOMS_CLOUD_SITE";
	public static final String KEY_AMOUNT_POINT_NM = "SESSION_AMOUNT_POINT_NM"; // 금액단위에 화폐별 소수점 표시 숫자(2020.04.27)
	public static final String KEY_MANAGER_YN = "SESSION_MANAGER_YN"; // 관리자 여부
	
	// 회사정보
	public static final String KEY_COM_LOGO_PATH = "COM_LOGO_PATH";
	public static final String KEY_COM_LOGO_FILE = "COM_LOGO_FILE";
	public static final String KEY_COM_MAIN_PATH = "COM_MAIN_PATH";
	public static final String KEY_COM_MAIN_FILE = "COM_MAIN_FILE";
	
	// 메인구성정보
	public static final String KEY_COM_MAIN_DEFAULT_PATH = "/images/origin/main/";
	public static final String KEY_COM_MAIN_DEFAULT_FILE = "img_main.gif";
	public static final String KEY_COM_LOGO_DEFAULT_PATH = "/images/origin/common/";
	public static final String KEY_COM_LOGO_DEFAULT_FILE = "logo.gif";
	
	// 사용권한정보
	public static final String KEY_SEL_AUTH = "SEL_AUTH";
	public static final String KEY_REG_AUTH = "REG_AUTH";
	public static final String KEY_UPD_AUTH = "UPD_AUTH";
	public static final String KEY_DEL_AUTH = "DEL_AUTH";
	public static final String KEY_EXC_AUTH = "EXC_AUTH";
	public static final String KEY_FLE_AUTH = "FLE_AUTH";
	
	// 업무 일반 세션정보
	public static final String KEY_CURRENT_MENU_LINK_URL = "CURRENT_MENU_LINK_URL";
	public static final String KEY_LOG_SEQ = "LOG_SEQ";
	public static final String KEY_SSO_LOGIN = "SSO_LOGIN_FLAG";
	public static final String KEY_WORK_YYYY_MM = "SESSION_WORK_YYYY_MM";					 // 년이나 월마감으로 지정된 일자가 있는 경우 적용되는 설정변수
	public static final String KEY_WORK_INVESTIGATE_ID = "SESSION_WORK_INVESTIGATE_ID";      // 조사 기간(from~to)으로 일자가 적용된 설정변수
	public final static String KEY_SE_DIVISION_CD = "SESSION_DIVISION_CD";
	public static final String KEY_SE_PRODUCT_ASSETS_TYPE = "SESSION_PRODUCT_ASSETS_TYPE";
	public static final String KEY_SE_PART_ASSETS_TYPE = "SESSION_PART_ASSETS_TYPE";
	
	// 시스템 설정
	public static final String KEY_FTA_NATION = "SESSION_FTA_NATION";
	public static final String KEY_MAX_UPLOAD_SIZE = "SESSION_MAX_UPLOAD_SIZE";
	public static final String KEY_LOCAL_CURRENCY = "SESSION_LOCAL_CURRENCY";
	public static final String KEY_EXCHANGE_CURRENCY = "SESSION_EXCHANGE_CURRENCY";
	public static final String KEY_EXCHANGE_RATE = "SESSION_EXCHANGE_RATE";
	public static final String KEY_EXCHANGE_CURRENCY_TYPE = "SESSION_EXCHANGE_CURRENCY_TYPE";
	public static final String KEY_SESSION_VENDOR_CO_TARGET = "SESSION_VENDOR_CO_TARGET";
	
	public static final String KEY_SESSION_CRTCT_GOOD_YN = "SESSION_CRTCT_GOOD_YN";
	public static final String KEY_SESSION_CRTCT_IMP_YN = "SESSION_CRTCT_IMP_YN";
	public static final String KEY_SESSION_CUSTOMER_CO_TARGET = "SESSION_CUSTOMER_CO_TARGET";
	public static final String KEY_SESSION_CRTCT_SUBITEM_YN = "SESSION_CRTCT_SUBITEM_YN";
	public static final String KEY_SESSION_CRTCT_EXPORT_YN = "SESSION_CRTCT_EXPORT_YN";
	public static final String KEY_SESSION_EXCEL_MAX_ROWNUM = "SESSION_EXCEL_MAX_ROWNUM";
	public static final String KEY_SESSION_CSV_MAX_ROWNUM = "SESSION_CSV_MAX_ROWNUM";
	
	// 시스템 권한
	public static final String SYS_CONFIG_KEY = "_SYS_CONFIG";
	public static final String KEY_SYS_CONFIG_CODE = "SYS_CONF_CODE"; // 표준코드 적용범위(EC(Each Company):회사별로 적용, AC(All Company):모든 회사에 적용)
	public static final String KEY_SYS_CONFIG_AUTH = "SYS_CONF_AUTH"; // 권한 적용범위(EC(Each Company):회사별로 적용, AC(All Company):모든 회사에 적용)
	
	// 시스템 환경 설정
	public static final String KEY_SYS_CONFIG_01 = "SYS_CONFIG_01";
	public static final String KEY_SYS_CONFIG_02 = "SYS_CONFIG_02";
	public static final String KEY_SYS_CONFIG_03 = "SYS_CONFIG_03";
	public static final String KEY_SYS_CONFIG_04 = "SYS_CONFIG_04";
	public static final String KEY_SYS_CONFIG_05 = "SYS_CONFIG_05";
	public static final String KEY_SYS_CONFIG_06 = "SYS_CONFIG_06";
	public static final String KEY_SYS_CONFIG_07 = "SYS_CONFIG_07";
	public static final String KEY_SYS_CONFIG_08 = "SYS_CONFIG_08";
	public static final String KEY_SYS_CONFIG_09 = "SYS_CONFIG_09";
	public static final String KEY_SYS_CONFIG_10 = "SYS_CONFIG_10";
	// 어플리케이션 환경 설정
	public static final String KEY_APP_CONFIG_01 = "APP_CONFIG_01";
	public static final String KEY_APP_CONFIG_02 = "APP_CONFIG_02";
	public static final String KEY_APP_CONFIG_03 = "APP_CONFIG_03";
	public static final String KEY_APP_CONFIG_04 = "APP_CONFIG_04";
	public static final String KEY_APP_CONFIG_05 = "APP_CONFIG_05";
	public static final String KEY_APP_CONFIG_06 = "APP_CONFIG_06";
	public static final String KEY_APP_CONFIG_07 = "APP_CONFIG_07";
	public static final String KEY_APP_CONFIG_08 = "APP_CONFIG_08";
	public static final String KEY_APP_CONFIG_09 = "APP_CONFIG_09";
	public static final String KEY_APP_CONFIG_10 = "APP_CONFIG_10";
    
	// 메시지 변수
	public static final String COLUMN_MESSAGE_CODE = "MESSAGE_CODE";
	public static final String COLUMN_MESSAGE_LENG = "NATION_CODE";
	public static final String COLUMN_MESSAGE_NAME = "MESSAGE";
	
	// 엑셀 사이즈 관련 변수
	public static int EXCEL_MAX_ROWS = 10000;
	public static int EXCEL_SHEET_ROWS = 1000;
	
	// 데이터베이스 배치 크기
	public static int DB_BATCH_SIZE = 2000;
	
	// 파일 업로드 최대 크기
	public static long FILE_MAX_UPLOAD_SIZE = 5000000; // default(5MB) 
	// 파일 메모리 최대 저장용량
	public static int FILE_MAX_MEMORY_SIZE = 5000000; // default(5MB)
	
	static {
		configure();
	}
	
	public static void configure() {
		try {
			Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			
			APPLICATION_REAL_PATH = configurator.getString("application.path");
			APPLICATION_CONTEXT_CHARSET = StringHelper.null2string(configurator.getString("application.context.charset"), "utf-8");
			DEFAULT_LANGUAGE = StringHelper.null2string(configurator.getString("application.context.language"), DEFAULT_LANGUAGE);
			APPLICATION_FILE_ENCODING = StringHelper.null2void(configurator.getString("application.file.encoding"));
			APPLICATION_LEVEL = StringHelper.null2string(configurator.getString("application.level"), "O");
			APPLICATION_TIME_NATION = StringHelper.null2string(configurator.getString("application.time.nation"), "KR");
			APPLICATION_SERVICE_TYPE = StringHelper.null2string(configurator.getString("application.service.type"), "ON");
			APPLICATION_DB_TYPE = configurator.getString("application.db.type");
			APPLICATION_CHANGE_MSGCODE = StringHelper.null2boolean(configurator.getString("application.change.message"), true);
			APPLICATION_SYSTEM_ID = StringHelper.null2string(configurator.getString("application.system.id"), "IF"); // 시스템 ID(FTA:IF, 통관:CC,  중계서버:RS)
			APPLICATION_BATCH_ID = StringHelper.null2string(configurator.getString("batch.user.id"), "toms");
					
			String heapGcSize = StringHelper.null2void(configurator.getString("system.gc.rate"));
			String excelMaxRow = StringHelper.null2void(configurator.getString("biz.excel.max.rows"));
			String excelSheetRow = StringHelper.null2void(configurator.getString("biz.excel.sheet.rows"));
			String maxUploadSize = StringHelper.null2void(configurator.getString("file.max.upload.size"));
			String maxMemorySize = StringHelper.null2void(configurator.getString("file.max.memory.size"));
			
			if(!excelMaxRow.isEmpty()) EXCEL_MAX_ROWS =  Integer.parseInt(excelMaxRow);
			if(!excelSheetRow.isEmpty()) EXCEL_SHEET_ROWS = Integer.parseInt(excelSheetRow);
			if(!maxUploadSize.isEmpty()) FILE_MAX_UPLOAD_SIZE = Long.parseLong(maxUploadSize);
			if(!maxMemorySize.isEmpty()) FILE_MAX_MEMORY_SIZE = Integer.parseInt(maxMemorySize);
			if(!heapGcSize.isEmpty()) GARBAGE_COLLECTION_LIMIT_RATE = StringHelper.null2float(configurator.getString("system.gc.rate"));
			
			InetAddress inet = InetAddress.getLocalHost();
            
			SERVER_HOST_ADDRESS = inet.getHostAddress();
			SERVER_HOST_NAME = inet.getHostAddress();
			
			DB_BATCH_SIZE = StringHelper.null2zero(configurator.getString("db.batch.size"), DB_BATCH_SIZE);
			
			TEST_USER_EAMIL = StringHelper.null2void(configurator.getString("test.user.email"));
			TEST_USER_SMS = StringHelper.null2void(configurator.getString("test.user.sms"));
			
			if(!APPLICATION_FILE_ENCODING.isEmpty()) SystemHelper.setSystemProperty("file.encoding", APPLICATION_FILE_ENCODING);
		} catch(Exception exp) {
			exp.printStackTrace();
		}
	}
}