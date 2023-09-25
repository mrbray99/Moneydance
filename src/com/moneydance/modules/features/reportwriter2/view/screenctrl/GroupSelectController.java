package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.SortedMap;

import com.moneydance.modules.features.reportwriter2.Constants.FieldFunction;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportBanner;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.tables.GroupSelectionRow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GroupSelectController extends PopUpController {
	@FXML
	private TableView<GroupSelectionRow> fieldTab;
	private ObservableList<GroupSelectionRow> model;
	private GroupSelectionRow selectedRow=null;
	private boolean dirty=true;
	public void setUpFields(ReportTemplate template,SortedMap<String,ReportField>fields, ReportBanner banner) {
		model = FXCollections.observableArrayList();
		for (ReportField field : fields.values()) {
			if (!field.isGroupSelected()||(banner != null && field==banner.getGroupField())) {
				GroupSelectionRow row = new GroupSelectionRow(field);
				model.add(row);
				if (banner != null && field==banner.getGroupField())
					selectedRow = row;
			}
		}
		fieldTab.setEditable(true);
		fieldTab.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		/*
		 * Name
		 */
		TableColumn<GroupSelectionRow, String> name = new TableColumn<>("Field Name");
		/*
		 * Analysis Type
		 */
		TableColumn<GroupSelectionRow, ComboBox<String>> analysis = new TableColumn<>("Analysis Type");
		fieldTab.getColumns().addAll(name, analysis);
		fieldTab.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("fieldTitle"));
		analysis.setCellValueFactory(new PropertyValueFactory<>("analysisType"));
		fieldTab.getSelectionModel().selectedItemProperty().addListener((options, oldVal, newVal)->{
			if (oldVal != null) 
				oldVal.getField().setGroupSelected(false);
			selectedRow = newVal;
			selectedRow.getField().setGroupSelected(true);
			dirty=true;
		});
		if (selectedRow != null)
			fieldTab.getSelectionModel().select(selectedRow);
	}
	public boolean valid() {
		if (selectedRow == null) {
			OptionMessage.displayErrorMessage("You must select a field to group on");
			return false;
		}
		return true;
	}
	public boolean isDirty() {
		return dirty;
	}
	public ReportField getField() {
		GroupSelectionRow row = fieldTab.getSelectionModel().getSelectedItem();
		if (row==null)
			return null;
		return row.getField();
	}
	public FieldFunction getFunction() {
		if (selectedRow==null)
			return null;
		return selectedRow.getFunction();
	}
}
