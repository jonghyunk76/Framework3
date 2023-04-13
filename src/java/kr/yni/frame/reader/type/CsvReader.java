package kr.yni.frame.reader.type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;

import kr.yni.frame.reader.FileReader;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.ExcelUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.vo.ValueObject;

/**
 * CSV파일로 엑셀 대용량 다운로드 처리를 위한 클래스(2022-03-06)
 * 
 * @author ador2
 *
 */
public class CsvReader extends FileReader {
	
	private static Log log = LogFactory.getLog(CsvReader.class);
			
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

	@Override
	public List read(File file, int index) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List read(File file, int index, String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List read(File file, int index, String id, boolean formula) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
		ValueObject vo = null;
		String[] columns = null;
		int startSheet = 0;
		
		if(list != null && list.size() > 0) {
            // list 에는 1개이상의 엑셀시트를 포함하고 있음
        	for(int si = 0; si < list.size(); si++) {
        		List<Map<String, Object>> dataList = (List<Map<String, Object>>)list.get(si);
        		int lastIndex = dataList.size() - 1;
	            
	            if(dataList.get(lastIndex) != null) {
	            	Map<String, Object> map = dataList.get(lastIndex);
	                Object colObj = map.get("headers");
	                String sheetName = StringHelper.null2string(map.get("sheet"), "sheet");
	                
	                
	                if(colObj != null && colObj instanceof List) { // 해더정보를 제외한 row data를 구한다.
	                    List<Object> colList = (List<Object>) colObj;
	                    
	                    dataList.remove(lastIndex);
	                    vo = ExcelUtil.transformVO(dataList, sheetName);
	                    
	                    if(colList != null && colList.size() > 0) {
	                    	workbook = build(vo, colList, startSheet);
	                    }
	                }
	            }
        	}
		}
	}
	
