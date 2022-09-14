package kr.yni.frame.mapper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.mapper.element.Jco;
import kr.yni.frame.pool.JcoPoolManager;

/**
 * <p>
 * RFC 인터페이스 정보를 가지는 XML를 식별하여 로드하기 위한 클래스
 * </p>
 * 
 * @author 김종현 (ador218@bluesolution.co.kr)
 * 
 */
public class ParamReader {
	private static Log log = LogFactory.getLog(ParamReader.class);

	private static Map<String, Jco> jcoMap = new LinkedHashMap<String, Jco>();

	/**
	 * <p>
	 * JCO Map정보를 획득하기 위한 매소드
	 * </p>
	 * @throws Exception
	 */
	private static void readJcoMap() throws Exception {
		List<File> fileList = new ArrayList<File>();

		try {
			String path = JcoPoolManager.getJcoMapPath();

			if (log.isDebugEnabled()) {
				log.debug("readJcoMap() file path=" + path);
			}

			if (path != null) {
				File file = new File(path);

				// get file list
				if (file.exists()) {
					if (file.isDirectory()) {
						String[] files = file.list(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.endsWith(".xml");
							}
						});

						for (int i = 0; i < files.length; i++) {
							fileList.add(new File(path + "/" + files[i]));
							if (log.isDebugEnabled()) {
								log.debug(files[i]);
							}
						}
					}
					if (file.isFile()) {
						fileList.add(file);
					}
				} else {
					if (log.isErrorEnabled()) {
						log.error("not exists file.");
					}
				}

				if (log.isDebugEnabled()) {
					log.debug("JcoMap File Size=" + fileList.size());
				}

				for (int j = 0; j < fileList.size(); j++) {
					ParamParser parser = new ParamParser();

					Jco jco = parser.parse(fileList.get(j));
					if (log.isDebugEnabled()) {
						log.debug("JCO=" + jco.toString());
					}

					if (jcoMap.get(jco.getId()) != null) {
						jcoMap.remove(jco.getId());
					}

					jcoMap.put(jco.getId(), jco);
				}
			}
		} catch (Exception exp) {
			if (log.isErrorEnabled()) {
				log.error("readJcoMap() : " + exp);
			}
			throw exp;
		}
	}
	
	/**
	 * 해당 file의 xml를 리로딩하고 파싱된 Jco를 리턴한다.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Jco readJcoMap(File file) throws Exception {
		String jcoName = "";
		try {
			ParamParser parser = new ParamParser();
	
			Jco jco = parser.parse(file);
			
			jcoName = jco.getId();
			
			if (log.isDebugEnabled()) {
				log.debug("JCO name=" + jcoName + ", info=" + jco.toString());
			}
			
			if (jcoMap.get(jcoName) != null) {
				jcoMap.remove(jcoName);
			}
	
			jcoMap.put(jcoName, jco);
		} catch (Exception exp) {
			if (log.isErrorEnabled()) {
				log.error("readJcoMap() : " + exp);
			}
			throw exp;
		}
		
		return jcoMap.get(jcoName);
	}
	
	/**
	 * Sap 파라메터 정보를 찾아 리턴하는 매소드
	 * 
	 * @param name
	 *            jso ID
	 * @return
	 */
	public static Jco getJcoParameter(String name) throws Exception {
		Jco jco = jcoMap.get(name);

		try {
			if (name == null) return null;

			if (jco == null) readJcoMap();
		} catch (Exception exp) {
			if (log.isErrorEnabled()) {
				log.error("ParamReader() : " + exp);
			}
			throw exp;
		}
		
		return jcoMap.get(name);
	}
	
	@SuppressWarnings("static-access")
	public Map<String, Jco> getJcoMap() {
		return this.jcoMap;
	}
}

