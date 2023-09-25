package com.moneydance.modules.features.reportwriter2.view.controls;


import java.util.List;
import java.util.SortedMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.ReportFieldType;
import com.moneydance.modules.features.reportwriter2.IndentXML;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoRecord;
import com.moneydance.modules.features.reportwriter2.view.LayoutPane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

public class ReportLayout extends StackPane implements XmlWriter, UndoAction {
	private String name;
	private String text;
	private SimpleDoubleProperty x;
	private SimpleDoubleProperty y;
	private double fieldWidth = 100.0;
	private double fieldHeight = 17.0;
	private boolean inError=false;
	private boolean limitError=false;
	private FieldBorder border;
	private ReportLayoutLabel label;;
	private ReportField field;
	private ReportLayout thisObj;
	private ReportBanner banner;
	private Boolean selected = false;
	private Boolean blank=false;
	private ReportTemplate template;
	private ReportStyle layoutStyle=null;
	private ReportFieldType type=null;
	private TreeItem<LayoutTreeNode> treeItem;

	public ReportLayout() {
		super();
		inError = false;
		label = new ReportLayoutLabel(this);
		label.setSelected(selected);
		label.setInError(false);
		x = new SimpleDoubleProperty();
		y = new SimpleDoubleProperty();
	}

	public ReportLayout(ReportTemplate template, ReportBanner banner, ReportField field, double x, double y) {
		super();
		label = new ReportLayoutLabel(this);
		label.setSelected(selected);
		label.setInError(false);
		this.x = new SimpleDoubleProperty();
		this.y = new SimpleDoubleProperty();
		this.template = template;
		this.banner = banner;
		setField(field);
		this.name = field.getName();
		this.text = field.getFieldText();
		this.x.set(x);
		this.y.set(y);
		this.type = field.getReportType();
		this.blank=false;
		inError = false;
		setUpField();
	}

	public void draw() {
		resetStyle();
		label.setVisible(true);
		if (!withinLimits (x.get(),y.get()) || inError)
			drawError();
		else
			if (selected)
				drawSelected();
			else
				drawUnselected();

	}
	public void resetStyle() {
		ReportStyle textStyle=null;
		if ( layoutStyle == null) {
			if (banner.getBannerStyle() == null)
				textStyle = template.getDefaultStyle();
			else
				textStyle =banner.getBannerStyle();
		}
		else
			textStyle = layoutStyle;
		if (textStyle != null)
			textStyle.getDisplayText(label,text);
	}
	public void deleteField() {
		getChildren().clear();
	}
	public void drawError() {
		inError = true;
		label.setInError(inError);
		border.drawError();
	}

	public void drawSelected() {
		border.drawSelected();
	}

	public void drawUnselected() {
		border.drawUnselected();
	}

	public ReportBanner getBanner() {
		return banner;
	}

	public ReportField getField() {
		return field;
	}

	public double getFieldHeight() {
		return fieldHeight;
	}

	public double getFieldWidth() {
		return fieldWidth;
	}

	public String getName() {
		return name;
	}
	public double getRelativeX() {
		return x.get()/(Constants.LAYOUTDIVIDER*10.0);
	}
	public double getRelativeY() {
		return y.get()/(Constants.LAYOUTDIVIDER*10.0);
	}

	public String getText() {
		return text;
	}
	public ReportFieldType getType() {
		return type;
	}
	public double getX() {
		return x.get();
	}
	public SimpleDoubleProperty getXProperty() {
		return x;
	}
	public SimpleDoubleProperty getYProperty() {
		return y;
	}

	public double getY() {
		return y.get();
	}

	public Boolean getBlank() {
		return blank;
	}

	public boolean isInError() {
		return inError;
	}

	public boolean isSelected() {
		return selected;
	}
	public ReportStyle getLayoutStyle() {
		return layoutStyle;
	}

	public void setLayoutStyle(ReportStyle layoutStyle) {
		this.layoutStyle = layoutStyle;
	}

