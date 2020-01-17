package com.moneydance.modules.features.forecaster;

import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.AccountUtil;
import com.infinitekind.moneydance.model.Budget;
import com.infinitekind.moneydance.model.BudgetItemDetail;
import com.infinitekind.moneydance.model.BudgetList;
import com.infinitekind.moneydance.model.BudgetListener;
import com.infinitekind.moneydance.model.DateRange;
import com.infinitekind.moneydance.model.Reminder;
import com.infinitekind.moneydance.model.ReminderListener;
import com.infinitekind.moneydance.model.ReminderSet;
import com.infinitekind.util.DateUtil;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.awt.GridC;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBInconsistencyException;
import com.moneydance.modules.features.mrbutil.MRBPreferences;

public class ForecastParameters implements  BudgetListener, ReminderListener {



	// Moneydance objects
	private  Account acctDefaultExpense;
	private  Account acctDefaultIncome;
	private  FeatureModuleContext ctxtCurrent;
	private  AccountBook abCurrent;
	private  Account acctRoot;
	private  BudgetList bdltCurrent;
	private  List<Budget> listBudgets;
	private  Budget budCurrent;
	private  Budget.CalculationResults bcrData;
	// File variables
	private  File fiCurFolder;
	private  FileInputStream fiCurInFile;
	private  FileOutputStream fiCurOutFile;
	private  String strFileName;
	private  String strFullFileName;
	private ReminderSet objReminders;
	/*
	 * Listener fields
	 */
	private  List<ParameterListener> listListeners = new ArrayList<ParameterListener>();
	/*
	 * maps for finding items
	 */
	// All accounts in the current file
	private  SortedMap<String, Account> mapAccounts;
	// All expense categories in the current file
	private  SortedMap<String, Account> mapExpenseAccounts;
	// All income categories in the current file
	private  SortedMap<String, Account> mapIncomeAccounts;
	// The hierarchy of names for all income categories in the current file
	// All budgets in the current file
	private  SortedMap<String,Budget> mapBudgets;
	// All expense categories in the current file that have amounts in the current dates
	private  SortedMap<String,BudgetItemDetail> mapExpenseBudgetItems;
	// All income categories in the current file that have amounts in the current dates
	private  SortedMap<String,BudgetItemDetail> mapIncomeBudgetItems;
	// All Reminders in the current file
	private  SortedMap<String, Reminder> mapReminders;
	// All Reminders in the current file
	private  SortedMap<String, Account> mapSecurities;
	/*
	 *  fields
	 */
	private  int iEnd;
	private int iStartDate;
	private int iYearEndDate;
	private  List<DateRange> listDates;
	private int iLastMonthDate;
	private  boolean bDirty = false;
	private boolean bGenerated = false;
	private Generator objGenData;
	private MRBDebug objDebug;
	/*
	 * Preferences
	 */
	MRBPreferences prefGlobal;
	/*
	 * Saved Variables - all data is copied to proxy
	 */
	SavedData objSavedData;
	// number of years in the report
	private int iNumberYears;
	// number of years to be displayed in months
	private int iMonths;
	// Type of year to forecast (CALENDAR or FISCAL)
	private Constants.Type enumType;
	// Selected accounts
	private SortedMap<String,IncludedAccount> mapIncludedAccounts;
	// Default account to be used for expense budget source
	private String strDefaultExpense;
	// Default account to be used for income budget source
	private String strDefaultIncome;
	// Budget to be used for the report
	private String strBudget;
	// List of expense categories, will match mapExpenseBudgetItems
	private SortedMap<String,IncludedBudget> mapIncludedExpenseBudgets;
	// List of income categories, will match mapIncomeBudgetItems
	private SortedMap<String,IncludedBudget> mapIncludedIncomeBudgets;
	// List of entered transfers
	private SortedMap<String,IncludedTransfer> mapIncludedTransfers;
	// List of selected reminder items
	private SortedMap<String,IncludedReminder> mapIncludedReminders;
	// List of selected securities
	private SortedMap<String,IncludedSecurity> mapIncludedSecurities;
	// Date used to determine which actuals are used.  If bLastMonth is true, this is ignored
	private int iActual;
	// If set actuals are used up to the last day of the previous month, the date will change with the change of current date
	private boolean bActualsLastMonth;
	// RPI used for all figures (can be changed on a line by line basis) when calculating future years
	private double dRPI;
	/* ******************************************************************************
	 * Constructor
	 * ******************************************************************************
	*/
	public ForecastParameters()
			throws MRBInconsistencyException {
		ctxtCurrent = Main.getContxt();
		objDebug = MRBDebug.getInstance();
		objDebug.debug("ForecastParameters","Constructor",MRBDebug.SUMMARY, "Forecaster Parameters Created");
		if (ctxtCurrent == null) {
			throw new MRBInconsistencyException(new Throwable(
					"Extension Context has not been initialised"));
		}
		abCurrent = ctxtCurrent.getCurrentAccountBook();
		if (abCurrent == null) {
			throw new MRBInconsistencyException(new Throwable(
					"Account Book has not been initialised"));
		}
		acctRoot = abCurrent.getRootAccount();
		/*
		 * set up preferences
		 */
		prefGlobal = MRBPreferences.getInstance();
		/*
		 * load accounts into maps
		 */
		loadAccountData();
		/*
		 * Load budgets
		 */
		bdltCurrent = abCurrent.getBudgets();
		if (bdltCurrent == null) {
			throw new MRBInconsistencyException(new Throwable(
					"Budget List has not been intialized"));
		}
		bdltCurrent.addListener(this);
		loadBudgets();
		/*
		 * load reminders
		 */
		objReminders = abCurrent.getReminders();
		if (objReminders == null) {
			throw new MRBInconsistencyException(new Throwable(
					"Reminders have not been intialized"));
		}
		/*
		 * Add a listener for changes to reminders
		 */
		objReminders.addReminderListener(this);
		loadReminders();
		loadParameters();
		if (strBudget == null)
			strBudget = "";
		else {
			budCurrent = mapBudgets.get(strBudget);
			budCurrent.addBudgetListener(this);
		}
		/*
		 * calculate dates based on parameters
		 */
		calculateDates();

	}

