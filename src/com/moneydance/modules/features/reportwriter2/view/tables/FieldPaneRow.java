package com.moneydance.modules.features.reportwriter2.view.tables;

import com.moneydance.modules.features.reportwriter2.Constants.ReportFieldType;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.BEANFIELDTYPE;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.FieldPaneController;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;

public class FieldPaneRow {
	private SimpleBooleanProperty  dataDirty;
	private ReportField field;
	private String key;
	private String record;
	private String fieldName;
	private CheckBox checkSelect;
	private boolean selected;
	private BEANFIELDTYPE type;
	private ReportFieldType reportType;
	private FieldPaneController controller;
	private FieldPaneRow thisObj;
	public FieldPaneRow(ReportField field, FieldPaneController controller) {
		this.controller = controller;
		thisObj = this;
		this.field = field;
		record = field.getFieldBean().getTableName();
		key = field.getKey();
		fieldName = field.getName();
		type = field.getFieldType();
		reportType = field.getReportType();
		selected = field.isSelected();
		dataDirty = new SimpleBooleanProperty();
		dataDirty.addListener((value)->{
			thisObj.controller.setDirty(dataDirty.getValue());
		});
		dataDirty.set(false);
	}
	
	public ReportField getField() {
		return field;
	}
	

	public boolean isSelected() {
		return selected;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setSelect(boolean select) {
		if (this.selected && !select)
			if(controller.checkFieldDelete(field)) {
				OptionMessage.displayErrorMessage("You can not deselect this field as it is in use on the layout");
				this.selected=true;
				return;
			}
		this.selected = select;
//		controller.notifyListeners(field,select);
	}

	public BEANFIELDTYPE getType() {
		return type;
	}

	public void setType(BEANFIELDTYPE type) {
		this.type = type;
	}

	public ReportFieldType getReportType() {
		return reportType;
	}

	public void setReportType(ReportFieldType reportType) {
		this.reportType = reportType;
	}

	public String getCol1() {
		return record;
	}
	public String getCol2() {
		return fieldName;
	}
	public String getCol3() {
		return type.toString();
	}
	public CheckBox getCol4() {
		checkSelect = new CheckBox();
		checkSelect.setOnAction((event)->{
			dataDirty.set(true);
			setSelect(checkSelect.isSelected());
			checkSelect.setSelected(selected);
		});
		checkSelect.setSelected(selected);
		return checkSelect;
	}
}
