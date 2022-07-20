package kr.yni.frame.web.view;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractView;

import kr.yni.frame.util.StringHelper;

public class JsonView extends AbstractView {
	
	private static Log log = LogFactory.getLog(JsonView.class);
	
	private static final String DEFAULT_JSON_CONTENT_TYPE = "application/json";
	
	public JsonView() {
		super();
		setContentType( DEFAULT_JSON_CONTENT_TYPE );
	}
	
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		response.setContentType(this.getContentType());
		
		String jsonData = StringHelper.null2void(model.get("rows"));
		
		if(log.isDebugEnabled()) log.debug("View contentType = " + this.getContentType() + ", json data = " + jsonData);
		
		PrintWriter writer = response.getWriter();
		
		writer.write(jsonData);
	}
}
