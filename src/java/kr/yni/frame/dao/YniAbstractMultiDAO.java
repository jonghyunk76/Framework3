package kr.yni.frame.dao;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * <p>
 * 이기종간의 DB를 구현하기 위한 클래스로 타 시스템의 DB연결이 적용된다.(XA Mode적용)<br>
 * 단, 글로벌 트랜젝션 관리가 적용되기 때문에 단일 DB연결보다 10%~20% 성능저하가 발생하므로, 단일DB을 적용할 경우에는 사용하지 않는게 좋다.
 * <p>
 * @author YNI-maker
 * @since 2013. 5. 8. 오후 3:10:14
 * @version 1.0
 *
 * @see
 * YNI-maker 2013. 5. 8. Initial version
 *
 */
public class YniAbstractMultiDAO extends YniAbstractDAO {
	
	// 로그를 기록하기 위한 Log 인스턴스
	protected Log log = LogFactory.getLog(this.getClass());
    
    /**
	 * sqlMapClient 지정
	 * 
	 * @param org.springframework.orm.ibatis.SqlMapClientFactoryBean bean클래스
	 */
	@Resource(name = "sqlMapClient_rpt")
	public void setSuperSqlMapClient(SqlMapClient sqlMapClient) {
		if(log.isDebugEnabled()) log.debug("get setSuperSqlMapClient.... start");
		
		super.setSuperSqlMapClient(sqlMapClient);
	}

}
