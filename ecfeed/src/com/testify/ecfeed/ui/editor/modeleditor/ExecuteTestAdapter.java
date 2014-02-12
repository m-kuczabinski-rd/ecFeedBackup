package com.testify.ecfeed.ui.editor.modeleditor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.ParameterizedMethod;
import com.testify.ecfeed.runner.TestClassLoader;
import com.testify.ecfeed.ui.common.Messages;

public class ExecuteTestAdapter extends SelectionAdapter {
	private MethodNodeDetailsPage fPage;
	
	public ExecuteTestAdapter(MethodNodeDetailsPage page) {
		fPage = page;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void widgetSelected(SelectionEvent event){
		Class testClass = loadTestClass();
		Method testMethod = getTestMethod(testClass, fPage.getMethodNode());
		if(testMethod == null){
			new MessageDialog(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_MESSAGE(fPage.getMethodNode().toString()),
					MessageDialog.ERROR, 
					new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID).open();
		}
		Collection<TestCaseNode> selectedTestCases = getSelectedTestCases();
		ParameterizedMethod frameworkMethod = new ParameterizedMethod(testMethod, selectedTestCases);
		try {
			frameworkMethod.invokeExplosively(testClass.newInstance(), new Object[]{});
		} catch (Throwable e) {
			new MessageDialog(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_MESSAGE(fPage.getMethodNode().toString(), e.getMessage()),
					MessageDialog.ERROR, 
					new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID).open();
		} 
	}

	protected Collection<TestCaseNode> getSelectedTestCases() {
		Collection<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
		for(Object element : fPage.getTestCaseViewer().getCheckedElements()){
			if(element instanceof TestCaseNode){
				testCases.add((TestCaseNode)element);
			}
		}
		return testCases;
	}

	protected Method getTestMethod(Class<?> testClass, MethodNode methodModel) {
		for(Method method : testClass.getMethods()){
			if(isModel(method, methodModel) && hasTestAnnotation(method)){
				return method;
			}
		}
		return null;
	}

	private boolean hasTestAnnotation(Method method) {
		for(Annotation annotation : method.getAnnotations()){
			if(annotation instanceof Test){
				return true;
			}
		}
		return false;
	}

	protected boolean isModel(Method method, MethodNode methodModel) {
		String methodName = method.getName();
		List<String> argTypes = getArgTypes(method);
		return fPage.getMethodNode().getClassNode().getMethod(methodName, argTypes) == methodModel;
	}

	protected List<String> getArgTypes(Method method) {
		List<String> argTypes = new ArrayList<String>();
		for(Class<?> arg : method.getParameterTypes()){
			argTypes.add(arg.getSimpleName());
		}
		return argTypes;
	}

	@SuppressWarnings("rawtypes")
	protected Class loadTestClass() {
		Class testClass = null;
		ClassLoader parentLoader = this.getClass().getClassLoader();
		ClassNode classNode = fPage.getMethodNode().getClassNode();
		String className = classNode.getQualifiedName();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject project : projects){
			try {
				if(project.isOpen() && project.hasNature(JavaCore.NATURE_ID)){
					IPath path = getOuptutPath(project);
					TestClassLoader loader = new TestClassLoader(parentLoader, path.toString());
					testClass = loader.loadClass(className.toString());
				}
			}catch (ClassNotFoundException | CoreException e) {
			}
		}
		if(testClass == null){
			new MessageDialog(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_MESSAGE(className),
					MessageDialog.ERROR, 
					new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID).open();
		}
		return testClass;
	}

	protected IPath getOuptutPath(IProject project) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		IPath path = project.getWorkspace().getRoot().getLocation();
		path = path.append(javaProject.getOutputLocation());
		return path;
	}
}
