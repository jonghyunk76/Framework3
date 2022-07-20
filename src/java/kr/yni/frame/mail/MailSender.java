package kr.yni.frame.mail;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import kr.yni.frame.Constants;

public class MailSender {
	
	private static Log log = LogFactory.getLog(MailSender.class);
	
	private static final String DEFAULT_CHARSET = Constants.APPLICATION_CONTEXT_CHARSET;
	
	private static JavaMailSenderImpl mailSender;
	private static Properties props;
	private static Session session;
    
	public MailSender(JavaMailSenderImpl sender) {
		if(mailSender == null) {
			mailSender = sender;
			
			props = sender.getSession().getProperties();
			
			props.setProperty("mail.smtp.host", sender.getHost());
			props.setProperty("mail.mime.charset", DEFAULT_CHARSET);
			props.setProperty("mail.mime.encodefilename", "true");
			
			if(log.isDebugEnabled()) log.debug("set Mail configration(" + props + ")");
			
			session = Session.getDefaultInstance(getProperties(), new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(mailSender.getUsername(), mailSender.getPassword());
	            }
	        });
		}
	}
	
	/**
	 * 메일을 보내기 위한 객체를 리턴한다.
	 * @return
	 */
	public static JavaMailSenderImpl getInstance() {
		return mailSender;
	}
	
	public static Properties getProperties() {
		return props;
	}
	
	public static Session getSession() {
		return session;
	}
	
}
