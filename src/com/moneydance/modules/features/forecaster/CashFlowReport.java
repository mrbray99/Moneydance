package com.moneydance.modules.features.forecaster;

import java.awt.Color;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.AccountUtil;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.moneydance.model.DateRange;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBInconsistencyException;
import com.moneydance.modules.features.mrbutil.MRBRecordRow;
import com.moneydance.modules.features.mrbutil.MRBReport;
import com.moneydance.modules.features.mrbutil.MRBReportGenerator;
import com.moneydance.modules.features.mrbutil.MRBReportViewer;

public class CashFlowReport extends MRBReportGenerator {
	/*
	 * Moneydance Data
	 */
	AccountBook objAcctBook;
	CurrencyType ctBase;
	/*
	 * Program Data
	 */
	private ForecastParameters objParams;
	private Transactions txnSet;
	private List<AnalysisTxn> listTxns;
	private SortedMap<String,Account> mapAccounts;
	private SortedMap<String,long[]> mapData;
	private DateRange[] arrDates;
	private int iNumDates;
	private MRBDebug objDebug = MRBDebug.getInstance();
	/*
	 * Report Data
	 */
	private String [] arrColumns;
	private int[] arrValueWidth;
	
	public CashFlowReport (ForecastParameters objParamsp) throws MRBInconsistencyException {
		objParams = objParamsp;
		txnSet = objParams.getGenTxns();
		if (txnSet == null) {
					throw new MRBInconsistencyException(new Throwable(
							"No Transactions Generated"));
		}
		objAcctBook = Main.getContxt().getCurrentAccountBook();
		ctBase = objAcctBook.getCurrencies().getBaseType();
		mapAccounts = objParams.getSelectedAccts();
		arrDates = objParams.getDates();
		iNumDates = arrDates.length;
		mapData = new TreeMap<String,long []>();
		for (Account acct : mapAccounts.values()) {
			long [] arrAmounts = new long[iNumDates];
			if (acct.getCreationDateInt() < arrDates[0].getStartDateInt()) {
				arrAmounts[0] = AccountUtil.getBalanceAsOfDate(Main.getContxt().getCurrentAccountBook(),acct, arrDates[0].getStartDateInt());
				objDebug.debug("CashFlowReport","constructor",MRBDebug.DETAILED, "Start balance "+acct.getFullAccountName()+" "+arrAmounts[0]);
			}
			else {
				for (int i=0;i<iNumDates;i++) {
					if (arrDates[i].containsInt(acct.getCreationDateInt())) {
						if (acct.getAccountType() == AccountType.LOAN) {
							if (acct.getInitialTransfer() == null) {
								arrAmounts[i] = acct.getStartBalance();
							}
							else {
								AbstractTxn txn = acct.getInitialTransfer();
								arrAmounts[i] = txn.getValue();	
							}
						}
						else
						    arrAmounts[i] = acct.getStartBalance();
						objDebug.debug("CashFlowReport","constructor",MRBDebug.DETAILED, "Start balance "+acct.getFullAccountName()+" "+arrAmounts[i]);
						
					}
				}
			}
			mapData.put(acct.getFullAccountName(),arrAmounts);
		}
		mapData.put("Unknown", new long[iNumDates]);
		listTxns = txnSet.getSortedList();
		String strLast = "";
		long [] arrAmounts = new long[iNumDates];
		int iCrntRange = 0;
		for (AnalysisTxn txn : listTxns) {
			if (txn.getAcct() == null) {
				objDebug.debug("CashFlowReport","constructor",MRBDebug.DETAILED,"Blank Account "+txn.toString());
				continue;
			}
			if (mapData.get(txn.getAcct().getFullAccountName()) == null) {
				objDebug.debug("CashFlowReport","constructor",MRBDebug.DETAILED,"Account Name not found "+txn.getAcct().getFullAccountName());
				continue;
			}
			if (!txn.getAcct().getFullAccountName().equals(strLast)) {
				strLast = txn.getAcct().getFullAccountName();
				arrAmounts = mapData.get(strLast);
				if (arrAmounts == null)
					throw new MRBInconsistencyException(new Throwable(
							"Missing bank -"+strLast+"-"));
				iCrntRange = 0;
			}
			int iDate = txn.getDate();
			while (iCrntRange < iNumDates-1 && iDate > arrDates[iCrntRange].getEndDateInt()) 
				iCrntRange++;
			arrAmounts[iCrntRange] += txn.getAmount();
			objDebug.debug("CashFlowReport","constructor",MRBDebug.DETAILED,"Txn "+txn.getAcct().getFullAccountName()+" "+iDate+" "+txn.getAmount());
		}
		/* 
		 * Set up column names
		 */
		arrColumns = new String[iNumDates+1];
		arrColumns[0] = Constants.ACCOUNTHEAD;
		arrValueWidth = new int[iNumDates+1];
		String strColumn;
		for (int i = 0;i<iNumDates;i++) {
			JDateField jdtTemp = new JDateField(Main.getCdate());
			strColumn= "<html>";
			strColumn += jdtTemp.getStringFromDateInt(arrDates[i].getStartDateInt());
			strColumn +="-<br>";
			strColumn += jdtTemp.getStringFromDateInt(arrDates[i].getEndDateInt());
			strColumn +="</html>";
			arrColumns[i+1] = strColumn;
			arrValueWidth[i+1] = 11;
		}
		/*
		 * Now we have the column names create the report itself
		 */
		objReport = new MRBReport(objParams.getFileName(), arrColumns);
		setTitle ("Cash Flow Report");
		JDateField jdtDate = new JDateField(Main.getCdate());
		setSubTitle ("Predicted cash flow from: "+jdtDate.getStringFromDateInt(arrDates[0].getStartDateInt())+" to "+
				jdtDate.getStringFromDateInt(arrDates[iNumDates-1].getEndDateInt()));
		objReport.setRowHeaders(2);
		objReport.setFooter("Cash Flow report {pagenum} of {numpages}");
		setData();
		objReport.setColumnWidth(arrValueWidth);
	}
	private void setData() {
		long [] arrTotals = new long[iNumDates];
		for (Entry<String, long[]> objEntry: mapData.entrySet()) {
			String [] arrValues = new String[iNumDates+1];
			byte [] arrAlign = new byte[iNumDates+1];
			Color [] arrColour = new Color[iNumDates+1];
			Color [] arrColourFG = new Color[iNumDates+1];
			byte [] arrStyle = new byte[iNumDates+1];
			byte [] arrBorder = new byte[iNumDates+1];
			arrValues[0] = objEntry.getKey();	
			arrStyle [0] = MRBReportViewer.STYLE_BOLD;
			arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
			arrColour[0] = Color.WHITE;
			arrColourFG[0] = Color.BLACK;
			arrBorder[0] = MRBReportViewer.BORDER_BOTTOM;
			long[] arrAmounts = objEntry.getValue();
			long lAmount;
			for (int j=1;j<iNumDates+1;j++) {
				if (j>1) {
					lAmount = arrAmounts[j-1] + arrAmounts[j-2];
				}
				else
					lAmount = arrAmounts[j-1];
				arrAmounts[j-1] = lAmount;
				arrColour[j] = arrColour[0];
				if (lAmount < 0)
					arrColourFG[j] = Color.RED;
				else
					arrColourFG[j] = Color.BLACK;
				arrBorder[j] = arrBorder[0];
				arrAlign[j] = MRBReportViewer.ALIGN_RIGHT;
				arrValues[j] = ctBase.formatFancy(lAmount, '.');
				arrTotals[j-1] += lAmount;
			}
			if (arrValues[0].length() + 1 > arrValueWidth[0])
				arrValueWidth[0] = arrValues[0].length()+1;
			for (int j=2;j<iNumDates+1;j++) {
				if (arrValues[j].length() > arrValueWidth[j])
					arrValueWidth[j] = arrValues[j].length();
			}
			objReport.addRow(new MRBRecordRow(arrValues, arrAlign, arrColour, arrColourFG, arrStyle, arrBorder));	
		}
		String [] arrValues = new String[iNumDates+1];
		byte [] arrAlign = new byte[iNumDates+1];
		Color [] arrColour = new Color[iNumDates+1];
		Color [] arrColourFG = new Color[iNumDates+1];
		byte [] arrStyle = new byte[iNumDates+1];
		byte [] arrBorder = new byte[iNumDates+1];
		arrValues[0] = "Total";	
		arrStyle [0] = MRBReportViewer.STYLE_BOLD;
		arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
		arrColour[0] = Color.WHITE;
		arrColourFG[0] = Color.BLACK;
		arrBorder[0] = MRBReportViewer.BORDER_TOP;
		for (int i=0;i<iNumDates;i++){
			arrColour[i+1] = arrColour[0];
			if (arrTotals[i] < 0)
				arrColourFG[i+1] = Color.RED;
			else
				arrColourFG[i+1] = Color.BLACK;
			arrBorder[i+1] = arrBorder[0];
			arrAlign[i+1] = MRBReportViewer.ALIGN_RIGHT;
			arrValues[i+1] = ctBase.formatFancy(arrTotals[i], '.');
		}
		if (arrValues[0].length() + 1 > arrValueWidth[0])
			arrValueWidth[0] = arrValues[0].length()+1;
		for (int j=2;j<iNumDates+1;j++) {
			if (arrValues[j].length() > arrValueWidth[j])
				arrValueWidth[j] = arrValues[j].length();
		}
		objReport.addRow(new MRBRecordRow(arrValues, arrAlign, arrColour, arrColourFG, arrStyle, arrBorder));			
	}
	/*
	 * gets
	 */
	public MRBReport getReport() {
		return objReport;
	}
}
