package com.moneydance.modules.features.reportwriter2.view.controls;

import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.IndentXML;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.Constants.FieldAlign;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.LayoutPaneController;

import javafx.scene.control.TreeItem;

public class ReportFormat implements XmlWriter, UndoAction{
	private ReportTemplate template;
	private String name;
	private Constants.FieldPattern formatType=null;
	private Integer patternDecPlaces=null;
	private String patternText=null;
	private boolean useMMark=false;
	private boolean useRedNeg=false;
	private boolean useCurrSign=false;
	private boolean selected=false;
	private boolean defaultFormat=false;
	private TreeItem<LayoutTreeNode> treeItem;
	private Constants.FieldAlign fieldAlign;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Constants.FieldPattern getFormatType() {
		return formatType;
	}

	public void setFormatType(Constants.FieldPattern formatType) {
		this.formatType = formatType;
	}

	public Integer getPatternDecPlaces() {
		return patternDecPlaces;
	}

	public void setPatternDecPlaces(Integer patternDecPlaces) {
		this.patternDecPlaces = patternDecPlaces;
	}

	public String getPatternText() {
		return patternText;
	}

	public void setPatternText(String patternText) {
		this.patternText = patternText;
	}

	public boolean isUseMMark() {
		return useMMark;
	}

	public void setUseMMark(boolean useMMark) {
		this.useMMark = useMMark;
	}

	public boolean isUseRedNeg() {
		return useRedNeg;
	}

	public void setUseRedNeg(boolean useRedNeg) {
		this.useRedNeg = useRedNeg;
	}

	public boolean isUseCurrSign() {
		return useCurrSign;
	}

	public void setUseCurrSign(boolean useCurrSign) {
		this.useCurrSign = useCurrSign;
	}
	
	
	public FieldAlign getFieldAlign() {
		return fieldAlign;
	}

	public void setFieldAlign(Constants.FieldAlign fieldAlign) {
		this.fieldAlign = fieldAlign;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected, LayoutPaneController controller) {
		this.selected = selected;
	}

	public boolean isDefaultFormat() {
		return defaultFormat;
	}

	public void setDefaultFormat(boolean defaultFormat) {
		this.defaultFormat = defaultFormat;
	}
	public TreeItem<LayoutTreeNode> getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(TreeItem<LayoutTreeNode> treeItem) {
		this.treeItem = treeItem;
	}
	
	
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redo(List<UndoFieldDetail> changes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeXML(IndentXML writer) throws XMLStreamException {
		try {
			writer.writeDataElement(XmlWriter.FORNAME,name);
			if (formatType != null)
				writer.writeDataElement(XmlWriter.FORTYPE,formatType.toString());
			if (patternDecPlaces != null) {
				writer.writeDataElement(XmlWriter.FORPATDEC,patternDecPlaces.toString());
			}
			if (patternText != null) {
				writer.writeDataElement(XmlWriter.FORPATTEXT,patternText);
			}
			writer.writeDataElement(XmlWriter.FORPATMMARK,useMMark?"true":"false");
			writer.writeDataElement(XmlWriter.FORPATREDNEG,useRedNeg?"true":"false");
			writer.writeDataElement(XmlWriter.FORPATCURR,useCurrSign?"true":"false");
			writer.writeDataElement(XmlWriter.FORALIGN,fieldAlign.toString());
		}catch (XMLStreamException e) {
			throw e;
		}

	}

	@Override
	public void loadXML(XMLEventReader xmlEventReader, XMLEvent xmlEvent, ReportTemplate template)
			throws XMLStreamException {
		this.template = template;
		boolean done = false;
		while (!done) {
			try {
				if (xmlEvent.isStartElement()) {
					StartElement startElement = xmlEvent.asStartElement();
					switch (startElement.getName().getLocalPart()) {
					case XmlWriter.FORNAME:
						xmlEvent=xmlEventReader.nextEvent();
						name = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.FORTYPE:
						xmlEvent=xmlEventReader.nextEvent();
						formatType = Constants.FieldPattern.valueOf(xmlEvent.asCharacters().getData());
						break;						
					case XmlWriter.FORPATDEC:
						xmlEvent=xmlEventReader.nextEvent();
						patternDecPlaces = Integer.parseInt(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.FORPATTEXT:
						xmlEvent=xmlEventReader.nextEvent();
						if (xmlEvent.isEndElement())
							patternText = "";
						else
							patternText = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.FORPATMMARK:
						xmlEvent=xmlEventReader.nextEvent();
						useMMark = xmlEvent.asCharacters().getData().equals("true")?true:false;
						break;
					case XmlWriter.FORPATREDNEG:
						xmlEvent=xmlEventReader.nextEvent();
						useRedNeg = xmlEvent.asCharacters().getData().equals("true")?true:false;
						break;
					case XmlWriter.FORPATCURR:
						xmlEvent=xmlEventReader.nextEvent();
						useCurrSign = xmlEvent.asCharacters().getData().equals("true")?true:false;
						break;
					case XmlWriter.FORALIGN:
						xmlEvent=xmlEventReader.nextEvent();
						fieldAlign  = Constants.FieldAlign.valueOf(xmlEvent.asCharacters().getData());
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
				e.printStackTrace();
			}
		}


	}

}
