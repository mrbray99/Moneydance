package com.moneydance.modules.features.forecaster;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ReminderTable extends JTable {

	/*
	 * Renderer for select column
	 */
	private class RenderSelect extends JComponent implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (modModel.getType(row) == Constants.ReminderLineType.PARENT) {
				JCheckBox boxTemp = new JCheckBox();
				if ((boolean) value) {
					boxTemp.setBackground(Constants.ALTERNATECLR);
				} else {
					boxTemp.setBackground(Color.WHITE);
				}
				boxTemp.setForeground(Color.BLACK);
				boxTemp.setSelected((boolean) value);
				return boxTemp;
			} else {
				JTextField txtField = new JTextField(" ");
				Border empty = BorderFactory.createEmptyBorder();
				if (modModel.getParent(row).getSelected()) {
					txtField.setBackground(Constants.ALTERNATECLR);
				} else {
					txtField.setBackground(Color.WHITE);
				}
				txtField.setBorder(empty);
				return txtField;
			}
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
			boolean bLineSelected;
			if (modModel.getType(row) == Constants.ReminderLineType.SPLIT) 
				bLineSelected = modModel.getParent(row).getSelected();
			else
				bLineSelected = modModel.getLine(row).getSelected();
			if(bLineSelected)
				c.setBackground(Constants.ALTERNATECLR);
			else
				c.setBackground(Color.WHITE);
			c.setForeground(Color.BLACK);
			return c;
		}
	}


	private RenderSelect objSelectRowRender = new RenderSelect();
	private NonSelectedCellRenderer objNonSelRender = new NonSelectedCellRenderer();
	private ReminderTableModel modModel;

	public ReminderTable(ReminderTableModel modModelp) {
		super(modModelp);
		modModel = modModelp;
		this.setFillsViewportHeight(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellSelectionEnabled(true);
		/*
		 * Select
		 */
		TableColumn colSelect = this.getColumnModel().getColumn(0);
		colSelect.setPreferredWidth(25);
		colSelect.setCellEditor(new DefaultCellEditor(new JCheckBox()));
		colSelect.setCellRenderer(objSelectRowRender);
		/*
		 * Name
		 */
		this.getColumnModel().getColumn(1).setResizable(false);
		this.getColumnModel().getColumn(1).setPreferredWidth(100);
		this.getColumnModel().getColumn(1).setCellRenderer(objNonSelRender);
		/*
		 * From Date
		 */
		colSelect = this.getColumnModel().getColumn(2);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * To Date
		 */
		colSelect = this.getColumnModel().getColumn(3);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * From Account/Category
		 */
		colSelect = this.getColumnModel().getColumn(4);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(100);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * To Account/Category
		 */
		colSelect = this.getColumnModel().getColumn(5);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(100);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Amount
		 */
		colSelect = this.getColumnModel().getColumn(6);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Frequency
		 */
		colSelect = this.getColumnModel().getColumn(7);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Header Colour
		 */
		JTableHeader objHeader = getTableHeader();
		objHeader.setBackground(Constants.TABLEHEADER);
		objHeader.setOpaque(false);
		objHeader.setFont(objHeader.getFont().deriveFont(Font.BOLD));
	}

}
