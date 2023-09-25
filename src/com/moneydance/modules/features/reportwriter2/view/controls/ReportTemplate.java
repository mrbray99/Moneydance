package com.moneydance.modules.features.reportwriter2.view.controls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.Utilities;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.Constants.BannerType;
import com.moneydance.modules.features.reportwriter2.Constants.DatasetType;
import com.moneydance.modules.features.reportwriter2.IndentXML;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;

import javafx.print.PageOrientation;
import javafx.print.Paper;
public class ReportTemplate implements XmlWriter {
	private String name;
private boolean question1=false;
	private boolean question2=false;
	private boolean question3=false;
	private boolean question4=false;
	private DatasetType dataset;
	private String datasetSQL=null;
	private List<ReportBanner> banners=null;
	private SortedMap<String,ReportField> availableFields=null;
	private SortedMap<String,ReportField>labels=null;
	private SortedMap<String,ReportField>variables=null;
	private SortedMap<String,ReportField> selectedFields=null;
	private SortedMap<String,ReportStyle> styles=null;
	private SortedMap<String, ReportStyle> defaultStyles=null;
	private List<ReportField>sortedVariables;
	private Parameters params;
	private Paper paperSize; // class
	private PageOrientation orientation;
	private ReportStyle defaultStyle=null;
	private ReportStyle header1Style=null;
	private ReportStyle header2Style=null;
	private ReportStyle header3Style=null;
	private double leftMargin=0.0; // metric
	private double rightMargin=0.0; // metric 
	private double topMargin=0.0;// metric
	private double bottomMargin=0.0; // metric
	private boolean dirty=false;
	public ReportTemplate(Parameters params) {
		this.params = params;
		paperSize = Paper.A4;
		orientation = PageOrientation.LANDSCAPE;
		availableFields = new TreeMap<String,ReportField>();
		labels = new TreeMap<String,ReportField>();
		variables = new TreeMap<String, ReportField>();
		selectedFields = new TreeMap<String,ReportField>();
		styles = new TreeMap<String,ReportStyle>();
		loadDefaultStyles();
}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public DatasetType getDataset() {
		return dataset;
	}

	public void setDataset(DatasetType dataset) {
		this.dataset = dataset;
	}

	public String getDatasetSQL() {
		Boolean orderByFound=false;
		String orderFld1 = "";
		String orderFld2="";
		String orderFld3="";
		for (ReportBanner banner:banners) {
			if (banner.getBannerType() == BannerType.GROUPHEAD) {
				if (banner.getPosition() == BannerType.GROUPHEAD.getPosition()) {
					orderByFound=true;
					orderFld1 = banner.getGroupField().getKey();
				}
				if (banner.getPosition() == BannerType.GROUPHEAD.getPosition()+1) {
					orderByFound=true;
					orderFld2 = banner.getGroupField().getKey();
				}
				if (banner.getPosition() == BannerType.GROUPHEAD.getPosition()+2) {
					orderByFound=true;
					orderFld3 = banner.getGroupField().getKey();
				}
			}
		}
		StringBuilder orderSql = new StringBuilder();
		if (orderByFound) {
			orderSql.append(" ORDER BY ");
			if (!orderFld1.isEmpty())
				orderSql.append(orderFld1.substring(orderFld1.indexOf('.')+1));			
			if (!orderFld2.isEmpty()) {
				orderSql.append(",");
				orderSql.append(orderFld2.substring(orderFld2.indexOf('.')+1));
			}
			if (!orderFld3.isEmpty()) {
				orderSql.append(",");
				orderSql.append(orderFld3.substring(orderFld3.indexOf('.')+1));
			}
		}
		StringBuilder sql = new StringBuilder();;
		switch (dataset) {
		case ACCOUNT:
			sql.append("SELECT * FROM ACCOUNT");
			if (question1)
				sql.append(" UNION TRANSACTION where ACCOUNT.ACCOUNTID = TRANSACTION.ACCTID");
			break;
		case ADDRESS:
			break;
		case BUDGET:
			break;
		case CATEGORY:
			break;
		case CURRENCY:
			break;
		case INVESTMENT:
			break;
		case REMINDER:
			break;
		case SECURITY:
			break;
		case TRANSACTION:
			break;
		default:
			break;
		}
		if (orderByFound)
			sql.append(orderSql.toString().toUpperCase());
		sql.append(";");
		datasetSQL = sql.toString();
		return datasetSQL;
	}

