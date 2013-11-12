package com.testify.generators.test.algorithms;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.generators.CartesianProductGenerator;
import com.testify.generators.algorithms.INWiseAlgorithm;
import com.testify.generators.algorithms.Tuples;
import com.testify.generators.test.utils.TestUtils;

public class NWiseAlgorithmTest{
	protected final TestUtils utils = new TestUtils(); 
	
	protected void testCorrectness(INWiseAlgorithm<String> algorithmUnderTest, 
			int maxVariables, int maxPartitionsPerVariable,
			IProgressMonitor progressMonitor) {
		for(int numOfVariables = 1; numOfVariables <= maxVariables; numOfVariables++){
			for(int partitionsPerVariable = 1; partitionsPerVariable <= maxPartitionsPerVariable; partitionsPerVariable++){
				for(int n = 1; n <= numOfVariables; n++){
					utils.trace(progressMonitor, "Variables: " + numOfVariables + ", partitions: " + partitionsPerVariable + ", n: " + n);
					List<List<String>> input = utils.prepareInput(numOfVariables, partitionsPerVariable);
					Collection<IConstraint<String>> constraints = null;
					try {
						algorithmUnderTest.initialize(n, input, constraints, progressMonitor);
						Set<List<String>> algorithmResult = utils.algorithmResult(algorithmUnderTest, progressMonitor);
						utils.trace(progressMonitor, "Result size: " + algorithmResult.size() + "\n");
						assertTrue(containsAllTuples(algorithmResult, input, n));
					} catch (GeneratorException e) {
						fail("Unexpected algorithm exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	protected void testConstraints(INWiseAlgorithm<String> algorithmUnderTest, 
			int maxVariables, int maxPartitionsPerVariable,
			IProgressMonitor progressMonitor){
		for(int numOfVariables = 1; numOfVariables <= maxVariables; numOfVariables++){
			for(int partitionsPerVariable = 1; partitionsPerVariable <= maxPartitionsPerVariable; partitionsPerVariable++){
				for(int n = 1; n <= numOfVariables; n++){
					List<List<String>> input = utils.prepareInput(numOfVariables, partitionsPerVariable);
					Collection<IConstraint<String>> constraints = utils.generateRandomConstraints(input);
					try {
						algorithmUnderTest.initialize(n, input, constraints, progressMonitor);
						Set<List<String>> algorithmResult = utils.algorithmResult(algorithmUnderTest, progressMonitor);
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
				generator.initialize(next, null, null, null);
				List<String> tuple;
				while((tuple = generator.next()) != null){
					result.add(tuple);
				}
			}
			return result;
	}
}
