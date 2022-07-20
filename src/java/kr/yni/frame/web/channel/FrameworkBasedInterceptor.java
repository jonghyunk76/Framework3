package kr.yni.frame.web.channel;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import kr.yni.frame.Constants;
import kr.yni.frame.collection.DataMap;
import kr.yni.frame.util.JsonUtil;
import kr.yni.frame.util.ServletHelper;
import kr.yni.frame.util.StringHelper;

/**
 * BasedHandlerInterceptor 클래스
 * 
 * @author YNI-maker
 *
 */
public class FrameworkBasedInterceptor extends HandlerInterceptorAdapter {
	
	private static Log log = LogFactory.getLog(FrameworkBasedInterceptor.class);
	
	/**
	 * <p>
	 * Controller 전달전 처리 작업 수행하는 클래스<br>
	 * dataMap를 <code>HttpServletRequest</code>객체에 담아 controller에 전달되도록 함
	 * </p>
	 * @return 반환 값이 true일 경우 정상적으로 진행이 되고, false일 경우 실행이 멈춥니다.(컨트롤러 진입을 하지 않음)
	 */
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		
		// context path 설정
		Constants.SERVLET_CONTEXT_PATH = request.getSession().getServletContext().getRealPath("/");
		String conType = request.getContentType();
		
		if(conType == null) conType = "";
		
		if(log.isInfoEnabled()) log.info("Content Type = " + conType + " Character Encoding = " + request.getCharacterEncoding() );
		
		boolean breaker = true;
		DataMap dataMap = new DataMap();

		// 공통 유틸을 조회하는 경우 세션체크를 하지 않도록 설정
		if(request.getRequestURI().startsWith("/mm/cbox/")) { // 2016.03.27
			if(log.isDebugEnabled()) {
				log.debug("No seesion URL(" + request.getRequestURI() + ")");
			}
			breaker = false;
		}
		
		// 로그인 인증이 필요없는 화면인 경우 세션체크를 하지 않도록 설정
		if(request.getRequestURI().startsWith("/mm/noses/")) { // 2017.09.06 
			if(log.isDebugEnabled()) {
				log.debug("No session URL(" + request.getRequestURI() + ")");
			}
			breaker = false;
		}
		
		// TOMS Cloud HUB 인증인 경우 세션체크를 하지 않도록 설정(TOMS Cloud 통신전용)
		if(request.getRequestURI().startsWith("/ch/link/")) { // 2017.09.28 
			if(log.isDebugEnabled()) {
				log.debug("certification of TOMS Cloud HUB(" + request.getRequestURI() + ")");
			}
			breaker = false;
		}
		
		// 도움말 찹업은 세션을 체크하지 않도록 함
		if(request.getRequestURI().startsWith("/mm/pop/mmA029")) { // 2019-11-23
			if(log.isDebugEnabled()) {
				log.debug("No seesion URL(" + request.getRequestURI() + ")");
			}
			breaker = false;
		}
		
		if(request.getRequestURI().contains("kakao.com")) { // 2021.05.18
			if(log.isDebugEnabled()) {
				log.debug("No seesion URL(" + request.getRequestURI() + ")");
			}
			breaker = false;
		}
		
		// 중계서버 인증인 경우 세션체크를 하지 않도록 설정(중계서버 Broker용)
		if(request.getRequestURI().startsWith("/rs/batch/")) { // 2022.06.09 
			if(log.isDebugEnabled()) {
				log.debug("certification of Relay Server(" + request.getRequestURI() + ")");
			}
			breaker = false;
		}
		
		Map member = (LinkedHashMap) request.getSession().getAttribute(Constants.SESSION_KEY);
		
