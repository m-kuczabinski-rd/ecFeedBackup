package com.testify.ecfeed.parsers;

import java.io.ByteArrayOutputStream;
import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.constants.Constants;

public class EctWriterTest extends EcWriter {
	private ByteArrayOutputStream fOStream;
	private EcWriter fWriter;
	
	public EctWriterTest() {
		super(new ByteArrayOutputStream());
		fOStream = new ByteArrayOutputStream();
		fWriter = new EcWriter(fOStream);
	}

	@Test
	public void testWriteDocument(){
		RootNode root = new RootNode("Root");
		ClassNode classNode1 = new ClassNode("com.testify.ecfeed.Class1");
		ClassNode classNode2 = new ClassNode("com.testify.ecfeed.Class2");
		root.addClass(classNode1);
		root.addClass(classNode2);
		
		fWriter.writeXmlDocument(root);
		System.out.println("Written document:\n" + fOStream.toString());
	}
	
	@SuppressWarnings("unused")
	private String expectedRootStartTag(String name){
		return "<" + Constants.ROOT_NODE_NAME + " " + Constants.NODE_NAME_ATTRIBUTE + "=\"" + name + "\">";
	}

	@SuppressWarnings("unused")
	private String expectedRootEndTag(){
		return "</" + Constants.ROOT_NODE_NAME + ">";
	}

	@SuppressWarnings("unused")
	private String expectedClassStartTag(String name, String qualifiedName){
		return "<" + Constants.CLASS_NODE_NAME + " " + Constants.NODE_NAME_ATTRIBUTE + "=\"" + name + 
				"\" " + Constants.QUALIFIED_NAME_ATTRIBUTE + "=\"" + qualifiedName + "\">";
	}

	@SuppressWarnings("unused")
	private String expectedClassEndTag(){
		return "</" + Constants.CLASS_NODE_NAME + ">";
	}
	
	@SuppressWarnings("unused")
	private String expectedDocumentStartTag(){
		return "<?xml version=\"1.0\"?>";
	}

//	@Before
//	public void flushOutputStream() throws IOException{
//		if(fOStream != null){
//			fOStream.flush();
//		}
//	}
//	
//	@Test
//	public void testWriteStartDocument() throws IOException{
//		fOStream.flush();
//		fWriter.openWriterStream();
//		try {
//			fWriter.writeStartDocumentStream();
//		} catch (XMLStreamException e) {
//			fail("Unexpected exception: " + e.getMessage());
//		}
//		fWriter.closeWriterStream();
//		assertEquals(expectedDocumentStartTag(), fOStream.toString());
//	}
//	
//	@Test
//	public void testWriteRootNode() {
//		fWriter.openWriterStream();
//		RootNode root = new RootNode("root");
//		
//		try {
//			fWriter.writeNodeXmlStream(root);
//		} catch (XMLStreamException e) {
//			fail("Unexpected exception: " + e.getMessage());
//		}
//		fWriter.closeWriterStream();
//		assertEquals(expectedRootStartTag("root") + "\n" + expectedRootEndTag() + "\n", fOStream.toString());
//	}
//
//	@Test
//	public void testWriteClassNode() {
//		fWriter.openWriterStream();
//		final String className = "class";
//		final String qualifiedName = "com.testify.ecfeed.class";
//		
//		ClassNode classNode = new ClassNode(className, qualifiedName);
//		
//		try {
//			fWriter.writeNodeXmlStream(classNode);
//		} catch (XMLStreamException e) {
//			fail("Unexpected exception: " + e.getMessage());
//		}
//		fWriter.closeWriterStream();
//		
//		System.out.println("Expected stream:\n" + expectedClassStartTag(className, qualifiedName) + "\n" + expectedClassEndTag() + "\n");
//		System.out.println("\nActual stream:\n" + fOStream.toString());
//
//		assertEquals(expectedClassStartTag(className, qualifiedName) + "\n" + expectedClassEndTag() + "\n",
//				fOStream.toString());
//	}
//	
//	@Test
//	public void testWriteDocument(){
//		final String rootName = "root";
//		final String class1Name = "Class1";
//		final String class1QualifiedName = "com.testify.ecfeed.Class1";
//		final String class2Name = "Class2";
//		final String class2QualifiedName = "com.testify.ecfeed.Class2";
//		
//		String expectedDocument = 
//				expectedDocumentStartTag() + 
//				"\n" + 
//				"\n" +
//				expectedRootStartTag(rootName) + "\n" +
//				expectedClassStartTag(class1Name, class1QualifiedName) + "\n" + 
//				expectedClassEndTag() + "\n" + 
//				expectedClassStartTag(class2Name, class2QualifiedName) + "\n" + 
//				expectedClassEndTag() + "\n" +
//				expectedRootEndTag() + "\n";
//		
//		RootNode root = new RootNode(rootName);
//		ClassNode class1 = new ClassNode(class1Name, class1QualifiedName);
//		ClassNode class2 = new ClassNode(class2Name, class2QualifiedName);
//		root.addClass(class1);
//		root.addClass(class2);
//		
//		fWriter.openWriterStream();
//		fWriter.writeXmlDocument(root);
//		fWriter.closeWriterStream();
//		
//		assertEquals(expectedDocument, fOStream.toString());
//	}
//	
}
