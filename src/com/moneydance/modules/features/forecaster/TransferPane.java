package com.moneydance.modules.features.forecaster;

import java.awt.BorderLayout;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.infinitekind.moneydance.model.Account;

public class TransferPane extends JPanel implements ParameterListener, EntryListener{
	/*
	 * Data fields
	 */
	private ForecastParameters objParams;
	private SortedMap<String,Account> mapAccounts;
	private TransferTable tabTransfers;
	private TransferTableModel modTransfers;
	/*
	 * Panel fields
	 */
	private JLabel lblInstruct;
	private JScrollPane spTransfers;
	/*
	 * fields to manage update listeners
	 */
	private boolean bAccountsChanged=false;
	public TransferPane(ForecastParameters objParamsp) {
		super();
		objParams = objParamsp;
		objParams.addListener(this);
		lblInstruct = new JLabel("<html><br>Each transfer has to have a source account, destination account, frequency period and an amount."+
				"<br>Only occurances after the Actuals Date will be used in the calculation.<br></html>");
		mapAccounts = objParams.getSelectedAccts();
		Set<String> setKeys = mapAccounts.keySet();
		String [] arrAccounts = setKeys.toArray(new String [setKeys.size()]);
		modTransfers = new TransferTableModel(objParams);
		tabTransfers = new TransferTable(modTransfers,arrAccounts);
        /*
         * set up panels
         */
		spTransfers = new JScrollPane(tabTransfers);
		setLayout(new BorderLayout());
		add(lblInstruct,BorderLayout.NORTH);
		add(spTransfers,BorderLayout.CENTER);
	}
	/*
	 * Listener code
	 * If parameters have changed capture which ones
	 */
	@Override
	public void parametersChanged(Constants.ParameterType enumType) {
		switch (enumType) {
		case DATES :
		case ALL :
			/*
			 * when transfer data changes reload lines
			 */
			modTransfers.rebuildLines();
			modTransfers.fireTableDataChanged();
			break;
			
		default :
		}
		
	}
	/*
	 * called when tab entered. 
	 * If the accounts have changed, reload combo boxes
	 */
	@Override
	public void tabEntered() {
		if (bAccountsChanged) {
			mapAccounts = objParams.getSelectedAccts();
			Set<String> setKeys = mapAccounts.keySet();
			String [] arrAccounts = setKeys.toArray(new String [setKeys.size()]);
			if (tabTransfers != null)
				tabTransfers.resetAccounts(arrAccounts);
			bAccountsChanged = false;
		}
	}

}