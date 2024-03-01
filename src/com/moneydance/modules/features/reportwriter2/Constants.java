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
package com.moneydance.modules.features.reportwriter2;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.infinitekind.moneydance.model.BudgetItem;
import com.moneydance.modules.features.reportwriter2.databeans.*;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.BEANFIELDTYPE;

import javafx.print.PageOrientation;
import javafx.print.Paper;

/**
 * Constants used throughout the extension
 * @author Mike Bray
 *
 */
public abstract class Constants {
	/*
	 * General
	 */
	public static final String ABOUT1 = "   Moneydance Report Writer Extension\n";
	public static final String ABOUT2 = "         By Mike Bray (2021)\n";
	public static final String ABOUT3 = "         Build ";
	public static final String ABOUT4 = " Jasper Reports\u00AE and Jasper Studio\u00AE\n"
									  + "	are registered names of Tibco Jaspersoft\u00AE\n\n";
	public static final String ABOUT5 = " Icons used with this extension are supplied by http://icons8.com\n";
   /*
	 * Program control
	 */
	public static final String CLOSEFIELDLIST = "closefieldlist";
	public static final String CLOSEFIELDSSCREEN = "closefieldsscreen";
	public static final String CLOSELAYOUTSCREEN = "closelayoutscreen";
	public static final String COMPILEREPORT = "compileReport";
	public static final String CONFIGURATIONFILE = "reportwriterConfiguration.json";
	public static final String DATABASEADAPTER = "databaseadapter.xml";
	public static final String DATABASEDRIVER = "org.h2.Driver";
	public static final String DATAEXTENSION = ".mbjrd";
	public static final String DEFAULTDATABASE = "database.mv.db";
	public static final String DEFAULTPARAMETERFILE = "mrbrwdefault";
	public static final String DEFAULTSTYLENAME="Default";
	public static final String HEADER1STYLENAME="Header 1";
	public static final String HEADER2STYLENAME="Header 2";
	public static final String HEADER3STYLENAME="Header 3";
	public static final String EXTENSIONNAME = "Report Writer 2";
	public static final String FILLREPORT = "fillReport";
	public static final String FIRSTRUNDIR = "firstrundir";
	public static final String FIRSTRUNTEXT="firstrun.txt";
	public static final String HELPURL = "https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer";
	public static final String MOVEFIELD = "movefield";
	public static final String NODIRECTORY = "none";
	public static final String PARMEXTENSION = ".mbjrp";
	public static final String PLACEFIELD = "placefield";
	public static final String PROGRAMNAME = "reportwriter2";
	public static final String REPORTEXTENSION = ".mbjrr";
	public static final String REPOSITORY = "https://bitbucket.org/mikerb/moneydance-2019/downloads/";
	public static final String RESOURCES = "/com/moneydance/modules/features/reportwriter2/resources/";
	public static final String SAMPLESFILE = "rwsamples.zip";
	public static final String SELEXTENSION = ".mbjrs";
	public static final String SETENVIRONMENT = "setEnvironment";
	public static final String SETEXTENSION = "setExtension";
	public static final String SHOWHELP = "showhelp";
	public static final String STYLESHEET="reportwriter.css";
	public static final String TEMPLATEEXTENSION = ".mbtmx";
	public static final String TEMPLATELISTFILE = "templateList.txt";
	public static final String TEMPLATESFILE = "tmtemplates.zip";
	public static final String VIEWREPORT = "viewReport";
	public static final String VIEWREPORTCMD = "ViewReport";

