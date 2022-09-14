package kr.yni.frame.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import kr.yni.frame.Constants;
import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorException;
import kr.yni.frame.config.ConfiguratorFactory;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.reader.FileReader;
import kr.yni.frame.reader.type.ExcelHSSFReader;
import kr.yni.frame.reader.type.ExcelSSReader;
import kr.yni.frame.reader.type.ExcelXSSFReader;
import kr.yni.frame.reader.type.XmlReader;
import kr.yni.frame.web.upload.FormFile;

/**
 * 파일 탐색 기능을 제공하는 클래스이다.
 * 
 * @author YNI-maker
 *
 */
public class FileUtil {
	
    /**
     * <p>
     * 에러나 이벤트와 관련된 각종 메시지를 로깅하기 위한 Log 오브젝트
     * </p>
     */
    private static final Log log = LogFactory.getLog(FileUtil.class);

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
     * <p>
     * 지정한 확장자들만 가져올 수 있도록 하기 위한 FilenameFilter의 구현 클래스
     * </p>
     */
    private static class FileExtensionFilter implements FilenameFilter {

        String acceptableExtensions[];

        FileExtensionFilter(String ext[]) {
            acceptableExtensions = ext;
        }

        public boolean accept(File dir, String fname) {
            if (acceptableExtensions == null) {
                return true;
            }

            // check if the extension of the file is in the ecceptable list.
            for (int i = 0; i < acceptableExtensions.length; i++) {
                if (fname.endsWith(acceptableExtensions[i])) {
                    return true;
                }
            }

            // if the file is a directory, then return true.
            File tempFile = new File(dir, fname);
            return tempFile.isDirectory();
        }
    }

    /**
     * <p>
     * 지정된 패턴에 해당되는 파일명으로 검색하기 위한 FilenameFilter의 구현 클래스
     * </p>
     */
    private static class FilenamePatternFilter implements FilenameFilter {

    	List<Pattern> patternList = new ArrayList<Pattern>();

    	FilenamePatternFilter(String patternStr) {
    		if (patternStr != null) {
    			patternList.add(Pattern.compile(patternStr));
    		}
    	}
    	
        FilenamePatternFilter(String[] patternStr) {
        	if (patternStr == null) {
        		return;
        	}
        	
            for(int i=0;i<patternStr.length;i++) {
            	patternList.add(Pattern.compile(patternStr[i]));
            }
        }

        public boolean accept(File dir, String fname) {

            // check if the extension of the file is in the ecceptable list.
            for (int i=0;i<patternList.size();i++) {
            	if (patternList.get(i).matcher(fname).matches()) {
            		return true;
            	}
            }

            // if the file is a directory, then return true.
            File tempFile = new File(dir, fname);
            return tempFile.isDirectory();
        }
    }
 
    /**
     * 여러 개의 파일을 하나의 파일로 합치는 기능을 제공한다.
     * @param targetName
     * @param sourceNames
     * @param 파일 합친 후 소스 파일 삭제 여부
     * @throws IOException 
     */
    public static void mergeFile(String targetName, String[] sourceNames, boolean deleteSource) throws IOException {
    	File targetFile = new File(targetName);
    	FileOutputStream fos = new FileOutputStream(targetFile);
    	
    	File sourceFile = null;
    	FileInputStream fis = null;
    	int len = 0;
    	byte[] buf = new byte[1024*1024];
    	
    	try {
	    	for(int i=0;i<sourceNames.length;i++) {
	    		sourceFile = new File(sourceNames[i]);
	    		fis = new FileInputStream(sourceFile);
	    		while((len=fis.read(buf))>0) {
	    			fos.write(buf,0,len);
	    		}
	    		fis.close();
	    		sourceFile.delete();
	    	}
    	} finally {
    		fos.close();
    	}
    }
    
    
    /**
     * 주어진 dir 하위의 내용을 모두 삭제한다.
     * @param dir 삭제할 디렉토리
     * @param include dir도 삭제할지 여부
     * @return
     */
    public static boolean deleteDirectory(File dir, boolean include) {
    	if (dir.exists()) {
    		File[] files = dir.listFiles();
    		if (files != null) {
    			for(int i=0;i<files.length;i++) {
	    			if (files[i].isDirectory()) {
	    				deleteDirectory(files[i],true);
	    			} else {
	    				files[i].delete();
	    			}
	    		}
    		}
    		if (include) {
        		return dir.delete();
        	} else {
        		return true;
        	}
    	} else {
    		return true;
    	}
    }
    
