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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.SortedMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.Platform;


public class MyTable extends JTable {
	private static final long serialVersionUID = 1L;
	private MyCheckBox boxSelect = new MyCheckBox();
	String [] arrSources;
	private Parameters params;
	private DefaultCellEditor exchangeField;
	private DefaultTableCellRenderer rightRender;
	private MyCurrencyEditor currencyEditor;
	private MyDateEditor dateEditor;
	private SortedMap<String,Integer> tickerStatus;
	private MyTable tableObj;
	private MyTableModel dm;
	private JTableHeader header;
	private MRBDebug debugInst = Main.debugInst;
	private boolean isColumnWidthChanged;
	private int[] columnWidths;
	private JPopupMenu sourcePopup;
	private JMenuItem sourceDoNotLoad;
	private JMenuItem sourceYahoo;
	private JMenuItem sourceYahooHist;
	private JMenuItem sourceFT;
	private JMenuItem sourceFTHD;
	private JComboBox<String> allSources;
	private JComboBox<String> currencySources;
	public static int selectCol = 0;
	public static int tickerCol = 1;
	public static int exchangeCol = 2;
	public static int accountCol = 3;
	public static int sourceCol = 4;
	public static int lastPriceCol = 5;
	public static int lastDateCol = 6;
	public static int newPriceCol = 7;
	public static int tradeDateCol = 8;
	public static int tradeCurCol = 9;
	public static int volumeCol = 10;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private Double screenHeight;

