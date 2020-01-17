package com.moneydance.modules.features.forecaster;

import com.infinitekind.moneydance.model.Account;

public class BudgetLine {
	private Constants.BudgetLineType enumType;
	private BudgetLine objParent;
	private IncludedBudget objItem;
	private SourceItem objSource;
	private boolean bSelected;
	private String strCategory;
	private Account objCategory;
	private String strIndentedName;
	private long lAnnualAmt;
	private long lBudgetAmt;
	private long lActualAmt;
	private long lTotalBudgetAmt;
	private long lAnnualActualAmt;
	private double dAddRPI;
	private String strAccount;
	private Account objAccount;
	private long lAcctAmt;
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
	 * line type
	 */
	public void setType(Constants.BudgetLineType enumTypep) {
		enumType = enumTypep;
	}
	public Constants.BudgetLineType getType() {
		return enumType;
	}
	/*
	 * Parent Object
	 */
	public BudgetLine getParent () {
		return objParent;
	}
	public void setParent(BudgetLine objParentp) {
		objParent = objParentp;
	}
	/*
	 * Amount Error
	 */
	public boolean getAmtError () {
		return objItem == null ? false : objItem.getError();
	}
	/* 
	 * Budget Item
	 */
	public void setItem(IncludedBudget objItemp) {
		objItem=objItemp;
	}
	public IncludedBudget getItem() {
		return objItem;
	}
	/*
	 * Source Items
	 */
	public void setSource(SourceItem objSourcep) {
		objSource= objSourcep;
	}
	public SourceItem getSource() {
		return objSource;
	}
	/*
	 * category name and object
	 */
	public void setCategoryName(String strCategoryp) {
		strCategory= strCategoryp;
	}
	public void setIndentedName(String strIndentedNamep) {
		strIndentedName= strIndentedNamep;
	}
	public String getCategoryName() {
		return strCategory;
	}
	public String getIndentedName() {
		return strIndentedName;
	}
	public void setCategory(Account objCategoryp) {
		objCategory= objCategoryp;
	}
	public Account getCategory() {
		return objCategory;
	}
	/*
	 * Annual Amount
	 */
	public void setAmount(long lAmtp) {
		lAnnualAmt = lAmtp;
	}
	public long getAmount() {
		return lAnnualAmt;
	}
	/*
	 * Budget Amount
	 */
	public void setBudgetAmount(long lAmtp) {
		lBudgetAmt = lAmtp;
	}
	public long getBudgetAmount() {
		return lBudgetAmt;
	}
	/*
	 * Actual Amount
	 */
	public void setActualAmount(long lAmtp) {
		lActualAmt = lAmtp;
	}
	public long getActualAmount() {
		return lActualAmt;
	}
	/*
	 * Total Budget Amount
	 */
	public void setTotalBudgetAmount(long lAmtp) {
		lTotalBudgetAmt = lAmtp;
	}
	public long getTotalBudgetAmount() {
		return lTotalBudgetAmt;
	}
	/*
	 * Annual Actual Amount
	 */
	public void setAnnualActualAmount(long lAmtp) {
		lAnnualActualAmt = lAmtp;
	}
	public long getAnnualActualAmount() {
		return lAnnualActualAmt;
	}
	/*
	 * RPI
	 */
	public void setRPI(double dRPIp) {
		dAddRPI = dRPIp;
	}
	public double getRPI() {
		return dAddRPI;
	}
	/*
	 * Source Account and object
	 */
	public void setAccountName(String strAccountp) {
		strAccount = strAccountp;
	}
	public String getFullAccountName() {
		return strAccount;
	}
	public void setAccount(Account objAccountp) {
		objAccount = objAccountp;
	}
	public Account getAccount() {
		return objAccount;
	}
	/*
	 * Account amount
	 */
	public void setAcctAmount(long lAmt) {
		lAcctAmt = lAmt;
	}
	public long getAcctAmount() {
		return lAcctAmt;
	}
	
}
