package com.moneydance.modules.features.forecaster;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.table.DefaultTableModel;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;

public class SecurityTableModel extends DefaultTableModel {
	private ForecastParameters objParams;
	private SortedMap<String, Account> mapData;
	private SortedMap<String,IncludedSecurity> mapSelectedSecurities;
	private SortedMap<String,Account> mapSelectedAccts;
	private List<SecurityLine> listSecurityLines;
	private String[] arrColumnNames = { "Select","Investment Acct", "Security","Add.. RPI"};

	public SecurityTableModel(ForecastParameters objParamsp) {
		super();
		objParams = objParamsp;
		rebuildLines();
	}

	/* **************************************************************************************
	 * separated out to allow data to be reloaded when underlying data changes
	 * **************************************************************************************/
	public void rebuildLines() {
		if (listSecurityLines == null)
			listSecurityLines = new ArrayList<SecurityLine>();
		else
			listSecurityLines.clear();
		mapData = objParams.getAccounts();
		mapSelectedAccts = objParams.getSelectedAccts();
		mapSelectedSecurities = objParams.getSelectedSecurities();
		for (SortedMap.Entry<String, Account> objEntry : mapData.entrySet()) {
			Account objAcct = objEntry.getValue();
			/*
			 * Check to see if this category has any budget data
			 */
			if (objAcct == null)
				continue;
			if (objAcct.getAccountType() != AccountType.INVESTMENT)
				continue;
			if (objAcct.getSubAccountCount() < 1)
				continue;
			SecurityLine objLine = new SecurityLine();
			listSecurityLines.add(objLine);
			objLine.setInvestName(objEntry.getKey());
			if (mapSelectedAccts.get(objEntry.getKey())== null)
				objLine.setSelected(false);
			else
				objLine.setSelected(true);
			objLine.setInvestAcct(objAcct);
			objLine.setType(Constants.SecurityLineType.PARENT);
			SecurityLine objLine2 = objLine;
			for (int i=0;i<objAcct.getSubAccountCount();i++) {
				objLine2 = new SecurityLine();
				listSecurityLines.add(objLine2);
				objLine2.setType(Constants.SecurityLineType.SPLIT);
				objLine2.setParentLine(objLine);
				Account objSecure = objAcct.getSubAccount(i);
				IncludedSecurity objSelect = mapSelectedSecurities.get(objSecure.getFullAccountName());
				if (objSelect != null) {
					objLine2.setSelected(true);
					objLine2.setRPI(objSelect.getRPI());
					objLine2.setItem(objSelect);
				}
				else {
					objLine2.setSelected(false);
					objLine2.setRPI(0.0);
				}
				objLine2.setAccountName(objSecure.getFullAccountName());
				objLine2.setAccount(objSecure);
			}

		}

	}
	/*
	 * Table Model Overrides
	 */
	@Override
	public int getRowCount() {
		return listSecurityLines == null ? 0 : listSecurityLines.size();
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
		SecurityLine objSecure = listSecurityLines.get(iRow);
		switch (iCol) {
		/*
		 * Select
		 */
		case 0:
			return objSecure.getSelected();
			/*
			 * Name
			 */
		case 1:
			if (objSecure.getType() == Constants.SecurityLineType.PARENT)
				return objSecure.getInvestName();
			return "   ";
		case 2:
			if (objSecure.getType() == Constants.SecurityLineType.SPLIT)
				return objSecure.getFullAccountName();
			return "   ";
		case 3:
			if (objSecure.getType() == Constants.SecurityLineType.SPLIT)
				return String.format("%1$,.2f%%", objSecure.getRPI());
			return "    ";

		}
		/*
		 * action
		 */
		return null;

	}
	@Override
	public boolean isCellEditable(int iRow, int iCol) {
		SecurityLine objSecure = listSecurityLines.get(iRow);
		switch (iCol) {
		case 0:
			return true;
		case 3:
			if (objSecure.getType() == Constants.SecurityLineType.SPLIT  &&
				objSecure.getSelected())
				return true;
		default :
			return false;
		}
	}

	@Override
	public void setValueAt(Object value, int iRow, int iCol) {
		SecurityLine objSecure= listSecurityLines.get(iRow);
		/*
		 * copes with call when data is invalid
		 */
		if (value == null)
			return;
		switch (iCol) {
		case 0:
			objSecure.setSelected((Boolean) value);
			if (objSecure.getType() == Constants.SecurityLineType.PARENT) {
				if (objSecure.getSelected())
					objParams.addSelectedAccount(objSecure.getInvestName());
				else
					objParams.removeSelectedAccount(objSecure.getInvestName());	
			}
			else {
				if (objSecure.getSelected()) {
					objParams.addSelectedSecurity(objSecure.getFullAccountName());
					objSecure.setItem(mapSelectedSecurities.get(objSecure.getFullAccountName()));
				}
				else {
					objParams.removeSelectedSecurity(objSecure.getFullAccountName());
					objSecure.setItem(null);
				}
			}
			break;
		case 3:
			if (objSecure.getType() == Constants.SecurityLineType.SPLIT &&
				objSecure.getSelected()) {	
				String strRPI = (String) value;
				while (strRPI.endsWith(" ") && strRPI.length() > 0){
					strRPI = strRPI.substring(0,strRPI.length()-1);
				}
				if (strRPI.endsWith("%"))
					strRPI = strRPI.substring(0,strRPI.length()-1);
				try {
					objSecure.setRPI(Double.parseDouble(strRPI));
				}
				catch (NumberFormatException e){					
					objSecure.setRPI(0.0d);
					}
				objSecure.getItem().setRPI(objSecure.getRPI());
			}
		}
		fireTableDataChanged();
	}
	public SecurityLine getLine(int iRow){
		return listSecurityLines.get(iRow);
	}
}
