package com.testify.ecfeed.generators;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.testify.ecfeed.generators.NWiseGenerator;
import com.testify.ecfeed.generators.algorithms.FastNWiseAlgorithm;
import com.testify.ecfeed.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class NWiseGeneratorTest extends NWiseGenerator<String>{
	
	int MAX_VARIABLES = 5;
	int MAX_PARTITIONS = 5;
	GeneratorTestUtils utils = new GeneratorTestUtils();
	
	@Test
	public void initTest(){
		testN();
		
//		DISABLED
//		testAlgorithm();
	}
	
	private void testN() {
		List<List<String>> input = utils.prepareInput(5, 5);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Collection<IConstraint<String>> constraints = utils.generateRandomConstraints(input);
		boolean exceptionCaught = false;
		try {
			super.initialize(input, constraints, null);
		} catch (GeneratorException e) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);

		try {
			exceptionCaught = false;
			super.initialize(input, constraints, parameters);
		} catch (GeneratorException e) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);

		try {
			exceptionCaught = false;
			parameters.put("N", -1);
			super.initialize(input, constraints, parameters);
		} catch (GeneratorException e) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);

		try {
			exceptionCaught = false;
			parameters.put("N", 7);
			super.initialize(input, constraints, parameters);
		} catch (GeneratorException e) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);

		try {
			exceptionCaught = false;
			parameters.put("N", 3);
			super.initialize(input, constraints, parameters);
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
			exceptionCaught = true;
		}
		assertFalse(exceptionCaught);
	}

	@SuppressWarnings("unused")
	private void testAlgorithm() {
		List<List<String>> input = utils.prepareInput(5, 5);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Collection<IConstraint<String>> constraints = utils.generateRandomConstraints(input);
		parameters.put("N", 3);
		
		try {
			super.initialize(input, constraints, parameters);
			assertTrue(super.getAlgorithm() instanceof OptimalNWiseAlgorithm);
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
		}

		try {
			parameters.put(ALGORITHM_PARAMETER_NAME, "FAST");
			super.initialize(input, constraints, parameters);
			assertTrue(super.getAlgorithm() instanceof FastNWiseAlgorithm);
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
		}

		boolean exceptionCaught = false;
		try {
			parameters.put(ALGORITHM_PARAMETER_NAME, "UNSUPPORTED_ALGORITHM");
			super.initialize(input, constraints, parameters);
		} catch (GeneratorException e) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}
	
	@Test
	public void generateDataTest(){
		List<List<String>> input = utils.prepareInput(5, 5);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Collection<IConstraint<String>> constraints = utils.generateRandomConstraints(input);
		parameters.put("N", 2);
		try {
			IGenerator<String> compactGenerator = new NWiseGenerator<String>();
			Set<List<String>> compactResult = new HashSet<List<String>>();
			parameters.put(ALGORITHM_PARAMETER_NAME, "COMPACT");
			compactGenerator.initialize(input, constraints, parameters);
			List<String> next;
			while((next = compactGenerator.next()) != null){
				compactResult.add(next);	
			}

//			parameters.put(ALGORITHM_PARAMETER_NAME, "FAST");
//			fastGenerator.initialize(input, constraints, parameters, null);
//			while((next = fastGenerator.next()) != null){
//				fastResult.add(next);	
//			}
//			assertTrue(fastResult.size() > compactResult.size());
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

}