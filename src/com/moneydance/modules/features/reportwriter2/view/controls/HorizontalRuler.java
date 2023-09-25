package com.moneydance.modules.features.reportwriter2.view.controls;


import com.moneydance.modules.features.reportwriter2.Constants;

import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class HorizontalRuler extends Ruler {
	public HorizontalRuler (Paper paper, PageOrientation layout) {
		super(paper,layout);
		rulerHeight = 40;
		setPrefWidth(rulerWidth);
		background.setPrefWidth(rulerWidth);
		background.setMinWidth(rulerWidth);
		background.setMaxWidth(rulerWidth);
		background.setPrefHeight(rulerHeight);
		background.setMinHeight(rulerHeight);
		background.setMaxHeight(rulerHeight);
		foreground.setPrefHeight(rulerHeight);
		foreground.setMinHeight(rulerHeight);
		foreground.setMaxHeight(rulerHeight);
		foreground.setPrefWidth(rulerWidth);
		foreground.setMinWidth(rulerWidth);
		foreground.setMaxWidth(rulerWidth);
	}
	@Override
	protected double getPageWidth() {
		switch (layout) {
		case LANDSCAPE:
			return paper.getHeight();
		default : 
			return paper.getWidth();
		}
	}

	@Override
	protected double getPageHeight() {
		return 40;
	}
	@Override
	protected double incrementX(double x) {
		crntScale +=0.1;
		return x+divider;
	}
	@Override
	protected double incrementY(double y) {
		return y;
	}
	@Override
	protected boolean checkDone(double x, double y,double scale) {
		if (scale > paperWidth/10)
			return true;
		return false;
	}

	@Override
	protected void writeMajorStroke(double x, double y) {
		Rectangle rect = new Rectangle();
		rect.setLayoutX(0);
		rect.setLayoutY(0);
		rect.setWidth(divider);
		rect.setHeight(10);
		if (crntScale < leftMargin || (rightMargin > 0 &&crntScale>paperWidth -rightMargin)) {
			rect.getStyleClass().add(Constants.RULERMARGINBACKGROUND);
		}
		else {
			rect.getStyleClass().add(Constants.RULERBACKGROUND);
		}
		AnchorPane holding = new AnchorPane();
		holding.setLayoutX(x);
		holding.setLayoutY(y);
		Line majorStroke = new Line();
		majorStroke.getStyleClass().add(Constants.RULERSTROKE);
		majorStroke.setStartX(0);
		majorStroke.setStartY(0);
		majorStroke.setEndX(0);
		majorStroke.setEndY(10);
		majorStroke.setStrokeWidth(1.5);
		holding.getChildren().addAll(rect,majorStroke);
		foreground.getChildren().add(holding);
	}
	@Override
	protected void writeMinorStroke(double x, double y) {
		Rectangle rect = new Rectangle();
		rect.setLayoutX(0);
		rect.setLayoutY(0);
		rect.setWidth(divider);
		rect.setHeight(10);
		if (crntScale < leftMargin || (rightMargin > 0 &&crntScale>paperWidth -rightMargin)) {
			rect.getStyleClass().add(Constants.RULERMARGINBACKGROUND);
		}
		else {
			rect.getStyleClass().add(Constants.RULERBACKGROUND);
		}
		AnchorPane holding = new AnchorPane();
		holding.setLayoutX(x);
		holding.setLayoutY(y);
		Line minorStroke = new Line();
		minorStroke.getStyleClass().add(Constants.RULERSTROKE);
		minorStroke.setStartX(0);
		minorStroke.setStartY(0);
		minorStroke.setEndX(0);
		minorStroke.setEndY(4);
		minorStroke.setStrokeWidth(0.5);
		holding.getChildren().addAll(rect,minorStroke);
		foreground.getChildren().add(holding);
		
	}
	@Override
	protected void writeMidStroke(double x, double y) {
		Rectangle rect = new Rectangle();
		rect.setLayoutX(0);
		rect.setLayoutY(0);
		rect.setWidth(divider);
		rect.setHeight(10);
		if (crntScale < leftMargin || (rightMargin > 0 &&crntScale>paperWidth -rightMargin)) {
			rect.getStyleClass().add(Constants.RULERMARGINBACKGROUND);
		}
		else {
			rect.getStyleClass().add(Constants.RULERBACKGROUND);
		}
		AnchorPane holding = new AnchorPane();
		holding.setLayoutX(x);
		holding.setLayoutY(y);
		Line midStroke = new Line();
		midStroke.getStyleClass().add(Constants.RULERSTROKE);
		midStroke.setStartX(0);
		midStroke.setStartY(0);
		midStroke.setEndX(0);
		midStroke.setEndY(6);
		midStroke.setStrokeWidth(1.0);
		holding.getChildren().addAll(rect,midStroke);
		foreground.getChildren().add(holding);
		
	}
	protected void writeSelectLine(double x, double y) {
		Line selectStroke = new Line();
		selectStroke.setStartX(x);
		selectStroke.setStartY(0);
		selectStroke.setEndX(x);
		selectStroke.setEndY(30);
		selectStroke.setStrokeWidth(1.5);
		selectStroke.getStyleClass().add(Constants.RULERFIELDLINE);
		background.getChildren().add(selectStroke);
		fieldLines.add(selectStroke);
		
	}
	protected void removeSelectLine(double x, double y) {
		Line removeLine = null;
		for (Line line:fieldLines) {
			if (line.getStartX()== x) {
				removeLine=line;
				break;
			}
		}
		if (removeLine !=null) {
			background.getChildren().remove(removeLine);
			fieldLines.remove(removeLine);
		}
	}
	protected boolean lineExists(double x, double y) {
		for (Line line:fieldLines) {
			if(line.getStartX()== x)
				return true;
		}
		return false;
	}
	protected void writeScale(double x, double y, double scale) {
		Text t=new Text();
		t.setText(String.valueOf(scale));
		t.setX(x+3);
		t.setY(y+25);
		foreground.getChildren().add(t);
		
	}
}
