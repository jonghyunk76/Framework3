package kr.yni.frame.web.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * multipart/form-date형식으로 전송된 폼 데이터를 처리하는 클래스
 * </p>
 * 
 * @author YNI-maker
 *
 */
public class FormFile {
	protected static Log log = LogFactory.getLog(FormFile.class);

    private MultipartFile fileItem;

    private String newFileName;
    
    /**
     * <p>
     * FormFile 클래스의 생성자로써 FileItem 객체를 저장한다.
     * </p>
     *
     * @param fileItem 파일 업로드 리퀘스트로부터 얻어지는 FileItem 객체
     * @param key FileItem 객체에 대한 키 값
     */
    public FormFile(MultipartFile fileItem) {
        this.fileItem = fileItem;
    }

    /**
     * <p>
     * 업로드 파일에 대한 컨텐트 타입을 리턴한다.
     * </p>
     *
     * @return 업로드 파일의 컨텐트 타입에 대한 문자열
     */
    public String getContentType() {
        return fileItem.getContentType();
    }

    /**
     * <p>
     * 파일의 사이즈를 리턴한다.
     * </p>
     *
     * @return 파일 사이즈
     */
    public int getFileSize() {
        return (int) fileItem.getSize();
    }

    /**
     * <p>
     * 파일의 이름을 리턴한다.(경로명을 제회시킨 파일명만 리턴된다.)
     * </p>
     * @return  파일명
     */
    public String getOriginalFilename() {
        return fileItem.getOriginalFilename();
    }

    /**
     * <p>
     * 파일의 내용을 byte[] 형태로 리턴한다.
     * </p>
     *
     * @return 파일의 내용(byte[])
     * @throws FileNotFoundException 해당 파일을 찾지 못하는 경우
     * @throws IOException 해당 파일로 부터 내용을 읽지 못하는 경우
     */
    public byte[] getFileData() throws FileNotFoundException, IOException {
    	if(log.isDebugEnabled()) log.debug("empty true/false = " + this.isEmpty() + ", file size = " + this.getFileSize() +
    			"file name = " + this.getFileName() + ", origin file name = " + this.getOriginalFilename());
    	
    	if(this.isEmpty()) {
    		return null;
    	}
    	
        return fileItem.getBytes();
    }

    /**
     * <p>
     * 파일의 내용을 InputStream 형태로 리턴한다.
     * </p>
     *
     * @return 파일의 내용(InputStream)
     * @throws FileNotFoundException 해당 파일을 찾지 못하는 경우
     * @throws IOException 해당 파일로 부터 내용을 읽지 못하는 경우
     */
    public InputStream getInputStream() throws FileNotFoundException, IOException {
        return fileItem.getInputStream();
    }

    /**
     * <p>
     * 클라이언트 파일시스템에서 가지고 있던 원본 파일명을 리턴한다. 
     * </p>
     * @return 업로드 파일에 대한 키
     */
    public String getFieldName(){
        return getBaseFileName(fileItem.getName());
    }
    
    /**
     * 실제 저장된 파일명을 등록한다.
     * 
     * @param name 저장 파일명
     */
    public void setFileName(String name) {
    	this.newFileName = name;
    }
    
    /**
     * 실제 저장된 파일명을 리턴한다.
     * 
     * @return
     */
    public String getFileName() {
    	return this.newFileName;
    }
    
    /**
     * 파일의 확장자를 구한다. 
     * @return
     */
    public String getOriginalExtension(){
    	String fileName = getOriginalFilename();
    	
    	return fileName.substring(fileName.lastIndexOf("."));
    }
    
    /**
     * Mutipart 파일의 정보를 화면에 보여준다.
     */
    public void printFileInfo() {
    	if(log.isDebugEnabled()) {
    		log.debug("Content type = "+ fileItem.getContentType());
    		log.debug("Size = "+ fileItem.getSize());
    		log.debug("Original file name() = "+ fileItem.getOriginalFilename());
    		log.debug("File name() = "+ fileItem.getName());
    		log.debug("Save file name() = "+ newFileName);
    	}
    }
    
    /**
     * 지정한 파일객체로 파일 내용을 저장한다.
     * 
     * @param file
     * @throws Exception
     */
    public void write(File file) throws Exception {
    	fileItem.transferTo(file);
    }
    
    /**
     * 업로드   파일이 비어있는 경우. 즉, Multifile form안에 file 없거나, 지정된 file 내용이 없는 경우 true를 리턴한다.
     * @return
     */
    public boolean isEmpty() {
    	return fileItem.isEmpty();
    }
    
    /**
     * <p>
     * 경로명을 제외한 파일명을 리턴한다.
     * </p>
     * 
     * @param filePath  파일의 전체 경로명
     * @return 경로명을 제외한 파일명
     */
    protected String getBaseFileName(String filePath) {
        String fileName = (new File(filePath)).getName();
        
        int colonIndex = fileName.indexOf(":");
        if (colonIndex == -1) {
            colonIndex = fileName.indexOf("\\\\");
        }
        int backslashIndex = fileName.lastIndexOf("\\");
        if (colonIndex > -1 && backslashIndex > -1) {
            fileName = fileName.substring(backslashIndex + 1);
        }
        return fileName;
    }
    
}

