package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountUtil;


public class SourceItem implements java.io.Serializable {
	private static final long serialVersionUID = 1l;
	private String strAccount;
	private String strUUID;
	private transient Account objAccount;
	private long lAmt;
	public Account getAccount(){
		if (objAccount == null)
			objAccount = AccountUtil.findAccountWithID(Main.getContxt().getRootAccount(), strUUID);
		return objAccount;
	}
	public String getAccountName(){
		return strAccount;
	}
	public long getAmount(){
		return lAmt;
	}
	public void setAccount(Account objAccountp){
		objAccount = objAccountp;
		if (objAccount != null)
			strUUID = objAccount.getUUID();
	}
	public void setAccountName(String strAccountp){
		strAccount = strAccountp;
	}
	public void setAmount(long lAmtp){
		lAmt = lAmtp;
	}
}

