package com.moneydance.modules.features.reportwriter2.view.controls;


import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.moneydance.modules.features.reportwriter2.IndentXML;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.Utilities;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.TextAlignment;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.LayoutPaneController;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class ReportStyle implements XmlWriter,   UndoAction {
	private String name="";
	private String fontName="";
	private String fontWeight;
	private Integer fontSize=0;
	private boolean italic=false;
	private boolean underline=false;
	private TextAlignment align=null;
	private Font font;
	private Color colour;
	private boolean selected=false;
	private boolean defaultStyle=false;
	private TreeItem<LayoutTreeNode> treeItem;
	public ReportStyle() {
		fontName = Main.reportFont;
		fontSize = Main.reportFontSize;
		fontWeight = Constants.FONTNORMAL;;
		italic = false;
		underline=false;
		align=TextAlignment.LEFT;
		colour=Color.BLACK;
		defaultStyle=false;
	}
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	public String getFontWeight() {
		return fontWeight.toString();
	}
	public void setFontWeight(String fontWeight) {
		this.fontWeight =fontWeight;
	}
	public Integer getFontSize() {
		return fontSize;
	}
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}
	public boolean isItalic() {
		return italic;
	}
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	public boolean isUnderline() {
		return underline;
	}
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	public TextAlignment getAlign() {
		return align;
	}
	public void setAlign(TextAlignment align) {
		this.align = align;
	}
	public Color getColour() {
		return colour;
	}
	public void setColour(Color colour) {
		this.colour = colour;
	}
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}


	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected, LayoutPaneController controller) {
		this.selected = selected;
	}

	public boolean isDefaultStyle() {
		return defaultStyle;
	}

	public void setDefaultStyle(boolean defaultStyle) {
		this.defaultStyle = defaultStyle;
	}
	public TreeItem<LayoutTreeNode> getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(TreeItem<LayoutTreeNode> treeItem) {
		this.treeItem = treeItem;
	}

	public void getDisplayText(Label sampleText,String displayText) {
		sampleText.setText(displayText);
		if (name=="")
			sampleText.setText("no Style");
		else {
			String textFill = "-fx-text-fill: "+Utilities.colorToWeb(colour)+";";
			FontPosture posture = isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
			FontWeight weight = FontWeight.valueOf(getFontWeight());
			Font font = Font.font(getFontName(), weight, posture, getFontSize());
			sampleText.setFont(font);
			sampleText.setUnderline(isUnderline());
			sampleText.setStyle(textFill);
			if (isUnderline())
				sampleText.setUnderline(true);
		}
		return;
	}

	@Override
	public void writeXML(IndentXML writer) throws XMLStreamException {
		writer.writeDataElement(XmlWriter.STYLENAME, name);
		writer.writeDataElement(XmlWriter.STYLEFONT, fontName);
		writer.writeDataElement(XmlWriter.STYLEWEIGHT,fontWeight.toString());
		writer.writeDataElement(XmlWriter.STYLESIZE, String.valueOf(fontSize));
		writer.writeDataElement(XmlWriter.STYLEITALIC, italic ? "true" : "false");
		writer.writeDataElement(XmlWriter.STYLEUNDERLINE, underline ? "true" : "false");
		writer.writeDataElement(XmlWriter.STYLEALIGN, align.toString());
		writer.writeDataElement(XmlWriter.STYLECOLOUR, Utilities.colorToWeb(colour));		
		writer.writeDataElement(XmlWriter.STYLEDEFAULT, defaultStyle ? "true" : "false");
		
	}
	@Override
	public void loadXML(XMLEventReader xmlEventReader, XMLEvent xmlEvent, ReportTemplate template)
			throws XMLStreamException {
		boolean done = false;
		while (!done) {
			try {
				if (xmlEvent.isStartElement()) {
					StartElement startElement = xmlEvent.asStartElement();
					switch (startElement.getName().getLocalPart()) {
					case XmlWriter.STYLENAME:
						xmlEvent = xmlEventReader.nextEvent();
						name = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.STYLEFONT:
						xmlEvent = xmlEventReader.nextEvent();
						fontName = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.STYLEWEIGHT:
						xmlEvent = xmlEventReader.nextEvent();
						fontWeight= xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.STYLESIZE:
						xmlEvent = xmlEventReader.nextEvent();
						fontSize = Integer.parseInt(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.STYLEITALIC:
						xmlEvent = xmlEventReader.nextEvent();
						italic = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true : false;
						break;
					case XmlWriter.STYLEUNDERLINE:
						xmlEvent = xmlEventReader.nextEvent();
						underline = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true : false;
						break;
					case XmlWriter.STYLECOLOUR:
						xmlEvent = xmlEventReader.nextEvent();
						colour = Utilities.webStringToColor(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.STYLEDEFAULT:
						xmlEvent = xmlEventReader.nextEvent();
						defaultStyle = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true: false;
						break;
						}
				}
				if (xmlEvent.isEndElement()) {
					EndElement endElement = xmlEvent.asEndElement();
					if (endElement.getName().getLocalPart().equals(XmlWriter.STYLE))
						done = true;
				}
				if (!done && xmlEventReader.hasNext())
					xmlEvent = xmlEventReader.nextEvent();
				else
					done = true;
			} catch (XMLStreamException e) {
				throw e;
			}
		}

		
	}
	public Font getFontObject() {
		FontPosture posture = isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
		FontWeight weight = FontWeight.valueOf(getFontWeight());
		font = Font.font(getFontName(), weight, posture, getFontSize());
		return font;
	}
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redo(List<UndoFieldDetail> changes) {
		// TODO Auto-generated method stub
		
	}
	
	

}
