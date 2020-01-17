package com.moneydance.modules.features.mrbutil;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.awt.GridC;

public class MRBSelectPanel{
	private JScrollPane missingScroll;
	private JScrollPane selectedScroll;
	private JList<String> listDisplayMissing;
	private JList<String> listDisplaySelect;
	private Map<Account,String> selectedMap;
	private SortedMap<String,MRBListItem> mapAccounts;
	private MRBListModel missingModel;
	private MRBListModel selectModel;
	private MRBLocale locale;
	private Boolean includeZero = false;
	private int iPANELHEIGHT=400;
	private int iPANELWIDTH=600;
	private int iSCROLLWIDTH=(iPANELWIDTH- 50)/2;
	private int iSCROLLHEIGHT=iPANELHEIGHT- 150;
	private AccountBook acctBook;
	private SortedMap<AccountType,Boolean> typeIncluded;
	private JButton selectBtn;
	private JButton deselectBtn;
	private JPanel panDisplay;
	public MRBSelectPanel (AccountBook acctBookp) {
		acctBook = acctBookp;
		locale = new MRBLocale(this,MRBConstants.LOCALEFILE);
		typeIncluded = new TreeMap<AccountType,Boolean>();
		mapAccounts = new TreeMap<String, MRBListItem>();
		
	}
	public void SetType (AccountType acctType, Boolean bInclude){
		if (typeIncluded.containsKey(acctType))
			typeIncluded.replace(acctType,bInclude);
		else
			typeIncluded.put(acctType,bInclude);
	}
	public Boolean GetType (AccountType acctType) {
		if (typeIncluded.containsKey(acctType))
			return typeIncluded.get(acctType);
		else
			return false;
	}
	public void SetIncludeZero (Boolean bIncludeZerop){
		includeZero = bIncludeZerop;
	}
	public Boolean GetIncludeZero() {
		return includeZero;
	}
	public void display() {
		loadAccounts(acctBook.getRootAccount());
		missingModel = new MRBListModel(mapAccounts,MRBConstants.SPT_MISSING);
		selectModel = new MRBListModel(mapAccounts,MRBConstants.SPT_SELECT);
		listDisplayMissing= new JList<String>(missingModel);
		listDisplaySelect = new JList<String>(selectModel);
		panDisplay = new JPanel(new GridBagLayout());
		int ix=0;
		int iy=0;
		JLabel lblAccmis = new JLabel(locale.getString(MRBConstants.LL_AVAILABLE,"Available Items"));
		panDisplay.add(lblAccmis, GridC.getc(ix,iy));
		ix=+2;
		JLabel lblAccsel = new JLabel(locale.getString(MRBConstants.LL_INCLUDED,"Included Items"));
		panDisplay.add(lblAccsel, GridC.getc(ix,iy));
		/*
		 * Accounts Available
		 */
		ix = 0;
		iy++;
		missingScroll = new JScrollPane(listDisplayMissing);
		missingScroll.setPreferredSize(new Dimension(iSCROLLWIDTH, iSCROLLHEIGHT));
		missingScroll.setMinimumSize(new Dimension(iSCROLLWIDTH, iSCROLLHEIGHT));
		missingScroll.setToolTipText(locale.getString(MRBConstants.LL_TT_SCROLL,"Shows the items not included in the report"));
		panDisplay.add(missingScroll, GridC.getc(ix,iy).rowspan(2).insets(0,10,0,0));
		/*
		 * Account Buttons
		 */
		ix=1;
		selectBtn = new JButton(locale.getString(MRBConstants.LL_BTN_SEL,"Sel"));
		selectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accountSelect();
			}
		});
		selectBtn.setToolTipText(locale.getString(MRBConstants.LL_TT_ADD,"Click to add the selected item(s) to the report"));
		panDisplay.add(selectBtn,  GridC.getc(ix,iy).fillx().south().insets(40, 5, 5, 5));
		deselectBtn = new JButton(locale.getString(MRBConstants.LL_BTN_DES,"Des"));
		deselectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accountDeselect();
			}
		});
		deselectBtn.setToolTipText(locale.getString(MRBConstants.LL_TT_REMOVE,"Click to remove the selected item(s) from the report"));
		panDisplay.add(deselectBtn, GridC.getc(ix,iy+1).fillx().north().insets(0, 5, 5, 5));
		/*
		 * Accounts Selected
		 */
		ix=2;
		selectedScroll = new JScrollPane(listDisplaySelect);
		selectedScroll.setPreferredSize(new Dimension(iSCROLLWIDTH,iSCROLLHEIGHT));
		selectedScroll.setMinimumSize(new Dimension(iSCROLLWIDTH, iSCROLLHEIGHT));
		selectedScroll.setToolTipText(locale.getString(MRBConstants.LL_TT_SHOW,"Shows the items that will be included in the report"));
		panDisplay.add(selectedScroll, GridC.getc(ix,iy).rowspan(2).insets(0, 0, 0, 10));
		JOptionPane.showMessageDialog(null,panDisplay,locale.getString(MRBConstants.LL_SELECT,"Select Accounts/Categories"),JOptionPane.INFORMATION_MESSAGE);


	}
	private void accountSelect() {
		int[] iSelected = listDisplayMissing.getSelectedIndices();
		if (iSelected.length != 0) {
			for (int iRow : iSelected) {
				MRBMyEntry meTemp = missingModel.getEntry(iRow);
				if(meTemp.getValue() != null) {
					meTemp.getValue().setSelected(true);
					selectedMap.put(meTemp.getValue().getAccount()," ");
				}
			}
		}
		listDisplayMissing.clearSelection();
		missingModel.update(mapAccounts);
		selectModel.update(mapAccounts);
		panDisplay.revalidate();
	}

	/*
	 * Account Deselect - move selected account lines to available
	 */
	private void accountDeselect() {
		int[] iSelected = listDisplaySelect.getSelectedIndices();
		if (iSelected.length != 0) {
			for (int iRow : iSelected) {
				MRBMyEntry meTemp = selectModel.getEntry(iRow);
				if (meTemp.getValue() != null) {
					meTemp.getValue().setSelected(false);
					selectedMap.remove(meTemp.getValue().getAccount());
				}
			}
		}
		listDisplaySelect.clearSelection();
		missingModel.update(mapAccounts);
		selectModel.update(mapAccounts);
		panDisplay.revalidate();

	}
	/*
	 * Create 3 tables of current rate, date rate set and account object
	 * all keyed by ticker symbol
	 */
	 private void loadAccounts(Account parentAcct) {
	    int sz = parentAcct.getSubAccountCount();
	    for(int i=0; i<sz; i++) {
	      Account acct = parentAcct.getSubAccount(i);
	    	  if((acct.getCurrentBalance() != 0L)||(includeZero)){
	    		  if(GetType(acct.getAccountType())) {
	    			  MRBListItem liTemp = new MRBListItem (acct);
	    			  if (selectedMap.containsKey(acct))
	    				  liTemp.setSelected(true);
	    			  mapAccounts.put(liTemp.getKey(),liTemp);
	    		      loadAccounts(acct);
	    		  }
	    	  }
	    	  else
	    		  loadAccounts(acct);
	    }
	 }
	 public List<Account> getSelected() {
		 List<Account> listAccount = new ArrayList<Account>();
		 for (MRBListItem meTemp : mapAccounts.values()){
			 if (meTemp.isSelected())
				 listAccount.add(meTemp.getAccount());
		 }
		 return listAccount;
	 }
	 public void setSelected(List<Account> selectedAccts){
		 selectedMap = new HashMap<Account,String>();
		 for (Account acct : selectedAccts) {
			 selectedMap.put(acct, " ");
		 }
		 for (MRBListItem meTemp : mapAccounts.values()){
			 if (selectedMap.containsKey(meTemp.getAccount()))
				 meTemp.setSelected(true);
		 }
		 
	 }

}
