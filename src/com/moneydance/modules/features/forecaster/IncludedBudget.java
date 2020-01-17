package com.moneydance.modules.features.forecaster;

import java.util.ArrayList;
import java.util.List;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountUtil;

public class IncludedBudget implements java.io.Serializable {
	private static final long serialVersionUID = 1l;
	private boolean bSelected;
	private String strBudgetCategory;
	private String strUUID;
	private transient Account objBudgetCategory;
	private double dAddRPI;
	private boolean bError;
	private long lAmount;
	private long lBudgetAmt;
	private long lActualAmt;
	private Constants.AnnualAmount enumAnnual;
	private List<SourceItem> listSource;
	/*
	 * Selected
	 */
	public boolean getSelected(){
		return bSelected;
	}
	public void setSelected (Boolean bSelectedp) {
		bSelected = bSelectedp;
		determineError();
	}
	/*
	 * Category name and object
	 */
	public Account getBudgetCategory() {
		if (objBudgetCategory == null)
			objBudgetCategory = AccountUtil.findAccountWithID(Main.getContxt().getRootAccount(), strUUID);
		return objBudgetCategory;
	}
	public void setBudgetCategory(Account objBudgetCategoryp) {
		objBudgetCategory = objBudgetCategoryp;
		if (objBudgetCategory != null)
			strUUID = objBudgetCategory.getUUID();
	}
	public String getBudgetCategoryName() {
		return strBudgetCategory;
	}
	public void setBudgetCategoryName(String strBudgetCategoryp) {
		strBudgetCategory = strBudgetCategoryp;
	}
	/*
	 * amount
	 */
	public long getAmount(){
		return lAmount;
	}
	public void setAmount (long lAmountp) {
		lAmount = lAmountp;
		determineError();
	}
	/*
	 * budget amount
	 */
	public long getBudgetAmt(){
		return lBudgetAmt;
	}
	public void setBudgetAmt (long lAmountp) {
		lBudgetAmt = lAmountp;
	}
	/*
	 * Actual amount
	 */
	public long getActualAmt(){
		return lActualAmt;
	}
	public void setActualAmt (long lAmountp) {
		lActualAmt = lAmountp;
	}
	/*
	 * RPI
	 */
	public double getAddRPI() {
		return dAddRPI;
	}
	public void setAddRPI(double dAddRPIp) {
		dAddRPI = dAddRPIp;
	}
	/*
	 * Source list and item
	 */
	public List<SourceItem> getSourceItems() {
		return listSource;
	}
	public void createSourceList () {
		listSource = new ArrayList<SourceItem>();
	}
	public void addSource(SourceItem objSource) {
		listSource.add(objSource);
		determineError();
	}
	public void deleteSource(SourceItem objSource){
		listSource.remove(objSource);
		determineError();
	}
	/*
	 *  Annual Amount enum
	 */
	public void setAnnualAmtType(Constants.AnnualAmount enumAnnualp) {
		enumAnnual = enumAnnualp;
	}
	public Constants.AnnualAmount getAnnualAmtType() {
		if (enumAnnual == null) {
			enumAnnual = Constants.AnnualAmount.NONE;
		}
		return enumAnnual;
	}
	/*
	 * Error
	 */
	public void determineError() {
		long lAmt = 0;
		if (listSource == null)
			return;
		for (SourceItem objSource :listSource)
			lAmt += objSource.getAmount();
		if (lAmt != lAmount)
			bError = true;
		else
			bError = false;
	}
	public boolean getError() {
		return bError;
	}
}
