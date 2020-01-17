package com.moneydance.modules.features.dataextractor;

import java.awt.Color;


public abstract class Constants {
	public static final String chSeperator = "/";
	/*
	 * Column Headers
	 */
	public static final String TOTALS = "Total";
	public static final String CATEGORY= "Category";
	public static final String TYPE = "Type";
	/*
	 * Internal order for Period Type - note must match internal order on MD Budget Period Type
	 */
	public static final int PERIODWEEKLY = 0;
	public static final int PERIODBIWEEKLY = 1;
	public static final int PERIODMONTHLY = 2;
	public static final int PERIODANNUAL = 3;

 	/*
	 * Type of screen
	 */
	public static final int ACCOUNTS = 1;
	public static final int EXPENSES = 2;
	public static final int INCOME = 3;	
	public static final int MISSING= 3;
	public static final int SELECTED= 4;
	/*
	 * Type of line
	 */
	public static final int CHILD_LINE = 1;
	public static final int PARENT_LINE = 2;
	/*
	 * Default file name for report parameters
	 */
	public static final String DEFAULTFILE = "rwparms";
	public static final String CANCELLED = "***Cancelled***";
	public static final String PROGRAMNAME = "dataextractor";
	public static final String CRNTFRAMEWIDTH = "framewidth";
	public static final String CRNTFRAMEDEPTH = "framedepth";
	public static final String PARAMETEREXTENSION = "mbde";
	

