package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.testify.ecfeed.model.RootNode;

public class EctWriterTest extends EcWriter {

	@Test
	public void testGetStartDocumentStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EcWriter writer = new EcWriter();
		RootNode root = new RootNode("root");
		
		writer.getStartDocumentStream(out);
		assertEquals("<?xml version=\"1.0\"?>\n\n", out.toString());
	
		writer.getXmlStream(root, out);
		assertEquals("<?xml version=\"1.0\"?>\n\n<Model name=\"root\">\n</Model>", out.toString());
	}
}
