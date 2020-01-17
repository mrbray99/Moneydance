/*
 * Copyright (c) 2014, Michael Bray. All rights reserved.
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
package com.moneydance.modules.features.dataextractor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/*
 * Main window of budget parameters. Contains a row for each BudgetLine.  BudgetLines are
 * obtained from the Budget Parameters
 */
import javax.swing.filechooser.FileNameExtensionFilter;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.Account.AccountType;
import com.infinitekind.util.StringUtils;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.awt.GridC;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBGUI;
import com.moneydance.modules.features.mrbutil.MRBLocale;
import com.moneydance.modules.features.mrbutil.MRBPreferences;
import com.moneydance.modules.features.mrbutil.MRBPreferences2;
import com.moneydance.modules.features.mrbutil.MRBReport;
import com.moneydance.modules.features.mrbutil.MRBReportViewer;
import com.moneydance.modules.features.mrbutil.MRBSelectPanel;

public class ChoicesWindow extends JFrame implements DeleteOption {
	/*
	 * Moneydance objects
	 */
	FeatureModuleContext context;


	/*
	 * Identifies the budget being worked on
	 */
	public ReportParameters reportParms;
	public ReportListExtend budgetList;
	public Parameters params;
	public List<SelectionItem> listSelections;
	public List<OptionPane> listOptionPanes;
	private List<Account> selectedAccts;
	public boolean error = false;
	private MRBLocale locale;
	private MRBReportViewer viewer=null;
	private Report mainReport = null;
	private MRBReport report=null; 
	private MRBDebug objDebug = MRBDebug.getInstance();
	public String budget;
	public String fileName;
	/*
	 * Screen fields
	 */
	private JButton generateBtn;
	private JButton closeBtn;
	private JButton saveBtn;
	private JButton chooseBtn;
	private JButton addOptionBtn;
	private JRadioButton andBtn;
	private JRadioButton orBtn;
	private ButtonGroup btnGroup;
	private JTextField fileNameField;
	@SuppressWarnings("unused")
	private LookAndFeel previousLF;
	private JFileChooser fileChooser = null;
	private File parameters;
	/*
	 * date fields
	 */
	private int fiscalYear;
	private int year;
	private int fiscalMonth;
	private int fiscalDay;
	private JDateField startDateFld;
	private JDateField endDateFld;
	private JDateField fiscalStartFld;
	private JDateField fiscalEndFld;
	/*
	 * Preferences and window sizes
	 */
	private Preferences pref;
	private Preferences root;
	private MRBPreferences2 preferences;
	public int iFRAMEWIDTH = Constants.FRAMEWIDTH;
	public int iFRAMEDEPTH = Constants.FRAMEDEPTH;
	private int iTOPWIDTH;
	private int iTABWIDTH;
	private int iTABDEPTH;
	private int iOPTIONDEPTH;
	/*
	 * Panels
	 */
	private JPanel screenPan;
	private JPanel filePan;
	private JPanel formatPan;
	private JPanel selectPan;
	private JPanel optionsPan;
	private JScrollPane optionsSp;
	private JTabbedPane tabsPan;
	private ChoicesWindow thisWindow;