	public void setDatasetSQL(String datasetSQL) {
		this.datasetSQL = datasetSQL;
	}

	public List<ReportBanner> getBanners() {
		if (banners == null)
			banners=new ArrayList<ReportBanner>();
		return banners;
	}
	public ReportBanner getBannerByPos(Integer pos ) {
		for (ReportBanner tmpBanner:banners)
			if(tmpBanner.getPosition()==pos)
				return tmpBanner;
		return null;
	}
	public void addBanner(ReportBanner banner) {
		if (banners == null)
			banners=new ArrayList<ReportBanner>();
		banners.add(banner);
	}
	public void removeBanner(ReportBanner banner) {
		banners.remove(banner);
	}
	public void setBanners(List<ReportBanner> banners) {
		this.banners = banners;
	}
	public SortedMap<String,ReportField> getAvailableFields() {
		if (availableFields==null) {
			availableFields = new TreeMap<String,ReportField>();
		}
		return availableFields;
	}
	public void setAvailableFields(SortedMap<String,ReportField> availableFields) {
		this.availableFields = availableFields;
	}
	
	public SortedMap<String, ReportField> getSelectedFields() {
		if (selectedFields==null) {
			selectedFields = new TreeMap<String,ReportField>();
		}
		return selectedFields;
	}

	public void setSelectedFields(SortedMap<String, ReportField> selectedFields) {
		this.selectedFields = selectedFields;
	}
	
	public SortedMap<String, ReportField> getLabels() {
		return labels;
	}

	public void setLabels(SortedMap<String, ReportField> labels) {
		this.labels = labels;
	}

	public void addLabel(ReportField field) {
		labels.put(field.getKey().toLowerCase(),field);
	}
	public void deleteLabel(ReportField field) {
		labels.remove(field.getKey());
	}

	public Paper getPaperSize() {
		if (paperSize == null)
			paperSize=Paper.A4;
		return paperSize;
	}

	public void setPaperSize(Paper paperSize) {
		this.paperSize = paperSize;
	}

	public PageOrientation getOrientation() {
		if (orientation == null)
			orientation = PageOrientation.LANDSCAPE;
		return orientation;
	}
	public void setOrientation(PageOrientation orientation) {
		this.orientation = orientation;
	}
	public double getPaperWidth() { // metric
		if (orientation == PageOrientation.LANDSCAPE)
			return Utilities.printerToScreen(paperSize.getHeight());
		else 
			return Utilities.printerToScreen(paperSize.getWidth());
	}
	public double getPaperHeight() { // metric
		if (orientation == PageOrientation.PORTRAIT)
			return Utilities.printerToScreen(paperSize.getHeight());
		else 
			return Utilities.printerToScreen(paperSize.getWidth());
	}

	public double getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(double leftMargin) {
		this.leftMargin = leftMargin;
	}

	public double getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(double rightMargin) {
		this.rightMargin = rightMargin;
	}

	public double getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(double topMargin) {
		this.topMargin = topMargin;
	}

	public double getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(double bottomMargin) {
		this.bottomMargin = bottomMargin;
	}
	
