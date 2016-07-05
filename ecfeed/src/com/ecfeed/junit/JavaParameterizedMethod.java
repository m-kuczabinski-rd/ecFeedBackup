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
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.utils.EcException;

public class JavaParameterizedMethod extends AbstractFrameworkMethod {

	private Collection<TestCaseNode> fTestCases;

	public JavaParameterizedMethod(Method method, Collection<TestCaseNode> testCases, ModelClassLoader loader) {
		super(method, loader);
		fTestCases = testCases;
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{
		for(TestCaseNode testCase : fTestCases){
			try{
				super.invoke(target, testCase.getTestData());
			}catch (Throwable e){
				EcException.report(Messages.RUNNER_EXCEPTION(e.getMessage()), e);
			}
		}
		return null;
	}
}
