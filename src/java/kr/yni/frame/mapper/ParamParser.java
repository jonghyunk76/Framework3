package kr.yni.frame.mapper;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import kr.yni.frame.mapper.element.Changing;
import kr.yni.frame.mapper.element.Column;
import kr.yni.frame.mapper.element.Export;
import kr.yni.frame.mapper.element.Function;
import kr.yni.frame.mapper.element.Import;
import kr.yni.frame.mapper.element.Jco;
import kr.yni.frame.mapper.element.Parameter;
import kr.yni.frame.mapper.element.Table;
import kr.yni.frame.util.StringHelper;

/**
 * <p>
 * RFC 인터페이스 정보를 획득하기 위한 xml parser 클래스
 * </p>
 * 
 * @author 김종현 (ador218@bluesolution.co.kr)
 * 
 */
public class ParamParser extends DefaultHandler {
	protected static Log log = LogFactory.getLog(ParamParser.class);

	private static final String JCO_NODE = "jco";
	private static final String ATTR_JCO_ID = "id";

	private static final String FUNCTION_NODE = "function";
	private static final String ATTR_FUNCTION_NAME = "name";
	private static final String ATTR_FUNCTION_DESC = "desc";

	private static final String IMPORT_NODE = "import";
	private static final String IMPORT_NAME = "name";
	
	private static final String EXPORT_NODE = "export";
	private static final String EXPORT_NAME = "name";
	
	private static final String CHANGING_NODE = "changing";
	private static final String CHANGING_NAME = "name";
	
	private static final String TABLE_NODE = "table";
	private static final String TABLE_NAME = "name";
	
	private static final String PARAMETER_NODE = "parameter";
	private static final String ATTR_PARAMETER_NAME = "name";
	private static final String ATTR_PARAMETER_TYPE = "type";
	private static final String ATTR_PARAMETER_OFFSET = "offset";
	private static final String ATTR_PARAMETER_LENGTH = "length";
	private static final String ATTR_PARAMETER_DECIMALS = "decimals";
	private static final String ATTR_PARAMETER_TRANS = "trans";
	private static final String ATTR_PARAMETER_DESC = "desc";
	private static final String ATTR_PARAMETER_DEFAULT = "default";
	
	private static final String COLUMN_NODE = "column";
	private static final String ATTR_COLUMN_NAME = "name";
	private static final String ATTR_COLUMN_TYPE = "type";
	private static final String ATTR_COLUMN_OFFSET = "offset";
	private static final String ATTR_COLUMN_LENGTH = "length";
	private static final String ATTR_COLUMN_DECIMALS = "decimals";
	private static final String ATTR_COLUMN_TRANS = "trans";
	private static final String ATTR_COLUMN_REQUIRED = "required";
	private static final String ATTR_COLUMN_FORMAT = "format";
	private static final String ATTR_COLUMN_DEFAULT = "default";
	private static final String ATTR_COLUMN_BASECODE = "basecode";
	private static final String ATTR_COLUMN_ALERT = "alert";
	private static final String ATTR_COLUMN_MESSAGE = "message";
	private static final String ATTR_COLUMN_DESC = "desc";

	private Jco jco = null;
	private Jco currentJco = null;
	private Function currentFunction = null;
	private Import currentImport = null;
	private Export currentExport = null;
	private Changing currentChanging = null;
	private Table currentTable = null;
	private Parameter currentParameter = null;
	private Column currentColumn = null;

