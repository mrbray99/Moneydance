package com.moneydance.modules.features.forecaster;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.infinitekind.moneydance.model.AccountUtil;
import com.infinitekind.moneydance.model.BudgetItemDetail;
import com.infinitekind.moneydance.model.DateRange;
import com.infinitekind.moneydance.model.ParentTxn;
import com.infinitekind.moneydance.model.Reminder;
import com.infinitekind.util.DateUtil;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class Generator {
	/*
	 * Moneydance data
	 */
	// txnSet will contain all of the dummy transactions used for analysis
	Transactions txnSet;
	Account acctRoot;
	/*
	 * Program data
	 */
	private SortedMap<String, Account> mapSelectedAccounts;
	// List of expense categories, will match mapExpenseBudgetItems
	private SortedMap<String, IncludedBudget> mapIncludedExpenseBudgets;
	private SortedMap<String, BudgetItemDetail> mapExpenseBudgets;
	// List of income categories, will match mapIncomeBudgetItems
	private SortedMap<String, BudgetItemDetail> mapIncomeBudgets;
	private SortedMap<String, IncludedBudget> mapIncludedIncomeBudgets;
	// List of entered transfers
	private SortedMap<String, IncludedTransfer> mapTransfers;
	// List of selected reminder items
	private SortedMap<String, Reminder> mapReminders;
	private SortedMap<String, IncludedReminder> mapReminderItems;
	// List of selected securities
	private ForecastParameters objParams;
	private int iActualDate;
	private int iLastMonthDate;
	private DateRange[] arrDates;
	private MRBDebug objDebug = MRBDebug.getInstance();

	public Generator(ForecastParameters objParamsp) throws DataErrorException {
		objParams = objParamsp;
		acctRoot = Main.getContxt().getRootAccount();
		mapSelectedAccounts = objParams.getSelectedAccts();
		mapIncludedExpenseBudgets = objParams
				.getSelectedBudgetData(Constants.ScreenType.EXPENSE);
		mapIncludedIncomeBudgets = objParams
				.getSelectedBudgetData(Constants.ScreenType.INCOME);
		mapExpenseBudgets = objParams.getBudgetData(objParams.getBudget(),
				Constants.ScreenType.EXPENSE);
		mapIncomeBudgets = objParams.getBudgetData(objParams.getBudget(),
				Constants.ScreenType.INCOME);
		mapTransfers = objParams.getTransfers();
		mapReminders = objParams.getReminders();
		mapReminderItems = objParams.getReminderItems();
		mapTransfers = objParams.getTransfers();
		iActualDate = objParams.getActualDate();
		iLastMonthDate = objParams.getLastMonthDate();
		arrDates = objParams.getDates();
		txnSet = new Transactions(objParams);
		if (checkBudgetErrors())
			throw new DataErrorException(
					new Throwable("Budget Data has errors"));
	}

	/**
	 * 
	 * @return true if budget data has amounts that do not match the source
	 *         account amounts
	 */
	public boolean checkBudgetErrors() {
		for (IncludedBudget objItem : mapIncludedIncomeBudgets.values()) {
			if (objItem.getError())
				return true;
		}
		for (IncludedBudget objItem : mapIncludedExpenseBudgets.values()) {
			if (objItem.getError())
				return true;
		}
		return false;
	}

	public void calculateData() {
		int iNoTxn = copyActuals();
		objDebug.debug("Generator", "calculateData", MRBDebug.SUMMARY,
				"Actuals copied. No. txn=" + iNoTxn);
		iNoTxn = genBudgets(mapIncludedIncomeBudgets, mapIncomeBudgets);
		objDebug.debug("Generator", "calculateData", MRBDebug.SUMMARY,
				"Income Budgets copied. No. txn=" + iNoTxn);
		iNoTxn = genBudgets(mapIncludedExpenseBudgets, mapExpenseBudgets);
		objDebug.debug("Generator", "calculateData", MRBDebug.SUMMARY,
				"Expense Budgets copied. No. txn=" + iNoTxn);
		iNoTxn = genReminders();
		objDebug.debug("Generator", "calculateData", MRBDebug.SUMMARY,
				"Reminders copied. No. txn=" + iNoTxn);
		iNoTxn = genTransfers();
		objDebug.debug("Generator", "calculateData", MRBDebug.SUMMARY,
				"Transfers copied. No. txn=" + iNoTxn);
	}

	/*
	 * use AllTransactions to select actuals copy into dummy transaction set
	 */
	private int copyActuals() {
		AllTransactions txnAll = new AllTransactions(objParams);
		return txnSet.copyInto(txnAll.getTransactions());
	}

	/*
	 * generate Budget transactions for items after the Actuals Date Generate 1
	 * parent transaction for each included item Generate 1 split transaction
	 * for each Source Item within an included item Only generate transactions
	 * where accounts have been selected
	 */
	private int genBudgets(SortedMap<String, IncludedBudget> mapItems,
			SortedMap<String, BudgetItemDetail> mapDetailItems) {
		int iCount = 0;
		BudgetItemDetail objDetail;
		double dRPI;
		for (IncludedBudget ibItem : mapItems.values()) {
			dRPI = objParams.getRPI() + ibItem.getAddRPI();
			Long lAmount;
			Double dAmount;
			if (!ibItem.getSelected())
				continue;
			int iThisDate;
			boolean bIsIncome;
			boolean bUseBudget;
			if (ibItem.getBudgetCategory().getAccountType() == AccountType.INCOME)
				bIsIncome = true;
			else
				bIsIncome = false;
			if (ibItem.getAnnualAmtType() == Constants.AnnualAmount.BUDGET
					|| ibItem.getAnnualAmtType() == Constants.AnnualAmount.MIXED) {
				bUseBudget = true;
				objDetail = mapDetailItems.get(ibItem.getBudgetCategoryName());
			} else {
				bUseBudget = false;
				objDetail = null;
			}
			for (int i = 0; i < arrDates.length; i++) {
				if (arrDates[i].getEndDateInt() < iActualDate)
					continue;
				if (bUseBudget
						&& DateUtil.calculateDaysBetween(
								arrDates[0].getStartDateInt(),
								arrDates[i].getEndDateInt()) < 367) {
					lAmount = objDetail == null ? 0 : objDetail.getBudgeted(i);
					if (!bIsIncome)
						lAmount *= -1;
					dAmount = lAmount.doubleValue();

				} else {
					if (bIsIncome)
						lAmount = ibItem.getAmount();
					else
						lAmount = -ibItem.getAmount();
					dAmount = lAmount.doubleValue();
					/*
					 * check if date is to be monthly or yearly
					 */
					if (arrDates[i].getEndDateInt() < iLastMonthDate
							|| arrDates[i].containsInt(iLastMonthDate))
						dAmount = dAmount / 12.0;
				}
				/*
				 * Increase due to RPI
				 */
				iThisDate = arrDates[i].getStartDateInt();
				while (iThisDate > objParams.getYearEndDate()) {
					dAmount = dAmount * (100 + dRPI) / 100.0;
					iThisDate = DateUtil.decrementYear(iThisDate);
				}
				lAmount = dAmount.longValue();
				/*
				 * Only generate transactions for source accounts
				 */
				Long lAnnualAmt = ibItem.getAmount();
				Double dAnnualAmt = lAnnualAmt.doubleValue();
				Long lSourceAmt;
				Double dSourceAmt;
				for (SourceItem objSource : ibItem.getSourceItems()) {
					lSourceAmt = objSource.getAmount();
					dSourceAmt = lSourceAmt.doubleValue();
					/*
					 * Calculate split of actual amount to allocate to source
					 * account
					 */
					dSourceAmt = dAmount * dSourceAmt / dAnnualAmt;
					/*
					 * create split txn
					 */
					AnalysisTxn txnSplit = new AnalysisTxn();
					txnSplit.setAcct(objSource.getAccount());
					txnSplit.setDate(arrDates[i].getEndDateInt());
					txnSplit.setAmount(dSourceAmt.longValue());
					txnSet.add(txnSplit);
					iCount++;
				}
			}
		}
		return iCount;
	}

	private int genReminders() {
		int iCount = 0;
		double dRPI = objParams.getRPI();
		Account objLoanAcct = null;
		Account objLoanEscrow = null;
		LoanPayments objPayments = null;
		long lLoanEscrow = 0;
		int iNumLoanPayments = 0;
		Double dAmount;
		Long lAmount;
		Calendar calCrnt = Calendar.getInstance();
		for (Map.Entry<String, IncludedReminder> objEntry : mapReminderItems
				.entrySet()) {
			if (!objEntry.getValue().getSelected())
				continue;
			Reminder objRem = mapReminders.get(objEntry.getKey());
			if (objRem == null)
				continue;
			if (objRem.isLoanReminder()) {
				ParentTxn txnRem = objRem.getTransaction();
				if (txnRem == null)
					continue;
				objLoanAcct = null;
				for (int i = 0; i < txnRem.getOtherTxnCount(); i++) {
					AbstractTxn objSplit = txnRem.getOtherTxn(i);
					if (objSplit.getAccount().getAccountType() == AccountType.LOAN)
						objLoanAcct = objSplit.getAccount();
				}
				if (objLoanAcct == null)
					continue;
				objPayments = new LoanPayments(objLoanAcct);
				lLoanEscrow = objLoanAcct.getEscrowPayment();
				objLoanEscrow = objLoanAcct.getEscrowAccount();
				iNumLoanPayments = objPayments.getRemainingPayments();
			}
			int iThisDate;
			int iCrnt;
			int iLoanCnt = 0;
			for (int i = 0; i < arrDates.length; i++) {
				for (int j = 0; j < arrDates[i].getNumDays(); j++) {
					if (arrDates[i].getEndDateInt() < iActualDate) {
						continue;
					}
					iCrnt = DateUtil.incrementDate(
							arrDates[i].getStartDateInt(), 0, 0, j);
					DateUtil.setCalendarDate(calCrnt, iCrnt);
					if (objRem.occursOnDate(calCrnt)) {
						if (objRem.isLoanReminder()
								&& iLoanCnt >= iNumLoanPayments)
							continue;
						/*
						 * Only generate transactions for selected accounts
						 * Reminders for loans need to be taken from the loan
						 * accounts Loans are not subject to RPI
						 */
						ParentTxn txnParent = objRem.getTransaction();
						AnalysisTxn txnAParent = new AnalysisTxn();
						if (mapSelectedAccounts.get(txnParent.getAccount()
								.getFullAccountName()) != null) {
							txnAParent.setAcct(txnParent.getAccount());
							if (objRem.isLoanReminder())
								lAmount = objPayments.getPayment(iLoanCnt)
										+ objPayments.getInterest(iLoanCnt)
										+ lLoanEscrow;
							else {
								lAmount = txnParent.getValue();
								dAmount = lAmount.doubleValue();
								iThisDate = arrDates[i].getStartDateInt();
								while (iThisDate > objParams.getYearEndDate()) {
									dAmount = dAmount * (100 + dRPI) / 100.0;
									iThisDate = DateUtil
											.decrementYear(iThisDate);
								}
								lAmount = dAmount.longValue();
							}
							txnAParent.setAmount(lAmount);
							txnAParent.setDate(iCrnt);
							txnSet.add(txnAParent);
							iCount++;
						}
						/*
						 * Loans need to be treated differently.
						 */
						if (objRem.isLoanReminder()) {
							if (mapSelectedAccounts.get(objLoanAcct
									.getFullAccountName()) != null) {
								AnalysisTxn txnALoan = new AnalysisTxn();
								txnALoan.setAcct(objLoanAcct);
								lAmount = objPayments.getPayment(iLoanCnt);
								txnALoan.setAmount(lAmount);
								txnALoan.setDate(iCrnt);
								txnSet.add(txnALoan);
								iCount++;
							}
							if (mapSelectedAccounts.get(objLoanEscrow
									.getFullAccountName()) != null) {
								AnalysisTxn txnALoan = new AnalysisTxn();
								txnALoan.setAcct(objLoanEscrow);
								lAmount = lLoanEscrow;
								txnALoan.setAmount(lAmount);
								txnALoan.setDate(iCrnt);
								txnSet.add(txnALoan);
								iCount++;
							}
							iLoanCnt++;
						} else {
							for (int k = 0; k < txnParent.getOtherTxnCount(); k++) {
								AbstractTxn txnSplit = txnParent.getOtherTxn(k);
								AnalysisTxn txnASplit = new AnalysisTxn();
								if (mapSelectedAccounts.get(txnSplit
										.getAccount().getFullAccountName()) != null) {
									txnASplit.setAcct(txnSplit.getAccount());
									lAmount = txnSplit.getValue();
									dAmount = lAmount.doubleValue();
									iThisDate = arrDates[i].getStartDateInt();
									while (iThisDate > objParams
											.getYearEndDate()) {
										dAmount = dAmount * (100 + dRPI)
												/ 100.0;
										iThisDate = DateUtil
												.decrementYear(iThisDate);
									}
									lAmount = dAmount.longValue();
									txnASplit.setAmount(lAmount);
									txnASplit.setDate(iCrnt);
									txnSet.add(txnASplit);
									iCount++;
								}
							}
						}
					}
				}
			}
		}
		return iCount;
	}

	private int genTransfers() {
		int iCount = 0;
		int iStartDate = arrDates[0].getStartDateInt();
		int iStartRange = 0;
		int iGenDate;
		int iRepeat;
		while (iStartDate <= iActualDate && iStartRange < arrDates.length) {
			iStartRange++;
			iStartDate = arrDates[iStartRange].getStartDateInt();
		}
		if (iStartRange > 0)
			iStartRange--;
		iStartDate = arrDates[iStartRange].getStartDateInt();
		for (IncludedTransfer trnItem : mapTransfers.values()) {
			if (!trnItem.getSelected())
				continue;
			switch (trnItem.getPeriod()) {
			case Constants.PERIOD_WEEK_IX:
				iGenDate = DateUtil.firstDayInWeek(iStartDate);
				if (iGenDate < iStartDate)
					iGenDate = DateUtil.incrementDate(iGenDate, 0, 0, 7);
				iRepeat = 7;
				break;
			case Constants.PERIOD_BIWEEK_IX:
				iGenDate = DateUtil.firstDayInWeek(iStartDate);
				if (iGenDate < iStartDate)
					iGenDate = DateUtil.incrementDate(iGenDate, 0, 0, 7);
				int iDays = DateUtil.calculateDaysBetween(
						arrDates[0].getStartDateInt(), iGenDate);
				int iTemp = iDays / 14;
				iTemp = iTemp * 14;
				if (iDays != iTemp)
					iGenDate = DateUtil.incrementDate(iGenDate, 0, 0, 7);
				iRepeat = 14;
				break;
			case Constants.PERIOD_MONTH_IX:
			case Constants.PERIOD_TENMONTH_IX:
				iGenDate = DateUtil.firstDayInMonth(iStartDate);
				if (iGenDate < iStartDate)
					iGenDate = DateUtil.incrementDate(iGenDate, 0, 1, 0);
				if (trnItem.getPeriod() == Constants.PERIOD_MONTH_IX)
					iRepeat = -1;
				else
					iRepeat = -10;
				break;
			case Constants.PERIOD_QUARTER_IX:
				iGenDate = DateUtil.firstDayInQuarter(iStartDate);
				if (iGenDate < iStartDate)
					iGenDate = DateUtil.incrementDate(iGenDate, 0, 3, 0);
				iRepeat = -3;
				break;
			default:
				iGenDate = DateUtil.firstDayInYear(iStartDate);
				if (iGenDate < iStartDate)
					iGenDate = DateUtil.incrementDate(iGenDate, 1, 0, 0);
				iRepeat = -12;
				break;
			}
			iCount += generateTransferAmounts(trnItem, iGenDate, iRepeat, iActualDate);
		}
		return iCount;
	}

	private int generateTransferAmounts(IncludedTransfer trnItem,
			int iStartDate, int iPeriod, int iActualDate) {
		int iCount = 0;
		int iCrntDate = iStartDate;
		int iThisDate;
		double dRPI = objParams.getRPI() + trnItem.getRPI();
		while (iCrntDate < arrDates[arrDates.length - 1].getEndDateInt()) {
			Long lAmount = trnItem.getAmount();
			Double dAmount = lAmount.doubleValue();
			iThisDate = iCrntDate;
			while (iThisDate > objParams.getYearEndDate()) {
				dAmount = dAmount * (100 + dRPI) / 100.0;
				iThisDate = DateUtil.decrementYear(iThisDate);
			}
			lAmount = dAmount.longValue();
			AnalysisTxn txnParent = new AnalysisTxn();
			AnalysisTxn txnSplit = new AnalysisTxn();
			txnParent.setAcct(trnItem.getSourceAccount());
			txnParent.setDate(iCrntDate);
			txnParent.setAmount(-lAmount);
			txnSplit.setAcct(trnItem.getDestAccount());
			txnSplit.setDate(iCrntDate);
			txnSplit.setAmount(lAmount);
			if (iPeriod > 0)
				iCrntDate = DateUtil.incrementDate(iCrntDate, 0, 0, iPeriod);
			else {
				if (iPeriod != -10)
					iCrntDate = DateUtil.incrementDate(iCrntDate, 0, -iPeriod,
							0);
				else {
					iCrntDate = DateUtil.incrementDate(iCrntDate, 0, 1, 0);
					int iMonths = Math.round(DateUtil.monthsInPeriod(
							arrDates[0].getStartDateInt(), iCrntDate));
					int iTemp = iMonths / 12;
					iTemp = iMonths - iTemp * 12;
					if (iTemp > 9)
						iCrntDate = DateUtil.incrementDate(iCrntDate, 0,
								12 - iTemp, 0);
				}
			}
			if (txnParent.getDate() > iActualDate)
				txnSet.add(txnParent);
			if (txnSplit.getDate() > iActualDate)
				txnSet.add(txnSplit);
			iCount += 2;
		}
		return iCount;
	}

	/*
	 * gets and miscellaneous
	 */
	public Transactions getTrans() {
		return txnSet;
	}

	/*
	 * calculate loan payment schedule. Use balance as of the Actuals DateUse
	 * payment schedule to determine payment amounts. Use number of payments,
	 * start date and number of payments per year to determine end of loan
	 */
	public class LoanPayments {
		long lStartBalance;
		long lActualsBalance;
		long lEscrowPayment;
		long lFixedAmt;
		long lInterest;
		long lLoanPayment;
		long lTotalPayment;
		int iNumPayments;
		int iPaymentsPerYear;
		int iPaymentsLeft;
		double dApr;
		double dPoints;
		double dRate;
		long[] arrPrincipals;
		long[] arrInterest;
		long lAmtBorrowed;
		Account objLoanEscrow;

		public LoanPayments(Account objLoanAcct) {
			if (objLoanAcct == null)
				return;
			if (objLoanAcct.getInitialTransfer() == null)
				lStartBalance = -objLoanAcct.getStartBalance();
			else
				lStartBalance = -objLoanAcct.getInitialTransfer().getValue();
			lEscrowPayment = objLoanAcct.getEscrowPayment();
			lFixedAmt = objLoanAcct.getFixedMonthlyPaymentAmount();
			lInterest = 0L;
			lLoanPayment = 0l;
			lTotalPayment = 0l;
			iNumPayments = objLoanAcct.getNumPayments();
			iPaymentsPerYear = objLoanAcct.getPaymentsPerYear();
			dApr = objLoanAcct.getInterestRate();
			dPoints = objLoanAcct.getPoints();
			dRate = (dApr / 100.0D) / iPaymentsPerYear;
			arrPrincipals = new long[iNumPayments];
			arrInterest = new long[iNumPayments];
			lAmtBorrowed = 0L;
			/*
			 * Determine starting balance. If xfr to another account get from
			 * txn else get from account. If Start date of loan is before
			 * actuals date need to get remaining balance to determine payments
			 */
			if (iActualDate < objLoanAcct.getCreationDateInt())
				lActualsBalance = lStartBalance;
			else
				lActualsBalance = -AccountUtil.getBalanceAsOfDate(Main
						.getContxt().getCurrentAccountBook(), objLoanAcct,
						Generator.this.iActualDate);

			lAmtBorrowed = lActualsBalance
					+ Math.round(lStartBalance * dPoints / 100.0D);
			/*
			 * determine average payment, use APR to calculate compound interest
			 */
			if (dApr == 0.0D) {
				lLoanPayment = (long) Math.ceil(lAmtBorrowed / iNumPayments);
			} else {
				double dPayment = Math.ceil(lAmtBorrowed
						* (dRate * Math.pow(1.0D + dRate, iNumPayments) / (Math
								.pow(1.0D + dRate, iNumPayments) - 1.0D)));

				lLoanPayment = (long) dPayment;
			}

			if (objLoanAcct.getCalcPmt()) {
				lTotalPayment = lLoanPayment + lEscrowPayment;
			} else {
				lTotalPayment = lFixedAmt - lEscrowPayment;
			}
			/*
			 * generate payments:
			 * 1. Determine interest due on remaining balance
			 * 2. Subtract interest from payment to get contribution to	principal
			 * 3. Reduce balance by principal
			 */
			int i = 0;
			while ((i < iNumPayments) && (lActualsBalance > 0L)) {
				arrInterest[i] = Math.round(lActualsBalance * dRate);
				if (lTotalPayment - arrInterest[i] < lActualsBalance)
					arrPrincipals[i] = (lLoanPayment - arrInterest[i]);
				else {
					arrPrincipals[i] = lActualsBalance;
				}

				lInterest += arrInterest[i];

				lActualsBalance -= arrPrincipals[i];
				i++;
			}
			iPaymentsLeft = i;
		}

		public int getRemainingPayments() {
			return iPaymentsLeft;
		}

		public long getPayment(int iNum) {
			return arrPrincipals[iNum];
		}

		public long getInterest(int iNum) {
			return arrInterest[iNum];
		}
	}

}
