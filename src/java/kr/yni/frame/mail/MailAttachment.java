package kr.yni.frame.mail;

import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailAttachment;

import kr.yni.frame.Constants;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.util.StringHelper;

/**
 * 첨부파일을 명세한 클래스
 *
 * @author 
 */

public class MailAttachment {
    private String DEFAULT_FILE_CHARSET = Constants.APPLICATION_CONTEXT_CHARSET;
    
    private EmailAttachment emailAttachment;
    
    /**
     * <p>
     * 기본 생성자로 메일 첨부파일 개체를 생성한다.<br>
     * </p>
     */
    public MailAttachment() { 
    	emailAttachment = new EmailAttachment();
    }
    
    /**
     * <p>
     * 문자셋을 설정한다. default는 "EUC-KR"이다.<br>
     * {@link java.nio.charset.Charset } 참고
     * </p>
     *
     * @param charset <code>String</code> 설정할 문자셋
     */
    public void setCharset(String charset) throws Exception {
    	this.DEFAULT_FILE_CHARSET = charset;
    }

    /**
     * <p>
     * 현재 설정된 문자셋을 얻어온다. default는  "EUC_KR"이다.<br>
     * </P>
     *
     * @return <code>String</code> 현재 설정된 문자셋
     */
    public String getCharset() {
        return this.DEFAULT_FILE_CHARSET;
    }

    /**
     * <p>
     * 첨부할 파일의 절대경로를 입력한다.<br>
     * 첨부될 파일의 경로는 c:\file\added.jpg 처름 실제 파일이름을 포함하는 full path이다<br>
     * </p>
     *
     * @param path <code>String</code> 첨부될 파일의 실제 위치(파일이름 포함)
     */
    public void setPath(String path) {
        emailAttachment.setPath(path);
    }

    /**
     * <p>
     * 설정된 첨부 파일의 절대경로를 얻어온다.<br>
     * </p>
     *
     * @return <code>String</code> 설정된 첨부파일의 절대경로
     */
    public String getPath() {
        return emailAttachment.getPath();
    }

    /**
     * <p>
     * 첨부할 파일의 URL을 설정한다.<br>
     * </p>
     *
     * @param url {@link java.net.URL } 첨부할 파일이 위치한 URL
     */
    public void setURL(URL url) {
        emailAttachment.setURL(url);
    }

    /**
     * <p>
     * 설정된 첨부 파일의 URL을 얻어온다.<br>
     * </p>
     *
     * @return {@link java.net.URL }  설정된 첨부파일의 URL
     */
    public URL getURL() {
        return emailAttachment.getURL();
    }

    /**
     * <p>
     * 첨부파일의 별칭을 설정한다.<br>
     * </p>
     *
     * @param name <code>String</code> 첨부파일의 별칭으로 받는 이에게 첨부파일이름으로 보여진다.
     * @throws FrameException 설정된 encoding 방식이 올바르지 않으면 발생한다. 기본 encoding 방식은 "EUC-KR"이다
     */
    public void setName(String name) throws FrameException {
        try {
            emailAttachment.setName(MimeUtility.encodeText(name, StringHelper.null2string(this.getCharset(), this.DEFAULT_FILE_CHARSET), "B"));
        } catch (UnsupportedEncodingException uee) {
            throw new FrameException("인코딩 형식이 올바르지 않습니다.", uee);
        }
    }

    /**
     * <p>
     * 설정된 첨부파일의 별칭을 얻어온다.<br>
     * </p>
     *
     * @return <code>String</code> 설정된 첨부파일의 별칭
     */
    public String getName() {
        return emailAttachment.getName();
    }

    /**
     * <p>
     * 개체의 disposition을 설정한다.<br>
     * 기본값은 javax.mail.Part.ATTACHMENT 이다 <br>
     * </p>
     *
     * @param disposition <code>String</code> 원하는 disposition
     */
    public void setDisposition(String disposition) {
        emailAttachment.setDisposition(disposition);
    }

    /**
     * <p>
     * 설정된 disposition 값을 얻어온다.<br>
     * 기본값은 javax.mail.Part.ATTACHMENT 이다 <br>
     * </p>
     *
     * @return <code>String</code> 설정된 disposition
     */
    public String getDisposition() {
        return emailAttachment.getDisposition();
    }

    /**
     * <p>
     * EmailAttachment 개체를 반환한다.<br>
     * </p>
     *
     * @return {@link org.apache.commons.mail.EmailAttachment }
     * @throws FrameException MailAttachment 개체를 완전하게 설정하지 않았을 경우
     */
    public EmailAttachment getAttachment() throws FrameException {
        if (this.getPath() == "" && this.getURL().toString() == "") {
            throw new FrameException("첨부파일의 위치와 URL를 찾을 수 없습니다.");
        }
        
        return this.emailAttachment;
    }
}
