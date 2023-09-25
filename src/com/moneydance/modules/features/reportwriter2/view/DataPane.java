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

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.view.tables.DataDataRow;
import com.moneydance.modules.features.reportwriter2.view.tables.DataRow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
/*
 * Displays a panel of available data parameters.  The user can create, update and delete data parameters
 * A data parameter group determines how individual records are selected
 */
public class DataPane extends ScreenPanel {
	private Parameters params;
	private ObservableList<DataRow> model;
    private TableView<DataRow> thisTable;
    private Button editBtn;
    private Button deleteBtn;
    private Button addBtn;
    private Button copyBtn;
    private Tooltip editTip = new Tooltip();
    private Tooltip deleteTip = new Tooltip();
    private Tooltip addTip = new Tooltip();
    private Tooltip copyTip = new Tooltip();
	public DataPane(Parameters paramsp) {
		params = paramsp;
		setUpTable();
		Label templateLbl = new Label("Data Filter Parameters");
		templateLbl.setTextAlignment(TextAlignment.CENTER);
		templateLbl.setFont(Font.font("Veranda",FontWeight.BOLD,20.0));
		add(templateLbl,0,0);
		setMargin(templateLbl,new Insets(10,10,10,10));
		setColumnSpan(templateLbl,4);
		GridPane.setHalignment(templateLbl, HPos.CENTER);
		add(thisTable,0,1);
		setColumnSpan(thisTable,4);
		editBtn = new Button();
		setMargin(editBtn,new Insets(10,10,10,10));
		if (Main.loadedIcons.editImg == null)
			editBtn.setText("Edit");
		else
			editBtn.setGraphic(new ImageView(Main.loadedIcons.editImg));
		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				editRow();
			}
		});
		editTip.setText("Edit an existing Data Filter Parameters set");
		editBtn.setTooltip(editTip);
		deleteBtn = new Button();
		setMargin(deleteBtn,new Insets(10,10,10,10));
		if (Main.loadedIcons.deleteImg == null)
			deleteBtn.setText("Delete");
		else
			deleteBtn.setGraphic(new ImageView(Main.loadedIcons.deleteImg));
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				deleteRow();
			}
		});
		deleteTip.setText("Delete an existing Data Filter Parameters set");
		deleteBtn.setTooltip(deleteTip);
		addBtn = new Button();
		setMargin(addBtn,new Insets(10,10,10,10));
		if (Main.loadedIcons.addImg == null)
			addBtn.setText("+");
		else
			addBtn.setGraphic(new ImageView(Main.loadedIcons.addImg));
		addBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				addRow();
			}
		});
		addTip.setText("Add a new Data Filter Parameters set");
		addBtn.setTooltip(addTip);
		copyBtn = new Button();
		setMargin(copyBtn,new Insets(10,10,10,10));
		if (Main.loadedIcons.addImg == null)
			copyBtn.setText("+");
		else
			copyBtn.setGraphic(new ImageView(Main.loadedIcons.copyImg));
		copyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				copyRow();
			}
		});
		copyTip.setText("Copy a Data Filter Parameters set");
		copyBtn.setTooltip(copyTip);
		add(addBtn,0,2);
		add(editBtn,1,2);
		add(copyBtn,2,2);
		add(deleteBtn,3,2);
		resize();
	}
	@Override
	protected void newMsg() {
		addRow();
	}
	private void addRow() {
		DataDataPane dataDataPan = new DataDataPane(params);
		DataDataRow row = dataDataPan.displayPanel();
		if(row != null) {
			DataRow tabRow = new DataRow();
			tabRow.setName(row.getName());
			tabRow.setFileName(params.getDataDirectory()+"/"+row.getName()+Constants.DATAEXTENSION);
			tabRow.setLastModified(Main.cdate.format(Main.now));
			tabRow.setLastUsed(Main.cdate.format(Main.now));
			tabRow.setCreated(Main.cdate.format(Main.now));
			params.addDataRow(tabRow);
			row.saveRow(params);
			resetData();
		}
	}
	@Override
	protected void openMsg() {
		editRow();
	}
	private void editRow() {
		DataRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a parameter set");
			return;
		}
		DataDataRow rowEdit = new DataDataRow();
		if (rowEdit.loadRow(row.getName(), params)) {
			DataDataPane pane = new DataDataPane(params,rowEdit);
			rowEdit = pane.displayPanel();
			if (rowEdit != null) {
				row.setLastModified(Main.cdate.format(Main.now));
				params.updateDataRow(row);
				rowEdit.saveRow(params);			
				resetData();
			}
		}
		
	}
	protected void deleteMsg() {
		deleteRow();
	}
	private void deleteRow() {
		DataRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a Parameter Set");
			return;
		}
		if(params.checkDataGroup(row.getName())) {
			OptionMessage.displayMessage("This Data Filter Parameters entry is used in a report.  It can not be deleted.");
			return;
		}
		DataDataRow rowEdit = new DataDataRow();
		if (rowEdit.loadRow(row.getName(), params)) {
			Boolean result = OptionMessage.yesnoMessage("Are you sure you wish to delete data filter parameters "+row.getName());
			if (result) {
				rowEdit.delete(params);
				params.removeDataRow(row);
			}
			resetData();
		}
		
	}
	private void copyRow() {
		DataRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a Parameter Set");
			return;
		}
		String result = "";
		while (result.isEmpty()) {
			result = OptionMessage.inputMessage("Enter the name of the new row");
			if (result.equals(Constants.CANCELPRESSED))
				return;
			if (result.isEmpty())
				OptionMessage.displayMessage("A name must be entered");
			else {
				DataDataRow newRow = new DataDataRow();
				if (newRow.loadRow(result,params)) {
					OptionMessage.displayMessage("New Name already exists");
					result = "";
				}
				else {
					newRow.loadRow(row.getName(), params);
					newRow.setName(result);
					newRow.saveRow(params);
				}				
			}
		}
		resetData();
	}
	public void resetData() {
		params.setDataTemplates();
		model =FXCollections.observableArrayList(params.getDataList());
		thisTable.setItems(model);	
		thisTable.refresh();
	}

	public void resize() {
		super.resize();
		thisTable.setPrefWidth(SCREENWIDTH);
		thisTable.setPrefHeight(SCREENHEIGHT);
	}
	private void setUpTable () {
		thisTable = new TableView<>();
		thisTable.setEditable(true);
		thisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		thisTable.setMaxWidth(Double.MAX_VALUE);
		thisTable.setMaxHeight(Double.MAX_VALUE);
		/*
		 * Name
		 */
		TableColumn<DataRow,String> name = new TableColumn<>("Name");
		/*
		 * Last Created
		 */
		TableColumn<DataRow,String> created = new TableColumn<>("Created");
		/*
		 * Last Modified
		 */
		TableColumn<DataRow,String> lastModified = new TableColumn<>("Modified");
		/*
		 * Last Used
		 */
		TableColumn<DataRow,String> lastUsed = new TableColumn<>("Used");
		thisTable.setRowFactory(tv->{
			TableRow<DataRow> row = new TableRow<>();
			row.setOnMouseClicked(event->{
				if (event.getClickCount()==2 && (!row.isEmpty())) {
					editRow();
				}
			});
			return row;
		});
		thisTable.getColumns().addAll(name,created,lastModified,lastUsed);
		model =FXCollections.observableArrayList(params.getDataList());
		thisTable.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		created.setCellValueFactory(new PropertyValueFactory<>("created"));
		lastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
		lastUsed.setCellValueFactory(new PropertyValueFactory<>("lastUsed"));
	}

}
