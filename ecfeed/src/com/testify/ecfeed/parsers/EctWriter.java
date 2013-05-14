package com.testify.ecfeed.parsers;

import java.io.OutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.Node;
import com.testify.ecfeed.model.Root;

public class EctWriter {
	private XMLOutputFactory fOutputFactory;
	private XMLEventFactory fEventFactory;

	private final XMLEvent fEndLineEvent;
	
	private final String fDummyPrefix = "";
	private final String fDummyNamespaceUri = "";

	public EctWriter(){
		fOutputFactory = XMLOutputFactory.newInstance();
		fEventFactory = XMLEventFactory.newInstance();
		
		fEndLineEvent = fEventFactory.createDTD("\n");
	}
	
	public void getStartDocumentStream(OutputStream out){
		try{
			XMLEventWriter writer = fOutputFactory.createXMLEventWriter(out);
	
			StartDocument startDocument = fEventFactory.createStartDocument();
			writer.add(startDocument);
			writer.add(fEndLineEvent);
			writer.add(fEndLineEvent);
			writer.close();
		}catch(XMLStreamException e){
		}
	}

	public void getXmlStream(Node node, OutputStream out){
		try {
			XMLEventWriter writer = fOutputFactory.createXMLEventWriter(out);

			if (node instanceof Root){
				getXmlRootStream((Root) node, writer);
			}
		} catch (XMLStreamException e) {
		}
	}

	private void getXmlRootStream(Root node, XMLEventWriter writer) {
		String localName = Constants.ROOT_NODE_NAME;
		
		StartElement startElement = fEventFactory.createStartElement(fDummyPrefix, fDummyNamespaceUri, localName);
		EndElement endElement = fEventFactory.createEndElement(fDummyPrefix, fDummyNamespaceUri, localName);
		
		try {
			writer.add(startElement);
			Attribute nameAttribute = fEventFactory.createAttribute(Constants.NODE_NAME_ATTRIBUTE, node.getName());
			writer.add(nameAttribute);
			writer.add(fEndLineEvent);
			writer.add(endElement);
			writer.close();
		} catch (XMLStreamException e) {
			System.out.println("Exception: " + e.getMessage());
		}
		
	}

}