	public ChoicesWindow(Main extension) {
		thisWindow = this;
		previousLF = UIManager.getLookAndFeel();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (IllegalAccessException | UnsupportedLookAndFeelException
				| InstantiationException | ClassNotFoundException e) {
		}
		fileChooser = new JFileChooser(); //TODO: update for MAC
		locale = Main.locale;
		context = extension.getUnprotectedContext();
		MRBPreferences2.loadPreferences(context);
		preferences = MRBPreferences2.getInstance();

		/*
		 * set up dates for periods (Fiscal Start date is set when BudgetExtend
		 * is created)
		 */
		String strFiscal = extension.up
				.getSetting(UserPreferences.FISCAL_YEAR_START_MMDD);
		fiscalYear = StringUtils.isBlank(strFiscal) ? 101 : Integer
				.parseInt(strFiscal);
		Calendar gc = Calendar.getInstance();
		year = gc.get(Calendar.YEAR);
		fiscalMonth = fiscalYear / 100;
		fiscalDay = fiscalYear - fiscalMonth * 100;
		Calendar fiscalStartCal = Calendar.getInstance();
		Calendar fiscalEndCal = Calendar.getInstance();
		fiscalStartCal.set(year, fiscalMonth - 1, fiscalDay);
		Calendar todayCal = new GregorianCalendar();
		if (fiscalStartCal.after(todayCal))
			fiscalStartCal.set(year - 1, fiscalMonth - 1, fiscalDay);
		fiscalStartFld = new JDateField(Main.cdate);
		fiscalEndFld = new JDateField(Main.cdate);
		startDateFld = new JDateField(Main.cdate);
		endDateFld = new JDateField(Main.cdate);
		fiscalStartFld.setDate(fiscalStartCal.getTime());
		fiscalEndCal.setTime(fiscalStartFld.parseDate());
		fiscalEndCal.add(Calendar.YEAR, 1);
		fiscalEndFld.setDate(fiscalEndCal.getTime());
		fiscalEndFld.decrementDate();
		/*
		 * set up report objects
		 */
		reportParms = new ReportParameters(context);
		fileName = reportParms.getFile();
		if (fileName.equals(""))
			fileName = Constants.DEFAULTFILE;
		params = new Parameters(fileName, fiscalStartFld,
				fiscalEndFld);
		listSelections = params.getSelectionList();
		selectedAccts = params.getAccounts();
		listOptionPanes = new ArrayList<>();
		/*
		 * start of screen
		 */
		tabsPan = new JTabbedPane();
		screenPan = new JPanel();
		this.getContentPane().add(screenPan);
		screenPan.setLayout(new BoxLayout(screenPan,BoxLayout.Y_AXIS));
		screenPan.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				JPanel panScreen = (JPanel) arg0.getSource();
				Dimension screenDimension = panScreen.getSize();
				updatePreferences(screenDimension);
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
		setPreferences(); // set the screen sizes
		filePan = new JPanel(new GridBagLayout());
		int ix = 0;
		int iy = 0;
		/*
		 * Line 0
		 * parameter file
		 */
		JLabel fileNameLbl = new JLabel(locale.getString(Constants.CWC_PARAMETERS,"Parameters : "));
		filePan.add(fileNameLbl,  GridC.getc(ix++,iy).fillx().east().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		fileNameField = new JTextField();
		fileNameField.setColumns(20);
		fileNameField.setText(fileName);
		fileNameField.setToolTipText(locale.getString(Constants.CWTT_FILENAME,"<html>Enter a file name without an extension or <br>click on the button to the right to find a file</html>"));
		filePan.add(fileNameField, GridC.getc(ix,iy).colspan(3).insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		ix=+4;
		chooseBtn = new JButton();
		Image img = getIcon("Search-Folder-icon.jpg");
		if (img == null) {
			chooseBtn.setText(locale.getString(Constants.CWBTN_FIND,"Find"));
			chooseBtn.setBorder(javax.swing.BorderFactory.createLineBorder(filePan
					.getBackground()));
		}
		else {
			chooseBtn.setIcon(new ImageIcon(img));
			chooseBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		}
		chooseBtn.setToolTipText(locale.getString(Constants.CWTT_BTNCHOOSE,"Click to browse for file"));
		filePan.add(chooseBtn, GridC.getc(ix,iy).insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		chooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});
		ix=0;
		iy++;
		/*
		 * Button 1 - Save
		 */
		saveBtn = new JButton(locale.getString(Constants.CWBTN_SAVE,"Save Parameters"));
		saveBtn.setToolTipText(locale.getString(Constants.CWTT_SAVEPARM,"Click to save parameters.  You will be asked for a file name"));
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		filePan.add(saveBtn, GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		/*
		 * Button 2 - Generate values
		 */
		generateBtn = new JButton(locale.getString(Constants.CWBTN_GENERATE,"Generate"));
		generateBtn.setToolTipText(locale.getString(Constants.CWTT_GENERATE,"Click to generate the report"));
		generateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generate();
			}
		});
		filePan.add(generateBtn, GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		/*
		 * Button 3 - Close
		 */
		closeBtn = new JButton(locale.getString(Constants.CWBTN_CLOSE,"Close"));
		closeBtn.setToolTipText(locale.getString(Constants.CWTT_CLOSE,"<html>Click to close the extension.  <br>If there are unsaved changes to the parameters, <br>you will be asked if you wish to continue</html>"));
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		filePan.add(closeBtn, GridC.getc(ix,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		filePan.setPreferredSize(new Dimension(iTOPWIDTH, Constants.TOPDEPTH));

		screenPan.add(filePan);
		/*
		 * Format panel
		 */
		formatPan = new formatTab();

		/*
		 * Options panel
		 */
		// 0,0
		selectPan = new selectTab ();
//		optionsSp = new JScrollPane();
//		optionsSp.setViewportView(selectPan);

		tabsPan.addTab("Select",selectPan);
		tabsPan.addTab("Format", formatPan);
		screenPan.add(tabsPan);

		/*
		 * Set dirty back to false so real changes are caught
		 */
		params.resetDirty();
		getContentPane().setPreferredSize(
				new Dimension(iFRAMEWIDTH, iFRAMEDEPTH));
		this.pack();
	}


	/*
	 * Refresh all fields from parameters
	 */
	private void refreshData()
	{	
		startDateFld.setDateInt(params.getStartDate());
		endDateFld.setDateInt(params.getEndDate());
		if (params.isUnionTypeAnd())
			andBtn.setSelected(true);
		if (!params.isUnionTypeAnd())
			orBtn.setSelected(true);
		listOptionPanes= new ArrayList<>();
		for (SelectionItem line : listSelections){
			OptionPane pane = new OptionPane(thisWindow,line);
			pane.build();
			listOptionPanes.add(pane);
		}
		int ix=0;
		int iy=0;
		optionsPan.removeAll();
		optionsPan.add(addOptionBtn, GridC.getc(1,0).insets(0,10,0,0));
		optionsSp.remove(optionsPan);
		for (OptionPane pane : listOptionPanes){
			optionsPan.add(pane,GridC.getc(ix,iy++).insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
		}
		optionsSp.setViewportView(optionsPan);

	}
	/*
	 * Select a file
	 */
	private void chooseFile() {
		//TODO: update for MAC
		fileChooser
				.setFileFilter(new FileNameExtensionFilter(Constants.PARAMETEREXTENSION, Constants.PARAMETEREXTENSION.toUpperCase()));
		fileChooser.setCurrentDirectory(context.getCurrentAccountBook()
				.getRootFolder());
		int iReturn = fileChooser.showDialog(this, "Select File");
		if (iReturn == JFileChooser.APPROVE_OPTION) {
			parameters = fileChooser.getSelectedFile();
			fileNameField.setText(parameters.getName().substring(0,
					parameters.getName().lastIndexOf('.')));
		}
		if (!fileNameField.getText().equals(fileName)) {
			params.refreshData(fileNameField.getText(),
					fiscalStartFld, fiscalEndFld);
			startDateFld.setDateInt(params.getStartDate());
			endDateFld.setDateInt(params.getEndDate());
			refreshData();
			screenPan.revalidate();
			fileName = fileNameField.getText();
			reportParms.setFile(fileName);
			reportParms.saveParams();
		}
	}

	private Image getIcon(String icon) {
		try {
			ClassLoader cl = getClass().getClassLoader();
			java.io.InputStream in = cl
					.getResourceAsStream("/com/moneydance/modules/features/dataextractor/"
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

	/*
	 * Button actions
	 */

	/*
	 * Generate Report
	 */
	private void generate() {
		mainReport = new Report(params);
		report = mainReport.getReport();
		JFrame frame = new JFrame(params.getReportName());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		viewer = new MRBReportViewer(report);
		viewer.setReport(report);
		frame.getContentPane().add(viewer);
		if (Main.imgIcon != null)
			frame.setIconImage(Main.imgIcon);
		// Display the window.
		frame.pack(); 
		frame.setVisible(true);
	}

	/*
	 * Close the window checking for data changes first. Give user chance to
	 * save the data
	 */
	public void close() {
		if (params.isDirty()) {
			JFrame fTemp = new JFrame();
			int iResult = JOptionPane
					.showConfirmDialog(fTemp,
							"The parameters have been changed.  Do you wish to save them?");
			if (iResult == JOptionPane.YES_OPTION) {
				String strFileName = fileNameField.getText();
				if (!strFileName.equals(Constants.CANCELLED)) {
					strFileName = askForFileName(strFileName);
					params.saveParams(strFileName);
				}
			}
		}
		if (mainReport !=null)
			mainReport.close();
		if (viewer != null)
			viewer.close();
		screenPan.setVisible(false);
		Main.open = false;
		this.dispose();
	}

	/*
	 * save parameters at user request
	 */
	private void save() {
		String strFileName = fileNameField.getText();
		strFileName = askForFileName(strFileName);
		if (!strFileName.equals(Constants.CANCELLED)) {
			params.saveParams(strFileName);
			fileNameField.setText(strFileName);
		}
	}


	/*
	 * ask for parameters file name
	 */
	private String askForFileName(String fileNamep) {
		String fileName;
		JPanel panInput = new JPanel(new GridBagLayout());
		JLabel lblType = new JLabel("Enter File Name:");
		fileName = fileNamep;
		panInput.add(lblType, GridC.getc(0,0).insets(10, 10, 10, 10));
		JTextField txtType = new JTextField();
		txtType.setText(fileName);
		txtType.setColumns(20);
		panInput.add(txtType, GridC.getc(1,0).insets(10, 10, 10, 10));
		while (true) {
			int result = JOptionPane.showConfirmDialog(null, panInput,
					"Save Parameters", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				if (txtType.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							"File Name can not be blank");
					continue;
				}
				fileName = txtType.getText();
				break;
			}
			if (result == JOptionPane.CANCEL_OPTION) {
				fileName = Constants.CANCELLED;
				break;
			}
		}
		return fileName;

	}

	void goAway() {
		close();
		setVisible(false);
		dispose();
	}

	/*
	 * preferences
	 */
	private void setPreferences() {
		iFRAMEWIDTH = preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH,-1);
		iFRAMEDEPTH = preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEDEPTH,-1);
		if (iFRAMEWIDTH < 0 || iFRAMEDEPTH < 0) {
			root = Preferences.userRoot();
			pref = root
					.node("com.moneydance.modules.features.dataextractor.choiceswindow");
			iFRAMEWIDTH = pref.getInt(Constants.CRNTFRAMEWIDTH, Constants.FRAMEWIDTH);
			iFRAMEDEPTH = pref.getInt(Constants.CRNTFRAMEDEPTH, Constants.FRAMEDEPTH);
		}
		iTOPWIDTH = iFRAMEWIDTH-100;
		iTABWIDTH = iFRAMEWIDTH-100;
		iTABDEPTH = iFRAMEDEPTH - Constants.TOPDEPTH;
		iOPTIONDEPTH = 300;
	}

	private void updatePreferences(Dimension dim) {
		preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH, dim.width);
		preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEDEPTH, dim.height);
		preferences.isDirty();
	}

	private class selectTab extends JPanel {
		private int ix =0;
		private int iy=0;
		private MRBGUI gui = new MRBGUI();
		private AccountType [] acctType;
		private String [] acctTypeCodes;
		private String [] acctTypeNames;
		private List<AccountType> selectedTypes;
		private int assetIndex= 0;
		private int bankIndex= 0;
		private int cardIndex= 0;
		private int investmentIndex= 0;
		private int liabilityIndex= 0;
		private int loanIndex= 0;
		private int securityIndex= 0;

		protected selectTab() {
			this.setLayout(new GridBagLayout());
			acctType = AccountType.values();
			acctTypeCodes = new String[acctType.length];
			acctTypeNames = new String[acctType.length];
			for (int i=0; i< acctType.length;i++) {
				if (acctType[i].equals(AccountType.ASSET))
					assetIndex = i;
				if (acctType[i].equals(AccountType.BANK))
					bankIndex = i;
				if (acctType[i].equals(AccountType.CREDIT_CARD	))
					cardIndex = i;
				if (acctType[i].equals(AccountType.INVESTMENT))
					investmentIndex = i;
				if (acctType[i].equals(AccountType.LIABILITY))
					liabilityIndex = i;
				if (acctType[i].equals(AccountType.LOAN))
					loanIndex = i;
				if (acctType[i].equals(AccountType.SECURITY))
					securityIndex = i;
				acctTypeCodes[i] = "acct_type"+acctType[i].code()+"s";
				acctTypeNames[i] = gui.getStr(acctTypeCodes[i]);
			}
			selectedTypes = params.getTypes();
			/*
			 * Start Date
			 */
			ix=0;
			iy=0;
			// 0,0
			JLabel datesStartLbl = new JLabel(locale.getString(Constants.CWC_DATESTART,"Dates - Start :"));
			add(datesStartLbl, GridC.getc(ix++,iy).east().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			startDateFld.setDateInt(params.getStartDate());
			startDateFld.setDisabledTextColor(Color.BLACK);
			// 1,0
			startDateFld.setToolTipText(locale.getString(Constants.CWTT_STARTDATE,"Enter the start date for the report"));
			add(startDateFld, GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			startDateFld.setInputVerifier(new InputVerifier() {
				@Override
				public boolean verify(JComponent jcField) {
					JDateField jdtStart = (JDateField) jcField;
					Calendar dtStart = Calendar.getInstance();
					Calendar dtEnd = Calendar.getInstance();
					dtStart.setTime(jdtStart.getDate());
					dtEnd.setTime(endDateFld.getDate());
					if (!dtEnd.after(dtStart)) {
						JFrame fTemp = new JFrame();
						JOptionPane.showMessageDialog(fTemp,
								locale.getString(Constants.CWER_ENDDATE,"End Date must be after Start Date"));
						return false;
					}
					params.setStartDate(jdtStart.getDateInt());
					return true;
				}
			});
			/*
			 * End Date
			 */
			JLabel endlbl = new JLabel(locale.getString(Constants.CWC_DATEEND,"End :"));
			// 2,0
			add(endlbl,GridC.getc(ix++,iy).east().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));

			endDateFld.setDateInt(params.getEndDate());
			endDateFld.setDisabledTextColor(Color.BLACK);
			endDateFld.setToolTipText(locale.getString(Constants.CWTT_ENDDATE,"Enter the end date for the report"));
			// 3,0
			add(endDateFld, GridC.getc(ix,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			endDateFld.setInputVerifier(new InputVerifier() {
				@Override
				public boolean verify(JComponent jcField) {
					JDateField jdtEnd = (JDateField) jcField;
					Calendar dtStart = Calendar.getInstance();
					Calendar dtEnd = Calendar.getInstance();
					dtStart.setTime(startDateFld.getDate());
					dtEnd.setTime(jdtEnd.getDate());
					if (!dtEnd.after(dtStart)) {
						JFrame fTemp = new JFrame();
						JOptionPane.showMessageDialog(fTemp,
								locale.getString(Constants.CWER_ENDDATE,"End Date must be after start date"));
						return false;
					}
					params.setEndDate(jdtEnd.getDateInt());
					return true;
				}
			});
			// 0,1
			iy=1;
			ix =0;
			JLabel accountsLbl = new JLabel(locale.getString(Constants.CW_ACCOUNTS,"Select Accounts"));
			this.add(accountsLbl,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			final JCheckBox type0 = new JCheckBox(acctTypeNames[assetIndex]);
			type0.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox temp = (JCheckBox)e.getSource();
					if (temp.isSelected())
						params.addType(acctType[assetIndex]);
					else
						params.removeType(acctType[assetIndex]);
				}
			});
			if(selectedTypes.contains(acctType[assetIndex]))
				type0.setSelected(true);
			else
				type0.setSelected(false);
			//1,1
			this.add(type0,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			final JCheckBox type1 = new JCheckBox(acctTypeNames[bankIndex]);
			type1.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox temp = (JCheckBox)e.getSource();
					if (temp.isSelected())
						params.addType(acctType[bankIndex]);
					else
						params.removeType(acctType[bankIndex]);
				}
			});
			if(selectedTypes.contains(acctType[bankIndex]))
				type1.setSelected(true);
			else
				type1.setSelected(false);
			//2,1
			this.add(type1,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			
			final JCheckBox type2 = new JCheckBox(acctTypeNames[cardIndex]);
			type2.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox temp = (JCheckBox)e.getSource();
					if (temp.isSelected())
						params.addType(acctType[cardIndex]);
					else
						params.removeType(acctType[cardIndex]);
				}
			});
			if(selectedTypes.contains(acctType[cardIndex]))
				type2.setSelected(true);
			else
				type2.setSelected(false);
			//3,1
			this.add(type2,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			final JCheckBox type3 = new JCheckBox(acctTypeNames[investmentIndex]);
			type3.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox temp = (JCheckBox)e.getSource();
					if (temp.isSelected())
						params.addType(acctType[investmentIndex]);
					else
						params.removeType(acctType[investmentIndex]);
				}
			});
			if(selectedTypes.contains(acctType[investmentIndex]))
				type3.setSelected(true);
			else
				type3.setSelected(false);
			//4,1
			this.add(type3,GridC.getc(ix,iy++).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			final JCheckBox type4 = new JCheckBox(acctTypeNames[liabilityIndex]);
			type4.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox temp = (JCheckBox)e.getSource();
					if (temp.isSelected())
						params.addType(acctType[liabilityIndex]);
					else
						params.removeType(acctType[liabilityIndex]);
				}
			});
			if(selectedTypes.contains(acctType[liabilityIndex]))
				type4.setSelected(true);
			else
				type4.setSelected(false);
			ix=1;
			//1,2
			this.add(type4,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			final JCheckBox type5 = new JCheckBox(acctTypeNames[loanIndex]);
			type5.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox temp = (JCheckBox)e.getSource();
					if (temp.isSelected())
						params.addType(acctType[loanIndex]);
					else
						params.removeType(acctType[loanIndex]);
				}
			});
			if(selectedTypes.contains(acctType[loanIndex]))
				type5.setSelected(true);
			else
				type5.setSelected(false);
			//2,2
			this.add(type5,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			final JCheckBox type6 = new JCheckBox(acctTypeNames[securityIndex]);
			type6.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox temp = (JCheckBox)e.getSource();
					if (temp.isSelected()) {
						params.addType(acctType[securityIndex]);
						params.addType(acctType[investmentIndex]);
						type3.setSelected(true);
					}
					else
						params.removeType(acctType[securityIndex]);
				}
			});
			if(selectedTypes.contains(acctType[securityIndex])){
				type6.setSelected(true);
				type3.setSelected(true);
			}
			else
				type6.setSelected(false);
			//3,2
			this.add(type6,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			JButton selectAccts = new JButton(locale.getString(Constants.OPC_SELECTBTN,"Select "));
			selectAccts.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MRBSelectPanel selectPane = new MRBSelectPanel(Main.context.getCurrentAccountBook());
					if (type0.isSelected())
						selectPane.SetType(acctType[assetIndex],true);
					if (type1.isSelected())
						selectPane.SetType(acctType[bankIndex],true);
					if (type2.isSelected())
						selectPane.SetType(acctType[cardIndex],true);
					if (type3.isSelected())
						selectPane.SetType(acctType[investmentIndex],true);
					if (type4.isSelected())
						selectPane.SetType(acctType[liabilityIndex],true);
					if (type5.isSelected())
						selectPane.SetType(acctType[loanIndex],true);
					if (type6.isSelected())
						selectPane.SetType(acctType[securityIndex],true);
					selectPane.setSelected(selectedAccts);
					selectPane.display();
					accountsAdded(selectPane);
				}
			});
			ix++;
			// 5,2
			add(selectAccts, GridC.getc(ix,iy++).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
		
			JTextArea introOptions = new JTextArea(2,50);
			introOptions.setText(locale.getString(Constants.CW_OPT_TEXT,"Options"));
			introOptions.setWrapStyleWord(true);
			introOptions.setLineWrap(true);
			introOptions.setOpaque(false);
			introOptions.setEditable(false);
			introOptions.setFocusable(false);
			ix=0;
			//0,3
			this.add(introOptions,GridC.getc(ix,iy++).colspan(5).insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			JLabel selectOptions = new JLabel(locale.getString(Constants.CW_OPT_SEL,"Select Options"));
			ix=0;
			// 0,4
			this.add(selectOptions,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			andBtn = new JRadioButton();
			andBtn.setText(locale.getString(Constants.CW_OPT_AND,"And"));
			andBtn.setActionCommand("And");
			andBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed (ActionEvent e) {
					if (e.getActionCommand().equals("And"))
						params.setUnionTypeAnd(true);
					if (e.getActionCommand().equals("Or"))
						params.setUnionTypeAnd(false);
				}
			});
			if (params.isUnionTypeAnd())
				andBtn.setSelected(true);
			orBtn = new JRadioButton();
			orBtn.setText(locale.getString(Constants.CW_OPT_OR,"Or"));
			if (!params.isUnionTypeAnd())
				orBtn.setSelected(true);
			orBtn.setActionCommand("Or");
			orBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed (ActionEvent e) {
					if (e.getActionCommand().equals("And"))
						params.setUnionTypeAnd(true);
					if (e.getActionCommand().equals("Or"))
						params.setUnionTypeAnd(false);
				}
			});
			btnGroup = new ButtonGroup();
			btnGroup.add(andBtn);
			btnGroup.add(orBtn);
			// 1,4
			this.add(andBtn,GridC.getc(ix++,iy).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			//2,4
			this.add(orBtn,GridC.getc(ix,iy++).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));

			addOptionBtn = new JButton("+");
			addOptionBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SelectionItem newOption = new SelectionItem();
					OptionPane newOptionPane = new OptionPane(thisWindow,newOption);
					newOptionPane.build();
					JOptionPane.showMessageDialog(null, newOptionPane,"Information",JOptionPane.INFORMATION_MESSAGE);
					optionAdded(newOptionPane);
				}
			});
			ix=5;
			//5,5
			objDebug.debug("selectTab","selectTab",MRBDebug.DETAILED,"addOption x="+ix+" iy="+iy);
			this.add(addOptionBtn, GridC.getc(ix,iy++).insets(0,10,0,0));
			/*
			 * Options
			 */

			for (SelectionItem line : listSelections){
				OptionPane pane = new OptionPane(thisWindow,line);
				pane.build();
				pane.addDeleteBtn();
				listOptionPanes.add(pane);
			}
			optionsPan = new JPanel();
			optionsPan.setLayout(new BoxLayout(optionsPan, BoxLayout.PAGE_AXIS));
			for (OptionPane pane:listOptionPanes){
				optionsPan.add(pane);
			}
			optionsSp = new JScrollPane();
			optionsSp.setViewportView(optionsPan);
			optionsSp.setPreferredSize(new Dimension(iTABWIDTH,iOPTIONDEPTH));
			ix=0;
			//0,6
			objDebug.debug("selectTab","selectTab",MRBDebug.DETAILED,"optionsSp x="+ix+" iy="+iy);
			this.add(optionsSp,GridC.getc(ix,iy).colspan(6).insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			
		}
		/*
		 * after account select button, retrieve new selections add to parameters
		 */
		private void accountsAdded(MRBSelectPanel pane){
			selectedAccts = pane.getSelected();
			params.setAccounts(selectedAccts);
		}
		/*
		 * after new option pane added, add to list of panes, update parameters
		 */
		private void optionAdded(OptionPane options) {
			SelectionItem newOptionItem = options.getResult();
			listSelections.add(newOptionItem);
			params.setSelectionList(listSelections);
			listOptionPanes.add(options);
			optionsPan.add(options);
			options.addDeleteBtn();
			optionsPan.revalidate();
			optionsSp.revalidate();
			screenPan.revalidate();
			
		}

	}
	/*
	 * called when user clicks on Delete button on object pane, removes the pane from the lists
	 * If niumber of panes is zero, reset options panel as remove doesn't work
	 */
	@Override
	public void deletePane(OptionPane pane){
		SelectionItem item = pane.getResult();
		listSelections.remove(item);
		listOptionPanes.remove(pane);
		if (listOptionPanes.isEmpty()){
			optionsPan = new JPanel();
			optionsPan.setLayout(new BoxLayout(optionsPan, BoxLayout.PAGE_AXIS));
			optionsSp.setViewportView(optionsPan);
			optionsSp.setPreferredSize(new Dimension(iTABWIDTH,iOPTIONDEPTH));
		}
		else
			optionsPan.remove(pane);
		params.setSelectionList(listSelections);
		optionsPan.revalidate();
		optionsSp.revalidate();
		screenPan.revalidate();
	}
	private class formatTab extends JPanel {
		CalculatePane calculatePan;
		JScrollPane calculateSp;
		ColumnPane columnPan;
		JScrollPane columnSp;
		SortPane sortPan;
		JScrollPane sortSp;
		int panelWidth;
		int panelDepth;
		int ix=0;
		int iy=0;
		protected formatTab() {
			setLayout(new GridBagLayout());
			setPreferredSize(new Dimension(iTOPWIDTH, Constants.TOPDEPTH));
			GridBagLayout layout = new GridBagLayout();
			setLayout(layout);
			panelWidth = iTABWIDTH/2 - 50;
			panelDepth = iOPTIONDEPTH/2 - 50;
			calculateSp = new JScrollPane ();
			calculateSp.setViewportView(calculatePan);
			calculateSp.setPreferredSize(new Dimension(panelWidth,panelDepth));
			add(calculateSp, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			columnSp = new JScrollPane ();
			columnPan = new ColumnPane();
			columnSp.setViewportView(columnPan);
			columnSp.setPreferredSize(new Dimension(panelWidth,panelDepth));
			add(columnSp, GridC.getc(ix,iy++).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			ix=0;
			sortSp = new JScrollPane ();
			sortPan = new SortPane();
			sortSp.setViewportView(sortPan);
			sortSp.setPreferredSize(new Dimension(panelWidth,panelDepth));
			add(sortSp, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
		}
	}


}
