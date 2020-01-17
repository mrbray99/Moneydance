package com.moneydance.modules.features.jasperreports;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class SelectionDataRow {
	String name;
	Boolean accounts;
	Boolean accountTypes;
	Boolean address;
	Boolean budgets;
	Boolean currency;
	Boolean security;
	Boolean transactions;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getAccounts() {
		return accounts;
	}
	public void setAccounts(Boolean accounts) {
		this.accounts = accounts;
	}
	public Boolean getAccountTypes() {
		return accountTypes;
	}
	public void setAccountTypes(Boolean accountTypes) {
		this.accountTypes = accountTypes;
	}
	public Boolean getAddress() {
		return address;
	}
	public void setAddress(Boolean address) {
		this.address = address;
	}
	public Boolean getBudgets() {
		return budgets;
	}
	public void setBudgets(Boolean budgets) {
		this.budgets = budgets;
	}
	public Boolean getCurrency() {
		return currency;
	}
	public void setCurrency(Boolean currency) {
		this.currency = currency;
	}
	public Boolean getSecurity() {
		return security;
	}
	public void setSecurity(Boolean security) {
		this.security = security;
	}
	public Boolean getTransactions() {
		return transactions;
	}
	public void setTransactions(Boolean transactions) {
		this.transactions = transactions;
	}
	public boolean loadRow(String name,Parameters paramsp) {
		String dir = paramsp.getDataDirectory();
		String fileName = dir+"/"+name+Constants.SELEXTENSION;
		SelectionDataRow row = new SelectionDataRow();
		try {
			JsonReader reader = new JsonReader(new FileReader(fileName));
			row = new Gson().fromJson(reader,SelectionDataRow.class);
			reader.close();
			setName(row.getName());
			setAccounts(row.getAccounts());
			setAccountTypes(row.getAccountTypes());
			setAddress(row.getAddress());
			setBudgets(row.getBudgets());
			setCurrency(row.getCurrency());
			setSecurity(row.getSecurity());
			setTransactions(row.getTransactions());
			Main.debugInst.debugThread("SelectionDataRow", "loadRow", MRBDebug.DETAILED, "Row loaded "+name);
		}
		catch (JsonParseException e) {
			Main.debugInst.debugThread("SelectionDataRow", "loadRow", MRBDebug.DETAILED, "Parse Exception "+e.getMessage());
			return false;
		}
		catch (IOException e){
			return false;
		}
		return true;
	}
	
	public void saveRow(Parameters paramsp) {
		String dir = paramsp.getDataDirectory();
		String fileName = dir+"/"+getName()+Constants.SELEXTENSION;
		try {
			   FileWriter writer = new FileWriter(fileName);
			   String jsonString = new Gson().toJson(this);
			   writer.write(jsonString);
			   writer.close();	
			   Main.debugInst.debug("SelectionDataRow", "saveRow", MRBDebug.DETAILED, "Row Saved "+name);
          }
			 catch (IOException i) {
				 Main.debugInst.debug("SelectionDataRow", "saveRow", MRBDebug.DETAILED, "IO Exception "+i.getMessage());
					   i.printStackTrace();
          }
	}

}
