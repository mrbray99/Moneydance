/*
 *  Copyright (c) 2014, 2016, Michael Bray. All rights reserved.
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
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.util.DateUtil;
import com.moneydance.awt.GridC;
import com.moneydance.awt.JDateField;

public class loadPricesWindow extends JPanel implements TableListener {
    private SortedSet<QIFEntry> setLine;
    private Account acct;
    private MyTableModel pricesModel;
    private MyTable pricesTab;
    private GenerateWindow generateWindow;
    private Parameters params;
    private JScrollPane spPrices;
    private JPanel panBot;
    private JPanel panTop;
    private JPanel panMid;
    private JButton btnClose;
    private JButton btnGenerate;
    private JButton switchValue;
    private MyCheckBox jcSelect;
    private List<QIFEntry> transactions = new ArrayList();
    private List<Account>categories;

    JTextField txtAccount;
	public loadPricesWindow(JTextField txtFileName,Account acctp, Parameters objParmsp,List<Account>categoriesp) {
		acct = acctp;
		params = objParmsp;
		categories = categoriesp;
		
		loadFile (txtFileName);
		pricesModel = new MyTableModel (transactions, Main.mapAccounts);
		pricesTab = new MyTable (pricesModel);
		/*
		 * Start of screen
		 * 
		 * Top Panel Account
		 */
		this.setLayout(new BorderLayout());
		panTop = new JPanel (new GridBagLayout());
		int x=0;
		int y=0;
		JLabel lbAccount = new JLabel("Account: ");
		panTop.add(lbAccount,GridC.getc(x,y));
		x++;
		txtAccount = new JTextField(acct.getAccountName());
		panTop.add(txtAccount,GridC.getc(x,y));
		this.add(panTop,BorderLayout.PAGE_START);
		/*
		 * Middle Panel table
		 */
		panMid = new JPanel ();
		panMid.setLayout(new BoxLayout(panMid,BoxLayout.Y_AXIS));
		spPrices = new JScrollPane (pricesTab);
		spPrices.setAlignmentX(LEFT_ALIGNMENT);
		panMid.add(spPrices,BorderLayout.LINE_START);
		spPrices.setPreferredSize(new Dimension(Constants.LOADSCREENWIDTH,Constants.LOADSCREENHEIGHT));
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
				for (int i=0;i<pricesModel.getRowCount();i++)
					pricesModel.setValueAt(bNewValue, i, 0);
				pricesModel.fireTableDataChanged();
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
		x=0;
		y=0;
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		panBot.add(btnClose,GridC.getc(x,y).west().insets(15,15,15,15));
		/*
		 * Button 2
		 */
		x++;
		switchValue = new JButton("Switch Values");
		switchValue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (QIFEntry entry : transactions) {
					entry.setAmount(entry.getAmount()*-1.0);
				}
				pricesModel.fireTableDataChanged();
			}
		});
		panBot.add(switchValue,GridC.getc(x,y).west().insets(15,15,15,15));
		/*
		 * Button 3
		 */
		x++;
		btnGenerate = new JButton("Generate Transactions");
		btnGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generate();
			}
		});
		panBot.add(btnGenerate,GridC.getc(x,y).insets(15,15,15,15));
		
		this.add(panBot,BorderLayout.PAGE_END);
		

	}

	 /*
	  * try to load selected file
	  */
	 private void loadFile(JTextField txtFileName) {
		 	@SuppressWarnings("unused")
			String fileType;
			try {
				FileReader frPrices = new FileReader(txtFileName.getText());
				BufferedReader brPrices = new BufferedReader(frPrices);
				/*
				 * Get the file type
				 */
				String strLine = brPrices.readLine();
				if (strLine.charAt(0) == '!')
					fileType = strLine.substring(1);
				else {
					JFrame fTemp = new JFrame();
					JOptionPane.showMessageDialog(fTemp,"File Type missing");
					return;
				}
			QIFEntry entry=new QIFEntry();
			while ((strLine = brPrices.readLine())!= null) {
					switch (strLine.charAt(0)) {
					case 'D':
						String datestr = strLine.substring(1);
						JDateField dateFld = new JDateField (Main.cdate); 
						int date = DateUtil.convertDateToInt(dateFld.getDateFromString(datestr));
						entry.setDate(date);
						break;
					case 'P':
						entry.setDescription(strLine.substring(1));
						break;
					case 'T':
						Double value;
						try {
							value = Double.valueOf(strLine.substring(1));
							entry.setAmount(value);
						}
						catch (Exception e) {
							value = 0.0;
						}
						entry.setAmount(value);
						break;
					case 'N' :
						entry.setCheque(strLine.substring(1));
						break;
					case 'C' :
						switch(strLine.charAt(1)) {
						case ' ' :
							entry.setCleared(AbstractTxn.STATUS_UNRECONCILED);
							break;
						case '*' :
						case 'c' :
							entry.setCleared(AbstractTxn.STATUS_CLEARED);
							break;
						case 'X' :
						case 'R' :
							entry.setCleared(AbstractTxn.STATUS_RECONCILING);
							break;
						default :
							entry.setCleared(AbstractTxn.STATUS_UNRECONCILED);
							break;						
						}
						break;		
					case 'L':
						entry.setCategory(strLine.substring(1).trim());
						break;
					case '^' :
						transactions.add(entry);
						entry = new QIFEntry();
					}
				}
				brPrices.close();
			}
			catch (FileNotFoundException e) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"File "+txtFileName+" not Found");
				close();
			}
			catch (IOException e) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"I//O Error whilst reading "+txtFileName);
				close();
				
			}
			
	 }
	 
	 public void close() {
		this.setVisible(false);
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.dispose();

	 }
	 private void generate() {
	      //Create and set up the window.
	      JFrame frame = new JFrame("QIF Loader - Build "+Main.buildStr);
	      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	      generateWindow = new GenerateWindow(transactions,acct,params,categories);
	      frame.getContentPane().add(generateWindow);

	      //Display the window.
	      frame.pack();
	      frame.setLocationRelativeTo(null);
	      frame.setVisible(true);
	 }

	@Override
	public void tableChanged () {
		pricesModel.fireTableDataChanged();
		panMid.revalidate();
	}

}
