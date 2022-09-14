package kr.yni.frame.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Element;

import kr.yni.frame.exception.FrameException;
import kr.yni.frame.mapper.ParamReader;
import kr.yni.frame.mapper.element.Column;
import kr.yni.frame.mapper.element.Jco;
import kr.yni.frame.mapper.element.Parameter;
import kr.yni.frame.mapper.element.Table;
import kr.yni.frame.reader.FileReader;

public class JcoXmlHelper {
	
	private static final Log log = LogFactory.getLog(JcoXmlHelper.class);
	
	public final static String XML_TEMP_PATH = "TEMP_PATH";
	public final static String XML_TEMP_NAME = "TEMP_NAME";
	public final static String XML_JCO_ID = "JCO_ID";
	public final static String XML_FILE_NAME = "FILE_NAME";
	
	public final static String XML_ELEMENT = "ELEMENT";
	public final static String XML_CHILD = "CHILD";
	public final static String XML_PARENT = "PARENT";
	public final static String XML_UPDATE_FLAG = "UPDATE_FLAG";
	public final static String XML_DELETE_FLAG = "DELETE_FLAG";
	
	/**
	 * <p>
     * 임시로 저장된 XML파일을 읽어 실제 저장될 위치에 파일 생성<br>
     * Map map = new LinkedHashMap();<br>
     * <br>
     * map.put(FileUitl.XML_TEMP_PATH, tempPath);<br>
     * map.put(FileUitl.XML_TEMP_NAME, tempFileName);<br>
     * map.put(FileUitl.XML_JCO_ID, jcoId);<br>
     * map.put(FileUitl.FILE_NAME, fileName);<br>
     * </p>
     * 
     * @param map 임시파일 위치와 생성할 파일명
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static void createTempXml(Map map) throws Exception {
    	String tempPath = StringHelper.null2void(map.get(XML_TEMP_PATH));
    	String tepmFileName = StringHelper.null2void(map.get(XML_TEMP_NAME));
    	String jcoId = StringHelper.null2string(map.get(XML_JCO_ID), "TEMP");
    	String fileName = StringHelper.null2string(map.get("FILE_NAME"), DateHelper.getCurrentDateTimeMilliSecondAsString()+"_JCO.xml");
    	
    	createTempXml(tempPath, tepmFileName, fileName, jcoId);
    }
    
    /**
     * 임시로 저장된 XML파일을 읽어 실제 저장될 위치에 파일 생성
     * 
     * @param tempPath 임시 저장 경로
     * @param tepmFileName 임시 저장 파일명
     * @param fileName 생성할 파일명
     * @throws Exception
     */
	public static void createTempXml(String tempPath, String tepmFileName, String fileName) throws Exception {
    	createTempXml(tempPath, tepmFileName, fileName, null);
    }
    
    /**
     * 임시로 저장된 XML파일을 읽어 실제 저장될 위치에 파일 생성
     * 
     * @param tempPath 임시 저장 경로
     * @param tepmFileName 임시 저장 파일명
     * @param fileName 생성할 파일명
     * @param jcoId JCO id
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static void createTempXml(String tempPath, String tepmFileName, String fileName, String jcoId) throws Exception {
    	File file = new File(tempPath + "/" + tepmFileName);
 		
 		if(file.isFile()){
 			FileReader reader = FileUtil.getFileReader(tepmFileName);
 			List fileContent = reader.read(FileUtil.getFile(tempPath, tepmFileName), -1, jcoId);
 			
 			if(log.isDebugEnabled()) log.debug("Element count = " + fileContent.size());
 			
 			Element jcoElement = (Element) fileContent.get(0);
 			if(jcoId != null && !jcoId.isEmpty()) {
 				jcoElement.setAttribute("id", jcoId);
 			}

 			FileUtil.createXmlFile(fileContent, tempPath, fileName);
 		} else {
 			throw new FrameException("File not exists(path=" + tempPath + "/" + tepmFileName + ")");
 		}
    }
    
    /**
     * Map에 등록된 정보로 XML를 수정한 후 파일로 저장한다. 
     * 
     * @param path 경로 또는 .properties 속성명
     * @param fileName 파일명
     * @param map 등록 또는 삭제할 대상 Element 정보
     * @throws Exception
     */
 	@SuppressWarnings("rawtypes")
	public static void createJcoXml(String path, String fileName, Map map) throws Exception{
 		String jcoId = StringHelper.null2string(map.get(XML_JCO_ID), "empty");;
 		String flag = StringHelper.null2void(map.get(XML_DELETE_FLAG));
 		
 		FileReader reader = FileUtil.getFileReader(fileName);
 		List fileContent = reader.read(FileUtil.getFile(path, fileName), -1, jcoId);
 		
 		if(log.isDebugEnabled()) log.debug("Element count = " + fileContent.size());
 		
 		if(flag.equals("Y")) {
			removeElement(fileContent, null, map);
		} else {
			setElement(fileContent, null, map);
		}
 		
		FileUtil.createXmlFile(fileContent, path, fileName);
 	}
 	
