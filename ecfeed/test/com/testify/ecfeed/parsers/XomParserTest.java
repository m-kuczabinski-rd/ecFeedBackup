package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;
import nu.xom.Element;

import org.junit.Test;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.xml.XomConverter;
import com.testify.ecfeed.parsers.xml.XomParser;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class XomParserTest {
	
	RandomModelGenerator fModelGenerator = new RandomModelGenerator();
	XomParser fParser = new XomParser();
	
	@Test
	public void parseRootTest(){
		RootNode root = fModelGenerator.generateModel();
		
		Element rootElement = (Element)root.convert(new XomConverter());
		
		try {
			RootNode parsedRoot = fParser.parseRoot(rootElement);
			
			assertTrue(parsedRoot.compare(root));
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

}
