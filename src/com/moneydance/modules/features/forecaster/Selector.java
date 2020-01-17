package com.moneydance.modules.features.forecaster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.infinitekind.moneydance.model.AccountListener;
import com.moneydance.awt.GridC;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBInconsistencyException;
import com.moneydance.modules.features.mrbutil.MRBPreferences;
import com.moneydance.modules.features.mrbutil.MRBReport;
import com.moneydance.modules.features.mrbutil.MRBReportViewer;

public class Selector extends JFrame implements ChangeListener, AccountListener{
	/*
	 * Moneydance variables
	 */
	/*
	 * panels
	 * 
	 */
	private JPanel panMain;
	private JScrollPane spMain;
	private JPanel panTop;
	private JTabbedPane panTabs;
	private AccountsPane panBanks;
	private AccountsPane panCredit;
	private AccountsPane panLoan;
	private AccountsPane panAsset;
	private AccountsPane panLiability;
	private BudgetPane panBudget;
	private JPanel panRemTran;
	private JPanel panAcctSec;
	private ReminderPane panReminders;
	private TransferPane panTransfers;
	private SecurityPane panSecurities;
	private JPanel panButtons;
	/*
	 * screen sizes
	 */
	private int iFRAMEWIDTH;
	private int iFRAMEDEPTH;
	private List<PanelSize> listSizeListeners;
	/*
	 * fields
	 */
	private JLabel lblYears = new JLabel("Years to forecast");
	private JLabel lblFileName = new JLabel("File Name");
	private JLabel lblMonths = new JLabel("Number years in months");
	private JLabel lblType = new JLabel("Year Type");
	private JLabel lblRPI = new JLabel("Default RPI");
	private JLabel lblRPINote = new JLabel("<html>Used to increase/decrease each value at the end of the year.<br>You can add to this on each line.</html>");
	private JLabel lblActual = new JLabel("Use Actuals up to:");
	private JLabel lblToday = new JLabel("or end of last month");
	private JDateField jdtActual = new JDateField(Main.getCdate());
	private JFormattedTextField txtRPI = new JFormattedTextField();
	private JComboBox<Integer> boxYears = new JComboBox<Integer>();
	private JComboBox<Integer> boxMonths = new JComboBox<Integer>();
	private JComboBox<String> boxStart = new JComboBox<String>(new String[] {"Fiscal","Calendar"});
	private JCheckBox chkToday = new JCheckBox();
	private JButton btnCashReport = new JButton("Cash Flow Report");
	private JButton btnCashGraph = new JButton("Cash Flow Graph");
	private JButton btnNetReport = new JButton("Net Worth Report");
	private JButton btnNetGraph = new JButton("Net Worth Graph");
	private JButton btnClose = new JButton("Close");
	private boolean bStartError = false;
	private boolean bActualError = false;
	private boolean bRPIError = false;
	private boolean bYearError = false;
	private boolean bMonthError = false;
	private Border bdValid;
	private Border bdInvalid= BorderFactory.createLineBorder(Color.RED, 1);;

	/*
	 * File data
	 */
	private JButton btnChoose = new JButton(); 
	private JButton btnLoad = new JButton("Load File");
	private JButton btnSave = new JButton("Save Parameters");
	private JTextField txtFileName = new JTextField();
	private JFileChooser objFileChooser = null;
	private File fParameters;
	/*
	 * Preferences
	 */
	private MRBPreferences prefGlobal;
	private String strFileName;
	/*
	 * Parameter data
	 */
	private ForecastParameters objParams;
	private CashFlowReport objCashFlowReport;
	private MRBReport objReport;
	private MRBReportViewer objViewer;
	private MRBDebug objDebug;
	
