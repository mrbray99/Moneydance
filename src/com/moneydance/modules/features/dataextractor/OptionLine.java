package com.moneydance.modules.features.dataextractor;

import java.util.List;

public class OptionLine implements java.io.Serializable{
	private List<String> listSelectedAccounts;
	private String fromCheque;
	private String toCheque;
	private List<String> listTags;
	private Boolean expenses;
	private Boolean income;

	public OptionLine () {
	}
	/**
	 * gets
	 */
	public List<String> getAccounts() {
		return listSelectedAccounts;
	}
	public String getFromCheque() {
		return fromCheque;
	}
	public String getToCheque() {
		return toCheque;
	}
	public List<String> getTags() {
		return listTags;
	}
	public Boolean isExpenses() {
		return expenses;
	}
	public Boolean isIncome() {
		return income;
	}
	/**
	 * Sets
	 */
	public void setAccounts(List<String> listAccountsp){
		listSelectedAccounts = listAccountsp;
	}
	public void setFromCheque(String fromChequep){
		fromCheque = fromChequep;
	}
	public void setToCheque(String toChequep){
		toCheque = toChequep;
	}
	public void setTags(List<String> listTagsp){
		listTags = listTagsp;
	}
	public void setExpense(Boolean expensesp){
		expenses = expensesp;
	}
	public void setIncome(Boolean incomep){
		income = incomep;
	}

}
