/*
 *   Copyright (c) 2018, Michael Bray.  All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencySnapshot;
import com.infinitekind.moneydance.model.CurrencyTable;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.moneydance.model.CurrencyUtil;
import com.infinitekind.util.DateUtil;
import com.moneydance.apps.md.controller.Util;
import com.moneydance.awt.GridC;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.HelpMenu;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.Platform;


public class loadPricesWindow extends JFrame implements ActionListener, TaskListener {
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	protected SortedMap<String,Double> newPricesTab;
	protected SortedMap<String,Integer>newTradeDate;
	protected SortedMap<String, Double> currentPriceTab;
	protected SortedMap<String,Integer> datesTab; 
	protected SortedMap<String,List<HistoryPrice>> historyTab;
	protected SortedMap<String,DummyAccount> accountsTab;
	protected SortedMap<String,CurrencyType> currencyTab;
	protected SortedMap<String, Integer> tickerStatus;
	protected SortedMap<String,String> tradeCurr;
	protected SortedMap<String,Double> quotePrice;
	protected SortedMap<String,PseudoCurrency> pseudoCurrencies;
	protected SortedMap<String,String>selectedExchanges;
	protected SortedMap<String,String>alteredTickers;
	protected SortedMap<String,Long>volumes;
	protected List<SecurityPrice> yahooStocksList;
	protected List<SecurityPrice> yahooHistStocksList;
	protected List<SecurityPrice> ftStocksList;
	protected List<SecurityPrice> ftHistStocksList;
	protected JProgressBar tasksProgress;
	protected GetQuotesProgressMonitor listener;
	protected MyTableModel pricesModel;
	protected MyTable pricesDisplayTab;
	protected CurrencyType baseCurrency;
	protected String baseCurrencyID;
	protected Boolean completed=false;
	protected Boolean currencyOnly = false;
	protected Boolean securityOnly = false;
	protected Boolean addVolume = false;
	protected int runtype = 0;
	protected double multiplier;

	/*
	 * Panels, Preferences and window sizes
	 */
	private JPanel panScreen;
	private int iFRAMEWIDTH = Constants.FRAMEWIDTH;
	private int iFRAMEDEPTH = Constants.FRAMEHEIGHT;
	private JScrollPane spPrices;
	private JPanel panBot;
	private JPanel panTop;
	private JPanel panMid;
	private JPanel panSource;
	private JButton closeBtn;
	private JButton secCalendarBtn;
	private JButton curCalendarBtn;
	private JButton saveBtn;
	private JButton saveValBtn;
	private JButton getPricesBtn;
	private JButton getRatesBtn;
	private JButton exportBtn;
	private JButton autoResetBtn;
    private JLabel fileName;
    private JComboBox<Integer> decimalComBo;
	private JComboBox<String> timeCombo;
	private JDateField secNextRunDate;
	private JDateField curNextRunDate;
	private MyCheckBox zeroCB;
	private MyCheckBox currencyCB;
	private MyCheckBox selectCB;
	private MyCheckBox addVolumeCB;
	private MyCheckBox historyCB;
	private HelpMenu menu;
	private JMenuItem onlineMenu = new JMenuItem("Online Help");
	private JMenu debugMenu = new JMenu("Turn Debug on/off");
	private JRadioButtonMenuItem offMItem;
	private JRadioButtonMenuItem infoMItem;
	private JRadioButtonMenuItem summMItem;
	private JRadioButtonMenuItem detMItem;
	private HelpMenu exportMenu;
	private JCheckBoxMenuItem exportSaveItem;
	private JCheckBoxMenuItem exportSaveAutoItem;
	private JMenuItem selectFolderItem;
	private JComboBox<String> secAutorunCombo;
	private JComboBox<String> curAutorunCombo;
	private Charset charSet = Charset.forName("UTF-8");
	private Image calendarIcon;
	private String exportFolder;
	/*
	 * Shared
	 */
	protected Parameters params;
	protected MRBDebug debugInst = MRBDebug.getInstance();
	protected Main main;
	protected int closeBtnx=0;
	protected int closeBtny=0;
	protected String testTicker="";
	protected String command;
	protected boolean errorsFound = false;;
	protected List<String> errorTickers;
	public loadPricesWindow(Main mainp,int runtypep)
	{
		runtype = runtypep;
		if (runtype != Constants.MANUALRUN  && runtype !=0)
			return;

		main = mainp;
		errorTickers = null;
		calendarIcon = main.getIcon(Constants.CALENDARIMAGE);
		/*
		 * start of screen, set up listener for resizing
		 */
		params = new Parameters();
		Main.params = params;
		panScreen = new JPanel();
		panTop = new JPanel (new GridBagLayout());
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
		/*
		 * set up internal tables
		 */
		setPreferences(); // set the screen sizes
		newPricesTab = new TreeMap<> ();
		newTradeDate = new TreeMap<>();
		currentPriceTab = new TreeMap<> ();
		datesTab = new TreeMap<> ();
		accountsTab = new TreeMap<> ();
		currencyTab = new TreeMap<> ();
		tickerStatus = new TreeMap<> ();
		tradeCurr = new TreeMap<>();
		quotePrice = new TreeMap<>();
		pseudoCurrencies = params.getPseudoCurrencies();
		selectedExchanges = params.getExchangeSelect();
		volumes = new TreeMap<>();
		/*
		 * Load base accounts and currencies
		 */
		loadAccounts(Main.context.getRootAccount());
		baseCurrency = Main.context.getCurrentAccountBook()
				.getCurrencies()
				.getBaseType();
		baseCurrencyID = baseCurrency.getIDString();
		if(params.getCurrency() || params.getZero()){
			loadCurrencies(Main.context.getCurrentAccountBook());
		}
		/*
		 * set up screen table
		 */
		int gridX=0;
		int gridY=0;
		pricesModel = new MyTableModel (params,newPricesTab,
				newTradeDate,
				currentPriceTab,
				datesTab,
				accountsTab,
				currencyTab,
				tradeCurr,
				quotePrice,
				selectedExchanges,
				volumes);
		pricesDisplayTab = new MyTable (params,pricesModel,tickerStatus);
		/*
		 * set up menus
		 */
		menu = new HelpMenu ("Help");
		menu.add(onlineMenu);	
		onlineMenu.addActionListener(this);
		menu.add(debugMenu);
		ButtonGroup group = new ButtonGroup();
		offMItem = new JRadioButtonMenuItem("Off");
		if (debugInst.getDebugLevel() == MRBDebug.OFF)
			offMItem.setSelected(true);
		offMItem.setMnemonic(KeyEvent.VK_R);
		offMItem.addActionListener(this);
		group.add(offMItem);
		debugMenu.add(offMItem);
		infoMItem = new JRadioButtonMenuItem("Information");
		if (debugInst.getDebugLevel() == MRBDebug.INFO)
			infoMItem.setSelected(true);
		infoMItem.setMnemonic(KeyEvent.VK_R);
		infoMItem.addActionListener(this);
		group.add(infoMItem);
		debugMenu.add(infoMItem);
		summMItem = new JRadioButtonMenuItem("Summary");
		if (debugInst.getDebugLevel() == MRBDebug.SUMMARY)
			summMItem.setSelected(true);
		summMItem.setMnemonic(KeyEvent.VK_R);
		summMItem.addActionListener(this);
		group.add(summMItem);
		debugMenu.add(summMItem);
		detMItem = new JRadioButtonMenuItem("Detailed");
		if (debugInst.getDebugLevel() == MRBDebug.DETAILED)
			detMItem.setSelected(true);
		detMItem.setMnemonic(KeyEvent.VK_R);
		detMItem.addActionListener(this);
		group.add(detMItem);
		debugMenu.add(detMItem);
		exportMenu = new HelpMenu("CSV Settings");
		exportSaveItem = new JCheckBoxMenuItem("Export on Save");
		exportSaveItem.setSelected(params.isExport());
		exportSaveItem.addActionListener(this);
		exportMenu.add(exportSaveItem);
		exportSaveAutoItem = new JCheckBoxMenuItem("Export on Auto Run");
		exportSaveAutoItem.setSelected(params.isExportAuto());
		exportSaveAutoItem.addActionListener(this);
		exportMenu.add(exportSaveAutoItem);
		selectFolderItem = new JMenuItem("Choose Folder");
		selectFolderItem.addActionListener(this);
		exportMenu.add(selectFolderItem);
		
		
		/*
		 * set up top screen
		 */
		final int startTime = Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.STARTTIME,Constants.RUNSTARTUP);
		timeCombo = new JComboBox<>(Constants.TIMETEXT);
		switch (startTime){
			case Constants.RUNSTARTUP :
				timeCombo.setSelectedIndex(0);
				break;
			case Constants.RUN0900 :
				timeCombo.setSelectedIndex(1);
				break;	
			case Constants.RUN1100 :
				timeCombo.setSelectedIndex(2);
				break;	
			case Constants.RUN1300 :
				timeCombo.setSelectedIndex(3);
				break;	
			case Constants.RUN1500 :
				timeCombo.setSelectedIndex(4);
				break;	
			case Constants.RUN1700 :
				timeCombo.setSelectedIndex(5);
				break;	
			case Constants.RUN1900 :
				timeCombo.setSelectedIndex(6);
				break;	
			case Constants.RUN2100 :
				timeCombo.setSelectedIndex(7);
				break;	
			case Constants.RUN2200 :
				timeCombo.setSelectedIndex(8);
				break;	
			}

		final String secMode = Main.preferences.getString(Constants.PROGRAMNAME+"."+Constants.SECRUNMODE,Constants.MANUALMODE);
		final String secRunperiod = Main.preferences.getString(Constants.PROGRAMNAME+"."+Constants.SECRUNTYPE,Constants.RUNYEARLY);
		secAutorunCombo = new JComboBox<>(Constants.AUTOTEXT);
		if (secMode.equals(Constants.MANUALMODE))
			secAutorunCombo.setSelectedIndex(0);
		else {
			switch (secRunperiod){
			case Constants.RUNDAILY :
				secAutorunCombo.setSelectedIndex(1);
				break;
			case Constants.RUNWEEKLY :
				secAutorunCombo.setSelectedIndex(2);
				break;	
			case Constants.RUNMONTHLY :
				secAutorunCombo.setSelectedIndex(3);
				break;	
			case Constants.RUNQUARTERLY :
				secAutorunCombo.setSelectedIndex(4);
				break;	
			case Constants.RUNYEARLY :
				secAutorunCombo.setSelectedIndex(5);
				break;	
			}
		}	
		final String curMode = Main.preferences.getString(Constants.PROGRAMNAME+"."+Constants.CURRUNMODE,Constants.MANUALMODE);
		final String curRunperiod = Main.preferences.getString(Constants.PROGRAMNAME+"."+Constants.CURRUNTYPE,Constants.RUNYEARLY);
		curAutorunCombo = new JComboBox<>(Constants.AUTOTEXT);
		if (curMode.equals(Constants.MANUALMODE))
			curAutorunCombo.setSelectedIndex(0);
		else {
			switch (curRunperiod){
			case Constants.RUNDAILY :
				curAutorunCombo.setSelectedIndex(1);
				break;
			case Constants.RUNWEEKLY :
				curAutorunCombo.setSelectedIndex(2);
				break;	
			case Constants.RUNMONTHLY :
				curAutorunCombo.setSelectedIndex(3);
				break;	
			case Constants.RUNQUARTERLY :
				curAutorunCombo.setSelectedIndex(4);
				break;	
			case Constants.RUNYEARLY :
				curAutorunCombo.setSelectedIndex(5);
				break;	
			}
		}
		timeCombo.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cbRun = (JComboBox<String>) e.getSource();
				int runTime = Constants.RUNSTARTUP;
				switch (cbRun.getSelectedIndex()) {
				case 0 :
					runTime = Constants.RUNSTARTUP;
					break;
				case 1 :
					runTime = Constants.RUN0900;
					break;
				case 2 :
					runTime = Constants.RUN1100;
					break;
				case 3 :
					runTime = Constants.RUN1300;
					break;
				case 4 :
					runTime = Constants.RUN1500;
					break;
				case 5 :
					runTime = Constants.RUN1700;
					break;
				case 6 :
					runTime = Constants.RUN1900;
					break;
				case 7 :
					runTime = Constants.RUN2100;
					break;
				case 8 :
					runTime = Constants.RUN2200;
					break;
				}
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.STARTTIME, runTime);;
				Main.preferences.isDirty();
			}
		});		
		secAutorunCombo.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cbRun = (JComboBox<String>) e.getSource();
				String modeStr = Constants.MANUALMODE;
				String runperiodStr="";
				switch (cbRun.getSelectedIndex()) {
				case 0 :
					modeStr = Constants.MANUALMODE;
					runperiodStr = "";
					break;
				case 1 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNDAILY;
					break;
				case 2 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNWEEKLY;
					break;
				case 3 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNMONTHLY;
					break;
				case 4 :
					modeStr = Constants.AUTOMODE;
					runperiodStr =Constants.RUNQUARTERLY;
					break;
				case 5 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNYEARLY;
					break;
				}
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.SECRUNMODE, modeStr);;
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.SECRUNTYPE,runperiodStr);
				Main.preferences.isDirty();
				if (modeStr.equals(Constants.AUTOMODE)) {
					showCalendar(Constants.SECRUNTYPE,Constants.SECRUNPARAM);
					int newDate = calculateNextRunDate(Constants.SECRUNTYPE,Constants.SECRUNPARAM,Constants.SECLASTRUN);
					if (newDate != 0)
						secNextRunDate.setDateInt(newDate);
				}
			}
		});		
		curAutorunCombo.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cbRun = (JComboBox<String>) e.getSource();
				String modeStr = Constants.MANUALMODE;
				String runperiodStr="";
				switch (cbRun.getSelectedIndex()) {
				case 0 :
					modeStr = Constants.MANUALMODE;
					runperiodStr = "";
					break;
				case 1 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNDAILY;
					break;
				case 2 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNWEEKLY;
					break;
				case 3 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNMONTHLY;
					break;
				case 4 :
					modeStr = Constants.AUTOMODE;
					runperiodStr =Constants.RUNQUARTERLY;
					break;
				case 5 :
					modeStr = Constants.AUTOMODE;
					runperiodStr = Constants.RUNYEARLY;
					break;
				}
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CURRUNMODE, modeStr);;
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CURRUNTYPE,runperiodStr);
				Main.preferences.isDirty();
				if (modeStr.equals(Constants.AUTOMODE)) {
					showCalendar(Constants.CURRUNTYPE,Constants.CURRUNPARAM);
					int newDate = calculateNextRunDate(Constants.CURRUNTYPE,Constants.CURRUNPARAM,Constants.CURLASTRUN);
					if (newDate != 0)
						curNextRunDate.setDateInt(newDate);
				}
			}
		});		
		/*
		 * Button  Calendar
		 */
		secCalendarBtn = new JButton();
		if (calendarIcon == null)
			secCalendarBtn.setText("Calendar");
		else
			secCalendarBtn.setIcon(new ImageIcon(calendarIcon));
		secCalendarBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCalendar(Constants.SECRUNTYPE,Constants.SECRUNPARAM);
				int newDate = calculateNextRunDate(Constants.SECRUNTYPE,Constants.SECRUNPARAM,Constants.SECLASTRUN);
				if (newDate != 0)
					secNextRunDate.setDateInt(newDate);
			}
		});
		curCalendarBtn = new JButton();
		if (calendarIcon == null)
			curCalendarBtn.setText("Calendar");
		else
			curCalendarBtn.setIcon(new ImageIcon(calendarIcon));
		curCalendarBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCalendar(Constants.CURRUNTYPE,Constants.CURRUNPARAM);
				int newDate = calculateNextRunDate(Constants.CURRUNTYPE,Constants.CURRUNPARAM,Constants.CURLASTRUN);
				if (newDate != 0)
					curNextRunDate.setDateInt(newDate);
			}
		});
		autoResetBtn = new JButton("Reset Auto Run");
		autoResetBtn.setToolTipText("Click to restart the Auto Run facility after changing the Auto Run fields");
		autoResetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.CHECKAUTOCMD);
					}
				});
				
			}
		});
		
		JLabel runLbl = new JLabel("Automatic Run Types");
		JLabel timeLbl = new JLabel("Time of Run");
		gridX=0;
		gridY=0;
		JLabel secRunLbl = new JLabel("(Securities)");
		panTop.add(runLbl, GridC.getc(gridX++,gridY).insets(5,10,5,10));
		panTop.add(timeLbl, GridC.getc(gridX++,gridY).insets(5,10,5,10));
		panTop.add(timeCombo, GridC.getc(gridX++,gridY).insets(5,10,5,10));
		panTop.add(secRunLbl, GridC.getc(gridX++,gridY).insets(5,10,5,10));
		panTop.add(secAutorunCombo, GridC.getc(gridX++,gridY).insets(5,10,5,10));
		panTop.add(secCalendarBtn,GridC.getc(gridX++,gridY).insets(5,10,5,10));
		String lastRunStr = Main.cdate.format(Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.SECLASTRUN,0));
		JLabel secDateLbl = new JLabel("Last Run ("+ lastRunStr+") Next");
		secNextRunDate = new JDateField(Main.cdate,10);
		secNextRunDate.addPropertyChangeListener(new PropertyChangeListener () {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getPropertyName() == JDateField.PROP_DATE_CHANGED){
					JDateField date = (JDateField)arg0.getSource();
					setNextRunDate(date,Constants.SECNEXTRUN);
				}		
			}
		});
		secNextRunDate.setDateInt(DateUtil.getStrippedDateInt());
		if (!secMode.equals(Constants.MANUALMODE)) 	{
			secNextRunDate.setDateInt(Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.SECNEXTRUN,DateUtil.getStrippedDateInt()));
		}
		panTop.add(secDateLbl, GridC.getc(gridX++,gridY).insets(5,10,5,10));
		panTop.add(secNextRunDate, GridC.getc(gridX++,gridY).insets(5,10,5,10));
		panTop.add(menu, GridC.getc(gridX,gridY++).insets(5,10,5,10));
		gridX=0;
		panTop.add(autoResetBtn,GridC.getc(gridX,gridY).insets(5,10,5,10));
		gridX=3;
		JLabel curRunLbl = new JLabel("(Currencies)");
		panTop.add(curRunLbl, GridC.getc(gridX++,gridY).insets(0,10,5,10));
		panTop.add(curAutorunCombo, GridC.getc(gridX++,gridY).insets(0,10,5,10));
		panTop.add(curCalendarBtn,GridC.getc(gridX++,gridY).insets(5,10,5,10));	
		lastRunStr = Main.cdate.format(Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CURLASTRUN,0));
		JLabel curDateLbl = new JLabel("Last Run ("+ lastRunStr+") Next");
		curNextRunDate = new JDateField(Main.cdate,10);
		curNextRunDate.addPropertyChangeListener(new PropertyChangeListener () {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getPropertyName() == JDateField.PROP_DATE_CHANGED){
					JDateField date = (JDateField)arg0.getSource();
					setNextRunDate(date,Constants.CURNEXTRUN);
				}
			}
		});
		curNextRunDate.setDateInt(DateUtil.getStrippedDateInt());
		if (!curRunperiod.equals(Constants.MANUALMODE)) 	{
			curNextRunDate.setDateInt(Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CURNEXTRUN,DateUtil.getStrippedDateInt()));
		}
		panTop.add(curDateLbl, GridC.getc(gridX++,gridY).insets(0,10,5,10));
		panTop.add(curNextRunDate, GridC.getc(gridX++,gridY).insets(0,10,5,10));
		panTop.add(exportMenu, GridC.getc(gridX,gridY++).insets(5,10,5,10));
		/*
		 * Parameter Date
		 */
		gridX=0;
		zeroCB = new MyCheckBox("Include zero accounts?");
		zeroCB.setToolTipText("If selected any security that does not have any holdings but has 'Show on summary page' set will be loaded");
		zeroCB.setSelected(params.getZero());
		zeroCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox chbZeroT = (JCheckBox) e.getSource();
				params.setZero(chbZeroT.isSelected());
				resetData();
			}
		});
		Font cbFont = zeroCB.getFont();
		int style = cbFont.getStyle();
		style &= ~Font.BOLD;
		cbFont = cbFont.deriveFont(style);
		zeroCB.setFont(cbFont);;
		panTop.add(zeroCB,GridC.getc(gridX++,gridY).west().insets(5,10,10,5));
		currencyCB = new MyCheckBox("Process Currencies");
		currencyCB.setToolTipText("If selected currencies with 'Show on summary Page' set will be loaded");
		currencyCB.setFont(cbFont);;
		currencyCB.setSelected(params.getCurrency());
		currencyCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox chbCurrencyT = (JCheckBox) e.getSource();
				params.setCurrency(chbCurrencyT.isSelected());
				resetData();
			}
		});
		panTop.add(currencyCB,GridC.getc(gridX++,gridY).west().insets(5,10,10,5));
		JLabel lblDecimal = new JLabel("Decimal Digits");
		panTop.add(lblDecimal,GridC.getc(gridX++,gridY).east().insets(5,10,10,5) );	
		decimalComBo = new JComboBox<>(Parameters.decimals);
		decimalComBo.setToolTipText("By default the prices are shown to 4dp. You can select 5,6,7 or 8 dp to display");
		decimalComBo.setSelectedIndex(params.getDecimal()-2);
		decimalComBo.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<Integer> cbDec = (JComboBox<Integer>) e.getSource();
				params.setDecimal((int)cbDec.getSelectedItem());
				resetData();
			}
		});
		
		panTop.add(decimalComBo,GridC.getc(gridX++,gridY).west().insets(5,10,10,5));
