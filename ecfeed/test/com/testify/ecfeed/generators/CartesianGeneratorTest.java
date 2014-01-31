package com.testify.ecfeed.generators;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.generators.algorithms.CartesianProductAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class CartesianGeneratorTest{
	@Test
	public void initializeTest(){
		CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
		
		List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3, 3);
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();

		try {
			generator.initialize(inputDomain, constraints, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
		assertTrue(generator.getAlgorithm() instanceof CartesianProductAlgorithm);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			generator.initialize(inputDomain, constraints, parameters);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
		assertTrue(generator.getAlgorithm() instanceof CartesianProductAlgorithm);
	}
}