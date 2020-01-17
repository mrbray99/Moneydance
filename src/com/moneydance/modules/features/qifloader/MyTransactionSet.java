/*
 *  Copyright (c) 2014, 2016, Michael Bray. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of the author may not used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package com.moneydance.modules.features.qifloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.TransactionListener;
import com.infinitekind.moneydance.model.Txn;
import com.infinitekind.moneydance.model.TxnSearch;
import com.infinitekind.moneydance.model.TxnSet;
import com.infinitekind.tiksync.SyncRecord;

public class MyTransactionSet implements TxnSearch, TransactionListener {
	private TxnSet stlTrans;
	private Account acct;
	private Map<Integer,List<AbstractTxn>> mapLines;
	private Parameters objParms;
	private  List<TableListener> listListeners = new ArrayList<TableListener>();
	private SortedSet<SecLine> setLine;
	public MyTransactionSet(Account root, Account acctp, Parameters objParmsp, SortedSet<SecLine> setLinep) {
		acct = acctp;
		objParms = objParmsp;
		stlTrans = Main.tranSet.getTransactions(this);
		setLine = setLinep;
		Main.tranSet.addTransactionListener(this);
		/*
		 * Create map of transactions by date
		 */
		mapLines = new HashMap<Integer, List<AbstractTxn>>();
		for (int i=0;i<stlTrans.getSize();i++) {
			AbstractTxn txnLine = stlTrans.getTxn(i);
			if (mapLines.get(txnLine.getDateInt()) == null) {
				List<AbstractTxn> listTxn = new ArrayList<AbstractTxn>();
				listTxn.add(txnLine);
				mapLines.put(txnLine.getDateInt(), listTxn);
			}
			else {
				List<AbstractTxn> listTxn2 = mapLines.get(txnLine.getDateInt());
				listTxn2.add(txnLine);
			}
		}	
	}
	@Override
	public boolean matches(Txn txnParm) {
		AbstractTxn atTran = (AbstractTxn)txnParm;
		SyncRecord srTags = atTran.getTags();
		if (srTags.getString(Constants.TAGGEN,null) != null) {
			if (txnParm.getParentTxn().getAccount()== acct)
				return true;
		}
		return false;
	}

	@Override
	public boolean matchesAll() {
		return false;
	}
	/*
	 * determine if current line has been generated
	 * 
	 *
	 */
	public void findTransaction (SecLine slTran) {
		/*
		 * check same date
		 */
		List<AbstractTxn> listTxn = mapLines.get(slTran.getDate());
		if (listTxn == null) {
			return;
		}		
		for (AbstractTxn txnLine : listTxn) {
			long lValue = slTran.getValue();
			long lTxnValue = txnLine.getValue();
			lValue = (lValue < 0 ? lValue*-1 : lValue);
			lTxnValue = (lTxnValue < 0 ? lTxnValue*-1 : lTxnValue);
			/*
			 * check the same unsigned value (Moneydance manipulates the amounts)
			 */
			if ( lTxnValue != lValue)
				continue;
			/*
			 * determine if same Transaction Transfer Type
			 */
			SyncRecord srTran = txnLine.getTags();
			String strTag = srTran.getString(Constants.TAGGEN,null);
			if (strTag != null)
				if (!(objParms.isDefined(strTag)))
					continue;
			
			slTran.setProcessed(true);
			AbstractTxn.ClearedStatus csNum = txnLine.getClearedStatus();
			if (csNum.equals(AbstractTxn.ClearedStatus.CLEARED))
				slTran.setCleared(Constants.TXNCLEARED);
			else
				if (csNum.equals(AbstractTxn.ClearedStatus.RECONCILING))
					slTran.setCleared(Constants.TXNRECONCILED);
		}
		return;
	}
	@Override
	public void transactionAdded(AbstractTxn t){
		
	}
	/*
	 * 
	 * transaction modified, check to see if it affects the table
	 */
	@Override
	public void transactionModified(AbstractTxn txnLine){
		long lTxnValue = txnLine.getValue();
		for (SecLine slTran : setLine) {
			long lValue = slTran.getValue();
			lValue = (lValue < 0 ? lValue*-1 : lValue);
			lTxnValue = (lTxnValue < 0 ? lTxnValue*-1 : lTxnValue);
			/*
			 * check the same unsigned value (Moneydance manipulates the amounts)
			 */
			if ( lTxnValue != lValue)
				continue;
			/*
			 * determine if same Transaction Transfer Type
			 */
			SyncRecord srTran = txnLine.getTags();
			String strTag = srTran.getString(Constants.TAGGEN,null);
			if (strTag != null)
				if (!(objParms.isDefined(strTag)))
					continue;
			
			slTran.setProcessed(true);
			AbstractTxn.ClearedStatus csNum = txnLine.getClearedStatus();
			if (csNum.equals(AbstractTxn.ClearedStatus.CLEARED))
				slTran.setCleared(Constants.TXNCLEARED);
			else
				if (csNum.equals(AbstractTxn.ClearedStatus.RECONCILING))
					slTran.setCleared(Constants.TXNRECONCILED);
				else
					slTran.setCleared(" ");
			for (TableListener tabListener : listListeners){
				tabListener.tableChanged();
			}
		}
		
	}
	@Override
	public void transactionRemoved(AbstractTxn t){
		
	}

	public void addListener(TableListener objListener) {
		listListeners.add(objListener);
	}
}