	public Jco parse(File mapFile) throws SAXException {

		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			SAXParser parser = factory.newSAXParser();

			parser.parse(mapFile, this);
		} catch (SAXException e) {
			if (log.isErrorEnabled()) {
				log.error(
						"Error while parsing action map file : " + mapFile.getPath(), e);
			}
			throw e;
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error(
						"Error while reading action map file : " + mapFile.getPath(), e);
			}
			throw new SAXException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			if (log.isErrorEnabled()) {
				log.error("serious SAX parser configuration error.", e);
			}
			throw new SAXException(e.getMessage(), e);
		}

		return jco;
	}

	// SAXParser의 DefaultHandler 재정의
	public void startDocument() throws SAXException {
		if (log.isDebugEnabled()) {
			log.debug("parsing a submit map file...");
		}
	}

	public void endDocument() throws SAXException {
		if (log.isDebugEnabled()) {
			log.debug("parsing the submit map has completed.");
		}
	}

	@SuppressWarnings("static-access")
	public void startElement(String namespaceURI, String sName, String qName,
			Attributes attrs) throws SAXException {
		if (this.COLUMN_NODE.equals(qName)) {
			this.startColumnElement(attrs);
		} else if (this.PARAMETER_NODE.equals(qName)) {
			this.startParameterElement(attrs);
		} else if (this.IMPORT_NODE.equals(qName)) {
			this.startImportElement(attrs);
		} else if (this.EXPORT_NODE.equals(qName)) {
			this.startExportElement(attrs);
		} else if (this.CHANGING_NODE.equals(qName)) {
			this.startChangingElement(attrs);
		} else if (this.TABLE_NODE.equals(qName)) {
			this.startTableElement(attrs);
		} else if (this.FUNCTION_NODE.equals(qName)) {
			this.startFunctionElement(attrs);
		} else if (this.JCO_NODE.equals(qName)) {
			this.startJcoElement(attrs);
		} else {
			throw new SAXException("unknown element <" + qName + ">.");
		}
	}

	@SuppressWarnings("static-access")
	public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException {
		if (this.COLUMN_NODE.equals(qName)) {
			this.endColumnElement();
		} else if (this.PARAMETER_NODE.equals(qName)) {
			this.endParameterElement();
		} else if (this.IMPORT_NODE.equals(qName)) {
			this.endImportElement();
		} else if (this.EXPORT_NODE.equals(qName)) {
			this.endExportElement();
		} else if (this.CHANGING_NODE.equals(qName)) {
			this.endChangingElement();
		} else if (this.TABLE_NODE.equals(qName)) {
			this.endTableElement();
		} else if (this.FUNCTION_NODE.equals(qName)) {
			this.endFunctionElement();
		} else if (this.JCO_NODE.equals(qName)) {
			this.endJcoElement();
		} else {
			throw new SAXException("unknown element </" + qName + ">");
		}
	}

	@SuppressWarnings("static-access")
	private void startJcoElement(Attributes attrs) throws SAXException {
		String id = attrs.getValue(this.ATTR_JCO_ID);

		if (log.isDebugEnabled()) {
			log.debug("jco [id=" + id + "]");
		}

		// check validation.
		if (StringHelper.isNull(id)) {
			throw new SAXException("<jco> must have 'id' attribute.");
		}

		this.currentJco = new Jco(id);
	}

	private void endJcoElement() throws SAXException {
		if (currentJco.getFunctionCount() == 0) {
			throw new SAXException("<function> element is not defined.");
		} else {
			jco = currentJco;
			currentJco = null;
		}
	}

	@SuppressWarnings("static-access")
	private void startFunctionElement(Attributes attrs) throws SAXException {
		if (currentJco == null) {
			throw new SAXException("unexpected <function> element.");
		}

		if (currentFunction != null) {
			throw new SAXException("nested <function> element.");
		}

		String name = attrs.getValue(this.ATTR_FUNCTION_NAME);
		String desc = attrs.getValue(this.ATTR_FUNCTION_DESC);

		if (StringHelper.isNull(name)) {
			throw new SAXException("<function> must have 'name' attribute.");
		}

		this.currentFunction = new Function(name);
		currentFunction.setDesc(desc);
	}

	private void endFunctionElement() throws SAXException {
		if (currentFunction == null) {
			throw new SAXException("unexpected </function> element.");
		}

		// 현재 action 객체를 submit 객체에 설정한다.
		this.currentJco.setFunction(currentFunction);
		currentFunction = null;
	}

	@SuppressWarnings("static-access")
	private void startImportElement(Attributes attrs) throws SAXException {
		if (currentFunction == null) {
			throw new SAXException("unexpected <import> element.");
		}
		
		String name = attrs.getValue(this.IMPORT_NAME);
		
		if (currentImport != null) {
			throw new SAXException("nested <import> element.");
		}
		
		try {
			this.currentImport = new Import();
			currentImport.setName(name);
		} catch (Exception exp) {
			log.error("Import:" + exp);
			throw new SAXException(exp.getMessage());
		}
	}

	private void endImportElement() throws SAXException {
		if (currentImport == null) {
			throw new SAXException("unexpected </import> element.");
		}

		// 현재 action 객체를 submit 객체에 설정한다.
		this.currentFunction.setImports(currentImport);
		currentImport = null;
	}

	@SuppressWarnings("static-access")
	private void startExportElement(Attributes attrs) throws SAXException {
		if (currentFunction == null) {
			throw new SAXException("unexpected <export> element.");
		}
		
		String name = attrs.getValue(this.EXPORT_NAME);
		
		if (currentExport != null) {
			throw new SAXException("nested <export> element.");
		}
		
		try {
			this.currentExport = new Export();
			currentExport.setName(name);
		} catch (Exception exp) {
			log.error("Export:" + exp);
			throw new SAXException(exp.getMessage());
		}
	}

	private void endExportElement() throws SAXException {
		if (currentExport == null) {
			throw new SAXException("unexpected </export> element.");
		}

		// 현재 action 객체를 submit 객체에 설정한다.
		this.currentFunction.setExports(currentExport);
		currentExport = null;
	}

	@SuppressWarnings("static-access")
	private void startChangingElement(Attributes attrs) throws SAXException {
		if (currentFunction == null) {
			throw new SAXException("unexpected <changing> element.");
		}
		
		String name = attrs.getValue(this.CHANGING_NAME);
		
		if (currentChanging != null) {
			throw new SAXException("nested <changing> element.");
		}
		
		try {
			this.currentChanging = new Changing();
			currentChanging.setName(name);
		} catch (Exception exp) {
			log.error("Changing:" + exp);
			throw new SAXException(exp.getMessage());
		}
	}

	private void endChangingElement() throws SAXException {
		if (currentChanging == null) {
			throw new SAXException("unexpected </changing> element.");
		}

		// 현재 action 객체를 submit 객체에 설정한다.
		this.currentFunction.setChanging(currentChanging);
		currentChanging = null;
	}

	@SuppressWarnings("static-access")
	private void startTableElement(Attributes attrs) throws SAXException {
		if (currentFunction == null) {
			throw new SAXException("unexpected <table> element.");
		}
		
		String name = attrs.getValue(this.TABLE_NAME);
		
		if (currentTable != null) {
			throw new SAXException("nested <table> element.");
		}
		
		try {
			this.currentTable = new Table();
			currentTable.setName(name);
		} catch (Exception exp) {
			log.error("Table:" + exp);
			throw new SAXException(exp.getMessage());
		}
	}

	private void endTableElement() throws SAXException {
		if (currentTable == null) {
			throw new SAXException("unexpected </table> element.");
		}

		// 현재 action 객체를 submit 객체에 설정한다.
		this.currentFunction.setTable(currentTable);
		currentTable = null;
	}

	@SuppressWarnings("static-access")
	private void startParameterElement(Attributes attrs) throws SAXException {
		if (this.currentImport == null && this.currentExport == null
				&& this.currentChanging == null && this.currentTable == null) {
			throw new SAXException("unexpected <parameter> element.");
		}

		if (this.currentParameter != null) {
			throw new SAXException("nested <parameter> element.");
		}

		String name = attrs.getValue(this.ATTR_PARAMETER_NAME);
		String type = attrs.getValue(this.ATTR_PARAMETER_TYPE);
		String offset = attrs.getValue(this.ATTR_PARAMETER_OFFSET);
		String length = attrs.getValue(this.ATTR_PARAMETER_LENGTH);
		String decimals = attrs.getValue(this.ATTR_PARAMETER_DECIMALS);
		String trans = attrs.getValue(this.ATTR_PARAMETER_TRANS);
		String desc = attrs.getValue(this.ATTR_PARAMETER_DESC);
		String def = attrs.getValue(this.ATTR_PARAMETER_DEFAULT);
		
		if (StringHelper.isNull(name)) {
			throw new SAXException("<parameter> must have 'name' attribute .");
		}

		if (StringHelper.isNull(type)) {
			throw new SAXException("<parameter> must have 'type' attribute .");
		}

		try {
			this.currentParameter = new Parameter(name, type);
			currentParameter.setOffset(offset);
			currentParameter.setLength(length);
			currentParameter.setDecimals(decimals);
			currentParameter.setTrans(trans);
			currentParameter.setDesc(desc);
			currentParameter.setDefault(def);
		} catch (Exception exp) {
			log.error("Parameter:" + exp);
			throw new SAXException(exp.getMessage());
		}
	}

	private void endParameterElement() throws SAXException {
		if (currentParameter == null) {
			throw new SAXException("unexpected </parameter> element.");
		}

		if (this.currentImport != null) {
			currentImport.setParams(currentParameter);
		}
		if (this.currentExport != null) {
			currentExport.setParams(currentParameter);
		}
		if (this.currentChanging != null) {
			currentChanging.setParams(currentParameter);
		}
		if (this.currentTable != null) {
			currentTable.setParams(currentParameter);
		}

		currentParameter = null;
	}

	@SuppressWarnings("static-access")
	private void startColumnElement(Attributes attrs) throws SAXException {
		if (currentParameter == null) {
			throw new SAXException("unexpected <column> element.");
		}

		if (currentColumn != null) {
			throw new SAXException("nested <column> element.");
		}

		String name = attrs.getValue(this.ATTR_COLUMN_NAME);
		String type = attrs.getValue(this.ATTR_COLUMN_TYPE);
		String offset = attrs.getValue(this.ATTR_COLUMN_OFFSET);
		String length = attrs.getValue(this.ATTR_COLUMN_LENGTH);
		String decimals = attrs.getValue(this.ATTR_COLUMN_DECIMALS);
		String trans = attrs.getValue(this.ATTR_COLUMN_TRANS);
		String required = attrs.getValue(this.ATTR_COLUMN_REQUIRED);
		String format = attrs.getValue(this.ATTR_COLUMN_FORMAT);
		String defaultval = attrs.getValue(this.ATTR_COLUMN_DEFAULT);
		String basecode = attrs.getValue(this.ATTR_COLUMN_BASECODE);
		String alert = attrs.getValue(this.ATTR_COLUMN_ALERT);
		String message = attrs.getValue(this.ATTR_COLUMN_MESSAGE);
		String desc = attrs.getValue(this.ATTR_COLUMN_DESC);

		if (StringHelper.isNull(name)) {
			throw new SAXException("<column> must have 'name' attribute .");
		}

		if (StringHelper.isNull(type)) {
			throw new SAXException("<column> must have 'type' attribute .");
		}

		try {
			this.currentColumn = new Column(name, type);
			currentColumn.setOffset(offset);
			currentColumn.setLength(length);
			currentColumn.setDecimals(decimals);
			currentColumn.setTrans(trans);
			currentColumn.setRequired(required);
			currentColumn.setFormat(format);
			currentColumn.setDefault(defaultval);
			currentColumn.setBaseCode(basecode);
			currentColumn.setAlert(alert);
			currentColumn.setMessage(message);
			currentColumn.setDesc(desc);
		} catch (Exception exp) {
			log.error("Column:" + exp);
			throw new SAXException(exp.getMessage());
		}
	}

	public void endColumnElement() throws SAXException {
		if (currentColumn == null) {
			throw new SAXException("unexpected </column> element.");
		}

		currentParameter.setColumn(currentColumn);
		currentColumn = null;
	}
}

