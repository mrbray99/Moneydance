/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
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
 * 
 */
package com.moneydance.modules.features.securityquoteload;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.SortedMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.MRBDebug;


public class ExchangePopUp extends JDialog
{
    SortedMap<String,ExchangeLine> mapExchanges;
    ArrayList<String>listCodes;
    ArrayList<ExchangeLine>listExchanges;
    String ticker;
    String curExchange;
    private final JTable exTable;
    private final MyTableModel dm; 
    private final Parameters params;
    private final ExchangeData model;
    MRBDebug debugInst = MRBDebug.getInstance();
    

    public ExchangePopUp(int row,Parameters paramsp, MyTableModel priceModel)
    {
    	super((JFrame)null,"Select an Exchange",true);
    	dm=priceModel;
    	params = paramsp;
    	ticker = (String)priceModel.getValueAt(row,MyTable.tickerCol);
    	curExchange = (String)priceModel.getValueAt(row, MyTable.exchangeCol);
    	BorderLayout layout = new BorderLayout();
    	this.setLayout(layout);
    	debugInst.debug("ExchangePopUp", "ExchangePopUp", MRBDebug.SUMMARY, "started ");
    	mapExchanges = params.getExchangeLines();
    	listCodes = new ArrayList<>(mapExchanges.keySet());
    	listExchanges = new ArrayList<>(mapExchanges.values());
    	// use JInternalFrame as pop up editor
    	model = new ExchangeData();
        exTable = new JTable(model);
		exTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		exTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		exTable.setAutoCreateRowSorter(true);

       	debugInst.debug("ExchangePopUp", "ExchangePopUp", MRBDebug.SUMMARY, "Rows cols "+exTable.getRowCount()+" "+ exTable.getColumnCount());
       	JScrollPane scroll = new JScrollPane(exTable);
        this.add(scroll,BorderLayout.CENTER);
        for (int i=0;i<model.getRowCount();i++){
        	if (((String)model.getValueAt(i,0)).equals(curExchange)) {
        		exTable.setRowSelectionInterval(i,i);
        		Rectangle rect = exTable.getCellRect(i, 0, false);
        		exTable.scrollRectToVisible(rect);
        	}
        }
        JPanel buttons = new JPanel(new GridBagLayout());
        JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = exTable.getSelectedRow();
				if (selected != -1) {
					String exchange = (String) model.getValueAt(selected, 0);
					if (exchange.isEmpty())
						params.setExchange(ticker, null);
					else
						params.setExchange(ticker, exchange);
				}
				dm.fireTableDataChanged();
				dispose();
			}
		});	
        JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});	


        buttons.add(okButton,GridC.getc(0, 0).center().insets(5,20,5,5));
        buttons.add(cancelButton,GridC.getc(1, 0).center().insets(5,20,5,5));
        this.add(buttons,BorderLayout.SOUTH);
		pack();
        }
     
    public void close() {
    }

	private class ExchangeData extends DefaultTableModel {
		@Override
		public int getRowCount() {

			return mapExchanges.size()+1;
		}
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c){
			return String.class;
		}

		@Override
		public int getColumnCount() {
				return 2;
		}	
		@Override
		public String getColumnName(int c) {
			switch (c){
			case 0:
				return "Code";
			default :
				return "Name";
			}
		}
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			/*
			 * Select
			 */
			case 0:
				if (rowIndex == 0)
					return " ";
				return  listCodes.get(rowIndex-1);
			default:
				if (rowIndex == 0)
					return "Do not use an exchange";
				return  listExchanges.get(rowIndex-1).getName();

			}
		}
		@Override
	    public boolean isCellEditable(int row, int col) {
			return false;
	    }
	}
  
}
