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

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.junit.annotations.TestSuites;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.ITestMethodInvoker;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.runner.android.AndroidTestMethodInvoker;

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

		ClassNode classNode = (ClassNode)methodNode.getParent();
		Method method = frameworkMethod.getMethod();
		Collection<TestCaseNode> testCases = getTestCases(methodNode, getTestSuites(frameworkMethod));

		if (classNode.getRunOnAndroid()) {
			ITestMethodInvoker invoker = new AndroidTestMethodInvoker(classNode.getAndroidRunner());
			frameworkMethods.add(new AndroidParameterizedMethod(method, testCases, getLoader(), invoker));
		}
		else {
			frameworkMethods.add(new JavaParameterizedMethod(method, testCases, getLoader()));
		}
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