	public void loadXML(XMLEventReader xmlEventReader, XMLEvent xmlEvent, ReportTemplate template) {
		this.template = template;
		boolean done = false;
		while (!done) {
			try {
				if (xmlEvent.isStartElement()) {
					StartElement startElement = xmlEvent.asStartElement();
					switch (startElement.getName().getLocalPart()) {
					case XmlWriter.LAYOUTNAME:
						xmlEvent = xmlEventReader.nextEvent();
						name = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.LAYOUTTEXT:
						xmlEvent = xmlEventReader.nextEvent();
						text = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.LAYOUTWIDTH:
						xmlEvent = xmlEventReader.nextEvent();
						fieldWidth = Double.parseDouble(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.LAYOUTHEIGHT:
						xmlEvent = xmlEventReader.nextEvent();
						fieldHeight = Double.parseDouble(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.LAYOUTX:
						xmlEvent = xmlEventReader.nextEvent();
						x.set(Double.parseDouble(xmlEvent.asCharacters().getData()));
						break;
					case XmlWriter.LAYOUTY:
						xmlEvent = xmlEventReader.nextEvent();
						y.set(Double.parseDouble(xmlEvent.asCharacters().getData()));
						break;
					case XmlWriter.LAYOUTBLANK:
						xmlEvent=xmlEventReader.nextEvent();
						blank = xmlEvent.asCharacters().getData().equals("true")?true:false;
						break;
					case XmlWriter.LAYOUTFLDTYPE:
						xmlEvent = xmlEventReader.nextEvent();
						type = ReportFieldType.valueOf(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.LAYOUTSTYLE:
						xmlEvent = xmlEventReader.nextEvent();
						String styleName =xmlEvent.asCharacters().getData();
						SortedMap<String,ReportStyle> styles = template.getStyles();
						if (styles.containsKey(styleName))
							layoutStyle = styles.get(styleName);
						else {
							layoutStyle = null;
							Main.rwDebugInst.debugThread("ReportLayout", "loadXML", MRBDebug.DETAILED,"layout style not found "+styleName);
						}
						break;
					case XmlWriter.LAYOUTSRCFLD:
						Attribute keyAttr = startElement
								.getAttributeByName(new QName(XmlWriter.SRCFLDKEY));
						if (keyAttr != null) {
							String key = keyAttr.getValue().toLowerCase();
							switch (type) {
							case DATABASE:
								field = template.getSelectedFields().get(key);
								break;
							case GROUPNAME:
								break;
							case LABEL:
								field = template.getLabels().get(key);
								break;
							case TOTAL:
								break;
							case VARIABLE:
								field = template.getVariables().get(key);
								break;
							default:
								break;
							
							}
						}
					}
				}
				if (xmlEvent.isEndElement()) {
					EndElement endElement = xmlEvent.asEndElement();
					if (endElement.getName().getLocalPart().equals(XmlWriter.LAYOUTFIELD))
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

	public void move(double x, double y) {
		template.setDirty(true);
		banner.layoutController.ruler.deselectField(this);
		banner.ruler.deselectField(this);
		this.x.set(x);
		this.y.set(y);
		this.relocate(x, y);
		banner.layoutController.ruler.selectField(this);
		banner.ruler.selectField(this);
		draw();
	}

	public void selected(boolean selected) {
		Main.rwDebugInst.debugThread("ReportLayout", "selected", MRBDebug.DETAILED, "select  "+selected);
		this.selected=selected;
		if (label != null)
			label.setSelected(this.selected);
		draw();
		if (selected) {
			banner.layoutController.ruler.selectField(thisObj);
			banner.ruler.selectField(thisObj);
		}
		else {
			banner.layoutController.ruler.deselectField(thisObj);
			banner.ruler.deselectField(thisObj);
		}
	}

	public void setBanner(ReportBanner banner) {
		this.banner = banner;
	}

	public void setField(ReportField field) {
		this.field = field;
	}

	public void setFieldHeight(double height) {
		template.setDirty(true);
		label.setPrefHeight(height);
		label.setMinHeight(height);
		this.fieldHeight = height;
		border.setBorderHeight(height);
		border.toBack();
	}

	public void setFieldWidth(double width) {
		template.setDirty(true);
		label.setPrefWidth(width);
		label.setMinWidth(width);
		this.fieldWidth = width;
		border.setBorderWidth(width);
		border.toBack();
	}

	public void setInError(boolean inError) {
		this.inError = inError;
		label.setInError(inError);
	}

	public void setName(String name) {
		this.name = name;
		template.setDirty(true);
	}

	public void setSelected(boolean selected) {
		this.selected=selected;
		if (label != null) {
			label.setSelected(this.selected);
		}
	}
	public void setText(String text) {
		this.text = text;
		template.setDirty(true);
	}

	public void setUpField() {
		inError = false;
		label = new ReportLayoutLabel(this);
		label.setInError(false);
		label.setPrefWidth(fieldWidth-2);
		label.setMinWidth(fieldWidth-2);
		label.setPrefHeight(fieldHeight-2);
		label.setMinHeight(fieldHeight-2);
		label.getStyleClass().add(Constants.BANNERFIELD);

		border= new FieldBorder(fieldWidth,fieldHeight);
		thisObj = this;
		label.setOnDragDetected((t) -> {
			Main.rwDebugInst.debugThread("ReportLayout", "label.drag", MRBDebug.DETAILED, "Label drag detected");
			if (!selected) {
				t.consume();
				return;
			}
			Dragboard db = thisObj.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString(Constants.MOVEFIELD);
			db.setContent(content);
			LayoutPane.fieldBeingMoved=this;
		});
		this.getChildren().addAll(border, label);
		this.relocate(this.x.get(), this.y.get());
		border.relocate(this.x.get(), this.y.get());
		border.toBack();

	}
	public void setType(ReportFieldType type) {
		this.type = type;	
	}

	public void setX(double x) {
		this.x.set(x);
	}

	public void setY(double y) {
		this.y.set(y);
	}

	public void setBlank(Boolean blank) {
		this.blank = blank;
	}

	public TreeItem<LayoutTreeNode> getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(TreeItem<LayoutTreeNode> treeItem) {
		this.treeItem = treeItem;
	}

	public boolean withinLimits(double x, double y) {
		limitError = false;
		if (x < template.getLeftMargin() * Constants.LAYOUTDIVIDER * 10.0)
			return limitError;
		double rightMargin = (template.getPaperWidth() - template.getRightMargin() * 10) * Constants.LAYOUTDIVIDER;
		if ((x + fieldWidth) > rightMargin)
			return limitError;
		if (y < 0)
			return limitError;
		if (y + fieldHeight > banner.getCanvasHeight())
			return limitError;
		limitError = true;
		return limitError;
	}

	public void writeXML(IndentXML writer) throws XMLStreamException {
		try {
			writer.writeDataElement(XmlWriter.LAYOUTNAME, name);
			writer.writeDataElement(XmlWriter.LAYOUTTEXT, text);
			writer.writeDataElement(XmlWriter.LAYOUTWIDTH, String.valueOf(fieldWidth));
			writer.writeDataElement(XmlWriter.LAYOUTHEIGHT, String.valueOf(fieldHeight));
			writer.writeDataElement(XmlWriter.LAYOUTX, String.valueOf(x.get()));
			writer.writeDataElement(XmlWriter.LAYOUTY, String.valueOf(y.get()));
			writer.writeDataElement(XmlWriter.LAYOUTFLDTYPE, String.valueOf(type));
			writer.writeDataElement(XmlWriter.LAYOUTBLANK,blank?"true":"false");
			if (layoutStyle!= null)
				writer.writeDataElement(XmlWriter.LAYOUTSTYLE,layoutStyle.getName());

			writer.writeStartElement(XmlWriter.LAYOUTSRCFLD);
			writer.writeAttribute(XmlWriter.SRCFLDKEY, field.getKey());
			writer.writeIndentedEndElement();
		} catch (XMLStreamException e) {
			throw e;
		}

	}

	@Override
	public void undo(List<UndoFieldDetail>changes) {
		Main.rwDebugInst.debugThread("ReportLayout", "undo", MRBDebug.DETAILED, "undo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.LAYOUTX:
				Double newX;
				try {
					newX = Double.valueOf(detailRec.getBeforeValue());
				}
				catch (NumberFormatException e) {
					newX = x.get();
				}
				move(newX,y.get());
				banner.checkOverlap();
				break;
			case XmlWriter.LAYOUTY:
				Double newY;
				try {
					newY = Double.valueOf(detailRec.getBeforeValue());
				}
				catch (NumberFormatException e) {
					newY = x.get();
				}
				move(x.get(),newY);
				banner.checkOverlap();
				break;
			}
		}
		draw();
	}
	

	@Override
	public void redo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("ReportLayout", "undo", MRBDebug.DETAILED, "redo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.LAYOUTX:
				Double newX;
				try {
					newX = Double.valueOf(detailRec.getAfterValue());
				}
				catch (NumberFormatException e) {
					newX = x.get();
				}
				move(newX,y.get());
				banner.checkOverlap();
				break;
			case XmlWriter.LAYOUTY:
				Double newY;
				try {
					newY = Double.valueOf(detailRec.getAfterValue());
				}
				catch (NumberFormatException e) {
					newY = x.get();
				}
				move(x.get(),newY);
				banner.checkOverlap();
				break;
		}
		}
		draw();
		
	}
}
