package com.moneydance.modules.features.reportwriter.view;

import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.OptionMessage;
import com.moneydance.modules.features.reportwriter.Parameters;

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


public class SelectionPane extends ScreenPanel{
		private Parameters params;
		private ObservableList<SelectionRow> model;
	    private TableView<SelectionRow> thisTable;
	    private Button editBtn;
	    private Button deleteBtn;
	    private Button addBtn;
	    private Button copyBtn;
	    private Tooltip editTip = new Tooltip();
	    private Tooltip deleteTip = new Tooltip();
	    private Tooltip addTip = new Tooltip();
	    private Tooltip copyTip = new Tooltip();
		public SelectionPane(Parameters paramsp) {
			params = paramsp;
			setUpTable();
			Label templateLbl = new Label("Data Selection Groups");
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
			editTip.setText("Edit an existing Data Selection Group");
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
			deleteTip.setText("Delete an existing Data Selection Group");
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
			addTip.setText("Add a new Data Selection Group");
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
			copyTip.setText("Copy a Data Selection Group");
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
			SelectionDataPane selectDataPan = new SelectionDataPane(params);
			SelectionDataRow row = selectDataPan.displayPanel();
			if(row != null) {
				SelectionRow tabRow = new SelectionRow();
				tabRow.setName(row.getName());
				tabRow.setFileName(params.getDataDirectory()+"/"+row.getName()+Constants.SELEXTENSION);
				tabRow.setLastModified(Main.cdate.format(Main.now));
				tabRow.setLastUsed(Main.cdate.format(Main.now));
				tabRow.setCreated(Main.cdate.format(Main.now));
				params.addSelectionRow(tabRow);
				resetData();
			}
		}
		@Override
		protected void openMsg() {
			editRow();
		}
		private void editRow() {
			SelectionRow row = thisTable.getSelectionModel().getSelectedItem();
			if (row ==null) {
				OptionMessage.displayMessage("Please Select a group");
				return;
			}
			SelectionDataRow rowEdit = new SelectionDataRow();
			if (rowEdit.loadRow(row.getName(), params)) {
				SelectionDataPane pane = new SelectionDataPane(params,rowEdit);
				rowEdit = pane.displayPanel();
				row.setLastModified(Main.cdate.format(Main.now));
				row.setLastUsed(Main.cdate.format(Main.now));
				params.updateSelectionRow(row);
				resetData();
			}
			
		}
		protected void deleteMsg() {
			deleteRow();
		}
		private void deleteRow() {
			SelectionRow row = thisTable.getSelectionModel().getSelectedItem();
			if (row ==null) {
				OptionMessage.displayMessage("Please Select a group");
				return;
			}
			if(params.checkSelectionGroup(row.getName())) {
				OptionMessage.displayMessage("This Selection Group is used in a report.  It can not be deleted.");
				return;
			}
			SelectionDataRow rowEdit = new SelectionDataRow();
			if (rowEdit.loadRow(row.getName(), params)) {
				Boolean result = OptionMessage.yesnoMessage("Are you sure you wish to delete group "+row.getName());
				if (result) {
					rowEdit.delete(params);
					params.removeSelectionRow(row);
				}
				resetData();
			}
			
		}
		private void copyRow() {
			SelectionRow row = thisTable.getSelectionModel().getSelectedItem();
			if (row ==null) {
				OptionMessage.displayMessage("Please Select a Data Selection Group");
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
					SelectionDataRow newRow = new SelectionDataRow();
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
		@Override
		public void resize() {
			super.resize();
			thisTable.setPrefWidth(SCREENWIDTH);
			thisTable.setPrefHeight(SCREENHEIGHT);
		}
		public void resetData() {
			params.setDataTemplates();
			model =FXCollections.observableArrayList(params.getSelectionList());
			thisTable.setItems(model);		
			thisTable.refresh();
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
			TableColumn<SelectionRow,String> name = new TableColumn<>("Name");
			/*
			 * Last Created
			 */
			TableColumn<SelectionRow,String> created = new TableColumn<>("Created");
			/*
			 * Last Modified
			 */
			TableColumn<SelectionRow,String> lastModified = new TableColumn<>("Modified");
			/*
			 * Last Used
			 */
			TableColumn<SelectionRow,String> lastUsed = new TableColumn<>("Used");
			thisTable.setRowFactory(tv->{
				TableRow<SelectionRow> row = new TableRow<>();
				row.setOnMouseClicked(event->{
					if (event.getClickCount()==2 && (!row.isEmpty())) {
						editRow();
					}
				});
				return row;
			});
			thisTable.getColumns().addAll(name,created,lastModified,lastUsed);
			model =FXCollections.observableArrayList(params.getSelectionList());
			thisTable.setItems(model);
			name.setCellValueFactory(new PropertyValueFactory<>("name"));
			created.setCellValueFactory(new PropertyValueFactory<>("created"));
			lastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
			lastUsed.setCellValueFactory(new PropertyValueFactory<>("lastUsed"));


		}


}
