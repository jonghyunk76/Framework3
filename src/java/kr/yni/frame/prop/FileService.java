package kr.yni.frame.prop;

import java.io.File;

import kr.yni.frame.util.ReloadPropertiesUtil;

public interface FileService {
	
	public void setPropertiesUtil(ReloadPropertiesUtil propertiesUtil);
	
	public void onDirectoryCreate(File file) throws Exception;
	
	public void onDirectoryChange(File file) throws Exception;
	
	public void onDirectoryDelete(File file) throws Exception;
	
	public void onFileCreate(File file) throws Exception;
	
	public void onFileChange(File file) throws Exception;
	
	public void onFileDelete(File file) throws Exception;
	
}
