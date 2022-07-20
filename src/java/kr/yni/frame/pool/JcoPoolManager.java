package kr.yni.frame.pool;

import java.io.File;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorFactory;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.util.StringHelper;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;

/**
 * <p>
 * JCO ConnectionPool의 관리 클래스
 * </p>
 * <br>
 * config.jco_resouce.properties 파일를 필요로 한다.
 * 
 * @author 김종현
 * @since ver1.0
 */
public class JcoPoolManager {
	
	private static Log log = LogFactory.getLog(JcoPoolManager.class);
	
	private static StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	
	/**
	 * 최대 pool 갯수
	 */
	private static final String POOL_MAX_COUNT = "50";
	
	/**
	 * 최대 Connection 갯수
	 */
	private static final String CONNECTION_MAX_COUNT = "100";
	
	/**
	 * time out 시간(msecs)
	 */
	private static final String CONNECTION_TIME_OUT = "10000";
	
	/**
	 * 대기 시간(msecs)
	 */
	private static final String CONNECTION_WAIT_TIME = "30000";
	
	/**
	 * default language
	 */
	private static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage();

	/**
	 * function module의 META 정보 저장
	 */
	private static Hashtable<String, IRepository> repositoryTable = new Hashtable<String, IRepository>();

	/**
	 * Client SID
	 */
	private static String SID;

	/**
	 * 최대 조회 건수의 제한 값을 설정한다.
	 */
	private static int maxFetchLimit = Integer.MAX_VALUE;

	/**
	 * 
	 */
	private static String jcoMapPath = null;

	/**
	 * class initializer
	 */
	static {
		configure();
	}

	/**
	 * properties 파일에 설정된 ClientID을 구한다.
	 */
	private static void configure() {
		try {
			Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			
			jcoMapPath = configurator.getString("infc.map.path");
			String dbName = StringHelper.null2void(configurator.getString("infc.map.subfix.path"));
			
			if(dbName.isEmpty()) dbName = StringHelper.null2void(configurator.getString("application.db.type"));
			
			jcoMapPath += File.separator + dbName.toLowerCase();
			
			//if(!encryptor.isInitialized()) {  // 암호화 적용안할 경우 강제로 풀고 아래 라인은 삭제할 것
			if(false) { // 암호화 적용 안함
				encryptor.setPassword("1wax3rdv");
				SID = encryptor.decrypt(configurator.getString("use.client.name"));
			} else {
				SID = configurator.getString("use.client.name");
			}
			
			if (configurator.getString("max.fetch.limit") != null) {
				maxFetchLimit = configurator.getInt("max.fetch.limit");
			}
			if (log.isDebugEnabled()) {
				log.debug("paramPath=" + jcoMapPath);
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("exception: \n" + ex);
			}
		}
	}

	/**
	 * properties 파일에 설정된 기본 SID을 구한다.
	 * 
	 * @return ClientID
	 */
	public static String getDefaultSID() {
		return SID;
	}

	public static String getJcoMapPath() {
		return jcoMapPath;
	}

	/**
	 * properties 파일에 설정된 기본 maxFetchLimit을 구한다.
	 * 
	 * @return ClientID
	 */
	public static int getMaxFetchLimit() {
		return maxFetchLimit;
	}

	public static IRepository getRepository() throws Exception {
		return getRepository(SID);
	}

	public static void removeRepositoryTable(String sid) {
		repositoryTable.remove(sid);
	}

