/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
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
 * 
 */
package com.moneydance.modules.features.securityquoteload;

import java.util.ArrayList;
import java.util.TreeMap;

import com.moneydance.modules.features.mrbutil.MRBDebug;

public class AutomaticRun extends loadPricesWindow{

		public AutomaticRun (Main mainp, int runtype)
		{
			super(mainp,runtype);
			errorsFound = false;
			errorTickers = new ArrayList<>();
			main = mainp;
			params = new Parameters();
			Main.params=params;
			/*
			 * set up internal tables
			 */
			newPricesTab = new TreeMap<> ();
			newTradeDate = new TreeMap<>();
			currentPriceTab = new TreeMap<> ();
			datesTab = new TreeMap<> ();
			accountsTab = new TreeMap<> ();
			currencyTab = new TreeMap<> ();
			tickerStatus = new TreeMap<> ();
			tradeCurr = new TreeMap<>();
			quotePrice = new TreeMap<>();
			volumes = new TreeMap<>();
			pseudoCurrencies = params.getPseudoCurrencies();
			selectedExchanges = params.getExchangeSelect();
			/*
			 * Load base accounts and currencies
			 */
			loadAccounts(Main.context.getRootAccount());
			baseCurrency = Main.context.getCurrentAccountBook()
					.getCurrencies()
					.getBaseType();
			baseCurrencyID = baseCurrency.getIDString();
			if(params.getCurrency() || params.getZero()){
				loadCurrencies(Main.context.getCurrentAccountBook());
			}
			pricesModel = new MyTableModel (params,newPricesTab,
					newTradeDate,
					currentPriceTab,
					datesTab,
					accountsTab,
					currencyTab,
					tradeCurr,
					quotePrice,
					selectedExchanges,
					volumes);
			pricesDisplayTab = new MyTable (params,pricesModel,tickerStatus);
			debugInst.debug("AutomaticRun", "AutomaticRun", MRBDebug.DETAILED, "get Prices");
			switch (runtype){
				case Constants.SECAUTORUN :
					currencyOnly = false;
					securityOnly = true;
					break;
				case Constants.CURAUTORUN :
					currencyOnly = true;
					securityOnly = false;
					break;
				case Constants.BOTHAUTORUN :
					currencyOnly = false;
					securityOnly = false;
					break;
			}
			getPrices();

		}

}
