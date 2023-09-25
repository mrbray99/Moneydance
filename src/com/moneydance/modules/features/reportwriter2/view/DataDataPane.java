/*
 * Copyright (c) 2020, Michael Bray.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of the author may not used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.moneydance.modules.features.reportwriter2.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.util.DateUtil;
import com.moneydance.modules.features.mrbutil.MRBFXSelectionPanel;
import com.moneydance.modules.features.mrbutil.MRBFXSelectionRow;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.view.controls.MyGridPane;
import com.moneydance.modules.features.reportwriter2.view.tables.DataDataRow;
import com.moneydance.modules.features.reportwriter2.view.tables.SelectionDataRow;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class DataDataPane extends ScreenDataPane {
	private Parameters params;
	private TextField name;
	private SelectionDataRow selRow;
	private SortedMap<String,DataParameter> parameters;
	private Scene scene;
	private MyGridPane pane;
	private DataDataRow row;
	private GridPane parmPanes;
	private HBox buttons;
	private boolean newRow = false;
	private DatePicker fromDate;
	private DatePicker toDate;
	private CheckBox selectDates;
	private CheckBox selectAccounts;
	private CheckBox selectCategories;
	private CheckBox selectBudgets;
	private CheckBox selectCurrencies;
	private CheckBox selectSecurities;
	private CheckBox selectTrans;
	private CheckBox selectInvestTrans;
	private CheckBox selAsset;
	private CheckBox selBank;
	private CheckBox selCredit;
	private CheckBox selInvestment;
	private CheckBox selLiability;
	private CheckBox selLoan;
	private CheckBox today;
	private CheckBox inactiveAccts;
	private Button acctSelBtn;
	private CheckBox selIncome;
	private CheckBox selExpense;
	private Button catSelBtn;
	private Button budgets;
	private Button currSelBtn;
	private Button secSelBtn;
	private CheckBox selCleared;
	private CheckBox selReconciling;
	private CheckBox selUnreconciled;
	private Button tagsSelBtn;
	private TextField fromCheque;
	private TextField toCheque;
	private Button ttypSelBtn;
	private Button invAcctBtn;
	private boolean dirty=false;
	private MRBFXSelectionPanel acctPanel=null;
	private MRBFXSelectionPanel catPanel=null;
	private MRBFXSelectionPanel budPanel=null;
	private MRBFXSelectionPanel currPanel=null;
	private MRBFXSelectionPanel secPanel=null;
	private MRBFXSelectionPanel invPanel=null;
	private MRBFXSelectionPanel ttypPanel=null;
	private MRBFXSelectionPanel tagsPanel=null;
	


	public DataDataPane(Parameters paramsp) {
		super();
		screenName = "DataDataPane";
		screenTitle = "Data Filter Parameters";
		params = paramsp;
		row = new DataDataRow();
		parameters = new TreeMap<String,DataParameter>();
		row.setParameters(parameters);
		newRow= true;
		selRow = new SelectionDataRow();
	}
	public DataDataPane(Parameters paramsp, DataDataRow rowp) {
		super();
		screenName = "DataDataPane";
		row = rowp;
		params = paramsp;
		selRow = new SelectionDataRow();
		selRow.loadRow(row.getName(), params);
		parameters = row.getParameters();
	}
	public DataDataRow displayPanel() {
		DEFAULTSCREENWIDTH = Constants.DATADATASCREENWIDTH;
		DEFAULTSCREENHEIGHT = Constants.DATADATASCREENHEIGHT;
		setStage(new Stage());
		stage.setResizable(true);
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new MyGridPane(Constants.WINDATADATA);
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
				if (saveRow(name.getText()))
					stage.close();
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


		int ix = 0;
		int iy=0;
		parmPanes = setParameters();
		pane.add(parmPanes, ix, iy++);
		GridPane.setMargin(parmPanes, new Insets(10,10,10,10));
		GridPane.setColumnSpan(parmPanes, 3);
		buttons = new HBox();
		Button okBtn = new Button();
		if (Main.loadedIcons.okImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (saveRow(name.getText()))
						stage.close();
			}
		});
		Button cancelBtn = new Button();
		if (Main.loadedIcons.cancelImg == null)
			cancelBtn.setText("Cancel");
		else
			cancelBtn.setGraphic(new ImageView(Main.loadedIcons.cancelImg));
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				row = null;
				stage.close();
			}
		});
		buttons.getChildren().addAll(okBtn,cancelBtn);
		HBox.setMargin(okBtn, new Insets(10,10,10,10));
		HBox.setMargin(cancelBtn, new Insets(10,10,10,10));
		pane.add(buttons, 0, iy);
		GridPane.setColumnSpan(buttons,2);
		dirty=false;
		resize();
		stage.showAndWait();
		return row;
	}
	private boolean saveRow(String fileName) {
		if (name.getText().isEmpty()) {
			OptionMessage.displayMessage("Name must be entered");
			return false;
		}
		boolean updateName=false;
		DataDataRow tempRow = new DataDataRow();
		if (!newRow && !row.getName().equals(name.getText()))
			updateName=true;
		if ((newRow || updateName) && tempRow.loadRow(fileName, params)) {
			if (OptionMessage.yesnoMessage("Data Parameters already exists.  Do you wish to overwrite them?"))  
				tempRow.delete(params);
			else
				return false;
		}
		if (!updateParms())
			return false;
		if (newRow) {
			row.setName(fileName);
			row.saveRow(params);
		}
		else {
			if (updateName) 
				row.renameRow(fileName,params);
			else
				row.saveRow(params);
		}
		return true;
	}

	private GridPane setParameters() {
		GridPane pane = new GridPane();
		name = new TextField();
		if (!newRow) {
			name.setText(row.getName());
		}
		name.textProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		Label nameLbl = new Label("Name");
		Label fromDateLbl = new Label("From Date");
		fromDate = new DatePicker();
		fromDate.setConverter(Main.dateConverter); 
	    fromDate.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
	            if (!newValue){
	                fromDate.setValue(fromDate.getConverter().fromString(fromDate.getEditor().getText()));
	            }
				
			}
	    });
	    fromDate.valueProperty().addListener((ov, oldDate, newDate)->{if (oldDate!=newDate)dirty=true;});
	    if (parameters.containsKey(Constants.PARMFROMDATE)) {
			Date tempDate = DateUtil.convertIntDateToLong(Integer.valueOf(parameters.get(Constants.PARMFROMDATE).getValue()));
			fromDate.setValue(tempDate.toInstant().atZone(Main.zone).toLocalDate());
		}
		else
			fromDate.setValue(LocalDate.now());
		Label toDateLbl = new Label("To Date");
		toDate = new DatePicker();
		toDate.setConverter(Main.dateConverter);
	    toDate.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
	            if (!newValue){
	                toDate.setValue(toDate.getConverter().fromString(toDate.getEditor().getText()));
	            }
				
			}
	    });
	    toDate.valueProperty().addListener((ov, oldDate, newDate)->{if (oldDate!=newDate)dirty=true;});
	    today = new CheckBox("Today");
	    if (parameters.containsKey(Constants.PARMTODATE)) {
			Date tempDate = DateUtil.convertIntDateToLong(Integer.valueOf(parameters.get(Constants.PARMTODATE).getValue()));
			toDate.setValue(tempDate.toInstant().atZone(Main.zone).toLocalDate());
		}
		else
			toDate.setValue(LocalDate.now());
    	today.setSelected(parameters.containsKey(Constants.PARMTODAY)?true:false);
		selectDates = new CheckBox("Filter by Dates");
		selectDates.setSelected(parameters.containsKey(Constants.PARMSELDATES)?true:false);
		selectAccounts = new CheckBox("Filter by Accounts");
		selectAccounts.setSelected(parameters.containsKey(Constants.PARMSELACCT)?true:false);
		selectAccounts.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selectCategories = new CheckBox("Filter by Categories");
		selectCategories.setSelected(parameters.containsKey(Constants.PARMSELCAT)?true:false);
		selectCategories.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selectBudgets = new CheckBox("Filter by Budgets");
		selectBudgets.setSelected(parameters.containsKey(Constants.PARMSELBUDGET)?true:false);
		selectBudgets.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selectCurrencies = new CheckBox("Filter by Currencies");
		selectCurrencies.setSelected(parameters.containsKey(Constants.PARMSELCURRENCY)?true:false);
		selectCurrencies.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selectSecurities = new CheckBox("Filter by Securities");
		selectSecurities.setSelected(parameters.containsKey(Constants.PARMSELSECURITY)?true:false);
		selectSecurities.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selectTrans = new CheckBox ("Filter by Transactions");
		selectTrans.setSelected(parameters.containsKey(Constants.PARMSELTRANS)?true:false);
		selectTrans.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selectInvestTrans = new CheckBox ("Filter by Invest. Transactions");
		selectInvestTrans.setSelected(parameters.containsKey(Constants.PARMSELINVTRANS)?true:false);
		selectInvestTrans.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selAsset = new CheckBox("Assets");
		selAsset.setSelected(parameters.containsKey(Constants.PARMASSET)?true:false);
		selAsset.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selBank = new CheckBox("Banks");
		selBank.setSelected(parameters.containsKey(Constants.PARMBANK)?true:false);
		selBank.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selCredit = new CheckBox("Credit Cards");
		selCredit.setSelected(parameters.containsKey(Constants.PARMCREDIT)?true:false);
		selCredit.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selInvestment = new CheckBox("Investments");
		selInvestment.setSelected(parameters.containsKey(Constants.PARMINVESTMENT)?true:false);
		selInvestment.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selLiability = new CheckBox("Liabilities");
		selLiability.setSelected(parameters.containsKey(Constants.PARMLIABILITY)?true:false);
		selLiability.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selLoan = new CheckBox("Loans");
		selLoan.setSelected(parameters.containsKey(Constants.PARMLOAN)?true:false);
		selLoan.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		inactiveAccts = new CheckBox("Include Inactive");
	    if (parameters.containsKey(Constants.PARMINACTIVE))
	    	inactiveAccts.setSelected(parameters.containsKey(Constants.PARMINACTIVE));
	    else
	    	inactiveAccts.setSelected(false);
		acctSelBtn = new Button("Select");
		acctSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>();
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMACCOUNTS))
					selected = parameters.get(Constants.PARMACCOUNTS).getList();
				else
					selected = new ArrayList<>();
				if (selAsset.isSelected())
					addAccounts(list,Main.extension.assetAccounts);
				if (selBank.isSelected())
					addAccounts(list,Main.extension.bankAccounts);
				if (selCredit.isSelected())
					addAccounts(list,Main.extension.creditAccounts);
				if (selLiability.isSelected())
					addAccounts(list,Main.extension.liabilityAccounts);
				if (selInvestment.isSelected())
					addAccounts(list,Main.extension.investmentAccounts);
				if (selLoan.isSelected())
					addAccounts(list,Main.extension.loanAccounts);
				if (list.isEmpty()) {
					OptionMessage.displayMessage("Please select at least one type of account");
					return;
				}
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				acctPanel = new MRBFXSelectionPanel(list,null,"Select Accounts");
				acctPanel.display();
			}
		});
		selIncome = new CheckBox("Income");
		selIncome.setSelected(parameters.containsKey(Constants.PARMINCOME)?true:false);
		selIncome.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selExpense = new CheckBox("Expense");
		selExpense.setSelected(parameters.containsKey(Constants.PARMEXPENSE)?true:false);
		selExpense.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		catSelBtn = new Button("Select");
		catSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>();
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMCATEGORIES))
					selected = parameters.get(Constants.PARMCATEGORIES).getList();
				else
					selected = new ArrayList<>();
				if (selExpense.isSelected())
					list.addAll(Main.extension.expenseCategories);
				if (selIncome.isSelected())
					list.addAll(Main.extension.incomeCategories);
				if (list.isEmpty()) {
					OptionMessage.displayMessage("Please select at least one type of category");
					return;
				}
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				catPanel = new MRBFXSelectionPanel(list,"Select Active","Select Categories");
				catPanel.display();
				dirty=true;
			}
		});
		budgets = new Button("Select");
		budgets.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>(Main.extension.budgets);
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMBUDGET))
					selected = parameters.get(Constants.PARMBUDGET).getList();
				else
					selected = new ArrayList<>();
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				budPanel = new MRBFXSelectionPanel(list,null,"Select Budgets");
				budPanel.display();
				dirty=true;
			}
		});		
		currSelBtn = new Button("Select");
		currSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>(Main.extension.currencies);
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMCURRENCY))
					selected = parameters.get(Constants.PARMCURRENCY).getList();
				else
					selected = new ArrayList<>();
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				currPanel = new MRBFXSelectionPanel(list,"Shown on Summary Page","Select Currencies");
				currPanel.display();
				dirty=true;
			}
		});		
		secSelBtn = new Button("Select");
		secSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>(Main.extension.securities);
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMSECURITY))
					selected = parameters.get(Constants.PARMSECURITY).getList();
				else
					selected = new ArrayList<>();
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				secPanel = new MRBFXSelectionPanel(list,"Shown on Summary Page","Select Securities");
				secPanel.display();
				dirty=true;
			}
		});		
		selCleared = new CheckBox("Cleared");
		selCleared.setSelected(parameters.containsKey(Constants.PARMCLEARED)?true:false);
		selCleared.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selReconciling = new CheckBox("Reconciling");
		selReconciling.setSelected(parameters.containsKey(Constants.PARMRECON)?true:false);
		selReconciling.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		selUnreconciled = new CheckBox("Unreconciled");
		selUnreconciled.setSelected(parameters.containsKey(Constants.PARMUNRECON)?true:false);
		selUnreconciled.selectedProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		tagsSelBtn = new Button("Filter by Tags");
		tagsSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>(Main.extension.tags);
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMTAGS))
					selected = parameters.get(Constants.PARMTAGS).getList();
				else
					selected = new ArrayList<>();
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				tagsPanel = new MRBFXSelectionPanel(list,null,"Select Tags");
				tagsPanel.display();
				dirty=true;
			}
		});
		Label fromChequeLbl = new Label("From Cheque No");
		fromCheque = new TextField();
		fromCheque.textProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		if (parameters.containsKey(Constants.PARMFROMCHEQUE))
			fromCheque.setText(parameters.get(Constants.PARMFROMCHEQUE).getValue());
		Label toChequeLbl = new Label("To Cheque No");
		toCheque = new TextField();
		toCheque.textProperty().addListener((ov,oldv,newv)->{if (oldv!=newv)dirty=true;});
		if (parameters.containsKey(Constants.PARMTOCHEQUE))
			toCheque.setText(parameters.get(Constants.PARMTOCHEQUE).getValue());
		invAcctBtn = new Button("Select Accounts and Securities");
		invAcctBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>(Main.extension.securityAccounts);
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMINVACCTS))
					selected = parameters.get(Constants.PARMINVACCTS).getList();
				else
					selected = new ArrayList<>();
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				invPanel = new MRBFXSelectionPanel(list,"Select with Holdings","Select Investment Accounts and Security Holdings");
				invPanel.display();
				dirty=true;
			}
		});		
		ttypSelBtn = new Button("Filter by Transfer Types");
		ttypSelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<MRBFXSelectionRow> list = new ArrayList<MRBFXSelectionRow>(Main.extension.transferTypes);
				List<String> selected;
				SortedMap<String,String> selMap = new TreeMap<String, String>();
				if (parameters.containsKey(Constants.PARMTRANSFER))
					selected = parameters.get(Constants.PARMTRANSFER).getList();
				else
					selected = new ArrayList<>();
				for (String acct : selected) {
					selMap.put(acct,acct);
				}
				for (MRBFXSelectionRow row : list) {
					if (selMap.get(row.getRowId()) != null)
						row.setSelected(true);;
				}
				ttypPanel = new MRBFXSelectionPanel(list,null,"Select Investment Transfer Types");
				ttypPanel.display();
				dirty=true;
			}
		});		

		int ix = 0;
		int iy = 0;
		pane.add(nameLbl, ix++,iy);
		pane.add(name, ix, iy++);
		ix= 0;
		pane.add(selectDates, ix++, iy);
		HBox datePane = new HBox();
		datePane.setSpacing(5.0);
		datePane.getChildren().addAll(fromDateLbl,fromDate,toDateLbl,toDate,today);
		pane.add(datePane, ix, iy++);
		GridPane.setColumnSpan(datePane, 5);
		ix=0;
		pane.add(selectAccounts, ix++, iy);
		pane.add(selAsset, ix++, iy);
		pane.add(selBank, ix++, iy);
		pane.add(selCredit, ix++, iy);
		pane.add(inactiveAccts, ix++, iy);
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
		pane.add(budgets, ix, iy++);
		ix=0;
		pane.add(selectCurrencies, ix++, iy);
		pane.add(currSelBtn, ix, iy++);
		ix=0;
		pane.add(selectSecurities, ix++, iy);
		pane.add(secSelBtn, ix, iy++);
		ix=0;
		pane.add(selectTrans, ix++, iy);
		pane.add(selCleared, ix++, iy);
		pane.add(selUnreconciled, ix++, iy);
		pane.add(selReconciling, ix, iy++);
		ix=1;
		pane.add(fromChequeLbl, ix++, iy);
		pane.add(fromCheque, ix, iy++);
		ix=1;
		pane.add(toChequeLbl, ix++, iy);
		pane.add(toCheque, ix, iy++);
		ix=1;
		pane.add(tagsSelBtn, ix, iy++);
		ix=0;
		pane.add(selectInvestTrans, ix++, iy);
		pane.add(invAcctBtn, ix++, iy);
		pane.add(ttypSelBtn, ix, iy);
		pane.setHgap(10.0);
		pane.setVgap(10.0);
		return pane;
	}
	private void addAccounts(List<MRBFXSelectionRow> list,List<MRBFXSelectionRow> accounts) {
		for (MRBFXSelectionRow row:accounts) {
			String uuid = row.getRowId();
			Account acct = Main.book.getAccountByUUID(uuid);
			if(acct !=null && acct.getAccountIsInactive() && !inactiveAccts.isSelected())
				continue;
			list.add(row);
		}
	}
	private boolean updateParms() {
		DataParameter nullParm = new DataParameter();
		if (selectDates.isSelected())
			parameters.put(Constants.PARMSELDATES,nullParm);
		else
			parameters.remove(Constants.PARMSELDATES);
		if (selectDates.isSelected()) {
			nullParm.setValue(null);
			DataParameter fromDateParm = new DataParameter();
			Date tempDate =Date.from(fromDate.getValue().atStartOfDay().atZone(Main.zone).toInstant());
			int intDate=DateUtil.convertDateToInt(tempDate);
			fromDateParm.setValue(String.valueOf(intDate));
			parameters.put(Constants.PARMFROMDATE, fromDateParm);
			DataParameter toDateParm = new DataParameter();
			if (toDate.getValue() == null)
				tempDate = Main.now;
			else
				tempDate =Date.from(toDate.getValue().atStartOfDay().atZone(Main.zone).toInstant());
			int toIntDate=DateUtil.convertDateToInt(tempDate);
			toDateParm.setValue(String.valueOf(toIntDate));
			parameters.put(Constants.PARMTODATE, toDateParm);
			if (today.isSelected())
				parameters.put(Constants.PARMTODAY,nullParm);
			else {
				parameters.remove(Constants.PARMTODAY);
				if(intDate > toIntDate) {
					OptionMessage.displayMessage("From Date must be before To Date unless Today is checked");
					return false;					
				}
			}
		}
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
		if (selectCurrencies.isSelected())
			parameters.put(Constants.PARMSELCURRENCY,nullParm);
		else
			parameters.remove(Constants.PARMSELCURRENCY);
		if (selectSecurities.isSelected())
			parameters.put(Constants.PARMSELSECURITY,nullParm);
		else
			parameters.remove(Constants.PARMSELSECURITY);
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
		if (inactiveAccts.isSelected())
			parameters.put(Constants.PARMINACTIVE,nullParm);
		else
			parameters.remove(Constants.PARMINACTIVE);
		if (selIncome.isSelected())
			parameters.put(Constants.PARMINCOME,nullParm);
		else
			parameters.remove(Constants.PARMINCOME);
		if (selExpense.isSelected())
			parameters.put(Constants.PARMEXPENSE,nullParm);
		else
			parameters.remove(Constants.PARMEXPENSE);
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
		if (fromCheque.getText().isBlank())
			parameters.remove(Constants.PARMFROMCHEQUE);
		else {
			DataParameter parm = new DataParameter();
			parm.setValue(fromCheque.getText());
			parameters.put(Constants.PARMFROMCHEQUE, parm);
		}
		if (toCheque.getText().isBlank())
			parameters.remove(Constants.PARMTOCHEQUE);
		else {
			DataParameter parm = new DataParameter();
			parm.setValue(toCheque.getText());
			parameters.put(Constants.PARMTOCHEQUE, parm);
		}

		if (selectInvestTrans.isSelected())
			parameters.put(Constants.PARMSELINVTRANS,nullParm);
		else
			parameters.remove(Constants.PARMSELINVTRANS);
		List<String>panelList;
		if (acctPanel != null)
		{
			parameters.remove(Constants.PARMACCOUNTS);
			DataParameter parm = new DataParameter();
			panelList = acctPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
				parameters.put(Constants.PARMACCOUNTS, parm);
			}
		}
		if (catPanel != null)
		{
			parameters.remove(Constants.PARMCATEGORIES);
			DataParameter parm = new DataParameter();
			panelList = catPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
				parameters.put(Constants.PARMCATEGORIES, parm);
			}
		}
		if (budPanel != null)
		{
			parameters.remove(Constants.PARMBUDGET);
			DataParameter parm = new DataParameter();
			panelList = budPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
				parameters.put(Constants.PARMBUDGET, parm);
			}
		}
		if (currPanel != null)
		{
			parameters.remove(Constants.PARMCURRENCY);
			DataParameter parm = new DataParameter();
			panelList = currPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
				parameters.put(Constants.PARMCURRENCY, parm);
			}
		}
		if (secPanel != null)
		{
			parameters.remove(Constants.PARMSECURITY);
			DataParameter parm = new DataParameter();
			panelList = secPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
				parameters.put(Constants.PARMSECURITY, parm);
			}
		}
		if (ttypPanel != null)
		{
			parameters.remove(Constants.PARMTRANSFER);
			DataParameter parm = new DataParameter();
			panelList = ttypPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
				parameters.put(Constants.PARMTRANSFER, parm);
			}
		}
		if (invPanel != null ) {
			parameters.remove(Constants.PARMINVACCTS);
			DataParameter parm = new DataParameter();
			panelList = invPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
				parameters.put(Constants.PARMINVACCTS, parm);
			}
		}
		if (tagsPanel != null)
		{
			parameters.remove(Constants.PARMTAGS);
			DataParameter parm = new DataParameter();
			panelList = tagsPanel.getSelected();
			if (panelList != null && !panelList.isEmpty()) {
				parm.setList(panelList);
			parameters.put(Constants.PARMTAGS, parm);
			}
		}
		return true;
	}
}

