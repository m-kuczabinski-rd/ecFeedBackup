package com.testify.ecfeed.core.generators.testutils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.core.generators.algorithms.Tuples;

public class AlgorithmTestUtils {

	public static long calculateTotalTuples(List<List<String>> input, int n) {
		long totalWork = 0;
		Tuples<List<String>> tuples = new Tuples<List<String>>(input, n);
		while (tuples.hasNext()) {
			long combinations = 1;
			List<List<String>> tuple = tuples.next();
			for (List<String> parameter : tuple) {
				combinations *= parameter.size();
			}
			totalWork += combinations;
		}
		return totalWork;
	}

	public static int calculateCoveredTuples(List<List<String>> suite,
			List<List<String>> input, int n) {
		Set<List<String>> tuplesCovered = new HashSet<List<String>>();
		for (List<String> testCase : suite) {
			Tuples<String> tuples = new Tuples<String>(testCase, n);
			tuplesCovered.addAll(tuples.getAll());
		}
		return tuplesCovered.size();
	}

}
