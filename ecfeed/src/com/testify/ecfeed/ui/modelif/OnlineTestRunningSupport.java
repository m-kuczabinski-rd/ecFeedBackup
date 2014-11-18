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
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.runner.java.JavaTestRunner;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.ExecuteOnlineSetupDialog;
import com.testify.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;

public class OnlineTestRunningSupport extends TestExecutionSupport{

	private MethodNode fTarget;
	private JavaTestRunner fRunner;
	private int fExecutedTestCases;
	
	private class ExecuteRunnable implements IRunnableWithProgress{

		private IGenerator<PartitionNode> fGenerator;
		private List<List<PartitionNode>> fInput;
		private Collection<IConstraint<PartitionNode>> fConstraints;
		private Map<String, Object> fParameters;

		ExecuteRunnable(IGenerator<PartitionNode> generator, 
				List<List<PartitionNode>> input, 
				Collection<IConstraint<PartitionNode>> constraints, 
				Map<String, Object> parameters){
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
		}
		
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try{
				fRunner.setTarget(fTarget);
				List<PartitionNode> next;
				fGenerator.initialize(fInput, fConstraints, fParameters);
				monitor.beginTask(Messages.EXECUTING_TEST_WITH_PARAMETERS, fGenerator.totalWork());
				while((next = fGenerator.next()) != null && monitor.isCanceled() == false){
					++fExecutedTestCases;
					try{
						fRunner.runTestCase(next);
					} catch(RunnerException e){
						addFailedTest(e);
					}
					monitor.worked(fGenerator.workProgress());
				}
				monitor.done();
			} catch (Throwable e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}
	}
	
	public OnlineTestRunningSupport(MethodNode target){
		this();
		setTarget(target);
	}
	
	public OnlineTestRunningSupport() {
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader);
		fExecutedTestCases = 0;
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
			ExecuteOnlineSetupDialog dialog = new ExecuteOnlineSetupDialog(Display.getCurrent().getActiveShell(), fTarget);
			if(dialog.open() == IDialogConstants.OK_ID){
				IGenerator<PartitionNode> selectedGenerator = dialog.getSelectedGenerator();
				List<List<PartitionNode>> algorithmInput = dialog.getAlgorithmInput();
				Collection<IConstraint<PartitionNode>> constraintList = new ArrayList<IConstraint<PartitionNode>>();
				constraintList.addAll(dialog.getConstraints());
				Map<String, Object> parameters = dialog.getGeneratorParameters();
				
				fExecutedTestCases = 0;
				executeGeneratedTests(selectedGenerator, algorithmInput, constraintList, parameters);
				displayTestStatusDialog(fExecutedTestCases);
			}
		} else {
			executeSingleTest();
		}
		System.setOut(currentOut);
	}

	private void executeSingleTest() {
		try {
			fRunner.runTestCase(new ArrayList<PartitionNode>());
			MessageDialog.openInformation(null, "Test case executed correctly", "The execution of " + fTarget.toString() + " has been succesful");
		} catch (RunnerException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
	}

	private void executeGeneratedTests(IGenerator<PartitionNode> generator,
			List<List<PartitionNode>> input,
			Collection<IConstraint<PartitionNode>> constraints,
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
