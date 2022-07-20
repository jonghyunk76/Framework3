package kr.yni.frame.mapper.element;

import java.util.ArrayList;
import java.util.List;

import kr.yni.frame.mapper.type.ColumnTypeCaster;
import kr.yni.frame.util.StringHelper;


public class Parameter {
	private String name = null;
	private int type = 0;
	private int offset = 0;
	private int length = 0;
	private int decimals = 0;
	private String trans = null;
	private String desc = null;
	private String def = null;

	private List<Column> columns = new ArrayList<Column>();

	public Parameter(String name, String type) {
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
	
	public void setTrans(String trans) {
		this.trans = trans;
	}

	public String getTrans() {
		return trans;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDefault(String def) {
		this.def = def;
	}

	public String getDefault() {
		return def;
	}
	
	public void setColumn(Column column) {
		columns.add(column);
	}

	public Column getColumn(int idx) {
		return columns.get(idx);
	}

	public int getColumnCount() {
		return columns.size();
	}

	public String toString() {
		return "<parameter " + name + "," + type + "," + offset + "," + length
				+ "," + decimals + "," + desc + ">" + columns;
	}
}
