package com.moneydance.modules.features.filedisplay;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;

import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;

/**
 * Pluggable module used to give display the contents of a moneydance file
 */

public class Main extends FeatureModule {
	private FileDisplayWindow filedisplayWindow = null;
	public static FeatureModuleContext context;
	public static String buildStr;
	private int buildNum;

	@Override
	public void init() {
		// the first thing we will do is register this module to be invoked
		// via the application toolbar
		context = getContext();
		try {
			context.registerFeature(this, "showconsole",
					getIcon("filedisplay2"), getName());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	    buildNum = getBuild();
	    buildStr = String.valueOf(buildNum);  

	}

	private Image getIcon(String action) {
		try {
			ClassLoader cl = getClass().getClassLoader();
			java.io.InputStream in = cl
					.getResourceAsStream("/com/moneydance/modules/features/filedisplay/iconip.gif");
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

	@Override
	public void cleanup() {
		closeConsole();
	}

	/** Process an invocation of this module with the given URI */
	@Override
	public void invoke(String uri) {
		String command = uri;
		@SuppressWarnings("unused")
		String parameters = "";
		int theIdx = uri.indexOf('?');
		if (theIdx >= 0) {
			command = uri.substring(0, theIdx);
			parameters = uri.substring(theIdx + 1);
		} else {
			theIdx = uri.indexOf(':');
			if (theIdx >= 0) {
				command = uri.substring(0, theIdx);
			}
		}

		if (command.equals("showconsole")) {
			showConsole();
		}
	}

	@Override
	public String getName() {
		return "File Display";
	}
	/*
	 * determine if file is being closed and close down extension
	 * 
	 */
	@Override
	public void handleEvent(String appEvent) {

	    if ("md:file:closing".equals(appEvent)) {
	        closeConsole();
	    }

	}
	private synchronized void showConsole() {
		if (filedisplayWindow == null) {
			filedisplayWindow = new FileDisplayWindow(this);
			filedisplayWindow.setVisible(true);
		} else {
			filedisplayWindow.setVisible(true);
			filedisplayWindow.toFront();
			filedisplayWindow.requestFocus();
		}
	}

	FeatureModuleContext getUnprotectedContext() {
		return getContext();
	}

	synchronized void closeConsole() {
		if (filedisplayWindow != null) {
			filedisplayWindow.goAway();
			filedisplayWindow = null;
			System.gc();
		}
	}
	
}
