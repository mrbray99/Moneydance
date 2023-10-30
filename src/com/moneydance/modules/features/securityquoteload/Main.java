/*
 *  Copyright (c) 2018, Michael Bray.  All rights reserved.
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

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.infinitekind.util.CustomDateFormat;
import com.infinitekind.util.DateUtil;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.apps.md.view.MoneydanceUI;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBPreferences2;
import com.moneydance.modules.features.mrbutil.Platform;
import com.moneydance.modules.features.securityquoteload.quotes.QuoteManager;
import com.moneydance.modules.features.securityquoteload.view.CalculateRunDate;

/**
 * Moneydance extension to load security prices obtained from Yahoo.com and
 * ft.com
 * <p>
 * Main class to create main window
 * 
 * @author Mike Bray
 */

public class Main extends FeatureModule {
	public static CustomDateFormat cdate;
	public static char decimalChar;
	public static FeatureModuleContext context;
	public static UserPreferences up;
	public static MRBDebug debugInst;
	public static Main extension;
	public static Parameters params=null;
	public static String buildNo;
	public static String versionNo="00";
	public static Boolean autoSettingsChanged=false;
	public static boolean standAloneRequested=false;

	private Image selectedBlack = null;
	private Image selectedLight;
	private Image unselectedBlack;
	private Image unselectedLight;
	public ImageIcon selectedIcon;
	public ImageIcon unselectedIcon;
	private String mdVersion;
	private MainPriceWindow frame;
	private Thread iamhereTimeout;
	private Thread overallTimeout;
	private AtomicBoolean quotesCompleted = new AtomicBoolean(false);
	private AtomicBoolean timerInterrupt = new AtomicBoolean(false);
	private int timeoutCount = 0;
	private String uri;
	private String command;
	private String cmdParam="quit";
	public static MRBPreferences2 preferences;
	private String secMode;
	private String curMode;
	public String serverName = Constants.PROGRAMNAME;;
	public int runtype = Constants.NORUN;
	public List<String> errorTickers;
	private TaskExecutor autoRun = null;
	public static ClassLoader loader;
	public boolean startUp = true;
	public static ZoneId defaultZone = ZoneId.systemDefault();
	private Boolean closingRequested = false;
	public static boolean autoRunning = false;
	public static boolean secondRunRequired = false;
	public static boolean isUpdating = false;
	public static boolean isGUIOpen = false;
	public static boolean isQuotesRunning = false;
	private boolean isSyncing=false;
	private int timeoutMax = Constants.TIMEOUTCOUNT;
	private Timer autoDelay;
	private MoneydanceUI mdGUI;
	private com.moneydance.apps.md.controller.Main mdMain;


