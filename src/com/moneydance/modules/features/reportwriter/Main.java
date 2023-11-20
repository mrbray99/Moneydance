/*
 * Copyright (c) 2020, Michael Bray.  All rights reserved.
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
package com.moneydance.modules.features.reportwriter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.AccountIterator;
import com.infinitekind.moneydance.model.AccountListener;
import com.infinitekind.moneydance.model.Budget;
import com.infinitekind.moneydance.model.BudgetList;
import com.infinitekind.moneydance.model.BudgetListener;
import com.infinitekind.moneydance.model.CurrencyListener;
import com.infinitekind.moneydance.model.CurrencyTable;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.moneydance.model.InvestTxnType;
import com.infinitekind.moneydance.model.TxnUtil;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBDirectoryUtils;
import com.moneydance.modules.features.mrbutil.MRBFXSelectionRow;
import com.moneydance.modules.features.mrbutil.MRBPreferences2;
import com.moneydance.modules.features.reportwriter.factory.OutputCSV;
import com.moneydance.modules.features.reportwriter.factory.OutputDatabase;
import com.moneydance.modules.features.reportwriter.factory.OutputFactory;
import com.moneydance.modules.features.reportwriter.factory.OutputSpreadsheet;
import com.moneydance.modules.features.reportwriter.sandbox.MyJarLauncher;
import com.moneydance.modules.features.reportwriter.view.AccelKeys;
import com.moneydance.modules.features.reportwriter.view.MyReport;
import com.moneydance.modules.features.reportwriter.view.ReportDataRow;
import com.moneydance.modules.features.reportwriter.Utilities.FxDatePickerConverter;

import javafx.application.Platform;
import javafx.scene.Scene;



/** 
 * Generalized Moneydance extension to extract data
 * <p>
 * Main class to create main window
 * @author Mike Bray
 */

public class Main extends FeatureModule implements AccountListener, BudgetListener, CurrencyListener
{
	public static String minorBuildNo = "00";
	public static String databaseChanged = "20210121";
	private static MyJarLauncher launcher=null;
	private static MyJarLauncher dbLauncher=null;

