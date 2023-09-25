package com.moneydance.modules.features.reportwriter2.view.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoLayoutManager;
import com.moneydance.modules.features.reportwriter2.edit.UndoRecord;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail.DetailType;
import com.moneydance.modules.features.reportwriter2.Constants.BannerType;
import com.moneydance.modules.features.reportwriter2.IndentXML;
import com.moneydance.modules.features.reportwriter2.selection.BannerSelectModel;
import com.moneydance.modules.features.reportwriter2.selection.FieldSelectModel;
import com.moneydance.modules.features.reportwriter2.view.LayoutPane;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.LayoutPaneController;

import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;

public class ReportBanner extends AnchorPane implements XmlWriter, UndoAction {
	private List<ReportLayout> fields;
	private BannerType bannerType;
	private SimpleDoubleProperty canvasWidth; // pixels
	private SimpleDoubleProperty canvasHeight; // pixels
	private SimpleDoubleProperty pageHeight; // metric
	private String name;
	private ReportBanner thisObj;
	private AnchorPane background;
	private AnchorPane foreground;
	private Text nameWatermark ;
	private Rectangle resizeButton;
	private Line seperatorLine;
	private Line moveLine;
	private boolean newPage=false;
	private boolean repeat=false;
	private boolean topLine=false;
	private boolean bottomLine=false;
	private boolean hasFooter=false;
	private BooleanProperty selected=new SimpleBooleanProperty();
	private StringProperty selectedText=new SimpleStringProperty();
	private int position;
	private int otherPos;
	private BannerSelectModel<ReportBanner> bannerSelectModel;
	private FieldSelectModel<ReportLayout> fieldSelectModel;
	private Border border;
	private Border borderSelected;
	private ReportTemplate template;
	private ReportStyle bannerStyle=null;
	private ReportField groupField=null;
	private boolean bannerVisible=true;
	private TreeItem<LayoutTreeNode> treeItem;
	public LayoutPaneController layoutController;
	public VerticalRuler ruler;
	private UndoLayoutManager undoManager;
	public ReportBanner(String name,BannerType type) {
		super();
		undoManager = UndoLayoutManager.getInstance();
		thisObj = this;
		selected.set(false);
		selectedText.set("false");
		this.name=name;
		this.bannerType=type;
		position = type.getPosition();
		canvasWidth = new SimpleDoubleProperty(template.getPaperWidth() * Constants.LAYOUTDIVIDER);
		canvasHeight = new SimpleDoubleProperty(100.0);
		pageHeight = new SimpleDoubleProperty(100.0);	
	}
	public ReportBanner(BannerType type, String name, int position, ReportTemplate template,
			BannerSelectModel<ReportBanner> bannerSelectionModel,
			FieldSelectModel<ReportLayout> fieldSelectionModel, LayoutPaneController layoutController) {
		super();
		undoManager = UndoLayoutManager.getInstance();
		selected.set(false);
		selectedText.set("false");
		thisObj = this;
		this.name=name;
		this.bannerType=type;
		canvasWidth = new SimpleDoubleProperty(template.getPaperWidth() * Constants.LAYOUTDIVIDER);
		canvasHeight = new SimpleDoubleProperty(100.0);
		pageHeight = new SimpleDoubleProperty(100.0);
		this.position = position;	
		setReportBanner( template, bannerSelectionModel, fieldSelectionModel, layoutController);
	}

