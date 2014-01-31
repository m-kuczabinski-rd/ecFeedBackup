package com.testify.ecfeed.generators.algorithms;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.generators.algorithms.FastNWiseAlgorithm;
import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class FastNWiseAlgorithmTest extends NWiseAlgorithmTest {

	@Test
	public void testCorrectness() {
		testCorrectness(FastNWiseAlgorithm.class);
	}

	@Test
	public void testConstraints() {
		testCorrectness(FastNWiseAlgorithm.class);
	}
	

	@Test
	public void testSpeed(){
		for(int variables : new int[]{1, 2, 5}){
		for(int partitions : new int[]{1, 2, 5}){
			List<List<String>> input = GeneratorTestUtils.prepareInput(variables, partitions);
			for(int n = 1; n <= variables; n++){
				testSpeed(input, n);
			}
		}
		}
	}

	private void testSpeed(List<List<String>> input, int n) {
		try{
			IAlgorithm<String> fastAlgorithm = new FastNWiseAlgorithm<String>(n);
			IAlgorithm<String> referenceAlgorithm = new OptimalNWiseAlgorithm<String>(n);
			Collection<IConstraint<String>> constraints = GeneratorTestUtils.generateRandomConstraints(input);
			fastAlgorithm.initialize(input, constraints);
			referenceAlgorithm.initialize(input, constraints);

			long timestampStart = new Date().getTime();
			GeneratorTestUtils.algorithmResult(fastAlgorithm);
			long timestampEnd = new Date().getTime();
			long duration = timestampEnd - timestampStart;

			timestampStart = new Date().getTime();
			GeneratorTestUtils.algorithmResult(referenceAlgorithm);
			timestampEnd = new Date().getTime();
			long referenceDuration = timestampEnd - timestampStart;

			assertTrue(duration <= referenceDuration + 100 /*initialization margin for small generated sets*/);
		}catch(GeneratorException e){
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

}