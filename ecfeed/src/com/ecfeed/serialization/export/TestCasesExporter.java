/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.serialization.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.export.TestCasesExportHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.utils.EclipseHelper;

public class TestCasesExporter {

	String fHeaderTemplate;
	String fTestCaseTemplate;
	String fTailTemplate;
	int fExportedTestCases;

	public TestCasesExporter(String headerTemplate, String testCaseTemplate,
			String tailTemplate) {
		fHeaderTemplate = headerTemplate;
		fTestCaseTemplate = testCaseTemplate;
		fTailTemplate = tailTemplate;
	}

	public void runExport(MethodNode method,
			Collection<TestCaseNode> testCases, String file) throws IOException {

		FileOutputStream outputStream = new FileOutputStream(file);

		try {
			runExportWithProgress(method, testCases, outputStream, true);
		} finally {
			outputStream.close();
		}
	}

	public void runExportWithProgress(MethodNode method,
			Collection<TestCaseNode> testCases, OutputStream outputStream,
			boolean fromGui) {

		ExportRunnable exportRunnable = new ExportRunnable(method, testCases,
				outputStream);
		try {
			if (fromGui) {
				ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(
						EclipseHelper.getActiveShell());

				progressMonitorDialog.run(true, true, exportRunnable);
			} else {
				exportRunnable.run(null);
			}
		} catch (InvocationTargetException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		} catch (InterruptedException e) {
		}
	}

	private void exportHeader(MethodNode method, OutputStream outputStream)
			throws IOException {
		if (fHeaderTemplate != null) {
			String section = TestCasesExportHelper.generateSection(method,
					fHeaderTemplate) + StringHelper.newLine();
			outputStream.write(section.getBytes());
		}

		fExportedTestCases = 0;
	}

	private void exportTestCase(TestCaseNode testCase, OutputStream outputStream)
			throws IOException {

		String testCaseText = TestCasesExportHelper.generateTestCaseString(
				fExportedTestCases, testCase, fTestCaseTemplate)
				+ StringHelper.newLine();

		outputStream.write(testCaseText.getBytes());
		++fExportedTestCases;
	}

	private void exportFooter(MethodNode method, OutputStream outputStream)
			throws IOException {
		if (fTailTemplate != null) {
			String section = TestCasesExportHelper.generateSection(method,
					fTailTemplate) + StringHelper.newLine();
			outputStream.write(section.getBytes());
		}
	}

	private class ExportRunnable implements IRunnableWithProgress {

		MethodNode fMethod;
		Collection<TestCaseNode> fTestCases;
		OutputStream fOutputStream;

		public ExportRunnable(MethodNode method,
				Collection<TestCaseNode> testCases, OutputStream outputStream) {
			fMethod = method;
			fTestCases = testCases;
			fOutputStream = outputStream;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

			ExportMonitor exportMonitor = new ExportMonitor(monitor);

			try {
				exportMonitor.setStatus("Exporting header...");
				exportHeader(fMethod, fOutputStream);

				exportTestCases(exportMonitor);

				exportMonitor.setStatus("Exporting footer...");
				exportFooter(fMethod, fOutputStream);

				exportMonitor.done();

			} catch (IOException e) {
				ExceptionHelper.reportRuntimeException(e.getMessage());
			}
		}

		private void exportTestCases(ExportMonitor exportMonitor)
				throws IOException {

			if (fTestCaseTemplate == null) {
				return;
			}

			exportMonitor.setStatus("Exporting test cases...");
			int count = 0;
			int maxCount = fTestCases.size();
			for (TestCaseNode testCase : fTestCases) {

				exportTestCase(testCase, fOutputStream);
				exportMonitor.setStatus("Exported test cases: " + (++count)
						+ "/" + maxCount);

				if (exportMonitor.isCanceled()) {
					break;
				}
			}
		}
	}

	public class ExportMonitor {

		private IProgressMonitor fMonitor;

		public ExportMonitor(IProgressMonitor monitor) {
			fMonitor = monitor;
		}

		public void setStatus(String status) {
			if (fMonitor == null) {
				return;
			}
			fMonitor.subTask(status);
		}

		public boolean isCanceled() {
			if (fMonitor == null) {
				return false;
			}
			return fMonitor.isCanceled();
		}

		public void done() {
			if (fMonitor == null) {
				return;
			}
			fMonitor.done();
		}
	}

}