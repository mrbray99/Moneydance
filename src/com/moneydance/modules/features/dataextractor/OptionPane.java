package com.moneydance.modules.features.dataextractor;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.MRBLocale;
import com.moneydance.modules.features.mrbutil.MRBSelectPanel;

public class OptionPane extends JPanel {
	/*
	 * internal fields
	 */
	private List<Account> listCategories;
	private JCheckBox categories;
	private JCheckBox expenseCB;
	private JCheckBox incomeCB;
	private MRBLocale locale = Main.locale;
	private SelectionItem optionItem;
	private JCheckBox cheques;
	private JTextField fromChequeFld;
	private JTextField toChequeFld;
	private JCheckBox status;
	private JCheckBox cleared;
	private JCheckBox reconciling;
	private JCheckBox uncleared;
	private JCheckBox tagCB;
	private Integer catTextx;
	private Integer catTexty;
	private Integer tagTextx;
	private Integer tagTexty;
	private JLabel noTags;
	private JLabel noCategories;
	private ChoicesWindow optionListener;
	private OptionPane thispane;
	
	
	public OptionPane (ChoicesWindow optionListenerp, SelectionItem optionItemp) {
		super();
		optionListener = optionListenerp;
		thispane = this;
		optionItem = optionItemp;
		listCategories= optionItem.getCategoryMap();
	}
	public void build() {
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Option");
		GridBagLayout gbl_pan = new GridBagLayout();
		gbl_pan.columnWidths = new int[] { 100, 80, 100, 80, 100, 30 };
		setLayout(gbl_pan);
		setBorder(title);
		int ix=0;
		int iy=0;
		// 0,0
		JLabel selectCat= new JLabel(locale.getString(Constants.OPC_CATEGORY,"Select Categories :"));
		categories = new JCheckBox();
		categories.setSelected(optionItem.includeCategories());
		categories.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionItem.setCategories(((JCheckBox)e.getSource()).isSelected());
			}
		});

		incomeCB = new JCheckBox(locale.getString(Constants.OPC_INCOME,"Income "));
		incomeCB.setSelected(optionItem.includeIncome());
		incomeCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				incomeUpdated();
			}
		});
		expenseCB = new JCheckBox(locale.getString(Constants.OPC_EXPENSE,"Expenses "));
		expenseCB.setSelected(optionItem.includeExpense());
		expenseCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				expenseUpdated();
			}
		});
		JButton selectCatBtn = new JButton(locale.getString(Constants.OPC_SELECTBTN,"Select "));
		add(selectCat, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		// 1,0
		add(categories, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		// 2,0
		add(incomeCB, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		// 3,0
		add(expenseCB, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		// 4,0
		add(selectCatBtn, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		selectCatBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MRBSelectPanel selectPane = new MRBSelectPanel(Main.context.getCurrentAccountBook());
				if (incomeCB.isSelected())
					selectPane.SetType(AccountType.INCOME, true);
				if (expenseCB.isSelected())
					selectPane.SetType(AccountType.EXPENSE, true);
				selectPane.setSelected(listCategories);
				selectPane.display();
				optionAdded(selectPane);
			}
		});
		iy++;
		ix=2;
		//2,1
		setNumCats(ix,iy);
		catTextx = ix;
		catTexty = iy;
		iy++;
		ix=0;
		
		//0,2
		JLabel selectCheques = new JLabel(locale.getString(Constants.OPC_SELECTCHEQUE,"Select Cheques:"));
		cheques = new JCheckBox();
		cheques.setSelected(optionItem.includeCheques());
		cheques.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionItem.setCheques(((JCheckBox)e.getSource()).isSelected());
			}
		});
		JLabel fromChequeLbl = new JLabel(locale.getString(Constants.OPC_FROMCHEQUE,"From Cheque no: "));
		JLabel toChequeLbl = new JLabel(locale.getString(Constants.OPC_TOCHEQUE,"To Cheque no: "));
		fromChequeFld = new JTextField();
		fromChequeFld.setText(optionItem.getFromCheque());
		fromChequeFld.getDocument().addDocumentListener(new DocumentListener() {
			  @Override
			public void changedUpdate(DocumentEvent e) {
				updateFromCheque();
			  }
			  @Override
			public void removeUpdate(DocumentEvent e) {
				updateFromCheque();
			  }
			  @Override
			public void insertUpdate(DocumentEvent e) {
				updateFromCheque();
			  }


			});
		toChequeFld = new JTextField();
		toChequeFld.setText(optionItem.getToCheque());
		toChequeFld.getDocument().addDocumentListener(new DocumentListener() {
			  @Override
			public void changedUpdate(DocumentEvent e) {
				updateToCheque();
			  }
			  @Override
			public void removeUpdate(DocumentEvent e) {
				updateToCheque();
			  }
			  @Override
			public void insertUpdate(DocumentEvent e) {
				updateToCheque();
			  }
			});
		add(selectCheques, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//,2
		add(cheques, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//2,2
		add(fromChequeLbl, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//3,2
		add(fromChequeFld, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//4,2
		add(toChequeLbl, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//5,2
		add(toChequeFld, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		ix=0;
		iy++;
		//0,3
		status = new JCheckBox();
		status.setSelected(optionItem.includeStatus());
		status.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionItem.setStatus(((JCheckBox)e.getSource()).isSelected());
			}
		});
	JLabel statusLbl = new JLabel(locale.getString(Constants.OPC_STATUS, "Transaction Status:"));
		JLabel clearedLbl = new JLabel(locale.getString(Constants.OPC_CLEARED, "Cleared"));
		JLabel reconcilingLbl = new JLabel(locale.getString(Constants.OPC_RECONCILING, "Reconciling"));
		JLabel unclearedLbl = new JLabel(locale.getString(Constants.OPC_UNCLEARED, "Uncleared"));
		cleared = new JCheckBox();
		cleared.setSelected(optionItem.isCleared());
		cleared.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearedUpdated();
			}
		});
		reconciling = new JCheckBox();
		reconciling.setSelected(optionItem.isReconciling());
		reconciling.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reconcilingUpdated();
			}
		});
		uncleared = new JCheckBox();
		uncleared.setSelected(optionItem.isUncleared());
		uncleared.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				unclearedUpdated();
			}
		});
		add(statusLbl, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//1,3
		add(status, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//2,3
		add(clearedLbl, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//3,3
		add(cleared, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//4,3
		add(reconcilingLbl, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//5,3
		add(reconciling, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//6,3
		add(unclearedLbl, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//7,3
		add(uncleared, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		ix=0;
		iy++;
		//0,4
		JLabel selectTag= new JLabel(locale.getString(Constants.OPC_SELTAG,"Select Tags :"));
		tagCB = new JCheckBox();
		tagCB.setSelected(optionItem.getSelTag());
		tagCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tagUpdated();
			}
		});
		JButton tagBtn = new JButton(locale.getString(Constants.OPC_SELECTBTN,"Select "));
		tagBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionItem.setSelTag(true);
				TagSelectPane tagPane = new TagSelectPane(Main.context.getCurrentAccountBook(),optionItem.getTags());
				tagPane.display();
				tagsAdded(tagPane);
			}
		});
		add(selectTag, GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//1,4
		add(tagCB, GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		//2,4
		add(tagBtn, GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		iy++;
		ix=2;
		//2,5
		setNumTags(ix,iy);
		tagTextx = ix;
		tagTexty = iy;
	}
   public void addDeleteBtn() {
			JButton deleteBtn = new JButton(locale.getString(Constants.OPC_DELETEBTN,"Delete Option "));
			int iy=6;
			int ix=6;
			//6,6
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(null, locale.getString(Constants.OPC_AREYOUSURE,"Are you sure?"),
							Constants.OPC_DELETEBTN, JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION){
						optionListener.deletePane(thispane);
					}
	
				}
			});
			add(deleteBtn, GridC.getc(ix,iy).insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
	}


	private void setNumCats(int ix,int iy) {
		if (noCategories == null) {
			noCategories = new JLabel();
			add(noCategories, GridC.getc(ix,iy).fillx().colspan(2).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
		}
		String catSel = locale.getString(Constants.OPC_CATEGORIES,"Categories selected :");
		if (listCategories.size() < 1)
			catSel += " " + locale.getString(Constants.OPC_CATALL,"All");
		else
			catSel += " " + listCategories.size();
		noCategories.setText(catSel);
		noCategories.revalidate();
	}
	public void incomeUpdated() {
		     optionItem.setIncome(incomeCB.isSelected());
		  }
	public void expenseUpdated() {
	     optionItem.setExpense(expenseCB.isSelected());
	  }
	public void updateFromCheque() {
		     optionItem.setFromCheque(fromChequeFld.getText());
		  }
	public void updateToCheque() {
		     optionItem.setToCheque(toChequeFld.getText());
		  }
	private void optionAdded(MRBSelectPanel selectPane){
		listCategories = selectPane.getSelected();
		if (listCategories != null)
			optionItem.resetCategories(listCategories);
		setNumCats(catTextx, catTexty);
	}
	public void clearedUpdated() {
	     optionItem.setCleared(cleared.isSelected());
	  }
	public void reconcilingUpdated() {
	     optionItem.setReconciling(reconciling.isSelected());
	  }
	public void unclearedUpdated() {
	     optionItem.setUncleared(uncleared.isSelected());
	  }
	public void tagUpdated() {
	     optionItem.setSelTag(tagCB.isSelected());
	  }
	public void tagsAdded(TagSelectPane tagPanep){
		optionItem.resetTags(tagPanep.getSelected());
		setNumTags(tagTextx, tagTexty);
	}
	private void setNumTags(int ix,int iy){
		if (noTags == null) {
			noTags = new JLabel();
			add(noTags, GridC.getc(ix,iy).fillx().colspan(2).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
		}
		String tagSel = locale.getString(Constants.OPC_TAGS,"Tags selected :");
		int listsize = optionItem.getTags().size();
		if (listsize < 1)
			tagSel += " " + locale.getString(Constants.OPC_CATALL,"All");
		else
			tagSel += " " + listsize;
		noTags.setText(tagSel);
		noTags.revalidate();
	}
	public SelectionItem getResult() {
		return optionItem;
	}
}
