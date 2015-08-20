/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.IPrimitiveTypePredicate;
import com.testify.ecfeed.model.MethodNode;

public abstract class AbstractJavaImplementationStatusResolver extends CachedImplementationStatusResolver{

	public AbstractJavaImplementationStatusResolver(
			IPrimitiveTypePredicate primitiveTypePredicate) {
		super(primitiveTypePredicate);
	}

	protected abstract boolean androidCodeImplemented(ClassNode classNode) throws EcException;

	protected EImplementationStatus implementationStatus(ClassNode classNode) throws EcException{
		if (classNode.getRunOnAndroid() && !androidCodeImplemented(classNode)) {
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		return super.implementationStatus(classNode);
	}

	protected EImplementationStatus implementationStatus(MethodNode method) throws EcException{
		ClassNode classNode = method.getClassNode();

		if (classNode.getRunOnAndroid() && !androidCodeImplemented(classNode)) {
			return EImplementationStatus.NOT_IMPLEMENTED;
		}

		return super.implementationStatus(method);
	}
}
