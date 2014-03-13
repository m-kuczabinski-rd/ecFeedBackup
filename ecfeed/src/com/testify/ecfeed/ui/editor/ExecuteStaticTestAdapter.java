package com.testify.ecfeed.ui.editor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.ParameterizedMethod;
import com.testify.ecfeed.ui.common.Messages;

public class ExecuteStaticTestAdapter extends ExecuteTestAdapter {

	private TestCasesViewer fViewerSection;

	public ExecuteStaticTestAdapter(TestCasesViewer viewerSection) {
		fViewerSection = viewerSection;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void widgetSelected(SelectionEvent event){
		try {
			Class testClass = loadTestClass();
			Method testMethod = getTestMethod(testClass, getMethodModel());
			if(testMethod == null){
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_TITLE, 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_MESSAGE(getMethodModel().toString()));
			}
			Collection<TestCaseNode> selectedTestCases = getSelectedTestCases();
			ParameterizedMethod frameworkMethod = new ParameterizedMethod(testMethod, selectedTestCases);
			frameworkMethod.invokeExplosively(testClass.newInstance(), new Object[]{});
		} catch (Throwable e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
				Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_TITLE, 
				Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_MESSAGE(getMethodModel().toString(), e.getMessage()));
		} 
	}

	protected Collection<TestCaseNode> getSelectedTestCases() {
		Collection<TestCaseNode> testCases = new HashSet<TestCaseNode>();
		for(Object element : fViewerSection.getCheckedElements()){
			if(element instanceof TestCaseNode){
				testCases.add((TestCaseNode)element);
			}
			else if(element instanceof String && !fViewerSection.getCheckboxViewer().getGrayed(element)){
				testCases.addAll(getMethodModel().getTestCases((String)element));
			}
		}
		return testCases;
	}

	@Override
	protected MethodNode getMethodModel() {
		return fViewerSection.getSelectedMethod();
	}
}