	public ReportBanner(ReportTemplate template) {
		thisObj = this;
		undoManager = UndoLayoutManager.getInstance();
		this.template = template;
		border = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
				new BorderWidths(1)));
		borderSelected = new Border(
				new BorderStroke(Main.selectColour, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));
		canvasWidth = new SimpleDoubleProperty();
		canvasWidth.set(template.getPaperWidth()* Constants.LAYOUTDIVIDER);
		canvasHeight = new SimpleDoubleProperty(100.0);
		pageHeight = new SimpleDoubleProperty(100.0);
	}
	
	public void setReportBanner( ReportTemplate template,
			BannerSelectModel<ReportBanner> bannerSelectionModel,
			FieldSelectModel<ReportLayout> fieldSelectionModel, LayoutPaneController layoutController) {
		this.layoutController = layoutController;
		this.bannerSelectModel = bannerSelectionModel;
		this.fieldSelectModel = fieldSelectionModel;
		border = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
				new BorderWidths(1)));
		borderSelected = new Border(
				new BorderStroke(Main.selectColour, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));
		if (fields==null)
			fields = new ArrayList<ReportLayout>();

		this.template = template;
	}

	public void draw() {
		Main.rwDebugInst.debugThread("ReportBanner","draw",MRBDebug.DETAILED,"drawing  "+name);
		Main.rwDebugInst.debugThread("ReportBanner","draw",MRBDebug.DETAILED,"size  "+String.valueOf(canvasWidth.get())+"/"+String.valueOf(canvasHeight.get()));		
		getChildren().clear();
		if (background == null)
			background = new AnchorPane();
		else
			background.getChildren().clear();
		background.prefHeightProperty().bind(canvasHeight);
		background.minHeightProperty().bind(canvasHeight);
		background.maxHeightProperty().bind(canvasHeight);
		if (foreground == null)
			foreground = new AnchorPane();
		else
			foreground.getChildren().clear();
		foreground.prefHeightProperty().bind(canvasHeight);
		foreground.minHeightProperty().bind(canvasHeight);
		foreground.maxHeightProperty().bind(canvasHeight);
		foreground.prefWidthProperty().bind(canvasWidth);
		foreground.minWidthProperty().bind(canvasWidth);
		foreground.maxWidthProperty().bind(canvasWidth);
		focusedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean oldV, Boolean newV) -> {
			if (newV) {
				seperatorLine.setStroke(Main.selectColour);
			} else {
				seperatorLine.setStroke(Main.unSelectColour);
			}
		});
		if (nameWatermark == null)
			nameWatermark = new Text();
		if (bannerVisible)
			nameWatermark.setText(name);
		else
			nameWatermark.setText("INVISIBLE - "+name);
		nameWatermark.setX(canvasWidth.get() / 2);
		nameWatermark.setY(canvasHeight.get() / 2);
		nameWatermark.setFill(Main.unSelectColour);
		if (seperatorLine == null)
			seperatorLine = new Line();
		seperatorLine.setStartX(0);
		seperatorLine.setStartY(canvasHeight.get());
		seperatorLine.setEndX(canvasWidth.get());
		seperatorLine.setEndY(canvasHeight.get());
		seperatorLine.setStroke(Main.unSelectColour);
		if (moveLine == null)
			moveLine = new Line();
		moveLine.setStartX(0);
		moveLine.setStartY(canvasHeight.get());
		moveLine.setEndX(canvasWidth.get());
		moveLine.setEndY(canvasHeight.get());
		moveLine.setStroke(Main.unSelectColour);
		moveLine.setVisible(false);
		if (resizeButton == null)
			resizeButton = new Rectangle(7, 7);
		resizeButton.setLayoutX(canvasWidth.get() / 2);
		resizeButton.setLayoutY(canvasHeight.get() - 7);
		resizeButton.setStroke(Main.unSelectColour);
		resizeButton.setOnMouseClicked((t) -> {
			Main.rwDebugInst.debugThread("ReportBanner", "resize.click", MRBDebug.DETAILED,
					"resize button clicked");
		});
		resizeButton.setOnDragDetected((t) -> {
			Main.rwDebugInst.debugThread("ReportBanner", "resize.drag", MRBDebug.DETAILED,
					"resize button drag detected");
			if (!selected.get()) {
				t.consume();
				return;
			}
			Dragboard db = thisObj.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString(Constants.RESIZEBANNER);
			db.setContent(content);
			LayoutPane.bannerBeingResized = thisObj;
			layoutController.setNextBanner(thisObj);
			t.consume();
		});
		background.getChildren().addAll(nameWatermark, seperatorLine,moveLine);
		foreground.getChildren().add(resizeButton);
		getChildren().addAll(background, foreground);
		foreground.toFront();
		prefHeightProperty().bind(canvasHeight);
		Main.rwDebugInst.debugThread("ReportBanner", "draw", MRBDebug.DETAILED,
				"height " + canvasHeight.get() + " y " + getLayoutY());
		setOnDragOver((t) -> {
			Main.rwDebugInst.debugThread("ReportBanner", "dragover", MRBDebug.DETAILED, "drag over detected");
			ReportField fld = null;
			Dragboard db = t.getDragboard();
			String content = db.hasString() ? db.getString() : "";
			switch (content) {
			case Constants.MOVEFIELD:
				if (LayoutPane.fieldBeingMoved == null) {
					t.consume();
					return;
				}
				if(!LayoutPane.fieldBeingMoved.withinLimits(t.getX(),t.getY())) {
					t.consume();
					return;
				}
				t.acceptTransferModes(TransferMode.MOVE);
				break;
			case Constants.PLACEFIELD:
				for (ReportLayout layout : fields) {
					if (layout.getField().equals(LayoutPane.itemBeingDragged))
						return;
				}
				fld = LayoutPane.itemBeingDragged;
				if (!withinLimits(t.getX(),t.getY(),fld.getFieldWidth(),fld.getFieldHeight())) {
					t.consume();
					return;
				}
				t.acceptTransferModes(TransferMode.COPY);
				break;
			case Constants.RESIZEBANNER:
				if (LayoutPane.bannerBeingResized != thisObj &&
					LayoutPane.nextBannerBeingResized!=thisObj &&
					LayoutPane.nextBannerBeingResized!=null) {
					t.consume();
					return;
				}
				if (LayoutPane.bannerBeingResized == thisObj &&
					t.getY()<=LayoutPane.bannerBeingResized.findLastField()) {
					t.consume();
					return;
				}
				t.acceptTransferModes(TransferMode.MOVE);
				moveLine.setStartY(t.getY());
				moveLine.setEndY(t.getY());
				moveLine.setVisible(true);
				break;
			default:
				return;
			}
			t.consume();
		});
		setOnDragExited((t)->{
			Dragboard db = t.getDragboard();
			String content = db.hasString() ? db.getString() : "";
			if (!content.equals(Constants.RESIZEBANNER)) {
				t.consume();
				return;
			}
			moveLine.setStartY(seperatorLine.getLayoutY());
			moveLine.setEndY(seperatorLine.getLayoutY());
		});
		setOnDragDropped((t) -> {
			Main.rwDebugInst.debugThread("ReportBanner", "dragdrop", MRBDebug.DETAILED, "drag drop detected");
			ReportLayout layout = null;
			Dragboard db = t.getDragboard();
			String content = db.hasString() ? db.getString() : "";
			ReportField fldDropped = null;
			switch (content) {
			case Constants.MOVEFIELD:
				UndoRecord undoRec=null;
				String oldX = "";
				String oldY = "";
				layout = LayoutPane.fieldBeingMoved;
					if (!undoManager.getUndoUnderway()) {
					oldX=String.valueOf(layout.getX());
					oldY=String.valueOf(layout.getY());
					undoRec=undoManager.newRecord();
				}

				double xdiff = t.getX()-layout.getX();
				double ydiff = t.getY()-layout.getY();
				layout.move(t.getX(), t.getY());
				template.setDirty(true);
				ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems();
				if (selFields.size()>1) {
					for (ReportLayout field :selFields) {
						if (field != layout) {
							double newX = field.getX()+xdiff;
							if (newX < template.getLeftMargin() * Constants.LAYOUTDIVIDER * 10.0)
								xdiff = -field.getX();
							double rightMargin = (template.getPaperWidth() - template.getRightMargin() * 10) * Constants.LAYOUTDIVIDER;
							if ((newX + field.getFieldWidth()) > rightMargin)
								xdiff = rightMargin - field.getFieldWidth()- field.getX();
							double newY = field.getY()+ydiff;	
							double newYDiff=ydiff;
							if (field.getBanner()==thisObj) {
								if (newY < 0)
									newYDiff = -field.getY();
								if (newY + field.getFieldHeight() > canvasHeight.get())
									newYDiff = canvasHeight.get()-field.getFieldHeight()-field.getY();
							}
							else {
								ReportBanner otherBanner = field.getBanner();
								double otherHeight = otherBanner.getCanvasHeight();
								if (newY < 0)
									newYDiff = -field.getY();
								if (newY + field.getFieldHeight() > otherHeight)
									newYDiff = otherHeight-field.getFieldHeight()-field.getY();
							}
							field.move(field.getX()+xdiff, field.getY()+newYDiff);
						}
					}
				}
				t.setDropCompleted(true);
				t.consume();
				if (checkOverlap())
					layoutController.setError(Constants.ERRFIELDSOVERLAP);
				if (!undoManager.getUndoUnderway()) {
					undoRec.newAction(layout,DetailType.MOVELAYOUT, oldX,String.valueOf(t.getX()),XmlWriter.LAYOUTX);
					undoRec.newAction(layout,DetailType.MOVELAYOUT, oldY,String.valueOf(t.getY()),XmlWriter.LAYOUTY);
					undoManager.addRecord(undoRec);
				}

				redrawFields();
				return;
			case Constants.RESIZEBANNER:
				if (LayoutPane.bannerBeingResized==thisObj) {
					setCanvasHeight(t.getY());
					moveLine.setVisible(false);
				}
				else {
					double oldHeight = LayoutPane.bannerBeingResized.getCanvasHeight();
					LayoutPane.bannerBeingResized.setCanvasHeight(oldHeight+t.getY());
					LayoutPane.bannerBeingResized.setMoveLineVisible(false);
				}
				Platform.runLater(()->{
					layoutController.resizeBanners();
				});
				t.setDropCompleted(true);
				template.setDirty(true);
				t.consume();
				return;
			}
			fldDropped = LayoutPane.itemBeingDragged;
			layout = new ReportLayout(template, thisObj, fldDropped, t.getX(), t.getY());
			layout.setX(t.getX());
			layout.setY(t.getY());
			if (checkOverlap())
				layoutController.setError(Constants.ERRFIELDSOVERLAP);
			addField(layout);
			layout.draw();
			template.setDirty(true);
			t.setDropCompleted(true);
			layoutController.selectField(layout);
			t.consume();
		});
		setOnMouseClicked((t) -> {
			Main.rwDebugInst.debugThread("ReportBanner", "mouseclicked", MRBDebug.DETAILED, "mouse click detected in "+name);
			double x = t.getX();
			double y = t.getY();
			for (ReportLayout item : fields) {
				if (x >= item.getX() && x < item.getX() + item.getFieldWidth() && y >= item.getY()
						&& y < item.getY() + item.getFieldHeight()) {
					if (item.isSelected())
						layoutController.deselectTreeItem(item.getTreeItem());
					else
						layoutController.selectTreeItem(item.getTreeItem());
					return;
				}
			}
			if (thisObj.isSelected())
				layoutController.deselectTreeItem(thisObj.getTreeItem());
			else
				layoutController.selectTreeItem(thisObj.getTreeItem());
		});
		ruler = new VerticalRuler(40,canvasHeight.get()/(Constants.LAYOUTDIVIDER));
		background.getChildren().add(ruler);
		ruler.setLayoutX(0.0);
		ruler.setLayoutY(0.0);
		ruler.drawRuler();
		ruler.setVisible(false);
		ReadOnlyObjectProperty<Bounds> backbounds= background.boundsInLocalProperty();
		Main.rwDebugInst.debugThread("ReportBanner","draw",MRBDebug.DETAILED,"background  "+String.valueOf(background.getWidth())+"/"+String.valueOf(background.getHeight())+ " position "+backbounds.toString());		
		ReadOnlyObjectProperty<Bounds>forebounds= foreground.boundsInLocalProperty();
		Main.rwDebugInst.debugThread("ReportBanner","draw",MRBDebug.DETAILED,"foreground  "+String.valueOf(foreground.getWidth())+"/"+String.valueOf(foreground.getHeight())+ " position "+forebounds.toString());		
		ReadOnlyObjectProperty<Bounds>rulebounds= ruler.boundsInLocalProperty();
		Main.rwDebugInst.debugThread("ReportBanner","draw",MRBDebug.DETAILED,"ruler  "+String.valueOf(ruler.getWidth())+"/"+String.valueOf(ruler.getHeight())+ " position "+rulebounds.toString());		
	}
	public void addField(ReportLayout field) {
		fields.add(field);
		if (foreground==null)
			foreground = new AnchorPane();
		foreground.getChildren().add(field);
		layoutController.addField(field);
		
	}
	public BooleanProperty selectedProperty() {
		return selected;
	}
	
	public Boolean getSelected() {
		return selected.get();
	}


	public String getSelectedText() {
		return selectedText.get();
	}
	public void setSelectedText(String selectedText) {
		this.selectedText.set(selectedText);
	}
	public StringProperty selectedTextProperty() {
		return selectedText;
	}
	public void drawFields() {
		Main.rwDebugInst.debugThread("ReportBanner","drawFields",MRBDebug.DETAILED,"drawing fields  "+name);
		for (ReportLayout field : fields) {
			field.setUpField();
			if (!foreground.getChildren().contains(field))
				foreground.getChildren().add(field);
			field.draw();
		}
	}
	public void moveBanner(double  x,double y) {
		relocate(x,y);
		ruler.relocate(0.0,0.0);
		ReadOnlyObjectProperty<Bounds> backbounds= background.boundsInLocalProperty();
		Main.rwDebugInst.debugThread("ReportBanner","moveBanner",MRBDebug.DETAILED,"background  "+String.valueOf(background.getWidth())+"/"+String.valueOf(background.getHeight())+ " position "+backbounds.toString());		
		ReadOnlyObjectProperty<Bounds>forebounds= foreground.boundsInLocalProperty();
		Main.rwDebugInst.debugThread("ReportBanner","moveBanner",MRBDebug.DETAILED,"foreground  "+String.valueOf(foreground.getWidth())+"/"+String.valueOf(foreground.getHeight())+ " position "+forebounds.toString());		
		ReadOnlyObjectProperty<Bounds>rulebounds= ruler.boundsInLocalProperty();
		Main.rwDebugInst.debugThread("ReportBanner","moveBanner",MRBDebug.DETAILED,"ruler  "+String.valueOf(ruler.getWidth())+"/"+String.valueOf(ruler.getHeight())+ " position "+rulebounds.toString());		
	}
	public void resetStyle() {
		for (ReportLayout field : fields) {
			field.draw();
		}
		
	}
	public boolean checkOverlap() {
		boolean result = false;
		layoutController.setError("");
		List<ReportLayout>tempFields = new ArrayList<ReportLayout>(fields);
		for (ReportLayout field:fields) {
			field.setInError(false);
		}
		for (ReportLayout field:fields) {
			double thisX=field.getX();
			double thisX2=thisX+field.getFieldWidth();
			double thisY=field.getY();
			double thisY2=thisY+field.getFieldHeight();
			for (ReportLayout prevField:tempFields) {
				if (prevField == field)
					continue;
				double prevX=prevField.getX();
				double prevX2=prevX+prevField.getFieldWidth();
				double prevY=prevField.getY();
				double prevY2=prevY+prevField.getFieldHeight();
				if (thisX >= prevX && thisX <= prevX2 && thisY >= prevY && thisY <= prevY2) {
					field.setInError(true);
					prevField.setInError(true);
					result = true;
					continue;
				}
				if (thisX2 >= prevX && thisX2 <= prevX2 && thisY >= prevY && thisY <= prevY2) {
					field.setInError(true);
					prevField.setInError(true);
					result = true;
					continue;
				}
				if (thisX >= prevX && thisX <= prevX2 && thisY2 >= prevY && thisY2 <= prevY2) {
					field.setInError(true);
					prevField.setInError(true);
					result = true;
					continue;
				}
				if (thisX2 >= prevX && thisX2 <= prevX2 && thisY2 >= prevY && thisY2 <= prevY2) {
					field.setInError(true);
					prevField.setInError(true);
					result = true;
					continue;
				}
			}
		}
		return result;
	}
	public double findLastField() {
		double lastY=0.0;
		for (ReportLayout field : fields) {
			if ((field.getY()+field.getHeight())> lastY)
				lastY = field.getY()+field.getHeight();
		}
		return lastY;
	}
	public BannerSelectModel<ReportBanner> getBannerSelectModel() {
		return bannerSelectModel;
	}
	public BannerType getBannerType() {
		return bannerType;
	}
	public double getCanvasHeight() {
		return canvasHeight.get();
	}


	public double getCanvasWidth() {
		return canvasWidth.get();
	}
	public List<ReportLayout> getFields() {
		return fields;
	}
	public FieldSelectModel<ReportLayout> getFieldSelectModel() {
		return fieldSelectModel;
	}
	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}

	public int getOtherPos() {
		return otherPos;
	}
	public ReportTemplate getTemplate() {
		return template;
	}
	public boolean isBottomLine() {
		return bottomLine;
	}

	public boolean isEmpty() {
		return fields.isEmpty();
	}
	public boolean isInError() {
		if (fields !=null) {
			for (ReportLayout layout : fields) {
				if ( layout.isInError())
					return true;
			}
		}
		return false;
	}

	public boolean isNewPage() {
		return newPage;
	}
	
	public boolean isRepeat() {
		return repeat;
	}
	public boolean isSelected() {
		return selected.get();
	}

	public boolean isTopLine() {
		return topLine;
	}
	

	public boolean getHasFooter() {
		return hasFooter;
	}
	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}
	public void loadXML(XMLEventReader xmlEventReader, XMLEvent xmlEvent, ReportTemplate template)
			throws XMLStreamException {
		boolean done = false;
		while (!done) {
			try {
				if (xmlEvent.isStartElement()) {
					StartElement startElement = xmlEvent.asStartElement();
					switch (startElement.getName().getLocalPart()) {
					case XmlWriter.NAME:
						xmlEvent = xmlEventReader.nextEvent();
						name = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.BANNERHEIGHT:
						xmlEvent = xmlEventReader.nextEvent();
						pageHeight.set(Double.parseDouble(xmlEvent.asCharacters().getData()));
						canvasHeight.set(pageHeight.get());
						break;
					case XmlWriter.BANNERTYPE:
						xmlEvent = xmlEventReader.nextEvent();
						bannerType = Constants.BannerType.valueOf(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.POSITION:
						xmlEvent = xmlEventReader.nextEvent();
						position = Integer.parseInt(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.BANNEROTHERPOS:
						xmlEvent = xmlEventReader.nextEvent();
						otherPos = Integer.parseInt(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.BANNERNEWPAGE:
						xmlEvent = xmlEventReader.nextEvent();
						newPage = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true : false;
						break;
					case XmlWriter.BANNERREPEAT:
						xmlEvent = xmlEventReader.nextEvent();
						repeat = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true : false;
						break;
					case XmlWriter.BANNERTOPLINE:
						xmlEvent = xmlEventReader.nextEvent();
						topLine = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true : false;
						break;
					case XmlWriter.BANNERBOTTOMLINE:
						xmlEvent = xmlEventReader.nextEvent();
						bottomLine = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true: false;
						break;
					case XmlWriter.BANNERHASFOOTER:
						xmlEvent = xmlEventReader.nextEvent();
						hasFooter = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true: false;
						break;
					case XmlWriter.BANNERVISIBLE:
						xmlEvent = xmlEventReader.nextEvent();
						bannerVisible = xmlEvent.asCharacters().getData().equalsIgnoreCase("true") ? true: false;
						break;
						
					case XmlWriter.BANNERSTYLE:
						xmlEvent = xmlEventReader.nextEvent();
						String styleName =xmlEvent.asCharacters().getData();
						SortedMap<String,ReportStyle> styles = template.getStyles();
						if (styles.containsKey(styleName))
							bannerStyle = styles.get(styleName);
						else {
							bannerStyle = null;
							Main.rwDebugInst.debugThread("ReportBanner", "loadXML", MRBDebug.DETAILED,"banner style not found "+styleName);
						}
						break;
					case XmlWriter.LAYOUTFIELDS:
						fields = new ArrayList<ReportLayout>();
						break;
					case XmlWriter.LAYOUTFIELD:
						if (fields == null)
							fields = new ArrayList<ReportLayout>();
						ReportLayout layout = new ReportLayout();
						layout.setBanner(this);
						layout.loadXML(xmlEventReader, xmlEvent, template);
						fields.add(layout);
						break;
					case XmlWriter.BANNERGROUPFIELD:
						SortedMap<String, ReportField> selFields = template.getSelectedFields();
						xmlEvent = xmlEventReader.nextEvent();
						String groupFieldKey =xmlEvent.asCharacters().getData();
						groupField = selFields.get(groupFieldKey);
					}
				}
				if (xmlEvent.isEndElement()) {
					EndElement endElement = xmlEvent.asEndElement();
					if (endElement.getName().getLocalPart().equals(XmlWriter.BANNER))
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

	public void redrawFields() {
		Main.rwDebugInst.debugThread("ReportBanner","redrawFields",MRBDebug.DETAILED,"redrawing fields "+name);
		for (ReportLayout field : fields) {
				field.draw();
		}
	}
	public void removeFields(ObservableList<ReportLayout> selFields) {
		Main.rwDebugInst.debugThread("ReportBanner","removeFields",MRBDebug.DETAILED,"removing fields from  "+name);
		for (ReportLayout field : selFields) {
			foreground.getChildren().remove(field);
			fields.remove(field);
			fieldSelectModel.removeField(field);
		}
	}
	
	public void reset(double paperWidth) {
		Main.rwDebugInst.debugThread("ReportBanner","reset",MRBDebug.DETAILED,"resetting  "+name);
		if (nameWatermark == null)
			return;
		if (paperWidth == 0.0)
			return;
		canvasWidth.set(paperWidth);
		nameWatermark.setX(canvasWidth.get() / 2);
		nameWatermark.setY(canvasHeight.get() / 2);
		seperatorLine.setStartY(canvasHeight.get());
		seperatorLine.setEndY(canvasHeight.get());
		seperatorLine.setEndX(canvasWidth.get() - 2.0);
		foreground.toFront();
		moveLine.setStartX(0);
		moveLine.setStartY(canvasHeight.get());
		moveLine.setEndX(canvasWidth.get());
		moveLine.setEndY(canvasHeight.get());
		resizeButton.setLayoutY(canvasHeight.get() - 7);
		ruler.setHeight(canvasHeight.get());
		ruler.drawRuler();
	}

	public void setSelected(Boolean selection) {
		selected.set(selection);
		selectedText.set(selection.toString());
		if (selection) {
			foreground.setBorder(borderSelected);
			resizeButton.setStroke(Main.selectColour);
			resizeButton.setFill(Main.selectColour);
			resizeButton.toFront();
			ruler.setVisible(true);
			checkOverlap();
		} else {
			foreground.setBorder(border);
			resizeButton.setStroke(Main.unSelectColour);
			resizeButton.setFill(Main.unSelectColour);
			ruler.setVisible(false);
		}
		redrawFields();
		foreground.toFront();
	}

	public void setBannerSelectModel(BannerSelectModel<ReportBanner> bannerSelectModel) {
		this.bannerSelectModel = bannerSelectModel;
	}

	public void setBannerType(BannerType bannerType) {
		this.bannerType = bannerType;
	}
	public void setBottomLine(boolean bottomLine) {
		this.bottomLine = bottomLine;
		template.setDirty(true);
	}

	public void setCanvasHeight(double canvasHeight) {
		if (nameWatermark == null)
			return;
		Main.rwDebugInst.debugThread("ReportBanner", "setCanvasHeight", MRBDebug.DETAILED, "canvasHeight changed "+canvasHeight);
		this.canvasHeight.set(canvasHeight);
		pageHeight.set(this.canvasHeight.get()/(Constants.LAYOUTDIVIDER));
		Main.rwDebugInst.debugThread("ReportBanner", "setCanvasHeight", MRBDebug.DETAILED, "pageHeight changed "+pageHeight.get());
		reset(canvasWidth.get());
		ruler.setHeight(this.canvasHeight.get());
		ruler.drawRuler();
		template.setDirty(true);
//		layoutController.selectBanner(thisObj);
	}

	public void setCanvasWidth(double canvasWidth) {
		this.canvasWidth.set(canvasWidth);
	}

	public void setController(LayoutPaneController controller) {
		this.layoutController = controller;
	}

	public void setFields(List<ReportLayout> fields) {
		this.fields = fields;
	}

	public void setFieldSelectModel(FieldSelectModel<ReportLayout> fieldSelectModel) {
		this.fieldSelectModel = fieldSelectModel;
	}

	public void setMoveLineVisible(boolean visible) {
		moveLine.setVisible(visible);
	}

	public void setMoveLineY(double newY) {
		moveLine.setStartY(newY);
		moveLine.setEndY(newY);
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setNewPage(boolean newPage) {
		this.newPage = newPage;
		template.setDirty(true);
	}
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
		template.setDirty(true);
	}public void setPosition(int position) {
		this.position = position;
	}
	
	public void setOtherPos(int otherPos) {
		this.otherPos = otherPos;
	}
	public void setTopLine(boolean topLine) {
		this.topLine = topLine;
		template.setDirty(true);
	}
	
	public boolean isBannerVisible() {
		return bannerVisible;
	}
	public void setBannerVisible(boolean bannerVisible) {
		this.bannerVisible = bannerVisible;
		if (bannerVisible)
			nameWatermark.setText(name);
		else
			nameWatermark.setText("INVISIBLE - "+name);
		template.setDirty(true);
	}
	public ReportField getGroupField() {
		return groupField;
	}
	public void setGroupField(ReportField groupField) {
		this.groupField = groupField;
	}
	public ReportStyle getBannerStyle() {
		return bannerStyle;
	}
	public void setBannerStyle(ReportStyle bannerStyle) {
		this.bannerStyle = bannerStyle;
		template.setDirty(true);
	}
	
	public TreeItem<LayoutTreeNode> getTreeItem() {
		return treeItem;
	}
	public void setTreeItem(TreeItem<LayoutTreeNode> treeItem) {
		this.treeItem = treeItem;
	}
	public boolean withinLimits(double x, double y, double width, double height) {
		Main.rwDebugInst.debugThread("ReportBanner","withinLimits", MRBDebug.DETAILED, " x/y "+ x + " / "+y);
		if (x<template.getLeftMargin()*Constants.LAYOUTDIVIDER*10.0)
			return false;
		double rightMargin =(template.getPaperWidth()-template.getRightMargin()*10)*Constants.LAYOUTDIVIDER;
		Main.rwDebugInst.debugThread("ReportLayout","withinLimits", MRBDebug.DETAILED, " paper width "+template.getPaperWidth()+" margin " + template.getRightMargin()+" rightMargin "+ rightMargin);
		if ((x+width)>rightMargin)
			return false;
		if (y<0)
			return false;
		if(y+height>canvasHeight.get())
			return false;
		return true;
	}

	public void writeXML(IndentXML writer) throws XMLStreamException {
		try {
			writer.writeDataElement(XmlWriter.NAME, name);
			writer.writeDataElement(XmlWriter.BANNERHEIGHT, String.valueOf(canvasHeight.get()));
			writer.writeDataElement(XmlWriter.BANNERTYPE, bannerType.name());
			writer.writeDataElement(XmlWriter.POSITION, String.valueOf(position));
			writer.writeDataElement(XmlWriter.BANNEROTHERPOS, String.valueOf(otherPos));
			writer.writeDataElement(XmlWriter.BANNERNEWPAGE, newPage ? "true" : "false");
			writer.writeDataElement(XmlWriter.BANNERREPEAT, repeat ? "true" : "false");
			writer.writeDataElement(XmlWriter.BANNERTOPLINE, topLine ? "true" : "false");
			writer.writeDataElement(XmlWriter.BANNERBOTTOMLINE, bottomLine ? "true" : "false");
			writer.writeDataElement(XmlWriter.BANNERHASFOOTER, hasFooter ? "true" : "false");
			writer.writeDataElement(XmlWriter.BANNERVISIBLE,bannerVisible ? "true" : "false");
			if (bannerStyle!= null)
				writer.writeDataElement(XmlWriter.BANNERSTYLE,bannerStyle.getName());
			if (groupField !=null)
				writer.writeDataElement(XmlWriter.BANNERGROUPFIELD, groupField.getKey());
			writer.writeStartElement(XmlWriter.LAYOUTFIELDS);
			writer.writeNewLine();
			if (fields != null)
				for (ReportLayout field : fields) {
					writer.writeStartElement(XmlWriter.LAYOUTFIELD);
					writer.writeAttribute(XmlWriter.FIELDKEY, field.getName());
					writer.writeNewLine();
					field.writeXML(writer);
					writer.writeIndentedEndElement();
				}
			writer.writeIndentedEndElement(); // layout fields
		} catch (XMLStreamException e) {
			throw e;
		}
	}
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("ReportBanner", "undo", MRBDebug.DETAILED, "undo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.BANNERNEWPAGE:
				newPage=Boolean.valueOf(detailRec.getBeforeValue());
				break;
			/*
			 *  BANNER is used to delete group field for group headers
			 */
			case XmlWriter.BANNER:
				DetailType type = DetailType.findType(detailRec.getAction());
				switch (type) {
				case ADDLAYOUT:
					ReportLayout found=null;
					for (ReportLayout layout:fields) {
						if (layout.getName().equals(detailRec.getAfterValue()))
							found=layout;
					}
					if (found != null) {
						if (found.getField()==groupField)
							groupField = null;
						if (foreground != null)
							foreground.getChildren().remove(found);
						fieldSelectModel.removeField(found);
						fields.remove(found);		
						layoutController.removeLayoutNode(thisObj, found);
					}
					break;
				}
			}
		}
		
	}
	@Override
	public void redo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("ReportBanner", "redo", MRBDebug.DETAILED, "redo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.BANNERNEWPAGE:
				newPage=Boolean.valueOf(detailRec.getAfterValue());
				break;
			}
		}	
		
	}
}
