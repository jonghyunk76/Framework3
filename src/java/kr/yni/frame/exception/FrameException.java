package kr.yni.frame.exception;

import java.text.MessageFormat;

import kr.yni.frame.resources.MessageResource;

/**
 * <p>
 * 에플리케이션에서 발생한 에러나 이벤트에 대한 정보를 전달하기 위한 <code>Exception</code>클래스로서 다음 두 가지 형태로 사용이 가능하다. 
 * 첫 번째  방법은 
 * 대부분의  <code>Exception</code> 클래스들 처럼 해당 에러나 이벤트에 대한 완전한 메시지를 담아 전달하는 형태로 사용하는 것 
 * 두 번째 방법은
 * 에러나 이벤트에 대한 메시지를 원하는 시점에 원하는 형태대로  생성할 수 있도록 메시지 코드와 여기에 포함될 파라미터 값들을 담아서 
 * 전달하는 형태로 사용하는 것이다.
 * </p>
 * 
 * @author YNI-maker
 * @since 2013. 5. 7. 오전 10:18:27
 * @version 1.0
 *
 * @see
 * YNI-maker 2013. 5. 7. Initial version
 *
 */
public class FrameException extends Exception {
	
	private String locale;
	
	/**
     * <p>
     * 에러나 이벤트와 관련된 각종 메시지를 로깅하기 위한 Log 오브젝트
     * </p>
     */
	private static final long serialVersionUID = 7172530507104568224L;
	
	/**
     * <p>
     * 에러나 이벤트에 대한 메시지 생성 시에 포멧 스트링에 포함될 파라미터 값들
     * </p>
     */
    private Object[] params = null;

    /**
     * <p>
     * 에러나 이벤트에 대한 메시지 생성 시에 사용하는 포멧 스트링
     * </p>
     */
    private String formatString = null;
    
	/**
     * <p>
     * 아무런 인자도 갖지 않는 디폴트 <code>OriginException</code> 컨스트럭터
     * </p>
     */
	public FrameException() {
		this(null, null, null);
	}
	
	/**
	 * <p>
	 * 에러나 이벤트에 대한 메시지나 해당 메시지의 생성을 위한 메시지 포맷 스트링을 나타내는
     * 키(Key)만을 인자로 갖는 <code>OriginException</code> 컨스트럭터
	 * </p>
	 * 
	 * @param key <code>messageSource</code>에 등록된 메시지 코드
	 */
	public FrameException(String key) {
		this(key, null, null);
	}
	
	/**
	 * <p>
	 * 에러나 이벤트에 대한 메시지나 해당 메시지의 생성을 위한 메시지 포맷 스트링을 나타내는
     * 키(Key)와 메시지 포맷 스트링에 적용될 파라메터을 인자로 갖는 <code>OriginException</code> 컨스트럭터
	 * </p>
	 * 
	 * @param key <code>messageSource</code>에 등록된 메시지 코드
	 * @param params 메시지 포멧 스트링에 적용될 파라미터 값들로 최대 4개까지 사용 가능
	 */
	public FrameException(String key, Object[] params) {
		this(key, params, null);
	}
	
	/**
     * <p>
     * 에러나 이벤트에 대한 메시지의 생성을 위한 메시지 포멧 스트링을 나타내는 키(Key)와
     * 해당 메시지 포멧 스트링에 적용될 한 개의 파라미터 값을 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는 키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     */
    public FrameException(String key, Object param1) {
        this(key, new Object[]{param1}, null);
    }

    /**
     * <p>
     * 에러나 이벤트에 대한 메시지의 생성을 위한 메시지 포멧 스트링을 나타내는 키(Key)와
     * 해당 메시지 포멧 스트링에 적용될 한 개의 파라미터 값을 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는 키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될 두 번째 파라미터 값
     */
    public FrameException(String key, Object param1, Object param2) {
        this(key, new Object[]{param1, param2}, null);
    }

    /**
     * <p>
     * 에러나 이벤트에 대한 메시지의 생성을 위한 메시지 포멧 스트링을 나타내는 키(Key)와
     * 해당 메시지 포멧 스트링에 적용될 한 개의 파라미터 값을 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는 키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될 두 번째 파라미터 값
     * @param param3 메시지 포멧 스트링에 적용될 세 번째 파라미터 값
     */
    public FrameException(String key, Object param1, Object param2, Object param3) {
        this(key, new Object[]{param1, param2, param3}, null);
    }

