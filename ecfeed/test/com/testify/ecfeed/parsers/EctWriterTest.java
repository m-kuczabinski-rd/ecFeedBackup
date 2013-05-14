package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.testify.ecfeed.model.Root;

public class EctWriterTest extends EctWriter {

	@Test
	public void testGetStartDocumentStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EctWriter writer = new EctWriter();
		Root root = new Root("root");
		
		writer.getStartDocumentStream(out);
		assertEquals("<?xml version=\"1.0\"?>\n\n", out.toString());
	
		writer.getXmlStream(root, out);
		assertEquals("<?xml version=\"1.0\"?>\n\n<Model name=\"root\">\n</Model>", out.toString());
	}
}
