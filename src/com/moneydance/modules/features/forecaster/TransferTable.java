package com.moneydance.modules.features.forecaster;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class TransferTable extends JTable {
	/*
	 * pop up menu items
	 */
	private JPopupMenu menAction;
	private JMenuItem mitActionAdd;
	private JMenuItem mitActionDel;

	/*
	 * Renderer for select column
	 */
	private class RenderSelect extends JComponent implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
			JCheckBox boxTemp = new JCheckBox();
			if ((boolean)value) {
				boxTemp.setBackground(Constants.ALTERNATECLR);
			} else {
				boxTemp.setBackground(Color.WHITE);
			}
			boxTemp.setForeground(Color.BLACK);
			boxTemp.setSelected((boolean) value);
			return boxTemp;
		}

	}

	/*
	 * Renderer for columns where the user can change data
	 */
	public class SelectedCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			TransferLine objLine = modModel.getLine(row);
			Boolean bSelected = objLine.getSelected() ? isSelected ? true
					: false : false;
			if (objLine.getSelected()) {
				if (bSelected)
					c.setBackground(Constants.SELECTEDCLR);
				else
					c.setBackground(Constants.ALTERNATECLR);
			} else {
				if (bSelected)
					c.setBackground(Constants.SELECTEDCLR);
				else
					c.setBackground(Color.WHITE);
			}
			c.setForeground(Color.BLACK);
			return c;
		}
	}

	/*
	 * Renderer for columns where the user can not change data
	 */
	public class NonSelectedCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			c.setForeground(Color.BLACK);
			if ((boolean)modModel.getValueAt(row, 0)) {
				c.setBackground(Constants.ALTERNATECLR);
			} else {
				c.setBackground(Color.WHITE);
			}
			return c;
		}
	}



	private JComboBox<String> boxAccounts;
	private JComboBox<String> boxPeriods;
	private RenderSelect objSelectRowRender = new RenderSelect();
	private NonSelectedCellRenderer objNonSelRender = new NonSelectedCellRenderer();
	private SelectedCellRenderer objSelRender = new SelectedCellRenderer();
	private TransferTableModel modModel;
	private MyCurrencyEditor objCurrency;
	private String[] arrAccounts;

	public TransferTable(TransferTableModel modModelp, String[] arrAccountsp) {
		super(modModelp);
		modModel = modModelp;
		arrAccounts = arrAccountsp;
		boxAccounts = new JComboBox<String>(arrAccounts);
		boxPeriods = new JComboBox<String>(Constants.arrPeriod);
		this.setFillsViewportHeight(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellSelectionEnabled(true);
		objCurrency = new MyCurrencyEditor ();
		/*
		 * Select
		 */
		TableColumn colSelect = this.getColumnModel().getColumn(0);
		colSelect.setPreferredWidth(25);
		colSelect.setCellEditor(new DefaultCellEditor(new JCheckBox()));
		colSelect.setCellRenderer(objSelectRowRender);
		/*
		 * source account
		 */
		this.getColumnModel().getColumn(1).setResizable(false);
		this.getColumnModel().getColumn(1).setPreferredWidth(200);
		this.getColumnModel().getColumn(1)
				.setCellEditor(new DefaultCellEditor(boxAccounts));
		this.getColumnModel().getColumn(1).setCellRenderer(objNonSelRender);
		/*
		 * destination account
		 */
		this.getColumnModel().getColumn(2).setResizable(false);
		this.getColumnModel().getColumn(2).setPreferredWidth(200);
		this.getColumnModel().getColumn(2)
				.setCellEditor(new DefaultCellEditor(boxAccounts));
		this.getColumnModel().getColumn(2).setCellRenderer(objNonSelRender);
		/*
		 * Period
		 */
		this.getColumnModel().getColumn(3).setResizable(false);
		this.getColumnModel().getColumn(3).setPreferredWidth(100);
		this.getColumnModel().getColumn(3)
		.setCellEditor(new DefaultCellEditor(boxPeriods));
		this.getColumnModel().getColumn(3).setCellRenderer(objNonSelRender);

		/*
		 * Actual Amount
		 */
		colSelect = this.getColumnModel().getColumn(4);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setCellEditor(objCurrency);
		colSelect.setCellRenderer(objSelRender);
		/*
		 * RPI
		 */
		colSelect = this.getColumnModel().getColumn(5);
		colSelect.setPreferredWidth(40);
		colSelect.setCellRenderer(objSelRender);
		colSelect.setCellEditor(new MyPercEditor());
		/*
		 * Header Colour
		 */
		JTableHeader objHeader = getTableHeader();
		objHeader.setBackground(Constants.TABLEHEADER);
		objHeader.setOpaque(false);
		objHeader.setFont(objHeader.getFont().deriveFont(Font.BOLD));
		/*
		 * pop up menu
		 */
		ActionListener mitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aeEvent) {
				String strAction = aeEvent.getActionCommand();
				int iRow = TransferTable.this.getSelectedRow();
				if (strAction.contains(Constants.ADDTRANSFER)) {
					modModel.addSourceLine (iRow);
				} else if (strAction.contains(Constants.DELETETRANSFER)) {
					modModel.deleteSourceLine (iRow);
				}
				TransferTable.this.modModel.fireTableDataChanged();
			}
		};
		menAction = new JPopupMenu();
		mitActionAdd = new JMenuItem(Constants.ADDTRANSFER);
		mitActionAdd.addActionListener(mitListener);
		menAction.add(mitActionAdd);
		mitActionDel = new JMenuItem(Constants.DELETETRANSFER);
		mitActionDel.addActionListener(mitListener);
		menAction.add(mitActionDel);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {

				showPopup(me);
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				showPopup(me);
			}
		});
	}

	/*
	 * popup menu
	 */
	private void showPopup(MouseEvent me) {
		// is this event a popup trigger?
		if (me.isPopupTrigger()) {
			Point p = me.getPoint();
			int iRow = rowAtPoint(p);
			int iCol = columnAtPoint(p);
			setRowSelectionInterval(iRow, iRow);
			setColumnSelectionInterval(iCol, iCol);
			// if we've clicked on a row in the amount col
			if (iRow != -1) {
				menAction.show(me.getComponent(), me.getX(), me.getY());
			}
		}
	}
	public void resetAccounts(String [] arrAccountsp){
		boxAccounts.removeAllItems();
		for (String strItem : arrAccountsp){
			boxAccounts.addItem(strItem);
		}
		modModel.fireTableDataChanged();
	}

}