	/*
	 * Called when extension is loaded<p> Need to register the feature and the URI
	 * command to be called when the user selects the extension.
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
		up = UserPreferences.getInstance();
		debugInst = new MRBDebug();
		String dateFormatStr;
		dateFormatStr = up.getSetting(UserPreferences.DATE_FORMAT);
		cdate = new CustomDateFormat(dateFormatStr);
		decimalChar = up.getDecimalChar();
		debugInst.debug("Quote Load", "Init", MRBDebug.DETAILED, "Decimal Character " + decimalChar);
		mdVersion = up.getSetting("current_version");
		int mdVersionNo = Integer.parseInt(mdVersion.substring(0, 4));
		if (mdVersionNo < Constants.MINIMUMVERSIONNO) {
			JOptionPane.showMessageDialog(null,
					"This version of Quote Loader is designed to be run on Moneydance version "
							+ Constants.MINIMUMVERSIONNO.toString() + ". You are running version "
							+ mdVersion,
					"Quote Loader", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			context.registerFeature(this, "showconsole", getIcon(Constants.QUOTELOADIMAGE), getName());
			debugInst.setExtension("Quote Load");
			debugInst.setDebugLevel(MRBDebug.INFO);
			debugInst.debug("Quote Load", "Init", MRBDebug.INFO, "Started Build " + buildNo+"."+versionNo);
			debugInst.debug("Quote Load", "Init", MRBDebug.INFO, "Locale " + Locale.getDefault());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	/**
	 * retrieves an image from within the .mxt file. Must be included when the
	 * extension is compiled
	 * 
	 * @param action the name of the image to load
	 * @return the image
	 */
	public Image getIcon(String action) {
		try {
			loader = getClass().getClassLoader();
			java.io.InputStream in = loader.getResourceAsStream(Constants.RESOURCEPATH + "/" + action);
			if (in != null) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
				byte buf[] = new byte[256];
				int n = 0;
				while ((n = in.read(buf, 0, buf.length)) >= 0)
					bout.write(buf, 0, n);
				return Toolkit.getDefaultToolkit().createImage(bout.toByteArray());
			}
		} catch (Throwable e) {
		}
		return null;
	}

	@Override
	public void cleanup() {
		debugInst.debug("Quote Load", "cleanup", MRBDebug.SUMMARY, "cleanup  ");
		closeConsole();
	}

	@Override
	public void unload() {
		debugInst.debug("Quote Load", "unload", MRBDebug.SUMMARY, "unload  ");
		super.unload();
		if (autoRun != null) {
			autoRun.stop();
			autoRun = null;
		}
		closeConsole();

	}

	@Override
	public void handleEvent(String appEvent) {
		super.handleEvent(appEvent);
		debugInst.debug("Quote Load", "HandleEvent", MRBDebug.SUMMARY, "Event " + appEvent);
		if (appEvent.compareToIgnoreCase("md:file:opening") == 0) {
			handleEventFileOpening();
		} else if (appEvent.compareToIgnoreCase("md:file:opened") == 0) {
			handleEventFileOpened();
		} else if (appEvent.compareToIgnoreCase("md:file:closing") == 0) {
			handleEventFileClosing();
		} else if (appEvent.compareToIgnoreCase("md:file:closed") == 0) {
			handleEventFileClosed();
		}

	}

	protected void handleEventFileOpening() {
		debugInst.debug("Main", "HandleEventFileOpening", MRBDebug.DETAILED, "Opening ");
		closingRequested = false;
	}

	protected void handleEventFileOpened() {
		closingRequested = false;
		if (preferences != null)
			MRBPreferences2.forgetInstance();
		MRBPreferences2.loadPreferences(context);
		preferences = MRBPreferences2.getInstance();
		params=Parameters.getParameters();
		debugInst.setDebugLevel(
				preferences.getInt(Constants.PROGRAMNAME + "." + Constants.DEBUGLEVEL, MRBDebug.INFO));
		String debug = "OFF";
		if (debugInst.getDebugLevel() == MRBDebug.INFO)
			debug = "INFO";
		else if (debugInst.getDebugLevel() == MRBDebug.SUMMARY)
			debug = "SUMM";
		else if (debugInst.getDebugLevel() == MRBDebug.DETAILED)
			debug = "DET";
		else
			debug = "OFF";
		debugInst.debug("Quote Load", "HandleEventFileOpened", MRBDebug.INFO, "Debug level set to " + debug);
		context = getContext();
		String serverType = Constants.USESTANDALONE;
		debugInst.debug("Quote Load", "HandleEventFileOpened", MRBDebug.DETAILED, "Servertype " + serverType);
		serverName = Constants.PROGRAMNAME;
		debugInst.debug("Quote Load", "HandleEventFileOpened", MRBDebug.SUMMARY, "Using Standalone ");
		sendAuto();
	}

	public void sendAuto() {
		debugInst.debug("Quote Load", "sendAuto", MRBDebug.DETAILED, "checking syncing ");
		if (context != null && context.getCurrentAccountBook().getSyncer().isSyncing()) {
			debugInst.debug("Quote Load", "sendAuto", MRBDebug.INFO, "Syncing - delay set ");
			isSyncing=true;
			if (autoDelay == null) {
				autoDelay = new Timer(1, ((ae) -> {
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							debugInst.debug("Quote Load", "sendAuto", MRBDebug.INFO,
									"Check Auto Sync after delay ");
							context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"
									+ Constants.CHECKAUTOSYNC);
						}
					});

				}));
				autoDelay.setInitialDelay(30000);
			}
			autoDelay.start();
			return;
		} 
		isSyncing=false;
		if (standAloneRequested) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"
							+ Constants.STANDALONEREQUESTED);
				}
			});
		}
		else {
			debugInst.debug("Quote Load", "sendAuto", MRBDebug.INFO, "Check Auto without delay ");
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":" + Constants.CHECKAUTOCMD);
				}
			});
		}
	}

	private void resetAutoRun() {
		if (autoRun == null)
			autoRun = new TaskExecutor(this);
		else {
			autoRun.stop();
			autoRun = new TaskExecutor(this);

		}
		LocalTime now = LocalTime.now();
		LocalTime next;
		if (now.compareTo(LocalTime.of(2, 0)) < 0)
			next = LocalTime.of(2, 0);
		else if (now.compareTo(LocalTime.of(4, 0)) < 0)
			next = LocalTime.of(4, 0);
		else if (now.compareTo(LocalTime.of(6, 0)) < 0)
			next = LocalTime.of(6, 0);
		else if (now.compareTo(LocalTime.of(8, 0)) < 0)
			next = LocalTime.of(8, 0);
		else if (now.compareTo(LocalTime.of(9, 0)) < 0)
			next = LocalTime.of(9, 0);
		else if (now.compareTo(LocalTime.of(11, 0)) < 0)
			next = LocalTime.of(11, 0);
		else if (now.compareTo(LocalTime.of(13, 0)) < 0)
			next = LocalTime.of(13, 0);
		else if (now.compareTo(LocalTime.of(15, 0)) < 0)
			next = LocalTime.of(15, 0);
		else if (now.compareTo(LocalTime.of(17, 0)) < 0)
			next = LocalTime.of(17, 0);
		else if (now.compareTo(LocalTime.of(19, 0)) < 0)
			next = LocalTime.of(19, 0);
		else if (now.compareTo(LocalTime.of(21, 0)) < 0)
			next = LocalTime.of(21, 0);
		else if (now.compareTo(LocalTime.of(22, 0)) < 0)
			next = LocalTime.of(22, 0);
		else if (now.compareTo(LocalTime.of(23, 0)) < 0)
			next = LocalTime.of(23, 0);
		else
			next = LocalTime.of(23, 59);
		LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), next);
		if (dateTime.compareTo(LocalDateTime.now()) < 0)
			dateTime.plusDays(1l);
		debugInst.debug("Quote Load", "sendAuto", MRBDebug.INFO,
				"now " + now.toString() + " next " + dateTime.toString());
		autoRun.startExecutionAt(dateTime);
	}

	protected void handleEventFileClosing() {
		debugInst.debug("Quote Load", "HandleEventFileClosing", MRBDebug.DETAILED, "Closing ");
		if (frame !=null && (frame.isSecDirty()|| frame.isCurDirty()||frame.isParamDirty()))
			frame.close();
		closingRequested = true;
		if (autoRun != null) {
			autoRun.stop();
			autoRun = null;
		}
		closeConsole();
	}

	protected void handleEventFileClosed() {
		closingRequested = true;
		debugInst.debug("Quote Load", "HandleEventFileClosed", MRBDebug.DETAILED, "Closing ");
		if (autoRun != null) {
			autoRun.stop();
			autoRun = null;
		}
		closeConsole();
	}

	/**
	 * Processes the uri from Moneydance. Called by Moneydance
	 * <p>
	 * Commands:
	 * <ul>
	 * <li>showconsole - called when the user selects the extension
	 * <li>timeout - the timeout started when the check for the backend request has
	 * expired
	 * <li>loadPrice - Backend has returned a price
	 * <li>errorQuote - Backend has found an error
	 * <li>doneQuote - Backend has completed all symbols on a particular getQuote
	 * <li>checkprogram - Overall timer has expired, check to see if any outstanding
	 * quotes
	 * </ul>
	 * 
	 * @param uri the uri from Moneydance
	 */
	@Override
	public synchronized void invoke(String uri) {
		debugInst.debug("Quote Load", "invoke", MRBDebug.DETAILED, "Command uri " + uri);
		if (closingRequested)
			return;
		/*
		 * load JCheckBox icons for Unix due to customised UIManager Look and feel
		 */
		if (Platform.isUnix() || Platform.isFreeBSD()) {
			if (selectedBlack == null) {
				selectedBlack = getIcon(Constants.SELECTEDBLACKIMAGE);
				selectedLight = getIcon(Constants.SELECTEDLIGHTIMAGE);
				unselectedBlack = getIcon(Constants.UNSELECTEDBLACKIMAGE);
				unselectedLight = getIcon(Constants.UNSELECTEDLIGHTIMAGE);
				UIDefaults uiDefaults = UIManager.getDefaults();
				Color theme = uiDefaults.getColor("Panel.foreground");
				double darkness = 0;
				if (theme != null) {
					darkness = 1 - (0.299 * theme.getRed() + 0.587 * theme.getGreen()
							+ 0.114 * theme.getBlue()) / 255;
					debugInst.debug("Quote Load", "Init", MRBDebug.DETAILED,
							"Panel.foreground Color " + theme.toString() + " Red " + theme.getRed()
									+ " Green " + theme.getGreen() + " Blue " + theme.getBlue()
									+ " Darkness " + darkness);
				}
				if (darkness > 0.5) {
					if (selectedBlack != null) {
						debugInst.debug("Quote Load", "Init", MRBDebug.DETAILED, "selected black loaded");
						selectedIcon = new ImageIcon(
								selectedBlack.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
					}
					if (unselectedBlack != null) {
						debugInst.debug("Quote Load", "Init", MRBDebug.DETAILED, "unselected black loaded");
						unselectedIcon = new ImageIcon(
								unselectedBlack.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
					}
				} else {
					if (selectedLight != null) {
						debugInst.debug("Quote Load", "Init", MRBDebug.DETAILED, "selected light loaded");
						selectedIcon = new ImageIcon(
								selectedLight.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
					}
					if (unselectedLight != null) {
						debugInst.debug("Quote Load", "Init", MRBDebug.DETAILED, "unselected light loaded");
						unselectedIcon = new ImageIcon(
								unselectedLight.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
					}
				}
			}
		}
		if (preferences == null) {
			MRBPreferences2.loadPreferences(context);
			preferences = MRBPreferences2.getInstance();
		}
		if (params==null)
			params=Parameters.getParameters();
		this.uri = uri;
		command = this.uri;
		int theIdx = uri.indexOf('?');
		if (theIdx >= 0) {
			command = uri.substring(0, theIdx);
		} else {
			theIdx = uri.indexOf(':');
			if (theIdx >= 0) {
				command = uri.substring(0, theIdx);
				cmdParam = uri.substring(theIdx+1);
			}
		}
		if (command.equalsIgnoreCase(Constants.RUNSTANDALONECMD)) {
			if (!cmdParam.equalsIgnoreCase("quit")&&!cmdParam.equalsIgnoreCase("noquit")) {
				JOptionPane.showMessageDialog(null, "Invalid Quote Loader runauto parameter: "+cmdParam,
						"Quote Loader", JOptionPane.INFORMATION_MESSAGE);
				return;
			}			
		}
		/*
		 * showConsole will be on AWT-Event-Queue, all other commands will be on the
		 * thread of the calling program, make sure all commands are processed on the
		 * AWT-Event-Queue to preserve sequence
		 */
		debugInst.debug("Quote Load", "invoke", MRBDebug.SUMMARY, "Command " + command);
		if (command.equals(Constants.RUNSTANDALONECMD)) {
			standAloneRequested = true;
			sendAuto();
			return;
		}
		if (command.equals("showconsole")) {
			if (frame != null && runtype > Constants.MANUALRUN) {
				JOptionPane.showMessageDialog(null, "Quote Loader is running an automatic update,please wait",
						"Quote Loader", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			debugInst.debug("Quote Load", "invoke", MRBDebug.DETAILED, "runtype set to manual");
			runtype = Constants.MANUALRUN;
			showConsole();
		} else {
			if (command.equals(Constants.RUNAUTOCMD)) {
				debugInst.debug("Quote Load", "invoke", MRBDebug.DETAILED, "command Run Auto");
				showConsole();
			} else {
				if (command.equals(Constants.STANDALONEREQUESTED)) {
					runtype = Constants.STANDALONERUN;
					debugInst.debug("Quote Load", "invoke", MRBDebug.INFO, "command Run Standalone");
					showConsole();
				}
				else {
					processCommand(command,uri);
				}
			}
		}
	}

	private synchronized void processCommand(String command,String uri) {
		debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED, "process command invoked " + command);
		if (command.equals(Constants.CHECKAUTOSYNC)) {
			if (autoDelay != null) {
				autoDelay.stop();
			}
			sendAuto();
			return;
		}
		if (command.equals(Constants.CHECKAUTOCMD)) {
			/*
			 *  Check if an Auto run is due
			 */
			if (params==null)
				Parameters.getParameters();
			debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED, "Running Checkautocmd");
			autoSettingsChanged=false;
			secMode = preferences.getString(Constants.PROGRAMNAME + "." + Constants.SECRUNMODE,
					Constants.MANUALMODE);
			curMode = preferences.getString(Constants.PROGRAMNAME + "." + Constants.CURRUNMODE,
					Constants.MANUALMODE);
			boolean secRunAuto = false;
			boolean curRunAuto = false;
			int secNextrun = 0;
			int curNextrun = 0;
			int today = DateUtil.getStrippedDateInt();
			/*
			 * check for securities
			 */
			if (secMode.equals(Constants.AUTOMODE)) {
				debugInst.debug("Quote Load", "Process Command", MRBDebug.DETAILED,
						"Security Auto mode detected");
				secNextrun = preferences.getInt(Constants.PROGRAMNAME + "." + Constants.SECNEXTRUN, today);
				if (secNextrun <= today)
					secRunAuto = true;
			}
			/*
			 * check for currencies
			 */
			if (params.getCurrency()&&curMode.equals(Constants.AUTOMODE)) {
				debugInst.debug("Quote Load", "Process Command", MRBDebug.DETAILED,
						"Currency Auto mode detected");
				curNextrun = preferences.getInt(Constants.PROGRAMNAME + "." + Constants.CURNEXTRUN, today);
				if (curNextrun <= today)
					curRunAuto = true;
			}
			debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED,
					"Date check " + secRunAuto + " " + curRunAuto);
			int startTime = preferences.getInt(Constants.PROGRAMNAME + "." + Constants.STARTTIME,
					Constants.RUNSTARTUP);
			if (secRunAuto || curRunAuto) {
				if (startTime != Constants.RUNSTARTUP) {
					/*
					 * Not a run at startup - check if time has been reached
					 */
					LocalTime now = LocalTime.now();
					LocalTime runTime = LocalTime.of(23, 59);
					for (int i = 0; i < Constants.TIMEVALUES.length; i++) {
						if (Constants.TIMEVALUES[i] == startTime) {
							if (Constants.TIMESTART[i] != 24)
								runTime = LocalTime.of(Constants.TIMESTART[i], 0);
						}
					}

					LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), runTime);
					if (dateTime.compareTo(LocalDateTime.now()) < 0)
						dateTime.plusDays(1l);
					debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED,
							"Time check - Sec " + secRunAuto + " "+secNextrun +" "+startUp);
					debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED,
							"Time check - Cur " + curRunAuto + " "+curNextrun +" "+startUp);
					debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED,
							"Time check - run time " + runTime + " "+now );
					if (!((secRunAuto && secNextrun < today && startUp)
							|| (curRunAuto && curNextrun < today && startUp))) {
						if (runTime.isAfter(now)) {
							/*
							 * start time not reached - reset auto run
							 */
							resetAutoRun();
							return;
						}
						startUp = false;
					}
				}
				if (secRunAuto || curRunAuto) {
					if (frame != null && runtype == Constants.MANUALRUN) {
						/*
						 * check to see if Quote Loader is open
						 */
						if (JOptionPane.showConfirmDialog(frame,
								"Quote Loader is trying to run an Automatic update.  Do you wish to close this window to allow it to run",
								"Quote Loader", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
							/*
							 * user has replied yes
							 */
							if (frame!=null)
								frame.close();
							else
								closeConsole();
						} else {
							/*
							 * user has replied no
							 */
							resetAutoRun();
							return;
						}
					}
				}
				/*
				 * determine what type of run
				 */
				if (secRunAuto && !curRunAuto)
					runtype = Constants.SECAUTORUN;
				if (!secRunAuto && curRunAuto)
					runtype = Constants.CURAUTORUN;
				if (secRunAuto && curRunAuto)
					runtype = Constants.BOTHAUTORUN;
				debugInst.debug("Quote Load", "Process Command", MRBDebug.DETAILED, "Submitting Auto Run");
				/*
				 * send Run Auto Cmd
				 */
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"
								+ Constants.RUNAUTOCMD);
						context.showURL("moneydance:setprogress?meter=0&label="
								+ "Quote Loader Updating Prices");

					}
				});
				if (secRunAuto) {
					/*
					 * reset security next run date
					 */
					preferences.put(Constants.PROGRAMNAME + "." + Constants.SECLASTRUN, today);
					CalculateRunDate secRun = new CalculateRunDate(Constants.SECRUNTYPE,
							Constants.SECRUNPARAM, Constants.SECLASTRUN);
					preferences.put(Constants.PROGRAMNAME + "." + Constants.SECNEXTRUN, secRun.getDate());
					preferences.isDirty();
				}
				/*
				 * reset currency next run date
				 */
				if (curRunAuto) {
					preferences.put(Constants.PROGRAMNAME + "." + Constants.CURLASTRUN, today);
					CalculateRunDate curRun = new CalculateRunDate(Constants.CURRUNTYPE,
							Constants.CURRUNPARAM, Constants.CURLASTRUN);
					preferences.put(Constants.PROGRAMNAME + "." + Constants.CURNEXTRUN, curRun.getDate());
					preferences.isDirty();
				}
				resetAutoRun();
			} else {
				debugInst.debug("Quote Load", "Process Command", MRBDebug.DETAILED, "Nothing to run");
				resetAutoRun();
			}
			return;
		}
		if (command.equals(Constants.GETQUOTECMD)) {
			Runnable task = new Runnable() {
				public void run() {
					QuoteManager manager = new QuoteManager();
					manager.getQuotes(uri);
				}
			};
			new Thread(task).start();
			return;
		}
		if (command.equals(Constants.TIMEOUTCMD)) {
			debugInst.debug("Quote Load", "invoke", MRBDebug.SUMMARY, "time out received");
			return;
		}
		Integer totalQuotes;
		if (command.equals(Constants.STARTQUOTECMD)) {
			try {
				totalQuotes = Integer.valueOf(uri.substring(uri.indexOf("?numquotes=") + 11));
			} catch (NumberFormatException e) {
				totalQuotes = 0;
			}
			timeoutMax = totalQuotes < 100 ? 12 : totalQuotes > 99 & totalQuotes < 200 ? 24 : 36;
			debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED,
					"total quotes=" + totalQuotes + " maximum timeout set to " + timeoutMax);
			overallTimeout = new Thread(new QuoteTimer());
			overallTimeout.start();
			isQuotesRunning = true;
			return;
		}
		if (command.equalsIgnoreCase(Constants.TESTTICKERCMD)) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.testTicker(uri);
				}
			});
			return;
		}
		if (command.equals(Constants.LOADPRICECMD)) {
			if (frame != null) {
				timeoutCount = 0;
				debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED, "updating price " + uri);
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.updatePrices(uri);
					}
				});
			}
			return;
		}
		if (command.equals(Constants.LOADHISTORYCMD)) {
			if (frame != null) {
				timeoutCount = 0;
				debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED, "updating history " + uri);
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.updateHistory(uri);
					}
				});
			}
			return;
		}
		if (command.equals(Constants.ERRORQUOTECMD)) {
			if (frame != null) {
				timeoutCount = 0;
				debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED, "error returned " + uri);
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.failedQuote(uri);
					}
				});
			}
			return;
		}
		if (command.equals(Constants.DONEQUOTECMD)) {
			if (frame != null) {
				timeoutCount = 0;
				debugInst.debug("Quote Load", "processCommand", MRBDebug.DETAILED, "Done " + uri);
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.doneQuote(uri);
					}
				});
			}
			return;
		}
		if (command.equals(Constants.CHECKPROGRESSCMD)) {
			if (frame != null) {
				if (frame.checkProgress()) {
					quotesCompleted.set(true);
				} else {
					debugInst.debug("Quote Load", "ProcessCommand", MRBDebug.SUMMARY,
							"Still Waiting for Backend");
					if (timeoutCount > timeoutMax) {
						JOptionPane.showMessageDialog(null, "Backend has failed to respond", "Quote Loader",
								JOptionPane.ERROR_MESSAGE);
						frame.closeQuotes();
					} else
						timeoutCount++;
				}
			} else
				quotesCompleted.set(true);
			return;
		}
		/*
		 * check for end of automatic run completed
		 */
		if (command.equals(Constants.AUTODONECMD)) {
			runtype = 0;
			if (frame != null) {
				frame.dispose();
				frame = null;
				autoRunning = false;
				isQuotesRunning = false;
			}
			context.showURL("moneydance:setprogress?meter=0&label=" + "Quote Loader Update Completed");
			debugInst.debug("Quote Load", "ProcessCommand", MRBDebug.DETAILED, "Auto run done");
			if (standAloneRequested && cmdParam.equalsIgnoreCase("quit")) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"
								+ Constants.STANDALONEDONE);
					}
				});
			}
			return;
		}
		/*
		 * check for standalone run completed
		 */
		if (command.equals(Constants.STANDALONEDONE)) {
			debugInst.debug("Quote Load", "ProcessCommand", MRBDebug.INFO, "Standalone  run done");
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
		
					mdMain = com.moneydance.apps.md.controller.Main.mainObj;
					mdGUI = mdMain.getUI();
					mdMain.saveCurrentAccount();
					mdGUI.shutdownApp(false);
				}
			}); 
		}
		/*
		 * check for manual run done
		 */
		if (command.equals(Constants.MANUALDONECMD)) {
			runtype = 0;
			isQuotesRunning = false;
		}
		/*
		 * second run required one autorun to allow currencies to be done first
		 */
		if (command.equals(Constants.RUNSECONDRUNCMD))
			if (frame != null && frame instanceof AutomaticRun)
				((AutomaticRun)frame).secondRun();

	}

	/**
	 * Return the name of this extension
	 */
	@Override
	public String getName() {
		return "Quote Loader";
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event dispatch thread.
	 */
	private void createAndShowGUI() {
		if (context.getCurrentAccountBook().getCurrencies().getBaseType() == null) {
			JOptionPane.showMessageDialog(null,
					"The Quote Loader extension depends on the base currency having been set. Please set the base currency before restarting",
					"Quote Loader", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Create and set up the window.
		if (frame != null) {
			frame.requestFocus();
			return;
		}
		frame = new MainPriceWindow(this, runtype);
		if (errorTickers != null)
			frame.setErrorTickers(errorTickers);
		if (!versionNo.equals("00"))
			frame.setTitle("Quote Loader " + buildNo+"."+versionNo);
		else
			frame.setTitle("Quote Loader " + buildNo);
		frame.setIconImage(getIcon(Constants.QUOTELOADIMAGE));
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				closeConsole();
				// }
			}
		});
		frame.setVisible(true);


	}

	/**
	 * Starts the user interface for the extension
	 * 
	 * First it checks if is present by sending a hello message to
	 * 
	 * @see #invoke(String)
	 */
	private synchronized void showConsole() {

		debugInst.debug("Quote Load", "showConsole", MRBDebug.DETAILED, "Starting Quote Load - runtype "+runtype);
		if (runtype != Constants.MANUALRUN) {
			String runmsg="";
			switch (runtype) {
			case 2:
				runmsg="Auto Run - Securities Only";
				break;
			case 3:
				runmsg="Auto Run - Currencies Only";
				break;
			case 4:
				runmsg="Auto Run - Securities and Currencies";
				break;
			case 5:
				runmsg="Standalone  Run";
				break;
			}
			debugInst.debug("Quote Load", "showConsole", MRBDebug.INFO, runmsg);
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
					frame = new AutomaticRun(Main.this, runtype);
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null,
								"The Quote Loader extension has encountered an unexpected error while running an automatic run.  Please see the Console Log for details",
								"Quote Loader", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		} else {
			debugInst.debug("Quote Load", "showConsole", MRBDebug.INFO, "Manual Run");
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					isGUIOpen = true;
					createAndShowGUI();
				}
			});
		}