	public class PriceRenderer extends DefaultTableCellRenderer {
	    @Override
	    public  Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        String ticker = (String)((MyTableModel)table.getModel()).getValueAt(row,1);
	        Integer status = tickerStatus.get(ticker);
	        if (status == null)
	        	status = 0;
	        switch (status){
	        case Constants.TASKSTARTED :
	        	setOpaque(true);
	        	setForeground(Color.BLACK);
	        	setBackground(Color.YELLOW);
	        	break;
	        case Constants.TASKFAILED :
	        	setOpaque(true);
	        	setForeground(Color.BLACK);
	        	setBackground(Color.RED);
	        	break;
	        case Constants.TASKCOMPLETED :
	        	setOpaque(true);
	        	setForeground(Color.BLACK);
	        	setBackground(Color.GREEN);
	        	break;
        	default :
        		setOpaque(false);
       			setForeground (UIManager.getColor("TextField.Foreground"));
	        }
	        setHorizontalAlignment(JLabel.RIGHT);
	        return this;
	    }
	}
	private int getBrightness(Color c) {
	    return (int) Math.sqrt(
	      c.getRed() * c.getRed() * .241 +
	      c.getGreen() * c.getGreen() * .691 +
	      c.getBlue() * c.getBlue() * .068);
	}
	 
	public Color getForeGroundColor(Color color) {
	    if (getBrightness(color) < 130)
	        return Color.white;
	    else
	        return Color.black;
	}	
	public class DateRenderer extends DefaultTableCellRenderer {
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
	        String date = (String) value;
	        setForeground(UIManager.getColor("TextField.Foreground"));
	        if (date.endsWith("*"))
	        	setForeground(Color.RED);	
	        setHorizontalAlignment(JLabel.RIGHT);
	        return this;
	    }
	}
	public static class CheckBoxRenderer extends DefaultTableCellRenderer {
		@Override
		 public Component getTableCellRendererComponent(
                 JTable table, Object color,
                 boolean isSelected, boolean hasFocus,
                 int row, int column) {
			JCheckBox checkBox;
			 if ((boolean)table.getValueAt(row, column))
				 checkBox =  new JCheckBox(Main.extension.selectedIcon);
			 else
				 checkBox = new JCheckBox(Main.extension.unselectedIcon);
			 checkBox.setHorizontalAlignment(JLabel.CENTER);
			 return checkBox;
		 }
	}

	public MyTable(Parameters paramsp, MyTableModel dmp, SortedMap<String,Integer>tickerStatusp) {
		super(dmp);
		dm=dmp;
		tableObj=this;
		params = paramsp;
		screenHeight = screenSize.getHeight();
		header = getTableHeader();
		tickerStatus = tickerStatusp;
		columnWidths = Main.preferences.getIntArray(Constants.PROGRAMNAME+"."+Constants.CRNTCOLWIDTH);
		if (columnWidths.length == 0 || columnWidths.length<Constants.NUMTABLECOLS)
			columnWidths = Main.preferences.getIntArray(Constants.CRNTCOLWIDTH);
		if (columnWidths.length == 0 || columnWidths.length<Constants.NUMTABLECOLS)
			columnWidths = Constants.DEFAULTCOLWIDTH;
		allSources = new JComboBox<String>(params.getSourceArray());
		currencySources = new JComboBox<String>(params.getCurSourceArray());
		currencyEditor = new MyCurrencyEditor(params);
		dateEditor = new MyDateEditor(params);
		rightRender = new DefaultTableCellRenderer();
		rightRender.setHorizontalAlignment(JLabel.RIGHT);
		setRowHeight(20);
		this.setFillsViewportHeight(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellSelectionEnabled(true);
		this.setDefaultRenderer(MyCheckBox.class,new CheckBoxRenderer());
		this.getColumnModel().addColumnModelListener(new WidthListener());
		this.getTableHeader().addMouseListener(new HeaderMouseListener());
		this.addMouseListener(new TableMouseListener());
		/*
		 * Select
		 */
		TableColumn colSelect = this.getColumnModel().getColumn(selectCol);
		colSelect.setCellEditor(new DefaultCellEditor(boxSelect));
		if (Platform.isFreeBSD() || Platform.isUnix())
			colSelect.setCellRenderer(new CheckBoxRenderer());
		colSelect.setPreferredWidth(columnWidths[selectCol]);
		/*
		 * Ticker
		 */
		this.getColumnModel().getColumn(tickerCol).setResizable(true);
		this.getColumnModel().getColumn(tickerCol).setPreferredWidth(columnWidths[tickerCol]);
		/*
		 * Exchange Modifier
		 */
		this.getColumnModel().getColumn(exchangeCol).setResizable(true);
		this.getColumnModel().getColumn(exchangeCol).setCellEditor(exchangeField);
		this.getColumnModel().getColumn(exchangeCol).setPreferredWidth(columnWidths[exchangeCol]);
		/*
		 * Account
		 */
		this.getColumnModel().getColumn(accountCol).setResizable(true);
		this.getColumnModel().getColumn(accountCol).setPreferredWidth(columnWidths[accountCol]);
		/*
		 * Source
		 */
		this.getColumnModel().getColumn(sourceCol).setPreferredWidth(columnWidths[sourceCol]);
		this.getColumnModel().getColumn(sourceCol).setResizable(true);
		/*
		 * Current Price
		 */
		this.getColumnModel().getColumn(lastPriceCol).setResizable(true);
		this.getColumnModel().getColumn(lastPriceCol).setPreferredWidth(columnWidths[lastPriceCol]);
		this.getColumnModel().getColumn(lastPriceCol).setCellRenderer(rightRender);
		/*
		 * Current Price Date
		 */
		this.getColumnModel().getColumn(lastDateCol).setResizable(true);
		this.getColumnModel().getColumn(lastDateCol).setPreferredWidth(columnWidths[lastDateCol]);
		this.getColumnModel().getColumn(lastDateCol).setCellRenderer(new DateRenderer());
		/*
		 * New Price
		 */
		this.getColumnModel().getColumn(newPriceCol).setResizable(true);
		this.getColumnModel().getColumn(newPriceCol).setCellEditor(currencyEditor);
		this.getColumnModel().getColumn(newPriceCol).setPreferredWidth(columnWidths[newPriceCol]);
		this.getColumnModel().getColumn(newPriceCol).setCellRenderer(new PriceRenderer());
		/*
		 * Trade Date
		 */
		this.getColumnModel().getColumn(tradeDateCol).setResizable(true);
		this.getColumnModel().getColumn(tradeDateCol).setCellEditor(dateEditor);
		this.getColumnModel().getColumn(tradeDateCol).setPreferredWidth(columnWidths[tradeDateCol]);
		this.getColumnModel().getColumn(tradeDateCol).setCellRenderer(rightRender);
		/*
		 * Trade Currency
		 */
		this.getColumnModel().getColumn(tradeCurCol).setResizable(true);
		this.getColumnModel().getColumn(tradeCurCol).setPreferredWidth(columnWidths[tradeCurCol]);
		/*
		 * Volume
		 */
		this.getColumnModel().getColumn(volumeCol).setResizable(true);
		this.getColumnModel().getColumn(volumeCol).setPreferredWidth(columnWidths[volumeCol]);
		this.getColumnModel().getColumn(volumeCol).setCellRenderer(rightRender);
		/*
		 * pop up menu
		 */
		ActionListener popupListener = new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent aeEvent) {
				String strAction = aeEvent.getActionCommand();
				if (strAction.contains("Do Not")){
					dm.updateAllSources(0);
					return;
				}
				if (strAction.contains("Yahoo HD")){
					dm.updateAllSources(3);
					return;
				}
				if (strAction.contains("Yahoo")){
					dm.updateAllSources(1);
					return;
				}
				if (strAction.contains("FT HD")){
					dm.updateAllSources(4);
					return;
				}
				if (strAction.contains("FT")){
					dm.updateAllSources(2);
					return;
				}
			}
		};
		sourcePopup = new JPopupMenu();
		sourceDoNotLoad = new JMenuItem();
		sourceDoNotLoad.setText("Set all to Do Not Load");
		sourcePopup.add(sourceDoNotLoad);
		sourceDoNotLoad.addActionListener(popupListener);
		sourceYahoo = new JMenuItem();
		sourceYahoo.setText("Set all to Yahoo");
		sourcePopup.add(sourceYahoo);
		sourceYahoo.addActionListener(popupListener);
		sourceYahooHist = new JMenuItem();
		sourceYahooHist.setText("Set all to Yahoo HD");
		sourcePopup.add(sourceYahooHist);
		sourceYahooHist.addActionListener(popupListener);
		sourceFT = new JMenuItem();
		sourceFT.setText("Set all to FT");
		sourcePopup.add(sourceFT);
		sourceFT.addActionListener(popupListener);
		sourceFTHD = new JMenuItem();
		sourceFTHD.setText("Set all to FT HD");
		sourcePopup.add(sourceFTHD);
		sourceFTHD.addActionListener(popupListener);
	}
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
        {
            int modelColumn = convertColumnIndexToModel( column );
            int numAccts = dm.getNumAccounts();
	    	debugInst.debug("MyTable", "getCellEditor", MRBDebug.SUMMARY, "Account list "+numAccts);
	    	if (modelColumn == sourceCol)
            {
       	    	debugInst.debug("MyTable", "getCellEditor", MRBDebug.SUMMARY, "Row "+row);
           		if (row >= numAccts)
            			return new DefaultCellEditor(currencySources);
            		else
            			return new DefaultCellEditor(allSources);
            }
            return super.getCellEditor(row, column);
        }
 	}
	/*
	 * popup menu
	 */
	private void showPopup(MouseEvent me) {
		// is this event a popup trigger?
			Point p = me.getPoint();
			int iCol = columnAtPoint(p);
			// if we've clicked on a row in the source col
	    	debugInst.debug("MyTable", "showPopup", MRBDebug.SUMMARY, "source selected "+iCol);
			if (iCol == sourceCol) {
				sourcePopup.show(header, me.getX(), me.getY());
			}
	} 
	private void showExchangePopup(int row, Point p){
    	debugInst.debug("MyTable", "showExchangePopup", MRBDebug.SUMMARY, "displaying exchange popup ");
    	ExchangePopUp popup = new ExchangePopUp(row,params,dm);
    	Dimension popupSize = popup.getSize();
    	if (p.getY()+popupSize.getHeight()> screenHeight){
    		int dy = (int) Math.round(screenHeight-p.getY()-popupSize.getHeight()-20);
    		p.translate(0,dy);
    	}
      	popup.setLocation(p);
    	popup.setVisible(true);
	}
	private void displayExchangeTicker(int row){
	    debugInst.debug("MouseListener", "displayExchangeTicker", MRBDebug.SUMMARY, "on ro "+row );    
		String ticker = (String) dm.getValueAt(row,tickerCol);
		String exchange = (String)dm.getValueAt(row,exchangeCol);
		String source = (String)dm.getValueAt(row,sourceCol);
		int sourceid = 0;
		String tickerSource="";
		if (source.equals(Constants.YAHOO)) {
			sourceid = Constants.YAHOOINDEX;
			tickerSource = Constants.SOURCEYAHOO;
		}
		if (source.equals(Constants.YAHOOHIST)) {
			sourceid = Constants.YAHOOHISTINDEX;
			tickerSource = Constants.SOURCEYAHOOHIST;
		}
		if (source.equals(Constants.FT)) {
			sourceid = Constants.FTINDEX;
			tickerSource = Constants.SOURCEFT;
		}
		if (source.equals(Constants.FTHIST)) {
			sourceid = Constants.FTHISTINDEX;
			tickerSource = Constants.SOURCEFTHIST;
		}
		if (!exchange.isEmpty())
			ticker = params.getNewTicker(ticker, exchange, sourceid);
		Rectangle rect = this.getCellRect(row, exchangeCol, false);
		final String tickerFinal = ticker;
		final String sourceFinal = tickerSource;
		final String exchangeFinal = exchange;
		ActionListener tickerListener = new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent aeEvent) {
				String strAction = aeEvent.getActionCommand();
				if (strAction.contains("Test")){
					Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.TESTTICKERCMD
							+"?qs="+sourceFinal+"&s="+tickerFinal);
				}
				if (strAction.contains("Copy")){
					StringSelection stringSelection = new StringSelection(tickerFinal);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);;
				}
				if (strAction.contains("Set")){
					dm.selectAllExchanges(exchangeFinal);
					dm.fireTableDataChanged();
				}
			}
		};		
		JPopupMenu menu=new JPopupMenu();
		JMenuItem test = new JMenuItem("Test " + ticker);
		test.addActionListener(tickerListener);
		JMenuItem copy = new JMenuItem("Copy "+ ticker);
		copy.addActionListener(tickerListener);
		JMenuItem setAll = new JMenuItem("Set all exchanges to "+exchange);
		setAll.addActionListener(tickerListener);
		menu.add(test);
		menu.add(copy);
		menu.add(setAll);
		menu.show(this, rect.x+rect.width, rect.y);
	}
    public boolean getColumnWidthChanged() {
        return isColumnWidthChanged;
    }

    public void setColumnWidthChanged(boolean widthChanged) {
        isColumnWidthChanged = widthChanged;
    }

	private class WidthListener extends MouseAdapter implements TableColumnModelListener
	{
	    @Override
	    public void columnMarginChanged(ChangeEvent e)
	    {
	        /* columnMarginChanged is called continuously as the column width is changed
	           by dragging. Therefore, execute code below ONLY if we are not already
	           aware of the column width having changed */
	    	if(!tableObj.getColumnWidthChanged())
	        {
	            /* the condition  below will NOT be true if
	               the column width is being changed by code. */
	            if(tableObj.getTableHeader().getResizingColumn() != null)
	            {
	                // User must have dragged column and changed width
	                tableObj.setColumnWidthChanged(true);
	            }
	        }
	    }
	    // line to force save
	    @Override
	    public void columnMoved(TableColumnModelEvent e) { }

	    @Override
	    public void columnAdded(TableColumnModelEvent e) { }

	    @Override
	    public void columnRemoved(TableColumnModelEvent e) { }

	    @Override
	    public void columnSelectionChanged(ListSelectionEvent e) { }
	}
		private class HeaderMouseListener extends MouseAdapter
	{
		int resizingColumn=-2;
		int oldWidth=-2;
		int newWidth=-2;
		@Override
	    public void mousePressed(MouseEvent e)
	    {
	        /* On mouse release, check if column width has changed */
			if (SwingUtilities.isRightMouseButton(e) || e.isControlDown())
				showPopup(e);
			else {
    	    	debugInst.debug("MouseListener", "mouseReleased", MRBDebug.SUMMARY, "width started ");
    	        if(e.getSource() instanceof JTableHeader) {
    	            TableColumn tc = ((JTableHeader)e.getSource()).getResizingColumn();
    	            if(tc != null) {
     	                resizingColumn = tc.getModelIndex();
    	                oldWidth = tc.getPreferredWidth();
    	            } else {
    	                resizingColumn = -1;
    	                oldWidth = -1;
    	            }
    	        }
  
    	    	debugInst.debug("MouseListener", "mouseReleased", MRBDebug.SUMMARY, "column "+resizingColumn+" oldWidth "+oldWidth);
			}
	    }
		@Override
	    public void mouseReleased(MouseEvent e)
	    {
	        /* On mouse release, check if column width has changed */
			if (SwingUtilities.isRightMouseButton(e)|| e.isControlDown())
				showPopup(e);
			else {
		        if(tableObj.getColumnWidthChanged())
		        {
	    	    	debugInst.debug("MouseListener", "mouseReleased", MRBDebug.SUMMARY, "width finished ");
	    	        if(e.getSource() instanceof JTableHeader) {
	    	            TableColumn tc = ((JTableHeader)e.getSource()).getColumnModel().getColumn(resizingColumn);
	    	            if(tc != null) {
	    	            	newWidth = tc.getPreferredWidth();
	    	            } else {
	    	                resizingColumn = -1;
	    	                oldWidth = -1;
	    	            }
	    	        }   
	    	    	debugInst.debug("MouseListener", "mouseReleased", MRBDebug.SUMMARY, "Column "+ resizingColumn +"new width "+newWidth);
	    	    	columnWidths[resizingColumn] = newWidth;
	    	    	Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTCOLWIDTH, columnWidths);
	    	    	Main.preferences.isDirty();
		            // Reset the flag on the table.
		            tableObj.setColumnWidthChanged(false);
		            debugInst.debug("MouseListener", "mouseReleased", MRBDebug.SUMMARY, "column "+resizingColumn+" oldWidth "+oldWidth);

		        }
		    }
	    }
	}
	private class TableMouseListener extends MouseAdapter
	{
		@Override
	    public void mouseReleased(MouseEvent e)
	    {
			JTable tc = (JTable)e.getSource();
       		Point p = e.getPoint();
       		int row = tc.rowAtPoint(p);
			if (dm.getRowType(row).equals(Constants.CURRENCYTYPE))
				return;
			if (tc.getSelectedColumn() == exchangeCol) {
	  	       	if (SwingUtilities.isRightMouseButton(e) || e.isControlDown() ) {
	  	       		displayExchangeTicker(row);
	  	       	}
	  	       	else {
	  	       		if (e.getClickCount() == 2) {
		   	           	Rectangle rect = tc.getCellRect(tc.getSelectedRow(), exchangeCol, false);
		   	           	Point p2 = new Point(rect.x+rect.width,rect.y+rect.width);
		   	 	        showExchangePopup(tc.getSelectedRow(),p2);
	  	       		}
	  	       	}
			}
	    }
	}
}