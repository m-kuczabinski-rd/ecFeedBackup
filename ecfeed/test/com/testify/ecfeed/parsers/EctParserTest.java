package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.testify.ecfeed.model.Root;

public class EctParserTest extends EctParser {

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
		EctParser parser = new EctParser();
		
		Root parsedModel = parser.parseEctFile(new ByteArrayInputStream(ostream.toByteArray()));
		Root expectedModel = new Root("model");
		assertEquals(expectedModel, parsedModel);
		} catch (IOException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

}