	public static synchronized IRepository getRepository(String sid)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("getRepositorySource() start .. sid = " + sid);
		}

		IRepository repository = repositoryTable.get(sid);
		if (repository != null) {
			if (log.isDebugEnabled()) {
				log.debug("IRepository (" + repositoryTable.size() + ") = " + repository);
			}
		} else {
			try {
				setClient(sid);
				repository = repositoryTable.get(sid);
			} catch (Exception exp) {
				throw new FrameException(exp.getMessage());
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("getRepository() end");
		}

		return repository;
	}

	@SuppressWarnings("unused")
	private static void setClient(String sid) throws Exception {
		try {
			Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			
			// pool 설정 정보
			int maxpool = configurator.getInt("jco.pool.max", POOL_MAX_COUNT);
			int maxconnection = configurator.getInt("jco.conection.max", CONNECTION_MAX_COUNT);
			long timeout = (long)configurator.getInt("jco.time.out", CONNECTION_TIME_OUT);
			long waittime = (long)configurator.getInt("jco.wait.time", CONNECTION_WAIT_TIME);
			
			// 연결 SAP서버 정보
			String client = configurator.getString(sid + ".client", "").trim();
			String userid = null;
			String password = null;
			String hostname = null;
			String language = null;
			String sysnr = null;
			
			//if(!encryptor.isInitialized()) {
			if(false) { // 암호화 적용 안함
				userid = encryptor.decrypt(configurator.getString(sid + ".userid", "")).trim();
				password = encryptor.decrypt(configurator.getString(sid + ".password", "")).trim();
				hostname = encryptor.decrypt(configurator.getString(sid + ".hostname", "")).trim();
				language = encryptor.decrypt(configurator.getString(sid + ".language", DEFAULT_LANGUAGE)).trim();
				sysnr = encryptor.decrypt(configurator.getString(sid + ".system.number", "")).trim();
			} else {
				userid = configurator.getString(sid + ".userid", "").trim();
				password = configurator.getString(sid + ".password", "").trim();
				hostname = configurator.getString(sid + ".hostname", "").trim();
				language = configurator.getString(sid + ".language", DEFAULT_LANGUAGE).trim();
				sysnr = configurator.getString(sid + ".system.number", "").trim();
			}
			
			if (log.isDebugEnabled()) {
				log.debug("jco_resource.properties > max connection=" + maxconnection);
				log.debug("jco_resource.properties > client=" + client);
				log.debug("jco_resource.properties > userid=" + userid);
				log.debug("jco_resource.properties > password=" + password);
				log.debug("jco_resource.properties > host=" + hostname);
				log.debug("jco_resource.properties > language=" + language);
				log.debug("jco_resource.properties > system number=" + sysnr);
			}

			jcoConnection(sid, maxconnection, client, userid, password,
					hostname, language, sysnr, maxpool, timeout, waittime);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * sid에 대한 connection pool을 추가하고 repository를 생성한다.
	 * 
	 * @param sid : Alias for this pool
	 * @param max_connections : Max. number of connections
	 * @param client : SAP client
	 * @param user : user id
	 * @param passwd : password
	 * @param ashost : host name
	 * @param lang : language
	 * @param sysnr :
	 * @param maxpool : Max. number of pools
	 * @param timeout : Time out
	 * @param waittime : Wait time
	 */
	@SuppressWarnings("deprecation")
	public static void jcoConnection(String sid, int max_connections,
			String client, String user, String passwd, String ashost,
			String lang, String sysnr, int maxpool, long timeout, long waittime) throws Exception {
		try {
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			
			if (JCO.getClientPoolManager().getPool(sid) != null) {
				JCO.getClientPoolManager().removePool(sid);
			}

			// JCO Client 생성
			JCO.addClientPool(sid, max_connections, client, user, passwd, lang, ashost, sysnr);
			
			long ftime = System.currentTimeMillis();
			if (log.isDebugEnabled()) {
				log.info("2. add JCO client pool.(execute time=" + (ftime - stime) + " msec.");
				viewConnectionInformation(sid, "jco connection ==> ");
			}
			
			stime = System.currentTimeMillis(); // 수행 시간 계산용
			
			JCO.getClientPoolManager().getPool(sid).setMaxPoolSize(maxpool);
			JCO.getClientPoolManager().getPool(sid).setTimeoutCheckPeriod(timeout);
			JCO.getClientPoolManager().getPool(sid).setMaxWaitTime(waittime);
			
			// Enables/disables cleanup when the client is being returned to its pool
			JCO.getClientPoolManager().getPool(sid).setResetOnRelease(true);

			// JCO를 통해 호출된 function 모듈의 모든 메타정보를 저장하기 위한 Repository 생성
			IRepository repository = JCO.createRepository("FTARepository", sid);

			repositoryTable.put(sid, repository);
			
			ftime = System.currentTimeMillis();
			if(log.isDebugEnabled()) log.info("3. created JCO repository.(execute time=" + (ftime - stime) + " msec.");
		} catch (JCO.Exception exp) {
			if (log.isErrorEnabled()) {
				log.error("jcoConnection(" + sid + ") : " + exp);
			}
			throw exp;
		}
	}

	@SuppressWarnings("deprecation")
	public static void viewConnectionInformation(String sid, String msg) {
		if (log.isDebugEnabled()) {
			int maxpool = JCO.getClientPoolManager().getPool(sid).getMaxPoolSize();
			int current = JCO.getClientPoolManager().getPool(sid).getCurrentPoolSize();
			int use = JCO.getClientPoolManager().getPool(sid).getMaxUsed();
			int connection = JCO.getClientPoolManager().getPool(sid).getMaxConnections();
			long timeout = JCO.getClientPoolManager().getPool(sid).getTimeoutCheckPeriod();
			long wait = JCO.getClientPoolManager().getPool(sid).getMaxWaitTime();
			int waitTh = JCO.getClientPoolManager().getPool(sid).getNumWaitingThreads();

			if (log.isDebugEnabled()) {
				log.debug(msg + sid + "'s Connection Pool information\n(max pool size=" + maxpool + ", max connection=" + connection
						+ ", current pool size=" + current + ", max used=" + use + ", max waiting thread=" + waitTh
						+ ", time out=" + timeout + ".ms, wait time=" + wait + ".ms)");
			}
		}
	}
}
