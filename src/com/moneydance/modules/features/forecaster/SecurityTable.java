package com.moneydance.modules.features.forecaster;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class SecurityTable extends JTable {

	/*
	 * Renderer for select column
	 */
	private class RenderSelect extends JComponent implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
			JCheckBox boxTemp = new JCheckBox();
			if((boolean)value) {
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
	 * Renderer for columns where the user can not change data
	 */
	public class NonSelectedCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			if ((boolean)modModel.getValueAt(row, 0)) {
				c.setBackground(Constants.ALTERNATECLR);
			} else {
				c.setBackground(Color.WHITE);
			}
			return c;
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
			SecurityLine objLine = modModel.getLine(row);
			Boolean bSelected = objLine.getSelected() ? isSelected ? true
					: false : false;
			if ((boolean)modModel.getValueAt(row, 0)) {
				if (bSelected)
					c.setBackground(Constants.SELECTEDCLR);
				else
					c.setBackground(Constants.ALTERNATECLR);
			} else {
				c.setBackground(Color.WHITE);
			}
			c.setForeground(Color.BLACK);
			return c;
		}
	}

	private RenderSelect objSelectRowRender = new RenderSelect();
	private NonSelectedCellRenderer objNonSelRender = new NonSelectedCellRenderer();
	private SelectedCellRenderer objSelRender = new SelectedCellRenderer();
	private SecurityTableModel modModel;

	public SecurityTable(SecurityTableModel modModelp) {
		super(modModelp);
		modModel = modModelp;
		this.setFillsViewportHeight(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellSelectionEnabled(true);
		/*
		 * Select
		 */
		TableColumn colSelect = this.getColumnModel().getColumn(0);
		colSelect.setPreferredWidth(50);
		colSelect.setMaxWidth(50);
		colSelect.setCellEditor(new DefaultCellEditor(new JCheckBox()));
		colSelect.setCellRenderer(objSelectRowRender);
		/*
		 * Investment Name
		 */
		colSelect = this.getColumnModel().getColumn(1);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(200);
		colSelect.setMaxWidth(400);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Investment Name
		 */
		colSelect = this.getColumnModel().getColumn(2);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(200);
		colSelect.setMaxWidth(400);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * RPI
		 */
		colSelect = this.getColumnModel().getColumn(3);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(60);
		colSelect.setMaxWidth(80);
		colSelect.setCellRenderer(objSelRender);
		colSelect.setCellEditor(new MyPercEditor());
		/*
		 * Header Colour
		 */
		JTableHeader objHeader = getTableHeader();
		objHeader.setBackground(Constants.TABLEHEADER);
		objHeader.setOpaque(false);
		objHeader.setFont(objHeader.getFont().deriveFont(Font.BOLD));

	}

}
