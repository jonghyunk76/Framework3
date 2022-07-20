package kr.yni.frame.reader.type;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import kr.yni.frame.reader.FileReader;

/**
 * XML Parsing를 처리하는 클래스
 * 
 * @author YNI-maker
 *
 */
public class XmlReader extends FileReader {
	
	private static Log log = LogFactory.getLog(XmlReader.class);
	
	@SuppressWarnings("rawtypes")
	public List read(File file, int index) throws Exception {
		return read(file, index, null);
	}
	
	@SuppressWarnings("rawtypes")
	public List read(File file, int index, String id) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document jdomdoc = null;
		
		try {
			if(file.isFile()) {
				jdomdoc = builder.build(file);
			} else {
				jdomdoc = new Document();
	 			
	 			Element newElement = new Element("jco");
	 			if(id != null && !id.isEmpty()) {
	 				newElement.setAttribute("id", id);
	 			}
	 			
	 			jdomdoc.addContent(newElement);
			}
		} catch(JDOMException jdm) {
			if(log.isErrorEnabled()) log.error(jdm.getCause());
		} catch(IOException io) {
			if(log.isErrorEnabled()) log.error(io.getCause());
		}
		
		return jdomdoc.getContent();
	}

	@Override
	public void write(List list, File file) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void write(List list, File file, String[] columns) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void view(List<List<Map<String, Object>>> list, Object workbook) throws Exception {
		// TODO Auto-generated method stub
	}

	public List read(File file, int index, String id, boolean formula)
			throws Exception {
		return this.read(file, index, id);
	}

	@Override
	public Workbook getWorkbook() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Workbook getWorkbook(File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
