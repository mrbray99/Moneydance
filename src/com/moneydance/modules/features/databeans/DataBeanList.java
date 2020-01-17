package com.moneydance.modules.features.databeans;

import java.util.ArrayList;

public class DataBeanList {
	private ArrayList<AccountBean> accountList = new ArrayList<>();
	private ArrayList<AccountTypeBean> accountTypeList = new ArrayList<>();
	private ArrayList<BudgetBean> budgetList = new ArrayList<>();
	private ArrayList<AddressBean> addressList = new ArrayList<>();
	private ArrayList<CurrencyBean> currencyList = new ArrayList<>();
	private ArrayList<TransactionBean> transactionList = new ArrayList<>();
	public ArrayList<AccountBean> getAccountBeanList() {
		return accountList;		
	}
	public ArrayList<AccountTypeBean> getAccountTypeBeanList() {
		return accountTypeList;		
	}
	public ArrayList<BudgetBean> getBudgetBeanList() {
		return budgetList;		
	}
	public ArrayList<AddressBean> getAddressBeanList() {
		return addressList;		
	}
	public ArrayList<CurrencyBean> getCurrencyBeanList() {
		return currencyList;		
	}
	public ArrayList<TransactionBean> getTransactionBeanList() {
		return transactionList;		
	}
	public void add (DataBean bean) {
		if (bean instanceof AccountBean)
			accountList.add((AccountBean)bean);
		if (bean instanceof AccountTypeBean)
			accountTypeList.add((AccountTypeBean)bean);
		if (bean instanceof AddressBean)
			addressList.add((AddressBean)bean);
		if (bean instanceof CurrencyBean)
			currencyList.add((CurrencyBean)bean);
		if (bean instanceof BudgetBean)
			budgetList.add((BudgetBean)bean);
		if (bean instanceof TransactionBean)
			transactionList.add((TransactionBean)bean);
	}
	public boolean remove(DataBean bean) {
		if (bean instanceof AccountBean)
			return accountList.add((AccountBean)bean);
		if (bean instanceof AccountTypeBean)
			return accountTypeList.add((AccountTypeBean)bean);
		if (bean instanceof AddressBean)
			return addressList.add((AddressBean)bean);
		if (bean instanceof CurrencyBean)
			return currencyList.add((CurrencyBean)bean);
		if (bean instanceof BudgetBean)
			return budgetList.add((BudgetBean)bean);
		if (bean instanceof TransactionBean)
			return transactionList.add((TransactionBean)bean);
		return false;
	}
	
}
