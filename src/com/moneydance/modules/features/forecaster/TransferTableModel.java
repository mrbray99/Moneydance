package com.moneydance.modules.features.forecaster;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.table.DefaultTableModel;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyType;

public class TransferTableModel extends DefaultTableModel {
	private AccountBook objAcctBook;
	private CurrencyType ctBase;
	private ForecastParameters objParams;
	private SortedMap<String, IncludedTransfer> mapData;
	private SortedMap<String, Account> mapAccounts;
	private List<TransferLine> listTransferLines;
	private String[] arrColumnNames = { "Select", "Source", "Destination",
			"Period","Amount","Add. RPI"};

	public TransferTableModel(ForecastParameters objParamsp) {
		super();
		objParams = objParamsp;
		objAcctBook = Main.getContxt().getCurrentAccountBook();
		ctBase = objAcctBook.getCurrencies().getBaseType();
		mapAccounts = objParams.getAccounts();
		listTransferLines = new ArrayList<TransferLine>();
		rebuildLines();
	}

	/* **************************************************************************************
	 * separated out to allow data to be reloaded when underlying data changes
	 * **************************************************************************************/
	public void rebuildLines() {
		listTransferLines.clear();
		mapData = objParams.getTransfers();
		for (SortedMap.Entry<String, IncludedTransfer> objTransfer : mapData
				.entrySet()) {
			IncludedTransfer objValue = objTransfer.getValue();
			TransferLine objLine = new TransferLine();
			listTransferLines.add(objLine);
			objLine.setSelected(objValue.getSelected());
			objLine.setUUID(objTransfer.getKey());
			objLine.setSourceName(objValue.getSourceAccountName());
			objLine.setSource(objValue.getSourceAccount());
			objLine.setAmount(objValue.getAmount());
			objLine.setPeriod(objValue.getPeriod());
			objLine.setDestName(objValue.getDestAccountName());
			objLine.setDest(objValue.getDestAccount());
			objLine.setRPI(objValue.getRPI());
			objLine.setItem(objValue);
		}
		if (listTransferLines.size() == 0)
			addSourceLine(0);
	}
	/*
	 * Table Model Overrides
	 */
	@Override
	public int getRowCount() {
		return listTransferLines == null ? 0 : listTransferLines.size();
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
		TransferLine objTransfer = listTransferLines.get(iRow);
		CurrencyType ctSource = null;
		if (objTransfer.getSource() != null)
			ctSource = objTransfer.getSource().getCurrencyType();
		long lTemp;
		switch (iCol) {
		/*
		 * Select
		 */
		case 0:
			return objTransfer.getSelected();
			/*
			 * Source Account
			 */
		case 1:
			return objTransfer.getSourceName();
			/*
			 * Destination account
			 */
		case 2:
			return objTransfer.getDestName();
			/*
			 * Period
			 */
		case 3:
			return Constants.arrPeriod[objTransfer.getPeriod()];
			/*
			 * Amount
			 */
		case 4:
			lTemp = objTransfer.getAmount();
			if (ctSource != null
						&& ctSource.compareToCurrency(ctBase) != 0)
					lTemp = ctSource.invertValue(lTemp);
			return ctBase.formatFancy(lTemp, Main.getDecimal());
		case 5:
			return String.format("%1$,.2f%%", objTransfer.getRPI());

		}
		return null;

	}
	@Override
	public boolean isCellEditable(int iRow, int iCol) {
		TransferLine objTransfer = listTransferLines.get(iRow);
		switch (iCol) {
		case 0:
			return true;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			if (objTransfer.getSelected())
				return true;
			return false;
		default:
			return true;
		}
	}

	@Override
	public void setValueAt(Object value, int iRow, int iCol) {
		TransferLine objTrans = listTransferLines.get(iRow);
		/*
		 * copes with call when data is invalid
		 */
		if (value == null)
			return;
		switch (iCol) {
		case 0:
			objTrans.setSelected((Boolean) value);
			objParams.setTransferSelected(objTrans.getItem(),objTrans.getSelected());
			break;
		case 1:
			if (value instanceof String) {
				objTrans.setSourceName((String) value);
				objTrans.setSource(mapAccounts.get(objTrans.getSourceName()));
				objParams.setTransferSource(objTrans.getItem(),objTrans.getSourceName(),objTrans.getSource());
			}
			break;
		case 2:
			if (value instanceof String) {
				objTrans.setDestName((String) value);
				objTrans.setDest(mapAccounts.get(objTrans.getDestName()));
				objParams.setTransferDest(objTrans.getItem(),objTrans.getDestName(),objTrans.getDest());
			}
			break;
		case 3:
			int iResult = 0;
			if (value instanceof String){
				for (int i=0;i<Constants.arrPeriod.length;i++) {
					if (((String)value).equals(Constants.arrPeriod[i]))
						iResult = i;
				}
				objTrans.setPeriod(iResult);
				objParams.setTransferPeriod(objTrans.getItem(),objTrans.getPeriod());
			}
			break;
		case 4:
			/*
			 * convert string to long using base currency
			 */
			long lAmt = ctBase.parse((String) value, '.');
			objTrans.setAmount(lAmt);
			objParams.setTransferAmt(objTrans.getItem(),lAmt);
			break;
		case 5:
			String strRPI = (String) value;
			if (strRPI.endsWith("%"))
				strRPI = strRPI.substring(0, strRPI.length() - 1);
			try {
				objTrans.setRPI(Double.parseDouble(strRPI));
			}
			catch (NumberFormatException pe2) {
				objTrans.setRPI(0.0);
			}
			objTrans.setRPI(Double.parseDouble(strRPI));
			objParams.setTransferRPI(objTrans.getItem(),objTrans.getRPI());
			break;

		}
		fireTableDataChanged();
	}
	/* *********************************************************************************
	 * Gets
	 * *********************************************************************************/

	public TransferLine getLine(int iRow) {
		return listTransferLines.get(iRow);
	}
	public void addSourceLine (int iRow) {
		TransferLine objLine = new TransferLine();
		objLine.setSelected(false);
		objLine.setAmount(0L);
		objLine.setSourceName(mapAccounts.firstKey());
		objLine.setSource(mapAccounts.get(objLine.getSourceName()));
		objLine.setDestName(mapAccounts.firstKey());
		objLine.setDest(mapAccounts.get(objLine.getDestName()));
		objLine.setRPI(0.0);
		objLine.setPeriod(0);
		IncludedTransfer objItem = new IncludedTransfer();
		objItem.setSelected(false);
		objItem.setPeriod(0);
		objItem.setSourceAccountName(objLine.getSourceName());
		objItem.setSourceAccount(objLine.getSource());
		objItem.setDestAccountName(objLine.getDestName());
		objItem.setDestAccount(objLine.getDest());	
		objItem.setRPI(0.0);
		objLine.setItem(objItem);
		objLine.setUUID(objParams.addTransfer(objItem));
		listTransferLines.add(iRow,objLine);
		fireTableDataChanged();
	}
	public void deleteSourceLine(int iRow) {
		TransferLine objLine = listTransferLines.get(iRow);
		objParams.removeTransfer(objLine.getUUID());
		listTransferLines.remove(iRow);
	}
}