    /**
     * <p>
     * 에러나 이벤트에 대한 메시지의 생성을 위한 메시지 포멧 스트링을 나타내는 키(Key)와
     * 해당 메시지 포멧 스트링에 적용될 한 개의 파라미터 값을 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는 키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될 두 번째 파라미터 값
     * @param param3 메시지 포멧 스트링에 적용될 세 번째 파라미터 값
     * @param param4 메시지 포멧 스트링에 적용될 네 번째 파라미터 값
     */
    public FrameException(String key, Object param1, Object param2, Object param3, Object param4) {
        this(key, new Object[]{param1, param2, param3, param4}, null);
    }
    
    /**
     * <p>
     * 에러나 이벤트의 원인이 되는 <code>Exception</code>과 이에 대한 메시지나  해당 메시지의
     * 생성을 위한 메시지 포멧 스트링을 나타내는 키(Key)를  인자로 갖는
     * <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key   에러나 이벤트에 대한 메시지나 해당 메시지의 생성을 위한 메시지 포멧 스트링을
     *              나타내는 키(Key)
     * @param cause 에러나 이벤트의 원인이 되는 <code>Exception</code>
     */
    public FrameException(String key, Throwable cause) {
        this(key, null, cause);
    }
    
    /**
     * <p>
     * 에러나 이벤트의 원이이 되는 <code>Exception</code>과 이에 대한  메시지의 생성을 위한
     * 메시지 포멧 스트링을 나타내는 키(Key)와  메시지 포멧 스트링에 적용될  한 개의 파라미터 값을
     * 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는  키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     * @param cause  에러나 이벤트의 원인이 되는 <code>Exception</code>
     */
    public FrameException(String key, Object param1, Throwable cause) {
        this(key, new Object[]{param1}, cause);
    }

    /**
     * <p>
     * 에러나 이벤트의 원이이 되는 <code>Exception</code>과 이에 대한  메시지의 생성을 위한
     * 메시지 포멧 스트링을 나타내는 키(Key)와  메시지 포멧 스트링에 적용될  두 개의 파라미터 값을
     * 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는  키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될 두 번째 파라미터 값
     * @param cause  에러나 이벤트의 원인이 되는 <code>Exception</code>
     */
    public FrameException(String key, Object param1, Object param2, Throwable cause) {
        this(key, new Object[]{param1, param2}, cause);
    }

    /**
     * <p>
     * 에러나 이벤트의 원이이 되는 <code>Exception</code>과 이에 대한  메시지의 생성을 위한
     * 메시지 포멧 스트링을 나타내는 키(Key)와  메시지 포멧 스트링에 적용될  세 개의 파라미터 값을
     * 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는  키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될 두 번째 파라미터 값
     * @param param3 메시지 포멧 스트링에 적용될 세 번째 파라미터 값
     * @param cause  에러나 이벤트의 원인이 되는 <code>Exception</code>
     */
    public FrameException(String key, Object param1, Object param2, Object param3, Throwable cause) {
        this(key, new Object[]{param1, param2, param3}, cause);
    }

    /**
     * <p>
     * 에러나 이벤트의 원이이 되는 <code>Exception</code>과 이에 대한  메시지의 생성을 위한
     * 메시지 포멧 스트링을 나타내는 키(Key)와  메시지 포멧 스트링에 적용될  네 개의 파라미터 값을
     * 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     *
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는  키(Key)
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될 두 번째 파라미터 값
     * @param param3 메시지 포멧 스트링에 적용될 세 번째 파라미터 값
     * @param param4 메시지 포멧 스트링에 적용될 네 번째 파라미터 값
     * @param cause  에러나 이벤트의 원인이 되는 <code>Exception</code>
     */
    public FrameException(String key, Object param1, Object param2, Object param3, Object param4, Throwable cause) {
        this(key, new Object[]{param1, param2, param3, param4}, cause);
    }
    
    /**
     * <p>
     * 에러나 이벤트의 원이이 되는 <code>Exception</code>과 이에 대한  메시지의 생성을 위한
     * 메시지 포멧 스트링을 나타내는 키(Key)와  메시지 포멧 스트링에 적용될  파라미터 값들을
     * 인자로 갖는 <code>OriginException</code> 컨스트럭터
     * </p>
     * 
     * @param key    생성될 메시지를 위한 메시지 포멧 스트링을 나타내는  키(Key)
     * @param params 메시지 포멧 스트링에 적용될 파라미터 값들로 최대 4개까지 사용 가능
     * @param cause  에러나 이벤트의 원인이 되는 <code>Exception</code>
     */
    public FrameException(String key, Object[] params, Throwable cause) {
    	super(key, cause);
    	
    	if (key != null) {
            this.params = params;
            this.formatString = MessageResource.getMessageInstance().getMessage(key); // 메시지 소스에서 포맷 스트링을 구한다.
        } else {
            this.params = null;
        }
    }
    
