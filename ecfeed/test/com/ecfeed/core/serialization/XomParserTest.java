/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization;

import static com.ecfeed.testutils.Constants.SUPPORTED_TYPES;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.junit.Test;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.ect.Constants;
import com.ecfeed.core.serialization.ect.XomAnalyser;
import com.ecfeed.core.serialization.ect.XomAnalyserFactory;
import com.ecfeed.core.serialization.ect.XomBuilder;
import com.ecfeed.core.serialization.ect.XomBuilderFactory;
import com.ecfeed.testutils.ModelStringifier;
import com.ecfeed.testutils.RandomModelGenerator;

public class XomParserTest {

	private final boolean DEBUG = false;

	RandomModelGenerator fModelGenerator = new RandomModelGenerator();

	int version = 0;
	XomBuilder fConverter = XomBuilderFactory.createXomBuilder(version);
	XomAnalyser fXomAnalyser = XomAnalyserFactory.createXomAnalyser(version);
	ModelStringifier fStringifier = new ModelStringifier();
	Random rand = new Random();

	@Test
	public void parseRootTest(){
		try {
			RootNode r = fModelGenerator.generateModel(3);
			Element rootElement = (Element)r.accept(fConverter);
			TRACE(rootElement);
			RootNode parsedR = fXomAnalyser.parseRoot(rootElement);
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
			ClassNode parsedClass = fXomAnalyser.parseClass(element, null);
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
				MethodNode m1 = fXomAnalyser.parseMethod(element, null);
				assertElementsEqual(m, m1);
			}
			catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseParameterTest(){
		for(String type : SUPPORTED_TYPES){
			try{
				for(boolean expected : new Boolean[]{true, false}){
					MethodNode m = new MethodNode("method");
					MethodParameterNode c = fModelGenerator.generateParameter(type, expected, 3, 3, 3);
					m.addParameter(c);
					Element element = (Element)c.accept(fConverter);
					TRACE(element);
					MethodParameterNode c1 = fXomAnalyser.parseMethodParameter(element, m);
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
					TestCaseNode tc1 = fXomAnalyser.parseTestCase(element, m);
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
					ConstraintNode c1 = fXomAnalyser.parseConstraint(element, m);
					assertElementsEqual(c, c1);
				} catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage() + "\nMethod\n" + new ModelStringifier().stringify(m, 0));
				}
			}
		}
	}

	@Test
	public void parseChoiceTest(){
		for(String type: SUPPORTED_TYPES){
			try {
				ChoiceNode p = fModelGenerator.generateChoice(3, 3, 3, type);
				Element element = (Element)p.accept(fConverter);
				TRACE(element);
				ChoiceNode p1 = fXomAnalyser.parseChoice(element);
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

			StaticStatement parsedTrue = fXomAnalyser.parseStaticStatement(trueElement);
			StaticStatement parsedFalse = fXomAnalyser.parseStaticStatement(falseElement);

			assertStatementsEqual(trueStatement, parsedTrue);
			assertStatementsEqual(falseStatement, parsedFalse);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseChoiceStatementTest(){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
				ChoicesParentStatement s = fModelGenerator.generateChoicesParentStatement(m);
				Element element = (Element)s.accept(fConverter);
				TRACE(element);
				ChoicesParentStatement parsedS = null;
				switch(element.getLocalName()){
				case Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
					parsedS = fXomAnalyser.parseLabelStatement(element, m);
					break;
				case Constants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME:
					parsedS = fXomAnalyser.parseChoiceStatement(element, m);
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
				ExpectedValueStatement parsedS = fXomAnalyser.parseExpectedValueStatement(element, m);
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
			StatementArray parsedS = fXomAnalyser.parseStatementArray(element, m);
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

			fXomAnalyser.parseRoot(rootElement);

			try {
				fXomAnalyser.parseClass(classElement, null);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}

			try {
				fXomAnalyser.parseClass(rootElement, null);
				fail("exception expected");
			} catch (Exception e) {
			}

			try {
				fXomAnalyser.parseRoot(classElement);
				fail("exception expected");
			} catch (Exception e) {
			}
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

	}

	private void assertStatementsEqual(AbstractStatement s1, AbstractStatement s2) {
		if(s1.compare(s2) == false){
			fail("Parsed statement\n" + fStringifier.stringify(s1, 0) + "\ndiffers from original\n" + fStringifier.stringify(s2, 0));
		}

	}

	private void assertElementsEqual(AbstractNode n, AbstractNode n1) {
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
