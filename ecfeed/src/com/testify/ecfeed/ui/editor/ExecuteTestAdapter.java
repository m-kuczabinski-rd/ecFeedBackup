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
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.ClassUtils;

public abstract class ExecuteTestAdapter extends SelectionAdapter {

	protected Class<?> loadTestClass() {
		Class<?> testClass = null;
		ClassNode classNode = getMethodModel().getClassNode();
		String className = classNode.getQualifiedName();
		URLClassLoader loader;
		try {
			loader = ClassUtils.getClassLoader(true, null);
			testClass = loader.loadClass(className.toString());
			Method[] methods = testClass.getMethods();
			for (Method method : methods){
				Class<?>[] parameters = method.getParameterTypes();
				for (Class<?> parameter : parameters){
					if (parameter.isEnum()){
						loader.loadClass(parameter.getName());
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_TITLE,
					Messages.DIALOG_COULDNT_LOAD_TEST_CLASS_MESSAGE(className));
		}
		return testClass;
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
			if (arg.isEnum()) {
				argTypes.add(arg.getCanonicalName());				
			} else {
				argTypes.add(arg.getSimpleName());	
			}
		}
		return argTypes;
	}

	protected boolean isModel(Method method, MethodNode methodModel) {
		String methodName = method.getName();
		List<String> argTypes = getArgTypes(method);
		boolean result = getMethodModel().getClassNode().getMethod(methodName, argTypes) == methodModel; 
		return result;
	}
	
	protected abstract MethodNode getMethodModel();
}
