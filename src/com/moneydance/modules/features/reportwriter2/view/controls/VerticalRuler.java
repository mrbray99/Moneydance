package com.moneydance.modules.features.reportwriter2.view.controls;

import com.moneydance.modules.features.reportwriter2.Constants;

import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class VerticalRuler extends Ruler {
	public VerticalRuler (Paper paper, PageOrientation layout) {
		super(paper,layout);
	}
	public VerticalRuler(double width, double height) {
		super(width, height);
		setPrefHeight(rulerHeight);
		rulerWidth = 40;
		foreground.setPrefWidth(rulerWidth);
		foreground.setMinWidth(rulerWidth);
		foreground.setMaxWidth(rulerWidth);
		foreground.setPrefHeight(rulerHeight);
		foreground.setMinHeight(rulerHeight);
		foreground.setMaxHeight(rulerHeight);
		background.setPrefWidth(rulerWidth);
		background.setMinWidth(rulerWidth);
		background.setMaxWidth(rulerWidth);
		background.setPrefHeight(rulerHeight);
		background.setMinHeight(rulerHeight);
		background.setMaxHeight(rulerHeight);
	}
	@Override
	protected double getPageWidth() {
		return 40.0;
	}

	@Override
	protected double getPageHeight() {
		switch (layout) {
		case PORTRAIT:
			return paper.getHeight();
		default : 
			return paper.getWidth();
		}
	}

	@Override
	protected void writeMajorStroke(double x, double y) {
		Line majorStroke = new Line();
		majorStroke.getStyleClass().add(Constants.RULERSTROKE);
		majorStroke.setStartX(x);
		majorStroke.setStartY(y);
		majorStroke.setEndX(x+10);
		majorStroke.setEndY(y);
		majorStroke.setStrokeWidth(1.5);
		foreground.getChildren().add(majorStroke);
	}

	@Override
	protected void writeMidStroke(double x, double y) {
		Line midStroke = new Line();
		midStroke.getStyleClass().add(Constants.RULERSTROKE);
		midStroke.setStartX(x);
		midStroke.setStartY(y);
		midStroke.setEndX(x+6);
		midStroke.setEndY(y);
		midStroke.setStrokeWidth(1.0);
		foreground.getChildren().add(midStroke);

	}

	@Override
	protected void writeMinorStroke(double x, double y) {
		Line minorStroke = new Line();
		minorStroke.getStyleClass().add(Constants.RULERSTROKE);
		minorStroke.setStartX(x);
		minorStroke.setStartY(y);
		minorStroke.setEndX(x+4);
		minorStroke.setEndY(y);
		minorStroke.setStrokeWidth(0.5);
		foreground.getChildren().add(minorStroke);

	}
	protected void writeSelectLine(double x, double y) {
		Line selectStroke = new Line();
		selectStroke.setStartX(0);
		selectStroke.setStartY(y);
		selectStroke.setEndX(30);
		selectStroke.setEndY(y);
		selectStroke.setStrokeWidth(1.5);
		selectStroke.getStyleClass().add(Constants.RULERFIELDLINE);
		background.getChildren().add(selectStroke);
		fieldLines.add(selectStroke);
		
	}
	protected boolean lineExists(double x, double y) {
		for (Line line:fieldLines) {
			if(line.getStartY()== y)
				return true;
		}
		return false;
	}

	protected void removeSelectLine(double x, double y) {
		Line removeLine = null;
		for (Line line:fieldLines) {
			if (line.getStartY()== y) {
				removeLine=line;
				break;
			}
		}
		if (removeLine !=null) {
			background.getChildren().remove(removeLine);
			fieldLines.remove(removeLine);
		}
	}

	@Override
	protected double incrementX(double x) {
		return x;
	}

	@Override
	protected double incrementY(double y) {
		crntScale +=0.1;
		return y+divider;
	}

	@Override
	protected boolean checkDone(double x, double y, double scale) {
		if (smallRuler) {
			if ( scale >scaleHeight)
				return true;
			return false;
		}
		if (scale>paperHeight)
			return true;
		return false;

	}

	protected void writeScale(double x, double y, double scale) {
		Text t=new Text();
		t.setText(String.valueOf(scale));
		t.setX(x+10);
		t.setY(y+10);
		foreground.getChildren().add(t);		
	}
	public void setHeight(double height) {
		rulerHeight = height;
		scaleHeight = rulerHeight/(Constants.LAYOUTDIVIDER*10);
		paperHeight = scaleHeight;
		foreground.setPrefHeight(rulerHeight);
		foreground.setMinHeight(rulerHeight);
		foreground.setMaxHeight(rulerHeight);
		background.setPrefHeight(rulerHeight);
		background.setMinHeight(rulerHeight);
		background.setMaxHeight(rulerHeight);
	}
}
