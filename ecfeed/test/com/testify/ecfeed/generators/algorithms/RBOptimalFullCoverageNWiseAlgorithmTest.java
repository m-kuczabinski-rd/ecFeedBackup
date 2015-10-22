package com.testify.ecfeed.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.testify.ecfeed.generators.algorithms.RBOptimalFullCoverageNWiseAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class RBOptimalFullCoverageNWiseAlgorithmTest {

	private final Collection<IConstraint<Integer>> EMPTY_CONSTRAINTS = new HashSet<IConstraint<Integer>>();

	@Test
	public void testGetFirstNTupels() {

		System.out.println("\nTesting getFirstNTupels()");
		List<List<Integer>> input = generateInput(new int[] {2,3,2});

		RBOptimalFullCoverageNWiseAlgorithm<Integer> alg = new RBOptimalFullCoverageNWiseAlgorithm<Integer>(2);
		try {
			alg.initialize(input, EMPTY_CONSTRAINTS);

			List<List<Integer>> res = alg.getFirstNTupels();
			printList(res);

			assertNotNull(res);
			assertEquals(res.size(), 6);

		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Test
	public void testGenerateNm1ParamIndexCombinations() {
		System.out.println("\nTesting generateNm1ParamIndexCombinations()");

		List<List<Integer>> input = generateInput(5, 4);

		RBOptimalFullCoverageNWiseAlgorithm<Integer> alg = new RBOptimalFullCoverageNWiseAlgorithm<Integer>(3);

		try {
			alg.initialize(input, EMPTY_CONSTRAINTS);

			Set<List<Boolean>> res = alg.generateNm1ParamIndexCombinations();

			assertNotNull(res);
			assertEquals(res.size(), 10);

			printList(res);

		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testGetSubset() {
		System.out.println("\nTesting getSubset(Set<List<Boolean>>, int)");

		int inputsCount = 5;
		List<List<Integer>> input = generateInput(inputsCount, 2);

		int N = 3;
		RBOptimalFullCoverageNWiseAlgorithm<Integer> alg = new RBOptimalFullCoverageNWiseAlgorithm<Integer>(N);
		try {
			alg.initialize(input, EMPTY_CONSTRAINTS);
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}

		Set<List<Boolean>> res = alg.generateNm1ParamIndexCombinations();
		for (int i = (inputsCount - 1 - N); i >= 0; i--) {
			Set<List<Boolean>> selection = alg.getSubset(res, i);
			assertNotNull(selection);
			System.out.println("\ni: " + i + ", size: " + selection.size() + ":");
			printList(selection);
		}
	}

	@Test
	public void testGetExpectedNumberOfTuples() {
		System.out.println("\nTesting getExpectedNumberOfTuples(Set<List<Boolean>>, int)");

		List<List<Integer>> input = generateInput(new int[] {2,3,4,2, 2});
		int inputsCount = input.size();

		int N = 3;
		RBOptimalFullCoverageNWiseAlgorithm<Integer> alg = new RBOptimalFullCoverageNWiseAlgorithm<Integer>(N);
		try {
			alg.initialize(input, EMPTY_CONSTRAINTS);
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}

		int[] expected = new int[] {52, 88};
		int ind = 0;
		Set<List<Boolean>> res = alg.generateNm1ParamIndexCombinations();
		for (int i = (inputsCount - 1 - N); i >= 0; i--) {
			Set<List<Boolean>> sel = alg.getSubset(res, i);
			//printList(sel);
			int cnt = alg.getExpectedNumberOfTuples(sel, i);
			System.out.println(cnt);
			assertEquals(expected[ind++], cnt);
		}
		

	}

	private List<List<Integer>> generateInput(int count, int maxSize) {
		List<List<Integer>> input = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			int size = maxSize;
			if (maxSize > 2)
				size = (new Random()).nextInt(maxSize - 2) + 2;
			List<Integer> in = new ArrayList<>();
			for (int j = 0; j < size; j++)
				in.add(j);
			input.add(in);
		}
		return input;
	}
	
	private List<List<Integer>> generateInput(int[] is) {
		List<List<Integer>> input = new ArrayList<>();
		
		for (int i:is) {
			List<Integer> in = new ArrayList<>();
			for (int j = 0; j < i; j++)
				in.add(j);
			input.add(in);
		}
		return input;
	}

	private void printList(List<List<Integer>> res) {
		for (List<Integer> tuple : res) {
			for (Integer i : tuple)
				System.out.print(i + " ");
			System.out.println();
		}
	}

	private void printList(Set<List<Boolean>> res) {
		for (List<Boolean> r : res) {
			for (Boolean b : r)
				System.out.print(b ? "1" : "0");
			System.out.println();
		}
	}
}
