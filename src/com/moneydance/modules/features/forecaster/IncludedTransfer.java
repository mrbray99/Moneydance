package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountUtil;


public class IncludedTransfer implements java.io.Serializable {
	private static final long serialVersionUID = 1l;
	private boolean bSelected;
	private String strSourceAccount;
	private String strSourceUUID;
	private transient Account objSourceAccount;
	private String strDestAccount;
	private String strDestUUID;
	private transient Account objDestAccount;
	private int iPeriod;
	private long lAmount;
	private double dRPI;
	public boolean getSelected() {
		return bSelected;
	}
	public void setSelected(boolean bSelected) {
		this.bSelected = bSelected;
	}
	public String getSourceAccountName() {
		return strSourceAccount;
	}
	public void setSourceAccountName(String strSourceAccount) {
		this.strSourceAccount = strSourceAccount;
	}
	public Account getSourceAccount() {
		if (objSourceAccount == null)
			objSourceAccount = AccountUtil.findAccountWithID(Main.getContxt().getRootAccount(), strSourceUUID);
		return objSourceAccount;
	}
	public void setSourceAccount(Account objSourceAccount) {
		this.objSourceAccount = objSourceAccount;
		if (objSourceAccount != null)
			strSourceUUID = this.objSourceAccount.getUUID();
	}
	public String getDestAccountName() {
		return strDestAccount;
	}
	public void setDestAccountName(String strDestAccount) {
		this.strDestAccount = strDestAccount;
	}
	public Account getDestAccount() {
		if (objDestAccount == null)
			objDestAccount = AccountUtil.findAccountWithID(Main.getContxt().getRootAccount(), strDestUUID);
		return objDestAccount;
	}
	public void setDestAccount(Account objDestAccount) {
		this.objDestAccount = objDestAccount;
		if (objDestAccount != null)
			strDestUUID = this.objDestAccount.getUUID();
	}
	public int getPeriod() {
		return iPeriod;
	}
	public void setPeriod(int iPeriod) {
		this.iPeriod = iPeriod;
	}
	public long getAmount() {
		return lAmount;
	}
	public void setAmount(long lAmount) {
		this.lAmount = lAmount;
	}
	public double getRPI() {
		return dRPI;
	}
	public void setRPI(double dRPIp) {
		dRPI = dRPIp;
	}
}
