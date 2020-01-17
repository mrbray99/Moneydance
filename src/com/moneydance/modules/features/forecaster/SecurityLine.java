package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;

public class SecurityLine {
	private Constants.SecurityLineType enumType;
	private boolean bSelected;
	private SecurityLine objParent;
	private String strAccount;
	private Account objAccount;
	private double dRPI;
	private String strInvestAcct;
	private Account objInvestAcct;
	private IncludedSecurity objItem;
	/**
	 * @return the enumType
	 */
	public Constants.SecurityLineType getType() {
		return enumType;
	}	/**
	 * @return the bSelected
	 */
	public boolean getSelected() {
		return bSelected;
	}

	/**
	 * @return the objParent
	 */
	public SecurityLine getParentLine() {
		return objParent;
	}
	/**
	 * @return the strInvestAcct
	 */
	public String getInvestName() {
		return strInvestAcct;
	}
	/**
	 * @return the objInvestAcct
	 */
	public Account getInvestAcct() {
		return objInvestAcct;
	}
	/**
	 * @return the strAccount
	 */
	public String getFullAccountName() {
		return strAccount;
	}
	/**
	 * @return the objAccount
	 */
	public Account getAccount() {
		return objAccount;
	}
	/**
	 * @return the dRPI
	 */
	public double getRPI() {
		return dRPI;
	}
	/**
	 * @return the objItem
	 */
	public IncludedSecurity getItem() {
		return objItem;
	}
	/**
	 * @param enumType the type to set
	 */
	public void setType(Constants.SecurityLineType enumType) {
		this.enumType = enumType;
	}	/**
	 * @param bSelected the bSelected to set
	 */
	public void setSelected(boolean bSelected) {
		this.bSelected = bSelected;
	}
	/**
	 * @param objParent the Parent Line to set
	 */
	public void setParentLine(SecurityLine objParent) {
		this.objParent = objParent;
	}
	/**
	 * @param strInvestAcct the Investment account to set
	 */
	public void setInvestName(String strAccount) {
		this.strInvestAcct = strAccount;
	}
	/**
	 * @param objInvestAcct the investment account to set
	 */
	public void setInvestAcct(Account objAccount) {
		this.objInvestAcct = objAccount;
	}
	/**
	 * @param strFromAccount the account name to set
	 */
	public void setAccountName(String strFromAccount) {
		this.strAccount = strFromAccount;
	}
	/**
	 * @param objFromAccount the Account object to set
	 */
	public void setAccount(Account objFromAccount) {
		this.objAccount = objFromAccount;
	}
	/**
	 * @param dRPI the RPI to set
	 */
	public void setRPI(double dRPIp) {
		dRPI = dRPIp;
	}
	/**
	 * @param objItemp the IncludedSecurity to set
	 */
	public void setItem(IncludedSecurity objItemp) {
		objItem = objItemp;
	}

}
