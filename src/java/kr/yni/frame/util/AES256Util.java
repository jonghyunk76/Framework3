package kr.yni.frame.util;

import java.io.UnsupportedEncodingException;  
import java.security.GeneralSecurityException;  
import java.security.Key;  
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;  
import javax.crypto.spec.IvParameterSpec;  
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.config.Configurator;
import kr.yni.frame.config.ConfiguratorException;
import kr.yni.frame.config.ConfiguratorFactory;
import kr.yni.frame.exception.FrameException;
/**
 * 방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 * 
 * @author jonghyunkim
 *
 */
public class AES256Util {
	
	private static Log log = LogFactory.getLog(AES256Util.class);
			
	private String iv;
	
    private Key keySpec;
    
    private String charset;

    /**
     * 16자리의 키값을 입력하여 객체를 생성한다.
     * 
     * @param key 암/복호화를 위한 키값(라이센스 키)
     * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
     * @throws ConfiguratorException 시스템 설정값을 조회하면서 발생하는 오류
     */
    public AES256Util(String key) throws UnsupportedEncodingException, ConfiguratorException, FrameException {
    	try {
	    	Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			this.charset = configurator.getString("application.context.charset");
			
			if(charset == null || charset.isEmpty()) charset = "UTF-8";
			
			log.debug("key value = " + key + ", key length = " + key.length() +", charset = " + this.charset);
			
			if(key == null || key.isEmpty() || key.length() != 15) {
				throw new FrameException("You used the wrong key.");
			} else {
				this.iv = "$" + key;
			}
	        
	        byte[] keyBytes = new byte[16];
	        byte[] b = key.getBytes(this.charset);
	        int len = b.length;
	        
	        if(len > keyBytes.length){
	            len = keyBytes.length;
	        }
	        
	        System.arraycopy(b, 0, keyBytes, 0, len);
	        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
	        
	        log.debug("keySpec = " + keySpec);
	        
	        this.keySpec = keySpec;
    	} catch(Exception e) {
    		log.error(e);
    		throw e;
    	}
    }
    
    /**
     * 16자리의 키값을 입력하여 객체를 생성한다.
     * 
     * @param key 암/복호화를 위한 키값(라이센스 키)
     * @param len 
     * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
     * @throws ConfiguratorException 시스템 설정값을 조회하면서 발생하는 오류
     */
    public AES256Util(String key, int lens) throws UnsupportedEncodingException, ConfiguratorException, FrameException {
    	try {
	    	Configurator configurator = ConfiguratorFactory.getInstance().getConfigurator();
			this.charset = configurator.getString("application.context.charset");
			
			if(charset == null || charset.isEmpty()) charset = "UTF-8";
			
			log.debug("key value = " + key + ", key length = " + key.length() +", charset = " + this.charset);
			
			this.iv = key.substring(0, lens);
	        
	        byte[] keyBytes = new byte[lens];
	        byte[] b = key.getBytes(this.charset);
	        int len = b.length;
	        
	        if(len > keyBytes.length){
	            len = keyBytes.length;
	        }
	        
	        System.arraycopy(b, 0, keyBytes, 0, len);
	        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
	        
	        log.debug("keySpec = " + keySpec);
	        
	        this.keySpec = keySpec;
    	} catch(Exception e) {
    		log.error(e);
    		throw e;
    	}
    }

    /**
     * AES256으로 암호화
     * 
     * @param str 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        
        byte[] encrypted = c.doFinal(str.getBytes(this.charset));
        String enStr = new String(Base64.encodeBase64(encrypted));
        
        return enStr;
    }

    /**
     * AES256으로 암호화된 문자열을 복호화
     * 
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        
        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        
        return new String(c.doFinal(byteStr), this.charset);
    }
    
}
