/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.serialization;

import static com.testify.ecfeed.testutils.Constants.SUPPORTED_TYPES;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.serialization.ect.Constants;
import com.testify.ecfeed.serialization.ect.XomAnalyser;
import com.testify.ecfeed.serialization.ect.XomBuilder;
import com.testify.ecfeed.testutils.ModelStringifier;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class XomParserTest {
	
	private final boolean DEBUG = false;
	
	RandomModelGenerator fModelGenerator = new RandomModelGenerator();
	XomBuilder fConverter = new XomBuilder();
	XomAnalyser fParser = new XomAnalyser();
	ModelStringifier fStringifier = new ModelStringifier();
	Random rand = new Random();
	
	@Test
	public void parseRootTest(){
		try {
			RootNode r = fModelGenerator.generateModel(3);
			Element rootElement = (Element)r.accept(fConverter);
			TRACE(rootElement);
			RootNode parsedR = fParser.parseRoot(rootElement);
			assertElementsEqual(r, parsedR);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void parseClassTest(){
		try {
			ClassNode _class = fModelGenerator.generateClass(3);
			Element element = (Element)_class.accept(fConverter);
			TRACE(element);
			ClassNode parsedClass = fParser.parseClass(element);
			assertElementsEqual(_class, parsedClass);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void parseMethodTest(){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(5, 5, 5);
				Element element = (Element)m.accept(fConverter);
				TRACE(element);
				MethodNode m1 = fParser.parseMethod(element); 
				assertElementsEqual(m, m1);
			}
			catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void parseCategoryTest(){
		for(String type : SUPPORTED_TYPES){
			try{
			for(boolean expected : new Boolean[]{true, false}){
				CategoryNode c = fModelGenerator.generateCategory(type, expected, 3, 3, 3);
				Element element = (Element)c.accept(fConverter);
				TRACE(element);
					CategoryNode c1 = fParser.parseCategory(element); 
					assertElementsEqual(c, c1);
				}
			}
			catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void parseTestCaseTest(){
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
			for(int j = 0; j < 100; j++){
				try {
					TestCaseNode tc = fModelGenerator.generateTestCase(m);
					Element element = (Element)tc.accept(fConverter);
					TRACE(element);
					TestCaseNode tc1 = fParser.parseTestCase(element, m);
					assertElementsEqual(tc, tc1);
				} catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage());
				}
			}
		}
	}
	
	@Test
	public void parseConstraintTest(){
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(3, 0, 0);
			for(int j = 0; j < 10; j++){
				try {
					ConstraintNode c = fModelGenerator.generateConstraint(m);
					Element element = (Element)c.accept(fConverter);
					TRACE(element);
					ConstraintNode c1 = fParser.parseConstraint(element, m);
					assertElementsEqual(c, c1);
				} catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage() + "\nMethod\n" + new ModelStringifier().stringify(m, 0));
				}
			}
		}
	}
	
	@Test
	public void parsePartitionTest(){
		for(String type: SUPPORTED_TYPES){
			try {
				PartitionNode p = fModelGenerator.generatePartition(3, 3, 3, type);
				Element element = (Element)p.accept(fConverter);
				TRACE(element);
				PartitionNode p1 = fParser.parsePartition(element);
				assertElementsEqual(p, p1);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	@Test 
	public void parseStaticStatementTest(){
		StaticStatement trueStatement = new StaticStatement(true);
		StaticStatement falseStatement = new StaticStatement(false);
		try{
			Element trueElement = (Element)trueStatement.accept(fConverter);
			Element falseElement = (Element)falseStatement.accept(fConverter);
			TRACE(trueElement);
			TRACE(falseElement);

			StaticStatement parsedTrue = fParser.parseStaticStatement(trueElement);
			StaticStatement parsedFalse = fParser.parseStaticStatement(falseElement);

			assertStatementsEqual(trueStatement, parsedTrue);
			assertStatementsEqual(falseStatement, parsedFalse);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test 
	public void parsePartitionStatementTest(){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
				PartitionedCategoryStatement s = fModelGenerator.generatePartitionedStatement(m);
				Element element = (Element)s.accept(fConverter);
				TRACE(element);
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
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void parseExpectedValueStatementTest(){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(10, 0, 0);
				ExpectedValueStatement s = fModelGenerator.generateExpectedValueStatement(m);
				Element element = (Element)s.accept(fConverter);
				TRACE(element);
				ExpectedValueStatement parsedS = fParser.parseExpectedValueStatement(element, m);
				assertStatementsEqual(s, parsedS);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseStatementArrayTest(){
		try{
			MethodNode m = fModelGenerator.generateMethod(10, 0, 0);
			StatementArray s = fModelGenerator.generateStatementArray(m, 4);
			Element element = (Element)s.accept(fConverter);
			TRACE(element);
			StatementArray parsedS = fParser.parseStatementArray(element, m);
			assertStatementsEqual(s, parsedS);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
		
	}
	
	@Test
	public void assertTypeTest(){
		try{
			RootNode root = fModelGenerator.generateModel(3);
			ClassNode _class = fModelGenerator.generateClass(3);

			Element rootElement = (Element)root.accept(fConverter);
			Element classElement = (Element)_class.accept(fConverter);
		
			fParser.parseRoot(rootElement);
			
			try {
				fParser.parseClass(classElement);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		
			try {
				fParser.parseClass(rootElement);
				fail("exception expected");
			} catch (Exception e) {
			}
		
			try {
				fParser.parseRoot(classElement);
				fail("exception expected");
			} catch (Exception e) {
			}
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	
	}

	private void assertStatementsEqual(BasicStatement s1, BasicStatement s2) {
		if(s1.compare(s2) == false){
			fail("Parsed statement\n" + fStringifier.stringify(s1, 0) + "\ndiffers from original\n" + fStringifier.stringify(s2, 0));
		}
		
	}

	private void assertElementsEqual(GenericNode n, GenericNode n1) {
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
