package com.moneydance.modules.features.reportwriter2.view.controls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.Constants.DatasetType;
import com.moneydance.modules.features.reportwriter2.Constants.ReportFieldType;
import com.moneydance.modules.features.reportwriter2.IndentXML;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.RWException;
import com.moneydance.modules.features.reportwriter2.Utilities;
import com.moneydance.modules.features.reportwriter2.databeans.AccountBean;
import com.moneydance.modules.features.reportwriter2.databeans.AddressBean;
import com.moneydance.modules.features.reportwriter2.databeans.DataBean;
import com.moneydance.modules.features.reportwriter2.databeans.InvTranBean;
import com.moneydance.modules.features.reportwriter2.databeans.ReminderBean;
import com.moneydance.modules.features.reportwriter2.databeans.SecurityBean;
import com.moneydance.modules.features.reportwriter2.databeans.TransactionBean;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.report.FieldValue;
import com.moneydance.modules.features.reportwriter2.report.GroupValues;

import javafx.scene.control.TreeItem;

import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.BEANFIELDTYPE;
import com.moneydance.modules.features.reportwriter2.databeans.BudgetBean;
import com.moneydance.modules.features.reportwriter2.databeans.CategoryBean;
import com.moneydance.modules.features.reportwriter2.databeans.CurrencyBean;


