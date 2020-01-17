package com.moneydance.modules.features.databeans;

import com.google.gson.annotations.Expose;

public class BeanRecord {
	@Expose private String beanType;
	@Expose private String beanData;
	public BeanRecord() {
		
	}
	public BeanRecord(String type,String data) {
		beanType = type;
		beanData = data;
	}
	public String getBeanType() {
		return beanType;
	}
	public void setBeanType(String beanType) {
		this.beanType = beanType;
	}
	public String getBeanData() {
		return beanData;
	}
	public void setBeanData(String beanData) {
		this.beanData = beanData;
	}
	
}