	/**
	 * 데이터 추출
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> view(List<List<Map<String, Object>>> list) throws Exception {
		ValueObject vo = null;
		String[] columns = null;
		int startSheet = 0;
		List<List<String>> csvList = null;
		
		if(list != null && list.size() > 0) {
            // list 에는 1개이상의 엑셀시트를 포함하고 있음
        	for(int si = 0; si < list.size(); si++) {
        		List<Map<String, Object>> dataList = (List<Map<String, Object>>)list.get(si);
        		int lastIndex = dataList.size() - 1;
	            
	            if(dataList.get(lastIndex) != null) {
	            	Map<String, Object> map = dataList.get(lastIndex);
	                Object colObj = map.get("headers");
	                String sheetName = StringHelper.null2string(map.get("sheet"), "sheet");
	                
	                
	                if(colObj != null && colObj instanceof List) { // 해더정보를 제외한 row data를 구한다.
	                    List<Object> colList = (List<Object>) colObj;
	                    
	                    dataList.remove(lastIndex);
	                    vo = ExcelUtil.transformVO(dataList, sheetName);
	                    
	                    if(colList != null && colList.size() > 0) {
	                    	csvList = build(vo, colList, startSheet);
	                    }
	                }
	            }
        	}
		}
		
		return csvList;
	}
	
	/**
     * 데이타 그리드에서 지정된 해더정보를 이용하여 엑셀파일을 생성한다.
     * 
     * @param ValueObject
     * @param colList : 해더 정보
     * @param startSheet : 시작할 시트번호
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<List<String>> build(ValueObject pVO, List colList, int startSheet) throws Exception {
        if(log.isDebugEnabled()) log.debug("csv build start....");
        List<List<String>> data_list = new LinkedList<List<String>>();  // 해더의 열이 두개인 경우를 처리하기 위한 임시객체
        
        int total = pVO.size();     // 데이터 전체 갯수
        int sheetCnt = 1;    // 시트 갯수
        int startCnt = 0;    // 기록할 row index 번호
        
        // 해더 속성
        String field = null;        // 컬럼명
        String title = null;        // 해더명
        int width = 0;              // 넓이
        String align = null;        // 정렬
        boolean hidden = true;     // 숨김:true, 보임:false
        String format = null;		// 데이터 타입 
        int mergeRow = 0;           // 머지열수
        int mergeCol = 0;           // 머지행수
        String styler = null;		// 셀 스타일
        
        try {
	        for(int jnx = 1 ; jnx <= sheetCnt ; jnx++) { // 시트는 1개로 고정
	            String sname = StringHelper.null2string(pVO.getName(), "Sheet" + (jnx));
	            if(sheetCnt > 1) sname = sname + "(" + jnx + ")";
	            
	            if(log.isDebugEnabled()) log.debug("sheet name = " + sname);
	            
	            int rownum = 0;
	            List header_list = new LinkedList();  // 해더의 열이 두개인 경우를 처리하기 위한 임시객체
	            
	            for (int c = 0; c < colList.size() ; c++) { // 해더 작성부분
	            	Object colListObj = colList.get(c);
	            	
	                if(colListObj != null && colListObj instanceof List) { // 해더 목록
	                    List list = (List) colListObj;
	                    int idex = 0; 
	                    List<String> dlist = new ArrayList<String>();
	                    
	                    for(int m = 0 ; m < list.size(); m++) { // 해더의 column 수만큼 Loop...
	                        Object colMapObj = list.get(m);
	                        
	                        if(colMapObj != null && colMapObj instanceof Map) {
	                            Map map = (Map) colMapObj;
	                            
	                            field = StringHelper.null2void(map.get("field"));
	                            title = StringHelper.null2void(map.get("title"));
	                            title = title.replaceAll("<BR>", "").replaceAll("<br>", "");
	                            width = StringHelper.null2zero(map.get("width"));
	                            hidden = StringHelper.null2boolean(map.get("hidden"), false);
	                            format = StringHelper.null2void(map.get("formatter"));
	                            mergeRow = StringHelper.null2zero(map.get("merge_row"));    
	                            mergeCol = StringHelper.null2zero(map.get("merge_col"));
	                            styler = StringHelper.null2void(map.get("styler"));
	                            
	                            if(log.isDebugEnabled()) log.debug(map);
	                            
	                            // 해더 부분 작성
	                            // 1. 해더 merge 처리
	                            // 2. 마지막 해더에 row를 머지한 file ID 저장
	                            if(!hidden) {
	                            	// csv add cell
	                            	// 해더정보가 1개인 경우에는 본 해더의 정보는 바로 포함시킨다.
	                            	if(colList.size() == 1) {
	                                	int start_col = 0; // 시작행
	                                    int start_row = 0; // 시작열
	                                    int end_col = 0;    // 종료행
	                                    int end_row = 0;    // 종료열
	                                    
	                                    if(mergeRow > 0) {
	                                        start_col = idex;
	                                        start_row = 0;
	                                        end_col = start_col;
	                                        end_row = 1;
	
	                                        header_list.add(end_col, map);
	                                    } else if(mergeCol > 0) {
	                                        start_col = idex;
	                                        start_row = 0;
	                                        end_col = start_col+(mergeCol-1);
	                                        end_row = 0;
	                                        
	                                        for(int i = idex; i <= end_col; i++) {
	                                            header_list.add(i, null);
	                                            log.debug("index("+i+") is null");
	                                        }
	                                    } else {
	                                        header_list.add(idex, map);
	                                    }
	                                    
	                                    dlist.add(idex, title);
	                                    
	                                    log.debug("header size:"+colList.size()+", s_col("+idex+"):"+start_col+", s_row:"+start_row+", e_col:"+end_col+", e_row:"+end_row);
	                                } else {
	                                	if(c == (colList.size()-1)) {
	                                		// 두번째 행부터는 colspan한 상위열의 순서대로 등록한다.
	                                        int header_size = header_list.size();
	                                        
	                                        // 해더에 표시할 두번째 행의 기본값을 생성한다.
	                                        if(dlist.size() == 0) {
		                                        for(int i = 0; i < header_size; i++) {
		                                        	dlist.add(i, "");
		                                        }
	                                        }
	                                        
	                                        // 타이틀이 위치한 곳에 값으로 수정한다.
	                                        for(int i = 0; i < header_size; i++) {
	                                            if(header_list.get(i) == null) {
	                                                header_list.set(i, map); // 비어있는 배열index에 순서대로 등록한다.
	                                                dlist.set(i, title);
	                                                
	                                                log.debug("header size("+header_size+") / list("+i+")="+map +" / title = " + title + " / title size = " + dlist.size());
	                                                break;
	                                            }
	                                        }
	                                	} else {
	                                		// 첫번째 열을 생성한다. 이때 Merge 설정이 있으면 실행한다.
	                                        int start_col = 0; // 시작행
	                                        int start_row = 0; // 시작열
	                                        int end_col = 0;    // 종료행
	                                        int end_row = 0;    // 종료열
	                                        
	                                        if(mergeRow > 0) {
	                                            start_col = idex;
	                                            start_row = c;
	                                            end_col = start_col;
	                                            end_row = c+(mergeRow-1);
	                                            if(c == (colList.size()-2)) {
	                                            	header_list.add(end_col, map);
	                                            }
	                                        } else if(mergeCol > 0) {
	                                            start_col = idex;
	                                            start_row = c;
	                                            end_col = start_col+(mergeCol-1);
	                                            end_row = c;
	                                            
	                                            if(c == (colList.size()-2)) {
		                                            for(int i = idex; i <= end_col; i++) {
		                                            	header_list.add(i, null);
		                                            	dlist.add(i, "");
		                                                log.debug("index("+i+") is null");
		                                            }
	                                            }
	                                        } else {
	                                            if(c == (colList.size()-2)) {
	                                            	header_list.add(idex, map);
	                                            }
	                                        }
	                                        
	                                        dlist.add(start_col, title);
	                                        
	                                        log.debug("header size:"+colList.size()+", s_col("+(c+1)+"):"+start_col+", s_row:"+start_row+", e_col:"+end_col+", e_row:"+end_row);
	                                	}
	                                }
	                                
	                                if(mergeCol > 0){
	                                    idex = idex + 1 + (mergeCol-1);
	                                } else {
	                                    idex++;
	                                }
	                            }
	                        }
	                    }
	                    
	                    rownum++;
	                    data_list.add(dlist);
	                }
	            }
	            
	            if(total > 0) {  // 본부 작성 부분
	                for(int n = startCnt ; n < total ; n++) {
	                    Object colListObj = header_list;
	                    List<String> dlist = new ArrayList<String>();
	                    
	                    if(colListObj != null && colListObj instanceof List) { // 해더 정보
	                        List list = (List) colListObj;
	                        int idex = 0;
	                        
	                        for(int m = 0 ; m < list.size(); m++) {
	                            Object colMapObj = list.get(m);
	                            
	                            if(colMapObj != null && colMapObj instanceof Map) {
	                                Map map = (Map) colMapObj;
	                                
	                                field = StringHelper.null2void(map.get("field"));
	                                title = StringHelper.null2void(map.get("title"));
	                                hidden = StringHelper.null2boolean(map.get("hidden"), true);
	                                align = StringHelper.null2void(map.get("align"));
	                                format = StringHelper.null2void(map.get("formatter"));
	                                styler = StringHelper.null2void(map.get("style"));
	                                
//	                                log.debug("value = " + pVO.getString(n, field) +" / map = " + map.toString());
	                                
	                                if(!hidden) {
	                                	try {
	                                		if(StringHelper.null2void(pVO.getInt(n, field)).isEmpty()) {
	                                			dlist.add(idex, "");
		                                	} else if(format.equals("int") && pVO != null) {
		                                		int val = pVO.getInt(n, field);
		                                		
		                                		dlist.add(idex, Integer.toString(val));
		                                	} else if((format.equals("float") || format.equals("amount") || format.equals("quantity") || format.equals("rate") || format.equals("exchange")) && pVO != null) {
		                                		float val = pVO.getFloat(n, field);
		                                		
		                                		dlist.add(idex, Float.toString(val));
		                                	} else if(format.equals("percent")) {
                            	                double value = Double.parseDouble(StringHelper.null2string(pVO.getFloat(n, field), "0"))*100;
                            	                dlist.add(idex, String.valueOf(value+"%"));
                            	            } else if(format.equals("percent1")) {
                            	                double value = Double.parseDouble(StringHelper.null2string(pVO.getFloat(n, field), "0"));
                            	                dlist.add(idex, String.valueOf(value+"%"));
                            	            } else {
                            	            	dlist.add(idex, pVO.getString(n, field));
		                                	}
	                                	} catch(Exception e) {
	                                		dlist.add(idex, pVO.getString(n, field));
	                                	}
                                		
	                                    idex++;
	                                }
	                            }
	                        }
//	                        
//	                        log.debug("dlist size  = " + dlist.size());
	                        
	                    }
	                    
	                    data_list.add(dlist);
	                }
	            }
	        }
	        
	        if(log.isDebugEnabled()) log.debug("csv build end.");
        } catch(Exception e) {
        	if(log.isErrorEnabled()) log.error(e);
        	
        	throw e;
        }
        
        return data_list;
    }
    
}
