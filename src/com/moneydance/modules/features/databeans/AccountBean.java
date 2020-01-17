package com.moneydance.modules.features.databeans;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.infinitekind.moneydance.model.Account;

public class AccountBean extends DataBean{
@Expose 	private String accountName;
@Expose 	private String accountType;
@Expose 	private String accountDescription;
@Expose 	private long annualFee;
@Expose 	private double apr;
@Expose 	private double aprPercent;
@Expose 	private String bankAccountNumber;
@Expose 	private String bankName;
@Expose 	private String broker;
@Expose 	private String brokerPhone;
@Expose 	private int cardExpirationMonth;
@Expose 	private int cardExpirationYear;
@Expose 	private String cardNumber;
@Expose 	private long creditLimit;
@Expose 	private String currencyTypeID;
@Expose 	private String currencyTypeName;
@Expose 	private String defaultCategory;
@Expose 	private String fullAccountName;
	/*
	 * Transient fields
	 */
	private transient Account account;
	private transient String tableName = "Accounts";
	
	public AccountBean () {
		super();
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}

	public long getAnnualFee() {
		return annualFee;
	}

	public void setAnnualFee(long annualFee) {
		this.annualFee = annualFee;
	}

	public double getApr() {
		return apr;
	}

	public void setApr(double apr) {
		this.apr = apr;
	}

	public double getAprPercent() {
		return aprPercent;
	}

	public void setAprPercent(double aprPercent) {
		this.aprPercent = aprPercent;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String getBrokerPhone() {
		return brokerPhone;
	}

	public void setBrokerPhone(String brokerPhone) {
		this.brokerPhone = brokerPhone;
	}

	public int getCardExpirationMonth() {
		return cardExpirationMonth;
	}

	public void setCardExpirationMonth(int cardExpirationMonth) {
		this.cardExpirationMonth = cardExpirationMonth;
	}

	public int getCardExpirationYear() {
		return cardExpirationYear;
	}

	public void setCardExpirationYear(int cardExpirationYear) {
		this.cardExpirationYear = cardExpirationYear;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public long getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(long creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getCurrencyTypeID() {
		return currencyTypeID;
	}

	public void setCurrencyTypeID(String currencyTypeID) {
		this.currencyTypeID = currencyTypeID;
	}
	public String getCurrencyTypeName() {
		return currencyTypeName;
	}

	public void setCurrencyTypeName(String currencyTypeName) {
		this.currencyTypeName = currencyTypeName;
	}

	public String getDefaultCategory() {
		return defaultCategory;
	}

	public void setDefaultCategory(String defaultCategory) {
		this.defaultCategory = defaultCategory;
	}

	public String getFullAccountName() {
		return fullAccountName;
	}

	public void setFullAccountName(String fullAccountName) {
		this.fullAccountName = fullAccountName;
	}
	public static DataBean deserialize(String beanData) {
		AccountBean bean;
		Type listType = new TypeToken<AccountBean>(){}.getType();
		bean = new Gson().fromJson(beanData,listType);
		return bean;
	}
	public String serialize(DataBean bean) {
		return new Gson().toJson(bean);
	}

	public void addAccount(Account account) {
		this.account = account;
	}
	
	public Account retrieveAccount() {
		return account;
	}

	@Override
	public void populateData() {
		setAccountName(account.getAccountName());
		setAccountType(account.getAccountType().name());
		setAccountDescription(account.getAccountDescription());
		setAnnualFee(account.getAnnualFee());
		setApr(account.getAPR());
		setAprPercent(account.getAPRPercent());
		setBankAccountNumber(account.getBankAccountNumber());
		setBankName(account.getBankName());
		setBroker(account.getBroker());
		setBrokerPhone(account.getBrokerPhone());
		setCardExpirationMonth(account.getCardExpirationMonth());
		setCardExpirationYear(account.getCardExpirationYear());
		setCardNumber(account.getCardNumber());
		setCurrencyTypeID(account.getCurrencyType().getIDString());
		setCurrencyTypeName(account.getCurrencyType().getName());
		if (account.getDefaultCategory()  == null)
			setDefaultCategory("none");
		else
			setDefaultCategory(account.getDefaultCategory().getAccountName());
		setFullAccountName(account.getFullAccountName());
	}
	
	@Override
	public String createTable() {
		StringBuilder bld = new StringBuilder("Create table "+tableName+ " (");
		bld.append("AccountName Varchar(255)");
		bld.append(",FullAccountName Varchar(255)");
		bld.append(",Type Varchar(255)");
		bld.append(",AnnualFee Bigint");
		bld.append(",APR Double");
		bld.append(",APRPercent Float");
		bld.append(",Accountnumber Varchar(20)");
		bld.append(",Bank Varchar(255)");
		bld.append(",Broker Varchar(255)");
		bld.append(",Brokerphone Varchar(255)");
		bld.append(",Cardmonth Tinyint(2)");
		bld.append(",Cardyear Tinyint(4)");
		bld.append(",Cardnumber Varchar(16)");
		bld.append(",Creditlimit Bigint(10)");
		bld.append(",CurrencyID Varchar(10)");
		bld.append(",CurrencyName Varchar(255)");
		bld.append(",Category Varchar(255))");
		return bld.toString();	
	}
	@Override
	public String createSQL() {
		StringBuilder bld = new StringBuilder("Insert into "+tableName+ " (");
		bld.append("AccountName");
		bld.append(",FullAccountName");
		bld.append(",Type");
		bld.append(",AnnualFee");
		bld.append(",APR");
		bld.append(",APRPercent");
		bld.append(",Accountnumber");
		bld.append(",Bank");
		bld.append(",Broker");
		bld.append(",Brokerphone");
		bld.append(",Cardmonth");
		bld.append(",Cardyear");
		bld.append(",Cardnumber");
		bld.append(",Creditlimit");
		bld.append(",CurrencyID");
		bld.append(",CurrencyName");
		bld.append(",Category) values ('");
		bld.append(accountName);
		bld.append("','");
		bld.append(fullAccountName);
		bld.append("','");
		bld.append(accountType);
		bld.append("',");
		bld.append(annualFee);
		bld.append(",");
		bld.append(apr);
		bld.append(",");
		bld.append(aprPercent);
		bld.append(",'");
		bld.append(bankAccountNumber);
		bld.append("','");
		bld.append(bankName);
		bld.append("','");
		bld.append(broker);
		bld.append("','");
		bld.append(brokerPhone);
		bld.append("',");
		bld.append(cardExpirationMonth);
		bld.append(",");
		bld.append(cardExpirationYear);
		bld.append(",'");
		bld.append(cardNumber);
		bld.append("',");
		bld.append(creditLimit);
		bld.append(",'");
		bld.append(currencyTypeID);
		bld.append("','");
		bld.append(currencyTypeName);
		bld.append("','");
		bld.append(defaultCategory);
		bld.append("')");

		return bld.toString();	

	}
	
	@Override
	public String getTableName() {
		return tableName;
	}

}

