package com.moneydance.modules.features.databeans;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class TransactionBean extends DataBean {
	@Expose private String account;
	@Expose private String address;
	@Expose private String check;
	@Expose private String cleared;
	@Expose private String entered;
	@Expose private String date;
	@Expose private String description;
	@Expose private String status;
	@Expose private String tax;
	@Expose private Double value;
	@Expose private String transfer;

	public TransactionBean (String account, String address,String check, String cleared, String entered, String date, String description,
			String status, String tax,Double value,String transfer) {
		this.account = account;
		this.address = address;
		this.check = check;
		this.cleared = cleared;
		this.entered = entered;
		this.date = date;
		this.description = description;
		this.status = status;
		this.tax = tax;
		this.value = value;
		this.transfer = transfer;
	}
	public TransactionBean () {
		
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public String getCleared() {
		return cleared;
	}
	public void setCleared(String cleared) {
		this.cleared = cleared;
	}
	public String getEntered() {
		return entered;
	}
	public void setEntered(String entered) {
		this.entered = entered;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}public String getTax() {
		return tax;
	}
	public void setTax(String tax) {
		this.tax = tax;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public String getTransfer() {
		return transfer;
	}
	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}
	public static DataBean deserialize(String beanData) {
		TransactionBean bean = new TransactionBean();
		Type listType = new TypeToken<TransactionBean>(){
		}.getType();
		bean = new Gson().fromJson(beanData, listType);
		return bean;
	}
	public String createSQL() {
		StringBuilder bld = new StringBuilder("insert into transactions ");
		bld.append("(Account,address,checknum,cleared,entered,trandate,descripition,status,txnvalue,tax,transfer)");
		bld.append(" values('");
		bld.append(getAccount());
		bld.append("','");
		bld.append(getAddress());
		bld.append("','");
		bld.append(getCheck());
		bld.append("','");
		bld.append(getCleared());
		bld.append("','");
		bld.append(getEntered());
		bld.append("','");
		bld.append(getDate());
		bld.append("','");
		bld.append(getDescription());
		bld.append("','");
		bld.append(getStatus());
		bld.append("',");
		bld.append(getValue());
		bld.append(",'");
		bld.append(getTax());
		bld.append("','");
		bld.append(getTransfer());
		bld.append("');");
		return bld.toString();
	}
	@Override
	public void populateData() {
		// TODO Auto-generated method stub

	}
}
