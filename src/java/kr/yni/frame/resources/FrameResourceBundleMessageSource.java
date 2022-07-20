package kr.yni.frame.resources;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import kr.yni.frame.Constants;
import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorFactory;
import kr.yni.frame.util.JsonUtil;
import kr.yni.frame.util.StringHelper;

public class FrameResourceBundleMessageSource extends
        ReloadableResourceBundleMessageSource {
    private static Log log = LogFactory.getLog(FrameResourceBundleMessageSource.class);

    public FrameResourceBundleMessageSource() { }

    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        StringBuffer parseMsg = new StringBuffer();
        MessageFormat format = null;
        String rstMsg = null;
        
        if (!StringHelper.isNull(code)) {
            StringTokenizer splist = new StringTokenizer(this.getLanguageMessage(code, locale), ",");
            
            int cnt = 0;
            
            while(splist.hasMoreElements()) {
                String str = splist.nextToken();
                
                if(Locale.ENGLISH == locale || locale.getCountry() == "EN") { // 영문인 경우에는 무조건 공백을 추가한다.
                    if(cnt != 0) parseMsg.append(" ");
                } if("es" == locale.getLanguage() || locale.getCountry() == "ES") { // 스페인어인 경우에는 무조건 공백을 추가한다.
                    if(cnt != 0) parseMsg.append(" ");
                } if("vi" == locale.getLanguage() || locale.getCountry() == "VN") { // 베트남인 경우에는 무조건 공백을 추가한다.
                    if(cnt != 0) parseMsg.append(" ");
                } else { // 공백이 있을 경우 공백을 추가한다.
                    if(str.startsWith(" ")) parseMsg.append(" ");
                    if(str.endsWith(" ")) parseMsg.append(" ");
                }
                
                str = str.trim();
                format = this.resolveCode(str, locale);
                
                if (format == null) {
                    parseMsg.append(str);
                } else {
                    parseMsg.append(super.resolveCodeWithoutArguments(str, locale));
                }
                
                cnt++;
            }
        }
        
        rstMsg = parseMsg.toString();
        
        try {
        	String nation = locale.getCountry();
        	if(nation == null || nation.isEmpty()) {
        		if(Locale.ENGLISH == locale || locale.getCountry() == "EN") {
        			nation = "EN";
        		} else if("es" == locale.getLanguage() || locale.getCountry() == "ES") {
        			nation = "ES";
        		} else if("vi" == locale.getLanguage() || locale.getCountry() == "VN") {
        			nation = "VN";
        		} else if(Locale.KOREA == locale || Locale.KOREAN == locale || locale.getCountry() == "KR" || locale.getCountry() == "KO") {
        			nation = "KR";
        		}
        	}
        	
	        Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
	        String cword = configurator.getString("CONVERSION_WORD_"+nation);
			
			// 특정 문자열을 변경(김종현, 2019-05-16)
			if(cword != null && !cword.isEmpty()) {
				List clist = JsonUtil.getList(cword);
				
//				log.debug("[resolveCodeWithoutArguments] 1.conversion word = " + cword + ", nation = " + nation);
				
				if(clist.size() > 0) {
					for(int i = 0; i < clist.size(); i++) {
						Map cMap = (Map) clist.get(i);
						String name = StringHelper.null2void(cMap.get("name"));
						String word = StringHelper.null2void(cMap.get("word"));
						
						rstMsg = rstMsg.replace(name, word);
					}
					
//					log.debug("[resolveCodeWithoutArguments] 2.conversion message = " + rstMsg);
				}
			}
        } catch(Exception e) {
        	if(log.isErrorEnabled()) log.error(e);
        }
        
        return rstMsg;
    }
    
    protected Object[] resolveArguments(Object[] args, Locale locale) {
    	MessageFormat format = null;
    	
        for(int i = 0; i < args.length; i++) {
        	String code = StringHelper.null2void(args[i]);
        	StringBuffer parseMsg = new StringBuffer();
        	
        	if(!code.isEmpty()) {
        		StringTokenizer splist = new StringTokenizer(this.getLanguageMessage(code, locale), ",");
        		
        		int cnt = 0;
        		
        		while(splist.hasMoreElements()) {
        			String str = splist.nextToken();
        			
        			if(Locale.ENGLISH == locale || locale.getCountry() == "EN") { // 영문인 경우에는 무조건 공백을 추가한다.
                        if(cnt != 0) parseMsg.append(" ");
                    } else { // 공백이 있을 경우 공백을 추가한다.
                        if(str.startsWith(" ")) parseMsg.append(" ");
                        if(str.endsWith(" ")) parseMsg.append(" ");
                    }
        			
        			str = str.trim();
                    format = this.resolveCode(str, locale);
                    
                    if (format == null) {
                        parseMsg.append(str);
                    } else {
                        parseMsg.append(super.resolveCodeWithoutArguments(str, locale));
                    }
                    
                    cnt++;
        		}
        		
        		args[i] = parseMsg.toString();
                
                try {
                	String nation = locale.getCountry();
                	if(nation == null || nation.isEmpty()) {
                		if(Locale.ENGLISH == locale || locale.getCountry() == "EN") {
                			nation = "EN";
                		} else if("es" == locale.getLanguage() || locale.getCountry() == "ES") {
                			nation = "ES";
                		} else if("vi" == locale.getLanguage() || locale.getCountry() == "VN") {
                			nation = "VN";
                		} else if(Locale.KOREA == locale || Locale.KOREAN == locale || locale.getCountry() == "KR" || locale.getCountry() == "KO") {
                			nation = "KR";
                		}
                	}
                	
        	        Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
        	        String cword = configurator.getString("CONVERSION_WORD_"+nation);
        			
        			// 특정 문자열을 변경(김종현, 2019-05-16)
        			if(cword != null && !cword.isEmpty()) {
        				List clist = JsonUtil.getList(cword);
        				
//        				log.debug("[resolveArguments] 1.conversion word = " + cword + ", nation = " + nation);
        				
        				if(clist.size() > 0) {
        					for(int k = 0; k < clist.size(); k++) {
        						Map cMap = (Map) clist.get(k);
        						String name = StringHelper.null2void(cMap.get("name"));
        						String word = StringHelper.null2void(cMap.get("word"));
        						
        						args[i] = StringHelper.null2void(args[i]).replace(name, word);
        					}
        					
//        					log.debug("[resolveArguments] 2.conversion message = " + args[i]);
        				}
        			}
                } catch(Exception e) {
                	if(log.isErrorEnabled()) log.error(e);
                }
        	}
        }
        
        return args;
    }
    
    /**
     * <p>
     * 다국어의 어순이 맞지 않을 경우 적용할 용어를 지정할 수 있음<br>
     * 용어의 선택은 국가별 언어로 처리됨<br>
     * ^EN:영어, ^JP:일어, ^KR:한글, none:모국어<br>
     * <사용법><br>
     * messageCode = "<spring:message code='LOCAL,^KR,KOR,^EN,ENG,^JP,JAPAN'/>"
     * </p>
     * - 스페인어(ES)는 추후 번역작업 시 정상적으로 등록하기 위해 현재는 과거 메시지코드를 그대로 사용함(2020.05.06) 
	 * - 통관 시스템의 메시지는 CC_+메시지로 규정하고 실제 다국어 표시할 경우 CC_를 제거함(메시지 다국어 처리 시 CC_로 검색하면 됨)
	 * 
     * @param messageCode 메시지 코드
     * @parma locale      언어
     * @return
     */
    private String getLanguageMessage(String messageCode, Locale locale) {
        String message = null;
        String[] language = {"^EN", "^JP", "^KR", "^ES", "^VN"};
        String delimiter = null;
        int lastIndex = 0;
        int langIndex = 0;
        
        if(Locale.ENGLISH == locale || locale.getCountry().equals("EN")) {
        	delimiter = language[0];
        } else if(Locale.JAPAN == locale || Locale.JAPANESE == locale || locale.getCountry().equals("JP")) {
        	delimiter = language[1];
        } else if(Locale.KOREA == locale || Locale.KOREAN == locale || locale.getCountry().equals("KR")) {
        	delimiter = language[2];
        } else if(locale.getCountry().equals("ES")) {
        	delimiter = language[3];
        } else if(locale.getCountry().equals("VN")) {
        	delimiter = language[4];
        }
        
        if(delimiter == null) {
        	//if(log.isDebugEnabled()) log.debug("Origin message = " + messageCode + ", locale = " + locale.getCountry() + ", delimiter = " + delimiter);
        	return messageCode;
        }
        
        int startIndex = messageCode.indexOf(delimiter);
        
        if(startIndex < 0) {
        	startIndex = 0;
        } else {
        	startIndex = startIndex + 4;
        }

    	// 마지막 인덱스 번호 구하기
    	for(int i = 0; i < language.length; i++) {
    		langIndex = messageCode.indexOf(language[i]);
    		
        	if(startIndex < langIndex && (lastIndex == 0 || lastIndex >= langIndex)) {
        		lastIndex = langIndex;
        	}
        }
    	
    	if(lastIndex == 0) {
    		message = messageCode.substring(startIndex);
    	} else {
    		message = messageCode.substring(startIndex, lastIndex - 1);
    	}
        
    	if(lastIndex > 0) {
    		if(log.isDebugEnabled()) log.debug("Origin message = " + messageCode + ", Code = " + message + "[start = " + startIndex + ", last = " + (lastIndex-1) + "]");
    	}
    	
//    	log.debug("message before = " + message);
    	
    	// 메시지가 변환된 내용인지 체크
    	boolean changeYn = false;
    	
    	if(message.indexOf(".") > 1) { // .이 있으면 변환된 메시지로 인식
    		changeYn = true;
    	}
    	
    	if(!changeYn) {
	    	if(!locale.getCountry().equals("ES") && !messageCode.startsWith("CC_")) { // 스페인어는 나중에 번역작업 후 등록할 예정임
	    		if(messageCode.startsWith("SS_")) {
	    			message = StringHelper.replace(messageCode, "SS_", "");
	    		} else {
			    	if(Constants.APPLICATION_CHANGE_MSGCODE) {
			    		message = StringHelper.replace(StringHelper.replace(messageCode, ",", "_"), " ", "");
			    		message = StringHelper.changeCode4Message(message);
			    	}
	    		}
	    	} else if(messageCode.startsWith("CC_")) {
	    		message = StringHelper.replace(messageCode, "CC_", "");
	    	}
    	}
    	
//    	log.debug("message after = " + message);
    	
        return message;
    }
    
}
