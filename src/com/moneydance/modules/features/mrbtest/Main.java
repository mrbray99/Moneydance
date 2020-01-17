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
package com.moneydance.modules.features.mrbtest;

import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.infinitekind.util.CustomDateFormat;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.awt.GridC;
import com.moneydance.awt.JDateField;
import com.moneydance.modules.features.mrbutil.MRBPreferences;

/**
 * MoneyDance extension to generate budget items for the 'new' type of budgets
 * 
 * Main class to create main window
 */

public class Main extends FeatureModule {
	public static CustomDateFormat cdate;
	public static FeatureModuleContext context;
	public UserPreferences up = null;
	public static Image imgIcon;
	public static String strBuild;
	private JFrame frame = new JFrame();
	private JPanel screenPan;
	MRBPreferences preferences;
	JDateField date;
	JDateField curdate;
	String PROGRAMNAME = "securityquoteload";
	String SECLASTRUN = "lastrun";
	String CURLASTRUN = "curlastrun";




	@Override
	public void init() {
		// the first thing we will do is register this module to be invoked
		// via the application toolbar
		context = getContext();
		try {
			imgIcon = getIcon("/com/moneydance/modules/features/mrbtest/mrb icon2.png");
			context.registerFeature(this, "showconsole",
					imgIcon, getName());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	    int iBuild = getBuild();
	    strBuild = String.valueOf(iBuild);  

	}

	/*
	 * Get Icon is not really needed as Icons are not used. Included as the
	 * register feature method requires it
	 */

	private Image getIcon(String action) {
		try {
			ClassLoader cl = getClass().getClassLoader();
			java.io.InputStream in = cl
					.getResourceAsStream(action);
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
	 * This does not seem to be called, make sure handleEvent("md:file:closing") is present
	 */
	@Override
	public void cleanup() {
		closeConsole();
	}

	/*
	 * determine if file is being closed and close down extension
	 */
	@Override
	public void handleEvent(String appEvent) {

		if ("md:file:closing".equals(appEvent)) {
			closeConsole();
		}
	}

	/** Process an invocation of this module with the given URI */
	@Override
	public void invoke(String uri) {
		String command = uri;
		String strDateFormat;
		up = UserPreferences.getInstance();
		strDateFormat = up.getSetting(UserPreferences.DATE_FORMAT);
		cdate = new CustomDateFormat(strDateFormat);
		int theIdx = uri.indexOf('?');
		if (theIdx >= 0) {
			command = uri.substring(0, theIdx);
		} else {
			theIdx = uri.indexOf(':');
			if (theIdx >= 0) {
				command = uri.substring(0, theIdx);
			}
		}
		MRBPreferences.loadPreferences(context);
		preferences = MRBPreferences.getInstance();

		if (command.equals("showconsole")) {
			showConsole();
		}
	}

	@Override
	public String getName() {
		return "Test Bed";
	}

	private synchronized void showConsole() {
		screenPan = new JPanel(new GridBagLayout());
		MRBPreferences.loadPreferences(context);
		MRBPreferences preferences = MRBPreferences.getInstance();
		JLabel lastLbl = new JLabel("Securities Last Launch Date  ");
		CustomDateFormat format = new CustomDateFormat("dd/mm/yyyy");
		date = new JDateField(format);
		JButton ok = new JButton("Save");
		ok.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDate(SECLASTRUN,date);
			}
		});	
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetDate(SECLASTRUN,date);
			}
		});	

		date.setDateInt(preferences.getInt(PROGRAMNAME+"."+SECLASTRUN,0));
		screenPan.add(lastLbl,GridC.getc(0,0).insets(10,10,10,10));
		screenPan.add(date,GridC.getc(1,0).insets(10,10,10,10));
		screenPan.add(ok,GridC.getc(0,1).insets(10,10,10,10));
		screenPan.add(reset,GridC.getc(1,1).insets(10,10,10,10));
		JLabel lastCurLbl = new JLabel("Currencies Last Launch Date  ");
		curdate = new JDateField(format);
		JButton curok = new JButton("Save");
		curok.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDate(CURLASTRUN,curdate);
			}
		});	
		JButton curreset = new JButton("Reset");
		curreset.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetDate(CURLASTRUN,curdate);
			}
		});	

		curdate.setDateInt(preferences.getInt(PROGRAMNAME+"."+CURLASTRUN,0));
		screenPan.add(lastCurLbl,GridC.getc(0,2).insets(10,10,10,10));
		screenPan.add(curdate,GridC.getc(1,2).insets(10,10,10,10));
		screenPan.add(curok,GridC.getc(0,3).insets(10,10,10,10));
		screenPan.add(curreset,GridC.getc(1,3).insets(10,10,10,10));
		JButton done = new JButton("Close");
		done.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeConsole();
			}
		});	
		screenPan.add(done,GridC.getc(0,4));
		frame = new JFrame();
		frame.getContentPane().add(screenPan);

		frame.pack();
		frame.setVisible(true);
	}

	FeatureModuleContext getUnprotectedContext() {
		return getContext();
	}

	synchronized void closeConsole() {
			frame.dispose();
			System.gc();
		
	}
	private void updateDate(String node,JDateField date){
		preferences.put(PROGRAMNAME+"."+node,date.getDateInt());
		preferences.isDirty();
	}
	private void resetDate(String node,JDateField date){
		preferences.put(PROGRAMNAME+"."+node,(String)null);
		preferences.isDirty();
		date.setDateInt(0);
	}
}
