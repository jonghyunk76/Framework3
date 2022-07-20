package kr.yni.frame.web.view;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

import com.opencsv.CSVWriter;

import kr.yni.frame.Constants;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.reader.FileReader;
import kr.yni.frame.reader.type.CsvReader;
import kr.yni.frame.reader.type.ExcelSXSSFReader;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.DateHelper;
import kr.yni.frame.util.FileUtil;
import kr.yni.frame.util.SessionUtil;
import kr.yni.frame.util.StringHelper;

/**
 *  JExcel 기반으로 엑셀 파일을 생성한다.
 *  
 * @author YNI-maker
 *
 */
//public class ExcelView extends AbstractExcelView {
public class ExcelView extends AbstractView {
	
	private static Log log = LogFactory.getLog(ExcelView.class);
	
	private static final String DEFAULT_JSON_CONTENT_TYPE = "application/octet-stream";
	
	/**
	 * AbstractExcelView의 확장 매소드로 default로 실행된다.
	 /
	@SuppressWarnings("unchecked")
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook
			, HttpServletRequest request, HttpServletResponse response) throws Exception { 
		String fileName = createFileName(StringHelper.null2void(model.get("fileName")), request);	// 파일명
		MessageResource messageSource = MessageResource.getMessageInstance();
		
		if(StringHelper.isNull(fileName)) {
			fileName = messageSource.getMessage("TXT_SYSTEM_NAME", null, "ENG") + "_" + DateHelper.getCurrentDateTimeMilliSecondAsString() + ".xls";
		}
		
		if(log.isDebugEnabled()) log.debug("Excel file name = " + fileName);
		
		setFileNameToResponse(request, response, fileName);	// xls파일생성
		
		List<List<Map<String, Object>>> list = (List<List<Map<String, Object>>>) model.get("list");	// 엑셀에 기록될 값
		
		if(list != null && list.size() > 0) {  // 엑셀다운로드할 값이 없으면 실행하지 않는다.
			FileReader reader = new ExcelHSSFReader();
			reader.view(list, workbook);
		}
	}
	*/
	
	public ExcelView() {
		super();
		setContentType( DEFAULT_JSON_CONTENT_TYPE );
	}
	
	/**
	 * 
	 */
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BufferedOutputStream fos = null; 
		ServletOutputStream out = null;
		String fileName = null;
		byte bytes[] = null;
		File temFile = null;
		
		MessageResource messageSource = MessageResource.getMessageInstance();
		
		try {
			log.debug("Request URL = " + request.getRequestURI());
			
			String language = StringHelper.null2string(SessionUtil.getString(request.getSession(), Constants.KEY_DEFAULT_LANGUAGE), "EN");
			fileName = createFileName(StringHelper.null2void(model.get("fileName")), request);	// 파일명
			
			if(StringHelper.isNull(fileName)) {
				fileName = messageSource.getMessage("TXT_SYSTEM_NAME", null, language) + "_" + DateHelper.getCurrentDateTimeMilliSecondAsString() + ".xls";
			}
			
			if(log.isDebugEnabled()) log.debug("Excel file name = " + fileName);
			
			List<List<Map<String, Object>>> list = (List<List<Map<String, Object>>>) model.get("list");	// 엑셀에 기록될 값
			
			// 실제 출력할 데이터 구하기
			List<Map<String, Object>> dataList = null;
			int dataSize = 0;
			
			for(int j = 0; j < list.size(); j++) {
        		dataList = (List<Map<String, Object>>) list.get(j);
        		dataSize += (dataList.size()-1);
			}
			
			if(dataSize < 0) dataSize = 0;
			
			String maxRow = StringHelper.null2void(SessionUtil.getString(request.getSession(), Constants.KEY_SESSION_EXCEL_MAX_ROWNUM)).replaceAll(",", "");
			String limitRow = StringHelper.null2void(SessionUtil.getString(request.getSession(), Constants.KEY_SESSION_CSV_MAX_ROWNUM)).replaceAll(",", "");
			int maxRowNum = -1;
			int limitRowNum = -1;
			
			if(maxRow.isEmpty()) maxRowNum = -1;
			else maxRowNum = Integer.parseInt(maxRow);
			
			if(limitRow.isEmpty()) limitRowNum = -1;
			else limitRowNum = Integer.parseInt(limitRow);
			
			if(log.isInfoEnabled()) log.info("System Configration(Excel max rownum = " + maxRowNum + ", CSV max rownum = " + limitRowNum + "), vo size = " + dataSize);
			
			// 리소스 부족으로 서버가 다운로드되는 경우가 발생하여 엑셀 다운로드 가능 수를 제한시킴(2022-03-05)
			if(limitRowNum > -1 && limitRowNum < dataSize) {
				// throw new FrameException(messageSource.getMessage("TXT_SYSTEM_NAME", null, language));
				response.setContentType("application/json");
				
				String msg = messageSource.getMessage("MSG_EXCEL_MAX_ROWNUM", null, language) + "<br>(" + messageSource.getMessage("TXT_MAX_ROWNUM", null, language) + ": " + limitRow + 
						", " + messageSource.getMessage("TOTAL_CNTNB", null, language) + ": " + dataSize + ")";
				msg = "{\"message\":\""+msg+"\"}";
				if(log.isDebugEnabled()) log.debug("View contentType = " + this.getContentType() + ", json data = " + msg);
				
				PrintWriter writer = response.getWriter();
				
				writer.write(msg);
			} else {
				// 엑셀 최대건수를 초과할 경우, CSV파일로 다운로드 되도록 변경함(2022-03-05)
				if(maxRowNum > -1 && maxRowNum < dataSize) {
					int lastIdx = fileName.lastIndexOf(".");
					fileName = fileName.substring(0, lastIdx) + ".csv";
					String localFile = FileUtil.getFullPath(null)+fileName;
					
					if(list != null && dataSize > 0) {  // 엑셀다운로드할 값이 없으면 실행하지 않는다.
						if(log.isDebugEnabled()) log.debug("make csv file - start...");
						
						CSVWriter cw = null;
						List<List<String>> csvList = null;
						
						if(language.equals("KR")) {
							cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(localFile), "euc-kr"));
						} else {
							cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(localFile), Constants.APPLICATION_CONTEXT_CHARSET));
						}
						
