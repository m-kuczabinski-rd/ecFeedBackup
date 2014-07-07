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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.parsers.IModelParser;
import com.testify.ecfeed.parsers.ParserException;
import com.testify.ecfeed.parsers.xml.XmlModelParser;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.TestSuites;

public class StaticRunnerTest extends StaticRunner{
	public StaticRunnerTest() throws InitializationError {
		super(StaticRunnerTest.class);
	}

	protected final static String MODEL_PATH = "test/com/testify/ecfeed/runner/StaticRunnerTest.ect";
	protected final static String TEST_SUITES[] = {"Test Suite 1", "Test Suite 2"};
	
	protected static Set<List<Integer>> fExecutedTestCases;
	
	@RunWith(StaticRunner.class)
	@EcModel(MODEL_PATH)
	public static class TestClass1{
		@Test
		public void noArgsTestFunction(){
			if(fExecutedTestCases != null){
				fExecutedTestCases.add(new ArrayList<Integer>());
			}
		}
		
		@Test
		public void noTestSuitesTestFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
		@Test
		@TestSuites({"Test Suite 1", "Test Suite 2"})
		public void testSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
	}
	
	@RunWith(StaticRunner.class)
	@EcModel(MODEL_PATH)
	@TestSuites({"Test Suite 1", TestSuites.ALL})
	public static class TestClassAll{

		/*
		 * method annotation overrides class annotation. It should execute no suites.
		 */
		@Test
		@TestSuites(TestSuites.NONE)
		public void noneTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
	
