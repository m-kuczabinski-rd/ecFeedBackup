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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.android.external.ApkInstallerExt;
import com.testify.ecfeed.android.external.DeviceCheckerExt;
import com.testify.ecfeed.core.adapter.java.ILoaderProvider;
import com.testify.ecfeed.core.adapter.java.ModelClassLoader;
import com.testify.ecfeed.core.generators.api.IConstraint;
import com.testify.ecfeed.core.generators.api.IGenerator;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.core.runner.JavaTestRunner;
import com.testify.ecfeed.core.runner.RunnerException;
import com.testify.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;
import com.testify.ecfeed.ui.dialogs.SetupDialogOnline;
import com.testify.ecfeed.ui.dialogs.basic.ErrorDialog;

public abstract class AbstractOnlineSupport extends TestExecutionSupport {

	protected enum RunMode {
		TEST_LOCALLY, TEST_ON_ANDROID, EXPORT
	}

	public enum Result {
		OK, CANCELED
	}

	private MethodNode fTarget;
	private JavaTestRunner fRunner;
	private IFileInfoProvider fFileInfoProvider;
	private RunMode fRunMode;
	private String fTargetFile;
	private String fExportTemplate;
	String fInitialExportTemplate;

	public AbstractOnlineSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, String initialExportTemplate,
			RunMode runMode) {
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader, testMethodInvoker);
		fFileInfoProvider = fileInfoProvider;
		fInitialExportTemplate = initialExportTemplate;
		fRunMode = runMode;
	}

	protected abstract SetupDialogOnline createSetupDialogOnline(
			Shell activeShell, MethodNode methodNode,
			IFileInfoProvider fileInfoProvider, String initialExportTemplate);

	public void setTarget(MethodNode target) {
		try {
			fRunner.setTarget(target);
			fTarget = target;
		} catch (RunnerException e) {
			ErrorDialog.open(Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE,
					e.getMessage());
		}
	}

	public void setTargetForExport(MethodNode target) {
		fRunner.setTargetForExport(target);
		fTarget = target;
	}

	public Result proceed() {
		PrintStream currentOut = System.out;
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager
				.getOutputStream());

		Result result = Result.CANCELED;

		if (fTarget.getParametersCount() > 0) {
			result = displayParametrizedTestsDialog();
		} else {
			if (fRunMode != RunMode.EXPORT) {
				runNonParametrizedTest();
				result = Result.OK;
			}
		}
		System.setOut(currentOut);
		return result;
	}

	private Result displayParametrizedTestsDialog() {
		SetupDialogOnline dialog = createSetupDialogOnline(Display.getCurrent()
				.getActiveShell(), fTarget, fFileInfoProvider,
				fInitialExportTemplate);

		if (dialog.open() != IDialogConstants.OK_ID) {
			return Result.CANCELED;
		}

		IGenerator<ChoiceNode> selectedGenerator = dialog
				.getSelectedGenerator();
		List<List<ChoiceNode>> algorithmInput = dialog.getAlgorithmInput();
		Collection<IConstraint<ChoiceNode>> constraintList = new ArrayList<IConstraint<ChoiceNode>>();
		constraintList.addAll(dialog.getConstraints());
		Map<String, Object> parameters = dialog.getGeneratorParameters();

		runParametrizedTests(selectedGenerator, algorithmInput, constraintList,
				parameters);

		if (fRunMode != RunMode.EXPORT) {
			displayTestStatusDialog();
		}

		fTargetFile = dialog.getTargetFile();
		fExportTemplate = dialog.getExportTemplate();

		return Result.OK;
	}

	private void runParametrizedTests(IGenerator<ChoiceNode> generator,
			List<List<ChoiceNode>> input,
			Collection<IConstraint<ChoiceNode>> constraints,
			Map<String, Object> parameters) {

		GeneratorProgressMonitorDialog progressDialog = new GeneratorProgressMonitorDialog(
				Display.getCurrent().getActiveShell(), generator);

		ParametrizedTestRunnable runnable = new ParametrizedTestRunnable(
				generator, input, constraints, parameters);
		progressDialog.open();
		try {
			progressDialog.run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE,
					e.getMessage());
		}
	}

	private void runNonParametrizedTest() {
		try {
			IRunnableWithProgress operation = new NonParametrizedTestRunnable();
			new ProgressMonitorDialog(Display.getCurrent().getActiveShell())
					.run(true, true, operation);

			MessageDialog.openInformation(null, "Test case executed correctly",
					"The execution of " + fTarget.toString()
							+ " has been succesful");
		} catch (InvocationTargetException | InterruptedException
				| RuntimeException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE,
					e.getMessage());
		}
	}

	public String getExportTemplate() {
		return fExportTemplate;
	}

	public String getTargetFile() {
		return fTargetFile;
	}

	private class ParametrizedTestRunnable implements IRunnableWithProgress {

		private IGenerator<ChoiceNode> fGenerator;
		private List<List<ChoiceNode>> fInput;
		private Collection<IConstraint<ChoiceNode>> fConstraints;
		private Map<String, Object> fParameters;

		ParametrizedTestRunnable(IGenerator<ChoiceNode> generator,
				List<List<ChoiceNode>> input,
				Collection<IConstraint<ChoiceNode>> constraints,
				Map<String, Object> parameters) {
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
		}

		@Override
		public void run(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {

			try {
				if (fRunMode == RunMode.TEST_ON_ANDROID) {
					prepareAndroidRun();
				}

				setProgressMonitor(progressMonitor);
				setTarget();

				List<ChoiceNode> next;
				fGenerator.initialize(fInput, fConstraints, fParameters);
				beginTestExecution(fGenerator.totalWork());

				while ((next = fGenerator.next()) != null
						&& progressMonitor.isCanceled() == false) {
					try {
						setTestProgressMessage();
						processTestCase(next);
					} catch (RunnerException e) {
						addFailedTest(e);
					}
					addExecutedTest(fGenerator.workProgress());
				}
				progressMonitor.done();
			} catch (Throwable e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}

		private void prepareAndroidRun() throws InvocationTargetException {
			DeviceCheckerExt.checkIfOneDeviceAttached();
			EclipseProjectHelper projectHelper = new EclipseProjectHelper(
					fFileInfoProvider);
			new ApkInstallerExt(projectHelper).installApplicationsIfModified();
		}

		private void setTarget() throws RunnerException {
			if (fRunMode == RunMode.EXPORT) {
				fRunner.setTargetForExport(fTarget);
			} else {
				fRunner.setTarget(fTarget);
			}
		}

		private void processTestCase(List<ChoiceNode> testData)
				throws RunnerException {
			if (fRunMode == RunMode.EXPORT) {
				fRunner.prepareTestCaseForExport(testData);
			} else {
				fRunner.runTestCase(testData);
			}
		}
	}

	private class NonParametrizedTestRunnable implements IRunnableWithProgress {

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			if (fRunMode == RunMode.TEST_ON_ANDROID) {
				runAndroidTest(monitor);
				return;
			}

			runStandardTest(monitor);
		}

		private void runAndroidTest(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Installing applications and running test...", 4);

			DeviceCheckerExt.checkIfOneDeviceAttached();
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}

			EclipseProjectHelper projectHelper = new EclipseProjectHelper(
					fFileInfoProvider);
			new ApkInstallerExt(projectHelper).installApplicationsIfModified();
			monitor.worked(3);
			if (monitor.isCanceled()) {
				return;
			}

			try {
				executeSingleTest();
			} catch (RunnerException e) {
				SystemLogger.logCatch(e.getMessage());
				ExceptionHelper.reportRuntimeException(e.getMessage());
			}
			monitor.worked(4);
			monitor.done();
		}

		private void runStandardTest(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Running test...", 1);

			try {
				executeSingleTest();
			} catch (RunnerException e) {
				SystemLogger.logCatch(e.getMessage());
				ExceptionHelper.reportRuntimeException(e.getMessage());
			}

			monitor.worked(1);
			monitor.done();
		}

		private void executeSingleTest() throws RunnerException {
			fRunner.runTestCase(new ArrayList<ChoiceNode>());
		}
	}

}
