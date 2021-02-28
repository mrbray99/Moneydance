package com.moneydance.modules.features.reportwriter.view;

import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;

import javafx.scene.layout.GridPane;

public abstract class ScreenPanel extends GridPane {
    protected int SCREENWIDTH; 
    protected int SCREENHEIGHT; 

	public ScreenPanel () {
		super();
	}
	public void fireDataChanged() {
		
	}
	public void resize() {
		SCREENWIDTH =Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEWIDTH,Constants.DATASCREENWIDTH);
		SCREENHEIGHT =Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEHEIGHT,Constants.DATASCREENHEIGHT);
		setPrefSize(SCREENWIDTH,SCREENHEIGHT);

	}
	protected void openMsg() {
		
	}
	protected void saveMsg () {
		
	}
	protected void deleteMsg() {
		
	}
	protected void newMsg() {
		
	}
}
