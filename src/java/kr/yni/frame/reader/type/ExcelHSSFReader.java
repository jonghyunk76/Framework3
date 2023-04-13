package kr.yni.frame.reader.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;

import kr.yni.frame.Constants;
import kr.yni.frame.exception.FrameException;
import kr.yni.frame.reader.FileReader;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.ExcelUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.vo.ValueObject;

/**
 * Excel 2003~2007 버전의 읽기와 쓰기를 처리하는 클래스
 * 참고로, HSSF는 SS보다 처리속도가 빠르기 때문에 지원하고 있음 
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings("deprecation")
public class ExcelHSSFReader extends FileReader {
	
	private static Log log = LogFactory.getLog(ExcelHSSFReader.class);
	
	private static final int MAX_PER_SHEET_COUNT = Constants.EXCEL_SHEET_ROWS;    // 시트당 건씩 생성
    
    /**
     * <p>
     * 엑셀파일을 분석하여 List에 담아 리턴한다.(해더를 제외한 데이터만 포함시킨다.)<br>
     * 예제:<br>
     * List list = new ArrayList();<br>
     * Map map = new LinkedHashMap();<br>
     * map.put("COL_0", cell1.value);<br>
     * map.put("COL_1", cell2.value);<br>
     * map.put("COL_2", cell3.value);<br>
     * map.put("COL_3", cell4.value);<br>
     * list.add(map);
     * </p>
     * 
     * @param file 엑셀 파일
     * @param headIndex 읽어들일 row의 인덱스 시작번호
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> read(File file, int headIndex) throws Exception {
        return read(file, headIndex, null);
    }
    
    /**
     * <p>
     * 엑셀파일을 분석하여 List에 담아 리턴한다.(해더를 제외한 데이터만 포함시킨다.)<br>
     * </p>
     * 
     * @param path 파일경로(절대경로)
     * @param fileName 엑셀 파일명
     * @param headIndex 읽어들일 row의 인덱스 시작번호
     * @param sheetName 읽어들일 시트명
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> read(File file, int headIndex, String sheetName) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        HSSFWorkbook workbook = null;
        HSSFSheet sheet = null;
        Object cellData = null;
        
        if(!file.exists() || !file.isFile()) {
            throw new FrameException("not found file : " + file.getAbsolutePath());
        }
        
        FileInputStream in = new FileInputStream(file);
        POIFSFileSystem fs = new POIFSFileSystem(in);
        workbook = new HSSFWorkbook(fs);
        
        // 읽어들일 sheet를 구한다.
        if(sheetName == null || sheetName.isEmpty()) {
            sheet = workbook.getSheetAt(0);
        } else {
        	sheet = workbook.getSheet(sheetName);
            
            if(sheet == null) {
            	throw new FrameException("There is not found sheet.", sheetName);
            }
        }
            
        int rows = sheet.getPhysicalNumberOfRows();         // data row 갯수 
        
        if(log.isInfoEnabled()) {
            log.info("Excel info(File Source : " + file.getAbsolutePath() + ", Sheet Name : " + sheetName + ", ROW count:" + rows + ")");
        }
        
        if(headIndex < 0) {
            headIndex = 0;
        }
        
        try {
	        if(rows > headIndex){
	            // 엑셀 데이터 추출
	            for(int i = headIndex ; i < rows ; i++) {
	                Map<String, Object> hs = new LinkedHashMap<String, Object>();
	                boolean emptyCheck = false;
	                
	                // data column 갯수
	                HSSFRow row = sheet.getRow(i);
	                
	                if(row == null) {
	                	throw new FrameException("[ExcelRowLoadException] Excel load failed...");
	                }
	                
	                int colls = row.getLastCellNum(); //row.getPhysicalNumberOfCells();
	                
	                for(int k = 0 ; k < colls ; k++) {
	                	HSSFCell cell = row.getCell(k);
	                	
	                	if(cell != null) {
		                    // 숫자타입은 임의적으로 문자열 타입으로 변환시킨다.
		                    if(CellType.NUMERIC == cell.getCellType()) {
		                    	cell.setCellType(CellType.STRING);
		                    }
		                    
		                    if(CellType.FORMULA == cell.getCellType()) {
		                    	try {
		                    		DecimalFormat formatter = new DecimalFormat("0.########");
		                    		cellData = StringHelper.null2void(formatter.format(cell.getNumericCellValue()));
		                    	} catch(Exception e) {
		                    		try {
		                    			cellData = StringHelper.null2void(cell.getStringCellValue()).replace(",", "€");
		                    		} catch(Exception ex) {
		                    			if(log.isErrorEnabled()) log.error(ex);
		                    			cellData = null;
		                    		}
		                    	}
								break;
		                    } else if(CellType.NUMERIC == cell.getCellType()) {
								if(DateUtil.isCellDateFormatted(cell)) { // 날짜
									SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
									cellData = StringHelper.null2void(formatter.format(cell.getDateCellValue()));
								} else { // 숫자
									DecimalFormat formatter = new DecimalFormat("0.########");
									cellData = StringHelper.null2void(formatter.format(cell.getNumericCellValue()));
									
									// 숫자인 경우, null값이 인식되지 않은 경우가 있어 숫자로 변환 후 체크하도록 추가함
		                			cell.setCellType(CellType.STRING); 
		                			String strValue = StringHelper.null2void(cell.getStringCellValue()).trim();
		                			
		                			if(strValue == null || strValue.isEmpty()) {
		                				cellData = "";
		                			}
		                			
//			                			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								}
								break;
		                    } else if(CellType.STRING == cell.getCellType()) {
								cellData = StringHelper.null2void(cell.getStringCellValue()).trim();
		                        cellData = StringHelper.null2void(cellData).replaceAll(",", ""); // 숫자인 경우 ,를 제거함
		                        break;
		                    } else if(CellType.BLANK == cell.getCellType()) {
								cellData = "";
								break;
		                    } else if(CellType.BOOLEAN == cell.getCellType()) {
								cellData = StringHelper.null2void(cell.getBooleanCellValue());
								break;
		                    } else if(CellType.ERROR == cell.getCellType()) {
								cellData = StringHelper.null2void(cell.getErrorCellValue());
								break;
		                    } else {
								cellData = StringHelper.null2void(cell.getRichStringCellValue());
		                    }
		                  if(i == headIndex || i == (rows-1)) {
		                	  if(log.isDebugEnabled()) log.debug("(" + i + "row) Cell type = " + cell.getCellType() + ", data = " + cellData);
		                  }
	                	} else {
	                		cellData = "";
	                	}
	                	
	                    hs.put("COL_" + k, cellData);
	                    
	                    if(cellData != null && !"".equals(cellData)) {
	                        emptyCheck = true;
	                    }
	                }
	                
	                if(emptyCheck) { // 데이터가 없는 경우엔 예외처리한다.
	                    list.add(hs);
	                }
	            }
	        }
        } catch(Exception e){
            if(log.isErrorEnabled()) log.error(e);
            throw e;
        } finally {
        	if(in != null) in.close();
        }
        
        if(log.isDebugEnabled())  log.debug("File contents size = " + list.size());
        
        return list;
    }
    
    /**
     * <code>List</code>에 담긴 데이터를 Excel파일로 변환한다.<br> 
     *  컬럼명은 list-Map에 등록된 key값으로 자동 설정된다.
     * 
     * @param pVO 
     * @param file Excel 파일 저장경로
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void write(List list, File file) throws Exception {
    	FileOutputStream out = null;
        try {
	    	out = new FileOutputStream(file);
	        
	        ValueObject vo = ExcelUtil.transformVO(list, null);
	        String[] columns = ExcelUtil.transformColumns((Map<String, Object>) list.get(0));
	        
	        write(vo, out, columns);
        } catch(Exception ex) {
        	if(out != null) out.close();
        }
    }
    
    /**
     * <code>List</code>에 담긴 데이터를 Excel파일로 변환하고 파일을 서버에 저장한다.<br>
     * columns에 해당하는 컬럼만 엑셀에 표시한다.
     * 
     * @param pVO 
     * @param file Excel 파일 저장경로
     * @param columns 컬럼명 배열(엑셀에 등록하고자 하는 컬럼만 배열에 포함시킬 것)
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public void write(List list, File file, String[] columns) throws Exception {
    	FileOutputStream out = null;
        try {
        	out = new FileOutputStream(file);
            
            ValueObject vo = ExcelUtil.transformVO(list, file.getName());
            
            write(vo, out, columns);
        } catch(Exception ex) {
        	if(out != null) out.close();
        }
    }
    
    /**
     * <code>List</code>에 담긴 데이터를 Excel 파일로 변환한다.<br>
     * ExcelView에서 호출된다.
     * 
     * @param list : type = [[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string}],
     *                             ,[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string}]]
     * @param workbook : excel object
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public void view(List<List<Map<String, Object>>> list, Object wb) throws Exception {
    	HSSFWorkbook workbook = (HSSFWorkbook)wb;
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
        HSSFWorkbook workbook = null;
        
        try {
            workbook = new HSSFWorkbook();
            
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
    private int build(ValueObject pVO, HSSFWorkbook workbook, List colList, int startSheet) throws Exception {
        if(log.isDebugEnabled()) log.debug("excel build start....");
        
        HSSFSheet sheet = null;
        HSSFRow row = null;
        
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
        
        for(int jnx = 1 ; jnx <= sheetCnt ; jnx++) {
            String sname = StringHelper.null2string(pVO.getName(), "Sheet" + (jnx));
            if(sheetCnt > 1) sname = sname + "(" + jnx + ")";
            
            if(log.isDebugEnabled()) log.debug("sheet name = " + sname + ", sheet number = " + (startSheet+(jnx-1)) + ", row count(per sheet) = " + MAX_PER_SHEET_COUNT);
            
            // 시트 생성
            if(workbook.getSheet(sname) == null) {
            	sheet = workbook.createSheet(sname);
            }
            
            // 폰트 색상
            HSSFFont font_01 = workbook.createFont();
            font_01.setColor(IndexedColors.BLACK.index);
            font_01.setBold(true);
            
            HSSFFont font_02 = workbook.createFont();
            font_02.setColor(IndexedColors.DARK_GREEN.index);
            
            int rownum = 0;
            List header_list = new LinkedList();  // 해더의 열이 두개인 경우를 처리하기 위한 임시객체
            
            for (int c = 0; c < colList.size() ; c++) { // 해더 작성부분
            	// 해더의 style 선언
                HSSFCellStyle cs = workbook.createCellStyle();
                cs.setFont(font_01);
                cs.setWrapText(true);
                
                Object colListObj = colList.get(c);
                
                if(colListObj != null && colListObj instanceof List) {
                    List list = (List) colListObj;
                    int idex = 0; 
                    
                    // excel add row
                    row = sheet.createRow((short)c);
                    
                    for(int m = 0 ; m < list.size(); m++) { // 해더의 column 수만큼 Loop...
                        Object colMapObj = list.get(m);
                        
                        if(colMapObj != null && colMapObj instanceof Map) {
                            Map map = (Map) colMapObj;
                            
                            field = StringHelper.null2void(map.get("field"));
                            title = StringHelper.null2void(map.get("title"));
                            title = title.replace("<BR>", "").replace("<br>", "");
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
                                        
                                        sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, (short)start_col, (short)end_col));

                                        sheet.setColumnWidth(idex, width);    // 넓이 조정
                                        header_list.add(end_col, map);
                                    } else if(mergeCol > 0) {
                                        start_col = idex;
                                        start_row = 0;
                                        end_col = start_col+(mergeCol-1);
                                        end_row = 0;
                                        
                                        sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, (short)start_col, (short)end_col));// 행, 열, 행, 열

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
                                            
                                            sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, (short)start_col, (short)end_col));

                                            sheet.setColumnWidth(idex, width);    // 넓이 조정
                                            if(c == (colList.size()-2)) header_list.add(end_col, map);
                                        } else if(mergeCol > 0) {
                                            start_col = idex;
                                            start_row = c;
                                            end_col = start_col+(mergeCol-1);
                                            end_row = c;
                                            
                                            sheet.addMergedRegion(new CellRangeAddress(start_row, end_row, (short)start_col, (short)end_col));// 행, 열, 행, 열
                                            
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
	                    HSSFCell cell = row.getCell(i);
	                    if(cell == null) {
	                    	cell = row.createCell(i);
	                    	cell.setCellStyle(getCellFormat(cs, "header"));
	                    }
                    }
                    
                    rownum++;
                }
            }
            
            if(total > 0) {  // 본부 작성 부분
            	HSSFCellStyle cs1 = workbook.createCellStyle(); // 일반 셀
                cs1.setFont(font_02);
                
                HSSFCellStyle cs2 = workbook.createCellStyle(); // 필수항목 셀
                cs2.setFont(font_02);
                
            	// 본문의 style 선언
                startCnt = MAX_PER_SHEET_COUNT * (jnx - 1);
                endCnt = (MAX_PER_SHEET_COUNT - 1) + MAX_PER_SHEET_COUNT * (jnx - 1);
                
                if(endCnt > total) endCnt = total - 1;
                
                for(int n = startCnt ; n <= endCnt ; n++) {
                	Object colValue = null;
                    Object colListObj = header_list;//colList.get(colList.size()-1);
                    
                    if(colListObj != null && colListObj instanceof List) {
                        List list = (List) colListObj;
                        int idex = 0;
                        
                        // excel add row
                        row = sheet.createRow((short)rownum);
                        
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
                                	try {
	                                	if(format.equals("int") && pVO != null) {
	                                		colValue = pVO.getInt(n, field);
	                                	} if((format.equals("float") || format.equals("amount") || format.equals("quantity") || format.equals("rate") || format.equals("exchange")) && pVO != null) {
	                                		colValue = pVO.getFloat(n, field);
	                                	} else {
	                                		colValue = pVO.getString(n, field);
	                                	}
                                	} catch(Exception e) {
                                		colValue = pVO.getString(n, field);
                                	}
                                	
                                    if(title.startsWith("*")) {
                                    	makeCell(idex, row, colValue, cs2, "mandatory", format);
                                    } else {
                                    	makeCell(idex, row, colValue, cs1, "contents", format);
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
    private void build(ValueObject pVO, HSSFWorkbook workbook, String[] columns) throws Exception {
        HSSFSheet sheet = null;
        HSSFRow row = null;
        
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
            
            // excel add row
            row = sheet.createRow((short)0);
            
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
                row = sheet.createRow((short)n);
                
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
    private void makeCell(int col, HSSFRow row, Object columnValue, HSSFCellStyle cellStyle, String type) throws Exception {
    	makeCell(col, row, columnValue, cellStyle, type, null);
    }
    
    private void makeCell(int col, HSSFRow row, Object columnValue, HSSFCellStyle cellStyle, String type, String format) throws Exception {
        HSSFCell cell = row.createCell(col);
        
        if(columnValue instanceof Integer) {
            cell.setCellValue(Double.parseDouble(StringHelper.null2void(columnValue)));
        	cell.setCellType(CellType.NUMERIC);
        } else if (columnValue instanceof Number) {
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
    private HSSFCellStyle getCellFormat(HSSFCellStyle cellStyle, String type) throws Exception {
        if(type.equals("header")) {
        	cellStyle.setBorderBottom(BorderStyle.THIN);
        	cellStyle.setBorderTop(BorderStyle.THIN);
        	cellStyle.setBorderLeft(BorderStyle.THIN);
        	cellStyle.setBorderRight(BorderStyle.THIN);
        	cellStyle.setLocked(true);
        	cellStyle.setAlignment(HorizontalAlignment.CENTER);
        	cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        	cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if(type.equals("contents")) {
        	cellStyle.setBorderBottom(BorderStyle.DOTTED);
        	cellStyle.setBorderTop(BorderStyle.DOTTED);
        	cellStyle.setBorderLeft(BorderStyle.DOTTED);
        	cellStyle.setBorderRight(BorderStyle.DOTTED);
        	cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        	cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if(type.equals("mandatory")) {
        	cellStyle.setBorderBottom(BorderStyle.DOTTED);
        	cellStyle.setBorderTop(BorderStyle.DOTTED);
        	cellStyle.setBorderLeft(BorderStyle.DOTTED);
        	cellStyle.setBorderRight(BorderStyle.DOTTED);
        	cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        	cellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        	
        	if(log.isDebugEnabled()) log.debug("cell background = " + IndexedColors.YELLOW.index + ", type = " + type);
        }
        
        return cellStyle;
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
