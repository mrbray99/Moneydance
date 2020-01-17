package com.moneydance.modules.features.forecaster;

import java.util.SortedMap;

public class SavedData implements java.io.Serializable{
	public static final long serialVersionUID = 1l;
	public int iNumberYears;
	public int iMonths;
	public Constants.Type enumType;
	public SortedMap<String,IncludedAccount> mapIncludedAccounts;
	public String strDefaultExpense;
	public String strDefaultIncome;
	public String strBudget;
	public SortedMap<String,IncludedBudget> mapExpenseBudgets;
	public SortedMap<String,IncludedBudget> mapIncomeBudgets;
	public SortedMap<String,IncludedTransfer> mapIncludedTransfers;
	public SortedMap<String,IncludedReminder> mapIncludedReminders;
	public SortedMap<String,IncludedSecurity> mapIncludedSecurities;
	public int iActual;
	public boolean bLastMonth;
	public double dRPI;

}
