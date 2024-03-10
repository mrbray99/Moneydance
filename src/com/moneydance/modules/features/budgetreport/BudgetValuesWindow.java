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
package com.moneydance.modules.features.budgetreport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
/*
 * Main window of budget parameters. Contains a row for each BudgetLine.  BudgetLines are
 * obtained from the Budget Parameters
 */
import javax.swing.filechooser.FileNameExtensionFilter;

import com.infinitekind.util.StringUtils;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.apps.md.view.MoneydanceUI;
import com.moneydance.awt.GridC;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.MRBPreferences2;
import com.moneydance.modules.features.mrbutil.MRBReport;
import com.moneydance.modules.features.mrbutil.MRBReportViewer;

public class BudgetValuesWindow extends JFrame {
	/*
	 * Moneydance objects
	 */
	FeatureModuleContext context;
	/*
	 * Identifies the budget being worked on
	 */
	public static ReportParameters objReportParms;
	public static BudgetListExtend objBudgetList;
	public static BudgetExtend objBudget;
	public static BudgetParameters objParams;
	public boolean bError = false;
	private MRBReportViewer objViewer=null;
	private BudgetReport objBudgetReport = null;
	private MRBReport objReport=null; 
	private JList<String> listIncomeMissing;
	private JList<String> listExpenseMissing;
	private JList<String> listIncomeSelect;
	private JList<String> listExpenseSelect;
	private MyListModel mylmIncomeMissing;
	private MyListModel mylmIncomeSelect;
	private MyListModel mylmExpenseMissing;
	private MyListModel mylmExpenseSelect;
	public String strBudget;
	public String strFileName;
	/*
	 * Screen fields
	 */
	private JButton btnGenerate;
	private JButton btnClose;
	private JButton btnSave;
	private JButton btnChoose;
	private JComboBox<String> boxBudget;
	private JCheckBox chkRoll;
	private JTextField txtFileName;
	private MoneydanceUI mdGUI;
	private com.moneydance.apps.md.controller.Main mdMain;
	private JButton helpBtn;
	@SuppressWarnings("unused")
	private LookAndFeel previousLF;
	private JFileChooser fileChooser;
	private File fParameters;
	/*
	 * date fields
	 */
	private int iFiscalYear;
	private int iYear;
	private int iFiscalMonth;
	private int iFiscalDay;
	private JDateField jdtStartDate;
	private JDateField jdtEndDate;
	private JDateField jdtFiscalStart;
	private JDateField jdtFiscalEnd;
	/*
	 * Preferences and window sizes
	 */
	private Preferences objPref;
	private Preferences objRoot;
	private MRBPreferences2 objPreferences;
	public int iFRAMEWIDTH = Constants.FRAMEWIDTH;
	public int iFRAMEDEPTH = Constants.FRAMEDEPTH;
	private int iTOPWIDTH;
	private int iMIDWIDTH;
	private int iMIDDEPTH;
	private int iPANELWIDTH;
	private int iPANELHEIGHT;
	/*
	 * Colour Pickers
	 */
	private Color ccHeadersBG;
	private Color ccBudgetBG;
	private Color ccActualBG;
	private Color ccPositiveBG;
	private Color ccNegativeBG;
	private Color ccPositiveFG;
	private Color ccNegativeFG;
	/*
	 * Panels
	 */
	private JPanel panScreen;
	private JPanel panTop;
	private JPanel panMid;
	private JScrollPane spExpenseMissing;
	private JScrollPane spExpenseSelected;
	private JScrollPane spIncomeMissing;
	private JScrollPane spIncomeSelected;
	private JButton btnESelect;
	private JButton btnEDeselect;
	private JButton btnISelect;
	private JButton btnIDeselect;

