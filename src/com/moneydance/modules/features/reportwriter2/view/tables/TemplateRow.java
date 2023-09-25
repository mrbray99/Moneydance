package com.moneydance.modules.features.reportwriter2.view.tables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Parameters;


public class TemplateRow {
	private String name;
	private String fileName;
	private String lastVerified;
	private String lastModified;
	private String created;
	private String lastUsed;
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
	public void touchFile() {
		String dir = params.getReportDirectory();
		String fullFileName = dir+name+Constants.TEMPLATEEXTENSION;
		Path touchFile = Paths.get(fullFileName);
		try {
			Files.setAttribute(touchFile, "basic:lastAccessTime", FileTime.fromMillis(new Date().getTime()));
		}
		catch (IOException e) {}

	}

}
