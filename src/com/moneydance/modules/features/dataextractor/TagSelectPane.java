package com.moneydance.modules.features.dataextractor;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.TxnUtil;
import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.MRBLocale;

public class TagSelectPane{
	private JScrollPane missingScroll;
	private JScrollPane selectedScroll;
	private JList<String> listDisplayMissing;
	private JList<String> listDisplaySelect;
	private List<String> tagsSelected;
	private List<String> tagsMissing;
	private DefaultListModel<String> missingModel;
	private DefaultListModel<String> selectModel;
	private MRBLocale locale;
	private int iPANELHEIGHT=400;
	private int iPANELWIDTH=600;
	private int iSCROLLWIDTH=(iPANELWIDTH- 50)/2;
	private int iSCROLLHEIGHT=iPANELHEIGHT- 150;
	private AccountBook acctBook;
	private JButton selectBtn;
	private JButton deselectBtn;
	private JPanel panDisplay;
	public TagSelectPane (AccountBook acctBookp, List<String>tagsSelectedp) {
		acctBook = acctBookp;
		locale = Main.locale;
		tagsSelected = tagsSelectedp;
		tagsMissing = new ArrayList<String>();
	}
	public void display() {
		loadTags(acctBook);
		missingModel = new DefaultListModel<String>();
		selectModel = new DefaultListModel<String>();
		listDisplayMissing= new JList<String>(missingModel);
		listDisplaySelect = new JList<String>(selectModel);
		for(String tag : tagsMissing)
			missingModel.addElement(tag);
		for(String tag : tagsSelected)
			selectModel.addElement(tag);
		
		panDisplay = new JPanel(new GridBagLayout());
		int ix=0;
		int iy=0;
		JLabel lblAccmis = new JLabel(locale.getString(Constants.TP_AVAILABLE,"Available Items"));
		panDisplay.add(lblAccmis, GridC.getc(ix,iy));
		ix=+2;
		JLabel lblAccsel = new JLabel(locale.getString(Constants.TP_INCLUDED,"Included Items"));
		panDisplay.add(lblAccsel, GridC.getc(ix,iy));
		/*
		 * Tags Available
		 */
		ix = 0;
		iy++;
		missingScroll = new JScrollPane(listDisplayMissing);
		missingScroll.setPreferredSize(new Dimension(iSCROLLWIDTH, iSCROLLHEIGHT));
		missingScroll.setMinimumSize(new Dimension(iSCROLLWIDTH, iSCROLLHEIGHT));
		missingScroll.setToolTipText(locale.getString(Constants.TP_TT_SCROLL,"Shows the items not included in the report"));
		panDisplay.add(missingScroll, GridC.getc(ix,iy).rowspan(2).insets(0,10,0,0));
		/*
		 * Tag Buttons
		 */
		ix=1;
		selectBtn = new JButton(locale.getString(Constants.TP_BTN_SEL,"Sel"));
		selectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tagSelect();
			}
		});
		selectBtn.setToolTipText(locale.getString(Constants.TP_TT_ADD,"Click to add the selected item(s) to the report"));
		panDisplay.add(selectBtn,  GridC.getc(ix,iy).fillx().south().insets(40, 5, 5, 5));
		deselectBtn = new JButton(locale.getString(Constants.TP_BTN_DES,"Des"));
		deselectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tagDeselect();
			}
		});
		deselectBtn.setToolTipText(locale.getString(Constants.TP_TT_REMOVE,"Click to remove the selected item(s) from the report"));
		panDisplay.add(deselectBtn, GridC.getc(ix,iy+1).fillx().north().insets(0, 5, 5, 5));
		/*
		 * Tags Selected
		 */
		ix=2;
		selectedScroll = new JScrollPane(listDisplaySelect);
		selectedScroll.setPreferredSize(new Dimension(iSCROLLWIDTH,iSCROLLHEIGHT));
		selectedScroll.setMinimumSize(new Dimension(iSCROLLWIDTH, iSCROLLHEIGHT));
		selectedScroll.setToolTipText(locale.getString(Constants.TP_TT_SHOW,"Shows the items that will be included in the report"));
		panDisplay.add(selectedScroll, GridC.getc(ix,iy).rowspan(2).insets(0, 0, 0, 10));
		JOptionPane.showMessageDialog(null,panDisplay,locale.getString(Constants.TP_SELECT,"Select Tags"),JOptionPane.INFORMATION_MESSAGE);


	}
	private void tagSelect() {
		List<String> selectedTags = listDisplayMissing.getSelectedValuesList();
		if (selectedTags.size() != 0) {
			for (String tag : selectedTags) {
				missingModel.removeElement(tag);
				selectModel.addElement(tag);
				tagsSelected.add(tag);
				tagsMissing.remove(tag);
			}
		}
		listDisplayMissing.clearSelection();
		panDisplay.revalidate();
	}

	/*
	 * Account Deselect - move selected account lines to available
	 */
	private void tagDeselect() {
		List<String> selectedTags = listDisplaySelect.getSelectedValuesList();
		if (selectedTags.size() != 0) {
			for (String tag : selectedTags) {
				selectModel.removeElement(tag);
				missingModel.addElement(tag);
				tagsSelected.remove(tag);
				tagsMissing.add(tag);
		}
		}
		listDisplaySelect.clearSelection();
		panDisplay.revalidate();
	}
	
	public List<String> getSelected() {
		return tagsSelected;
	}
	/*
	 * Find all tags, split between selected and available
	 * 
	 */
	 private void loadTags(AccountBook parentAcct) {
	    List<String> listTags = TxnUtil.getListOfAllUsedTransactionTags(parentAcct.getTransactionSet().getAllTxns());
	    for(String tag : listTags) {
	    	if (!tagsSelected.contains(tag))
	    		tagsMissing.add(tag);
	    }
	    List<String> tagsRemove = new ArrayList<String>();
	    for(String tag : tagsSelected) {
	    	if (!listTags.contains(tag))
	    		tagsRemove.add(tag);
	    }
	    for(String tag : tagsRemove) {
    		tagsSelected.remove(tag);
	    }
	 }

}
