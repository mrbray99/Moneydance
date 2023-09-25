package com.moneydance.modules.features.reportwriter2.view;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.FieldExpressionController;


public class FieldExpression {
	private PopUpScreen screen;
	private FieldExpressionController controller;
	public FieldExpression (ReportTemplate template, ReportField field) {
		controller = new FieldExpressionController();
		screen = new PopUpScreen(Constants.FIELDEXPRESPANE,controller,(Integer parm)-> {
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
		controller.setUpFields(template,field);
		screen.display();			
	}

}
