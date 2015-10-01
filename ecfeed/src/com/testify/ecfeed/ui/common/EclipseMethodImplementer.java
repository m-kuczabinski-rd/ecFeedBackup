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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseMethodImplementer {

	private final IFileInfoProvider fFileInfoProvider;

	EclipseMethodImplementer(final IFileInfoProvider fileInfoProvider) {
		fFileInfoProvider = fileInfoProvider;
	}

	public void implementMethodDefinition(MethodNode node) throws CoreException, EcException {
		IType classType = getJavaProject().findType(JavaUtils.getQualifiedName(node.getClassNode()));
		if(classType != null){
			classType.createMethod(methodDefinitionContent(node), null, false, null);
			for(AbstractParameterNode parameter : node.getParameters()){
				String type = parameter.getType();
				if(JavaUtils.isUserType(type)){
					String packageName = JavaUtils.getPackageName(type);
					if(packageName.equals(JavaUtils.getPackageName(node.getClassNode())) == false){
						classType.getCompilationUnit().createImport(type, null, null);
					}
				}
			}
		}
		ICompilationUnit unit = classType.getCompilationUnit();
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	public boolean methodDefinitionImplemented(MethodNode node) {
		try{
			IType type = getJavaProject().findType(node.getClassNode().getName());
			if(type == null){
				return false;
			}
			EclipseModelBuilder builder = new EclipseModelBuilder();
			for(IMethod method : type.getMethods()){
				MethodNode model = builder.buildMethodModel(method);
				if(model != null && model.getName().equals(node.getName()) && model.getParametersTypes().equals(node.getParametersTypes())){
					return true;
				}
			}
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private String methodDefinitionContent(MethodNode node){
		String methodSignature = "public void " + node.getName() + "(" + getMethodArgs(node) +")"; 

		String methodBody =	
				" {\n"+ 
						"\t" + "// TODO Auto-generated method stub" + "\n" + 
						"\t" + createLoggingInstruction(node) + "\n"+ 
						"}";

		return methodSignature + methodBody;
	}

	private String createLoggingInstruction(MethodNode methodNode) {
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

	private String getMethodArgs(MethodNode node) {
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
