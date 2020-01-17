/*
 *  Copyright (c) 2014, Michael Bray. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of the author may not used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package com.moneydance.modules.features.qifloader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.OnlineTxn;
import com.infinitekind.moneydance.model.ParentTxn;
import com.infinitekind.moneydance.model.SplitTxn;
import com.moneydance.apps.md.controller.Util;


public class GenerateWindow extends JPanel {
	private List<QIFEntry> setLine;
	private Account acct;
	private GenerateTable transTab;
	private GenerateTableModel transModel;
	private JPanel panTop;
	private JPanel panMid;
	private JPanel panBot;
	private JTextField txtAccount;
	private MyCheckBox jcSelect;
	private JButton btnClose;
	private JButton btnSave;

	private JScrollPane spTrans;
	private Parameters objParms;
	private SortedMap<String,Account> categories;
	public GenerateWindow(List<QIFEntry> setLinep, Account acctp, Parameters objParmsp,List<Account>categoriesp) {
		setLine = setLinep;
		acct = acctp;
		objParms = objParmsp;
		categories = new TreeMap<String,Account>();
		for (Account category : categoriesp) {
			String name=category.getAccountName().toUpperCase();
			categories.put(name,category);
		}
		transModel = new GenerateTableModel();
		transTab = new GenerateTable(transModel);
		generateTrans();
		/*
		 * Start of screen
		 * 
		 * Top Panel Account
		 */
		this.setLayout(new BorderLayout());
		panTop = new JPanel (new GridBagLayout());
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		JLabel lbAccount = new JLabel("Investment Account:");
		panTop.add(lbAccount,gbc_label);
		GridBagConstraints gbc_account = new GridBagConstraints();
		gbc_account.gridx = 1;
		gbc_account.gridy = 0;
		txtAccount = new JTextField(acct.getAccountName());
		panTop.add(txtAccount,gbc_account);
		this.add(panTop,BorderLayout.PAGE_START);
		/*
		 * Middle Panel table
		 */
		panMid = new JPanel ();
		panMid.setLayout(new BoxLayout(panMid,BoxLayout.Y_AXIS));
		spTrans = new JScrollPane (transTab);
		spTrans.setAlignmentX(LEFT_ALIGNMENT);
		panMid.add(spTrans,BorderLayout.LINE_START);
		spTrans.setPreferredSize(new Dimension(Constants.LOADSCREENWIDTH,Constants.LOADSCREENHEIGHT));
		jcSelect = new MyCheckBox();
		jcSelect.setAlignmentX(LEFT_ALIGNMENT);
		jcSelect.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean bNewValue;
				if (e.getStateChange() == ItemEvent.DESELECTED)
					bNewValue = false;
				else
					bNewValue = true;
				for (int i=0;i<transModel.getRowCount();i++)
					transModel.setValueAt(bNewValue, i, 0);
				transModel.fireTableDataChanged();
			}
		});
		panMid.add(jcSelect);
		this.add(panMid,BorderLayout.CENTER);
		
		
		/*
		 * Add Buttons
		 */
		panBot = new JPanel(new GridBagLayout());
		/*
		 * Button 1
		 */
		GridBagConstraints gbcbt1 = new GridBagConstraints();
		gbcbt1.gridx = 0;
		gbcbt1.gridy = 0;
		gbcbt1.anchor = GridBagConstraints.LINE_START;
		gbcbt1.insets = new Insets(15,15,15,15);
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		panBot.add(btnClose,gbcbt1);

		/*
		 * Button 2
		 */
		GridBagConstraints gbcbt2 = new GridBagConstraints();
		gbcbt2.gridx = gbcbt1.gridx+1;
		gbcbt2.gridy = gbcbt1.gridy;
		gbcbt2.insets = new Insets(15,15,15,15);
		btnSave = new JButton("SaveTransactions");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				panMid.invalidate();
				panMid.validate();
			}
		});
		panBot.add(btnSave,gbcbt2);
		
		this.add(panBot,BorderLayout.PAGE_END);
			
	}
	private void generateTrans() {
		for (QIFEntry entry :setLine) {
			if (!entry.isSelected())
				continue;
			Long amount = Math.round(entry.getAmount()*100);
			transModel.addLine(new GenerateTransaction(Constants.PARENT,acct,entry.getDate(),
				amount,entry.getDescription(),entry.getCheque(),entry.getCleared(),""));
			Account category=null;
			if (entry.getCategory().isEmpty())
				category = acct.getDefaultCategory();
			else {
				String cat = entry.getCategory().toUpperCase();
				if (categories.containsKey(cat))
					category = categories.get(cat);
				else
					category =acct.getDefaultCategory(); 
			}
			transModel.addLine(new GenerateTransaction(Constants.SPLIT,category,
							entry.getDate(),-amount,entry.getDescription(),entry.getCheque(),entry.getCleared(),""));
		}
		
		
	}
	 
	 public void close() {
		this.setVisible(false);
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.dispose();

	 }
	 
	 public void save() {
		 int i = 0;
		 while (i<transModel.getRowCount()) {
			 if (transModel.getLine(i).getType() == Constants.PARENT) {
				 if ((boolean)transModel.getValueAt(i, 0)) {
					 GenerateTransaction transLine = transModel.getLine(i);					 
					 GenerateTransaction transLine2 = transModel.getLine(i+1);
					 ParentTxn ptTran = ParentTxn.makeParentTxn(Main.acctBook, transLine.getDate(), transLine.getDate(), transLine.getDate(), transLine.getCheque(), acct, transLine.getDesc(),"" ,(long)-1,transLine.getStatus());
						 
					 /*
					  * Amount needs to be negative as SplitTxn will negate parent amount						  * 
					  */
					 SplitTxn stTran1 = SplitTxn.makeSplitTxn(ptTran,-transLine.getAmount(),-transLine.getAmount(),1.0,transLine2.getAccount(),transLine.getDesc(),(long) -1,transLine.getStatus());
					 ptTran.addSplit(stTran1);
					 ptTran.setIsNew(true);
					 transLine.setIndex(i);
					 transLine2.setIndex(i+1);
					 i+=2;
					 ptTran.syncItem();
				 }
				 else
					 i++;
			 }
			 else
				 i++;
		 }
		 /*
		  * finished creating transactions:
		  *   1. check processed transactions again
		  *   2. Delete processed rows from window
		  *   3. redisplay window 
		  */
		 int iRowCount = transModel.getRowCount();
		 for (int j = iRowCount -1; j>= 0; j--) {
			 int iIndex = transModel.getLine(j).getIndex();
			 if (iIndex >= 0)
				 transModel.deleteLine(iIndex);
		 }
		 transModel.fireTableDataChanged();
	 }

}
