package com.moneydance.modules.features.jasperreports;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectionDataPane  {
	private Parameters params;
	private TextField name;
	private CheckBox accountsCB;
	private CheckBox accountTypesCB;
	private CheckBox addressCB ;
	private CheckBox budgetsCB;
	private CheckBox currenciesCB;
	private CheckBox securityCB;
	private CheckBox transactionsCB;
	private Stage stage;
	private Scene scene;
	private GridPane pane;
	private SelectionDataRow row;
	private boolean newRow = false;
	
	public SelectionDataPane(Parameters paramsp) {
		params = paramsp;
		row = new SelectionDataRow();
		newRow= true;
	}
	public SelectionDataPane(Parameters paramsp, SelectionDataRow rowp) {
		row = rowp;
		params = paramsp;
	}
	public SelectionDataRow displayPanel() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new GridPane();
		scene = new Scene(pane,400,200);
		stage.setScene(scene);
		Label selectionLbl = new Label("Selection Name");
		name = new TextField();
		name.setPadding(new Insets(10,10,10,10));
		accountsCB = new CheckBox("Accounts");
		accountsCB.setPadding(new Insets(10,10,10,10));
		accountTypesCB = new CheckBox("Account Types");
		accountTypesCB.setPadding(new Insets(10,10,10,10));
		addressCB = new CheckBox("Addresses");
		addressCB.setPadding(new Insets(10,10,10,10));
		budgetsCB = new CheckBox("Budgets");
		budgetsCB.setPadding(new Insets(10,10,10,10));
		currenciesCB = new CheckBox("Currencies");
		currenciesCB.setPadding(new Insets(10,10,10,10));
		securityCB = new CheckBox("Securities");
		securityCB.setPadding(new Insets(10,10,10,10));
		transactionsCB = new CheckBox("Transactions");
		transactionsCB.setPadding(new Insets(10,10,10,10));
		if (!newRow) {
			name.setText(row.getName());
			accountsCB.setSelected(row.getAccounts());
			accountTypesCB.setSelected(row.getAccountTypes());
			addressCB.setSelected(row.getAddress());
			budgetsCB.setSelected(row.getBudgets());
			currenciesCB.setSelected(row.getCurrency());
			securityCB.setSelected(row.getSecurity());
			transactionsCB.setSelected(row.getTransactions());
		}
		int ix = 1;
		int iy = 0;
		pane.add(selectionLbl, ix++, iy);
		pane.add(name, ix, iy++);
		ix = 0;
		pane.add(accountsCB, ix++, iy);
		pane.add(accountTypesCB,ix++, iy);
		pane.add(addressCB,ix++, iy);
		pane.add(budgetsCB,ix, iy++);
		ix = 0;
		pane.add(currenciesCB,ix++, iy);
		pane.add(securityCB, ix++, iy);
		pane.add(transactionsCB,ix, iy++);
		Button okBtn = new Button("OK");
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (name.getText().isEmpty()) {
					OptionMessage.displayMessage("Name must be entered");
					return;
				}
				int count = 0;
				if (accountsCB.isSelected()) 
					count++;
				if (accountTypesCB.isSelected())
					count++;
				if (addressCB.isSelected())
					count++;
				if (budgetsCB.isSelected())
					count++;
				if (currenciesCB.isSelected())
					count++;
				if (securityCB.isSelected())
					count++;
				if (transactionsCB.isSelected())
					count++;
				if (count < 1) {
					OptionMessage.displayMessage("At least one selection must be chosen");
					return;
				}
				else
				{
					boolean createRow;
					SelectionDataRow tempRow = new SelectionDataRow();
					if (newRow && tempRow.loadRow(name.getText(), params)) {
						if (OptionMessage.yesnoMessage("Selection Template already exists.  Do you wish to overwrite it?")) {
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
						row.setName(name.getText());
						row.setAccounts(accountsCB.isSelected());
						row.setAccountTypes(accountTypesCB.isSelected());
						row.setAddress(addressCB.isSelected());
						row.setBudgets(budgetsCB.isSelected());
						row.setCurrency(currenciesCB.isSelected());
						row.setSecurity(securityCB.isSelected());
						row.setTransactions(transactionsCB.isSelected());
						row.saveRow(params);
					}
				}
				stage.close();
			}
		});
		Button cancelBtn = new Button("Cancel");
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				row=null;
				stage.close();
			}
		});
		ix=1;
		pane.add(okBtn, ix++, iy);
		pane.add(cancelBtn,ix++, iy);
		stage.showAndWait();
		return row;
	}



}
