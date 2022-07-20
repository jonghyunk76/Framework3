package kr.yni.frame.dao.oracle;

import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OracleTypeHelper {
	
	private static final Log log = LogFactory.getLog(OracleTypeHelper.class);
	
	/**
     * <p>
     * BLOB 객체의 내용을 바이트 배열로 변환하여 리턴하는 메소드.
     * </p>
     *
     * @param blob 바이트 배열로 변환될 BLOB 객체
     * @return 변환된 바이트 배열
     */
    public static byte[] getBytes(Blob blob) throws SQLException {
        try {
            return blob.getBytes(1, (int) blob.length());
        } catch (SQLException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create bytes[] object from BLOB", e);
            }
            
            throw e;
        }
    }
    
    /**
     * <p>
     * CLOB 객체의 내용을 문자열 데이터로 변환하여 리턴하는 메소드.
     * </p>
     *
     * @param clob 문자열로 변환될 CLOB 객체
     * @return 변환된 문자열
     * @throws LiveException CLOB 객체에서 문자열 정보를 추출할 때 에러가 발생하는 경우
     */
    public static String getStringForCLOB(Clob clob) throws SQLException {
        StringBuffer sbf = new StringBuffer();
        Reader br = null;
        char[] buf = new char[1024];
        int readcnt;

        try {
            br = clob.getCharacterStream();
            
            while ((readcnt = br.read(buf, 0, 1024)) != -1) {
                sbf.append(buf, 0, readcnt);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create String object from CLOB", e);
            }
            throw (SQLException)new SQLException("fail to create String object from CLOB.").initCause(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Failed to close BufferedReader object", e);
                    }
                    throw (SQLException)new SQLException("fail to close BufferedReader object for CLOB").initCause(e);
                }
            }
        }
        return sbf.toString();
    }
}
