package com.testify.ecfeed.ui.modelif;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaMethodUtils;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddConstraint;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddParameter;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddTestCase;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddTestSuite;
import com.testify.ecfeed.modelif.java.method.MethodOperationConvertTo;
import com.testify.ecfeed.modelif.java.method.MethodOperationRename;
import com.testify.ecfeed.modelif.java.method.MethodOperationRenameTestCases;
import com.testify.ecfeed.runner.JavaTestRunner;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.ui.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.ui.editor.TestSuiteGenerationSupport;

public class MethodInterface extends GenericNodeInterface {

	private MethodNode fTarget;

	public MethodInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	public void setTarget(MethodNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public MethodNode getTarget(){
		return fTarget;
	}

	public List<String> getArgTypes(MethodNode method) {
		return JavaMethodUtils.getArgTypes(method);
	}

	public List<String> getArgNames(MethodNode method) {
		return JavaMethodUtils.getArgNames(method);
	}

	public boolean setName(String newName, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new MethodOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public boolean convertTo(MethodNode method, AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationConvertTo(fTarget, method), source, updateListener, Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}
	
	public CategoryNode addNewParameter(AbstractFormPart source, IModelUpdateListener updateListener) {
		EclipseModelBuilder modelBuilder = new EclipseModelBuilder();
		String name = generateNewParameterName(fTarget);
		String type = generateNewParameterType(fTarget);
		String defaultValue = modelBuilder.getDefaultExpectedValue(type);
		CategoryNode parameter = new CategoryNode(name, type, defaultValue, false);
		List<PartitionNode> defaultPartitions = modelBuilder.defaultPartitions(type);
		parameter.addPartitions(defaultPartitions);
		if(addNewParameter(parameter, fTarget.getCategories().size(), source, updateListener)){
			return parameter;
		}
		return null;
	}
	
	public boolean addNewParameter(CategoryNode parameter, int index, AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationAddParameter(fTarget, parameter, index), source, updateListener, Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}
	
	public boolean removeParameters(Collection<CategoryNode> parameters, AbstractFormPart source, IModelUpdateListener updateListener){
		Set<ConstraintNode> constraints = fTarget.mentioningConstraints(parameters);
		if(constraints.size() > 0 || fTarget.getTestCases().size() > 0){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_PARAMETERS_WARNING_TITLE, Messages.DIALOG_REMOVE_PARAMETERS_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return super.removeChildren(parameters, source, updateListener, Messages.DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE);
	}

	public ConstraintNode addNewConstraint(AbstractFormPart source, IModelUpdateListener updateListener){
		Constraint constraint = new Constraint(new StaticStatement(true), new StaticStatement(true));
		ConstraintNode node = new ConstraintNode(Constants.DEFAULT_NEW_CONSTRAINT_NAME, constraint);
		if(addNewConstraint(node, source, updateListener)){
			return node;
		}
		return null;
	}
	
	public boolean addNewConstraint(ConstraintNode constraint, AbstractFormPart source, IModelUpdateListener updateListener){
		IModelOperation operation = new MethodOperationAddConstraint(fTarget, constraint, fTarget.getConstraintNodes().size());
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_CONSTRAINT_PROBLEM_TITLE);
	}
	
	public boolean removeConstraints(Collection<ConstraintNode> constraints, AbstractFormPart source, IModelUpdateListener updateListener){
		return removeChildren(constraints, source, updateListener, Messages.DIALOG_REMOVE_CONSTRAINTS_PROBLEM_TITLE);
	}
	
	public TestCaseNode addTestCase(AbstractFormPart source, IModelUpdateListener updateListener) {
		for(CategoryNode category : fTarget.getCategories()){
			if(!category.isExpected() && category.getPartitions().isEmpty()){
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.DIALOG_ADD_TEST_CASE_PROBLEM_TITLE, Messages.DIALOG_TEST_CASE_WITH_EMPTY_CATEGORY_MESSAGE);
				return null;
			}
		}
		
		AddTestCaseDialog dialog = new AddTestCaseDialog(Display.getCurrent().getActiveShell(), fTarget);
		dialog.create();
		if (dialog.open() == IDialogConstants.OK_ID) {
			String testSuite = dialog.getTestSuite();
			List<PartitionNode> testData = dialog.getTestData();
			TestCaseNode testCase = new TestCaseNode(testSuite, testData);
			if(addTestCase(testCase, source, updateListener)){
				return testCase;
			}
		}
		return null;
	}

	public boolean addTestCase(TestCaseNode testCase, AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationAddTestCase(fTarget, testCase, fTarget.getTestCases().size()), source, updateListener, Messages.DIALOG_ADD_TEST_CASE_PROBLEM_TITLE);
	}

