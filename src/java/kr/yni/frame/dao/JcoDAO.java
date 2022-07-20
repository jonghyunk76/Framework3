package kr.yni.frame.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.exception.FrameException;
import kr.yni.frame.pool.JcoPoolManager;
import kr.yni.frame.util.StringHelper;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Client;
import com.sap.mw.jco.JCO.Field;
import com.sap.mw.jco.JCO.FieldIterator;
import com.sap.mw.jco.JCO.Function;
import com.sap.mw.jco.JCO.Structure;

/**
 * <p>
 * RFC 인터페이스를 위한 JCO DAO 클래스
 * </p>
 * 
 * @author 김종현
 * 
 */
public class JcoDAO {
	protected static Log log = LogFactory.getLog(JcoDAO.class);

	private String sid = null;

	private static JCO.Client client = null;

	private IRepository repository = null;

	/**
	 * 최대 조회 건수의 제한 값을 설정한다.
	 */
	protected static int maxFetchLimit = Integer.MAX_VALUE;

	static {
		maxFetchLimit = JcoPoolManager.getMaxFetchLimit();
	}

	/**
	 * jco_resource.properties<use.client.name>에서 설정한 sid 명를 사용하여 JCO.Client객체를
	 * 얻고자 할 경우
	 */
	public JcoDAO() {
		this(null, null);
	}

	/**
	 * 주어진 sid 명를 사용하여 JCO.Client객체를 얻고자 할 경우
	 * 
	 * @param sid
	 */
	public JcoDAO(String sid) {
		this(sid, null);
	}

	public JcoDAO(String sid, JCO.Client client) {
		if (sid == null) {
			sid = JcoPoolManager.getDefaultSID();
		}
		
		this.sid = sid;
		this.client = client;
		
		if (log.isDebugEnabled()) {
			log.info("1. set JCO connect Info. (Client SID = " + sid + ", maxFetchLimit = " + maxFetchLimit + ")");
		}
	}

	/**
	 * 외부에서 생성한 JCO.Client를 지정한다. 이때 이미 지정한 JCO.Client가 있다면 재 지정하지 않는다.
	 * 
	 * @param cl
	 *            설정할 JCO.Client 객체
	 */
	public void setClient(JCO.Client cl) {
		if (client == null) {
			client = cl;
		}
	}

