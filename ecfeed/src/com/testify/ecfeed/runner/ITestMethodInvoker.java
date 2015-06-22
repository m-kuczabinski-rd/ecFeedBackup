/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.List;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;

public interface ITestMethodInvoker {
	void invoke(Method fTestMethod, 
			Object instance, 
			Object[] arguments, 
			MethodNode fTarget, 
			List<ChoiceNode> testData) throws RunnerException;
}
