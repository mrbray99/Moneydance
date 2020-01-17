package com.moneydance.modules.features.forecaster;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.infinitekind.moneydance.model.Account.AccountType;
import com.moneydance.apps.md.view.gui.MDImages;

public class AccountsPane extends JPanel implements ParameterListener, EntryListener{
	private ForecastParameters objParams;
	protected JTree objTree;
	private AccountTableModel modAccts;
	private AccountTable tabAccounts;
	private JScrollPane spAccounts;
	private JLabel lblType;
	private JPanel panType;
	private AccountType enumAcctType;

	
	public AccountsPane(ForecastParameters objParamsp, AccountType enumAcctTypep) {
		super();
		objParams = objParamsp;
		enumAcctType = enumAcctTypep;
		panType = new JPanel(new FlowLayout());
		MDImages objImages = MDImages.getSingletonImages();
		Icon objIcon = objImages.getIconWithColor(MDImages.getIconPathForAccountType(enumAcctType),
				objImages.getIconTintForAccountType(enumAcctType));
		JLabel lblIcon = new JLabel();
		lblIcon.setIcon(objIcon);
		panType.add(lblIcon);
		objParams.addListener(this);
		setLayout(new BorderLayout());
		lblType = new JLabel(Constants.panelNames.get(enumAcctType));
		lblType.setFont(lblType.getFont().deriveFont(20.0f).deriveFont(Font.BOLD));
		panType.add(lblType);
		add(panType,BorderLayout.NORTH); 
		modAccts = new AccountTableModel(objParams, enumAcctType);
		tabAccounts = new AccountTable(modAccts);
		spAccounts = new JScrollPane(tabAccounts);
		add(spAccounts, BorderLayout.CENTER);

	}

	/*
	 * Listener code
	 * If parameters have changed capture which ones
	 */
	@Override
	public void parametersChanged(Constants.ParameterType enumType) {
		switch (enumType) {
		case ACCOUNTS :
		case ALL :
			modAccts.rebuildLines();
			modAccts.fireTableDataChanged();
			break;
		default :
		}
	}
	/*
	 * called when tab entered. 
	 * Nothing to do on this pane
	 */
	@Override
	public void tabEntered() {
		
	}

}

