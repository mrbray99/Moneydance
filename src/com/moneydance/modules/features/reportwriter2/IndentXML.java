package com.moneydance.modules.features.reportwriter2;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.moneydance.modules.features.mrbutil.MRBDebug;

public class IndentXML {
	private XMLStreamWriter writer;
	private String crntElement;
	private int level = 0;
	public IndentXML(XMLStreamWriter writer) {
			this.writer = writer;
	}
	public void writeStartDocument() throws XMLStreamException {
		try {
			writer.writeStartDocument();
			writer.writeCharacters("\n");
			level = 0;
		}
		catch (XMLStreamException e) {
			Main.rwDebugInst.debugThread("IndentXML", "writeStartDocument",MRBDebug.DETAILED,"Error "+ e.getMessage());
			throw e;
		}
	}
	public void writeNewLine()throws XMLStreamException{
		try {
			writeCharacters("\n");
			
		}
		catch(XMLStreamException e) {
			throw e;
		}
	}
	public void writeStartElement(String text)throws XMLStreamException {
		try {
			writer.writeCharacters(createIndent());
			writer.writeStartElement(text);
			level++;
			crntElement = text;
		}
		catch (XMLStreamException e) {
			Main.rwDebugInst.debugThread("IndentXML", "writeStartDocument",MRBDebug.DETAILED,"Error starting element "+text+ " error "+ e.getMessage());
			throw e;
		}
	}
	public void writeAttributedElement(String element,String key, String value) throws XMLStreamException  {
		try {
			writeStartElement(element);
			writeAttribute(key,value);
			writeEndElement();
			
		}
		catch(XMLStreamException e) {
			throw e;
		}
		
		
	}
	public void writeDataElement(String element,String value)throws XMLStreamException  {
		try {
			writeStartElement(element);
			writeCharacters(value);
			writeEndElement();
		}
		catch(XMLStreamException e) {
			throw e;
		}
	}
	public void writeCharacters(String text)throws XMLStreamException {
		try {
			if (text==null)
				writer.writeCharacters(" ");
			else
				writer.writeCharacters(text);
	}
		catch (XMLStreamException e) {
			Main.rwDebugInst.debugThread("IndentXML", "writeStartDocument",MRBDebug.DETAILED,"Error writing characters  "+text +" for element "+crntElement+ " error "+ e.getMessage());
			throw e;
		}
	}
	public void writeAttribute(String key, String value) throws XMLStreamException {
		try {
			if (key==null|| key.isEmpty())
				return;
			if (value==null)
				writer.writeAttribute(key," ");
			else	
				writer.writeAttribute(key,value);
		}
		catch (XMLStreamException e) {
			Main.rwDebugInst.debugThread("IndentXML", "writeStartDocument",MRBDebug.DETAILED,"Error writing attribute "+value +" for element"+crntElement+ " error "+ e.getMessage());
			throw e;
		}
	}
	public void writeEndElement()throws XMLStreamException {
		try {
			level--;
			writer.writeEndElement();
			writer.writeCharacters("\n");
			if (level < 0 )
				level=0;
		}
		catch (XMLStreamException e) {
			Main.rwDebugInst.debugThread("IndentXML", "writeStartDocument",MRBDebug.DETAILED,"Error ending element "+crntElement +" error "+ e.getMessage());
			throw e;
		}
	}
	public void writeIndentedEndElement()throws XMLStreamException {
		try {
			level--;
			writer.writeCharacters(createIndent());
			writer.writeEndElement();
			writer.writeCharacters("\n");
			if (level < 0 )
				level=0;
		}
		catch (XMLStreamException e) {
			Main.rwDebugInst.debugThread("IndentXML", "writeStartDocument",MRBDebug.DETAILED,"Error ending element "+crntElement +" error "+ e.getMessage());
			throw e;
		}
	}

	private String createIndent() {
		String indent = "";
		if (level > 0) {
			for (int i=0;i<level;i++)
				indent+="    ";
		}
		return indent;
	}
}
