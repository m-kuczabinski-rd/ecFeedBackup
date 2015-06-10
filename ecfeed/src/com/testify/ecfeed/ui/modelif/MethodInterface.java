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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.operations.MethodOperationAddConstraint;
import com.testify.ecfeed.adapter.operations.MethodOperationAddParameter;
import com.testify.ecfeed.adapter.operations.MethodOperationAddTestCase;
import com.testify.ecfeed.adapter.operations.MethodOperationAddTestSuite;
import com.testify.ecfeed.adapter.operations.MethodOperationConvertTo;
import com.testify.ecfeed.adapter.operations.MethodOperationRenameTestCases;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.java.AndroidTestMethodInvoker;
import com.testify.ecfeed.runner.java.TestMethodInvoker;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.ui.dialogs.CalculateCoverageDialog;
import com.testify.ecfeed.ui.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.ui.dialogs.SelectCompatibleMethodDialog;

public class MethodInterface extends ParametersParentInterface {

	private ITypeAdapterProvider fAdapterProvider;

	public MethodInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	public List<String> getArgTypes(MethodNode method) {
		return JavaUtils.getArgTypes(method);
	}

	public List<String> getArgNames(MethodNode method) {
		return JavaUtils.getArgNames(method);
	}

	public boolean convertTo(MethodNode method) {
		return execute(new MethodOperationConvertTo(getTarget(), method), Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}

	@Override
	public MethodParameterNode addNewParameter() {
		EclipseModelBuilder modelBuilder = new EclipseModelBuilder();
		String name = generateNewParameterName();
		String type = generateNewParameterType();
		String defaultValue = modelBuilder.getDefaultExpectedValue(type);
		MethodParameterNode parameter = new MethodParameterNode(name, type, defaultValue, false);
		List<ChoiceNode> defaultChoices = modelBuilder.defaultChoices(type);
		parameter.addChoices(defaultChoices);
		if(addParameter(parameter, getTarget().getParameters().size())){
			return parameter;
		}
		return null;
	}

	public boolean addParameter(MethodParameterNode parameter, int index) {
		return execute(new MethodOperationAddParameter(getTarget(), parameter, index), Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}

	public boolean removeMethodParameters(Collection<MethodParameterNode> parameters){
		Set<ConstraintNode> constraints = getTarget().mentioningConstraints(parameters);
		if(constraints.size() > 0 || getTarget().getTestCases().size() > 0){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_REMOVE_PARAMETERS_WARNING_TITLE, Messages.DIALOG_REMOVE_PARAMETERS_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return super.removeParameters(parameters);
	}

	public ConstraintNode addNewConstraint(){
		Constraint constraint = new Constraint(new StaticStatement(true), new StaticStatement(true));
		ConstraintNode node = new ConstraintNode(Constants.DEFAULT_NEW_CONSTRAINT_NAME, constraint);
		if(addNewConstraint(node)){
			return node;
		}
		return null;
	}

	public boolean addNewConstraint(ConstraintNode constraint){
		IModelOperation operation = new MethodOperationAddConstraint(getTarget(), constraint, getTarget().getConstraintNodes().size());
		return execute(operation, Messages.DIALOG_ADD_CONSTRAINT_PROBLEM_TITLE);
	}

	public boolean removeConstraints(Collection<ConstraintNode> constraints){
		return removeChildren(constraints, Messages.DIALOG_REMOVE_CONSTRAINTS_PROBLEM_TITLE);
	}

	public TestCaseNode addTestCase() {
		for(MethodParameterNode parameter : getTarget().getMethodParameters()){
			if(!parameter.isExpected() && parameter.getChoices().isEmpty()){
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.DIALOG_ADD_TEST_CASE_PROBLEM_TITLE, Messages.DIALOG_TEST_CASE_WITH_EMPTY_CATEGORY_MESSAGE);
				return null;
			}
		}

		AddTestCaseDialog dialog = new AddTestCaseDialog(Display.getCurrent().getActiveShell(), getTarget());
		dialog.create();
		if (dialog.open() == IDialogConstants.OK_ID) {
			String testSuite = dialog.getTestSuite();
			List<ChoiceNode> testData = dialog.getTestData();
			TestCaseNode testCase = new TestCaseNode(testSuite, testData);
			if(addTestCase(testCase)){
				return testCase;
			}
		}
		return null;
	}

	public boolean addTestCase(TestCaseNode testCase) {
		return execute(new MethodOperationAddTestCase(getTarget(), testCase, fAdapterProvider, getTarget().getTestCases().size()), Messages.DIALOG_ADD_TEST_CASE_PROBLEM_TITLE);
	}

	public boolean removeTestCases(Collection<TestCaseNode> testCases) {
		return removeChildren(testCases, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
	}

	public void renameSuite() {
		RenameTestSuiteDialog dialog =
				new RenameTestSuiteDialog(Display.getDefault().getActiveShell(), getTarget().getTestSuites());
		dialog.create();
		if (dialog.open() == Window.OK) {
			String oldName = dialog.getRenamedTestSuite();
			String newName = dialog.getNewName();
			renameSuite(oldName, newName);
		}
	}

	public void renameSuite(String oldName, String newName) {
		try{
			execute(new MethodOperationRenameTestCases(getTarget().getTestCases(oldName), newName),
					Messages.DIALOG_RENAME_TEST_SUITE_PROBLEM);
		}
		catch(ModelOperationException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_RENAME_TEST_SUITE_PROBLEM,
					e.getMessage());
		}
	}

	public boolean generateTestSuite(){
		TestSuiteGenerationSupport testGenerationSupport = new TestSuiteGenerationSupport(getTarget());
		testGenerationSupport.proceed();
		if(testGenerationSupport.hasData() == false) return false;

		String testSuiteName = testGenerationSupport.getTestSuiteName();
		List<List<ChoiceNode>> testData = testGenerationSupport.getGeneratedData();

		int dataLength = testData.size();
		if(dataLength < 0 && (testGenerationSupport.wasCancelled() == false)){
			MessageDialog.openInformation(Display.getDefault().getActiveShell(),
			Messages.DIALOG_ADD_TEST_SUITE_PROBLEM_TITLE,
			Messages.DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE);
			return false;
		}
		if(testData.size() > Constants.TEST_SUITE_SIZE_WARNING_LIMIT){
			if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
					Messages.DIALOG_LARGE_TEST_SUITE_GENERATED_TITLE,
					Messages.DIALOG_LARGE_TEST_SUITE_GENERATED_MESSAGE(dataLength)) == false){
				return false;
			}
		}
		IModelOperation operation = new MethodOperationAddTestSuite(getTarget(), testSuiteName, testData, fAdapterProvider);
		return execute(operation, Messages.DIALOG_ADD_TEST_SUITE_PROBLEM_TITLE);
	}

