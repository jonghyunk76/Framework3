package kr.yni.frame.prop.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;

import kr.yni.frame.prop.FileService;
import kr.yni.frame.util.ReloadPropertiesUtil;

public class PropertieFileServiceImpl implements FileService {

	private ReloadPropertiesUtil propertiesUtil;

	@Required
	public void setPropertiesUtil(ReloadPropertiesUtil propertiesUtil) {
		this.propertiesUtil = propertiesUtil;
	}

	@Override
	public void onDirectoryCreate(File file) throws Exception {
	}

	@Override
	public void onDirectoryChange(File file) throws Exception {
	}

	@Override
	public void onDirectoryDelete(File file) throws Exception {
	}

	@Override
	public void onFileCreate(File file) throws Exception {
		propertiesUtil.reload();
	}

	@Override
	public void onFileChange(File file) throws Exception {
		propertiesUtil.reload();
	}

	@Override
	public void onFileDelete(File file) throws Exception {
		propertiesUtil.reload();
	}

}