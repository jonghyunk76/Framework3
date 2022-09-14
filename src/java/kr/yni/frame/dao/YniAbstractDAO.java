package kr.yni.frame.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;

import kr.yni.frame.Constants;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.mapper.util.JcoMapValidator.DataHelper;
import kr.yni.frame.util.DataMapHelper;
import kr.yni.frame.util.ExcelUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.util.SystemHelper;

/**
 * <p>
 * iBatis Framework를 확장 구현한 DAO클래스
 * </p>
 * <p>
 * <code>org.springframework.orm.ibatis.support.SqlMapClientDaoSupport</code>클래스 상속
 * </p>
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings("deprecation")
public class YniAbstractDAO extends SqlMapClientDaoSupport {
	//org.springframework.orm.ibatis.support.
	//	로그를 기록하기 위한 Log 인스턴스
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected int MAX_LIMIT = 100000; // 페이지 하나에 표시할 row수
	
	@Autowired 
	protected Properties properties;
	
	@Resource(name = "messageSource")
	protected MessageSource messageSource;
	
	public YniAbstractDAO() { }
	
	/**
	 * sqlMapClient 지정
	 * 
	 * @param org.springframework.orm.ibatis.SqlMapClientFactoryBean bean클래스
	 */
	@Resource(name = "sqlMapClient")
	protected void setSuperSqlMapClient(SqlMapClient sqlMapClient) {
		super.setSqlMapClient(sqlMapClient);
	}
	
	/**
	 * insert Query 수행
	 * 
	 * @param queryId : sqlMap ID
	 * @param parameterObject
	 * @return
	 */
	protected Object insert(String queryId, Object parameterObject) throws Exception {
		Object obj =  null;
		
		try {
			obj =  DataMapHelper.toUpperCaseObject(getSqlMapClientTemplate().insert(queryId, parameterObject));
			if(obj != null) log.debug("complete inserted...(return class = " + obj.getClass() + ", value = " + obj.toString() + ")");
		} catch(Exception e) {
			log.error("failed insert...(Exception = " + e.toString() + ")");
			throw e;
		}
		
		return obj;
	}

	/**
	 * <p>
	 * update Query 수행
	 * </p>
	 * 
	 * @param queryId : sqlMap ID
	 * @param parameterObject
	 * @return
	 */
	protected int update(String queryId, Object parameterObject) throws Exception {
		int count = 0;
		
		try {
			count = getSqlMapClientTemplate().update(queryId, parameterObject);
			log.debug("complete updated...(row count = " + count + ")");
		} catch(Exception e) {
			log.error("failed update...(Exception = " + e.toString() + ")");
			throw e;
		}
		return count;
	}
	
	/**
	 * <p>
	 * delete Query 수행
	 * </p>
	 * 
	 * @param queryId : sqlMap ID
	 * @param parameterObject
	 * @return
	 */
	protected int delete(String queryId, Object parameterObject) throws Exception {
		int count = 0;
		
		try {
			count = getSqlMapClientTemplate().delete(queryId, parameterObject);
			log.debug("complete deleted...(row count = " + count + ")");
		} catch(Exception e) {
			log.error("failed delete...(Exception = " + e.toString() + ")");
			throw e;
		}
		return count;
	}
	
	/**
	 * <p>
	 * select Query를 수행한 결과를 Object타입으로 리턴
	 * </p>
	 * 
	 * @param queryId
	 * @return
	 */
	protected Object selectByPk(String queryId) {
		return DataMapHelper.toUpperCaseObject(getSqlMapClientTemplate().queryForObject(queryId));
	}
	
	/**
	 * <p>
	 * select Query를 수행한 결과를 Object타입으로 리턴
	 * </p>
	 * 
	 * @param queryId
	 * @param parameterObject
	 * @return
	 */
	protected Object selectByPk(String queryId, Object parameterObject) {
		return DataMapHelper.toUpperCaseObject(getSqlMapClientTemplate().queryForObject(queryId, parameterObject));
	}
	
	/**
	 * <p>
	 * select Query를 수행한 결과를 Object타입으로 리턴
	 * </p>
	 * <p>
	 * resultObject를 Controller 클래스에서 사용가능한 Object를 리턴한다.
	 * </p> 
	 * 
	 * @param queryId
	 * @param parameterObject
	 * @param resultObject dto 객체
	 * @return
	 */
	protected Object selectByPk(String queryId, Object parameterObject, Object resultObject) {
		return DataMapHelper.toUpperCaseObject(getSqlMapClientTemplate().queryForObject(queryId, parameterObject, resultObject));
	}
	
	/**
	 * <p>
	 * select Query를 수행한 결과 전체를 List에 담아 리턴
	 * </p>
	 * 
	 * @param queryId
	 * @param parameterObject
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected List list(String queryId, Object parameterObject) {
		return ExcelUtil.setHeaderColumns(getSqlMapClientTemplate().queryForList(queryId, parameterObject), parameterObject);
	}
	
	/**
	 * <p>
	 * select Query를 수행한 결과 중 지정된 범위만큼을 List에 담아 리턴한다.
	 * </p>
	 * 
	 * @param queryId
	 * @param parameterObject
	 * @param pageIndex 시작위치
	 * @param pageSize 조회할 row수
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected List listWithPaging(String queryId, Object parameterObject, int pageIndex, int pageSize) {
		int skipResults = (pageIndex - 1) * pageSize; // 조회할 시작 Index번호로 지정된 번호만큼 Skip됨
		int maxResults = pageSize; // 조회할 row수

		return ExcelUtil.setHeaderColumns(getSqlMapClientTemplate().queryForList(queryId, parameterObject, skipResults, maxResults), parameterObject);
	}

	/**
	 * <p>
	 * select Query를 수행한 결과 중 지정된 범위만큼을 List에 담아 리턴한다.<br>
	 * 페이지 번호 = map.get("page");
	 * 표시할 row수 = map.get("rows");
	 * @param queryId
	 * @param parameterObject
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected List listWithRowPaging(String queryId, Object parameterObject) {
		int pageIndex = 0;		// 초기 인덱스 번호
		int pageRows = this.MAX_LIMIT;	// 페이지에 표시할 열수
		// 엑셀 다운로드시에는 Max row수를 재설정한다.
		if(parameterObject instanceof Map) {
			Map map = (Map) parameterObject;
			String columns = StringHelper.null2void(map.get("headers"));
			
			if(!StringHelper.isNull(columns)) {
				pageRows = Constants.EXCEL_MAX_ROWS;
			}
		}
		if(parameterObject instanceof Map) {
			Map map = (Map) parameterObject;
			String pageChk = StringHelper.null2void(map.get("page"));
			String rowsChk = StringHelper.null2void(map.get("rows"));
			
			if(!DataHelper.isNumber(pageChk)) pageChk = Integer.toString(pageIndex);
			if(!DataHelper.isNumber(rowsChk)) rowsChk = Integer.toString(pageRows);
			
			int index = StringHelper.null2zero(pageChk);
			int rows = StringHelper.null2zero(rowsChk);
			
			pageIndex = (index-1) * rows;
			if(pageIndex < 1) pageIndex = 0;
			pageRows = rows;
			
			if(log.isInfoEnabled()) log.info("page index=" + pageIndex + ", rows=" + pageRows);
		}
		
		if(pageRows < 1) {
			return ExcelUtil.setHeaderColumns(getSqlMapClientTemplate().queryForList(queryId, parameterObject), parameterObject);
		} else {
			return ExcelUtil.setHeaderColumns(getSqlMapClientTemplate().queryForList(queryId, parameterObject, pageIndex, pageRows), parameterObject);
		}
	}

	/**
	 * <p>
	 * List크기만큼 지정된 Query의 배치를 수행한다.
	 * </p>
	 * 
	 * @param queryId
	 * @param p_mapList
	 * @return Oacle의 경우 -2를 리턴함.
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int executeBatch(final String queryId, final List p_mapList) throws Exception {
		Integer result = (Integer) getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				int count = 0; // 배치실행 row 수
				int total = 0; // 배치실행 수
				
				executor.startBatch();
				
				for (Iterator iterator = p_mapList.iterator(); iterator.hasNext();) {
					Map map = (Map) iterator.next();
					
					executor.insert(queryId, map);
					
					count++;
					if(count % Constants.DB_BATCH_SIZE == 0) {
						total += executor.executeBatch();
						executor.startBatch();
					}
				}
				
				total += executor.executeBatch();
				
				log.debug("complete executeBatch...(rows = " + count + ", update count = " + total + ")");
				
				return new Integer(executor.executeBatch());
			}
		});
		
		return result.intValue();
	}
	
	/**
	 * <p>
	 * List크기만큼 지정된 파라메터를 포함한 Query의 배치를 수행한다.
	 * </p>
	 * 
	 * @param p_sqlID
	 * @param p_mapList
	 * @param l_map
	 * @return Oacle의 경우 -2를 리턴함.
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int executeBatch(final String p_sqlID, final List p_mapList,final Map l_map) throws Exception {
		Integer result = null;
		
		try {
			getSqlMapClient().startTransaction();
			
			result = (Integer) getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
				public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
					int count = 0; // 배치실행 row 수
					int total = 0; // 배치실행 수
					
					executor.startBatch();
					
					for (Iterator iterator = p_mapList.iterator(); iterator.hasNext();) {
						Map map = (Map) iterator.next();
						
						map.put(Constants.KEY_COMPANY_CD, l_map.get(Constants.KEY_COMPANY_CD));
						map.put(Constants.KEY_DEFAULT_LANGUAGE, l_map.get(Constants.KEY_DEFAULT_LANGUAGE));
						map.put(Constants.KEY_S_COMPANY_CD, l_map.get(Constants.KEY_S_COMPANY_CD));
						map.put(Constants.KEY_S_VENDOR_CD, l_map.get(Constants.KEY_S_VENDOR_CD));
						map.put(Constants.KEY_S_DEFAULT_LANGUAGE, l_map.get(Constants.KEY_S_DEFAULT_LANGUAGE));
						map.put(Constants.KEY_UPDATE_BY, l_map.get(Constants.KEY_UPDATE_BY));
						map.put(Constants.KEY_CREATE_BY, l_map.get(Constants.KEY_CREATE_BY));
						
						executor.insert(p_sqlID, map);
						
						count++;
						if(count % Constants.DB_BATCH_SIZE == 0) {
							total += executor.executeBatch();
							executor.startBatch();
						}
					}
					
					total += executor.executeBatch();
					
					log.debug("complete executeBatch...(rows = " + count + ", update count = " + total + ")");
					
					return new Integer(total);
				}
			});
			
			getSqlMapClient().commitTransaction();
		} catch(SQLException e) {
			new FrameException(StringHelper.null2void(e.getCause()), e.getMessage());
		} finally {
           try {
               this.getSqlMapClient().endTransaction();
            } catch (SQLException e) { 
            	e.printStackTrace();
            }
	    }
		
		return result.intValue();
	}
	
	/**
	 * <p>
	 * 프로시져 실행 시 지정된 파라메터의 OUT value를 Map에 담아 리턴한다.
	 * </p>
	 * 
	 * @param queryId 쿼리ID
	 * @param map      map파라메터
	 * @return map파라메터에 OUT value를 담아 리턴한다.
	 * @throws Exception
	 */
	public Object executeProcedure(String queryId, Map map) throws Exception {
		Object resultObj = getSqlMapClientTemplate().queryForObject(queryId, map);
		
		return resultObj;
	}
	
	/**
	 * 실행하는 Query구문 조회(ibatis용)
	 * 
	 * @param sqlId sql ID
	 * @param params bind 파라메터
	 * @return Query문
	 */
	public String getSqlQuery(String sqlId, Object params) {
		String sqlStr = null;
		
		try {
			MappedStatement mappedStatement;
			StatementScope statementScope;
			SessionScope sessionScope;
			Sql sql ;
			
			mappedStatement = ((ExtendedSqlMapClient) getSqlMapClient()).getMappedStatement(sqlId);
			
			sessionScope = new SessionScope();
			statementScope = new StatementScope(sessionScope);
			
			// 생략 하면 다이나믹 쿼리가 적용안됨
			mappedStatement.initRequest(statementScope);
			
			sql = mappedStatement.getSql();
			
			String sqlString = sql.getSql(statementScope, params);
			Object[] paramObj = sql.getParameterMap(statementScope, params).getParameterObjectValues(statementScope, params);
			
			sqlStr = bindQueryParam(sqlString, paramObj);
		} catch(Exception e) {
			log.error(e);
		}
		
		return sqlStr;
	}

	public String bindQueryParam(String sql, Object... params) {
	    for (Object param : params) {
	       sql = sql.replaceFirst("\\?",
	               param == null ? "null" : "'"+param.toString()+"'");
	    }
	    
	    return sql;
	}

	/**
	 * <code>Connection</code>객체를 구한다.
	 * @return Connection 객체
	 */
	protected Connection getConnection() {
		Connection con = null;
		try {
			con = super.getSqlMapClient().getDataSource().getConnection();
			
			if(log.isDebugEnabled()) {
				log.debug("Connection Object = " + con);
			}
		} catch(SQLException se) {
			new FrameException("DB Connect fail", se.getCause());
		}
		return con;
	}
	
	/**
	 * key에 해당하는 메시지를 구한다.
	 * 
	 * @param messageKey
	 * @return
	 */
	protected String getMessage(String messageKey) {
		return getMessage(messageKey, null, null);
	}
	
	/**
	 * key에 해당하는 메시지를 구한다.
	 * 
	 * @param messageKey
	 * @param messageParameters 맵핑할 인자
	 * @return
	 */
	protected String getMessage(String messageKey, Object messageParameters[]) {
		return getMessage(messageKey, messageParameters, null);
	}
	
	/**
	 * key에 해당하는 메시지를 구한다.
	 * 
	 * @param messageKey
	 * @param messageParameters 맵핑할 인자
	 * @param locale 언어(KOR, ENG, LOC)
	 * @return
	 */
	protected String getMessage(String messageKey, Object messageParameters[], String locale) {
		Locale rLocale = SystemHelper.getLocale(locale);
		
		return messageSource.getMessage(messageKey, messageParameters, null, rLocale);
	}
	
}
