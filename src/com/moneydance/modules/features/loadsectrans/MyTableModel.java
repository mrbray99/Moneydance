/* 
 * Copyright (c) 2014, 2016, Michael Bray. All rights reserved.
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.table.DefaultTableModel;

import com.infinitekind.moneydance.model.Account;


public class MyTableModel extends DefaultTableModel {
    private List<SecLine>listLines;
	private static String[] arrColumns = {"Select","Ticker","Settle","C","Reference","Description","Value"};

	public MyTableModel(Set<SecLine> setLinesp,SortedMap<String,Account> mapAccountsp){
		super();
		listLines = new ArrayList<SecLine> (setLinesp);
	}

	@Override
	public int getRowCount() {
		if (listLines == null)
			return 0;
		return (listLines.size());
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class getColumnClass(int c){
		if (c == 0)
			return Boolean.class;
		return String.class;
	}

		@Override
	public int getColumnCount() {
			return 7;
	}	
	@Override
	public String getColumnName(int c) {
		return arrColumns[c];
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DecimalFormat dfNumbers = new DecimalFormat("#0.0000");
		switch (columnIndex) {
		case 0:
			return  listLines.get(rowIndex).getSelect();
		case 1:
			return  listLines.get(rowIndex).getTicker();
		case 2:
			return  Main.cdate.format(listLines.get(rowIndex).getDate());
		case 3:
			return  listLines.get(rowIndex).getCleared();
		case 4:
			return  listLines.get(rowIndex).getReference();
		case 5:
			return  listLines.get(rowIndex).getDescription();
		default:
			
			return dfNumbers.format(listLines.get(rowIndex).getValue()/100.0);
		}
	}
	@Override
    public boolean isCellEditable(int row, int col) {
        /*
         * Only Select, amount, period, start date and RPI are editable
         * Category, Year 1, Year 2, Year 3 are not
         */
		if (col ==0 && !listLines.get(row).getIgnore())
			return true;
		else
			return false;
    }
	@Override
	public void setValueAt(Object value, int row, int col){
		/*
		 * copes with call when data is invalid
		 */
		if (value == null)
			return;
		if (col ==0)
			if (!(listLines.get(row).getIgnore() ||
				 listLines.get(row).getProcessed() ||
				 !listLines.get(row).getValid()))			
				listLines.get(row).setSelect((boolean) value);
	}
	
	public SecLine getLine(int row) {
		return listLines.get(row);
	}
}
