package com.ecfeed.core.serialization.export;

/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.export.TestCasesExportHelper;

public class TestCasesExportHelperTest {

	void performTest(String template, String expectedResult) {
		performTest("0", "1", template, expectedResult);
	}

	void performTest(String par0, String par1,  String template, String expectedResult) {
		performTest(0, par0, par1, template, expectedResult);
	}

	void performTest(int sequenceIndex, String template, String expectedResult) {
		performTest(sequenceIndex, "", "",  template, expectedResult);
	}

	void performTest(int sequenceIndex, String par0, String par1,  String template, String expectedResult) {
		ClassNode theClass = new ClassNode("package.Test");

		MethodNode method = new MethodNode("testMethod");
		theClass.addMethod(method);

		MethodParameterNode parameter0 = new MethodParameterNode("par0", "int",
				"0", false);
		ChoiceNode choiceNode00 = new ChoiceNode("p0", par0);
		parameter0.addChoice(choiceNode00);
		method.addParameter(parameter0);

		MethodParameterNode parameter1 = new MethodParameterNode("par1", "int",
				"0", false);
		ChoiceNode choiceNode11 = new ChoiceNode("p1", par1);
		parameter1.addChoice(choiceNode11);
		method.addParameter(parameter1);

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		choices.add(choiceNode00);
		choices.add(choiceNode11);

		TestCaseNode testCase = new TestCaseNode("default", choices);
		testCase.setParent(method);

		String result = TestCasesExportHelper.generateTestCaseString(sequenceIndex, testCase, template);
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldParseNoParams() {
		performTest("ABCD", "ABCD");
	}

	@Test
	public void shouldParseOneParam() {
		performTest("$1.value", "0");
	}

	@Test
	public void shouldParseTwoParams() {
		performTest("$1.value, $2.value", "0, 1");
	}

	@Test
	public void shouldParseThreeParamsTemplate() {
		performTest("$1.value, $2.value, $3.value", "0, 1, $3.value");
	}

	@Test
	public void shouldParseIndex() {
		performTest(5, "%index", "5");
	}

	@Test
	public void shouldParseIndexWithOneParamAndText() {
		performTest(5, "55", "", "%index, $1.value ABCD", "5, 55 ABCD");
	}

	@Test
	public void shouldParseName() { 
		performTest("$1.name", "par0");
	}

	@Test
	public void shouldParsePackageClassMethod() { 
		performTest("%package, %class, %method", "package, Test, testMethod");
	}

	@Test
	public void shouldParseChoiceNames() { 
		performTest("$1.choice, $2.full_choice", "p0, p1");
	}

	@Test
	public void shouldParseTestSuite() { 
		performTest("%suite", "default");
	}

}
