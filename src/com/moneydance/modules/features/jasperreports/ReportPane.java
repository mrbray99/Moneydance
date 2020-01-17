package com.moneydance.modules.features.jasperreports;

import java.io.InputStream;

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

public class ReportPane extends ScreenPanel {
	private Parameters params;
	private ObservableList<ReportRow> model;
    private TableView<ReportRow> thisTable;
    private int SCREENWIDTH; 
    private int SCREENHEIGHT; 
    private Button editBtn;
    private Button deleteBtn;
    private Button addBtn;
    private Button viewBtn;
    private Database database;
	public ReportPane(Parameters paramsp) {
		params = paramsp;
		setUpTable();
		Label templateLbl = new Label("Reports");
		templateLbl.setTextAlignment(TextAlignment.CENTER);
		templateLbl.setFont(Font.font("Veranda",FontWeight.BOLD,20.0));
		add(templateLbl,0,0);
		setMargin(templateLbl,new Insets(10,10,10,10));
		GridPane.setColumnSpan(templateLbl,4);
		GridPane.setHalignment(templateLbl, HPos.CENTER);
		add(thisTable,0,1);
		GridPane.setColumnSpan(thisTable,4);
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
		viewBtn = new Button();
		setMargin(viewBtn,new Insets(10,10,10,10));
		InputStream streamView = getClass().getResourceAsStream(Constants.RESOURCES+"PrintPreview24.gif");
		Image viewImg = null;
		if (streamView != null)
			viewImg = new Image(streamView);
		if (viewImg == null)
			viewBtn.setText("View Report");
		else
			viewBtn.setGraphic(new ImageView(viewImg));
		viewBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				viewReport();
			}
		}); 
		add(addBtn,0,2);
		add(editBtn,1,2);
		add(deleteBtn,2,2);	
		add(viewBtn,3,2);
		resize();
	}
	private void addRow() {
		ReportDataPane reportDataPan = new ReportDataPane(params);
		ReportDataRow row = reportDataPan.displayPanel();
		if(row != null) {
			ReportRow tabRow = new ReportRow();
			tabRow.setName(row.getName());
			tabRow.setFileName(params.getDataDirectory()+"/"+row.getName()+Constants.SELEXTENSION);
			tabRow.setLastUsed(Main.cdate.format(Main.now));
			tabRow.setCreated(Main.cdate.format(Main.now));
			tabRow.setTemplate(row.getTemplate());
			tabRow.setSelection(row.getSelection());
			tabRow.setData(row.getDataParms());
			params.addReportRow(tabRow);
			row.saveRow(params);
			resetData();
		}
	}
	private void editRow() {
		ReportRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a report");
			return;
		}
		ReportDataRow rowEdit = new ReportDataRow();
		if (rowEdit.loadRow(row.getName(), params)) {
			ReportDataPane pane = new ReportDataPane(params,rowEdit);
			rowEdit = pane.displayPanel();
			row.setLastUsed(Main.cdate.format(Main.now));
			params.updateReportRow(row);
			resetData();
		}
		
	}
	private void deleteRow() {
		ReportRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a report");
			return;
		}
		ReportDataRow rowEdit = new ReportDataRow();
		if (rowEdit.loadRow(row.getName(), params)) {
			Boolean result = OptionMessage.yesnoMessage("Are you sure you wish to delete report "+row.getName());
			if (result) {
				row.delete();
				params.removeReportRow(row);
			}
			resetData();
		}
		
	}
	private void viewReport() {
		ReportRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a report");
			return;
		}
		ReportDataRow rowEdit = new ReportDataRow();
		if (rowEdit.loadRow(row.getName(), params)) {
			SelectionDataRow selection = new SelectionDataRow();
			if (!selection.loadRow(rowEdit.getSelection(), params)){
				OptionMessage.displayMessage("Selection Group file "+rowEdit.getSelection()+" not found");
				return;
			}
			DataDataRow data = new DataDataRow();
			if(!data.loadRow(rowEdit.getDataParms(), params)){
				OptionMessage.displayMessage("Data Parameters file "+rowEdit.getDataParms()+" not found");
				return;				
			}
			/*
			 * send command to read data and view report running it on the EDT rather than FX Thread
			 */
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.VIEWREPORTCMD+"?"+row.getName());
				}
			});

		}

	}
	


	public void resize() {
		SCREENWIDTH = Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEWIDTH,Constants.DATASCREENWIDTH);
		SCREENHEIGHT =Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEHEIGHT,Constants.DATASCREENHEIGHT);
		setPrefSize(SCREENWIDTH,SCREENHEIGHT);
		thisTable.setPrefWidth(SCREENWIDTH);
		thisTable.setPrefHeight(SCREENHEIGHT);
	}
	public void resetData() {
		model =FXCollections.observableArrayList(params.getReportList());
		thisTable.setItems(model);		
	}
	
	private void setUpTable () {
		thisTable = new TableView<>();
		thisTable.setEditable(true);
		/*
		 * Name
		 */
		TableColumn <ReportRow,String>name = new TableColumn<>("Name");
		/*
		 * Report
		 */
		TableColumn<ReportRow,String> reportCol = new TableColumn<>("Template");
		/*
		 * Selection
		 */
		TableColumn<ReportRow,String> selectionCol = new TableColumn<>("Selection");
		/*
		 * Data
		 */
		TableColumn<ReportRow,String> dataCol = new TableColumn<>("Data Parms");
		/*
		 * Last verified
		 */
		TableColumn<ReportRow,String> lastVerified = new TableColumn<>("Last Verified Date");
		thisTable.getColumns().addAll(name,reportCol,selectionCol,dataCol,lastVerified);
		model =FXCollections.observableArrayList(params.getReportList());
		thisTable.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		reportCol.setCellValueFactory(new PropertyValueFactory<>("template"));
		selectionCol.setCellValueFactory(new PropertyValueFactory<>("selection"));
		dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));
		lastVerified.setCellValueFactory(new PropertyValueFactory<>("lastUsed"));
	}


}
