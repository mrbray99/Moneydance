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


import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.MRBPreferences2;



public class FileSelectWindow extends JPanel{
	  private static final long serialVersionUID = 1L;
	  private JTextField fileName;
	  private JButton loadBtn;
	  private JButton chooseBtn;
	  private JButton addBtn;
	  private JComboBox<String> accountsCB;
	  private JComboBox<String> tickerCB;
	  private JComboBox<String> dateCB;
	  private JComboBox<String> valueCB;
	  private JComboBox<String> referenceCB;
	  private JComboBox<String> descCB;
	  private JCheckBox exchCB;
	  private JScrollPane fieldsScroll;
	  private JPanel fieldsPan;
	  private Parameters params;
	  private JFileChooser fileChooser = null;
	  private File securities;
	  private loadPricesWindow loadWindow;
	  private Map<String,Account> mapNames = new HashMap<String,Account>(); 
	  private String[] arrNames;
	  private LookAndFeel previousLF;
	  private MRBPreferences2 preferences;

	  private String [] arColumns;
	  private List<FieldLine> listLines;
	  private Map<Object,String> mapCombos;
	  private List<Account> listCategories;
	  private String [] arrCategories;
	  private Map<String,Account> mapAccts;
	  private String strName = "";
	public FileSelectWindow() throws HeadlessException {
		MRBPreferences2.loadPreferences(Main.context);
		preferences = MRBPreferences2.getInstance();
	    listCategories = new ArrayList<Account>();
	    mapAccts = new HashMap<String,Account>();
	    mapNames.put("Please Select an Account", null);
		loadAccounts(Main.context.getRootAccount(),strName);
		arrCategories = new String[listCategories.size()];
		int i=0;
		for (Account acct :listCategories) {
			arrCategories[i++] = acct.getAccountName();
		}
		fileChooser = new JFileChooser();
		arrNames = mapNames.keySet().toArray(new String[0]);
		GridBagLayout gbl_panel = new GridBagLayout();
		this.setLayout(gbl_panel);
		int x = 0;
		int y = 0;
		JLabel lblFileName = new JLabel("File : ");
		this.add(lblFileName, GridC.getc(x,y).insets(20, 20, 0, 0));
		
		fileName = new JTextField();
		fileName.setText("");
		fileName.setColumns(50);
		x++;
		this.add(fileName, GridC.getc(x,y).colspan(3).insets(20,5, 0, 0));
	
		chooseBtn = new JButton();
	    Image img = getIcon("Search-Folder-icon.jpg");
	    if (img == null)
	    	chooseBtn.setText("Find");
	    else
	    	chooseBtn.setIcon(new ImageIcon(img));
		x=+4;
		this.add(chooseBtn, GridC.getc(x,y).insets(10, 5, 0, 0));
		chooseBtn.setBorder(javax.swing.BorderFactory.createLineBorder(this.getBackground()));     
		chooseBtn.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});
				
			
		JLabel lblAccounts = new JLabel("Which Investment Account : ");
		x=1;
		y++;
		this.add(lblAccounts, GridC.getc(x,y).insets(10, 0, 0, 0));

		accountsCB = new JComboBox<String>(arrNames);
		x++;
		this.add(accountsCB, GridC.getc(x,y).insets(5, 0, 0, 0));
		
		JLabel lblLReference = new JLabel("Select Transaction Type Field");
		x=1;
		y++;
		this.add(lblLReference, GridC.getc(x,y).insets(5, 5, 5, 5));	
		referenceCB = new JComboBox<String>();
		x++;
		referenceCB.addItem("Please Select a Field");
		referenceCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<Integer> cbReferenceT = (JComboBox<Integer>) e.getSource();
				params.setReference((String)cbReferenceT.getSelectedItem());
			}
		});
		this.add(referenceCB, GridC.getc(x,y).insets(5, 5, 5, 5).west());
		JLabel lblLTicker = new JLabel("Select Ticker Field");
		x=1;
		y++;
		this.add(lblLTicker, GridC.getc(x,y).insets(5, 5, 5, 5));	
		tickerCB = new JComboBox<String>();
		x++;
		tickerCB.addItem("Please Select a Field");
		tickerCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<Integer> cbTick = (JComboBox<Integer>) e.getSource();
				params.setTicker((String)cbTick.getSelectedItem());
			}
		});
		this.add(tickerCB, GridC.getc(x,y).insets(5, 5, 5, 5).west());	
		JLabel lblLExch = new JLabel("Remove Exchange from Ticker?");
		x++;
		this.add(lblLExch, GridC.getc(x,y).insets(5, 5, 5, 5).east() );	
		exchCB = new JCheckBox();
		x++;
		exchCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox chbExchT = (JCheckBox) e.getSource();
				params.setExch(chbExchT.isSelected());
			}
		});
		this.add(exchCB, GridC.getc(x,y).insets(5, 5, 5, 5).west());	
		JLabel lblLDate = new JLabel("Select Date Field");
		x=1;
		y++;
		this.add(lblLDate, GridC.getc(x,y).insets(5, 5, 5, 5));
		x++;
		dateCB = new JComboBox<String>();
		dateCB.addItem("Please Select a Field");
		dateCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<Integer> cbDateT = (JComboBox<Integer>) e.getSource();
				params.setDate((String)cbDateT.getSelectedItem());
			}
		});
		this.add(dateCB, GridC.getc(x,y).insets(5, 5, 5, 5).west());	
		JLabel lblLValue = new JLabel("Select Value Field");
		x=1;
		y++;
		this.add(lblLValue, GridC.getc(x,y).insets(5, 5, 5, 5));	
		x++;
		valueCB = new JComboBox<String>();
		valueCB.addItem("Please Select a Field");
		valueCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<Integer> cbValueT = (JComboBox<Integer>) e.getSource();
				params.setValue((String)cbValueT.getSelectedItem());
			}
		});
		this.add(valueCB, GridC.getc(x,y).insets(5, 5, 5, 5).west());	
		x=1;
		y++;
		JLabel lblLDesc = new JLabel("Select Description Field");		
		this.add(lblLDesc, GridC.getc(x,y).insets(5, 5, 5, 5));
		x++;
		descCB = new JComboBox<String>();
		descCB.addItem("Please Select a Field");
		descCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<Integer> cbDescT = (JComboBox<Integer>) e.getSource();
				params.setDesc((String)cbDescT.getSelectedItem());
			}
		});
		this.add(descCB, GridC.getc(x,y).insets(5, 5, 5, 5).west());
		x=1;
		y++;
		JLabel lblTransType = new JLabel("Transaction Types");
		this.add(lblTransType, GridC.getc(x,y).insets(5, 5, 5, 5));	
		JLabel lblTrantxt = new JLabel("Add one entry for each type of transaction to generate");
		x++;
		this.add(lblTrantxt,GridC.getc(x,y).insets(5, 5, 5, 5));	
		fieldsPan = new JPanel(new GridBagLayout());
		fieldsPan.setPreferredSize(new Dimension(800,100));
		fieldsPan.setAutoscrolls(true);
		fieldsScroll = new JScrollPane(fieldsPan);
		fieldsScroll.setPreferredSize(new Dimension(800,100));
		x=1;
		y++;
		this.add(fieldsScroll,GridC.getc(x,y).colspan(5).insets(5, 5, 5, 5));

		loadBtn = new JButton("Load");
		y++;
		this.add(loadBtn, GridC.getc(x,y).insets(5, 5, 5, 5));
		loadBtn.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadSecurities();
			}
		});
		x++;
		JButton btnSave = new JButton("Save Parameters");
		this.add(btnSave, GridC.getc(x,y).insets(5, 5, 5, 5));
		btnSave.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				params.save();
			}
		});
		

		
		x++;
		JButton btnClose = new JButton("Close");
		this.add(btnClose, GridC.getc(x,y).insets(5, 5, 5, 5));
		btnClose.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
	}
	private void chooseFile() {
		String lastDir = preferences.getString(Constants.PROGRAMNAME+Constants.PREFLASTDIRECTORY, "");
		fileChooser.setFileFilter(new FileNameExtensionFilter("csv","CSV"));
		if (!lastDir.isEmpty()) {
			File lastDirFile = new File(lastDir);
			fileChooser.setCurrentDirectory(lastDirFile);
		}
		int iReturn = fileChooser.showDialog(this, "Select File");
		if (iReturn == JFileChooser.APPROVE_OPTION) {
			securities = fileChooser.getSelectedFile();
			fileName.setText(securities.getAbsolutePath());
		}
		lastDir = fileChooser.getCurrentDirectory().getAbsolutePath();
		preferences.put(Constants.PROGRAMNAME+Constants.PREFLASTDIRECTORY,lastDir);
		preferences.isDirty();
		params = new Parameters();
		for (FieldLine objLine:params.getLines()){
			objLine.setAccountObject();
		}
		loadFields();
	}
	private void loadFields() {
		FileReader frPrices;
		BufferedReader brPrices;
		if (fileName.getText().equals("")) {
			JFrame fTemp = new JFrame();
			JOptionPane.showMessageDialog(fTemp,"Please Select a file first");
			return;
		}
		try {
			frPrices = new FileReader(fileName.getText());
			brPrices = new BufferedReader(frPrices);
			/*
			 * Get the headers
			 */
			String strLine = brPrices.readLine(); 
			arColumns = strLine.split(",");
			brPrices.close();
		}
		catch (FileNotFoundException e) {
			JFrame fTemp = new JFrame();
			JOptionPane.showMessageDialog(fTemp,"File "+fileName+" not Found");
			return;
		}
		catch (IOException e) {
			JFrame fTemp = new JFrame();
			JOptionPane.showMessageDialog(fTemp,"I//O Error whilst reading "+fileName);
			return;
		}
		int iTickerItem = -1;
		int iDateItem = -1;
		int iReferenceItem = -1;
		int iDescItem = -1;
		int iValueItem = -1;
		for (int i=0;i<arColumns.length;i++) {
			if (arColumns[i].equals(params.getTicker()))
				iTickerItem = i;
			tickerCB.addItem(arColumns[i]);
			if (arColumns[i].equals(params.getValue()))
				iValueItem = i;
			valueCB.addItem(arColumns[i]);
			if (arColumns[i].equals(params.getDate()))
				iDateItem = i;
			dateCB.addItem(arColumns[i]);
			if (arColumns[i].equals(params.getReference()))
				iReferenceItem = i;
			referenceCB.addItem(arColumns[i]);
			if (arColumns[i].equals(params.getDesc()))
				iDescItem = i;
			descCB.addItem(arColumns[i]);
		}
		tickerCB.setSelectedIndex(iTickerItem == -1?0:iTickerItem+1);
		valueCB.setSelectedIndex(iValueItem == -1?0:iValueItem+1);
		dateCB.setSelectedIndex(iDateItem == -1?0:iDateItem+1);
		referenceCB.setSelectedIndex(iReferenceItem == -1?0:iReferenceItem+1);
		descCB.setSelectedIndex(iDescItem == -1?0:iDescItem+1);
		exchCB.setSelected(params.getExch());
		listLines = params.getLines();
		mapCombos = new HashMap<Object,String>();
		buildLines();
		exchCB.revalidate();
		tickerCB.revalidate();
		valueCB.revalidate();
		dateCB.revalidate();
		referenceCB.revalidate();
		descCB.revalidate();
		fieldsPan.revalidate();
		fieldsScroll.revalidate();
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.pack();
	}
	  private void buildLines() {
			int iRow = 1;
			int x=0;
			mapCombos.clear();
			JLabel lblRTypeH = new JLabel("Transaction Type");
			fieldsPan.add(lblRTypeH, GridC.getc(x,iRow).colspan(2).insets(5, 5, 5, 5));
			x=+2;
			JLabel lblTTypeH = new JLabel("Investment Type");
			fieldsPan.add(lblTTypeH, GridC.getc(x,iRow).insets(5, 5, 5, 5));
			x++;
			JLabel lblAcctH = new JLabel("Category");
			fieldsPan.add(lblAcctH, GridC.getc(x,iRow).insets(5, 5, 5, 5));
			iRow++;
			int iHeight = 100;
			for (FieldLine objLine : listLines) {
				iHeight+=50;
				x=0;
				JLabel lblType = new JLabel(objLine.getType());
				fieldsPan.add(lblType,GridC.getc(x,iRow).insets(10, 10, 10, 10));
				x++;
				JButton btnChange = new JButton("Chg");
				mapCombos.put(btnChange,objLine.getType());
				fieldsPan.add(btnChange, GridC.getc(x,iRow).insets(10, 10, 10, 10));
				btnChange.addActionListener(new ActionListener () {
					@Override
					public void actionPerformed(ActionEvent e) {
						JButton btnTemp = (JButton) e.getSource();
						changeTransType(mapCombos.get(btnTemp));
					}
				});
				x++;
				JComboBox<String> cbTType = new JComboBox<String>(Constants.TRANSTYPES);
				cbTType.setSelectedIndex(objLine.getTranType());
				mapCombos.put(cbTType,objLine.getType());
				cbTType.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						@SuppressWarnings("unchecked")
						JComboBox<String> cbTTypeT = (JComboBox<String>) e.getSource();
						String strType = mapCombos.get(cbTTypeT);
						params.updateTransType(strType, cbTTypeT.getSelectedIndex());
					}
				});
				fieldsPan.add(cbTType,GridC.getc(x,iRow).insets(10, 10, 10, 10) );
				x++;
				JComboBox<String> cbAcct = new JComboBox<String>(arrCategories);
				cbAcct.setSelectedItem(objLine.getAccountName());
				mapCombos.put(cbAcct,objLine.getType());
				cbAcct.addActionListener(new ActionListener () {
					@Override
					public void actionPerformed(ActionEvent e) {
						@SuppressWarnings("unchecked")
						JComboBox<String> cbAcctT = (JComboBox<String>) e.getSource();
						String strType = mapCombos.get(cbAcctT);
						String strAcct = (String) cbAcctT.getSelectedItem();
						params.updateAccount(strType, strAcct, mapAccts.get(strAcct));
					}
				});
				fieldsPan.add(cbAcct,GridC.getc(x,iRow).insets(10, 10, 10, 10));	
				x++;
				JButton btnDelete = new JButton("Delete");
				btnDelete.addActionListener(new ActionListener () {
					@Override
					public void actionPerformed(ActionEvent e) {
						deleteTransType(e);
					}
				});
				fieldsPan.add(btnDelete,GridC.getc(x,iRow).insets(10, 10, 10, 10));
				mapCombos.put(btnDelete,objLine.getType());
				
				iRow++;
			}
			x=0;
			addBtn = new JButton("Add Transaction Type");
			addBtn.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					addTran();
				}
			});
			fieldsPan.add(addBtn, GridC.getc(x,iRow).insets(10, 10, 10, 10));
			fieldsPan.revalidate();
			fieldsPan.setPreferredSize(new Dimension(800,iHeight));
			if (iHeight>400)
				iHeight = 300;
			fieldsScroll.setPreferredSize(new Dimension(800,iHeight));

	  }
	  private void deleteTransType(ActionEvent e){
			String strType = mapCombos.get(e.getSource());
			params.deleteField(strType);
			fieldsPan.removeAll();
			buildLines();
			fieldsPan.revalidate();
			fieldsScroll.revalidate();
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
			topFrame.pack();
		  
	  }
	  private void changeTransType (String strType) {
		  	displayTran(strType);
	  }
	  private void addTran() {
		  	displayTran(Constants.NEWTRAN);
	  }
	  private void displayTran(String strTran) {
		  int x=0;
		  int y=0;
		    FieldLine objCurrent = null;
			JPanel panInput = new JPanel(new GridBagLayout());
			JLabel lblType = new JLabel("Enter Transaction Type");
			panInput.add(lblType,GridC.getc(x,y).insets(10, 10, 10, 10));
			JTextField txtType = new JTextField();
			txtType.setColumns(20);
			x++;
			panInput.add(txtType,GridC.getc(x,y).insets(10, 10, 10, 10));
			x++;
			JLabel lblMult = new JLabel("Type");
			panInput.add(lblMult,GridC.getc(x,y).insets(10, 10, 10, 10));
			x++;
			JComboBox<String> cbTType = new JComboBox<String>(Constants.TRANSTYPES);
			panInput.add(cbTType,GridC.getc(x,y).insets(10, 10, 10, 10));
			x++;
			JComboBox<String> cbAcct = new JComboBox<String>(arrCategories);
			panInput.add(cbAcct,GridC.getc(x,y).insets(10, 10, 10, 10));
		  	if (!strTran.equals(Constants.NEWTRAN)) {
		  		objCurrent = params.matchType(strTran);
		  		if (objCurrent != null) {
		  			txtType.setText(objCurrent.getType());
		  			cbTType.setSelectedIndex(objCurrent.getTranType());
		  			cbAcct.setSelectedItem(objCurrent.getAccountName());
		  		}
		  	}
		  	while (true) {
				int iResult = JOptionPane.showConfirmDialog(null,  panInput, 
						"Enter Exchange and Multiplier", JOptionPane.OK_CANCEL_OPTION);
				if (iResult == JOptionPane.OK_OPTION){
					String strType = txtType.getText();
					if (strType.equals("")) {
						JOptionPane.showMessageDialog(null, "Transaction Type can not be blank");
						continue;
					}
					FieldLine objTempLine = params.matchType(strType);
					if (objTempLine != null) {
						if (objCurrent != null && objCurrent != objTempLine)
							JOptionPane.showMessageDialog(null, "Transaction Type already defined");
							continue;
					}
					if (objCurrent != null) {
						String strOldType = objCurrent.getType();
						objCurrent.setType(strType);
						objCurrent.setTranType(cbTType.getSelectedIndex());
						objCurrent.setAccount((String)cbAcct.getSelectedItem(),
							mapAccts.get(cbAcct.getSelectedItem()));
						for (Map.Entry<Object,String> entry : mapCombos.entrySet()){
							String strValue = entry.getValue();
							if (strValue.equals(strOldType))
								entry.setValue(strType);
						}
					}
					else
						params.addField(strType,
							(String)cbAcct.getSelectedItem(),
							mapAccts.get(cbAcct.getSelectedItem()),
							cbTType.getSelectedIndex());
					break;
				}
				if (iResult == JOptionPane.CANCEL_OPTION)
					break;
			}
			fieldsPan.removeAll();
			buildLines();
			fieldsPan.revalidate();
			fieldsScroll.revalidate();
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
			topFrame.pack();
			return;
		
	  }
	private void loadSecurities() {
		if (fileName.getText().equals("")) {
			JFrame fTemp = new JFrame();
			JOptionPane.showMessageDialog(fTemp,"Please Select a file first");
			return;
		}
			
		if (referenceCB.getSelectedIndex()==0 ||
				tickerCB.getSelectedIndex() == 0 ||
				dateCB.getSelectedIndex() == 0 ||
				valueCB.getSelectedIndex() == 0 ||
				descCB.getSelectedIndex() == 0) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"All fields must be selected");
				return;			
			}
			int [] iSelected = new int[arColumns.length+1];
			for (int i=0;i<iSelected.length;i++)
				iSelected[i] = 0;
			iSelected[referenceCB.getSelectedIndex()] = 1;
			if (iSelected[tickerCB.getSelectedIndex()] != 0) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"No field can be selected twice");
				return;	
			}
			else
				iSelected[tickerCB.getSelectedIndex()]= 1;
			if (iSelected[dateCB.getSelectedIndex()] != 0) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"No field can be selected twice");
				return;	
			}
			else
				iSelected[dateCB.getSelectedIndex()]= 1;
			if (iSelected[descCB.getSelectedIndex()] != 0) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"No field can be selected twice");
				return;	
			}
			else
				iSelected[descCB.getSelectedIndex()]= 1;
			if (iSelected[valueCB.getSelectedIndex()] != 0) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"No field can be selected twice");
				return;	
			}
			else
				iSelected[valueCB.getSelectedIndex()]= 1;
	     //Create and set up the window.
		if (accountsCB.getSelectedIndex()==0) {
			JFrame fTemp = new JFrame();
			JOptionPane.showMessageDialog(fTemp,"Please select an account");
			return;				
		}
	    JFrame frame = new JFrame("MoneyDance Load Security Prices");
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    Account acct = mapNames.get(accountsCB.getSelectedItem());
	    loadTickers(acct);
	    loadWindow = new loadPricesWindow(fileName,acct,params);
	    frame.getContentPane().add(loadWindow);

	    //Display the window.
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);

	  }
	  private void close() {
		  this.setVisible(false);
		  if (loadWindow != null)
			  loadWindow.close();
		  JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		  topFrame.dispose();		  
	  }
		/*
		 * Create an array of Investment Accounts for combo box
		 */
	  private void loadAccounts(Account parentAcct,String strName) {
		    int sz = parentAcct.getSubAccountCount();
		    for(int i=0; i<sz; i++) {
		      Account acct = parentAcct.getSubAccount(i);
		      if (acct.getAccountType() == Account.AccountType.INVESTMENT){
		    	  mapNames.put(acct.getAccountName(),acct);
		      }
		      String strEntry = strName.equals("")?acct.getAccountName():strName+":"+acct.getAccountName();
			  if (acct.getAccountType () == Account.AccountType.EXPENSE ||
		    	  acct.getAccountType () == Account.AccountType.INCOME) {
		    	  listCategories.add(acct);
		    	  mapAccts.put(strEntry, acct);		    	  
		      }
		      loadAccounts(acct,strEntry);
		    }
	  }
	  private Image getIcon(String icon) {
	     try {
		      ClassLoader cl = getClass().getClassLoader();
		      java.io.InputStream in = 
		        cl.getResourceAsStream("/com/moneydance/modules/features/loadsectrans/"+icon);
		      if (in != null) {
		        ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
		        byte buf[] = new byte[256];
		        int n = 0;
		        while((n=in.read(buf, 0, buf.length))>=0)
		          bout.write(buf, 0, n);
		        return Toolkit.getDefaultToolkit().createImage(bout.toByteArray());
		      }
		    } 
	     catch (Throwable e) {} 
   		return null;
	  }
/*
 * Create table of Securities keyed by Ticker
 */
 	  private void loadTickers(Account parentAcct) {
 		int sz = parentAcct.getSubAccountCount();
        for(int i=0; i<sz; i++) {
        	Account acct = parentAcct.getSubAccount(i);
        	if (acct.getAccountType() == Account.AccountType.SECURITY){
        		CurrencyType ctTicker = acct.getCurrencyType();
        		if (ctTicker != null) {
        			if (!ctTicker.getTickerSymbol().equals("")) {
        				Main.mapAccounts.put(ctTicker.getTickerSymbol(), acct);
        			}
        		}
        	}
            loadTickers(acct);
        }
    }
 }