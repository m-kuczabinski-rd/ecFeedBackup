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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.junit.annotations.TestSuites;

public class StaticRunner extends AbstractJUnitRunner {

	public StaticRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<FrameworkMethod> generateTestMethods() throws RunnerException {
		List<FrameworkMethod> frameworkMethods = new ArrayList<FrameworkMethod>();
		for(FrameworkMethod frameworkMethod : getTestClass().getAnnotatedMethods(Test.class)){
			if(frameworkMethod.getMethod().getParameterTypes().length == 0){
				//standard jUnit test
				frameworkMethods.add(frameworkMethod);
			} else{
				MethodNode methodNode = getMethodModel(getModel(), frameworkMethod);
				if(methodNode == null){
					continue;
				}

				addFrameworkMethod(methodNode, frameworkMethod, frameworkMethods);
			}
		}

		return frameworkMethods;
	}

	private void addFrameworkMethod(MethodNode methodNode,
			FrameworkMethod frameworkMethod,
			List<FrameworkMethod> frameworkMethods) throws RunnerException {

		Method method = frameworkMethod.getMethod();
		Collection<TestCaseNode> testCases = getTestCases(methodNode, getTestSuites(frameworkMethod));

		frameworkMethods.add(new JavaParameterizedMethod(method, testCases, getLoader()));
	}

	protected Set<String> getTestSuites(FrameworkMethod method) throws RunnerException{
		Set<String> result;
		Annotation annotation = method.getAnnotation(TestSuites.class);
		if(annotation != null){
			result = new HashSet<String>(Arrays.asList(((TestSuites)annotation).value()));
		}
		else{
			result = getMethodModel(getModel(), method).getTestSuites();
		}
		return result;
	}

	private Collection<TestCaseNode> getTestCases(MethodNode methodModel, Set<String> testSuites) {
		Collection<TestCaseNode> result = new LinkedList<TestCaseNode>();
		for(String testSuite : testSuites){
			result.addAll(getImplementedTestCases(methodModel, testSuite));
		}
		return result;
	}

	private LinkedList<TestCaseNode> getImplementedTestCases(MethodNode methodModel, String testSuite) {
		LinkedList<TestCaseNode> result = new LinkedList<TestCaseNode>();
		for (TestCaseNode testCase : methodModel.getTestCases(testSuite)) {
			if (implementationStatus(testCase) == EImplementationStatus.IMPLEMENTED) {
				result.add(testCase);
			}
		}
		return result;
	}
}