	/*
	 * FXML file names
	 */
	public static final String TEMPLATESCREENFXML = "templatedatapane.fxml";
	public static final String STYLEDETAILFXML="styledetailpane.fxml";
	public static final String BANNERDETAILFXML="bannerdetailpane.fxml";
	public static final String FIELDDETAILFXML="fielddetailpane.fxml";
	public static final String RESIZEBANNER = "resizebanner";
	public static final String LAYOUTPANE= "LayoutPane.fxml";
	public static final String FIELDPANE= "fieldSelectPane.fxml";
	public static final String FIELDEXPRESPANE = "fieldExp.fxml";
	public static final String GROUPSELECTPANE = "groupSelectPane.fxml";
	/*
	 * layout sub menu actions
	 */
	public static final String ACTIONDELETE = "delete";
	public static final String ACTIONSHOW = "show properties";
	public static final String ACTIONMOVEUP = "move up";
	public static final String ACTIONMOVEDOWN = "move down";
	public static final String ACTIONADDFOOTER = "add footer";
	public static final String ACTIONEDIT= "edit group details";
	public static final String ACTIONSELECT= "select fields";
	public static final String ACTIONADDBANNER= "add banner";
	public static final String ACTIONADDGROUP= "add group";
	public static final String ACTIONSHOWSTYLE= "show/hide standard styles";
	public static final String ACTIONADDSTYLE= "create style";
	public static final String ACTIONSELECTALL= "select all";
	public static final String ACTIONADDLABEL= "create label";
	public static final String ACTIONADDVARIABLE= "create variable";

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
	public static final String FIELDPANEWIDTH = "fieldpanewidth";
	public static final String FIELDPANEHEIGHT = "fieldpaneheight";
	public static final String CRNTFRAMEX = "framex";
	public static final String CRNTFRAMEY = "framey";
	public static final String DEBUGLEVEL="debuglevel";
	public static final String LAYOUTCOL1="layoutcol1";//ok
	public static final String LAYOUTCOL2="layoutcol2";//ok
	public static final String LAYOUTMAXIMISE="layoutmaximise";//ok
	public static final String LAYOUTWIDTH="layoutwidth";//ok
	public static final String LAYOUTHEIGHT="layoutheight";//ok
	public static final String FIELDMAXIMISE="fieldmaximise";//ok
	public static final String FIELDWIDTH="fieldwidth";//ok
	public static final String FIELDHEIGHT="fieldheight";//ok
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
	public static final int DATASCREENWIDTH = 800;
	public static final int DATADATASCREENHEIGHT = 550;
	public static final int DATADATASCREENWIDTH = 830;
	public static final int DATASELECTSCREENHEIGHT = 350;
	public static final int DATASELECTSCREENWIDTH = 800;
	public static final int DATAREPORTSCREENHEIGHT = 370;
	public static final int DATAREPORTSCREENWIDTH = 800;
	public static final int DATASCREENWIDTHMIN =800;
	public static final int DATASCREENHEIGHTMIN = 600;
	public static final int FIELDSCREENHEIGHT = 400;
	public static final int FIELDSCREENWIDTH = 300;
	public static final int MINSCREENWIDTH=420;
	public static final int MINSCREENHEIGHT=430;
	public static final double MINLAYOUTWIDTH=300.0;
	public static final double MINLAYOUTHEIGHT=500.0;
	public static final double MINFIELDWIDTH=300.0;
	public static final double MINFIELDHEIGHT=500.0;
	public static final double MINLAYOUTFIELDSPERC=0.15;
	public static final double MINLAYOUTBANNERPERC=0.15;
	public static final double MAXLAYOUTFIELDSPERC=0.25;
	public static final double MAXLAYOUTBANNERPERC=0.25;
	public static final int LAYOUTDIVIDER = 5;
	public static final String RULERBACKGROUND = "rulerBackground";
	public static final String RULERMARGINBACKGROUND = "rulerMarginBackground";
	public static final String RULERFOREGROUND = "rulerForeground";
	public static final String RULERFIELDLINE = "rulerFieldLine";
	public static final String RULERSTROKE = "rulerStroke";
	public static final String BANNERFIELD = "bannerField";
	public static final String BANNERFIELDERROR = "bannerFieldError";
	public static final String SELECTEDBANNERFIELD = "selectedBannerField";
	public static final String OPTIONMESSAGES = "optionMessages";
	public static final String FONTNORMAL = "NORMAL";
	public static final String FONTBOLD = "BOLD";
	

