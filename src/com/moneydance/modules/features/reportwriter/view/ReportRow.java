package com.moneydance.modules.features.reportwriter.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.Parameters;

public class ReportRow {
	private String name;
	private String fileName;
	private String template;
	private String selection; 
	private String data;
	private String created;
	private String lastUsed;
	private Integer type;
	public ReportRow() {
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
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getSelection() {
		return selection;
	}
	public void setSelection(String selection) {
		this.selection = selection;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
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
	public Integer getType() {
		return type;
	}
	public void setType (Integer type) {
		this.type = type; 
	}

	public void delete() {
		Main.rwDebugInst.debugThread("ReportRow", "delete", MRBDebug.SUMMARY, "Delete "+fileName);
		File file = new File(fileName);
		if (file.delete())
			Main.rwDebugInst.debugThread("SelectionRow", "delete", MRBDebug.SUMMARY, "Deleted "+fileName);
		else
			Main.rwDebugInst.debugThread("SelectionRow", "delete", MRBDebug.SUMMARY, "Delete failed "+fileName);

	}
	public static List<String> getSelections(Parameters params){
		String reportDirectory = params.getReportDirectory();
		List<String> list = new ArrayList<>();
		if (reportDirectory == null || reportDirectory == Constants.NODIRECTORY)
			return null;
		
		list.clear();
		File folder = new File(reportDirectory);
		File [] files = folder.listFiles();
		for (int i=0;i<files.length;i++) {
			if (files[i].isFile()) {
				String fileName = files[i].getName();
				Main.rwDebugInst.debugThread("SelectionRow", "getSelections", MRBDebug.SUMMARY, "Processing "+fileName);
				if (fileName.toLowerCase().endsWith(Constants.REPORTEXTENSION)) {
					list.add(fileName.substring(0,fileName.lastIndexOf(".")));
				}
			}
		}
		return list;
	}
}
