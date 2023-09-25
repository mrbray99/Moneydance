package com.moneydance.modules.features.reportwriter2.report;

import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;

public class GroupValues {
	private String key;
	private ReportField keyField;
	private FieldValue currentValue;
	private SortedMap<Integer, FieldValue> functionValues;
	private SortedMap<String, FieldValue>variableValues;
	public GroupValues() {
		keyField = null;
		key="";
		functionValues = new TreeMap<Integer, FieldValue>();
		variableValues= new TreeMap<String, FieldValue>();
		currentValue = null;
		variableValues = new TreeMap<String,FieldValue>();
	}
	public GroupValues(ReportField keyField) {
		/*
		 * group value 0 is the field for this group
		 */
		this.keyField = keyField;
		key = keyField.getKey();
		functionValues = new TreeMap<Integer, FieldValue>();
		functionValues.put(0, new FieldValue(key, 0.0, ""));
		currentValue = null;
		variableValues = new TreeMap<String,FieldValue>();
	}

	public ReportField getKeyField() {
		return keyField;
	}

	public String getKey() {
		return key;
	}

	public String getKeyValueStr() {
		return functionValues.get(0).getText();
	}

	public Double getKeyValueNum() {
		return functionValues.get(0).getNumeric();
	}
	public FieldValue getFieldValue(String key) {
		return functionValues.get(key);
	}
	public SortedMap<Integer, FieldValue> getValues() {
		return functionValues;
	}
	public void setValues(SortedMap<Integer, FieldValue> values) {
		this.functionValues = values;
	}
	public FieldValue getCurrentValue() {
		return currentValue;
	}
	public void setCurrrentValue(FieldValue currrentValue) {
		this.currentValue = currrentValue;
	}
	public SortedMap<String, FieldValue> getVariableValues() {
		return variableValues;
	}
	public void setVariableValues(SortedMap<String, FieldValue> variableValues) {
		this.variableValues = variableValues;
	}
	
}

