package com.moneydance.modules.features.jasperreports;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedMap;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class DataDataRow {
	private String name;
	private String selectionGroup;
	private SortedMap<String,DataParameter> parameters;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSelectionGroup() {
		return selectionGroup;
	}
	public void setSelectionGroup(String selectionGroup) {
		this.selectionGroup = selectionGroup;
	}
	public SortedMap<String, DataParameter> getParameters() {
		return parameters;
	}
	public void setParameters(SortedMap<String, DataParameter> parameters) {
		this.parameters = parameters;
	}
	public boolean loadRow(String name,Parameters paramsp) {
		String dir = paramsp.getDataDirectory();
		String fileName = dir+"/"+name+Constants.DATAEXTENSION;
		DataDataRow row = new DataDataRow();
		try {
			JsonReader reader = new JsonReader(new FileReader(fileName));
			row = new Gson().fromJson(reader,DataDataRow.class);
			reader.close();
			setName(row.getName());
			setSelectionGroup(row.getSelectionGroup());
			setParameters(row.getParameters());
			Main.debugInst.debugThread("DataDataRow", "loadRow", MRBDebug.DETAILED, "Row loaded "+name);
		}
		catch (JsonParseException e) {
			Main.debugInst.debugThread("DataDataRow", "loadRow", MRBDebug.DETAILED, "Parse Exception "+e.getMessage());
			return false;
		}
		catch (IOException e){
			return false;
		}
		return true;
	}
	
	public void saveRow(Parameters paramsp) {
		String dir = paramsp.getDataDirectory();
		String fileName = dir+"/"+getName()+Constants.DATAEXTENSION;
		try {
			   FileWriter writer = new FileWriter(fileName);
			   String jsonString = new Gson().toJson(this);
			   writer.write(jsonString);
			   writer.close();	
			   Main.debugInst.debugThread("DataDataRow", "saveRow", MRBDebug.DETAILED, "Row Saved "+name);
          }
			 catch (IOException i) {
				 Main.debugInst.debugThread("DataDataRow", "saveRow", MRBDebug.DETAILED, "IO Exception "+i.getMessage());
					   i.printStackTrace();
          }
	}
	public void delete(Parameters paramsp) {
		String dir = paramsp.getDataDirectory();
		String fileName = dir+"/"+getName()+Constants.DATAEXTENSION;
		Main.debugInst.debugThread("DataRow", "delete", MRBDebug.SUMMARY, "Delete "+fileName);
		File file = new File(fileName);
		if (file.delete())
			Main.debugInst.debugThread("DataRow", "delete", MRBDebug.SUMMARY, "Deleted "+fileName);
		else
			Main.debugInst.debugThread("DataRow", "delete", MRBDebug.SUMMARY, "Delete failed "+fileName);

	}

}
