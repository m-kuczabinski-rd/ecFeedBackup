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

package com.testify.ecfeed.ui.editor.modeleditor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.runner.TestClassLoader;
import com.testify.ecfeed.ui.common.Messages;

public class ExecuteTestAdapter extends SelectionAdapter {
	private MethodNodeDetailsPage fPage;
	
	public ExecuteTestAdapter(MethodNodeDetailsPage page) {
		fPage = page;
	}

	protected IPath getOuptutPath(IProject project) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		IPath path = project.getWorkspace().getRoot().getLocation();
		path = path.append(javaProject.getOutputLocation());
		return path;
	}

	protected Class<?> loadTestClass() {
		Class<?> testClass = null;
		ClassLoader parentLoader = this.getClass().getClassLoader();
		ClassNode classNode = fPage.getSelectedMethod().getClassNode();
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
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_TITLE, 
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_MESSAGE(className));
		}
		return testClass;
	}

	protected Method getTestMethod(Class<?> testClass, MethodNode methodModel) throws InvocationTargetException {
		for(Method method : testClass.getMethods()){
			if(isModel(method, methodModel) && hasTestAnnotation(method)){
				return method;
			}
		}
		throw new InvocationTargetException(null, "Method " + methodModel.toString() + " does not exist in loaded test class");
	}

	protected List<String> getArgTypes(Method method) {
		List<String> argTypes = new ArrayList<String>();
		for(Class<?> arg : method.getParameterTypes()){
			argTypes.add(arg.getSimpleName());
		}
		return argTypes;
	}

	protected boolean isModel(Method method, MethodNode methodModel) {
		String methodName = method.getName();
		List<String> argTypes = getArgTypes(method);
		return fPage.getSelectedMethod().getClassNode().getMethod(methodName, argTypes) == methodModel;
	}

	protected MethodNodeDetailsPage getPage(){
		return fPage;
	}
	
	private boolean hasTestAnnotation(Method method) {
		for(Annotation annotation : method.getAnnotations()){
			if(annotation instanceof Test){
				return true;
			}
		}
		return false;
	}
}
