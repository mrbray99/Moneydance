package com.moneydance.modules.features.forecaster;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.infinitekind.moneydance.model.Account;
import com.moneydance.awt.GridC;

public class BudgetPane extends JPanel implements ParameterListener, EntryListener{
	/*
	 * Data fields
	 */
	private ForecastParameters objParams;
	private SortedMap<String,Account> mapAccounts;
	private BudgetTable tabIncome;
	private BudgetTable tabExpense;
	private BudgetTableModel modIncome;
	private BudgetTableModel modExpense;
	/*
	 * selected items
	 */
	private String strBudget;
	/*
	 * Panel fields
	 */
	private JTextArea lblInstruct;
	private JLabel lblDefaultIncome;
	private JLabel lblDefaultExpense;
	private JLabel lblBudget;
	private JComboBox<String> boxBudget;
	private JComboBox<String> boxIncome;
	private JComboBox<String> boxExpense;
	private JTabbedPane panTabs = new JTabbedPane();
	private JPanel panIncome;
	private JPanel panExpense;
	private JScrollPane spIncome;
	private JScrollPane spExpense;

	/*
	 * fields to manage update listeners
	 */
	private boolean bAccountsChanged=false;
	public BudgetPane(ForecastParameters objParamsp) {
		super();
		objParams = objParamsp;
		objParams.addListener(this);
		lblInstruct = new JTextArea("Each budget amount needs to be assigned to a source account."+
				" If none are specified the default accounts will be used."+
				"You can split a budget into two or more accounts. The total must add up to the budget amount.",3,70);
		lblInstruct.setLineWrap(true);
		lblDefaultIncome = new JLabel("Default Income Account");
		lblDefaultExpense = new JLabel("Default Expense Account");
		lblBudget = new JLabel("Budget");
		mapAccounts = objParams.getSelectedAccts();
		Set<String> setKeys = mapAccounts.keySet();
		String [] arrAccounts = setKeys.toArray(new String [setKeys.size()]);
		boxBudget = new JComboBox<String>(objParams.getBudgetList());
		strBudget = objParams.getBudget();
		objParams.loadBudgetData(strBudget);
		if (strBudget != null && !strBudget.equals("")) {
			boxBudget.setSelectedItem(strBudget);
		}
		boxIncome = new JComboBox<String>(arrAccounts);
		boxIncome.setSelectedItem(objParams.getDefaultIncome());
		boxIncome.addItemListener(new ItemListener() {
			@Override
		    public void itemStateChanged(ItemEvent e) {
			       if (e.getStateChange() == ItemEvent.SELECTED) {
						@SuppressWarnings("unchecked")
						JComboBox<String> boxTemp = (JComboBox<String>)e.getSource();
						objParams.setDefaultIncomeAccount((String)boxTemp.getSelectedItem());
						modIncome.rebuildLines(Constants.ScreenType.INCOME);
						BudgetPane.this.revalidate();
			       }
			}
		});
		boxExpense = new JComboBox<String>(arrAccounts);
		boxExpense.setSelectedItem(objParams.getDefaultExpense());
		boxExpense.addItemListener(new ItemListener() {
			@Override
		    public void itemStateChanged(ItemEvent e) {
			       if (e.getStateChange() == ItemEvent.SELECTED) {
						@SuppressWarnings("unchecked")
						JComboBox<String> boxTemp = (JComboBox<String>)e.getSource();
						objParams.setDefaultExpenseAccount((String)boxTemp.getSelectedItem());
						modExpense.rebuildLines(Constants.ScreenType.EXPENSE);
						BudgetPane.this.revalidate();
			       }
			}
		});
		modIncome = new BudgetTableModel(objParams,strBudget,Constants.ScreenType.INCOME);
		modExpense = new BudgetTableModel(objParams,strBudget,Constants.ScreenType.EXPENSE);
		tabIncome = new BudgetTable(modIncome,arrAccounts);
		tabExpense = new BudgetTable(modExpense,arrAccounts);
        /*
         * set up panels
         */
		spIncome = new JScrollPane(tabIncome);
		spExpense = new JScrollPane(tabExpense);
		panIncome = new JPanel(new BorderLayout());
		panExpense = new JPanel(new BorderLayout());
		panIncome.add(spIncome,BorderLayout.CENTER);
		panExpense.add(spExpense,BorderLayout.CENTER);
		panTabs.addTab("Income Budgets",panIncome);
		panTabs.addTab("Expense Budgets",panExpense);
		setLayout(new BorderLayout());
		JPanel panTop = new JPanel(new GridBagLayout());
		panTop.add(lblInstruct,GridC.getc(0,0).colspan(4).insets(0,0,0,0));
		panTop.add(lblBudget,GridC.getc(0,1).insets(5,5,5,5).west());
		panTop.add(boxBudget,GridC.getc(1,1).insets(5,5,5,5).west());
		panTop.add(lblDefaultIncome,GridC.getc(0,2).insets(5,5,5,5).west());
		panTop.add(boxIncome,GridC.getc(1,2).insets(5,5,5,5).west());
		panTop.add(lblDefaultExpense,GridC.getc(2,2).insets(5,5,5,5).west());
		panTop.add(boxExpense,GridC.getc(3,2).insets(5,5,5,5).west()); 
		add(panTop, BorderLayout.NORTH);
		add(panTabs,BorderLayout.CENTER);
	}
	/*
	 * Listener code
	 * If parameters have changed capture which ones
	 */
	@Override
	public void parametersChanged(Constants.ParameterType enumType) {
		switch (enumType) {
		case ALL :
			bAccountsChanged = true;
			/*
			 * when budget data changes externally or the dates on the selector pane change
			 * budget data needs to be reloaded
			 */
			objParams.loadBudgetData(strBudget);
			if (modIncome != null) {
				modIncome.rebuildLines(Constants.ScreenType.INCOME);
				modIncome.fireTableDataChanged();
			}
			if (modExpense != null) {
				modExpense.rebuildLines(Constants.ScreenType.EXPENSE);
				modExpense.fireTableDataChanged();
			}
			BudgetPane.this.revalidate();
			break;
		case ACCOUNTS :
			bAccountsChanged = true;
			break;
		case BUDGETDATA :
		case DATES :
			/*
			 * when budget data changes externally or the dates on the selector pane change
			 * budget data needs to be reloaded
			 */
			objParams.loadBudgetData(strBudget);
			if (modIncome != null) {
				modIncome.rebuildLines(Constants.ScreenType.INCOME);
				modIncome.fireTableDataChanged();
			}
			if (modExpense != null) {
				modExpense.rebuildLines(Constants.ScreenType.EXPENSE);
				modExpense.fireTableDataChanged();
			}
			BudgetPane.this.revalidate();
			break;
		case BUDGETS :
			boxBudget.removeAllItems();
			for (String strItem :objParams.getBudgetList()){
				boxBudget.addItem(strItem);
			}
			boxBudget.setSelectedItem(strBudget);
			BudgetPane.this.revalidate();
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
			String strIncome = (String) boxIncome.getSelectedItem();
			String strExpense = (String) boxExpense.getSelectedItem();
			boxIncome.removeAllItems();
			boxExpense.removeAllItems();
			mapAccounts = objParams.getSelectedAccts();
			Set<String> setKeys = mapAccounts.keySet();
			String [] arrAccounts = setKeys.toArray(new String [setKeys.size()]);
			for (int i=0;i<arrAccounts.length;i++) {
				boxIncome.addItem(arrAccounts[i]);
				boxExpense.addItem(arrAccounts[i]);
			}
			boxIncome.setSelectedItem(strIncome);
			boxExpense.setSelectedItem(strExpense);
			if (tabIncome != null)
				tabIncome.resetAccounts(arrAccounts);
			if (tabExpense != null)
				tabExpense.resetAccounts(arrAccounts);
			bAccountsChanged = false;
		}
	}

}