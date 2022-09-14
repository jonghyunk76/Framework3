package kr.yni.frame.web.view;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractView;

import kr.yni.frame.collection.DataMap;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.DateHelper;
import kr.yni.frame.util.StringHelper;

public class IreportView extends AbstractView {
	private static Log log = LogFactory.getLog(IreportView.class);
	
	/**
	 * 레포트 출력 형태
	 */
	private String REPORT_OUTPUT_TYPE = "xlsx";
	
	/**
	 * 레포트 파일명
	 */
	private String REPORT_FILE_NAME = null;
	
	/**
	 * 응답 컨텐츠 타입
	 */
	private final String DEFAULT_JSON_CONTENT_TYPE = "application/octet-stream";
	
	/**
	 * export설정
	 */
	private Map<JRExporterParameter, Object> EXPORT_PARAMETERS = new LinkedHashMap<JRExporterParameter, Object>();
	
	/**
	 * Default Constructor
	 */
	public IreportView() {
		super();
		setContentType( DEFAULT_JSON_CONTENT_TYPE );
	}
	
	@SuppressWarnings("unchecked")
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<JasperPrint> jasperPrint = (List<JasperPrint>) model.get("data");
		
		ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
		
		EXPORT_PARAMETERS.put(JRXlsExporterParameter.OUTPUT_STREAM, ouputStream);
		EXPORT_PARAMETERS.put(JRXlsExporterParameter.JASPER_PRINT_LIST, jasperPrint);
		
		REPORT_OUTPUT_TYPE = StringHelper.null2string(model.get("REPORT_TYPE"), REPORT_OUTPUT_TYPE);
		REPORT_FILE_NAME = StringHelper.null2string(model.get("file"), REPORT_FILE_NAME);
		
		if(log.isDebugEnabled()) log.debug("jasper name = " + ((JasperPrint)jasperPrint.get(0)).getName() + 
				", file info(type = " + model.get("REPORT_TYPE") + "/" + REPORT_OUTPUT_TYPE + ", file name = " + REPORT_FILE_NAME + ")");
		
		MessageResource messageSource = MessageResource.getMessageInstance();
		if(StringHelper.isNull(this.REPORT_FILE_NAME)) {
			this.REPORT_FILE_NAME = messageSource.getMessage("SYSTEM_NAME", null, "ENG") + "_" + DateHelper.getCurrentDateTimeMilliSecondAsString();
		}
		
		// export 생성
		this.createExporter();
		
		// 레포트 뷰
		this.renderReport(response);
	}
	
	/**
	 * 파라메터에서 지정한 형태의 매개변수를 설정한다. 
	 * 
	 * @return
	 * @throws Exception
	 */
	public void createExporter() throws Exception {
		JRExporter exporter = null;
		
		if("pdf".equals(REPORT_OUTPUT_TYPE)) {
			exporter = new JRPdfExporter();
		} else if("xls".equals(REPORT_OUTPUT_TYPE) || "xlsx".equals(REPORT_OUTPUT_TYPE)) {
			exporter = new JRXlsxExporter();
		} else if("doc".equals(REPORT_OUTPUT_TYPE) || "docx".equals(REPORT_OUTPUT_TYPE)) {
			exporter = new JRDocxExporter();
		} else if("applet".equals(REPORT_OUTPUT_TYPE)) {
			exporter = null;
		}
		
		// 문서를 보낼 매개변수를 설정한다.
		if(exporter != null) {
			Iterator<JRExporterParameter> keys = EXPORT_PARAMETERS.keySet().iterator();
			while(keys.hasNext()) {
				JRExporterParameter key = keys.next();
				Object value = EXPORT_PARAMETERS.get(key);
				exporter.setParameter(key, value);
			}
			
			exporter.exportReport();
		}
	}
	
	/**
	 * Perform rendering for a single Jasper Reports exporter,
	 * i.e. a pre-defined output format.
	 */
	@SuppressWarnings("rawtypes")
	public void renderReport(HttpServletResponse response) throws Exception {
		byte bytes[] = null;
		
		if(!"applet".equals(REPORT_OUTPUT_TYPE)) {
			ByteArrayOutputStream output = (ByteArrayOutputStream) EXPORT_PARAMETERS.get(JRExporterParameter.OUTPUT_STREAM);
			bytes = output.toByteArray();
			
			if(log.isDebugEnabled()) log.debug("report byte = " + bytes.length);
			
			if (bytes != null && bytes.length > 0) {
				if("pdf".equals(REPORT_OUTPUT_TYPE)) {
					response.setContentType("application/pdf");
					response.setHeader("Content-disposition", "attachment; filename=\"" + REPORT_FILE_NAME + ".pdf\"");
				} else if("xls".equals(REPORT_OUTPUT_TYPE) || "xlsx".equals(REPORT_OUTPUT_TYPE)) {
					response.setContentType("application/x-msexcel");
					response.setHeader("Content-disposition", "attachment; filename=\"" + REPORT_FILE_NAME + ".xlsx\"");
				} else if("doc".equals(REPORT_OUTPUT_TYPE) || "docx".equals(REPORT_OUTPUT_TYPE)) {
					response.setContentType("application/msword");
					response.setHeader("Content-disposition", "attachment; filename=\"" + REPORT_FILE_NAME + ".docx\"");
				}
				
				response.setContentLength(bytes.length);
				
				ServletOutputStream ouputStream = response.getOutputStream();
				try {
					ouputStream.write(bytes, 0, bytes.length);                    
					ouputStream.flush();
				} finally {
					if(ouputStream != null) ouputStream.close();
				}
			} else {
				log.error("Byte is zero.");
			}
		} else {
			ObjectOutputStream output = null;
			OutputStream outputStream = response.getOutputStream();
			
			try {
				output =(ObjectOutputStream) EXPORT_PARAMETERS.get(JRExporterParameter.OUTPUT_STREAM);
				List jasperList = (List) EXPORT_PARAMETERS.get(JRExporterParameter.JASPER_PRINT_LIST);
				
				output.writeObject((JasperPrint)jasperList.get(0));
				output.flush();
			} finally {
				if(outputStream != null) outputStream.close();
				if(output != null) output.close();
			}
		}
	}
}
