package com.moneydance.modules.features.forecaster;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class BudgetTable extends JTable {
	/*
	 * pop up menu items
	 */
	private JPopupMenu menAmount;
	private JMenuItem mitBudget;
	private JMenuItem mitActuals;
	private JMenuItem mitMixed;
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
			if (modModel.getType(row) == Constants.BudgetLineType.PARENT) {
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
				if (modModel.getLine(row).getParent().getSelected()) {
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
	 * Renderer for columns where the user can change data
	 */
	public class SelectedCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			BudgetLine objLine = modModel.getLine(row);
			boolean bLineSelect;
			if (objLine.getType() == Constants.BudgetLineType.SPLIT)
				bLineSelect = objLine.getParent().getSelected();
			else
				bLineSelect = objLine.getSelected();
			if (bLineSelect) {
				if (isSelected)
					c.setBackground(Constants.SELECTEDCLR);
				else
					c.setBackground(Constants.ALTERNATECLR);
			} else {
				if (isSelected)
					c.setBackground(Constants.SELECTEDCLR);
				else
					c.setBackground(Color.WHITE);
			}
			c.setForeground(Color.BLACK);
			/*
			 * if rendering Account Amount and the error has been set set
			 * background to red.
			 */
			if (column == 9 || column == 6) {
				if (objLine.getAmtError())
					c.setBackground(Color.RED);
			}
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
			BudgetLine objLine = modModel.getLine(row);
			c.setForeground(Color.BLACK);
			if (row > -1) {
				IncludedBudget objItem = objLine.getItem();
				if (objLine.getType() == Constants.BudgetLineType.PARENT) {
					if (objItem != null) {
						switch (column) {
						case 2:
							if (objItem.getAnnualAmtType() == Constants.AnnualAmount.MIXED) {
								c.setBackground(Constants.BUDGETSELECT);
								return c;
							}
							break;
						case 3:
							if (objItem.getAnnualAmtType() == Constants.AnnualAmount.ACTUAL) {
								c.setBackground(Constants.BUDGETSELECT);
								return c;
							}
							break;
						case 4:
							if (objItem.getAnnualAmtType() == Constants.AnnualAmount.MIXED) {
								c.setBackground(Constants.BUDGETSELECT);
								return c;
							}
							break;
						case 5:
							if (objItem.getAnnualAmtType() == Constants.AnnualAmount.BUDGET) {
								c.setBackground(Constants.BUDGETSELECT);
								return c;
							}
							break;
						}
					}
				}
			}
			boolean bLineSelect;
			if (objLine.getType() == Constants.BudgetLineType.SPLIT)
				bLineSelect = objLine.getParent().getSelected();
			else
				bLineSelect = objLine.getSelected();
			if (bLineSelect) {
				c.setBackground(Constants.ALTERNATECLR);
			} else {
				c.setBackground(Color.WHITE);
			}
			return c;
		}
	}

	public class ActionRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JButton butAction = new JButton();
			butAction.setPreferredSize(new Dimension(40, 20));
			Constants.BudgetLineType enumType = (Constants.BudgetLineType) value;
			switch (enumType) {
			case PARENT:
				butAction.setText("Add");
				break;
			default:
				butAction.setText("Del");
			}
			butAction.addActionListener(new ActionListener () {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					System.err.println("clicked");
					
				}
				
			});
			return butAction;
		}
	}

	private JComboBox<String> boxAccounts;
	private RenderSelect objSelectRowRender = new RenderSelect();
	private SelectedCellRenderer objSelRender = new SelectedCellRenderer();
	private NonSelectedCellRenderer objNonSelRender = new NonSelectedCellRenderer();
	private MyCurrencyEditor objCurrencyEdit;
	private BudgetTableModel modModel;
	private String[] arrAccounts;

	public BudgetTable(BudgetTableModel modModelp, String[] arrAccountsp) {
		super(modModelp);
		modModel = modModelp;
		arrAccounts = arrAccountsp;
		boxAccounts = new JComboBox<String>(arrAccounts);
		objCurrencyEdit = new MyCurrencyEditor(modModel);
		this.setFillsViewportHeight(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellSelectionEnabled(true);
		/*
		 * Select
		 */
		TableColumn colSelect = this.getColumnModel().getColumn(0);
		colSelect.setPreferredWidth(25);
		colSelect.setMinWidth(25);
		colSelect.setCellEditor(new DefaultCellEditor(new JCheckBox()));
		colSelect.setCellRenderer(objSelectRowRender);
		/*
		 * category
		 */
		this.getColumnModel().getColumn(1).setResizable(false);
		this.getColumnModel().getColumn(1).setPreferredWidth(200);
		this.getColumnModel().getColumn(1).setMinWidth(200);
		this.getColumnModel().getColumn(1).setCellRenderer(objNonSelRender);
		/*
		 * Actual Amount
		 */
		colSelect = this.getColumnModel().getColumn(2);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setMinWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Pro Rata Actuals
		 */
		colSelect = this.getColumnModel().getColumn(3);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setMinWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Budget Amount
		 */
		colSelect = this.getColumnModel().getColumn(4);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setMinWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Total Budget Amount
		 */
		colSelect = this.getColumnModel().getColumn(5);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setMinWidth(50);
		colSelect.setCellRenderer(objNonSelRender);
		/*
		 * Annual Amount
		 */
		colSelect = this.getColumnModel().getColumn(6);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setMinWidth(50);
		colSelect.setCellRenderer(objSelRender);
		colSelect.setCellEditor(objCurrencyEdit);
		/*
		 * RPI
		 */
		colSelect = this.getColumnModel().getColumn(7);
		colSelect.setPreferredWidth(40);
		colSelect.setMinWidth(40);
		colSelect.setCellRenderer(objSelRender);
		colSelect.setCellEditor(new MyPercEditor());
//		 colSelect.setCellRenderer(objPerc);
		/*
		 * account
		 */
		this.getColumnModel().getColumn(8).setResizable(false);
		this.getColumnModel().getColumn(8).setPreferredWidth(200);
		this.getColumnModel().getColumn(8).setMinWidth(200);
		this.getColumnModel().getColumn(8)
				.setCellEditor(new DefaultCellEditor(boxAccounts));
		this.getColumnModel().getColumn(8).setCellRenderer(objNonSelRender);
		/*
		 * Account Amount
		 */
		colSelect = this.getColumnModel().getColumn(9);
		colSelect.setResizable(false);
		colSelect.setPreferredWidth(50);
		colSelect.setMinWidth(40);
		colSelect.setCellRenderer(objSelRender);
		colSelect.setCellEditor(objCurrencyEdit);
		/*
		 * pop up menu
		 */
		ActionListener mitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aeEvent) {
				String strAction = aeEvent.getActionCommand();
				int iRow = BudgetTable.this.getSelectedRow();
				if (strAction.contains(Constants.BUDGETMENU)) {
					modModel.setTotalBudget(iRow);
				} else if (strAction.contains(Constants.ACTUALMENU)) {
					modModel.setAnnualActual(iRow);
				} else if (strAction.contains(Constants.MIXEDMENU)) {
					modModel.setMixedAmount(iRow);
				} else if (strAction.contains(Constants.ADDSOURCE)) {
					modModel.addSourceLine (iRow);
				} else if (strAction.contains(Constants.DELETESOURCE)) {
					modModel.deleteSourceLine (iRow);
				}
				BudgetTable.this.modModel.fireTableDataChanged();
			}
		};
		menAmount = new JPopupMenu();
		mitBudget = new JMenuItem();
		mitBudget.addActionListener(mitListener);
		mitActuals = new JMenuItem();
		mitActuals.addActionListener(mitListener);
		mitMixed = new JMenuItem();
		mitMixed.addActionListener(mitListener);
		menAmount.add(mitBudget);
		menAmount.add(mitActuals);
		menAmount.add(mitMixed);
		menAction = new JPopupMenu();
		mitActionAdd = new JMenuItem(Constants.ADDSOURCE);
		mitActionAdd.addActionListener(mitListener);
		menAction.add(mitActionAdd);
		mitActionDel = new JMenuItem(Constants.DELETESOURCE);
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
		/*
		 * Header Colour
		 */
		JTableHeader objHeader = getTableHeader();
		objHeader.setBackground(Constants.TABLEHEADER);
		objHeader.setOpaque(false);
		objHeader.setFont(objHeader.getFont().deriveFont(Font.BOLD));
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
			BudgetLine objLine = modModel.getLine(iRow);
			// if we've clicked on a row in the amount col
			if ((iRow != -1) && (iCol == 6) && objLine.getParent().getSelected()) {
				mitBudget.setText(Constants.BUDGETMENU + " ("
						+ modModel.getTotalBudget(iRow) + ")");
				mitActuals.setText(Constants.ACTUALMENU + " ("
						+ modModel.getAnnualActual(iRow) + ")");
				mitMixed.setText(Constants.MIXEDMENU + " ("
						+ modModel.getMixedAmount(iRow) + ")");
				menAmount.show(me.getComponent(), me.getX(), me.getY());
			}
			else {
				if ((iRow!=-1) && (iCol > 7) && objLine.getParent().getSelected()) {
					if (objLine != null) {
						IncludedBudget objItem = objLine.getItem();
						if (objItem != null) {
							List<SourceItem> listSource = objItem.getSourceItems();
							if (listSource.size() <= 1) {
								int iPos = menAction.getComponentIndex(mitActionDel);
								if (iPos > -1)
									menAction.remove(mitActionDel);
							}
							else {
								int iPos = menAction.getComponentIndex(mitActionDel);
								if (iPos == -1)
									menAction.add(mitActionDel);
							}
							menAction.show(me.getComponent(), me.getX(), me.getY());
						}
						
					}
				}
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
