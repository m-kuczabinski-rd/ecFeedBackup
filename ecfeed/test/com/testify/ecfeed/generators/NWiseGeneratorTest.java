package com.testify.ecfeed.generators;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.testify.ecfeed.generators.NWiseGenerator;
import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class NWiseGeneratorTest{
	
	@Test
	public void initializeTest(){
		try {
			
			NWiseGenerator<String> generator = new NWiseGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3, 3);
			Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("N", 2);

			generator.initialize(inputDomain, constraints, parameters);
			IAlgorithm<String> algorithm = generator.getAlgorithm(); 
			assertTrue(algorithm instanceof OptimalNWiseAlgorithm);
			assertEquals(2, ((OptimalNWiseAlgorithm<String>)algorithm).getN());
			
			try{
				parameters.put("N", 5);
				generator.initialize(inputDomain, constraints, parameters);
				fail("GeneratorException expected");
			}catch(GeneratorException e) {
			}
			try{
				parameters.put("N", -1);
				generator.initialize(inputDomain, constraints, parameters);
				fail("GeneratorException expected");
			}catch(GeneratorException e) {
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
}