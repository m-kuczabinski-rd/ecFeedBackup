package com.testify.ecfeed.parsers;

import static com.testify.ecfeed.testutils.Constants.SUPPORTED_TYPES;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.parsers.xml.XomConverter;
import com.testify.ecfeed.parsers.xml.XomParser;
import com.testify.ecfeed.testutils.ModelStringifier;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class XomParserTest {
	
	private final boolean DEBUG = false;
	
	RandomModelGenerator fModelGenerator = new RandomModelGenerator();
	XomConverter fConverter = new XomConverter();
	XomParser fParser = new XomParser();
	ModelStringifier fStringifier = new ModelStringifier();
	
//	@Test
	public void parseRootTest(){
		RootNode root = fModelGenerator.generateModel();
		
		Element rootElement = (Element)root.convert(fConverter);
		
		TRACE(rootElement);
		
		try {
			RootNode parsedRoot = fParser.parseRoot(rootElement);
			
			assertTrue(parsedRoot.compare(root));

			

		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
//	@Test
	public void parseClassTest(){
		ClassNode _class = fModelGenerator.generateClass();

		Element classElement = (Element)_class.convert(fConverter);
		
		TRACE(classElement);
		
		try {
			ClassNode parsedRoot = fParser.parseClass(classElement);
			
			assertTrue(parsedRoot.compare(_class));

		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void parseMethodTest(){
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
			
			Element element = (Element)m.convert(fConverter);
			TRACE(element);

			try{
				MethodNode m1 = fParser.parseMethod(element); 
				assertElementsEqual(m, m1);
			}
			catch (ParserException e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void parseCategoryTest(){
		for(String type : SUPPORTED_TYPES){
			for(boolean expected : new Boolean[]{true, false}){
				CategoryNode c = fModelGenerator.generateCategory(type, expected, 3, 3, 3);
				Element element = (Element)c.convert(fConverter);
				TRACE(element);
				try{
					CategoryNode c1 = fParser.parseCategory(element); 
					assertElementsEqual(c, c1);
				}
				catch (ParserException e) {
					fail("Unexpected exception: " + e.getMessage());
				}
			}
		}
	}
	
	@Test
	public void parseTestCaseTest(){
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
			for(int j = 0; j < 100; j++){
				TestCaseNode tc = fModelGenerator.generateTestCase(m);
				Element element = (Element)tc.convert(fConverter);
				TRACE(element);
				try {
					TestCaseNode tc1 = fParser.parseTestCase(element, m);
					assertElementsEqual(tc, tc1);
				} catch (ParserException e) {
					fail("Unexpected exception: " + e.getMessage());
				}
			}
		}
	}
	
	@Test
	public void parsePartitionTest(){
		for(String type: SUPPORTED_TYPES){
			PartitionNode p = fModelGenerator.generatePartition(3, 3, 3, type);
			Element element = (Element)p.convert(fConverter);
			TRACE(element);
			try {
				PartitionNode p1 = fParser.parsePartition(element);
				assertElementsEqual(p, p1);
			} catch (ParserException e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	@Test 
	public void parseStaticStatementTest(){
		StaticStatement trueStatement = new StaticStatement(true);
		StaticStatement falseStatement = new StaticStatement(false);

		Element trueElement = (Element)trueStatement.accept(fConverter);
		Element falseElement = (Element)falseStatement.accept(fConverter);

		try{
			StaticStatement parsedTrue = fParser.parseStaticStatement(trueElement);
			StaticStatement parsedFalse = fParser.parseStaticStatement(falseElement);

			assertStatementsEqual(trueStatement, parsedTrue);
			assertStatementsEqual(falseStatement, parsedFalse);
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test 
	public void parsePartitionStatementTest(){
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
			PartitionedCategoryStatement s = fModelGenerator.generatePartitionedStatement(m);

			try{
				Element element = (Element)s.accept(fConverter);
				PartitionedCategoryStatement parsedS = null;
				switch(element.getLocalName()){
				case Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
					parsedS = fParser.parseLabelStatement(element, m);
					break;
				case Constants.CONSTRAINT_PARTITION_STATEMENT_NODE_NAME:
					parsedS = fParser.parsePartitionStatement(element, m);
					break;
				}					

				assertStatementsEqual(s, parsedS);
			} catch (ParserException e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	//		@Test
	public void assertTypeTest(){
		RootNode root = fModelGenerator.generateModel();
		ClassNode _class = fModelGenerator.generateClass();
		
		Element rootElement = (Element)root.convert(fConverter);
		Element classElement = (Element)_class.convert(fConverter);
		
		try {
			fParser.parseRoot(rootElement);
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	
		try {
			fParser.parseClass(classElement);
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	
		try {
			fParser.parseClass(rootElement);
			fail("exception expected");
		} catch (ParserException e) {
		}
	
		try {
			fParser.parseRoot(classElement);
			fail("exception expected");
		} catch (ParserException e) {
		}
	}

	private void assertStatementsEqual(BasicStatement s1, BasicStatement s2) {
		if(s1.compare(s2) == false){
			fail("Parsed statement\n" + fStringifier.stringify(s1, 0) + "\ndiffers from original\n" + fStringifier.stringify(s2, 0));
		}
		
	}

	private void assertElementsEqual(IGenericNode n, IGenericNode n1) {
		if(n.compare(n1) == false){
			fail("Parsed element differs from original\n" + fStringifier.stringify(n, 0) + "\n" + fStringifier.stringify(n1, 0));
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
