package com.testify.ecfeed.generators.algorithms;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.algorithms.Tuples;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class NWiseAlgorithmTest{
	private final Collection<IConstraint<String>> EMPTY_CONSTRAINTS = new HashSet<IConstraint<String>>();
	
	@SuppressWarnings("rawtypes")
	protected void testCorrectness(Class<? extends IAlgorithm> algorithmUnderTestClass) {
		for(int numOfVariables : new int[]{1, 2, 5}){
		for(int partitionsPerVariable : new int[]{1, 2, 5}){
			for(int n = 1; n <= numOfVariables; n++){
				List<List<String>> input = GeneratorTestUtils.prepareInput(numOfVariables, partitionsPerVariable);
				try{
					IAlgorithm<String> algorithmUnderTest = getAlgorithm(algorithmUnderTestClass, n);
					algorithmUnderTest.initialize(input, EMPTY_CONSTRAINTS);
					Set<List<String>> algorithmResult = GeneratorTestUtils.algorithmResult(algorithmUnderTest);
					assertTrue(containsAllTuples(algorithmResult, input, n));
				} catch (Exception e) {
					fail("Unexpected algorithm exception: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void testConstraints(Class<? extends IAlgorithm> algorithmUnderTestClass){
		for(int numOfVariables : new int[]{1, 2, 5}){
		for(int partitionsPerVariable : new int[]{1, 2, 5}){
			for(int n = 1; n <= numOfVariables; n++){
				List<List<String>> input = GeneratorTestUtils.prepareInput(numOfVariables, partitionsPerVariable);
				Collection<IConstraint<String>> constraints = GeneratorTestUtils.generateRandomConstraints(input);
				try {
					IAlgorithm<String> algorithmUnderTest = getAlgorithm(algorithmUnderTestClass, n);
					algorithmUnderTest.initialize(input, constraints);
					Set<List<String>> algorithmResult = GeneratorTestUtils.algorithmResult(algorithmUnderTest);
					for(List<String> vector : algorithmResult){
						for(IConstraint<String> constraint : constraints){
							assertTrue(constraint.evaluate(vector));
						}
					}
				} catch (GeneratorException e) {
					fail("Unexpected algorithm exception: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private IAlgorithm<String> getAlgorithm(Class<? extends IAlgorithm> algorithmUnderTestClass, int n) {
		Constructor<? extends IAlgorithm> algorithmUnderTestConstructor;
		try {
			algorithmUnderTestConstructor = algorithmUnderTestClass.getConstructor(int.class);
			IAlgorithm<String> algorithm = algorithmUnderTestConstructor.newInstance(n);
			return algorithm;
		} catch (Exception e) {
			fail("Unexpected algorithm exception: " + e.getMessage());
			return null;
		}
	}

	protected boolean containsAllTuples(Set<List<String>> algorithmResult,
			List<List<String>> input, int n) throws GeneratorException {
		Set<List<String>> notCoveredTuples = getAllTuples(input, n);
		for(List<String> vector : algorithmResult){
			notCoveredTuples.removeAll((new Tuples<String>(vector, n)).getAll());
		}
		return notCoveredTuples.isEmpty();
	}


	protected Set<List<String>> getAllTuples(List<List<String>> input, int n) throws GeneratorException{
			Set<List<String>> result  = new HashSet<List<String>>();
			Tuples<List<String>> categoryTuples = new Tuples<List<String>>(input, n);
			while(categoryTuples.hasNext()){
				List<List<String>> next = categoryTuples.next();
				CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
				generator.initialize(next, EMPTY_CONSTRAINTS, null);
				List<String> tuple;
				while((tuple = generator.next()) != null){
					result.add(tuple);
				}
			}
			return result;
	}
}