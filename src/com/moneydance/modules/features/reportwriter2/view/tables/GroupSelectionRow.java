package com.moneydance.modules.features.reportwriter2.view.tables;

import com.moneydance.modules.features.reportwriter2.Constants.FieldFunction;
import com.moneydance.modules.features.reportwriter2.Constants.FuncType;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class GroupSelectionRow {
	private ReportField field;
	private FieldFunction selectedFunc=null;
	public GroupSelectionRow(ReportField field) {
		this.field = field;
	}		
	public String getFieldTitle() {
		return field.getName();
	}
	public ReportField getField() {
		return field;
	}
	public ComboBox<String> getAnalysisType() {
		ComboBox<String> box = new ComboBox<>();
		box.getSelectionModel().selectedItemProperty().addListener((options,oldValue, newValue)->{
			selectedFunc= null;
			if (newValue.equals("None")) {
				selectedFunc = null;
				return;
			}
			for (FieldFunction func :FieldFunction.values()) {
				if (newValue.equals(func.getName())) {
					selectedFunc = func;
				}
			}				
		});
		ObservableList<String> functions = FXCollections.observableArrayList();
		functions.add("None");
		for (FieldFunction func :FieldFunction.values()) {
			switch (field.getFieldType()) {
			case BOOLEAN:
				if (func.getOutputType() == FuncType.STRING)
					functions.add(func.getName());
				break;
			case DATEINT:
				if (func.getOutputType() == FuncType.DATE)
					functions.add(func.getName());
				break;
			case DOUBLE:
			case INTEGER:
			case LONG:
			case MONEY:
			case NUMERIC:
			case PERCENT:
				if (func.getOutputType() == FuncType.NUMERIC)
					functions.add(func.getName());
				break;
			case STRING:
				if (func.getOutputType() == FuncType.STRING)
					functions.add(func.getName());
				break;
			default:
				break;
			}
		}
		box.setItems(functions);
		return box;
	}
	public FieldFunction getFunction() {
		return selectedFunc;
	}
}
