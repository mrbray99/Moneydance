package com.moneydance.modules.features.forecaster;

import java.util.SortedMap;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.Txn;
import com.infinitekind.moneydance.model.TxnSearch;
import com.infinitekind.moneydance.model.TxnSet;
public class AllTransactions implements TxnSearch{
	/*
	 * Moneydance data
	 */
	private AccountBook abCurrentBook;
	private TxnSet stlTrans;
	/*
	 * Program Data
	 */
	private ForecastParameters objParams;
	private int iStartDate;
	private int iActualDate;
	private SortedMap<String,Account> mapAccounts;

	public AllTransactions (ForecastParameters objParamsp) {
		objParams = objParamsp;
		abCurrentBook = Main.getContxt().getCurrentAccountBook();
		iStartDate = objParams.getStartDate();
		iActualDate = objParams.getActualDate();
		mapAccounts = objParams.getAccounts();
		stlTrans = abCurrentBook.getTransactionSet().getTransactions(this);
	}
	@Override
	public boolean matches(Txn txnParm) {
		if ( txnParm.getDateInt() < iStartDate ||
			 txnParm.getDateInt() > iActualDate)
			return false;
		if (txnParm.getAccount() == null)
			return false;
		if (mapAccounts.containsKey(txnParm.getAccount().getFullAccountName()))
			return true;
		return false;
	}

	@Override
	public boolean matchesAll() {
		return false;
	}
	public TxnSet getTransactions() {
		return stlTrans;
	}
}
