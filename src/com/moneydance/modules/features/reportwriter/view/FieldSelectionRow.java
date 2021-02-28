package com.moneydance.modules.features.reportwriter.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

public class FieldSelectionRow {
	private String fieldName;
	private String fieldTitle;
	private Boolean selected;
	public FieldSelectionRow(String fieldName, String fieldTitle,Boolean selected) {
		setFieldName(fieldName);
		setFieldTitle(fieldTitle);
		setSelected(selected);
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldTitle() {
		return fieldTitle;
	}
	public void setFieldTitle(String fieldTitle) {
		this.fieldTitle = fieldTitle;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public CheckBox getIncluded() {
		CheckBox includedBox = new CheckBox();
		includedBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				setSelected(new_val);
			}
			
		});
		includedBox.setSelected(selected);
		return includedBox;
	}
	

}
