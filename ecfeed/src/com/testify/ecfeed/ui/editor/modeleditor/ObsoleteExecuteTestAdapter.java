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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.Messages;

public class ObsoleteExecuteTestAdapter extends SelectionAdapter {
	private ObsoleteMethodNodeDetailsPage fPage;
	
	public ObsoleteExecuteTestAdapter(ObsoleteMethodNodeDetailsPage page) {
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
		ClassNode classNode = fPage.getSelectedMethod().getClassNode();
		String className = classNode.getQualifiedName();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<URL> urls = new ArrayList<URL>();
			try {
				for(IProject project : projects){
					if(project.isOpen() && project.hasNature(JavaCore.NATURE_ID)){
						IPath path = getOuptutPath(project);
						URL classUrl = getClassUrl(path, className);
						urls.add(classUrl);
					}
				}
				URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[]{}));
				testClass = loader.loadClass(className.toString());
				loader.close();
			}catch (ClassNotFoundException | CoreException | IOException e) {
			}
		if(testClass == null){
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_TITLE, 
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_MESSAGE(className));
		}
		return testClass;
	}

	private URL getClassUrl(IPath path, String className) {
		String localPath = className;
		localPath = localPath.replaceAll("\\.", "/");
		localPath = localPath.substring(0, localPath.lastIndexOf('/'));
		String urlString = path.toOSString() + "/";
		try {
			URL url = new URL("file", "", urlString);
			return url;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	protected Method getTestMethod(Class<?> testClass, MethodNode methodModel) throws InvocationTargetException {
		for(Method method : testClass.getMethods()){
			if(isModel(method, methodModel)){
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
		boolean result = fPage.getSelectedMethod().getClassNode().getMethod(methodName, argTypes) == methodModel; 
		return result;
	}

	protected ObsoleteMethodNodeDetailsPage getPage(){
		return fPage;
	}
}