 	/**
	 * 지정된 XML파일의 정보를 <code>List</code>객체로 리턴
	 * 
	 * @param path 파일의 절대경로
	 * @param fileName 파일명
	 * @return xml read list data를 parent - child 관계의 list로 변환한 data list
	 * @throws Exception
	 */
 	@SuppressWarnings("rawtypes")
	public static List getJcoList(String path, String fileName) throws Exception {
 		File file = new File(path + "/" + fileName);
 		
 		if(file != null && file.isFile()) {
 			FileReader reader = FileUtil.getFileReader(fileName);
 	 		List fileContent = reader.read(FileUtil.getFile(path, fileName), -1);
 	 		
 			if(log.isDebugEnabled()) log.debug("Element count = " + fileContent.size());
 			
			List resultList = new ArrayList();
			resultList = getElement(fileContent, null, resultList);
			
			return resultList;
 		} else {
 			throw new FrameException("File not exists(path=" + path + "/" + fileName + ")");
 		}
 	}
 	
 	/**
 	 * JDOM 요소를 Tree 구조로 변경한 <code>List</code> 리턴
 	 * 
 	 * @param elList XML 파일을 read 한 list data
 	 * @param parentId parent id(초기값 null)
 	 * @param resultList 리턴할 List
 	 * @return Tree 구조를 갖는 List 객체
 	 */
 	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List getElement(List elList, String parentId, List resultList) throws Exception {
 		Map treeMap = null;
 		
		for (int i = 0; i < elList.size(); i++) {
			treeMap = new LinkedHashMap();
			
			Element root = (Element) elList.get(i);
			List childList = root.getChildren();
			
			// 자신의 상위 element 구하기
			String parentCnt = parentId == null ? "" : parentId.replace(root.getParentElement().getName() + "_", "");
			String childName = root.getName() + "_" + (parentCnt + "" + i);
			
			// 각 Element 상위 명칭을 재 정의한다.(중복된 명칭없이 Tree를 구성한다.)
			treeMap.put(XML_ELEMENT, root.getName());
			treeMap.put(XML_CHILD, childName);
			treeMap.put(XML_PARENT, parentId);
			
			// 각 Element 속성을 <code>Map</code>에 등록한다.
			List attrList = root.getAttributes();
			
			for(int j=0; j < attrList.size(); j++){
				Attribute attr = (Attribute)attrList.get(j);
				treeMap.put(attr.getName().toUpperCase(), attr.getValue());
			}
			
			resultList.add(treeMap);
			
			// 자식 Element 가 끝나는 시점까지 재귀시킨다.
			if (childList.size() > 0) {
				getElement(childList,  childName, resultList);
			}
		}

		return resultList;
	}
 	
