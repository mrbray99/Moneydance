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

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.infinitekind.moneydance.model.CurrencySnapshot;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.util.DateUtil;
import com.moneydance.apps.md.controller.Util;
import com.moneydance.modules.features.mrbutil.MRBDebug;


public class MyTableModel extends DefaultTableModel {
	private Parameters params;
    private SortedMap<String,Double> newPricesTab;
    private SortedMap<String,Integer> newTradeDate;
    private SortedMap<String,String> tradeCurr;
    private SortedMap<String,ExtraFields> volumes;
    private SortedMap<String,Double> quotePrice;
    private SortedMap<String,Integer>accountSource;
    private SortedMap<String,String>selectedExchanges;
    private List<Entry<String,CurrencyType>> listCurrencies;
	private SortedMap<String,List<HistoryPrice>> historyTab;
 	private List<Entry<String,Integer>> listDates;
    private List<Entry<String,DummyAccount>> listAccounts;
    private List<Entry<String,Double>> listCurrent;
    private List<String> errorTickers;
    private CurrencyType baseCurrency;
    private DecimalFormat dfNumbers;
    private DecimalFormatSymbols dfSymbols;
	Double multiplier;
	private boolean[] arrSelect;
	private String [] arrSource;
	private MRBDebug debugInst = Main.debugInst;
	private static String[] arrColumns = {"Select","Ticker","Exch Mod","Name","Source","Last Price","Price Date","New Price","% chg","Trade Date","Trade Currency","Volume"};

	public MyTableModel(Parameters paramsp,SortedMap<String,Double> pricesp,
			SortedMap<String,Integer> newTradeDatep,
			SortedMap<String, Double> currentp,
			SortedMap<String,Integer> datesp, 
			SortedMap<String,DummyAccount> accountsp,
			SortedMap<String,CurrencyType> currenciesp,
			SortedMap<String,String> tradeCurrp,
			SortedMap<String,Double> quotePricep,
			SortedMap<String,String>selectedExchangesp,
			SortedMap<String,ExtraFields>volumesp){
		super();
		params = paramsp;
		newPricesTab = pricesp;
		newTradeDate = newTradeDatep;
		quotePrice = quotePricep;
		selectedExchanges = selectedExchangesp;
		volumes = volumesp;
		listCurrent = new ArrayList<Entry<String,Double>>(currentp.entrySet());
		listDates = new ArrayList<Entry<String,Integer>>(datesp.entrySet());
		listAccounts = new ArrayList<Entry<String,DummyAccount>>(accountsp.entrySet());
		listCurrencies = new ArrayList<Entry<String, CurrencyType>>(currenciesp.entrySet());
		tradeCurr = tradeCurrp;
		accountSource = params.getAccountsMap();
		arrSource = params.getSourceArray();
		arrSelect = new boolean[currentp.size()];
		for (int i=0;i<arrSelect.length;i++)
			arrSelect[i] = false;
		baseCurrency = Main.context.getCurrentAccountBook()
				.getCurrencies()
				.getBaseType();
		resetNumberFormat ();

	}
	private void resetNumberFormat() {
		multiplier = Math.pow(10.0,Double.valueOf(params.getDecimal()));
		String strDec = "#,##0.00";
		int iDec = params.getDecimal()-2;
		if (iDec > 0) {
			for (int i=0;i<iDec;i++)
				strDec += "0";
		}
		debugInst.debug("MyTableModel", "MyTableModel", MRBDebug.DETAILED,
				"Decimal Format "+strDec);
	
		dfSymbols = new DecimalFormatSymbols();
		dfSymbols.setDecimalSeparator(Main.decimalChar);
		if (Main.decimalChar == ',')
			dfSymbols.setGroupingSeparator('.');
		dfNumbers = new DecimalFormat(strDec,dfSymbols);

	}
	public void resetData(SortedMap<String, Double> mapCurrentp,
			SortedMap<String,Integer> mapDatesp, SortedMap<String,DummyAccount> mapAccountsp,
			SortedMap<String,CurrencyType> mapCurrenciesp,
			SortedMap<String, Double>newPricesTabp,
			SortedMap<String,Integer> newTradeDatep,
			SortedMap<String,String> tradeCurrp,
			SortedMap<String,Double> quotePricep,
			SortedMap<String,ExtraFields>volumesp){
		listCurrent = new ArrayList<Entry<String,Double>>(mapCurrentp.entrySet());
		listDates = new ArrayList<Entry<String,Integer>>(mapDatesp.entrySet());
		listAccounts = new ArrayList<Entry<String,DummyAccount>>(mapAccountsp.entrySet());
		listCurrencies = new ArrayList<Entry<String, CurrencyType>>(mapCurrenciesp.entrySet());
		tradeCurr = tradeCurrp;
		newPricesTab = newPricesTabp;
		newTradeDate = newTradeDatep;
		quotePrice = quotePricep;
		volumes = volumesp;
		arrSelect = new boolean[mapCurrentp.size()];
		for (int i=0;i<arrSelect.length;i++)
			arrSelect[i] = false;
		resetNumberFormat();
	}
	public void resetHistory(SortedMap<String,List<HistoryPrice>> historyTabp) {
		historyTab = historyTabp;
	}
	public void resetPrices() {
		for (Entry<String,Double>priceEntry : newPricesTab.entrySet() ){
			newPricesTab.replace(priceEntry.getKey(), 0.0);
			newTradeDate.replace(priceEntry.getKey(), 0);
			tradeCurr.replace(priceEntry.getKey(), "");
			volumes.replace(priceEntry.getKey(),null);
		}
		this.fireTableDataChanged();
	}
	@Override
	public int getRowCount() {
		int iRows;
		if (listAccounts == null)
			iRows = 0;
		else
			iRows = listAccounts.size();
		if (!(listCurrencies == null))
			iRows +=listCurrencies.size();
		return iRows;
	}
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class getColumnClass(int c){
		if (c == 0)
			return Boolean.class;
		return String.class;
	}

