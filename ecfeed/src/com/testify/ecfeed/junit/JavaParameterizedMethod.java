/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.junit;

import java.lang.reflect.Method;
import java.util.Collection;

import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.utils.EcException;

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
