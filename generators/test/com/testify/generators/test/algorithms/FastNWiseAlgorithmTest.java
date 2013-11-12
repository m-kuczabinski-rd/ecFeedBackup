package com.testify.generators.test.algorithms;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.generators.algorithms.FastNWiseAlgorithm;
import com.testify.generators.algorithms.INWiseAlgorithm;
import com.testify.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.generators.monitors.SilentProgressMonitor;

public class FastNWiseAlgorithmTest extends NWiseAlgorithmTest {
	final int MAX_VARIABLES = 5;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	final IProgressMonitor PROGRESS_MONITOR = new SilentProgressMonitor();
	
	@Test
	public void testCorrectness() {
		testCorrectness(new FastNWiseAlgorithm<String>(), MAX_VARIABLES, MAX_PARTITIONS_PER_VARIABLE, PROGRESS_MONITOR);
	}

	@Test
	public void testConstraints() {
		testCorrectness(new OptimalNWiseAlgorithm<String>(), MAX_VARIABLES, MAX_PARTITIONS_PER_VARIABLE, PROGRESS_MONITOR);
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
			INWiseAlgorithm<String> fastAlgorithm = new FastNWiseAlgorithm<String>();
			INWiseAlgorithm<String> referenceAlgorithm = new OptimalNWiseAlgorithm<String>();
			Collection<IConstraint<String>> constraints = utils.generateRandomConstraints(input);
			fastAlgorithm.initialize(n, input, constraints, PROGRESS_MONITOR);
			referenceAlgorithm.initialize(n, input, constraints, PROGRESS_MONITOR);

			long timestampStart = new Date().getTime();
			utils.algorithmResult(fastAlgorithm, PROGRESS_MONITOR);
			long timestampEnd = new Date().getTime();
			long duration = timestampEnd - timestampStart;

			timestampStart = new Date().getTime();
			utils.algorithmResult(referenceAlgorithm, PROGRESS_MONITOR);
			timestampEnd = new Date().getTime();
			long referenceDuration = timestampEnd - timestampStart;

			assertTrue(duration <= referenceDuration + 100 /*initialization margin for small generated sets*/);
		}catch(GeneratorException e){
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

}
