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

/**
 * Constants used throughout the extension
 * @author Mike Bray
 *
 */
public abstract class Constants {
	/*
	 * General
	 */

    /*
	 * Program control
	 */
	public static final String PROGRAMNAME = "jasperreports";
	public static final String EXTENSIONNAME = "Jasper Report Writer";
	public static final String SERVERNAME = "MDJasperServer";
	public static final String CLASSNAME = "com.mrb.jasper.JasperReport";
	public static final String DEFAULTPARAMETERFILE = "mrbrwdefault";
	public static final String PARMEXTENSION = ".mbjrp";
	public static final String SELEXTENSION = ".mbjrs";
	public static final String DATAEXTENSION = ".mbjrd";
	public static final String TEMPLATEEXTENSION = ".jasper";
	public static final String REPORTEXTENSION = ".mbjrr";
	public static final String SETENVIRONMENT = "setEnvironment";
	public static final String COMPILEREPORT = "compileReport";
	public static final String FILLREPORT = "fillReport";
	public static final String VIEWREPORT = "viewReport";
	public static final String LOGCONFIGFILE = "jasperreports-log4j.properties";
	public static final String CONFIGURATIONFILE = "jasperConfiguration.json";
	public static final String NODIRECTORY = "none";
	public static final String RESOURCES = "/com/moneydance/modules/features/jasperreports/resources/";
	public static final String VIEWREPORTCMD = "ViewReport";
/*
 * Preferences
 */
	
	public static final String CRNTFRAMEWIDTH = "framewidth";
	public static final String CRNTFRAMEHEIGHT = "frameheight";
	public static final String TEMPLATECRNTCOLWIDTH = "templatecolumnwidth";
	public static final String REPORTCRNTCOLWIDTH = "templatecolumnwidth";
	public static final String DATACRNTCOLWIDTH = "templatecolumnwidth";
	public static final String SELECTIONCRNTCOLWIDTH = "templatecolumnwidth";
	public static final String DATAPANEWIDTH = "datapanewidth";
	public static final String DATAPANEHEIGHT = "datapaneheight";
/*
 * Screen parameters
 */
	public static final int TEMPLATENUMTABLECOLS = 2; 
	public static final int[]  TEMPLATEDEFAULTCOLWIDTH = {100,40};
	public static final int REPORTNUMTABLECOLS = 2; 
	public static final int[]  REPORTDEFAULTCOLWIDTH = {100,40};
	public static final int DATANUMTABLECOLS = 2; 
	public static final int[]  DATADEFAULTCOLWIDTH = {100,40};
	public static final int SELECTIONNUMTABLECOLS = 2; 
	public static final int[]  SELECTIONDEFAULTCOLWIDTH = {100,40};
	public static final int FRAMEWIDTH = 800;	
	public static final int FRAMEDEPTH = 800;
	public static final int MAINSCREENWIDTH = 1000;
	public static final int MAINSCREENHEIGHT = 800;
	public static final int DATASCREENHEIGHT = 300;
	public static final int DATASCREENWIDTH = 300;
	public static final int DATASCREENWIDTHMIN = 100;
	public static final int DATASCREENWIDTHMAX = 600;
	public static final int DATASCREENHEIGHTMIN = 100;
	public static final int DATASCREENHEIGHTMAX = 600;
 
    /*
     * Menu names
     * 
     */
    public static final String MENUFILE = "File";
    public static final String MENUREPORT = "Manage Reports";
    public static final String MENUDATA = "Manage Data";
    public static final String MENUVIEW = "View Reports";
    public static final String MENUHELP= "Help";
    public static final String ITEMFILEOPTIONS = "Set Options";
    public static final String ITEMFILECLOSE = "Close Extension";
    public static final String ITEMFILESAVE = "Save Parameters";
    public static final String ITEMFILESAVEAS = "Save Parameters As";
    /*
     * Selection Names
     */
    public static final String SELACCOUNTS = "Accounts";
    public static final String SELADDRESS = "Address";
    public static final String SELBUDGETS = "Budgets";
    public static final String SELCURRENCY = "Currency";
    public static final String SELSECURITY = "Security";
    public static final String SELTRANSACTION = "Transaction";
    /*
     * parameter field names
     */
    public static final String PARMFROMDATE=  "parmfromdate";
    public static final String PARMTODATE=  "parmtodate";
    public static final String PARMSELACCT=  "parmselacct";
    public static final String PARMASSET=  "parmasset";
    public static final String PARMBANK=  "parmbank";
    public static final String PARMCREDIT=  "parmcredit";
    public static final String PARMLIABILITY=  "parmliability";
    public static final String PARMLOAN=  "parmloan";
    public static final String PARMINVESTMENT=  "parminvestment";
    public static final String PARMACCOUNTS=  "parmaccounts";
    public static final String PARMSELCAT=  "parmselcat";
    public static final String PARMINCOME=  "parmincome";
    public static final String PARMEXPENSE=  "parmexpense";
    public static final String PARMCATEGORIES=  "parmcategories";
    public static final String PARMSELBUDGET=  "parmselbudget";
    public static final String PARMBUDGET=  "parmbudget";
    public static final String PARMBUDITEMS=  "parmbuditems";
    public static final String PARMSELCURRENCY=  "parmselcurrency";
    public static final String PARMCURRENCY=  "parmcurrency";
    public static final String PARMSELTRANS=  "parmseltrans";
    public static final String PARMCLEARED=  "parmcleared";
    public static final String PARMUNRECON=  "parmunrecon";
    public static final String PARMRECON=  "parmrecon";
    public static final String PARMTRANSFER=  "parmtransfer";
    public static final String PARMOTHERSIDE=  "parmotherside";

    
}
