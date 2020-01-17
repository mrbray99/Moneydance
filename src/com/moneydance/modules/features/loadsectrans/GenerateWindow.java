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
package com.moneydance.modules.features.loadsectrans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.SortedSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.InvestFields;
import com.infinitekind.moneydance.model.InvestTxnType;
import com.infinitekind.moneydance.model.OnlineTxn;
import com.infinitekind.moneydance.model.ParentTxn;
import com.infinitekind.moneydance.model.SplitTxn;

public class GenerateWindow extends JPanel {
	private SortedSet<SecLine> setLine;
	private Account acct;
	private GenerateTable transTab;
	private GenerateTableModel transModel;
	private InvestFields investFields;
	private JPanel panTop;
	private JPanel panMid;
	private JPanel panBot;
	private JTextField txtAccount;
	private MyCheckBox jcSelect;
	private JButton btnClose;
	private JButton btnSave;

	private JScrollPane spTrans;
	private Parameters objParms;
	public GenerateWindow(SortedSet<SecLine> setLinep, Account acctp, Parameters objParmsp) {
		setLine = setLinep;
		acct = acctp;
		objParms = objParmsp;
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
		for (SecLine objLine :setLine) {
			if (!objLine.getSelect())
				continue;
			FieldLine objMatch = objParms.matchType(objLine.getReference());
			if (objMatch != null) {
				investFields = new InvestFields();
				investFields.hasFee = false;
				ParentTxn ptTran = new ParentTxn(Main.acctBook);
				ptTran.setAccount(acct);
				switch (Constants.TRANSTYPES[objMatch.getTranType()]) {
				case Constants.SECURITY_INCOME :
					investFields.amount=objLine.getValue();
					investFields.hasAmount = true;
					investFields.category = objMatch.getAccount();
					investFields.hasCategory=true;
					investFields.date = objLine.getDate();
					investFields.taxDate=objLine.getDate();
					investFields.txnType = InvestTxnType.MISCINC;
					investFields.security = Main.mapAccounts.get(objLine.getTicker());
					investFields.hasSecurity = true;
					investFields.storeFields(ptTran);
					transModel.addLine(new GenerateTransaction(Constants.PARENT,acct,objLine.getDate(),
							objLine.getValue(),"","",
							AbstractTxn.TRANSFER_TYPE_MISCINCEXP,objLine.getReference(),ptTran.getParentTxn()));
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,(Main.mapAccounts.get(objLine.getTicker())),
							objLine.getDate(),0,Main.mapAccounts.get(objLine.getTicker()).getAccountName(),"",
							AbstractTxn.TRANSFER_TYPE_MISCINCEXP,objLine.getReference(),ptTran.getSplit(0)));
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,objMatch.getAccount(),objLine.getDate(),
							objLine.getValue()*-1,
							OnlineTxn.INVEST_TXN_MISCINC+" "+Main.mapAccounts.get(objLine.getTicker()).getAccountName(),"",
							AbstractTxn.TRANSFER_TYPE_MISCINCEXP, objLine.getReference(),ptTran.getSplit(1)));				
					break;
				case Constants.SECURITY_DIVIDEND :
					investFields.amount=objLine.getValue();
					investFields.hasAmount = true;
					investFields.category = objMatch.getAccount();
					investFields.hasCategory=true;
					investFields.date = objLine.getDate();
					investFields.taxDate=objLine.getDate();
					investFields.txnType = InvestTxnType.DIVIDEND;
					investFields.security = Main.mapAccounts.get(objLine.getTicker());
					investFields.hasSecurity = true;
					investFields.storeFields(ptTran);
					transModel.addLine(new GenerateTransaction(Constants.PARENT,
							acct, //account
							objLine.getDate(), //date
							objLine.getValue(), // value
							"", // descr
							"",  // cheque
							AbstractTxn.TRANSFER_TYPE_DIVIDEND, // type
							objLine.getReference(),ptTran.getParentTxn())); // reference
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,(Main.mapAccounts.get(objLine.getTicker())),
							objLine.getDate(),0,Main.mapAccounts.get(objLine.getTicker()).getAccountName(),"",AbstractTxn.TRANSFER_TYPE_DIVIDEND,objLine.getReference(),ptTran.getSplit(0)));
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,objMatch.getAccount(),objLine.getDate(),
							objLine.getValue()*-1,
							OnlineTxn.INVEST_TXN_DIVIDEND+" "+Main.mapAccounts.get(objLine.getTicker()).getAccountName(),"",
							AbstractTxn.TRANSFER_TYPE_DIVIDEND,objLine.getReference(),ptTran.getSplit(1)));
					
					break;
				case Constants.SECURITY_COST :
					investFields.amount=objLine.getValue();
					investFields.hasAmount = true;
					investFields.category = objLine.getAccount();
					investFields.hasCategory=true;
					investFields.date = objLine.getDate();
					investFields.taxDate=objLine.getDate();
					investFields.txnType = InvestTxnType.MISCEXP;
					investFields.security = Main.mapAccounts.get(objLine.getTicker());
					investFields.hasSecurity = true;
					investFields.storeFields(ptTran);
					transModel.addLine(new GenerateTransaction(Constants.PARENT,acct,objLine.getDate(),
							objLine.getValue()*-1,"","",
							AbstractTxn.TRANSFER_TYPE_MISCINCEXP,objLine.getReference(),ptTran.getParentTxn()));
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,(Main.mapAccounts.get(objLine.getTicker())),
							objLine.getDate(),0,Main.mapAccounts.get(objLine.getTicker()).getAccountName(),"",AbstractTxn.TRANSFER_TYPE_MISCINCEXP,
							objLine.getReference(),ptTran.getSplit(0)));
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,objMatch.getAccount(),objLine.getDate(),
							objLine.getValue(),
							OnlineTxn.INVEST_TXN_MISCINC+" "+Main.mapAccounts.get(objLine.getTicker()).getAccountName(),"",
							AbstractTxn.TRANSFER_TYPE_MISCINCEXP, objLine.getReference(),ptTran.getSplit(1)));				
					break;
				case Constants.INVESTMENT_INCOME:
					investFields.amount=objLine.getValue();
					investFields.hasAmount = true;
					investFields.xfrAcct = objMatch.getAccount();
					investFields.hasXfrAcct=true;
					investFields.date = objLine.getDate();
					investFields.taxDate=objLine.getDate();
					investFields.txnType = InvestTxnType.BANK;
					investFields.storeFields(ptTran);
					transModel.addLine(new GenerateTransaction(Constants.PARENT,acct,objLine.getDate(),
							objLine.getValue(),objLine.getDescription(),"",AbstractTxn.TRANSFER_TYPE_BANK,objLine.getReference(),ptTran.getParentTxn()));
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,objMatch.getAccount(),objLine.getDate(),
							objLine.getValue()*-1,acct.getAccountName(),"",
							AbstractTxn.TRANSFER_TYPE_BANK,objLine.getReference(),ptTran.getSplit(0)));
					break;
				case Constants.INVESTMENT_COST :
					investFields.amount=objLine.getValue();
					investFields.hasAmount = true;
					investFields.xfrAcct = objMatch.getAccount();
					investFields.hasXfrAcct=true;
					investFields.date = objLine.getDate();
					investFields.taxDate=objLine.getDate();
					investFields.txnType = InvestTxnType.BANK;
					investFields.storeFields(ptTran);
					transModel.addLine(new GenerateTransaction(Constants.PARENT,acct,objLine.getDate(),
							objLine.getValue(),objLine.getDescription(),"",
							AbstractTxn.TRANSFER_TYPE_BANK,objLine.getReference(),ptTran.getParentTxn()));
					transModel.addLine(new GenerateTransaction(Constants.SPLIT,objMatch.getAccount(),objLine.getDate(),
							objLine.getValue()*-1,acct.getAccountName(),"",
							AbstractTxn.TRANSFER_TYPE_BANK,objLine.getReference(),ptTran.getSplit(0)));
					break;
				default :
					/*
					 * not interested in buys/sells/transfers/card payments
					 */
				}
			}
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
				 	 ParentTxn ptTran = (ParentTxn)transLine.getTxn();
				 	 SplitTxn stTran1 = (SplitTxn) ptTran.getSplit(0);
					 ptTran.setParameter(Constants.TAGGEN, transLine.getRef());
					 stTran1.setParameter(Constants.TAGGEN, transLine.getRef());
					 if (ptTran.getSplitCount() > 1) {
						 SplitTxn stTran2 = (SplitTxn) ptTran.getSplit(1);
						 stTran2.setParameter(Constants.TAGGEN, transLine.getRef());
					 }
					 ptTran.syncItem();
					 i++;
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
		 Main.tranSet = Main.acctBook.getTransactionSet();
		 Main.generatedTranSet = new MyTransactionSet(Main.root, acct,objParms,setLine);
		 for (SecLine objLine :setLine) {
			if (!objLine.getSelect())
				continue;
			objLine.setProcessed(false);
			Main.generatedTranSet.findTransaction(objLine);
		 }
		 int iRowCount = transModel.getRowCount();
		 for (int j = iRowCount -1; j>= 0; j--) {
			 int iIndex = transModel.getLine(j).getIndex();
			 if (iIndex >= 0)
				 transModel.deleteLine(iIndex);
		 }
		 transModel.fireTableDataChanged();

	 }

}