    /*
	 * Screen Size Parameters
	 */
	public static final int FRAMEWIDTH =1000;
	public static final int FRAMEDEPTH = 800;
	public static final int TOPDEPTH = 150;
	public static final int GENSCREENWIDTH = 1200;
	public static final int GENSCREENHEIGHT = 800;
	public static final int GENMINSCREENWIDTH = 600;
	public static final int GENMINSCREENHEIGHT = 300;
	public static final int TOPINSET = 5;
	public static final int BOTTOMINSET = 5;
	public static final int LEFTINSET = 5;
	public static final int RIGHTINSET = 5;
	/*
	 * Generate screen panel sizes
	 */
	public static final int GENBOTHEIGHT = 50;
	public static final int GENMINMIDWIDTH = GENMINSCREENWIDTH-50;
	public static final int GENMINMIDDEPTH = GENMINSCREENHEIGHT-10;
	public static final int GENCATPREFWIDTH = 200;
	public static final int GENCATMINWIDTH = 100;
	public static final int GENAMOUNTWIDTH = 80;
	/*
	 * Flags
	 */
	public static final int KEY_NOT_FOUND = -1;
	/*
	 * Colours
	 */
	public static final Color CLRHEAD = new Color(0xCC, 0xCC, 0xFF);
	public static final Color CLRBUDGET = new Color(0xDF, 0xF8, 0xF7);
	public static final Color CLRACTUAL = new Color(0xF9, 0xE0, 0xF3);
	public static final Color CLRPOSITIVE = Color.WHITE;
	public static final Color CLRNEGATIVE = Color.WHITE;
	public static final Color CLRFGPOSITIVE = Color.BLACK;
	public static final Color CLRFGNEGATIVE = new Color(0xFF, 0x00, 0x00);
	/*
	 * Calculate field constants
	 */
	public enum CalculateFieldType {
		FORMULA,
		STATUSLOOKUP,
		TYPELOOKUP,
		DATERANGE,
		CHEQUERANGE,
		CATEGORYLOOKUP,
		TAGLOOKUP,
		DESCRIPTION
	}
	/*
	 * Language Text Constants
	 */
	public static final String LOCALEFILENAME = "/com/moneydance/modules/features/dataextractor/strings/Locale_en.dict";
	public static final String CWC_PARAMETERS = "cwc_Parameters";
	public static final String CWTT_FILENAME="tt_filename";
	public static final String CWBTN_FIND = "btn_find";
	public static final String CWTT_BTNCHOOSE ="btn_choose";
	public static final String CWC_DATESTART = "cwc_datestart";
	public static final String CWTT_STARTDATE="tt_startdate";
	public static final String CWC_DATEEND = "cwc_dateend";
	public static final String CWTT_ENDDATE="tt_enddate";
	public static final String CWER_ENDDATE="er_enddate";
	public static final String CWC_BACKGROUND = "cwc_background";
	public static final String CWC_TEXT = "cwc_text";
	public static final String CWC_REPORTCOLOURS = "cwc_reportcolours";
	public static final String CWC_HEADERS = "cwc_headers";
	public static final String CWTT_COLOURS="tt_colours";
	public static final String CWC_CHOOSECOLOUR = "cwc_choosecolour";
	public static final String CWC_ACTUAL = "cwc_actual";
	public static final String CWTT_BACKGROUNDACT="tt_backgroundact";
	public static final String CWC_CHOOSEACTCOLOURACT = "cwc_chooseactcolouract";
	public static final String CWC_POSITIVE = "cwc_positive";
	public static final String CWTT_BACKGROUNDDIF="tt_backgrounddif";
	public static final String CWC_CHOOSEACTCOLOURDIF = "cwc_chooseactcolourdif";
	public static final String CWTT_FOREPOS="tt_forepos";
	public static final String CWC_CHOOSEPOSDIF = "cwc_chooseposdif";
	public static final String CWC_NEGATIVE = "cwc_negative";
	public static final String CWTT_BACKNEG="tt_backneg";
	public static final String CWC_CHOOSENEGDIF = "cwc_choosenegdif";
	public static final String CWTT_FORENEG="tt_foreneg";
	public static final String CWBTN_SAVE = "btn_save";
	public static final String CWTT_SAVEPARM="tt_saveparm";
	public static final String CWBTN_GENERATE = "btn_generate";
	public static final String CWBTN_CLOSE = "btn_close";
	public static final String CWTT_GENERATE="tt_generate";
	public static final String CWTT_CLOSE="tt_close";
	public static final String CW_OPT_TEXT="cwc_opt_text";
	public static final String CW_OPT_SEL="cwc_opt_sel";
	public static final String CW_OPT_AND="cwc_opt_and";
	public static final String CW_OPT_OR="cwc_opt_or";
	public static final String OPC_CATEGORY="opc_Categories";
	public static final String OPC_INCOME="opc_Income";
	public static final String OPC_EXPENSE="opc_Expense";
	public static final String OPC_SELECTBTN="opc_SelectBtn";
	public static final String OPC_FROMCHEQUE="opc_FromCheque";
	public static final String OPC_TOCHEQUE="opc_ToCheque";
	public static final String OPC_STATUS="opc_transaction";
	public static final String OPC_CLEARED="opc_cleared";
	public static final String OPC_RECONCILING="opc_reconciling";
	public static final String OPC_UNCLEARED="opc_uncleared";
	public static final String OPC_CATEGORIES="opc_categoriesSel";
	public static final String OPC_CATALL="opc_catall";
	public static final String TP_AVAILABLE = "tp_available_items";
	public static final String TP_INCLUDED = "tp_included_items";
	public static final String TP_TT_SCROLL ="tp_scroll_pane";
	public static final String TP_TT_ADD ="tp_add_selected";
	public static final String TP_BTN_SEL ="tp_btn_Sel";
	public static final String TP_BTN_DES ="tp_btn_Des";
	public static final String TP_TT_REMOVE ="tp_remove_selected";
	public static final String TP_TT_SHOW ="tp_show_selected";
	public static final String TP_SELECT ="tp_select";
	public static final String OPC_SELTAG="opc_seltag";
	public static final String OPC_TAGS="opc_tagsSel";
	public static final String OPC_DELETEBTN="opc_delbtn";
	public static final String OPC_AREYOUSURE="opc_rusure";
	public static final String CW_ACCOUNTS="cw_accounts";
	public static final String OPC_SELECTCHEQUE="opc_selcheque";
	public static final String CP_TITLE="cp_title";
	public static final String CP_FLDNAME="cp_name";
	public static final String CP_FLDTYPE="cp_type";
	public static final String COP_TITLE="cop_title";
	public static final String COP_FLDNAME="cop_name";
	public static final String COP_COLUMN="cop_column";
	public static final String COP_HEADING="cop_heading";
	public static final String SP_TITLE="sp_title";
	public static final String SP_SEQUENCE="sp_sequence";
	public static final String CF_FORMULA="cf_formula";
	public static final String CF_STATUSLOOK="cf_status";
	public static final String CF_TYPELOOK="cf_type";
	public static final String CF_DATERANGE="cf_daterange";
	public static final String CF_CHEQUERANGE="cf_chequerange";
	public static final String CF_CATEGORY="cf_categorylookup";
	public static final String CF_TAG="cf_taglookup";
	public static final String CF_DESCRIPTION="cf_description";
	public static final String CFP_TYPE="cfp_type";
	

}
