package com.testify.generators.test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGenerator;
import com.testify.generators.CartesianProductGenerator;
import com.testify.generators.monitors.SilentProgressMonitor;
import com.testify.generators.test.utils.TestUtils;

public class CartesianGeneratorTest extends CartesianProductGenerator<String>{
	IGenerator<String> fGeneratorUnderTest;
	IProgressMonitor PROGRESS_MONITOR = new SilentProgressMonitor();
	TestUtils utils = new TestUtils();
	int MAX_VARIABLES = 6;
	int MAX_PARTITIONS = 10;
	
	public CartesianGeneratorTest() {
		fGeneratorUnderTest = new CartesianProductGenerator<String>();
	}
	
	@Test
	public void testInitialize(){
		List<List<String>> input = utils.prepareInput(5, 5);
		Collection<IConstraint<String>> constraints = utils.generateRandomConstraints(input);
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			fGeneratorUnderTest.initialize(input, constraints, null, PROGRESS_MONITOR);
		} catch (GeneratorException e) {
			fail("Unexpected generator exception");
		}
		try {
			fGeneratorUnderTest.initialize(input, constraints, parameters, PROGRESS_MONITOR);
		} catch (GeneratorException e) {
			fail("Unexpected generator exception");
		}
		parameters.put("DUMMY_PARAMETER", 0);
		boolean exceptionCaught = false;
		try {
			fGeneratorUnderTest.initialize(input, constraints, parameters, PROGRESS_MONITOR);
		} catch (GeneratorException e) {
			exceptionCaught = true;
		}
		if(exceptionCaught == false){
			fail("Unexpected generator exception");
		}
	}

	@Test
	public void testResults(){
		for(int variables = 1; variables <= MAX_VARIABLES; variables++){
			for(int partitions = 1; partitions <= MAX_PARTITIONS; partitions++){
				List<List<String>> input = utils.prepareInput(variables, partitions);
				try {
					fGeneratorUnderTest.initialize(input, null, null, PROGRESS_MONITOR);
					Set<List<String>> referenceResult = Sets.cartesianProduct(utils.referenceInput(input));
					assertEquals(referenceResult.size(), utils.generatorResult(fGeneratorUnderTest, PROGRESS_MONITOR).size());
				} catch (GeneratorException e) {
					fail("Unexpected generator exception");
				}
			}
		}
	}
	
	@Test
	public void testReset(){
		List<List<String>> input = utils.prepareInput(5, 5);
		try {
			fGeneratorUnderTest.initialize(input, null, null, PROGRESS_MONITOR);
			Set<List<String>> referenceResult = utils.generatorResult(fGeneratorUnderTest, PROGRESS_MONITOR);
			for(int i = 0; i < 100; i++){
				//generate some data
				fGeneratorUnderTest.next();
			}
			fGeneratorUnderTest.reset();
			Set<List<String>> result = new HashSet<List<String>>();
			List<String> next;
			while((next = fGeneratorUnderTest.next()) != null){
				result.add(next);
			}
			assertEquals(referenceResult, result);
		} catch (GeneratorException e) {
			fail("Unexpected generator exception");
		}
	}
	
	@Test
	public void testParameters(){
		assertTrue(fGeneratorUnderTest.parameters().size() == 0);
	}
}
