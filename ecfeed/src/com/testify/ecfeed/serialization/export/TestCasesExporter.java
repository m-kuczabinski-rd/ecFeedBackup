/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.serialization.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

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

	public void exportTestCases(
			MethodNode method, 
			Collection<TestCaseNode> testCases, 
			String file) throws IOException {

		fOutputStream = new FileOutputStream(file);

		try {
			exportHeader(method);

			for (TestCaseNode testCase : testCases)	{
				exportTestCase(testCase);
			}

			exportTail(method);
		} finally {
			fOutputStream.close();
		}
	}


	private void exportHeader(MethodNode method) throws IOException{
		if (fHeaderTemplate != null) {
			fOutputStream.write(TestCasesExportHelper.generateSection(method, fHeaderTemplate).getBytes());
		}

		fExportedTestCases = 0;
	}

	private void exportTestCase(TestCaseNode testCase) throws IOException{
		String testCaseText = 
				TestCasesExportHelper.generateTestCaseString(
						fExportedTestCases, testCase, fTestCaseTemplate);

		fOutputStream.write(testCaseText.getBytes());
		++fExportedTestCases; 
	}

	private void exportTail(MethodNode method) throws IOException{
		if(fTailTemplate != null){
			fOutputStream.write(TestCasesExportHelper.generateSection(method, fTailTemplate).getBytes());
		}
	}	

}
