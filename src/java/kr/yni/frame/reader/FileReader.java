package kr.yni.frame.reader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 파일에 대한 기능을 추적상으로 정의해 놓은 클래스
 * 
 * @author YNI-maker
 *
 */
@SuppressWarnings("rawtypes")
public abstract class FileReader {
	
	/**
     * 인스턴스된 XSSFWorkbook을 리턴한다. 
     * 
     * @return
     * @throws Exception
     */
    public abstract Workbook getWorkbook();
    
    /**
     * XSSFWorkbook이 null이라면 생성한 후 리턴한다. 
     * 
     * @param file file객체
     * @return
     * @throws Exception
     */
    public abstract Workbook getWorkbook(File file) throws Exception;
	/**
	 * <p>
	 * 서버로 업로드된 파일을 파싱한 후 List<Map>으로 반환한다.
	 * </p>
	 * 
	 * @param path 경로 또는 properties 속성명
	 * @param name  파일명
	 * @param index 인덱스 번호
	 * @return data list와 처리결과를 리턴
	 */
	public abstract List read(File file, int index)  throws Exception;
	
	/**
	 * <p>
	 * 서버로 업로드된 파일을 파싱한 후 List<Map>으로 반환한다.
	 * </p>
	 * 
	 * @param path 경로 또는 properties 속성명
	 * @param name  파일명
	 * @param index 인덱스 번호
	 * @param id 시트명 또는 식별ID
	 * @return data list와 처리결과를 리턴
	 */
	public abstract List read(File file, int index, String id) throws Exception;
	
	/**
	 * <p>
	 * 서버로 업로드된 파일을 파싱한 후 List<Map>으로 반환한다.
	 * </p>
	 * 
	 * @param path 경로 또는 properties 속성명
	 * @param name  파일명
	 * @param index 인덱스 번호
	 * @param id 시트명 또는 식별ID
	 * @param formula 계산식 적용여부
	 * @return data list와 처리결과를 리턴
	 */
	public abstract List read(File file, int index, String id, boolean formula) throws Exception;
	
	/**
     * <code>List</code>에 담긴 데이터를 Excel파일로 변환한다.<br> 
     *  컬럼명은 list-Map에 등록된 key값으로 자동 설정된다.
     * 
     * @param pVO 
     * @param file Excel 파일 저장경로
     * @throws IOException
     */
    public abstract void write(List list, File file) throws Exception;
    
    /**
     * <code>List</code>에 담긴 데이터를 Excel파일로 변환하고 파일을 서버에 저장한다.<br>
     * columns에 해당하는 컬럼만 엑셀에 표시한다.
     * 
     * @param pVO 
     * @param file Excel 파일 저장경로
     * @param columns 컬럼명 배열(엑셀에 등록하고자 하는 컬럼만 배열에 포함시킬 것)
     * @throws IOException
     */
    public abstract void write(List list, File file, String[] columns) throws Exception;
    
    /**
     * <code>List</code>에 담긴 데이터를 Excel 파일로 변환한다.<br>
     * ExcelView에서 호출된다.
     * 
     * @param list : type = [[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string}],
     *                             ,[{col1=value, col2=value},{col1=value, col2=value},{headers=[head1, head2], file=string, sheet=string}]]
     * @param workbook : excel object
     * @throws Exception
     */
	public abstract void view(List<List<Map<String, Object>>> list, Object workbook) throws Exception;
	
}
