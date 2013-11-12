package com.testify.generators.test.algorithms;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.generators.algorithms.INWiseAlgorithm;
import com.testify.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.generators.algorithms.Tuples;
import com.testify.generators.monitors.SilentProgressMonitor;

public class OptimalNWiseTest extends NWiseAlgorithmTest{

	final int MAX_VARIABLES = 5;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	final IProgressMonitor PROGRESS_MONITOR = new SilentProgressMonitor();
	
	@Test
	public void testCorrectness() {
		testCorrectness(new OptimalNWiseAlgorithm<String>(), MAX_VARIABLES, MAX_PARTITIONS_PER_VARIABLE, PROGRESS_MONITOR);
	}
	
	@Test
	public void testConstraints() {
		testConstraints(new OptimalNWiseAlgorithm<String>(), MAX_VARIABLES, MAX_PARTITIONS_PER_VARIABLE, PROGRESS_MONITOR);
	}
	
	@Test
	public void testSize(){
		try{
		for(int variables = 1; variables <= MAX_VARIABLES; variables++){
			for(int partitions = 1; partitions <= MAX_PARTITIONS_PER_VARIABLE; partitions++){
				for(int n = 1; n <= variables; n++){
					utils.trace(PROGRESS_MONITOR, "Variables: " + variables + ", partitions: " + partitions + ", n: " + n);
					List<List<String>>input = utils.prepareInput(variables, partitions);
					Collection<IConstraint<String>> constraints = null;
					INWiseAlgorithm<String> algorithm = new OptimalNWiseAlgorithm<String>();

					algorithm.initialize(n, input, constraints, PROGRESS_MONITOR);
					int generatedDataSize = utils.algorithmResult(algorithm, PROGRESS_MONITOR).size();
					int referenceDataSize = referenceResult(input, n).size();
					assertTrue(Math.abs(generatedDataSize - referenceDataSize) <= referenceDataSize / 30);
					utils.trace(PROGRESS_MONITOR, "Generated size: " + generatedDataSize + ", reference size: " + referenceDataSize);
				}
			}
		}
		}catch(GeneratorException e){
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

	private Set<List<String>> referenceResult(List<List<String>> input, int n) throws GeneratorException {
		List<Set<String>> referenceInput = utils.referenceInput(input); 
		Set<List<String>> cartesianProduct = Sets.cartesianProduct(referenceInput);
		Set<List<String>> referenceResult = new HashSet<List<String>>();
		Set<List<String>> remainingTuples = getAllTuples(input, n);
		for(int k = maxTuples(input, n); k > 0; k--){
			for(List<String> vector : cartesianProduct){
				Set<List<String>> originalTuples = getTuples(vector, n);
				originalTuples.retainAll(remainingTuples);
				if(originalTuples.size() == k){
					referenceResult.add(vector);
					remainingTuples.removeAll(originalTuples);
				}
			}
		}
		return referenceResult;
	}
	
	protected int maxTuples(List<? extends List<String>> input, int n){
		return (new Tuples<List<String>>(input, n)).getAll().size();
	}

	protected Set<List<String>> getTuples(List<String> vector, int n){
		return (new Tuples<String>(vector, n)).getAll();
	}

}
