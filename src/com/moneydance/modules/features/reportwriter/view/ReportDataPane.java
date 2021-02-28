package com.moneydance.modules.features.reportwriter.view;

import java.util.List;

import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.OptionMessage;
import com.moneydance.modules.features.reportwriter.Parameters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ReportDataPane extends ScreenDataPane {
	private Parameters params;
	private TextField name;
	private ComboBox<String> templates;
	private Label templateLbl;
	private ComboBox<String> selections;
	private ComboBox<String> dataParms;
	private ComboBox<String> csvDelimiter;
	private Label delimiterLbl;
 	private RadioButton jasperReport;
	private RadioButton createDatabase;
	private RadioButton createSpreadsheet;
	private RadioButton createCsvFile;
	private CheckBox addBOM;
	private CheckBox generateName;
	private CheckBox overWriteFile;
	private CheckBox addDateStamp;
	private TextField fileName;
	private ToggleGroup group;
	private HBox csvBox;
	private List<TemplateRow> listTemplates;
	private List<SelectionRow> listSelections;
	private List<DataRow> listDataParms;
	private ObservableList<String> listTempNames;
	private ObservableList<String> listSelNames;
	private ObservableList<String> listDataNames;
	private Scene scene;
	private GridPane pane;
	private ReportDataRow row;
	private boolean newRow = false;
	private boolean dirty = false;
	
	public ReportDataPane(Parameters paramsp) {
		super();
		screenName = "ReportDataPane";
		screenTitle = "Report Definition Screen";
		params = paramsp;
		row = new ReportDataRow();
		newRow= true;
	}
	public ReportDataPane(Parameters paramsp, ReportDataRow rowp) {
		super();
		screenName = "ReportDataPane";
		row = rowp;
		params = paramsp;
	}
	public ReportDataRow displayPanel() {
		DEFAULTSCREENWIDTH = Constants.DATAREPORTSCREENWIDTH;
		DEFAULTSCREENHEIGHT = Constants.DATAREPORTSCREENHEIGHT;
		setStage(new Stage());
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new MyGridPane(Constants.WINREPORTDATA);
		scene = new Scene(pane);
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
				if (saveRow())
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
		resize();
		listTemplates = params.getTemplateList();
		listSelections = params.getSelectionList();
		listDataParms = params.getDataList();
		Label reportLbl = new Label("Name");
		GridPane.setMargin(reportLbl,new Insets(10,10,10,10));
		name = new TextField();
		name.setPadding(new Insets(10,10,10,10));
		if (!newRow) {
			name.setText(row.getName());	
		}
		name.textProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		delimiterLbl = new Label("CSV Delimiter");
		delimiterLbl.setVisible(true);
		csvDelimiter = new ComboBox<String>();
		csvDelimiter.setItems(FXCollections.observableArrayList(Constants.DELIMITERS));
		csvDelimiter.getSelectionModel().selectedItemProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		Label addBOMLbl = new Label("Target Excel");
		Label templatesLbl = new Label("Report Template");
		addBOM = new CheckBox();
		addBOM.selectedProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		csvBox = new HBox(delimiterLbl, csvDelimiter, addBOMLbl, addBOM);
		csvBox.setSpacing(10.0);
		GridPane.setMargin(csvBox,new Insets(10,10,10,10));
		templates = new ComboBox<>();
		templateLbl = new Label();
		templates.getSelectionModel().selectedItemProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		GridPane.setMargin(templatesLbl,new Insets(10,10,10,10));
		listTempNames = FXCollections.observableArrayList();
		for (TemplateRow rowT : listTemplates) {
			listTempNames.add(rowT.getName());
		}
		templates.setItems(listTempNames);
		if (!newRow)
			templates.getSelectionModel().select(row.getTemplate());
		GridPane.setMargin(templates,new Insets(10,10,10,10));
		jasperReport = new RadioButton ("View Jasper Report");
		createDatabase = new RadioButton ("Create Database");
		createSpreadsheet = new RadioButton ("Create Spreadsheet");
		createCsvFile = new RadioButton ("Create .CSV file");
		GridPane.setMargin(jasperReport,new Insets(10,10,10,10));
		GridPane.setMargin(createDatabase,new Insets(10,10,10,10));
		GridPane.setMargin(createSpreadsheet,new Insets(10,10,10,10));
		GridPane.setMargin(createCsvFile,new Insets(10,10,10,10));
		group = new ToggleGroup();
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
				RadioButton rb = (RadioButton) group.getSelectedToggle();
				dirty = true;
				csvBox.setVisible(false);
				if (rb == jasperReport) { 
					templates.setDisable(false);
					templates.setVisible(true);
					templateLbl.setVisible(false);
				}
				else {
					templates.setDisable(true);
					templates.setVisible(false);
					templateLbl.setVisible(true);
					if (rb == createDatabase)
						templateLbl.setText("Create Database");
					else if (rb==createSpreadsheet)
						templateLbl.setText("Create Spreadsheet");
					else if (rb==createCsvFile) {
						templateLbl.setText("Create CSV File");
						csvBox.setVisible(true);

					}
				}
			}
		});
		jasperReport.setToggleGroup(group);
		createDatabase.setToggleGroup(group);
		createSpreadsheet.setToggleGroup(group);
		createCsvFile.setToggleGroup(group);
		csvBox.setVisible(false);
		if (newRow)
			jasperReport.setSelected(true);
		else {
			switch (row.getType()) {
			case JASPER:
				jasperReport.setSelected(true);
				templates.setVisible(true);
				break;
			case DATABASE:
				createDatabase.setSelected(true);;
				templates.setVisible(false);
				templateLbl.setVisible(true);
				templateLbl.setText("Create Database");
				break;
			case SPREADSHEET:
				createSpreadsheet.setSelected(true);;
				templates.setVisible(false);
				templateLbl.setVisible(true);
				templateLbl.setText("Create Spreadsheet");
				break;
			case CSV:
				createCsvFile.setSelected(true);;
				templates.setVisible(false);
				templateLbl.setVisible(true);
				templateLbl.setText("Create CSV File");
				csvBox.setVisible(true);
				csvDelimiter.setValue(row.getDelimiter());
				addBOM.setSelected(row.getTargetExcel());

				break;
			}
		}
		Label fileNameLbl = new Label("File Name");
		fileName = new TextField();
		fileName.textProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		fileName.setPadding(new Insets(10,10,10,10));
		generateName = new CheckBox("Generate Name");
		generateName.selectedProperty().addListener((ov,oldv,newv)->{dirty=true;
			if(newv) {
				fileName.setDisable(true);
				addDateStamp.setDisable(true);
			}
			else {
				fileName.setDisable(false);
				addDateStamp.setDisable(false);
			}
		});
		overWriteFile = new CheckBox("Ovewrwrite");
		overWriteFile.selectedProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		addDateStamp = new CheckBox("Add Date Stamp");
		addDateStamp.selectedProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		GridPane.setMargin(fileNameLbl,new Insets(10,10,10,10));
		GridPane.setMargin(fileName,new Insets(10,10,10,10));
		GridPane.setMargin(generateName,new Insets(10,10,10,10));
		GridPane.setMargin(overWriteFile,new Insets(10,10,10,10));
		GridPane.setMargin(addDateStamp,new Insets(10,10,10,10));
		if(!newRow) {
			fileName.setText(row.getOutputFileName());
			generateName.setSelected(row.getGenerate());
			if (row.getGenerate()) {
				fileName.setDisable(true);
				addDateStamp.setDisable(true);
			}
			else {
				fileName.setDisable(false);
				addDateStamp.setDisable(false);
			}
			overWriteFile.setSelected(row.getOverWrite());
			addDateStamp.setSelected(row.getAddDate());
		}
		Label selectionLbl = new Label("Selection Group");
		selections = new ComboBox<>();
		selections.getSelectionModel().selectedItemProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		GridPane.setMargin(selectionLbl,new Insets(10,10,10,10));
		listSelNames = FXCollections.observableArrayList();
		for (SelectionRow rowT : listSelections) {
			listSelNames.add(rowT.getName());
		}
		selections.setItems(listSelNames);
		if (!newRow)
			selections.getSelectionModel().select(row.getSelection());
		GridPane.setMargin(selections,new Insets(10,10,10,10));
		Label dataParmsLbl = new Label("Data Parameters");
		dataParms = new ComboBox<>();
		dataParms.getSelectionModel().selectedItemProperty().addListener((ov,oldv,newv)->{if(newv!=oldv)dirty=true;});
		GridPane.setMargin(dataParmsLbl,new Insets(10,10,10,10));
		listDataNames = FXCollections.observableArrayList();
		for (DataRow rowT : listDataParms) {
			listDataNames.add(rowT.getName());
		}
		dataParms.setItems(listDataNames);
		if (!newRow)
			dataParms.getSelectionModel().select(row.getDataParms());
		GridPane.setMargin(dataParms,new Insets(10,10,10,10));
		Button okBtn = new Button();
		if (Main.loadedIcons.okImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				saveRow();
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
				row=null;
				stage.close();
			}
		});
		GridPane.setMargin(okBtn,new Insets(10,10,10,10));
		GridPane.setMargin(cancelBtn,new Insets(10,10,10,10));
		int ix=0;
		int iy=0;
		pane.add(reportLbl, ix++, iy);
		pane.add(name, ix, iy++);
		ix=1;
		pane.add(jasperReport, ix++, iy);
		pane.add(createDatabase, ix++, iy);
		pane.add(createSpreadsheet, ix++, iy);
		pane.add(createCsvFile, ix, iy++);
		ix=0;
		pane.add(fileNameLbl, ix++, iy);
		pane.add(fileName, ix++, iy);
		pane.add(generateName, ix++, iy);
		pane.add(overWriteFile, ix++, iy);
		pane.add(addDateStamp, ix, iy++);
		ix=0;
		pane.add(templatesLbl, ix++, iy);
		pane.add(templateLbl, ix, iy);
		pane.add(templates, ix++, iy);
		pane.add(csvBox, ix, iy++);
		GridPane.setColumnSpan(csvBox, 3);
		ix=0;
		pane.add(selectionLbl, ix++, iy);
		pane.add(selections, ix, iy++);
		ix=0;
		pane.add(dataParmsLbl, ix++, iy);
		pane.add(dataParms, ix, iy++);
		ix=0;
		pane.add(okBtn, ix++, iy);
		pane.add(cancelBtn,ix++, iy);
		dirty=false;
		stage.showAndWait();
		return row;
	}

	private boolean saveRow() {
		if (name.getText().isEmpty()) {
			OptionMessage.displayMessage("Name must be entered");
			return false;
		}
		if (jasperReport.isSelected() && templates.getSelectionModel().isEmpty()) { 
			OptionMessage.displayMessage("A Report Template must be selected");
			return false;
		}
		if (selections.getSelectionModel().isEmpty()) { 
			OptionMessage.displayMessage("A Selection Group must be selected");
			return false;
		}
		if (dataParms.getSelectionModel().isEmpty()) { 
			OptionMessage.displayMessage("A Data Parameter Group must be selected");
			return false;
		}
		if (fileName.getText().isEmpty() && !generateName.isSelected()) {
			OptionMessage.displayMessage("You must either enter a file name or select Generate Name");
			return false;					
		}
		boolean updateName=false;
		ReportDataRow tempRow = new ReportDataRow();
		if (!newRow && !row.getName().equals(name.getText()))
			updateName=true;
		if ((newRow || updateName) && tempRow.loadRow(name.getText(), params)) {
			if (OptionMessage.yesnoMessage("Report already exists.  Do you wish to overwrite them?"))  
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
		stage.close();
		return true;
	}
	private void updateParms() {
		row.setSelection(selections.getSelectionModel().getSelectedItem());
		row.setDataParms(dataParms.getSelectionModel().getSelectedItem());
		RadioButton selectedType = (RadioButton) group.getSelectedToggle();
		if (selectedType == createCsvFile) {
			row.setType(Constants.ReportType.CSV);
			row.setTemplate(templateLbl.getText());
		}
		else {
			if (selectedType == createDatabase) {
				row.setType(Constants.ReportType.DATABASE);
				row.setTemplate(templateLbl.getText());
			}
			else 
				if (selectedType == createSpreadsheet) {
					row.setType(Constants.ReportType.SPREADSHEET);
					row.setTemplate(templateLbl.getText());
				}
				else {
					row.setType(Constants.ReportType.JASPER);
					row.setTemplate(templates.getSelectionModel().getSelectedItem());
				}
		}
		row.setOutputFileName(fileName.getText());
		row.setGenerate(generateName.isSelected());
		row.setOverWrite(overWriteFile.isSelected());
		row.setAddDate(addDateStamp.isSelected());
		row.setDelimiter(csvDelimiter.getValue());
		row.setTargetExcel(addBOM.isSelected());
		
	}
}
