/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.external.IMethodImplementHelper;

public class AndroidMethodImplementer extends AbstractMethodImplementer {

	AndroidMethodImplementer(IFileInfoProvider fileInfoProvider,
			MethodNode methodNode, IMethodImplementHelper methodImplementHelper) {
		super(fileInfoProvider, methodNode, methodImplementHelper);
	}

	protected String createLoggingInstructionPrefix(String methodName) {
		return "android.util.Log.d(\"ecFeed\", \"" + methodName + "(";
	}
}
