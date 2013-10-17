/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.modeleditor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.api.IAlgorithm;
import com.testify.ecfeed.api.IAlgorithmInput;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.dialogs.GenerateTestSuiteDialog;

class GenerateTestSuiteAdapter extends SelectionAdapter{
	
	private final MethodNodeDetailsPage fPage;
	private boolean fCanceled;

	GenerateTestSuiteAdapter(
			MethodNodeDetailsPage methodNodeDetailsPage) {
		this.fPage = methodNodeDetailsPage;
	}

	private class AlgorithmProgressMonitor implements IProgressMonitor{
		private Display fDisplay = Display.getDefault();
		private IProgressMonitor fMonitor;
		
		public AlgorithmProgressMonitor(ProgressMonitorDialog dialog) {
			fMonitor = dialog.getProgressMonitor();
		}

		@Override
		public void beginTask(final String name, final int totalWork) {
			fDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					fMonitor.beginTask(name, totalWork);
				}
			});
		}

		@Override
		public void done() {
			fDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					fMonitor.done();
				}
			});
		}

		@Override
		public void internalWorked(final double work) {
			fDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					fMonitor.internalWorked(work);
				}
			});
		}

		@Override
		public boolean isCanceled() {
			return fMonitor.isCanceled();
		}

		@Override
		public void setCanceled(boolean value) {
			fMonitor.setCanceled(value);
		}

		@Override
		public void setTaskName(final String name) {
			fDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					fMonitor.setTaskName(name);
				}
			});
		}

		@Override
		public void subTask(final String name) {
			fDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					fMonitor.subTask(name);
				}
			});
		}

		@Override
		public void worked(final int work) {
			fDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					fMonitor.worked(work);
				}
			});
		}
	}
	
	private class AlgorithmInput implements IAlgorithmInput<PartitionNode>{
//		public IAlgorithm<PartitionNode> algorithm;
		public List<List<PartitionNode>> algorithmInput;
		public Collection<IConstraint<PartitionNode>> constraints;

		public AlgorithmInput(//IAlgorithm<PartitionNode> algorithm, 
				List<List<PartitionNode>> algorithmInput,
				Collection<IConstraint<PartitionNode>> constraints){
//			this.algorithm = algorithm;
			this.algorithmInput = algorithmInput;
			this.constraints = constraints;
		}

		@Override
		public List<List<PartitionNode>> getInput() {
			return algorithmInput;
		}

		@Override
		public Collection<IConstraint<PartitionNode>> getConstraints() {
			return constraints;
		}
	}
	
	private class AlgorithmRunnable implements IRunnableWithProgress{
		private IAlgorithm<PartitionNode> algorithm;
		private AlgorithmInput context;
		private IProgressMonitor progressMonitor;
		private Set<List<PartitionNode>> generatedData;
		
		public AlgorithmRunnable(IAlgorithm<PartitionNode> algorithm,
				AlgorithmInput context,
				IProgressMonitor progressMonitor) {
			this.algorithm = algorithm;
			this.context = context;
			this.progressMonitor = progressMonitor;
		}
		
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			generatedData = algorithm.generate(context, progressMonitor);
		}
	
		public Set<List<PartitionNode>> getGeneratedData(){
			return generatedData;
		}
	}
	
	@Override
	public void widgetSelected(SelectionEvent e){
		GenerateTestSuiteDialog dialog = 
				new GenerateTestSuiteDialog(fPage.getActiveShell(), 
						fPage.getSelectedMethod());
		if(dialog.open() == IDialogConstants.OK_ID){
			IAlgorithm<PartitionNode> selectedAlgorithm = dialog.getSelectedAlgorithm();
			List<List<PartitionNode>> algorithmInput = dialog.getAlgorithmInput();
			Collection<IConstraint<PartitionNode>> constraints = dialog.getConstraints();
			AlgorithmInput context = 
					new AlgorithmInput(algorithmInput, constraints);
			String testSuiteName = dialog.getTestSuiteName();
			
			Set<List<PartitionNode>> generatedData = generateTestData(selectedAlgorithm, context);
			if(generatedData == null){
				return;
			}

			addGeneratedDataToModel(testSuiteName, generatedData);
		}
	}

	private Set<List<PartitionNode>> generateTestData(final IAlgorithm<PartitionNode> algorithm, 
			final AlgorithmInput context) {
		ProgressMonitorDialog progressDialog = 
				new ProgressMonitorDialog(fPage.getActiveShell());
		AlgorithmProgressMonitor progressMonitor = 
				new AlgorithmProgressMonitor(progressDialog);
		AlgorithmRunnable algorithmRunnable = 
				new AlgorithmRunnable(algorithm, context, progressMonitor);
		try {
			progressDialog.run(true, true, algorithmRunnable);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		fCanceled = progressMonitor.isCanceled();
		return algorithmRunnable.getGeneratedData();
	}

	private void addGeneratedDataToModel(String testSuiteName, 
			Set<List<PartitionNode>> generatedData) {
		int dataLength = generatedData.size();
		if(dataLength > 0){
			if(generatedData.size() > Constants.TEST_SUITE_SIZE_WARNING_LIMIT){
				MessageDialog warningDialog = 
						new MessageDialog(Display.getDefault().getActiveShell(), 
								DialogStrings.DIALOG_LARGE_TEST_SUITE_GENERATED_TITLE, 
								Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
								DialogStrings.DIALOG_LARGE_TEST_SUITE_GENERATED_MESSAGE(dataLength),
								MessageDialog.WARNING, 
								new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 
								IDialogConstants.OK_ID);
				if(warningDialog.open() == IDialogConstants.CANCEL_ID){
					return;
				}
			}
			addTestSuiteToModel(testSuiteName, generatedData);
		}
		else if (!fCanceled){
			new MessageDialog(Display.getDefault().getActiveShell(), 
					DialogStrings.DIALOG_EMPTY_TEST_SUITE_GENERATED_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 
					DialogStrings.DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE,
					MessageDialog.INFORMATION, 
					new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID).open();
		}
	}

	private void addTestSuiteToModel(String testSuiteName, Set<List<PartitionNode>> generatedData) {
		List<TestCaseNode> testSuite = new ArrayList<TestCaseNode>();
		MethodNode selectedMethod = fPage.getSelectedMethod();
		for(List<PartitionNode> testCase : generatedData){
			List<PartitionNode> testData = (List<PartitionNode>)testCase;
			TestCaseNode testCaseNode = new TestCaseNode(testSuiteName, testData);
			testSuite.add(testCaseNode);
		}
		replaceExpectedValues(testSuite);
		for(TestCaseNode testCase : testSuite){
			selectedMethod.addTestCase(testCase);
		}
		fPage.updateModel(selectedMethod);
	}

	private void replaceExpectedValues(List<TestCaseNode> testSuite) {
		if(fPage.getSelectedMethod().getExpectedCategoriesNames().size() == 0){
			return;
		}
		//replace expected values partitions with anonymous ones
		for(TestCaseNode testCase : testSuite){
			List<PartitionNode> testData = testCase.getTestData();
			for(int i = 0; i < testData.size(); i++){
				CategoryNode category = testData.get(i).getCategory();
				if(category.isExpected()){
					PartitionNode anonymousPartition = 
							new PartitionNode(Constants.EXPECTED_VALUE_PARTITION_NAME, 
									testData.get(i).getValue());
					anonymousPartition.setParent(category);
					testData.set(i, anonymousPartition);
				}
			}
		}
	}
}
