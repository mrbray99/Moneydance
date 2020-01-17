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
package com.moneydance.modules.features.defaultextension;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.infinitekind.util.CustomDateFormat;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBPreferences;


/** 
 * MoneyDance extension to load security prices returned by the back end Rhumba extension
 * <p>
 * Main class to create main window
 * @author Mike Bray
 */

public class Main extends FeatureModule
{
	
	public static CustomDateFormat cdate;
	public static char decimalChar;
	public static FeatureModuleContext context;
	public static UserPreferences up;
	private static MRBDebug debugInst;
	public static Main extension;
	public static String buildNo;
	private JFrame frame;
	private String uri;
	private String command;
	public static MRBPreferences preferences;
	public static ClassLoader loader;
	public static final String EXTENSIONNAME = "Default Extension";
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
		try {
			context.registerFeature(this, "showconsole",
					getIcon("mrb icon2.png"),
					getName());
			debugInst = MRBDebug.getInstance();
			debugInst.setExtension(EXTENSIONNAME);
			debugInst.setDebugLevel(MRBDebug.INFO);
			debugInst.debug(EXTENSIONNAME, "Init", MRBDebug.INFO, "Started Build "+buildNo);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		String strDateFormat;
		up = UserPreferences.getInstance();
		strDateFormat = up.getSetting(UserPreferences.DATE_FORMAT);
		cdate = new CustomDateFormat(strDateFormat);
		decimalChar = up.getDecimalChar();
		debugInst.debug("Quote Load", "Init", MRBDebug.DETAILED, "Decimal Character "+decimalChar);

	}
	/**
	 * retrieves an image from within the .mxt file.  Must be included when the extension 
	 * is compiled
	 * @param action the name of the image to load
	 * @return 	the image		
	 */
	public Image getIcon(String action) {
		try {
			loader = getClass().getClassLoader();
			java.io.InputStream in = 
					loader.getResourceAsStream("/com/moneydance/modules/features/securityquoteload/"+action);
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
		debugInst.debug("Quote Load", "cleanup", MRBDebug.SUMMARY, "cleanup  ");
		closeConsole();
	}
    @Override
    public void unload() {
		debugInst.debug("Quote Load", "unload", MRBDebug.SUMMARY, "unload  ");
        super.unload();
        closeConsole();
 
    }
    @Override
    public void handleEvent(String appEvent) {
        super.handleEvent(appEvent);
		debugInst.debug("Quote Load", "HandleEvent", MRBDebug.SUMMARY, "Event "+appEvent);       
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
		debugInst.debug("Quote Load", "HandleEventFileOpened", MRBDebug.DETAILED, "Send check for autorun ");
		if (preferences !=null)
			MRBPreferences.forgetInstance();
		MRBPreferences.loadPreferences(context);
		preferences = MRBPreferences.getInstance();		
		context = getContext();
    }

    
    protected void handleEventFileClosed() {
		debugInst.debug("Quote Load", "HandleEventFileClosed", MRBDebug.DETAILED, "Closing ");
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
		else
			processCommand();
	}
	private void processCommand() {
			
	}
	/**
	 * Return the name of this extension
	 */
	@Override
	public String getName() {
		return "Quote Loader";
	}
	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private void createAndShowGUI() {
		if (context.getCurrentAccountBook().getCurrencies().getBaseType() == null) {
			JOptionPane.showMessageDialog(null,"The Quote Loader extension depends on the base currency having been set. Please set the base currency before restarting");
			return;
		}
		//Create and set up the window.
		if (frame != null) {
			frame.requestFocus();
			return;
		}
		frame = new JFrame();
		frame.setTitle("MoneyDance "+EXTENSIONNAME+" "+buildNo);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure you want to close Quote Load?", "Close Window?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					debugInst.debug("Main", "createAndShowGUI", MRBDebug.SUMMARY, "Yes");	        	
		            closeConsole();
		        }
		    }
		});
		frame.setVisible(true);

	}
	/**
	 * Starts the user interface for the extension
	 * 
	 * First it checks if Rhumba is present by sending a hello message to Rhumba
	 * @see #invoke(String)
	 */
	private synchronized void showConsole() {
		debugInst.debug("Main", "showConsole", MRBDebug.INFO, "Manual Run");
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
		if(frame != null){
			frame.setVisible(false);
			frame.dispose();
			frame=null;
		}
		System.gc();
	}
}


