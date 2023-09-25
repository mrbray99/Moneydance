package com.moneydance.modules.features.reportwriter2.view;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.databeans.AccountBean;
import com.moneydance.modules.features.reportwriter2.databeans.AddressBean;
import com.moneydance.modules.features.reportwriter2.databeans.BudgetBean;
import com.moneydance.modules.features.reportwriter2.databeans.BudgetItemBean;
import com.moneydance.modules.features.reportwriter2.databeans.CategoryBean;
import com.moneydance.modules.features.reportwriter2.databeans.CurrencyBean;
import com.moneydance.modules.features.reportwriter2.databeans.CurrencyRateBean;
import com.moneydance.modules.features.reportwriter2.databeans.DataBean;
import com.moneydance.modules.features.reportwriter2.databeans.InvTranBean;
import com.moneydance.modules.features.reportwriter2.databeans.LotsBean;
import com.moneydance.modules.features.reportwriter2.databeans.ReminderBean;
import com.moneydance.modules.features.reportwriter2.databeans.SecurityBean;
import com.moneydance.modules.features.reportwriter2.databeans.SecurityPriceBean;
import com.moneydance.modules.features.reportwriter2.databeans.TransactionBean;
import com.moneydance.modules.features.reportwriter2.view.controls.MyGridPane;
import com.moneydance.modules.features.reportwriter2.view.tables.SelectionDataRow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectionDataPane extends ScreenDataPane {
	private Parameters params;
	private TextField name;
	private Button accountsFieldBtn;
	private Button addressFieldBtn;
	private Button budgetsFieldBtn;
	private Button currenciesFieldBtn;
	private Button securitiesFieldBtn;
	private Button transactionsFieldBtn;
	private Button categoriesFieldBtn;
	private Button invTransFieldBtn;
	private Button budgetItemsFieldBtn;
	private Button securityPricesFieldBtn;
	private Button lotsFieldBtn;
	private Button currencyRatesFieldBtn;
	private Button remindersFieldBtn;
	private CheckBox accountsCB;
	private CheckBox acctSecCB;
	private CheckBox addressCB ;
	private CheckBox budgetsCB;
	private CheckBox currenciesCB;
	private CheckBox securitiesCB;
	private CheckBox lotsCB;
	private CheckBox transactionsCB;
	private CheckBox invTransCB;
	private CheckBox budgetItemsCB;
	private CheckBox categoriesCB;
	private CheckBox securityPricesCB;
	private CheckBox currencyRatesCB;
	private CheckBox remindersCB;
	private GridPane pane;
	private SelectionDataRow row;
	private boolean newRow = false;
	private boolean dirty=false;
	private SortedMap<String,DataParameter> dataParams;
	
	public SelectionDataPane(Parameters params) {
		super();
		screenName = "SelectionDataPane";
		screenTitle="Selection Data Groups";
		this.params = params;
		row = new SelectionDataRow();
		dataParams = new TreeMap<String,DataParameter>();
		newRow= true;
	}
	public SelectionDataPane(Parameters params, SelectionDataRow row) {
		super();
		screenName = "SelectionDataPane";
		this.row = row;
		this.params = params;
		dataParams = row.getParameters();
		if (dataParams == null )
			dataParams = new TreeMap<String,DataParameter>();	
}
	public SelectionDataRow displayPanel() {
		DEFAULTSCREENWIDTH = Constants.DATASELECTSCREENWIDTH;
		DEFAULTSCREENHEIGHT = Constants.DATASELECTSCREENHEIGHT;
		setStage(new Stage());
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new MyGridPane(Constants.WINSELECTIONDATA);
		scene = new Scene(pane);
		scene.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
		stage.setScene(scene);
		stage.setOnCloseRequest(ev->{
			if (dirty) {
				if (OptionMessage.yesnoMessage("Parameters have changed.  Do you wish to abandon them?")) {
					row = null;
					stage.close();
				}
				else
					ev.consume();
			}
			else {
				row = null;
				stage.close();
			}
		});
		Main.accels.setSceneSave(scene, new Runnable () {
			@Override
			public void run() {
				if (saveRow()) {
					stage.close();
				}
			}
		});
		Main.accels.setSceneClose(scene, new Runnable () {
			@Override
			public void run() {
				if (dirty) {
					if (OptionMessage.yesnoMessage("Parameters have changed.  Do you wish to abandon them?")) {
						row = null;
						stage.close();
					}
				}
				else {
					row = null;
					stage.close();
				}
			}
		});	
		resize();
		Label selectionLbl = new Label("Selection Name");
		name = new TextField();
		name.textProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		GridPane.setMargin(name,new Insets(10,10,10,10));
		accountsCB = new CheckBox("Accounts");
		accountsFieldBtn = new Button("Fields");
		accountsCB.setPadding(new Insets(10,10,10,10));
		accountsCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(accountsCB, accountsFieldBtn);
			if (newv) {
				acctSecCB.setDisable(false);
			}
			else {
				acctSecCB.setDisable(true);
			}
		});
		acctSecCB = new CheckBox("Include Securities");
		acctSecCB.setPadding(new Insets(10,10,10,10));
		acctSecCB.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		GridPane.setMargin(accountsFieldBtn,new Insets(10,10,10,10));
		accountsFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new AccountBean(),Constants.PARMFLDACCT,accountsCB);
				dirty=true;
			}
		});
		addressCB = new CheckBox("Addresses");
		addressCB.setPadding(new Insets(10,10,10,10));
		addressCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(addressCB, addressFieldBtn);
		});
		addressFieldBtn = new Button("Fields");
		GridPane.setMargin(addressFieldBtn,new Insets(10,10,10,10));
		addressFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new AddressBean(),Constants.PARMFLDADDR,addressCB);
				dirty=true;
			}
		});
		budgetsCB = new CheckBox("Budgets");
		budgetsCB.setPadding(new Insets(10,10,10,10));
		budgetsCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(budgetsCB, budgetsFieldBtn);
		});
		budgetsFieldBtn = new Button("Fields");
		GridPane.setMargin(budgetsFieldBtn,new Insets(10,10,10,10));
		budgetsFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new BudgetBean(),Constants.PARMFLDBUDG,budgetsCB);
				dirty=true;
			}
		});
		currenciesCB = new CheckBox("Currencies");
		currenciesCB.setPadding(new Insets(10,10,10,10));
		currenciesCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(currenciesCB, currenciesFieldBtn);
		});
		currenciesFieldBtn = new Button("Fields");
		GridPane.setMargin(currenciesFieldBtn,new Insets(10,10,10,10));
		currenciesFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new CurrencyBean(),Constants.PARMFLDCUR,currenciesCB);
				dirty=true;
			}
		});
		securitiesCB = new CheckBox("Securities");
		securitiesCB.setPadding(new Insets(10,10,10,10));
		securitiesCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(securitiesCB, securitiesFieldBtn);
		});
		securitiesFieldBtn = new Button("Fields");
		GridPane.setMargin(securitiesFieldBtn,new Insets(10,10,10,10));
		securitiesFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new SecurityBean(),Constants.PARMFLDSEC,securitiesCB);
				dirty=true;
			}
		});
		transactionsCB = new CheckBox("Transactions");
		transactionsCB.setPadding(new Insets(10,10,10,10));
		transactionsCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(transactionsCB, transactionsFieldBtn);
		});
		transactionsFieldBtn = new Button("Fields");
		GridPane.setMargin(transactionsFieldBtn,new Insets(10,10,10,10));
		transactionsFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new TransactionBean(),Constants.PARMFLDTRAN,transactionsCB);
				dirty=true;
			}
		});
		invTransCB = new CheckBox("Invest. Trans");
		invTransCB.setPadding(new Insets(10,10,10,10));
		invTransCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(invTransCB, invTransFieldBtn);
		});
		invTransFieldBtn = new Button("Fields");
		GridPane.setMargin(invTransFieldBtn,new Insets(10,10,10,10));
		invTransFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new InvTranBean(),Constants.PARMFLDINVTRAN,invTransCB);
				dirty=true;
			}
		});
		lotsCB = new CheckBox("Security Lots");
		lotsCB.setPadding(new Insets(10,10,10,10));
		lotsCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(lotsCB, lotsFieldBtn);
		});
		lotsFieldBtn = new Button("Fields");
		GridPane.setMargin(lotsFieldBtn,new Insets(10,10,10,10));
		lotsFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new LotsBean(),Constants.PARMFLDLOTS,lotsCB);
				dirty=true;
			}
		});
		budgetItemsCB = new CheckBox("Budget Items");
		budgetItemsCB.setPadding(new Insets(10,10,10,10));
		budgetItemsCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(budgetItemsCB, budgetItemsFieldBtn);
		});
		budgetItemsFieldBtn = new Button("Fields");
		GridPane.setMargin(budgetItemsFieldBtn,new Insets(10,10,10,10));
		budgetItemsFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new BudgetItemBean(),Constants.PARMFLDBUDGI,budgetItemsCB);
				dirty=true;
			}
		});
		categoriesCB = new CheckBox("Categories");
		categoriesCB.setPadding(new Insets(10,10,10,10));
		categoriesCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(categoriesCB, categoriesFieldBtn);
		});
		categoriesFieldBtn = new Button("Fields");
		GridPane.setMargin(categoriesFieldBtn,new Insets(10,10,10,10));
		categoriesFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new CategoryBean(),Constants.PARMFLDCAT,categoriesCB);
				dirty=true;
			}
		});
		securityPricesCB = new CheckBox("Sec. prices");
		securityPricesCB.setPadding(new Insets(10,10,10,10));
		securityPricesCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(securityPricesCB, securityPricesFieldBtn);
		});
		securityPricesFieldBtn = new Button("Fields");
		GridPane.setMargin(securityPricesFieldBtn,new Insets(10,10,10,10));
		securityPricesFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new SecurityPriceBean(),Constants.PARMFLDSECP,securityPricesCB);
				dirty=true;
			}
		});
		currencyRatesCB = new CheckBox("Currency Rates");
		currencyRatesCB.setPadding(new Insets(10,10,10,10));
		currencyRatesCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(currencyRatesCB, currencyRatesFieldBtn);
		});
		currencyRatesFieldBtn = new Button("Fields");
		GridPane.setMargin(currencyRatesFieldBtn,new Insets(10,10,10,10));
		currencyRatesFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new CurrencyRateBean(),Constants.PARMFLDCURR,currencyRatesCB);
				dirty=true;
			}
		});
		remindersCB = new CheckBox("Reminders");
		remindersCB.setPadding(new Insets(10,10,10,10));
		remindersCB.selectedProperty().addListener((ov,oldv,newv)->{
			dirty=true;
			setFieldBtn(remindersCB, remindersFieldBtn);
		});
		remindersFieldBtn = new Button("Fields");
		GridPane.setMargin(remindersFieldBtn,new Insets(10,10,10,10));
		remindersFieldBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				displayFields(new ReminderBean(),Constants.PARMFLDREM,remindersCB );
				dirty=true;
			}
		});
		if (!newRow) {
			name.setText(row.getName());
			accountsCB.setSelected(row.getAccounts());
			acctSecCB.setSelected(row.getAcctSec());
			addressCB.setSelected(row.getAddress());
			budgetsCB.setSelected(row.getBudgets());
			currenciesCB.setSelected(row.getCurrencies());
			securitiesCB.setSelected(row.getSecurities());
			transactionsCB.setSelected(row.getTransactions());
			invTransCB.setSelected(row.getInvTrans());
			lotsCB.setSelected(row.getLots());
			budgetItemsCB.setSelected(row.getBudgetItems());
			categoriesCB.setSelected(row.getCategories());
			securityPricesCB.setSelected(row.getSecurityPrices());
			currencyRatesCB.setSelected(row.getCurrencyRates());
			remindersCB.setSelected(row.getReminders());
		}
		setFieldBtn(accountsCB, accountsFieldBtn);
		setFieldBtn(addressCB, addressFieldBtn);
		setFieldBtn(budgetsCB, budgetsFieldBtn);
		setFieldBtn(currenciesCB, currenciesFieldBtn);
		setFieldBtn(securitiesCB, securitiesFieldBtn);
		setFieldBtn(securitiesCB, securitiesFieldBtn);
		setFieldBtn(transactionsCB, transactionsFieldBtn);
		setFieldBtn(invTransCB, invTransFieldBtn);
		setFieldBtn(lotsCB, lotsFieldBtn);
		setFieldBtn(budgetItemsCB, budgetItemsFieldBtn);
		setFieldBtn(categoriesCB, categoriesFieldBtn);
		setFieldBtn(securityPricesCB, securityPricesFieldBtn);
		setFieldBtn(currencyRatesCB, currencyRatesFieldBtn);
		setFieldBtn(remindersCB, remindersFieldBtn);
		int ix = 0;
		int iy = 0;
		pane.add(selectionLbl, ix++, iy);
		pane.add(name, ix, iy++);
		GridPane.setColumnSpan(name, 2);
		ix = 0;
		pane.add(accountsCB, ix++, iy);
		pane.add(accountsFieldBtn, ix++, iy);
		pane.add(acctSecCB, ix++, iy);
		ix++;
		pane.add(addressCB,ix++, iy);
		pane.add(addressFieldBtn, ix++, iy);
		pane.add(budgetsCB,ix++, iy);
		pane.add(budgetsFieldBtn, ix, iy++);
		ix=0;
		pane.add(transactionsCB,ix++, iy);
		pane.add(transactionsFieldBtn, ix++, iy);
		pane.add(invTransCB,ix++, iy);
		pane.add(invTransFieldBtn, ix++, iy);
		pane.add(lotsCB,ix++, iy);
		pane.add(lotsFieldBtn, ix++, iy);
		pane.add(budgetItemsCB,ix++, iy);
		pane.add(budgetItemsFieldBtn, ix, iy++);
		ix=0;
		pane.add(securitiesCB, ix++, iy);
		pane.add(securitiesFieldBtn, ix++, iy);
		pane.add(currenciesCB,ix++, iy);
		pane.add(currenciesFieldBtn, ix++, iy);
		pane.add(categoriesCB,ix++, iy);
		pane.add(categoriesFieldBtn, ix, iy++);
		ix=0;
		pane.add(securityPricesCB,ix++, iy);
		pane.add(securityPricesFieldBtn, ix++, iy);
		pane.add(currencyRatesCB,ix++, iy);
		pane.add(currencyRatesFieldBtn, ix++, iy);
		pane.add(remindersCB,ix++, iy);
		pane.add(remindersFieldBtn, ix, iy++);
		ix=0;
		Button okBtn = new Button();
		GridPane.setMargin(okBtn,new Insets(10,10,10,10));
		if (Main.loadedIcons.okImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (saveRow()) {
					stage.close();
				}
			}
		});
		Button cancelBtn = new Button();
		GridPane.setMargin(cancelBtn,new Insets(10,10,10,10));
		if (Main.loadedIcons.cancelImg == null)
			cancelBtn.setText("Cancel");
		else
			cancelBtn.setGraphic(new ImageView(Main.loadedIcons.cancelImg));
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
		dirty=false;
		stage.showAndWait();
		return row;
	}
	private void setFieldBtn(CheckBox cb, Button btn) {
		if (cb.isSelected())
			btn.setDisable(false);
		else
			btn.setDisable(true);
	}
	private void displayFields(DataBean bean, String parm, CheckBox box) {
		if (!box.isSelected()) {
			OptionMessage.displayMessage("You must select the records before selecting fields");
			return;
		}
		List<String> selectedFields;
		if (dataParams.containsKey(parm)) 
			selectedFields = dataParams.get(parm).getList();
		else
			selectedFields = new ArrayList<String>();
		FieldSelectionPane fieldPane = new FieldSelectionPane(parm,bean,selectedFields);
		selectedFields= fieldPane.displayPanel();
		if (selectedFields.isEmpty()) {
			if(dataParams.containsKey(parm))
				dataParams.remove(parm);
		}
		else {
			if (dataParams.containsKey(parm))
				dataParams.get(parm).setList(selectedFields);
			else {
					DataParameter newParams = new DataParameter();
					newParams.setList(selectedFields);
					dataParams.put(parm, newParams);
			}
		}
			
	}
	private boolean saveRow() {
		if (name.getText().isEmpty()) {
			OptionMessage.displayMessage("Name must be entered");
			return false;
		}
		int count = 0;
		if (accountsCB.isSelected()) 
			count++;
		if (addressCB.isSelected())
			count++;
		if (budgetsCB.isSelected())
			count++;
		if (currenciesCB.isSelected())
			count++;
		if (securitiesCB.isSelected())
			count++;
		if (lotsCB.isSelected())
			count++;
		if (transactionsCB.isSelected())
			count++;
		if (invTransCB.isSelected())
			count++;
		if (budgetItemsCB.isSelected())
			count++;
		if (categoriesCB.isSelected())
			count++;
		if (securityPricesCB.isSelected())
			count++;
		if (currencyRatesCB.isSelected())
			count++;
		if (remindersCB.isSelected())
			count++;
		if (count < 1) {
			OptionMessage.displayMessage("At least one selection must be chosen");
			return false;
		}
		else
		{
			boolean updateName=false;
			SelectionDataRow tempRow = new SelectionDataRow();
			if (!newRow && !row.getName().equals(name.getText()))
				updateName=true;
			if ((newRow || updateName) && tempRow.loadRow(name.getText(), params)) {
				if (OptionMessage.yesnoMessage("Data Parameters already exists.  Do you wish to overwrite them?"))  
					tempRow.delete(params);
				else
					return false;
			}
			updateParms();
			if (newRow) {
				row.setName(name.getText());
				row.saveRow(params);
			}
			else {
				if (updateName) 
					row.renameRow(name.getText(),params);
				else
					row.saveRow(params);
			}
		}
		return true;
	}
	private void updateParms() {
		row.setAccounts(accountsCB.isSelected());
		row.setAcctSec(acctSecCB.isSelected());
		row.setAddress(addressCB.isSelected());
		row.setBudgets(budgetsCB.isSelected());
		row.setTransactions(transactionsCB.isSelected());
		row.setInvTrans(invTransCB.isSelected());
		row.setLots(lotsCB.isSelected());
		row.setCurrencies(currenciesCB.isSelected());
		row.setSecurities(securitiesCB.isSelected());
		row.setBudgetItems(budgetItemsCB.isSelected());
		row.setCategories(categoriesCB.isSelected());
		row.setSecurityPrices(securityPricesCB.isSelected());
		row.setCurrencyRates(currencyRatesCB.isSelected());
		row.setReminders(remindersCB.isSelected());
		row.setParameters(dataParams);
	}

}