//		}

	}

	/**
	 * Get the extension context
	 * 
	 * @return FeatureModuleContext context
	 */
	FeatureModuleContext getUnprotectedContext() {
		return getContext();
	}

	/**
	 * closes the extension
	 */
	synchronized void closeConsole() {
		debugInst.debug("Quote Load", "closeConsole", MRBDebug.DETAILED, "closing Console ");
		isGUIOpen = false;
		if (frame != null)
			frame.checkUnsaved();
		if (iamhereTimeout != null && iamhereTimeout.isAlive()) {
			timerInterrupt.set(true);
			iamhereTimeout.interrupt();
		}
		quotesCompleted.set(true);
		if (overallTimeout != null && overallTimeout.isAlive())
			overallTimeout.interrupt();
		errorTickers = null;
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
		runtype = Constants.NORUN;
		if (params != null)
			Parameters.closeParameters();
		params = null;
	}

	/**
	 * @author Mike Bray Sets up and runs a timer to check if backend has responded.
	 *         If it expires it sends a 'timeout' message to the invoke(uri) method
	 *
	 *
	 */
	class QuoteTimer implements Runnable {
		@Override
		public void run() {
			debugInst.debug("Quote Load", "QuoteTimer", MRBDebug.SUMMARY, "Timer started");
			while (!quotesCompleted.get()) {
				try {
					TimeUnit.SECONDS.sleep(Constants.OVERALLTIMEOUT);
				} catch (InterruptedException e) {
					if (!quotesCompleted.get())
						e.printStackTrace();
				}
				debugInst.debug("Quote Load", "QuoteTimer", MRBDebug.SUMMARY, "Timer expired");
				if (quotesCompleted.get())
					debugInst.debug("Quote Load", "QuoteTimer", MRBDebug.INFO, "Quotes Completed");
				else {
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"
									+ Constants.CHECKPROGRESSCMD);
						}
					});
				}
			}
		}

	}
}