	public static SimpleDateFormat cdate;
	public static DateTimeFormatter cdateFX;
	public static ZoneId zone;
	public static String datePattern;
	public static FxDatePickerConverter dateConverter;
	public static AccelKeys accels;
	public List<MRBFXSelectionRow> currencies;
	public List<MRBFXSelectionRow> transferTypes;
	public List<MRBFXSelectionRow> bankAccounts;
	public List<MRBFXSelectionRow> assetAccounts;
	public List<MRBFXSelectionRow> liabilityAccounts;
	public List<MRBFXSelectionRow> creditAccounts;
	public List<MRBFXSelectionRow> loanAccounts;
	public List<MRBFXSelectionRow> investmentAccounts;
	public List<MRBFXSelectionRow> securityAccounts;
	public List<MRBFXSelectionRow> incomeCategories;
	public List<MRBFXSelectionRow> expenseCategories;
	public List<MRBFXSelectionRow> tags;
	public List<MRBFXSelectionRow> securities;
	public List<MRBFXSelectionRow> budgets;
	public static Date now;
	public static char decimalChar;
	public static FeatureModuleContext context;
	public static AccountBook book;
	public static UserPreferences up;
	public static CurrencyType baseCurrency;
	public static MRBDebug rwDebugInst;
	public static Main extension;
	public static String buildNo;
	public static MyReport frameReport=null;
	public static JFrame frame;
	public static Images loadedIcons;
	private JFrame progressFrame;
	private JScrollPane progressScroll;
	private JTextArea progressArea;
	private String progressText;
	private String uri;
	private String command;
	private String extensionDir;
	public static Image mainIcon;
	public static MRBPreferences2 preferences;
	public static ClassLoader loader;
	private Database database;
	private static Scene scene;
	public int SCREENWIDTH;
	public int SCREENHEIGHT;
	public static Font labelFont;
	private boolean extensionOpen = false;
	/*
	 * Called when extension is loaded<p>
	 * Need to register the feature and the URI command to be called 
	 * when the user selects the extension.
	 * 
	 * normally "showconsole"
	 */
	@Override
	public void init() {
		// the first thing we will do is register this module to be invoked
		// via the application toolbar
		extension = this;
		context = getContext();
		int iBuild = getBuild();
		buildNo = String.valueOf(iBuild);  
		mainIcon = getIcon("mrb icon2.png");
		try {
			context.registerFeature(this, "showconsole",
					mainIcon,
					getName());
			rwDebugInst = new MRBDebug();
			rwDebugInst.setExtension(Constants.EXTENSIONNAME);
			rwDebugInst.setDebugLevel(MRBDebug.INFO);
			rwDebugInst.debug(Constants.EXTENSIONNAME, "Init", MRBDebug.INFO, "Started Build "+buildNo+"."+minorBuildNo);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		up = UserPreferences.getInstance();
		datePattern = up.getSetting(UserPreferences.DATE_FORMAT);
		cdate = new SimpleDateFormat(datePattern);
		cdateFX = DateTimeFormatter.ofPattern(datePattern);
		dateConverter = new FxDatePickerConverter();
		zone= ZoneId.systemDefault();
		now = new Date();
		decimalChar = up.getDecimalChar();
		loadedIcons = new Images();
		labelFont = UIManager.getFont("Label.font");
		/*
		 * Need to ensure Jasper Server is available in the .moneydance/fmodule/.reportwriter folder
		 * 
		 */
		if (!setReportDirectory()) {
			JOptionPane.showMessageDialog(null,"Problem loading Report Writer. Look at the Console Log for more detail");
		}
	}
	/**
	 * retrieves an image from within the .mxt file.  Must be included when the extension 
	 * is compiled
	 * @param action the name of the image to load
	 * @return 	the image		
	 */
	public Image getIcon(String resource) {
		try {
			loader = getClass().getClassLoader();
			java.io.InputStream in = 
					loader.getResourceAsStream(Constants.RESOURCES+resource);
			if (in != null) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
				byte buf[] = new byte[256];
				int n = 0;
				while((n=in.read(buf, 0, buf.length))>=0)
					bout.write(buf, 0, n);
				return Toolkit.getDefaultToolkit().createImage(bout.toByteArray());
			}
		} catch (Throwable e) { }
		return null;
	}
	/*
	 * Need to capture MD calling cleanup so FX page is closed
	 */
	@Override
	public void cleanup() {
		rwDebugInst.debug("ReportWriter", "cleanup", MRBDebug.SUMMARY, "cleanup  ");
		closeConsole();
		extensionOpen= false;
	}
	@Override
	public void unload() {
		rwDebugInst.debug("ReportWriter", "unload", MRBDebug.SUMMARY, "unload  ");
		super.unload();
		closeConsole();
		extensionOpen= false;
	}
	@Override
	public void handleEvent(String appEvent) {
		super.handleEvent(appEvent);
		rwDebugInst.debug("Main", "HandleEvent", MRBDebug.SUMMARY, "Event "+appEvent);       
		if (appEvent.compareToIgnoreCase("md:file:opening") == 0) {
			handleEventFileOpening();
		} else if (appEvent.compareToIgnoreCase("md:file:opened") == 0) {
			handleEventFileOpened();
		} else if (appEvent.compareToIgnoreCase("md:file:closing") == 0) {
			handleEventFileClosed();
		}
	}

	protected void handleEventFileOpening() {
		rwDebugInst.debug("Main","HandleEventFileOpening", MRBDebug.SUMMARY, "Opening ");
	}

