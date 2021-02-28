package com.moneydance.modules.features.reportwriter.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;

import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Parameters;

public class TemplateRow {
	private String name;
	private String fileName;
	private String lastVerified;
	private Parameters params;
	public TemplateRow(Parameters paramsp) {
		params = paramsp;
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
	public String getLastVerified() {
		return lastVerified;
	}
	public void setLastVerified(String lastVerified) {
		this.lastVerified = lastVerified;
	}
	public void touchFile() {
		String dir = params.getReportDirectory();
		String fullFileName = dir+"/"+name+Constants.TEMPLATEEXTENSION;
		Path touchFile = Paths.get(fullFileName);
		try {
			Files.setAttribute(touchFile, "basic:lastAccessTime", FileTime.fromMillis(new Date().getTime()));
		}
		catch (IOException e) {}

	}

}
