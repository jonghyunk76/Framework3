package kr.yni.frame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.Constants;
import kr.yni.frame.util.FileUtil;
import kr.yni.frame.util.StringHelper;

/**
 * 압축된 ZIP파일과 관련된 기능을 제공하는 클래스
 * @author YNI-maker
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ZipUtil {
	
	 private static final Log log = LogFactory.getLog(ZipUtil.class);

	/**
     * <p>
     * 파일생성 정보를 저장할 Keys
     * <p>
     */
    public final static String ORIGINAL_FILE_NAME = "ORIGINAL_FILE_NAME";
	public final static String ORIGINAL_FILE_EXTENSION = "ORIGINAL_FILE_EXTENSION";
	public final static String NEW_FILE_NAME = "NEW_FILE_NAME";
	public final static String FILE_PATH = "FILE_PATH";
	public final static String DOWNLOAD_URL = "DOWNLOAD_URL";
	public final static String FILE_SIZE = "FILE_SIZE";
	
	/**
	 * 압출을 푼다.
	 * @param fileMap ZIP파일이 담겨있는 경로
	 * @return List   압축된 파일을 풀어 임시 디렉토리에 저장된 파일목록
	 * @throws Exception
	 */
    public static List unZip(Map fileMap) throws Exception {
    	List files = new ArrayList();
    	ZipInputStream in = null;
    	
    	String filePath =  FileUtil.getFullPath(null);  // Full 경로(C:/work/CM/workspace/CMMs_LGE/webapp/upload/Temp/)
    	String workPath = FileUtil.getWorkPath(null);   // Application Context 실제경로를 나머지 경로(/upload/Temp/)
    	
    	try {
    		String zipPath = Constants.APPLICATION_REAL_PATH + StringHelper.null2void(fileMap.get("DOWNLOAD_URL")); // 파일이 저장된 위치
    		
    		log.debug("file encoding = " + SystemHelper.getSystemProperty("file.encoding"));
    		
	        in = new ZipInputStream(new FileInputStream(zipPath));
	        ZipEntry entry = null;
	        
	        while ((entry = in.getNextEntry()) != null) {
	        	if(!entry.isDirectory()) {
	        		String extensionName = "";
		        	String fileName = entry.getName();
		        	
		        	log.debug("file name is " + fileName + " in zip");
		        	
		        	if(fileName.lastIndexOf(".") > -1) {
		        		extensionName = fileName.substring(fileName.lastIndexOf("."));
		        	}
		        	
		        	File newFile = null;
		        	String newFileName = null;
		    		String newFilePath = null;
		    		Integer dupCount = 0;
		    		String dupName = null;
		    		
		        	// unique한 파일명을 생성
		    		while(true) {
		    			dupName = (dupCount++ == 0) ? "" : "(" + dupCount.toString() + ")";
		    			
		    			newFileName = UUID.randomUUID().toString() + dupName + extensionName; 
		    			
		    			newFilePath = filePath + newFileName;
		    			newFile = new File(newFilePath);
		    			
		    			if (!newFile.exists()) {
		    				break;
		    			}
		    		}
		    		
		            OutputStream out = new FileOutputStream(newFilePath);
		            
		            byte[] buf = new byte[1024];
		            int len;
		            while ((len = in.read(buf)) > 0) {
		                out.write(buf, 0, len);
		            }
		
		            if(in != null) in.closeEntry();
		            if(out != null) out.close();
		            
		            Map map = new LinkedHashMap();
		            String downloadUrl = workPath + newFileName;
		            
		            map.put(ORIGINAL_FILE_NAME, fileName);
		    		map.put(NEW_FILE_NAME, newFileName);
		    		map.put(FILE_PATH, workPath);
		    		map.put(DOWNLOAD_URL, downloadUrl);
		    		map.put(ORIGINAL_FILE_EXTENSION, extensionName);
		    		map.put("FILE_SIZE", Long.toString(newFile.length()));
		    		
		    		files.add(map);
	        	}
	        }
	        // 압축해제된 zip파일은 삭제한다.
	        File zipFile = new File(zipPath);
	        
	        if(zipFile.exists()) {
	        	zipFile.delete();
	        }
	    } catch (IOException e) {
	    	throw e;
	    } finally {
	    	if(in != null) in.close();
	    }
    	
    	return files;
	}
}
