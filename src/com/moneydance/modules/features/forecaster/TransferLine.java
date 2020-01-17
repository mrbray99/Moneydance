package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;

public class TransferLine {
	private IncludedTransfer objItem;
	private String UUID;
	private boolean bSelected;
	private String strSource;
	private Account objSource;
	private long lAmount;
	private int iPeriod;
	private String strDest;
	private Account objDest;
	private double dRPI;
	/*
	 * UUID - used as key for Transfer Items
	 */
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String UUIDp) {
		UUID = UUIDp;
	}
	
	/*
	 * Selected
	 */
	public void setSelected(boolean bSelectedp) {
		bSelected = bSelectedp;
	}
	public boolean getSelected() {
		return bSelected;
	}
	/*
	 * period
	 */
	public void setPeriod(int iPeriodp) {
		iPeriod = iPeriodp;
	}
	public int getPeriod() {
		return iPeriod;
	}
	/* 
	 * Transfer Item
	 */
	public void setItem(IncludedTransfer objItemp) {
		objItem=objItemp;
	}
	public IncludedTransfer getItem() {
		return objItem;
	}
	/*
	 * category name and object
	 */
	public void setSourceName(String strSourcep) {
		strSource= strSourcep;
	}
	public String getSourceName() {
		return strSource;
	}
	public void setSource(Account objSourcep) {
		objSource= objSourcep;
	}
	public Account getSource() {
		return objSource;
	}
	/*
	 * Annual Amount
	 */
	public void setAmount(long lAmtp) {
		lAmount = lAmtp;
	}
	public long getAmount() {
		return lAmount;
	}
	/*
	 * Destination Account and object
	 */
	public void setDestName(String strAccountp) {
		strDest = strAccountp;
	}
	public String getDestName() {
		return strDest;
	}
	public void setDest(Account objAccountp) {
		objDest = objAccountp;
	}
	public Account getDest() {
		return objDest;
	}
	/*
	 * RPI
	 */
	public double getRPI() {
		return dRPI;
	}
	public void setRPI(double dRPIp) {
		dRPI = dRPIp;
	}
}
