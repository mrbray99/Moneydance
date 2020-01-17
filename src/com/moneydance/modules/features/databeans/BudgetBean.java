package com.moneydance.modules.features.databeans;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.infinitekind.moneydance.model.Budget;
import com.infinitekind.moneydance.model.BudgetItem;

public class BudgetBean extends DataBean {
	@Expose private String name;
	@Expose private String period;
	@Expose private String account;
	@Expose private String currency;
	@Expose private String daterange;
	@Expose private int interval;
	@Expose private int intervalend;
	@Expose private String transferacct;
	@Expose private long amount;
	/*
	 * Transient fields
	 */
	private transient Budget budget;
	private transient BudgetItem item;
	
	
	public BudgetBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDaterange() {
		return daterange;
	}
	public void setDaterange(String daterange) {
		this.daterange = daterange;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getIntervalend() {
		return intervalend;
	}
	public void setIntervalend(int intervalend) {
		this.intervalend = intervalend;
	}
	public String getTransferacct() {
		return transferacct;
	}
	public void setTransferacct(String transferacct) {
		this.transferacct = transferacct;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	public static DataBean deserialize(String beanData) {
		BudgetBean bean;
		Type listType = new TypeToken<BudgetBean>(){}.getType();
		bean = new Gson().fromJson(beanData,listType);
		return bean;
	}
	@Override
	public void populateData() {
	}

}
