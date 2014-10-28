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
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.runner.java.JavaTestRunner;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;

public class StaticTestExecutionSupport extends TestExecutionSupport{
	
	private Collection<TestCaseNode> fTestCases;
	private JavaTestRunner fRunner;
	private List<TestCaseNode> fFailedTests;
	
	private class ExecuteRunnable implements IRunnableWithProgress{

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			fFailedTests.clear();
			monitor.beginTask(Messages.EXECUTING_TEST_WITH_PARAMETERS, fTestCases.size());
			for(TestCaseNode testCase : fTestCases){
				if(monitor.isCanceled() == false){
					try {
						fRunner.setTarget(testCase.getMethod());
						fRunner.runTestCase(testCase.getTestData());
					} catch (RunnerException e) {
						addFailedTest(e);
					}
					monitor.worked(1);
				}
			}
			monitor.done();
		}
	}
	
	public StaticTestExecutionSupport(Collection<TestCaseNode> testCases){
		super();
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader);
		fTestCases = testCases;
		fFailedTests = new ArrayList<>();
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
		displayTestStatusDialog(fTestCases.size());

		System.setOut(currentOut);
	}

}
