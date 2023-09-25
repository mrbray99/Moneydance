package com.moneydance.modules.features.reportwriter2.report;

public class FieldValue {
	private String name;
	private Double numeric;
	private String text;
	private Integer count;

	public FieldValue(String name, Double numeric, String text) {
		this.name = name;
		this.numeric = numeric;
		this.text = text;
		count=0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getNumeric() {
		return numeric;
	}

	public void setNumeric(Double numeric) {
		this.numeric = numeric;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
}