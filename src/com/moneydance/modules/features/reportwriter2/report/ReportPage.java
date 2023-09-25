package com.moneydance.modules.features.reportwriter2.report;

import java.sql.ResultSet;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.RWException;
import com.moneydance.modules.features.reportwriter2.Utilities;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportStyle;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ReportPage extends Canvas {
	private double crntY;
	private GraphicsContext gc;
	private ReportTemplate template;
	public ReportPage(ReportTemplate template,double width, double height) {
		super(width, height);
		this.template = template;
		crntY = Utilities.screenToPrinter(this.template.getTopMargin()*10.0);
		gc=this.getGraphicsContext2D();
		
	}
	public void addField(ReportLayout field,ResultSet results, GroupValues groupValues) throws RWException{
		String columnValue="";
		switch(field.getType()) {
		case DATABASE:
			ReportField fldField = field.getField();
			FieldValue dbValue= groupValues.getVariableValues().get(fldField.getKey());
			if (dbValue == null ) {
					try {
						columnValue= fldField.getResultsValue(results).getText();
					}
					catch (RWException e) {
						throw new RWException ("Problem rendering report - "+e.getLocalizedMessage());
					}
			}
			else {
				if (field.getField().getOutputType()==Constants.TEXTOUTPUTTYPE)
					columnValue = dbValue.getText();
				else
					columnValue =String.valueOf(dbValue.getNumeric());
			}
			break;
		case GROUPNAME:
			break;
		case LABEL:
			columnValue =field.getText();
			break;
		case TOTAL:
			break;
		case VARIABLE:
			String fieldKey = field.getField().getKey();
			FieldValue tmpValue = groupValues.getVariableValues().get(fieldKey);
			if (tmpValue==null) {
				tmpValue=new FieldValue("",0.0,"");
				groupValues.getVariableValues().put(fieldKey, tmpValue);
			}
			if (field.getField().getOutputType()==Constants.TEXTOUTPUTTYPE)
				columnValue = tmpValue.getText();
			else
				columnValue =String.valueOf(tmpValue.getNumeric());
			break;
		default:
			break;
		
		}
		ReportStyle fieldStyle = field.getLayoutStyle();
		if (fieldStyle == null)
			fieldStyle=field.getBanner().getBannerStyle();
		if (fieldStyle==null)
			fieldStyle=field.getBanner().getTemplate().getDefaultStyle();
		if (fieldStyle != null) {
			gc.setFont(fieldStyle.getFontObject());
			gc.setFill(fieldStyle.getColour());
		}
		double printX = Utilities.screenToPrinter( field.getX()/Constants.LAYOUTDIVIDER);
		double printY = Utilities.screenToPrinter(field.getY()/Constants.LAYOUTDIVIDER);
		double printWidth = Utilities.screenToPrinter((field.getFieldWidth())/Constants.LAYOUTDIVIDER);
		gc.fillText(columnValue,printX,crntY+printY,printWidth);
		if (fieldStyle!=null && fieldStyle.isUnderline()) {
			gc.strokeLine(printX, crntY+printY+1.0, printX+printWidth,crntY+ printY+2.0);
		}
	}
	public void nextBanner(double size) {
		crntY+=size;
	}
	public void writeLine(double yPos) {
		gc.setStroke(Color.BLACK);
		gc.setFill(Color.BLACK);
		gc.strokeLine(0, crntY+yPos, this.getWidth(), crntY+yPos+2);
		crntY+=2;
	}
	public double getSpaceLeft() {
		return this.getHeight()-crntY;
	}
}
