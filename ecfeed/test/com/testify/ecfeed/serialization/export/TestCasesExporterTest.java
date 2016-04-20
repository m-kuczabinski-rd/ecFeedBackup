/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.serialization.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class TestCasesExporterTest {

	@Test
	public void methodWithTwoParamsExportTest(){
		ClassNode theClass = new ClassNode("Test");

		MethodNode method = new MethodNode("testMethod");
		theClass.addMethod(method);

		MethodParameterNode parameter0 = new MethodParameterNode("par0", "int", "0", false);
		ChoiceNode choiceNode00 = new ChoiceNode("value00", "0");
		parameter0.addChoice(choiceNode00);
		method.addParameter(parameter0);

		MethodParameterNode parameter1 = new MethodParameterNode("par1", "int", "0", false);
		ChoiceNode choiceNode11 = new ChoiceNode("value11", "1");
		parameter1.addChoice(choiceNode11);
		method.addParameter(parameter1);

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		choices.add(choiceNode00);
		choices.add(choiceNode11);

		TestCaseNode testCase = new TestCaseNode("default", choices);
		method.addTestCase(testCase);

		Collection<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
		testCases.add(testCase);

		TestCasesExporter exporter = 
				new TestCasesExporter("$1.name, $2.name", "$1.value, $2.value", "end");

		OutputStream stream = new ByteArrayOutputStream();

		try {
			exporter.runExport(method, testCases, stream);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		String result = stream.toString();

		String expectedResult = 
				"par0, par1" + System.lineSeparator() + 
				"0, 1" + System.lineSeparator() +
				"end" + System.lineSeparator();

		assertEquals(expectedResult, result);
	}

}

