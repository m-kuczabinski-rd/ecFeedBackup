/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.external.EclipseMethodImplementHelper;
import com.testify.ecfeed.ui.common.external.IMethodImplementHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseMethodImplementer {

	private final IFileInfoProvider fFileInfoProvider;
	private final IMethodImplementHelper fMethodImplementHelper;
	private final MethodNode fMethodNode;

	EclipseMethodImplementer(
			final IFileInfoProvider fileInfoProvider,
			final MethodNode methodNode
			) throws JavaModelException, CoreException {
		fFileInfoProvider = fileInfoProvider;
		fMethodNode = methodNode;

		final String className = JavaUtils.getQualifiedName(methodNode.getClassNode());
		fMethodImplementHelper = new EclipseMethodImplementHelper(fileInfoProvider, className);
	}

	public void implementMethodDefinition() throws CoreException, EcException {
		fMethodImplementHelper.createMethod(methodDefinitionContent(fMethodNode));
		createImportsForUserParams(fMethodNode);
		fMethodImplementHelper.commitChanges();
	}

	private void createImportsForUserParams(MethodNode methodNode) throws JavaModelException {
		for(AbstractParameterNode parameter : methodNode.getParameters()){
			final String type = parameter.getType();
			if(JavaUtils.isUserType(type)){
				final String packageName = JavaUtils.getPackageName(type);
				if(packageName.equals(JavaUtils.getPackageName(methodNode.getClassNode())) == false){
					fMethodImplementHelper.createImport(type);
				}
			}
		}
	}

	public boolean methodDefinitionImplemented() {
		try{
			final IType type = getJavaProject().findType(fMethodNode.getClassNode().getName());
			if(type == null){
				return false;
			}
			final EclipseModelBuilder builder = new EclipseModelBuilder();
			for(IMethod method : type.getMethods()){

				final MethodNode model = builder.buildMethodModel(method);
				if (model != null 
						&& model.getName().equals(fMethodNode.getName()) 
						&& model.getParametersTypes().equals(fMethodNode.getParametersTypes())){
					return true;
				}
			}
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private String methodDefinitionContent(final MethodNode node){
		String methodSignature = "public void " + node.getName() + "(" + getMethodArgs(node) +")"; 

		String methodBody =	
				" {\n"+ 
						"\t" + "// TODO Auto-generated method stub" + "\n" + 
						"\t" + createLoggingInstruction(node) + "\n"+ 
						"}";

		return methodSignature + methodBody;
	}

	private String createLoggingInstruction(final MethodNode methodNode) {
		String result = "";

		if (methodNode.getRunOnAndroid()) {
			result = "android.util.Log.d(\"ecFeed\", \"" + methodNode.getName() + "(";
		} else {
			result = "System.out.println(\"" + methodNode.getName() + "(";
		}

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		if(parameters.size() == 0) {
			return result + ")\");";
		}

		result +=  "\" + ";
		for(int index = 0; index < parameters.size(); ++index) {
			result += parameters.get(index).getName();
			if(index != parameters.size() - 1) {
				result += " + \", \"";
			}
			result += " + ";
		}

		return result + "\")\");"; 
	}

	private String getMethodArgs(final MethodNode node) {
		List<AbstractParameterNode> parameters = node.getParameters();

		if(parameters.size() == 0) {
			return new String();
		}

		String args = "";

		for(int i = 0; i < parameters.size(); ++i) {
			AbstractParameterNode param = parameters.get(i);
			args += JavaUtils.getLocalName(param.getType()) + " " + param.getName();
			if(i != parameters.size() - 1){
				args += ", ";
			}
		}
		return args;
	}

	private IJavaProject getJavaProject() throws CoreException{
		if(fFileInfoProvider.getProject().hasNature(JavaCore.NATURE_ID)){
			return JavaCore.create(fFileInfoProvider.getProject());
		}
		return null;
	}	
}
