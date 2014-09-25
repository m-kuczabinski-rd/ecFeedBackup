package com.testify.ecfeed.ui.modelif;

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

import com.testify.ecfeed.abstraction.java.ModelClassLoader;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.runner.JavaTestRunner;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.ExecuteOnlineSetupDialog;
import com.testify.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;

public class OnlineTestRunningSupport {

	private MethodNode fTarget;
	private JavaTestRunner fRunner;
	
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
				JavaTestRunner testRunner = new JavaTestRunner(EclipseLoaderProvider.createLoader());
				testRunner.setTarget(fTarget);
				List<PartitionNode> next;
				fGenerator.initialize(fInput, fConstraints, fParameters);
				monitor.beginTask(Messages.EXECUTING_TEST_WITH_PARAMETERS, fGenerator.totalWork());
				while((next = fGenerator.next()) != null && monitor.isCanceled() == false){
					testRunner.runTestCase(next);
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
		ModelClassLoader loader = new EclipseLoaderProvider().getLoader(true, null);
		fRunner = new JavaTestRunner(loader);
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
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());
		if (fTarget.getCategories().size() > 0) {
			ExecuteOnlineSetupDialog dialog = new ExecuteOnlineSetupDialog(Display.getCurrent().getActiveShell(), fTarget);
			if(dialog.open() == IDialogConstants.OK_ID){
				IGenerator<PartitionNode> selectedGenerator = dialog.getSelectedGenerator();
				List<List<PartitionNode>> algorithmInput = dialog.getAlgorithmInput();
				Collection<IConstraint<PartitionNode>> constraintList = new ArrayList<IConstraint<PartitionNode>>();
				constraintList.addAll(dialog.getConstraints());
				Map<String, Object> parameters = dialog.getGeneratorParameters();
				
				executeGeneratedTests(selectedGenerator, algorithmInput, constraintList, parameters);
			}
		} else {
			executeSingleTest();
		}
	}

	private void executeSingleTest() {
		try {
			fRunner.runTestCase(new ArrayList<PartitionNode>());
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