	public BudgetValuesWindow(Main extension) {
		mdMain = com.moneydance.apps.md.controller.Main.mainObj;
		mdGUI = mdMain.getUI();
		fileChooser = new JFileChooser();
		context = extension.getUnprotectedContext();
		MRBPreferences2.loadPreferences(context);
		objPreferences = MRBPreferences2.getInstance();
		/*
		 * set up dates for periods (Fiscal Start date is set when BudgetExtend
		 * is created)
		 */
		String strFiscal = extension.up
				.getSetting(UserPreferences.FISCAL_YEAR_START_MMDD);
		iFiscalYear = StringUtils.isBlank(strFiscal) ? 101 : Integer
				.parseInt(strFiscal);
		Calendar gc = Calendar.getInstance();
		iYear = gc.get(Calendar.YEAR);
		iFiscalMonth = iFiscalYear / 100;
		iFiscalDay = iFiscalYear - iFiscalMonth * 100;
		Calendar dtFiscalStart = Calendar.getInstance();
		Calendar dtFiscalEnd = Calendar.getInstance();
		dtFiscalStart.set(iYear, iFiscalMonth - 1, iFiscalDay);
		Calendar dtToday = new GregorianCalendar();
		if (dtFiscalStart.after(dtToday))
			dtFiscalStart.set(iYear - 1, iFiscalMonth - 1, iFiscalDay);
		jdtFiscalStart = new JDateField(Main.cdate);
		jdtFiscalEnd = new JDateField(Main.cdate);
		jdtStartDate = new JDateField(Main.cdate);
		jdtEndDate = new JDateField(Main.cdate);
		jdtFiscalStart.setDate(dtFiscalStart.getTime());
		dtFiscalEnd.setTime(jdtFiscalStart.parseDate());
		dtFiscalEnd.add(Calendar.YEAR, 1);
		jdtFiscalEnd.setDate(dtFiscalEnd.getTime());
		jdtFiscalEnd.decrementDate();
		/*
		 * set up budget objects
		 * 
		 * First get last budget/file used
		 */
		objReportParms = new ReportParameters(context);
		strBudget = objReportParms.getBudget();
		strFileName = objReportParms.getFile();
		if (strFileName.isEmpty())
			strFileName = Constants.DEFAULTFILE;
		objParams = new BudgetParameters(context, strFileName, jdtFiscalStart,
				jdtFiscalEnd);
		objBudgetList = new BudgetListExtend(context);
		objBudget = objBudgetList.getBudget(strBudget);
		if (objBudget == null){
			JFrame fTemp = new JFrame();
			JOptionPane.showMessageDialog(fTemp,
					"No Budgets have been declared for this File");
			bError = true;
			return;
		}
		objReportParms.setBudget(objBudget.getName());
		ccHeadersBG = objParams.getColourHeaders();
		ccBudgetBG = objParams.getColourBudget();
		ccActualBG = objParams.getColourActual();
		ccPositiveBG = objParams.getColourPositive();
		ccNegativeBG = objParams.getColourNegative();
		ccPositiveFG = objParams.getColourFGPositive();
		ccNegativeFG = objParams.getColourFGNegative();
		/*
		 * set up lists for scroll panes
		 */
		mylmIncomeMissing = new MyListModel(objParams, Constants.INCOME_SCREEN,
				Constants.MISSING);
		mylmExpenseMissing = new MyListModel(objParams,
				Constants.EXPENSE_SCREEN, Constants.MISSING);
		mylmIncomeSelect = new MyListModel(objParams, Constants.INCOME_SCREEN,
				Constants.SELECTED);
		mylmExpenseSelect = new MyListModel(objParams,
				Constants.EXPENSE_SCREEN, Constants.SELECTED);
		listIncomeMissing = new JList<>(mylmIncomeMissing);
		listExpenseMissing = new JList<>(mylmExpenseMissing);
		listIncomeSelect = new JList<>(mylmIncomeSelect);
		listExpenseSelect = new JList<>(mylmExpenseSelect);
		/*
		 * start of screen
		 */
		panScreen = new JPanel();
		this.add(panScreen);
		panScreen.setLayout(new BorderLayout());
		panScreen.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				JPanel panScreen = (JPanel) arg0.getSource();
				Dimension objDimension = panScreen.getSize();
				updatePreferences(objDimension);
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
		/*
		 * Top panel
		 */
		panTop = new JPanel();
		panTop.setPreferredSize(new Dimension(iTOPWIDTH, Constants.TOPDEPTH));
		GridBagLayout gbl_panTop = new GridBagLayout();
		gbl_panTop.columnWidths = new int[] { 100, 80, 100, 80, 100, 30 };
		panTop.setLayout(gbl_panTop);

		// Budget
		JLabel lblAccountsName = new JLabel("Budget:");
		panTop.add(lblAccountsName, GridC.getc(0,0).fillx().east().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		// Select Budget
		objBudgetList = new BudgetListExtend(context);
		String[] strNames = objBudgetList.getBudgetNames();
		boxBudget = new JComboBox<>(strNames);
		boxBudget.setToolTipText("Select the Moneydance Budget you wish to report against");
		boxBudget.addActionListener(e -> {
            JComboBox<String> cbBudgetT = ((JComboBox<String>) e
                    .getSource());
            objBudget.refreshData(objBudgetList
                    .refreshData((String) (cbBudgetT.getSelectedItem())));
            objReportParms.setBudget(objBudget.getName());
            objReportParms.saveParams();
        });
		panTop.add(boxBudget,  GridC.getc(1,0).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		/*
		 * parameter file
		 */
		JLabel lblFileName = new JLabel("Parameters : ");
		panTop.add(lblFileName,  GridC.getc(0,1).fillx().east().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		txtFileName = new JTextField();
		txtFileName.setColumns(20);
		txtFileName.setText(strFileName);
		txtFileName.setToolTipText("<html>Enter a file name without an extension or <br>click on the button to the right to find a file</html>");
		panTop.add(txtFileName, GridC.getc(1,1).fillx().west().colspan(3).insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		btnChoose = new JButton();
		Image img = getIcon("Search-Folder-icon.jpg");
		if (img == null) {
			btnChoose.setText("Find");
			btnChoose.setBorder(javax.swing.BorderFactory.createLineBorder(panTop
					.getBackground()));
		}
		else {
			btnChoose.setIcon(new ImageIcon(img));
			btnChoose.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		}
		btnChoose.setToolTipText("Click to browse for file");
		panTop.add(btnChoose, GridC.getc(4,1).insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		btnChoose.addActionListener(e -> chooseFile());
		/*
		 * Start Date
		 */
		JLabel lblDatesStart = new JLabel("Dates - Start :");
		panTop.add(lblDatesStart, GridC.getc(0,2).east().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		jdtStartDate.setDateInt(objParams.getStartDate());
		jdtStartDate.setDisabledTextColor(Color.BLACK);
		jdtStartDate.setToolTipText("Enter the start date for the report");
		panTop.add(jdtStartDate, GridC.getc(1,2).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		jdtStartDate.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent jcField) {
				JDateField jdtStart = (JDateField) jcField;
				Calendar dtStart = Calendar.getInstance();
				Calendar dtEnd = Calendar.getInstance();
				dtStart.setTime(jdtStart.getDate());
				dtEnd.setTime(jdtEndDate.getDate());
				if (!dtEnd.after(dtStart)) {
					JFrame fTemp = new JFrame();
					JOptionPane.showMessageDialog(fTemp,
							"End Date must be after Start Date");
					return false;
				}
				objParams.setStartDate(jdtStart.getDateInt());
				return true;
			}
		});
		/*
		 * End Date
		 */
		JLabel lblEnd = new JLabel("End :");
		panTop.add(lblEnd,GridC.getc(2,2).east().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		jdtEndDate.setDateInt(objParams.getEndDate());
		jdtEndDate.setDisabledTextColor(Color.BLACK);
		jdtEndDate.setToolTipText("Enter the end date for the report");
		panTop.add(jdtEndDate, GridC.getc(3,2).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		jdtEndDate.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent jcField) {
				JDateField jdtEnd = (JDateField) jcField;
				Calendar dtStart = Calendar.getInstance();
				Calendar dtEnd = Calendar.getInstance();
				dtStart.setTime(jdtStartDate.getDate());
				dtEnd.setTime(jdtEnd.getDate());
				if (!dtEnd.after(dtStart)) {
					JFrame fTemp = new JFrame();
					JOptionPane.showMessageDialog(fTemp,
							"End Date must be after start date");
					return false;
				}
				objParams.setEndDate(jdtEnd.getDateInt());
				return true;
			}
		});
		/*
		 * Roll Up
		 */
		JLabel lblRoll = new JLabel("Roll Up? :");
		panTop.add(lblRoll, GridC.getc(4,2).east().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		chkRoll = new JCheckBox();
		chkRoll.setSelected(objParams.getRollup());
		chkRoll.setToolTipText("Click if you want actuals rolled up into parent categories");
		panTop.add(chkRoll, GridC.getc(5,2).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		/*
		 * Colour Choosers
		 */
		Border brdButtons = new LineBorder(Color.BLACK,1);
		JLabel lblBackground = new JLabel("Background");
		panTop.add(lblBackground, GridC.getc(2,3).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JLabel lblForeground = new JLabel("Text");
		panTop.add(lblForeground, GridC.getc(3,3).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JLabel lblColours = new JLabel("Report Colours:");
		panTop.add(lblColours, GridC.getc(0,4).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JLabel lblHeaders = new JLabel("Headers");
		panTop.add(lblHeaders, GridC.getc(1,4).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JButton btnHeaderColour = new JButton("           ");
		btnHeaderColour.setBackground(ccHeadersBG);
		btnHeaderColour.setContentAreaFilled(false);
		btnHeaderColour.setOpaque(true);
		btnHeaderColour.setBorder(brdButtons);
		btnHeaderColour.setToolTipText("Click to select background colour for the header lines");
		btnHeaderColour.addActionListener(e -> {
            JButton btnTemp = (JButton) e.getSource();
            ccHeadersBG = JColorChooser.showDialog(panTop,
                    "Choose Header Colour", ccHeadersBG);
            btnTemp.setBackground(ccHeadersBG);
            objParams.setColourHeaders(ccHeadersBG);
        });
		panTop.add(btnHeaderColour, GridC.getc(2,4).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		JLabel lblBudget = new JLabel("Budget Lines");
		panTop.add(lblBudget, GridC.getc(1,5).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JButton btnBudgetColour = new JButton("           ");
		btnBudgetColour.setBackground(ccBudgetBG);
		btnBudgetColour.setContentAreaFilled(false);
		btnBudgetColour.setOpaque(true);
		btnBudgetColour.setBorder(brdButtons);
		btnBudgetColour.setToolTipText("Click to select background colour for the budget lines");
		btnBudgetColour.addActionListener(e -> {
            JButton btnTemp = (JButton) e.getSource();
            ccBudgetBG = JColorChooser.showDialog(panTop,
                    "Choose Budget Line Colour", ccBudgetBG);
            btnTemp.setBackground(ccBudgetBG);
            objParams.setColourBudget(ccBudgetBG);
        });
		panTop.add(btnBudgetColour, GridC.getc(2,5).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		JLabel lblActual = new JLabel("Actual Lines");
		panTop.add(lblActual, GridC.getc(1,6).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JButton btnActualColour = new JButton("           ");
		btnActualColour.setBackground(ccActualBG);
		btnActualColour.setContentAreaFilled(false);
		btnActualColour.setOpaque(true);
		btnActualColour.setBorder(brdButtons);
		btnActualColour.setToolTipText("Click to select background colour for the actuals lines");
		btnActualColour.addActionListener(e -> {
            JButton btnTemp = (JButton) e.getSource();
            ccActualBG = JColorChooser.showDialog(panTop,
                    "Choose Actual Line Colour", ccActualBG);
            btnTemp.setBackground(ccActualBG);
            objParams.setColourActual(ccActualBG);
        });
		panTop.add(btnActualColour, GridC.getc(2,6).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		JLabel lblPositive = new JLabel("Positive Values");
		panTop.add(lblPositive, GridC.getc(1,7).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JButton btnPositiveColour = new JButton("           ");
		btnPositiveColour.setBackground(ccPositiveBG);
		btnPositiveColour.setContentAreaFilled(false);
		btnPositiveColour.setOpaque(true);
		btnPositiveColour.setBorder(brdButtons);
		btnPositiveColour.setToolTipText("Click to select background colour for positive figures on difference lines");
		btnPositiveColour.addActionListener(e -> {
            JButton btnTemp = (JButton) e.getSource();
            ccPositiveBG = JColorChooser.showDialog(panTop,
                    "Choose Colour for Positive Differences", ccPositiveBG);
            btnTemp.setBackground(ccPositiveBG);
            objParams.setColourPositive(ccPositiveBG);
        });
		panTop.add(btnPositiveColour, GridC.getc(2,7).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		JLabel lblNegative = new JLabel("Negative Values");
		panTop.add(lblNegative, GridC.getc(1,8).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JButton btnNegativeColour = new JButton("           ");
		btnNegativeColour.setBackground(ccNegativeBG);
		btnNegativeColour.setContentAreaFilled(false);
		btnNegativeColour.setOpaque(true);
		btnNegativeColour.setBorder(brdButtons);
		btnNegativeColour.setToolTipText("Click to select background colour for negative figures on difference lines");
		btnNegativeColour.addActionListener(e -> {
            JButton btnTemp = (JButton) e.getSource();
            ccNegativeBG = JColorChooser.showDialog(panTop,
                    "Choose Colour for Negative Differences", ccNegativeBG);
            btnTemp.setBackground(ccNegativeBG);
            objParams.setColourNegative(ccNegativeBG);
        });
		panTop.add(btnNegativeColour, GridC.getc(2,8).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		JButton btnPositiveColourFG = new JButton("           ");
		btnPositiveColourFG.setBackground(ccPositiveFG);
		btnPositiveColourFG.setContentAreaFilled(false);
		btnPositiveColourFG.setOpaque(true);
		btnPositiveColourFG.setBorder(brdButtons);
		btnPositiveColourFG.setToolTipText("Click to select foreground colour for positive figures on difference lines");
		btnPositiveColourFG.addActionListener(e -> {
            JButton btnTemp = (JButton) e.getSource();
            ccPositiveFG = JColorChooser.showDialog(panTop,
                    "Choose Colour for Positive Differences", ccPositiveFG);
            btnTemp.setBackground(ccPositiveFG);
            objParams.setColourFGPositive(ccPositiveFG);
        });
		panTop.add(btnPositiveColourFG, GridC.getc(3,7).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		JButton btnNegativeColourFG = new JButton("           ");
		btnNegativeColourFG.setBackground(ccNegativeFG);
		btnNegativeColourFG.setContentAreaFilled(false);
		btnNegativeColourFG.setOpaque(true);
		btnNegativeColourFG.setBorder(brdButtons);
		btnNegativeColourFG.setToolTipText("Click to select foreground colour for negative figures on difference lines");
		btnNegativeColourFG.addActionListener(e -> {
            JButton btnTemp = (JButton) e.getSource();
            ccNegativeFG = JColorChooser.showDialog(panTop,
                    "Choose Colour for Negative Differences", ccNegativeFG);
            btnTemp.setBackground(ccNegativeFG);
            objParams.setColourFGNegative(ccNegativeFG);
        });
		panTop.add(btnNegativeColourFG, GridC.getc(3,8).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		/*
		 * Button 1 - Save
		 */
		btnSave = new JButton("Save Parameters");
		btnSave.setToolTipText("Click to save parameters.  You will be asked for a file name");
		btnSave.addActionListener(e -> save());
		panTop.add(btnSave, GridC.getc(0,9).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		/*
		 * Button 2 - Generate values
		 */
		btnGenerate = new JButton("Generate");
		btnGenerate.setToolTipText("Click to generate the report");
		btnGenerate.addActionListener(e -> generate());
		panTop.add(btnGenerate, GridC.getc(1,9).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));

		/*
		 * Button 3 - Close
		 */
		btnClose = new JButton("Close");
		btnClose.setToolTipText("<html>Click to close the extension.  <br>If there are unsaved changes to the parameters, <br>you will be asked if you wish to continue</html>");
		btnClose.addActionListener(e -> close());
		panTop.add(btnClose, GridC.getc(2,9).west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		/*
		 * Button 4 - Help
		 *
		 */
		helpBtn = new JButton("Help");
		helpBtn.setToolTipText("Display help information");
		helpBtn.addActionListener(e -> {
            String url = "https://github.com/mrbray99/moneydanceproduction/wiki/Budget-Report";
            mdGUI.showInternetURL(url);
        });
		panTop.add(helpBtn, GridC.getc(3, 9).west().insets(10, 10, 10, 10));
		panTop.setPreferredSize(new Dimension(iTOPWIDTH, Constants.TOPDEPTH));

		panScreen.add(panTop, BorderLayout.PAGE_START);

		/*
		 * Middle panel - table
		 */
		panMid = new JPanel(new GridBagLayout());
		JLabel lblIncome = new JLabel("Income Categories :");
		panMid.add(lblIncome, GridC.getc(0,0).northWest().insets(0,10,0,0));
		JLabel lblIncmis = new JLabel("Available");
		panMid.add(lblIncmis, GridC.getc(0,1));
		JLabel lblIncsel = new JLabel("Selected");
		panMid.add(lblIncsel, GridC.getc(2,1));
		spIncomeMissing = new JScrollPane(listIncomeMissing);
		spIncomeMissing.setPreferredSize(new Dimension(iPANELWIDTH,
				iPANELHEIGHT));
		spIncomeMissing
				.setMinimumSize(new Dimension(iPANELWIDTH, iPANELHEIGHT));
		spIncomeMissing.setToolTipText("Shows the income categories not included in the report");
		panMid.add(spIncomeMissing, GridC.getc(0,2).rowspan(2).insets(0,10,0,0));
		btnISelect = new JButton("Sel");
		btnISelect.addActionListener(e -> incomeSelect());
		btnISelect.setToolTipText("Click to add the selected income categories to the report");
		panMid.add(btnISelect,  GridC.getc(1,2).fillx().south().insets(40, 5, 5, 5));
		btnIDeselect = new JButton("Des");
		btnIDeselect.addActionListener(e -> incomeDeselect());
		btnIDeselect.setToolTipText("Click to remove the selected income categories from the report");
		panMid.add(btnIDeselect, GridC.getc(1,3).fillx().north().insets(0, 5, 5, 5));
		spIncomeSelected = new JScrollPane(listIncomeSelect);
		spIncomeSelected.setPreferredSize(new Dimension(iPANELWIDTH,
				iPANELHEIGHT));
		spIncomeSelected
				.setMinimumSize(new Dimension(iPANELWIDTH, iPANELHEIGHT));
		spIncomeSelected.setToolTipText("Shows the income categories that will be included in the report");
		panMid.add(spIncomeSelected, GridC.getc(2,2).rowspan(2).insets(0, 0, 0, 10));

		JLabel lblExpense = new JLabel("Expense Categories :");
		panMid.add(lblExpense, GridC.getc(0,4).northWest().insets(10,10,0,0));
		JLabel lblExpmis = new JLabel("Available");
		panMid.add(lblExpmis, GridC.getc(0,5));
		JLabel lblExpsel = new JLabel("Selected");
		panMid.add(lblExpsel, GridC.getc(2,5));
		spExpenseMissing = new JScrollPane(listExpenseMissing);
		spExpenseMissing.setPreferredSize(new Dimension(iPANELWIDTH,
				iPANELHEIGHT));
		spExpenseMissing
				.setMinimumSize(new Dimension(iPANELWIDTH, iPANELHEIGHT));
		spExpenseMissing.setToolTipText("Shows the expense categories not included in the report");
		panMid.add(spExpenseMissing, GridC.getc(0,6).rowspan(2).insets(0,10,0,0));
		btnESelect = new JButton("Sel");
		btnESelect.addActionListener(e -> expenseSelect());
		btnESelect.setToolTipText("Click to add the selected expense categories to the report");
		panMid.add(btnESelect, GridC.getc(1,6).fillx().south().insets(40, 5, 5, 5));
		btnEDeselect = new JButton("Des");
		btnEDeselect.addActionListener(e -> expenseDeselect());
		btnEDeselect.setToolTipText("Click to remove the selected expense categories from the report");
		panMid.add(btnEDeselect, GridC.getc(1,7).fillx().north().insets(0, 5, 5, 5));
		spExpenseSelected = new JScrollPane(listExpenseSelect);
		spExpenseSelected.setPreferredSize(new Dimension(iPANELWIDTH,
				iPANELHEIGHT));
		spExpenseSelected.setMinimumSize(new Dimension(iPANELWIDTH,
				iPANELHEIGHT));
		spExpenseSelected.setToolTipText("Shows the expense categories will be included in the report");
		panMid.add(spExpenseSelected, GridC.getc(2,6).rowspan(2).insets(0, 0, 0, 10));

		panMid.setPreferredSize(new Dimension(iMIDWIDTH, iMIDDEPTH));
		panScreen.add(panMid, BorderLayout.CENTER);
		/*
		 * Set dirty back to false so real changes are caught
		 */
		objParams.resetDirty();
		getContentPane().setPreferredSize(
				new Dimension(iFRAMEWIDTH, iFRAMEDEPTH));
		this.pack();
	}

	/*
	 * Select a file
	 */
	private void chooseFile() {
		fileChooser
				.setFileFilter(new FileNameExtensionFilter("bprp", "BPRP"));
		fileChooser.setCurrentDirectory(context.getCurrentAccountBook()
				.getRootFolder());
		int iReturn = fileChooser.showDialog(this, "Select File");
		if (iReturn == JFileChooser.APPROVE_OPTION) {
			fParameters = fileChooser.getSelectedFile();
			txtFileName.setText(fParameters.getName().substring(0,
					fParameters.getName().lastIndexOf('.')));
		}
		if (!txtFileName.getText().equals(strFileName)) {
			objParams.refreshData(context, txtFileName.getText(),
					jdtFiscalStart, jdtFiscalEnd);
			jdtStartDate.setDateInt(objParams.getStartDate());
			jdtEndDate.setDateInt(objParams.getEndDate());
			chkRoll.setSelected(objParams.getRollup());
			mylmIncomeMissing.update();
			mylmIncomeSelect.update();
			mylmExpenseMissing.update();
			mylmExpenseSelect.update();
			panScreen.revalidate();
			strFileName = txtFileName.getText();
			objReportParms.setFile(strFileName);
			objReportParms.saveParams();
		}
	}

	private Image getIcon(String icon) {
		try {
			ClassLoader cl = getClass().getClassLoader();
			java.io.InputStream in = cl
					.getResourceAsStream("/com/moneydance/modules/features/budgetreport/"
							+ icon);
			if (in != null) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
				byte[] buf = new byte[256];
				int n;
				while ((n = in.read(buf, 0, buf.length)) >= 0)
					bout.write(buf, 0, n);
				return Toolkit.getDefaultToolkit().createImage(
						bout.toByteArray());
			}
		} catch (Throwable e) {
			return null;
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
		objBudgetReport = new BudgetReport(objParams, objBudget.getName());
		objReport = objBudgetReport.getReport();
		JFrame frame = new JFrame("Budget Report");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		objViewer = new MRBReportViewer(objReport);
		objViewer.setReport(objReport);
		frame.getContentPane().add(objViewer);
		frame.setTitle("Report - Build "+Main.strBuild);
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
		if (objParams.isDirty()) {
			JFrame fTemp = new JFrame();
			int iResult = JOptionPane
					.showConfirmDialog(fTemp,
							"The parameters have been changed.  Do you wish to save them?");
			if (iResult == JOptionPane.YES_OPTION) {
				String strFileName = txtFileName.getText();
				if (!strFileName.equals(Constants.CANCELLED)) {
					strFileName = askForFileName(strFileName);
					objParams.saveParams(strFileName);
				}
			}
		}
		if (objBudgetReport !=null)
			objBudgetReport.close();
		if (objViewer != null)
			objViewer.close();
		panScreen.setVisible(false);
		this.dispose();
	}

	/*
	 * save parameters at user request
	 */
	private void save() {
		String strFileName = txtFileName.getText();
		strFileName = askForFileName(strFileName);
		if (!strFileName.equals(Constants.CANCELLED)) {
			objParams.saveParams(strFileName);
			txtFileName.setText(strFileName);
		}
	}

	/*
	 * Income Select - move selected available income lines to selected
	 */
	private void incomeSelect() {
		int[] iSelected = listIncomeMissing.getSelectedIndices();
		if (iSelected.length != 0) {
			for (int iRow : iSelected) {
				mylmIncomeSelect.addElement(mylmIncomeMissing.getLineKey(iRow));
			}
			for (int i = iSelected.length - 1; i > -1; i--)
				mylmIncomeMissing.remove(iSelected[i]);
		}
		objParams.resetLists();
		mylmIncomeMissing.update();
		mylmIncomeSelect.update();
		listIncomeMissing.clearSelection();
		panScreen.revalidate();
	}

	/*
	 * Income Deselect - move selected income lines to available
	 */
	private void incomeDeselect() {
		int[] iSelected = listIncomeSelect.getSelectedIndices();
		if (iSelected.length != 0) {
			for (int iRow : iSelected) {
				mylmIncomeMissing.addElement(mylmIncomeSelect.getLineKey(iRow));
			}
			for (int i = iSelected.length - 1; i > -1; i--)
				mylmIncomeSelect.remove(iSelected[i]);
		}
		objParams.resetLists();
		mylmIncomeMissing.update();
		mylmIncomeSelect.update();
		listIncomeSelect.clearSelection();
		panScreen.revalidate();

	}

	/*
	 * Expense Select - move selected available expense lines to selected
	 */
	private void expenseSelect() {
		int[] iSelected = listExpenseMissing.getSelectedIndices();
		if (iSelected.length != 0) {
			for (int iRow : iSelected) {
				mylmExpenseSelect.addElement(mylmExpenseMissing
						.getLineKey(iRow));
			}
			for (int i = iSelected.length - 1; i > -1; i--)
				mylmExpenseMissing.remove(iSelected[i]);
		}
		objParams.resetLists();
		mylmExpenseMissing.update();
		mylmExpenseSelect.update();
		listIncomeSelect.clearSelection();
		panScreen.revalidate();

	}

	/*
	 * Expense Deselect - move selected expense lines to available
	 */
	private void expenseDeselect() {
		int[] iSelected = listExpenseSelect.getSelectedIndices();
		if (iSelected.length != 0) {
			for (int iRow : iSelected) {
				mylmExpenseMissing.addElement(mylmExpenseSelect
						.getLineKey(iRow));
			}
			for (int i = iSelected.length - 1; i > -1; i--)
				mylmExpenseSelect.remove(iSelected[i]);
		}
		objParams.resetLists();
		mylmExpenseMissing.update();
		mylmExpenseSelect.update();
		listIncomeSelect.clearSelection();
		panScreen.revalidate();

	}
	/*
	 * ask for parameters file name
	 */
	private String askForFileName(String strFileNamep) {
		String strFileName;
		JPanel panInput = new JPanel(new GridBagLayout());
		JLabel lblType = new JLabel("Enter File Name:");
		strFileName = strFileNamep;
		panInput.add(lblType, GridC.getc(0,0).insets(10, 10, 10, 10));
		JTextField txtType = new JTextField();
		txtType.setText(strFileName);
		txtType.setColumns(20);
		panInput.add(txtType, GridC.getc(1,0).insets(10, 10, 10, 10));
		while (true) {
			int iResult = JOptionPane.showConfirmDialog(null, panInput,
					"Save Parameters", JOptionPane.OK_CANCEL_OPTION);
			if (iResult == JOptionPane.OK_OPTION) {
				if (txtType.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
							"File Name can not be blank");
					continue;
				}
				strFileName = txtType.getText();
				break;
			}
			if (iResult == JOptionPane.CANCEL_OPTION) {
				strFileName = Constants.CANCELLED;
				break;
			}
		}
		return strFileName;

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
		iFRAMEWIDTH = objPreferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH,-1);
		iFRAMEDEPTH = objPreferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEDEPTH,-1);
		if (iFRAMEWIDTH < 0 || iFRAMEDEPTH < 0) {
			objRoot = Preferences.userRoot();
			objPref = objRoot
					.node("com.moneydance.modules.features.budgetreport.budgetvalueswindow");
			iFRAMEWIDTH = objPref.getInt(Constants.CRNTFRAMEWIDTH, Constants.FRAMEWIDTH);
			iFRAMEDEPTH = objPref.getInt(Constants.CRNTFRAMEDEPTH, Constants.FRAMEDEPTH);
		}
		iTOPWIDTH = iFRAMEWIDTH - 100;
		iMIDWIDTH = iFRAMEWIDTH;
		iMIDDEPTH = iFRAMEDEPTH - Constants.TOPDEPTH;
		iPANELWIDTH = iMIDWIDTH / 2 - 50;
		iPANELHEIGHT = iMIDDEPTH / 2 - 100;
	}

	private void updatePreferences(Dimension objDim) {
		objPreferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH, objDim.width);
		objPreferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEDEPTH, objDim.height);
		objPreferences.isDirty();
	}
}
