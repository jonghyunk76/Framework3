package kr.yni.frame.exception;

/**
 * ValueObject의 지정된 row를 초과했을 경우 발생시키는 Exception
 * 
 * @author YNI-maker
 * @since 2013. 5. 7. 오전 10:11:33
 * @version 1.0
 *
 * @see
 * YNI-maker 2013. 5. 7. Initial version
 *
 */
public class TooManyRowsException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public TooManyRowsException() {
        super();
    }
	
	public TooManyRowsException(String msg) {
        super(msg);
    }
}
