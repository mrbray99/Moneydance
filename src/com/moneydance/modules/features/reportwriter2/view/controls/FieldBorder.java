package com.moneydance.modules.features.reportwriter2.view.controls;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FieldBorder extends Pane {
	private Rectangle fieldRet;
	private Rectangle leftTopRet = new Rectangle(2,2);
	private Rectangle rightTopRet = new Rectangle(2,2);
	private Rectangle centreTopRet = new Rectangle(2,2);
	private Rectangle leftBotRet = new Rectangle(2,2);
	private Rectangle rightBotRet = new Rectangle(2,2);
	private Rectangle centreBotRet = new Rectangle(2,2);
	public FieldBorder(double width, double height) {
		fieldRet = new Rectangle(width, height);
		fieldRet.setFill(Color.TRANSPARENT);
		this.getChildren().addAll(fieldRet,leftTopRet,rightTopRet,centreTopRet,leftBotRet,rightBotRet,centreBotRet);
		setBorderWidth(width);
		setBorderHeight(height);
		drawUnselected();
	}
	public void setBorderWidth(double width) {
		fieldRet.setWidth(width);
		leftTopRet.setLayoutX(0);
		rightTopRet.setLayoutX(width-2);
		centreTopRet.setLayoutX(width/2-2);
		leftBotRet.setLayoutX(0);
		rightBotRet.setLayoutX(width-2);
		centreBotRet.setLayoutX(width/2-2);
	}
	public void setBorderHeight(double height) {
		fieldRet.setHeight(height);
		leftTopRet.setLayoutY(0);
		rightTopRet.setLayoutY(0);
		centreTopRet.setLayoutY(0);
		leftBotRet.setLayoutY(height-2);
		rightBotRet.setLayoutY(height-2);
		centreBotRet.setLayoutY(height-2);
		
	}
	public void drawSelected() {
		fieldRet.getStyleClass().clear();
		fieldRet.getStyleClass().add(Constants.SELECTEDBANNERFIELD);
		fieldRet.setVisible(true);
		leftTopRet.getStyleClass().clear();
		leftTopRet.getStyleClass().add(Constants.SELECTEDBANNERFIELD);
		leftTopRet.setVisible(true);
		rightTopRet.getStyleClass().clear();
		rightTopRet.getStyleClass().add(Constants.SELECTEDBANNERFIELD);
		rightTopRet.setVisible(true);
		centreTopRet.getStyleClass().clear();
		centreTopRet.getStyleClass().add(Constants.SELECTEDBANNERFIELD);
		centreTopRet.setVisible(true);
		leftBotRet.getStyleClass().clear();
		leftBotRet.getStyleClass().add(Constants.SELECTEDBANNERFIELD);
		leftBotRet.setVisible(true);
		rightBotRet.getStyleClass().clear();
		rightBotRet.getStyleClass().add(Constants.SELECTEDBANNERFIELD);
		rightBotRet.setVisible(true);
		centreBotRet.getStyleClass().clear();
		centreBotRet.getStyleClass().add(Constants.SELECTEDBANNERFIELD);
		centreBotRet.setVisible(true);
		this.toBack();
	}
	public void drawError() {
		fieldRet.getStyleClass().clear();
		fieldRet.getStyleClass().add(Constants.BANNERFIELDERROR);
		fieldRet.setVisible(true);
		leftTopRet.getStyleClass().clear();
		leftTopRet.getStyleClass().add(Constants.BANNERFIELDERROR);
		leftTopRet.setVisible(true);
		rightTopRet.getStyleClass().clear();
		rightTopRet.getStyleClass().add(Constants.BANNERFIELDERROR);
		rightTopRet.setVisible(true);
		centreTopRet.getStyleClass().clear();
		centreTopRet.getStyleClass().add(Constants.BANNERFIELDERROR);
		centreTopRet.setVisible(true);
		leftBotRet.getStyleClass().clear();
		leftBotRet.getStyleClass().add(Constants.BANNERFIELDERROR);
		leftBotRet.setVisible(true);
		rightBotRet.getStyleClass().clear();
		rightBotRet.getStyleClass().add(Constants.BANNERFIELDERROR);
		rightBotRet.setVisible(true);
		centreBotRet.getStyleClass().clear();
		centreBotRet.getStyleClass().add(Constants.BANNERFIELDERROR);
		centreBotRet.setVisible(true);
		this.toBack();
	}
	public void drawUnselected() {
		fieldRet.setVisible(false);
		leftTopRet.setVisible(false);
		rightTopRet.setVisible(false);
		centreTopRet.setVisible(false);
		leftBotRet.setVisible(false);
		rightBotRet.setVisible(false);
		centreBotRet.setVisible(false);
		this.toBack();
	}
}
