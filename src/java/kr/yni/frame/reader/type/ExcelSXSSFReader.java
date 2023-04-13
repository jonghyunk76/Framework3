package kr.yni.frame.reader.type;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import kr.yni.frame.reader.FileReader;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.ExcelUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.vo.ValueObject;

/**
 * XSSF의 Streaming Version으로 엑셀 대용량 다운로드 처리를 위한 클래스
 * (2021-11-08)
 * 
 * @author ador2
 *
 */
public class ExcelSXSSFReader extends FileReader {
	private static Log log = LogFactory.getLog(ExcelSXSSFReader.class);
	private static final int MAX_PER_SHEET_COUNT = 1000000;    // 시트당 건씩 생성(백만건으로 고정), 최대 1,048,576행
	
	
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
	public void view(List<List<Map<String, Object>>> list, Object wb) throws Exception {
		SXSSFWorkbook workbook = (SXSSFWorkbook) wb;
		ValueObject vo = null;
        String[] columns = null;
        int startSheet = 0;

        if(list != null && list.size() > 0) {
            // list 에는 1개이상의 엑셀시트를 포함하고 있음
        	for(int si = 0; si < list.size(); si++) {
        		List<Map<String, Object>> sheetList = (List<Map<String, Object>>)list.get(si);
        		
	            int lastIndex = sheetList.size() - 1;
	            
	            if(sheetList.get(lastIndex) != null) {
	                Map<String, Object> map = sheetList.get(lastIndex);
	                Object colObj = map.get("headers");
	                String sheetName = StringHelper.null2string(map.get("sheet"), "sheet");
	                
	                if(colObj != null && colObj instanceof List) {
	                    List<Object> colList = (List<Object>) colObj;
	                    
	                    // 해더정보를 제외한 row data를 구한다.
	                    sheetList.remove(lastIndex);
	                    vo = ExcelUtil.transformVO(sheetList, sheetName);
	                    
	                    if(colList != null && colList.size() > 0) {
	                    	startSheet = build(vo, workbook, colList, startSheet);
	                    }
	                } else {
	                    vo = ExcelUtil.transformVO(sheetList, sheetName);
	                    columns = ExcelUtil.transformColumns((Map<String, Object>) sheetList.get(0));
	                    
	                    build(vo, workbook, columns);
	                }
	            }
        	}
        } else {
            build(vo, workbook, columns);
        }
	}
	
