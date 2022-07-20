package kr.yni.frame.web.channel;

import javax.servlet.ServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;

import kr.yni.frame.Constants;
import kr.yni.frame.collection.DataMap;

/**
 * 
 * ServletRequestDataBinder를 확장한 클래스
 * 
 * @author YNI-maker
 *
 */
public class DataMapDataBinder extends ServletRequestDataBinder {

	public DataMapDataBinder(Object target) {
		super(target);
	}// end of constructor

	public DataMapDataBinder(Object target, String objectName) {
		super(target, objectName);
	}// end of constructor

	@SuppressWarnings("unchecked")
	@Override
	public void bind(ServletRequest request) {
		if (this.getTarget() != null && DataMap.class.isAssignableFrom(this.getTarget().getClass())) {
			DataMap map = (DataMap) this.getTarget();
			
			if ((DataMap) request.getAttribute(Constants.DATA_MAP) != null) {
				map.putAll(((DataMap) request.getAttribute(Constants.DATA_MAP)).getMap());
				map.setSession(((DataMap) request.getAttribute(Constants.DATA_MAP)).getSession());
			}
			
			request.setAttribute((Constants.DATA_MAP), map);
		} else {
			super.bind(request);
		}
	}

}
