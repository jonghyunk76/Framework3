package kr.yni.frame.web.channel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * <p>
 * Map 기반의 Command Object 데이터 바인딩을 수행하기 위한 annotation Adapter
 * </p>
 * 
 * @author YNI-maker
 *
 */
public class AbstractMapBasedAnnotationAdapter extends
		AnnotationMethodHandlerAdapter {

	protected ServletRequestDataBinder createBinder(HttpServletRequest webRequest, Object command
			, String objectName) throws Exception {
		
		ServletRequestDataBinder binder = new DataMapDataBinder(command, objectName);
		
		return binder;
	}

}
