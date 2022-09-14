package kr.yni.frame.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.mapper.ParamReader;
import kr.yni.frame.mapper.element.Column;
import kr.yni.frame.mapper.element.Export;
import kr.yni.frame.mapper.element.Import;
import kr.yni.frame.mapper.element.Jco;
import kr.yni.frame.mapper.element.Parameter;
import kr.yni.frame.mapper.element.Table;
import kr.yni.frame.mapper.util.JcoMapValidator;
import com.sap.mw.jco.JCO;


/**
 * <p>
 * JCO 파라메터와 관련된 요청정보를 처리하는 클래스
 * </p>
 * 
 * @author 김종현 (ador218@bluesolution.co.kr)
 * 
 */
public class JcoMapHelper {
	private static Log log = LogFactory.getLog(JcoMapHelper.class);

	private Jco jco = null;

	public JcoMapHelper(String jcoID) throws Exception {
		jco = ParamReader.getJcoParameter(jcoID);
	}
	
	public String getFunctionName() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("function name = " + jco.getFunction(0).getName());
		}
		return jco.getFunction(0).getName();
	}
	
	public String getImportName() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("import name = " + jco.getFunction(0).getImports().getName());
		}
		return jco.getFunction(0).getImports().getName();
	}
	
	public String getExportName() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("export name = " + jco.getFunction(0).getExports().getName());
		}
		return jco.getFunction(0).getExports().getName();
	}
	
	public String getTableName() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("table name = " + jco.getFunction(0).getTable().getName());
		}
		return jco.getFunction(0).getTable().getName();
	}
	
	public String getChangingName() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("changing name = " + jco.getFunction(0).getChanging().getName());
		}
		return jco.getFunction(0).getChanging().getName();
	}
	
	/**
	 * JCO MAP에서 import Parameter에 적용한 값을 생성하는 매소드
	 * 
	 * @param map 요청 파라메터
	 * @return parameter Map Data
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> getImportParameter(Map map) throws Exception {
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();

		Import imp = jco.getFunction(0).getImports();
		
		if(imp == null) {
			return paramMap;
		}
		
		for (int i = 0; i < imp.getParameterCount(); i++) {
			Parameter param = imp.getParams(i);
			String paramName = param.getName(); // table명

			if(param.getType() == JCO.TYPE_TABLE) { // table타입은 하위에 column을  갖는다.
				JCO.MetaData meta = new JCO.MetaData(paramName);

				if (param.getColumnCount() > 0) {
					// table 하위의 컬럼 정보을 등록한다.
					for (int k = 0; k < param.getColumnCount(); k++) {
						Column col = param.getColumn(k);
						meta.addInfo(col.getName(), col.getType(), col.getOffset(), col.getLength(), col.getDecimals());
					}

					// JCO테이블을 생성하고 컬럼에 값을 등록한다.
					JCO.Table tbl = JCO.createTable(meta);
					
					// 파라메터에서 컬럼명에 해당하는 값을 찾는다.
					// Map.put("paramName명", List.add(Map.put("컬럼명", value)))
					List<Object> columnList = (List<Object>) map.get(paramName);

					if (columnList == null || columnList.size() < 1) {
						if (log.isErrorEnabled()) {
							log.error("Where the requestParameter.<"+ paramName + "> was not found.");
						}
					} else if (columnList.size() > 0) {
						for (int j = 0; j < columnList.size(); j++) {
							tbl.appendRow(); // row추가
							
							Map<String, Object> columnMap = (Map<String, Object>) columnList.get(j);
							
							Iterator iter = columnMap.entrySet().iterator();
							while (iter.hasNext()) {
								Map.Entry entry = (Map.Entry) iter.next();
								
								for (int k = 0; k < param.getColumnCount(); k++) {
									Column col = param.getColumn(k);
									
									String colName = StringHelper.null2void(col.getName());
									String transName = StringHelper.null2void(col.getTrans());
									Object ovalue = entry.getValue();
									String source = JcoMapValidator.getSourceString(ovalue, col, null);
									
									if(!transName.isEmpty()) {
										if (transName.equals(entry.getKey())) {
											if(log.isDebugEnabled()) log.debug("By trans column. The value input in the table(table =" + paramName + ", trans = " + transName + ", column = " + colName + ", value = " + source);
											tbl.setValue(source, colName);
										}
									} else {
										if (colName.equals(entry.getKey())) {
											if(log.isDebugEnabled()) log.debug("By name column. The value input in the table(table =" + paramName + ", column = " + colName + ", value = " + source);
											tbl.setValue(source, colName);
										}
									}
								}
							}
						}
					}

					paramMap.put(paramName, tbl);
				} else {
					if (log.isErrorEnabled()) {
						log.error("There not found column into table.");
					}
				}
			} else if(param.getType() == JCO.TYPE_ITAB) { // itab인 경우에는 SAP JCO 정보를 사용하지 않음(2020-12-19)
				List tbl = new ArrayList();
				Object mapObj = map.get(paramName);
				
				if(param.getColumnCount() > 0) {
					if(mapObj != null && mapObj instanceof List) { // 리스트 값이 입력된 경우
						List lobj = (List) mapObj;
						
						for(int j = 0; j <lobj.size(); j++) {
							Map colMap = new LinkedHashMap();
							Map mobj = (Map) lobj.get(j);
							
							for (int k = 0; k < param.getColumnCount(); k++) {
								Column col = param.getColumn(k);
								
								String colName = StringHelper.null2void(col.getName());
								String transName = StringHelper.null2void(col.getTrans());
								String paramValue = null;
								String defaultVal = StringHelper.null2void(col.getDefault());
								
								if(!transName.isEmpty()) {
									paramValue = StringHelper.null2string(mobj.get(transName), defaultVal);
								} else {
									paramValue = StringHelper.null2string(mobj.get(paramName), defaultVal);
								}
								
								colMap.put(colName, paramValue);
							}
							
							tbl.add(colMap);
						}
					} else { // Map 값이 입력된 경우
						Map colMap = new LinkedHashMap();
						
						for(int k = 0; k < param.getColumnCount(); k++) {
							Column col = param.getColumn(k);
							
							String colName = StringHelper.null2void(col.getName());
							String transName = StringHelper.null2void(col.getTrans());
							String paramValue = null;
							String defaultVal = StringHelper.null2void(col.getDefault());
							
							if(!transName.isEmpty()) {
								paramValue = StringHelper.null2string(map.get(transName), defaultVal);
							} else {
								paramValue = StringHelper.null2string(map.get(paramName), defaultVal);
							}
							
							colMap.put(colName, paramValue);
						}
						
						tbl.add(colMap);
					}
				}
				
				paramMap.put(paramName, tbl);
			} else if(param.getType() == JCO.TYPE_XSTRING) {
				if(param.getColumnCount() > 0) {
					Map colMap = new LinkedHashMap();
					
					for(int k = 0; k < param.getColumnCount(); k++) {
						Column col = param.getColumn(k);
						
						String colName = StringHelper.null2void(col.getName());
						String transName = StringHelper.null2void(col.getTrans());
						String paramValue = null;
						String defaultVal = StringHelper.null2void(col.getDefault());
						
						if(!transName.isEmpty()) {
							paramValue = StringHelper.null2string(map.get(transName), defaultVal);
						} else {
							paramValue = StringHelper.null2string(map.get(paramName), defaultVal);
						}
						
						colMap.put(colName, paramValue);
					}
					
					paramMap.put(paramName, colMap);
				}
		    } else {
				String transName = StringHelper.null2void(param.getTrans());
				String defaultVal = StringHelper.null2void(param.getDefault());
				int lang = param.getOffset();
				String paramValue = null;
				
				if(!transName.isEmpty()) {
					paramValue = StringHelper.null2string(map.get(transName), defaultVal);
				} else {
					paramValue = StringHelper.null2string(map.get(paramName), defaultVal);
				}
				
				if(paramValue.length() > lang) {
					paramMap.put(paramName, paramValue.substring(0, lang));
				} else {
					paramMap.put(paramName, paramValue);
				}
			}
		}

		return paramMap;
	}

	/**
	 * JCO.Table 파라메터명을 구하는 매소드
	 * 
	 * @return
	 */
	public List<String> getTableParameterName() throws Exception {
		List<String> tblName = new ArrayList<String>();
		Table tbl = jco.getFunction(0).getTable();
		
		if(tbl != null) {
			for (int j = 0; j < tbl.getParameterCount(); j++) {
				tblName.add(tbl.getParams(j).getName());
			}
		}

		return tblName;
	}
	
	/**
	 * JCO.Table의 컬럼명을 구하는 매소드
	 * 
	 * @param idx 테이블 등록 번호
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getTableColumnName(int idx) throws Exception {
		List<Map<String, Object>> tblCols = new ArrayList<Map<String, Object>>();
		Map<String, Object> colAtrMap = null;
		
		Table tbl = jco.getFunction(0).getTable(); // <table>
		
		if(tbl != null) {
			Parameter param = tbl.getParams(idx); // <table>.<parameter> 
			
			for (int j = 0; j < param.getColumnCount(); j++) {
				colAtrMap = new LinkedHashMap<String, Object>();
				
				Column col = param.getColumn(j); // <table>.<parameter>.<column>
				
				// attribute
				String name = col.getName();
				Integer type = col.getType();
				Integer leng = col.getLength();
				Integer decimals = col.getDecimals();
				String format = col.getFormat();
				String required = col.getRequired();
				String defaultval = col.getDefault();
				String trans = col.getTrans();
				String desc = col.getDesc();
				String basecode = col.getBaseCode();
				String alert = col.getAlert();
				String message = col.getMessage();
				
				colAtrMap.put("COLUMN_NAME", name);
				colAtrMap.put("COLUMN_TYPE", type);
				colAtrMap.put("COLUMN_LENG", leng);
				colAtrMap.put("COLUMN_DECIMALS", decimals);
				colAtrMap.put("COLUMN_FORMAT", format);
				colAtrMap.put("COLUMN_REQUIRED", required);
				colAtrMap.put("COLUMN_DEFAULTVAL", defaultval);
				colAtrMap.put("COLUMN_TRANS", trans);
				colAtrMap.put("COLUMN_DESC", desc);
				colAtrMap.put("COLUMN_BASECODE", basecode);
				colAtrMap.put("COLUMN_ALERT", alert);
				colAtrMap.put("COLUMN_MESSAGE", message);
				
				tblCols.add(j, colAtrMap);
			}
		}

		return tblCols;
	}
	
	/**
	 * export될 파라메터명을 구하는 매소드
	 * 
	 * @return
	 */
	public List<String> getExportParameterName() throws Exception {
		List<String> expName = new ArrayList<String>();
		
		Export exp = jco.getFunction(0).getExports();
		
		if(exp == null) {
			return expName;
		}
		
		for (int j = 0; j < exp.getParameterCount(); j++) {
			expName.add(exp.getParams(j).getName());
		}

		return expName;
	}

	/**
	 * export의 타입을 구하는 매소드
	 * 
	 * @return
	 */
	public int getExportParameterType(String param) throws Exception {
		int type = 0;
		Export exp = jco.getFunction(0).getExports();
		
		if(exp == null) {
			return 0;
		}
		
		for (int j = 0; j < exp.getParameterCount(); j++) {
			if (param.equals(exp.getParams(j).getName())) {
				type = exp.getParams(j).getType();
			}
		}

		return type;
	}
	
	/**
	 * Table의 파라메터의 타입을 구하는 매소드
	 * 
	 * @return
	 */
	public int getTableParameterType(String param) throws Exception {
		Table tbl = jco.getFunction(0).getTable();
		int type = 0;

		for (int j = 0; j < tbl.getParameterCount(); j++) {
			if (param.equals(tbl.getParams(j).getName())) {
				type = tbl.getParams(j).getType();
			}
		}

		return type;
	}
	
}