    /**
     * <p> 지정한 디렉토리와 그 하위 디렉토리 내에 있는 파일들의 목록을 이름으로 가져온다.
     *
     * @param rootDir       시작 디렉토리 path 문자열
     * @param ext           가져올 파일들의 확장자 목록을 지정한 String 배열
     * @param excludingRoot true이면 파일이름의 Path명에서 rootFile의 Path명을 제외한다.
     * @return 파일들의 이름 리스트
     */
    public static String[] getFilenamesUnder(String rootDir, String ext[], boolean excludingRoot) {
        List<String> filenames = new ArrayList<String>();
        File rootFile = null;

        if (rootDir == null) {
            rootDir = ".";
        }
        rootFile = new File(rootDir);

        String rootPathString = rootFile.getPath();
        File tempFile = null;
        String tempString = null;

        for (Iterator<File> i = getAllFilesUnder(rootFile, new FileExtensionFilter(ext)).iterator();
             i.hasNext();) {
            tempFile = (File) i.next();
            tempString = tempFile.getPath();
            if (excludingRoot) {
                tempString = tempString.substring(rootPathString.length() + 1, tempString.length());
            }
            if (tempString.startsWith(File.separator)) {
                tempString = tempString.substring(1);
            }

            filenames.add(tempString);
        }

        return (String[]) filenames.toArray(new String[0]);
    }

    /**
     * <p> 지정한 디렉토리와 그 하위 디렉토리 내에 있는 클래스 파일들을 찾아서 그 클래스명을
     * 가져온다. <code>pkgPrefix</code>를 지정하면 클래스명의 패키지 구조 앞에 <code>pkgPrefix</code>가
     * 붙어서 클래스명이 생성된다.
     * </p>
     *
     * @param rootDir       시작 디렉토리 path 문자열
     * @param pkgPrefix     공통 패키지 명
     * @param excludingRoot true이면 클래스의 패키지명 시작을 rootDir의 위치를 기준으로 생성한다.
     * @return 검색된 클래스 명 리스트
     */
    public static String[] getClassnamesUnder(String rootDir, String pkgPrefix, boolean excludingRoot) {

        List<String> classnames = new ArrayList<String>();
        File rootFile = null;

        if (rootDir == null) {
            rootDir = ".";
        }
        rootFile = new File(rootDir);

        String rootPathString = rootFile.getPath();
        File tempFile = null;
        String tempString = null;

        for (Iterator<File> i = getAllFilesUnder(rootFile, new FileExtensionFilter(new String[]{".class"})).iterator();
             i.hasNext();) {
            tempFile = (File) i.next();
            tempString = tempFile.getPath();
            if (excludingRoot) {
                tempString = tempString.substring(rootPathString.length(), tempString.length() - 6);
            } else {
                tempString = tempString.substring(0, tempString.length() - 6);
            }
            tempString = tempString.replace(File.separatorChar, '.');
            if (pkgPrefix != null) {
                tempString = pkgPrefix.concat(tempString);
            }
            if (tempString.startsWith(".")) {
                tempString = tempString.substring(1);
            }
            classnames.add(tempString);
        }

        return (String[]) classnames.toArray(new String[0]);
    }

    /**
     * 지정한 디렉토리들에 대하여 각 디렉토리와 그 하위 디렉토리 내에 있는 파일들중에서 
     * 주어진 확장자를 가진 파일들의 목록을 가져온다.
     *
     * @param rootDir 시작 디렉토리 path 들
     * @param pattern 가져올 파일들의 파일명 패턴 목록(regexp)
     * @param subDir 하위 디렉토리 탐색 여부
     * @return 파일 목록을 담은 Set 객체, 디렉토리가 존재하지 않으면 null
     */
    public static Set<File> getFilesWithExtension(File[] rootDir, String[] ext, boolean subDir) {
    	FilenameFilter filter = new FileExtensionFilter(ext);
        TreeSet<File> set = new TreeSet<File>();
        
        for(int i=0;i<rootDir.length;i++) {
        	set.addAll(getAllFilesUnder(rootDir[i],filter,subDir));
        }
       
        return set;
    }
    
