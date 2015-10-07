/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.utils;

import java.util.List;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.android.external.IImplementerExt;
import com.testify.ecfeed.android.external.IMethodImplementHelper;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.MethodNode;

public abstract class AbstractMethodImplementer implements IImplementerExt {

	private IMethodImplementHelper fMethodImplementHelper = null;
	private final MethodNode fMethodNode;

	public AbstractMethodImplementer(
			final MethodNode methodNode,
			IMethodImplementHelper methodImplementHelper) {
		fMethodNode = methodNode;
		fMethodImplementHelper = methodImplementHelper;
	}

	@Override
	public void implementContent() {
		fMethodImplementHelper.createMethod(methodDefinitionContent(fMethodNode));
		createImportsForUserParams(fMethodNode);
		fMethodImplementHelper.commitChanges();
	}

	private void createImportsForUserParams(MethodNode methodNode) {
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

	@Override
	public boolean contentImplemented() {
		return fMethodImplementHelper.methodDefinitionImplemented();
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
		String result = createLoggingInstructionPrefix(methodNode.getName());

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

	protected abstract String createLoggingInstructionPrefix(String methodName);

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
}
