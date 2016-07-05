/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.EcException;

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
