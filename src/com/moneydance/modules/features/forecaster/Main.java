/*
 * Copyright (c) 2015, Michael Bray. All rights reserved.
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
package com.moneydance.modules.features.forecaster;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import com.infinitekind.util.CustomDateFormat;
import com.infinitekind.util.DateUtil;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBPreferences;

/**
 * MoneyDance extension to forecast account balances and net worth
 * 
 * Main class to create main window
 */

public class Main extends FeatureModule {
	private static CustomDateFormat cdate;
	private static FeatureModuleContext context;
	private Selector objSelector;
	private static UserPreferences up = null;
	private static Image imgIcon;
	private String strDecimal;
	private static char chDecimal;
	private static int iMonth;
	private static int iDay;
	private static int iFiscal;
	private static int iCalendar;
	private static int iLastMonth;
	private static String strFiscal;
	private static MRBDebug objDebug;
	/*
	 * static methods to retrieve static fields
	 */
	public static FeatureModuleContext getContxt(){
		return context;
	}
	public static CustomDateFormat getCdate() {
		return cdate;
	}
	public static UserPreferences getUp() {
		return up;
	}
	public static Image getIcon() {
		return imgIcon;
	}
	public static char getDecimal() {
		return chDecimal;
	}
	public static int getFiscalMonth() {
		return iMonth;
	}
	public static int getFiscalDay() {
		return iDay;
	}
	public static int getFiscalDate(){
		return iFiscal;
	}
	public static int getCalendarDate(){
		return iCalendar;
	}
	public static int getLastMonthDate(){
		return iLastMonth;
	}

	@Override
	public void init() {
		// the first thing we will do is register this module to be invoked
		// via the application toolbar
		context = getContext();
		try {
			imgIcon = getIcon("/com/moneydance/modules/features/forecaster/mrb icon2.png");
			context.registerFeature(this, "showconsole", getIcon("forecaster"),
					getName());
			objDebug = MRBDebug.getInstance();
			objDebug.setDebugLevel(MRBDebug.DETAILED);
			objDebug.setExtension("Forecaster");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
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
	 * This does not seem to be called, make sure handleEvent("md:file:closing")
	 * is present
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
		objDebug.debug("Main","invoke",MRBDebug.SUMMARY, "Invoked");
		up = UserPreferences.getInstance();
		strDateFormat = up.getSetting(UserPreferences.DATE_FORMAT);
		cdate = new CustomDateFormat(strDateFormat);
		strDecimal = up.getSetting(UserPreferences.DECIMAL_CHAR);
		chDecimal = strDecimal.charAt(0);
		strFiscal =  up.getSetting(UserPreferences.FISCAL_YEAR_START_MMDD);
		iMonth = Integer.parseInt(strFiscal)/100;
		iDay = Integer.parseInt(strFiscal) - iMonth*100;
		Calendar calToday = Calendar.getInstance();
		int iYear = calToday.get(Calendar.YEAR);
		iFiscal = DateUtil.getDate(iYear,iMonth,iDay);
		int iToday = DateUtil.convertCalToInt(calToday);
		if (iFiscal > iToday)
			DateUtil.decrementYear(iFiscal);
		iCalendar = DateUtil.getDate(iYear, 1, 1);
		iLastMonth = DateUtil.incrementDate(DateUtil.firstDayInMonth(iToday),0,0,-1);
		MRBPreferences.loadPreferences(context);
		int theIdx = uri.indexOf('?');
		if (theIdx >= 0) {
			command = uri.substring(0, theIdx);
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
		return "Forecaster";
	}

	private synchronized void showConsole() {
		if (objSelector == null) {
			objSelector = new Selector();
			objSelector.setVisible(true);
		} else {
			objSelector.reenter();
			objSelector.setVisible(true);
			objSelector.toFront();
			objSelector.requestFocus();
		}
		if (imgIcon != null)
			objSelector.setIconImage(imgIcon);
		objSelector.setTitle("Cash Flow Forecaster");

	}

	FeatureModuleContext getUnprotectedContext() {
		return getContext();
	}

	synchronized void closeConsole() {
		if (objSelector != null) {
			objSelector.goAway();
			objSelector = null;
			MRBPreferences.forgetInstance();
			System.gc();
		}
	}
}