						try {
							CsvReader reader = new CsvReader();
							csvList = reader.view(list);
							
							log.debug("sss - data_list size  = " + csvList.size());
							
							if(csvList != null && csvList.size() > 0) {
								for(int i = 0; i < csvList.size(); i++) {
									List<String> clist = csvList.get(i);
									String[] cary = new String[clist.size()];
									
									// 문자열 배열로 변경
									for(int k = 0; k < clist.size(); k++) {
										cary[k] = clist.get(k);
									}
									
									cw.writeNext(cary);
								}
							}
							
							temFile = new File(localFile);
							
							if(log.isDebugEnabled()) log.debug("make csv file(" + temFile.length() + ") - end...");
						} catch(Exception e) {
							if(log.isErrorEnabled()) log.error(e);
						} finally {
							if(cw != null) cw.close();
						}
					}
				} else {
					SXSSFWorkbook workbook = new SXSSFWorkbook();
					workbook.setCompressTempFiles(true);
					
					if(list != null && list.size() > 0) {  // 엑셀다운로드할 값이 없으면 실행하지 않는다.
						try {
							if(log.isDebugEnabled()) log.debug("make work sheet - start...");
							
							FileReader reader = new ExcelSXSSFReader();
							reader.view(list, workbook);
							
							if(log.isDebugEnabled()) log.debug("make work sheet - end...");
							
							String localFile = FileUtil.getFullPath(null)+fileName;
							temFile = new File(localFile);
							fos = new BufferedOutputStream(new FileOutputStream(temFile));
							
							workbook.write(fos);
							
							if(log.isDebugEnabled()) log.debug("Excel file path-temp = " + localFile + temFile.exists() +  " / " + temFile.getName());
						} catch(Exception e) {
							if(log.isErrorEnabled()) log.error(e);
						} finally {
							workbook.close();
							workbook.dispose();
							if(fos != null) fos.close();
						}
					}
				}
				
				// 임시저장된 파일이 있다면 계속 진행
				if(temFile != null) {
					// 파일 처리
		            bytes = FileUtils.readFileToByteArray(temFile);
		            
					if(fileName.toLowerCase().endsWith(".pdf")) {
						response.setContentType("application/pdf");
					} else if(fileName.toLowerCase().endsWith(".xls") || fileName.toLowerCase().endsWith(".xlsx")) {
						response.setContentType("application/x-msexcel");
					} else if(fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx")) {
						response.setContentType("application/msword");
					} else if(fileName.toLowerCase().endsWith(".xml")) {
						response.setContentType("application/xml");
					} else if(fileName.toLowerCase().endsWith(".csv")) {
						response.setContentType("text/csv");
					} else {
						response.setContentType(getContentType());
					}
					//response.setHeader("Content-Transfer-Encoding", "binary");
					setFileNameToResponse(request, response, fileName);	// xls파일생성
					
					if(log.isDebugEnabled()) log.debug("Download file(Content Type=" + response.getContentType() + ", Name = " + fileName + ", size = " + temFile.length() + "byte)");
					
					if (bytes != null) {
						out = response.getOutputStream();
			
						out.write(bytes, 0, bytes.length);                    
						out.flush();
					} else {
						throw new FrameException("There is an error while downloading a file.");
					}
				}
			}
		} finally {
			if(out != null) out.close();
			
			try {
				if(temFile != null && temFile.exists()) {
					boolean delyn = temFile.delete();
					
					if(log.isDebugEnabled()) log.debug("delete file(result = " + delyn + ")");
				}
			} catch(Exception e) {
				if(log.isErrorEnabled()) log.error(e);
			}
		}
	}
	
	private void setFileNameToResponse(HttpServletRequest request, HttpServletResponse response, String fileName) {
		String userAgent = request.getHeader("User-Agent");
		
		if(userAgent.indexOf("MSIE 5.5") >= 0){
			response.setContentType("doesn/matter");
			response.setHeader("Content-Disposition","filename=\""+fileName+"\"");
		}else{
			response.setHeader("Content-Disposition","attachment; filename=\""+fileName+"\"");
		}
	}
	
    private String createFileName(String fileName, HttpServletRequest request) throws Exception {
    	String fileStr = null;
    	String userAgent = request.getHeader("User-Agent");
    	
    	if (userAgent.indexOf("MSIE 5.5") > -1) { // MS IE 5.5 이하
    		fileStr = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ");
		} else if (userAgent.contains("Edge")){
    		fileStr = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ");
    	} else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) { // IE 11버전부터 Trident로 변경되었기때문에 추가해준다.
    		fileStr = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ");
    	} else if (userAgent.contains("Chrome")) {
    		fileStr = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
    	} else if (userAgent.contains("Opera")) {
    		fileStr = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
    	} else if (userAgent.contains("Firefox")) {
    		fileStr = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
    	} else { // 모질라나 오페라
			fileStr = new StringBuilder(StringHelper.get8859_1(fileName)).toString();
		}

    	
        SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        
//        return fileStr + "-" + fileFormat.format(new Date()) + ".xls";
        return fileStr + "-" + fileFormat.format(new Date()) + ".xlsx";
    }
	
}
