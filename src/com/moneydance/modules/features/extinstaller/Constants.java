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

/**
 * Constants used throughout the extension
 * @author Mike Bray
 *
 */
public abstract class Constants {
	/*
	 * General
	 */
	public static final Integer MINIMUMVERSIONNO = 2019;
	public static final String MRBLOADIMAGE = "mrb icon2.png";
	public static final String REPOSITORY = "https://bitbucket.org/mikerb/moneydance-2019/downloads/";
	public static final String MODULESFILE = "buildnumbers.csv";
	

    /*
	 * Program control
	 */
	public static final String PROGRAMNAME = "extinstaller";
	public static final String PACKAGEPATH = "com/moneydance/modules/features/extinstaller";
	public static final String RESOURCEPATH = "com/moneydance/modules/features/extinstaller/resources";
	public static final String DEBUGLEVEL ="debuglevel";
	public static final String GETBUILDNUM ="getbuildnum";
	public static final String RETURNBUILD ="buildnums";

	public static final String SHOWCONSOLECMD = "showconsole";
	public static final String HELLOCMD = "showconsole";
	/*
	 * screen sizes
	 */
	public static final String CRNTFRAMEWIDTH = "framewidth";
	public static final String CRNTFRAMEDEPTH = "framedepth";
	public static final String CRNTCOLWIDTH = "columnwidth";

	public static final int FRAMEWIDTH = 620;	
	public static final int FRAMEHEIGHT = 750;
	public static final int EXTPANELHEIGHT = 175;

  
    /*
     * Module information
     */
    public static class ModuleInfo {
    	public String name;
    	public String fileName;
    	public String description;
    	public String url;
    	public ModuleInfo(String namep,String fileNamep, String descriptionp, String urlp) {
    		name = namep;
    		fileName = fileNamep;
    		description = descriptionp;
    		url= urlp;
    	}
    }
    public static ModuleInfo modules[] = {
    		new ModuleInfo("Budget Generator","budgetgen.mxt",
    		"Helps the user generate budget information.  Allows the user to calculate the budget amount for each category and time period",
    		"https://bitbucket.org/mikerb/moneydance-2019/wiki/Budget%20Gen"),
    		new ModuleInfo("Budget Report","budgetreport.mxt",
    		"Helps the user generate budget reports.  The report is presented in a table format by category and time period",
    		"https://bitbucket.org/mikerb/moneydance-2019/wiki/Budget%20Report"),
    		new ModuleInfo("MB Extension Loader","extinstaller.mxt",
    		"This extension. Helps the user install, update and remove extensions authored by Mike Bray",
    		"http://bitbucket.org/mikerb/Moneydance-2019/wiki"),
    		new ModuleInfo("File Display","filedisplay.mxt",
    		"Displays the data in the current Moneydance file",
    		"https://bitbucket.org/mikerb/moneydance-2019/wiki/File%20Display"),
    		new ModuleInfo("Security Transaction Loader","loadsectrans.mxt",
    		"Loads miscellaneous transactions for an investment account from a .csv file. Can be configured to match the keys in the .csv file with Moneydance transaction types",
    		"https://bitbucket.org/mikerb/moneydance-2019/wiki/Security%20Transaction%20Load"),
    		new ModuleInfo("Security Price Loader","securitypriceload.mxt",
    		"Loads current security and exchange rate prices from a .csv file",
    		"https://bitbucket.org/mikerb/moneydance-2019/wiki/Security%20Price%20Load"),
    		new ModuleInfo("Security History Loader","securityhistoryload.mxt",
    		"Loads historical security and exchange rate prices from a .csv file",
    		"https://bitbucket.org/mikerb/moneydance-2019/wiki/Security%20Price%20Load"),
    		new ModuleInfo("Quote Loader","securityquoteload.mxt",
    		"Interfaces with the Rhumba back end server (from hleofxquotes) to obtain current security and exchange rate prices from either yahoo.com or ft.com and load them into Moneydance",
    		"https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote%20Loader"),
    		new ModuleInfo("Rhumba","rhumba.mxt",
    		"Back end server (from hleofxquotes) to obtain current security and exchange rate prices from either yahoo.com or ft.com and load them into Moneydance, part of the Quote Loader extension",
    		"http://bitbucket.org/mikerb/Moneydance-2019/wiki"),
    		new ModuleInfo("Quote Load Web Engine","hleofxquotes.jar",
    		"Web engine (from hleofxquotes) to obtain current security and exchange rate prices from either yahoo.com or ft.com for the Rhumba extension, part of the Quote Loader extension",
    		"http://bitbucket.org/mikerb/Moneydance-2019/wiki")

    		};
    	public static final String RHUMBA="rhumba.mxt";
    	public static final String WEBSERVER="hleofxquotes.jar";
    	public static final String QUOTELOADER="securityquoteload.mxt";
    	public static final String QUOTELOADERPGM="securityquoteload";
}
