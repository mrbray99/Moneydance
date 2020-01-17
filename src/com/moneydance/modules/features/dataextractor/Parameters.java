/*
 * Copyright (c) 2014, Michael Bray. All rights reserved.
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
 */

package com.moneydance.modules.features.dataextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.DateRange;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.awt.JDateField;

/*
 * Container for all parameters for the report.  Data is serialised into file
 * {filepath}/{budgetname}.mbde
 * 
 * {filepath} is file system path from AccountBook
 * {budgetname} is the internal key for the selected budget
 */
public class Parameters implements java.io.Serializable {

	/*
	 * Static and transient fields are not stored
	 */
	private transient FeatureModuleContext currentCon;
	private transient AccountBook curAcctBook;
	private transient Account rootAcct;
	private transient File curFolder;
	private transient FileInputStream curInFile;
	private transient FileOutputStream curOutFile;
	private transient String fileName;
	private transient String fullFileName;
	private transient boolean dirty;
	private transient SortedMap<String, AccountDetails> mapAccounts;
	private transient long[] arrTotalActIncome;
	private transient long[] arrTotalActExpenses;
	private transient long[] arrTotalAct;
	private transient List<Account> selectedAccts;
	private transient List<AccountType> acctTypeTypes;

	/*
	 * The following fields are stored
	 */

	private int startDate;
	private int endDate;
	private List<ReportLine> listAccountLines;
	private List<String> listAccounts;
	private boolean landscapeMode;
	private boolean unionTypeAnd;
	private String reportName;
	private List<String> listColumns;
	private List<String> listSort;
	private List<SelectionItem> listSelections;
	private List<String> acctTypeNames;
	private List<CalculateField> calcFields;
	

	/*
	 * Constructor
	 */
	public Parameters(String strFileNamep,
			JDateField jdtFiscalStart, JDateField jdtFiscalEnd) {
		currentCon = Main.context;
		refreshData(strFileNamep, jdtFiscalStart, jdtFiscalEnd);
	}

	public void refreshData(String strFileNamep,
			JDateField jdtFiscalStart, JDateField jdtFiscalEnd) {
		mapAccounts = new TreeMap<String, AccountDetails>();
		fileName = strFileNamep;
		/*
		 * set parameters to default Start Date - Fiscal Start End Date - Fiscal
		 * End Rollup - true;
		 */
		startDate = jdtFiscalStart.getDateInt();
		endDate = jdtFiscalEnd.getDateInt();
		/*
		 * load Expense Accounts
		 */
		rootAcct = currentCon.getRootAccount();
		/*
		 * determine if file already exists
		 */
		curAcctBook = currentCon.getCurrentAccountBook();
		curFolder = curAcctBook.getRootFolder();
		/*
		 * File suffix is .BPEX for expenses, .BPIC for income
		 */

		fullFileName = curFolder.getAbsolutePath() + "\\" + fileName
				+ "."+Constants.PARAMETEREXTENSION;

		try {
			curInFile = new FileInputStream(fullFileName);
			ObjectInputStream ois = new ObjectInputStream(curInFile);
			/*
			 * file exists, copy temporary object to this object
			 */
			Parameters objTemp = (Parameters) ois.readObject();
			this.startDate = objTemp.startDate;
			this.endDate = objTemp.endDate;
			this.listAccountLines = objTemp.listAccountLines;
			this.listAccounts = objTemp.listAccounts;
			this.landscapeMode= objTemp.landscapeMode;
			this.reportName=objTemp.reportName;
			this.unionTypeAnd = objTemp.unionTypeAnd;
			this.listColumns=objTemp.listColumns;
			this.listSort=objTemp.listSort;
			this.listSelections = objTemp.listSelections;
			this.calcFields= objTemp.calcFields;
			this.acctTypeNames = objTemp.acctTypeNames;
			/*
			 * can not guarantee Account object ids are correct, go through the
			 * budget lines and set the account object ids using a map of names
			 * to object ids
			 * 
			 * Set date to force set up of internal fields
			 */

			curInFile.close();
		} catch (IOException | ClassNotFoundException ioException) {
			/*
			 * file does not exist
			 * set field default values 
			 */
			listAccountLines = new ArrayList<ReportLine>();
			listAccounts = new ArrayList<String>();
			this.startDate = 0;
			this.endDate = 99999999;
			this.landscapeMode= true;
			this.reportName="";
			this.listColumns=new ArrayList<String>();;
			this.listSort=new ArrayList<String> ();
			this.listSelections = new ArrayList<SelectionItem>();
			this.acctTypeNames = new ArrayList<String>();
			this.calcFields = new ArrayList<CalculateField>();


			}
			/*
			 * create the file
			 */
			try {
				curOutFile = new FileOutputStream(fullFileName);
				ObjectOutputStream oos = new ObjectOutputStream(curOutFile);
				oos.writeObject(this);
				curOutFile.close();
			} catch (IOException i) {
				i.printStackTrace();
			}
			/*
			 * Identify actual accounts 
			 */
			selectedAccts = new ArrayList<Account>();
			for (String strAccount : listAccounts) {
				Account acct = rootAcct.getAccountByName(strAccount);
				if (acct != null)
					selectedAccts.add(acct);
			}
			for (SelectionItem siObj : listSelections) {
				siObj.setCategories(rootAcct);
			}
			/* 
			 * set up account types, check for unknown types
			 */
			acctTypeTypes = new ArrayList<AccountType>();
			List<String> remove = new ArrayList<String>();
			for (String type :acctTypeNames){
				try {
					AccountType newType = AccountType.valueOf(type);
					acctTypeTypes.add(newType);
				}
				catch (IllegalArgumentException i){
					remove.add(type);
				}
			}
			for (String type : remove){
				acctTypeNames.remove(type);
			}
	}

