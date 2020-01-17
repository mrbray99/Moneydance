package com.moneydance.modules.features.forecaster;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.table.DefaultTableModel;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.BudgetItemDetail;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.modules.features.mrbutil.MRBInconsistencyException;

public class BudgetTableModel extends DefaultTableModel {
	private AccountBook objAcctBook;
	private CurrencyType ctBase;
	private ForecastParameters objParams;
	private String strBudget;
	private SortedMap<String, IncludedBudget> mapData;
	private SortedMap<String, BudgetItemDetail> mapBudgetData;
	private SortedMap<String, Account> mapAccounts;
	private Constants.ScreenType enumScreen;
	private List<BudgetLine> listBudgetLines;
	private String[] arrColumnNames = { "Select", "Category", "Actual Amt",
			"Pro Rata Actuals","Budget Amt", "Total Budget",  "Annual Amt",
			"Addn. RPI", "Account", "Account Amt"};

	public BudgetTableModel(ForecastParameters objParamsp, String strBudgetp,
			Constants.ScreenType enumScreenp) {
		super();
		objParams = objParamsp;
		objAcctBook = Main.getContxt().getCurrentAccountBook();
		ctBase = objAcctBook.getCurrencies().getBaseType();
		strBudget = strBudgetp;
		enumScreen = enumScreenp;
		mapData = objParams.getSelectedBudgetData(enumScreen);
		mapAccounts = objParams.getBudgetAccounts(enumScreen);
		listBudgetLines = new ArrayList<BudgetLine>();
		rebuildLines(enumScreen);
	}

