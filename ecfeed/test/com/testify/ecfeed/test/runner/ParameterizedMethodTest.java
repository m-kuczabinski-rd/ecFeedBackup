package com.testify.ecfeed.test.runner;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.runner.ParameterizedMethod;

public class ParameterizedMethodTest {

	private Set<List<Integer>> fExecuted;
	private final int MAX_TEST_SUITE_SIZE = 1000;
	
	public void functionUnderTest(int arg1, int arg2){
		List<Integer> parameters = new ArrayList<Integer>();
		parameters.add(arg1);
		parameters.add(arg2);
		fExecuted.add(parameters);
	}

	@Test
	public void testExecution(){
		for(int i = 1; i <= MAX_TEST_SUITE_SIZE; i++){
			test(i);
		}
	}
	
	public void test(int testSuiteSize) {
		try {
			fExecuted = new HashSet<List<Integer>>();
			Method methodUnterTest = this.getClass().getMethod("functionUnderTest", int.class, int.class);
			Collection<? extends List<PartitionNode>> testSuite = generateTestSuite(testSuiteSize, 2);
			Set<List<Integer>> referenceResult = generateReferenceResult(testSuite);
			
			FrameworkMethod m = new ParameterizedMethod(methodUnterTest, testSuite);
			m.invokeExplosively(this, new Object[]{});
			
			assertEquals(referenceResult, fExecuted);
			
		} catch (NoSuchMethodException e){
			fail("NoSuchMethodException: " + e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected exception from invoked method: " + e.getMessage());
		}
	}

	private Collection<? extends List<PartitionNode>> generateTestSuite(int size, int parameters) {
		Random random = new Random();
		Collection<List<PartitionNode>> suite = new HashSet<List<PartitionNode>>();
		for(int i = 0; i < size; i++){
			List<PartitionNode> testCase = new ArrayList<PartitionNode>();
			for(int j = 0; j < parameters; j++){
				testCase.add(new PartitionNode("dummy", random.nextInt()));
			}
			suite.add(testCase);
		}
		return suite;
	}

	private Set<List<Integer>> generateReferenceResult(
			Collection<? extends List<PartitionNode>> testSuite) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		for(List<PartitionNode> testCase : testSuite){
			List<Integer> parameters = new ArrayList<Integer>();
			for(PartitionNode parameter : testCase){
				parameters.add((int)parameter.getValue());
			}
			result.add(parameters);
		}
		return result;
	}


}
