package com.testify.ecfeed.parsers;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.Node;
import com.testify.ecfeed.model.Root;

public class EctParser {
	public Root parseEctFile(InputStream istream){
		Root modelRoot = null;
		
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(istream);
			
			while(eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();
				if(event.isStartDocument()){
					//TODO Xml parser - Handle Start Document event
				}
				else if(event.isStartElement()){
					StartElement startElement = event.asStartElement();
					Node node = parseElement(startElement, eventReader);
					if(node instanceof Root){
						modelRoot = (Root)node;
					}
				}
			}
			
		} catch (XMLStreamException e) {
			System.out.println(e.getMessage());
			modelRoot = null;
		}
		return modelRoot;
	}

	public Node parseElement(StartElement startElement,
			XMLEventReader eventReader) {
		
		switch (startElement.getName().getLocalPart()){
		case Constants.ROOT_NODE_NAME:
			return parseRoot(startElement, eventReader);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Node parseRoot(StartElement startElement, XMLEventReader eventReader) {
		Iterator<Attribute> attributes = (Iterator<Attribute>)startElement.getAttributes();
		String name = null;

		while(attributes.hasNext()){
			Attribute attribute = attributes.next();
			String attributeName = attribute.getName().getLocalPart();
			String attributeValue = attribute.getValue();
			switch (attributeName){
			case Constants.NODE_NAME_ATTRIBUTE:
				name = attributeValue; 
			}
		}
		
		return new Root(name);
	}


}
