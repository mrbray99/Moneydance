package com.moneydance.modules.features.forecaster;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.infinitekind.moneydance.model.Account.AccountType;

public class Constants {
	/*
	 * preferences fields
	 */
	public static String PROGRAMNAME = "mrbforecaster";
	public static String EXTENSION = "mbfr";
	public static String FILENAME = "mrbforecastparms";
	public enum Field {
		BUDGET("budget"),
		FILENAME("filename"),
		MAINWIDTH("mainwidth"),
		MAINDEPTH("maindepth");
		private final String strName;
		Field(String strNamep){
			strName = strNamep;
		}
		@Override
		public String toString() { return strName;}
	}
	/*
	 * General enums
	 */
	public enum ParameterType {GENERAL,ACCOUNTS,BUDGETS,REMINDERS,TRANSFERS, BUDGETDATA,DATES,ALL};
	public enum ScreenType {INCOME,EXPENSE};
	public enum BudgetLineType {PARENT,SPLIT};
	public enum ReminderLineType {PARENT,SPLIT};
	public enum AnnualAmount {NONE,ACTUAL,BUDGET,MIXED};
	public enum SecurityLineType {PARENT,SPLIT};
	public enum DataTypes {ACCOUNTS,BUDGETS,REMINDERS,PARAMETERS};
		
	/*
	 * parameter values
	 */
	public enum Type {FISCAL,CALENDAR};
	public enum Selected {SELECTED,NOTSELECTED, PARTIAL};
	/*
	 * screen sizes
	 * 
	 */
	public static int MAINFRAMEWIDTH = 1000;
	public static int MAINFRAMEDEPTH = 800;
	public static int MAINMINFRAMEWIDTH = 600;
	public static int MAINMINFRAMEDEPTH = 400;
	public static int TABMINDEPTH = 250;
	public static int ACCTMINWIDTH = 240;
	public static int ACCTMINDEPTH = 100;
	
	/*
	 * default values
	 */
	public static String NOBUDGET = "** No Budgets available **";
	public static String SEPARATOR = "/";
	public static String MDSEPARATOR = ":";
	public static String UNCATEGORIZED = "No Category"; 
	public static String BUDGETMENU = "Use Annual Budget Amount for Annual Amount";
	public static String ACTUALMENU = "Use Forecast Actuals for Annual Amount";
	public static String MIXEDMENU = "Use Actuals up to Specified Date and Budget to end of Year";
	public static String ADDSOURCE = "Add another source account";
	public static String DELETESOURCE = "Delete this source account";
	public static String ADDTRANSFER = "Add another transfer";
	public static String DELETETRANSFER = "Delete transfer";
	public static final String CANCELLED = "***Cancelled***";
	public static final String PERIOD_BIWEEK = "Bi-weekly";
	public static final String PERIOD_MONTH = "Monthly";
	public static final String PERIOD_WEEK = "Weekly";
	public static final String PERIOD_QUARTER = "Quarterly";
	public static final String PERIOD_TENMONTH = "Ten Monthly";
	public static final String PERIOD_YEAR = "Annual";
	public static final int PERIOD_BIWEEK_IX = 1;
	public static final int PERIOD_MONTH_IX = 2;
	public static final int PERIOD_WEEK_IX = 0;
	public static final int PERIOD_QUARTER_IX = 3;
	public static final int PERIOD_TENMONTH_IX = 4;
	public static final int PERIOD_YEAR_IX = 5;
	public static final String [] arrPeriod = {PERIOD_WEEK,PERIOD_BIWEEK,PERIOD_MONTH,PERIOD_QUARTER,PERIOD_TENMONTH,PERIOD_YEAR};
	public static final Map<AccountType, String> panelNames;
	    static
	    {
	    	panelNames = new HashMap<AccountType, String>();
	    	panelNames.put (AccountType.BANK,"Banks"); 
	    	panelNames.put (AccountType.CREDIT_CARD,"Credit Cards"); 
	    	panelNames.put (AccountType.LOAN,"Loans"); 
	    	panelNames.put (AccountType.ASSET,"Assets"); 
	    	panelNames.put (AccountType.INVESTMENT,"Investments"); 
	    	panelNames.put (AccountType.LIABILITY,"Liabilities"); 
	    }
	 /*
	 * Colours
	 */
	public static final Color ALTERNATECLR = new Color(0xF1,0xFA,0xFE);
	public static final Color SELECTEDCLR = new Color(0xFF, 0xFF, 0xCC);
	public static final Color BUDGETSELECT = new Color(0x98, 0xFB, 0x98);
	public static final Color TABLEHEADER = new Color(0xB5, 0xDA, 0xFF);
	/*
	 * Report Strings
	 */
	public static final String ACCOUNTHEAD = "Account";
}
