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

import static org.junit.Assert.*;

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
import com.testify.ecfeed.parsers.etc.XmlModelParser;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.runner.StaticRunner;
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
	public static class TestClass{
		@Test
		public void noArgsTestFunction(){
			if(fExecutedTestCases != null){
				fExecutedTestCases.add(new ArrayList<Integer>());
			}
		}
		
		@Test
		public void noTestSuitesTestFunction(int arg1, int arg2){
			List<Integer> args = new ArrayList<Integer>();
			args.add(arg1); 
			args.add(arg2);
			if(fExecutedTestCases != null){
				fExecutedTestCases.add(args);
			}
		}
		
		@Test
		@TestSuites({"Test Suite 1", "Test Suite 2"})
		public void testSuitesFunction(int arg1, int arg2){
			List<Integer> args = new ArrayList<Integer>();
			args.add(arg1); 
			args.add(arg2);
			if(fExecutedTestCases != null){
				fExecutedTestCases.add(args);
			}
		}
	}

	@Test
	public void frameworkMethodsTest(){
		try {
			StaticRunner runner = new StaticRunner(TestClass.class);
			List<FrameworkMethod> methods = runner.computeTestMethods();
			RootNode model = getModel(MODEL_PATH);
			TestClass target = new TestClass();
			for(FrameworkMethod method : methods){
				try {
					fExecutedTestCases = new HashSet<List<Integer>>();
					try {
						method.invokeExplosively(target, (Object[])null);
					} catch (Throwable e) {
						fail("Unexpected invokation exception: " + e.getMessage());
					}
					MethodNode methodModel = getMethodModel(model, method);
					Set<List<Integer>> referenceResult = referenceResult(method, methodModel); 
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
			MethodNode methodModel) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		if(method.getMethod().getParameterTypes().length == 0){
			result.add(new ArrayList<Integer>());
		}
		else{
			Set<String> testSuites;
			testSuites = getTestSuites(method, methodModel);
			Collection<TestCaseNode> testCases = getTestCases(methodModel, testSuites);
			for(TestCaseNode testCase : testCases){
				addTestCaseResult(testCase, result);
			}
		}
		return result;
	}

	private Set<String> getTestSuites(FrameworkMethod method, MethodNode methodModel){
		Set<String> result;
		Annotation annotation = method.getAnnotation(TestSuites.class);
		if(annotation != null){
			result = new HashSet<String>(Arrays.asList(((TestSuites)annotation).value()));
		}
		else{
			result = methodModel.getTestSuites();
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
		if(testSuites.size() == 0){
			testCases = methodModel.getTestCases();
		}
		else{
			for(String testSuite : testSuites){
				testCases.addAll(methodModel.getTestCases(testSuite));
			}
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