	/*
	 * Window Names
	 */
	public static final String WINSELECTIONDATA = "WINSELECTIONDATA";
	public static final String WINDATADATA = "WINDATADATA";
	public static final String WINREPORTDATA = "WINREPORTDATA";
	public static final String WINTEMPLATEDATA = "WINTEMPLATEDATA";
	/*
	 * not present values
	 */
	public static final String MISSINGSTRING="****no value*****";
	public static final Long MISSINGLONG=-99999999L;
	public static final Integer MISSINGINT=-99999999;
	public static final Date MISSINGDATE=Utilities.getSQLDate(19000101);
	public static final Double MISSINGDOUBLE=-99999999.9;
	public static final String CANCELPRESSED="***cancel***";
	
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
    public static final String PARMTODAY= "parmtoday";
    public static final String PARMSELACCT=  "parmselacct";
    public static final String PARMSELDATES=  "parmseldates";
    public static final String PARMACCOUNTS=  "parmaccounts";
    public static final String PARMASSET=  "parmasset";
    public static final String PARMBANK=  "parmbank";
    public static final String PARMCREDIT=  "parmcredit";
    public static final String PARMLIABILITY=  "parmliability";
    public static final String PARMLOAN=  "parmloan";
    public static final String PARMINACTIVE = "parminactive";
    public static final String PARMINVESTMENT=  "parminvestment";
    public static final String PARMFROMCHEQUE=  "parmfromcheque";
    public static final String PARMTOCHEQUE=  "parmtocheque";
     public static final String PARMSELCAT=  "parmselcat";
    public static final String PARMINCOME=  "parmincome";
    public static final String PARMEXPENSE=  "parmexpense";
    public static final String PARMCATEGORIES=  "parmcategories";
    public static final String PARMSELBUDGET=  "parmselbudget";
    public static final String PARMBUDGET=  "parmbudget";
    public static final String PARMBUDITEMS=  "parmbuditems";
    public static final String PARMSELCURRENCY=  "parmselcurrency";
    public static final String PARMCURRENCY=  "parmcurrency";
    public static final String PARMSELSECURITY=  "parmselsecurity";
    public static final String PARMSECURITY=  "parmsecurity";
    public static final String PARMSELTRANS=  "parmseltrans";
    public static final String PARMSELINVTRANS=  "parmselinvtrans";
    public static final String PARMCLEARED=  "parmcleared";
    public static final String PARMUNRECON=  "parmunrecon";
    public static final String PARMRECON=  "parmrecon";
    public static final String PARMTRANSFER=  "parmtransfer";
    public static final String PARMPARENTTRAN=  "parmparenttran";
    public static final String PARMOTHERTRAN=  "parmothertran";
    public static final String PARMINVACCTS=  "parminvaccts";
    public static final String PARMTAGS=  "parmtags";
    public static final String PARMFLDACCT = "parmfldacct";
    public static final String PARMFLDADDR = "parmfldaddr";
    public static final String PARMFLDBUDG = "parmfldbudg";
    public static final String PARMFLDCAT = "parmfldcat";
    public static final String PARMFLDCUR = "parmfldcur";
    public static final String PARMFLDSEC = "parmfldsec";
    public static final String PARMFLDLOTS = "parmfldlots";
    public static final String PARMFLDCURR = "parmfldcurr";
    public static final String PARMFLDSECP = "parmfldsecp";
    public static final String PARMFLDBUDGI = "parmfldbnudgi";
    public static final String PARMFLDTRAN = "parmfldtran";
    public static final String PARMFLDINVTRAN = "parmfldinvtran";
    public static final String PARMFLDREM = "parmfldrem";
    public static final String PARMLASTDB="parmlastdb";
    /*
     * Error messages
     */
    public static final String ERRFIELDSOVERLAP="Overlapping fields";


