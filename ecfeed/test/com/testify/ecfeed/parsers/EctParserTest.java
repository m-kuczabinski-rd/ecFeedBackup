package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.testify.ecfeed.model.RootNode;

public class EctParserTest extends EcParser {

	@Test
	public void testParseEctFile() {
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		try {
			ostream.write(
					(
					"<?xml version=\"1.0\"?>" +
					"\n" +
					"\n" +
					"<Model name=\"model\">\n</Model>"
					).getBytes());
		EcParser parser = new EcParser();
		
		RootNode parsedModel = parser.parseEctFile(new ByteArrayInputStream(ostream.toByteArray()));
		RootNode expectedModel = new RootNode("model");
		assertEquals(expectedModel, parsedModel);
		} catch (IOException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

}
