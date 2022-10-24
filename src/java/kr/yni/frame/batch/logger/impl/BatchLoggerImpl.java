package kr.yni.frame.batch.logger.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import kr.yni.frame.Constants;
import kr.yni.frame.batch.logger.BatchLogger;
import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorFactory;

/**
 * <p>
 * 인터페이스 스케쥴 및 배치 실행중에 발생하는 로그를 파일로 기록하는 클래스
 * </p>
 * <br>
 * BatchLogger 사용 시 logStart() > logMessage() > logEnd() 순으로 호출되어야 함
 * 
 * @author YNI-maker
 *
 */
public class BatchLoggerImpl implements BatchLogger {

	private static Log log = LogFactory.getLog(BatchLoggerImpl.class);

	private static String filePath = null;
	
	private static String serverKey = null;

	private JobExecutionContext context = null;
	
	private BufferedWriter write = null;

	private String target = null;

	private String transID = null;

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
			
			filePath = configurator.getString("batch.log.path");
			serverKey = configurator.getString("application.license.key");
			
			if(log.isDebugEnabled()) {
				log.debug("log file path=" + filePath);
			}
		} catch (Exception ex) {
			if(log.isErrorEnabled()) {
				log.error("exception: \n" + ex);
			}
		}
	}

	public void setContext(JobExecutionContext ctx) {
		this.context = ctx;
	}

	public JobExecutionContext getContext() {
		return this.context;
	}

	public void setTransactionID(String id) {
		this.transID = id;
	}
	
	/**
	 * 로그의 시작 기록
	 */
	@SuppressWarnings("rawtypes")
	public void logStart(String batchTarget, List<Object> args)
			throws Exception {
		StringBuffer buf = new StringBuffer();
		target = batchTarget;

		if(args != null) {
			buf.append("["+serverKey+"] [ dataSet = ");
			for (int i = 0; i < args.size(); i++) {
				if(args.get(i) instanceof Map) {
					Map map = (Map) args.get(i);
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						buf.append(entry.getKey());
						buf.append("=");
						buf.append(entry.getValue());
						buf.append(" ");
					}
				} else {
					buf.append(args.get(i));
					buf.append(" ");
				}
			}
			buf.append("]");
		} else {
			transID = null;
			buf.append("stat new batch");
		}

		this.logMessage(buf.toString());
	}

	@SuppressWarnings("rawtypes")
	public void logMessage(String message, List<Object> args) throws Exception {
		StringBuffer buf = new StringBuffer();

		buf.append("["+serverKey+"] [");
		buf.append(message);
		buf.append(" = ");
		for (int i = 0; i < args.size(); i++) {
			if(args.get(i) instanceof Map) {
				Map map = (Map) args.get(i);
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					buf.append(entry.getKey());
					buf.append("=");
					buf.append(entry.getValue());
					buf.append(" ");
				}
			} else {
				buf.append(args.get(i));
				buf.append(" ");
			}
		}
		buf.append("]");

		this.logMessage(buf.toString());
	}

	public void logMessage(String message) throws Exception {
		StringBuffer buf = new StringBuffer();

		buf.append("["+serverKey+"] [");
		buf.append(this.getDateFormat("yyyy.MM.dd HH:mm:ss"));
		buf.append("] ");

		if(transID != null) {
			buf.append("[");
			buf.append(transID);
			buf.append("] ");
		}

		if(target != null) {
			buf.append("[target-");
			buf.append(target);
			buf.append("] ");
		}

		buf.append(message);

		if(write == null) {
			this.write = (BufferedWriter) this.getOutFileWriter(null);
		}

		write.append(buf.toString());
		write.newLine();
		write.flush();

		if(log.isDebugEnabled()) {
			log.debug("write message : " + message);
		}
	}

	private Writer getOutFileWriter(String outFileName) throws Exception {
		File file = new File(filePath);
		if(!file.exists()) {
			file.mkdir();
		}
		String fileName = outFileName;
		if(fileName == null) {
			fileName = "batch_" + getDateFormat("yyyyMMdd") + ".log";
		}
		file = new File(filePath + fileName);

		FileWriter file_write = new FileWriter(file, true);
		write = new BufferedWriter(file_write);

		return this.write;
	}

	private void closeOutFileWriter(String outFileName) {
		try {
			if(this.write != null) {
				write.close();
				write = null;
			}
		} catch (IOException io) {
			try {
				this.logMessage("[ERROR] " + io.getCause().toString());
			} catch (Exception exp) {
				if(log.isErrorEnabled()) {
					log.error(exp);
				}
			}
		}
	}

	private String getDateFormat(String format) {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		return sdf.format(date);
	}

	public void logEnd(boolean succ, String message) throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("["+serverKey+"] [result=");
		if(succ) {
			buf.append("successed");
		} else {
			buf.append("failed");
		}
		buf.append("]");
		buf.append(message);
		
		if(this.write != null) {
			this.logMessage(buf.toString());
		}

		this.closeOutFileWriter(null);
	}

	public void logEnd(boolean succ) throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("["+serverKey+"] [result=");
		if(succ) {
			buf.append("successed");
		} else {
			buf.append("failed");
		}
		buf.append("]");
		
		if(this.write != null) {
			this.logMessage(buf.toString());
		}
		this.closeOutFileWriter(null);
	}

	public void setStatus(String key, Object status) {
		// TODO Auto-generated method stub

	}

	public Object getStatus(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getStatus(String key, Object defValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public void logStatus() throws Exception {
		// TODO Auto-generated method stub

	}
}