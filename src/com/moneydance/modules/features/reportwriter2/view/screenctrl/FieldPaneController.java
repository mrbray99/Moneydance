package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.FieldSelectListener;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.tables.FieldPaneRow;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;

public class FieldPaneController {
	@FXML
	private GridPane gridPane;
	@FXML
	private Label templateName;
	@FXML
	private TableView<FieldPaneRow> fieldTable;
	@FXML
	private TableColumn<FieldPaneRow,String> recordCol;
	@FXML
	private TableColumn <FieldPaneRow,String> fieldCol;
	@FXML
	private TableColumn <FieldPaneRow,String>typeCol;
	@FXML
	private TableColumn <FieldPaneRow,CheckBox>selectCol;
	@FXML
	private Button okBtn;
	private ObservableList<FieldPaneRow> tableItems;
	private SortedMap<String,ReportField> fields;
	private SortedMap<String,ReportField> availableFields;
	private List<FieldSelectListener> listeners = new ArrayList<FieldSelectListener>();
	private Callback<String,Integer> controller;
	private ReportTemplate template;
	public FieldPaneController(Callback<String,Integer> controller) {
		this.controller = controller;
	}
	public void setFields(ReportTemplate template) {
		this.template = template;
		if (Main.loadedIcons.closeImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.closeImg));

		templateName.setText(template.getName());
		availableFields = template.getAvailableFields();
		tableItems = FXCollections.observableArrayList();
		fields = template.getSelectedFields();
		for (Entry<String, ReportField> entry:availableFields.entrySet()) {
			ReportField tempFld;
			if (fields.containsKey(entry.getKey())) {
				tempFld = fields.get(entry.getKey());
				tempFld.setSelected(true);
			}
			else {
				tempFld = entry.getValue();
				tempFld.setSelected(false);
			}
			FieldPaneRow row = new FieldPaneRow(tempFld,this);
			tableItems.add(row);
		}
		fieldTable.setEditable(true);
		fieldTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		fieldTable.setMaxWidth(Double.MAX_VALUE);
		fieldTable.setMaxHeight(Double.MAX_VALUE);
		recordCol.setCellValueFactory(new PropertyValueFactory<>("Col1"));
		fieldCol.setCellValueFactory(new PropertyValueFactory<>("Col2"));
		typeCol.setCellValueFactory(new PropertyValueFactory<>("Col3"));
		selectCol.setCellValueFactory(new PropertyValueFactory<>("Col4"));
		fieldTable.setItems(tableItems);
		ObservableList<RowConstraints> rows = gridPane.getRowConstraints();
		RowConstraints row1 = rows.get(0);
		row1.setMaxHeight(40.0);
		RowConstraints row3 = rows.get(2);
		row3.setMaxHeight(50.0);
		
	}

	@FXML
	private void saveAndClose(ActionEvent action) {
		for (FieldPaneRow row : tableItems) {
			ReportField field = row.getField();
			if(row.isSelected()) {
				field.setSelected(row.isSelected());
				field.setKey(row.getKey());
				field.setFieldType(row.getType());
				field.setReportType(row.getReportType());
				fields.put(row.getKey(),field);
			}
			else
				fields.remove(row.getKey());
		}
		template.setSelectedFields(fields);
		Platform.runLater(new Runnable() {
			@Override public void run() {
				controller.call(Constants.CLOSEFIELDLIST);
			}
		});
	}
	public void setDirty(Boolean dirty) {
		template.setDirty(dirty);
	}
	public boolean  checkFieldDelete(ReportField field) {
		for (FieldSelectListener listener : listeners) {
			if (listener.checkFieldDelete(field))
				return true;
		}
		return false;
	}
	public void  notifyListeners(ReportField field, boolean select) {
		for (FieldSelectListener listener : listeners) {
			if (select)
				listener.fieldSelectionAdded(field);
			else
				listener.fieldSelectionRemoved(field);
		}
		return;
	}
	public void  notifyListenersUpdate(ReportField field,boolean selected) {
		for (FieldSelectListener listener : listeners) {
			listener.fieldSelectionUpdated(field,selected);
		}
		return;
	}
	public void addFieldListener(FieldSelectListener listener) {
		listeners.add(listener);
	}
	public void removeFieldListener(FieldSelectListener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}
	
}
