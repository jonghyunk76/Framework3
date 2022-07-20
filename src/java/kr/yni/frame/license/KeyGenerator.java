package kr.yni.frame.license;

/**
 * CD Key 생성 클래스
 * 
 * @author jonghyunkim
 *
 */
public class KeyGenerator {
	
	/**
	 * 보안중요
	 * 
	 * @return 시디키
	 */
	public static String generateKey() {
        String rndKey = "";
        
        for(int i = 0; i < 12; i++) {
            rndKey += (int)(Math.random() * 10);
            if(i==3 || i==8) rndKey += "-";
        }
        
        return rndKey + getChecksum(rndKey);
    }
	
	/**
	 * 보안중요
	 * 
	 * @param generatedKey
	 * @return
	 */
    private static long getChecksum(String generatedKey) {
        long checksum = 3L;
        
        for(int i=1; i<=generatedKey.length(); i++) {
            if(i != 5 && i != 11) {
                checksum += (Long.parseLong(generatedKey.substring(i-1,i))^(2*checksum));
            }
        }
        
        return checksum % 10;
    }
    
}
