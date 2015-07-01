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

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.junit.JavaParameterizedMethod;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.java.JUnitTestMethodInvoker;

public class ParameterizedMethodsTest {

	public enum Enum{
		VALUE1, VALUE2, VALUE3;
	}

	private Set<List<Integer>> fExecuted;
	private List<Enum> fExecutedEnum;
	private final int MAX_TEST_SUITE_SIZE = 1000;

	private final String CLASS_NAME = this.getClass().getCanonicalName();
	private final String FUNCTION_UNDER_TEST_NAME = "functionUnderTest";
	private final String ENUM_FUNCTION_UNDER_TEST_NAME = "enumFunctionUnderTest";

	public void functionUnderTest(int arg1, int arg2){
		List<Integer> parameters = new ArrayList<Integer>();
		parameters.add(arg1);
		parameters.add(arg2);
		fExecuted.add(parameters);
	}

	public void enumFunctionUnderTest(Enum e){
		fExecutedEnum.add(e);
	}

	@Test
	public void javaMethodTest(){
		for(int i = 1; i <= MAX_TEST_SUITE_SIZE; i++){
			test(false, i);
		}
	}

	@Test
	public void androidMethodTest(){
		for(int i = 1; i <= MAX_TEST_SUITE_SIZE; i++){
			test(true, i);
		}
	}	

	@Test
	public void enumExecutionTest(){
		ModelClassLoader loader = new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader());

		ClassNode classNode = new ClassNode(CLASS_NAME);
		MethodNode methodNode = new MethodNode(ENUM_FUNCTION_UNDER_TEST_NAME);
		classNode.addMethod(methodNode);
		MethodParameterNode c = new MethodParameterNode("c", Enum.class.getCanonicalName(), "0", false);
		methodNode.addParameter(c);
		fExecutedEnum = new ArrayList<Enum>();

		for(Enum v : Enum.values()){
			ChoiceNode p = new ChoiceNode(v.name(), v.name());
			c.addChoice(p);
			List<ChoiceNode> td = new ArrayList<>();
			td.add(p);
			methodNode.addTestCase(new TestCaseNode("", td));
		}

		try{
			Method methodUnterTest = this.getClass().getMethod(ENUM_FUNCTION_UNDER_TEST_NAME, Enum.class);

			FrameworkMethod m = createParametrizedMethod(false, this.getClass().getName(), methodUnterTest, methodNode.getTestCases(), loader);
			m.invokeExplosively(this, new Object[]{});
			for(Enum v : Enum.values()){
				assertTrue(fExecutedEnum.contains(v));
			}
		}catch(Throwable e){
			System.out.println("Unexpected exception: " + e);
		}
	}

	public void test(boolean isAndroidTest, int testSuiteSize) {
		try {
			ModelClassLoader loader = new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader());

			ClassNode classNode = new ClassNode(CLASS_NAME);
			MethodNode methodNode = new MethodNode(FUNCTION_UNDER_TEST_NAME);
			classNode.addMethod(methodNode);
			methodNode.addParameter(new MethodParameterNode("c1", "int", "0", false));
			methodNode.addParameter(new MethodParameterNode("c1", "int", "0", false));


			fExecuted = new HashSet<List<Integer>>();
			Method methodUnterTest = this.getClass().getMethod(FUNCTION_UNDER_TEST_NAME, int.class, int.class);
			Collection<TestCaseNode> testCases = generateTestCases(methodNode, testSuiteSize);
			Set<List<Integer>> referenceResult = generateReferenceResult(testCases);

			FrameworkMethod m = createParametrizedMethod(isAndroidTest, this.getClass().getName(), methodUnterTest, testCases, loader);
			m.invokeExplosively(this, new Object[]{});

			assertEquals(referenceResult, fExecuted);

		} catch (NoSuchMethodException e){
			fail("NoSuchMethodException: " + e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected exception from invoked method: " + e.getMessage());
		}
	}

	private FrameworkMethod createParametrizedMethod(
			boolean isAndroidTest,
			String className,
			Method methodUnterTest, 
			Collection<TestCaseNode> testCases, 
			ModelClassLoader loader) {

		if (isAndroidTest) {
			return new JavaParameterizedMethod(methodUnterTest, testCases, loader);
		} else {
			return new AndroidParameterizedMethod(className, methodUnterTest, testCases, loader, new JUnitTestMethodInvoker());
		}
	}

	private Collection<TestCaseNode> generateTestCases(MethodNode method, int size) {
		Random random = new Random();
		for(int i = 0; i < size; i++){
			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			for(int j = 0; j < method.getParameters().size(); j++){
				ChoiceNode choice = new ChoiceNode("dummy", Integer.toString(random.nextInt()));
				choice.setParent(method.getParameters().get(j));
				testData.add(choice);
			}
			TestCaseNode tc = new TestCaseNode("dummy", testData);
			method.addTestCase(tc);
		}
		return method.getTestCases();
	}

	private Set<List<Integer>> generateReferenceResult(
			Collection<TestCaseNode> testSuite) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		for(TestCaseNode testCase : testSuite){
			List<Integer> parameters = new ArrayList<Integer>();
			for(ChoiceNode parameter : testCase.getTestData()){
				parameters.add(Integer.valueOf(parameter.getValueString()));
			}
			result.add(parameters);
		}
		return result;
	}

}
