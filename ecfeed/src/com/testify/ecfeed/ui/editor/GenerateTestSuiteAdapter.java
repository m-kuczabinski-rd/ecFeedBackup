/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.GenerateTestSuiteDialog;

class GenerateTestSuiteAdapter extends SelectionAdapter{

	private boolean fCanceled;
	private TestCasesViewer fViewerSection;
	private Collection<Constraint> fSelectedConstraints;

	private class GeneratorRunnable implements IRunnableWithProgress{

		private IGenerator<PartitionNode> fGenerator;
		private List<List<PartitionNode>> fGeneratedData;
		private List<List<PartitionNode>> fInput;
		private Collection<IConstraint<PartitionNode>> fConstraints;
		private Map<String, Object> fParameters;

		GeneratorRunnable(IGenerator<PartitionNode> generator, 
				List<List<PartitionNode>> input, 
				Collection<IConstraint<PartitionNode>> constraints, 
				Map<String, Object> parameters, 
				List<List<PartitionNode>> generated){
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
			fGeneratedData = generated;
		}
		
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			List<PartitionNode> next;
			try {
				fGenerator.initialize(fInput, fConstraints, fParameters);
				monitor.beginTask("Generating test data", fGenerator.totalWork());
				while((next = fGenerator.next()) != null && monitor.isCanceled() == false){
					fGeneratedData.add(next);
					monitor.worked(fGenerator.workProgress());
				}
				monitor.done();
			} catch (GeneratorException e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}
		
	}
	
	GenerateTestSuiteAdapter(TestCasesViewer viewerSection) {
		fViewerSection = viewerSection;
	}
	
	private MethodNode getSelectedMethod(){
		return fViewerSection.getSelectedMethod();
	}

	@Override
	public void widgetSelected(SelectionEvent e){
		GenerateTestSuiteDialog dialog = 
				new GenerateTestSuiteDialog(getActiveShell(), getSelectedMethod());
		if(dialog.open() == IDialogConstants.OK_ID){
			IGenerator<PartitionNode> selectedGenerator = dialog.getSelectedGenerator();
			List<List<PartitionNode>> algorithmInput = dialog.getAlgorithmInput();
			fSelectedConstraints = dialog.getConstraints();

			List<IConstraint<PartitionNode>> constraints = new ArrayList<IConstraint<PartitionNode>>();
			for(Constraint constraint : fSelectedConstraints){
				constraints.add(constraint);
			}
			
			String testSuiteName = dialog.getTestSuiteName();
			Map<String, Object> parameters = dialog.getGeneratorParameters();
			
			List<List<PartitionNode>> generatedData = generateTestData(selectedGenerator, algorithmInput, constraints, parameters);
			addGeneratedDataToModel(testSuiteName, generatedData);
		}
	}

	private Shell getActiveShell() {
		return Display.getCurrent().getActiveShell();
	}

	private List<List<PartitionNode>> generateTestData(final IGenerator<PartitionNode> generator, 
			final List<List<PartitionNode>> input, 
			final Collection<IConstraint<PartitionNode>> constraints,
			final Map<String, Object> parameters) {

		ProgressMonitorDialog progressDialog = 
				new ProgressMonitorDialog(getActiveShell());
		List<List<PartitionNode>> generated = new ArrayList<List<PartitionNode>>();
		fCanceled = false;
		try {
			GeneratorRunnable runnable = new GeneratorRunnable(generator, input, constraints, parameters, generated);
			progressDialog.open();
			progressDialog.run(true, true, runnable);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Exception", e.getMessage());
			fCanceled = true;
		}catch (InterruptedException e) {
			fCanceled = true;
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Exception", e.getMessage());
			e.printStackTrace();
		}
		fCanceled |= progressDialog.getProgressMonitor().isCanceled();
		if(!fCanceled){
			return generated;
		}
		else{
			//return empty set if the operation was canceled
			//TODO add a decision dialog where user may choose whether to add generated data
			return new ArrayList<List<PartitionNode>>();
		}
	}

	private void addGeneratedDataToModel(String testSuiteName, 
			List<List<PartitionNode>> generatedData) {
		int dataLength = generatedData.size();
		if(dataLength > 0){
			if(generatedData.size() > Constants.TEST_SUITE_SIZE_WARNING_LIMIT){
				if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
						Messages.DIALOG_LARGE_TEST_SUITE_GENERATED_TITLE,
						Messages.DIALOG_LARGE_TEST_SUITE_GENERATED_MESSAGE(dataLength)) == false){
					return;
				}
			}
			addTestSuiteToModel(testSuiteName, generatedData);
		}
		else if (!fCanceled){
			MessageDialog.openInformation(Display.getDefault().getActiveShell(),
					Messages.DIALOG_EMPTY_TEST_SUITE_GENERATED_TITLE,
					Messages.DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE);
		}
	}

	private void addTestSuiteToModel(String testSuiteName, List<List<PartitionNode>> generatedData) {
		List<TestCaseNode> testSuite = new ArrayList<TestCaseNode>();
		for(List<PartitionNode> testCase : generatedData){
			List<PartitionNode> testData = (List<PartitionNode>)testCase;
			TestCaseNode testCaseNode = new TestCaseNode(testSuiteName, testData);
			testSuite.add(testCaseNode);
		}
		replaceExpectedValues(testSuite);
		for(TestCaseNode testCase : testSuite){
			getSelectedMethod().addTestCase(testCase);
		}
		fViewerSection.modelUpdated();
	}

	private void replaceExpectedValues(List<TestCaseNode> testSuite) {
		if(getSelectedMethod().getExpectedCategoriesNames().size() == 0){
			return;
		}
		//replace expected values partitions with anonymous ones
		for(TestCaseNode testCase : testSuite){
			List<PartitionNode> testData = testCase.getTestData();
			for(int i = 0; i < testData.size(); i++){
				AbstractCategoryNode category = testData.get(i).getCategory();
				if(category instanceof ExpectedCategoryNode){
					PartitionNode anonymousPartition = testData.get(i).getCopy();
					anonymousPartition.setParent(category);
					testData.set(i, anonymousPartition);
				}
			}
		}
	}
}
