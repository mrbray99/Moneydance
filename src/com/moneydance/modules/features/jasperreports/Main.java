/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
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
package com.moneydance.modules.features.jasperreports;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.AccountIterator;
import com.infinitekind.moneydance.model.AccountListener;
import com.infinitekind.moneydance.model.Budget;
import com.infinitekind.moneydance.model.BudgetList;
import com.infinitekind.moneydance.model.BudgetListener;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.moneydance.model.InvestTxnType;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.modules.features.jasperreports.sandbox.MyJarLauncher;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBPreferences;

import javafx.application.Platform;
import javafx.scene.Scene;



/** 
 * MoneyDance extension to load security prices returned by the back end Rhumba extension
 * <p>
 * Main class to create main window
 * @author Mike Bray
 */

public class Main extends FeatureModule implements AccountListener, BudgetListener
{

	private static MyJarLauncher launcher=null;

	public static SimpleDateFormat cdate;
	public static DateTimeFormatter cdateFX;
	public List<String> currencies;
	public List<String> transferTypes;
	public List<String> bankAccounts;
	public List<String> assetAccounts;
	public List<String> liabilityAccounts;
	public List<String> creditAccounts;
	public List<String> loanAccounts;
	public List<String> investmentAccounts;
	public List<String> incomeCategories;
	public List<String> expenseCategories;
	public List<String> budgets;
	public static Date now;
	public static char decimalChar;
	public static FeatureModuleContext context;
	private AccountBook book;
	public static UserPreferences up;
	public static MRBDebug debugInst;
	public static Main extension;
	public static String buildNo;
	public static MyJasperReport frameReport;
	public static JFrame frame;
	private boolean closeRequested;
	private String uri;
	private String command;
	public static Image mainIcon;
	public static MRBPreferences preferences;
	public static ClassLoader loader;
	private Database database;
	private static Scene scene;
	public int SCREENWIDTH;
	public int SCREENHEIGHT;
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
			debugInst = MRBDebug.getInstance();
			debugInst.setExtension(Constants.EXTENSIONNAME);
			debugInst.setDebugLevel(MRBDebug.DETAILED);
			debugInst.debug(Constants.EXTENSIONNAME, "Init", MRBDebug.INFO, "Started Build "+buildNo);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		String strDateFormat;
		up = UserPreferences.getInstance();
		strDateFormat = up.getSetting(UserPreferences.DATE_FORMAT);
		cdate = new SimpleDateFormat(strDateFormat);
		cdateFX = DateTimeFormatter.ofPattern(strDateFormat);
		now = new Date();
		decimalChar = up.getDecimalChar();
		debugInst.debug("JasperReports", "Init", MRBDebug.DETAILED, "Decimal Character "+decimalChar);

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
	@Override
	public void cleanup() {
		debugInst.debug("JasperReports", "cleanup", MRBDebug.SUMMARY, "cleanup  ");
		closeConsole();
	}
	@Override
	public void unload() {
		debugInst.debug("JasperReports", "unload", MRBDebug.SUMMARY, "unload  ");
		super.unload();
		closeConsole();

	}
	@Override
	public void handleEvent(String appEvent) {
		super.handleEvent(appEvent);
		debugInst.debug("Main", "HandleEvent", MRBDebug.SUMMARY, "Event "+appEvent);       
		if (appEvent.compareToIgnoreCase("md:file:opening") == 0) {
			handleEventFileOpening();
		} else if (appEvent.compareToIgnoreCase("md:file:opened") == 0) {
			handleEventFileOpened();
		} else if (appEvent.compareToIgnoreCase("md:file:closed") == 0) {
			handleEventFileClosed();
		}
	}

	protected void handleEventFileOpening() {
		debugInst.debug("Main","HandleEventFileOpening", MRBDebug.DETAILED, "Opening ");
	}

	protected void handleEventFileOpened() {
		debugInst.debug("Main", "HandleEventFileOpened", MRBDebug.DETAILED, "File Opened");
		if (preferences !=null)
			MRBPreferences.forgetInstance();
		context = getContext();
		MRBPreferences.loadPreferences(context);
		preferences = MRBPreferences.getInstance();		
	}