	public void executeOnlineTests() {
		OnlineTestRunningSupport runner 
			= new OnlineTestRunningSupport(createTestMethodInvoker());
		runner.setTarget(getTarget());
		runner.proceed();
	}

	public void executeStaticTests(Collection<TestCaseNode> testCases) {
		StaticTestExecutionSupport support 
			= new StaticTestExecutionSupport(testCases, createTestMethodInvoker());
		support.proceed();
	}
	
	private TestMethodInvoker createTestMethodInvoker()
	{
		// TODO - XYX if android then create AndroidTestMethodInvoker else JunitTestMethodInvoker
		// return new JUnitTestMethodInvoker();
		return new AndroidTestMethodInvoker();
	}

	public Collection<TestCaseNode> getTestCases(String testSuite){
		return getTarget().getTestCases(testSuite);
	}

	public Collection<String> getTestSuites() {
		return getTarget().getTestSuites();
	}

	public Collection<TestCaseNode> getTestCases() {
		return getTarget().getTestCases();
	}

	public void reassignTarget() {
		SelectCompatibleMethodDialog dialog = new SelectCompatibleMethodDialog(Display.getDefault().getActiveShell(), getCompatibleMethods());
		if(dialog.open() == IDialogConstants.OK_ID){
			MethodNode selectedMethod = dialog.getSelectedMethod();
			convertTo(selectedMethod);
		}
	}

	public List<MethodNode> getCompatibleMethods(){
		List<MethodNode> compatibleMethods = new ArrayList<MethodNode>();
		for(MethodNode m : ClassInterface.getOtherMethods(getTarget().getClassNode())){
			if(m.getParametersTypes().equals(getTarget().getParametersTypes())){
				compatibleMethods.add(m);
			}
		}
		return compatibleMethods;
	}

	public void openCoverageDialog(Object[] checkedElements, Object[] grayedElements) {
		Shell activeShell = Display.getDefault().getActiveShell();
		new CalculateCoverageDialog(activeShell, getTarget(), checkedElements, grayedElements).open();
	}

	public List<GlobalParameterNode> getAvailableGlobalParameters() {
		return getTarget().getAvailableGlobalParameters();
	}

	@Override
	public void goToImplementation(){
		IMethod method = JavaModelAnalyser.getIMethod(getTarget());
		if(method != null){
			try{
				JavaUI.openInEditor(method);
			}catch(Exception e){}
		}
	}

	@Override
	protected MethodNode getTarget(){
		return (MethodNode)super.getTarget();
	}

	@Override
	protected String generateNewParameterType() {
		for(String type : JavaUtils.supportedPrimitiveTypes()){
			List<String> newTypes = getTarget().getParametersTypes();
			newTypes.add(type);
			if(getTarget().getClassNode().getMethod(getTarget().getName(), newTypes) == null){
				return type;
			}
		}
		String type = Constants.DEFAULT_USER_TYPE_NAME;
		int i = 0;
		while(true){
			List<String> newTypes = getTarget().getParametersTypes();
			newTypes.add(type);
			if(getTarget().getClassNode().getMethod(getTarget().getName(), newTypes) == null){
				break;
			}
			else{
				type = Constants.DEFAULT_USER_TYPE_NAME + i++;
			}
		}
		return type;
	}
}