public class ReportField  implements XmlWriter, UndoAction{
	private String key;
	private String name;
	private DataBean fieldBean;
	private ReportFieldType reportType;
	private BEANFIELDTYPE fieldType;
	private double fieldWidth;
	private double fieldHeight;	
	private boolean selected;
	private ReportStyle layoutStyle;
	private ReportTemplate template;
	private FieldLabel label;
	private String fieldText=null;
	private String fieldPattern=null;
	private String fieldExp=null;
	private Integer depth;
	private Integer outputType=0;
	private boolean groupSelected;
	private TreeItem<LayoutTreeNode> treeItem;
	public ReportField(ReportTemplate template) {
		this.template = template;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataBean getFieldBean() {
		return fieldBean;
	}
	public void setFieldBean(DataBean fieldBean) {
		this.fieldBean = fieldBean;
	}
	public ReportFieldType getReportType() {
		return reportType;
	}
	public void setReportType(ReportFieldType reportType) {
		this.reportType = reportType;
	}
	
	public BEANFIELDTYPE getFieldType() {
		return fieldType;
	}
	public void setFieldType(BEANFIELDTYPE fieldType) {
		this.fieldType = fieldType;
	}
	public double getFieldWidth() {
		return fieldWidth;
	}
	public double getFieldHeight() {
		return fieldHeight;
	}
	
	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Integer getOutputType() {
		return outputType;
	}

	public void setOutputType(Integer outputType) {
		this.outputType = outputType;
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public void setFieldWidth(double fieldWidth) {
		this.fieldWidth = fieldWidth;
	}
	public void setFieldHeight(double fieldHeight) {
		this.fieldHeight = fieldHeight;
	}
	
	public String getFieldText() {
		return fieldText;
	}

	public void setFieldText(String fieldText) {
		this.fieldText = fieldText;
	}

	public void resetStyle() {
		ReportStyle textStyle=null;
		if ( layoutStyle != null) {
			textStyle = layoutStyle;
			textStyle.getDisplayText(label,fieldText);
		}
	}
	public ReportStyle getLayoutStyle() {
		return layoutStyle;
	}

	public void setLayoutStyle(ReportStyle layoutStyle) {
		this.layoutStyle = layoutStyle;
	}
	

	public String getFieldPattern() {
		return fieldPattern;
	}

	public void setFieldPattern(String fieldPattern) {
		this.fieldPattern = fieldPattern;
	}

	public String getFieldExp() {
		return fieldExp;
	}

	public void setFieldExp(String fieldExp) {
		this.fieldExp = fieldExp;
	}

	public boolean isGroupSelected() {
		return groupSelected;
	}

	public void setGroupSelected(boolean groupSelected) {
		this.groupSelected = groupSelected;
	}

	public TreeItem<LayoutTreeNode> getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(TreeItem<LayoutTreeNode> treeItem) {
		this.treeItem = treeItem;
	}
	public FieldValue getResultsValue(ResultSet results) throws RWException{
		String columnName;
		String columnValue="";
		Double columnNum=0.0;
		columnName =key.substring(key.indexOf('.')+1);
		try {
			switch (fieldType) {
			case BOOLEAN:
				boolean bool = results.getBoolean(columnName);
				columnValue = bool?"True":"False";
				columnNum=0.0;
				break;
			case DATEINT:
				Integer dateValue = results.getInt(columnName);
				columnValue = Utilities.getDateStr(dateValue);
				columnNum= Double.valueOf(dateValue);
				break;
			case DOUBLE:
				Double dbValue = results.getDouble(columnName);
				columnValue=String.valueOf(dbValue);
				columnNum = dbValue;
				break;
			case INTEGER:
				Integer intValue = results.getInt(columnName);
				columnValue = String.valueOf(intValue);
				columnNum= Double.valueOf(intValue);
				break;
			case LONG:
				Long longValue = results.getLong(columnName);
				columnValue = String.valueOf((longValue));
				columnNum= Double.valueOf(longValue);
				break;
			case MONEY:
				Double moneyValue = results.getDouble(columnName);
				columnValue = Main.moneyFmt.format(moneyValue);
				columnNum = moneyValue;
				break;
			case PERCENT:
				Double perValue = results.getDouble(columnName);
				columnValue =Main. perFmt.format(perValue);
				columnNum = perValue;
				break;
			case STRING:
				columnValue = results.getString(columnName);
				break;
			default:
				break;				
			}
		}
		catch (SQLException e) {
			throw new RWException ("Problem rendering report - "+e.getLocalizedMessage());
		}
		return new FieldValue(name,columnNum,columnValue);
	}

	public void loadXML(XMLEventReader xmlEventReader, XMLEvent xmlEvent, ReportTemplate template) throws XMLStreamException {
		boolean done=false;
		String crntField="";
		while (!done) {
			try {
				if (xmlEvent.isStartElement()) {
					StartElement startElement = xmlEvent.asStartElement();
					crntField = startElement.toString();
					switch (startElement.getName().getLocalPart()) {
					case XmlWriter.SRCFLDNAME:
						xmlEvent=xmlEventReader.nextEvent();
						name = xmlEvent.asCharacters().getData();
						break;						
					case XmlWriter.SRCFLDBEAN:
						xmlEvent=xmlEventReader.nextEvent();
						DatasetType fldType = DatasetType.valueOf(xmlEvent.asCharacters().getData()) ;
						switch(fldType) {
						case ACCOUNT:
							fieldBean = new AccountBean();
							break;
						case ADDRESS:
							fieldBean = new AddressBean();
							break;
						case BUDGET:
							fieldBean = new BudgetBean();
							break;
						case CATEGORY:
							fieldBean = new CategoryBean();
							break;
						case CURRENCY:
							fieldBean = new CurrencyBean();
							break;
						case INVESTMENT:
							fieldBean = new InvTranBean();
							break;
						case REMINDER:
							fieldBean = new ReminderBean();
							break;
						case SECURITY:
							fieldBean = new SecurityBean();
							break;
						case TRANSACTION:
							fieldBean = new TransactionBean();
							break;
						default:
							fieldBean = new TransactionBean();
							break;					
						}
						break;
					case XmlWriter.SRCFLDREPTYPE:
						xmlEvent=xmlEventReader.nextEvent();
						reportType= ReportFieldType.valueOf(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.SRCFLDTYPE:
						xmlEvent=xmlEventReader.nextEvent();
						fieldType= BEANFIELDTYPE.getEnum(xmlEvent.asCharacters().getData());
						if (reportType == ReportFieldType.DATABASE) {
							switch (fieldType) {
							case BOOLEAN:
							case DATEINT:
							case STRING:
								outputType = 1;
								break;
							case DOUBLE:
							case INTEGER:
							case LONG:
							case MONEY:
							case NUMERIC:
							case PERCENT:
								outputType = 0;
								break;
							default:
								break;
							}
						}					
						break;
					case XmlWriter.SRCFLDWIDTH:
						xmlEvent=xmlEventReader.nextEvent();
						fieldWidth = Double.parseDouble(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.SRCFLDHEIGHT:
						xmlEvent=xmlEventReader.nextEvent();
						fieldHeight = Double.parseDouble(xmlEvent.asCharacters().getData());
						break;
					case XmlWriter.SRCFLDSELECTED:
						xmlEvent=xmlEventReader.nextEvent();
						selected = xmlEvent.asCharacters().getData().equals("true")?true:false;
						break;
					case XmlWriter.SRCFLDTEXT:
						xmlEvent=xmlEventReader.nextEvent();
						fieldText = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.SRCFLDPATTERN:
						xmlEvent=xmlEventReader.nextEvent();
						fieldPattern = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.SRCFLDEXP:
						xmlEvent=xmlEventReader.nextEvent();
						fieldExp = xmlEvent.asCharacters().getData();
						break;
					case XmlWriter.SRCFLDGROUP:
						xmlEvent=xmlEventReader.nextEvent();
						groupSelected = xmlEvent.asCharacters().getData().equals("true")?true:false;
						break;
					case XmlWriter.SRCFLDOUTPUT:
						xmlEvent=xmlEventReader.nextEvent();
						outputType = Integer.parseUnsignedInt(xmlEvent.asCharacters().getData());
						break;
			}
				}
				if (xmlEvent.isEndElement()) {
					EndElement endElement = xmlEvent.asEndElement();
					String endName = endElement.getName().getLocalPart();
					if (endName.equals(XmlWriter.SELFIELD)|| endName.equals(XmlWriter.LABEL)|| endName.equals(XmlWriter.VARIABLE))
						done = true;
				}
				if (!done && xmlEventReader.hasNext())
					xmlEvent = xmlEventReader.nextEvent();
				else
					done = true;
			}
			catch (XMLStreamException e) {
				throw e;
			}
			catch (IllegalArgumentException e2) {
				OptionMessage.displayMessage("Illegal argument on field "+crntField );
				throw new XMLStreamException(e2.getMessage());
			}
			if (fieldText == null)
				fieldText=name;
		}

	}
	public String getFieldString(GroupValues groupValues) {
		FieldValue fieldValue = groupValues.getFieldValue(key);
		return "";
	}
	public void writeXML(IndentXML writer) throws XMLStreamException {
		try {
			writer.writeDataElement(XmlWriter.SRCFLDNAME,name);
			String dataset="";
			if (fieldBean instanceof TransactionBean)
				dataset=DatasetType.TRANSACTION.toString();
			if (fieldBean instanceof AccountBean)
				dataset=DatasetType.ACCOUNT.toString();
			if (fieldBean instanceof AddressBean)
				dataset=DatasetType.ADDRESS.toString();
			if (fieldBean instanceof BudgetBean)
				dataset=DatasetType.BUDGET.toString();
			if (fieldBean instanceof CategoryBean)
				dataset=DatasetType.CATEGORY.toString();
			if (fieldBean instanceof CurrencyBean)
				dataset=DatasetType.CURRENCY.toString();
			if (fieldBean instanceof InvTranBean)
				dataset=DatasetType.INVESTMENT.toString();
			if (fieldBean instanceof SecurityBean)
				dataset=DatasetType.SECURITY.toString();
			if (fieldBean instanceof ReminderBean)
				dataset=DatasetType.REMINDER.toString();	
			if (dataset!="")
				writer.writeDataElement(XmlWriter.SRCFLDBEAN,dataset);
			writer.writeDataElement(XmlWriter.SRCFLDREPTYPE,reportType.toString());
			if (fieldType != null)
				writer.writeDataElement(XmlWriter.SRCFLDTYPE,fieldType.toString());
			writer.writeDataElement(XmlWriter.SRCFLDWIDTH,String.valueOf(fieldWidth));
			writer.writeDataElement(XmlWriter.SRCFLDHEIGHT,String.valueOf(fieldHeight));
			writer.writeDataElement(XmlWriter.SRCFLDSELECTED,selected?"true":"false");
			writer.writeDataElement(XmlWriter.SRCFLDGROUP,groupSelected?"true":"false");
			writer.writeDataElement(XmlWriter.SRCFLDOUTPUT,String.valueOf(outputType));
			if (fieldText != null) {
				writer.writeDataElement(XmlWriter.SRCFLDTEXT,fieldText);
			}
			if (fieldPattern != null) {
				writer.writeDataElement(XmlWriter.SRCFLDPATTERN,fieldPattern);
			}
			if (fieldExp != null) {
				writer.writeDataElement(XmlWriter.SRCFLDEXP,fieldExp);
			}
		}
		catch (XMLStreamException e) {
			throw e;
		}
		
	}
	@Override
	public String toString() {
		return name;
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
