package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;

public class EctParserTest {

	@Test
	public void testParseEctFile() {
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		EcWriter writer = new EcWriter(ostream);
		
		RootNode root = new RootNode("Root");
		ClassNode classNode1 = new ClassNode("com.testify.ecfeed.Class1");
		ClassNode classNode2 = new ClassNode("com.testify.ecfeed.Class2");
		root.addClass(classNode1);
		root.addClass(classNode2);

		writer.writeXmlDocument(root);

		System.out.println("Generated document:\n" + ostream.toString());
		
		
		InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
		EcParser parser = new EcParser();
		RootNode parsedRoot = parser.parseEctFile(istream);
		

		System.out.println("Parsed:\n");
		System.out.println("Root name: " + parsedRoot.getName() + "\n");

		assertEquals(root.getName(), parsedRoot.getName());
		
		System.out.println("Root number of children: " + parsedRoot.getChildren().size() + "\n");
		if(root.getChildren().size() != parsedRoot.getChildren().size()){
			fail("root.getChildren().size() != parsedRoot.getChildren().size()");
		}
		
		for(int i = 0; i < root.getChildren().size(); i++){
			System.out.println("Class: " + i + "\n");
			ClassNode classNode = (ClassNode)root.getChildren().elementAt(i);
			ClassNode parsedClassNode = (ClassNode)parsedRoot.getChildren().elementAt(i);
			System.out.println("Class name: " + parsedClassNode.getName() + "\n");
			System.out.println("Class qualified name: " + parsedClassNode.getQualifiedName() + "\n");
			assertEquals(classNode.getName(), parsedClassNode.getName());
			assertEquals(classNode.getQualifiedName(), parsedClassNode.getQualifiedName());
		}
	}
}