 	/**
 	 * 원래의 XML표준에 맞게 Element의 명을 변경하거나 추가한 List 리턴
 	 * 
 	 * @param elList XML 파일을 read 한 list data
 	 * @param parentId Parent id(초기값 null)
 	 * @param paramMap 추가할 Elements
 	 */
 	@SuppressWarnings("rawtypes")
	public static List setElement(List elList, String parentId, Map paramMap) throws Exception {
 		String flag = StringHelper.null2void(paramMap.get(XML_UPDATE_FLAG));
 		String element = StringHelper.null2void(paramMap.get(XML_ELEMENT));
 		
		for (int i = 0; i < elList.size(); i++) {
			Element root = (Element) elList.get(i);
			List childList = root.getChildren();
			
			String parentCnt = parentId == null ? "" : parentId.replace(root.getParentElement().getName() + "_", "");
			String childName = root.getName() + "_" + (parentCnt + "" + i);
			
			if(StringHelper.null2void(paramMap.get(XML_CHILD)).equals(childName)){
				Element newElement = null;
				
				if(flag.equals("Y")){
					newElement = root;
				} else {
					newElement = new Element(element);
				}
				
				Iterator iterator = paramMap.entrySet().iterator();
				
                while (iterator.hasNext()) {
                	Map.Entry entry = (Map.Entry) iterator.next();
                	
                	String key = entry.getKey().toString();
                	String value = StringHelper.null2void(entry.getValue());
                	
                	if(!value.isEmpty()){
	                	if(entry.getKey().toString().startsWith("JCO_")){
	                		key = key.replace("JCO_", "").toLowerCase();
	                		newElement.setAttribute(key, value);
	                	}
                	}
                }
                
                if(!flag.equals("Y")){
                	root.addContent(newElement);
                }
 			} else {
 				if (childList.size() > 0) {
 					setElement(childList, childName, paramMap);
 				}
 			}
		}
		
		return elList;
	}
 	
 	/**
 	 * 원래의 XML표준에 맞게 Element 삭제한 List 리턴
 	 * 
 	 * @param elList xml 파일을 read 한 list data
 	 * @param parentId parent id(초기값 null)
 	 * @param paramMap 삭제한 Elements
 	 * @return
 	 */
 	@SuppressWarnings("rawtypes")
	public static void removeElement(List elList, String parentId, Map paramMap) throws Exception {
		for (int i = 0; i < elList.size(); i++) {
			Element root = (Element) elList.get(i);
			List childList = root.getChildren();
			
			String parentCnt = parentId == null ? "" : parentId.replace(root.getParentElement().getName() + "_", "");
			String childName = root.getName() + "_" + (parentCnt + "" + i);
			
			if(StringHelper.null2void(paramMap.get(XML_CHILD)).equals(childName)){
				Element parentElement = (Element) root.getParentElement();
				parentElement.removeContent(root);
 			} else {
 				if (childList.size() > 0) {
 					removeElement(childList, root.getName() + "_" + (parentCnt + "" + i), paramMap);
 				}
 			}
			
		}
	}
 	
 	/**
 	 * 테이블 정보에서 지정된 파라메터의 컬럼 정보를 찾아 리턴
 	 * @param jcoID JCO Map XML 식별자
 	 * @param paramName 검색할 Paramaeter Name
 	 * @param colName 검색할 컬럼 Name
 	 * @return 컬럼정보(없으면 null 리턴)
 	 * @throws Exception
 	 */
 	public static Column getColumnForTableParameter(String jcoID, String paramName, String colName) throws Exception {
 		// JCO Map XML에서 표시할 메시지 정보 조회
		Jco jco = ParamReader.getJcoParameter(jcoID);
		
		if(jco != null && jco.getFunctionCount() > 0) {
	        Table tbl = jco.getFunction(0).getTable();
	        
	        for (int i = 0; i < tbl.getParameterCount(); i++) {
	        	Parameter param = tbl.getParams(i);
	            String pname = param.getName();
	            
	        	if(pname.equals(paramName)) {
	            	for (int j = 0; j < param.getColumnCount(); j++) {
	                    Column col = param.getColumn(j);
	                    
	                    String name = col.getName();
	                    
	                    if(name.equals(colName)) {
	                    	return col;
	                    }
	            	}
	            	if(log.isDebugEnabled()) log.debug("There is not found Column.");
	            }
	        }
	        if(log.isDebugEnabled()) log.debug("There is not found Parameter.");
		} else {
			if(log.isDebugEnabled()) log.debug("There is not found Function Table.");
		}
		
        return null;
 	}
}
