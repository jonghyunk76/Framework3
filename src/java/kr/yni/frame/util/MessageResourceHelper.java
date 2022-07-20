package kr.yni.frame.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.Constants;
import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorFactory;
import kr.yni.frame.exception.FrameException;

/**
 * 메시지 리소스를 생성하기 위한 도움을 주는 클래스
 * 
 * @author YNI-maker
 *
 */
public class MessageResourceHelper {
	
	private static final Log log = LogFactory.getLog(MessageResourceHelper.class);
	
	// 다국어 분류
	private static String[] categoryList = { "KOR", "ENG", "LOC" };
	
	// javascript 에서 사용할 메시지 파일 경로
	private static String JS_MESSAGE_PATH;
	
	// java 에서 사용할 메시지 파일 경로
	private static String PROP_MESSAGE_PATH;
	
	static {
		configure();
	}
	
	public static void configure() {
		try {
			Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			
			JS_MESSAGE_PATH = Constants.APPLICATION_REAL_PATH.concat(configurator.getString("js.message.dir"));
			
			PROP_MESSAGE_PATH = Constants.APPLICATION_REAL_PATH.concat(configurator.getString("prop.message.dir"));
			
		} catch (Exception ex) {
			if (log.isErrorEnabled()) { log.error("Exception : " + ex); }
		}
	}
	
	/**
	 * <p>
	 * 적용할 언어의 종류를 설정한다.<br>
	 * 기본설정<br>
	 * String[] categoryList = { "KOR", "ENG", "LOC" };
	 * </p>
	 * 
	 * @param category
	 */
	public static void setMessageCategory(String[] category) {
		categoryList = category;
	}
	
	/**
	 * Message Category 종류에 따라 javascript 파일을 생성한다. 
	 * 
	 * @param list
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void generateJsMessageFile(List<LinkedHashMap<String, Object>> list) throws Exception {
		BufferedWriter out = null;
		StringBuffer variableName = null;
		
		try {
			FileUtil.makeDirectrory(JS_MESSAGE_PATH);
			
			for(int i = 0; i < categoryList.length; i++) {
				String filePath = JS_MESSAGE_PATH + "/message_" + categoryList[i] + ".js";
				
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), Constants.APPLICATION_CONTEXT_CHARSET));
				
				for (int j = 0; j < list.size(); j++) {
					variableName = new StringBuffer();
					LinkedHashMap msgMap = (LinkedHashMap) list.get(j);
					
					if(categoryList[i].equals(StringHelper.null2void(msgMap.get(Constants.COLUMN_MESSAGE_LENG)))) {
						variableName.append("var ");
						variableName.append(StringHelper.null2void(msgMap.get(Constants.COLUMN_MESSAGE_CODE)));
						variableName.append(" = \"");
						variableName.append(StringHelper.null2void(msgMap.get(Constants.COLUMN_MESSAGE_NAME)).replace("\"", "\\\""));
						variableName.append("\";");
	
						// 메시지 내용 등록
						out.write(variableName.toString());
						out.newLine();
					}
				}
				
				out.flush();
				
				if(log.isDebugEnabled()) log.debug("created message file(path=" + filePath + ")");
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			throw new FrameException("MSG_MSGFILE_CREATE_ERR", null, null);
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new FrameException("MSG_MSGFILE_CREATE_ERR", null, null);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	/**
	 * Message Category 종류에 따라 properties 파일을 생성한다. 
	 * 
	 * @param list
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void generatePropertiesMessageFile(List<LinkedHashMap<String, Object>> list) throws Exception {
		BufferedWriter out = null;
		StringBuffer variableName = null;
		
		try {
			configure();
			if(PROP_MESSAGE_PATH == null) {
				log.debug(PROP_MESSAGE_PATH  + "is null");
			} else {
				log.debug(PROP_MESSAGE_PATH + "is not null");
			}
			
			FileUtil.makeDirectrory(PROP_MESSAGE_PATH);
			
			log.debug(PROP_MESSAGE_PATH);
			
			for(int i = 0; i < categoryList.length; i++) {
				String locale = "_kr";
				String category = categoryList[i];
				
				if (category.equals("EN")) {
					locale = "_" + Locale.ENGLISH.getLanguage();
				} else if (category.equals("KR")) {
					locale = "_kr";
				} else if (category.equals("KO")) {
					locale = "_ko";
					category = "KR";
				} else if (category.equals("ES")) {
					locale = "_es";
				} else if (category.equals("VN")) {
					locale = "_vi";
				} else {
					locale = "_" + Locale.getDefault().getLanguage();
				}
				
				String filePath = PROP_MESSAGE_PATH + "/message"+ locale + ".properties";
				
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), Constants.APPLICATION_CONTEXT_CHARSET));
				
				for (int j = 0; j < list.size(); j++) {
					variableName = new StringBuffer();
					LinkedHashMap msgMap = (LinkedHashMap) list.get(j);

					if(category.equals(StringHelper.null2void(msgMap.get(Constants.COLUMN_MESSAGE_LENG)))) {
						variableName.append(StringHelper.null2void(msgMap.get(Constants.COLUMN_MESSAGE_CODE)));
						variableName.append(" = ");
						variableName.append(StringHelper.null2void(msgMap.get(Constants.COLUMN_MESSAGE_NAME)).replace("\"", "\\\""));
						
						// 메시지 내용 등록
						out.write(variableName.toString());
						out.newLine();
					}
				}
				
				out.flush();
				
				if(log.isDebugEnabled()) log.debug("created message file(path=" + filePath + ")");
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			throw new FrameException("MSG_MSGFILE_CREATE_ERR", null, null);
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new FrameException("MSG_MSGFILE_CREATE_ERR", null, null);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}
