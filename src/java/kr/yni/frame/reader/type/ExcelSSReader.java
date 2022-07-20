package kr.yni.frame.reader.type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import kr.yni.frame.exception.FrameException;
import kr.yni.frame.reader.FileReader;
import kr.yni.frame.util.ExcelUtil;
import kr.yni.frame.util.StringHelper;

/**
 * Excel 통합문서(SS=HSSF+XSSF)의 읽기와 쓰기를 처리하는 클래스 
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings("unchecked")
public class ExcelSSReader extends FileReader {
	
	private static Log log = LogFactory.getLog(ExcelSSReader.class);
	
	private Workbook workbook = null;
    private int maxRowWidth = 0;
    private int formattingConvention = 0;
    private int startRownum = 0;
    private DataFormatter formatter = null;
    private FormulaEvaluator evaluator = null;
    private String separator = null;

    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String DEFAULT_SEPARATOR = ",";

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
            this.openWorkbook(excelFile, formula);
            
            if(headIndex <0) startRownum = 0;
            
            list = this.convertToCSV(sheetName);
        }
        
        return list;
    }

    /**
     * 엑셀파일을 열고, 수식계산을 위한 에뮬레이터와 데이터 포맷터를 생성한다. 
     * 
     * @param file 파일 객체
     * @param formula 엑셀 수식 적용여부
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidFormatException
     */
    private void openWorkbook(File file, boolean formula) throws Exception {
        InputStream fis = null;
        POIFSFileSystem pfs = null;
        
        try {
            if(log.isDebugEnabled()) log.debug("Opening workbook [" + file.getName() + "]");
            
            fis = new FileInputStream(file);
            if(file.getName().toLowerCase().endsWith(".xls")) {
	            if(!fis.markSupported()) {
	        		fis = new PushbackInputStream(fis, 8);
	        	}
	            
	            pfs  = new POIFSFileSystem(fis);
	            
//	        	if(POIFSFileSystem.hasPOIFSHeader(fis)) {
//	        		String password = StringHelper.null2void(Biff8EncryptionKey.getCurrentUserPassword());
//	                
//	        		if(log.isInfoEnabled()) log.info("password : " + password);
//	        		
//	                if (!password.isEmpty()) {
//	                	throw new RuntimeException("Unable to process: document is encrypted");
//	                }
//	        	}
	        	
	        	this.workbook = WorkbookFactory.create(pfs);
            } else {
            	this.workbook = WorkbookFactory.create(fis);
            }
            if(formula) this.evaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
            this.formatter = new DataFormatter(true);
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
        Sheet sheet = null;
        Row row = null;
        int lastRowNum = 0;
        List list = new ArrayList<Map<String, Object>>();

        if(log.isDebugEnabled()) log.debug("Converting files contents to CSV format(sheet name = " + sheetName + ").");
        
        if(sheetName == null) {
	        int numSheets = this.workbook.getNumberOfSheets();
	
	        for(int i = 0; i < numSheets; i++) {
	            sheet = ((Workbook) this.workbook).getSheetAt(i);
	            
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
        	
        	if(sheet != null) {
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
    private Map rowToCSV(Row row, int rownum) throws Exception {
        Cell cell = null;
        int lastCellNum = 0;
        Map<String, Object> csvLine = new LinkedHashMap<String, Object>();
        String emptyTest = "";
        
        if(row != null) {
            lastCellNum = row.getLastCellNum();
            
            for(int i = 0; i <= lastCellNum; i++) {
                cell = row.getCell(i);
                String ch = StringHelper.nextAlphabet(i);
                String cellName = ch + rownum;
                
                if(cell == null) {
                    csvLine.put(cellName , "");
                } else {
                	CellType cellValueType = cell.getCellType();
                	
                	if(this.evaluator != null) {
	                	CellValue cvalue = this.evaluator.evaluate(cell);
	                    cellValueType = cvalue.getCellType();
	                    
	                    log.debug("SS Reader > evaluator's cellValueType = " + cellValueType);
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
//                    	log.debug(cellName + " - 2.getFormulaValue data = " + cellValue);
                    } else if(cellValueType == CellType.NUMERIC) {
                    	if(ExcelUtil.isADateFormat(cell)) { // 날짜 포맷인지 체크
                    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    		String cellValue = StringHelper.null2void(sdf.format(cell.getDateCellValue()));
                    		
//                    		log.debug(cellName + " - 2.getDateCellValue date = " + cellValue);
                    		
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
                			
//                			cell.setCellType(CellType.NUMERIC);
                			
//                			log.debug(cellName + " - 2.1.getNumericCellValue = " + cellValue);
                			
                    		csvLine.put(cellName, cellValue);
                    		
                    		emptyTest += cellValue;
                    	}
                    } else {
                    	String cellValue = this.formatter.formatCellValue(cell);
                    	csvLine.put(cellName, cellValue);
                    	
                    	emptyTest += cellValue;
                    }
                }
            }

            if(lastCellNum > this.maxRowWidth) {
                this.maxRowWidth = lastCellNum;
            }
            
            // 열 속성정보를 추가함(2020.06.27)
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
     */
    class ExcelFilenameFilter implements FilenameFilter {
        public boolean accept(File file, String name) {
            return (name.endsWith(".xls"));
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
                char ch = 'A';
                
                for(int j = 0; j < this.maxRowWidth; j++) {
                    if(line.size() > j) {
                    	String cellName = StringHelper.nextAlphabet(j)+number; // Character.toString(ch) + (number);
                    	csvLineElement = StringHelper.null2void(line.get(cellName)).replace("€", ",");
                        
                        if(csvLineElement != null) {
                            buffer.append(this.escapeEmbeddedCharacters(csvLineElement));
                        }
                    }
                    if(j < (this.maxRowWidth - 1)) {
                        buffer.append(this.separator);
                    }
                    
                    ch++;
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

	@Override
	public void view(List<List<Map<String, Object>>> list, Object workbook) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
