package com.testify.ecfeed.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.management.RuntimeErrorException;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class RandomizedNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

	final private int RANDOM_TEST_TRIES = 10;

	private Set<List<Integer>> allDimCombs = null;

	private Set<List<Variable<E>>> fRemainingTuples = null;

	final private int CONSISTENCY_LOOP_LIM = 10;

	public RandomizedNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);
	}

	@Override
	public void reset() {
		try {
			fRemainingTuples = getAllNTuples();
		} catch (GeneratorException e) {
			throw new RuntimeErrorException(new Error(e));
		}
		super.reset();
	}

	@Override
	public List<E> getNext() throws GeneratorException {

		if (fRemainingTuples.size() == 0)
			return null;

		while (true) {

			List<Variable<E>> nTuple = fRemainingTuples.iterator().next();
			fRemainingTuples.remove(nTuple);
			List<E> randomTest = generateRandomTest(nTuple);

			if (randomTest != null) {
				if (fRemainingTuples.size() == 0)
					return randomTest;

				List<E> improvedTest = improveCoverageOfTest(randomTest, nTuple);
				removeCoveredNTuples(improvedTest);
				return improvedTest;
			} else {
				// GeneratorException.report("Cannot generate test for " +
				// toString(nTuple));
				System.out.println("randomTest is nul!!!" + fRemainingTuples.size());
				fRemainingTuples.add(nTuple);
			}
		}
	}

	String toString(List<Variable<E>> nTuple) {
		String str = "< ";
		for (Variable<E> var : nTuple)
			str += "(" + var.dimension + ", " + var.selectedFeature + ") ";
		return str + ">";
	}

	/*
	 * Removes from allDesiredTuples all the nTuples that are covered by
	 * improvedTest
	 * 
	 * @param improvedTest
	 */
	private void removeCoveredNTuples(List<E> test) {
		Set<List<Variable<E>>> coveredTuples = getCoveredNTuples(test);
		fRemainingTuples.removeAll(coveredTuples);
	}

	private Set<List<Variable<E>>> getCoveredNTuples(List<E> test) {
		int k = allDimCombs.size();
		Set<List<Variable<E>>> coveredTuples = new HashSet<>();
		for (List<Variable<E>> nTuple : fRemainingTuples) {
			if (k == 0)
				break;

			boolean isCovered = true;
			for (Variable<E> var : nTuple)
				if (!test.get(var.dimension).equals(var.selectedFeature)) {
					isCovered = false;
					break;
				}
			if (isCovered) {
				k--;
				coveredTuples.add(nTuple);
			}
		}
		return coveredTuples;
	}

	private List<E> improveCoverageOfTest(List<E> randomTest, List<Variable<E>> nTuple) {
		/*
		 * while it is possible to improve coverage make a random ordering of
		 * modifiable indices for each index in the list check all available
		 * values for that index and choose the best if the coverage is
		 * improved, use the newly generated tuple (test)
		 */
		List<E> improvedTest = randomTest;

		Map<Integer, Integer> dims = getModifiableDimensions(randomTest, nTuple);
		boolean progress;

		do {
			progress = false;

			// shuffle dims
			List<Integer> mDims = new ArrayList<>(dims.values());
			Collections.shuffle(mDims);

			for (int i = 0; i < mDims.size(); i++) {
				int coverage;
				int bestCov = getCoverage(improvedTest);
				int dim = mDims.get(i);
				List<E> input = getInput().get(dim);
				List<E> bests = new ArrayList<>();
				bests.add(improvedTest.get(dim));
				for (E feature : input) {
					improvedTest.set(dim, feature);
					if (checkConstraints(improvedTest)) {
						coverage = getCoverage(improvedTest);

						if (coverage >= bestCov) {
							if (coverage > bestCov) {
								progress = true;
								bestCov = coverage;
								bests.clear();
							}
							bests.add(feature);
						}
					}
				}
				// use the best feature
				improvedTest.set(dim, bests.get((new Random().nextInt(bests.size()))));
			}
		} while (progress);

		return improvedTest;
	}

	private Map<Integer, Integer> getModifiableDimensions(List<E> randomTest, List<Variable<E>> nTuple) {
		// make a list of dimensions that do not appear in nTuple
		Map<Integer, Integer> dims = new HashMap<>();
		for (int i = 0; i < randomTest.size(); i++)
			dims.put(i, i);

		for (int i = 0; i < nTuple.size(); i++)
			dims.remove(nTuple.get(i).dimension);
		return dims;
	}

	/*
	 * Randomly generates a test that contains 'nTuple' and satisfies all the
	 * constraints.
	 */
	private List<E> generateRandomTest(List<Variable<E>> nTuple) {

		List<E> bestTest = null;
		int bestCov = 1;
		List<List<E>> tInput = getInput();

		for (int r = 0; r < RANDOM_TEST_TRIES; r++) {
			List<E> candidate = null;
			int itr = 0;
			do {
				candidate = new ArrayList<>();
				for (int i = 0; i < tInput.size(); i++) {
					List<E> features = tInput.get(i);
					candidate.add(features.get((new Random()).nextInt(features.size())));
				}

				// plug the tuple into the randomly generated test
				for (Variable<E> var : nTuple)
					candidate.set(var.dimension, var.selectedFeature);

			} while (!checkConstraints(candidate) && ++itr < CONSISTENCY_LOOP_LIM);

			if (itr < CONSISTENCY_LOOP_LIM) {
				if (fRemainingTuples.size() == 0)
					return candidate;
				// one extra point for the current tuple
				int cov = getCoverage(candidate) + 1;
				// System.out.println("[RB] cov: " + cov);
				if (cov >= bestCov) {
					bestTest = candidate;
					bestCov = cov;
				}
			}
		}

		return bestTest;
	}

	private int getCoverage(List<E> test) {
		return getCoveredNTuples(test).size();
	}

	private Set<List<Integer>> getAllDimensionCombinations() {
		int dimCount = getInput().size();
		List<Integer> dimentions = new ArrayList<>();
		for (int i = 0; i < dimCount; i++)
			dimentions.add(new Integer(i));

		return (new Tuples<Integer>(dimentions, N)).getAll();
	}

	private Set<List<Variable<E>>> getAllNTuples() throws GeneratorException {

		Set<List<Integer>> allCombs = getAllDimCombs();
		Set<List<Variable<E>>> allNTuples = new HashSet<>();

		for (List<Integer> comb : allCombs) {
			List<List<Variable<E>>> tempIn = new ArrayList<>();
			for (int i = 0; i < comb.size(); i++) {
				List<Variable<E>> values = new ArrayList<>();
				for (E e : getInput().get(comb.get(i)))
					values.add(new Variable<E>(comb.get(i), e));
				tempIn.add(values);
			}

			CartesianProductAlgorithm<Variable<E>> cartAlg = new CartesianProductAlgorithm<>();
			cartAlg.initialize(tempIn, new HashSet<IConstraint<Variable<E>>>());
			List<Variable<E>> tuple = null;
			while ((tuple = cartAlg.getNext()) != null) {
				// Generate a full tuple from this nTuple to make sure that it is consistent with the constraints
				List<E> fullTuple = new ArrayList<>();
				for(int i=0; i< getInput().size(); i++)
					fullTuple.add(null);
				for(Variable<E> var : tuple)
					fullTuple.set(var.dimension, var.selectedFeature);
				if(checkConstraints(fullTuple))
					allNTuples.add(tuple);
			}
		}

		return allNTuples;
	}

	@SuppressWarnings("unchecked")
	void generateNTuples(Set<List<Integer>> allCombs, Set<List<Variable<E>>> allNTuples) throws GeneratorException {
		for (List<Integer> comb : allCombs) {
			List<List<E>> tempIn = new ArrayList<>();
			for (int i = 0; i < comb.size(); i++)
				tempIn.add(getInput().get(comb.get(i)));

			CartesianProductAlgorithm<E> cartAlg = new CartesianProductAlgorithm<>();
			cartAlg.initialize(tempIn, (Collection<IConstraint<E>>) getConstraints());
			List<E> tuple = null;
			while ((tuple = cartAlg.getNext()) != null) {
				List<Variable<E>> extendedTuple = new ArrayList<>();
				for (int i = 0; i < comb.size(); i++)
					extendedTuple.add(new Variable<>(comb.get(i), tuple.get(i)));
				allNTuples.add(extendedTuple);
			}
		}
	}

	public static class Variable<E> {
		// list of features of this variable
		E selectedFeature;
		// dimension of this variable
		int dimension;

		public Variable(int d, E f) {
			dimension = d;
			selectedFeature = f;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Variable))
				return false;

			Variable<?> var = (Variable<?>) obj;
			return var.dimension == this.dimension && this.selectedFeature.equals(var.selectedFeature);
		}

	}

	protected Set<List<Integer>> getAllDimCombs() {
		if (allDimCombs == null)
			allDimCombs = getAllDimensionCombinations();
		return allDimCombs;
	}

}