	protected void handleEventFileOpened() {
		rwDebugInst.debug("Main", "HandleEventFileOpened", MRBDebug.INFO, "File Opened");
		if (!extensionOpen) {
			MRBPreferences2.forgetInstance();
//			context = getContext();
			book=context.getCurrentAccountBook();
			MRBPreferences2.loadPreferences(context);
			preferences = MRBPreferences2.getInstance();		
			rwDebugInst.setDebugLevel(preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, MRBDebug.INFO));
			String debug="OFF";
			if (rwDebugInst.getDebugLevel()==MRBDebug.INFO)
				debug = "INFO";
			else if (rwDebugInst.getDebugLevel()==MRBDebug.SUMMARY)
				debug = "SUMM";
			else if (rwDebugInst.getDebugLevel()== MRBDebug.DETAILED)
				debug = "DET";
			else
				debug = "OFF";	
			rwDebugInst.debug("ReportWriter", "HandleEventFileOpened", MRBDebug.INFO, "Debug level set to "+debug);
		}
		book.addAccountListener(this);
		book.getBudgets().addListener(this);
		book.getCurrencies().addCurrencyListener(this);
	}
	private boolean setReportDirectory() {
		File extensionData = MRBDirectoryUtils.getExtensionDataDirectory(Constants.PROGRAMNAME);
		extensionDir = extensionData.getAbsolutePath();
		Boolean fileFound = false;
		Boolean dbFound = false;
		String fileVersion=null;
		String fileName="";
		if (extensionData != null && extensionData.exists()) {
			rwDebugInst.debug("Main", "setReportDirectory", MRBDebug.SUMMARY, "Extension directory found");
			String [] filenames = extensionData.list();
			for (String jarFile : filenames) {
				rwDebugInst.debug("Main", "setReportDirectory", MRBDebug.SUMMARY, "File "+jarFile);
				if (jarFile.startsWith("MDJasper") && jarFile.endsWith(".jar")) {
					fileFound = true;
					int dash = jarFile.indexOf('-');
					if (fileVersion == null) {
						fileVersion = jarFile.substring(dash+1,dash+5);
						fileName=jarFile;
					}
					else {
						if(fileVersion.compareTo(jarFile.substring(dash+1, dash+5))<0) {
							fileVersion = jarFile.substring(dash+1,dash+5);
							fileName=jarFile;							
						}
					}
					continue;
				}
				if (jarFile.startsWith("h2-") && jarFile.endsWith(".jar")) {
					dbFound = true;
					continue;
				}
					
				if (jarFile.endsWith(".java")) {
					File deleteFile = new File(extensionDir+"/"+jarFile);
					try {
						deleteFile.delete();
					}
					catch (Exception e) {
						e.printStackTrace();
						rwDebugInst.debug("Main", "setReportDirectory", MRBDebug.SUMMARY, "Error deleting temp file "+jarFile);
						return false;
					}
				}
					
			}
		}
		else {
			rwDebugInst.debug("Main", "setReportDirectory", MRBDebug.SUMMARY, "Extension directory not found");
			return false;
		}
		if (fileFound && fileVersion.compareTo(Constants.JASPERJAR.substring(15,19)) < 0) {
			File oldJar = new File(extensionDir+"/"+fileName);
			if (oldJar.exists())
				oldJar.delete();
			fileFound = false;
		}
		if (!fileFound) {
			InputStream stream = this.getClass().getResourceAsStream(Constants.RESOURCES+Constants.JASPERJAR);
			if (stream != null) {
				try {
					String newName = extensionDir+"/"+Constants.JASPERJAR;
					newName = newName.replace("jarsav", "jar");
					Files.copy(stream, Paths.get(newName),StandardCopyOption.REPLACE_EXISTING);
				}
				catch (IOException e) {
					e.printStackTrace();
					rwDebugInst.debug("Main", "setReportDirectory", MRBDebug.SUMMARY, "Error copying jar file ");					
					return false;
				}
			}
		}
		if (!dbFound) {
			InputStream stream = this.getClass().getResourceAsStream(Constants.RESOURCES+Constants.DATABASEJAR);
			String outputName = extensionDir+"/"+Constants.DATABASEJAR;
			outputName = outputName.replace(".jarsav", ".jar");
			if (stream != null) {
				try {
					Files.copy(stream, Paths.get(outputName),StandardCopyOption.REPLACE_EXISTING);
				}
				catch (IOException e) {
					e.printStackTrace();
					rwDebugInst.debug("Main", "setReportDirectory", MRBDebug.SUMMARY, "Error copying database jar file ");					
					return false;
				}
			}
				
		}
		return true;
	}

	protected void handleEventFileClosed() {
		rwDebugInst.debug("Main", "HandleEventFileClosed", MRBDebug.INFO, "Closing ");
		closeConsole();
	}
	/**
	 * Processes the uri from Moneydance.  Called by Moneydance
	 * <p>Commands:
	 * <ul>
	 * 	<li>showconsole - called when the user selects the extension
	 * <li>viewreport - View a report, must be done on AWT-Event-Queue
	 *   </ul>
	 *  @param uri		the uri from Moneydance
	 */
	@Override
	public void invoke(String urip) {
		if (book == null)
			book=context.getCurrentAccountBook();
		baseCurrency = book.getCurrencies().getBaseType();
		accels = new AccelKeys();
		if (preferences == null){
			MRBPreferences2.loadPreferences(context);
			preferences = MRBPreferences2.getInstance();
		}
		uri = urip;
		command = uri;
		int theIdx = uri.indexOf('?');
		if(theIdx>=0) {
			command = uri.substring(0, theIdx);
		}
		else {
			theIdx = uri.indexOf(':');
			if(theIdx>=0) {
				command = uri.substring(0, theIdx);
			}
		}
		/*
		 * showConsole will be on AWT-Event-Queue, all other commands will be on the thread of the calling
		 * program, make sure all commands are processed on the AWT-Event-Queue to preserve sequence
		 */
		rwDebugInst.debug("Main","invoke",MRBDebug.SUMMARY,"Command "+ command);
		switch (command) {
		case "showconsole" :
			showConsole();
			break;
		case Constants.VIEWREPORTCMD :
			viewReport(uri);
			break;
		case Constants.SHOWHELP :
			String url =Constants.HELPURL;

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
			break;
		}
	}
	@Override
	public String getName() {
		return Constants.EXTENSIONNAME;
	}
	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private void createAndShowGUI() {
		rwDebugInst.debug("ReportWriter", "createandShowGUI", MRBDebug.SUMMARY, "cleanup  ");
		if (launcher == null)
			initServices();
		if (extensionOpen && frame !=null) {
			frame.requestFocus();
			return;
		}
		collectData();
		book.addAccountListener(this);
		book.getBudgets().addListener(this);
		book.getCurrencies().addCurrencyListener(this);
		frame = new JFrame();
		frameReport = new MyReport();
		frame.setTitle(Constants.EXTENSIONNAME+" "+buildNo+"."+minorBuildNo);
		frame.setIconImage(mainIcon);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//Display the window.
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frameReport, 
						"Are you sure you want to close Report Writer?", "Close Window?", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					rwDebugInst.debug("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Yes");	        	
					closeConsole();
				}
			}
		});
		SCREENWIDTH =preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH,Constants.MAINSCREENWIDTH);
		rwDebugInst.debug("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Width "+SCREENWIDTH);
		SCREENHEIGHT =preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEHEIGHT,Constants.MAINSCREENHEIGHT);
		rwDebugInst.debug("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Height "+SCREENHEIGHT);
		frame.add(frameReport);
		frame.getContentPane().setPreferredSize(new Dimension(SCREENWIDTH,SCREENHEIGHT));
		frame.pack();
		rwDebugInst.debug("Main",  "createAndShowGUI", MRBDebug.SUMMARY, "frame "+frame.getWidth()+"/"+frame.getHeight());
		frame.setVisible(true);
		frame.setLocation(preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEX,0),preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEY,0));

		frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
			}
	
			@Override
			public void componentMoved(ComponentEvent e) {
				Component c = (Component)e.getSource();
				Point currentLocation = c.getLocationOnScreen();
				Main.rwDebugInst.debugThread("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Component moved "+currentLocation.x+"/"+currentLocation.y);
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEX, currentLocation.x);
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEY, currentLocation.y);
				Main.preferences.isDirty();
			}
	
			@Override
			public void componentShown(ComponentEvent e) {
			
			}
	
			@Override
			public void componentHidden(ComponentEvent e) {			
			}
		});
		Platform.runLater(new Runnable () {
			@Override
			public void run() {
				extensionOpen=true;
				initFX(frameReport);
			}
		});
	}
	/*
	 * Initiate JavaFX, this runs on the FX thread
	 */
	private static void initFX(MyReport fxPanel) {
		rwDebugInst.debugThread("ReportWriter", "initFX", MRBDebug.SUMMARY, "setting javafx scene");
		// This method is invoked on the JavaFX thread
		scene = fxPanel.createScene();
		fxPanel.setScene(scene);
		fxPanel.setSizes();
	}

	/**
	 * Starts the user interface for the extension
	 * 
	 * First it checks if Rhumba is present by sending a hello message to Rhumba
	 * @see #invoke(String)
	 */
	private synchronized void showConsole() {
		rwDebugInst.debug("Main", "showConsole", MRBDebug.INFO, "Show Console");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});

	}
	/**
	 * Get the extension context
	 * @return FeatureModuleContext context
	 */
	public FeatureModuleContext getUnprotectedContext() {
		return getContext();
	}
	/**
	 * closes the extension - need to close the FX Panel 
	 */
	public synchronized void closeConsole() {
		rwDebugInst.debug("Main", "closeConsole", MRBDebug.DETAILED, "closing Console ");
		extensionOpen=false;
		if (frameReport !=null) {
			frameReport.closeDown();
			Platform.exit();
			scene=null;
			frameReport=null;
		}
		if(frame != null){
			frame.setVisible(false);
			frame=null;
		}
		if (preferences != null) {
			MRBPreferences2.forgetInstance();
			preferences=null;
		}
		book=null;
		
	}

	/*
	 * collect the MD data required for the parameter and group panes
	 */
	private void collectData() {
		loadAccounts();
		loadBudgets();
		loadTags();
		loadCurrencies();
		InvestTxnType[] txnTypes = InvestTxnType.ALL_TXN_TYPES;
		transferTypes = new ArrayList<>();
		for (InvestTxnType type : txnTypes) {
			MRBFXSelectionRow row = new MRBFXSelectionRow(type.toString(),type.getIDString(), "Transfer Type", false);
			row.setDepth(0);
			transferTypes.add(row);
		}
	}
	private void loadBudgets(){
		budgets = new ArrayList<>();
		BudgetList budgetList = book.getBudgets();
		if (budgetList != null) {
			for (Budget budget : budgetList.getAllBudgets()) {
				MRBFXSelectionRow row = new MRBFXSelectionRow(budget.getUUID(),budget.toString(), "Budget", false);
				row.setDepth(0);
				budgets.add(row);
			}
		}

	}
	private synchronized void loadCurrencies() {
		currencies = new ArrayList<>();
		securities = new ArrayList<>();
		List<CurrencyType> currencyTable = book.getCurrencies().getAllCurrencies();
		for (CurrencyType type : currencyTable) {
			if (type.getCurrencyType()== CurrencyType.Type.CURRENCY) {
				MRBFXSelectionRow row = new MRBFXSelectionRow(type.getUUID(),type.getName()+"("+type.getIDString()+")", "Currency", false);
				row.setDepth(0);
				row.setInActive(type.getHideInUI());
				currencies.add(row);
			}
			if (type.getCurrencyType()== CurrencyType.Type.SECURITY) {
				MRBFXSelectionRow row = new MRBFXSelectionRow(type.getUUID(),type.getName()+"("+type.getIDString()+")", "Security", false);
				row.setDepth(0);
				row.setInActive(type.getHideInUI());
				securities.add(row);
			}
		}
		Collections.sort(currencies,new CompareCurrency());		
		Collections.sort(securities,new CompareCurrency());		
	}
	private synchronized void loadAccounts() {
		AccountIterator it = new AccountIterator(book);
		if (bankAccounts == null)
			bankAccounts = new ArrayList<>();
		else
			bankAccounts.clear();
		if (assetAccounts == null)
			assetAccounts = new ArrayList<>();
		else
			assetAccounts.clear();
		if (creditAccounts == null)
			creditAccounts = new ArrayList<>();
		else
			creditAccounts.clear();
		if (liabilityAccounts == null)
			liabilityAccounts = new ArrayList<>();
		else
			liabilityAccounts.clear();
		if (loanAccounts == null)
			loanAccounts = new ArrayList<>();
		else
			loanAccounts.clear();
		if (investmentAccounts == null)
			investmentAccounts = new ArrayList<>();
		else
			investmentAccounts.clear();
		if (securityAccounts == null)
			securityAccounts = new ArrayList<>();
		else
			securityAccounts.clear();
		if (incomeCategories == null)
			incomeCategories  = new ArrayList<>();
		else
			incomeCategories .clear();
		if (expenseCategories == null)
			expenseCategories = new ArrayList<>();
		else
			expenseCategories.clear();
		while (it.hasNext()) {
			Account acct = it.next();
			MRBFXSelectionRow row = new MRBFXSelectionRow(acct.getUUID(),acct.getAccountName(),"Account",false);
			row.setInActive(false);
			row.setSortText(acct.getFullAccountName());
			row.setDepth(0);
			switch (acct.getAccountType()) {
			case ASSET :
				row.setType("Asset");
				assetAccounts.add(row);
				break;
			case BANK :
				row.setType("Bank");
				bankAccounts.add(row);
				break;
			case CREDIT_CARD :
				row.setType("Credit Card");
				creditAccounts.add(row);
				break;
			case INVESTMENT :
				row.setType("Invest");
				investmentAccounts.add(row);
				securityAccounts.add(row);
				break;
			case LIABILITY :
				row.setType("Liability");
				liabilityAccounts.add(row);
				break;
			case LOAN :
				row.setType("Loan");
				loanAccounts.add(row);
				break;
			case INCOME:
				row.setText(acct.getIndentedName());
				row.setType("Income");
				row.setDepth(acct.getDepth());
				row.setInActive(acct.getAccountIsInactive());
				incomeCategories.add(row);
				break;
			case EXPENSE:
				row.setText(acct.getIndentedName());
				row.setType("Expense");
				row.setDepth(acct.getDepth());
				row.setInActive(acct.getAccountIsInactive());
				expenseCategories.add(row);
				break;
			case ROOT:
				break;
			case SECURITY:
				row.setText("   "+acct.getAccountName());
				row.setType("Security");
				row.setDepth(acct.getDepth());
				row.setInActive(acct.getCurrentBalance() == 0L);
				securityAccounts.add(row);
				break;
			default:
				break;
			}
		}
	}
	private void loadTags() {
		tags = new ArrayList<>();
		List<String> tagList = TxnUtil.getListOfAllUsedTransactionTags(book.getTransactionSet().getAllTxns());
		for (String tagStr : tagList) {
			tags.add(new MRBFXSelectionRow(tagStr,tagStr,"Tag",false));
			
		}
	}
	/*
	 * find Jasper Server jar and associate with a class loader.  This will create an instance of JasperReports and obtain 
	 * pointers to the methods
	 * 
	 */
	public void initServices() {
		rwDebugInst.debug("Main","initServices",MRBDebug.SUMMARY,"Initialising services");
		File launcherFile = Utilities.getLauncherFile();
		if ((launcherFile != null) && (launcherFile.exists())) {
			try {
				launcher = new MyJarLauncher(launcherFile);
				Main.setLauncher(launcher);	
				try {
				    File direct = MRBDirectoryUtils.getExtensionDataDirectory(Constants.PROGRAMNAME);
				    String [] args = {direct.getAbsolutePath(),Constants.JASPERJAR};
					Object [] foldersWrapper = {args};
					launcher.getSetExtension().invoke(launcher.getInstance(),foldersWrapper);
				}
				catch (Exception e) {
					OptionMessage.displayMessage("Error setting environment "+e.getLocalizedMessage());
					return;								
				}
				
				rwDebugInst.debug("Main","initServices",MRBDebug.SUMMARY,"launcher: " + getLauncher());
			} catch (Exception e) {
				rwDebugInst.debug("Main","initServices",MRBDebug.SUMMARY,e.getMessage());
				JOptionPane.showMessageDialog(null,"Error initialising Jasper Reports. View Console Log for more details");
			}
		}
	}
	public static MyJarLauncher getLauncher() {
		return launcher;
	}

	private static void setLauncher(MyJarLauncher launcherp) {
		launcher = launcherp;
	}
	public static MyJarLauncher getDbLauncher() {
		return dbLauncher;
	}

	/*
	 * running on EDT
	 * Obtains data and writes to an h2 database
	 * Compiles the Jasper Report producing {name}.jasper
	 * Fills it with data producing {name}.jrprint
	 * Displays the report
	 * 
	 * Output files are stored in the defined reports directory
	 * A temporary file with a .java extension is created in the extension data directory
	 */
	@SuppressWarnings("unused")
	private void viewReport(String uri)  {
		String name = uri.substring(uri.indexOf("?")+1);
		Parameters params = Parameters.getInstance();
		ReportDataRow rowEdit = new ReportDataRow();
		if (!rowEdit.loadRow(name, params)) {
			JOptionPane.showMessageDialog(null,"Report "+name+" not found");
			return;
		}
		OutputFactory output=null;
		displayProgressWindow();
		try {
			switch (rowEdit.getType()) {
			case DATABASE:
			case JASPER :
				output = new OutputDatabase(rowEdit,params);
				database = ((OutputDatabase)output).getDatabase();
				break;
			case SPREADSHEET :
				output = new OutputSpreadsheet(rowEdit,params);
				frameReport.resetData();
				output = null;
				closeProgressWindow();
				return;
			case CSV:
				output = new OutputCSV(rowEdit,params);
				frameReport.resetData();
				output = null;
				closeProgressWindow();
				return;
			}
		}
		catch (RWException e) {
			JOptionPane.showMessageDialog(null,e.getLocalizedMessage());	
			if (output !=null) {
				try {
					output.closeOutputFile();
				}
				catch (RWException e2) {}
			}
			output = null;
			return;
		}
		if (rowEdit.getType() == Constants.ReportType.JASPER) {
			try {
				launcher.getSetEnvironment().invoke(launcher.getInstance(),params.getReportDirectory());
				rwDebugInst.debug("Main","viewReport",MRBDebug.DETAILED,"Environment set ");
				updateProgress("Environment set");
			}
			catch (Exception e) {
				OptionMessage.displayMessage("Error setting environment "+rowEdit.getTemplate()+"-"+e.getLocalizedMessage());
				return;								
			}
			try {
				launcher.getCompileReport().invoke(launcher.getInstance(),rowEdit.getTemplate());
				rwDebugInst.debug("Main","viewReport",MRBDebug.DETAILED,"Report compiled ");
				updateProgress("Report compiled");
			}
			catch (Exception e) {
				rwDebugInst.debug("Main","viewReport",MRBDebug.DETAILED,"Error compiling report - "+e.getMessage());
				OptionMessage.customBtnMessageSwing("Error compiling report "+rowEdit.getTemplate()+"-"+e.getMessage(),"View Jasper Log",new BtnCallBack() {
					@Override
					public void callbackAction() {
						displayJasperLogFile();
					}
				});
				try {
					if (database != null)
						database.close();
				}
				catch (RWException e1) {}
				return;								
			}
			try {
				launcher.getFillReport().invoke(launcher.getInstance(),rowEdit.getTemplate(),database.getConnection());
				rwDebugInst.debug("Main","viewReport",MRBDebug.DETAILED,"Report filled ");
				database.close();
				updateProgress("Report filled");
			}
			catch (Exception e) {
				rwDebugInst.debug("Main","viewReport",MRBDebug.DETAILED,"Error filling report - "+e.getMessage());
				OptionMessage.customBtnMessageSwing("Error filling report "+rowEdit.getTemplate()+"-"+e.getMessage(),"View Jasper Log",new BtnCallBack() {
					@Override
					public void callbackAction() {
						displayJasperLogFile();
					}
				});
				try {
					if (database != null)
						database.close();
				}
				catch (RWException e1) {}
				return;								
			}
			try {
				launcher.getViewReport().invoke(launcher.getInstance(),rowEdit.getTemplate());
				updateProgress("Report displayed");
				rowEdit.touchFile();
				frameReport.resetData();
			}
			catch (Exception e) {
				OptionMessage.customBtnMessageSwing("Error viewing report "+rowEdit.getTemplate()+"-"+e.getMessage(),"View Jasper Log",new BtnCallBack() {
					@Override
					public void callbackAction() {
						displayJasperLogFile();
					}
				});
				rwDebugInst.debug("Main","viewReport",MRBDebug.DETAILED,"Error viewing report - "+e.getMessage());
				try {
					if (database != null)
						database.close();
				}
				catch (RWException e1) {}
				return;								
			}
		}
		closeProgressWindow();

	}
	private void displayProgressWindow() {
		progressFrame = new JFrame();
		progressText = "";
		progressArea=new JTextArea(30,70);
		progressArea.setText(progressText);
		progressScroll = new JScrollPane(progressArea);
		progressFrame.setSize(200,200);
		progressFrame.getContentPane().add(progressScroll,BorderLayout.CENTER);
		progressFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		progressFrame.pack();
		progressFrame.setLocationRelativeTo(null);
		progressFrame.setVisible(true);
	
	}
	public void updateProgress(String line) {
		progressArea.append(line+System.lineSeparator());
		progressArea.setCaretPosition(progressArea.getText().length() - 1);
		progressArea.update(progressArea.getGraphics());
		progressScroll.validate();
	}
	private void closeProgressWindow() {
		progressFrame.setVisible(false);
		progressFrame=null;
	}
	private void displayJasperLogFile() {
		JTextArea fileText = new JTextArea(30,70);
		String logFileName = MRBDirectoryUtils.getExtensionDataDirectory(Constants.PROGRAMNAME).getAbsolutePath()+"/logs/log.txt";
		File logFile = new File(logFileName);
		if (logFile.exists()) {
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(
						new FileInputStream(logFile)));
				fileText.read(input,"Jasper Log File");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JScrollPane scroll = new JScrollPane(fileText);
	    frame.getContentPane().add(scroll, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true);		
	}
	public void openOutput() {
		Parameters params = Parameters.getInstance();
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(params.getOutputDirectory());
            desktop.open(dirToOpen);
        } catch (IllegalArgumentException | IOException iae) {
			iae.printStackTrace();
			rwDebugInst.debug("Main","openOutput",MRBDebug.DETAILED,"Error opening Output folder - "+iae.getLocalizedMessage());
		}
	}
	/*
	 * Required Listener methods
	 */
	@Override
	public void accountModified(Account paramAccount) {
		loadAccounts();
		if (frameReport !=null)
			frameReport.resetData();
	}
	@Override
	public void accountBalanceChanged(Account paramAccount) {
		// TODO Auto-generated method stub

	}
	@Override
	public void accountDeleted(Account paramAccount1, Account paramAccount2) {
		loadAccounts();
		if (frameReport !=null)
			frameReport.resetData();
	}
	@Override
	public void accountAdded(Account paramAccount1, Account paramAccount2) {
		loadAccounts();
		if (frameReport !=null)
			frameReport.resetData();
	}
	@Override
	public void budgetListModified(BudgetList paramBudgetList) {
		loadAccounts();
		if (frameReport !=null)
			frameReport.resetData();
	}
	@Override
	public void budgetAdded(Budget paramBudget) {
		loadBudgets();
		if (frameReport !=null)
			frameReport.resetData();
	}
	@Override
	public void budgetRemoved(Budget paramBudget) {
		loadBudgets();
		if (frameReport !=null)
			frameReport.resetData();
	}
	@Override
	public void budgetModified(Budget paramBudget) {

	}
	public class CompareCurrency implements Comparator<MRBFXSelectionRow>{
		public int compare(MRBFXSelectionRow a, MRBFXSelectionRow b) {
			return a.getText().compareTo(b.getText());
		}
	}
	@Override
	public void currencyTableModified(CurrencyTable arg0) {
		loadCurrencies();
		if (frameReport !=null)
			frameReport.resetData();		
	}
}


