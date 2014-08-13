package com.testify.ecfeed.ui.modelif;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

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
import com.testify.ecfeed.modelif.java.common.RemoveNodesOperation;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddConstraint;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddParameter;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddTestCase;
import com.testify.ecfeed.modelif.java.method.MethodOperationConvertTo;
import com.testify.ecfeed.modelif.java.method.MethodOperationRename;
import com.testify.ecfeed.modelif.java.method.MethodOperationRenameTestCases;
import com.testify.ecfeed.ui.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.ui.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

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

	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new MethodOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public boolean convertTo(MethodNode method, BasicSection source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationConvertTo(fTarget, method), source, updateListener, Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}
	
	public CategoryNode addNewParameter(BasicSection source, IModelUpdateListener updateListener) {
		EclipseModelBuilder modelBuilder = new EclipseModelBuilder();
		String name = generateNewParameterName(fTarget);
		String type = generateNewParameterType(fTarget);
		CategoryNode parameter = new CategoryNode(name, type, false);
		parameter.setDefaultValueString(modelBuilder.getDefaultExpectedValue(type));
		List<PartitionNode> defaultPartitions = modelBuilder.defaultPartitions(type);
		parameter.addPartitions(defaultPartitions);
		if(addNewParameter(parameter, source, updateListener)){
			return parameter;
		}
		return null;
	}
	
	public boolean addNewParameter(CategoryNode parameter, BasicSection source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationAddParameter(fTarget, parameter), source, updateListener, Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}
	
	public boolean removeParameters(Collection<CategoryNode> parameters, BasicSection source, IModelUpdateListener updateListener){
		Set<ConstraintNode> constraints = fTarget.mentioningConstraints(parameters);
		if(constraints.size() > 0 || fTarget.getTestCases().size() > 0){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_PARAMETERS_WARNING_TITLE, Messages.DIALOG_REMOVE_PARAMETERS_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return execute(new RemoveNodesOperation(parameters), source, updateListener, Messages.DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE);
	}

	public ConstraintNode addNewConstraint(BasicSection source, IModelUpdateListener updateListener){
		Constraint constraint = new Constraint(new StaticStatement(true), new StaticStatement(true));
		ConstraintNode node = new ConstraintNode(Constants.DEFAULT_NEW_CONSTRAINT_NAME, constraint);
		if(addNewConstraint(node, source, updateListener)){
			return node;
		}
		return null;
	}
	
	public boolean addNewConstraint(ConstraintNode constraint, BasicSection source, IModelUpdateListener updateListener){
		IModelOperation operation = new MethodOperationAddConstraint(fTarget, constraint, fTarget.getConstraintNodes().size());
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_CONSTRAINT_PROBLEM_TITLE);
	}
	
	public boolean removeConstraints(Collection<ConstraintNode> constraints, BasicSection source, IModelUpdateListener updateListener){
		return execute(new RemoveNodesOperation(constraints), source, updateListener, Messages.DIALOG_REMOVE_CONSTRAINTS_PROBLEM_TITLE);
	}
	
	public TestCaseNode addTestCase(BasicSection source, IModelUpdateListener updateListener) {
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

	public boolean addTestCase(TestCaseNode testCase, BasicSection source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationAddTestCase(fTarget, testCase, fTarget.getTestCases().size()), source, updateListener, Messages.DIALOG_ADD_TEST_CASE_PROBLEM_TITLE);
	}

	public void removeTestCases(Collection<TestCaseNode> testCases, BasicSection source, IModelUpdateListener updateListener) {
		execute(new RemoveNodesOperation(testCases), source, updateListener, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
	}

	public void renameSuite(BasicSection source, IModelUpdateListener updateListener) {
		RenameTestSuiteDialog dialog = 
				new RenameTestSuiteDialog(Display.getDefault().getActiveShell(), fTarget.getTestSuites());
		dialog.create();
		if (dialog.open() == Window.OK) {
			String oldName = dialog.getRenamedTestSuite();
			String newName = dialog.getNewName();
			renameSuite(oldName, newName, source, updateListener);
		}
	}

	public void renameSuite(String oldName, String newName, BasicSection source, IModelUpdateListener updateListener) {
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
	

	
}
