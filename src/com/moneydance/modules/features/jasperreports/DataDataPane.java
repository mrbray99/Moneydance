package com.moneydance.modules.features.jasperreports;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.mrbutil.MRBFXSelectionPanel;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class DataDataPane {
	private Parameters params;
	private TextField name;
	private SelectionDataRow selRow;
	private SortedMap<String,DataParameter> parameters;
	private Stage stage;
	private Scene scene;
	private GridPane pane;
	private DataDataRow row;
	private GridPane parmPanes;
	private boolean newRow = false;
	private DatePicker fromDate;
	private DatePicker toDate;
	private CheckBox selectAccounts;
	private CheckBox selectCategories;
	private CheckBox selectBudgets;
	private CheckBox selectCurrency;
	private CheckBox selectTrans;
	private CheckBox selAsset;
	private CheckBox selBank;
	private CheckBox selCredit;
	private CheckBox selInvestment;
	private CheckBox selLiability;
	private CheckBox selLoan;
	private Button acctSelBtn;
	private CheckBox selIncome;
	private CheckBox selExpense;
	private Button catSelBtn;
	private ComboBox<String> budgets;
	private CheckBox selBudItems;
	private Button currSelBtn;
	private CheckBox selCleared;
	private CheckBox selReconciling;
	private CheckBox selUnreconciled;
	private Button ttypSelBtn;
	private CheckBox selOtherSide;
	private MRBFXSelectionPanel acctPanel=null;
	private MRBFXSelectionPanel catPanel=null;
	private MRBFXSelectionPanel currPanel=null;
	private MRBFXSelectionPanel ttypPanel=null;
	


	public DataDataPane(Parameters paramsp) {
		params = paramsp;
		row = new DataDataRow();
		parameters = new TreeMap<>();
		row.setParameters(parameters);
		newRow= true;
		selRow = new SelectionDataRow();
	}
	public DataDataPane(Parameters paramsp, DataDataRow rowp) {
		row = rowp;
		params = paramsp;
		selRow = new SelectionDataRow();
		selRow.loadRow(row.getName(), params);
		parameters = row.getParameters();
	}
	public DataDataRow displayPanel() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new GridPane();
		scene = new Scene(pane,600,500);
		stage.setScene(scene);

		int ix = 0;
		int iy=0;
		parmPanes = setParameters();
		pane.add(parmPanes, ix, iy++);
		GridPane.setMargin(parmPanes, new Insets(10,10,10,10));
		GridPane.setColumnSpan(parmPanes, 3);
		ix = 0;
		Button okBtn = new Button("OK");
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (name.getText().isEmpty()) {
					OptionMessage.displayMessage("Name must be entered");
					return;
				}
				boolean createRow;
				DataDataRow tempRow = new DataDataRow();
				if (newRow && tempRow.loadRow(name.getText(), params)) {
					if (OptionMessage.yesnoMessage("Data Parameters already exists.  Do you wish to overwrite them?")) {
						createRow = true;
						/* 
						 * new row exists and user wishes to overwrite
						 */
					}
					else
						createRow= false;
					/*
					 * new row exists and user does not wish to overwrite 
					 */
				}
				else {
					createRow = true;
					/*
					 * edit mode or new row does not exist
					 */
				}
				if (createRow) {	
					if (row==null)
						row = new DataDataRow();
					row.setName(name.getText());
					updateParms();
					row.saveRow(params);
				}
				stage.close();
			}
		});
		Button cancelBtn = new Button("Cancel");
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				row = null;
				stage.close();
			}
		});
		ix=1;
		pane.add(okBtn, ix++, iy);
		GridPane.setMargin(okBtn, new Insets(10,10,10,10));
		pane.add(cancelBtn,ix++, iy);
		GridPane.setMargin(cancelBtn, new Insets(10,10,10,10));
		stage.showAndWait();
		return row;
	}

	private GridPane setParameters() {
		GridPane pane = new GridPane();
		name = new TextField();
		if (!newRow) {
			name.setText(row.getName());
			name.setDisable(true);
		}
		Label nameLbl = new Label("Name");
		Label fromDateLbl = new Label("From Date");
		fromDate = new DatePicker();
		if (parameters.containsKey(Constants.PARMFROMDATE))
			fromDate.setValue(LocalDate.parse(parameters.get(Constants.PARMFROMDATE).getValue(), Main.cdateFX));
		else
			fromDate.setValue(LocalDate.now());
		Label toDateLbl = new Label("To Date");
		toDate = new DatePicker();
		if (parameters.containsKey(Constants.PARMTODATE))
			toDate.setValue(LocalDate.parse(parameters.get(Constants.PARMTODATE).getValue(), Main.cdateFX));
		else
			toDate.setValue(LocalDate.now());
		selectAccounts = new CheckBox("Select Accounts");
		selectAccounts.setSelected(parameters.containsKey(Constants.PARMSELACCT)?true:false);
		selectCategories = new CheckBox("Select Categories");
		selectCategories.setSelected(parameters.containsKey(Constants.PARMSELCAT)?true:false);
		selectBudgets = new CheckBox("Select Budgets");
		selectBudgets.setSelected(parameters.containsKey(Constants.PARMSELBUDGET)?true:false);
		selectCurrency = new CheckBox("Select Currencies");
		selectCurrency.setSelected(parameters.containsKey(Constants.PARMSELCURRENCY)?true:false);
		selectTrans = new CheckBox ("Select Transactions");
		selectTrans.setSelected(parameters.containsKey(Constants.PARMSELTRANS)?true:false);
		selAsset = new CheckBox("Assets");
		selAsset.setSelected(parameters.containsKey(Constants.PARMASSET)?true:false);
		selBank = new CheckBox("Banks");
		selBank.setSelected(parameters.containsKey(Constants.PARMBANK)?true:false);
		selCredit = new CheckBox("Credit Cards");
		selCredit.setSelected(parameters.containsKey(Constants.PARMCREDIT)?true:false);
		selInvestment = new CheckBox("Investments");
		selInvestment.setSelected(parameters.containsKey(Constants.PARMINVESTMENT)?true:false);
		selLiability = new CheckBox("Liabilities");
		selLiability.setSelected(parameters.containsKey(Constants.PARMLIABILITY)?true:false);
		selLoan = new CheckBox("Loans");
		selLoan.setSelected(parameters.containsKey(Constants.PARMLOAN)?true:false);
		acctSelBtn = new Button("Select");
		acctSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<String> missing = new ArrayList<>();
				List<String> selected;
				if (parameters.containsKey(Constants.PARMACCOUNTS))
					selected = parameters.get(Constants.PARMACCOUNTS).getList();
				else
					selected = new ArrayList<>();
				if (selAsset.isSelected())
					missing.addAll(Main.extension.assetAccounts);
				if (selBank.isSelected())
					missing.addAll(Main.extension.bankAccounts);
				if (selCredit.isSelected())
					missing.addAll(Main.extension.creditAccounts);
				if (selLiability.isSelected())
					missing.addAll(Main.extension.liabilityAccounts);
				if (selInvestment.isSelected())
					missing.addAll(Main.extension.investmentAccounts);
				if (selLoan.isSelected())
					missing.addAll(Main.extension.loanAccounts);
				if (missing.isEmpty()) {
					OptionMessage.displayMessage("Please select at least one type of account");
					return;
				}
				List<String> remove = new ArrayList<>();
				List<String> removeSel = new ArrayList<>();
				for (String acct : selected) {
					if (missing.contains(acct))
						remove.add(acct);
					else
						removeSel.add(acct);
				}
				for (String acct : remove) {
					missing.remove(acct);
				}
				for (String acct : removeSel) {
					selected.remove(acct);
				}
				acctPanel = new MRBFXSelectionPanel(missing,selected);
				acctPanel.display();
			}
		});
		selIncome = new CheckBox("Income");
		selIncome.setSelected(parameters.containsKey(Constants.PARMINCOME)?true:false);
		selExpense = new CheckBox("Expense");
		selExpense.setSelected(parameters.containsKey(Constants.PARMEXPENSE)?true:false);
		catSelBtn = new Button("Select");
		catSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<String> missing = new ArrayList<>();
				List<String> selected;
				if (parameters.containsKey(Constants.PARMCATEGORIES))
					selected = parameters.get(Constants.PARMCATEGORIES).getList();
				else
					selected = new ArrayList<>();
				if (selIncome.isSelected())
					missing.addAll(Main.extension.incomeCategories);
				if (selExpense.isSelected())
					missing.addAll(Main.extension.expenseCategories);
				if (missing.isEmpty()) {
					OptionMessage.displayMessage("Please select at least one type of category");
					return;
				}
				List<String> remove = new ArrayList<>();
				List<String> removeSel = new ArrayList<>();
				for (String acct : selected) {
					if (missing.contains(acct))
						remove.add(acct);
					else
						removeSel.add(acct);
				}
				for (String acct : remove) {
					missing.remove(acct);
				}
				for (String acct : removeSel) {
					selected.remove(acct);
				}
				catPanel = new MRBFXSelectionPanel(missing,selected);
				catPanel.display();
			}
		});
		budgets = new ComboBox<>();
		budgets.setItems(FXCollections.observableArrayList(Main.extension.budgets));
		if (parameters.containsKey(Constants.PARMBUDGET)) 
			budgets.getSelectionModel().select(parameters.get(Constants.PARMBUDGET).getValue());
		selBudItems = new CheckBox("Include Budget Items");
		selBudItems.setSelected(parameters.containsKey(Constants.PARMBUDITEMS)?true:false);
		currSelBtn = new Button("Select Currencies");
		currSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<String> missing = new ArrayList<>(Main.extension.currencies);
				List<String> selected;
				if (parameters.containsKey(Constants.PARMCURRENCY))
					selected = parameters.get(Constants.PARMCURRENCY).getList();
				else
					selected = new ArrayList<>();
				List<String> remove = new ArrayList<>();
				List<String> removeSel = new ArrayList<>();
				for (String acct : selected) {
					if (missing.contains(acct))
						remove.add(acct);
					else
						removeSel.add(acct);
				}
				for (String acct : remove) {
					missing.remove(acct);
				}
				for (String acct : removeSel) {
					selected.remove(acct);
				}
				currPanel = new MRBFXSelectionPanel(missing,selected);
				currPanel.display();
			}
		});		
		selCleared = new CheckBox("Cleared");
		selCleared.setSelected(parameters.containsKey(Constants.PARMCLEARED)?true:false);
		selReconciling = new CheckBox("Reconciling");
		selReconciling.setSelected(parameters.containsKey(Constants.PARMRECON)?true:false);
		selUnreconciled = new CheckBox("Unreconciled");
		selUnreconciled.setSelected(parameters.containsKey(Constants.PARMUNRECON)?true:false);
		ttypSelBtn = new Button("Select Transfer Types");
		ttypSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<String> missing = new ArrayList(Main.extension.transferTypes);
				List<String> selected;
				if (parameters.containsKey(Constants.PARMTRANSFER))
					selected = parameters.get(Constants.PARMTRANSFER).getList();
				else
					selected = new ArrayList<>();
				List<String> remove = new ArrayList<>();
				List<String> removeSel = new ArrayList<>();
				for (String acct : selected) {
					if (missing.contains(acct))
						remove.add(acct);
					else
						removeSel.add(acct);
				}
				for (String acct : remove) {
					missing.remove(acct);
				}
				for (String acct : removeSel) {
					selected.remove(acct);
				}
				ttypPanel = new MRBFXSelectionPanel(missing,selected);
				ttypPanel.display();
			}
		});		
		selOtherSide = new CheckBox("Include Other Side");
		selOtherSide.setSelected(parameters.containsKey(Constants.PARMOTHERSIDE)?true:false);
		int ix = 0;
		int iy = 0;
		pane.add(nameLbl, ix++,iy);
		pane.add(name, ix, iy++);
		ix= 1;
		HBox datePane = new HBox();
		datePane.setSpacing(5.0);
		datePane.getChildren().addAll(fromDateLbl,fromDate,toDateLbl,toDate);
		pane.add(datePane, ix, iy++);
		GridPane.setColumnSpan(datePane, 4);
		ix=0;
		pane.add(selectAccounts, ix++, iy);
		pane.add(selAsset, ix++, iy);
		pane.add(selBank, ix++, iy);
		pane.add(selCredit, ix++, iy);
		pane.add(acctSelBtn, ix, iy++);
		ix=1;
		pane.add(selInvestment,ix++,iy);
		pane.add(selLiability, ix++, iy);
		pane.add(selLoan, ix, iy++);
		ix = 0;
		pane.add(selectCategories,ix++,iy);
		pane.add(selIncome, ix++, iy);
		pane.add(selExpense, ix++, iy);
		pane.add(catSelBtn, ix, iy++);
		ix=0;
		pane.add(selectBudgets, ix++, iy);
		pane.add(budgets, ix++, iy);
		pane.add(selBudItems, ix, iy++);
		ix=0;
		pane.add(selectCurrency, ix++, iy);
		pane.add(currSelBtn, ix, iy++);
		ix=0;
		pane.add(selectTrans, ix++, iy);
		pane.add(selCleared, ix++, iy);
		pane.add(selUnreconciled, ix++, iy);
		pane.add(selReconciling, ix, iy++);
		ix=1;
		pane.add(ttypSelBtn, ix, iy++);
		pane.add(selOtherSide, ix, iy);
		GridPane.setColumnSpan(selOtherSide, 2);
		pane.setHgap(10.0);
		pane.setVgap(10.0);
		return pane;
	}
	private void updateParms() {
		DataParameter nullParm = new DataParameter();
		nullParm.setValue(null);
		DataParameter fromDateParm = new DataParameter();
		fromDateParm.setValue(fromDate.getValue().format(Main.cdateFX));
		parameters.put(Constants.PARMFROMDATE, fromDateParm);
		DataParameter toDateParm = new DataParameter();
		toDateParm.setValue(toDate.getValue().format(Main.cdateFX));
		parameters.put(Constants.PARMTODATE, toDateParm);
		if (selectAccounts.isSelected())
			parameters.put(Constants.PARMSELACCT,nullParm);
		else
			parameters.remove(Constants.PARMSELACCT);
		if (selectCategories.isSelected())
			parameters.put(Constants.PARMSELCAT,nullParm);
		else
			parameters.remove(Constants.PARMSELCAT);
		if (selectBudgets.isSelected())
			parameters.put(Constants.PARMSELBUDGET,nullParm);
		else
			parameters.remove(Constants.PARMSELBUDGET);
		if (selectCurrency.isSelected())
			parameters.put(Constants.PARMSELCURRENCY,nullParm);
		else
			parameters.remove(Constants.PARMSELCURRENCY);
		if (selectTrans.isSelected())
			parameters.put(Constants.PARMSELTRANS,nullParm);
		else
			parameters.remove(Constants.PARMSELTRANS);
		if (selAsset.isSelected())
			parameters.put(Constants.PARMASSET,nullParm);
		else
			parameters.remove(Constants.PARMASSET);
		if (selBank.isSelected())
			parameters.put(Constants.PARMBANK,nullParm);
		else
			parameters.remove(Constants.PARMBANK);
		if (selCredit.isSelected())
			parameters.put(Constants.PARMCREDIT,nullParm);
		else
			parameters.remove(Constants.PARMCREDIT);
		if (selLiability.isSelected())
			parameters.put(Constants.PARMLIABILITY,nullParm);
		else
			parameters.remove(Constants.PARMLIABILITY);
		if (selLoan.isSelected())
			parameters.put(Constants.PARMLOAN,nullParm);
		else
			parameters.remove(Constants.PARMLOAN);
		if (selInvestment.isSelected())
			parameters.put(Constants.PARMINVESTMENT,nullParm);
		else
			parameters.remove(Constants.PARMINVESTMENT);
		if (selIncome.isSelected())
			parameters.put(Constants.PARMINCOME,nullParm);
		else
			parameters.remove(Constants.PARMINCOME);
		if (selExpense.isSelected())
			parameters.put(Constants.PARMEXPENSE,nullParm);
		else
			parameters.remove(Constants.PARMEXPENSE);
		if (selBudItems.isSelected())
			parameters.put(Constants.PARMBUDITEMS,nullParm);
		else
			parameters.remove(Constants.PARMBUDITEMS);
		if (selCleared.isSelected())
			parameters.put(Constants.PARMCLEARED,nullParm);
		else
			parameters.remove(Constants.PARMCLEARED);
		if (selReconciling.isSelected())
			parameters.put(Constants.PARMRECON,nullParm);
		else
			parameters.remove(Constants.PARMRECON);
		if (selUnreconciled.isSelected())
			parameters.put(Constants.PARMUNRECON,nullParm);
		else
			parameters.remove(Constants.PARMUNRECON);
		if (selOtherSide.isSelected())
			parameters.put(Constants.PARMOTHERSIDE,nullParm);
		else
			parameters.remove(Constants.PARMOTHERSIDE);
		if(budgets.getSelectionModel().getSelectedItem() == null) 
			parameters.remove(Constants.PARMBUDGET);
		else {
			DataParameter parm = new DataParameter();
			parm.setValue(budgets.getSelectionModel().getSelectedItem());
			parameters.put(Constants.PARMBUDGET, parm);
		}
		if (acctPanel != null)
		{
			DataParameter parm = new DataParameter();
			parm.setList(acctPanel.getSelected());
			parameters.put(Constants.PARMACCOUNTS, parm);
		}
		if (catPanel != null)
		{
			DataParameter parm = new DataParameter();
			parm.setList(catPanel.getSelected());
			parameters.put(Constants.PARMCATEGORIES, parm);
		}
		if (currPanel != null)
		{
			DataParameter parm = new DataParameter();
			parm.setList(currPanel.getSelected());
			parameters.put(Constants.PARMCURRENCY, parm);
		}
		if (ttypPanel != null)
		{
			DataParameter parm = new DataParameter();
			parm.setList(ttypPanel.getSelected());
			parameters.put(Constants.PARMTRANSFER, parm);
		}
	}
}

