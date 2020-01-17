package com.moneydance.modules.features.forecaster;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ReminderPane extends JPanel implements ParameterListener, EntryListener{
	private ForecastParameters objParams;
	protected ReminderTable tabReminders;
	protected ReminderTableModel modReminders;

	
	public ReminderPane(ForecastParameters objParamsp) {
		super();
		objParams = objParamsp;
		objParams.addListener(this);
		setLayout(new BorderLayout());
		modReminders = new ReminderTableModel(objParams);
		tabReminders = new ReminderTable(modReminders);
		JLabel lblInstructions = new JLabel("<html><br>Transactions will be generated for selected Reminders that occur after the Actuals Date."
				+ "<br>Reminders are subject to the RPI except for Loan Reminders which are calculated from the Loan Account.<br></html>");
		add(lblInstructions, BorderLayout.NORTH);
		JScrollPane spReminders = new JScrollPane(tabReminders);
		add(spReminders, BorderLayout.CENTER);

	}


	/*
	 * Listener code
	 * If parameters have changed capture which ones
	 */
	@Override
	public void parametersChanged(Constants.ParameterType enumType) {
		switch (enumType) {
		case REMINDERS :
		case ACCOUNTS :
		case ALL:
			modReminders.rebuildLines();
			modReminders.fireTableDataChanged();
		default:
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

