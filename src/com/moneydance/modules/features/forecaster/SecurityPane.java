package com.moneydance.modules.features.forecaster;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.moneydance.apps.md.view.gui.MDImages;

public class SecurityPane extends JPanel implements ParameterListener, EntryListener{
	private ForecastParameters objParams;
	protected JTree objTree;
	private SecurityTableModel modAccts;
	private SecurityTable tabAccounts;
	private JLabel lblType;
	private JPanel panType;
	JScrollPane spAccounts;

	
	public SecurityPane(ForecastParameters objParamsp) {
		super();
		objParams = objParamsp;
		objParams.addListener(this);
		setLayout(new BorderLayout());
		panType = new JPanel(new FlowLayout());
		MDImages objImages = MDImages.getSingletonImages();
		Icon objIcon = objImages.getIconWithColor(MDImages.getIconPathForAccountType(Account.AccountType.INVESTMENT),
				objImages.getIconTintForAccountType(AccountType.INVESTMENT));
		JLabel lblIcon = new JLabel();
		lblIcon.setIcon(objIcon);
		panType.add(lblIcon);
		lblType = new JLabel(Constants.panelNames.get(AccountType.INVESTMENT));
		lblType.setFont(lblType.getFont().deriveFont(20.0f).deriveFont(Font.BOLD));
		panType.add(lblType);
		add(panType,BorderLayout.NORTH); 
		modAccts = new SecurityTableModel(objParams);
		tabAccounts = new SecurityTable(modAccts);
		spAccounts = new JScrollPane(tabAccounts);
		add(spAccounts, BorderLayout.CENTER);

	}

	/*
	 * Listener code
	 * If parameters have changed capture which ones
	 */
	@Override
	public void parametersChanged(Constants.ParameterType enumType) {
		switch (enumType){
		case ALL:
			if (modAccts != null) {
				modAccts.rebuildLines();
				modAccts.fireTableDataChanged();
			}
			SecurityPane.this.revalidate();
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

