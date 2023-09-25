package com.moneydance.modules.features.reportwriter2.view;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;

import javafx.scene.layout.GridPane;

public abstract class ScreenPanel extends GridPane {
    protected int SCREENWIDTH; 
    protected int SCREENHEIGHT; 
    protected MyReport mainScreen;

	public ScreenPanel () {
		super();
	}
	public void fireDataChanged() {
		
	}
	public void setMainScreen(MyReport mainScreen) {
		this.mainScreen=mainScreen;
	}
	public MyReport getMainScreen() {
		return mainScreen;
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
