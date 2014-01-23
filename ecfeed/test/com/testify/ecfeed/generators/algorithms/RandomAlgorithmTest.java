package com.testify.ecfeed.generators.algorithms;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.algorithms.RandomAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class RandomAlgorithmTest {
	final int MAX_VARIABLES = 5;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	final int SAMPLE_SIZE = (int) (10 * Math.pow(MAX_PARTITIONS_PER_VARIABLE, MAX_VARIABLES));

	protected GeneratorTestUtils utils = new GeneratorTestUtils(); 
	
	@Test
	public void uniformityTest(){
		for(int variables = 1; variables <= MAX_VARIABLES; variables++){
			for(int partitions = 2; partitions <= MAX_PARTITIONS_PER_VARIABLE; partitions++){
				uniformityTest(variables, partitions);
			}
		}
	}
	
	protected void uniformityTest(int variables, int partitions) {
		Map<List<String>, Long> histogram = new HashMap<List<String>, Long>();
		List<List<String>> input = utils.prepareInput(variables, partitions);
		IAlgorithm<String> algorithm = new RandomAlgorithm<String>((int)(SAMPLE_SIZE), true);
		try {
			algorithm.initialize(input, null);
			List<String> next;
			while((next = algorithm.getNext()) != null){
				if(histogram.containsKey(next)){
					histogram.put(next, histogram.get(next) + 1);
				}
				else{
					histogram.put(next, 1l);
				}
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		testUniformity(histogram.values());
	}
	
	private void testUniformity(Collection<Long> values) {
		double mean = mean(values);
		double stdDev = 0;
		
		for(Long value : values){
			stdDev += (value - mean) * (value - mean);
		}
		stdDev /= values.size();
		stdDev = Math.sqrt(stdDev);
		assertTrue(stdDev < mean / 3);
	}

	@Test
	public void duplicatesTest(){
		for(int variables = 1; variables <= MAX_VARIABLES; variables++){
			for(int partitions = 2; partitions <= MAX_PARTITIONS_PER_VARIABLE; partitions++){
				duplicatesTest(variables, partitions);
			}
		}
	}

	private void duplicatesTest(int variables, int partitions) {
		Map<List<String>, Long> histogram = new HashMap<List<String>, Long>();
		List<List<String>> input = utils.prepareInput(variables, partitions);
		IAlgorithm<String> algorithm = new RandomAlgorithm<String>((int)(SAMPLE_SIZE), false);
		try {
			algorithm.initialize(input, null);
			List<String> next;
			while((next = algorithm.getNext()) != null){
				if(histogram.containsKey(next)){
					histogram.put(next, histogram.get(next) + 1);
				}
				else{
					histogram.put(next, 1l);
				}
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		
//		make sure that each value was chosen, given that the number of samples is higher than
//		number of possible results
		if(SAMPLE_SIZE > Math.pow(partitions, variables)){
			assertEquals((int)Math.pow(partitions, variables), histogram.size());
		}
		
//		check if no value was chosen more than once
		for(long freq : histogram.values()){
			assertEquals(1, freq);
		}
	}

	public double mean(Collection<Long> values){
		if(values.size() == 0) return 0;
		int sum = 0;
		for(long value: values){
			sum += value;
		}
		return (double)sum / (double)values.size(); 
	}
}