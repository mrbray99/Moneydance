package com.moneydance.modules.features.reportwriter2.view;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.PatternController;

public class Pattern {
	private PopUpScreen screen;
	private Constants.FieldPattern pattern;
	private PatternController controller;
	public Pattern(ReportTemplate template, ReportLayout layout, ReportField field, Constants.FieldDetailType fldType, Constants.FieldPattern pattern){
		controller = new PatternController();
		screen = new PopUpScreen(Constants.FIELDPATTERNPANE,controller,(Integer parm)-> {
				if (parm.equals(PopUpScreen.OKPRESSED)) {
					if (controller.close())
						return PopUpScreen.CLOSESCREEN;
				}
				if (parm.equals(PopUpScreen.CANCELPRESSED)) {
					if (controller.cancel())
						return PopUpScreen.CLOSESCREEN;
				}
				return PopUpScreen.LEAVEOPEN;
		});
		controller.setUpFields(template,layout,field,fldType,pattern);
		screen.display();			
	}



}
