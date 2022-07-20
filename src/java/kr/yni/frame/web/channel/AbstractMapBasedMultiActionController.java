package kr.yni.frame.web.channel;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Map 기반의 Command Object. 데이터 바인딩을 수행하기 위한 multi-action 컨트롤러
 * 
 * @author YNI-maker
 *
 */
public abstract class AbstractMapBasedMultiActionController extends MultiActionController {
	
	protected Log log = LogFactory.getLog(AbstractMapBasedMultiActionController.class);
			
	/*
	 * 기존에 제공되던 ServletRequestDataBinder 대신 SimpleMapDataBinder로 교체한다.
	 */
	protected ServletRequestDataBinder createBinder(HttpServletRequest request,
			Object command) throws Exception {
		log.debug("ServletRequestDataBinder Start");
		
		ServletRequestDataBinder binder = new DataMapDataBinder(command, getCommandName(command));
		
		initBinder(request, binder);
		
		log.debug("ServletRequestDataBinder End");
		
		return binder;
	}// end of createBinder()
}