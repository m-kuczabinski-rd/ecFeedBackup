/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.serialization.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.TestCaseNode;

public class TestCasesExporter {

	String fHeaderTemplate;
	String fTestCaseTemplate;
	String fTailTemplate;
	OutputStream fOutputStream;
	int fExportedTestCases;

	private static final String MSG_TEST_CASE_NOT_EMPTY = "Test case template must not be empty.";

	public TestCasesExporter(String headerTemplate, String testCaseTemplate, String tailTemplate) {

		if (testCaseTemplate == null) {
			throw new RuntimeException(MSG_TEST_CASE_NOT_EMPTY);
		}

		fHeaderTemplate = headerTemplate;
		fTestCaseTemplate = testCaseTemplate;
		fTailTemplate = tailTemplate;
	}

	public void runExport(
			MethodNode method, 
			Collection<TestCaseNode> testCases, 
			String file) throws IOException {

		FileOutputStream outputStream = new FileOutputStream(file);

		try {
			runExport(method, testCases, outputStream);
		} finally {
			fOutputStream.close();
		}
	}

	public void runExport(
			MethodNode method, 
			Collection<TestCaseNode> testCases, 
			OutputStream outputStream) throws IOException {
		
		fOutputStream = outputStream;
		
		exportHeader(method);

		for (TestCaseNode testCase : testCases)	{
			exportTestCase(testCase);
		}

		exportTail(method);
	}

	private void exportHeader(MethodNode method) throws IOException{
		if (fHeaderTemplate != null) {
			String section = TestCasesExportHelper.generateSection(method, fHeaderTemplate) + System.lineSeparator();
			fOutputStream.write(section.getBytes());
		}

		fExportedTestCases = 0;
	}

	private void exportTestCase(TestCaseNode testCase) throws IOException{
		String testCaseText = 
				TestCasesExportHelper.generateTestCaseString(
						fExportedTestCases, testCase, fTestCaseTemplate) + System.lineSeparator();

		fOutputStream.write(testCaseText.getBytes());
		++fExportedTestCases; 
	}

	private void exportTail(MethodNode method) throws IOException{
		if(fTailTemplate != null){
			String section = TestCasesExportHelper.generateSection(method, fTailTemplate) + System.lineSeparator();
			fOutputStream.write(section.getBytes());
		}
	}	

}