    /**
     * <p> 지정한 디렉토리와 그 하위 디렉토리 내에 있는 파일들중에서 주어진 확장자를 가진 파일들의 목록을 가져온다.
     *
     * @param rootDir 시작 디렉토리 path
     * @param pattern 가져올 파일들의 파일명 패턴 목록(regexp)
     * @param subDir 하위 디렉토리 탐색 여부
     * @return 파일 목록을 담은 Set 객체, 디렉토리가 존재하지 않으면 null
     */
    public static Set<File> getFilesWithExtension(File rootDir, String[] ext, boolean subDir) {
    	FilenameFilter filter = new FileExtensionFilter(ext);
    	
    	return getAllFilesUnder(rootDir,filter,subDir);
    }
    
    /**
     * 지정한 디렉토리들에 대하여 각 디렉토리와 그 하위 디렉토리 내에 있는 파일들중에서 
     * 주어진 패턴들에 매치되는 파일들의 목록을 가져온다.
     *
     * @param rootDir 시작 디렉토리 path 들
     * @param pattern 가져올 파일들의 파일명 패턴 목록(regexp)
     * @param subDir 하위 디렉토리 탐색 여부
     * @return 파일 목록을 담은 Set 객체, 디렉토리가 존재하지 않으면 empty
     */
    public static Set<File> getFilesOfPattern(File[] rootDir, String[] pattern, boolean subDir) {
    	FilenameFilter filter = new FilenamePatternFilter(pattern);
        TreeSet<File> set = new TreeSet<File>();
        
        for(int i=0;i<rootDir.length;i++) {
        	set.addAll(getAllFilesUnder(rootDir[i],filter,subDir));
        }
       
        return set;
    }
    
    /**
     * 지정한 디렉토리들에 대하여 각 디렉토리와 그 하위 디렉토리 내에 있는 파일들 중에서
     * 주어진 패턴들에 매치되는 파일들의 목록을 가져온다. 
     * 파일들을 가져오는 순서는 pattern 순서대로 가져오도록 보장한다.
     * @param rootDir 시작 디렉토리 path 들
     * @param pattern 가져올 파일들의 파일명 패턴 목록(regexp)
     * @param subDir 하위 디렉토리 탐색 여부
     * @return 파일 목록을 담은 List 객체, 디렉토리가 존재하지 않으면 empty
     */
    public static List<File> getFilesOfPatternAsPatternOrder(File[] rootDir, String[] pattern, boolean subDir) {
    	List<File> list = new ArrayList<File>();
    	
    	for(int i=0;i<pattern.length;i++) {
    		FilenameFilter filter = new FilenamePatternFilter(pattern[i]);
    		for(int j=0;j<rootDir.length;j++) {
    			list.addAll(getAllFilesUnder(rootDir[j],filter,subDir));
    		}
    	}
    	
    	return list;
    }
    
    /**
     * <p> 지정한 디렉토리와 그 하위 디렉토리 내에 있는 파일들중에서 주어진 패턴들에 매치되는 파일들의 목록을 가져온다.
     *
     * @param rootDir 시작 디렉토리 path
     * @param pattern 가져올 파일들의 파일명 패턴 목록(regexp)
     * @param subDir 하위 디렉토리 탐색 여부
     * @return 파일 목록을 담은 Set 객체, 디렉토리가 존재하지 않으면 null
     */
    public static Set<File> getFilesOfPattern(File rootDir, String[] pattern, boolean subDir) {
    	FilenameFilter filter = new FilenamePatternFilter(pattern);
    	
    	return getAllFilesUnder(rootDir,filter,subDir);
    }
    
    /**
     * <p> 지정한 디렉토리와 그 하위 디렉토리 내에 있는 파일들의 목록을 가져온다.
     *
     * @param rootDir 시작 디렉토리 path
     * @param filter 가져올 파일들의 확장자 목록을 지정한 필터 객체
     * @return 파일 목록을 담은 Set 객체, 디렉토리가 존재하지 않으면 null
     */
    public static Set<File> getAllFilesUnder(File rootDir, FilenameFilter filter) {
    	return getAllFilesUnder(rootDir,filter,true);
    }
    
    /**
     * <p> 지정한 디렉토리와 그 하위 디렉토리 내에 있는 파일들의 목록을 가져온다.
     *
     * @param rootDir 시작 디렉토리 path
     * @param filter 가져올 파일들의 확장자 목록을 지정한 필터 객체
     * @param subDir 하위 디렉토리 탐색 여부
     * @return 파일 목록을 담은 Set 객체, 디렉토리가 존재하지 않으면 null
     */
    public static Set<File> getAllFilesUnder(File rootDir, FilenameFilter filter, boolean subDir) {
        TreeSet<File> set = new TreeSet<File>();
        
        if (rootDir.exists() && rootDir.isDirectory()) {
            getFilesIn(rootDir, filter, set, false, subDir);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Directory does not exist. : " + rootDir.getPath());
            }
        }

