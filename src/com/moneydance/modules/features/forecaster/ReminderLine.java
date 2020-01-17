package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Reminder;

public class ReminderLine {
	private Constants.ReminderLineType enumType;
	private boolean bSelected;
	private Reminder objReminder;
	private ReminderLine objParent;
	private String strKey;
	private String strFromAccount;
	private String strToAccount;
	private Account objFromAccount;
	private Account objToAccount;
	private int iFromDate;
	private int iToDate;
	private long lAmount;
	private String strRepeat;
	/**
	 * @return the enumType
	 */
	public Constants.ReminderLineType getType() {
		return enumType;
	}
	/**
	 * @return the bSelected
	 */
	public boolean getSelected() {
		return bSelected;
	}
	/**
	 * @return the objReminder
	 */
	public Reminder getReminder() {
		return objReminder;
	}
	/**
	 * @return the objParent
	 */
	public ReminderLine getParent() {
		return objParent;
	}
	/**
	 * @return the strKey
	 */
	public String getKey() {
		return strKey;
	}
	
	/**
	 * @return the strFromAccount
	 */
	public String getFromAccountName() {
		return strFromAccount;
	}
	/**
	 * @return the objFromAccount
	 */
	public Account getFromAccount() {
		return objFromAccount;
	}
	/**
	 * @return the strToAccount
	 */
	public String getToAccountName() {
		return strToAccount;
	}
	/**
	 * @return the objToAccount
	 */
	public Account getToAccount() {
		return objToAccount;
	}
	/**
	 * @return the iFromDate
	 */
	public int getFromDate() {
		return iFromDate;
	}
	/**
	 * @return the iToDate
	 */
	public int getToDate() {
		return iToDate;
	}
	/**
	 * @return the lAmount
	 */
	public long getAmount() {
		return lAmount;
	}
	/**
	 * @return the strRepeat
	 */
	public String getRepeat() {
		return strRepeat;
	}
	/**
	 * @param enumType the type to set
	 */
	public void setType(Constants.ReminderLineType enumType) {
		this.enumType = enumType;
	}
	/**
	 * @param bSelected the bSelected to set
	 */
	public void setSelected(boolean bSelected) {
		this.bSelected = bSelected;
	}
	/**
	 * @param objReminder the objReminder to set
	 */
	public void setReminder(Reminder objReminder) {
		this.objReminder = objReminder;
	}
	/**
	 * @param objParent the parent to set
	 */
	public void setParent (ReminderLine objParentp) {
		objParent = objParentp;
	}
	/**
	 * @param strKey the strKey to set
	 */
	public void setKey(String strKey) {
		this.strKey = strKey;
	}
	/**
	 * @param strFromAccount the strFromAccount to set
	 */
	public void setFromAccountName(String strFromAccount) {
		this.strFromAccount = strFromAccount;
	}
	/**
	 * @param objFromAccount the objFromAccount to set
	 */
	public void setFromAccount(Account objFromAccount) {
		this.objFromAccount = objFromAccount;
	}
	/**
	 * @param strToAccount the strToAccount to set
	 */
	public void setToAccountName(String strToAccount) {
		this.strToAccount = strToAccount;
	}
	/**
	 * @param objToAccount the objToAccount to set
	 */
	public void setToAccount(Account objToAccount) {
		this.objToAccount = objToAccount;
	}
	/**
	 * @param iFromDate the iFromDate to set
	 */
	public void setFromDate(int iFromDate) {
		this.iFromDate = iFromDate;
	}
	/**
	 * @param iToDate the iToDate to set
	 */
	public void setToDate(int iToDate) {
		this.iToDate = iToDate;
	}
	/**
	 * @param lAmount the lAmount to set
	 */
	public void setAmount(long lAmount) {
		this.lAmount = lAmount;
	}
	/**
	 * @param strRepeat the strRepeat to set
	 */
	public void setRepeat(String strRepeat) {
		this.strRepeat = strRepeat;
	}
	
}