	protected void handleEventFileClosed() {
		debugInst.debug("Main", "HandleEventFileClosed", MRBDebug.DETAILED, "Closing ");
		closeConsole();
	}
	/**
	 * Processes the uri from Moneydance.  Called by Moneydance
	 * <p>Commands:
	 * <ul>
	 * 	<li>showconsole - called when the user selects the extension
	 * <li>timeout 	- the timeout started when the check for Rhumba is sent has expired
	 * <li>iamhere		- Rhumba has responded as being present
	 * <li>loadPrice	- Rhumba has returned a price
	 * <li>errorQuote	- Rhumba has found an error
	 *<li>doneQuote	- Rhumba has completed all symbols on a particular getQuote
	 * <li>checkprogram - Overall timer has expired, check to see if any outstanding quotes
	 *   </ul>
	 *  @param uri		the uri from Moneydance
	 */
	@Override
	public void invoke(String urip) {
		if (preferences == null){
			MRBPreferences.loadPreferences(context);
			preferences = MRBPreferences.getInstance();
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
		debugInst.debug("Main","invoke",MRBDebug.SUMMARY,"Command "+ command);
		if(command.equals("showconsole")) {
			showConsole();
		}
		if(command.equals(Constants.VIEWREPORTCMD)) {
			viewReport(uri);
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
		debugInst.debug("JasperReports", "createandShowGUI", MRBDebug.SUMMARY, "cleanup  ");
		if (launcher == null)
			initServices();
		collectData();
		book.addAccountListener(this);
		closeRequested = false;
		frame = new JFrame();
		frameReport = new MyJasperReport(this);
		frame.setTitle("MoneyDance "+Constants.EXTENSIONNAME+" "+buildNo);
		frame.setIconImage(mainIcon);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//Display the window.
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frameReport, 
						"Are you sure you want to close Jasper Reports?", "Close Window?", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					debugInst.debug("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Yes");	        	
					closeConsole();
				}
			}
		});
		SCREENWIDTH =preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH,Constants.MAINSCREENWIDTH);
		debugInst.debug("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Width "+SCREENWIDTH);
		SCREENHEIGHT =preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEHEIGHT,Constants.MAINSCREENHEIGHT);
		debugInst.debug("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Height "+SCREENHEIGHT);
		frame.add(frameReport);
		frame.setSize(SCREENWIDTH,SCREENHEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		Platform.runLater(new Runnable () {
			@Override
			public void run() {
				initFX(frameReport);
			}
		});
	}
	private static void initFX(MyJasperReport fxPanel) {
		debugInst.debugThread("JasperReports", "initFX", MRBDebug.SUMMARY, "setting javafx scene");
		// This method is invoked on the JavaFX thread
		scene = fxPanel.createScene();
		fxPanel.setScene(scene);
	}

	/**
	 * Starts the user interface for the extension
	 * 
	 * First it checks if Rhumba is present by sending a hello message to Rhumba
	 * @see #invoke(String)
	 */
	private synchronized void showConsole() {
		debugInst.debug("Main", "showConsole", MRBDebug.INFO, "Show Console");
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
	FeatureModuleContext getUnprotectedContext() {
		return getContext();
	}
	/**
	 * closes the extension 
	 */
	synchronized void closeConsole() {
		debugInst.debug("Main", "closeConsole", MRBDebug.DETAILED, "closing Console ");
		closeRequested = true;
		Platform.exit();
		if(frame != null){
			if (scene != null) {
				scene = null;
			}
			if (frameReport != null)
				frameReport = null;
			frame.setVisible(false);
			frame=null;
		}
		System.gc();
	}
	public void initServices() {
		MRBDebug.getInstance().debug("Main","initServices",MRBDebug.SUMMARY,"Initialising services");
		File launcherFile = JasperUtils.getLauncherFile();
		// file = null;
		if ((launcherFile != null) && (launcherFile.exists())) {
			try {


				launcher = new MyJarLauncher(launcherFile);

				Main.setLauncher(launcher);
				MRBDebug.getInstance().debug("Main","initServices",MRBDebug.SUMMARY,"launcher: " + getLauncher());

			} catch (Exception e) {
				MRBDebug.getInstance().debug("Main","initServices",MRBDebug.SUMMARY,e.getMessage());
			}
		}
	}
	public static MyJarLauncher getLauncher() {
		return launcher;
	}

	private static void setLauncher(MyJarLauncher launcherp) {
		launcher = launcherp;
	}

	private void collectData() {
		book = context.getCurrentAccountBook();
		loadAccounts();
		loadBudgets();
		currencies = new ArrayList<>();
		List<CurrencyType> currencyTable = book.getCurrencies().getAllCurrencies();
		for (CurrencyType type : currencyTable) {
			if (type.getCurrencyType()== CurrencyType.Type.CURRENCY)
				currencies.add(type.getName());
		}
		InvestTxnType[] txnTypes = InvestTxnType.ALL_TXN_TYPES;
		transferTypes = new ArrayList<>();
		for (InvestTxnType type : txnTypes) {
			transferTypes.add(type.toString());
		}
	}
	private void loadBudgets(){
		budgets = new ArrayList<>();
		BudgetList budgetList = book.getBudgets();
		if (budgetList != null) {
			for (Budget budget : budgetList.getAllBudgets()) {
				budgets.add(budget.getName());
			}
		}

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
			switch (acct.getAccountType()) {
			case ASSET :
				assetAccounts.add(acct.getAccountName());
				break;
			case BANK :
				bankAccounts.add(acct.getAccountName());
				break;
			case CREDIT_CARD :
				creditAccounts.add(acct.getAccountName());
				break;
			case INVESTMENT :
				investmentAccounts.add(acct.getAccountName());
				break;
			case LIABILITY :
				liabilityAccounts.add(acct.getAccountName());
				break;
			case LOAN :
				loanAccounts.add(acct.getAccountName());
				break;
			case INCOME:
				incomeCategories.add(acct.getAccountName());
				break;
			case EXPENSE:
				expenseCategories.add(acct.getAccountName());
				break;
			}
		}
	}
	/*
	 * running on EDT
	 */
	private void viewReport(String uri) {
		String name = uri.substring(uri.indexOf("?")+1);
		Parameters params = Parameters.getInstance();
		debugInst.debug("Main","viewReport",MRBDebug.INFO,"Processing "+uri);
		ReportDataRow rowEdit = new ReportDataRow();
		if (!rowEdit.loadRow(name, params)) {
			JOptionPane.showMessageDialog(null,"Report "+name+" not found");
			return;
		}

		SelectionDataRow selection = new SelectionDataRow();
		if (!selection.loadRow(rowEdit.getSelection(), params)){
			JOptionPane.showMessageDialog(null,"Selection Group file "+rowEdit.getSelection()+" not found");
			return;
		}
		DataDataRow data = new DataDataRow();
		if(!data.loadRow(rowEdit.getDataParms(), params)){
			JOptionPane.showMessageDialog(null,"Data Parameters file "+rowEdit.getDataParms()+" not found");
			return;				
		}
		database = new Database(params);
		if (selection.getAccounts()) {
			createAccountRecord(data);
			loadAccountRecord();
		}
		if (selection.getBudgets())
			createBudgetRecord();
		if (selection.getCurrency())
			createCurrencyRecord();
		if (selection.getSecurity())
			createSecurityRecord();
		if (selection.getTransactions())
			createTransactionRecord();
	}
	private void createAccountRecord(DataDataRow data) {
		database.createAccount();
		AccountFactory fact = new AccountFactory(book,data,database);
	}
	private void loadAccountRecord() {
	}
	private void createBudgetRecord() {

	}
	private void createCurrencyRecord() {

	}
	private void createSecurityRecord() {

	}
	private void createTransactionRecord() {

	}
	/*
	 * Required Listener methods
	 */
	@Override
	public void accountModified(Account paramAccount) {
		// TODO Auto-generated method stub

	}
	@Override
	public void accountBalanceChanged(Account paramAccount) {
		// TODO Auto-generated method stub

	}
	@Override
	public void accountDeleted(Account paramAccount1, Account paramAccount2) {
		loadAccounts();

	}
	@Override
	public void accountAdded(Account paramAccount1, Account paramAccount2) {
		loadAccounts();

	}
	@Override
	public void budgetListModified(BudgetList paramBudgetList) {
		// TODO Auto-generated method stub

	}
	@Override
	public void budgetAdded(Budget paramBudget) {
		loadBudgets();

	}
	@Override
	public void budgetRemoved(Budget paramBudget) {
		loadBudgets();
	}
	@Override
	public void budgetModified(Budget paramBudget) {
		// TODO Auto-generated method stub

	}

}


