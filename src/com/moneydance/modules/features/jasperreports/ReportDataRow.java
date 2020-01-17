package com.moneydance.modules.features.jasperreports;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class ReportDataRow {
	String name;
	String template;
	String selection;
	String dataParms;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getDataParms() {
		return dataParms;
	}
	public void setDataParms(String dataParms) {
		this.dataParms = dataParms;
	}
	public boolean loadRow(String name,Parameters paramsp) {
		String dir = paramsp.getReportDirectory();
		String fileName = dir+"/"+name+Constants.REPORTEXTENSION;
		ReportDataRow row = new ReportDataRow();
		try {
			JsonReader reader = new JsonReader(new FileReader(fileName));
			row = new Gson().fromJson(reader,ReportDataRow.class);
			reader.close();
			setName(row.getName());
			setSelection(row.getSelection());
			setTemplate(row.getTemplate());
			setDataParms(row.getDataParms());
			Main.debugInst.debugThread("ReportDataRow", "loadRow", MRBDebug.DETAILED, "Row loaded "+name);
		}
		catch (JsonParseException e) {
			Main.debugInst.debugThread("ReportDataRow", "loadRow", MRBDebug.DETAILED, "Parse Exception "+e.getMessage());
			return false;
		}
		catch (IOException e){
			return false;
		}
		return true;
	}
	
	public void saveRow(Parameters paramsp) {
		String dir = paramsp.getReportDirectory();
		String fileName = dir+"/"+getName()+Constants.REPORTEXTENSION;
		try {
			   FileWriter writer = new FileWriter(fileName);
			   String jsonString = new Gson().toJson(this);
			   writer.write(jsonString);
			   writer.close();	
			   Main.debugInst.debug("ReportDataRow", "saveRow", MRBDebug.DETAILED, "Row Saved "+name);
          }
			 catch (IOException i) {
				 Main.debugInst.debug("ReportDataRow", "saveRow", MRBDebug.DETAILED, "IO Exception "+i.getMessage());
					   i.printStackTrace();
          }
	}

}
