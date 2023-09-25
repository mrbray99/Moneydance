/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of the author may not used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.moneydance.modules.features.reportwriter2;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.view.tables.DataDataRow;
import com.moneydance.modules.features.reportwriter2.view.tables.DataRow;
import com.moneydance.modules.features.reportwriter2.view.tables.ReportDataRow;
import com.moneydance.modules.features.reportwriter2.view.tables.ReportRow;
import com.moneydance.modules.features.reportwriter2.view.tables.SelectionRow;
import com.moneydance.modules.features.reportwriter2.view.tables.TemplateRow;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Parameters implements Serializable{
	private AccountBook curAcctBook;
	private File curFolder;
	private String fileName;
	private NewParameters newParams;
	private Boolean introScreen;
	private String dataDirectory;
	private String outputDirectory;
	private String reportDirectory;
	private List<TemplateRow> templateList;
	private List<SelectionRow> selectionList;
	private List<DataRow> dataList;
	private List<ReportRow> reportList;
	private static Parameters thisObj;
	public Parameters() {
		curAcctBook = Main.context.getCurrentAccountBook();
		curFolder = curAcctBook.getRootFolder();
		thisObj = this;
		/*
		 * Determine if the new file exists
		 */
		fileName = curFolder.getAbsolutePath()+"\\"+Constants.DEFAULTPARAMETERFILE+Constants.PARMEXTENSION;
		try {
			JsonReader reader = new JsonReader(new FileReader(fileName));
			newParams = new Gson().fromJson(reader,NewParameters.class);
			reader.close();
			Main.rwDebugInst.debugThread("Parameters", "Parameters", MRBDebug.SUMMARY, "File found "+fileName);

		}
		catch (JsonParseException e) {
			Main.rwDebugInst.debugThread("Parameters", "Parameters", MRBDebug.DETAILED, "Parse Exception "+e.getMessage());
		}
		catch (IOException e){
			/*
			 * file does not exist, initialize fields
			 */
			newParams = new  NewParameters();
			newParams.setDataDirectory(Constants.NODIRECTORY);
			newParams.setReportDirectory(Constants.NODIRECTORY);
			newParams.setOutputDirectory(Constants.NODIRECTORY);
			newParams.setIntroScreen(true);
			/*
			 * create the file
			 */
			try {
			   FileWriter writer = new FileWriter(fileName);
			   String jsonString = new Gson().toJson(newParams);
			   writer.write(jsonString);
			   writer.close();
			   Main.rwDebugInst.debugThread("Parameters", "Parameters", MRBDebug.SUMMARY, "File created "+fileName);

             }
			 catch (IOException i) {
					   i.printStackTrace();
             }
		}
		dataDirectory = newParams.getDataDirectory();
		outputDirectory = newParams.getOutputDirectory();
		reportDirectory = newParams.getReportDirectory();
		introScreen = newParams.getIntroScreen();
		templateList = new ArrayList<>();
		dataList = new ArrayList<>();
		selectionList = new ArrayList<>();
		reportList = new ArrayList<>();
		setDataTemplates();
		setReportTemplates();

	} 
	public static Parameters getInstance() {
		return thisObj;
	}
	public void setDataTemplates() {
		Main.rwDebugInst.debugThread("Parameters", "setDataTemplates", MRBDebug.SUMMARY, "Data Directory "+dataDirectory);
		if (dataDirectory == null || dataDirectory.equals(Constants.NODIRECTORY))
			return;
		dataList.clear();
		reportList.clear();
		selectionList.clear();
		File folder = new File(dataDirectory);
		File [] files = folder.listFiles();
		if (files != null && files.length > 0) {
			Arrays.sort(files);
			for (int i=0;i<files.length;i++) {
				if (files[i].isFile()) {
					String fileName = files[i].getName();
					Main.rwDebugInst.debugThread("Parameters", "setDataTemplates", MRBDebug.SUMMARY, "Processing "+fileName);
					if (fileName.toLowerCase().endsWith(Constants.DATAEXTENSION)) {
							DataRow newRow = new DataRow();
							newRow.setName(fileName.substring(0,fileName.lastIndexOf(".")));
							newRow.setFileName(files[i].getAbsolutePath());
							DataDataRow dataRow = new DataDataRow();
							dataRow.loadRow(newRow.getName(), this);
							BasicFileAttributes atts;
							try {
								atts = Files.readAttributes(files[i].toPath(), BasicFileAttributes.class);
								newRow.setLastModified(Main.cdate.format(new Date(atts.lastModifiedTime().to(TimeUnit.MILLISECONDS))));
								newRow.setCreated(Main.cdate.format(new Date(atts.creationTime().to(TimeUnit.MILLISECONDS))));
								newRow.setLastUsed(Main.cdate.format(new Date(atts.lastAccessTime().to(TimeUnit.MILLISECONDS))));
							}
							catch (IOException e) {
								newRow.setLastModified("");
								newRow.setCreated("");
								newRow.setLastUsed("");
							}
							dataList.add(newRow);
					}
					if (fileName.toLowerCase().endsWith(Constants.SELEXTENSION)) {
						SelectionRow newRow = new SelectionRow();
						newRow.setName(fileName.substring(0,fileName.lastIndexOf(".")));
						newRow.setFileName(files[i].getAbsolutePath());
						BasicFileAttributes atts;
						try {
							atts = Files.readAttributes(files[i].toPath(), BasicFileAttributes.class);
							newRow.setLastModified(Main.cdate.format(new Date(atts.lastModifiedTime().to(TimeUnit.MILLISECONDS))));
							newRow.setCreated(Main.cdate.format(new Date(atts.creationTime().to(TimeUnit.MILLISECONDS))));
							newRow.setLastUsed(Main.cdate.format(new Date(atts.lastAccessTime().to(TimeUnit.MILLISECONDS))));
						}
						catch (IOException e) {
							newRow.setLastModified("");
							newRow.setCreated("");
							newRow.setLastUsed("");
						}
						selectionList.add(newRow);
					}
					if (fileName.toLowerCase().endsWith(Constants.REPORTEXTENSION)) {
						ReportRow newRow = new ReportRow();
						newRow.setName(fileName.substring(0,fileName.lastIndexOf(".")));
						newRow.setFileName(files[i].getAbsolutePath());
						ReportDataRow dataRow = new ReportDataRow();
						if (!dataRow.loadRow(newRow.getName(), this)) {
							Main.rwDebugInst.debugThread("Parameters", "setReportTemplates", MRBDebug.SUMMARY, "Processing "+fileName);
							Alert alert = new Alert(AlertType.ERROR,"Could not load data for data row "+newRow.getName());
							alert.showAndWait();
							continue;
						}
						newRow.setTemplate(dataRow.getTemplate());
						newRow.setSelection(dataRow.getSelection());
						newRow.setData(dataRow.getDataParms());
						newRow.setLastUsed(Main.cdate.format(new Date(files[i].lastModified())));
						reportList.add(newRow);
					}
				}
			}
		}
		else
			dataDirectory = Constants.NODIRECTORY;
	}
	public void setReportTemplates() {
		Main.rwDebugInst.debugThread("Parameters", "setReportTemplates", MRBDebug.SUMMARY, "Report Directory "+reportDirectory);
		if (reportDirectory == null || reportDirectory.equals(Constants.NODIRECTORY))
			return;
		templateList.clear();
		File folder = new File(reportDirectory);
		File [] files = folder.listFiles();
		if (files != null && files.length>0) {
			for (int i=0;i<files.length;i++) {
				if (files[i].isFile()) {
					String fileName = files[i].getName();
					Main.rwDebugInst.debugThread("Parameters", "setReportTemplates", MRBDebug.SUMMARY, "Processing "+fileName);
					if (fileName.toLowerCase().endsWith(Constants.TEMPLATEEXTENSION)) {
							TemplateRow newRow = new TemplateRow(this);
							newRow.setName(fileName.substring(0,fileName.lastIndexOf(".")));
							newRow.setFileName(files[i].getAbsolutePath());
							BasicFileAttributes atts;
							try {
								atts = Files.readAttributes(files[i].toPath(), BasicFileAttributes.class);
								newRow.setLastModified(Main.cdate.format(new Date(atts.lastModifiedTime().to(TimeUnit.MILLISECONDS))));
								newRow.setCreated(Main.cdate.format(new Date(atts.creationTime().to(TimeUnit.MILLISECONDS))));
								newRow.setLastUsed(Main.cdate.format(new Date(atts.lastAccessTime().to(TimeUnit.MILLISECONDS))));
							}
							catch (IOException e) {
								newRow.setLastModified("");
								newRow.setCreated("");
								newRow.setLastUsed("");
							}
							templateList.add(newRow);
					}
				}
			}
		}
		else
			reportDirectory=Constants.NODIRECTORY;
	}
	
	public String getDataDirectory() {
		return dataDirectory;
	}

	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	public String getReportDirectory() {
		return reportDirectory;
	}

	public void setReportDirectory(String reportDirectory) {
		this.reportDirectory = reportDirectory;
	}
	public List<TemplateRow> getTemplateList() {
		return templateList;
	}
	public void setTemplateList(List<TemplateRow> templateList) {
		this.templateList = templateList;
	}

	public List<SelectionRow> getSelectionList() {
		return selectionList;
	}
	
	
	public String getOutputDirectory() {
		return outputDirectory;
	}
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	public void addSelectionRow(SelectionRow row) {
		for (SelectionRow tempRow : selectionList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastModified(row.getLastModified());
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				return;
			}
		}
		selectionList.add(row);
	}

	public void updateSelectionRow(SelectionRow row) {
		for (SelectionRow tempRow : selectionList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastModified(row.getLastModified());
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				return;
			}
		}
	}
	public void removeSelectionRow(SelectionRow row) {
		selectionList.remove(row);
	}
	public void setSelectionList(List<SelectionRow> selectionList) {
		this.selectionList = selectionList;
	}

	public List<DataRow> getDataList() {
		return dataList;
	}

	public void setDataList(List<DataRow> dataList) {
		this.dataList = dataList;
	}
	public void addDataRow(DataRow row) {
		for (DataRow tempRow : dataList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastModified(row.getLastModified());
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				return;
			}
		}
		dataList.add(row);
	}

	public void updateDataRow(DataRow row) {
		for (DataRow tempRow : dataList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastModified(row.getLastModified());
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				return;
			}
		}
	}
	public void removeDataRow(DataRow row) {
		dataList.remove(row);
	}
	public void addTemplateRow(TemplateRow row) {
		for (TemplateRow tempRow : templateList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastModified(row.getLastModified());
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				return;
			}
		}
		templateList.add(row);
	}

	public void updateTemplateRow(TemplateRow row) {
		for (TemplateRow tempRow : templateList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastModified(row.getLastModified());
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				return;
			}
		}
	}
	public void removeTemplateRow(TemplateRow row) {
		templateList.remove(row);
	}
	public List<ReportRow> getReportList() {
		return reportList;
	}

	public void setReportList(List<ReportRow> reportList) {
		this.reportList = reportList;
	}

	public void addReportRow(ReportRow row) {
		for (ReportRow tempRow : reportList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				tempRow.setTemplate(row.getTemplate());
				tempRow.setSelection(row.getSelection());
				tempRow.setData(row.getData());
				tempRow.setType(row.getType());
				return;
			}
		}
		reportList.add(row);
	}

	public void updateReportRow(ReportRow row) {
		for (ReportRow tempRow : reportList) {
			if (tempRow.getName().equalsIgnoreCase(row.getName())) {
				tempRow.setLastUsed(row.getLastUsed());
				tempRow.setCreated(row.getCreated());
				tempRow.setFileName(row.getFileName());
				tempRow.setTemplate(row.getTemplate());
				tempRow.setSelection(row.getSelection());
				tempRow.setData(row.getData());
				tempRow.setType(row.getType());
				return;
			}
		}
	}
	public void removeReportRow(ReportRow row) {
		reportList.remove(row);
	}
	/*
	 * Save
	 */
	public void save() {

		/*
		 * create the file
		 */
		newParams.setDataDirectory(dataDirectory);
		newParams.setReportDirectory(reportDirectory);
		newParams.setOutputDirectory(outputDirectory);
		newParams.setIntroScreen(introScreen);

		try {
			   FileWriter writer2 = new FileWriter(fileName);
			   String jsonString = new Gson().toJson(newParams);
			   Main.rwDebugInst.debug("Parameters","save",MRBDebug.SUMMARY,"Json string "+jsonString);
			   writer2.write(jsonString);
			   writer2.close();			  
          } catch (IOException i) {
					   i.printStackTrace();
	
          }
	}
	public boolean checkDataGroup(String groupName) {
		for (ReportRow row : reportList) {
			if (row.getData()!=null && row.getData().equals(groupName))
				return true;
		}
		return false;
	}
	public boolean checkSelectionGroup(String groupName) {
		for (ReportRow row : reportList) {
			if (row.getSelection() !=null && row.getSelection().equals(groupName))
				return true;
		}
		return false;
	}
	public boolean checkTemplate(String templateName) {
		for (ReportRow row : reportList) {
			if (row.getTemplate() !=null && row.getTemplate().equals(templateName))
				return true;
		}
		return false;
	}	public Boolean getIntroScreen() {
		if (introScreen == null)
			introScreen=true;
		return introScreen;
	}
	public void setIntroScreen(Boolean introScreen) {
		this.introScreen = introScreen;
	}
	

}
