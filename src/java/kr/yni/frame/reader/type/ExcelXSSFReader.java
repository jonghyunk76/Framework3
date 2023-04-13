package kr.yni.frame.reader.type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
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
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import kr.yni.frame.exception.FrameException;
import kr.yni.frame.reader.FileReader;
import kr.yni.frame.resources.MessageResource;
import kr.yni.frame.util.ExcelUtil;
import kr.yni.frame.util.StringHelper;
import kr.yni.frame.vo.ValueObject;

/**
 * Excel 통합문서XSSF의 읽기와 쓰기를 처리하는 클래스 
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings("unchecked")
public class ExcelXSSFReader extends FileReader {
	
	private static Log log = LogFactory.getLog(ExcelXSSFReader.class);
//	private static final int MAX_PER_SHEET_COUNT = Constants.EXCEL_SHEET_ROWS;    // 시트당 건씩 생성
	private static final int MAX_PER_SHEET_COUNT = 1000000;    // 시트당 건씩 생성(백만건으로 고정), 최대 1,048,576행
	
	private XSSFWorkbook workbook = null;
    private int maxRowWidth = 0;
    private int formattingConvention = 0;
    private int startRownum = 0;
    private DataFormatter formatter = null;
    private FormulaEvaluator evaluator = null;
    private String separator = null;

    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String DEFAULT_SEPARATOR = ",";
    
    /**
     * 인스턴스된 XSSFWorkbook을 리턴한다. 
     * 
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook() {
    	return this.workbook;
    }
    
    /**
     * XSSFWorkbook이 null이라면 생성한 후 리턴한다. 
     * 
     * @param file file객체
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook(File file) throws Exception {
    	if(this.workbook == null) {
    		this.openWorkbook(file, false);
    	}
    	
    	return this.workbook;
    }
    
    /**
     * 특정 문자가 이스케이프에 관련된 경우 Excel의 서식 규칙을 준수해야 함을 나타냄
     * (필드 분리문자, 라인의 끝 등)
     */
    public static final int EXCEL_STYLE_ESCAPING = 0;

    /**
     * 특정 문자가 이스케이프에 관련된 경우 UNIX의 형식규칙을 준수해야 함을 나타냄
     * (필드 분리문자, 라인의 끝 등)
     */
    public static final int UNIX_STYLE_ESCAPING = 1;
    
	public List<Map<String, Object>> read(File file, int headIndex) throws Exception {
    	return this.read(file, headIndex, null, null, this.DEFAULT_SEPARATOR, this.EXCEL_STYLE_ESCAPING, false);
	}
    
    public List<Map<String, Object>> read(File file, int headIndex, String sheetName) throws Exception {
    	return this.read(file, headIndex, sheetName, null, this.DEFAULT_SEPARATOR, this.EXCEL_STYLE_ESCAPING, false);
	}
    
    public List<Map<String, Object>> read(File file, int headIndex, String sheetName, boolean formula) throws Exception {
    	return this.read(file, headIndex, sheetName, null, this.DEFAULT_SEPARATOR, this.EXCEL_STYLE_ESCAPING, formula);
	}
    
    /**
      <p>
     * 엑셀파일을 분석하여 List에 담아 리턴한다.<br>
     * </p>
     * 
     * @param strSource				Excel파일 위치(소스파일)
     * @param strDestination		CSV 저장 위치(디렉토리)
     * @param separator 			필드 분리자
     * @param formattingConvention  규칙의 구분자
     * @param formula				엑셀 수식 적용 여부
     * @throws FileNotFoundException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws InvalidFormatException
     */
    private List<Map<String, Object>> read(File file, int headIndex, String sheetName, String distPath, String separator, int formattingConvention, boolean formula)
                       throws Exception {
        File source = file;
        File[] filesList = null;
        List<Map<String, Object>> list = null;
        try {
	        // File checker
	        if(!source.exists()) {
	            throw new FrameException("The source for the Excel file(s) cannot be found.");
	        }
	
	        // 요규하는 내용이 규칙에 맞는지 체크
	        if(formattingConvention != this.EXCEL_STYLE_ESCAPING && formattingConvention != this.UNIX_STYLE_ESCAPING) {
	            throw new FrameException("The value passed to the formattingConvention parameter is out of range.");
	        }
	
	        this.separator = separator;
	        this.formattingConvention = formattingConvention;
	        
	        if(source.isDirectory()) {
	            filesList = source.listFiles(new ExcelFilenameFilter());
	        } else {
	            filesList = new File[]{source};
	        }
	
	        for(File excelFile : filesList) {
	        	if(log.isDebugEnabled()) log.debug("Opening XSSFWorkbook- " + excelFile.getName() + " [" + excelFile + "]");
	        	
	        	this.openWorkbook(excelFile, formula);
	            
	            if(headIndex <0) startRownum = 0;
	            
	            list = this.convertToCSV(sheetName);
	            
	            if(log.isDebugEnabled()) log.debug("row size = " + list.size());
	        }
        } catch(Exception ep) {
        	throw ep;
        }
        
        return list;
    }

    /**
     * 엑셀파일을 열고, 수식계산을 위한 에뮬레이터와 데이터 포맷터를 생성한다. 
     * 
     * @param file 파일 객체
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidFormatException
     */
    private void openWorkbook(File file, boolean formula) throws Exception {
        InputStream fis = null;
        POIFSFileSystem pfs = null;
        
        try {
        	if(file == null) {
                throw new FileNotFoundException("Unable to locate give file..");
        	} else {
        		// 대용량 엑셀 데이터 처리를 위해 변경(기본:1억 > 10억). 2023-02-01
        		fis = Files.newInputStream(file.toPath()); // FileInputStream에서 InputStream으로 변경
                IOUtils.setByteArrayMaxOverride(1000000000); // 최대크기 변경
                
	        	if(!fis.markSupported()) {
	        		fis = new PushbackInputStream(fis, 8);
	        	}
	        	
	        	pfs  = new POIFSFileSystem(fis);
	        	
//	        	if(POIFSFileSystem.hasPOIFSHeader(fis)) {
//	        		EncryptionInfo info = new EncryptionInfo(pfs);
//	                Decryptor d = Decryptor.getInstance(info);
//	                
//	                if (!d.verifyPassword(d.DEFAULT_PASSWORD)) {
//	                	throw new EncryptedDocumentException("Unable to process: document is encrypted");
//	                }
//	                
//	                fis = d.getDataStream(pfs);
//	        	}
                
	        	if(log.isInfoEnabled()) log.info("available file : " + fis.available());

	        	this.workbook = new XSSFWorkbook(fis);
	        	if(formula) this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
	        	this.formatter = new DataFormatter(true);
	        	
	        	log.debug("created workbook.... OK");
        	}
        } catch(Exception ep) {
        	log.error("File open Error : " + ep);
        	throw ep;
        } finally {
            if(fis != null) {
                fis.close();
            }
        }
    }

    /**
     * 엑셀파일의 내용을 Parsing 한다.
     * 
     * @param sheetName 엑셀 시트명
     * @return sheet에 입력된 값을 List<Map<String, ObjecT>>로 리턴
     * @throws Exception 
     */
    private List convertToCSV(String sheetName) throws Exception {
        XSSFSheet sheet = null;
        XSSFRow row = null;
        int lastRowNum = 0;
        List list = new ArrayList<Map<String, Object>>();

        if(log.isDebugEnabled()) log.debug("Converting files contents to CSV format(sheet name = " + sheetName + ").");
        
        if(sheetName == null) {
	        int numSheets = this.workbook.getNumberOfSheets();
	        
	        for(int i = 0; i < numSheets; i++) {
	        	sheet = this.workbook.getSheetAt(i);
	        	
	            if(sheet.getPhysicalNumberOfRows() > 0) {
	                lastRowNum = sheet.getLastRowNum();
	                
	                for(int j = startRownum; j <= lastRowNum; j++) {
	                    row = sheet.getRow(j);
	                    
	                    Map rowMap = this.rowToCSV(row, (j+1));
	                    
	                    if(rowMap != null && rowMap.size() > 0 ) {
	                    	list.add(rowMap);
	                    }
	                }
	            }
	        }
        } else {
        	sheet = this.workbook.getSheet(sheetName);
        	
        	if(sheet != null && sheet.getPhysicalNumberOfRows() > 0) {
                lastRowNum = sheet.getLastRowNum();
                
                for(int j = startRownum; j <= lastRowNum; j++) {
                    row = sheet.getRow(j);
                    Map rowMap = this.rowToCSV(row, (j+1));
                    
                    if(rowMap != null && rowMap.size() > 0 ) {
                    	list.add(rowMap);
                    }
                }
            } else {
            	throw new FrameException("There is not found sheet.", sheetName);
            }
        }
        
        return list;
    }

    /**
     * 엑셀내용을 <code>List</code>에 담는다.
     * @param row = 엑셀의 row 값
     * @param rownum = 읽기 시작할 열번호(default:0)
     * @return 열에 해당하는 값을 Map에 담아 리턴
     * @throws Exception
     */
    private Map rowToCSV(XSSFRow row, int rownum) throws Exception {
        XSSFCell cell = null;
        int lastCellNum = 0;
        Map<String, Object> csvLine = new LinkedHashMap<String, Object>();
        String emptyTest = null;
        
        if(row != null) {
            lastCellNum = row.getLastCellNum();
            char ch = 'A';
            
            for(int i = 0; i <= lastCellNum; i++) {
                cell = row.getCell(i);
                String cellName = StringHelper.nextAlphabet(i)+rownum; // Character.toString(ch) + rownum;
                
                if(cell == null) {
                    csvLine.put(cellName , "");
                } else {
                	CellType cellValueType = cell.getCellType();
                	
                	if(this.evaluator != null) {
	                	CellValue cvalue = this.evaluator.evaluate(cell);
	                    cellValueType = cvalue.getCellType();
	                    
	                    log.debug("XSS Reader > evaluator's cellValueType = " + cellValueType);
                	}
                	
                	if(cellValueType == CellType.FORMULA) {
                		String cellValue = null;
                		
                    	try {
//                    		if(this.evaluator == null) {
//                    			cellValue = "";
//                    		} else {
//                    			cellValue = StringHelper.null2void(cell.getStringCellValue()); // this.formatter.formatCellValue(cell, this.evaluator);
//                    		}
                    		DecimalFormat formatter = new DecimalFormat("0.########");
                    		cellValue = StringHelper.null2void(formatter.format(cell.getNumericCellValue()));
                    	} catch(Exception e) {
                    		try {
	//                    		if(this.evaluator != null) csvLine.put(cellName, this.formatter.formatCellValue(cell).replace(",", "€"));
	                    		cellValue = StringHelper.null2void(cell.getStringCellValue()).replace(",", "€");
                    		} catch(Exception ex) {
	                    		if(log.isErrorEnabled()) log.error(ex);
                    			cellValue = null;
                    		}
                    	}
                    	
                    	csvLine.put(cellName, cellValue);
                    } else if(cellValueType == CellType.NUMERIC) {
                    	if(ExcelUtil.isADateFormat(cell)) { // 날짜 포맷인지 체크
                    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    		String cellValue = StringHelper.null2void(sdf.format(cell.getDateCellValue()));
                    		
//                    		log.debug("2.getDateCellValue date = " + cellValue);
                    		
                    		csvLine.put(cellName, cellValue);
                    		
                    		emptyTest += cellValue;
                    	} else {
                    		String cellValue = null;
                    		
                    		DecimalFormat formatter = new DecimalFormat("0.########");
                			cellValue = StringHelper.null2void(formatter.format(cell.getNumericCellValue()));
                			
                			// 숫자인 경우, null값이 인식되지 않은 경우가 있어 숫자로 변환 후 체크하도록 추가함
                    		cell.setCellType(CellType.STRING); 
                			String strValue = StringHelper.null2void(cell.getStringCellValue()).trim();
                			
                			if(strValue == null || strValue.isEmpty()) {
                				cellValue = "";
                			}
                			
//                			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                			
//                			log.debug("2.getNumericCellValue = " + cellValue);
                			
                    		csvLine.put(cellName, cellValue);
                    		
                    		emptyTest += cellValue;
                    	}
                    } else {
                    	String cellValue = this.formatter.formatCellValue(cell);
                    	csvLine.put(cellName, cellValue);
                    	
                    	emptyTest += cellValue;
                    }
                }
                
                ch++;
            }

            if(lastCellNum > this.maxRowWidth) {
                this.maxRowWidth = lastCellNum;
            }
            
            csvLine.put("ROW_HEIGHT", row.getHeight());
        }
        
//        if(emptyTest == null || emptyTest.trim().isEmpty()) {
//        	csvLine = null;
//        }
        
        return csvLine;
    }
    
    /**
     * Cell값을 escape시킨 후 문자열 리턴
     * @param field = cell value
     * @return escape 문자열
     * @throws Exception
     */
    private String escapeEmbeddedCharacters(String field) throws Exception {
        StringBuffer buffer = null;

        if(this.formattingConvention == this.EXCEL_STYLE_ESCAPING) {
            if(field.contains("\"")) {
                buffer = new StringBuffer(field.replaceAll("\"", "\\\"\\\""));
                buffer.insert(0, "\"");
                buffer.append("\"");
            }
            else {
                buffer = new StringBuffer(field);
                if((buffer.indexOf(this.separator)) > -1 ||
                         (buffer.indexOf("\n")) > -1) {
                    buffer.insert(0, "\"");
                    buffer.append("\"");
                }
            }
            return(buffer.toString().trim());
        } else {
            if(field.contains(this.separator)) {
                field = field.replaceAll(this.separator, ("\\\\" + this.separator));
            }
            if(field.contains("\n")) {
                field = field.replaceAll("\n", "\\\\\n");
            }
            return(field);
        }
    }

    /**
     * 지정된 명칭이 엑셀2003 이상인지 체크
     * @param file = File 객체
     * @Param name = 파일명칭
     * @return .xlsx파일인지 여부(true|false)
     *
     */
    class ExcelFilenameFilter implements FilenameFilter {
        public boolean accept(File file, String name) {
            return (name.endsWith(".xlsx"));
        }
    }

    /**
     * <code>List</code> 내용을 CSV 파일로 저장한다.
     * 
     * @param list 파일에 기록할 내역
     * @param file 출력할 파일명(CSV파일)
     * @throws FileNotFoundException
     * @throws IOException
     */
	public void write(List list, File file) throws Exception {
		this.write(list, file, null);
	}
	
	/**
     * <code>List</code> 내용을 CSV 파일로 저장한다.
     * 
     * @param list 파일에 기록할 내역
     * @param file 출력할 파일명(CSV파일)
     * @param columns 컬럼명(사용하지 않음)
     * @throws FileNotFoundException
     * @throws IOException
     */
	public void write(List list, File file, String[] columns) throws Exception {
		FileWriter fw = null;
        BufferedWriter bw = null;
        Map<String, Object> line = null;
        StringBuffer buffer = null;
        String csvLineElement = null;
		
        String destinationFilename = file.getName();
        destinationFilename = destinationFilename.substring(0, destinationFilename.lastIndexOf(".")) + this.CSV_FILE_EXTENSION;
        
		File destination = new File(file.getParent(), destinationFilename);
        
        try {
        	if(log.isDebugEnabled()) log.debug("Saving the CSV file [" + destination.getName() + "]");
        	if(log.isDebugEnabled()) log.debug("this.maxRowWidth = " + this.maxRowWidth + ", start rownum = " + startRownum);
        	
            fw = new FileWriter(destination);
            bw = new BufferedWriter(fw);
            int number = startRownum+1;
            
            for(int i = 0; i < list.size(); i++) {
                buffer = new StringBuffer();
                line = (Map) list.get(i);
                
                for(int j = 0; j < this.maxRowWidth; j++) {
                	String ch = StringHelper.nextAlphabet(j);
                	
                    if(line.size() > j) {
                    	String cellName = ch + (number);
                    	csvLineElement = StringHelper.null2void(line.get(cellName)).replace("€", ",");
                        
                        if(csvLineElement != null) {
                            buffer.append(this.escapeEmbeddedCharacters(csvLineElement));
                        }
                    }
                    if(j < (this.maxRowWidth - 1)) {
                        buffer.append(this.separator);
                    }
                }
                
                bw.write(buffer.toString().trim());

                if(i < (list.size() - 1)) {
                    bw.newLine();
                }
                
                number++;
            }
        }
        
        finally {
            if(bw != null) {
                bw.flush();
                bw.close();
            }
        }
	}

	public void view(List<List<Map<String, Object>>> list, Object wb) throws Exception {
		XSSFWorkbook workbook = (XSSFWorkbook) wb;
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
        XSSFWorkbook workbook = null;
        
        try {
            workbook = new XSSFWorkbook();
            
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
    private int build(ValueObject pVO, XSSFWorkbook workbook, List colList, int startSheet) throws Exception {
        if(log.isDebugEnabled()) log.debug("excel build start....");
        
        XSSFSheet sheet = null;
        XSSFRow row = null;
        
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
            XSSFFont font_01 = workbook.createFont();
            font_01.setColor(IndexedColors.BLACK.index);
            font_01.setBold(true);
            
            XSSFFont font_02 = workbook.createFont();
            font_02.setColor(IndexedColors.DARK_GREEN.index);
            
            int rownum = 0;
            List header_list = new LinkedList();  // 해더의 열이 두개인 경우를 처리하기 위한 임시객체
            
            for (int c = 0; c < colList.size() ; c++) { // 해더 작성부분
            	// 해더의 style 선언
                XSSFCellStyle cs = workbook.createCellStyle();
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
	                    XSSFCell cell = row.getCell(i);
	                    if(cell == null) {
	                    	cell = row.createCell(i);
	                    	cell.setCellStyle(getCellFormat(cs, "header"));
	                    }
                    }
                    
                    rownum++;
                }
            }
            
            if(total > 0) {  // 본부 작성 부분
            	XSSFCellStyle cs1 = workbook.createCellStyle(); // 일반 셀
                cs1.setFont(font_02);
                this.getCellFormat(cs1, "contents");
                
                XSSFCellStyle cs2 = workbook.createCellStyle(); // 필수항목 셀
                cs2.setFont(font_02);
                this.getCellFormat(cs2, "mandatory");
                
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
                                	XSSFCell cell = row.createCell(idex);
                                	
                                	try {
                                		if(StringHelper.null2void(pVO.getInt(n, field)).isEmpty()) {
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
                            		
                                	if(title.trim().startsWith("*")) {
                                		cell.setCellStyle(cs2);
                                	} else {
                                		cell.setCellStyle(cs1);
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
    private void build(ValueObject pVO, XSSFWorkbook workbook, String[] columns) throws Exception {
        XSSFSheet sheet = null;
        XSSFRow row = null;
        
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
    private void makeCell(int col, XSSFRow row, Object columnValue, XSSFCellStyle cellStyle, String type) throws Exception {
    	makeCell(col, row, columnValue, cellStyle, type, null);
    }
    
    private void makeCell(int col, XSSFRow row, Object columnValue, XSSFCellStyle cellStyle, String type, String format) throws Exception {
        XSSFCell cell = row.createCell(col);
        
        if(columnValue instanceof Integer) {
            cell.setCellValue(Double.parseDouble(StringHelper.null2void(columnValue)));
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
    private XSSFCellStyle getCellFormat(XSSFCellStyle cellStyle, String type) throws Exception {
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
//        	cellStyle.setBorderBottom(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderTop(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderLeft(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderRight(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setVerticalAlignment(XSSFVerticalAlignment.CENTER);
        	cellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        	
        	if(log.isDebugEnabled()) log.debug("cell background = " + IndexedColors.YELLOW.index + ", type = " + type);
        }
//        else if(type.equals("contents")) {
//        	cellStyle.setBorderBottom(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderTop(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderLeft(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setBorderRight(XSSFCellStyle.BORDER_DOTTED);
//        	cellStyle.setVerticalAlignment(XSSFVerticalAlignment.CENTER);
//        	cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
//        	cellStyle.setFillPattern(XSSFFillPatternType.SOLID_FOREGROUND);
//        } 
        
        return cellStyle;
    }
	
}