	public boolean removeTestCases(Collection<TestCaseNode> testCases, AbstractFormPart source, IModelUpdateListener updateListener) {
		return removeChildren(testCases, source, updateListener, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
	}
	
	public void renameSuite(AbstractFormPart source, IModelUpdateListener updateListener) {
		RenameTestSuiteDialog dialog = 
				new RenameTestSuiteDialog(Display.getDefault().getActiveShell(), fTarget.getTestSuites());
		dialog.create();
		if (dialog.open() == Window.OK) {
			String oldName = dialog.getRenamedTestSuite();
			String newName = dialog.getNewName();
			renameSuite(oldName, newName, source, updateListener);
		}
	}

	public void renameSuite(String oldName, String newName, AbstractFormPart source, IModelUpdateListener updateListener) {
		try{
			execute(new MethodOperationRenameTestCases(fTarget.getTestCases(oldName), newName), 
					source, updateListener, Messages.DIALOG_RENAME_TEST_SUITE_PROBLEM);
		}
		catch(ModelIfException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_RENAME_TEST_SUITE_PROBLEM, 
					e.getMessage());
		}
	}

	public boolean generateTestSuite(AbstractFormPart source, IModelUpdateListener updateListener){
		TestSuiteGenerationSupport testGenerationSupport = new TestSuiteGenerationSupport(fTarget);
		testGenerationSupport.proceed();
		if(testGenerationSupport.hasData() == false) return false;
		
		String testSuiteName = testGenerationSupport.getTestSuiteName();
		List<List<PartitionNode>> testData = testGenerationSupport.getGeneratedData();
		
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
		return execute(new MethodOperationAddTestSuite(fTarget, testSuiteName, testData), source, updateListener, Messages.DIALOG_ADD_TEST_SUITE_PROBLEM_TITLE);
	}
	
	private String generateNewParameterName(MethodNode method) {
		int i = 0;
		String name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		while(method.getCategory(name) != null){
			name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}
		return name;
	}

	private String generateNewParameterType(MethodNode method) {
		for(String type : JavaUtils.supportedPrimitiveTypes()){
			List<String> newTypes = method.getCategoriesTypes();
			newTypes.add(type);
			if(method.getClassNode().getMethod(method.getName(), newTypes) == null){
				return type;
			}
		}
		String type = Constants.DEFAULT_USER_TYPE_NAME;
		int i = 0;
		while(true){
			List<String> newTypes = method.getCategoriesTypes();
			newTypes.add(type);
			if(method.getClassNode().getMethod(method.getName(), newTypes) == null){
				break;
			}
			else{
				type = Constants.DEFAULT_USER_TYPE_NAME + i++;
			}
		}
		return type;
	}

	public void executeOnlineTests() {
		OnlineTestRunningSupport runner = new OnlineTestRunningSupport();
		runner.setTarget(fTarget);
		runner.proceed();
	}

	public void executeStaticTests(Collection<TestCaseNode> testCases) {
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());
		JavaTestRunner runner = new JavaTestRunner(EclipseLoaderProvider.createLoader());
		try {
			runner.setTarget(fTarget);
			for(TestCaseNode testCase : testCases){
				runner.runTestCase(testCase.getTestData());
			}
		} catch (RunnerException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
	}
	
	public Collection<TestCaseNode> getTestCases(String testSuite){
		return fTarget.getTestCases(testSuite);
	}

	public Collection<String> getTestSuites() {
		return fTarget.getTestSuites();
	}

	public Collection<TestCaseNode> getTestCases() {
		return fTarget.getTestCases();
	}
}