	public ReportStyle getDefaultStyle() {
		return defaultStyle;
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean isQuestion1() {
		return question1;
	}

	public void setQuestion1(boolean question1) {
		this.question1 = question1;
	}

	public boolean isQuestion2() {
		return question2;
	}

	public void setQuestion2(boolean question2) {
		this.question2 = question2;
	}

	public boolean isQuestion3() {
		return question3;
	}

	public void setQuestion3(boolean question3) {
		this.question3 = question3;
	}

	public boolean isQuestion4() {
		return question4;
	}

	public void setQuestion4(boolean question4) {
		this.question4 = question4;
	}


	public SortedMap<String, ReportStyle> getStyles() {
		return styles;
	}
	public void removeStyle(ReportStyle style) {
		styles.remove(style.getName());
	}

	public void setStyles(SortedMap<String, ReportStyle> styles) {
		this.styles = styles;
	}
	public ReportStyle findStyle(String name) {
		if (name==null)
			return null;
		if (!styles.containsKey(name))
			return defaultStyle;
		return  styles.get(name);
	}
	public void addStyle(ReportStyle style) {
		if (styles==null)
			styles = new TreeMap<String, ReportStyle>();
		styles.put(style.getName().toLowerCase(), style);
	}
	public SortedMap<String, ReportField> getVariables() {
		return variables;
	}

	public void setVariables(SortedMap<String, ReportField> variables) {
		this.variables = variables;
	}

	public void addVariable(ReportField field) {
		variables.put(field.getKey().toLowerCase(),field);
	}
	public void deleteVariable(ReportField field) {
		variables.remove(field.getKey());
	}

	public List<ReportField> getSortedVariables() {
		return sortedVariables;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	public boolean loadTemplate() {
		String fileName = params.getReportDirectory()+"\\"+name+Constants.TEMPLATEEXTENSION;
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));
			while (xmlEventReader.hasNext()) {
				XMLEvent xmlEvent = xmlEventReader.nextEvent();
				if (xmlEvent.isStartElement()) 
					loadXML(xmlEventReader,xmlEvent,this);
			}
		}
		catch(FileNotFoundException e) {
			return false;
		}
		catch (XMLStreamException e2) {
			e2.printStackTrace();
			OptionMessage.displayMessage("Template file "+fileName+" invalid "+e2.getMessage());
			return false;
			
		}
		catch (Exception e3)
		 {
			e3.printStackTrace();
			OptionMessage.displayMessage("Unknown error reading template file "+fileName+ " "+e3.getMessage());
			return false;
		}
		/*
		 * make template complete
		 */
		if (styles==null) 
			styles = new TreeMap<String, ReportStyle>();
		if (styles.get(Constants.DEFAULTSTYLENAME)==null) 
			styles.put(Constants.DEFAULTSTYLENAME,defaultStyle);
		if (styles.get(Constants.HEADER1STYLENAME)==null) 
			styles.put(Constants.HEADER1STYLENAME,header1Style);
		if (styles.get(Constants.HEADER2STYLENAME)==null) 
			styles.put(Constants.HEADER2STYLENAME,header2Style);
		if (styles.get(Constants.HEADER3STYLENAME)==null) 
			styles.put(Constants.HEADER3STYLENAME,header3Style);
		if (variables == null)
			variables= new TreeMap<>();
		if (labels==null)
			labels= new TreeMap<>();
		if (styles==null)
			styles= new TreeMap<>();
		return true;
	}

	public void saveTemplate() {
		String fileName = params.getReportDirectory()+"\\"+name+Constants.TEMPLATEEXTENSION;
		try {
			OutputStream stream = new FileOutputStream(new File(fileName));
			XMLOutputFactory output = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = output.createXMLStreamWriter(stream, "utf-8");
			IndentXML indentWriter = new IndentXML(writer);
			writeXML(indentWriter);
			writer.close();
			stream.close();
		}
		catch (IOException | XMLStreamException e) {
			e.printStackTrace();
			OptionMessage.displayMessage("Error writing Template file "+fileName+e.getMessage());
			
		}
		catch (Exception exc) {
			exc.printStackTrace();
			OptionMessage.displayMessage("Unknown Error writing Template file "+fileName+exc.getMessage());
		
		}

	}
	public boolean  saveAs(String newName) {
		String fileName = params.getReportDirectory()+"\\"+newName+Constants.TEMPLATEEXTENSION;
		File f= new File(fileName);
		if (f.exists()) {
			OptionMessage.displayErrorMessage("Template already exists");
			return false;
		}
		else {
			try {
				if (f.createNewFile()) {
					f.delete();
					name = newName;
					return true;
				}
				OptionMessage.displayErrorMessage("Invalid template name");
				return false;
			}
			catch (IOException e) {
				OptionMessage.displayErrorMessage("Invalid template name");
				return false;
			}
		}
	}
	public void loadXML(XMLEventReader xmlEventReader,XMLEvent xmlEvent,ReportTemplate template) throws XMLStreamException{
		StartElement startElement = xmlEvent.asStartElement();
		ReportField field;
		Attribute keyAttr ;
		switch (startElement.getName().getLocalPart()) {
		case XmlWriter.TEMPLATE:
			break;
		case XmlWriter.NAME:
			xmlEvent=xmlEventReader.nextEvent();
			name = xmlEvent.asCharacters().getData();
			break;
		case XmlWriter.QUESTION1:
			xmlEvent=xmlEventReader.nextEvent();
			question1 = xmlEvent.asCharacters().getData().equals("true")?true:false;
			break;
		case XmlWriter.QUESTION2:
			xmlEvent=xmlEventReader.nextEvent();
			question2 = xmlEvent.asCharacters().getData().equals("true")?true:false;
			break;
		case XmlWriter.QUESTION3:
			xmlEvent=xmlEventReader.nextEvent();
			question3 = xmlEvent.asCharacters().getData().equals("true")?true:false;
			break;
		case XmlWriter.QUESTION4:
			xmlEvent=xmlEventReader.nextEvent();
			question4 = xmlEvent.asCharacters().getData().equals("true")?true:false;
			break;
		case XmlWriter.DATASQL:
			xmlEvent=xmlEventReader.nextEvent();
			datasetSQL = xmlEvent.asCharacters().getData();
			break;
		case XmlWriter.DATASET:
			xmlEvent=xmlEventReader.nextEvent();
			dataset = DatasetType.valueOf(xmlEvent.asCharacters().getData());
			break;
		case XmlWriter.PAPERSIZE :
			xmlEvent=xmlEventReader.nextEvent();
			paperSize = Paper.A4;
			String paperSizeStr = xmlEvent.asCharacters().getData();
			for (int i=0;i<Constants.PaperSizes.length;i++) {
				if (paperSizeStr.equalsIgnoreCase(Constants.PaperSizes[i])) {
					paperSize = Constants.PaperSizesObjects[i];
					break;
				}
			}
			break;
		case XmlWriter.PAGELAYOUT:
			xmlEvent=xmlEventReader.nextEvent();
			orientation  = xmlEvent.asCharacters().getData().equals("LANDSCAPE")?PageOrientation.LANDSCAPE:PageOrientation.PORTRAIT;
			break;
		case XmlWriter.LEFTMARGIN:
			xmlEvent=xmlEventReader.nextEvent();
			leftMargin=Double.parseDouble(xmlEvent.asCharacters().getData());
			break;
		case XmlWriter.RIGHTMARGIN:
			xmlEvent=xmlEventReader.nextEvent();
			rightMargin=Double.parseDouble(xmlEvent.asCharacters().getData());
			break;
		case XmlWriter.TOPMARGIN:
			xmlEvent=xmlEventReader.nextEvent();
			topMargin=Double.parseDouble(xmlEvent.asCharacters().getData());
			break;
		case XmlWriter.BOTTOMMARGIN:
			xmlEvent=xmlEventReader.nextEvent();
			bottomMargin=Double.parseDouble(xmlEvent.asCharacters().getData());
			break;	
		case XmlWriter.BANNERS:
			banners = new ArrayList<>();
			break;
		case XmlWriter.BANNER:
			if(banners == null)
				banners = new ArrayList<>();
			ReportBanner banner = new ReportBanner(this);
			banner.loadXML(xmlEventReader, xmlEvent,this);
			banners.add(banner);
			break;
		case XmlWriter.SELECTEDFIELDS:
			selectedFields = new TreeMap<>();
			break;
		case XmlWriter.SELFIELD:
			if (selectedFields == null)
				selectedFields = new TreeMap<>();
			 field = new ReportField(this);
			keyAttr = startElement.getAttributeByName(new QName(XmlWriter.FIELDKEY));
			if (keyAttr != null) {
				String key = keyAttr.getValue();
				field.setKey(key);
			}
			else
				field.setKey("");
			field.loadXML(xmlEventReader, xmlEvent,this);
			selectedFields.put(field.getKey().toLowerCase(),field);
			break;
		case XmlWriter.STYLES:
			styles = new TreeMap<>();
			break;
		case XmlWriter.STYLE:
			if (styles==null)
				styles = new TreeMap<>();
			ReportStyle style = new ReportStyle();
			style.loadXML(xmlEventReader, xmlEvent,this);
			styles.put(style.getName(), style);
			if (style.isDefaultStyle() && style.getName().equals(Constants.DEFAULTSTYLENAME))
				defaultStyle = style;
			if (style.isDefaultStyle() && style.getName().equals(Constants.HEADER1STYLENAME))
				header1Style = style;
			if (style.isDefaultStyle() && style.getName().equals(Constants.HEADER2STYLENAME))
				header2Style = style;
			if (style.isDefaultStyle() && style.getName().equals(Constants.HEADER3STYLENAME))
				header3Style = style;
		break;
		case XmlWriter.LABELS:
			labels = new TreeMap<>();
			break;
		case XmlWriter.LABEL:
			if (labels == null)
				labels = new TreeMap<>();
			field = new ReportField(this);
			keyAttr = startElement.getAttributeByName(new QName(XmlWriter.FIELDKEY));
			if (keyAttr != null) {
				String key = keyAttr.getValue();
				field.setKey(key);
			}
			else
				field.setKey("");
			field.loadXML(xmlEventReader, xmlEvent,this);
			labels.put(field.getKey().toLowerCase(),field);
			break;
			
		case XmlWriter.VARIABLES:
			variables= new TreeMap<>();
			break;
		case XmlWriter.VARIABLE:
			if (variables == null)
				variables = new TreeMap<>();
			field = new ReportField(this);
			keyAttr = startElement.getAttributeByName(new QName(XmlWriter.FIELDKEY));
			if (keyAttr != null) {
				String key = keyAttr.getValue();
				field.setKey(key);
			}
			else
				field.setKey("");
			field.loadXML(xmlEventReader, xmlEvent,this);
			variables.put(field.getKey().toLowerCase(),field);
			break;
			
		}
	}
	public void writeXML(IndentXML writer) throws XMLStreamException  {
		try {
			writer.writeStartDocument();
			writer.writeStartElement(XmlWriter.TEMPLATE);
			writer.writeNewLine();
			writer.writeDataElement(XmlWriter.NAME,name);
			writer.writeDataElement(XmlWriter.QUESTION1,question1?"true":"false");
			writer.writeDataElement(XmlWriter.QUESTION2,question2?"true":"false");
			writer.writeDataElement(XmlWriter.QUESTION3,question3?"true":"false");
			writer.writeDataElement(XmlWriter.QUESTION4,question4?"true":"false");
			writer.writeDataElement(XmlWriter.DATASET,dataset.toString());
			writer.writeDataElement(XmlWriter.DATASQL,datasetSQL);
			writer.writeDataElement(XmlWriter.PAPERSIZE,paperSize.toString());
			writer.writeDataElement(XmlWriter.PAGELAYOUT,orientation.toString());
			writer.writeDataElement(XmlWriter.LEFTMARGIN,String.valueOf(leftMargin));
			writer.writeDataElement(XmlWriter.RIGHTMARGIN,String.valueOf(leftMargin));
			writer.writeDataElement(XmlWriter.TOPMARGIN,String.valueOf(leftMargin));
			writer.writeDataElement(XmlWriter.BOTTOMMARGIN,String.valueOf(leftMargin));
			if (styles != null && !styles.isEmpty()) {
				writer.writeStartElement(XmlWriter.STYLES);
				for (ReportStyle style:styles.values()) {
					writer.writeStartElement(XmlWriter.STYLE);
					writer.writeNewLine();
					style.writeXML(writer);
					writer.writeIndentedEndElement(); // style
				}
				writer.writeIndentedEndElement(); // styles
			}		
			writer.writeStartElement(XmlWriter.SELECTEDFIELDS);
			writer.writeNewLine();
			if (selectedFields != null)
				for (Entry<String,ReportField> entry:selectedFields.entrySet()) {
					ReportField field = entry.getValue();
					writer.writeStartElement(XmlWriter.SELFIELD);
					writer.writeAttribute(XmlWriter.FIELDKEY,entry.getKey());
					writer.writeNewLine();
					field.writeXML(writer);
					writer.writeIndentedEndElement();
				}
			writer.writeIndentedEndElement(); // selected fields
			writer.writeStartElement(XmlWriter.LABELS);
			writer.writeNewLine();
			if (labels != null)
				for (Entry<String,ReportField> entry:labels.entrySet()) {
					ReportField field = entry.getValue();
					writer.writeStartElement(XmlWriter.LABEL);
					writer.writeAttribute(XmlWriter.FIELDKEY,entry.getKey());
					writer.writeNewLine();
					field.writeXML(writer);
					writer.writeIndentedEndElement();// label
				}
			writer.writeIndentedEndElement(); // labels
			writer.writeStartElement(XmlWriter.VARIABLES);
			writer.writeNewLine();
			if (variables != null)
				for (Entry<String,ReportField> entry:variables.entrySet()) {
					ReportField field = entry.getValue();
					writer.writeStartElement(XmlWriter.VARIABLE);
					writer.writeAttribute(XmlWriter.FIELDKEY,entry.getKey());
					writer.writeNewLine();
					field.writeXML(writer);
					writer.writeIndentedEndElement();// variable
				}
			writer.writeIndentedEndElement(); // variables			
			writer.writeStartElement(XmlWriter.BANNERS);
			writer.writeNewLine();
			if (banners != null) {
				for (ReportBanner banner:banners) {
					writer.writeStartElement(XmlWriter.BANNER);
					writer.writeNewLine();
					banner.writeXML(writer);
					writer.writeIndentedEndElement(); // banner
				}
			}	
			writer.writeIndentedEndElement(); // banners
			writer.writeEndElement();  // template
			
		}
		catch (Exception e) {
			e.printStackTrace();
			throw  e;
		}
	}
	public void deleteTemplate() {
			Main.rwDebugInst.debugThread("ReportTemplate", "delete", MRBDebug.SUMMARY, "Delete "+name);
			String fileName = params.getReportDirectory()+"\\"+name+Constants.TEMPLATEEXTENSION;
			File file = new File(fileName);
			if (file.delete())
				Main.rwDebugInst.debugThread("ReportTemplate", "delete", MRBDebug.SUMMARY, "Deleted "+fileName);
			else
				Main.rwDebugInst.debugThread("ReportTemplate", "delete", MRBDebug.SUMMARY, "Delete failed "+fileName);	
	}
	private void loadDefaultStyles() {
		if (defaultStyles == null)
			defaultStyles = new TreeMap<String, ReportStyle>();
		defaultStyle = new ReportStyle();
		defaultStyle.setDefaultStyle(true);
		defaultStyle.setName(Constants.DEFAULTSTYLENAME);
		defaultStyle.setFontName(Main.reportFont);
		defaultStyle.setFontSize(Main.reportFontSize);		
		defaultStyles.put(Constants.DEFAULTSTYLENAME,defaultStyle);
		header1Style = new ReportStyle();
		header1Style.setDefaultStyle(true);
		header1Style.setName(Constants.HEADER1STYLENAME);
		header1Style.setFontName(Main.header1Font);
		header1Style.setFontSize(Main.header1FontSize);		
		defaultStyles.put(Constants.HEADER1STYLENAME,header1Style);
		header2Style = new ReportStyle();
		header2Style.setDefaultStyle(true);
		header2Style.setName(Constants.HEADER2STYLENAME);
		header2Style.setFontName(Main.header2Font);
		header2Style.setFontSize(Main.header2FontSize);		
		defaultStyles.put(Constants.HEADER2STYLENAME,header2Style);
		header3Style = new ReportStyle();
		header3Style.setDefaultStyle(true);
		header3Style.setName(Constants.HEADER3STYLENAME);
		header3Style.setFontName(Main.header3Font);
		header3Style.setFontSize(Main.header3FontSize);		
		defaultStyles.put(Constants.HEADER3STYLENAME,header3Style);

	}
	public void determineVariables() {
		sortedVariables = new ArrayList<ReportField>();
		if (variables.values().isEmpty()) 
			return;
		for (ReportField variable:variables.values()) {
			int depth=0;
			variable.setDepth(checkVariable(variable,depth));
			sortedVariables.add(variable);
		}
		Collections.sort(sortedVariables,new DepthComparator());
	}
	private int checkVariable(ReportField variable,int depth) {
		int newDepth;
		ExpressionProcessor exp = new ExpressionProcessor(this,variable);
		if (!exp.parseExpression(variable.getFieldExp()))
				newDepth = depth;
		else {
			int maxDepth=depth;
			for (ReportField used :exp.getUsedVariables()) {
				int fieldDepth = checkVariable(used,depth+1);
				if (fieldDepth >maxDepth)
					maxDepth = fieldDepth;
			}
			newDepth = maxDepth;
		}
		return newDepth;
	}
	class DepthComparator implements java.util.Comparator<ReportField>{
		@Override
		public int compare(ReportField a, ReportField b) {
			return a.getDepth()-b.getDepth();
		}
	}
}