		@Override
	public int getColumnCount() {
			return arrColumns.length;
	}	
	@Override
	public String getColumnName(int c) {
		return arrColumns[c];
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		CurrencyType cellCur;
		CurrencyType relativeCur;
		String strKey;
		int iCurrentRow;
		switch (columnIndex) {
		/*
		 * Select
		 */
		case 0:
			return  arrSelect[rowIndex];
		/*
		 * Ticker
		 */
		case 1:
			if(rowIndex > listAccounts.size())
				iCurrentRow = rowIndex - listAccounts.size();
			else
				iCurrentRow = rowIndex;
			strKey = listCurrent.get(rowIndex).getKey();
			if (strKey.startsWith(Constants.CURRENCYID)) {
				if (strKey.length()>3)
					return strKey.substring(3);
			}
			return strKey;
		/*
		 * Exchange
		 */
		case 2:
			strKey = listCurrent.get(rowIndex).getKey();
			if (selectedExchanges.containsKey(strKey)) {
				return selectedExchanges.get(strKey);
			}
			return " ";
			
		/*
		 * Account Name
		 */
		case 3:
			strKey = listCurrent.get(rowIndex).getKey();
			if (strKey.startsWith(Constants.CURRENCYID)) {
					iCurrentRow = rowIndex - listAccounts.size();
					return "Cur:"+listCurrencies.get(iCurrentRow).getValue().getName(); 
				}
			return  listAccounts.get(rowIndex).getValue().getAccountName();
		case 4:
			/*
			 * Price Source
			 */
			strKey = listCurrent.get(rowIndex).getKey();
			if (accountSource.containsKey(strKey)) {
				return arrSource [accountSource.get(strKey)];
			}
			return arrSource [0];
			/*
			 * last price
			 */
		case 5:
			strKey = listCurrent.get(rowIndex).getKey();
			if (strKey.startsWith(Constants.CURRENCYID)) {
				return dfNumbers.format(listCurrent.get(rowIndex).getValue()); 
			}
			cellCur = baseCurrency;
			Double dValue = listCurrent.get(rowIndex).getValue();
			if (listAccounts.get(rowIndex).getValue().getDifferentCur()) {
				relativeCur = listAccounts.get(rowIndex).getValue().getRelativeCurrencyType();
//				Double dViewRate = CurrencyUtil.getUserRate(baseCurrency,  ctRelative);
//				dValue *= dViewRate;
				cellCur = relativeCur;
			}
			dValue = Math.round(dValue*multiplier)/multiplier;
			return  cellCur.getPrefix()+dfNumbers.format(dValue)+cellCur.getSuffix();
		/*
		 * last Price Date
		 */
		case 6:
			if (errorTickers != null && errorTickers.contains(listCurrent.get(rowIndex).getKey()))
				return Main.cdate.format(listDates.get(rowIndex).getValue())+"*";
			return  Main.cdate.format(listDates.get(rowIndex).getValue());
			/*
			 * New Price
			 */
		case 7:
			strKey = listCurrent.get(rowIndex).getKey();
			if (newPricesTab.get(strKey)== null)
				return "0"+Main.decimalChar+"0";
			Double newValue = Math.round(newPricesTab.get(strKey)*multiplier)/multiplier;
			return dfNumbers.format(newValue);
			/*
			 * % Change
			 */
		case 8:
			strKey = listCurrent.get(rowIndex).getKey();
			if (newPricesTab.get(strKey) == null)
				return "";
			if (!accountSource.containsKey(strKey) ) {
				return "";
			}
			Double newPrice;
			Double oldPriceValue;
			if (strKey.startsWith(Constants.CURRENCYID)) {
				newPrice = Math.round(newPricesTab.get(strKey)*multiplier)/multiplier;
				oldPriceValue =listCurrent.get(rowIndex).getValue(); 
			}
			else {
				 newPrice = Math.round(newPricesTab.get(strKey)*multiplier)/multiplier;
				cellCur = baseCurrency;
				oldPriceValue = listCurrent.get(rowIndex).getValue();
				if (listAccounts.get(rowIndex).getValue().getDifferentCur()) {
					relativeCur = listAccounts.get(rowIndex).getValue().getRelativeCurrencyType();
	//				Double dViewRate = CurrencyUtil.getUserRate(baseCurrency,  ctRelative);
	//				dValue *= dViewRate;
					cellCur = relativeCur;
				}
				oldPriceValue = Math.round(oldPriceValue*multiplier)/multiplier;
			}
			Double perChg = (oldPriceValue-newPrice)/oldPriceValue*-100.0;
			if (perChg == -0.0)
				perChg = 0.0;
			return dfNumbers.format(perChg);
			/*
			 * Trade Date
			 */
		case 9:
			String key = listCurrent.get(rowIndex).getKey();
			if (!newTradeDate.containsKey(key))
				return "";
			if (newTradeDate.get(key)==0)
				return "";
			String dateString = Main.cdate.format(newTradeDate.get(key));
			if (historyTab !=null && historyTab.containsKey(key))
				dateString +="++";
			return dateString;
			/*
			 * trade currency
			 */
		case 10:
			strKey = listCurrent.get(rowIndex).getKey();
			if (!tradeCurr.containsKey(strKey))
				return "";
			if (strKey.startsWith(Constants.CURRENCYID)) {
				iCurrentRow = rowIndex - listAccounts.size();
				return tradeCurr.get(listCurrent.get(rowIndex).getKey()); 
			}
			String quoteCurrency = tradeCurr.get(strKey);
			if (quoteCurrency.equals(""))
				return "";
			CurrencyType securityCurrency = listAccounts.get(rowIndex).getValue().getRelativeCurrencyType();
			if (securityCurrency == null)
				return tradeCurr.get(listCurrent.get(rowIndex).getKey());
			if (!securityCurrency.getIDString().equals(quoteCurrency)) {
				return tradeCurr.get(strKey)+"("+dfNumbers.format(quotePrice.get(listCurrent.get(rowIndex).getKey()))+")";
			}
			return tradeCurr.get(listCurrent.get(rowIndex).getKey());
			/*
			 * Volume
			 */
		default :
			strKey = listCurrent.get(rowIndex).getKey();
			if (volumes != null && volumes.containsKey(strKey)) {
				if (volumes.get(strKey) != null && volumes.get(strKey).getVolume() > 0L) {
					return Long.toString(volumes.get(strKey).getVolume());
				}
			}
			return " ";
		}
	}
	@Override
    public boolean isCellEditable(int row, int col) {
 		switch (col) {
 		case 0:
 		case 4:
 		case 7:
 		case 9:
			return true;
		default:
			return false;
 		}
    }
	@Override
	public void setValueAt(Object value, int row, int col){
		DecimalFormat dfNumbers = new DecimalFormat("#0.0000");
		String strKey = listCurrent.get(row).getKey();

		if (value == null)
			return;
		if (col ==0) {
			if (newPricesTab.get(listCurrent.get(row).getKey())== null){
				JOptionPane.showMessageDialog(null, "This line does not have a price");
				return;
			}
			if (dfNumbers.format(newPricesTab.get(listCurrent.get(row).getKey())).equals("0.0"))
					return;
			arrSelect [row] = (boolean) value;
		}
		if (col ==4) {
			for (int i=0;i<arrSource.length;i++) {
				if ((String)value == arrSource[i]){
					params.updateAccountSource(strKey, i);
					debugInst.debug("MyTableModel","setValueAt",MRBDebug.DETAILED, "Source updated "+strKey+" "+i);
				}
			}
			if (errorTickers !=null)
				errorTickers.remove(strKey);
		}
		if (col==7){
			String newValue = ((String)value).replace(Main.decimalChar, '.');
			if (newPricesTab.containsKey(strKey))
				newPricesTab.replace(strKey, Double.parseDouble(newValue));
			else
				newPricesTab.put(strKey,Double.parseDouble(newValue));
			if (!newTradeDate.containsKey(strKey)){
				newTradeDate.put(strKey, DateUtil.getStrippedDateInt());
			}
			if (errorTickers !=null)
				errorTickers.remove(strKey);
		}
		if (col ==9) {
			int date = Main.cdate.parseInt((String)value);
			if (newTradeDate.containsKey(strKey))
				newTradeDate.replace(strKey, date);
			else
				newTradeDate.put(strKey, date);
		}
		if (errorTickers !=null)
			errorTickers.remove(strKey);
	}
	public int getNumAccounts() {
		return listAccounts.size();
	}
	/**
	 * Update all non-zero lines
	 */
	public int selectAll(boolean select) {
		int numChanged =0;
		String strKey;
		for (int i=0;i<getRowCount();i++){
			strKey = listCurrent.get(i).getKey();
			if (newPricesTab.get(strKey)!= null && newPricesTab.get(strKey)!=0.0){
				arrSelect[i] = select;
				numChanged++;
			}
		}
		return numChanged;
	}
	/**
	 * Update exchange on all lines
	 */
	public void selectAllExchanges(String exchange) {
		String strKey;
		for (int i=0;i<getRowCount();i++){
			strKey = listCurrent.get(i).getKey();
			if (exchange.isEmpty())
				params.setExchange(strKey, null);
			else
				params.setExchange(strKey, exchange);
		}
		selectedExchanges = params.getExchangeSelect();
		return;
	}
	public String getRowType(int row){
		if (row<0 || row >= listCurrent.size())
			return "";
		String strKey = listCurrent.get(row).getKey();
		if (strKey.startsWith(Constants.CURRENCYID)) 
			return Constants.CURRENCYTYPE;
		else
			return Constants.STOCKTYPE;

	}
	/**
	 * Updates the sources to the same as the given row
	 * @param row
	 * @param col
	 */
	 public void updateAllSources(int source){
		 int i=0;
		   for (Entry<String, Double> entry : listCurrent) {
			    String key = entry.getKey();
			    if (i>= listAccounts.size() && source == Constants.FTHISTINDEX)
			    	params.updateAccountSource(key, Constants.FTINDEX);
			    else	    	
			    	params.updateAccountSource(key, source);
				debugInst.debug("MyTableModel","setValueAt",MRBDebug.DETAILED, "Source updated "+key+" "+source);
				i++;
			}
		   accountSource = params.getAccountsMap(); 
		   this.fireTableDataChanged();
	 }
	 /*
	  * add list of tickers in error on automatic run
	  */
	 public void addErrorTickers(List<String> errorTickersp){
		 errorTickers = errorTickersp;
	 }
	 /*
	  * add list of tickers in error on automatic run
	  */
	 public void clearErrorTickers(){
		 errorTickers = null;
	 }	/*
	 * update line
	 */
	public boolean updateLine(int iRow, BufferedWriter exportFile, boolean exportOnly) {
		DummyAccount acct;
		CurrencyType ctTicker;
		CurrencyType ctRelative = null;
		double dRate;
		double dViewRate=1.0;
		double dCurRate = 1.0;
		CurrencySnapshot objSnap;
		if (!arrSelect[iRow])
			return false; // line not selected - do not process
		/*
		 * If no rows > no of accounts it must be a currency 
		 */
		if (iRow >= listAccounts.size()){
			return updateCurrency(iRow,exportFile,exportOnly);
		}
		String ticker = listCurrent.get(iRow).getKey();
		int tradeDate = newTradeDate.get(ticker);
		if (newPricesTab.get(ticker)== null)
			return false; // no new price for line - do not process
		acct = listAccounts.get(iRow).getValue();
		ctTicker = acct.getCurrencyType();
		if (ctTicker == null)
			return false;  // no currency - do not process
		/*
		 * find date of last update
		 */
		List<CurrencySnapshot> snapShots = ctTicker.getSnapshots();
		int lastDate =0;
		if (!snapShots.isEmpty())
			lastDate = snapShots.get(0).getDateInt();
		ctRelative = getRelativeCurrency(ctTicker);
		/*
		 * assume displayed price is in security currency
		 * with MD 2019 store straight
		 */
		if(newPricesTab.get(ticker)==null)
			dRate = 0.0;
		else {
			dRate = newPricesTab.get(ticker);
			dViewRate = 1.0;
			dRate = dRate*dViewRate*dCurRate;
			if (exportFile != null) {
				String line = ticker+","+acct.getAccountName()+","+dRate+","+Main.cdate.format(tradeDate)+","+volumes.get(ticker).getVolume()+"\r\n";
				try {
					exportFile.write(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Double multiplier = Math.pow(10.0,Double.valueOf(params.getDecimal()));
			dRate = Math.round(dRate*multiplier)/multiplier;
			dRate =1/Util.safeRate(dRate);
		}
		if (exportOnly)
			return true;
		debugInst.debug("MyTableModel", "updateLine", MRBDebug.DETAILED, "cummulative price "+dRate);
		ctTicker.setEditingMode();
		objSnap = ctTicker.setSnapshotInt(tradeDate,  dRate, ctRelative);
		if(params.getAddVolume()) {
			if (volumes.containsKey(ticker)) {
				ExtraFields fields = volumes.get(ticker);
				objSnap.setDailyVolume(fields.getVolume());
				if (fields.getHigh() != 0.0) {
					Double rate = fields.getHigh();
					Double viewRate = 1.0;
					rate = rate*viewRate*dCurRate;
					Double multiplier = Math.pow(10.0,Double.valueOf(params.getDecimal()));
					rate = Math.round(rate*multiplier)/multiplier;
					rate =1/Util.safeRate(rate);
					objSnap.setDailyHigh(rate);
				}
				if (fields.getLow() != 0.0) {
					Double rate = fields.getLow();
					Double viewRate = 1.0;
					rate = rate*viewRate*dCurRate;
					Double multiplier = Math.pow(10.0,Double.valueOf(params.getDecimal()));
					rate = Math.round(rate*multiplier)/multiplier;
					rate =1/Util.safeRate(rate);
					objSnap.setDailyLow(rate);
				}
			}
		}
		long txnLongDate = DateUtil.convertIntDateToLong(tradeDate).getTime();
		boolean updateCurrentPrice = ctTicker.getLongParameter("price_date", 0) <= txnLongDate;
		if(updateCurrentPrice && tradeDate >= lastDate) {
		  ctTicker.setRate(Util.safeRate(dRate), ctRelative);
		  ctTicker.setParameter("price_date", Math.min(System.currentTimeMillis(), txnLongDate));
		}
		objSnap.syncItem();
		ctTicker.syncItem();
		arrSelect[iRow] = false;
		if (historyTab != null && historyTab.containsKey(ticker)) {
			List<HistoryPrice>historyList = historyTab.get(ticker);
			ctTicker.setEditingMode();
			for (HistoryPrice priceItem : historyList) {
				dRate = priceItem.getPrice();
				dViewRate = 1.0;
				dRate = dRate*dViewRate*dCurRate;
				Double multiplier = Math.pow(10.0,Double.valueOf(params.getDecimal()));
				dRate = Math.round(dRate*multiplier)/multiplier;
				dRate =1/Util.safeRate(dRate);
				objSnap = ctTicker.setSnapshotInt(priceItem.getDate(),  dRate, ctRelative);
				if (params.getAddVolume()) {
					objSnap.setDailyVolume(priceItem.getVolume());
					if (priceItem.getHighPrice() != 0.0) {
						Double rate = priceItem.getHighPrice();
						Double viewRate = 1.0;
						rate = rate*viewRate*dCurRate;
						rate = Math.round(rate*multiplier)/multiplier;
						rate =1/Util.safeRate(rate);
						objSnap.setDailyHigh(rate);
					}
					if (priceItem.getLowPrice() != 0.0) {
						Double rate = priceItem.getLowPrice();
						Double viewRate = 1.0;
						rate = rate*viewRate*dCurRate;
						rate = Math.round(rate*multiplier)/multiplier;
						rate =1/Util.safeRate(rate);
						objSnap.setDailyLow(rate);
					}
				}
				objSnap.syncItem();
			}
			ctTicker.syncItem();
		}
		return true;
	}
	/*
	 * Update line for currency
	 */
	public boolean updateCurrency(int iRow, BufferedWriter exportFile, boolean exportOnly) {
		CurrencyType ctTicker;
		Double dRate;
		CurrencySnapshot objSnap;
		if (!arrSelect[iRow])
			return false; // line not selected - do not process
		if (!newPricesTab.containsKey(listCurrent.get(iRow).getKey()))
			return false; // no new price for line - do not process
		int tradeDate = newTradeDate.get(listCurrent.get(iRow).getKey());
		ctTicker = listCurrencies.get(iRow-listAccounts.size()).getValue();
		if (ctTicker == null)
			return false;  // no currency - do not process
		if(newPricesTab.get(listCurrent.get(iRow).getKey())==null)
			dRate = 0.0;
		else
			dRate = newPricesTab.get(listCurrent.get(iRow).getKey());
		if (exportFile != null) {
			String line = ctTicker.getIDString()+","+ctTicker.getName()+","+dRate+","+Main.cdate.format(tradeDate)+",0\r\n";
			try {
				exportFile.write(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
		if (exportOnly)
			return true;
		List<CurrencySnapshot> snapShots= ctTicker.getSnapshots();
		int lastDate =0;
		
		if (!snapShots.isEmpty())
			lastDate = snapShots.get(0).getDateInt();
		ctTicker.setEditingMode();
		objSnap = ctTicker.setSnapshotInt(tradeDate,dRate);
		long txnLongDate = DateUtil.convertIntDateToLong(tradeDate).getTime();
		boolean updateCurrentPrice = ctTicker.getLongParameter("price_date", 0) <= txnLongDate;
		if(updateCurrentPrice && tradeDate >= lastDate) {
		  ctTicker.setRate(Util.safeRate(dRate), null);
		  ctTicker.setParameter("price_date", Math.min(System.currentTimeMillis(), txnLongDate));
		}
		objSnap.syncItem();
		arrSelect[iRow] = false;
		String ticker = listCurrent.get(iRow).getKey();
		if (historyTab != null && historyTab.containsKey(ticker)) {
			List<HistoryPrice>historyList = historyTab.get(ticker);
			ctTicker.setEditingMode();
			for (HistoryPrice priceItem : historyList) {
				dRate = priceItem.getPrice();
				objSnap = ctTicker.setSnapshotInt(priceItem.getDate(),  dRate);
				objSnap.syncItem();
			}
		}
		ctTicker.syncItem();
		return true;
	}
	  /** 
	   * Get the currency that the given security is priced relative to, if it's not
	   * the base currency
	   */
	  static CurrencyType getRelativeCurrency(CurrencyType curr) {
	    String relCurrID = curr.getParameter(CurrencyType.TAG_RELATIVE_TO_CURR);
	    return relCurrID == null ? null : curr.getBook().getCurrencies().getCurrencyByIDString(relCurrID);
	  }
	  
	  /** Get the most up-to-date price we have for the given currency */
//		static Double getLastPrice(CurrencyType curr) {
//		  CurrencyType relativeCurrency = getRelativeCurrency(curr);
//	    if (relativeCurrency != null) {
	      // return the rate relative to this specific security's base currency 
//	      return relativeCurrency.getDoubleValue(CurrencyUtil.convertValue(curr.getLongValue(1.0), curr, relativeCurrency));
//	    } else {
	      // return the rate relative to the base currency
//	      return 1/Util.safeRate(curr.getUserRate());
//	    }
//	  }
	  
	/*
	 * Reload current prices
	 */
	public void reloadPrices () {
		DummyAccount acct;
		CurrencyType ctTicker;
		for (int i=0;i<listCurrent.size();i++) {
			acct = listAccounts.get(i).getValue();
	    	ctTicker = acct.getCurrencyType();
	    	/*
	    	 * Get last price entry
	    	 */
	    	if (ctTicker != null) {
	    	  if (!ctTicker.getTickerSymbol().equals("")) {
    			  List<CurrencySnapshot> listSnap = ctTicker.getSnapshots();
	    		  int iSnapIndex = listSnap.size()-1;
		    	  CurrencySnapshot ctssLast = listSnap.get(iSnapIndex);
		    	  if (ctssLast != null) {
		    		  listCurrent.get(i).setValue(1.0/ctssLast.getRate());
		    		  }
	    		  }
	    	  }

		}
	}
}
