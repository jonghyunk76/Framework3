package kr.yni.frame.web.view;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractView;

import kr.yni.frame.Constants;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.DateHelper;
import kr.yni.frame.util.FileUtil;
import kr.yni.frame.util.StringHelper;

/**
 * 파일 다운로드 처리 클래스
 * 
 * @author YNI-maker
 *
 */
public class XmlView extends AbstractView {
	
	private static Log log = LogFactory.getLog(XmlView.class);
	
	private static final String DEFAULT_XML_CONTENT_TYPE = "application/xml";
	
	public XmlView() {
		super();
		setContentType(DEFAULT_XML_CONTENT_TYPE);
	}
	
	/**
	 * 
	 */
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		InputStream is = null;
		BufferedInputStream bis = null;
    	ByteArrayOutputStream arrayBuff = new ByteArrayOutputStream();
		ServletOutputStream out = null;
		String fileName = null;
		byte bytes[] = null;
		byte[] buffer = new byte[1024];
		
		MessageResource messageSource = MessageResource.getMessageInstance();
		
		try {
			Object obj = model.get("file");
			
			if(obj == null) {
				throw new FrameException("The system can not find the file.");
			}
			
			// <code>Object</code>타입을 비교하여 처리함
			if (obj instanceof File) {
				File file = (File) obj;
				
				fileName = (String) model.get(FileUtil.ORIGINAL_FILE_NAME);
				
				if(StringHelper.isNull(fileName)) {
					fileName = file.getName();
				}
				
				is = new FileInputStream(file);
				
				response.setContentLength((int) file.length());
				
				bis = new BufferedInputStream(is);
            	
            	int len = 0;
                while ((len = bis.read(buffer)) >= 0) {
                    arrayBuff.write(buffer, 0, len);
                }
                
                bytes = arrayBuff.toByteArray();
			} else if (obj instanceof InputStream) {
				fileName = StringHelper.null2void(model.get("fileName"));
				
				if(StringHelper.isNull(fileName)) {
					fileName = messageSource.getMessage("SYSTEM_NAME", null, "ENG") + "_" + DateHelper.getCurrentDateTimeMilliSecondAsString() + ".xls";
				}
				
				is = (InputStream) obj;
				
				bis = new BufferedInputStream(is);
            	
            	int len = 0;
                while ((len = bis.read(buffer)) >= 0) {
                    arrayBuff.write(buffer, 0, len);
                }
                
                bytes = arrayBuff.toByteArray();
			} else if(obj instanceof byte[]) {
				fileName = StringHelper.null2void(model.get("fileName"));
				
				if(StringHelper.isNull(fileName)) {
					fileName = messageSource.getMessage("SYSTEM_NAME", null, "ENG") + "_" + DateHelper.getCurrentDateTimeMilliSecondAsString() + ".xls";
				}
				
				bytes = (byte[]) obj;
			}
			
			response.setHeader("Cache-Control", "no-cache");
			
			if(log.isDebugEnabled()) log.debug("Download file(Content Type=" + response.getContentType() + ", Name = " + fileName + ", size = " + bytes.length + "byte)");
			
			if (bytes != null) {
				response.setContentLength(bytes.length);
				String xmlContents = new String(bytes, Constants.APPLICATION_CONTEXT_CHARSET);
				response.getWriter().print(xmlContents);
			} else {
				throw new FrameException("There is an error while downloading a file.");
			}
		} finally {
			if (is != null) is.close();
		}
	}
}
