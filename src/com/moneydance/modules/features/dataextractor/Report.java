package com.moneydance.modules.features.dataextractor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.moneydance.model.DateRange;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.MRBPopup;
import com.moneydance.modules.features.mrbutil.MRBRecordRow;
import com.moneydance.modules.features.mrbutil.MRBReport;
import com.moneydance.modules.features.mrbutil.MRBReportGenerator;
import com.moneydance.modules.features.mrbutil.MRBReportViewer;
import com.moneydance.modules.features.mrbutil.MRBViewTransactions;

public class Report extends MRBReportGenerator implements MRBPopup {
	private Parameters params;
	private MRBViewTransactions transViewer;
	private List<String> listCategoryName;
	private List<Account> listCategoryObj;
	private List<long[]> listActData;
	private List<long[]> listCurrentData;
	private List<Integer> listType;
	private long[] arrTotalCurrIncome;
	private long[] arrTotalCurrExpenses;
	private long[] arrTotalCurr;
	private long[] arrTotalActIncome;
	private long[] arrTotalActExpenses;
	private long[] arrTotalAct;
	int [] arrValueWidth;
	private int iPeriods = 0;
	private int iIncrement;
	private int startDate;
	private int endDate;
	private int iIncomeCount = 0;
	private int iExpenseCount = 0;
	private String strBudget;
	private CurrencyType localCur;
	private DateRange[] arrDates;
	private String strColumn = "Date";
	private List<ReportLine> reportLines;
	private static String CURRENT = "    ";
	private String [] arrColumns;

