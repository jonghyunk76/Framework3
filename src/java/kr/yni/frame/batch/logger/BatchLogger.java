package kr.yni.frame.batch.logger;

import java.util.List;

import org.quartz.JobExecutionContext;

/**
 * BatchLogger 인터페이스
 * 
 * @author YNI-maker
 *
 */
public interface BatchLogger {

	/**
	 * 배치로그를 남기기 위해 필요한 ServiceContext객체 설정
	 * 
	 * @param ctx
	 */
	public void setContext(JobExecutionContext ctx);

	/**
	 * 설정된 Service객체를 반환
	 * 
	 * @return
	 */
	public JobExecutionContext getContext();

	/**
	 * 트랜잭션 아이디 셋팅
	 * 
	 * @param id
	 */
	public void setTransactionID(String id);

	/**
	 * 배치 프로그램을 시작할 때 로그를 남긴다.
	 * 
	 * @param batchTarget
	 *            실행될 배치 클래스명 또는 배치서비스명
	 * @param args
	 */
	public void logStart(String batchTarget, List<Object> args)
			throws Exception;

	/**
	 * 배치 프로그램을 종료할 때 로그를 남긴다.
	 * 
	 * @param succ
	 *            성공여부
	 * @param message
	 *            메시지
	 * @throws Exception
	 */
	public void logEnd(boolean succ, String message) throws Exception;

	/**
	 * 배치 프로그램을 종료할 때 로그를 남긴다.
	 * 
	 * @param succ
	 *            성공여부
	 * @throws Exception
	 */
	public void logEnd(boolean succ) throws Exception;

	/**
	 * 배치프로그램 수행중 오류를 남긴다.
	 * 
	 * @param message
	 *            메시지
	 * @throws Exception
	 */
	public void logMessage(String message) throws Exception;

	public void logMessage(String message, List<Object> args) throws Exception;

	/**
	 * 배치 프로그램 현재 수행 상태를 설정한다.
	 * 
	 * @param key
	 *            상태 명
	 * @param status
	 *            상태 값
	 */
	public void setStatus(String key, Object status);

	/**
	 * 배치 프로그램의 현재 수행 상태 값을 가져온다.
	 * 
	 * @param key
	 * @return key에 대한 상태 값
	 */
	public Object getStatus(String key);

	/**
	 * 배치 프로그램의 현재 수행 상태 값을 가져온다.
	 * 
	 * @param key
	 * @param defValue
	 * @return key에 대한 상태 값(없으면 defValue)
	 */
	public Object getStatus(String key, Object defValue);

	/**
	 * 배치 프로그램의 현재 수행 상태로 설정된 값들을 로그로 남긴다.
	 * 
	 * @throws LiveException
	 */
	public void logStatus() throws Exception;
}
