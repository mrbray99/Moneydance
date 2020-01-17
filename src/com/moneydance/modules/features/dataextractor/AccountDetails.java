package com.moneydance.modules.features.dataextractor;

import com.infinitekind.moneydance.model.Account;

public class AccountDetails {
	private Account acct;
	private String parent;
	private ReportLine line;
	public AccountDetails(Account acctp, String parentp) {
		acct = acctp;
		parent = parentp;
	}
	public Account getAccount() {
		return acct;
	}
	public String getParent () {
		return parent;
	}
	public ReportLine getLine () {
		return line;
	}
	public void setLine (ReportLine objLinep) {
		line = objLinep;
	}

}
