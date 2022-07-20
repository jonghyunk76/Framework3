package kr.yni.frame.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import kr.yni.frame.collection.DataMap;
import kr.yni.frame.web.upload.FormFile;

/**
 * HttpServletRequest의 요청 파라메터를 처리해 주는 클래스
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ServletHelper {
    
    private static Log log = LogFactory.getLog(ServletHelper.class);
            
    /**
     * 개별로 업로드된 MultipartFiles을 new FormFile로 변환해서 dataMap에 위치시킨다.
     * 
     * @param multipartFiles
     * @param dataMap
     */
    public static Map getBindMultipartFiles(Map<String, MultipartFile> multipartFiles) throws Exception {
        Map map = new HashMap();
        
        for (Iterator it = multipartFiles.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            MultipartFile value = (MultipartFile) entry.getValue();
            
            log.debug("get bind multipart file : key = " + key + ", value=" + value);
            
            if (!value.isEmpty()) {
                map.put(key, new FormFile(value));
            }
        }
        
        return map;
    }
    
    /**
     * Multiple로 업로드된 MultipartFiles을 new FormFile로 변환해서 dataMap에 위치시킨다.
     * 
     * @param multipartFiles
     * @param dataMap
     */
    public static Map getBindMultipleFiles(MultiValueMap<String, MultipartFile> multipartFiles) throws Exception {
        Map map = new HashMap();
        
        for (Iterator it = multipartFiles.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            List<MultipartFile> files = multipartFiles.get(entry.getKey());
            
            List fileList = new LinkedList();
            int idx = 0;
            
            if(files != null) {
	            for(int i = 0; i < files.size(); i++) {
	            	MultipartFile value = files.get(i);
	            	
	            	if (!value.isEmpty()) {
		            	log.debug("get bind multiple file : key = " + entry.getKey() + ", value=" + value);
		            	
		            	fileList.add(idx, new FormFile(value));
		            	idx++;
	            	}
	            }
            }
            
            if (fileList.size() > 0) {
                map.put(entry.getKey(), fileList);
            }
        }
        
        return map;
    }
    
    /**
     * MultipartFiles을 new FormFile로 변환해서 dataMap에 위치시킨다.
     * 
     * @param multipartFiles
     * @param dataMap
     */
    public static void getBindMultipartFiles(Map<String, MultipartFile> multipartFiles, DataMap dataMap)  throws Exception {
        for (Iterator it = multipartFiles.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            MultipartFile value = (MultipartFile) entry.getValue();
            
            if (!value.isEmpty()) {
                if(log.isDebugEnabled()) log.debug("file 객체 key name : [" + key + "]");
                
                dataMap.put(key, new FormFile(value));
            }
        }
    }
    
    /**
     * request 에 담긴 parameter만 빼내서 Map으로 저장한 후 리턴한다.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public static Map getReqeustParameters(ServletRequest request) throws Exception {
    	String flagEscape = "Y";
    	Map map = new HashMap();
        Enumeration enum2 = request.getParameterNames();
        
        if(request.getParameter("UN_ESC") == null) {
        	flagEscape = StringHelper.null2string(request.getAttribute("UN_ESC"), "Y");
        } else {
        	flagEscape = StringHelper.null2string(request.getParameter("UN_ESC"), "Y");
        }
        
        while (enum2 != null && enum2.hasMoreElements()) {
            String paramName = (String) enum2.nextElement();
            
            String value = StringHelper.null2void(request.getParameter(paramName));
            
            // 악성코드 삽을 막기 위해 script 구문은 제거시킨다.
            
            if(flagEscape.equals("Y")) {
            	map.put(paramName, StringHelper.unescape(value));
            } else {
            	map.put(paramName, value);
            }
        }
        
        return map;
    }
    
    /**
     * Like 검색을 위한 검색어를 변환시킨 후 리턴한다.
     * 
     * @param request
     * @param prefix
     * @return
     */
    public static Map getChangeParameters(ServletRequest request, String prefix) throws Exception {
    	String flagEscape = "Y";
    	Enumeration enum2 = request.getParameterNames();
        Map params = new HashMap();
        int ecount = 0;
        
        try {
	        if (prefix == null) {
	            prefix = "";
	        }
	        
	        if(request.getParameter("UN_ESC") == null) {
	        	flagEscape = StringHelper.null2string(request.getAttribute("UN_ESC"), "Y");
	        } else {
	        	flagEscape = StringHelper.null2string(request.getParameter("UN_ESC"), "Y");
	        }
	        
	        if(log.isDebugEnabled()) log.debug("unescape flag = " + flagEscape + ", prefix = " +  prefix);
	        
	        while (enum2 != null && enum2.hasMoreElements()) {
	            String paramName = (String) enum2.nextElement();
	            
	            if ("".equals(prefix) || paramName.startsWith(prefix)) {
	                String unprefixed = paramName.substring(prefix.length());
	                String[] values = request.getParameterValues(paramName);
	                
	                // Like 검색 쿼리에서 사용할 검색어를 재 정의한다.
	                if(paramName.equals("schKeyWord")) {
	                	if(!StringHelper.isNull(values[0])) {
		                    String likeKey = StringHelper.null2void(request.getParameter("schKeyLike"));
		                    String keyWord = StringHelper.null2void(values[0]).replace("\t", "").trim();
		                    
		                    if(log.isDebugEnabled()) log.debug("schKeyWord = " + keyWord + " / schKeyLike = " + likeKey);
		                     
		                    if(likeKey.equals("C")) {
		                        values[0] = "%"+keyWord+"%";
		                    } else if(likeKey.equals("S")) {
		                        values[0] = keyWord+"%";
		                    } else if(likeKey.equals("E")) {
		                        values[0] = "%"+keyWord;
		                    }
	                	}
	                	
	                    // 만약 멀티검색인 경우에는 keyword를 강제로 생성해서 멀티검색을 지원함(2019-05-17)
	                    if(!StringHelper.null2void(request.getParameter("multiData")).isEmpty()) {
	                    	values[0] = "multiData";
	                    	
	                    	if(log.isDebugEnabled()) log.debug("Multi search OK...");
	                    }
	                }
	                
	                StringBuffer sb = new StringBuffer();
	                
	                for(int i = 0; i < values.length ; i++) {
	                	if(values[i].equals(" ")) {
	                		sb.append(values[i].trim());
						} else {
							if(flagEscape.equals("Y")) {
								sb.append(StringHelper.unescape(values[i]));
							} else {
								sb.append(StringHelper.null2void(values[i]));
							}
						}
	                	if(i > 0 && (i+1) != values.length) {
	                		sb.append("|");
	                	}
	                }
	                
	                if(sb.length() > 100) {
	                	if(flagEscape.equals("Y")) {
		                	if(log.isDebugEnabled()) log.debug("(unescape) param name = " + paramName + ", value size = " + values.length + ", defore value = over data parameter... ");
		                } else {
		                	if(log.isDebugEnabled()) log.debug("(no change) param name = " + paramName + ", value size = " + values.length + ", defore value = over data parameter... ");
		                }
	                } else {
		                if(flagEscape.equals("Y")) {
		                	if(log.isDebugEnabled()) log.debug("(unescape) param name = " + paramName + ", value size = " + values.length + ", defore value = " + sb.toString() + ", after = " + StringHelper.unescape(sb.toString()));
		                } else {
		                	if(log.isDebugEnabled()) log.debug("(no change) param name = " + paramName + ", value size = " + values.length + ", defore value = " + sb.toString());
		                }
	                }
	                
	                if (values != null && values.length > 1) {
	                    for(int i = 0; i < values.length ; i++) {
	                    	if(values[i].equals(" ")) {
	    						values[i] = StringHelper.null2void(values[i]).trim();
	    					} else {
	    						if(flagEscape.equals("Y")) {
	    							values[i] = StringHelper.unescape(values[i]).trim();
	    						} else {
	    							values[i] = StringHelper.null2void(values[i]).trim();
	    						}
	    					}
	                    }
	                    params.put(unprefixed, values);
	                } else {
	                	if(flagEscape.equals("Y")) {
	                		params.put(unprefixed, StringHelper.unescape(values[0]).trim());
	                	} else {
	                		params.put(unprefixed, StringHelper.null2void(values[0]).trim());
	                	}
	                }
	            }
	            
	            ecount++;
	        }
	        
	        if(log.isInfoEnabled()) log.info("request paramter count = " + ecount + ", size = " + StringHelper.getByteLength(params.toString()));
        } catch(Exception e) {
        	if(log.isErrorEnabled()) log.error("FrameworkBasedInterceptor > ServletHelper.getChangeParameters Error : " + e);
        	throw e;
        }
        
        return params;
    }
}
