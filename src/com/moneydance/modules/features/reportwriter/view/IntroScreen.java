package com.moneydance.modules.features.reportwriter.view;



import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.Parameters;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class IntroScreen {
	private Stage stage;
	private Scene scene;
	private VBox display;
	private CheckBox donotDisplay;
	private Button okBtn;
	private char [] bout = new char[102400];
	private Font boldFont;
	private Parameters params;
	private Font normFont = Font.font("Helvetica", FontWeight.NORMAL, 10);
	public IntroScreen(Parameters params) {
		this.params = params;
		boldFont = Font.font(Main.labelFont.getFamily(), FontWeight.BOLD, Main.labelFont.getSize());
		normFont = Font.font(Main.labelFont.getFamily(), FontWeight.NORMAL, Main.labelFont.getSize());
		display = new VBox(10);
		TextFlow textFlow = new TextFlow();
		textFlow.setLineSpacing(5.0);
		boolean boldStarted = false;
		boolean firstBold = false;
		boolean firstEnd = false;
		Text text=null;
		for (int j=0;j<Constants.INTROTEXT.length;j++ ) {
			bout = Constants.INTROTEXT[j].toCharArray();
			for (int i=0;i<bout.length;i++) {
				if(bout[i] == '*' && i<bout.length-1 && bout[i+1]=='*' && !boldStarted) {
					if (text == null) {
						text = new Text();
						
					}
					firstBold = true;
					boldStarted=true;
					text.setFont(boldFont);
					continue;
				}
				if (bout[i]=='*' && firstBold) {
					firstBold = false;
					continue;
				}
				if(bout[i] == '*' && i<bout.length-1 && bout[i+1]=='*' && boldStarted) {
					Text newLine = new Text("\n");
					textFlow.getChildren().addAll(text,newLine);
					text=null;
					firstBold = true;
					continue;
				}
				if (bout[i] == ':' && i<bout.length-1 && bout[i+1]==':') {
					Text newLine = new Text("\n");
					textFlow.getChildren().addAll(text,newLine);
					text=null;
					firstEnd = true;
					continue;
				}
				if (bout[i] == ':' && firstEnd)
					continue;
				if(bout[i] != ':') {
					if (text == null) {
						text=new Text();
						text.setFont(normFont);
					 }
					text.setText(text.getText()+bout[i]);
					continue;
				}
			}
		}
		if (text!= null)
			textFlow.getChildren().add(text);
		display.getChildren().add(textFlow);
		donotDisplay = new CheckBox("Do not display again");
		donotDisplay.setSelected(false); 
		okBtn = new Button();
		if (Main.loadedIcons.okImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				params.setIntroScreen(!donotDisplay.isSelected());
				params.save();
				stage.close();
				return;
			}
		});

		display.getChildren().addAll(donotDisplay,okBtn);
		display.setStyle("-fx-padding:10;");
		stage = new Stage();
		scene = new Scene(display);
		stage.setScene(scene);
		stage.showAndWait();
	}
}
