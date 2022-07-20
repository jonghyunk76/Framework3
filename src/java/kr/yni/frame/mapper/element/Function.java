package kr.yni.frame.mapper.element;

public class Function {
	private String name = null;
	private String desc = null;

	private Import imports = null;
	private Export exports = null;
	private Changing changing = null;
	private Table table = null;

	public Function(String name) {
		this.setName(name);
		this.setDesc(desc);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setImports(Import imports) {
		this.imports = imports;
	}

	public Import getImports() {
		return imports;
	}

	public void setExports(Export exports) {
		this.exports = exports;
	}

	public Export getExports() {
		return exports;
	}

	public void setChanging(Changing changing) {
		this.changing = changing;
	}

	public Changing getChanging() {
		return changing;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public Table getTable() {
		return table;
	}

	public String toString() {
		return "<function " + name + "," + desc + ">" + imports + exports
				+ changing + table;
	}
}
