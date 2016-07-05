/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.junit;

import java.lang.reflect.Method;
import java.util.Collection;

import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.utils.EcException;

public class AndroidParameterizedMethod extends AbstractFrameworkMethod {

	private Collection<TestCaseNode> fTestCases;
	private ITestMethodInvoker fMethodInvoker;
	private String fClassName;

	public AndroidParameterizedMethod(
			String className,
			Method method,
			Collection<TestCaseNode> testCases, 
			ModelClassLoader loader, 
			ITestMethodInvoker methodInvoker) {
		super(method, loader);
		fClassName = className;
		fTestCases = testCases;
		fMethodInvoker = methodInvoker;
	}

	@Override
	public Object invokeExplosively(Object target, Object... notUsed) throws Throwable{
		for(TestCaseNode testCase : fTestCases){
			try{
				fMethodInvoker.invoke(
						getMethod(), 
						fClassName,
						target, 
						choiceListToParamArray(testCase.getTestData()), 
						testCase.getTestData().toString());

			}catch (Throwable e){
				EcException.report(Messages.RUNNER_EXCEPTION(e.getMessage()), e);
			}
		}
		return null;
	}

}
