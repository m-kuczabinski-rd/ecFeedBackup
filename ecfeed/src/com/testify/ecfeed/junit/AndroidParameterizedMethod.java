/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.junit;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.runner.android.AndroidTestMethodInvoker;

public class AndroidParameterizedMethod extends AbstractFrameworkMethod {

	private Collection<TestCaseNode> fTestCases;
	private String fTestRunner = null;

	public AndroidParameterizedMethod(
			Method method, 
			Collection<TestCaseNode> testCases, 
			ModelClassLoader loader, 
			String testRunner) {
		super(method, loader);
		fTestCases = testCases;
		fTestRunner = testRunner;
	}

	@Override
	public Object invokeExplosively(Object target, Object... notUsed) throws Throwable{
		for(TestCaseNode testCase : fTestCases){
			try{
				invokeRemotely(target, testCase.getTestData());
			}catch (Throwable e){
				throw new Exception(Messages.RUNNER_EXCEPTION(e.getMessage()), e);
			}
		}
		return null;
	}

	private void invokeRemotely(Object target, List<ChoiceNode> arguments) throws RunnerException {
		AndroidTestMethodInvoker androidInvoker = new AndroidTestMethodInvoker(fTestRunner);
		androidInvoker.invoke(target.getClass().getName(), getMethod(), choiceListToParamArray(arguments));
	}
}
