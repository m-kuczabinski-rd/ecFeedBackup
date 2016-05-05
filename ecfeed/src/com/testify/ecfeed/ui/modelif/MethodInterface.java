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

import com.testify.ecfeed.android.external.TestMethodInvokerExt;
import com.testify.ecfeed.android.utils.AndroidBaseRunnerHelper;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.adapter.operations.MethodOperationAddConstraint;
import com.testify.ecfeed.core.adapter.operations.MethodOperationAddParameter;
import com.testify.ecfeed.core.adapter.operations.MethodOperationAddTestCase;
import com.testify.ecfeed.core.adapter.operations.MethodOperationAddTestSuite;
import com.testify.ecfeed.core.adapter.operations.MethodOperationConvertTo;
import com.testify.ecfeed.core.adapter.operations.MethodOperationRenameTestCases;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.Constraint;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.StaticStatement;
import com.testify.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.core.runner.java.ExportTestMethodInvoker;
import com.testify.ecfeed.core.runner.java.JUnitTestMethodInvoker;
import com.testify.ecfeed.core.serialization.export.TestCasesExporter;
import com.testify.ecfeed.core.utils.EcException;
import com.testify.ecfeed.core.utils.StringHelper;
import com.testify.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.ui.dialogs.CalculateCoverageDialog;
import com.testify.ecfeed.ui.dialogs.TestCasesExportDialog;
import com.testify.ecfeed.ui.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.ui.dialogs.SelectCompatibleMethodDialog;
import com.testify.ecfeed.ui.dialogs.TestCasesExportDialog.FileCompositeVisibility;
import com.testify.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.testify.ecfeed.ui.dialogs.basic.InfoDialog;

public class MethodInterface extends ParametersParentInterface {

	private IFileInfoProvider fFileInfoProvider;
	private ITypeAdapterProvider fAdapterProvider;

	public MethodInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
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
		TestSuiteGenerationSupport testGenerationSupport = 
				new TestSuiteGenerationSupport(getTarget(), fFileInfoProvider);
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

	public void executeOnlineTests(IFileInfoProvider fileInfoProvider) throws EcException {
		ClassNode classNode = getTarget().getClassNode();

		if (!isValidClassConfiguration(classNode))
			return;

		OnlineTestRunningSupport runner = 
				new OnlineTestRunningSupport(
						createTestMethodInvoker(fileInfoProvider), fileInfoProvider, classNode.getRunOnAndroid());

		runner.setTarget(getTarget());
		runner.proceed();
	}

	public void executeOnlineExport(IFileInfoProvider fileInfoProvider) throws EcException {

		ClassNode classNode = getTarget().getClassNode();

		if (!isValidClassConfiguration(classNode))
			return;

		ExportTestMethodInvoker methodInvoker = new ExportTestMethodInvoker(getTarget());

		OnlineExportSupport exportSupport = 
				new OnlineExportSupport(methodInvoker, fileInfoProvider);

		exportSupport.setTargetForExport(getTarget());
		exportSupport.proceed();

		runExport(
				methodInvoker.getTestCasesToExport(), 
				exportSupport.getHeaderTemplate(), 
				exportSupport.getTestCaseTemplate(), 
				exportSupport.getFooterTemplate(), 
				exportSupport.getTargetFile());
	}

	public void executeStaticTests(Collection<TestCaseNode> testCases, IFileInfoProvider fileInfoProvider) throws EcException {
		ClassNode classNode = getTarget().getClassNode();

		if (!isValidClassConfiguration(classNode))
			return;

		StaticTestExecutionSupport support = 
				new StaticTestExecutionSupport(
						testCases, 
						createTestMethodInvoker(fileInfoProvider), 
						fileInfoProvider, 
						classNode.getRunOnAndroid());
		support.proceed();
	}

	public void exportTestCases(Collection<TestCaseNode> checkedTestCases) {

		TestCasesExportDialog dialog = 
				new TestCasesExportDialog(
						getTarget().getParametersCount(),
						FileCompositeVisibility.VISIBLE);

		if(dialog.open() != IDialogConstants.OK_ID){
			return;
		}

		runExport(
				checkedTestCases, 
				dialog.getHeaderTemplate(), dialog.getTestCaseTemplate(), dialog.getFooterTemplate(), 
				dialog.getTargetFile());
	}

	private void runExport(
			Collection<TestCaseNode> testCases, 
			String headerTemplate, String testCaseTemplate, String footerTemplate, 
			String targetFile) {

		try {
			TestCasesExporter exporter = 
					new TestCasesExporter(headerTemplate, testCaseTemplate, footerTemplate);

			exporter.runExport(getTarget(), testCases, targetFile);
		} catch (Exception e) {
			ErrorDialog.open(e.getMessage());
			return;
		}

		final String EXPORT_FINISHED = "Export finished.";
		InfoDialog.open(EXPORT_FINISHED);
	}


	private boolean isValidClassConfiguration(ClassNode classNode) {
		if (classNode.getRunOnAndroid() && emptyAndroidBaseRunner(classNode)) {
			MessageDialog.openError(
					Display.getDefault().getActiveShell(), 
					Messages.DIALOG_MISSING_ANDROID_RUNNER_TITLE,
					Messages.DIALOG_MISSING_ANDROID_RUNNER_INFO(classNode.getName()));
			return false;
		}

		return true;
	}

	private ITestMethodInvoker createTestMethodInvoker(IFileInfoProvider fileInfoProvider) throws EcException
	{
		ClassNode classNode = getTarget().getClassNode();

		if (!classNode.getRunOnAndroid()) {
			return new JUnitTestMethodInvoker();
		}

		String projectPath = new EclipseProjectHelper(fileInfoProvider).getProjectPath();
		String androidRunner = AndroidBaseRunnerHelper.createFullAndroidRunnerName(projectPath);

		return TestMethodInvokerExt.createInvoker(androidRunner);
	}

	private boolean emptyAndroidBaseRunner(ClassNode classNode) {
		String androidBaseRunner = classNode.getAndroidBaseRunner();
		return StringHelper.isNullOrEmpty(androidBaseRunner);
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

	public void openCoverageDialog(
			Object[] checkedElements, 
			Object[] grayedElements, 
			IFileInfoProvider fileInfoProvider) {
		Shell activeShell = Display.getDefault().getActiveShell();
		new CalculateCoverageDialog(activeShell, getTarget(), checkedElements, grayedElements, fileInfoProvider).open();
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
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
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
