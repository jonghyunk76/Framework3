package kr.yni.frame.mapper.element;

import java.util.ArrayList;
import java.util.List;

public class Export {
	private List<Parameter> params = new ArrayList<Parameter>();
	private String name = null; 
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setParams(Parameter param) {
		params.add(param);
	}

	public Parameter getParams(int idx) {
		return params.get(idx);
	}

	public int getParameterCount() {
		return params.size();
	}

	public String toString() {
		return "<export " + params + ">";
	}
}
