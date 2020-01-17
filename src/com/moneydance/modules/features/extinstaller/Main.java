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
package com.moneydance.modules.features.extinstaller;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBPreferences2;


/** 
 * MoneyDance extension to load security prices returned by the back end Rhumba extension
 * <p>
 * Main class to create main window
 * @author Mike Bray
 */

public class Main extends FeatureModule
{
	public static FeatureModuleContext context;
	public static char decimalChar;
	public static UserPreferences up;
	public static ClassLoader loader;
	public static MRBDebug debugInst;
	public static Main extension;
	public static String buildNo;
	public static String rhumbaBuildNo;
	public static String hleBuildNo;
	public static String qlBuildNo;
	public String mdVersion;
	public int mdVersionNo;
	public LoadFilesWindow frame;
	private String uri;
	private String command;
	public static MRBPreferences2 preferences;
	public boolean startUp = true;
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
		up = UserPreferences.getInstance();
		debugInst = MRBDebug.getInstance();
		mdVersion = up.getSetting("current_version");
		mdVersionNo = Integer.parseInt(mdVersion.substring(0,4));
		try {
			context.registerFeature(this, "showconsole",
					getIcon(Constants.MRBLOADIMAGE),
					getName());
			debugInst.setExtension("MB Extension Installer");
			debugInst.setDebugLevel(MRBDebug.INFO);
			debugInst.debug("Main", "Init", MRBDebug.INFO, "Started Build "+buildNo);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}


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
					loader.getResourceAsStream(Constants.RESOURCEPATH+"/"+action);
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
	public void resetDebug() {
		int level = debugInst.getDebugLevel();
		debugInst.dispose();
		debugInst = MRBDebug.getInstance();
		debugInst.setExtension("MB Extension Installer");
		debugInst.setDebugLevel(level);
	}
	@Override
	public void cleanup() {
		debugInst.debug("Main", "cleanup", MRBDebug.SUMMARY, "cleanup  ");
		closeConsole();
	}
    @Override
    public void unload() {
		debugInst.debug("Main", "unload", MRBDebug.SUMMARY, "unload  ");
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
		if (preferences !=null)
			MRBPreferences2.forgetInstance();
		MRBPreferences2.loadPreferences(context);
		preferences = MRBPreferences2.getInstance();		
		debugInst.setDebugLevel(preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, MRBDebug.INFO));
		String debug="OFF";
		if (debugInst.getDebugLevel()==MRBDebug.INFO)
			debug = "INFO";
		else if (debugInst.getDebugLevel()==MRBDebug.SUMMARY)
			debug = "SUMM";
		else if (debugInst.getDebugLevel()== MRBDebug.DETAILED)
			debug = "DET";
		else
			debug = "OFF";	
		debugInst.debug("Main", "HandleEventFileOpened", MRBDebug.INFO, "Debug level set to "+debug);
		context = getContext();

    }
    protected void handleEventFileClosed() {
		debugInst.debug("Main", "HandleEventFileClosed", MRBDebug.DETAILED, "Closing ");
    	closeConsole();
    }
	@Override
	public synchronized void invoke(String urip) {
		if (preferences == null){
			MRBPreferences2.loadPreferences(context);
			preferences = MRBPreferences2.getInstance();
		}
		String uriLocal = urip;
		uri = uriLocal;
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
		if (command.startsWith(Constants.RETURNBUILD)) {
			String buildNums = command.substring(command.indexOf(",")+1);
			int index = buildNums.indexOf(",");
			qlBuildNo = buildNums.substring(0,index);
			buildNums = buildNums.substring(index+1);
			index = buildNums.indexOf(",");
			rhumbaBuildNo = buildNums.substring(0,index);
			hleBuildNo = buildNums.substring(index+1);
			if (frame!=null)
				frame.updateBuildNos();
		}
	}
	/**
	 * Return the name of this extension
	 */
	@Override
	public String getName() {
		return "MB Extension Installer";
	}
	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private void createAndShowGUI() {
		//Create and set up the window.
		if (frame != null) {
			frame.requestFocus();
			return;
		}
		frame = new LoadFilesWindow(this);
		frame.setTitle("Mike Bray Extension Installer "+buildNo);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure you want to close the Extension Installer?", "Close Window?", 
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


