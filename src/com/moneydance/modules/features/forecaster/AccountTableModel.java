package com.moneydance.modules.features.forecaster;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.table.DefaultTableModel;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;

public class AccountTableModel extends DefaultTableModel {
	private ForecastParameters objParams;
	private AccountType enumAcctType;
	private SortedMap<String, Account> mapData;
	private SortedMap<String,Account> mapSelectedAccts;
	private List<AccountLine> listAccountLines;
	private String[] arrColumnNames = { "Select", "Name"};

	public AccountTableModel(ForecastParameters objParamsp, AccountType enumAcctTypep) {
		super();
		objParams = objParamsp;
		enumAcctType = enumAcctTypep;
		rebuildLines();
	}

	/* **************************************************************************************
	 * separated out to allow data to be reloaded when underlying data changes
	 * **************************************************************************************/
	public void rebuildLines() {
		if (listAccountLines == null)
			listAccountLines = new ArrayList<AccountLine>();
		else
			listAccountLines.clear();
		mapData = objParams.getAccounts();
		mapSelectedAccts = objParams.getSelectedAccts();
		String [] arrLastPath = new String[0];
		String [] arrPath;
		for (SortedMap.Entry<String, Account> objEntry : mapData.entrySet()) {
			Account objAcct = objEntry.getValue();
			if (objAcct == null)
				continue;
			arrPath = objEntry.getKey().split(Constants.MDSEPARATOR);
			if (enumAcctType != objAcct.getAccountType())
				continue;
			AccountLine objLine = new AccountLine();
			listAccountLines.add(objLine);
			Account objSelect = mapSelectedAccts.get(objEntry.getKey());
			if (objSelect != null)
				objLine.setSelected(true);
			else
				objLine.setSelected(false);
			String strAccountName = "";
			for (int i=0;i<arrPath.length;i++){
				if (i<arrLastPath.length &&arrPath[i].equals(arrLastPath[i])) 
					strAccountName += "    ";
				else
					strAccountName += arrPath[i];
			}
			objLine.setAccountName(strAccountName);
			objLine.setAccount(objAcct);
			arrLastPath = arrPath;
		}

	}
	/*
	 * Table Model Overrides
	 */
	@Override
	public int getRowCount() {
		return listAccountLines == null ? 0 : listAccountLines.size();
	}

	@Override
	public int getColumnCount() {
		return arrColumnNames.length;
	}

	@Override
	public String getColumnName(int c) {
		return arrColumnNames[c];
	}

	@Override
	public Object getValueAt(int iRow, int iCol) {
		AccountLine objAcct = listAccountLines.get(iRow);
		switch (iCol) {
		/*
		 * Select
		 */
		case 0:
			return objAcct.getSelected();
			/*
			 * Name
			 */
		case 1:
			return objAcct.getAccountName();

		}
		/*
		 * action
		 */
		return null;

	}
	@Override
	public boolean isCellEditable(int iRow, int iCol) {
		switch (iCol) {
		case 0:
			return true;
		default :
			return false;
		}
	}

	@Override
	public void setValueAt(Object value, int iRow, int iCol) {
		AccountLine objAcct= listAccountLines.get(iRow);
		/*
		 * copes with call when data is invalid
		 */
		if (value == null)
			return;
		switch (iCol) {
		case 0:
			objAcct.setSelected((Boolean) value);
			if (objAcct.getSelected())
				objParams.addSelectedAccount(objAcct.getAccount().getFullAccountName());
			else
				objParams.removeSelectedAccount(objAcct.getAccount().getFullAccountName());
			break;

		}
		fireTableDataChanged();
	}

}
