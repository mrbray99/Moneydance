package com.moneydance.modules.features.forecaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.TxnSet;
import com.moneydance.modules.features.mrbutil.MRBDebug;


public class Transactions{
	private List<AnalysisTxn> listTxns;
	private ForecastParameters objParams;
	private Map <String, Account> mapSelectedAccounts;
	private MRBDebug objDebug = MRBDebug.getInstance();
	

	public Transactions (ForecastParameters objParamsp) {
		listTxns = new ArrayList<AnalysisTxn>();
		objParams = objParamsp;
		mapSelectedAccounts = objParams.getSelectedAccts();
		
	}
	public int copyInto (TxnSet txnSet) {
		int iCount = 0;
		for (int i=0;i<txnSet.getSize();i++){
			AbstractTxn txn = txnSet.getTxn(i);
			if (mapSelectedAccounts.get(txn.getAccount().getFullAccountName()) != null){
				AnalysisTxn objTxn = new AnalysisTxn();
				objTxn.setAcct(txn.getAccount());
				objTxn.setDate(txn.getDateInt());
				objTxn.setAmount(txn.getValue());
				listTxns.add(objTxn);
				iCount++;
			}
		};
		return iCount;
	}
	public void add(AnalysisTxn txn) {
		if (txn.getAcct() == null)
			objDebug.debug("Transactions","add",MRBDebug.DETAILED, "No account. Amount "+txn.getAmount());
		else {
			if (txn.getAcct().getFullAccountName() == null) {
				objDebug.debug("Transactions","add",MRBDebug.DETAILED, "No account name. Type "+txn.getAcct().getAccountType().name());
			}
		}
		listTxns.add(txn);
	}
	public List<AnalysisTxn> getSortedList() {
		Collections.sort(listTxns);
		return listTxns;
	}	
}
