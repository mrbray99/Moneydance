package com.moneydance.modules.features.jasperreports;

import java.io.InputStream;

import com.moneydance.modules.features.mrbutil.MRBDebug;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


public class SelectionPane extends ScreenPanel{
		private Parameters params;
		private ObservableList<SelectionRow> model;
	    private TableView<SelectionRow> thisTable;
	    private int SCREENWIDTH; 
	    private int SCREENHEIGHT; 
	    private Button editBtn;
	    private Button deleteBtn;
	    private Button addBtn;
		public SelectionPane(Parameters paramsp) {
			params = paramsp;
			setUpTable();
			Label templateLbl = new Label("Data Selection Groups");
			templateLbl.setTextAlignment(TextAlignment.CENTER);
			templateLbl.setFont(Font.font("Veranda",FontWeight.BOLD,20.0));
			add(templateLbl,0,0);
			setMargin(templateLbl,new Insets(10,10,10,10));
			setColumnSpan(templateLbl,3);
			GridPane.setHalignment(templateLbl, HPos.CENTER);
			add(thisTable,0,1);
			setColumnSpan(thisTable,3);
			editBtn = new Button();
			setMargin(editBtn,new Insets(10,10,10,10));
			InputStream stream = getClass().getResourceAsStream(Constants.RESOURCES+"Edit24.gif");
			Image editImg = null;
			if (stream != null)
				editImg = new Image(stream);
			if (editImg == null)
				editBtn.setText("Edit");
			else
				editBtn.setGraphic(new ImageView(editImg));
			editBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					editRow();
				}
			});
			deleteBtn = new Button();
			setMargin(deleteBtn,new Insets(10,10,10,10));
			InputStream streamDel = getClass().getResourceAsStream(Constants.RESOURCES+"Delete24.gif");
			Image deleteImg = null;
			if (streamDel != null)
				deleteImg = new Image(streamDel);
			if (deleteImg == null)
				deleteBtn.setText("Delete");
			else
				deleteBtn.setGraphic(new ImageView(deleteImg));
			deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					deleteRow();
				}
			});
			addBtn = new Button();
			setMargin(addBtn,new Insets(10,10,10,10));
			addBtn.setText("+");
			addBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					addRow();
				}
			});
			add(addBtn,0,2);
			add(editBtn,1,2);
			add(deleteBtn,2,2);
			resize();
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
		private void deleteRow() {
			SelectionRow row = thisTable.getSelectionModel().getSelectedItem();
			if (row ==null) {
				OptionMessage.displayMessage("Please Select a group");
				return;
			}
			SelectionDataRow rowEdit = new SelectionDataRow();
			if (rowEdit.loadRow(row.getName(), params)) {
				Boolean result = OptionMessage.yesnoMessage("Are you sure you wish to delete group "+row.getName());
				if (result) {
					row.delete();
					params.removeSelectionRow(row);
				}
				resetData();
			}
			
		}
		public void resize() {
			SCREENWIDTH =Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEWIDTH,Constants.DATASCREENWIDTH);
			SCREENHEIGHT =Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEHEIGHT,Constants.DATASCREENHEIGHT);
			setPrefSize(SCREENWIDTH,SCREENHEIGHT);
			Main.debugInst.debugThread("SelectionPan", "resize", MRBDebug.SUMMARY, "resized to "+SCREENWIDTH + "/"+SCREENHEIGHT);
			thisTable.setPrefWidth(SCREENWIDTH);
			thisTable.setPrefHeight(SCREENHEIGHT);
		}
		public void resetData() {
			model =FXCollections.observableArrayList(params.getSelectionList());
			thisTable.setItems(model);		
		}
		private void setUpTable () {
			thisTable = new TableView<>();
			thisTable.setEditable(true);

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
			thisTable.getColumns().addAll(name,created,lastModified,lastUsed);
			model =FXCollections.observableArrayList(params.getSelectionList());
			thisTable.setItems(model);
			name.setCellValueFactory(new PropertyValueFactory<>("name"));
			created.setCellValueFactory(new PropertyValueFactory<>("created"));
			lastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
			lastUsed.setCellValueFactory(new PropertyValueFactory<>("lastUsed"));


		}


}