    /**
     * <p>
     * 메시지 포멧 스트링에 적용될 파라미터 값들을 리턴한다.
     * </p>
     *
     * @return 메시지 포멧 스트링에 적용될 파라미터 값들
     */
    public Object[] getParams() {
        return (this.params);
    }
    
    /**
     * 메시지 포멧 스트링에 적용된 idx 번째 파라메터 값을 리턴한다.
     * 해당 객체가 없거나 범위를 벋어나면 null을 리턴한다.
     * @param idx
     * @return 파라메터 Object
     */
    public Object getParam(int idx) {
    	if (params == null) {
    		return null;
    	}
    	
    	if (idx < params.length) {
    		return params[idx];
    	} else {
    		return null;
    	}
    }
    
    /**
     * <p>
     * 메시지 포멧 스트링에 적용될 파라미터 값들을 설정한다.
     * </p>
     *
     * @param params 메시지 포멧 스트링에 적용될 파라미터 값들로 최대 4개 까지 사용 가능
     */
    public void setParams(Object[] params) {
        this.params = params;
    }
    
    /**
     * <p>
     * 메시지 포멧 스트링에 적용될 한 개의 파라미터 값을 설정한다.
     * </p>
     *
     * @param param1 메시지 포멧 스트링에 적용될 첫 번째 파라미터 값
     */
    public void setParams(Object param1) {
        this.params = new Object[]{param1};
    }

    /**
     * <p>
     * 메시지 포멧 스트링에 적용될 두  개의 파라미터 값을 설정한다.
     * </p>
     *
     * @param param1 메시지 포멧 스트링에 적용될  첫 번째  파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될  두 번째  파라미터 값
     */
    public void setParams(Object param1, Object param2) {
        this.params = new Object[]{param1, param2};
    }

    /**
     * <p>
     * 메시지 포멧 스트링에 적용될 세 개의 파라미터 값을 설정한다.
     * </p>
     *
     * @param param1 메시지 포멧 스트링에 적용될  첫 번째  파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될  두 번째  파라미터 값
     * @param param3 메시지 포멧 스트링에 적용될  세 번째  파라미터 값
     */
    public void setParams(Object param1, Object param2, Object param3) {
        this.params = new Object[]{param1, param2, param3};
    }

    /**
     * <p>
     * 메시지 포멧 스트링에 적용될 네 개의 파라미터 값을 설정한다.
     * </p>
     *
     * @param param1 메시지 포멧 스트링에 적용될  첫 번째  파라미터 값
     * @param param2 메시지 포멧 스트링에 적용될  두 번째  파라미터 값
     * @param param3 메시지 포멧 스트링에 적용될  세 번째  파라미터 값
     * @param param4 메시지 포멧 스트링에 적용될  네 번째  파라미터 값
     */
    public void setParams(Object param1, Object param2, Object param3, Object param4) {
        this.params = new Object[]{param1, param2, param3, param4};
    }

    /**
     * <p>
     * 메시지 포멧 스트링을 나타내는 키(Key)값을 리턴한다.
     * </p>
     *
     * @return 메시지 포멧 스트링을 나타내는 키(Key)
     */
    public String getKey() {
        return super.getMessage();
    }
    
    /**
     * <p>
     * 파라메터가 대체되어 변환된 메시지 스트링을 리턴한다.
     * 메시지 포맷 스트링은 <code>OriginException</code>이 생성될 때 만들어 진 것을 사용한다.
     * </p>
     *
     * @return 메시지 스트링
     */
    public String getMessage() {
        // 메시지 파일에 등록된 메시지를 가져온다.
        String msgString = null;

        if (formatString == null) {
            msgString = getKey();
        } else {
        	// MessageFormat를 이용하여 파라메터를 맵핑시킨 메시지 포맷 스트링을 생성한다.
        	// 예) "나는 {0}이다."에 Object[]{java}인 경우에는 "나는 java이다."로 표현된다. 
            MessageFormat format = new MessageFormat(formatString);
            msgString = format.format(params);
        }
        
        return msgString;
    }
    
    /**
     * <p>
     * <code>OriginException</code> 오브젝트에 대한 스트링 표현 값을 리턴한다.
     * </p>
     *
     * @return <code>OriginException</code> 오브젝트에 대한 스트링 표현
     */
    public String toString() {
        String answer = super.toString();

        if (this.getMessage() == null) {
            return answer;
        }

        if (this.params == null) {
            return answer;
        }

        StringBuffer buf = new StringBuffer(answer);

        for (int i = 0; i < this.params.length; ++i) {
            buf.append("\n\t");
            buf.append("param{");
            buf.append(i);
            buf.append("}=");
            buf.append(this.params[i]);
        }
        buf.append("\n\t");
        buf.append("message=");
        buf.append(this.getMessage());

        return buf.toString();
    }
    
    /**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}
	
    /**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
}