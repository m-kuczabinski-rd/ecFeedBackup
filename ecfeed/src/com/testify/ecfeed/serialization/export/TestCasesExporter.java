package com.testify.ecfeed.serialization.export;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class TestCasesExporter {

	OutputStream fOutputStream;
	int fExportedTestCases;
	String fHeaderTemplate;
	String fTestCaseTemplate;
	String fTailTemplate;

	public TestCasesExporter(String file, String headerTemplate, String testCaseTemplate, String tailTemplate) 
			throws FileNotFoundException {
		fOutputStream = new FileOutputStream(file);
		fExportedTestCases = 0;

		fHeaderTemplate = headerTemplate;
		fTestCaseTemplate = testCaseTemplate;
		fTailTemplate = tailTemplate;
	}

	public void exportHeader(MethodNode method) throws IOException{
		if (fHeaderTemplate != null) {
			fOutputStream.write(TestCasesExportHelper.generateSection(method, fHeaderTemplate).getBytes());
		}

		fExportedTestCases = 0;
	}

	public void exportTail(MethodNode method) throws IOException{
		if(fTailTemplate != null){
			fOutputStream.write(TestCasesExportHelper.generateSection(method, fTailTemplate).getBytes());
		}
	}	

	public void exportTestCase(TestCaseNode testCase) throws IOException{
		if(fTestCaseTemplate != null){
			fOutputStream.write(TestCasesExportHelper.generateTestCaseString(fExportedTestCases, testCase, fTestCaseTemplate).getBytes());
			++fExportedTestCases; 
		}
	}

	public void exportTestCases(MethodNode method, Collection<TestCaseNode> testCases) throws IOException{
		exportHeader(method);

		if (fTestCaseTemplate != null) {
			for (TestCaseNode testCase : testCases)	{
				exportTestCase(testCase);
			}
		}

		exportTail(method);
		fOutputStream.close();
	}

	public void close() throws IOException {
		fOutputStream.close();
	}
}
