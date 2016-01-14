package com.testify.ecfeed.generators.algorithms;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.testify.ecfeed.generators.algorithms.RandomizedNWiseAlgorithm.Variable;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class RandomizedNWiseAlgorithmTest {
	private final Collection<IConstraint<Integer>> EMPTY_CONSTRAINTS = new HashSet<IConstraint<Integer>>();

	int n, inputCount;
	RandomizedNWiseAlgorithm<Integer> alg;

	final int SAMPLE_COUNT = 100;
	final int TRIES_PER_SAMPLE = 10;

	@Before
	public void initialize() {
		n = 3;
		inputCount = 4;
		initialize(EMPTY_CONSTRAINTS);
	}

	private void initialize(Collection<IConstraint<Integer>> constraints) {
		alg = new RandomizedNWiseAlgorithm<>(n, 100);

		List<List<Integer>> input = new ArrayList<>();
		for (int i = 0; i < inputCount; i++) {
			List<Integer> in = new ArrayList<>();
			in.add(0);
			in.add(1);
			input.add(in);
		}

		try {
			alg.initialize(input, constraints);
		} catch (GeneratorException e) {
			fail("unexpected exception during initialization: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void getNextTest() {
		try {
			ArrayList<String> expected = new ArrayList<>(Arrays.asList(new String[] { "< 0 0 0 - >", "< 0 0 1 - >",
					"< 0 1 0 - >", "< 0 1 1 - >", "< 1 0 0 - >", "< 1 0 1 - >", "< 1 1 0 - >", "< 1 1 1 - >",
					"< 0 0 - 0 >", "< 0 0 - 1 >", "< 0 1 - 0 >", "< 0 1 - 1 >", "< 1 0 - 0 >", "< 1 0 - 1 >",
					"< 1 1 - 0 >", "< 1 1 - 1 >", "< 0 - 0 0 >", "< 0 - 0 1 >", "< 0 - 1 0 >", "< 0 - 1 1 >",
					"< 1 - 0 0 >", "< 1 - 0 1 >", "< 1 - 1 0 >", "< 1 - 1 1 >", "< - 0 0 0 >", "< - 0 0 1 >",
					"< - 0 1 0 >", "< - 0 1 1 >", "< - 1 0 0 >", "< - 1 0 1 >", "< - 1 1 0 >", "< - 1 1 1 >" }));
			List<Integer> tuple = alg.getNext();
			int cnt = 0;
			do {
				cnt++;
				assertNotEquals(0, expected.size());
				ArrayList<String> str = tupleToStringCombinations(tuple, inputCount, n);
				@SuppressWarnings("unchecked")
				ArrayList<String> strCopy = (ArrayList<String>) str.clone();
				// 'tuple' should cover at least one new N-tuple
				assertTrue(str.removeAll(expected));
				expected.removeAll(strCopy);
			} while ((tuple = alg.getNext()) != null);
			System.out.println("Number of tests: " + cnt);
			assertNull("still expecting " + expected.size() + " more n-tupels", alg.getNext());
			assertEquals(0, expected.size());
		} catch (GeneratorException e) {
			fail("unexpected exception when calling getNext: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void getNextTestWithConstraints() {

		IConstraint<Integer> myConstraint = new IConstraint<Integer>() {

			@Override
			public boolean evaluate(List<Integer> values) {
				return !(values.get(0) == 1 && values.get(1) == 1);
			}

			@Override
			public boolean adapt(List<Integer> values) {
				return true;
			}
		};

		Collection<IConstraint<Integer>> constraints = new HashSet<IConstraint<Integer>>();
		constraints.add(myConstraint);
		initialize(constraints);

		Method method;
		try {
			method = RandomizedNWiseAlgorithm.class.getDeclaredMethod("getAllNTuples");
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<Boolean, Set<List<Variable<Integer>>>> result = (Map<Boolean, Set<List<Variable<Integer>>>>) method.invoke(alg);
			
			assertNotNull(result);
			assertEquals("The expected number of N-Tuples is 28", 28, result.get(true).size() + result.get(null).size());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			fail(e1.getMessage());
			e1.printStackTrace();
		}

		try {
			ArrayList<String> expected = new ArrayList<>(Arrays
					.asList(new String[] { "< 0 0 0 - >", "< 0 0 1 - >", "< 0 1 0 - >", "< 0 1 1 - >", "< 1 0 0 - >",
							"< 1 0 1 - >", "< 0 0 - 0 >", "< 0 0 - 1 >", "< 0 1 - 0 >", "< 0 1 - 1 >", "< 1 0 - 0 >",
							"< 1 0 - 1 >", "< 0 - 0 0 >", "< 0 - 0 1 >", "< 0 - 1 0 >", "< 0 - 1 1 >", "< 1 - 0 0 >",
							"< 1 - 0 1 >", "< 1 - 1 0 >", "< 1 - 1 1 >", "< - 0 0 0 >", "< - 0 0 1 >", "< - 0 1 0 >",
							"< - 0 1 1 >", "< - 1 0 0 >", "< - 1 0 1 >", "< - 1 1 0 >", "< - 1 1 1 >" }));
			List<Integer> tuple = alg.getNext();
			int cnt = 0;
			do {
				cnt++;
				assertNotEquals(0, expected.size());
				ArrayList<String> str = tupleToStringCombinations(tuple, inputCount, n);
				@SuppressWarnings("unchecked")
				ArrayList<String> strCopy = (ArrayList<String>) str.clone();
				// 'tuple' should cover at least one new N-tuple
				assertTrue(str.removeAll(expected));
				expected.removeAll(strCopy);
			} while ((tuple = alg.getNext()) != null);
			System.out.println("Number of tests: " + cnt);
			assertEquals("still expecting " + expected.size() + " more n-tupels", 0, expected.size());
		} catch (GeneratorException e) {
			fail("unexpected exception when calling getNext: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * 'tuple' should be of size 'inCnt'. This method finds all sub-tuples of
	 * size 'm' (m-tuples) in 'tuple', and make a list of strings, where each
	 * string represents one sub-tuple.
	 */
	private ArrayList<String> tupleToStringCombinations(List<Integer> tuple, int inCnt, int m) {
		List<Integer> positions = new ArrayList<>();
		for (int i = 0; i < inCnt; i++)
			positions.add(new Integer(i));

		Set<List<Integer>> allCombs = (new Tuples<Integer>(positions, m)).getAll();
		ArrayList<String> result = new ArrayList<>();

		for (List<Integer> comb : allCombs) {
			String str = "< ";
			for (int i = 0; i < inCnt; i++)
				if (comb.contains(i))
					str += tuple.get(i) + " ";
				else
					str += "- ";
			str += ">";
			result.add(str);
		}
		return result;
	}

	@Test
	public void getAllDimensionCombinationsTest() {
		try {
			Method method = RandomizedNWiseAlgorithm.class.getDeclaredMethod("getAllDimensionCombinations");
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			Set<List<Integer>> combs = (Set<List<Integer>>) method.invoke(alg);
			assertEquals("possible combinations of 3 out of 4 is 4", combs.size(), 4);

			String[] combStrs = { "", "", "", "" };
			int i = 0;
			for (List<Integer> comb : combs) {
				String str = tupleToString(comb);
				combStrs[i++] = str;
			}

			List<String> expected = new ArrayList<>();
			expected.addAll(Arrays.asList(new String[] { "< 0 1 2 >", "< 0 2 3 >", "< 0 1 3 >", "< 1 2 3 >" }));
			for (String str : combStrs)
				assertTrue(expected.contains(str));

		} catch (NoSuchMethodException e) {
			fail("Cannot find method named 'getAllDimensionCombinations' in class RandomizedNWiseAlgorithm");
			e.printStackTrace();
		} catch (SecurityException e) {
			fail("Unexpected security exception happened when testing RandomizedNWiseAlgorithm.");
			e.printStackTrace();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception when invoking getAllDimentionCombinations.");
			e.printStackTrace();
		}
	}

	private String tupleToString(List<Integer> comb) {
		String str = "< ";
		for (Integer v : comb)
			str += v + " ";
		str += ">";
		return str;
	}

	@Test
	public void getAllNTuplesTest() {

		try {
			Method method = RandomizedNWiseAlgorithm.class.getDeclaredMethod("getAllNTuples");
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<Boolean, Set<List<Variable<Integer>>>> result = (Map<Boolean, Set<List<Variable<Integer>>>>) method.invoke(alg);
			assertNotNull(result);
			assertEquals("The expected number of N-Tuples is 32", 32, result.get(true).size());

			ArrayList<String> expected = new ArrayList<>(Arrays.asList(new String[] { "< 0 0 0 - >", "< 0 0 1 - >",
					"< 0 1 0 - >", "< 0 1 1 - >", "< 1 0 0 - >", "< 1 0 1 - >", "< 1 1 0 - >", "< 1 1 1 - >",
					"< 0 0 - 0 >", "< 0 0 - 1 >", "< 0 1 - 0 >", "< 0 1 - 1 >", "< 1 0 - 0 >", "< 1 0 - 1 >",
					"< 1 1 - 0 >", "< 1 1 - 1 >", "< 0 - 0 0 >", "< 0 - 0 1 >", "< 0 - 1 0 >", "< 0 - 1 1 >",
					"< 1 - 0 0 >", "< 1 - 0 1 >", "< 1 - 1 0 >", "< 1 - 1 1 >", "< - 0 0 0 >", "< - 0 0 1 >",
					"< - 0 1 0 >", "< - 0 1 1 >", "< - 1 0 0 >", "< - 1 0 1 >", "< - 1 1 0 >", "< - 1 1 1 >" }));

			ArrayList<String> actual = new ArrayList<>();
			for (List<Variable<Integer>> ntup : result.get(true))
				actual.add(ntupleToString(ntup, inputCount));

			assertTrue(expected.containsAll(actual));
			assertTrue(actual.containsAll(expected));

		} catch (NoSuchMethodException | SecurityException e) {
			fail("Exception happened when looking for method 'getAllNTuples'.");
			e.printStackTrace();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception happened when invoking method 'getAllNTuples'.");
			e.printStackTrace();
		}
	}

	private String ntupleToString(List<Variable<Integer>> ntup, int count) {
		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < count; i++)
			list.add(-1);
		for (Variable<Integer> v : ntup)
			list.set(v.dimension, v.selectedFeature);

		String str = "< ";
		for (Integer v : list)
			if (v < 0)
				str += "- ";
			else
				str += v + " ";
		str += ">";
		return str;
	}

	@Test
	public void generateRandomTestTest() {
		/*
		 * Because of the randomness in the nature of the algorithm, a
		 * statistical test is performed.
		 */

		int n = 3, inputCount = 6;
		RandomizedNWiseAlgorithm<Integer> alg = new RandomizedNWiseAlgorithm<>(n, 100);

		List<List<Integer>> input = new ArrayList<>();
		for (int i = 0; i < inputCount; i++) {
			List<Integer> in = new ArrayList<>(Arrays.asList(new Integer[] { 0, 1 }));
			input.add(in);
		}

		try {
			alg.initialize(input, EMPTY_CONSTRAINTS);
		} catch (GeneratorException e) {
			fail("unexpected exception during initialization: " + e.getMessage());
			e.printStackTrace();
		}

		Random random = new Random();
		List<Variable<Integer>> nTuple = new ArrayList<>();
		Set<Integer> indices = new HashSet<>();
		// sample 'n' distinct integers in the range [0, 'inputCount')
		while (indices.size() != n)
			indices.add(random.nextInt(inputCount));

		for (Integer i : indices)
			nTuple.add(new Variable<Integer>(i, random.nextInt(2)));

		Method method = null;
		try {
			method = RandomizedNWiseAlgorithm.class.getDeclaredMethod("generateRandomTest", List.class);
			method.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			fail("Exception happened when looking for method 'generateRandomTest'");
			e.printStackTrace();
		}

		double[] samples = sampleRandomTests(alg, nTuple, method);

		confidenceTest(n, inputCount, samples);
	}

	private void confidenceTest(int n, int inputCount, double[] samples) {
		double num = Math.pow(2, inputCount - n);
		double mu = (num + 1) / 2;
		double x2 = 0;
		for (int i = 1; i <= num; i++)
			x2 += ((i * i) / num);
		double sigma = Math.sqrt(x2 - mu * mu);
		double err = 2 * sigma / Math.sqrt(TRIES_PER_SAMPLE);

		int cnt = 0;
		for (int i = 0; i < SAMPLE_COUNT; i++)
			if (samples[i] >= (mu - err) && samples[i] <= (mu + err))
				cnt++;

		// TODO: Maybe make it 95% - would cause some failures once in a while
		double requiredAccuracy = 0.9;
		assertTrue("" + (Math.round(10000.0 * cnt / SAMPLE_COUNT) / 100.0)
				+ " %of the cases are in the desired range, but " + Math.round(requiredAccuracy * 100)
				+ "% is required", cnt >= SAMPLE_COUNT * requiredAccuracy);
	}

	private double[] sampleRandomTests(RandomizedNWiseAlgorithm<Integer> alg, List<Variable<Integer>> nTuple,
			Method method) {
		double[] samples = new double[SAMPLE_COUNT];

		for (int i = 0; i < SAMPLE_COUNT; i++) {
			int total = 0;
			for (int j = 0; j < TRIES_PER_SAMPLE; j++) {
				try {
					@SuppressWarnings("unchecked")
					List<Integer> test = (List<Integer>) method.invoke(alg, nTuple);
					for (Variable<Integer> v : nTuple) {
						assertEquals(v.selectedFeature, test.get(v.dimension));
						test.set(v.dimension, -1);
					}

					// assumption: each test represents a binary number
					int val = 0;
					for (int k = 0; k < test.size(); k++) {
						if (test.get(k) >= 0)
							val = val * 2 + test.get(k);
					}
					val++; // counting starts from one, instead of zero
					total += val;

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					fail("Exception happened when invoking method 'generateRandomTest'");
					e.printStackTrace();
				}
			}

			samples[i] = total / (1.0 * TRIES_PER_SAMPLE);
		}
		return samples;
	}

}
