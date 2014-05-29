package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.junit.Test;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.xml.XomConverter;
import com.testify.ecfeed.parsers.xml.XomParser;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class XomParserTest {
	
	private final boolean DEBUG = true;
	
	RandomModelGenerator fModelGenerator = new RandomModelGenerator();
	XomParser fParser = new XomParser();
	
	@Test
	public void parseRootTest(){
		RootNode root = fModelGenerator.generateModel();
		
		Element rootElement = (Element)root.convert(new XomConverter());
		
		TRACE(rootElement);
		
		try {
			RootNode parsedRoot = fParser.parseRoot(rootElement);
			
			assertTrue(parsedRoot.compare(root));

			

		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	private void TRACE(Element element){
		if(!DEBUG) return;
		
		Document document = new Document(element);
		OutputStream ostream = new ByteArrayOutputStream();
		Serializer serializer = new Serializer(ostream);
		// Uncomment for pretty formatting. This however will affect 
		// whitespaces in the document's ... infoset
		serializer.setIndent(4);
		try {
			serializer.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(ostream);
	}

}
