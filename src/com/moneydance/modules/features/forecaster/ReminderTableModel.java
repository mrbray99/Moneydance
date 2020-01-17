package com.moneydance.modules.features.forecaster;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.table.DefaultTableModel;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.moneydance.model.ParentTxn;
import com.infinitekind.moneydance.model.PaymentSchedule;
import com.infinitekind.moneydance.model.Reminder;
import com.moneydance.awt.JDateField;

public class ReminderTableModel extends DefaultTableModel{
	private AccountBook objAcctBook;
	private CurrencyType ctBase;
	private ForecastParameters objParams;
	private SortedMap<String, Reminder> mapData;
	private List<ReminderLine> listReminderLines;
	private String[] arrColumnNames = { "Select", "Name", "From Date",
			"To Date","From", "To",  "Amount", "Period"};

	public ReminderTableModel(ForecastParameters objParamsp) {
		super();
		objParams = objParamsp;
		objAcctBook = Main.getContxt().getCurrentAccountBook();
		ctBase = objAcctBook.getCurrencies().getBaseType();
		rebuildLines();
	}

	/* **************************************************************************************
	 * separated out to allow data to be reloaded when underlying data changes
	 * **************************************************************************************/
	public void rebuildLines() {
		if (listReminderLines == null)
			listReminderLines = new ArrayList<ReminderLine>();
		else
			listReminderLines.clear();
		mapData = objParams.getReminders();
		long lLoanPrincipal = 0;
		long lLoanEscrow = 0;
		long lLoanInterest = 0;
		Account objLoanAcct = null;
		Account objLoanEscrow = null;
		PaymentSchedule objSched;

		for (SortedMap.Entry<String, Reminder> objEntry : mapData.entrySet()) {
			Reminder objReminder = objEntry.getValue();
			/*
			 * Check to see if this category has any budget data
			 */
			if (objReminder == null)
				continue;
			if (objReminder.isLoanReminder()) {
				ParentTxn txnRem = objReminder.getTransaction();
				if (txnRem == null)
					continue;
				objLoanAcct = null;
				for (int i=0;i<txnRem.getOtherTxnCount();i++){
					AbstractTxn objSplit = txnRem.getOtherTxn(i);
					if (objSplit.getAccount().getAccountType() == AccountType.LOAN)
						objLoanAcct = objSplit.getAccount();
				}
				if (objLoanAcct == null)
					continue;
				objSched = objLoanAcct == null ? null : objLoanAcct.getPaymentSchedule();
				lLoanPrincipal = objSched == null ? 0 :objSched.getNxtPrincipal();
				lLoanInterest = objSched == null ? 0 : objSched.getNxtInterest();
				objLoanEscrow = objLoanAcct == null ? null : objLoanAcct.getEscrowAccount();
				lLoanEscrow = objLoanAcct == null ? 0 : objLoanAcct.getEscrowPayment();				
			}
			ParentTxn objParent = objReminder.getTransaction();
			AbstractTxn [] arrSplits = new AbstractTxn[objParent.getOtherTxnCount()];
			for (int i=0;i<arrSplits.length;i++) {
				arrSplits[i] = objParent.getOtherTxn(i);
			}
			ReminderLine objLine = new ReminderLine();
			objLine.setType(Constants.ReminderLineType.PARENT);
			listReminderLines.add(objLine);
			objLine.setReminder(objReminder);
			objLine.setSelected(objParams.isReminderSelected(objEntry.getKey()));
			Account objFrom = objParent.getAccount();
			if (objFrom == null)
				objLine.setFromAccountName("Unknown");
			else
				objLine.setFromAccountName(objFrom.getFullAccountName());
			objLine.setFromAccount(objFrom);
			Account objTo = arrSplits[0].getAccount();
			if (objTo == null)
				objLine.setToAccountName("Unknown");
			else
				objLine.setToAccountName(objTo.getFullAccountName());
			objLine.setToAccount(objTo);
			objLine.setFromDate(objReminder.getInitialDateInt());
			objLine.setToDate(objReminder.getLastDateInt());
			if (objReminder.isLoanReminder()) {
				if (objTo == objLoanAcct)
					objLine.setAmount(lLoanPrincipal);
				else
					if (objTo == objLoanEscrow)
						objLine.setAmount(lLoanEscrow);
					else
						if (objTo.getAccountType() == AccountType.EXPENSE)
							objLine.setAmount(lLoanInterest);
						else
							objLine.setAmount(arrSplits[0].getValue());
			}
			else
				objLine.setAmount(arrSplits[0].getValue());
			String strFrequency = "";
			if (objReminder.getRepeatDaily() == Reminder.REPEAT_BY_NDAYS)
				strFrequency += "D ";
			int iModifier = objReminder.getRepeatMonthlyModifier();
			int [] arrItems = objReminder.getRepeatMonthly();
			if (iModifier == 0 && arrItems != null && arrItems[0] != 0)
				strFrequency += "M ";
			if (iModifier > 0)
				strFrequency += "M ";
			iModifier = objReminder.getRepeatWeeklyModifier();
			arrItems = objReminder.getRepeatWeeklyDays();
			if (iModifier == 0 && arrItems != null && arrItems[0] != 0)
				strFrequency += "W ";
			if (iModifier > 0)
				strFrequency += "W ";
			if (objReminder.getRepeatYearly())
				strFrequency += "Y";
			objLine.setRepeat(strFrequency);
			if (arrSplits.length > 1) {
				for (int i = 1; i<arrSplits.length;i++) {
					ReminderLine objLine2 = new ReminderLine();
					objLine2.setType(Constants.ReminderLineType.SPLIT);
					listReminderLines.add(objLine2);
					objLine2.setReminder(objReminder);
					objLine2.setParent(objLine);
					objLine2.setSelected(objParams.isReminderSelected(objEntry.getKey()));
					objFrom = objParent.getAccount();
					if (objFrom == null)
						objLine2.setFromAccountName("Unknown");
					else
						objLine2.setFromAccountName(objFrom.getFullAccountName());
					objLine2.setFromAccount(objFrom);
					objTo = arrSplits[i].getAccount();
					if (objTo == null)
						objLine2.setToAccountName("Unknown");
					else
						objLine2.setToAccountName(objTo.getFullAccountName());
					objLine2.setToAccount(objTo);
					if (objReminder.isLoanReminder()) {
						if (objTo == objLoanAcct)
							objLine2.setAmount(lLoanPrincipal);
						else
							if (objTo == objLoanEscrow)
								objLine2.setAmount(lLoanEscrow);
							else
								if (objTo.getAccountType() == AccountType.EXPENSE)
									objLine2.setAmount(lLoanInterest);
								else
									objLine2.setAmount(arrSplits[i].getValue());
					}
					else
						objLine2.setAmount(arrSplits[i].getValue());
				}
			}
		}

	}
	/*
	 * Table Model Overrides
	 */
	@Override
	public int getRowCount() {
		return listReminderLines == null ? 0 : listReminderLines.size();
	}

