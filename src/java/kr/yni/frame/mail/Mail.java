package kr.yni.frame.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import kr.yni.frame.Constants;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.util.FileUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.vo.ValueObject;


/**
 * <p/>
 * 메일 자체를 나타내는 클래스로 MultiPartBody를 지원한다.(Text, Image 및 첨부파일등)<br>
 * </p>
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Mail {
	
	private static final Log log = LogFactory.getLog(Mail.class);
	
	private static final String DEFAULT_CHARSET = Constants.APPLICATION_CONTEXT_CHARSET;
    
    private String mailCharset = DEFAULT_CHARSET;
    private static JavaMailSenderImpl mailInstance;
    
    private Session session;
    private MimeMessage message;
    
    private static String uname;
    private static String pword;
    
    @SuppressWarnings("static-access")
	private String contentType = "text/plain;charset=" + this.DEFAULT_CHARSET;
    
    /**
     * <p/>
     * 기본생성자이며, 기본적으로 첨부파일 없음 & charset을 utf-8로 지정한다.<br>
     * </p>
     */
    public Mail() {
    	session = MailSender.getSession();
    	mailInstance = MailSender.getInstance();

    	message = new MimeMessage(session);
    }
    
    /**
     * <p/>
     * 기본생성자이며, 기본적으로 첨부파일 없음 & charset을 utf-8로 지정한다.<br>
     * </p>
     */
    public Mail(JavaMailSenderImpl javaMail) {    	
    	Properties props = javaMail.getSession().getProperties();
		
		props.setProperty("mail.smtp.host", javaMail.getHost());
		props.setProperty("mail.mime.charset", DEFAULT_CHARSET);
		props.setProperty("mail.mime.encodefilename", "true");
		
		uname = javaMail.getUsername();
		pword = javaMail.getPassword();
		
		session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname, pword);
            }
        });
		
		if(log.isFatalEnabled()) log.fatal("Set Multiple Mail configration(" + props + ", user = " + javaMail.getUsername() + ", password = " + javaMail.getPassword() + ")");
		
		mailInstance = javaMail;
    	message = new MimeMessage(session);
    }
    
    /**
     * <p/>
     * 현재 설정된 문자셋을 얻어온다. 기본값은 "utf-8"이다.<br>
     * </p>
     *
     * @return <code>String</code>
     */
    public String getCharset() {
        return this.mailCharset;
    }

    /**
     * <p/>
     * 메일서버의 호스트명을 가져온다.<br>
     * </p>
     *
     * @return <code>String</code>
     */
    public String getHost() {
        return mailInstance.getHost();
    }
    
    /**
     * <p/>
     * 메일서버의 사용자명을 가져온다.<br>
     * </p>
     *
     * @return <code>String</code>
     */
    public String getUsername() {
        return mailInstance.getUsername();
    }
    
    /**
     * <p/>
     * 메일서버의 포트번호을 가져온다.<br>
     * </p>
     *
     * @return <code>int</code>
     */
    public int getPort() {
        return mailInstance.getPort();
    }
    
    /**
     * 디버그 여부를 설정한다.
     * @param enable
     */
    public void setDebug(boolean enable) { 
    	session.setDebug(enable);
    }
    
    /**
     * <p/>
     * 보내는 이의 메일주소를 셋팅한다.<br>
     * </p>
     *
     * @param fromAddress <code>String</code> 보내는 이의 이메일 주소
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public void setFrom(String fromAddress) throws FrameException {
        try {
        	message.setFrom(new InternetAddress(fromAddress));
        } catch(MessagingException ee) {
            throw new FrameException("Mail send failed(by From address)", ee.getMessage());
        }
    }

    /**
     * <p/>
     * 보내는 이의 메일주소와 이름을 셋팅한다.<br>
     * </p>
     *
     * @param fromAddress <code>String</code> 보내는 이의 이메일 주소
     * @param fromName    <code>String</code> 보내는 이의 이름
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     * @throws UnsupportedEncodingException 
     */
    public void setFrom(String fromAddress, String fromName) throws FrameException, AddressException, UnsupportedEncodingException {
    	try {
        	message.setFrom(new InternetAddress(fromAddress, fromName));
        } catch(Exception ee) {
            throw new FrameException("Mail send failed(by From address)", ee.getMessage());
        }
    }

    /**
     * <p/>
     * 받는 사람의 메일 주소를 추가한다.<br>
     * 입력은 <홍길도1><abc@kr.com>,<홍길도2><dfg@kr.com>형태로 여러 메일을 입력가능하다.<br>
     * </p>
     *
     * @param toAddress <code>String</code> 받는 이의 이메일 주소
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail addTo(String toAddress) throws FrameException {
        try {
        	message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(toAddress));
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by To address)", ee.getMessage());
        }

        return this;
    }

    /**
     * <p/>
     * 받는 사람의 메일 주소와 이름을 추가한다.<br>
     * 입력은 <홍길도1><abc@kr.com>,<홍길도2><dfg@kr.com>형태로 여러 메일을 입력가능하다.<br>
     * </p>
     *
     * @param toAddress <code>String</code> 받는 이의 이메일 주소
     * @param toName    <code>String</code> 받는 이의 이름
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail addTo(String toAddress, String toName) throws FrameException {
    	this.addTo(toAddress);
    	
        return this;
    }

    /**
     * <p/>
     * 받는 사람의 이메일 주소와 이름을 가지는 {@link kr.co.hit.live.vo.ValueObject} 로 메일 주소를 설정한다.<br>
     * {@link java.util.Collection } 타입으로 메일주소 리스트를 설정하면 내부에서 {@link javax.mail.internet.InternetAddress } 형태로 변환해서 처리한다.
     * 이 때, <code>ValueObjce</code>의 key는 받는 사람의 이메일주소이며 value는 받는 사람의 이름이다.<br>
     * 예) <code>List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();
	 * Map<String, Object> map = new LinkedHashMap<String, Object>();
	 * map.put("addr","amugae1@mailserver.com");
	 * map.put("name","아무개1");
	 * toList.add(map);
	 * map = new LinkedHashMap<String, Object>();
	 * map.put("addr","amugae2@mailserver.com");
	 * map.put("name","아무개2");
	 * toList.add(map);
	 * map = new LinkedHashMap<String, Object>();
	 * map.put("addr","amugae3@mailserver.com");
	 * map.put("name","아무개3");
	 * toList.add(map);
     * Mail m_mail = new Mail();<br>
     * m_mail.setTo(toList);<br></code>
     * </p>
     *
     * @param toListVo {@link kr.co.hit.live.vo.ValueObject } 받는 이의 이메일 주소와 이름
     *
     * @return {@link kr.co.hit.live.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setTo(List toList) throws FrameException {
    	ValueObject toListVo = transformVO(toList, null);
    	InternetAddress[] address = new InternetAddress[toList.size()];
    	int idx = 0;
    	
    	try {
	        for (Iterator<String> i = toListVo.getRow(0).keySet().iterator(); i.hasNext();) {
	            String toAddress = i.next();
	            String toName = toListVo.getString(toAddress);
	            
	            address[idx] = new InternetAddress(toAddress, toName);
	            idx++;
	        }
	        
	        message.setRecipients(MimeMessage.RecipientType.TO, address);
    	} catch(Exception me) {
    		throw new FrameException("Mail send failed(by To address list)", me.getMessage());
    	}
    	
        return this;
    }

    /**
     * <p/>
     * 받는 사람의 이메일 주소를 가지는 {@link kr.co.hit.live.vo.ValueObject} 로 메일 주소를 설정한다.<br>
     * 이 때 받는 사람의 이메일주소가 담긴 Key를 지정해준다.<br>
     * 사용예)
     * <pre>
     * List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();<br>
     * Map<String, Object> map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae1@mailserver.com");<br>
	 * map.put("name","아무개1");<br>
	 * toList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae2@mailserver.com");<br>
	 * map.put("name","아무개2");<br>
	 * toList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae3@mailserver.com");<br>
	 * map.put("name","아무개3");<br>
	 * toList.add(map);<br>
     * Mail m_mail = new Mail();<br>
     * m_mail.setTo(toList,"addr");<br>
     * </pre>
     * @param toListVo {@link kr.co.hit.live.vo.ValueObject } 받는 이의 이메일 주소
     * @param addrKey 받는이의 이메일 주소가 담긴 컬럼의 컬럼명
     * @return {@link kr.co.hit.live.mail.Mail }
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setTo(List toList, String addrKey) throws FrameException {
    	ValueObject toListVO = transformVO(toList, null);
    	
    	return setTo(toListVO, addrKey, null);
    }
    
    /**
    * <p/>
     * 받는 사람의 이메일 주소와 이름을 가지는 {@link kr.co.hit.live.vo.ValueObject} 로 메일 주소를 설정한다.<br>
     * 이 때 받는 사람의 이메일주소와 받는 사람의 이름이 담긴 Key를 각각 지정해준다.<br>
     * 사용예)
     * <pre>
     * List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();<br>
     * Map<String, Object> map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae1@mailserver.com");<br>
	 * map.put("name","아무개1");<br>
	 * toList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae2@mailserver.com");<br>
	 * map.put("name","아무개2");<br>
	 * toList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae3@mailserver.com");<br>
	 * map.put("name","아무개3");<br>
	 * toList.add(map);<br>
     * Mail m_mail = new Mail();
     * m_mail.setTo(toList,"addr","name");
     * </pre>
     *
     * @param toListVo {@link kr.co.hit.live.vo.ValueObject } 받는 이의 이메일 주소와 이름
     * @param addrKey 받는이의 이메일 주소가 담긴 컬럼의 컬럼명
     * @param nameKey 받는이의 이름이 담긴 컬럼의 컬럼명
     * @return {@link kr.co.hit.live.mail.Mail }
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setTo(List toList, String addrKey, String nameKey) throws FrameException {
    	ValueObject toListVO = transformVO(toList, null);
    	
    	return setTo(toListVO, addrKey, nameKey);
    }
    /**
     * <p/>
     * 받는 사람의 이메일 주소와 이름을 가지는 {@link kr.co.hit.live.vo.ValueObject} 로 메일 주소를 설정한다.<br>
     * 이 때 받는 사람의 이메일주소와 받는 사람의 이름이 담긴 Key를 각각 지정해준다.<br>
     * 사용예)
     * <pre>
     * List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();<br>
     * Map<String, Object> map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae1@mailserver.com");<br>
	 * map.put("name","아무개1");<br>
	 * toList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae2@mailserver.com");<br>
	 * map.put("name","아무개2");<br>
	 * toList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae3@mailserver.com");<br>
	 * map.put("name","아무개3");<br>
	 * toList.add(map);<br>
     * Mail m_mail = new Mail();
     * m_mail.setTo(toList,"addr","name");
     * </pre>
     *
     * @param toListVo {@link kr.co.hit.live.vo.ValueObject } 받는 이의 이메일 주소와 이름
     * @param addrKey 받는이의 이메일 주소가 담긴 컬럼의 컬럼명
     * @param nameKey 받는이의 이름이 담긴 컬럼의 컬럼명
     * @return {@link kr.co.hit.live.mail.Mail }
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    private Mail setTo(ValueObject toListVO, String addrKey, String nameKey) throws FrameException {
    	InternetAddress[] address = new InternetAddress[toListVO.size()];
    	int idx = 0;
    	try {
	    	for(int i=0;i<toListVO.size();i++) {
	    		String toAddress = toListVO.getString(i,addrKey);
				
	    		address[idx] = new InternetAddress(toAddress);
	            idx++;
	    	}
	    	
	    	message.setRecipients(MimeMessage.RecipientType.TO, address);
    	}  catch(MessagingException me) {
    		throw new FrameException("Mail send failed(by To address VO)", me.getMessage());
    	}
    	
    	return this;
    }
    
    /**
     * <p/>
     * 참조 이메일 주소를 추가한다.<br>
     * 입력은 <홍길도1><abc@kr.com>,<홍길도2><dfg@kr.com>형태로 여러 메일을 입력가능하다.<br>
     * </p>
     *
     * @param ccAddress <code>String</code> 참조 대상 이메일 주소
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail addCC(String ccAddress) throws FrameException {
        try {
            message.setRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(ccAddress));
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by CC address)", ee.getMessage());
        }

        return this;
    }

    /**
     * <p/>
     * 참조 이메일 주소를 추가한다.<br>
     * </p>
     *
     * @param ccAddress <code>String</code> 참조 대상 이메일 주소
     * @param ccName    <code>String</code> 참조 대상자 이름
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail addCC(String ccAddress, String ccName) throws FrameException {
        this.addCC(ccAddress);
        
        return this;
    }

    /**
     * <p/>
     * 참조 이메일 주소 리스트를 설정한다.<br>
     * {@link java.util.Collection } 타입으로 메일주소 리스트를 설정하면 내부에서 {@link javax.mail.internet.InternetAddress } 형태로 변환해서 처리한다.
     * </p>
     *
     * @param ccList {@link java.util.Collection } 참조 이메일 주소 리스트
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setCC(Collection<String> ccList) throws FrameException {
    	InternetAddress[] address = new InternetAddress[ccList.size()];
    	int idx = 0;

        for (Iterator<String> i = ccList.iterator(); i.hasNext();) {
            try {
                address[idx] = new InternetAddress(i.next());
                idx++;
            } catch(AddressException ae) {
            	throw new FrameException("Mail send failed(by CC address list)", ae.getMessage());
            }
        }
        
        try {
            message.setRecipients(MimeMessage.RecipientType.CC, address);
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by CC address list)", ee.getMessage());
        }

        return this;
    }

    /**
     * <p/>
     * 참조 이메일 주소와 참조 대상의 이름을 가지는 Object 로 메일 주소를 설정한다.<br>
     * {@link java.util.Collection } 타입으로 메일주소 리스트를 설정하면 내부에서 {@link javax.mail.internet.InternetAddress } 형태로 변환해서 처리한다.
     * 이 때, <code>List</code>의 key는 참조대상의 이메일주소이며 value는 참조대상의 이름이다.<br>
     * 사용예)
     * <pre>
     * List<Map<String, Object>> ccList = new ArrayList<Map<String, Object>>();<br>
     * Map<String, Object> map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae1@mailserver.com");<br>
	 * map.put("name","아무개1");<br>
	 * ccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae2@mailserver.com");<br>
	 * map.put("name","아무개2");<br>
	 * ccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae3@mailserver.com");<br>
	 * map.put("name","아무개3");<br>
	 * ccList.add(map);<br>
     * Mail m_mail = new Mail();<br>
     * m_mail.setTo(ccList);<br>
     * </p>
     *
     * @param ccListVo  받는 이의 이메일 주소와 이름
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setCC(List ccList) throws FrameException {
    	ValueObject ccListVo = transformVO(ccList, null);
    	InternetAddress[] address = new InternetAddress[ccList.size()];
    	int idx = 0;
    	
    	try {
	        for (Iterator<String> i = ccListVo.getRow(0).keySet().iterator(); i.hasNext();) {
	            String ccAddress = i.next();
	            String ccName = ccListVo.getString(ccAddress);
	
	            address[idx] = new InternetAddress(ccAddress, ccName);
	            idx++;
	        }
	        
	        message.setRecipients(MimeMessage.RecipientType.CC, address);
    	} catch(Exception me) {
    		throw new FrameException("Mail send failed(by CC address list)", me.getMessage());
    	}
    	
        return this;
    }

    /**
     * <p/>
     * 참조 이메일 주소를 가지는 Object 로 참조자의 메일 주소를 설정한다.<br>
     * 이 때 참조자의 이메일주소가 담긴 Key를 지정해준다.<br>
     * 사용예)
     * <pre>
     * List<Map<String, Object>> ccList = new ArrayList<Map<String, Object>>();<br>
     * Map<String, Object> map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae1@mailserver.com");<br>
	 * map.put("name","아무개1");<br>
	 * ccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae2@mailserver.com");<br>
	 * map.put("name","아무개2");<br>
	 * ccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae3@mailserver.com");<br>
	 * map.put("name","아무개3");<br>
	 * ccList.add(map);<br>
     * Mail m_mail = new Mail();
     * m_mail.setCC(ccList,"addr");
     * </pre>
     * @param ccListVo  참조 이메일 주소
     * @param addrKey 참조 이메일 주소가 담긴 컬럼의 컬럼명
     * @return {@link kr.yni.frame.mail.Mail }
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setCC(List ccList,String addrKey) throws FrameException {
    	ValueObject ccListVO = transformVO(ccList, null);
    	
    	return setCC(ccListVO,addrKey,null);
    }
    
    public Mail setCC(List ccList,String addrKey, String nameKey) throws FrameException {
    	ValueObject ccListVO = transformVO(ccList, null);
    	
    	return setCC(ccListVO, addrKey, nameKey);
    }
    /**
     * <p/>
     * 참조 이메일 주소와 이름을 가지는 Object 로 참조자의 메일 주소를 설정한다.<br>
     * 이 때 참조자의 이메일주소와 참조자의 이름이 담긴 Key를 각각 지정해준다.<br>
     * 사용예)
     * <pre>
     * List<Map<String, Object>> ccList = new ArrayList<Map<String, Object>>();<br>
     * Map<String, Object> map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae1@mailserver.com");<br>
	 * map.put("name","아무개1");<br>
	 * ccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae2@mailserver.com");<br>
	 * map.put("name","아무개2");<br>
	 * ccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae3@mailserver.com");<br>
	 * map.put("name","아무개3");<br>
	 * ccList.add(map);<br>
     * Mail m_mail = new Mail();
     * m_mail.setCC(ccList,"addr","name");
     * </pre>
     *
     * @param ccListVo  참조자의 이메일 주소와 이름
     * @param addrKey 참조 이메일 주소가 담긴 컬럼의 컬럼명
     * @param nameKey 참조자의 이름이 담긴 컬럼의 컬럼명
     * @return {@link kr.yni.frame.mail.Mail }
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    private Mail setCC(ValueObject ccListVO,String addrKey, String nameKey) throws FrameException {
    	InternetAddress[] address = new InternetAddress[ccListVO.size()];
    	int idx = 0;
    	
    	try {
	    	for(int i=0;i<ccListVO.size();i++) {
				String toAddress = ccListVO.getString(i,addrKey);
				
				address[idx] = new InternetAddress(toAddress);
	            idx++;
			}
	    	
	    	message.setRecipients(MimeMessage.RecipientType.CC, address);
    	} catch(MessagingException me) {
    		throw new FrameException("Mail send failed(by CC address VO)", me.getMessage());
    	}
    	
		return this;
    }
    
    /**
     * <p/>
     * 숨은 참조 이메일 주소를 추가한다.<br>
     * </p>
     *
     * @param bccAddress <code>String</code> 숨은 참조 이메일 주소
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail addBCC(String bccAddress) throws FrameException {
        try {
        	message.setRecipients(MimeMessage.RecipientType.BCC, InternetAddress.parse(bccAddress));
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by Hiden CC address)", ee.getMessage());
        }

        return this;
    }

    /**
     * <p/>
     * 숨은 참조 이메일 주소와 숨은 참조 대상의 이름를 추가한다.<br>
     * </p>
     *
     * @param bccAddress <code>String</code> 숨은 참조 이메일 주소
     * @param bccName    <code>String</code>  숨은 참조대상의 이름
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail addBCC(String bccAddress, String bccName) throws FrameException {
        this.addBCC(bccAddress);

        return this;
    }

    /**
     * <p/>
     * 숨은 참조 이메일 주소 리스트를 셋팅한다.<br>
     * {@link java.util.Collection } 타입으로 메일주소 리스트를 설정하면 내부에서 {@link javax.mail.internet.InternetAddress } 형태로 변환해서 처리한다.<br>
     * </p>
     *
     * @param bccList {@link java.util.Collection } 숨은 참조 대상의 이메일 리스트
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setBCC(Collection<String> bccList) throws FrameException {
    	InternetAddress[] address = new InternetAddress[bccList.size()];
    	int idx = 0;

        for (Iterator<String> i = bccList.iterator(); i.hasNext();) {
            try {
            	address[idx] = new InternetAddress(i.next());
                idx++;
            } catch(AddressException ae) {
            	throw new FrameException("Mail send failed(by Hiden CC address List)", ae.getMessage());
            }
        }

        try {
        	message.setRecipients(MimeMessage.RecipientType.BCC, address);
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by Hiden CC address List)", ee.getMessage());
        }

        return this;
    }

    /**
     * <p/>
     * 숨은 참조 이메일 주소와 숨은 참조 대상의 이름을 가지는 Object 로 메일 주소를 설정한다.<br>
     * {@link java.util.Collection } 타입으로 메일주소 리스트를 설정하면 내부에서 {@link javax.mail.internet.InternetAddress } 형태로 변환해서 처리한다.
     * 이 때, <code>ValueObjce</code>의 key는 숨은 참조대상의 이메일주소이며 value는 참조대상의 이름이다.<br>
     * 사용예)
     * <pre>
     * List<Map<String, Object>> bccList = new ArrayList<Map<String, Object>>();<br>
     * Map<String, Object> map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae1@mailserver.com");<br>
	 * map.put("name","아무개1");<br>
	 * bccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae2@mailserver.com");<br>
	 * map.put("name","아무개2");<br>
	 * bccList.add(map);<br>
	 * map = new LinkedHashMap<String, Object>();<br>
	 * map.put("addr","amugae3@mailserver.com");<br>
	 * map.put("name","아무개3");<br>
	 * bccList.add(map);<br>
     * Mail m_mail = new Mail();<br>
     * m_mail.setTo(bccList);<br>
     * </p>
     *</pre>
     *
     * @param bccListVo  숨은 참조 이메일 주소와 이름
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 주소형식이 옳지 않을 경우 발생
     */
    public Mail setBCC(List bccList) throws FrameException {
    	ValueObject bccListVo = transformVO(bccList, null);
    	InternetAddress[] address = new InternetAddress[bccList.size()];
    	int idx = 0;
    	
    	try {
	        for (Iterator<String> i = bccListVo.getRow(0).keySet().iterator(); i.hasNext();) {
	            String bccAddress = i.next();
	            String bccName = bccListVo.getString(bccAddress);
	
	            address[idx] = new InternetAddress(bccAddress, bccName);
	            idx++;
	        }
	        
	        message.setRecipients(MimeMessage.RecipientType.BCC, address);
    	} catch(Exception me) {
    		throw new FrameException("Mail send failed(by hiden CC address list)", me.getMessage());
    	}
    	
        return this;
    }

    /**
     * <p/>
     * 메일의 제목을 설정한다.<br>
     * </p>
     *
     * @param subject <code>String</code> 메일 제목
     *
     * @return {@link kr.yni.frame.mail.Mail }
     */
    public Mail setSubject(String subject) throws FrameException {
    	try {
    		message.setSubject(subject);
    	} catch(MessagingException me) {
    		throw new FrameException("Mail send failed(by subject)", me.getMessage());
    	}
    	
        return this;
    }

    /**
     * <p/>
     * 메일의 내용을 설정한다.<br>
     * </p>
     *
     * @param msg <code>String</code> 메일 내용
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 메일 본문형식이 옳지 않을 경우 발생<br>
     *                       참고 : {@link javax.mail.internet.MimeBodyPart}
     */
    public Mail setMsg(String msg) throws FrameException {
        try {
            message.setText(msg);
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by Contents)", ee.getMessage());
        }

        return this;
    }
    
    /**
     * <p/>
     * HTML 형식의 메일의 내용을 설정한다.<br>
     * </p>
     *
     * @param msg <code>String</code> 메일 내용
     *
     * @return {@link kr.yni.frame.mail.Mail }
     *
     * @throws FrameException 메일 본문형식이 옳지 않을 경우 발생<br>
     *                       참고 : {@link javax.mail.internet.MimeBodyPart}
     */
    @SuppressWarnings("static-access")
	public Mail setHtmlMsg(String msg) throws FrameException {
        try {
        	contentType = "text/html;charset=" + this.DEFAULT_CHARSET;
        	message.setContent(msg, contentType); // 보낼 내용 설정 (HTML 형식)
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by HTML's contents)", ee.getMessage());
        }

        return this;
    }

    /**
     * <p/>
     * 메일의 제목과 내용을 설정한다.<br>
     * </p>
     *
     * @param subject <code>String</code> 메일 제목
     * @param msg     <code>String</code> 메일 내용
     *
     * @return {@link kr.yni.frame.mail.Mail}
     *
     * @throws FrameException 메일 본문형식이 옳지 않을 경우 발생<br>
     *                       참고 : {@link javax.mail.internet.MimeBodyPart}
     */
    public Mail setContext(String subject, String msg) throws FrameException {
        this.setSubject(subject);
        this.setMsg(msg);

        return this;
    }

    /**
     * <p/>
     * 작성된 메일을 전송한다.<br>
     * </p>
     *
     * @throws FrameException 메일 전송중에 오류발생 시
     * @see javax.mail.MessagingException
     */
    public void send() throws FrameException {
        try {
        	if(log.isDebugEnabled()) log.debug("Content type = " + contentType);
        	
        	message.setSentDate(new Date());
        	Transport.send(message);
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(" + ee.getMessage() + ")", ee.getMessage());
        }
    }

    /**
     * <p/>
     * {@link kr.yni.frame.mail.MailAttachment } 개체로 정의한 첨부파일을 첨주한다.<br>
     * </p>
     *
     * @param attachment {@link kr.yni.frame.mail.MailAttachment } 미리 정의된 첨부파일 개체
     * @return {@link kr.yni.frame.mail.Mail }
     * @throws FrameException attachment가 NULL 이거나 파일 첨부 중 오류가 발생했을 경우
     */
    @SuppressWarnings("static-access")
	public Mail attach(MailAttachment attachment) throws FrameException {
    	Multipart multipart = new MimeMultipart();
    	MimeBodyPart contents_mimeBodyPart = new MimeBodyPart();
    	MimeBodyPart attachFile_mimeBodyPart = new MimeBodyPart();
    	
        if (attachment == null) {
            throw new FrameException("Attachment file is null.");
        }

        try {
        	// 메일발송 내역 추가
        	contents_mimeBodyPart = new MimeBodyPart();
        	
        	contents_mimeBodyPart.setContent(message.getContent(), contentType);
        	contents_mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");
        	
        	multipart.addBodyPart(contents_mimeBodyPart);
        	
        	if(log.isDebugEnabled()) log.debug("Attachment file name = " + attachment.getName());
        	
        	// 파일 추가
        	attachFile_mimeBodyPart.setFileName(attachment.getName());
        	attachFile_mimeBodyPart.setDataHandler(new DataHandler(new FileDataSource(new File(attachment.getPath()))));
        	attachFile_mimeBodyPart.setDescription(new File(attachment.getPath()).getName().split("\\.")[0], this.DEFAULT_CHARSET);
        	multipart.addBodyPart(attachFile_mimeBodyPart);
        	
        	message.setContent(multipart);
        } catch(MessagingException ee) {
        	throw new FrameException("Mail send failed(by attachment file)", ee.getMessage());
        } catch(IOException ee) {
        	throw new FrameException("Mail send failed(by attachment file)", ee.getMessage());
        }

        return this;
    }

    /**
     * <p/>
     * {@link kr.yni.frame.mail.MailAttachment} 또는 {@link java.util.Map} 개체로 정의한 
     * 첨부파일 리스트 {@link java.util.Collection } 를 이용해서 여러개의 첨부파일을 한번에 첨부한다.<br>
     * Map으로 정의할 때 명칭과 데이터 타입에 주의할 것<br>
     * map.put("FILE_DATA", java.sql.Blob);<br>
     * map.put("FILE_NAME", java.lang.String);<br>
     *
     * @param attachList {@link java.util.Collection } 첨부파일 리스트
     * @return {@link kr.yni.frame.mail.Mail }
     * @throws FrameException attachList가 NULL이가너 파일 첨부 중 오류가 발생한 경우
     */
    public Mail attach(Collection<Object> attachList) throws FrameException {
        if (attachList.size() < 1 || attachList == null) {
            throw new FrameException("Attachment file is null.");
        }
        
        for (Iterator<Object> i = attachList.iterator(); i.hasNext();) {
        	Object o = i.next();
            
        	if(o instanceof MailAttachment) this.attach((MailAttachment) o);
        	if(o instanceof Map) {
        		Map map = (Map) o;
        		this.attach((Blob) map.get("FILE_DATA"), StringHelper.null2void(map.get("FILE_NAME")));
        	}
        }
        
        return this;
    }
    
    /**
     * Tebale에서 읽은 Blob 정보를 메일에 포함한다.
     * @param attach {@link java.sql.Blob } 파일 데이터
     * @param fileName {@link java.lang.String } 파일명
     * @return
     * @throws FrameException
     */
    public Mail attach(Blob attach, String fileName) throws FrameException {
    	InputStream is = null;
    	OutputStream fileWriter = null;
    	
    	if (attach == null) {
            throw new FrameException("Attachment file date is null.");
        }
        
        try {
        	String path = FileUtil.getFullPath(null) + "/" + fileName;
        	is = attach.getBinaryStream();
        	File file = new File(path);
            fileWriter = new FileOutputStream(file);
             
            int read = 0;
            byte[] bytes = new byte[1024];
            
            while ((read = is.read(bytes)) != -1) { 
            	fileWriter.write(bytes, 0, read);
        	}
            
            MailAttachment attachment = new MailAttachment();
			
            attachment.setName(fileName);
            attachment.setPath(path);
			
			this.attach(attachment);
			
			FileUtil.deleteTo(path);
        } catch(IOException ioex) {
        	throw new FrameException("Mail send failed(by attachment file data)", ioex.getMessage());
        } catch(Exception e) {
        	throw new FrameException("Mail send failed(by attachment file data)", e.getMessage());
        } finally {
        	try {
	        	if(is != null) is.close();
	        	if(fileWriter != null) fileWriter.close();
        	} catch(IOException io) {
        		io.printStackTrace();
        	}
        }

        return this;
    }
    
    /**
	 * <code>List</code>객체를 <code>ValueObject</code>로 변환한다.
	 * 
	 * @param list
	 * @param name
	 * @return
	 */
	private static ValueObject transformVO(List list, String name) {
		ValueObject vo = null;
		
		if(StringHelper.isNull(name)) {
			vo = new ValueObject();
		} else {
			vo = new ValueObject(name);
		}
		
		for(int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			
			vo.addRow(i, map);
		}
		
		if(log.isDebugEnabled()) log.debug("vo size = " + vo.size());
		
		return vo;
	}
}
