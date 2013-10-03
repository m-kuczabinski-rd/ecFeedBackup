package com.testify.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TupleGeneratorTest<E> {

	@Test
	public void test() {
		String x1 = "x1";
		String x2 = "x2";
		String x3 = "x3";

		String y1 = "y1";
		String y2 = "y2";
		String y3 = "y3";

		String z1 = "z1";
		String z2 = "z2";
		String z3 = "z3";


		ArrayList<String> x = new ArrayList<String>(Arrays.asList(new String[]{x1, x2, x3}));
		ArrayList<String> y = new ArrayList<String>(Arrays.asList(new String[]{y1, y2, y3}));
		ArrayList<String> z = new ArrayList<String>(Arrays.asList(new String[]{z1, z2, z3}));
		
		List<List<String>> input = new ArrayList<List<String>>();
		input.add(x);
		input.add(y);
		input.add(z);

		TupleGenerator<String> gen = new TupleGenerator<String>();
		Set<List<String>> pairs = gen.getNTuples(input, 2);

		Set<List<String>> expectedPairs = new HashSet<List<String>>();
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x1, y1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x1, y2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x1, y3})));
		
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x1, z1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x1, z2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x1, z3})));
		
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x2, y1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x2, y2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x2, y3})));
		                                                                    
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x2, z1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x2, z2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x2, z3})));
		
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x3, y1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x3, y2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x3, y3})));
		                                                                    
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x3, z1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x3, z2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{x3, z3})));
		
		
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y1, z1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y1, z2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y1, z3})));
		
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y2, z1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y2, z2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y2, z3})));
		
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y3, z1})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y3, z2})));
		expectedPairs.add(new ArrayList<String>(Arrays.asList(new String[]{y3, z3})));

		assertTrue(pairs.containsAll(expectedPairs));
		
	}

}
