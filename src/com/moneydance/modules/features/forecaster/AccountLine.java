package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;

public class AccountLine {
	private boolean bSelected;
	private String strAccount;
	private Account objAccount;
	/**
	 * @return the bSelected
	 */
	public boolean getSelected() {
		return bSelected;
	}

	/**
	 * @return the strAccount
	 */
	public String getAccountName() {
		return strAccount;
	}
	/**
	 * @return the objAccount
	 */
	public Account getAccount() {
		return objAccount;
	}
	/**
	 * @param bSelected the bSelected to set
	 */
	public void setSelected(boolean bSelected) {
		this.bSelected = bSelected;
	}
	/**
	 * @param strAccount the strAccount to set
	 */
	public void setAccountName(String strAccount) {
		this.strAccount = strAccount;
	}
	/**
	 * @param objAccount the objAccount to set
	 */
	public void setAccount(Account objAccount) {
		this.objAccount = objAccount;
	}	
}
