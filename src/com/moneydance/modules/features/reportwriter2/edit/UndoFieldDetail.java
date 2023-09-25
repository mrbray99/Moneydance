package com.moneydance.modules.features.reportwriter2.edit;

public class UndoFieldDetail {
	private String action;
	private String fieldName;
	private String beforeValue;
	private String afterValue;
	public String getFieldName() {
		return fieldName;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getBeforeValue() {
		return beforeValue;
	}
	public void setBeforeValue(String beforeValue) {
		this.beforeValue = beforeValue;
	}
	public String getAfterValue() {
		return afterValue;
	}
	public void setAfterValue(String afterValue) {
		this.afterValue = afterValue;
	}
	

}