	/* *****************************************************************************
	 * data loads - used for rebuilding lists when changed outside the extension
	 * *****************************************************************************
	 *
	 *  Reload
	 */
	public void reloadData(Constants.DataTypes enumType) {
		switch (enumType) {
		case ACCOUNTS:
			loadAccountData();
			break;
		case BUDGETS :
			loadBudgets();
			break;
		case REMINDERS :
			loadReminders();
			break;
		case PARAMETERS :
			loadParameters ();
			calculateDates();
			break;
		}
	}
	/* ***************************************************************************
	 * load parameters
	 * ***************************************************************************
	 */
	private void loadParameters() {
		try {
			strFileName = prefGlobal.getString(Constants.PROGRAMNAME+"."+Constants.Field.FILENAME, Constants.FILENAME);
			fiCurFolder = abCurrent.getRootFolder();
			strFullFileName = fiCurFolder.getAbsolutePath() + "\\" + strFileName
					+ "." + Constants.EXTENSION;
			fiCurInFile = new FileInputStream(strFullFileName);
			ObjectInputStream ois = new ObjectInputStream(fiCurInFile);
			/*
			 * file exists, load proxy data and copy to this object
			 */
			objDebug.debug("ForecastParameters","loadParameters",MRBDebug.SUMMARY, "Parameter File "+strFullFileName + " Exists");
			objSavedData = (SavedData) ois.readObject();
			iNumberYears = objSavedData.iNumberYears;
			iMonths = objSavedData.iMonths;
			enumType = objSavedData.enumType;
			mapIncludedAccounts = objSavedData.mapIncludedAccounts;
			strDefaultExpense = objSavedData.strDefaultExpense;
			strDefaultIncome = objSavedData.strDefaultIncome;
			strBudget = objSavedData.strBudget;
			mapIncludedExpenseBudgets = objSavedData.mapExpenseBudgets;
			mapIncludedIncomeBudgets = objSavedData.mapIncomeBudgets;
			mapIncludedTransfers = objSavedData.mapIncludedTransfers;
			mapIncludedReminders = objSavedData.mapIncludedReminders;
			mapIncludedSecurities = objSavedData.mapIncludedSecurities;
			iActual = objSavedData.iActual;
			bActualsLastMonth = objSavedData.bLastMonth;
			dRPI = objSavedData.dRPI;
			fiCurInFile.close();
			objDebug.debug("ForecastParameters","loadParameters",MRBDebug.DETAILED, "iNumberYears="+iNumberYears);
			objDebug.debug("ForecastParameters","loadParameters",MRBDebug.DETAILED, "iMonths="+iMonths);
			objDebug.debug("ForecastParameters","loadParameters",MRBDebug.DETAILED, "enumType="+enumType);
			objDebug.debug("ForecastParameters","loadParameters",MRBDebug.DETAILED, "iActual="+iActual);
			objDebug.debug("ForecastParameters","loadParameters",MRBDebug.DETAILED, "bActualsLastMonth="+bActualsLastMonth);
			objDebug.debug("ForecastParameters","loadParameters",MRBDebug.DETAILED, "dRPI="+dRPI);
			/*
			 * Account object ids will be incorrect, go through lists setting
			 * the account object ids using an Account Name
			 */
			acctDefaultExpense = AccountUtil.findAccountWithID(acctRoot,strDefaultExpense);
			acctDefaultIncome = AccountUtil.findAccountWithID(acctRoot,strDefaultIncome);
			List<String> listRemove = new ArrayList<String>();
			checkIncludedAccounts();
			/*
			 * Loop through and check if any accounts for transfers have been deleted
			 * If deleted add to list of accounts to drop, then drop each one after map has been released
			 */
			listRemove.clear();
			if (mapIncludedTransfers == null)
				mapIncludedTransfers = new TreeMap<String,IncludedTransfer>();
			for (Map.Entry<String,IncludedTransfer> objLine : mapIncludedTransfers.entrySet()) {
				IncludedTransfer objTranItem = objLine.getValue();
				objTranItem.setSourceAccount(mapAccounts.get(objTranItem.getSourceAccountName()));
				if (objTranItem.getSourceAccount() == null) {
					JOptionPane.showMessageDialog(null, "Account "
							+ objTranItem.getSourceAccountName()
							+ " has been deleted, transfer dropped","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
					listRemove.add(objLine.getKey());
					continue;
				}
				objTranItem.setDestAccount(mapAccounts.get(objTranItem.getDestAccountName()));
				if (objTranItem.getDestAccount() == null) {
					JOptionPane.showMessageDialog(null, "Account "
							+ objTranItem.getDestAccountName()
							+ " has been deleted, transfer dropped","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
					listRemove.add(objLine.getKey());
				}
			}
			for (String strItem : listRemove){
				mapIncludedTransfers.remove(strItem);
				setDirty();
			}
			/*
			 * Loop through and check if any reminders have been deleted
			 * If deleted add to list of reminders to drop, then drop each one after map has been released
			 */
			listRemove.clear();
			if (mapIncludedReminders == null)
				mapIncludedReminders = new TreeMap<String,IncludedReminder>();
			for (Map.Entry<String,IncludedReminder> objLine : mapIncludedReminders.entrySet()) {
				IncludedReminder objRemItem = objLine.getValue();
				if (objRemItem == null){
					JOptionPane.showMessageDialog(null, "Reminder "
							+ objLine.getKey() + " has been deleted","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
					listRemove.add(objLine.getKey());
				}
			}
			for (String strItem : listRemove){
				mapReminders.remove(strItem);
				setDirty();
			}
			/*
			 * Loop through and check if any securities have been deleted
			 * If deleted add to list of securities to drop, then drop each one after map has been released
			 */
			listRemove.clear();
			if (mapIncludedSecurities == null)
				mapIncludedSecurities = new TreeMap<String,IncludedSecurity>();
			for (Map.Entry<String,IncludedSecurity> objLine : mapIncludedSecurities.entrySet()) {
				IncludedSecurity objSecItem = objLine.getValue();
				if (objSecItem == null){
					JOptionPane.showMessageDialog(null, "Security "
							+ objLine.getKey() + " has been deleted","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
					listRemove.add(objLine.getKey());
				}
			}
			for (String strItem : listRemove){
				mapSecurities.remove(strItem);
				setDirty();
			}
		} 
		catch (IOException | ClassNotFoundException ioException) {
			/*
			 * file does not exist - create proxy, copy default data to it and store
			 */
			objDebug.debug("ForecastParameters","Constructor",MRBDebug.DETAILED, "Parameter File Error "+ioException.toString());
			iNumberYears = 5;
			iMonths = 1;
			enumType = Constants.Type.CALENDAR;
			mapIncludedAccounts = new TreeMap<String,IncludedAccount>();
			strDefaultExpense = "";
			acctDefaultExpense = null;
			strDefaultIncome = "";
			acctDefaultIncome = null;
			strBudget = mapBudgets.firstKey();
			mapIncludedExpenseBudgets = new TreeMap<String,IncludedBudget>();
			mapIncludedIncomeBudgets = new TreeMap<String,IncludedBudget>();
			mapIncludedTransfers = new TreeMap<String,IncludedTransfer>();
			mapIncludedReminders = new TreeMap<String,IncludedReminder>();
			mapIncludedSecurities = new TreeMap<String,IncludedSecurity>();
			iActual = DateUtil.getStrippedDateInt();
			bActualsLastMonth = false; 
			dRPI = 0.0;
			objSavedData = new SavedData();
			objSavedData.iNumberYears = iNumberYears;
			objSavedData.iActual= iActual;
			objSavedData.iMonths = iMonths;
			objSavedData.enumType = enumType;
			objSavedData.mapIncludedAccounts = mapIncludedAccounts;
			objSavedData.strDefaultExpense = strDefaultExpense;
			objSavedData.strDefaultIncome = strDefaultIncome;
			objSavedData.strBudget = strBudget;
			objSavedData.mapExpenseBudgets = mapIncludedExpenseBudgets;
			objSavedData.mapIncomeBudgets = mapIncludedIncomeBudgets;
			objSavedData.mapIncludedTransfers = mapIncludedTransfers;
			objSavedData.mapIncludedReminders = mapIncludedReminders;
			objSavedData.mapIncludedSecurities = mapIncludedSecurities;
			objSavedData.bLastMonth = bActualsLastMonth;
			objSavedData.dRPI = dRPI;
			/*
			 * create the file
			 */
			try {
				fiCurOutFile = new FileOutputStream(strFullFileName);
				ObjectOutputStream oos = new ObjectOutputStream(fiCurOutFile);
				oos.writeObject(objSavedData);
				fiCurOutFile.close();
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
		fireParametersChanged(Constants.ParameterType.ALL);
	}
	 /*
	 *
	 *	Load list of budgets
	 */
	private void loadBudgets() {
		listBudgets = bdltCurrent.getAllBudgets();
		if (listBudgets == null) {
			throw new MRBInconsistencyException(new Throwable(
					"Budget have not been intialized"));
		}
		mapBudgets = new TreeMap<String,Budget>();
		for (Budget objBudget : listBudgets) {
			mapBudgets.put(objBudget.getName(), objBudget);
		}		
	}
	/*
	 * Load lists of accounts/categories
	 */
	private void loadAccountData() {
		if (mapAccounts == null)
			mapAccounts = new TreeMap<String, Account>();
		else
			mapAccounts.clear();
		if (mapExpenseAccounts == null)
			mapExpenseAccounts = new TreeMap<String, Account>();
		else
			mapExpenseAccounts.clear();
		if (mapIncomeAccounts == null)
			mapIncomeAccounts = new TreeMap<String, Account>();
		else
			mapIncomeAccounts.clear();
		if (mapSecurities == null)
			mapSecurities = new TreeMap<String, Account>();
		else
			mapSecurities.clear();
		loadAccounts(acctRoot);
	}
	private void loadAccounts(Account parentAcct) {
		int sz = parentAcct.getSubAccountCount();
		String strAcctName;
		for (int i = 0; i < sz; i++) {
			Account acct = parentAcct.getSubAccount(i);
			if (acct.getAccountIsInactive())
				continue;
			strAcctName = acct.getFullAccountName();
			if (strAcctName.equals(""))
				strAcctName = Constants.UNCATEGORIZED;
			if (acct.getAccountType() == Account.AccountType.EXPENSE) {
				mapExpenseAccounts.put(strAcctName, acct);
			}
			else if (acct.getAccountType() == Account.AccountType.INCOME) {
				mapIncomeAccounts.put(strAcctName, acct);
			}
			else
				/*
				 * Security accounts can only be selected for net worth
				 */
				if (acct.getAccountType() == Account.AccountType.SECURITY)
					mapSecurities.put(strAcctName,  acct);
				else {
					mapAccounts.put(strAcctName, acct);
				}
			loadAccounts(acct);
		}
	}
	/*
	 * Checks to see if any changes have been made which invalidate the parameters
	 */
	public void checkIncludedAccounts(){
		List<String> listRemove = new ArrayList<String>();
		if (mapIncludedAccounts == null)
			mapIncludedAccounts = new TreeMap<String,IncludedAccount>();
		/*
		 * Loop through and check if any accounts have been deleted
		 * If deleted add to list of accounts to drop, then drop each one after mapAccounts has been released
		 */
		for (Map.Entry<String,IncludedAccount> objLine : mapIncludedAccounts.entrySet()) {
			IncludedAccount objIncItem = objLine.getValue();
//			objIncItem.setAccount(mapAccounts.get(objLine.getKey()));
			if (objIncItem.getAccount() == null) {
				JOptionPane.showMessageDialog(null, "Account "
						+ objLine.getKey() + " has been deleted","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
				listRemove.add(objLine.getKey());
			}
		}
		for (String strItem : listRemove){
			mapIncludedAccounts.remove(strItem);
			setDirty();
		}
		listRemove.clear();
		/*
		 * Loop through and check if any budget categories have been deleted
		 * If deleted add to list of categories to drop, then drop each one after maps have been released
		 */
		if (mapIncludedExpenseBudgets == null)
			mapIncludedExpenseBudgets = new TreeMap<String,IncludedBudget>();
		for (Map.Entry<String,IncludedBudget> objLine : mapIncludedExpenseBudgets.entrySet()) {
			IncludedBudget objBudItem = objLine.getValue();
//			objBudItem.setBudgetCategory(mapExpenseAccounts.get(objLine.getKey()));
			if (objBudItem.getBudgetCategory() == null) {
				JOptionPane.showMessageDialog(null, "Category "
						+ objLine.getKey() + " has been deleted","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
				listRemove.add(objLine.getKey());
			}
			else {
				/*
				 * category still exists, set source accounts and check if errors
				 */
				for (SourceItem objItem : objBudItem.getSourceItems()) {
					if (objItem.getAccountName().equals("")) {
						objItem.setAccountName(acctDefaultExpense.getFullAccountName());
						objItem.setAccount(acctDefaultExpense);
						setDirty();
					}
					else
					objItem.setAccount(mapAccounts.get(objItem.getAccountName()));
				}
				objBudItem.determineError();
			}
		}
		for (String strItem : listRemove){
			mapIncludedExpenseBudgets.remove(strItem);
			setDirty();
		}
		listRemove.clear();
		if (mapIncludedIncomeBudgets == null)
			mapIncludedIncomeBudgets = new TreeMap<String,IncludedBudget>();
		for (Map.Entry<String,IncludedBudget> objLine : mapIncludedIncomeBudgets.entrySet()) {
			IncludedBudget objBudItem = objLine.getValue();
//			objBudItem.setBudgetCategory(mapIncomeAccounts.get(objLine.getKey()));
			if (objBudItem.getBudgetCategory() == null) {
				JOptionPane.showMessageDialog(null, "Category "
						+ objLine.getKey() + " has been deleted","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
				listRemove.add(objLine.getKey());
			}
			else {
				/*
				 * category still exists, set source accounts and check if errors
				 */
				for (SourceItem objItem : objBudItem.getSourceItems()) {
					if (objItem.getAccountName().equals("")) {
						objItem.setAccountName(acctDefaultIncome.getFullAccountName());
						objItem.setAccount(acctDefaultIncome);
						setDirty();
					}
					else
						objItem.setAccount(mapAccounts.get(objItem.getAccountName()));
				}
				objBudItem.determineError();
			}
		}
		for (String strItem : listRemove){
			mapIncludedIncomeBudgets.remove(strItem);
		}

	}
	private void loadReminders() {
		List<Reminder> listReminders = objReminders.getAllReminders();
		if (listReminders == null) {
			throw new MRBInconsistencyException(new Throwable(
					"Reminder List has not been intialized"));
		}
		if (mapReminders == null)
			mapReminders = new TreeMap<String, Reminder>();
		else
			mapReminders.clear();
		for (Reminder objReminder : listReminders) {
			mapReminders.put(objReminder.getUUID(), objReminder);
		}
	}
	/*
	 * load budget data using the range of dates
	 */
	 public boolean loadBudgetData(String strBudgetp) {
		 Budget objBudget = mapBudgets.get(strBudgetp);
		 if (objBudget == null)
			 return false;
		 strBudget = strBudgetp;
		 DateRange[] arrDates = new DateRange[listDates.size()];
		 arrDates = listDates.toArray(arrDates);
		 bcrData = objBudget.calculate(arrDates, false, false);
		 List<BudgetItemDetail> listDetails;
		 listDetails = bcrData.getItemList();
		 if (mapIncomeBudgetItems == null)
			 mapIncomeBudgetItems = new TreeMap<String,BudgetItemDetail>();
		 else
			 mapIncomeBudgetItems.clear();
		 if (mapExpenseBudgetItems == null)
			 mapExpenseBudgetItems = new TreeMap<String,BudgetItemDetail>();
		 else
			 mapExpenseBudgetItems.clear();
		 String strAcctName;
		 for (BudgetItemDetail objDetail : listDetails) {
			 strAcctName = objDetail.getCategory().getFullAccountName();
			 if (strAcctName.equals(""))
				 strAcctName = Constants.UNCATEGORIZED;
			 if (objDetail.isIncome())
				 mapIncomeBudgetItems.put(strAcctName, objDetail);
			 else
				 mapExpenseBudgetItems.put(strAcctName, objDetail);
		 }
		 return true;
	 }
	/* *********************************************************************************** 
	 * BudgetListener methods
	 * ***********************************************************************************
	 */
	@Override
	public void budgetAdded(Budget budget) {
	}
	@Override
	public void	budgetListModified(BudgetList budgetList) {
		loadBudgets();
		fireParametersChanged(Constants.ParameterType.BUDGETS);		
	}
	@Override
	public void	budgetModified(Budget budget) {
		if (strBudget.equals(budget.getName())) {
			loadBudgetData(strBudget);
			fireParametersChanged(Constants.ParameterType.BUDGETDATA);	
		}
	}
	@Override
	public void budgetRemoved(Budget budget) {
	}
	/* *********************************************************************************** 
	 * ReminderListener methods
	 * ***********************************************************************************
	 */
	@Override
	public void reminderAdded(Reminder objItem) {
		loadReminders();
		fireParametersChanged (Constants.ParameterType.REMINDERS);
	}
	@Override
	public void reminderModified(Reminder objItem) {
		loadReminders();
		fireParametersChanged (Constants.ParameterType.REMINDERS);
	}
	@Override
	public void reminderRemoved(Reminder objItem) {
		loadReminders();
		fireParametersChanged (Constants.ParameterType.REMINDERS);
	}
	
	/* ****************************************************************************
	 * gets - selector fields
	 * ****************************************************************************
	 */
	public int getNumYears() {
		return iNumberYears;
	}

	public int getMonths() {
		return iMonths;
	}

	public Constants.Type getType() {
		return enumType;
	}

	public int getActualDate() {
		return iActual;
	}
	public int getStartDate() {
		return iStartDate;
	}
	public int getYearEndDate() {
		return iYearEndDate;
	}

	public boolean getActualsLastMonth() {
		return bActualsLastMonth;
	}

	public double getRPI() {
		return dRPI;
	}
	public DateRange[] getDates() {
		DateRange[] arrDates = new DateRange[listDates.size()];
		arrDates = listDates.toArray(arrDates);
		return arrDates;
	}
	public int getLastMonthDate() {
		return iLastMonthDate;
	}
	public String getFileName() {
		return strFileName;
	}
	public Transactions getGenTxns() {
		return objGenData == null? null : objGenData.getTrans();
	}
	/* ***************************************************************************
	 * gets - Account pane
	 * **************************************************************************
	 */
	public SortedMap<String,Account> getAccounts() {
		return mapAccounts;
	}
	public SortedMap<String,Account> getSelectedAccts(){
		SortedMap<String,Account> mapAccounts = new TreeMap<String,Account>();
		for (Map.Entry<String,IncludedAccount> entry : mapIncludedAccounts.entrySet()) {
			Account objAcct = entry.getValue().getAccount();
			mapAccounts.put(objAcct.getFullAccountName(), objAcct);
		}
		return mapAccounts;
	}
	
	/* ***************************************************************************
	 * gets - budget pane
	 * **************************************************************************
	 */
	
	public String[] getBudgetList() {
		if (mapBudgets == null || mapBudgets.size() == 0) {
			String [] arrTemp = new String[1];
			arrTemp[0] = Constants.NOBUDGET;
			return arrTemp;
		}
		String[] arrBudgets = new String[mapBudgets.size()];
		arrBudgets = mapBudgets.keySet().toArray(arrBudgets);
		return arrBudgets;
	}
	public String getBudget() {
		if (mapBudgets == null)
			return "";
		if (mapBudgets.size() == 0)
			return "";
		return strBudget;
	}
	 public String getDefaultIncome () {
		 if (acctDefaultIncome == null)
			 return "";
		 return acctDefaultIncome.getFullAccountName();
	 }
	 public String getDefaultExpense () {
		 if (acctDefaultExpense == null)
			 return "";
		 return acctDefaultExpense.getFullAccountName();
	 }
	 public Account getDefaultIncomeObj () {
		 return acctDefaultIncome;
	 }
	 public Account getDefaultExpenseObj () {
		 return acctDefaultExpense;
	 }
	public SortedMap<String,BudgetItemDetail> getBudgetData(String strSelBudget, Constants.ScreenType enumScreen) {
		if (!strBudget.equals(strSelBudget)) {
			if (!loadBudgetData (strSelBudget))
				return null;
		}
		switch (enumScreen) {
		case INCOME :
			return mapIncomeBudgetItems;
		default :
			return mapExpenseBudgetItems;
		}
	}
	public SortedMap<String,IncludedBudget> getSelectedBudgetData(Constants.ScreenType enumScreen) {
		switch (enumScreen) {
		case INCOME :
			return mapIncludedIncomeBudgets;
		default :
			return mapIncludedExpenseBudgets;
		}
	}
	public SortedMap<String,Account> getBudgetAccounts(Constants.ScreenType enumScreen) {
		switch (enumScreen) {
		case INCOME :
			return mapIncomeAccounts;
		default :
			return mapExpenseAccounts;
		}
	}
	
	/* ***************************************************************************
	 * gets -reminder and transfer panes
	 * **************************************************************************
	 */

	public SortedMap<String, Reminder> getReminders() {
		return mapReminders;
	}
	public SortedMap<String,IncludedReminder> getReminderItems() {
		return mapIncludedReminders;
	}
	public SortedMap<String, IncludedTransfer> getTransfers() {
		return mapIncludedTransfers;
	}
	public SortedMap<String,IncludedSecurity> getSelectedSecurities() {
		return mapIncludedSecurities;
	}
	
	/* ***************************************************************************
	 * Calculations
	 * **************************************************************************
	 */

	/*
	 * Calculate budget data, return an array of data
	 * 0 - budget + actual for the year
	 * 1 - Actual Amount up to Actual Date
	 * 2 - Budget Amount from Actual Date to end of year
	 * 3 - Budget Amount for the year
	 * 4 - Actual Amount forecast 
	 */
	public long[] calculateAnnualBudget (String strCategory, Constants.ScreenType enumScreen) {
		int iStartTemp = listDates.get(0).getStartDateInt();
		int iEndTemp = DateUtil.incrementYear(iStartTemp);
		long [] arrTotals = new  long[5];
		long lAmt = 0L;
		arrTotals[0] =0L;
		arrTotals[1] = 0L;
		arrTotals[2] = 0L;
		arrTotals[3] = 0L;
		arrTotals[4] = 0L;
		long lBudget = 0L;
		long lActual = 0L;
		long lTotalBudget = 0L;
		int iInterval =0;
		int iActualDate = bActualsLastMonth? Main.getLastMonthDate():iActual;
		switch (enumScreen) {
		case INCOME :
			if (mapIncomeBudgetItems == null)
				return arrTotals;
			BudgetItemDetail objDetail = mapIncomeBudgetItems.get(strCategory);
			if (objDetail == null)
				return arrTotals;
			while (iStartTemp < iEndTemp && iInterval < listDates.size()) {
				lTotalBudget += objDetail.getBudgeted(iInterval);
				if (iStartTemp < iActualDate)
					lActual += objDetail.getActual(iInterval);
				else
					lBudget += objDetail.getBudgeted(iInterval);
				iInterval++;
				if (iInterval <listDates.size())
					iStartTemp = listDates.get(iInterval).getStartDateInt();
				else
					iStartTemp = iEndTemp;
			}
			break;
		default :
			if (mapExpenseBudgetItems == null)
				return arrTotals;
			BudgetItemDetail objDetailExp = mapExpenseBudgetItems.get(strCategory);
			if (objDetailExp == null)
				return arrTotals;
			while (iStartTemp < iEndTemp && iInterval < listDates.size()) {
				lTotalBudget += objDetailExp.getBudgeted(iInterval);
				if (iStartTemp < iActualDate)
					lActual += objDetailExp.getActual(iInterval);
				else
					lBudget += objDetailExp.getBudgeted(iInterval);
				iInterval++;
				if (iInterval <listDates.size())
					iStartTemp = listDates.get(iInterval).getStartDateInt();
				else
					iStartTemp = iEndTemp;
			}
			break;
		}
		lAmt = lActual + lBudget;
		arrTotals[0] = lAmt;
		arrTotals[1] = lActual;
		arrTotals[2] = lBudget;
		arrTotals[3] = lTotalBudget;
		int iMnth = Math.round(DateUtil.monthsInPeriod(listDates.get(0).getStartDateInt(), iActual)+0.5F);
		arrTotals[4] = lActual/iMnth * 12;
		
		return arrTotals;
	}
	
	/*
	 * calculate the start and end dates, create the date ranges
	 */
	private void calculateDates() {
		switch (enumType) {
		case CALENDAR :
			iStartDate = Main.getCalendarDate();
			break;
		default :
			iStartDate = Main.getFiscalDate();
		}
		Calendar calTemp = Calendar.getInstance();
		synchronized (calTemp) {
			DateUtil.setCalendarDate(calTemp,iStartDate);
			calTemp.add(Calendar.YEAR, iNumberYears);
			iEnd = DateUtil.convertCalToInt(calTemp);
			DateUtil.setCalendarDate(calTemp,iStartDate);
			calTemp.add(Calendar.YEAR, 1);
			calTemp.add(Calendar.DAY_OF_YEAR, -1);
			iYearEndDate = DateUtil.convertCalToInt(calTemp);			
		}
		if (listDates == null)
			listDates = new ArrayList<DateRange>();
		else
			listDates.clear();
		int iEndDate = iStartDate;
		int iDate = iStartDate;
		iLastMonthDate = iEndDate;
		for (int i=0;i<iMonths*12;i++) {
 			iEndDate = DateUtil.incrementDate(iDate, 0, 1, 0);
 			iEndDate = DateUtil.incrementDate(iEndDate,0,0,-1);
 			iLastMonthDate = iEndDate;
 			DateRange drDate = new DateRange(iDate, iEndDate);
 			listDates.add(drDate);
 			iDate = DateUtil.incrementDate(iDate, 0, 1, 0);
 		}
 		while (iDate < iEnd) {
 			iEndDate = DateUtil.incrementDate(iDate, 1, 0, -1);
 			DateRange drDate = new DateRange(iDate, iEndDate);
			listDates.add(drDate); 			
 			iDate = DateUtil.incrementDate(iDate, 1, 0, 0);
 		}
 		if (bActualsLastMonth) {
 			iActual = Main.getLastMonthDate();
 		}
 	}
	/* ****************************************************************************
	 * Sets
	 * ****************************************************************************
	 * 
	 * Selector Pane
	 * 
	 */
	
	public void setActualDate(JDateField jdtActualp) {
		if (iActual != jdtActualp.getDateInt())
			fireParametersChanged(Constants.ParameterType.BUDGETDATA);			
		iActual = jdtActualp.getDateInt();
		setDirty();
		fireParametersChanged(Constants.ParameterType.DATES);
	}

	public void setActualsLastMonth(boolean bLastMonthp) {
		if (bActualsLastMonth != bLastMonthp)
			fireParametersChanged(Constants.ParameterType.DATES);			
		bActualsLastMonth = bLastMonthp;
		calculateDates();
		setDirty();
	}
	public void setRPI(double dRPIp) {
		dRPI = dRPIp;
		setDirty();
		fireParametersChanged(Constants.ParameterType.GENERAL);
	}

	public void setType(Constants.Type enumTypep) {
		enumType = enumTypep;
		setDirty();
		calculateDates();
		fireParametersChanged(Constants.ParameterType.DATES);
	}
	public void setNumYears(int iYearsp) {
		iNumberYears = iYearsp;
		calculateDates();
		setDirty();		fireParametersChanged(Constants.ParameterType.GENERAL);
	}

	public void setMonths(int iMonthsp) {
		iMonths = iMonthsp;
		calculateDates();
		setDirty();		fireParametersChanged(Constants.ParameterType.GENERAL);
	}
	public void setFileName(String strFileNamep) {
		strFileName = strFileNamep;
		prefGlobal.put(Constants.PROGRAMNAME+"."+Constants.Field.FILENAME, strFileName);
		prefGlobal.isDirty();
	}
	/*
	 * Account Pane
	 */
	public void addSelectedAccount(String strAccountp) {
		if (mapIncludedAccounts.containsKey(strAccountp))
			return;
		mapIncludedAccounts.put(strAccountp,new IncludedAccount(strAccountp,mapAccounts.get(strAccountp)));
		setDirty();	
		fireParametersChanged(Constants.ParameterType.ACCOUNTS);
	}
	public void removeSelectedAccount(String strAccountp) {
		if (!mapIncludedAccounts.containsKey(strAccountp))
			return;
		mapIncludedAccounts.remove(strAccountp);
		setDirty();	
		fireParametersChanged(Constants.ParameterType.ACCOUNTS);
	}
	public void addSelectedSecurity(String strAccountp) {
		if (mapIncludedSecurities.containsKey(strAccountp))
			return;
		mapIncludedSecurities.put(strAccountp,new IncludedSecurity(strAccountp,mapSecurities.get(strAccountp)));
		setDirty();
		fireParametersChanged(Constants.ParameterType.ACCOUNTS);
	}
	public void removeSelectedSecurity(String strAccountp) {
		if (!mapIncludedSecurities.containsKey(strAccountp))
			return;
		mapIncludedSecurities.remove(strAccountp);
		setDirty();
		fireParametersChanged(Constants.ParameterType.ACCOUNTS);
	}
	/*
	 * Budget Pane
	 */
	public void setDefaultIncomeAccount(String strAccountp){
		Account objAcct = mapAccounts.get(strAccountp);
		if (objAcct != null) {
			acctDefaultIncome = objAcct;
			strDefaultIncome = objAcct.getUUID();
			setDirty();
			fireParametersChanged(Constants.ParameterType.BUDGETDATA);
		}
	}
	public void setDefaultExpenseAccount(String strAccountp){
		Account objAcct = mapAccounts.get(strAccountp);
		if (objAcct != null) {
			acctDefaultExpense = objAcct;
			strDefaultExpense = objAcct.getUUID();
			setDirty();
			fireParametersChanged(Constants.ParameterType.BUDGETDATA);
		}
	}
	
	public void setBudget(String strBudgetp) {
		budCurrent.removeBudgetListener(this);		
		strBudget = strBudgetp;
		budCurrent = mapBudgets.get(strBudget);
		budCurrent.addBudgetListener(this);
		setDirty();
		loadBudgetData(strBudget);
	}
	public void setBudgetRPI (IncludedBudget objItem, double dRPIp){
		objItem.setAddRPI(dRPIp);
		setDirty();
	}
	public void setBudgetSourceAccount (SourceItem objItem, Account objAcct, String strAcctName){
		objItem.setAccount(objAcct);
		objItem.setAccountName(strAcctName);
		setDirty();
	}
	public void setBudgetSourceAmt (SourceItem objItem, long lAmt){
		objItem.setAmount(lAmt);
		setDirty();
	}
	/*
	 * Reminder Pane
	 */
	public void addSelectedReminder(String strReminderp) {
		if (mapIncludedReminders.containsKey(strReminderp)) {
			mapIncludedReminders.get(strReminderp).setSelected(true);
			return;
		}
		mapIncludedReminders.put(strReminderp,new IncludedReminder(true));
		setDirty();	}
	public void removeSelectedReminder(String strReminderp) {
		if (!mapIncludedReminders.containsKey(strReminderp))
			return;
		mapIncludedReminders.remove(strReminderp);
		setDirty();	
	}
	/*
	 * Transfer Pane
	 */
	public void setTransferSelected(IncludedTransfer objItem,boolean bSelected) {
		objItem.setSelected(bSelected);
		setDirty();
	}
	public void setTransferSource(IncludedTransfer objItem, String strAcctName, Account objAcct) {
		objItem.setSourceAccountName(strAcctName);
		objItem.setSourceAccount(objAcct);
		setDirty();
	}
	public void setTransferDest(IncludedTransfer objItem, String strAcctName, Account objAcct) {
		objItem.setDestAccountName(strAcctName);
		objItem.setDestAccount(objAcct);
		setDirty();
	}
	public void setTransferPeriod(IncludedTransfer objItem, int iPeriod) {
		objItem.setPeriod(iPeriod);
		setDirty();
	}
	public void setTransferAmt(IncludedTransfer objItem, long lAmount) {
		objItem.setAmount(lAmount);
		setDirty();
	}
	public void setTransferRPI(IncludedTransfer objItem, double dRPI) {
		objItem.setRPI(dRPI);
		setDirty();
	}
	/* **********************************************************************
	 * Miscellaneous
	 * *********************************************************************/
	public void setDirty() {
		bDirty = true;
		bGenerated = false;
	}
	/*
	 * select a budget line
	 */
	public void selectBudgetLine (Account objCategory, Constants.ScreenType enumType) {
		IncludedBudget objItem;
		List<SourceItem> listSource;
		switch (enumType) {
		case INCOME :
			objItem = mapIncludedIncomeBudgets.get(objCategory.getFullAccountName());
			if (objItem == null) 
				throw new MRBInconsistencyException(new Throwable(
						"Income budget missing"));				
			listSource = objItem.getSourceItems();
			break;
		default :
			objItem = mapIncludedExpenseBudgets.get(objCategory.getFullAccountName());
			if (objItem == null) 
				throw new MRBInconsistencyException(new Throwable(
						"Expense budget missing"));				
			listSource = objItem.getSourceItems();
		}
		if (listSource == null) {
			objItem.createSourceList();
			SourceItem objSource = new SourceItem();
			objSource.setAmount((objItem.getAmount()));
			switch (enumType) {
			case INCOME :
				objSource.setAccountName(acctDefaultIncome.getFullAccountName());
				objSource.setAccount(acctDefaultIncome);
				break;
			default :
				objSource.setAccountName(acctDefaultExpense.getFullAccountName());
				objSource.setAccount(acctDefaultExpense);
			}
			objItem.addSource(objSource);
		}
		objItem.setSelected(true);
		objItem.determineError();
		fireParametersChanged(Constants.ParameterType.BUDGETDATA);
		setDirty();
	}
	/*
	 * deselect a budget line
	 */
	public void removeBudgetLine(Account objCategory, Constants.ScreenType enumType) {
		IncludedBudget objItem;
		switch (enumType) {
		case INCOME :
			objItem = mapIncludedIncomeBudgets.get(objCategory.getFullAccountName());
			break;
		default :
			objItem = mapIncludedExpenseBudgets.get(objCategory.getFullAccountName());
		}
		objItem.setSelected(false);
		objItem.determineError();
		fireParametersChanged(Constants.ParameterType.BUDGETDATA);
		setDirty();
	}
	/*
	 * Add a new transfer line
	 */
	public String addTransfer(IncludedTransfer objItem) {
		String strUUID = UUID.randomUUID().toString();
		mapIncludedTransfers.put(strUUID, objItem);
		setDirty();
		return strUUID;
	}
	/*
	 * remove a transfer item
	 */
	public void removeTransfer(String strUUID) {
		mapIncludedTransfers.remove(strUUID);
		setDirty();
	}
	/*
	 * Add a new Reminder line
	 */
	public void addReminder(Reminder objReminder) {
		if (mapIncludedReminders.containsKey(objReminder.getUUID())) {
			IncludedReminder objItem = mapIncludedReminders.get(objReminder.getUUID());
			objItem.setSelected(true);
		}
		else {
			IncludedReminder objItem = new IncludedReminder(true);
			mapIncludedReminders.put(objReminder.getUUID(), objItem);
		}
		setDirty();
	}
	/*
	 * remove a reminder item
	 */
	public void removeReminder(Reminder objReminder) {
		if (mapIncludedReminders.containsKey(objReminder.getUUID())) {
			IncludedReminder objItem = mapIncludedReminders.get(objReminder.getUUID());
			objItem.setSelected(false);
			setDirty();
		}
	}
	/*
	 * checks
	 */
	public boolean isAccountSelected (String strAcct){
		if (mapIncludedAccounts.containsKey(strAcct))
			return true;
		return false;
	}
	public boolean isReminderSelected(String strReminder) {
		IncludedReminder objDetail = mapIncludedReminders.get(strReminder);
		if (objDetail != null)
			return objDetail.getSelected();
		return false;
	}
	public boolean isDirty() {
		return bDirty;
		
	}
	/*
	 * Save the parameters into the specified file
	 */
	public void save() {
		String strFileNamep = askForFileName(strFileName);
		if (strFileNamep.equals(Constants.CANCELLED))
			return;
		setFileName(strFileNamep);
		strFullFileName = fiCurFolder.getAbsolutePath() + "\\" + strFileName
				+ "." + Constants.EXTENSION;
		try {
			fiCurInFile = new FileInputStream(strFullFileName);
			JFrame fTemp = new JFrame();
			int iResult = JOptionPane.showConfirmDialog(fTemp,
					"File already exists.  Do you wish to overwrite?","Forecaster Parameters",JOptionPane.YES_NO_CANCEL_OPTION);
			fiCurInFile.close();
			if (iResult != JOptionPane.YES_OPTION)
				return;
		} catch (IOException i) {} // do nothing as file not found
		/*
		 * copy data to proxy and store
		 */
		objSavedData.iNumberYears = iNumberYears;
		objSavedData.iActual= iActual;
		objSavedData.iMonths = iMonths;
		objSavedData.enumType = enumType;
		objSavedData.mapIncludedAccounts = mapIncludedAccounts;
		objSavedData.strDefaultExpense = strDefaultExpense;
		objSavedData.strDefaultIncome = strDefaultIncome;
		objSavedData.strBudget = strBudget;
		objSavedData.mapExpenseBudgets = mapIncludedExpenseBudgets;
		objSavedData.mapIncomeBudgets = mapIncludedIncomeBudgets;
		objSavedData.mapIncludedTransfers = mapIncludedTransfers;
		objSavedData.mapIncludedReminders = mapIncludedReminders;
		objSavedData.mapIncludedSecurities = mapIncludedSecurities;
		objSavedData.bLastMonth = bActualsLastMonth;
		objSavedData.dRPI = dRPI;
		try {
			fiCurOutFile = new FileOutputStream(strFullFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fiCurOutFile);
			oos.writeObject(objSavedData);
			oos.close();
			fiCurOutFile.close();
		} catch (IOException i) {
			throw new MRBInconsistencyException(new Throwable(
					"IO Error on writing parameters"));
		}
		bDirty = false;
	}
	/*
	 * ask for parameters file name
	 */
	private String askForFileName(String strFileNamep) {
		String strFileName;
		JPanel panInput = new JPanel(new GridBagLayout());
		JLabel lblType = new JLabel("Enter File Name:");
		strFileName = strFileNamep;
		panInput.add(lblType, GridC.getc(0,0).insets(10, 10, 10, 10));
		JTextField txtType = new JTextField();
		txtType.setText(strFileName);
		txtType.setColumns(20);
		panInput.add(txtType, GridC.getc(1,0).insets(10, 10, 10, 10));
		while (true) {
			int iResult = JOptionPane.showConfirmDialog(null, panInput,
					"Save Parameters", JOptionPane.OK_CANCEL_OPTION);
			if (iResult == JOptionPane.OK_OPTION) {
				if (txtType.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							"File Name can not be blank","Forecaster Parameters",JOptionPane.DEFAULT_OPTION);
					continue;
				}
				strFileName = txtType.getText();
				break;
			}
			if (iResult == JOptionPane.CANCEL_OPTION) {
				strFileName = Constants.CANCELLED;
				break;
			}
		}
		return strFileName;

	}
	/*
	 * Generator methods
	 */
	public void Generate() throws DataErrorException {
		if (!bGenerated) {
			try {
				objGenData = new Generator(this);
				objGenData.calculateData();
				bGenerated = true;
			}
			catch (DataErrorException e) {
				throw new DataErrorException(new Throwable());

			}
		}
		
	}
	/*
	 * Listener methods
	 */
	public void addListener(ParameterListener objListener) {
		listListeners.add(objListener);
	}
	public void removeListener(ParameterListener objListener) {
		listListeners.remove(objListener);
	}
	public void fireParametersChanged (Constants.ParameterType enumType){
		for (ParameterListener objListener : listListeners){
			objListener.parametersChanged(enumType);
		}
	}
}
