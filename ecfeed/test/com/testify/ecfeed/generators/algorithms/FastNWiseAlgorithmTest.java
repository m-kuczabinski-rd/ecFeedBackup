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

public class FastNWiseAlgorithmTest extends NWiseAlgorithmTest {
	final int MAX_VARIABLES = 5;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	
	@Test
	public void testCorrectness() {
		testCorrectness(FastNWiseAlgorithm.class, MAX_VARIABLES, MAX_PARTITIONS_PER_VARIABLE);
	}

	@Test
	public void testConstraints() {
		testCorrectness(FastNWiseAlgorithm.class, MAX_VARIABLES, MAX_PARTITIONS_PER_VARIABLE);
	}
	

	@Test
	public void testSpeed(){
		for(int variables = 1; variables <= MAX_VARIABLES; variables++){
			for(int partitions = 1; partitions <= MAX_PARTITIONS_PER_VARIABLE; partitions++){
				List<List<String>> input = utils.prepareInput(variables, partitions);
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
			Collection<IConstraint<String>> constraints = utils.generateRandomConstraints(input);
			fastAlgorithm.initialize(input, constraints);
			referenceAlgorithm.initialize(input, constraints);

			long timestampStart = new Date().getTime();
			utils.algorithmResult(fastAlgorithm);
			long timestampEnd = new Date().getTime();
			long duration = timestampEnd - timestampStart;

			timestampStart = new Date().getTime();
			utils.algorithmResult(referenceAlgorithm);
			timestampEnd = new Date().getTime();
			long referenceDuration = timestampEnd - timestampStart;

			assertTrue(duration <= referenceDuration + 100 /*initialization margin for small generated sets*/);
		}catch(GeneratorException e){
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

}