	/**
	 * SAP Client 와 통신하기 위한 Connection 객체를 리턴한다.
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	protected Client getConnection() throws Exception {
		JCO.Client pooledClient;
		
		try {
			// JCO를 통해 호출된 function모듈의 모든 메타정보를 저장하기 위한 Repository 획득
			repository = JcoPoolManager.getRepository(sid);
			
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			
			if (client == null) {
				// Get a client from the pool
				pooledClient = JCO.getClient(sid);
			} else {
				// 외부에서 지정된 JCO.Client를 사용한다.
				pooledClient = client;
			}
			
			long ftime = System.currentTimeMillis();
			if(log.isDebugEnabled()) log.info("4. get a client from the pool.(execute time=" + (ftime - stime) + " msec. / pooledClient = " + pooledClient);
			
			stime = System.currentTimeMillis(); // 수행 시간 계산용
			
			// Sends a ping to the server
			pooledClient.ping();
			
			ftime = System.currentTimeMillis();
			if(log.isDebugEnabled()) log.info("5. sends a ping to the server.(execute time=" + (ftime - stime) + " msec.");
		} catch (JCO.Exception e) {
			if (log.isErrorEnabled()) {
				log.error("getConnection(" + sid + ") : " + e);
			}
			throw e;
		}

		return pooledClient;
	}

	/**
	 * 특정 Client의 ConnectionPool 반환
	 * 
	 * @param client
	 *            JCO.Client객체
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	protected void releaseClient(Client client) throws Exception {
		try {
			if (JCO.getClientPoolManager().getPool(sid) != null) {
				JcoPoolManager.viewConnectionInformation(sid, "before release ==> ");
			}
			
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			
			JCO.releaseClient(client);
			
			long ftime = System.currentTimeMillis();
			if (log.isDebugEnabled()) {
				log.info("11. release client.(execute time=" + (ftime - stime) + " msec.)");
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("release client(" + sid + ") : " + ex.toString());
			}
		}
		
		try {
			// 최근 사용된 Pool 갯수가 최대 Pool 갯수를 초과할 경우 Pool를 제거한다.
			int max = JCO.getClientPoolManager().getPool(sid).getMaxPoolSize();
			int current = JCO.getClientPoolManager().getPool(sid).getCurrentPoolSize();
			if (max <= current) {
				JCO.removeClientPool(sid);
				JcoPoolManager.removeRepositoryTable(sid);
			}
			if (JCO.getClientPoolManager().getPool(sid) != null) {
				JcoPoolManager.viewConnectionInformation(sid, "after release ==> ");
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("remove repository table(" + sid + ") : " + ex.toString());
			}
			throw ex;
		}
	}

	protected List<Object> getTableFromTableParameter(Function function,
			List<String> rfcTbl) throws Exception {
		return this.getTableFromTableParameter(function, rfcTbl, null, null, null);
	}

	protected List<Object> getTableFromTableParameter(Function function,
			String rfcTbl) throws Exception {
		return this.getTableFromTableParameter(function, rfcTbl, null, null, null);
	}

	protected List<Object> getTableFromTableParameter(Function function,
			List<String> rfcTbl, List<Object> rlist) throws Exception {
		return this.getTableFromTableParameter(function, rfcTbl, rlist, null, null);
	}

	protected List<Object> getTableFromTableParameter(Function function,
			String rfcTbl, List<Object> rlist) throws Exception {
		return this.getTableFromTableParameter(function, rfcTbl, rlist, null, null);
	}

	protected List<Object> getTableFromTableParameter(Function function,
			List<String> rfcTbl, Object offset, Object maxCount) throws Exception {
		return this.getTableFromTableParameter(function, rfcTbl, null, offset, maxCount);
	}

	protected List<Object> getTableFromTableParameter(Function function,
			String rfcTbl, Object offset, Object maxCount) throws Exception {
		return this.getTableFromTableParameter(function, rfcTbl, null, offset, maxCount);
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Table parameter 이면서 Table 정보를 가져오는 경우 호출
	 * 
	 * @param function
	 *            실행결과의 연결정보를 가지는 function 객체
	 * @param rcfTbl
	 *            실행결과가 저장된 table 명
	 * @param rlist
	 *            리턴될 List 객체
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getTableFromTableParameter(Function function, Object rfcParam, List<Object> rlist, Object offset, Object maxCount)
			throws Exception {
		if (function == null) {
			if (log.isErrorEnabled()) {
				log.error("not found JCO.Function................");
			}
			throw new FrameException("not found JCO.Function");
		}
		
		Map<String, Object> positionMap = new LinkedHashMap<String, Object>();
		int pageIndex = 0;
        int pageRows = maxFetchLimit;
        
		if (log.isDebugEnabled()) {
			log.debug((new StringBuilder("data name = ")).append(rfcParam).toString());
		}
		
        int index = StringHelper.null2zero(StringHelper.null2string(offset, Integer.toString(pageIndex)));
        int rows = StringHelper.null2zero(StringHelper.null2string(maxCount, Integer.toString(pageRows)));
        
        pageIndex = (index - 1) * rows;
        if(pageIndex < 1) pageIndex = 0;
        pageRows = rows * index;
        
        if(log.isDebugEnabled()) {
            log.debug((new StringBuilder("page index=")).append(pageIndex).append(", rows=").append(pageRows).toString());
        }
        
        positionMap.put("rows", StringHelper.null2string(pageRows, "0")); // 조회 시 처음을 건너뛰고 읽어 들일 시작 위치
        positionMap.put("page", StringHelper.null2string(pageIndex, "0")); // 조회 건수
        
		try {
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			long ftime = 0;
			
			if (rfcParam instanceof String) {
				String paramName = rfcParam.toString();
				
				if (!paramName.isEmpty()) {
					JCO.Table jcoTbl = function.getTableParameterList().getTable(paramName);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = table, table name = " + paramName + ", row count = " + jcoTbl.getNumRows() + ")");
					}
					
					stime = System.currentTimeMillis(); // 수행 시간 계산용
					
					rlist = fetchTableList(paramName, jcoTbl, rlist, positionMap);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-2. changed dataset from table or structure.(execute time=" + (ftime - stime) + " msec.");
					}
				}
			}

			if (rfcParam instanceof List) {
				List<String> rfcTbl = (List<String>) rfcParam;
				
				for (int i = 0; i < rfcTbl.size(); i++) {
					String tblName = rfcTbl.get(i);
					
					stime = System.currentTimeMillis();
					
					// Get table containing the contents
					JCO.Table jcoTbl = function.getTableParameterList().getTable(rfcTbl.get(i));
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = table, table name = " + tblName + ", row count = " + jcoTbl.getNumRows() + ")");
					}
					
					stime = System.currentTimeMillis(); // 수행 시간 계산용
					
					rlist = fetchTableList(tblName, jcoTbl, rlist, positionMap);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-2. changed dataset from table or structure.(execute time=" + (ftime - stime) + " msec.");
					}
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("result count =" + positionMap);
			}
		} catch (JCO.Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		}

		return rlist;
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 하나의 String 타입 정보를 가져오는 경우
	 * 호출
	 */
	protected String getStringFromExportParameter(Function function,
			String paramName) throws Exception {
		return StringHelper.null2string(this.getValueFromExportParameter(function, paramName, null), "").toString();
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 하나의 double 타입 정보를 가져오는 경우
	 * 호출
	 */
	protected double getDoubleFromExportParameter(Function function,
			String paramName) throws Exception {
		return Double.parseDouble(StringHelper.null2string(this.getValueFromExportParameter(function, paramName, null),"0"));
	}
	
	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 하나의 <code>byte</code>타입 정보를 가져오는 경우
	 * 호출
	 */
	protected byte[] getByteFromExportParameter(Function function,
			String paramName) throws Exception {
		Object obj = this.getValueFromExportParameter(function, paramName, null);
		
		if(obj instanceof byte[]) {
			return (byte[])this.getValueFromExportParameter(function, paramName, null);
		} else {
			throw new FrameException("[TypeCastingException] " + obj.getClass()); 
		}
	}
	
	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 하나의 double 타입 정보를 가져오는 경우
	 * 호출
	 */
	protected List<Object> getListFromExportParameter(Function function,
			String paramName, List<Object> rlist) throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		if (rlist == null) {
			rlist = new ArrayList<Object>();
		}
		
		Object obj = this.getValueFromExportParameter(function, paramName, null);
		
		if(obj instanceof byte[]) {
    		obj = (byte[]) obj;
    	} else {
    		obj = StringHelper.null2void(obj);
    	}
		
		map.put(paramName, obj);
		rlist.add(map);

		return rlist;
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 하나의 List 타입 정보를 가져오는 경우 호출
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getListFromExportParameter(Function function,
			List<String> list) throws Exception {
		return (List<Object>) this.getValueFromExportParameter(function, list, null);
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 하나의 List 타입 정보를 가져오는 경우 호출
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getListFromExportParameter(Function function,
			List<String> list, List<Object> rlist) throws Exception {
		return (List<Object>) this.getValueFromExportParameter(function, list, rlist);
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 Object 타입 정보를 가져오는 경우 호출
	 * 
	 * @param function
	 *            실행결과의 연결정보를 가지는 function 객체
	 * @param rfcParam
	 *            파라메터명 List
	 * @param rlist
	 *            리턴될 List 객체
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected Object getValueFromExportParameter(Function function, Object rfcParam, List<Object> rlist) throws Exception {
		
		if (function == null) {
			if (log.isErrorEnabled()) {
				log.error("not found JCO.Function................");
			}
			throw new FrameException("not found JCO.Function");
		}

		// 리턴할 list 가 null 인 경우 새로운 list 를 생성한다.
		if (rlist == null) {
			rlist = new ArrayList<Object>();
		}

		Object obj = null;
		try {
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			long ftime = 0;
			
			if (rfcParam instanceof String) {
				String paramName = rfcParam.toString();
				if (!paramName.isEmpty()) {
					obj = function.getExportParameterList().getValue(paramName);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = export, object name = " + paramName + ", return = " + obj + ")");
					}
				}
			}

			if (rfcParam instanceof List) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				List<String> paramList = (List<String>) rfcParam;
				
				for (int i = 0; i < paramList.size(); i++) {
					String paramName = paramList.get(i);
					
					stime = System.currentTimeMillis(); // 수행 시간 계산용
					
					Object value = function.getExportParameterList().getValue(paramName);

					map.put(paramName, value);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = export, object name = " + paramName + ", return = " + value + ")");
					}
				}
				rlist.add(map);

				obj = rlist;
			}
		} catch (JCO.Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		}

		return obj;
	}

	protected List<Object> getStructureFromExportParameter(Function function,
			String rfcStruc) throws Exception {
		return this.getStructureFromExportParameter(function, rfcStruc, null);
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 Structure 정보를 가져오는 경우 호출
	 * 
	 * @param function
	 *            실행결과의 연결정보를 가지는 function 객체
	 * @param rfcStrucs
	 *            실행결과가 저장된 Structure 명(Structure 속성은 없거나 1개 이상으로 구성됨)
	 * @param rlist
	 *            리턴될 List 객체
	 * @return
	 * @throws Exception
	 */
	protected List<Object> getStructureFromExportParameter(Function function,
			String rfcStruc, List<Object> rlist) throws Exception {
		if (function == null) {
			if (log.isErrorEnabled()) {
				log.error("not found JCO.Function................");
			}
			throw new FrameException("not found JCO.Function");
		}

		if (log.isDebugEnabled()) {
			log.debug("data name = " + rfcStruc);
		}

		try {
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			long ftime = 0;
			
			if (!rfcStruc.isEmpty()) {
				// Get table containing the contents
				JCO.Structure jcoStruc = function.getExportParameterList().getStructure(rfcStruc);
				
				ftime = System.currentTimeMillis();
				if (log.isDebugEnabled()) {
					log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = export,  structure name = " + rfcStruc + ", row count = " + jcoStruc.getFieldCount() + ")");
				}
				
				stime = System.currentTimeMillis();
				
				rlist = fetchStructureList(rfcStruc, jcoStruc, rlist);
				
				ftime = System.currentTimeMillis();
				if (log.isDebugEnabled()) {
					log.info("10-2. changed dataset from table or structure.(execute time=" + (ftime - stime) + " msec.");
				}
			}
		} catch (JCO.Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		}

		return rlist;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getStructureFromExportMapParameter(Function function, String rfcStruc)
            throws Exception {
        return this.getStructureFromExportMapParameter(function, rfcStruc, null);
    }
	
	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 Structure 정보를 가져오는 경우 호출
	 * 
	 * @param function 실행결과의 연결정보를 가지는 function 객체
	 * @param rfcStrucs 실행결과가 저장된 Structure 명(Structure 속성은 없거나 1개 이상으로 구성됨)
	 * @param structurePut 리턴될 Map 객체
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings("rawtypes")
	protected Map getStructureFromExportMapParameter(Function function, String rfcStruc, Map<String,Object> structurePut)
        throws Exception {
        if(function == null) {
            if(log.isErrorEnabled()) {
                log.error("not found JCO.Function................");
            }
            
            throw new FrameException("not found JCO.Function");
        }
        
        if(log.isDebugEnabled()) {
            log.debug((new StringBuilder("data name = ")).append(rfcStruc));
        }
        
        try {
        	long stime = System.currentTimeMillis(); // 수행 시간 계산용
			long ftime = 0;
			
            if(!rfcStruc.isEmpty()) {
                Structure jcoStruc = function.getExportParameterList().getStructure(rfcStruc);
                
                ftime = System.currentTimeMillis();
				if (log.isDebugEnabled()) {
					log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = export,  structure name = " + rfcStruc + ", row count = " + jcoStruc.getFieldCount() + ")");
				}
				
				stime = System.currentTimeMillis();
				
                structurePut = fetchStructureMap(rfcStruc, jcoStruc);
                
                ftime = System.currentTimeMillis();
				if (log.isDebugEnabled()) {
					log.info("10-2. changed dataset from table or structure.(execute time=" + (ftime - stime) + " msec.");
				}
            }
        } catch (JCO.Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		} catch (Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		}
        
        return structurePut;
    }
    
	protected List<Object> getTableFromExportParameter(Function function,
			List<String> rfcTbl) throws Exception {
		return this.getTableFromExportParameter(function, rfcTbl, null, null, null);
	}

	protected List<Object> getTableFromExportParameter(Function function,
			String rfcTbl) throws Exception {
		return this.getTableFromExportParameter(function, rfcTbl, null, null, null);
	}

	protected List<Object> getTableFromExportParameter(Function function,
			List<String> rfcTbl, List<Object> rlist) throws Exception {
		return this.getTableFromExportParameter(function, rfcTbl, rlist, null, null);
	}

	protected List<Object> getTableFromExportParameter(Function function,
			String rfcTbl, List<Object> rlist) throws Exception {
		return this.getTableFromExportParameter(function, rfcTbl, rlist, null, null);
	}

	protected List<Object> getTableFromExportParameter(Function function,
			List<String> rfcTbl, Object offset, Object maxCount)
			throws Exception {
		return this.getTableFromExportParameter(function, rfcTbl, null, offset, maxCount);
	}

	protected List<Object> getTableFromExportParameter(Function function,
			String rfcTbl, Object offset, Object maxCount) throws Exception {
		return this.getTableFromExportParameter(function, rfcTbl, null, offset, maxCount);
	}

	/**
	 * RFC Function 에서 전달되는 형태가 Export parameter 이면서 Table 정보를 가져오는 경우 호출
	 * 
	 * @param function 실행결과의 연결정보를 가지는 function 객체
	 * @param rcfTbl 실행결과가 저장된 table 명
	 * @param rlist 리턴될 List 객체
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getTableFromExportParameter(Function function,
			Object rfcParam, List<Object> rlist, Object offset, Object maxCount) throws Exception {
		if (function == null) {
			if (log.isErrorEnabled()) {
				log.error("not found JCO.Function................");
			}
			throw new FrameException("not found JCO.Function");
		}

		if (log.isDebugEnabled()) {
			log.debug("data name = " + rfcParam);
		}

		Map<String, Object> positionMap = new LinkedHashMap<String, Object>();
		positionMap.put("offset", StringHelper.null2string(offset, "0")); // 조회 시 처음을 건너뛰고 읽어 들일 시작 위치
		positionMap.put("maxCount", StringHelper.null2string(maxCount, "0")); // 조회 건수

		try {
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			long ftime = 0;
			
			if (rfcParam instanceof String) {
				String paramName = rfcParam.toString();
				if (!paramName.isEmpty()) {
					JCO.Table jcoTbl = function.getExportParameterList().getTable(paramName);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = export,  table name = " + paramName + ", row count = " + jcoTbl.getNumRows() + ")");
					}
					
					stime = System.currentTimeMillis();
					
					rlist = fetchTableList(paramName, jcoTbl, rlist, positionMap);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-2. changed dataset from table or structure.(execute time=" + (ftime - stime) + " msec.");
					}
				}
			}

			if (rfcParam instanceof List) {
				List<String> rfcTbl = (List<String>) rfcParam;
				for (int i = 0; i < rfcTbl.size(); i++) {
					String tblName = rfcTbl.get(i);
					
					stime = System.currentTimeMillis(); // 수행 시간 계산용
					
					JCO.Table jcoTbl = function.getExportParameterList().getTable(rfcTbl.get(i));
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-1. get table containing the contents.(execute time=" + (ftime - stime) + " msec. / parameter type = export, table name = " + tblName + ", row count = " + jcoTbl.getNumRows() + ")");
					}
					
					stime = System.currentTimeMillis(); // 수행 시간 계산용
					
					rlist = fetchTableList(tblName, jcoTbl, rlist, positionMap);
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("10-2. changed dataset from table or structure.(execute time=" + (ftime - stime) + " msec.");
					}
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("result count =" + positionMap);
			}
		} catch (JCO.Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		}

		return rlist;
	}

	/**
	 * RFC Function 호출 결과값을 List 에 담아 리턴한다.
	 * 
	 * @param client JCO client 객체
	 * @param func 호출할 function 명
	 * @param param function 의 parameter 값(parameter 는 없을거나 1개이상으로 구성됨)
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected Function execute(JCO.Client client, String func, Map<String, Object> param) throws Exception {
		String keyName; // parameter key
		Object keyValue; // parameter value
		JCO.Function function = null;

		try {
			long stime = System.currentTimeMillis(); // 수행 시간 계산용
			
			String functionName = StringHelper.null2void(func);
			if (functionName.isEmpty()) {
				throw new FrameException("[Error] function's name is null.");
			}

			// RFC Function exist
			// getFunctionTemplate()메소드는 원격 함수모듈의 모든 메타정보를 포함한
			// IFunctionTemplate인스턴스를 반환한다.
			IFunctionTemplate ftemplate = repository.getFunctionTemplate(functionName);
			
			long ftime = System.currentTimeMillis();
			if (log.isDebugEnabled()) {
				log.info("6. get SAP function(RFC) template.(execute time=" + (ftime - stime) + " msec. / function name = " + func + ")");
			}
			
			if (ftemplate != null) {
				stime = System.currentTimeMillis(); // 수행 시간 계산용
				
				// Create a function from the template
				// JCO.Function은 자바에서 원격 함수 모듈를 구한다.(RFM에 대한 Proxy 일종)
				function = ftemplate.getFunction();
				
				ftime = System.currentTimeMillis();
				if (log.isDebugEnabled()) {
					log.info("7. Create a function from the template.(execute time=" + (ftime - stime) + " msec. / function name = " + func + ")");
				}
				
				stime = System.currentTimeMillis(); // 수행 시간 계산용
				
				// Fill in input parameters
				JCO.ParameterList impParam = function.getImportParameterList();
				JCO.ParameterList tblParam = function.getTableParameterList();
				
				if(param != null && param.size() > 0) {
					Set keySet = param.keySet();
	
					for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
						keyName = (String) iterator.next();
						keyValue = param.get(keyName);
						
						if(keyValue instanceof JCO.Table) {
							JCO.Table tval = (JCO.Table) keyValue;
							JCO.Table tbl = tblParam.getTable(tval.getName());
							
							for(int t = 0; t < tval.getNumRows(); t++) {
								tval.setRow(t); // table 행 지정
								tbl.appendRow();
								
								for(int f = 0; f < tval.getNumFields(); f++) { // table field number
									JCO.Field field = tval.getField(f);
									
									tbl.setValue(field.getValue(), field.getName());
								}
							}
							
							if (log.isDebugEnabled()) log.debug("parameter type = table, name = "+tval.getName()+", keyName =" + keyName + ": keyValue=" + keyValue);
						} else {
							impParam.setValue(keyValue, keyName);
							
							if (log.isDebugEnabled()) log.debug("parameter type = string, keyName =" + keyName + ": keyValue=" + keyValue);
						}
					}
					
					ftime = System.currentTimeMillis();
					if (log.isDebugEnabled()) {
						log.info("8. Fill in input parameters.(execute time=" + (ftime - stime) + " msec. / function name = " + func + ")");
					}
				} else {
					if(log.isInfoEnabled()) {
						log.info("8. Import parameter is null.");
					}
				}
				
				stime = System.currentTimeMillis(); // 수행 시간 계산용
				
				// Call the remote system
				client.execute(function);

				JcoPoolManager.viewConnectionInformation(sid, "execute ==> "); // Connection Pool info
				
				ftime = System.currentTimeMillis();
				if (log.isDebugEnabled()) {
					log.info("9. Call the remote system.(execute time=" + (ftime - stime) + " msec.)");
				}
			}
		} catch (JCO.AbapException exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		} catch (JCO.Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		} catch (Exception exp) {
			if (log.isErrorEnabled()) {
				log.error(exp.getMessage());
			}
			throw exp;
		}

		return function;
	}

	/**
	 * 요청결과을 LinkedHashMap에 등록한 후 이를 List 에 담아 반환한다.
	 * 
	 * @param tblName
	 *            테이블명
	 * @param tbl
	 *            JCO.Table 객체
	 * @param rlist
	 *            결과 LIST
	 * @return
	 */
	protected List<Object> fetchTableList(String tblName, JCO.Table tbl,
			List<Object> rlist, Map<String, Object> positionMap) throws Exception {
		LinkedHashMap<String, Object> hsMap;

		int offset = 0; // 조회 시 처음에 offset 만큼 건너뛰고 읽는다.
		int maxCount = 0; // 최대 maxCount+1 개 조회한다.
		int totCount = 0; // 전체 조회 건수를 담는다.
		int rmnCount = 0; // 실제로 조회 결과로 담고 남은 개수이다.
		int getCount = 0; // 실제로 조회된 건수이다.

		// 리턴할 list가 null인 경우 새로운 list를 생성한다.
		if (rlist == null) {
			rlist = new ArrayList<Object>();
		}
		getCount = rlist.size();

		if (tbl.getNumRows() > 0) {
			offset = StringHelper.null2zero(positionMap.get("page"));
			maxCount = StringHelper.null2zero(positionMap.get("rows"));
			
			totCount = tbl.getNumRows() + rlist.size();

			for (int i = offset; i < ((maxCount == 0 || maxCount > tbl.getNumRows()) ? tbl.getNumRows() : maxCount); i++) {
				hsMap = new LinkedHashMap<String, Object>();

				/*
				 * if(getCount == maxFetchLimit) { if(log.isErrorEnabled()) {
				 * log.error("조회 가능한 최대 제한치인 " + i + "건를 초과했습니다. [" + tblName +
				 * "]"); } throw new
				 * OriginException("Views exceeded the maximum limit."); }
				 */

				tbl.setRow(i);

				for (JCO.FieldIterator e = tbl.fields(); e.hasMoreElements();) {
					JCO.Field field = e.nextField();

					hsMap.put(field.getName(), field.getString() == null ? "": field.getString());
				}
				hsMap.put("TOTALCOUNT", totCount);
				
				rlist.add(hsMap);
				getCount++;
			}
			
			rmnCount = totCount - (getCount + offset);
		}
		positionMap.put("getCount", getCount);
		positionMap.put("totCount", totCount);
		positionMap.put("rmnCount", rmnCount);

		if (log.isDebugEnabled()) {
			if (rlist.size() > 0) {
				log.debug(tblName + "=" + rlist.get(0));
			}
			log.info("table(" + tblName + ")'s rows = " + tbl.getNumRows() + " / return size = " + rlist.size());
		}
		
		return rlist;
	}

	/**
	 * 요청결과을 LinkedHashMap에 등록한 후 이를 List 에 담아 반환한다.
	 * 
	 * @param strucName  구조체 명
	 * @param rfcStruc   JCO.Structure 객체
	 * @param rlist      결과 LIST
	 * @return
	 */
	protected List<Object> fetchStructureList(String strucName,
			JCO.Structure rfcStruc, List<Object> rlist)  throws Exception {
		LinkedHashMap<String, Object> hsMap;

		// 리턴할 list 가 null 인 경우 새로운 list 를 생성한다.
		if (rlist == null) {
			rlist = new ArrayList<Object>();
		}

		if (rfcStruc.getFieldCount() > 0) {
			hsMap = new LinkedHashMap<String, Object>();

			for (JCO.FieldIterator e = rfcStruc.fields(); e.hasMoreElements();) {
				JCO.Field field = e.nextField();

				hsMap.put(field.getName(), field.getString() == null ? "" : field.getString());
			}
			// 테이블이 여러개인 경우 어느 테이블에 대한 값인지 확인하기 위해 index 를 지정한다.
			rlist.add(hsMap);
		}

		if (log.isDebugEnabled()) {
			log.debug(rfcStruc + "=" + rlist.get(0));
			log.info("Structure(" + strucName + ")'s field = " + rfcStruc.getFieldCount() + " / return index = " + rlist.size());
		}
		
		return rlist;
	}
	
	/**
	 * 요청결과을 LinkedHashMap에 등록한 후 이를 Map 에 담아 반환한다.
	 * 
	 * @param strucName  구조체 명
	 * @param rfcStruc   JCO.Structure 객체
	 * @return
	 * @throws Exception
	 */
	protected Map<String,Object> fetchStructureMap(String strucName, Structure rfcStruc)  throws Exception {
    	LinkedHashMap<String,Object> hsMap = new LinkedHashMap<String,Object>();

        if(rfcStruc.getFieldCount() > 0) {
            Field field = null;
            
            for(FieldIterator e = rfcStruc.fields(); e.hasMoreElements(); hsMap.put(field.getName(), field.getString() != null ? ((Object) (field.getString())) : "")) {
                field = e.nextField();
            }
        }
        
        if(log.isDebugEnabled()) {
            log.debug((new StringBuilder()).append(rfcStruc).append("=").append(hsMap).toString());
            log.info((new StringBuilder("Structure(")).append(strucName).append(")'s field = ").append(rfcStruc.getFieldCount()));
        }
        
        return hsMap;
    }
}