	/**
     * 파일로 다운로드 함
     * 
     * @param pVO
     * @param out
     * @param columns
     * @throws IOException
     */
    private void write(ValueObject pVO, OutputStream out, String[] columns) throws Exception {
        SXSSFWorkbook workbook = null;
        
        try {
            workbook = new SXSSFWorkbook();
            
            build(pVO, workbook, columns);
            
            workbook.write(out);
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                if(out != null) out.close();
            } catch(IOException ex) {
                throw (IOException)new IOException("workbook write error.").initCause(ex);
            }
        }
    }
    
    /**
     * 데이타 그리드에서 지정된 해더정보를 이용하여 엑셀파일을 생성한다.
     * 
     * @param pVO
     * @param workbook
     * @param columns
     * @param startSheet : 시작할 시트번호
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int build(ValueObject pVO, SXSSFWorkbook workbook, List colList, int startSheet) throws Exception {
        if(log.isDebugEnabled()) log.debug("excel build start....");
        
        SXSSFSheet sheet = null;
        SXSSFRow row = null;
        
        int total = pVO.size();     // 데이터 전체 갯수
        int sheetCnt = total / MAX_PER_SHEET_COUNT;    // 시트 갯수
        int startCnt = 0;    // 기록할 row index 번호
        int endCnt = 0;
        
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
        
        // 시트갯수 및 시트명과 번호 셋팅
        if((sheetCnt * MAX_PER_SHEET_COUNT) <= total) sheetCnt++;
        
        try {
	        for(int jnx = 1 ; jnx <= sheetCnt ; jnx++) {
	            String sname = StringHelper.null2string(pVO.getName(), "Sheet" + (jnx));
	            if(sheetCnt > 1) sname = sname + "(" + jnx + ")";
	            
	            if(log.isDebugEnabled()) log.debug("sheet name = " + sname + ", sheet number = " + (startSheet+(jnx-1)) + ", row count(per sheet) = " + MAX_PER_SHEET_COUNT);
	            
	            // 시트 생성
	            if(workbook.getSheet(sname) == null) {
	            	sheet = workbook.createSheet(sname);
	            }
	            
	            sheet.setRandomAccessWindowSize(100); // 메모리 행 100개로 제한, 초과 시 Disk로 flush
	            
	            // 폰트 색상
	            Font font_01 = workbook.createFont();
	            font_01.setColor(IndexedColors.BLACK.index);
	            font_01.setBold(true);
	            
	            Font font_02 = workbook.createFont();
	            font_02.setColor(IndexedColors.DARK_GREEN.index);
	            
	            Font font_03 = workbook.createFont();
	            font_03.setColor(IndexedColors.RED.index);
	            
	            Font font_04 = workbook.createFont();
	            font_04.setColor(IndexedColors.BLUE.index);
	            
	            int rownum = 0;
	            List header_list = new LinkedList();  // 해더의 열이 두개인 경우를 처리하기 위한 임시객체
	            
	            for (int c = 0; c < colList.size() ; c++) { // 해더 작성부분
	            	// 해더의 style 선언
	                CellStyle cs = workbook.createCellStyle();
	                cs.setFont(font_01);
	                cs.setWrapText(true);
	                Object colListObj = colList.get(c);
	                
	                if(colListObj != null && colListObj instanceof List) {
	                    List list = (List) colListObj;
	                    int idex = 0; 
	                    
	                    // excel add row
	                    row = sheet.createRow(c);
	                    
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
	                            	// excel add cell
	                            	// 해더정보가 1개인 경우에는 본 해더의 정보는 바로 포함시킨다.
	                            	if(colList.size() == 1) {
	                                	// 첫번째 열을 생성한다. 이때 Merge 설정이 있으면 실행한다. 
	                                    makeCell(idex, row, title, cs, "header");
	                                    
	                                    int start_col = 0; // 시작행
	                                    int start_row = 0; // 시작열
	                                    int end_col = 0;    // 종료행
	                                    int end_row = 0;    // 종료열
	                                    
	                                    if(mergeRow > 0) {
	                                        start_col = idex;
	                                        start_row = 0;
	                                        end_col = start_col;
	                                        end_row = 1;
	                                        
	                                        sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, start_col, end_col));
	
	                                        sheet.setColumnWidth(idex, width);    // 넓이 조정
	                                        header_list.add(end_col, map);
	                                    } else if(mergeCol > 0) {
	                                        start_col = idex;
	                                        start_row = 0;
	                                        end_col = start_col+(mergeCol-1);
	                                        end_row = 0;
	                                        
	                                        sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, start_col, end_col));// 행, 열, 행, 열
	
	                                        for(int i = idex; i <= end_col; i++) {
	                                            header_list.add(i, null);
	                                            log.debug("index("+i+") is null");
	                                        }
	                                    } else {
	                                        sheet.setColumnWidth(idex, width);    // 넓이 조정
	                                        
	                                        header_list.add(idex, map);
	                                    }
	                                    
	                                    log.debug("header size:"+colList.size()+", s_col(1):"+start_col+", s_row:"+start_row+", e_col:"+end_col+", e_row:"+end_row);
	                                } else {
	                                	if(c == (colList.size()-1)){
	                                		// 두번째 행부터는 colspan한 상위열의 순서대로 등록한다.
	                                        int header_size = header_list.size();
	                                        
	                                        for(int i = 0; i < header_size; i++) {
	                                            if(header_list.get(i) == null) {
	                                                makeCell(i, row, title, cs, "header");
	                                                sheet.setColumnWidth(i, width);    // 넓이 조정
	                                                
	                                                header_list.set(i, map); // 비어있는 배열index에 순서대로 등록한다.
	                                                
	                                                log.debug("header size("+header_size+") / list("+i+")="+map);
	                                                break;
	                                            }
	                                        }
	                                	} else {
	                                		// 첫번째 열을 생성한다. 이때 Merge 설정이 있으면 실행한다. 
	                                        makeCell(idex, row, title, cs, "header");
	                                        
	                                        int start_col = 0; // 시작행
	                                        int start_row = 0; // 시작열
	                                        int end_col = 0;    // 종료행
	                                        int end_row = 0;    // 종료열
	                                        
	                                        if(mergeRow > 0) {
	                                            start_col = idex;
	                                            start_row = c;
	                                            end_col = start_col;
	                                            end_row = c+(mergeRow-1);
	                                            
	                                            sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, start_col, end_col));
	
	                                            sheet.setColumnWidth(idex, width);    // 넓이 조정
	                                            if(c == (colList.size()-2)) header_list.add(end_col, map);
	                                        } else if(mergeCol > 0) {
	                                            start_col = idex;
	                                            start_row = c;
	                                            end_col = start_col+(mergeCol-1);
	                                            end_row = c;
	                                            
	                                            sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, start_col, end_col));// 행, 열, 행, 열
	                                            
	                                            if(c == (colList.size()-2)) {
		                                            for(int i = idex; i <= end_col; i++) {
		                                            	header_list.add(i, null);
		                                                log.debug("index("+i+") is null");
		                                            }
	                                            }
	                                        } else {
	                                            sheet.setColumnWidth(idex, width);    // 넓이 조정
	                                            
	                                            if(c == (colList.size()-2)) header_list.add(idex, map);
	                                        }
	                                        
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
	                    // cell이 비어있는 부분에 style을 적용한다.
	                    for(int i = 0; i < header_list.size(); i++) {
		                    SXSSFCell cell = row.getCell(i);
		                    if(cell == null) {
		                    	cell = row.createCell(i);
		                    	cell.setCellStyle(getCellFormat(cs, "header"));
		                    }
	                    }
	                    
	                    rownum++;
	                }
	            }
	            
	            if(total > 0) {  // 본부 작성 부분
	            	CellStyle cs1 = workbook.createCellStyle(); // 일반 셀
	                cs1.setFont(font_02);
	                this.getCellFormat(cs1, "contents");
	                
	                CellStyle cs2 = workbook.createCellStyle(); // 필수항목 셀
	                cs2.setFont(font_02);
	                this.getCellFormat(cs2, "mandatory");
	                
	                CellStyle cs3 = workbook.createCellStyle(); // 일반 셀
	                cs3.setFont(font_03);
	                this.getCellFormat(cs3, "contents");
	                
	                CellStyle cs4 = workbook.createCellStyle(); // 일반 셀
	                cs4.setFont(font_04);
	                this.getCellFormat(cs4, "contents");
	                
	            	// 본문의 style 선언
	                startCnt = MAX_PER_SHEET_COUNT * (jnx - 1);
	                endCnt = (MAX_PER_SHEET_COUNT - 1) + MAX_PER_SHEET_COUNT * (jnx - 1);
	                
	                if(endCnt > total) endCnt = total - 1;
	                
	                for(int n = startCnt ; n <= endCnt ; n++) {
//	                	Object colValue = null;
	                    Object colListObj = header_list;//colList.get(colList.size()-1);
	                    
	                    if(colListObj != null && colListObj instanceof List) {
	                        List list = (List) colListObj;
	                        int idex = 0;
	                        
	                        // excel add row
	                        row = sheet.createRow(rownum);
	                        
	                        if(rownum%1000 == 0 || rownum == 1 || rownum == endCnt) {
	                        	if(log.isDebugEnabled()) log.debug("add row number = " + rownum);
	                        }
	                        
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
	                                
	                                if(!hidden) {
	                                	SXSSFCell cell = row.createCell(idex);
	                                	String cval = null;
	                                	
	                                	try {
	                                		cval = StringHelper.null2void(pVO.getString(n, field));
	                                		
	                                		// 엑셀에 표시전에 예약어를 삭제하고 폰트 색상을 바꾸는 로직을 추가함.(2022-02-24)
	                                		pVO.set(n, field, cval.replaceAll("::RED", ""));
	                                		pVO.set(n, field, cval.replaceAll("::BLUE", ""));
	                                		
	                                		if(StringHelper.null2void(pVO.getString(n, field)).isEmpty()) {
		                                		cell.setCellValue("");
	                        	                cell.setCellType(CellType.BLANK);
		                                	} else if(format.equals("int") && pVO != null) {
		                                		cell.setCellValue(pVO.getInt(n, field));
		                                    	cell.setCellType(CellType.NUMERIC);
		                                	} else if((format.equals("float") || format.equals("amount") || format.equals("quantity") || format.equals("rate") || format.equals("exchange")) && pVO != null) {
		                                		cell.setCellValue(pVO.getFloat(n, field));
		                                    	cell.setCellType(CellType.NUMERIC);
		                                	} else if(format.equals("percent")) {
                            	                double value = Double.parseDouble(StringHelper.null2string(pVO.getFloat(n, field), "0"))*100;
                            	                cell.setCellValue(String.valueOf(value+"%"));
                            	                cell.setCellType(CellType.STRING);
                            	            } else if(format.equals("percent1")) {
                            	                double value = Double.parseDouble(StringHelper.null2string(pVO.getFloat(n, field), "0"));
                            	                cell.setCellValue(String.valueOf(value+"%"));
                            	                cell.setCellType(CellType.STRING);
                            	            } else {
		                                		cell.setCellValue(pVO.getString(n, field));
		                                    	cell.setCellType(CellType.STRING);
		                                	}
	                                	} catch(Exception e) {
	                                		cell.setCellValue(pVO.getString(n, field));
	                                    	cell.setCellType(CellType.STRING);
	                                	}
                                		
	                                	if(cval.indexOf("::RED") > -1) {
	                                		cell.setCellValue(cval.replaceAll("::RED", ""));
	                                		cell.setCellStyle(cs3);
	                                	} else if(cval.indexOf("::BLUE") > -1) {
	                                		cell.setCellValue(cval.replaceAll("::BLUE", ""));
	                                		cell.setCellStyle(cs4);
	                                	} else {
		                                	if(title.trim().startsWith("*")) {
		                                		cell.setCellStyle(cs2);
		                                	} else {
		                                		cell.setCellStyle(cs1);
		                                	}
	                                	}
	                                	
	                                    idex++;
	                                }
	                            }
	                        }
	                        
	                    }
	                    rownum++;
	                }
	            }
	            startSheet++; // 시트번호
	        }
	        
	        if(log.isDebugEnabled()) log.debug("excel build end.");
        } catch(Exception e) {
        	if(log.isErrorEnabled()) log.error(e);
        	
        	throw e;
        }
        
        return startSheet;
    }
    
    /**
     * 엑셀파일을 생성한다.
     * 
     * @param pVO
     * @param workbook
     * @param columns
     * @throws IOException
     */
    private void build(ValueObject pVO, SXSSFWorkbook workbook, String[] columns) throws Exception {
        SXSSFSheet sheet = null;
        SXSSFRow row = null;
        
        int total = pVO.size();     // 데이터 전체 갯수
        int sheetCnt = total / MAX_PER_SHEET_COUNT;    // 시트 갯수
        int startCnt = 0;    // load할 index시작 번호
        int endCnt = 0;
        
        // 시트갯수 
        if((sheetCnt * MAX_PER_SHEET_COUNT) < total) sheetCnt++;
        
        for(int jnx = 1 ; jnx <= sheetCnt ; jnx++) {
        	String sname = StringHelper.null2string(pVO.getName(),"Sheet") + jnx;
        	
        	if(log.isDebugEnabled()) log.debug("sheet name = " + sname + ", sheet number = " + (startCnt+(jnx-1)) + ", row count(per sheet) = " + MAX_PER_SHEET_COUNT);
            
        	// 시트 생성
            if(workbook.getSheet(sname) == null) {
            	sheet = workbook.createSheet(sname);
            }
            
            sheet.setRandomAccessWindowSize(100); // 메모리 행 100개로 제한, 초과 시 Disk로 flush

            // excel add row
            row = sheet.createRow(0);
            
            // 해더 부분 작성
            for (int c = 0; c < columns.length; c++) {
                String colName = StringHelper.null2void(columns[c]);
                
                if(!StringHelper.isNull(colName)) {
                    colName = MessageResource.getMessageInstance().getMessage("TXT_"+colName);
                    makeCell(c, row, colName, workbook.createCellStyle(), "header");
                }
            }
            
            int rownum = 1;
            startCnt = MAX_PER_SHEET_COUNT * (jnx - 1);
            endCnt = (MAX_PER_SHEET_COUNT - 1) + MAX_PER_SHEET_COUNT * (jnx - 1);
            
            if(endCnt > total) endCnt = total - 1;
            
            for(int n = startCnt ; n <= endCnt ; n++) {
                String colName = null;
                Object colValue = null;
                
                // excel add row
                row = sheet.createRow(n);
                
                for (int i = 0 ; i < columns.length ; i++) {
                    colName = columns[i];
                    
                    if(!StringHelper.isNull(colName)) {
                        colValue = pVO.get(n, colName);
                        
                        if(colName.startsWith("*")) {
                        	makeCell(i, row, colValue, workbook.createCellStyle(), "mandatory");
                        } else {
                        	makeCell(i, row, colValue, workbook.createCellStyle(), "contents");
                        }
                    }
                }
                
                rownum++;
            }
        }
    
    }
    
    /**
     * 셀 타입에 맞게 데이터를 생성한다. 
     * 
     * @param col
     * @param row
     * @param columnValue
     * @return
     */
    private void makeCell(int col, SXSSFRow row, Object columnValue, CellStyle cellStyle, String type) throws Exception {
    	makeCell(col, row, columnValue, cellStyle, type, null);
    }
    
    private void makeCell(int col, SXSSFRow row, Object columnValue, CellStyle cellStyle, String type, String format) throws Exception {
        SXSSFCell cell = row.createCell(col);
        
        if(columnValue instanceof Long) {
            cell.setCellValue(Long.parseLong(StringHelper.null2void(columnValue)));
        	cell.setCellType(CellType.NUMERIC);
        } else if(columnValue instanceof Number) {
            cell.setCellValue(Double.parseDouble(StringHelper.null2void(columnValue)));
        	cell.setCellType(CellType.NUMERIC);
        } else if (columnValue instanceof Float) {
            cell.setCellValue(Float.parseFloat(StringHelper.null2void(columnValue)));
        	cell.setCellType(CellType.NUMERIC);
        } else if (columnValue instanceof Date) {
            cell.setCellValue((Date)columnValue);
        	cell.setCellType(CellType.NUMERIC);
        } else if (columnValue instanceof Boolean) {
            cell.setCellValue((Boolean)columnValue);
        	cell.setCellType(CellType.BOOLEAN);
        } else {
        	if(!StringHelper.isNull(format)) {
	        	if(format.equals("percent") && columnValue != null) {
	                double value = Double.parseDouble(StringHelper.null2string(columnValue, "0"))*100;
	                cell.setCellValue(String.valueOf(value+"%"));
	                cell.setCellType(CellType.STRING);
	            } else if(format.equals("percent1") && columnValue != null) {
	                double value = Double.parseDouble(StringHelper.null2string(columnValue, "0"));
	                cell.setCellValue(String.valueOf(value+"%"));
	                cell.setCellType(CellType.STRING);
	            } else {
	            	cell.setCellValue(StringHelper.unescape(StringHelper.null2void(columnValue)));
	                cell.setCellType(CellType.STRING);
	            }
        	} else {
	            if (columnValue == null) {
	                cell.setCellValue("");
	                cell.setCellType(CellType.BLANK);
	            } else {
	                cell.setCellValue(StringHelper.unescape(StringHelper.null2void(columnValue)));
	                cell.setCellType(CellType.STRING);
	            }
        	}
        }
    	
        if(cellStyle != null) {
        	cell.setCellStyle(this.getCellFormat(cellStyle, type)); // set cell style
        }
    }
    
    /**
     * 엑셀의 cell 스타일 반환
     * 
     * @param type
     * @return
     */
    private CellStyle getCellFormat(CellStyle cellStyle, String type) throws Exception {
        if(type.equals("header")) {
        	cellStyle.setBorderBottom(BorderStyle.THIN);
        	cellStyle.setBorderTop(BorderStyle.THIN);
        	cellStyle.setBorderLeft(BorderStyle.THIN);
        	cellStyle.setBorderRight(BorderStyle.THIN);
//        	cellStyle.setLocked(true);
        	cellStyle.setAlignment(HorizontalAlignment.CENTER);
        	cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        	cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if(type.equals("mandatory")) {
//        	cellStyle.setBorderBottom(CellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderTop(CellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderLeft(CellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderRight(CellStyle.BORDER_DOTTED);
        	cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        	cellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 색상에 이상하게 나오는 문제가 있어 확인이 필요함
        } else if(type.equals("contents")) {
//        	cellStyle.setBorderBottom(CellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderTop(CellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderLeft(CellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderRight(CellStyle.BORDER_DOTTED);
        	cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        	cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
//        	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } 
        
        return cellStyle;
    }
	
}