    /*
     * Lists
     */
	public static final String[] DELIMITERS = {",",".","#","|","Tab",";",":"};
    /*
     * types
     */
	public enum TextAlignment {LEFT,RIGHT,CENTRE,JUSTIFIED};
	public enum ReportType {REPORT, DATABASE, SPREADSHEET, CSV};
	public enum ReportFieldType {DATABASE,LABEL,TOTAL,GROUPNAME,VARIABLE}
	public enum BannerType {
		TITLE("Title",10),
		PAGEHEAD("Page Header",20),
		COLUMNHEAD("Column Header",30),
		GROUPHEAD("Group Header",40),
		DETAIL("Detail",50),
		GROUPFOOT("Group Footer",60),
		COLUMNFOOT("Column Footer",70),
		PAGEFOOT("Page Footer",80),
		END("End Page",90);
		private String name;
		private Integer position;
		BannerType(String name, Integer position){
			this.name = name;
			this.position = position;
		}
		public String getName() {
			return name;
		}
		public Integer getPosition() {
			return position;
		}
		public static BannerType findType(String type) {
			for (BannerType tmpType:BannerType.values()) {
				if (type.equals(tmpType.getName()))
					return tmpType;
			}
			return null;
		}
	}
	public enum DatasetType {TRANSACTION,ACCOUNT,BUDGET,CATEGORY,CURRENCY,SECURITY,REMINDER,ADDRESS,INVESTMENT}
	public enum SortColumn {
		FIRST ("First"),
		SECOND("Second"),
		THIRD("Third");
		private String value;
		SortColumn(String value){
			this.value=value;
		}
		public String getValue(){
			return value;
		}
		@Override
		public String toString() {
			return this.getValue();
		}
		public static SortColumn getEnum(String value) {
			for (SortColumn column:values()) {
				if(column.getValue().equalsIgnoreCase(value)) 	return column;
			}
			throw new IllegalArgumentException();	
				
		}
	}
	public static final String[] SortColumnValues = {SortColumn.FIRST.toString(),SortColumn.SECOND.toString(),SortColumn.THIRD.toString()};
	public enum NodeType{ROOT,OUTLINE,AVAILABLEFIELDS,BANNERS,STYLES,STYLE,VARIABLES,
		BANNER,FIELD,VARIABLE,DATABASE,RECORD,DATABASEFIELD,LABELS,LABEL,
		FUNCTIONS, FUNCTION,OPERATOR,OPERATORS}
	public static int NUMBEROUTPUTTYPE = 0;
	public static int TEXTOUTPUTTYPE = 1;
	public enum FuncType {NUMERIC, STRING, LOGICAL, DATE, OPERATOR};
	public enum ExpNodeType {ROOT,FIELD,NUMBER, STRING,FUNCTION,PARAMETER,OPERATOR,VARIABLE,BRACKET};
	public enum FieldFunction {
		DATE("Date",FuncType.DATE,FuncType.STRING,"Todays date"),
		TIME("Time",FuncType.DATE,FuncType.STRING,"Time now"),
		DAY("Day",FuncType.DATE,FuncType.NUMERIC,"Day of Month",FuncType.DATE),
		MONTH("Month",FuncType.DATE,FuncType.NUMERIC,"Month of year",FuncType.DATE),
		YEAR("Year",FuncType.DATE,FuncType.NUMERIC,"Year",FuncType.DATE),
		WEEKDAY("Weekday",FuncType.DATE,FuncType.NUMERIC,"Day of week",FuncType.DATE),
		WEEKNUM("WeekNum",FuncType.DATE,FuncType.NUMERIC,"Week num of year",FuncType.DATE),
		IF("Condition",FuncType.LOGICAL,FuncType.NUMERIC,"Numeric Condition",FuncType.NUMERIC,FuncType.OPERATOR,FuncType.NUMERIC,FuncType.NUMERIC,FuncType.NUMERIC),
		IFS("Condition",FuncType.LOGICAL,FuncType.STRING,"Text Condition",FuncType.STRING,FuncType.OPERATOR,FuncType.STRING,FuncType.STRING,FuncType.STRING),
		CONCATENATE("Concatenate",FuncType.STRING,FuncType.STRING,"String",FuncType.STRING,FuncType.STRING),
		LEFT("Left",FuncType.STRING,FuncType.STRING,"Left of String",FuncType.STRING,FuncType.NUMERIC),
		RIGHT("Right",FuncType.STRING,FuncType.STRING,"Right of String",FuncType.STRING,FuncType.NUMERIC),
		MID("Mid",FuncType.STRING,FuncType.STRING,"Middle of string",FuncType.STRING,FuncType.NUMERIC),
		MAX("Maximum",FuncType.NUMERIC,FuncType.NUMERIC,"Maximum value of group",FuncType.NUMERIC),
		MIN("Minimum",FuncType.NUMERIC,FuncType.NUMERIC,"Minimum value of group",FuncType.NUMERIC),
		SUM("Sum",FuncType.NUMERIC,FuncType.NUMERIC,"Sum of values of group",FuncType.NUMERIC),
		COUNT("Count",FuncType.NUMERIC,FuncType.NUMERIC,"Count of values of group",FuncType.NUMERIC),
		COUNTS("Count",FuncType.STRING,FuncType.NUMERIC,"Count of values of group",FuncType.STRING),
		AVR("Average",FuncType.NUMERIC,FuncType.NUMERIC,"Average of values of group",FuncType.NUMERIC);	
		private String name;
		private Integer numParms;
		private String returnString;
		private FuncType outputType;
		private List<FuncType> parms = new  ArrayList<>();
		private FuncType funcType;
		FieldFunction(String name,FuncType funcType,FuncType  outputType,String returnString){
			this.name = name;
			this.numParms= 0;
			this.returnString = returnString;
			this.outputType = outputType;
			this.funcType = funcType;
		}
		FieldFunction(String name,FuncType funcType,FuncType outputType,String returnString,FuncType parm){
			this.name = name;
			this.parms.add(parm);		
			this.numParms= 1;
			this.returnString = returnString;
			this.outputType = outputType;
			this.funcType = funcType;
		}
		FieldFunction(String name,FuncType funcType,FuncType outputType,String returnString,FuncType parm1,FuncType parm2){
			this.name = name;
			this.parms.add(parm1);		
			this.parms.add(parm2);		
			this.numParms= 2;
			this.returnString = returnString;
			this.outputType = outputType;
			this.funcType = funcType;
		}
		FieldFunction(String name,FuncType funcType,Integer numParms,FuncType outputType,String returnString,FuncType parm1,FuncType parm2, FuncType parm3){
			this.name = name;
			this.parms.add(parm1);		
			this.parms.add(parm2);		
			this.parms.add(parm3);		
			this.numParms= 3;
			this.returnString = returnString;
			this.outputType = outputType;
			this.funcType = funcType;
		}
		FieldFunction(String name,FuncType funcType,FuncType outputType,String returnString,FuncType parm1,FuncType parm2,FuncType parm3,FuncType parm4,FuncType parm5){
			this.name = name;
			this.numParms= 5;
			this.returnString = returnString;
			this.parms.add(parm1);		
			this.parms.add(parm2);		
			this.parms.add(parm3);		
			this.parms.add(parm4);		
			this.parms.add(parm5);		
			this.outputType = outputType;
			this.funcType = funcType;
		}

