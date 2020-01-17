package com.moneydance.modules.features.mrbutil;

import java.awt.Image;

import javax.swing.Icon;

import com.infinitekind.moneydance.model.PeriodType;
import com.moneydance.apps.md.controller.UserPreferences;
import com.moneydance.apps.md.view.resources.MDResourceProvider;
import com.moneydance.apps.md.view.resources.Resources;

public class MRBGUI implements MDResourceProvider {
	private Resources resources;
	private UserPreferences userPref;

	public MRBGUI () {
		userPref = UserPreferences.getInstance();
		resources = userPref.getResources();
	}
	@Override
	public Icon getIcon(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resources getResources() {
		return resources;
	}

	@Override
	public String getStr(String strKey) {
		return resources.getString(strKey);
	}

	public String getString(PeriodType type) {
		return resources.getString(type);
	}

}
