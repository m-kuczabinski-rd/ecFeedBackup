/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;

class ReadDefaultRunnerOperation implements IRunnableWithProgress{

	final static String fEcFeedTestRunner = "EcFeedTestRunner";
	String runner = null;
	String fTestClassPackageName;

	ReadDefaultRunnerOperation(String testClassPackageName){
		fTestClassPackageName = testClassPackageName;
	}

	@Override
	public void run(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {

		monitor.beginTask("Looking for appropriate runner...", 1);
		runner = getDefaultAndroidRunnerIntr(fTestClassPackageName);
		monitor.worked(1);
		monitor.done();
	}

	public String getRunner() {
		return runner;
	}

	private String getDefaultAndroidRunnerIntr(String testClassPackageName) {

		String runner = "";
		Process process;
		try {
			process = startProcess();
			runner = getDefaultRunner(process, testClassPackageName);
			waitFor(process);
		} catch (Exception e) {
			System.out.println("Cannot get default Android runner. Reason:" + e.getMessage());
		} 

		return runner;
	}

	private Process startProcess() throws Exception {

		ProcessBuilder pb
		= new ProcessBuilder(
				"adb", 
				"shell",
				"pm",
				"list",
				"instrumentation" );

		Process process = null;
		try {
			process = pb.start();
		} catch (IOException e) {
			throw new Exception("Can not list instrumentations.");
		}

		return process;
	}

	private String getDefaultRunner(
			Process process, 
			String testClassPackageName) throws Exception {

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;

		try {
			while ((line = br.readLine()) != null) {
				String runner = getRunner(line, testClassPackageName);
				if (runner != null) {
					return runner;
				}
			}
		} catch (IOException e) {
			throw new Exception(Messages.IO_EXCEPITON_OCCURED(e.getMessage()));
		}

		return "";
	}

	private String getRunner(String line, String testingClassPackage) {

		if (line.indexOf(fEcFeedTestRunner) == -1) {
			return null;
		}

		String runner = extractEcFeedRunner(line);
		String testedClassTestPackage = getTestedClassPackage(runner);

		if (testedClassTestPackage == null) {
			return null;
		}

		if (testingClassPackage.indexOf(testedClassTestPackage) == -1){
			return null;
		}

		return runner;
	}

	private String extractEcFeedRunner(String line) {

		final String instrumentation = "instrumentation:";  
		if (line.indexOf(instrumentation) != -1) {
			line = line.substring(instrumentation.length());
		}

		int index = line.indexOf(fEcFeedTestRunner);

		if (line.indexOf(fEcFeedTestRunner) != -1) {
			line = line.substring(0, index + fEcFeedTestRunner.length());
		}

		return line;
	}

	private String getTestedClassPackage(String runner) {
		int separatorIndex = runner.indexOf("/");
		if ( separatorIndex == -1 ) {
			return null;
		}

		return runner.substring(0, separatorIndex).trim();
	}

	private void waitFor(Process process) throws RunnerException {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw new RunnerException(Messages.INTERRUPTED_EXCEPTION_OCCURED(e.getMessage()));
		}
	}	
}

public class ClassDetailsPagePrompter {

	public static String getDefaultAndroidRunner(String testClassPackageName) {

		ReadDefaultRunnerOperation operation = new ReadDefaultRunnerOperation(testClassPackageName);

		try {
			ProgressMonitorDialog progressDialog = 
					new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

			progressDialog.setCancelable(false);
			progressDialog.run(true, true, operation);
		} catch (InvocationTargetException | InterruptedException e) {
			return null;
		}

		return operation.getRunner();
	}
}