		public String getName() {
			return name;
		}
		public Integer getNumParms() {
			return numParms;
		}
		public List<FuncType> getParms(){
			return parms;
		}
		public String getReturnString() {
			return returnString;	
		}
		public static FieldFunction findFunction(String name) {
			for (FieldFunction field : values()) {
				if (field.getName().equalsIgnoreCase(name))
					return field;
			}
			return null;
		}
		public FuncType getOutputType() {
			return outputType;
		}
		public FuncType getFuncType() {
			return funcType;
		}
	}
	public enum LogicalOperators{
		EQUAL(".EQ."),
		LESSTHAN(".LT."),
		LESSEQUAL(".LE."),
		GREATERTHAN(".GT."),
		GREATEREQUAL(".GE."),
		SAME(".SAME.");
		private String operator;
		LogicalOperators (String operator) {
			this.operator=operator; 
		}
		public String getOperator() {
			return operator;
		}
		
	}
	public enum AnalyseType{
		SUM("Sum"),			
		AVERAGE("Average"),
		MINIMUM("Min"),
		MAXIMUM("Max"),
		COUNT("Count");
		private String value;
		AnalyseType(String value){
				this.value=value;
		}
		public String getValue(){
				return value;
		}
		@Override
		public String toString() {
			return this.getValue();
		}
		public static AnalyseType getEnum(String value) {
				for (AnalyseType type:values()) {
					if(type.getValue().equalsIgnoreCase(value)) 	return type;
				}
				throw new IllegalArgumentException();	
		}
	}
	public static final String[] AnalyseTypeValues = {AnalyseType.AVERAGE.toString(),AnalyseType.SUM.toString(),AnalyseType.MINIMUM.toString(),AnalyseType.MAXIMUM.toString(),AnalyseType.COUNT.toString()};
	public static final String[] PaperSizes = {Paper.A0.toString(),Paper.A1.toString(),Paper.A2.toString(),Paper.A3.toString(),Paper.A4.toString(),Paper.A5.toString(),Paper.LEGAL.toString(),Paper.NA_LETTER.toString()};
	public static final Paper[] PaperSizesObjects = {Paper.A0,Paper.A1,Paper.A2,Paper.A3,Paper.A4,Paper.A5,Paper.LEGAL,Paper.NA_LETTER};
	public static final String[] PageOrientations = {PageOrientation.LANDSCAPE.toString(),PageOrientation.PORTRAIT.toString()};
	public static final SortedMap<Integer, String> intervaltypes;
	static {
		intervaltypes = new TreeMap<Integer, String>();
		intervaltypes.put(BudgetItem.INTERVAL_ANNUALLY, "Annually");
		intervaltypes.put(BudgetItem.INTERVAL_BI_MONTHLY, "Bi-Monthly");
		intervaltypes.put(BudgetItem.INTERVAL_BI_WEEKLY, "Weekly");
		intervaltypes.put(BudgetItem.INTERVAL_DAILY, "Daily");
		intervaltypes.put(BudgetItem.INTERVAL_MONTHLY, "Monthly");
		intervaltypes.put(BudgetItem.INTERVAL_NO_REPEAT, "No Repeat");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_ANNUALLY, "Once Annually ");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_BI_MONTHLY,
				"Once Bi-Monthly");
		intervaltypes
				.put(BudgetItem.INTERVAL_ONCE_BI_WEEKLY, "Once Bi-Weekly ");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_MONTHLY, "Once Monthly");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_SEMI_ANNUALLY,
				"Once Semi-Annually");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_SEMI_MONTHLY,
				"Once Semi-Monthly");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_TRI_MONTHLY,
				"Once Tri-Monthly");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_TRI_WEEKLY,
				"Once Tri-Weekly");
		intervaltypes.put(BudgetItem.INTERVAL_ONCE_WEEKLY, "Once Weekly");
		intervaltypes.put(BudgetItem.INTERVAL_SEMI_ANNUALLY, "Semi-Annually");
		intervaltypes.put(BudgetItem.INTERVAL_SEMI_MONTHLY, "Semi-Monthly");
		intervaltypes.put(BudgetItem.INTERVAL_TRI_MONTHLY, "Tri-Monthly");
		intervaltypes.put(BudgetItem.INTERVAL_TRI_WEEKLY, "Tri-Weekly");
		intervaltypes.put(BudgetItem.INTERVAL_WEEKLY, "Weekly");
	} 
	public static final String[] INTROTEXT = {"**Welcome to the Report Writer extension by Mike Bray**",
			"This extension takes the data in your Moneydance file and outputs it into easy to use files. These files can be in Comma Separated Value format, an Excel workbook or an SQL Database.::",
			"The SQL database is typically used for creating reports using Jasper Reports, a third party tool from JasperSoft.::",
			"Before the extension can be used you need to tell it where you will store the parameters, the data output and any report templates from Jasper Reports.  These can all be the same folder.::",
			"To make best use of the extension it is important that you read the Help information. To access this click on the Question Mark at the bottom of the screen and click on 'Show Help'.::",
			"If you have any questions please post to the General Moneydance Public Discussion and mention me (Mike Bray) and Report Writer in the title of the post.::",
			"Regards::",
			"Mike Bray::"};
}
