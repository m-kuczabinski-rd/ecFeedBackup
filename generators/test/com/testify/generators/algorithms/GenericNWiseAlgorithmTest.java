package com.testify.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.testify.ecfeed.api.IConstraint;

public class GenericNWiseAlgorithmTest {

	@Test
	public void testGeneratorNoConstraints() {
		String x1 = "x1";
		String x2 = "x2";
		String x3 = "x3";

		String y1 = "y1";
		String y2 = "y2";
		String y3 = "y3";

		String z1 = "z1";
		String z2 = "z2";
		String z3 = "z3";

		String u1 = "u1";
		String u2 = "u2";
		String u3 = "u3";

		ArrayList<String> x = new ArrayList<String>(Arrays.asList(new String[]{x1, x2, x3}));
		ArrayList<String> y = new ArrayList<String>(Arrays.asList(new String[]{y1, y2, y3}));
		ArrayList<String> z = new ArrayList<String>(Arrays.asList(new String[]{z1, z2, z3}));
		ArrayList<String> u = new ArrayList<String>(Arrays.asList(new String[]{u1, u2, u3}));
		
		List<List<String>> input = new ArrayList<List<String>>();
		input.add(x);
		input.add(y);
		input.add(z);
		input.add(u);
		
		GenericNWiseAlgorithm<String> oneWiseAlgorithm = new GenericNWiseAlgorithm<>(1);
		GenericNWiseAlgorithm<String> twoWiseAlgorithm = new GenericNWiseAlgorithm<>(2);
		GenericNWiseAlgorithm<String> threeWiseAlgorithm = new GenericNWiseAlgorithm<>(3);
		GenericNWiseAlgorithm<String> fourWiseAlgorithm = new GenericNWiseAlgorithm<>(4);
		
		Collection<IConstraint<String>> constraints = new HashSet<IConstraint<String>>();
		
		Set<List<String>> oneWiseSet = oneWiseAlgorithm.generate(input, constraints);
		Set<List<String>> twoWiseSet = twoWiseAlgorithm.generate(input, constraints);
		Set<List<String>> threeWiseSet = threeWiseAlgorithm.generate(input, constraints);
		Set<List<String>> fourWiseSet = fourWiseAlgorithm.generate(input, constraints);

		System.out.println("1-wise: " + oneWiseSet.toString());
		System.out.println("2-wise: " + twoWiseSet.toString());
		System.out.println("3-wise: " + threeWiseSet.toString());
		System.out.println("4-wise: " + fourWiseSet.toString());

		TupleGenerator<String> tupleGenerator = new TupleGenerator<String>();
		Set<List<String>> oneTuples = tupleGenerator.getNTuples(input, 1);
		Set<List<String>> twoTuples = tupleGenerator.getNTuples(input, 2);
		Set<List<String>> threeTuples = tupleGenerator.getNTuples(input, 3);
		Set<List<String>> fourTuples = tupleGenerator.getNTuples(input, 4);
		
		System.out.println("Checking 1-wise");
		System.out.println("Possible " + oneTuples.size() + " tuples: " + oneTuples.toString());
		assertTrue(containsAllTuples(oneWiseSet, oneTuples));
		System.out.println("Generated " + oneWiseSet.size() + " combinations\n");

		System.out.println("Checking 2-wise");
		System.out.println("Possible " + twoTuples.size() + " tuples: " + twoTuples.toString());
		assertTrue(containsAllTuples(twoWiseSet, twoTuples));
		System.out.println("Generated " + twoWiseSet.size() + " combinations\n");

		System.out.println("Checking 3-wise");
		System.out.println("Possible " + threeTuples.size() + " tuples: " + threeTuples.toString());
		assertTrue(containsAllTuples(threeWiseSet, threeTuples));
		System.out.println("Generated " + threeWiseSet.size() + " combinations\n");

		System.out.println("Checking 4-wise");
		System.out.println("Possible " + fourTuples.size() + " tuples: " + fourTuples.toString());
		assertTrue(containsAllTuples(fourWiseSet, fourTuples));
		System.out.println("Generated " + fourWiseSet.size() + " combinations\n");

	}

	private boolean containsAllTuples(Set<List<String>> set, Set<List<String>> tuples) {
		for(List<String> vector : set){
			String message = "Vector " + vector.toString() + " contains: ";
			Iterator<List<String>> it = tuples.iterator();
			while(it.hasNext()){
				List<String> tuple = it.next();
				if(vector.containsAll(tuple)){
					message += tuple.toString() + " ";
					it.remove();
				}
			}
			System.out.println(message);
		}
		return tuples.size() == 0;
	}
}
