package  com.moneydance.modules.features.securitypriceload;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.CurrencyType;

public class DummyAccount {
	private String strAccountName;
	private Account acct;
	private CurrencyType ctCurrency;
	private CurrencyType ctRelativeCurrency;
	private Boolean bDifferentCur;
	public  DummyAccount (){
		
	}
	public String getAccountName (){
		return strAccountName;
	}
	public CurrencyType getCurrencyType() {
		return ctCurrency;
	}
	public CurrencyType getRelativeCurrencyType() {
		return ctRelativeCurrency;
	}
	public Boolean getDifferentCur(){
		return bDifferentCur;
	}
	public Account getAccount(){
		return acct;
	}
	public void setAccountName (String strAccountNamep){
		strAccountName = strAccountNamep;
	}
	public void setCurrencyType (CurrencyType ctCurrencyp){
		ctCurrency = ctCurrencyp;
		ctRelativeCurrency = getRelativeCurrency(ctCurrency);
		if (ctRelativeCurrency == null || Main.context.getCurrentAccountBook().getCurrencies().getBaseType()== ctRelativeCurrency)
			bDifferentCur = false;
		else
			bDifferentCur = true;
	}
	public void setAccount (Account acctp){
		acct = acctp;
	}
	static CurrencyType getRelativeCurrency(CurrencyType curr) {
	  String relCurrID = curr.getParameter(CurrencyType.TAG_RELATIVE_TO_CURR);
	  return relCurrID == null ? null : curr.getBook().getCurrencies().getCurrencyByIDString(relCurrID);
    }
}
