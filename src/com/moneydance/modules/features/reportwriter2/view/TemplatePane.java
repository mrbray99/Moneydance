/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
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


import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.samples.DownloadException;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.tables.TemplateRow;

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

public class TemplatePane extends ScreenPanel {
	private Parameters params;
	private ObservableList<TemplateRow> model;
	private TableView<TemplateRow> thisTable;
	private Button copyBtn;
      private Button editBtn;
      private Button deleteBtn;
      private Button addBtn;
      private Tooltip editTip = new Tooltip();
      private Tooltip deleteTip = new Tooltip();
      private Tooltip addTip = new Tooltip();
      private Tooltip copyTip = new Tooltip();

	public TemplatePane(Parameters paramsp) {
		super();
		setStyle(MyReport.css);
		params = paramsp;
		setUpTable();
		Label templateLbl = new Label("Report Templates");
		templateLbl.setTextAlignment(TextAlignment.CENTER);
		templateLbl.setFont(Font.font("Veranda", FontWeight.BOLD, 20.0));
		setMargin(templateLbl,new Insets(10,10,10,10));
		setColumnSpan(templateLbl,4);
		add(templateLbl, 0, 0);
		GridPane.setHalignment(templateLbl, HPos.CENTER);
		add(thisTable, 0, 1);
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
		editTip.setText("Edit an existing Report Template");
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
		deleteTip.setText("Delete an existing Report Template");
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
		addTip.setText("Add a new Report Template");
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
		copyTip.setText("Copy a Report Template");
		copyBtn.setTooltip(copyTip);
		add(addBtn,0,2);
		add(editBtn,1,2);
		add(copyBtn,2,2);
		add(deleteBtn,3,2);
		GridPane.setMargin(copyBtn,new Insets(10, 10, 10, 10));
		resize();
	}
	private void addRow() {
		TemplateDataPane  templateScreen = new TemplateDataPane(this,params);
		ReportTemplate  template = templateScreen.displayPanel();
		if(template != null) {
			TemplateRow templateRow = new TemplateRow(params);
			templateRow.setName(template.getName());
			templateRow.setFileName(params.getDataDirectory()+"/"+template.getName()+Constants.SELEXTENSION);
			templateRow.setLastModified(Main.cdate.format(Main.now));
			templateRow.setLastUsed(Main.cdate.format(Main.now));
			templateRow.setCreated(Main.cdate.format(Main.now));
			params.addTemplateRow(templateRow);
			resetData();
		}
	}
	@Override
	protected void openMsg() {
		editRow();
	}
	private void editRow() {
		TemplateRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a template");
			return;
		}
		ReportTemplate templateEdit = new ReportTemplate(params);
		templateEdit.setName(row.getName());
		if (templateEdit.loadTemplate()) {
			TemplateDataPane pane = new TemplateDataPane(this,params,templateEdit);
			templateEdit = pane.displayPanel();
			row.setLastModified(Main.cdate.format(Main.now));
			row.setLastUsed(Main.cdate.format(Main.now));
			params.updateTemplateRow(row);
			resetData();
		}
		
	}
	protected void deleteMsg() {
		deleteRow();
	}
	private void deleteRow() {
		TemplateRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a template");
			return;
		}
		if(params.checkTemplate(row.getName())) {
			OptionMessage.displayMessage("This Template is used in a report.  It can not be deleted.");
			return;
		}
		ReportTemplate rowEdit = new ReportTemplate(params);
		rowEdit.setName(row.getName());
		if (rowEdit.loadTemplate()) {
			Boolean result = OptionMessage.yesnoMessage("Are you sure you wish to delete Template "+row.getName());
			if (result) {
				rowEdit.deleteTemplate();
				params.removeTemplateRow(row);
			}
			resetData();
		}
		
	}
	private void copyRow() {
		TemplateRow row = thisTable.getSelectionModel().getSelectedItem();
		if (row ==null) {
			OptionMessage.displayMessage("Please Select a template");
			return;
		}
		String result = "";
		ReportTemplate oldTemplate = new ReportTemplate(params);
		oldTemplate.setName(row.getName());
		if (oldTemplate.loadTemplate()) {
			while (result.isEmpty()) {
				result = OptionMessage.inputMessage("Enter the name of the new template");
				if (result.equals(Constants.CANCELPRESSED))
					return;
				if (result.isEmpty())
					OptionMessage.displayMessage("A name must be entered");
				else {
					ReportTemplate template = new ReportTemplate(params);
					template.setName(result);
					if (template.loadTemplate()) {
						OptionMessage.displayMessage("New name "+result+" already exists");
						result = "";
					}
					else {
						TemplateRow newRow = new TemplateRow(params);
						newRow.setName(result);
						params.addTemplateRow(newRow);
						oldTemplate.setName(result);
						oldTemplate.saveTemplate();
					}				
				}
			}
		}
		else {
			OptionMessage.displayMessage("Template "+row.getName()+" can not be found");
		}
		resetData();
	}


	@Override
	public void resize() {
		super.resize();
		thisTable.setPrefWidth(SCREENWIDTH);
		thisTable.setPrefHeight(SCREENHEIGHT);
	}

	private void setUpTable() {
		thisTable = new TableView<TemplateRow>();
		thisTable.setEditable(true);
		thisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		thisTable.setMaxWidth(Double.MAX_VALUE);
		thisTable.setMaxHeight(Double.MAX_VALUE);
		/*
		 * Name
		 */
		TableColumn<TemplateRow,String> name = new TableColumn<>("Name");
		/*
		 * Last Created
		 */
		TableColumn<TemplateRow,String> created = new TableColumn<>("Created");
		/*
		 * Last Modified
		 */
		TableColumn<TemplateRow,String> lastModified = new TableColumn<>("Modified");
		/*
		 * Last Used
		 */
		TableColumn<TemplateRow,String> lastUsed = new TableColumn<>("Used");
		thisTable.setRowFactory(tv->{
			TableRow<TemplateRow> row = new TableRow<>();
			row.setOnMouseClicked(event->{
				if (event.getClickCount()==2 && (!row.isEmpty())) {
					editRow();
				}
			});
			return row;
		});	
		thisTable.getColumns().addAll(name, created,lastModified,lastUsed);
		model = FXCollections.observableArrayList(params.getTemplateList());
		thisTable.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		created.setCellValueFactory(new PropertyValueFactory<>("created"));
		lastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
		lastUsed.setCellValueFactory(new PropertyValueFactory<>("lastUsed"));
	}

	public void resetData() {
		Main.rwDebugInst.debug("TemplatePane", "watchReports",MRBDebug.DETAILED,"Templates reset");
		params.setReportTemplates();
		model = FXCollections.observableArrayList(params.getTemplateList());
		thisTable.setItems(model);
		thisTable.refresh();
	}

	private void downloadTemplates() throws DownloadException {
		TemplateDownloadPane dataPane = new TemplateDownloadPane(params);
		dataPane.displayPanel();
		
	}
	public void requestExtFocus() {
		this.getMainScreen().requestExtFocus();
	}
	public void closeDown() {
		Main.rwDebugInst.debugThread("TemplatePane", "closeDown", MRBDebug.SUMMARY,
				"Closing background thread");
	}

}
