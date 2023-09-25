package com.moneydance.modules.features.reportwriter2.view.controls;

import java.util.ArrayList;
import java.util.List;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Utilities;

import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

public abstract class Ruler extends AnchorPane {
	/*
	 *  paper sizes = utilities.printertoscreen (Paper sizes) i.e. /72*25.4 - millimeters
	 *  scale sizes = paper sizes /10; centimeters
	 *  ruler sizes = paper sizes * LAYOUTDIVIDER (5) pixels 
	 */
	protected double rulerWidth;  // pixels
	protected double rulerHeight; // pixels
	protected double scaleWidth; //centimetres
	protected double scaleHeight;
	protected double paperWidth;  // metric 
	protected double paperHeight; // metric
	protected Paper paper;
	protected PageOrientation layout;
	protected double divider=Constants.LAYOUTDIVIDER;
	private boolean done;
	protected AnchorPane background;
	protected AnchorPane foreground;
	protected List<Line> fieldLines;
	protected double topMargin=0.0;
	protected double leftMargin=0.0;
	protected double rightMargin=0.0;
	protected double bottomMargin=0.0;
	protected double crntScale=0.0;
	protected double crntX=0.0;
	protected double crntY=0.0;
	protected boolean smallRuler = false;
	public Ruler(Paper paper,PageOrientation layout) {
		this.paper = paper;
		this.layout = layout;
		background = new AnchorPane();
		foreground = new AnchorPane();
		background.getStyleClass().add(Constants.RULERBACKGROUND);
		foreground.getStyleClass().add(Constants.RULERFOREGROUND);
		getChildren().addAll(background,foreground);
		setPageSize();
		fieldLines= new ArrayList<Line>();
		
	}
	public Ruler(double width,double height) {
		scaleWidth = width/10;
		scaleHeight=height/10;
		rulerWidth = width*Constants.LAYOUTDIVIDER;
		rulerHeight= height*Constants.LAYOUTDIVIDER;
		smallRuler = true;
		background = new AnchorPane();
		foreground = new AnchorPane();
		background.getStyleClass().add(Constants.RULERBACKGROUND);
		foreground.getStyleClass().add(Constants.RULERFOREGROUND);
		getChildren().addAll(background,foreground);
		fieldLines= new ArrayList<Line>();
	}
	public void drawRuler() {
		foreground.getChildren().clear();
		done = false;
		crntScale = 0.0;
		crntX=0.0;
		crntY=0.0;
		while (!done) {
			writeMajorStroke(crntX,crntY);
			writeScale(crntX,crntY,Math.round(crntScale));
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMidStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			if(checkDone(crntX, crntY,crntScale)) {
				done=true;
				continue;
			}
			writeMinorStroke(crntX,crntY);
			crntX = incrementX(crntX);
			crntY = incrementY(crntY);
			done = checkDone(crntX, crntY,crntScale);
		}
	}
	public void selectField(ReportLayout field) {
		if (lineExists(field.getX(),field.getY()))
				return;
		writeSelectLine(field.getX(),field.getY());
	}
	public void deselectField(ReportLayout field) {
		if (!lineExists(field.getX(),field.getY()))
				return;
		removeSelectLine(field.getX(),field.getY());
	}
	protected boolean lineExists(double x,double y) {
		return false;
	}
	public double getPaperWidth() {
		return paperWidth;
	}
	public double getPaperHeight() {
		return paperHeight;
	}
	private void setPageSize() {
		if (layout == PageOrientation.LANDSCAPE) {
			paperWidth = Utilities.printerToScreen(paper.getHeight());
			paperHeight = Utilities.printerToScreen(paper.getWidth());
		}
		else {
			paperWidth = Utilities.printerToScreen(paper.getWidth());
			paperHeight = Utilities.printerToScreen(paper.getHeight());
		}
		scaleWidth = paperWidth/10;
		scaleHeight = paperHeight/10;
		rulerWidth = paperWidth*Constants.LAYOUTDIVIDER;
		rulerHeight = paperHeight*Constants.LAYOUTDIVIDER;
	}

	public Paper getPaper() {
		return paper;
	}
	public void setPaper(Paper paper) {
		this.paper = paper;
		setPageSize();
		drawRuler();
	}
	public PageOrientation getLayout() {
		return layout;
	}
	public void setLayout(PageOrientation layout) {
		this.layout = layout;
		setPageSize();
		drawRuler();
	}
	public double getRulerHeight() {
		return rulerHeight;
	}
	public double getTopMargin() {
		return topMargin;
	}
	public void setTopMargin(double topMargin) {
		this.topMargin = topMargin;
		drawRuler();
	}
	public double getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(double leftMargin) {
		this.leftMargin = leftMargin;
		drawRuler();
	}
	public double getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(double rightMargin) {
		this.rightMargin = rightMargin;
		drawRuler();
	}
	public double getBottomMargin() {
		return bottomMargin;
	}
	public void setBottomMargin(double bottomMargin) {
		this.bottomMargin = bottomMargin;
		drawRuler();
	}
	protected abstract double getPageWidth();
	protected abstract double getPageHeight();
	protected abstract void writeMajorStroke(double x, double y);
	protected abstract void writeMidStroke(double x, double y);
	protected abstract void writeMinorStroke(double x, double y);
	protected abstract void writeSelectLine(double x, double y);
	protected abstract void removeSelectLine(double x, double y);
	protected abstract double incrementX(double x);
	protected abstract double incrementY(double y);
	protected abstract boolean checkDone(double x, double y,double scale);
	protected abstract void writeScale(double x, double y, double scale);
}
