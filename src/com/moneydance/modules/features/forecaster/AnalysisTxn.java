package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;

public class AnalysisTxn implements Comparable<AnalysisTxn>{
	private Account objAcct;
	private int iDate;
	private long lAmount;
	/**
	 * @return the objAcct
	 */
	public Account getAcct() {
		return objAcct;
	}
	/**
	 * @return the iDate
	 */
	public int getDate() {
		return iDate;
	}
	/**
	 * @return the lAmount
	 */
	public long getAmount() {
		return lAmount;
	}
	/**
	 * @param objAcct the objAcct to set
	 */
	public void setAcct(Account objAcct) {
		this.objAcct = objAcct;
	}
	/**
	 * @param iDate the iDate to set
	 */
	public void setDate(int iDate) {
		this.iDate = iDate;
	}
	/**
	 * @param lAmount the lAmount to set
	 */
	public void setAmount(long lAmount) {
		this.lAmount = lAmount;
	}
	@Override
	public int compareTo(AnalysisTxn txnOther) {
		if (objAcct != txnOther.getAcct() ) {
			if (objAcct == null)
				return 1;
			if (txnOther.getAcct() == null)
				return -1;
			return objAcct.getFullAccountName().compareTo(txnOther.getAcct().getFullAccountName());
		}
		if (iDate < txnOther.getDate())
			return -1;
		else
			if (iDate > txnOther.getDate())
				return 1;
		return 0;
	}
}