	/* **************************************************************************************
	 * separated out to allow data to be reloaded when underlying data changes
	 * **************************************************************************************/
	public void rebuildLines(Constants.ScreenType enumScreen) {
		String [] arrPath;
		if (strBudget == null || strBudget.equals(""))
			return;
		listBudgetLines.clear();
		mapBudgetData = objParams.getBudgetData(strBudget, enumScreen);
		String [] arrLastName = new String[0];
		for (SortedMap.Entry<String, Account> objPath : mapAccounts
				.entrySet()) {
			String strKey = objPath.getKey();
			BudgetItemDetail objValue = mapBudgetData.get(strKey);
			/*
			 * Check to see if this category has any budget data
			 */
			if (objValue == null)
				continue;
			BudgetLine objLine = new BudgetLine();
			objLine.setType(Constants.BudgetLineType.PARENT);
			objLine.setParent(objLine);
			listBudgetLines.add(objLine);
			objLine.setCategoryName(objValue.getCategory().getFullAccountName());
			arrPath = objLine.getCategoryName().split(Constants.MDSEPARATOR);
			String strIndent = "";
			for (int i=0;i<arrPath.length;i++) {
				if (i<arrLastName.length && arrLastName[i].equals(arrPath[i]))
					strIndent += "   ";
				else
					strIndent += arrPath[i];
			}
			arrLastName = arrPath;
			objLine.setIndentedName(strIndent);
			objLine.setCategory(objValue.getCategory());
			long[] arrTotals = objParams.calculateAnnualBudget(objValue
					.getCategory().getFullAccountName(), enumScreen);
			/*
			 * [0] Actual + Budget Amount
			 * [1] Actual amount up to actuals date
			 * [2] Budget amount from actuals date to end of year
			 * [3] Total budget for the year 
			 * [4] Actual amount pro rated to the end of the year
			 */
			objLine.setAmount(arrTotals[0]);
			objLine.setActualAmount(arrTotals[1]);
			objLine.setBudgetAmount(arrTotals[2]);
			objLine.setTotalBudgetAmount(arrTotals[3]);
			objLine.setAnnualActualAmount(arrTotals[4]);
			if (mapData.get(strKey) == null) {
				/*
				 * Category not in selected items, set Account to default and then create entry not selected
				 */
				objLine.setSelected(false);
				switch (enumScreen) {
				case INCOME:
					objLine.setAccountName(objParams.getDefaultIncome());
					objLine.setAccount(objParams.getDefaultIncomeObj());
					break;
				default:
					objLine.setAccountName(objParams.getDefaultExpense());
					objLine.setAccount(objParams.getDefaultExpenseObj());
				}
				objLine.setAcctAmount(objLine.getAmount());
				objLine.setRPI(0.0D);
				IncludedBudget objNewItem = new IncludedBudget();
				objNewItem.setSelected(false);
				objNewItem.setAmount(objLine.getAmount());
				objNewItem.setBudgetCategoryName(objLine.getCategoryName());
				objNewItem.setBudgetCategory(objLine.getCategory());
				objNewItem.setAddRPI(0.0D);
				objNewItem.setAnnualAmtType(Constants.AnnualAmount.NONE);
				objNewItem.setActualAmt(objLine.getActualAmount());
				objNewItem.setBudgetAmt(objLine.getBudgetAmount());
				objNewItem.createSourceList();
				SourceItem objNewSource = new SourceItem();
				objNewSource.setAccount(objLine.getAccount());
				objNewSource.setAccountName(objLine.getFullAccountName());
				objNewSource.setAmount(objLine.getAcctAmount());
				objNewItem.addSource(objNewSource);
				mapData.put(strKey,objNewItem);
			} else {
				/*
				 * Category has been selected, If amount type is none then use
				 * amount on item otherwise reset to just loaded amounts reset
				 * the actual and budget amounts
				 */
				IncludedBudget objItem = mapData.get(strKey);
				switch (objItem.getAnnualAmtType()) {
				case NONE:
					objLine.setAmount(objItem.getAmount());
					break;
				case BUDGET:
					objLine.setAmount(objLine.getTotalBudgetAmount());
					break;
				case ACTUAL:
					objLine.setAmount(objLine.getAnnualActualAmount());
					break;
				case MIXED:
					break;
				}
				objItem.setAmount(objLine.getAmount());
				objItem.setActualAmt(objLine.getActualAmount());
				objItem.setBudgetAmt(objLine.getBudgetAmount());
				objLine.setItem(objItem);
				objLine.setSelected(objItem.getSelected());
				objLine.setRPI(objItem.getAddRPI());
				/*
				 * Source Items should never be null but put here to protect the
				 * program
				 */
				if (objItem.getSourceItems() == null) {
					switch (enumScreen) {
					case INCOME:
						objLine.setAccountName(objParams.getDefaultIncome());
						objLine.setAccount(objParams.getDefaultIncomeObj());
						break;
					default:
						objLine.setAccountName(objParams.getDefaultExpense());
						objLine.setAccount(objParams.getDefaultExpenseObj());
					}
					objLine.setAcctAmount(objLine.getAmount());
					objItem.createSourceList();
					SourceItem objNewSource = new SourceItem();
					objNewSource.setAccount(objLine.getAccount());
					objNewSource.setAccountName(objLine.getFullAccountName());
					objNewSource.setAmount(objLine.getAcctAmount());
					objItem.addSource(objNewSource);
					
				} else {
					/*
					 * Add source items to list as SPLITS.
					 */
					objLine.setAcctAmount(objItem.getSourceItems().get(0)
							.getAmount());
					List<SourceItem> listSource = objItem.getSourceItems();
					SourceItem objFirstSource = listSource.get(0);
					objLine.setSource(objFirstSource);
					if (objFirstSource.getAccountName()
							.equals("")) {
						switch (enumScreen) {
						case INCOME:
							objLine.setAccountName(objParams.getDefaultIncome());
							objLine.setAccount(objParams.getDefaultIncomeObj());
							break;
						default:
							objLine.setAccountName(objParams
									.getDefaultExpense());
							objLine.setAccount(objParams.getDefaultExpenseObj());
						}
						objFirstSource.setAccountName(objLine.getFullAccountName());
						objFirstSource.setAccount(objLine.getAccount());
						objFirstSource.setAmount(objLine.getAmount());
					} else {
						objLine.setAccountName(objItem.getSourceItems().get(0)
								.getAccountName());
						objLine.setAccount(objItem.getSourceItems().get(0)
								.getAccount());
					}
					for (int i = 1; i < objItem.getSourceItems().size(); i++) {
						SourceItem objSource = objItem.getSourceItems().get(i);
						BudgetLine objLine2 = new BudgetLine();
						listBudgetLines.add(objLine2);
						objLine2.setItem(objItem);
						objLine2.setSource(objSource);
						objLine2.setType(Constants.BudgetLineType.SPLIT);
						objLine2.setParent(objLine);
						objLine2.setCategoryName(objLine.getCategoryName());
						objLine2.setCategory(objLine.getCategory());
						objLine2.setAmount(objLine.getAmount());
						objLine2.setRPI(objLine.getRPI());
						objLine2.setAccountName(objSource.getAccountName());
						objLine2.setAccount(objSource.getAccount());
						objLine2.setAcctAmount(objSource.getAmount());
					}
				}
			}
		}
		this.fireTableDataChanged();
	}
	/*
	 * Table Model Overrides
	 */
	@Override
	public int getRowCount() {
		return listBudgetLines == null ? 0 : listBudgetLines.size();
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
		BudgetLine objBud = listBudgetLines.get(iRow);
		CurrencyType ctCategory = null;
		if (objBud.getCategory() != null)
			ctCategory = objBud.getCategory().getCurrencyType();
		CurrencyType ctAccount = null;
		if (objBud.getAccount() != null)
			ctAccount = objBud.getAccount().getCurrencyType();
		long lTemp;
		switch (iCol) {
		/*
		 * Select
		 */
		case 0:
			if (objBud.getType() == Constants.BudgetLineType.PARENT)
				return objBud.getSelected();
			else
				return "   ";
			/*
			 * Category
			 */
		case 1:
			if (objBud.getType() == Constants.BudgetLineType.PARENT)
				return objBud.getIndentedName();
			else
				return "   ";
			/*
			 * Actual Amount
			 */
		case 2:
			if (objBud.getType() == Constants.BudgetLineType.PARENT) {
				lTemp = objBud.getActualAmount();
				if (ctCategory != null
						&& ctCategory.compareToCurrency(ctBase) != 0)
					lTemp = ctCategory.invertValue(lTemp);
				return ctBase.formatFancy(lTemp, Main.getDecimal());
			} else
				return "   ";
			/*
			 * Pro rata actuals
			 */
		case 3:
			if (objBud.getType() == Constants.BudgetLineType.PARENT) {
				lTemp = objBud.getAnnualActualAmount();
				if (ctCategory != null
						&& ctCategory.compareToCurrency(ctBase) != 0)
					lTemp = ctCategory.invertValue(lTemp);
				return ctBase.formatFancy(lTemp, Main.getDecimal());
			} else
				return "   ";
			/*
			 * Budget Amount
			 */
		case 4:
			if (objBud.getType() == Constants.BudgetLineType.PARENT) {
				lTemp = objBud.getBudgetAmount();
				if (ctCategory != null
						&& ctCategory.compareToCurrency(ctBase) != 0)
					lTemp = ctCategory.invertValue(lTemp);
				return ctBase.formatFancy(lTemp, Main.getDecimal());
			} else
				return "   ";
			/*
			 * Total Budget Amount
			 */
		case 5:
			if (objBud.getType() == Constants.BudgetLineType.PARENT) {
				lTemp = objBud.getTotalBudgetAmount();
				if (ctCategory != null
						&& ctCategory.compareToCurrency(ctBase) != 0)
					lTemp = ctCategory.invertValue(lTemp);
				return ctBase.formatFancy(lTemp, Main.getDecimal());
			} else
				return "   ";
			/*
			 * Annual Amount
			 */
		case 6:
			if (objBud.getType() == Constants.BudgetLineType.PARENT) {
				lTemp = objBud.getAmount();
				if (ctCategory != null
						&& ctCategory.compareToCurrency(ctBase) != 0)
					lTemp = ctCategory.invertValue(lTemp);
				return ctBase.formatFancy(lTemp, Main.getDecimal());
			} else
				return "   ";
			/*
			 * RPI
			 */
		case 7:
			return String.format("%1$,.2f%%", objBud.getRPI());
			/*
			 * Account name
			 */
		case 8:
			return objBud.getFullAccountName();
			/*
			 * Account amount
			 */
		case 9:
			lTemp = objBud.getAcctAmount();
			if (ctAccount != null && ctAccount.compareToCurrency(ctBase) != 0)
				lTemp = ctCategory.invertValue(lTemp);
			return ctBase.formatFancy(lTemp, Main.getDecimal());
		}
		/*
		 * action
		 */
		return null;

	}
	@Override
	public boolean isCellEditable(int iRow, int iCol) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		switch (iCol) {
		case 0:
			if (objBud.getType() == Constants.BudgetLineType.PARENT)
				return true;
			else
				return false;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return false;
		case 6:
		case 7:
			if (objBud.getSelected() && objBud.getType() == Constants.BudgetLineType.PARENT)
				return true;
			return false;
		case 8:
		case 9:
			if (objBud.getType() == Constants.BudgetLineType.PARENT) {
				if (objBud.getSelected())
					return true;
			}
			else if (objBud.getParent().getSelected())
				return true;
			return false;
		default:
			return true;
		}
	}

	@Override
	public void setValueAt(Object value, int iRow, int iCol) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		/*
		 * copes with call when data is invalid
		 */
		if (value == null)
			return;
		switch (iCol) {
		case 0:
			/*
			 * Select
			 */
			objBud.setSelected((Boolean) value);
			if (objBud.getSelected()) {
				objParams.selectBudgetLine(objBud.getCategory(), enumScreen);
				switch (enumScreen) {
				case INCOME :
					objBud.setAccount(objParams.getDefaultIncomeObj());
					objBud.setAccountName(objParams.getDefaultIncome());
					break;
				default :
					objBud.setAccount(objParams.getDefaultExpenseObj());
					objBud.setAccountName(objParams.getDefaultExpense());
					break;
				}
			}
			else
				objParams.removeBudgetLine(objBud.getCategory(), enumScreen);
			break;
		case 6:
			/*
			 * Annual Amount
			 * convert string to long using base currency
			 */
			long lAmt = ctBase.parse((String) value, '.');
			objBud.getItem().setAmount(lAmt);
			objBud.getItem().setAnnualAmtType(Constants.AnnualAmount.NONE);
			objBud.setAmount(lAmt);
			objBud.getItem().determineError();
			break;
		case 7:
			/*
			 * Additional RPI
			 */
			String strRPI = (String) value;
			if (strRPI.endsWith("%"))
				strRPI = strRPI.substring(0, strRPI.length() - 1);
			try {
				objBud.setRPI(Double.parseDouble(strRPI));
			}
			catch (NumberFormatException pe2) {
				objBud.setRPI(0.0);
			}
			objParams.setBudgetRPI(objBud.getItem(),objBud.getRPI());
			break;
		case 8:
			/*
			 * Source Account
			 */
			if (value instanceof String) {
				objBud.setAccountName((String) value);
				objParams.setBudgetSourceAccount(objBud.getSource(),
						mapAccounts.get(objBud.getFullAccountName()),objBud.getFullAccountName());
			}
			break;
		case 9:
			/*
			 * Source Amount
			 * convert string to long using base currency
			 */
			long lAcctAmt = ctBase.parse((String) value, '.');
			objParams.setBudgetSourceAmt(objBud.getSource(),lAcctAmt);
			objBud.setAcctAmount(lAcctAmt);
			objBud.getItem().determineError();
			break;
		}
		fireTableDataChanged();
	}
	/* *********************************************************************************
	 * Gets
	 * *********************************************************************************/
	/*
	 * Total budget amount is taken from budget line 
	 */
	public String getTotalBudget(int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		CurrencyType ctCategory = null;
		if (objBud.getCategory() != null)
			ctCategory = objBud.getCategory().getCurrencyType();
		long lTemp = objBud.getTotalBudgetAmount();
		if (ctCategory != null && ctCategory.compareToCurrency(ctBase) != 0)
			lTemp = ctCategory.invertValue(lTemp);
		return ctBase.formatFancy(lTemp, Main.getDecimal());

	}

	/*
	 * Total Annual amount is taken from budget line (never displayed)
	 */
	public String getAnnualActual(int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		CurrencyType ctCategory = null;
		if (objBud.getCategory() != null)
			ctCategory = objBud.getCategory().getCurrencyType();
		long lTemp = objBud.getAnnualActualAmount();
		if (ctCategory != null && ctCategory.compareToCurrency(ctBase) != 0)
			lTemp = ctCategory.invertValue(lTemp);
		return ctBase.formatFancy(lTemp, Main.getDecimal());

	}

	/*
	 * Mixed amount is taken from the SelBudgetItem actual amt + budget amt
	 */
	public String getMixedAmount(int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		CurrencyType ctCategory = null;
		if (objBud.getCategory() != null)
			ctCategory = objBud.getCategory().getCurrencyType();
		long lTemp = objBud.getItem().getActualAmt()
				+ objBud.getItem().getBudgetAmt();
		if (ctCategory != null && ctCategory.compareToCurrency(ctBase) != 0)
			lTemp = ctCategory.invertValue(lTemp);
		return ctBase.formatFancy(lTemp, Main.getDecimal());

	}
	public Constants.BudgetLineType getType(int iRow) {
		return listBudgetLines.get(iRow).getType();
	}

	public BudgetLine getLine(int iRow) {
		return listBudgetLines.get(iRow);
	}
	/* *****************************************************************************
	 * Updaters
	 * *****************************************************************************/
	public void setTotalBudget(int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		objBud.setAmount(objBud.getTotalBudgetAmount());
		/*
		 * set Budget Amount on line so it matches annual amount (Budget figures
		 * chosen)
		 */
		objBud.getItem().setAmount(objBud.getAmount());
		objBud.getItem().setAnnualAmtType(Constants.AnnualAmount.BUDGET);
	}

	public void setAnnualActual(int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		objBud.setAmount(objBud.getAnnualActualAmount());
		objBud.getItem().setAmount(objBud.getAmount());
		objBud.getItem().setAnnualAmtType(Constants.AnnualAmount.ACTUAL);
	}

	public void setMixedAmount(int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		objBud.setAmount(objBud.getItem().getActualAmt()
				+ objBud.getItem().getBudgetAmt());
		/*
		 * reset budget amount back to prorated amount from the SelBudItem
		 */
		objBud.setBudgetAmount(objBud.getItem().getBudgetAmt());
		objBud.getItem().setAmount(objBud.getAmount());
		objBud.getItem().setAnnualAmtType(Constants.AnnualAmount.MIXED);
	}
	/*
	 * Manipulate source accounts
	 */
	public void addSourceLine (int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		IncludedBudget objItem = objBud == null? null : objBud.getItem();
		if (objItem == null)
			throw new MRBInconsistencyException(new Throwable(
					"Budget Line not found when adding a source account"));
		SourceItem objSource = new SourceItem();
		switch (enumScreen) {
		case INCOME :
			objSource.setAccount(objParams.getDefaultIncomeObj());
			objSource.setAccountName(objParams.getDefaultIncome());
			break;
		default :
			objSource.setAccount(objParams.getDefaultExpenseObj());
			objSource.setAccountName(objParams.getDefaultExpense());
			break;
		}
		objSource.setAmount(0L);
		objItem.addSource(objSource);
		rebuildLines(enumScreen);
		return;
	}
	public void deleteSourceLine (int iRow) {
		BudgetLine objBud = listBudgetLines.get(iRow);
		IncludedBudget objItem = objBud == null? null : objBud.getItem();
		if (objItem == null)
			throw new MRBInconsistencyException(new Throwable(
					"Budget Line not found when deleting a source account"));
		objItem.deleteSource(objBud.getSource());
		rebuildLines(enumScreen);
		return;
	}
}