        return set;
    }

    /**
     * <p> 지정한 Directory 내에 있는 파일들의 목록을 가져온다.
     *
     * @param rootFile      시작 디렉토리 path
     * @param filter        가져올 파일들의 확장자 목록을 지정한 필터 객체
     * @param set           가져온 파일들을 저장할 Set 객체
     * @param dirFlag       디렉토리도 가져오고자 하면 true로 입력
     * @param recursiveFlag 하위의 디렉토리도 계속적으로 탐색하고자 하면 true로 입력
     */
    private static void getFilesIn(File rootFile, FilenameFilter filter, Set<File> set, boolean dirFlag, boolean recursiveFlag) {
        File fileList[] = rootFile.listFiles(filter);

        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                if (recursiveFlag) {
                    getFilesIn(fileList[i], filter, set, dirFlag, recursiveFlag);
                }
                if (dirFlag) {
                    set.add(fileList[i]);
                }
            } else {
                set.add(fileList[i]);
            }
        }
    }
    
    /**
     * path에 맞게 디렉토리 생성
     * 
     * @param path
     */
    public static void makeDirectrory(String path) {
    	File file = new File(path);

		if (!file.exists()) {
			file.mkdir();
		}
    }
    
    /**
	 * <p>
	 * 지정된 경로로 파일을 저장한다.
	 * <p>
	 * 
	 * @param  	저장할 기본경로 
	 * @param propName	파일타입 - properties 속성명
	 * @param file	저장할 파일(MultipartFile)
	 * @return 저장한 정보를 리턴한다.<br>
	 * 		map.put(ORIGINAL_FILE_NAME, originalFileFullName);<br>
	 * 		map.put(NEW_FILE_NAME, newFileFullName);<br>
	 * 		map.put(FILE_PATH, workPath + DateHelper.getSimpleDate("yyyyMM") + File.separator);<br>
	 * 		map.put(DOWNLOAD_URL, downloadUrl);<br>
	 * 		map.put(ORIGINAL_FILE_EXTENSION, originalExtension);<br>
	 * 		map.put("FILE_SIZE", ""+file.getFileSize());<br>
	 * 		<br>
	 * @throws Exception
	 */
	public static Map<String, String> transferTo(String propName, FormFile file) throws Exception {
		if (file == null) {
			if(log.isDebugEnabled()) log.debug("FormFile is null");
			
			throw new FrameException("This formFile can't found.");
		}
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		String filePath =  getFullPath(propName);
		String workPath = getWorkPath(propName);
		
		String originalFileName = file.getOriginalFilename(); 	// 실제파일명
		String originalExtension = file.getOriginalExtension(); // 확장자

		File newFile = null;
		String newFileName = null;
		String newFilePath = null;
		Integer dupCount = 0;
		String dupName = null;
		
		File dir = new File(filePath);

		// directory 없을경우
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new FrameException("Failed make directory(" + dir.getName() + ")");
			}
		}
		
		// unique한 파일명을 생성
		while(true) {
			dupName = (dupCount++ == 0) ? "" : "(" + dupCount.toString() + ")";
			
			newFileName = UUID.randomUUID().toString() + dupName + originalExtension; 
			
			newFilePath = filePath + newFileName;
			newFile = new File(newFilePath);
			
			if (!newFile.exists()) {
				break;
			}
		}

		File uploadFile = new File(newFilePath);
		String downloadUrl = workPath + newFileName;
		
		// 파일 생성
		file.write(uploadFile);
		
		file.setFileName(newFileName);
		
		map.put(ORIGINAL_FILE_NAME, originalFileName);
		map.put(NEW_FILE_NAME, newFileName);
		map.put(FILE_PATH, workPath);
		map.put(DOWNLOAD_URL, downloadUrl);
		map.put(ORIGINAL_FILE_EXTENSION, originalExtension);
		map.put("FILE_SIZE", Integer.toString(file.getFileSize()));

		return map;
	}	
	
	/**
	 * <p>
	 * 지정된 경로로 파일을 저장한다.
	 * <p>
	 * 
	 * @param file	저장할 파일(MultipartFile)
	 * @return 저장한 정보를 리턴한다.<br>
	 * 		map.put(ORIGINAL_FILE_NAME, originalFileFullName);<br>
	 * 		map.put(NEW_FILE_NAME, newFileFullName);<br>
	 * 		map.put(FILE_PATH, workPath + DateHelper.getSimpleDate("yyyyMM") + File.separator);<br>
	 * 		map.put(DOWNLOAD_URL, downloadUrl);<br>
	 * 		map.put(ORIGINAL_FILE_EXTENSION, originalExtension);<br>
	 * 		map.put("FILE_SIZE", ""+file.getFileSize());<br>
	 * 		<br>
	 * @throws Exception
	 */
	public static Map<String, String> transferTo(FormFile file) throws Exception {
		return transferTo(null, file);
	}
	
	/**
	 * <P>
	 *  경로 하위에서 파일타입에 속한 경로내에 등록된 파일을 삭제한다. 
	 * </P>
	 * 
	 * @param 
	 * @param propName 파일타입 디렉토리 - properties 속성명
	 * @param filename 삭제할 파일명
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteTo(String propName, String filename) throws Exception {
		String filePath = getFullPath(propName) + filename;

		File uploadedFile = new File(filePath);
		boolean deleted = uploadedFile.delete();
		
		if(log.isDebugEnabled()) log.debug("delete file = " + filePath + ", delete true/false = " + deleted);
		
		return deleted;
	}
	
	public static boolean deleteTo(ServletContext servletContext, String workType, String filename) throws Exception {
		if(log.isDebugEnabled()) log.debug(StringUtils.repeat("#", 10) + "FileUtils.transferTo start");

		if (workType.trim().isEmpty()) {
			workType = "file.common.dir";
		} else {
			workType = workType.trim();
		}

		String workPath;

		try {
			Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			
			workPath = configurator.getString(workType);
		} catch (Exception e) {
			return false;
		}

		String basePath = servletContext.getRealPath(workPath);
		String newFileFullPath = basePath + "/"+filename;

		if(log.isDebugEnabled()) log.debug(StringUtils.repeat("#", 10) + "newFileFullPath:" + newFileFullPath);

		File uploadedFile = new File(newFileFullPath);

		boolean deleted = uploadedFile.delete();

		if(log.isDebugEnabled()) log.debug(StringUtils.repeat("#", 10) + "deleted:" + deleted);

		return deleted;
	}

	/**
	 * 임시저장 폴더의 파일을 삭제한다.
	 * <삭제조건>
	 *   - 임시저장 폴더내 파일 중 생성일자가 지정된 일수가 지난 파일 삭제
	 * @param days 저장된 일수
	 * @return 파일 삭제수
	 */
	public static int deleteTempFile(int days) throws Exception {
		int ret = 0;
		String tempPath = getFullPath(null);
		File tempFile = new File(tempPath);
		
		File[] files = tempFile.listFiles();
		
		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			
			String lastDay = getLastModify(file, "D");
			String currentDay = DateHelper.getDay();
			int interval = DateHelper.getDays(lastDay, currentDay); // 두일자간의 일수를 구한다.
			
			if(interval >= days) {
				if(file.isFile()) {
					deleteTo(null, file.getName());
					
					if(log.isDebugEnabled()) log.debug("delete template file(name=" + tempPath + file.getName() + ", last modify=" + lastDay + ")");
				}
				
				ret++;
			}
		}
		
		return ret;
	}
	
	/**
	 * 요청한 파일을 삭제한다. 파라메터는 경로+파일명이 동시에 등록되어야 한다.
	 * 
	 * @param file 삭제할 파일(경로+파일명)
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteTo(String filename) throws Exception {
		return deleteTo(null, filename);
	}
	
	/**
	 * 파일이 마지막에 수정된 일자를 구한다. 
	 * @param file 파일객체
	 * @param type 리턴타입 : Y=yyyy, M=yyyymm, D=yyyymmdd, H=yyyymmddhh, MI=yyyymmddhhmi, S=yyyymmddhhmiss
	 * @throws Exception
	 */
	public static String getLastModify(File file, String type) throws Exception {
		Calendar cc = new GregorianCalendar();
        cc.setTimeInMillis(file.lastModified());
        
        String year = Integer.toString(cc.get(Calendar.YEAR));
        String month = ((cc.get(Calendar.MONTH)+1)<10) ? "0"+Integer.toString((cc.get(Calendar.MONTH)+1)) : Integer.toString((cc.get(Calendar.MONTH)+1));
        String day = (cc.get(Calendar.DAY_OF_MONTH)<10) ? "0"+Integer.toString(cc.get(Calendar.DAY_OF_MONTH)) : Integer.toString(cc.get(Calendar.DAY_OF_MONTH)); 
        String hour = (cc.get(Calendar.HOUR_OF_DAY)<10) ? "0"+Integer.toString(cc.get(Calendar.HOUR_OF_DAY)) : Integer.toString(cc.get(Calendar.HOUR_OF_DAY));
        String minute = (cc.get(Calendar.MINUTE)<10) ? "0"+Integer.toString(cc.get(Calendar.MINUTE)) : Integer.toString(cc.get(Calendar.MINUTE)); 
        String second = (cc.get(Calendar.SECOND)<10) ? "0"+Integer.toString(cc.get(Calendar.SECOND)) : Integer.toString(cc.get(Calendar.SECOND)); 
        
        if("Y".equals(type)) return year;
        else if("M".equals(type)) return year + month;
        else if("D".equals(type)) return year + month + day;
        else if("H".equals(type)) return year + month + day + hour;
        else if("MI".equals(type)) return year + month + day + hour + minute;
        else if("S".equals(type)) return year + month + day + hour + minute + second;
        else return year + month + day;
	}
	
	/**
	 * <p>
	 * 파일을 Byte로 변환한다.
	 * </p>
	 * 
	 * @param file 로딩할 파일
	 * @return
	 * @throws Exception
	 */
	public static byte[] getBytesFromFile(File file) throws Exception {
		InputStream is = null;
		byte[] bytes = null;
		
		try{
			is = new FileInputStream(file);
	
			long length = file.length();
			bytes = new byte[(int) length];
	
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
	
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file" + file.getName());
			}
			is.close();
		}catch(Exception e){
			throw e;
		}finally{
			if(is != null) is.close();
		}
		return bytes;
	}
	
	/**
	 * 임시저장 디렉토리의 절대경로를 리턴한다. 
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getFullPath() throws Exception {
		return getFullPath(null);
	}
	
	/**
	 * 디렉토리의 절대경로를 리턴한다.
	 * 
	 * @param propName
	 * @return
	 * @throws Exception
	 */
	public static String getFullPath(String propName) throws Exception {
		return Constants.APPLICATION_REAL_PATH + getWorkPath(propName);
	}
	
	/**
	 * properties파일에 지정된 work 디렉토리 경로를 리턴한다.
	 * 
	 * @param propName
	 * @return
	 * @throws Exception
	 */
	public static String getWorkPath(String propName) throws Exception {
		String path = null;
		
		try {
			Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			
			if(!StringHelper.isNull(propName)) {
				path = configurator.getString(propName);
			} else {
				path = configurator.getString("temp.dir");
			}
			
			if (log.isInfoEnabled()) log.info("work directory path = " + path);
		} catch(ConfiguratorException ce) {
			path = propName;
			
			if(log.isWarnEnabled()) {
				log.warn("The parameter not include .properties " + path);
			}
	    } catch(Exception e) {
			if(log.isErrorEnabled()) {
				log.error("FileUtils.transferTo workType resource was not found, workType = " + propName);
			}
			
			throw new FrameException(propName + "'s file not found in the [.properties]");
		}
		
		return path;
	}
	
	/**
	 * extension 에 해당하는 파일 목록을 구한다.
	 * @param dirPath 파일 경로
	 * @param extension 확장자
	 * @return
	 */
 	public static File[] getDirFileList(String dirPath, String extension) {
 		final String rExtension = extension;
 		
 		// 파일 목록을 요청한 디렉토리를 가지고 파일 객체를 생성함
 		File dir = new File(dirPath);
 		
 		File[] files = null;

 		if (dir.exists()) {
 			FilenameFilter ff = new FilenameFilter(){
				public boolean accept(File dir, String name) {
					
					return name.toLowerCase().endsWith(rExtension);
				}
 			};
 			files = dir.listFiles(ff);
 		}

 		return files;
 	}
	
    /**
     * XML 파일 생성
     * 
     * @param contents 생성할 XML 문서내용
     * @param outputFolder 디렉토리명
     * @param crateXmlFileName 파일명
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static void createXmlFile(List contents, String outputFolder, String crateXmlFileName) throws Exception {
    	FileOutputStream fos = null;
    	OutputStreamWriter writer = null;
    	
    	try {
    		File dir = new File(outputFolder);
	
			if(!dir.isDirectory()) {
			  dir.mkdirs();
			}
			
			fos = new FileOutputStream(outputFolder + "/" + crateXmlFileName);
			writer = new OutputStreamWriter(fos, Constants.APPLICATION_CONTEXT_CHARSET);
			
			writer.write("<?xml version=\"1.0\" encoding=\"" + Constants.APPLICATION_CONTEXT_CHARSET + "\"?>\r\n");
			
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    		
			outputter.output(contents, writer);
    	} catch(Exception exp) {
    		throw exp;
    	} finally {
    		try {
    			if(writer != null) writer.close();
    			if(fos != null) fos.close();
    		} catch(Exception ex) {
    			throw ex;
    		}
    	}
    }
    
    /**
     * 파일을 지정된 디렉토리로 이동(단, 이동전에 저장된 파일을 삭제한다.)
     * 
     * @param path 파일경로
     * @param fileName 파일명
     * @param movePath 이동파일경로
     * @throws Exception
     */
    public static boolean fileMove(String path, String fileName, String newPath) throws Exception{
    	boolean rst = true;
    	File file = new File(path + "/" + fileName);
    	
    	if(file.isFile()){
    		File dir = new File(newPath);
    		
    		if(!dir.isDirectory()) {
  			  dir.mkdirs();
  			}
    		
	    	rst = file.renameTo(new File(newPath + "/" + fileName));
	    	file.delete();
    	} else {
    		rst = false;
    	}
    	
    	return rst;
    }
    
    /**
     * 파일을 지정된 디렉토리로 이동시 새 파일명으로 변경(단, 이동전에 저장된 파일을 삭제한다.)
     * 
     * @param path 파일경로
     * @param fileName 파일명
     * @param movePath 이동파일경로
     * @param newFileName 이동 시 변경될 파일명
     * @return 파일이동 실패시 Fialed가 리턴되고, 성공시 저장된 새 파일명이 리턴됨
     * @throws Exception
     */
    public static String fileMove(String path, String fileName, String newPath, String newFileName) throws Exception{
    	boolean rst = true;
    	String newFileFullName = "";
    	File file = new File(path + "/" + fileName);
    	
    	if(file.isFile()){
    		File dir = new File(newPath);
    		
    		if(!dir.isDirectory()) {
  			  dir.mkdirs();
  			}
    		
    		String originalFileName = newFileName.substring(0, newFileName.lastIndexOf("."));
    		String originalExtension = newFileName.substring(newFileName.lastIndexOf("."));
    		
    		
    		Integer dupCount = 0;
    		String dupName;

    		while (true) {
    			dupName = (dupCount++ == 0) ? "" : "(" + dupCount.toString() + ")";
    			newFileFullName = originalFileName + dupName + originalExtension;
    			String newFileFullPath = newPath + newFileFullName;
    			
    			File newFile = new File(newFileFullPath);
    			
    			if (!newFile.exists()) {
    				break;
    			}
    		}
    		
    		rst = file.renameTo(new File(newPath + "/" + newFileFullName));
	    	file.delete();
    	} else {
    		rst = false;
    	}
    	
    	if(!rst) newFileFullName = "Fialed";  
    	
    	return newFileFullName;
    }
    
	/**
	 * 파일명으로 FileReader 클래스 구분
	 * @param fileName 파일명(확장자 포함)
	 * @return
	 * @throws Exception
	 */
    public static FileReader getFileReader(String fileName) throws Exception {
		FileReader reader = null;
		
		if(fileName.toLowerCase().lastIndexOf(".xlsx") > 0) {
			if(log.isDebugEnabled()) log.debug("File type is Excel(2007~).");
			
			reader = new ExcelXSSFReader();
		} else if(fileName.toLowerCase().lastIndexOf(".xls") > 0) {
			if(log.isDebugEnabled()) log.debug("File type is Excel(2003~2007).");
			
//			reader = new ExcelHSSFReader();
			reader = new ExcelSSReader();
		} else if(fileName.toLowerCase().lastIndexOf(".xml") > 0) {
			if(log.isDebugEnabled()) log.debug("File type is XML.");
			
			reader = new XmlReader();
		} else {
			throw new FrameException("Wrong request. Please request again after entering the correct file name.");
		}
    	
    	return reader;
    }
    
    /**
     * <p>
     * 파일경로와 명칭으로 File 객체를 생성 후 반환<br>
     * 파일경로가 null이면, 임의로 지정된 위치로 File 객체 반환
     * </p>
     * @param fpath 파일경로
     * @param fname 파일명
     * @return
     * @throws Exception
     */
    public static File getFile(String fpath, String fname) throws Exception {
    	String tpath = null;
    	
    	if(fpath == null) {
    		tpath = FileUtil.getFullPath(fpath);
    	} else {
	    	File file = new File(fpath);
			
	    	if(file.exists()) {
				tpath = fpath;
			} else {
				makeDirectrory(fpath);
				tpath = FileUtil.getFullPath(fpath);
			}
    	}
    	
		return new File(tpath + File.separator + fname);
    }
    
    public static String transferToImage(String fileSavePath, byte[] encodeBytes, String FileFullName) throws Exception {
    	if(log.isDebugEnabled()) log.debug(StringUtils.repeat("#", 10) + "FileUtils.transferTo start");
    	
    	File file = new File(FileFullName);
		
    	if (!file.exists()) {
    		if(log.isDebugEnabled()) log.debug(StringUtils.repeat("#", 10) + "FileUtils.transferTo file is null");
    		return null;
    	}

    	String filePath  = fileSavePath;// + fileFullName;
		String newFileFullPath;
		String newFileFullName;
		File newFile;
		
		String originalFileName = FileFullName.substring(0, FileFullName.lastIndexOf("."));
		String originalExtension = FileFullName.substring(FileFullName.lastIndexOf("."));
		
		Integer dupCount = 0;
		String dupName;

		while (true) {
			dupName = (dupCount++ == 0) ? "" : "(" + dupCount.toString() + ")";
			newFileFullName = originalFileName + dupName + originalExtension;
			newFileFullPath = filePath + newFileFullName;
			newFile = new File(newFileFullPath);
			if (!newFile.exists()) {
				break;
			}
		}

		//저장할 폴더가 있는지 확인
		//여기서 filePath는 디렉토리임
		File dir = new File(filePath);
		
		//directory 없을경우
		//맹그러
		if(!dir.exists()){
			if(!dir.mkdirs()){
				throw new Exception("Failed Make Dir ==> " + dir.getName());
			}
		}

		//디렉토리가 준비되면
		//경로+파일명으로
		//file 개체생성
		//생성해놓고 newFileFullPath 잡는 이유는
		//저장 경로가  c:\abc\..\abk\122.bmp 이렇게 되어있어도 유효한 주소지만
		//보기 않좋으니까
		//new File을 할 때는 위 주소가 먹힘
		//getAbsolutePath() 하면
		//   c:\abk\122.bmp 라
	
		
		File uploadFile = new File(newFileFullPath);
		newFileFullPath = uploadFile.getAbsolutePath();
		
		DataOutputStream dis;
		
		FileOutputStream fout = new FileOutputStream(newFileFullPath); 
		dis = new DataOutputStream(fout); 
		dis.write(encodeBytes);
		dis.flush();
		dis.close();
		fout.close();
		
		if(log.isDebugEnabled()) log.debug(StringUtils.repeat("#", 10) + "FileUtils.transferTo end");
    	
		return newFileFullName;
	}
    
    /**
	 * <p>
	 * 지정된 경로로 파일을 저장한다.
	 * <p>
	 * @param propName properties 설정정보
	 * @param file	저장된 파일(MultipartFile)
	 * @return 저장한 정보를 리턴한다.<br>
	 * 		map.put(ORIGIN_FILE_NAME, 원본 파일명);<br>
	 * 		map.put(ORIGIN_FILE_PATH, 저장된 파일경로);<br>
	 * 		map.put(SAVED_FILE_NAME, 저장된 파일명);<br>
	 * 		map.put(ORIGINAL_FILE_EXTENSION, 파일 확장자);<br>
	 * 		map.put("FILE_SIZE", 파일크기);<br>
	 * 		<br>
	 * @throws Exception
	 */
    public static Map getFile(String propName, FormFile file) throws Exception {
		Map map = new LinkedHashMap();
        String filePath = getFullPath(propName);
        String workPath = getWorkPath(propName);
        String originalFileName = file.getOriginalFilename();
        String originalExtension = file.getOriginalExtension();
        String newFileName = file.getFileName();
        originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
 
        map.put("ORIGIN_FILE_NAME", originalFileName+originalExtension);
        map.put("ORIGIN_FILE_PATH", workPath);
        map.put("ORIGINAL_FILE_EXTENSION", originalExtension);
        map.put("SAVED_FILE_NAME", newFileName);
        map.put("FILE_SIZE", Integer.toString(file.getFileSize()));
        
        return map;
    }
}