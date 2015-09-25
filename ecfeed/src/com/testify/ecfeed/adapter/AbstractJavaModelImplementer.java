/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.adapter;

import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;

public abstract class AbstractJavaModelImplementer extends AbstractModelImplementer {

	public AbstractJavaModelImplementer(
			IImplementationStatusResolver statusResolver) {
		super(statusResolver);
	}

	protected abstract boolean androidCodeImplemented(ClassNode node) throws EcException;
	protected abstract void implementAndroidCode(ClassNode node) throws EcException;

	protected boolean implement(ClassNode classNode) throws Exception{
		if(classNode.getRunOnAndroid() && !androidCodeImplemented(classNode)) {
			implementAndroidCode(classNode);
		}
		super.implement(classNode);
		return true;
	}

	protected boolean implement(MethodNode methodNode) throws Exception{
		ClassNode classNode = methodNode.getClassNode();
		if(classNode.getRunOnAndroid() && !androidCodeImplemented(classNode)) {
			implementAndroidCode(classNode);
		}
		super.implement(methodNode);
		return true;
	}
}
