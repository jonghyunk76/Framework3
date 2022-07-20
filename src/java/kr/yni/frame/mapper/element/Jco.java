package kr.yni.frame.mapper.element;

import java.util.ArrayList;
import java.util.List;

public class Jco {
	private List<Function> functions = new ArrayList<Function>();

	private String id = null;

	public Jco(String id) {
		this.setId(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setFunction(Function function) {
		functions.add(function);
	}

	public Function getFunction(int idx) {
		return functions.get(idx);
	}

	public int getFunctionCount() {
		return functions.size();
	}

	public String toString() {
		return "<jco " + id + ">" + functions;
	}
}