		/*
		 * method annotation overrides class annotation. It should execute 1 out of 3 suites.
		 */
		@Test
		@TestSuites({"Test Suite 2"})
		public void oneTestSuiteFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
		/*
		 * no method annotation to override class annotation. It should execute 3 out of 3 suites.
		 */
		@Test
		public void noTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}	
		
	}
	
	@RunWith(StaticRunner.class)
	@EcModel(MODEL_PATH)
	@TestSuites({"Test Suite 1", "TestSuite 2"})
	public static class TestClassTwoSuites{
		
		/*
		 * method annotation overrides class annotation. It should execute no suite.
		 */
		@Test
		@TestSuites(TestSuites.NONE)
		public void noneTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
		/*
		 * no method annotation to override class annotation. It should execute 2 out of 3 suites.
		 */
		@Test
		public void noTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
		/*
		 * method annotation overrides class annotation. It should execute 1 out of 3 suites.
		 */
		@Test
		@TestSuites({"Test Suite 2"})
		public void oneTestSuiteFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
		/*
		 * method annotation overrides class annotation. It should execute 3 out of 3 suites.
		 */
		@Test
		@TestSuites(TestSuites.ALL)
		public void allTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
	}
	
	@RunWith(StaticRunner.class)
	@EcModel(MODEL_PATH)
	@TestSuites({"Test Suite 1", TestSuites.NONE})
	public static class TestClassNone{
		
		/*
		 * No method annotation to override class annotation. It should execute no suite.
		 */
		@Test
		public void noTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
		/*
		 * method annotation overrides class annotation. It should execute 2 out of 3 suites.
		 */
		@Test
		@TestSuites({"Test Suite 1", "Test Suite 2"})
		public void twoTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
		
		/*
		 * method annotation overrides class annotation. It should execute 3 out of 3 suites.
		 */
		@Test
		@TestSuites(TestSuites.ALL)
		public void allTestSuitesFunction(int arg1, int arg2){
			saveTestInput(arg1, arg2);
		}
	}

	private static void saveTestInput(int arg1, int arg2){
		List<Integer> args = new ArrayList<Integer>();
		args.add(arg1); 
		args.add(arg2);
		if(fExecutedTestCases != null){
			fExecutedTestCases.add(args);
		}
	}
	
	@Test
	public void frameworkMethodsTest(){
		frameworkMethodTest(TestClass1.class, new TestClass1());
		frameworkMethodTest(TestClassAll.class, new TestClassAll());
		frameworkMethodTest(TestClassNone.class, new TestClassNone());
		frameworkMethodTest(TestClassTwoSuites.class, new TestClassTwoSuites());
	}
	
	private void frameworkMethodTest(Class<?> klass, Object target){
		try {
			StaticRunner runner = new StaticRunner(klass);
			List<FrameworkMethod> methods = runner.computeTestMethods();
			RootNode model = getModel(MODEL_PATH);
			for(FrameworkMethod method : methods){
				try {
					fExecutedTestCases = new HashSet<List<Integer>>();
					try {
						method.invokeExplosively(target, (Object[])null);
					} catch (Throwable e) {
						fail("Unexpected invokation exception: " + e.getMessage());
					}
					MethodNode methodModel = getMethodModel(model, method);
					Set<List<Integer>> referenceResult = referenceResult(method, methodModel, klass); 
					
					assertEquals(fExecutedTestCases, referenceResult);
				} catch (RunnerException e) {
					fail("Unexpected runner exception: " + e.getMessage());
				}
			}
		} catch (InitializationError e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	private Set<List<Integer>> referenceResult(FrameworkMethod method,
			MethodNode methodModel, Class<?> clazz) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		if(method.getMethod().getParameterTypes().length == 0){
			result.add(new ArrayList<Integer>());
		}
		else{
			Set<String> testSuites;
			testSuites = getTestSuites(method, methodModel, clazz);
			Collection<TestCaseNode> testCases = getTestCases(methodModel, testSuites);
			for(TestCaseNode testCase : testCases){
				addTestCaseResult(testCase, result);
			}
		}
		return result;
	}

	private Set<String> getTestSuites(FrameworkMethod method, MethodNode methodModel, Class<?> clazz){
		Set<String> result;
		Annotation annotation = method.getAnnotation(TestSuites.class);
		if(annotation != null){
			result = new HashSet<String>(Arrays.asList(((TestSuites)annotation).value()));
			if(result.contains(TestSuites.ALL)){
				result = methodModel.getTestSuites();
			} else if(result.contains(TestSuites.NONE)){
				result.clear();
			}
		}
		else{
			Annotation classAnnotation = null;
			for(Annotation element : clazz.getAnnotations()){
				if(element.annotationType().equals(TestSuites.class)){
					classAnnotation = (TestSuites)element;
					break;
				}
			}
			if(classAnnotation != null) {
				result = new HashSet<String>(Arrays.asList(((TestSuites)classAnnotation).value()));
				if(result.contains(TestSuites.ALL)){
					result = methodModel.getTestSuites();
				} else if(result.contains(TestSuites.NONE)){
					result.clear();
				}
			}
			else {
				result = methodModel.getTestSuites();
			}
		}

		return result;
	}
	
	private void addTestCaseResult(TestCaseNode testCase,
			Set<List<Integer>> target) {
		List<Integer> result = new ArrayList<Integer>();
		for(PartitionNode parameter : testCase.getTestData()){
			result.add(Integer.valueOf(parameter.getValueString()));
		}
		target.add(result);
	}

	private Collection<TestCaseNode> getTestCases(MethodNode methodModel,
			Set<String> testSuites) {
		Collection<TestCaseNode> testCases = new HashSet<TestCaseNode>();
		for(String testSuite : testSuites){
			testCases.addAll(methodModel.getTestCases(testSuite));
		}
		return testCases;
	}

	protected RootNode getModel(String path){
		IModelParser parser = new XmlModelParser();
		InputStream istream;
		try {
			istream = new FileInputStream(new File(path));
			return parser.parseModel(istream);
		} catch (FileNotFoundException e) {
			fail("Cannot find file: " + path);
			return null;
		} catch (ParserException e) {
			fail("Cannot parse file " + path + ": " + e.getMessage());
			return null;
		}
	}
}
