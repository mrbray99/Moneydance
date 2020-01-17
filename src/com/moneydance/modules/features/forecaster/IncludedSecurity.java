package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountUtil;


public class IncludedSecurity implements java.io.Serializable {
	private static final long serialVersionUID = 1l;
	private String strAccount;
	private String strUUID;
	private transient Account objAccount;
	private double dRPI;
	public IncludedSecurity(String strAccountp, Account objAccountp){
		strAccount = strAccountp;
		objAccount = objAccountp;
		if (objAccount != null)
			strUUID = objAccount.getUUID();
	}
	public String getFullAccountName() {
		return strAccount;
	}
	public Account getAccount() {
		if (objAccount == null)
			objAccount = AccountUtil.findAccountWithID(Main.getContxt().getRootAccount(), strUUID);
		return objAccount;
	}
	
	public void setAccount(Account objAccountp) {
		objAccount = objAccountp;
		if (objAccount != null)
			strUUID = objAccount.getUUID();
	}
	public double getRPI() {
		return dRPI;
	}
	public void setRPI(double dRPIp) {
		dRPI = dRPIp;
	}
}
