package com.moneydance.modules.features.jasperreports;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.moneydance.modules.features.jasperreports.sandbox.MyJarLauncher;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBPreferences;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class MyJasperReport extends JFXPanel implements EventHandler<ActionEvent>{
	private Parameters params;
	private MyJarLauncher launcher;
	private Main main;
	/*
	 * Screen variables
	 */
	private Scene scene;
	private VBox menuBox;
	private GridPane mainScreen;
	private MainMenu menuBar;
	private TemplatePane templatePan;
	private SelectionPane selectionPan;
	private DataPane dataPan;
	private ReportPane reportPan;
	public int iFRAMEWIDTH = Constants.FRAMEWIDTH;
	public int iFRAMEDEPTH = Constants.FRAMEDEPTH;
	public int SCREENWIDTH;
	public int SCREENHEIGHT;

	public MyJasperReport(Main main){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException e) {}
		Main.preferences = MRBPreferences.getInstance();
		setPreferences();
		Main.debugInst.debugThread("MyJasperReports", "createScene", MRBDebug.SUMMARY, "New size "+iFRAMEWIDTH+"/"+iFRAMEDEPTH);
		this.main = main;
	}
	public Scene createScene(){
		menuBox = new VBox();
		mainScreen = new GridPane();
		menuBar = new MainMenu(this);
		menuBox.getChildren().addAll(menuBar,mainScreen);
		scene = new Scene(menuBox,iFRAMEDEPTH,iFRAMEWIDTH);
		params = new Parameters();
		if (params.getDataDirectory()== null  || params.getDataDirectory().equals(Constants.NODIRECTORY)) {
			FirstRun setParams = new FirstRun(this,params);
			if (params.getDataDirectory().equals(Constants.NODIRECTORY)) {
				Alert alert = new Alert(AlertType.ERROR,"Extension can not continue without setting the directories");
				alert.showAndWait();
			}
			else
				params.save();
		}
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override 
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				Main.debugInst.debugThread("MyJasperReports", "createScene", MRBDebug.SUMMARY, "New width "+newSceneWidth);
				updateWidth(newSceneWidth.intValue());
			}
		});
		scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override 
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				Main.debugInst.debugThread("MyJasperReports", "createScene", MRBDebug.SUMMARY, "New height "+newSceneHeight);
				updateHeight(newSceneHeight.intValue());
			}
		});
		setPreferences(); // set the screen sizes
		templatePan = new TemplatePane(params);
		dataPan = new DataPane(params);
		reportPan = new ReportPane(params);
		selectionPan = new SelectionPane(params);
		mainScreen.add(templatePan, 0, 0);
		GridPane.setMargin(templatePan,new Insets(10,10,10,10));
		mainScreen.add(selectionPan, 1, 0);
		GridPane.setMargin(selectionPan,new Insets(10,10,10,10));
		mainScreen.add(dataPan, 0, 1);
		GridPane.setMargin(dataPan,new Insets(10,10,10,10));
		mainScreen.add(reportPan, 1, 1);
		GridPane.setMargin(reportPan,new Insets(10,10,10,10));
		return scene;
	}

	protected void getQuote() throws IOException {
		MyJarLauncher launcher = Main.getLauncher();
		if (launcher == null) {
			main.initServices();
		}
		getReport();
	}

	protected void getReport() throws IOException {
		launcher = Main.getLauncher();
		String [] args = new String [] {"Line 1","line 2"};
		Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling setEnvironment");
		try {
			launcher.getSetEnvironment().invoke(launcher.getInstance(), new Object[] {args});
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling setEnvironment done");
		}
		catch (InvocationTargetException | IllegalAccessException e) {
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling setEnvironment failed");
			e.printStackTrace();
		}
		Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling compile");
		try {
			launcher.getCompileReport().invoke(launcher.getInstance(), "Report");
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling compile done");
		}
		catch (InvocationTargetException | IllegalAccessException e) {
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling compile failed");
			e.printStackTrace();
		}
		Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling fill");
		try {
			launcher.getFillReport().invoke(launcher.getInstance(),"Report");
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling fill done");
		}
		catch (InvocationTargetException | IllegalAccessException e) {
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling fill failed");
			e.printStackTrace();
		}
		Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling view");
		try {
			launcher.getViewReport().invoke(launcher.getInstance(), "Report");
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling view done");
		}
		catch (InvocationTargetException | IllegalAccessException e) {
			Main.debugInst.debug("MyJasperReport", "getReport", MRBDebug.SUMMARY,"Calling view failed");
			e.printStackTrace();
		}

	}
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() instanceof MenuItem) {
			MenuItem mItem = (MenuItem) event.getSource();
			String command = mItem.getText();
			switch (command) {
			case Constants.ITEMFILECLOSE :
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						main.closeConsole();
					}
				});
				break;
			case Constants.ITEMFILESAVE :
				params.save();
				break;
			case Constants.ITEMFILEOPTIONS :
				FirstRun setParams = new FirstRun(this,params);
				if (params.getDataDirectory().equals(Constants.NODIRECTORY)) {
					Alert alert = new Alert(AlertType.ERROR,"Extension can not continue without setting the directories");
					alert.showAndWait();
				}
				else
					params.save();			
			}
		}

	}

	/*
	 * preferences
	 */
	private void setPreferences() {
		iFRAMEWIDTH = Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH,Constants.MAINSCREENWIDTH);
		iFRAMEDEPTH = Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEHEIGHT,Constants.MAINSCREENHEIGHT);
	}
	private void updateWidth(int width) {
		Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEWIDTH,width);
		updatePreferences(width, iFRAMEDEPTH);

	}
	private void updateHeight(int height) {
		Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTFRAMEHEIGHT,height);			
		updatePreferences(iFRAMEWIDTH, height);
	}

	private void updatePreferences(int width, int height) {
		int dataWidth = (width-20)/2;
		int dataHeight = (height-20)/2;
		if (dataWidth<Constants.DATASCREENWIDTHMIN)
			dataWidth = Constants.DATASCREENWIDTHMIN;
		else
			if (dataWidth>Constants.DATASCREENWIDTHMAX)
				dataWidth = Constants.DATASCREENWIDTHMAX;
		if (dataHeight<Constants.DATASCREENHEIGHTMIN)
			dataHeight = Constants.DATASCREENHEIGHTMIN;
		else
			if (dataHeight>Constants.DATASCREENHEIGHTMAX)
				dataHeight = Constants.DATASCREENHEIGHTMAX;				
		Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DATAPANEWIDTH,dataWidth);
		Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DATAPANEHEIGHT,dataHeight);
		Main.preferences.isDirty();
		selectionPan.resize();
		reportPan.resize();
		templatePan.resize();
		dataPan.resize();
		Main.debugInst.debugThread("MyJasperReports", "updatePreferences", MRBDebug.SUMMARY, "New size "+width+"/"+height);
	}


}
