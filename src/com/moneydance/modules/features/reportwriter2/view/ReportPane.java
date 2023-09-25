package com.moneydance.modules.features.reportwriter2.view;




import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.RWException;
import com.moneydance.modules.features.reportwriter2.Constants.ReportType;
import com.moneydance.modules.features.reportwriter2.Database;
import com.moneydance.modules.features.reportwriter2.factory.OutputCSV;
import com.moneydance.modules.features.reportwriter2.factory.OutputDatabase;
import com.moneydance.modules.features.reportwriter2.factory.OutputFactory;
import com.moneydance.modules.features.reportwriter2.factory.OutputSpreadsheet;
import com.moneydance.modules.features.reportwriter2.report.Report;
import com.moneydance.modules.features.reportwriter2.view.tables.DataDataRow;
import com.moneydance.modules.features.reportwriter2.view.tables.ReportDataRow;
import com.moneydance.modules.features.reportwriter2.view.tables.ReportRow;
import com.moneydance.modules.features.reportwriter2.view.tables.SelectionDataRow;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportPane extends ScreenPanel {
	private Parameters params;
	private ObservableList<ReportRow> model;
    private TableView<ReportRow> thisTable;
    private Button editBtn;
    private Button deleteBtn;
    private Button addBtn;
    private Button viewBtn;
    private Button openOutBtn;
    private Button copyBtn;
    private Tooltip editTip = new Tooltip();
    private Tooltip deleteTip = new Tooltip();
    private Tooltip addTip = new Tooltip();
    private ImageView reportIcon=null;
    private ImageView csvIcon=null;
    private ImageView spreadIcon=null;
    private ImageView dbIcon=null;
    private GridPane thisObj;
    private Tooltip reportRunTip = new Tooltip();
    private Tooltip csvRunTip = new Tooltip();
    private Tooltip spreadRunTip = new Tooltip();
    private Tooltip dbRunTip = new Tooltip();
    private Tooltip copyTip = new Tooltip();
    private Stage progressFrame;
    private TextArea progressArea;
    private String progressText;
    private ScrollPane progressScroll;
    private VBox progressPane;

	public ReportPane(Parameters params) {
		this.params = params;
		thisObj = this;
		reportIcon = new ImageView(Main.loadedIcons.viewImg);
		csvIcon = new ImageView(Main.loadedIcons.csvImg);
		spreadIcon = new ImageView(Main.loadedIcons.spreadImg);
		dbIcon = new ImageView(Main.loadedIcons.dbImg);

		setUpTable();
		Label templateLbl = new Label("Reports");
		templateLbl.setTextAlignment(TextAlignment.CENTER);
		templateLbl.setFont(Font.font("Veranda",FontWeight.BOLD,20.0));
		add(templateLbl,0,0);
		setMargin(templateLbl,new Insets(10,10,10,10));
		GridPane.setColumnSpan(templateLbl,6);
		GridPane.setHalignment(templateLbl, HPos.CENTER);
		add(thisTable,0,1);
		GridPane.setColumnSpan(thisTable,6);
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
		editTip.setText("Edit an existing Reports set");
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
		deleteTip.setText("Delete an existing Reports set");
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
		addTip.setText("Add a new Reports set");
		addBtn.setTooltip(addTip);
		reportRunTip.setText("Run the selected Report");
		csvRunTip.setText("Create the selected .csv file");
		spreadRunTip.setText("Create the selected Spreadsheet");
		dbRunTip.setText("Create the selected Database");
		viewBtn = new Button();
		setMargin(viewBtn,new Insets(10,10,10,10));
		if (reportIcon == null)
			viewBtn.setText("View Report");
		else
			viewBtn.setGraphic(reportIcon);
		viewBtn.setTooltip(reportRunTip);
		viewBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				viewReport();
			}
		}); 
		openOutBtn = new Button();
		if (Main.loadedIcons.searchImg == null)
			openOutBtn.setText("Output Directory");
		else
			openOutBtn.setGraphic(new ImageView(Main.loadedIcons.searchImg));
		GridPane.setMargin(openOutBtn,new Insets(10, 10, 10, 10));
		openOutBtn.setTooltip(new Tooltip("Click to open Output folder"));
		openOutBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.extension.openOutput();
					}
				});	
			}
		});  
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
		copyBtn.setTooltip(copyTip);		add(addBtn,0,2);
		add(editBtn,1,2);
		add(copyBtn,2,2);
		add(deleteBtn,3,2);	
		add(viewBtn,4,2);
		add(openOutBtn,5,2);
		resize();
	}
	@Override
	protected void newMsg() {
		addRow();
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
			tabRow.setType(row.getTypeInt());
			params.addReportRow(tabRow);
			row.saveRow(params);
			resetData();
		}
	}
	@Override
	protected void openMsg() {
		editRow();
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
			if (rowEdit != null) {
				row.setLastUsed(Main.cdate.format(Main.now));
				row.setTemplate(rowEdit.getTemplate());
				row.setData(rowEdit.getDataParms());
				row.setSelection(rowEdit.getSelection());
				params.updateReportRow(row);
				rowEdit.saveRow(params);
				resetData();
			}
		}
		
	}
	protected void deleteMsg() {
		deleteRow();
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
			runReport(row.getName());
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
	private void runReport(String rowName) {
		Database database=null;
		ReportDataRow rowEdit = new ReportDataRow();
		if (!rowEdit.loadRow(rowName, params)) {
			JOptionPane.showMessageDialog(null, "Report " + rowName + " not found");
			return;
		}
		OutputFactory output = null;
		Main.extension.displayProgressWindow();
		try {
			switch (rowEdit.getType()) {
			case DATABASE:
			case REPORT:
				output = new OutputDatabase(rowEdit, params);
				database = ((OutputDatabase) output).getDatabase();
				if (rowEdit.getType()==ReportType.REPORT) {
							Report outputReport;
							outputReport = new Report(params,database,rowEdit);
							Main.extension.updateProgress("Building Report");
							outputReport.buildReport();
							Main.extension.updateProgress("Printing Report");
							outputReport.printReport();
				}
				Main.extension.closeProgressWindow();
				break;
			case SPREADSHEET:
				output = new OutputSpreadsheet(rowEdit, params);
//				frameReport.resetData();
				output = null;
				Main.extension.closeProgressWindow();
				return;
			case CSV:
				output = new OutputCSV(rowEdit, params);
//				frameReport.resetData();
				output = null;
				Main.extension.closeProgressWindow();
				return;
			}
		} catch (RWException e) {
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
			if (output != null) {
				try {
					output.closeOutputFile();
				} catch (RWException e2) {
				}
			}
			output = null;
			return;
		}
	}
	private void copyRow() {
		ReportRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a Report");
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
				ReportDataRow newRow = new ReportDataRow();
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

	public void resize() {
		super.resize();
		thisTable.setPrefWidth(SCREENWIDTH);
		thisTable.setPrefHeight(SCREENHEIGHT);
	}
	public void resetData() {
		params.setDataTemplates();
		model =FXCollections.observableArrayList(params.getReportList());
		thisTable.setItems(model);	
		thisTable.refresh();
	}
	
	private void setUpTable () {
		thisTable = new TableView<ReportRow>();
		thisTable.setEditable(true);
		thisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		thisTable.setMaxWidth(Double.MAX_VALUE);
		thisTable.setMaxHeight(Double.MAX_VALUE);
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
		thisTable.setRowFactory(tv->{
			TableRow<ReportRow> row = new TableRow<>();
			row.setOnMouseClicked(event->{
				if (event.getClickCount()==2 && (!row.isEmpty())) {
					editRow();
				}
			});
			return row;
		});
		thisTable.getColumns().addAll(name,reportCol,selectionCol,dataCol,lastVerified);
		model =FXCollections.observableArrayList(params.getReportList());
		thisTable.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		reportCol.setCellValueFactory(new PropertyValueFactory<>("template"));
		selectionCol.setCellValueFactory(new PropertyValueFactory<>("selection"));
		dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));
		lastVerified.setCellValueFactory(new PropertyValueFactory<>("lastUsed"));
		thisTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ReportRow>() {
  
			@Override
	           public void changed(ObservableValue<? extends ReportRow> observable,ReportRow oldValue, ReportRow newValue){
                if(newValue!=null){
                    ReportDataRow rowEdit = new ReportDataRow();
             		if (rowEdit.loadRow(newValue.getName(), params)) {
            			switch (rowEdit.getType()) {
            			case DATABASE:
               				removeBtnImages();
            				if (dbIcon == null)
            					viewBtn.setText("Create Database");
            				else
            					viewBtn.setGraphic(dbIcon);
            				viewBtn.setTooltip(dbRunTip);
            				break;
          			case REPORT :
            				removeBtnImages();
            				if (reportIcon == null)
            					viewBtn.setText("View Report");
            				else
            					viewBtn.setGraphic(reportIcon);
            				viewBtn.setTooltip(reportRunTip);
            				break;
            			case SPREADSHEET :
            				if (spreadIcon == null)
            					viewBtn.setText("Output Spreadsheet");
            				else
            					viewBtn.setGraphic(spreadIcon);
            				viewBtn.setTooltip(spreadRunTip);
            				break;
            			case CSV:
            				if (csvIcon == null)
            					viewBtn.setText("Output CSV");
            				else
            					viewBtn.setGraphic(csvIcon);
            				viewBtn.setTooltip(csvRunTip);
            				break;
            			}
            		}

                }
 				
			}

		});
	}
	private void removeBtnImages() {
		if (dbIcon != null)
			thisObj.getChildren().remove(dbIcon);		
		if (reportIcon != null)
			thisObj.getChildren().remove(reportIcon);		
		if (csvIcon != null)
			thisObj.getChildren().remove(csvIcon);		
		if (spreadIcon != null)
			thisObj.getChildren().remove(spreadIcon);		
	}


}
