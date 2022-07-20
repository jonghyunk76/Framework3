package kr.yni.frame.mapper.element;

import kr.yni.frame.mapper.type.ColumnTypeCaster;
import kr.yni.frame.util.StringHelper;

public class Column {
	private String name = null;
	private int type = 0;
	private int offset = 0;
	private int length = 0;
	private int decimals = 0;
	private String required = null;
	private String trans = null;
	private String format = null;
	private String defaultval = null;
	private String basecode = null;
	private String alert = null;
	private String message = null;
	private String desc = null;

	public Column(String name, String type) {
		this.setName(name);
		this.setType(type);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(String type) {
		this.type = ColumnTypeCaster.changeRfcType(type);
	}

	public int getType() {
		return type;
	}

	public void setOffset(String offset) {
		this.offset = Integer.parseInt(StringHelper.null2string(offset, "0"));
	}

	public int getOffset() {
		return offset;
	}

	public void setLength(String length) {
		this.length = Integer.parseInt(StringHelper.null2string(length, "0"));
	}

	public int getLength() {
		return length;
	}

	public void setDecimals(String decimals) {
		this.decimals = Integer.parseInt(StringHelper.null2string(decimals, "0"));
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public void setTrans(String trans) {
		this.trans = trans;
	}

	public String getTrans() {
		return trans;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getRequired() {
		return required;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public void setDefault(String defaultval) {
		this.defaultval = defaultval;
	}

	public String getDefault() {
		return defaultval;
	}
	
	public void setBaseCode(String basecode) {
		this.basecode = basecode;
	}

	public String getBaseCode() {
		return basecode;
	}
	
	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getAlert() {
		return alert;
	}
	
	public String toString() {
		return "<column " + name + "," + type + "," + offset + "," + length
				+ "," + format + "," + required + "," + decimals + "," + trans
				+ "," + defaultval+ "," + basecode+ "," + alert + "," + desc + ">";
	}
}