	public Selector () {
		prefGlobal = MRBPreferences.getInstance();
		objDebug = MRBDebug.getInstance();
		objDebug.debug("Selector","Constructor",MRBDebug.SUMMARY, "Selector Invoked");
		setLayout(new BorderLayout());
		strFileName = prefGlobal.getString(Constants.PROGRAMNAME+"."+Constants.Field.FILENAME, Constants.FILENAME);
		try {
			objParams = new ForecastParameters ();
		}
		catch (MRBInconsistencyException e){
			e.printStackTrace();
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (IllegalAccessException | UnsupportedLookAndFeelException
				| InstantiationException | ClassNotFoundException e) {
		}
		/*
		 * listen for changes to accounts
		 */
		Main.getContxt().getCurrentAccountBook().addAccountListener(this);
		objFileChooser = new JFileChooser();
		listSizeListeners = new ArrayList<PanelSize>();
		panMain = new JPanel(new BorderLayout());
		panTop = new JPanel(new GridBagLayout());
		panTabs = new JTabbedPane();
		panBanks = new AccountsPane(objParams, AccountType.BANK);
		panCredit = new AccountsPane(objParams,AccountType.CREDIT_CARD);
		panLoan = new AccountsPane(objParams,AccountType.LOAN);
		panAsset = new AccountsPane(objParams,AccountType.ASSET);
		panLiability = new AccountsPane(objParams,AccountType.LIABILITY);
		panBudget = new BudgetPane(objParams);
		panReminders = new ReminderPane(objParams);
		panTransfers = new TransferPane(objParams);
		panSecurities = new SecurityPane(objParams);
		panRemTran = new JPanel(new GridLayout(2,1));
		panRemTran.add(panReminders);
		panRemTran.add(panTransfers);
		panAcctSec = new JPanel(new GridLayout(3,2));
		panAcctSec.add(panBanks);
		panAcctSec.add(panCredit);
		panAcctSec.add(panSecurities);
		panAcctSec.add(panLoan);
		panAcctSec.add(panAsset);
		panAcctSec.add(panLiability);
		
		panTabs.addTab("Accounts/Securities", panAcctSec);
		panTabs.addTab("Budgets",panBudget);
		panTabs.addTab("Reminders/Transfers",panRemTran);
		/*
		 * listen for changes in selected tab
		 */
		panTabs.addChangeListener(this);
		panButtons = new JPanel(new GridLayout());
		/*
		 * handle screen size changes
		 */
		setScreenPreferences();
		addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				JFrame panScreen = (JFrame) arg0.getSource();
				Dimension objDimension = panScreen.getSize();
				updatePrefInt(Constants.Field.MAINWIDTH, objDimension.width);
				updatePrefInt(Constants.Field.MAINDEPTH, objDimension.height);
				setScreenPreferences();
				prefGlobal.isDirty();
				resize(iFRAMEWIDTH, iFRAMEDEPTH);
				spMain.revalidate();
				panMain.revalidate();
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// not needed
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// not needed
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// not needed
			}

		}); 
		/*
		 * set up top of screen
		 */
		int iRow = 0;
		int iCol = 0;
		/*
		 * Row 1 file name
		 */
		panTop.add(lblFileName,
				 GridC.getc(iCol, iRow).east().insets(5,5,5,5));
		txtFileName.setColumns(20);
		txtFileName.setText(strFileName);
		txtFileName.setToolTipText("Enter the file name (without extension), or click on button to the right to find the file");
		txtFileName.getDocument().addDocumentListener(new DocumentListener(){
			  @Override
			public void changedUpdate(DocumentEvent e) {
				    strFileName = txtFileName.getText();
					prefGlobal.put(Constants.PROGRAMNAME+"."+Constants.Field.FILENAME, strFileName);
				  }
				  @Override
				public void removeUpdate(DocumentEvent e) {
				    strFileName = "";
					prefGlobal.put(Constants.PROGRAMNAME+"."+Constants.Field.FILENAME, strFileName);
				  }
				  @Override
				public void insertUpdate(DocumentEvent e) {
					strFileName = txtFileName.getText();
					prefGlobal.put(Constants.PROGRAMNAME+"."+Constants.Field.FILENAME, strFileName);
				  }
		});
		iCol++;
		panTop.add(txtFileName, GridC.getc(iCol, iRow).fillx().west().colspan(3)
				.insets(5,5,5,5));

		Image img = getIcon("Search-Folder-icon.jpg");
		if (img == null)
			btnChoose.setText("Find");
		else
			btnChoose.setIcon(new ImageIcon(img));
		iCol +=3;
		panTop.add(btnChoose, GridC.getc(iCol, iRow)
				.insets(5,5,5,5));
		btnChoose.setBorder(javax.swing.BorderFactory
				.createLineBorder(panTop.getBackground()));
		btnChoose.setToolTipText("Click to open file dialog to find required file");
		btnChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});
		iCol++;
		btnLoad.setToolTipText("Click to load parameters from the file");
		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					objParams.reloadData(Constants.DataTypes.PARAMETERS);
				}
				catch (MRBInconsistencyException e2){
					e2.printStackTrace();
				}
				Selector.this.revalidate();
			}
		});
		panTop.add(btnLoad, GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		btnSave.setToolTipText("Click to save parameters to the file");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		panTop.add(btnSave, GridC.getc(iCol, iRow).insets(5,5,5,5));
		iRow++;
		iCol = 0;
		/*
		 * row 2 RPI
		 */
		panTop.add(lblRPI,GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		bdValid = txtRPI.getBorder();
		txtRPI.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				Double dTemp;
				JFormattedTextField ftf = (JFormattedTextField) input;
				String text = ftf.getText();
				try {
					dTemp = Double.parseDouble(text);
				} catch (NumberFormatException pe) {
					if (text.endsWith("%"))
						text = text.substring(0, text.length() - 1);
					try {
						dTemp = Double.parseDouble(text);
					} catch (NumberFormatException pe2) {
						JFrame fTemp = new JFrame();
						JOptionPane.showMessageDialog(fTemp,
								"Invalid RPI amount");
						bRPIError = true;
						txtRPI.setBorder(bdInvalid);
						return true;
					}
				}
				if (dTemp < -100.00 || dTemp > 100.00) {
					JFrame fTemp = new JFrame();
					JOptionPane.showMessageDialog(fTemp,
							"RPI amount must be within -100 to +100");
					bRPIError = true;
					txtRPI.setBorder(bdInvalid);
					return true;
				}
				try {
					objParams.setRPI(Double.parseDouble(text));
					txtRPI.setValue(String.format("%1$,.2f%%",
							objParams.getRPI()));
				} catch (NumberFormatException pe) {
				}
				bRPIError = false;
				txtRPI.setBorder(bdValid);
				return true;
			}
		});
		txtRPI.setValue(String.format("%1$,.2f%%", objParams.getRPI()));
		txtRPI.setColumns(10);
		txtRPI.setToolTipText("<html>RPI must be between -100 and +100.<br>  It is used to increase the amount in subsequent years</html>");
		panTop.add(txtRPI,GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		panTop.add(lblRPINote,GridC.getc(iCol, iRow).colspan(5).insets(5,5,5,5));
		iRow++;
		iCol=0;
		/*
		 * Row 3 date fields
		 */
		panTop.add(lblActual,GridC.getc(iCol, iRow).insets(5,5,5,5)); // 3,0
		iCol++;
		jdtActual.setDateInt(objParams.getActualDate());
		jdtActual.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent jcField) {
				JDateField jdtTemp = (JDateField) jcField;
				if(((objParams.getType() == Constants.Type.CALENDAR) && jdtTemp.getDateInt()<Main.getCalendarDate()) ||
					((objParams.getType() == Constants.Type.FISCAL) && jdtTemp.getDateInt()<Main.getFiscalDate())) {	
						JFrame fTemp = new JFrame();
						JOptionPane
								.showMessageDialog(fTemp,
										"Actuals date must be greater than or equal to report start date");
						bActualError = true;
						jdtActual.setBorder(bdInvalid);
						return true;
					}
				
				objParams.setActualDate(jdtTemp);
				bActualError = false;
				jdtActual.setBorder(bdValid);
				jdtActual.setDateInt(jdtTemp.getDateInt());
				return true;
			}
		});
		panTop.add(jdtActual,GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		panTop.add(lblYears, GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		for (int i=0;i<25;i++)
			boxYears.addItem(new Integer(i+1));
		boxYears.setSelectedIndex(objParams.getNumYears()-1);
		panTop.add(boxYears, GridC.getc(iCol, iRow).insets(5,5,5,5));
		boxYears.addItemListener(new ItemListener() {
			@Override
		     public void itemStateChanged(ItemEvent e) {
			       if (e.getStateChange() == ItemEvent.SELECTED) {
						@SuppressWarnings("unchecked")
						JComboBox<Integer> boxTemp = (JComboBox<Integer>)e.getSource();
						if (boxTemp.getSelectedIndex()+1 < objParams.getMonths()) {
							JFrame fTemp = new JFrame();
							JOptionPane
									.showMessageDialog(fTemp,
											"Number of Years must be greater or equal to months");
							bYearError = true;
							boxYears.setBorder(bdInvalid);
						}
						else {
							objParams.setNumYears(boxTemp.getSelectedIndex()+1);
							bYearError = false;
							boxYears.setBorder(bdValid);
						}	
			       }
			}
		});
		iRow++;
		iCol=0;
		panTop.add(lblToday,GridC.getc(iCol, iRow).insets(5,5,5,5));
		chkToday.setSelected(objParams.getActualsLastMonth());
		iCol++;
		chkToday.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				JCheckBox chkTemp = (JCheckBox) ce.getSource();
				objParams.setActualsLastMonth(chkTemp.isSelected());
			}
		});
		panTop.add(chkToday,GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		panTop.add(lblMonths, GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		for (int i=0;i<6;i++)
			boxMonths.addItem(new Integer(i));
		boxMonths.setSelectedIndex(objParams.getMonths()); 
		panTop.add(boxMonths, GridC.getc(iCol, iRow).insets(5,5,5,5));
		boxMonths.addItemListener(new ItemListener() {
			@Override
		    public void itemStateChanged(ItemEvent e) {
			       if (e.getStateChange() == ItemEvent.SELECTED) {
						@SuppressWarnings("unchecked")
						JComboBox<Integer> boxTemp = (JComboBox<Integer>)e.getSource();
						if (boxTemp.getSelectedIndex() > objParams.getNumYears()) {
							JFrame fTemp = new JFrame();
							JOptionPane
									.showMessageDialog(fTemp,
											"Number of Months must not be greater than the Number of Years");
							bMonthError = true;
							boxMonths.setBorder(bdInvalid);
						}
						else {
							objParams.setMonths(boxTemp.getSelectedIndex());
							bMonthError = false;
							boxMonths.setBorder(bdValid);
						}
			       }
			}
		});
		iCol++;
		panTop.add(lblType, GridC.getc(iCol, iRow).insets(5,5,5,5));
		iCol++;
		if (objParams.getType() == Constants.Type.FISCAL) 
			boxStart.setSelectedIndex(0);
		else
			boxStart.setSelectedIndex(1);
		boxStart.addItemListener(new ItemListener() {
			@Override
		    public void itemStateChanged(ItemEvent e) {
			    if (e.getStateChange() == ItemEvent.SELECTED) {
					@SuppressWarnings("unchecked")
					JComboBox<String> boxTemp = (JComboBox<String>)e.getSource();
					if (boxTemp.getSelectedIndex() == 0)
						objParams.setType(Constants.Type.FISCAL);
					else
						objParams.setType(Constants.Type.CALENDAR);
			    }
			}
		});
		panTop.add(boxStart, GridC.getc(iCol, iRow).insets(5,5,5,5));
		panMain.add(panTop,BorderLayout.NORTH);
		panMain.add(panTabs, BorderLayout.CENTER);
		/*
		 * Buttons
		 */
		btnCashReport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bStartError || bActualError || bRPIError || bYearError || bMonthError) {
					JFrame fTemp = new JFrame();
					JOptionPane
							.showMessageDialog(fTemp,
									"Please correct errors first");
					return;
				}
				cashReport();
			}
		});
		btnCashGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bStartError || bActualError || bRPIError || bYearError || bMonthError) {
					JFrame fTemp = new JFrame();
					JOptionPane
							.showMessageDialog(fTemp,
									"Please correct errors first");
					return;
				}
				cashGraph();
			}
		});
		btnNetReport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bStartError || bActualError || bRPIError || bYearError || bMonthError) {
					JFrame fTemp = new JFrame();
					JOptionPane
							.showMessageDialog(fTemp,
									"Please correct errors first");
					return;
				}
				networthReport();
			}
		});
		btnNetGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bStartError || bActualError || bRPIError || bYearError || bMonthError) {
					JFrame fTemp = new JFrame();
					JOptionPane
							.showMessageDialog(fTemp,
									"Please correct errors first");
					return;
				}
				networthGraph();
			}
		});
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bStartError || bActualError || bRPIError || bYearError || bMonthError) {
					goAway();
				}
				else {
					if (objParams.isDirty ()) {
						JFrame fTemp = new JFrame();
						int iResult = JOptionPane
								.showConfirmDialog(fTemp,
										"The parameters have been changed.  Do you wish to save them?");
						if (iResult == JOptionPane.YES_OPTION) {
							try {
								objParams.save();
							}
							catch (MRBInconsistencyException eSave) {
								eSave.printStackTrace();
							}
						}
					}
				}
				goAway();
			}
		});

		panButtons.add(btnCashReport);
		panButtons.add(btnCashGraph);
		panButtons.add(btnNetReport);
		panButtons.add(btnNetGraph);
		panButtons.add(btnClose);
		panMain.add(panButtons,BorderLayout.SOUTH);
		spMain = new JScrollPane(panMain);
		add(spMain);
		panMain.setPreferredSize(new Dimension(iFRAMEWIDTH-20, iFRAMEDEPTH-40));
		pack();
		setVisible(true);
	}
	/*
	 * called when the extension is re-entered
	 */
	public void reenter() {
		objParams.reloadData(Constants.DataTypes.ACCOUNTS);
		objParams.reloadData(Constants.DataTypes.BUDGETS);
		objParams.reloadData(Constants.DataTypes.REMINDERS);
		objParams.reloadData(Constants.DataTypes.PARAMETERS);
		objParams.fireParametersChanged(Constants.ParameterType.ACCOUNTS);
		objParams.fireParametersChanged(Constants.ParameterType.BUDGETS);
		objParams.fireParametersChanged(Constants.ParameterType.REMINDERS);
		panMain.invalidate();
		
	}
	/*
	 * Select a file
	 */
	private void chooseFile() {
		objFileChooser.setFileFilter(new FileNameExtensionFilter("Forecast Parameters File",Constants.EXTENSION,
				Constants.EXTENSION.toUpperCase()));
		objFileChooser.setCurrentDirectory(Main.getContxt()
				.getCurrentAccountBook().getRootFolder());
		int iReturn = objFileChooser.showDialog(this, "Select File");
		if (iReturn == JFileChooser.APPROVE_OPTION) {
			fParameters = objFileChooser.getSelectedFile();
			txtFileName.setText(fParameters.getName().substring(0,
					fParameters.getName().lastIndexOf('.')));
			strFileName = txtFileName.getText();
		}
		updatePrefString(Constants.Field.FILENAME,strFileName);
		prefGlobal.isDirty();
	}

	private Image getIcon(String icon) {
		try {
			ClassLoader cl = getClass().getClassLoader();
			java.io.InputStream in = cl
					.getResourceAsStream("/com/moneydance/modules/features/forecaster/"
							+ icon);
			if (in != null) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
				byte buf[] = new byte[256];
				int n = 0;
				while ((n = in.read(buf, 0, buf.length)) >= 0)
					bout.write(buf, 0, n);
				return Toolkit.getDefaultToolkit().createImage(
						bout.toByteArray());
			}
		} catch (Throwable e) {
		}
		return null;
	}
	public void goAway() {
		setVisible(false);
		dispose();
	}
	/*
	 * Report/graph generation
	 */
	public void cashReport() {
		objDebug.debug("Selector","cashReport",MRBDebug.SUMMARY, "Cash Flow Report Invoked");
		if (objParams.isDirty ()) {
			JFrame fTemp = new JFrame();
			int iResult = JOptionPane
					.showConfirmDialog(fTemp,
							"The parameters have been changed.  Do you wish to save them?");
			if (iResult == JOptionPane.YES_OPTION) {
				try {
					objParams.save();
				}
				catch (MRBInconsistencyException eSave) {
					eSave.printStackTrace();
				}
			}
			if (iResult == JOptionPane.CANCEL_OPTION)
				return;
		}
		try {
			objParams.Generate();
		}
		catch (DataErrorException e) {
			JOptionPane.showMessageDialog(null, "There are errors in the data.  Please correct before generating reports");
			return;
		}
		try {
			objCashFlowReport = new CashFlowReport(objParams);
		}
		catch (MRBInconsistencyException e){
			e.printStackTrace();
		}
		objReport = objCashFlowReport.getReport();
		JFrame frame = new JFrame("MoneyDance Cash Flow Report");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		objViewer = new MRBReportViewer(objReport);
		objViewer.setReport(objReport);
		frame.getContentPane().add(objViewer);
		frame.setTitle("Report");
		// Display the window.
		frame.pack(); 
		frame.setVisible(true);		//TODO cash report
	}
	public void cashGraph() {
		//TODO cash graph
	}
	public void networthReport() {
		//TODO net worth report
	}
	public void networthGraph() {
		//TODO net worth graph
	}
	public void save() {
		try {
			objParams.save();
		}
		catch (MRBInconsistencyException e){
			e.printStackTrace();
		}
	}
	/* **********************************************************************************
	 * Listeners 
	 ***********************************************************************************
	 */
	/*
	 * Tabbed Pane Changes
	 */
	@Override
	public void stateChanged (ChangeEvent e){
		if (!(e.getSource() instanceof JTabbedPane))
			return;
		JTabbedPane panTab = (JTabbedPane) e.getSource();
		switch (panTab.getSelectedIndex()) {
		case 0:
			panBanks.tabEntered();
			panCredit.tabEntered();
			panLoan.tabEntered();
			panAsset.tabEntered();
			panLiability.tabEntered();
			panSecurities.tabEntered();
			break;
		case 1:
			panBudget.tabEntered();
			break;
		case 2:
			panReminders.tabEntered();
			panTransfers.tabEntered();
		}
	}
	/*
	 * Account Changes
	 */
	@Override
	public void accountAdded(Account acctParent, Account acctNew) {
		objParams.reloadData(Constants.DataTypes.ACCOUNTS);
		objParams.fireParametersChanged(Constants.ParameterType.ACCOUNTS);
		panMain.invalidate();
	}
	@Override
	public void accountDeleted(Account acctParent, Account acctNew) {
		objParams.reloadData(Constants.DataTypes.ACCOUNTS);
		objParams.checkIncludedAccounts();
		objParams.fireParametersChanged(Constants.ParameterType.ACCOUNTS);
		panMain.invalidate();
	}
	@Override
	public void accountBalanceChanged(Account acctNew) {
		// nothing to do 		
	}
	@Override
	public void accountModified(Account acctNew) {
		objParams.fireParametersChanged(Constants.ParameterType.ACCOUNTS);
		panMain.invalidate();
	}
	
	/*
	 * preferences
	 */
	private void setScreenPreferences() {
		iFRAMEWIDTH = prefGlobal.getInt(Constants.PROGRAMNAME+"."+Constants.Field.MAINWIDTH,Constants.MAINFRAMEWIDTH);
		iFRAMEDEPTH = prefGlobal.getInt(Constants.PROGRAMNAME+"."+Constants.Field.MAINDEPTH,Constants.MAINFRAMEDEPTH);
		
	}

	private void updatePrefString (Constants.Field strField, String strValue) {
		prefGlobal.put(Constants.PROGRAMNAME+"."+strField, strValue);
	}
	private void updatePrefInt (Constants.Field strField, int iValue) {
		prefGlobal.put(Constants.PROGRAMNAME+"."+strField, iValue);
	}
	
	public void addSizeListener(PanelSize objSize) {
		listSizeListeners.add(objSize);
	}
	public void removeSizeListener(PanelSize objSize){
		listSizeListeners.remove(objSize);
	}
	@Override
	public void resize(int iWidth,int iDepth){
		int iMainWidth = Math.max(iWidth-20,  Constants.MAINMINFRAMEWIDTH);
		int iMainDepth = Math.max(iDepth-40, Constants.MAINMINFRAMEDEPTH);
		spMain.setPreferredSize(new Dimension(iMainWidth, iMainDepth));

	}
}
