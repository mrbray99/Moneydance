package com.moneydance.modules.features.reportwriter.view;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;

import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class ScreenDataPane {
	   protected int SCREENWIDTH; 
	   protected int SCREENHEIGHT;
	   protected int DEFAULTSCREENWIDTH; 
	   protected int DEFAULTSCREENHEIGHT;
	   protected String screenName;
	   protected Stage stage = null;
	   protected String screenTitle;
	   protected Scene scene;
	   protected WritableImage image;
	public ScreenDataPane () {
		
	}
	public void resize() {
		SCREENWIDTH =Main.preferences.getInt(Constants.PROGRAMNAME+"."+screenName+Constants.DATAPANEWIDTH,DEFAULTSCREENWIDTH);
		SCREENHEIGHT =Main.preferences.getInt(Constants.PROGRAMNAME+"."+screenName+Constants.DATAPANEHEIGHT,DEFAULTSCREENHEIGHT);
		if (stage !=null) {
			stage.setWidth(SCREENWIDTH);
			stage.setHeight(SCREENHEIGHT);
		}
		Main.rwDebugInst.debug("ScreenDataPane", "resize", MRBDebug.DETAILED, "Size set to "+SCREENWIDTH+"/"+SCREENHEIGHT);

	}
	public void setStage(Stage stage) {
		this.stage = stage;
		this.stage.setTitle(screenTitle);
		this.stage.getIcons().add(Main.loadedIcons.mainImg);
		stage.widthProperty().addListener((ov, oldv,newv)->{
			SCREENWIDTH = newv.intValue();
			Main.preferences.put(Constants.PROGRAMNAME+"."+screenName+Constants.DATAPANEWIDTH, SCREENWIDTH);
			Main.rwDebugInst.debug("ScreenDataPane", "setStage", MRBDebug.DETAILED, "Width set to "+SCREENWIDTH);
			widthChanged();
		});
		stage.heightProperty().addListener((ov, oldv,newv)->{
			SCREENHEIGHT = newv.intValue();
			Main.preferences.put(Constants.PROGRAMNAME+"."+screenName+Constants.DATAPANEHEIGHT, SCREENHEIGHT);
			Main.rwDebugInst.debug("ScreenDataPane", "setStage", MRBDebug.DETAILED, "Height set to "+SCREENHEIGHT);
			heightChanged();
		});
		resize();
		this.stage.sizeToScene();
	}
	public void widthChanged() {}
	public void heightChanged() {}

	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getScreenTitle() {
		return screenTitle;
	}
	public void setScreenTitle(String screenTitle) {
		this.screenTitle = screenTitle;
	}
	
}