	@Override
	public int getColumnCount() {
		return arrColumnNames.length;
	}

	@Override
	public String getColumnName(int c) {
		return arrColumnNames[c];
	}

	@Override
	public Object getValueAt(int iRow, int iCol) {
		JDateField jdtTemp = new JDateField(Main.getCdate());
		ReminderLine objRem = listReminderLines.get(iRow);
		CurrencyType ctFrom = null;
		if (objRem.getFromAccount() != null)
			ctFrom = objRem.getFromAccount().getCurrencyType();
		long lTemp;
		switch (iCol) {
		/*
		 * Select
		 */
		case 0:
			if (objRem.getType() == Constants.ReminderLineType.PARENT)
				return objRem.getSelected();
			else
				return "   ";
			/*
			 * Name
			 */
		case 1:
			if (objRem.getType() == Constants.ReminderLineType.SPLIT)
				return "   ";
			return objRem.getReminder().getDescription();
			/*
			 * From date
			 */
		case 2:
			if (objRem.getType() == Constants.ReminderLineType.SPLIT)
				return "   ";
			return jdtTemp.getStringFromDateInt(objRem.getFromDate());
			/*
			 * To date
			 */
		case 3:
			if (objRem.getType() == Constants.ReminderLineType.SPLIT)
				return "   ";
			if (objRem.getToDate() == 0)
				return "Unlimited";
			return  jdtTemp.getStringFromDateInt(objRem.getToDate());
			/*
			 * From Account
			 */
		case 4:
			if (objRem.getType() == Constants.ReminderLineType.SPLIT)
				return "   ";
			return objRem.getFromAccountName();
			/*
			 * To Account
			 */
		case 5:
			return objRem.getToAccountName();
			/*
			 * Amount
			 */
		case 6:
			lTemp = objRem.getAmount();
			if (ctFrom != null
						&& ctFrom.compareToCurrency(ctBase) != 0)
					lTemp = ctFrom.invertValue(lTemp);
			return ctBase.formatFancy(lTemp, Main.getDecimal());
			/*
			 * Period
			 */
		case 7:
			return objRem.getRepeat();
		}
		/*
		 * action
		 */
		return null;

	}
	@Override
	public boolean isCellEditable(int iRow, int iCol) {
		ReminderLine objRem = listReminderLines.get(iRow);
		switch (iCol) {
		case 0:
			if (objRem.getType() == Constants.ReminderLineType.PARENT)
				return true;
			else
				return false;
		default :
			return false;
		}
	}

	@Override
	public void setValueAt(Object value, int iRow, int iCol) {
		ReminderLine objRem = listReminderLines.get(iRow);
		/*
		 * copes with call when data is invalid
		 */
		if (value == null)
			return;
		switch (iCol) {
		case 0:
			objRem.setSelected((Boolean) value);
			if (objRem.getSelected())
				objParams.addReminder(objRem.getReminder());
			else
				objParams.removeReminder(objRem.getReminder());
			break;

		}
		fireTableDataChanged();
	}
	/* *********************************************************************************
	 * Gets
	 * *********************************************************************************/
	public Constants.ReminderLineType getType(int iRow) {
		return listReminderLines.get(iRow).getType();
	}
	
	public ReminderLine getLine(int iRow) {
		return listReminderLines.get(iRow);
	}
	
	public ReminderLine getParent (int iRow) {
		return listReminderLines.get(iRow).getParent();
		
	}
}