/*		roundPriceCB = new MyCheckBox();
		roundPriceCB.setToolTipText("Set to round returned prices before they are stored");
		roundPriceCB.setText("Round Prices before Saving");
		roundPriceCB.setFont(cbFont);;
		roundPriceCB.setSelected(params.isRoundPrices());
		roundPriceCB.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox chbroundPriceT = (JCheckBox) e.getSource();
				params.setRoundPrices(chbroundPriceT.isSelected());
				resetData();
			}
		});
		panTop.add(roundPriceCB,GridC.getc(gridX++,gridY).colspan(2).west().insets(5,10,10,5));
		gridX++;*/
		fileName = new JLabel("Export Folder : "+params.getExportFolder());
		panTop.add(fileName,GridC.getc(gridX++,gridY).colspan(4).west().insets(5,10,10,5));

		panSource = new JPanel(new GridBagLayout());
		panTop.add(panSource,GridC.getc(gridX,gridY).colspan(7).insets(5,10,10,5));
		panScreen.add(panTop,BorderLayout.PAGE_START);

		/*
		 * Prices Screen
		 */
		panMid = new JPanel (new BorderLayout());
		spPrices = new JScrollPane (pricesDisplayTab);
		panMid.add(spPrices,BorderLayout.CENTER);
		JPanel panCB = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panCB.setLayout(flowLayout);
		selectCB = new MyCheckBox();
		selectCB.setToolTipText("Select to tick all lines that have a price");
		selectCB.setAlignmentX(LEFT_ALIGNMENT);
		selectCB.setText("Click to Select all lines");
		selectCB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean bNewValue;
				if (e.getStateChange() == ItemEvent.DESELECTED)
					bNewValue = false;
				else
					bNewValue = true;
				int itemsChanged = pricesModel.selectAll(bNewValue);
				if (itemsChanged > 0)
					pricesModel.fireTableDataChanged();
				else
				{
					if (bNewValue)
						JOptionPane.showMessageDialog(null,"No prices have been downloaded.  Use Get Exchange Rates or Get Prices");
				}
			}
		});
		panCB.add(selectCB);
		addVolumeCB = new MyCheckBox();
		addVolumeCB.setToolTipText("Select to include volume data when saving price");
		addVolumeCB.setAlignmentX(LEFT_ALIGNMENT);
		addVolumeCB.setText("Include Volume Data");
		addVolumeCB.setSelected(params.getAddVolume());
		addVolumeCB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED)
					params.setAddVolume(false);
				else
					params.setAddVolume(true);
			}
		});
		panCB.add(addVolumeCB);
		historyCB = new MyCheckBox();
		historyCB.setToolTipText("Select to retrieve historical prices when using FT HD or Yahoo HD");
		historyCB.setAlignmentX(LEFT_ALIGNMENT);
		historyCB.setText("Retrieve missed prices");
		historyCB.setSelected(params.getHistory());
		historyCB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED)
					params.setHistory(false);
				else
					params.setHistory(true);
			}
		});
		panCB.add(historyCB);	
		panMid.add(panCB,BorderLayout.PAGE_END);
		panScreen.add(panMid,BorderLayout.CENTER);
		/*
		 * Buttons screen
		 */
		panBot = new JPanel(new GridBagLayout());
		gridX=0;
		gridY=0;
		/*
		 * Button Save Selected Values
		 */
		saveValBtn = new JButton("Save Selected Values");
		saveValBtn.setToolTipText("Save the selected prices");
		saveValBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		panBot.add(saveValBtn,GridC.getc(gridX++,gridY).insets(10,10,10,10));		

		/*
		 * Get Rates
		 */
		getRatesBtn = new JButton("Get Exchange Rates");
		panBot.add(getRatesBtn,GridC.getc(gridX++,gridY).west().insets(10,10,10,10));
		getRatesBtn.setToolTipText("Retrieve the exchange rates from the Internet");
		getRatesBtn.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				currencyOnly = true;
				securityOnly = false;
				getPrices();
			}
		});	
		/*
		 * Get
		 */
		getPricesBtn = new JButton("Get Prices");
		panBot.add(getPricesBtn,GridC.getc(gridX++,gridY).west().insets(10,10,10,10));
		getPricesBtn.setToolTipText("Retrieve the quotes from the Internet");
		getPricesBtn.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				currencyOnly = false;
				securityOnly = false;
				getPrices();
			}
		});
		/*
		 * Save
		 */
		saveBtn = new JButton("Save Parameters");
		panBot.add(saveBtn, GridC.getc(gridX++,gridY).insets(10,10,10,10));
		saveBtn.setToolTipText("Save the parameter settings to '{data directory}/securityquoteload.bpam2'");
		saveBtn.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				params.save();
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,"Parameters saved");		
			}
		});	
		/*
		 * Export
		 */
		exportBtn = new JButton("Create Prices CSV");
		exportBtn.setToolTipText("Output selected prices to a .csv file");
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		panBot.add(exportBtn,GridC.getc(gridX++,gridY).west().insets(10,10,10,10));
		/*
		 * Button Close
		 */
		closeBtn = new JButton("Close");
		closeBtn.setToolTipText("Close Quote Loader");
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		panBot.add(closeBtn,GridC.getc(gridX++,gridY).west().insets(10,10,10,10));
		closeBtnx= gridX;
		closeBtny= gridY;
		

		panScreen.add(panBot,BorderLayout.PAGE_END);
		getContentPane().setPreferredSize(
				new Dimension(iFRAMEWIDTH, iFRAMEDEPTH));
		this.pack();


	}
	protected void resetData() {
		currentPriceTab = new TreeMap<> ();
		datesTab = new TreeMap<> ();
		accountsTab = new TreeMap<> ();
		currencyTab = new TreeMap<> ();
		loadAccounts(Main.context.getRootAccount());
		if(params.getCurrency()|| params.getZero()){
			loadCurrencies(Main.context.getCurrentAccountBook());
			baseCurrency = Main.context.getCurrentAccountBook()
					.getCurrencies()
					.getBaseType();
			baseCurrencyID = baseCurrency.getIDString();
		}
		/*
		 * Clean up (removes any Account no longer  in the list
		 */
		pricesModel.resetData(currentPriceTab, 
				datesTab,
				accountsTab,
				currencyTab,
				newPricesTab,
				newTradeDate,
				tradeCurr,
				quotePrice,
				volumes);
		pricesModel.fireTableDataChanged();			 
	}
	public void setErrorTickers(List<String> errorTickersp){
		pricesModel.addErrorTickers(errorTickersp);
	}
	private void setNextRunDate (JDateField date,String dateNode){
	    int newDate = date.getDateInt();
	    Main.preferences.put(Constants.PROGRAMNAME+"."+dateNode,newDate);
	    Main.preferences.isDirty();

	}
	private int calculateNextRunDate (String typeNode, String typeParam, String lastRun){
		CalculateRunDate runDate = new CalculateRunDate(typeNode,typeParam,lastRun);
	    return runDate.getDate();
	}
	private void showCalendar(String runtype,String runParam){
		
		debugInst.debug("loadPricesWindow", "showCalendar", MRBDebug.DETAILED, "Displaying Calendar Popup");                
		CalendarPopup popup = new CalendarPopup(runtype,runParam);
		int ix = curCalendarBtn.getX();
		int iy = curCalendarBtn.getY();
		Point p = new Point(ix,iy);
		popup.setLocation(p);
		popup.setVisible(true);
	}

	/*
	 * Create 3 tables of current rate, date rate set and account object
	 * all keyed by ticker symbol
	 */
	protected void loadAccounts(Account parentAcct) {
		List<Account> lAccts = parentAcct.getSubAccounts();
		for (Account acct : lAccts) {
			if (acct.getAccountType() == Account.AccountType.SECURITY){
				if((acct.getCurrentBalance() != 0L)||(params.getZero())){
					CurrencyType ctTicker = acct.getCurrencyType();
					/*
					 * Get last price entry
					 */
					if (ctTicker != null) {
						if (!ctTicker.getTickerSymbol().equals("")) {
							List<CurrencySnapshot> listSnap = ctTicker.getSnapshots();
							String strTicker = ctTicker.getTickerSymbol().trim().toUpperCase();
							int iSnapIndex = listSnap.size()-1;
							if( ! accountsTab.containsKey(strTicker)){
								DummyAccount dacct = new DummyAccount();
								dacct.setAccount(acct);
								dacct.setCurrencyType(ctTicker);
								dacct.setAccountName(acct.getAccountName());
								if (iSnapIndex < 0) {
									currentPriceTab.put(strTicker,1.0);
									datesTab.put(strTicker,0);
									accountsTab.put(strTicker,dacct);
								}
								else {
									CurrencySnapshot ctssLast = listSnap.get(iSnapIndex);
									if (ctssLast != null) {
										currentPriceTab.put(strTicker,1.0/ctssLast.getRate());
									}
									else {
										currentPriceTab.put(strTicker,0.0);
									}
										
									datesTab.put(strTicker, ctssLast.getDateInt());
									accountsTab.put(strTicker, dacct);
								}
							}
						}
					}
				}
			}
			loadAccounts(acct);
		}
	}
	/*
	 * Load the currencies and add to Securities
	 */
	protected void loadCurrencies(AccountBook accountBook){
		CurrencyTable currencyTable = accountBook.getCurrencies();
		Iterator<CurrencyType> currTypeIterator = currencyTable.iterator();
		while(currTypeIterator.hasNext()){
			CurrencyType currencyType = currTypeIterator.next();
			if (currencyType.getCurrencyType() == CurrencyType.Type.SECURITY && params.getZero()){
				if (!currencyType.getTickerSymbol().equals("")&&!currencyType.getHideInUI()) {
					List<CurrencySnapshot> listSnap = currencyType.getSnapshots();
					String ticker = currencyType.getTickerSymbol().trim().toUpperCase();
					int snapIndex = listSnap.size()-1;
					if( ! accountsTab.containsKey(ticker)){
						DummyAccount dummyAcct = new DummyAccount();
						dummyAcct.setAccount(null);
						dummyAcct.setCurrencyType(currencyType);
						dummyAcct.setAccountName(currencyType.getName());
						if (snapIndex < 0) {
							currentPriceTab.put(ticker,1.0);
							datesTab.put(ticker,0);
							accountsTab.put(ticker,dummyAcct);
						}
						else {
							CurrencySnapshot lastSnapshot = listSnap.get(snapIndex);
							if (lastSnapshot != null) {
								currentPriceTab.put(ticker,1.0/lastSnapshot.getRate());
							}
							else {
								currentPriceTab.put(ticker,0.0);
							}
							datesTab.put(ticker, lastSnapshot.getDateInt());
							accountsTab.put(ticker, dummyAcct);
						}
					}
				}

			}
			else {		 
				if (currencyType.getCurrencyType()== CurrencyType.Type.CURRENCY &&
						!currencyType.getHideInUI() && 
						currencyType != baseCurrency && 
						params.getCurrency() ){
					List<CurrencySnapshot> listSnap = currencyType.getSnapshots();
					int snapIndex = listSnap.size()-1;
					if (snapIndex < 0) {
						currentPriceTab.put(Constants.CURRENCYID+currencyType.getIDString(),1.0);
						datesTab.put(Constants.CURRENCYID+currencyType.getIDString(),0);
						currencyTab.put(Constants.CURRENCYID+currencyType.getIDString(),currencyType);
					}
					else {
						CurrencySnapshot ctssLast = listSnap.get(snapIndex);
						if (ctssLast == null) 
							currentPriceTab.put(Constants.CURRENCYID+currencyType.getIDString(),1.0);
						else
							currentPriceTab.put(Constants.CURRENCYID+currencyType.getIDString(),ctssLast.getRate());
						datesTab.put(Constants.CURRENCYID+currencyType.getIDString(), ctssLast.getDateInt());
						currencyTab.put(Constants.CURRENCYID+currencyType.getIDString(), currencyType);
					}
				}
			}
		}		 	 }

	public void close() {
		if (params.paramsChanged()) {
	        if (JOptionPane.showConfirmDialog(this, 
		            "You have changed the Parameters.  Do you wish to save them before closing?","Save Parameters", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
	        		params.save();
		        }
		}
		this.setVisible(false);
		if(main != null)
			main.cleanup();

	}
	private void export() {
		String exportFolder = params.getExportFolder();
		if (exportFolder== null || exportFolder.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Export folder has not been set");
			return;
		}
		int iRows = pricesModel.getRowCount();
		int iRowCount = 0;
		if (newPricesTab.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No prices have been downloaded.  Use Get Exchange Rates or Get Prices");
			return;
		}
		for (int i=0;i<iRows;i++){
			if((Boolean)pricesModel.getValueAt(i,0)) {
				iRowCount++;
			}
		}
		if (iRowCount < 1){
			JOptionPane.showMessageDialog(null, "No prices have been selected.  Select individual lines or Select All.");
			return;
		}
		BufferedWriter exportFile = setupExportFile();
		iRowCount=0;
		for (int i=0;i<iRows;i++){
			if(pricesModel.updateLine(i,exportFile,true)) {
				iRowCount++;
			}
		}
		if (exportFile != null)
			try {
				exportFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		JOptionPane.showMessageDialog(null, "Prices Exported.");


	}
	private BufferedWriter setupExportFile() {
		FileOutputStream exportFile;
		OutputStreamWriter exportWriter;
		BufferedWriter exportBuffer;
		String filename = params.getExportFolder()+"/";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		filename += "priceexport"+ dtf.format(LocalDateTime.now())+".csv";
		try {
			exportFile = new FileOutputStream(filename);
			exportWriter = new OutputStreamWriter(exportFile,"UTF-8");
			exportBuffer = new BufferedWriter(exportWriter);
			exportWriter.write(Constants.EXPORTHEADER);
			debugInst.debug("loadPricesWindow", "setupExportFile", MRBDebug.DETAILED, "Created export file"+filename);                
		} catch (IOException e) {
			exportBuffer = null;
		}
		return exportBuffer;
	}
	private void chooseFile() {
		JFileChooser fileChooser;
		String strDirectory = params.getExportFolder();
		if (Platform.isOSX()) {
			JFrame parentWindow = (JFrame) SwingUtilities.getWindowAncestor(this); 
		       System.setProperty("com.apple.macos.use-file-dialog-packages", "true");
	         FileDialog fwin = new FileDialog(parentWindow, "choose_directory", FileDialog.LOAD);
	         System.setProperty( "apple.awt.fileDialogForDirectories", "true" );
	         fwin.setDirectory(strDirectory);
	         fwin.setVisible(true);
	   
	         strDirectory =fwin.getDirectory()+fwin.getFile();
     		debugInst.debug("loadPricesWindow", "ChooseFile2", MRBDebug.DETAILED,
	     				"Directory Name is:"+strDirectory);	   
	       }
		else {
			fileChooser = new JFileChooser();
			if (!(strDirectory == null || strDirectory ==""))
			{
				File directory = new File(strDirectory);
				fileChooser.setCurrentDirectory(directory);
			}
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int iReturn = fileChooser.showDialog(this, "Select Directory");
			if (iReturn == JFileChooser.APPROVE_OPTION) {
				File fSecurities =fileChooser.getSelectedFile();
				strDirectory = fSecurities.getAbsolutePath();
			}
		}
		exportFolder = strDirectory;
		return;
	}
	protected void save() {
		BufferedWriter exportFile;
		int iRows = pricesModel.getRowCount();
		boolean bUpdated = false;
		int iRowCount = 0;
		if (runtype != Constants.MANUALRUN  && runtype !=0){
			if (params.isExportAuto()) {
				exportFile = setupExportFile();
			}
			else
				exportFile = null;
			for (int i=0;i<iRows;i++){
				if(pricesModel.updateLine(i,exportFile,false)) {
					bUpdated = true;
					iRowCount++;
				}
			}
			if (exportFile != null)
				try {
					exportFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		else {
			if (newPricesTab.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No prices have been downloaded.  Use Get Exchange Rates or Get Prices");
				return;
			}
			if (params.isExport()) {
				exportFile = setupExportFile();
			}
			else
				exportFile = null;

			for (int i=0;i<iRows;i++){
				if(pricesModel.updateLine(i,exportFile,false)) {
					bUpdated = true;
					iRowCount++;
				}
			}
			if (exportFile != null)
				try {
					exportFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (iRowCount < 1){
				JOptionPane.showMessageDialog(null, "No prices have been selected.  Select individual lines or Select All.");
				return;
			}
		}
		/*
		 * if an automatic run it is expected that this object will be disposed off
		 */
		if (bUpdated){
			if (runtype == Constants.MANUALRUN) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,iRowCount+" prices updated");
				selectCB.setSelected(false);
				/*
				 * Clear current account data and reload before redisplaying
				 * the table
				 */
				currentPriceTab.clear();
				datesTab.clear();
				accountsTab.clear();
				currencyTab.clear();
				loadAccounts(Main.context.getRootAccount());
				if(params.getCurrency()|| params.getZero())
					loadCurrencies(Main.context.getCurrentAccountBook());
				pricesModel.resetData(currentPriceTab,
						datesTab,
						accountsTab,
						currencyTab,
						newPricesTab,
						newTradeDate,
						tradeCurr,
						quotePrice,
						volumes);
				pricesModel.fireTableDataChanged();	
				this.revalidate();
			}
			/*
			 * Clear current account data and reload before redisplaying
			 * the table
			 */
			currentPriceTab.clear();
			datesTab.clear();
			accountsTab.clear();
			currencyTab.clear();
			loadAccounts(Main.context.getRootAccount());
			if(params.getCurrency()|| params.getZero())
				loadCurrencies(Main.context.getCurrentAccountBook());
			pricesModel.resetData(currentPriceTab,
					datesTab,
					accountsTab,
					currencyTab,
					newPricesTab,
					newTradeDate,
					tradeCurr,
					quotePrice,
					volumes);
			pricesModel.fireTableDataChanged();			 
			this.revalidate();
		}
	}
	protected void getPrices (){
		tickerStatus.clear();
		completed = false;
		pricesModel.resetPrices();
		pricesModel.clearErrorTickers();
		alteredTickers = new TreeMap<>();
		historyTab = new TreeMap<String, List<HistoryPrice>>();
		pricesModel.resetHistory(historyTab);
		if (runtype == Constants.MANUALRUN) {
			final JProgressBar taskProgress = new JProgressBar(0, 100);
			taskProgress.setValue(0);
			taskProgress.setStringPainted(true);
			panBot.add(taskProgress,GridC.getc(closeBtnx, closeBtny).insets(10,10,10,10));
			this.tasksProgress = taskProgress;
			this.listener = new GetQuotesProgressMonitor(this.tasksProgress,this,tickerStatus);
		}
		else
			this.listener = new GetQuotesProgressMonitor(null,this,tickerStatus);
		if (runtype == Constants.MANUALRUN)
			getPricesBtn.setEnabled(false);
		yahooStocksList= new ArrayList<>();
		yahooHistStocksList= new ArrayList<>();
		ftStocksList= new ArrayList<>();
		ftHistStocksList= new ArrayList<>();
		for (AccountLine alTemp : params.getAccountsList()){
			if (alTemp.getSource() == Constants.YAHOOINDEX) {
				if ((alTemp.isCurrency() && currencyTab.containsKey(alTemp.getName()))) {
					if(!securityOnly ){
						SecurityPrice spLine = new SecurityPrice (alTemp.getName());
						yahooStocksList.add(spLine);
					}
				}
				else if(!alTemp.isCurrency() && accountsTab.containsKey(alTemp.getName())){
					if (!currencyOnly) {
						SecurityPrice spLine = new SecurityPrice (alTemp.getName());
						if (selectedExchanges.containsKey(alTemp.getName())) 
							spLine.setExchange(selectedExchanges.get(alTemp.getName()));
						else
							spLine.setExchange(null);
						yahooStocksList.add(spLine);
					}
				}
			}
			if (alTemp.getSource() == Constants.FTINDEX) {
				if (alTemp.isCurrency() && currencyTab.containsKey(alTemp.getName())) {
					if (!securityOnly) {
						SecurityPrice spLine = new SecurityPrice (alTemp.getName());
						ftStocksList.add(spLine);
					}
				}
				else {
					if (!alTemp.isCurrency() && accountsTab.containsKey(alTemp.getName())){
						if (!currencyOnly) {
							SecurityPrice spLine = new SecurityPrice (alTemp.getName());
							if (selectedExchanges.containsKey(alTemp.getName()))
								spLine.setExchange(selectedExchanges.get(alTemp.getName()));
							else
								spLine.setExchange(null);
							ftStocksList.add(spLine);
						}
					}
				}
			}
			if (alTemp.getSource() == Constants.YAHOOHISTINDEX) {
				if (alTemp.isCurrency() && currencyTab.containsKey(alTemp.getName())) {
					if (!securityOnly) {
						SecurityPrice spLine = new SecurityPrice (alTemp.getName());
						yahooHistStocksList.add(spLine);
					}
				}
				else {
					if (!alTemp.isCurrency() && accountsTab.containsKey(alTemp.getName())){
						if (!currencyOnly) {
							SecurityPrice spLine = new SecurityPrice (alTemp.getName());
							if (selectedExchanges.containsKey(alTemp.getName()))
								spLine.setExchange(selectedExchanges.get(alTemp.getName()));
							else
								spLine.setExchange(null);
							yahooHistStocksList.add(spLine);
						}
					}
				}
			}
			if (alTemp.getSource() == Constants.FTHISTINDEX) {
				if (!alTemp.isCurrency() && accountsTab.containsKey(alTemp.getName())){
					if (!currencyOnly) {
						SecurityPrice spLine = new SecurityPrice (alTemp.getName());
						if (selectedExchanges.containsKey(alTemp.getName()))
							spLine.setExchange(selectedExchanges.get(alTemp.getName()));
						else
							spLine.setExchange(null);
						ftHistStocksList.add(spLine);
					}
				}
			}
		}
		if(yahooStocksList.size() == 0 && ftStocksList.size()==0 && yahooHistStocksList.size() == 0 && ftHistStocksList.size() == 0) {
			if (runtype == Constants.MANUALRUN)
				getPricesBtn.setEnabled(true);
			else {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.AUTODONECMD);
					}
				});
			}
				
			return;
		}
		listener.setSubTaskSize(yahooStocksList.size()+ftStocksList.size()+yahooHistStocksList.size()+ftHistStocksList.size());
		completed = false;
		/*
		 * let main process know we are starting a quote
		 */
		Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.STARTQUOTECMD);
		String yahooUUID = UUID.randomUUID().toString();
		String yahooHistUUID = UUID.randomUUID().toString();
		String ftUUID = UUID.randomUUID().toString();
		String ftHistUUID = UUID.randomUUID().toString();
		if (yahooStocksList.size() > 0) {
			String url=null;
			String type;
			String ticker;
			for (SecurityPrice price : yahooStocksList){
				if (price.isCurrency()) {
					type = Constants.CURRENCYTYPE;
					String currency = price.getCurrency();
					if (currency.endsWith("-"))
						ticker = currency+baseCurrencyID;
					else
						ticker = baseCurrencyID+currency+Constants.CURRENCYTICKER;
				}
				else {
					type = Constants.STOCKTYPE;
					ticker = price.getTicker();
					String newTicker = params.getNewTicker(ticker,price.getExchange(), Constants.YAHOOINDEX);
					if (!newTicker.equals(ticker)){
						alteredTickers.put(newTicker,ticker);
						debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.DETAILED, "Yahoo Ticker changed from "+ticker+" to "+newTicker);                
						ticker = newTicker;
					}
				}
				if (url==null)
					url=newPriceUrl(Constants.SOURCEYAHOO,yahooUUID,ticker,type,-1);
				else
					url = url+addPriceUrl(ticker,type,-1);
				if (listener !=null)
					listener.started(price.getTicker(),yahooUUID);
			}
			Main.context.showURL(url);		
			debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.INFO, "URI "+url);
		}
		if (yahooHistStocksList.size() > 0) {
			String url=null;
			String type;
			String ticker;
			String oldTicker;
			for (SecurityPrice price : yahooHistStocksList){
				if (price.isCurrency()) {
					type = Constants.CURRENCYTYPE;
					String currency = price.getCurrency();
					if (currency.endsWith("-"))
						ticker = currency+baseCurrencyID;
					else
						ticker = baseCurrencyID+currency+Constants.CURRENCYTICKER;
					oldTicker = price.getTicker();
				}
				else {
					type = Constants.STOCKTYPE;
					ticker = price.getTicker();
					oldTicker = ticker;
					String newTicker = params.getNewTicker(ticker,price.getExchange(), Constants.YAHOOHISTINDEX);
					if (!newTicker.equals(ticker)){
						alteredTickers.put(newTicker,ticker);
						debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.DETAILED, "Yahoo History Ticker changed from "+ticker+" to "+newTicker);
						ticker = newTicker;
					}
				}
				Integer lastPriceDate = datesTab.get(oldTicker);
				if (url==null)
					url=newPriceUrl(Constants.SOURCEYAHOOHIST,yahooHistUUID,ticker,type,lastPriceDate);
				else
					url = url+addPriceUrl(ticker,type,lastPriceDate);
				if (listener !=null)
					listener.started(price.getTicker(),yahooHistUUID);
			}
			Main.context.showURL(url);		
			debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.INFO, "URI "+url);
		}
		if (ftStocksList.size() > 0) {
			String url=null;
			String type;
			String ticker;
			for (SecurityPrice price : ftStocksList){
				if (price.isCurrency()) {
					type = Constants.CURRENCYTYPE;
					String currency = price.getCurrency();
					if (currency.endsWith("-"))
						ticker = currency+baseCurrencyID;
					else
						ticker = baseCurrencyID+currency;
				}
				else {
					type = Constants.STOCKTYPE;
					ticker = price.getTicker();
					String newTicker = params.getNewTicker(ticker,price.getExchange(), Constants.FTINDEX);
					if (!newTicker.equals(ticker)){
						alteredTickers.put(newTicker,ticker);
						debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.DETAILED, "FT Ticker changed from "+ticker+" to "+newTicker);                
						ticker = newTicker;
				}
				}
				if (url==null)
					url=newPriceUrl(Constants.SOURCEFT,ftUUID,ticker,type,-1);
				else
					url = url+addPriceUrl(ticker,type,-1);
				if (listener !=null)
					listener.started(price.getTicker(),ftUUID);
			}
			Main.context.showURL(url);
			debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.INFO, "URI "+url);
		}
		if (ftHistStocksList.size() > 0) {
			String url=null;
			String type;
			String ticker;
			String oldTicker;
			for (SecurityPrice price : ftHistStocksList){
				if (price.isCurrency()) {
					type = Constants.CURRENCYTYPE;
					String currency = price.getCurrency();
					if (currency.endsWith("-"))
						ticker = currency+baseCurrencyID;
					else
						ticker = baseCurrencyID+currency;
					oldTicker = ticker;
				}
				else {
					type = Constants.STOCKTYPE;
					ticker = price.getTicker();
					oldTicker = ticker;
					String newTicker = params.getNewTicker(ticker,price.getExchange(), Constants.FTHISTINDEX);
					if (!newTicker.equals(ticker)){
						alteredTickers.put(newTicker,ticker);
						debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.DETAILED, "FT Hist Ticker changed from "+ticker+" to "+newTicker);                
						ticker = newTicker;
				}
				}
				Integer lastPriceDate = datesTab.get(oldTicker);
				if (url==null)
					url=newPriceUrl(Constants.SOURCEFTHIST,ftHistUUID,ticker,type,lastPriceDate);
				else
					url = url+addPriceUrl(ticker,type,lastPriceDate);
				if (listener !=null)
					listener.started(price.getTicker(),ftHistUUID);
			}
			Main.context.showURL(url);
			debugInst.debug("loadPricesWindow", "getPrices", MRBDebug.INFO, "URI "+url);
		}
	}

	protected String newPriceUrl(String source, String tid, String stock, String type,Integer lastPriceDate) {
		String queries = addPriceUrl(stock, type,lastPriceDate );
		String command = Constants.GETQUOTECMD+"?" + Constants.SOURCETYPE+"="+source;
		command +="&"+Constants.TIDCMD+"="+tid;
		String url = "moneydance:fmodule:" + Main.extension.serverName+ ":" + command+queries;
		return url;
	}	
	protected String addPriceUrl(String stock, String type,Integer lastPriceDate){
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair(type, stock));
		if (lastPriceDate >-1 && params.getHistory()) {
			parameters.add(new BasicNameValuePair(Constants.LASTPRICEDATETYPE,lastPriceDate.toString()));
		}
		String charset = "UTF8";
		String queries = URLEncodedUtils.format(parameters, charset);
		return "&"+queries;
	}
	public void testTicker(String url){
		debugInst.debug("loadPricesWindow", "testTicker", MRBDebug.INFO, "URI "+url);
		URI uri=null;
		String convUrl = url.replace("^", "%5E");
		try {
			uri = new URI(convUrl.trim());
		} catch (URISyntaxException e) {
			debugInst.debug("loadPricesWindow", "testTicker", MRBDebug.DETAILED, "URI invalid "+convUrl);
			e.printStackTrace();
			return;
		}
		List<NameValuePair> results = URLEncodedUtils.parse(uri, charSet);
		String ticker = "";
		String source = "";
		for (NameValuePair price :results){
			if (price.getName().compareToIgnoreCase(Constants.STOCKTYPE) == 0) {
				ticker = price.getValue();
			}
			if (price.getName().compareToIgnoreCase(Constants.SOURCETYPE) == 0) {
				source = price.getValue();
			}
		}
		if (!ticker.isEmpty()){
			String tid = UUID.randomUUID().toString();
			testTicker = ticker;
			String testurl=newPriceUrl(source,tid,ticker,Constants.STOCKTYPE,0);
			debugInst.debug("loadPricesWindow", "testTicker", MRBDebug.DETAILED, "URI "+testurl);
			Main.context.showURL(testurl);
		}
	}
	public synchronized void updatePrices (String url) {
		String uuid = "";
		debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.INFO, "URI "+url);
		URI uri=null;
		String convUrl = url.replace("^", "%5E");
		try {
			
			uri = new URI(convUrl.trim());
		} catch (URISyntaxException e) {
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "URI invalid "+convUrl);
			e.printStackTrace();
			return;
		}
		/*
		 * capture data from returned url
		 */
		List<NameValuePair> results = URLEncodedUtils.parse(uri, charSet);
		SecurityPrice newPrice=null;
		NumberFormat nf = NumberFormat.getNumberInstance();
		for (NameValuePair price :results){
			if (price.getName().compareToIgnoreCase(Constants.STOCKTYPE) == 0) {
				newPrice = new SecurityPrice(price.getValue());
			}
			if (price.getName().compareToIgnoreCase(Constants.CURRENCYTYPE) == 0) {
				String ticker = price.getValue();
				if (ticker.endsWith(Constants.CURRENCYTICKER))
					ticker = Constants.CURRENCYID+ticker.substring(3, ticker.indexOf(Constants.CURRENCYTICKER));
				else {
					if (ticker.contains("-")) {
						ticker = Constants.CURRENCYID+ticker.substring(0, ticker.indexOf('-')+1);
					}
					else
						ticker = Constants.CURRENCYID+ticker.substring(3);
				}

				newPrice = new SecurityPrice(ticker);
			}
			if (price.getName().compareToIgnoreCase(Constants.PRICETYPE) == 0) {
				if (newPrice != null) {
					String priceStr = price.getValue();
					ParsePosition pos = new ParsePosition(0);
					Number newNum = nf.parse(priceStr,pos);
					if (pos.getIndex() != priceStr.length()) {
						debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.INFO, "Invalid price returned for "+newPrice.getTicker()+" "+priceStr);
						newPrice.setSecurityPrice(0.0);
					}
					else
						newPrice.setSecurityPrice(newNum.doubleValue());
				}
			}
			if (price.getName().compareToIgnoreCase(Constants.TRADEDATETYPE) == 0) {
				String isoDate = price.getValue();
				String tradeDate = isoDate.substring(0, isoDate.indexOf("T"));
				tradeDate = tradeDate.replaceAll("-", "");
				debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "Trade Dates Sent="+isoDate+" extracted="+tradeDate);                
				newPrice.setTradeDate(Integer.parseInt(tradeDate));
			}
			if (price.getName().compareToIgnoreCase(Constants.TRADECURRTYPE) == 0) {
				newPrice.setCurrency(price.getValue());
			}
			if (price.getName().compareToIgnoreCase(Constants.TIDCMD) == 0) {
				uuid = price.getValue();
			}
			if (price.getName().compareToIgnoreCase(Constants.VOLUMETYPE) == 0) {
				newPrice.setVolume(Long.parseLong(price.getValue()));
			}
			
		}
		/*
		 * data extracted test for test ticker
		 */
		if (newPrice.getTicker().equals(testTicker)){
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "Test Ticker returned");                
			testTicker = "";
			String message = "Test of security "+newPrice.getTicker()+ " was successful. Price "+newPrice.getSecurityPrice()+" Currency "+newPrice.getCurrency();
			JOptionPane.showMessageDialog(null,message);
			return;
		}
		/*
		 * check for altered by exchange
		 */
		if (alteredTickers != null && alteredTickers.containsKey(newPrice.getTicker())) {
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "Ticker changed from "+newPrice.getTicker()+" to "+alteredTickers.get(newPrice.getTicker()));                
			newPrice.setTicker(alteredTickers.get(newPrice.getTicker()));
		}
		if (completed) {
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.INFO, "Late message");
			return;
		}
		if (listener ==null) {
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.INFO, "Update received after close "+uuid);                
			return;
		}
		if (!listener.checkTid(uuid)) {
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.INFO, "Update received after close "+uuid);                
			return;
		}
		if(newPrice.getSecurityPrice() == 0.0) {
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.INFO, "No price returned for "+newPrice.getTicker());                
			return;			
		}

		/*
		 * we have a price
		 */
		debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.SUMMARY, "Updating prices for "+newPrice.getTicker());
		String ticker = newPrice.getTicker();
		if (runtype != Constants.MANUALRUN && runtype !=0) {
			if (newPrice.isCurrency())
				Main.context.showURL("moneydance:setprogress?meter=0&label=Quote Loader price "+newPrice.getCurrency()+" updated");
			else
				Main.context.showURL("moneydance:setprogress?meter=0&label=Quote Loader price "+ticker+" updated");
		}
		Double dRate = 1.0;
		CurrencyType securityCur = null;
		String tradeCur = newPrice.getCurrency();
		Double stockPrice;
		/*
		 * check to see if trade currency is in the pseudocurrency file
		 */
		if (pseudoCurrencies.containsKey(tradeCur)) {
			PseudoCurrency line = pseudoCurrencies.get(newPrice.getCurrency());
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.SUMMARY, "Pseudo Currency Detected "+newPrice.getCurrency()+" for "+newPrice.getTicker());
			Double oldValue = newPrice.getSecurityPrice();
			stockPrice = oldValue * line.getMultiplier();
			tradeCur=line.getReplacement();
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.SUMMARY, "Price changed from "+oldValue+" to "+stockPrice);
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.SUMMARY, "Currency changed from "+line.getPseudo()+" to "+line.getReplacement());
		}
		else
			stockPrice = newPrice.getSecurityPrice();
		/*
		 * get currency type for trade
		 */
		CurrencyType tradeCurType = Main.context.getCurrentAccountBook().getCurrencies().getCurrencyByIDString(tradeCur);
		if (tradeCurType == null)
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "No Price currency ");
		else
			debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "Price currency "+tradeCurType.getIDString());
		/*
		 * check trade for stock/currency
		 */
		if (!newPrice.isCurrency()) {
			/*
			 * trade is stock
			 */
			if (accountsTab.containsKey(ticker)) {
				securityCur = accountsTab.get(ticker).getRelativeCurrencyType();
				if (securityCur !=null)
					debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "Relative currency "+securityCur.getIDString());
				else
					debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "No Relative currency ");
			}
			int currencyDate;
			currencyDate = newPrice.getTradeDate();
			if (tradeCurType !=null &&
						securityCur !=null) {
				if(!tradeCurType.equals(securityCur)) {
					/*
					 * trade currency is not security currency
					 */
					dRate = CurrencyUtil.getUserRate(tradeCurType, securityCur, currencyDate);		
					debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "quote to security rate "+dRate);
				}
			}
			else {
				/*
				 * following code commented out for MD 2019, no longer stored in base rate
				 */
//			if(tradeCurType != null &&
//						!tradeCurType.equals(baseCurrency)) {
					/*
					 * assume security is same as base currency, trade currency is different For 2019 do not multiply
					 */
//					dRate = CurrencyUtil.getUserRate(tradeCurType, baseCurrency,currencyDate);							
//					debugInst.debug("loadPricesWindow", "updatePrices", MRBDebug.DETAILED, "quote to base rate "+dRate);
//				}
			}
		}
		else
			stockPrice= newPrice.getSecurityPrice();
		/*
		 * Assume the price will be displayed in the currency of the security
		 */
		multiplier = Math.pow(10.0,Double.valueOf(params.getDecimal()));
		if (!newPrice.isCurrency()) {
			debugInst.debug("loadPricesWindow", "updatePrice", MRBDebug.DETAILED, "before rounding"+stockPrice);
			stockPrice = Math.round(stockPrice*multiplier)/multiplier;
			debugInst.debug("loadPricesWindow", "updatePrice", MRBDebug.DETAILED, "after rounding"+stockPrice);
		}
		if (stockPrice != 0.0) {
			if (newPrice.isCrypto()) {
				if (newPricesTab.containsKey(ticker))
					newPricesTab.replace(ticker, 1/Util.safeRate(stockPrice));
				else
					newPricesTab.put(ticker, 1/Util.safeRate(stockPrice));
			}
			else {
				if (newPricesTab.containsKey(ticker))
					newPricesTab.replace(ticker, stockPrice*dRate);
				else
					newPricesTab.put(ticker, stockPrice*dRate);
			}
			if (newTradeDate.containsKey(ticker))
				newTradeDate.replace(ticker, newPrice.getTradeDate());
			else
				newTradeDate.put(ticker, newPrice.getTradeDate());
			if (tradeCurr.containsKey(ticker))
				tradeCurr.replace(ticker, newPrice.getCurrency());
			else
				tradeCurr.put(ticker, newPrice.getCurrency());
			if (quotePrice.containsKey(ticker))
				quotePrice.replace(ticker, newPrice.getSecurityPrice());
			else
				quotePrice.put(ticker, newPrice.getSecurityPrice());
			if (volumes.containsKey(ticker))
				volumes.replace(ticker, newPrice.getVolume());
			else
				volumes.put(ticker, newPrice.getVolume());
		}
		if (listener !=null)
			listener.ended(newPrice.getTicker(),uuid);
		pricesModel.resetData(currentPriceTab,
				datesTab,
				accountsTab,
				currencyTab,
				newPricesTab,
				newTradeDate,
				tradeCurr,
				quotePrice,
				volumes);
		pricesModel.fireTableDataChanged();
	}
	public synchronized void updateHistory (String url) {
		String uuid = "";
		debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.INFO, "URI "+url);
		URI uri=null;
		String convUrl = url.replace("^", "%5E");
		try {
			
			uri = new URI(convUrl.trim());
		} catch (URISyntaxException e) {
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "URI invalid "+convUrl);
			e.printStackTrace();
			return;
		}
		/*
		 * capture data from returned url
		 */
		List<NameValuePair> results = URLEncodedUtils.parse(uri, charSet);
		SecurityPrice newPrice=null;
		NumberFormat nf = NumberFormat.getNumberInstance();
		for (NameValuePair price :results){
			if (price.getName().compareToIgnoreCase(Constants.STOCKTYPE) == 0) {
				newPrice = new SecurityPrice(price.getValue());
			}
			if (price.getName().compareToIgnoreCase(Constants.CURRENCYTYPE) == 0) {
				String ticker = price.getValue();
				if (ticker.endsWith(Constants.CURRENCYTICKER))
					ticker = Constants.CURRENCYID+ticker.substring(3, ticker.indexOf(Constants.CURRENCYTICKER));
				else {
					if (ticker.contains("-")) {
						ticker = Constants.CURRENCYID+ticker.substring(0, ticker.indexOf('-')+1);
					}
					else
						ticker = Constants.CURRENCYID+ticker.substring(3);
				}

				newPrice = new SecurityPrice(ticker);
			}
			if (price.getName().compareToIgnoreCase(Constants.PRICETYPE) == 0) {
				if (newPrice != null) {
					String priceStr = price.getValue();
					ParsePosition pos = new ParsePosition(0);
					Number newNum = nf.parse(priceStr,pos);
					if (pos.getIndex() != priceStr.length()) {
						debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.INFO, "Invalid price returned for "+newPrice.getTicker()+" "+priceStr);
						newPrice.setSecurityPrice(0.0);
					}
					else
						newPrice.setSecurityPrice(newNum.doubleValue());
				}
			}
			if (price.getName().compareToIgnoreCase(Constants.TRADEDATETYPE) == 0) {
				String tradeDate = price.getValue();
				newPrice.setTradeDate(Integer.parseInt(tradeDate));
			}
			if (price.getName().compareToIgnoreCase(Constants.TRADECURRTYPE) == 0) {
				newPrice.setCurrency(price.getValue());
			}
			if (price.getName().compareToIgnoreCase(Constants.TIDCMD) == 0) {
				uuid = price.getValue();
			}
			if (price.getName().compareToIgnoreCase(Constants.VOLUMETYPE) == 0) {
				newPrice.setVolume(Long.parseLong(price.getValue()));
			}
			
		}
		/*
		 * data extracted test for test ticker
		 */
		if (newPrice.getTicker().equals(testTicker)){
			testTicker = "";
			return;
		}
		/*
		 * check for altered by exchange
		 */
		if (alteredTickers != null && alteredTickers.containsKey(newPrice.getTicker())) {
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "Ticker changed from "+newPrice.getTicker()+" to "+alteredTickers.get(newPrice.getTicker()));                
			newPrice.setTicker(alteredTickers.get(newPrice.getTicker()));
		}
		/*
		 * history transactions are received after the main price and can be after all tasks completed.
		 */
		if(newPrice.getSecurityPrice() == 0.0) {
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.INFO, "No price returned for "+newPrice.getTicker());                
			return;			
		}

		/*
		 * we have a price
		 */
		debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.SUMMARY, "Updating history price for "+newPrice.getTicker());
		String ticker = newPrice.getTicker();
		Double dRate = 1.0;
		CurrencyType securityCur = null;
		String tradeCur = newPrice.getCurrency();
		Double stockPrice;
		/*
		 * check to see if trade currency is in the pseudocurrency file
		 */
		if (pseudoCurrencies.containsKey(tradeCur)) {
			PseudoCurrency line = pseudoCurrencies.get(newPrice.getCurrency());
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.SUMMARY, "Pseudo Currency Detected "+newPrice.getCurrency()+" for "+newPrice.getTicker());
			Double oldValue = newPrice.getSecurityPrice();
			stockPrice = oldValue * line.getMultiplier();
			tradeCur=line.getReplacement();
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.SUMMARY, "Price changed from "+oldValue+" to "+stockPrice);
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.SUMMARY, "Currency changed from "+line.getPseudo()+" to "+line.getReplacement());
		}
		else
			stockPrice = newPrice.getSecurityPrice();
		/*
		 * get currency type for trade
		 */
		CurrencyType tradeCurType = Main.context.getCurrentAccountBook().getCurrencies().getCurrencyByIDString(tradeCur);
		if (tradeCurType == null)
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "No Price currency ");
		else
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "Price currency "+tradeCurType.getIDString());
			/*
			 * trade is stock
			 */
		if (accountsTab.containsKey(ticker)) {
			securityCur = accountsTab.get(ticker).getRelativeCurrencyType();
			if (securityCur !=null)
				debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "Relative currency "+securityCur.getIDString());
			else
				debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "No Relative currency ");
		}
		int currencyDate;
		currencyDate = newPrice.getTradeDate();
		if (tradeCurType !=null &&
					securityCur !=null) {
			if(!tradeCurType.equals(securityCur)) {
				/*
				 * trade currency is not security currency
				 */
				dRate = CurrencyUtil.getUserRate(tradeCurType, securityCur, currencyDate);		
				debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "quote to security rate "+dRate);
			}
			}
		multiplier = Math.pow(10.0,Double.valueOf(params.getDecimal()));
		if (!newPrice.isCurrency()) {
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "before rounding"+stockPrice);
			stockPrice = Math.round(stockPrice*multiplier)/multiplier;
			debugInst.debug("loadPricesWindow", "updateHistory", MRBDebug.DETAILED, "after rounding"+stockPrice);
		}
		if (stockPrice != 0.0) {
			stockPrice = stockPrice*dRate;
			if (newPrice.isCrypto())
				stockPrice = 1/Util.safeRate(stockPrice);
			List<HistoryPrice> historyList = historyTab.get(newPrice.getTicker());
			if (historyList == null) {
				historyList = new ArrayList<HistoryPrice>();
				historyTab.put(newPrice.getTicker(), historyList);
			}
			HistoryPrice history = new HistoryPrice(newPrice.getTradeDate(),stockPrice, newPrice.getVolume());
			historyList.add(history);
			historyTab.replace(newPrice.getTicker(), historyList);
		}
		pricesModel.resetHistory(historyTab);
		pricesModel.fireTableDataChanged();
	}
	/*
	 * Failed quote
	 */
	public synchronized void failedQuote(String url){
		String uuid = "";
		debugInst.debug("loadPricesWindow", "failedQuote", MRBDebug.INFO, "URI "+url);
		/* 
		 * if completed set, ignore message
		 */
		if (completed) {
			debugInst.debug("loadPricesWindow", "failedQuote", MRBDebug.INFO, "Late message");
			return;
		}
		URI uri=null;
		String convUrl = url.replace("^", "%5E");
		try {
			uri = new URI(convUrl.trim());
		} catch (URISyntaxException e) {
			debugInst.debug("loadPricesWindow", "failedQuote", MRBDebug.SUMMARY, "URI invalid "+convUrl);
			e.printStackTrace();
			return;
		}
		List<NameValuePair> results = URLEncodedUtils.parse(uri, charSet);
		String ticker="";
		for (NameValuePair price :results){
			if (price.getName().compareToIgnoreCase(Constants.STOCKTYPE) == 0) {
				ticker = price.getValue();
				if (alteredTickers != null && alteredTickers.containsKey(ticker)) {
					ticker=alteredTickers.get(ticker);
					debugInst.debug("loadPricesWindow", "failed ", MRBDebug.DETAILED, "Ticker changed from "+price.getValue()+" to "+ticker);                
				}
			}
			if (price.getName().compareToIgnoreCase(Constants.CURRENCYTYPE) == 0) {
				ticker = price.getValue();
				debugInst.debug("loadPricesWindow", "failedQuote", MRBDebug.DETAILED, "Currency Ticker "+ticker);
				if (ticker.endsWith(Constants.CURRENCYTICKER))
					ticker = Constants.CURRENCYID+ticker.substring(3, ticker.indexOf(Constants.CURRENCYTICKER));
				else {
					if (ticker.contains("-"))
						ticker = Constants.CURRENCYID+ticker.substring(0, ticker.indexOf('-')+1);
					else
						ticker = Constants.CURRENCYID+ticker.substring(3);
				}	         
				debugInst.debug("loadPricesWindow", "failedQuote", MRBDebug.DETAILED, "Amended Currency Ticker "+ticker);
			}
			if (price.getName().compareToIgnoreCase(Constants.TIDCMD) == 0) {
				uuid = price.getValue();
			}
		}
		if (ticker.equals(testTicker)){
			testTicker = "";
			String message = "Test of security "+ticker+ " failed.";
			JOptionPane.showMessageDialog(null,message);
			return;
		}
		errorsFound = true;
		if(errorTickers!= null)
			errorTickers.add(ticker);
		if (listener !=null)
			listener.failed(ticker,uuid);
	}
	public synchronized void doneQuote(String url){
		int totalQuotes = 0;
		int successful=0;
		int failed = 0;
		/* 
		 * if completed set, ignore message
		 */
		if (completed) {
			debugInst.debug("loadPricesWindow", "doneQuote", MRBDebug.INFO, "Late message");
			return;
		}
		String uuid = "";
		URI uri=null;
		try {
			uri = new URI(url.trim());
		} catch (URISyntaxException e) {
			debugInst.debug("loadPricesWindow", "doneQuote", MRBDebug.DETAILED, "URI invalid "+url);
			e.printStackTrace();
			return;
		}
		List<NameValuePair> results = URLEncodedUtils.parse(uri, charSet);
		for (NameValuePair price :results){
			if (price.getName().compareToIgnoreCase(Constants.TOTALTYPE) == 0) {
				totalQuotes = Integer.parseInt(price.getValue());
			}
			if (price.getName().compareToIgnoreCase(Constants.OKTYPE) == 0) {
				successful = Integer.parseInt(price.getValue());
			}
			if (price.getName().compareToIgnoreCase(Constants.ERRTYPE) == 0) {
				failed = Integer.parseInt(price.getValue());
			}
			if (price.getName().compareToIgnoreCase(Constants.TIDCMD) == 0) {
				uuid = price.getValue();
			}
		}
		debugInst.debug("loadPricesWindow", "doneQuote", MRBDebug.INFO, "Finished quote "+uuid);
		if (listener !=null)
			listener.done(uuid,totalQuotes,successful,failed);

	}
	/*
	 * Listener for when tasks finished
	 */
	@Override
	public synchronized void  TasksCompleted () {
		if (!completed){
			debugInst.debug("loadPricesWindow", "TaskCompleted", MRBDebug.INFO, "Tasks Completed");
			completed=true;
			if (runtype == Constants.MANUALRUN) {
				tasksProgress.setValue(100);
				JOptionPane.showMessageDialog(null,"Prices Loaded");	
				tasksProgress.setVisible(false);
				panBot.remove(tasksProgress);
				getPricesBtn.setEnabled(true);
				pricesModel.fireTableDataChanged();
				this.revalidate();
			}
			if (runtype != Constants.MANUALRUN  && runtype !=0){
				if (errorsFound){
					JOptionPane.showMessageDialog(null,"Errors found on automatic run.  Look at 'Price Date' to determine which lines have not been updated");
					errorsFound = false;
				}
				debugInst.debug("AutomaticRun", "AutomaticRun", MRBDebug.DETAILED, "set saveall");		
				pricesModel.selectAll(true);
				main.errorTickers =errorTickers;
				debugInst.debug("AutomaticRun", "AutomaticRun", MRBDebug.DETAILED, "save data");
				save();
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.AUTODONECMD);
					}
				});
			}
		}
	}
	@Override
	public synchronized void Update() {
		debugInst.debug("loadPricesWindow", "Update", MRBDebug.DETAILED, "Progress Bar Updated");
		pricesModel.fireTableDataChanged();			 
		this.revalidate();	
	}
	public synchronized boolean checkProgress() {
		for (Entry<String,Integer> status : tickerStatus.entrySet()){
			if (status.getValue() == Constants.TASKSTARTED){
				debugInst.debug("loadPricesWindow", "checkProgress", MRBDebug.SUMMARY, "Quote "+status.getKey()+" has not finished");
				return false;
			}
		}
		return true;
	}
	public synchronized void closeQuotes() {
		for (Entry<String,Integer> status : tickerStatus.entrySet()){
			if (status.getValue() == Constants.TASKSTARTED){
				if(listener !=null)
					listener.failed(status.getKey());
			}
		}
		
	}

	/*
	 * preferences
	 */
	protected void setPreferences() {
		iFRAMEWIDTH = Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH,Constants.FRAMEWIDTH);
		iFRAMEDEPTH = Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEDEPTH,Constants.FRAMEHEIGHT);
	}

	protected void updatePreferences(Dimension objDim) {
		Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH, objDim.width);
		Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEDEPTH, objDim.height);
		Main.preferences.isDirty();
	}
	@Override
	public void actionPerformed(ActionEvent aeMenu) {
		JMenuItem miSource= null;
		JRadioButtonMenuItem radioItem = null;
		JCheckBoxMenuItem checkBox = null;
		if (aeMenu.getSource() instanceof JMenuItem)
			miSource = (JMenuItem) aeMenu.getSource();
		if (aeMenu.getSource() instanceof JRadioButtonMenuItem)
			radioItem = (JRadioButtonMenuItem) aeMenu.getSource();
		if (aeMenu.getSource() instanceof JCheckBoxMenuItem)
			checkBox = (JCheckBoxMenuItem) aeMenu.getSource();
		if (miSource == onlineMenu) {
			String url = "https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote%20Loader";

			if(Desktop.isDesktopSupported()){
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(new URI(url.trim()));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}else{
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("xdg-open " + url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (radioItem == offMItem){
			debugInst.setDebugLevel(MRBDebug.OFF);
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, debugInst.getDebugLevel());
			Main.preferences.isDirty();
			offMItem.setSelected(true);
		}
		if (radioItem == infoMItem){
			debugInst.setDebugLevel(MRBDebug.INFO);
			debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.INFO, "Debug turned to Info");
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, debugInst.getDebugLevel());
			Main.preferences.isDirty();
			infoMItem.setSelected(true);
		}
		if (radioItem == summMItem){
			debugInst.setDebugLevel(MRBDebug.SUMMARY);
			debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.SUMMARY, "Debug turned to Summary");
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, debugInst.getDebugLevel());
			Main.preferences.isDirty();
			summMItem.setSelected(true);
		}
		if (radioItem == detMItem){
			debugInst.setDebugLevel(MRBDebug.DETAILED);
			debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.DETAILED, "Debug turned to Detailed");
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, debugInst.getDebugLevel());
			Main.preferences.isDirty();
			detMItem.setSelected(true);
		}
		if (checkBox == exportSaveItem){
			debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.DETAILED, "Export on Save changed");
			if (params.isExport()) {
				debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.DETAILED, "Export on Save off");
				checkBox.setSelected(false);
			}
			else {
				debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.DETAILED, "Export on Save on");
				checkBox.setSelected(true);
			}
			params.setExport(checkBox.isSelected());
		}
		if (checkBox == exportSaveAutoItem){
			debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.DETAILED, "Export on Auto changed");
			if (params.isExportAuto())
				checkBox.setSelected(false);
			else
				checkBox.setSelected(true);
			params.setExportAuto(checkBox.isSelected());
		}
		if (miSource == selectFolderItem){
			debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.DETAILED, "Choose folder");
			chooseFile();
			params.setExportFolder(exportFolder);
			fileName.setText("Export Folder : "+params.getExportFolder());
		}
		



	}
}


