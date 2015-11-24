package com.testify.ecfeed.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

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

	private String toString(List<Variable<E>> nTuple) {
		String str = "< ";
		for (Variable<E> var : nTuple)
			str += "(" + var.dimention + ", " + var.selectedFeature + ") ";
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
				if (!test.get(var.dimention).equals(var.selectedFeature)) {
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
		List<E> improvedTest = randomTest;
		// TODO Auto-generated method stub
		return improvedTest;
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
					candidate.set(var.dimention, var.selectedFeature);

			} while (!checkConstraints(candidate) && ++itr < CONSISTENCY_LOOP_LIM);

			if (itr < CONSISTENCY_LOOP_LIM) {
				if(fRemainingTuples.size() == 0)
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

	@SuppressWarnings("unchecked")
	private Set<List<Variable<E>>> getAllNTuples() throws GeneratorException {

		Set<List<Integer>> allCombs = getAllDimCombs();
		Set<List<Variable<E>>> allNTuples = new HashSet<>();

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
		return allNTuples;
	}

	public static class Variable<E> {
		// list of features of this variable
		E selectedFeature;
		// dimension of this variable
		int dimention;

		public Variable(int d, E f) {
			dimention = d;
			selectedFeature = f;
		}
	}

	protected Set<List<Integer>> getAllDimCombs() {
		if (allDimCombs == null)
			allDimCombs = getAllDimensionCombinations();
		return allDimCombs;
	}

}
