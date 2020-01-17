/*
 *  Copyright (c) 2014, Michael Bray. All rights reserved.
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
package com.moneydance.modules.features.dataextractor;

import java.util.List;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.ParentTxn;
import com.infinitekind.moneydance.model.SplitTxn;
import com.infinitekind.moneydance.model.Txn;
import com.infinitekind.moneydance.model.TxnSearch;
import com.infinitekind.moneydance.model.TxnSet;

public class MyTransactionSet implements TxnSearch{
	private TxnSet stlTrans;
	private List<Account> accts;
	private Parameters params;
	private List<SelectionItem> options;
	private boolean unionAnd;
	private int startDate;
	private int endDate;
	public MyTransactionSet(Parameters paramsp) {
		params = paramsp;
		options = params.getSelectionList();
		unionAnd = params.isUnionTypeAnd();
		accts = params.getAccounts();
		startDate = params.getStartDate();
		endDate = params.getEndDate();
		stlTrans = Main.context.getCurrentAccountBook().getTransactionSet().getTransactions(this);		
	}
	@Override
	public boolean matches(Txn txnParm) {
		/*
		 * Parent Txn must be in selected accounts, Split Txn must have parent in selected accounts
		 */
		if (txnParm instanceof ParentTxn && !accts.contains(txnParm.getAccount()))
			return false;
		if (txnParm instanceof SplitTxn){
			ParentTxn parent = txnParm.getParentTxn();
			if (!accts.contains(parent.getAccount()))
				return false;
		}
		/*
		 * Txn date must be within dates
		 */
		if (txnParm.getDateInt() < startDate ||
				txnParm.getDateInt() > endDate)
			return false;
		/*
		 * If no options then select. If options determine if any are true and then apply 
		 * and/or
		 */
		int numOptions = 0;
		if (options.size()==0) {
			return true;
		}
		for (SelectionItem option : options){
			if (option.isSelected(txnParm))
				numOptions++;
		}
		if (unionAnd && numOptions >= options.size())
			return true;
		if (!unionAnd && numOptions > 0)
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
