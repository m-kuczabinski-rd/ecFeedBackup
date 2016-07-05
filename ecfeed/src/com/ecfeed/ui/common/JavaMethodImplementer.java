/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import com.ecfeed.android.external.IMethodImplementHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.AbstractMethodImplementer;

public class JavaMethodImplementer extends AbstractMethodImplementer {

	JavaMethodImplementer(
			MethodNode methodNode, IMethodImplementHelper methodImplementHelper) {
		super(methodNode, methodImplementHelper);
	}

	@Override
	protected String createLoggingInstructionPrefix(String methodName) {
		return "System.out.println(\"" + methodName + "(";
	}
}
