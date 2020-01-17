package com.moneydance.modules.features.dataextractor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.SplitTxn;
import com.infinitekind.moneydance.model.Txn;

public class SelectionItem implements Serializable {
	private transient List<Account> listCategoryMap;
	
	/**
	 * Items to be saved
	 */
	private List<String> listCategories;
	private boolean categoriesIncluded = false;
	private boolean selectIncome=false;
	private boolean selectExpense=false;
	private Integer fromDate;
	private Integer toDate;
	private boolean status=false;
	private boolean cleared=false;
	private boolean reconciling=false;
	private boolean uncleared=false;
	private boolean cheques=false;
	private String fromCheque;
	private String toCheque;
	private List<String> listTags;
	private boolean selTag=false;

	public SelectionItem () {
		if (listCategories == null)
			listCategories = new ArrayList<String>();
		if (listCategoryMap == null)
			listCategoryMap = new ArrayList<Account>();
		if (listTags == null)
			listTags = new ArrayList<String>();
	}
	public List<String> getCategories() {
		return listCategories;
	}
	public List<Account> getCategoryMap() {
		return listCategoryMap;
	}
	public Boolean includeCategories(){
		return categoriesIncluded;
	}
	public Boolean includeIncome(){
		return selectIncome;
	}
	public Boolean includeExpense() {
		return selectExpense;
	}
	public Boolean includeStatus() {
		return status;
	}
	public Boolean isCleared() {
		return cleared;
	}
	public Boolean isReconciling() {
		return reconciling;
	}
	public Boolean isUncleared () {
		return uncleared;
	}
	public Boolean includeCheques(){
		return cheques;
	}
	public String getFromCheque() {
		return fromCheque;
	}
	public String getToCheque () {
		return toCheque;
	}
	public List<String> getTags() {
		return listTags;
	}
	public Boolean getSelTag() {
		return selTag;
	}
	public void addCategory (Account acctCategory){
		if (listCategories == null)
			listCategories = new ArrayList<String>();
		if (listCategoryMap == null)
			listCategoryMap = new ArrayList<Account>();
		listCategories.add(acctCategory.getAccountName());
		listCategoryMap.add(acctCategory);
	}
	public void addCategories (List<Account> listCategoriesp){
		if (listCategories == null)
			listCategories = new ArrayList<String>();
		if (listCategoryMap == null)
			listCategoryMap = new ArrayList<Account>();
		listCategoryMap.addAll(listCategoriesp);
		for (Account acct :listCategoriesp){
			listCategories.add(acct.getAccountName());
		}
	}
	public void addDates (Integer iFrom, Integer iTo) {
		fromDate = iFrom;
		toDate = iTo;
	}
	public void setCategories(Boolean categoriesp){
		categoriesIncluded = categoriesp;
	}
	public void setIncome(Boolean selectIncomep){
		selectIncome = selectIncomep;
	}
	public void setExpense(Boolean selectExpensep){
		selectExpense = selectExpensep;
	}
	public void setCheques (boolean chequesp){
		cheques = chequesp;
	}
	public void setStatus (boolean statusp){
		status = statusp;
	}
	public void setCleared (boolean bClearedp){
		cleared = bClearedp;
	}
	public void setReconciling (boolean reconcilingp){
		reconciling = reconcilingp;
	}
	public void setUncleared (boolean unclearedp){
		uncleared = unclearedp;
	}
	public void setFromCheque(String fromp){
		fromCheque = fromp;
	}
	public void setToCheque(String top){
		toCheque = top;
	}
	public void addTag (String strTag){
		if (listTags == null)
			listTags = new ArrayList<String>();
		listTags.add(strTag);
	}
	public void addTags (List<String> listTagsp){
		if (listTags == null)
			listTags = new ArrayList<String>();
		listTags.addAll(listTagsp);
	}
	public void setCategories(Account rootAcct){
		listCategoryMap = new ArrayList<Account> (); 
		for (String strCategory : listCategories) {
			Account acct = rootAcct.getAccountByName(strCategory);
			if (acct != null)
				listCategoryMap.add(acct);
		}
	}
	public void setSelTag(Boolean selTagp){
		selTag = selTagp;
	}
	public void resetCategories (List<Account> listCategoriesp){
		listCategories = null;
		listCategoryMap = null;
		addCategories(listCategoriesp);
	}
	public void resetTags(List<String> listTagsp){
		listTags = null;
		addTags(listTagsp);
	}
	public boolean isSelected(Txn trans) {
		/*
		 * Categories
		 */
		if (categoriesIncluded && listCategoryMap.size() > 0) {
			if (trans instanceof SplitTxn) {
				if (!listCategoryMap.contains(trans.getAccount()))
					return false;
			}
			else {
				int otherCount = trans.getOtherTxnCount();
				boolean catFound = false;
				for (int i=0;i<otherCount;i++){
					Txn otherTxn = trans.getOtherTxn(i);
					if (listCategoryMap.contains(otherTxn.getAccount())){
						catFound = true;
						break;
					}
				}
				if (!catFound)
					return false;
			}
		}
		if (cheques) {
			if (trans.getCheckNumber().compareTo(fromCheque)>0)
				return false;
			if (trans.getCheckNumber().compareTo(toCheque)<0)
				return false;
		}
		if (status){
			AbstractTxn.ClearedStatus txnStatus = trans.getClearedStatus();
			switch (txnStatus){
			case CLEARED :
				if (!cleared)
					return false;
				break;
			case RECONCILING :
				if (!reconciling)
					return false;
				break;
			case UNRECONCILED :
				if (!uncleared) 
					return false;
			}
		}
		if(selTag){
			List<String>tags = trans.getKeywords();
			boolean tagFound = false;
			for (String tag : tags) {
				if(listTags.contains(tag)){
					tagFound = true;
					break;
				}
			}
			if (!tagFound)
				return false;

		}
		return true;
	}
}
