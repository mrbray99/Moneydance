package com.moneydance.modules.features.jasperreports;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportDataPane {
	private Parameters params;
	private TextField name;
	private ComboBox<String> templates;
	private ComboBox<String> selections;
	private ComboBox<String> dataParms;
	private List<TemplateRow> listTemplates;
	private List<SelectionRow> listSelections;
	private List<DataRow> listDataParms;
	private ObservableList<String> listTempNames;
	private ObservableList<String> listSelNames;
	private ObservableList<String> listDataNames;
	private Stage stage;
	private Scene scene;
	private GridPane pane;
	private ReportDataRow row;
	private boolean newRow = false;
	
	public ReportDataPane(Parameters paramsp) {
		params = paramsp;
		row = new ReportDataRow();
		newRow= true;
	}
	public ReportDataPane(Parameters paramsp, ReportDataRow rowp) {
		row = rowp;
		params = paramsp;
	}
	public ReportDataRow displayPanel() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new GridPane();
		scene = new Scene(pane,400,200);
		stage.setScene(scene);
		listTemplates = params.getTemplateList();
		listSelections = params.getSelectionList();
		listDataParms = params.getDataList();
		Label reportLbl = new Label("Name");
		name = new TextField();
		name.setPadding(new Insets(10,10,10,10));
		if (!newRow) {
			name.setText(row.getName());
			name.setDisable(true);
		}
		Label templatesLbl = new Label("Report Template");
		templates = new ComboBox<>();
		GridPane.setMargin(templatesLbl,new Insets(10,10,10,10));
		listTempNames = FXCollections.observableArrayList();
		for (TemplateRow rowT : listTemplates) {
			listTempNames.add(rowT.getName());
		}
		templates.setItems(listTempNames);
		if (!newRow)
			templates.getSelectionModel().select(row.getTemplate());
		GridPane.setMargin(templates,new Insets(10,10,10,10));
		Label selectionLbl = new Label("Selection Group");
		selections = new ComboBox<>();
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
		GridPane.setMargin(dataParmsLbl,new Insets(10,10,10,10));
		listDataNames = FXCollections.observableArrayList();
		for (DataRow rowT : listDataParms) {
			listDataNames.add(rowT.getName());
		}
		dataParms.setItems(listDataNames);
		if (!newRow)
			dataParms.getSelectionModel().select(row.getDataParms());
		GridPane.setMargin(dataParms,new Insets(10,10,10,10));
		Button okBtn = new Button("OK");
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (name.getText().isEmpty()) {
					OptionMessage.displayMessage("Name must be entered");
					return;
				}
				if (templates.getSelectionModel().isEmpty()) { 
					OptionMessage.displayMessage("A Report Template must be selected");
					return;
				}
				if (selections.getSelectionModel().isEmpty()) { 
					OptionMessage.displayMessage("A Selection Group must be selected");
					return;
				}
				if (dataParms.getSelectionModel().isEmpty()) { 
					OptionMessage.displayMessage("A Data Parameter Group must be selected");
					return;
				}
				boolean createRow;
				ReportDataRow tempRow = new ReportDataRow();
				if (newRow && tempRow.loadRow(name.getText(), params)) {
					if (OptionMessage.yesnoMessage("Report Instance already exists.  Do you wish to overwrite it?")) {
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
					row.setTemplate(templates.getSelectionModel().getSelectedItem());
					row.setSelection(selections.getSelectionModel().getSelectedItem());
					row.setDataParms(dataParms.getSelectionModel().getSelectedItem());
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
		int ix=0;
		int iy=0;
		pane.add(reportLbl, ix++, iy);
		pane.add(name, ix, iy++);
		ix=0;
		pane.add(templatesLbl, ix++, iy);
		pane.add(templates, ix, iy++);
		ix=0;
		pane.add(selectionLbl, ix++, iy);
		pane.add(selections, ix, iy++);
		ix=0;
		pane.add(dataParmsLbl, ix++, iy);
		pane.add(dataParms, ix, iy++);
		ix=0;
		pane.add(okBtn, ix++, iy);
		pane.add(cancelBtn,ix++, iy);
		stage.showAndWait();
		return row;
	}


}
