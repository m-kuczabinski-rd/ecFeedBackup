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

package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.modelif.java.PartitionValueParser;

public class ParameterizedMethod extends FrameworkMethod {

	private Collection<TestCaseNode> fTestCases;
	private PartitionValueParser fValueParser;
	
	public ParameterizedMethod(Method method, Collection<TestCaseNode> testCases, ModelClassLoader loader) {
		super(method);
		fTestCases = testCases;
		fValueParser = new PartitionValueParser(loader);
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{
		for(TestCaseNode testCase : fTestCases){
			Object[] arguments = getParameters(testCase.getTestData());
			try{
				super.invokeExplosively(target, arguments);
			}catch (Throwable e){
				String message = getName() + "(" + testCase + "): " + e.getMessage();
				throw new Exception(message, e);
			}
		}
		return null;
	}
	
	protected Object[] getParameters(List<PartitionNode> testCase) throws Exception {
		List<Object> parameters = new ArrayList<Object>();
		for(PartitionNode parameter : testCase){
			Object value = fValueParser.parseValue(parameter);
			if (value != null) {
				parameters.add(value);
			} else {
				throw new Exception("Value " + "\'" + (parameter.getValueString() + "\'" + " is incorrect for " + "\'" + parameter.getCategory().getType() + "\'" + " type."));
			}
		}
		return parameters.toArray();
	}
}
