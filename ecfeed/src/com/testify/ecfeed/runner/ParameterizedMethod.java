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
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.utils.ClassUtils;

public class ParameterizedMethod extends FrameworkMethod {
	

	private Collection<List<PartitionNode>> fTestData;

	public ParameterizedMethod(Method method, Collection<TestCaseNode> testCases) {
		super(method);
		fTestData = new LinkedList<List<PartitionNode>>();
		for(TestCaseNode testCase : testCases){
			fTestData.add(testCase.getTestData());
		}
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{
		for(List<PartitionNode> testCase : fTestData){
			Object[] arguments = getParameters(testCase);
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
		URLClassLoader loader = ClassUtils.getClassLoader(false, getClass().getClassLoader());
		for(PartitionNode parameter : testCase){
			Object value = ClassUtils.getPartitionValueFromString(parameter.getExactValueString(), parameter.getCategory().getType(), loader);
			if ((value != null) || (parameter.getCategory().getType().equals(com.testify.ecfeed.model.Constants.TYPE_NAME_STRING))) {
				parameters.add(value);
			} else {
				throw new Exception("Value " + "\'" + (parameter.getExactValueString() + "\'" + " is incorrect for " + "\'" + parameter.getCategory().getType() + "\'" + " type."));
			}
		}
		return parameters.toArray();
	}
}