	public Report (Parameters paramsp){
		super();
		params = paramsp;
		Calendar dtTemp;
        localCur = Main.context.getCurrentAccountBook().getCurrencies().getBaseType();
        /*
         * Determine date columns
         */
		startDate = params.getStartDate();
		endDate = params.getEndDate();
		DateRange drTemp = new DateRange(startDate,endDate);
		dtTemp = Calendar.getInstance();
		int iYeart = startDate/10000;
		int iMontht = (startDate - iYeart*10000)/100;
		int iDayt = startDate - iYeart*10000 - iMontht *100;
		dtTemp.set(iYeart,iMontht-1,iDayt);
		arrDates = new DateRange[iPeriods];
		Calendar dtTemp2 = Calendar.getInstance();
		for (int i=0;i<iPeriods;i++) {
			dtTemp2.setTime(dtTemp.getTime());
			if (iIncrement >0)
				dtTemp2.add(Calendar.DAY_OF_YEAR, iIncrement);
			else
				dtTemp2.add(Calendar.MONTH, -iIncrement);
			dtTemp2.add(Calendar.DAY_OF_YEAR, -1);			
			arrDates[i] = new DateRange(dtTemp.getTime(), dtTemp2.getTime());
			dtTemp.setTime(dtTemp2.getTime());
			dtTemp.add(Calendar.DAY_OF_YEAR, 1);	
		}
		/*
		 * Set up lists and arrays
		 */
		listCategoryName = new ArrayList<>();
		listCategoryObj = new ArrayList<>();
		listActData = new ArrayList<>();
		listCurrentData = new ArrayList<>();
		listType = new ArrayList<>();
		arrTotalCurrIncome = new long[iPeriods+1];
		arrTotalCurrExpenses = new long[iPeriods+1];
		arrTotalCurr = new long[iPeriods+1];
		for (int i = 0;i<iPeriods+1;i++) {
			arrTotalCurrIncome[i] = 0;
			arrTotalCurrExpenses[i] = 0;
			arrTotalCurr[i] = 0;
		}
		/* 
		 * Set up column names
		 */
		arrColumns = new String[iPeriods + 3];
		arrColumns[0] = Constants.CATEGORY;
		arrColumns[1] = Constants.TYPE;
		arrValueWidth = new int[iPeriods+3];
		for (int i = 0;i<iPeriods;i++) {
			JDateField jdtTemp = new JDateField(Main.cdate);
			strColumn= "<html>";
			strColumn += jdtTemp.getStringFromDateInt(arrDates[i].getStartDateInt());
			strColumn +="-<br>";
			strColumn += jdtTemp.getStringFromDateInt(arrDates[i].getEndDateInt());
			strColumn +="</html>";
			arrColumns[i+2] = strColumn;
		}
		arrColumns[iPeriods+2] = Constants.TOTALS;
		/*
		 * Now we have the column names create the report itself
		 */
		objReport = new MRBReport(params.getFileName(), arrColumns);
		setTitle ("Budget Report - Build "+Main.strBuild);
		JDateField jdtDate = new JDateField(Main.cdate);
		setSubTitle ("Budget: "+strBudget+" - "+jdtDate.getStringFromDateInt(startDate)+" - "+
				jdtDate.getStringFromDateInt(endDate));
		objReport.setRowHeaders(2);
		objReport.setFooter("Budget report {pagenum} of {numpages}");
		setData();
		arrValueWidth[1] = 5;
		objReport.setColumnWidth(arrValueWidth);
		objReport.setPopup(this);
	}
	/*
	 * Method used to set up the data 
	 */
	private void setData() {
		/*
		 * first generate the data
		 */
		params.generate(Main.context, arrDates);
		arrTotalActIncome = params.getIncomeTotals();
		arrTotalActExpenses = params.getExpensesTotals();
		arrTotalAct = params.getGrandTotals();
		/*
		 * go through the budget lines and add each one to the lists
		 * start with income lines
		 */

		reportLines = params.getAccountsLines();
		for ( ReportLine objLine :reportLines){
			listType.add(Constants.ACCOUNTS);
			listCategoryName.add(objLine.getCategoryName());
			listCategoryObj.add(objLine.getCategory());
			listActData.add(objLine.getTotals());
			/*
			 * get current budget items, add to totals
			 */
			long [] arrTemp = null;
			//ChoicesWindow.objBudget.getCurrentValues(objLine.getCategory(),arrDates);
			listCurrentData.add(arrTemp);
			for (int i=0;i<iPeriods+1;i++) {
				arrTotalCurrIncome[i] += arrTemp[i];
				arrTotalCurr[i] += arrTemp[i];
			}
			iIncomeCount++;
		}
		/*
		 * now the expense lines
		 */
//		listBudgetLines = objParams.getCategoryLines();
		for ( ReportLine objLine :reportLines){
			listType.add(Constants.EXPENSES);
			listCategoryName.add(objLine.getCategoryName());
			listCategoryObj.add(objLine.getCategory());
			listActData.add(objLine.getTotals());
			/*
			 * get current budget items, add to totals
			 */
			long [] arrTemp = null;
			//ChoicesWindow.objBudget.getCurrentValues(objLine.getCategory(),arrDates);
			listCurrentData.add(arrTemp);
			for (int i=0;i<iPeriods+1;i++) {
				arrTotalCurrExpenses[i] += arrTemp[i];
				arrTotalCurr[i] -= arrTemp[i];
			}
			iExpenseCount++;
		}
		/*
		 * Add each row to the report. Format:-
		 *   Income by category
		 *   	Budget
		 *   	Actual
		 *   	Diff
		 *   Income totals
		 *   Expense by category
		 *   	Budget
		 *   	Actual
		 *   	Diff
		 *   Expense totals
		 *   Grand Totals
		 */
		int iRow;
		int iRowType;
		arrValueWidth[0] = 0;
		arrValueWidth[1] = 15;
		for (int i=2; i<iPeriods+3;i++)
			arrValueWidth [i] = 11;
		for (int i=0;i<(iIncomeCount+iExpenseCount)*3+11;i++) {
			/*
			 * set up arrays for ReportRow
			 */
			String [] arrValues = new String[iPeriods+3];
			byte [] arrAlign = new byte[iPeriods+3];
			Color [] arrColour = new Color[iPeriods+3];
			Color [] arrColourFG = new Color[iPeriods+3];
			byte [] arrStyle = new byte[iPeriods+3];
			byte [] arrBorder = new byte[iPeriods+3];
			for (int j=0; j<iPeriods+3;j++) {
				arrBorder[j] = 0;
				switch (j){
				case 0:
					if (i == 0) {
						arrValues[0] = "Income";
						arrStyle [0] = MRBReportViewer.STYLE_BOLD;
						arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
						arrColourFG[0] = Color.BLACK;
						arrBorder[0] = MRBReportViewer.BORDER_BOTTOM;
						break;
					}
					if (i == iIncomeCount*3+1) {
						arrValues[0] = "Total Income";
						arrStyle [0] = MRBReportViewer.STYLE_ITALIC;
						arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
						arrColourFG[0] = Color.BLACK;
						arrBorder[0] = MRBReportViewer.BORDER_TOP;
						break;
					}
						
					if (i == iIncomeCount*3+4) {
						arrValues[0] = "Expenses";
						arrStyle [0] = MRBReportViewer.STYLE_BOLD;
						arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
						arrColourFG[0] = Color.BLACK;
						arrBorder[0] = MRBReportViewer.BORDER_BOTH;
						break;
					}
					if (i == iIncomeCount*3+iExpenseCount*3+5) {
						arrValues[0] = "Total Expenses";
						arrStyle [0] = MRBReportViewer.STYLE_ITALIC;
						arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
						arrColourFG[0] = Color.BLACK;
						arrBorder[0] = MRBReportViewer.BORDER_TOP;
						break;
					}
					if (i == iIncomeCount*3+iExpenseCount*3+8) {
						arrValues[0] =  "Grand Total";
						arrStyle [0] = MRBReportViewer.STYLE_BOLD;
						arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
						arrColourFG[0] = Color.BLACK;
						arrBorder[0] = MRBReportViewer.BORDER_TOP;
						break;
					}
					if (i < iIncomeCount*3+1) {
						iRow = (i-1) - ((i-1)/3) * 3;
						if (iRow ==0) {
							arrValues[0] = (listCategoryName.get((i-1)/3));
							arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
							arrBorder[0] = MRBReportViewer.BORDER_TOP_HALF;
							arrColourFG[0] = Color.BLACK;
							break;
						}
					}
					if (i > iIncomeCount*3+4 && i < iIncomeCount*3 + iExpenseCount*3 +5) {
						iRow = (i-5) - ((i-5)/3) * 3;
						if (iRow ==0) {
							arrValues[0] = (listCategoryName.get((i-5)/3));
							arrAlign[0] = MRBReportViewer.ALIGN_LEFT;
							arrBorder[0] = MRBReportViewer.BORDER_TOP_HALF;
							arrColourFG[0] = Color.BLACK;
							break;
						}
					}
					arrValues[0] = CURRENT;
					if (i < iIncomeCount*3+5)
						iRow = (i-1) - ((i-1)/3) * 3;
					else
						iRow = (i-5) - ((i-5)/3) * 3;			
					switch (iRow) {
					case 0 :
						arrColourFG[0] = Color.BLACK;
						break;
					case 1 : 
						arrColourFG[0] = Color.BLACK;
						break;
					default :
					}
					
					break;
				case 1:
					arrColour[1] = arrColour[0];
					arrBorder[1] = arrBorder[0];
					if (i == 0 || i == iIncomeCount*3+4) {
						arrValues[1] = CURRENT;
						break;
					}
					if (i < iIncomeCount*3+5)
						iRow = (i-1) - ((i-1)/3) * 3;
					else
						iRow = (i-5) - ((i-5)/3) * 3;			
					switch (iRow) {
					case 0 :
						arrValues[1] = "Bud";
						break;
					case 1 : 
						arrValues[1] = "Act";
						break;
					default :
						arrValues[1] = "Diff";
					}
					arrAlign[1] = MRBReportViewer.ALIGN_LEFT;
					break;
				default:
					arrColour[j] = arrColour[0];
					arrColourFG[j] = arrColourFG[0];
					arrBorder[j] = arrBorder[0];
					arrAlign[j] = MRBReportViewer.ALIGN_RIGHT;
					if (i == 0 || i == (iIncomeCount*3)+4) {
						arrValues[j] =  CURRENT;
						break;
					}
					if (i >iIncomeCount*3 && i < iIncomeCount*3+4) {
						switch (i-iIncomeCount*3){
						case 1:
							arrValues[j] =  localCur.formatFancy(arrTotalCurrIncome[j-2],'.');
							break;
						case 2:
							arrValues[j] =  localCur.formatFancy(arrTotalActIncome[j-2],'.');
							break;
						default :
							arrValues[j] =  localCur.formatFancy(arrTotalCurrIncome[j-2]
									-arrTotalActIncome[j-2],'.');
							if (arrTotalCurrIncome[j-2] < arrTotalActIncome[j-2]) {
								}
						}
						break;
					}
					if (i >(iIncomeCount*3 + iExpenseCount*3+4) && i < (iIncomeCount*3 + iExpenseCount*3+8)) {
						switch (i-((iIncomeCount*3 + iExpenseCount*3)+4)){
						case 1:
							arrValues[j] =  localCur.formatFancy(arrTotalCurrExpenses[j-2],'.');
							break;
						case 2:
							arrValues[j] =  localCur.formatFancy(arrTotalActExpenses[j-2],'.');
							break;
						default :
							arrValues[j] =  localCur.formatFancy(arrTotalCurrExpenses[j-2]
									-arrTotalActExpenses[j-2],'.');			
							if (arrTotalCurrExpenses[j-2] < arrTotalActExpenses[j-2]) {
							}
						}
						break;
					}
					if (i >(iIncomeCount*3 + iExpenseCount*3)+7) {
						switch (i-((iIncomeCount*3 + iExpenseCount*3)+7)){
						case 1:
							arrValues[j] =  localCur.formatFancy(arrTotalCurr[j-2],'.');
							if (arrTotalCurr[j-2] < 0) {
							}
							break;
						case 2:
							arrValues[j] =  localCur.formatFancy(arrTotalAct[j-2],'.');
							if (arrTotalAct[j-2] < 0) {
							}
							break;
						default :
							long lBudget = arrTotalCurr[j-2] < 0 ?arrTotalCurr[j-2]*-1:arrTotalCurr[j-2];
							long lActual = arrTotalAct[j-2] < 0 ?arrTotalAct[j-2]*-1:arrTotalAct[j-2];
							long lDiff = lBudget-lActual;
							
							arrValues[j] =  localCur.formatFancy(lDiff,'.');			
							if (lDiff < 0) {
							}
						}
						break;
					}
					if (i < (iIncomeCount*3)+1) {
						iRow = (i-1)/3;
						iRowType = (i-1) - ((i-1)/3) * 3;
					}
					else {
						iRow = (i-5)/3;
						iRowType = (i-5) - ((i-5)/3) * 3;
					}
					switch (iRowType) {
					case 0 :
						arrValues[j] =  localCur.formatFancy(listCurrentData.get(iRow)[j-2],'.');
						break;
					case 1 : 
						arrValues[j] =  localCur.formatFancy(listActData.get(iRow)[j-2],'.');
						break;
					default :
						arrValues[j] =  localCur.formatFancy(listCurrentData.get(iRow)[j-2]
								-listActData.get(iRow)[j-2],'.');
						if ((listCurrentData.get(iRow)[j-2] - listActData.get(iRow)[j-2])<0) {
						}
					}
					arrAlign[j] = MRBReportViewer.ALIGN_RIGHT;
				}
			}
			if (arrValues[0].length() + 1 > arrValueWidth[0])
				arrValueWidth[0] = arrValues[0].length()+1;
			for (int j=2;j<iPeriods+3;j++) {
				if (arrValues[j].length() > arrValueWidth[j])
					arrValueWidth[j] = arrValues[j].length();
			}
			objReport.addRow(new MRBRecordRow(arrValues, arrAlign, arrColour, arrColourFG, arrStyle, arrBorder));
		}
	}
	/*
	 * gets
	 */
	public MRBReport getReport() {
		return objReport;
	}
	/*
	 * pop up menu methods
	 */
	@Override
	public void actionPopup (String strActionp, int iRowp, int iColp){
		int iRow = 0;
		if (iColp < objReport.getRowHeaders())
			return;
		if (iRowp == 0) {
			return;
		}
		if (iRowp > iIncomeCount*3+1 && iRowp < iIncomeCount*3 + 5) {
			return;
		}
		if (iRowp == iIncomeCount*3+iExpenseCount*3+5) {
			return;
		}
		if (iRowp == iIncomeCount*3+iExpenseCount*3+8) {
			return;
		}
		if (iRowp > iIncomeCount*3 + iExpenseCount*3 + 5)
			return;
		if (iRowp < iIncomeCount*3+1) {
			iRow = (iRowp-1) - ((iRowp-1)/3) * 3;
			if (iRow != 1)
				return;
			iRow =(iRowp-1)/3;
		}
		if (iRowp > iIncomeCount*3+4) {
			iRow = (iRowp-5) - ((iRowp-5)/3) * 3;
			if (iRow !=1)
				return;
			iRow = (iRowp-5)/3;
		}		if (strActionp.contains("only")) {
//			objViewTrans = new MRBViewTransactions(false,Main.context.getCurrentAccountBook(), listCategoryObj.get(iRow),
//													objParams.getRollup(),arrDates[iColp-2].getStartDateInt(),arrDates[iColp-2].getEndDateInt());
		}
		else {
//			objViewTrans = new MRBViewTransactions(true,Main.context.getCurrentAccountBook(),listCategoryObj.get(iRow),
//					objParams.getRollup(),arrDates[iColp-2].getStartDateInt(),arrDates[iColp-2].getEndDateInt());
		}	
	}
	@Override
	public String[] getMenuItems(int iRowp, int iColp){
		int iRow = 0;
		if (iColp < objReport.getRowHeaders())
			return null;
		if (iRowp == 0) {
			return null;
		}
		if (iRowp > iIncomeCount*3+1 && iRowp < iIncomeCount*3 + 5) {
			return null;
		}
		if (iRowp == iIncomeCount*3+iExpenseCount*3+5) {
			return null;
		}
		if (iRowp == iIncomeCount*3+iExpenseCount*3+8) {
			return null;
		}
		if (iRowp > iIncomeCount*3 + iExpenseCount*3 + 5)
			return null;
		
		if (iRowp < iIncomeCount*3+1) {
			iRow = (iRowp-1) - ((iRowp-1)/3) * 3;
			if (iRow != 1)
				return null;
			iRow =(iRowp-1)/3;
		}
		if (iRowp > iIncomeCount*3+4) {
			iRow = (iRowp-5) - ((iRowp-5)/3) * 3;
			if (iRow !=1)
				return null;
			iRow = (iRowp-5)/3;
		}
		
		String [] strActions = new String[2];
		strActions[0] = "Display Transactions for "+listCategoryObj.get(iRow).getAccountName()+" only";
		strActions[1] =	"Display both sides of Transactions for "+listCategoryObj.get(iRow).getAccountName();
		return strActions;
	}
	
	public void close() {
		if (transViewer != null)
			transViewer.closetran();
	}

}