		if(breaker) {
			if(member == null) {
				ModelAndView mav = new ModelAndView();
				
				String url = StringHelper.null2void(request.getServletPath());
				String name = "";
				
				if(!url.isEmpty()) {
					String[] paths = url.split("/");
					if(paths.length > 0) {
						name = "/" + paths[1];
					}
				}
				
				if(url.indexOf(Constants.MAIN_VIEW) > 0) {
					if(log.isDebugEnabled()) log.debug("go page[Context path="+name+", main path="+Constants.MAIN_VIEW+", move path="+name + "/mm/login.htm"+"]");
					
					mav.setViewName("redirect:" + name + "/mm/login.htm");
				} else {
					if(log.isDebugEnabled()) log.debug("go page[Context path="+name+", main path="+Constants.MAIN_VIEW+", move path="+name + "/cert/sessionCheck"+"]");
					
					mav.setViewName("forward:" + name + "/cert/sessionCheck");
				}
				
				// 로그인 정보가 없을 때는 Exception을 걸어 Session interceptor를 건다.
				throw new ModelAndViewDefiningException(mav);
			}
		}
		
		if(log.isDebugEnabled()) log.debug("login information = " + member);
		
		if(log.isInfoEnabled()) log.info("request = " + request + " / type = " + conType);
		
		if(conType.toLowerCase().equals("application/json")) { // json타입의 요청을 처리하기 위해 추가(2022-06-10)
			Map map = new HashMap();
			StringBuffer json = new StringBuffer();
            String line = null;
            
            try {
	            BufferedReader reader = request.getReader();
	            
	            while((line = reader.readLine()) != null) {
	                json.append(line);
	            }
	            
	            if(json.length() > 0) {
	                String jsonStr = json.toString().trim();
	                
	                map = JsonUtil.getMap(jsonStr);
	            }
	            
	            dataMap.putAll(map);
            } catch(Exception e) {
            	log.error("Error reading JSON string: " + e.toString());
            }
		} else {
			dataMap.putAll(ServletHelper.getChangeParameters(request, null));
		}
		
		if(member != null) dataMap.setSession(member);
		
		if(request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			
			if(log.isDebugEnabled()) log.debug("file count = " + multipartRequest.getMultiFileMap().size());
			
			dataMap.putAll(ServletHelper.getBindMultipleFiles(multipartRequest.getMultiFileMap()));
		}
		
		long stime = System.currentTimeMillis();
		dataMap.put("RS_SERVICE_START_TIME", stime);
		
		request.setAttribute(Constants.DATA_MAP, dataMap);
		
		log.debug(dataMap);
		
		return true;
	}
	
	/**
	 * <p>
	 * Controller 진입 후 View가 랜더링되지 전에 수행하는 클래스
	 * </p>
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
            ModelAndView modelAndView) throws Exception {
		try {
			// 요청된 내용을 처리하기 전에 메모리를 체크해서 JVM을 70%이상을 사용하고 있으면 강제로 GC를 발생시킨다.
			float maxHeap = (float) Runtime.getRuntime().maxMemory() / 1000;   // KB로 표현
			float freeHeap = (float) Runtime.getRuntime().freeMemory() / 1000; // KB로 표현
			float useRate = ((maxHeap - freeHeap) / maxHeap);
			useRate = useRate * 100;
			
			if(log.isInfoEnabled()) log.info("JVM Heap Information(max size:" + maxHeap + "KB,free size: " + freeHeap + "KB, use rate:" + useRate + "%)");
			
			if(useRate > Constants.GARBAGE_COLLECTION_LIMIT_RATE) {
				System.gc();
				maxHeap = (float) Runtime.getRuntime().maxMemory() / 1000;   // KB로 표현
				freeHeap = (float) Runtime.getRuntime().freeMemory() / 1000; // KB로 표현
				useRate = ((maxHeap - freeHeap) / maxHeap);
				useRate = useRate * 100;
				if(log.isWarnEnabled()) log.warn("by user. execute Garbage Collection.(configurator rate:" + Constants.GARBAGE_COLLECTION_LIMIT_RATE + "%" +
				    "/ max size:" + maxHeap + "KB,free size: " + freeHeap + "KB, use rate:" + useRate + "%)");
			}
		} catch(com.sap.mw.jco.util.Enum.Exception e) {
			e.printStackTrace();
		}
	}
	
}
