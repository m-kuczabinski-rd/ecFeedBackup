/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.adapter.java.ILoaderProvider;
import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.android.external.ApkInstallerExt;
import com.testify.ecfeed.android.external.DeviceCheckerExt;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.JavaTestRunner;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.external.ITestMethodInvoker;

public class StaticTestExecutionSupport extends TestExecutionSupport{

	private Collection<TestCaseNode> fTestCases;
	private JavaTestRunner fRunner;
	private List<TestCaseNode> fFailedTests;
	private IFileInfoProvider fFileInfoProvider;
	private boolean fRunOnAndroid;

	private class ExecuteRunnable implements IRunnableWithProgress{

		@Override
		public void run(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {
			if (fRunOnAndroid) {
				DeviceCheckerExt.checkIfOneDeviceAttached();
				EclipseProjectHelper projectHelper = new EclipseProjectHelper(fFileInfoProvider); 
				new ApkInstallerExt(projectHelper).installApplicationsIfModified();
			}			

			setProgressMonitor(progressMonitor);
			fFailedTests.clear();
			beginTestExecution(fTestCases.size());

			for(TestCaseNode testCase : fTestCases){
				if(progressMonitor.isCanceled() == false){
					try {
						setTestProgressMessage();
						fRunner.setTarget(testCase.getMethod());
						fRunner.runTestCase(testCase.getTestData());
					} catch (RunnerException e) {
						addFailedTest(e);
					}
					addExecutedTest(1);
				}
			}
			progressMonitor.done();
		}
	}

	public StaticTestExecutionSupport(
			Collection<TestCaseNode> testCases, 
			ITestMethodInvoker testMethodInvoker, 
			IFileInfoProvider fileInfoProvider, 
			boolean runOnAndroid){
		super();
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader, testMethodInvoker);
		fTestCases = testCases;
		fFailedTests = new ArrayList<>();
		fFileInfoProvider = fileInfoProvider;
		fRunOnAndroid = runOnAndroid;
	}

	public void proceed(){
		PrintStream currentOut = System.out;
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());

		try{
			fFailedTests.clear();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			dialog.open();
			dialog.run(true, true, new ExecuteRunnable());
		}catch(InvocationTargetException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getTargetException().getMessage());
		} catch (InterruptedException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
		if(fFailedTests.size() > 0){
			String message = "Following tests were not successfull\n\n";
			for(TestCaseNode testCase : fFailedTests){
				message += testCase.toString() + "\n";
			}
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, message);
		}
		displayTestStatusDialog();

		System.setOut(currentOut);
	}

}
