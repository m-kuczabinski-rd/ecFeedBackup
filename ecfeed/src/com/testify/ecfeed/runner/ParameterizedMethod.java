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
		for(PartitionNode parameter : testCase){
			Object value = parameter.getValue();
			if ((value != null) && value.getClass().isEnum()) {
				ClassLoader loader = ClassUtils.getClassLoader(false, null);
				Object enumValue = ClassUtils.enumPartitionValue(((Enum<?>)value).name(), value.getClass().getName(), loader);
				if (enumValue != null) {
					parameters.add(enumValue);	
				} else {
					throw new Exception("Enum constant " + ((Enum<?>)value).name() + " not found in " + value.getClass().getName() + " enum definition.");
				}
				
			} else {
				parameters.add(value);
			}
		}
		return parameters.toArray();
	}
}