	/**
	 * @return the unionTypeAnd
	 */
	public boolean isUnionTypeAnd() {
		return unionTypeAnd;
	}

	/**
	 * @param unionTypeAnd the unionTypeAnd to set
	 */
	public void setUnionTypeAnd(boolean unionTypeAnd) {
		dirty = true;
		this.unionTypeAnd = unionTypeAnd;
	}

	public void deleteAccountLine(String strKey) {
		mapAccounts.remove(strKey);
	}


	public List<ReportLine> getAccountsLines() {
		return listAccountLines;
	}

	/*
	 * determine if dirty
	 */
	public boolean isDirty() {
		if (dirty)
			return true;
		for (ReportLine objLine : listAccountLines) {
			if (objLine.isDirty())
				return true;
		}
		return false;
	}

	/*
	 * reset all dirty flags
	 */
	public void resetDirty() {
		dirty = false;
		for (ReportLine objLine : listAccountLines) {
			objLine.setDirty(false);
		}
	}

	/*
	 * gets
	 */
	/*
	 * file name
	 */
	public String getFileName() {
		return fileName;
	}
	/*
	 * Start Date
	 */
	public int getStartDate() {
		return startDate;
	}
	/*
	 * End Date
	 */
	public int getEndDate() {
		return endDate;
	}
	/*
	 * Account Lines
	 */
	public List<ReportLine> getAccountLines () {
		return listAccountLines;
	}
	/*
	 * Landscape
	 */

	public Boolean getLandscape() {
		return landscapeMode;
	}
	/*
	 * Report Name
	 */

	public String getReportName() {
		return reportName;
	}
	/*
	 * Selection List
	 */

	public List<SelectionItem> getSelectionList () {
		return listSelections;
	}

	public SortedMap<String, AccountDetails> getAccountsSelect() {
		return mapAccounts;
	}
	public List<Account> getAccounts(){
		return selectedAccts;
	}
	public List<CalculateField> getCalcFields() {
		return calcFields;
	}

	/*
	 * Actuals totals
	 */
	public long[] getIncomeTotals() {
		return arrTotalActIncome;
	}

	public long[] getExpensesTotals() {
		return arrTotalActExpenses;
	}

	public long[] getGrandTotals() {
		return arrTotalAct;
	}

	/*
	 * Selected Account Types
	 */
	public List<AccountType> getTypes() {
		return acctTypeTypes;
	}
	
	/*
	 * sets
	 */
	/*
	 * set dirty flag
	 */
	public void setDirty(boolean bDir) {
		dirty = bDir;
		return;
	}

	/*
	 * Start Date
	 */
	public void setStartDate(int iStart) {
		dirty = true;
		startDate = iStart;
		return;
	}

	/*
	 * End Date
	 */
	public void setEndDate(int iEnd) {
		dirty = true;
		endDate = iEnd;
		return;
	}
	/*
	 * Account Lines
	 */
	public void setAccountLines (List<ReportLine> listAccountLinesp) {
		dirty = true;
		listAccountLines=listAccountLinesp;
	}
	/*
	 * Landscape
	 */

	public void setLandscape(Boolean bLandscapep) {
		dirty = true;
		landscapeMode=bLandscapep;
	}
	/*
	 * Report Name
	 */

	public void setReportName(String strReportNamep) {
		dirty = true;
		reportName=strReportNamep;
	}
	/*
	 * Selection List
	 */

	public void setSelectionList (List<SelectionItem> listSelectionsp) {
		dirty = true;
		listSelections=listSelectionsp;
	}
	public void setCalcFields(List<CalculateField> calcFieldsp) {
		dirty = true;
		calcFields = calcFieldsp;
	}

	/*
	 * Account Types for account selection
	 */
	public void addType(AccountType typep) {
		dirty = true;
		if (acctTypeTypes.contains(typep))
			return;
		acctTypeTypes.add(typep);
		acctTypeNames.add(typep.name());
	}
	public void removeType(AccountType typep) {
		dirty = true;
		if (!acctTypeTypes.contains(typep))
			return;
		acctTypeTypes.remove(typep);
		acctTypeNames.remove(typep.name());
	}
	public void setAccounts(List<Account> selectedAcctsp){
		dirty = true;
		selectedAccts = selectedAcctsp;
		listAccounts.clear();
		for (Account acct : selectedAccts){
			listAccounts.add(acct.getAccountName());
		}
	}

	/*
	 * Save the parameters into the specified file
	 */
	public void saveParams(String strFileNamep) {
		fileName = strFileNamep;
		fullFileName = curFolder.getAbsolutePath() + "\\" + fileName
				+ "."+Constants.PARAMETEREXTENSION;
		try {
			curInFile = new FileInputStream(fullFileName);
			JFrame fTemp = new JFrame();
			int iResult = JOptionPane.showConfirmDialog(fTemp, "File "
					+ fileName
					+ " already exists.  Do you wish to overwrite?");
			curInFile.close();
			if (iResult != JOptionPane.YES_OPTION)
				return;
		} catch (IOException i) {
		}
		listAccountLines.clear();
		for (Entry<String, AccountDetails> objEntry : mapAccounts
				.entrySet()) {
			listAccountLines.add(objEntry.getValue().getLine());
		}
		try {
			curOutFile = new FileOutputStream(fullFileName);
			ObjectOutputStream oos = new ObjectOutputStream(curOutFile);
			oos.writeObject(this);
			oos.close();
			curOutFile.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
		/*
		 * clear dirty flags
		 */
		resetDirty();

	}

	/*
	 * Generate
	*/
	public void generate(FeatureModuleContext context, DateRange[] arrPeriods) {
		
	}

}
