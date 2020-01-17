package com.moneydance.modules.features.jasperreports;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBDebug;

public class SelectionRow {
	private String name;
	private String fileName;
	private String lastModified;
	private String created;
	private String lastUsed;
	public SelectionRow() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getLastUsed() {
		return lastUsed;
	}
	public void setLastUsed(String lastUsed) {
		this.lastUsed = lastUsed;
	}
	public void delete() {
		Main.debugInst.debugThread("SelectionRow", "delete", MRBDebug.SUMMARY, "Delete "+fileName);
		File file = new File(fileName);
		if (file.delete())
			Main.debugInst.debugThread("SelectionRow", "delete", MRBDebug.SUMMARY, "Deleted "+fileName);
		else
			Main.debugInst.debugThread("SelectionRow", "delete", MRBDebug.SUMMARY, "Delete failed "+fileName);

	}
	public static List<String> getSelections(Parameters params){
		String dataDirectory = params.getDataDirectory();
		List<String> list = new ArrayList<>();
		if (dataDirectory == null || dataDirectory == Constants.NODIRECTORY)
			return null;
		
		list.clear();
		File folder = new File(dataDirectory);
		File [] files = folder.listFiles();
		for (int i=0;i<files.length;i++) {
			if (files[i].isFile()) {
				String fileName = files[i].getName();
				Main.debugInst.debugThread("SelectionRow", "getSelections", MRBDebug.SUMMARY, "Processing "+fileName);
				if (fileName.toLowerCase().endsWith(Constants.SELEXTENSION)) {
					list.add(fileName.substring(0,fileName.lastIndexOf(".")));
				}
			}
		}
		return list;
	}
	

}
