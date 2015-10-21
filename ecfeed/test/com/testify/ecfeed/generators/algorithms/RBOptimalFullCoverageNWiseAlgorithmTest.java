package com.testify.ecfeed.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.generators.algorithms.RBOptimalFullCoverageNWiseAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class RBOptimalFullCoverageNWiseAlgorithmTest {
	
	private final Collection<IConstraint<Integer>> EMPTY_CONSTRAINTS = new HashSet<IConstraint<Integer>>();


	@Test
	public void testGetFirstNTupels() {

		List<List<Integer>> input = new ArrayList<List<Integer>>();
		List<Integer> in1 = new ArrayList<>();
		in1.add(0); in1.add(1); 
		List<Integer> in2 = new ArrayList<>();
		in2.add(0); in2.add(1); in2.add(2); 
		List<Integer> in3 = new ArrayList<>();
		in3.add(0); in3.add(1); 
		
		input.add(in1);
		input.add(in2);
		input.add(in3);
		
		
		RBOptimalFullCoverageNWiseAlgorithm<Integer> alg = new RBOptimalFullCoverageNWiseAlgorithm<Integer>(2);
		try {
			alg.initialize(input, EMPTY_CONSTRAINTS);
			
			List<List<Integer>> res = alg.getFirstNTupels();
			
			for(List<Integer> tuple: res) {
				for(Integer i: tuple)
					System.out.print(i + " ");
				System.out.println();
			}
			
			assertNotNull(res);
			assertEquals(res.size(), 6);
			
			
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		
	}

}
