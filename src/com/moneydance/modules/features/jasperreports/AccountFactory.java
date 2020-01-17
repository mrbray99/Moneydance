package com.moneydance.modules.features.jasperreports;

import java.util.List;
import java.util.SortedMap;

import javax.swing.JOptionPane;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.AccountUtil;
import com.infinitekind.moneydance.model.AcctFilter;
import com.moneydance.modules.features.databeans.AccountBean;

public class AccountFactory implements AcctFilter{

	private DataDataRow dataParams;
	private Database database;
	private List<Account> selectedAccts;
	private List<String> accounts;
	private SortedMap<String, DataParameter> map;
	private AccountBook book;
	public AccountFactory(AccountBook bookp, DataDataRow dataParamsp,Database databasep) {
		book = bookp;
		dataParams = dataParamsp;
		database = databasep;
		map = dataParams.getParameters();
		if (!map.containsKey(Constants.PARMACCOUNTS)) {
			JOptionPane.showMessageDialog(null,"No accounts have been selected)");
			return;
		}
		else
			accounts = map.get(Constants.PARMACCOUNTS).getList();
		selectedAccts = AccountUtil.allMatchesForSearch(book, this);
		for (Account acct : selectedAccts) {
			AccountBean bean= new AccountBean();
			bean.addAccount(acct);
			bean.populateData();
			String sql = bean.createSQL();
			database.executeUpdate(sql);
		}
	}
	@Override
	public boolean matches(Account paramAccount) {
		if (accounts.contains(paramAccount.getAccountName()))
			return true;
		return false;
	}
	@Override
	public String format(Account paramAccount) {
		// TODO Auto-generated method stub
		return null;
	}
}
