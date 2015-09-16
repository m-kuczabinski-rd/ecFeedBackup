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
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.adapter.java.ILoaderProvider;
import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.runner.JavaTestRunner;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.ExecuteOnlineSetupDialog;
import com.testify.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;
import com.testify.ecfeed.ui.modelif.external.ITestMethodInvoker;

public class OnlineTestRunningSupport extends TestExecutionSupport{

	private MethodNode fTarget;
	private JavaTestRunner fRunner;
	private IFileInfoProvider fFileInfoProvider;
	private boolean fRunOnAndroid;

	private class ExecuteRunnable implements IRunnableWithProgress{

		private IGenerator<ChoiceNode> fGenerator;
		private List<List<ChoiceNode>> fInput;
		private Collection<IConstraint<ChoiceNode>> fConstraints;
		private Map<String, Object> fParameters;

		ExecuteRunnable(IGenerator<ChoiceNode> generator, 
				List<List<ChoiceNode>> input, 
				Collection<IConstraint<ChoiceNode>> constraints, 
				Map<String, Object> parameters){
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
		}

		@Override
		public void run(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {

			try{
				if (fRunOnAndroid) {
					new DeviceChecker().checkIfOneDeviceAttached();
					new ApkInstaller().installApplicationsIfModified(fFileInfoProvider);
				}

				setProgressMonitor(progressMonitor);
				fRunner.setTarget(fTarget);
				List<ChoiceNode> next;
				fGenerator.initialize(fInput, fConstraints, fParameters);
				beginTestExecution(fGenerator.totalWork());

				while((next = fGenerator.next()) != null && progressMonitor.isCanceled() == false){
					try{
						setTestProgressMessage();
						fRunner.runTestCase(next);
					} catch(RunnerException e){
						addFailedTest(e);
					}
					addExecutedTest(fGenerator.workProgress());
				}
				progressMonitor.done();
			} catch (Throwable e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}
	}

	public OnlineTestRunningSupport(
			MethodNode target, 
			ITestMethodInvoker testMethodInvoker, 
			IFileInfoProvider fileInfoProvider, 
			boolean runOnAndroid){
		this(testMethodInvoker, fileInfoProvider, runOnAndroid);
		setTarget(target);
	}

	public OnlineTestRunningSupport(
			ITestMethodInvoker testMethodInvoker, IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader, testMethodInvoker);
		fFileInfoProvider = fileInfoProvider;
		fRunOnAndroid = runOnAndroid;
	}

	public void setTarget(MethodNode target) {
		try {
			fRunner.setTarget(target);
			fTarget = target;
		} catch (RunnerException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
	}

	public void proceed(){
		PrintStream currentOut = System.out;
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());

		if (fTarget.getParameters().size() > 0) {
			ExecuteOnlineSetupDialog dialog = 
					new ExecuteOnlineSetupDialog(Display.getCurrent().getActiveShell(), fTarget, fFileInfoProvider);
			if(dialog.open() == IDialogConstants.OK_ID){
				IGenerator<ChoiceNode> selectedGenerator = dialog.getSelectedGenerator();
				List<List<ChoiceNode>> algorithmInput = dialog.getAlgorithmInput();
				Collection<IConstraint<ChoiceNode>> constraintList = new ArrayList<IConstraint<ChoiceNode>>();
				constraintList.addAll(dialog.getConstraints());
				Map<String, Object> parameters = dialog.getGeneratorParameters();

				executeGeneratedTests(selectedGenerator, algorithmInput, constraintList, parameters);
				displayTestStatusDialog();
			}
		} else {
			executeSingleTest();
		}
		System.setOut(currentOut);
	}

	private void executeSingleTest() {
		try {
			fRunner.runTestCase(new ArrayList<ChoiceNode>());
			MessageDialog.openInformation(null, "Test case executed correctly", "The execution of " + fTarget.toString() + " has been succesful");
		} catch (RunnerException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
	}

	private void executeGeneratedTests(IGenerator<ChoiceNode> generator,
			List<List<ChoiceNode>> input,
			Collection<IConstraint<ChoiceNode>> constraints,
			Map<String, Object> parameters) {

		GeneratorProgressMonitorDialog progressDialog = 
				new GeneratorProgressMonitorDialog(Display.getCurrent().getActiveShell(), generator);
		ExecuteRunnable runnable = new ExecuteRunnable(generator, input, constraints, parameters);
		progressDialog.open();
		try {
			progressDialog.run(true,  true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
	}

}
