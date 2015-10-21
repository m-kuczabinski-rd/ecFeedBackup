package com.testify.ecfeed.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class RBOptimalFullCoverageNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

	protected ArrayList<Parameter> inputs = new ArrayList<>();
	protected Collection<IConstraint<E>> myConstraints;

	public RBOptimalFullCoverageNWiseAlgorithm(int n) {
		super(n, 100);
	}

	@Override
	public void initialize(List<List<E>> input, Collection<IConstraint<E>> constraints) throws GeneratorException {
		super.initialize(input, constraints);

		myConstraints = constraints;

		for (int i = 0; i < input.size(); i++) {
			List<E> item = input.get(i);
			inputs.add(new Parameter(i, item.size()));
		}

		/* Parameters at higher indices have larger domain sizes. */
		Collections.sort(inputs);
	}

	@Override
	public List<E> getNext() throws GeneratorException {

		// TODO Auto-generated method stub
		return null;
	}

	public List<List<E>> generateAll() throws GeneratorException {
		List<List<E>> firstNTupels = getFirstNTupels();
		List<List<E>> originalIn = getInput();

		int cnt = inputs.size();
		List<List<Integer>> nMinus1prefixes = generateNminus1ParamCombinations();

		for (int i = (cnt - 1 - N); i >= 0; i--) {
			List<List<Integer>> cart = getRelevantTuples(nMinus1prefixes, i);
			int expected = getExpectedNumberOfTuples(i);
			Set<List<E>> foundTuples = new HashSet<>();

			for (int j = 0; j < firstNTupels.size(); j++) {

				if (firstNTupels.get(j).get(i) == null)
					firstNTupels.get(j).add(originalIn.get(inputs.get(i).index).get(0));

				checkCombs: for (List<Integer> c : cart) {
					List<E> remainingVals = new ArrayList<>(originalIn.get(inputs.get(i).index));

					for (int l = 0; l < firstNTupels.size(); l++) {
						if (compareValueTupels(firstNTupels.get(j), firstNTupels.get(l), c)) {
							if (firstNTupels.get(l).get(i) == null)
								firstNTupels.get(l).add(remainingVals.remove(0));
							else
								remainingVals.remove(firstNTupels.get(l).get(i));
							foundTuples.add(newNtuple(firstNTupels.get(l), c, i));
							if (remainingVals.size() == 0)
								break checkCombs;
						}
					}
				}
			}
			if (foundTuples.size() < expected)
				createNewFullTuples(firstNTupels);

		}

		return firstNTupels;
	}

	private void createNewFullTuples(List<List<E>> firstNTupels) {
		// TODO Auto-generated method stub

	}

	private List<E> newNtuple(List<E> list, List<Integer> c, int i) {
		// TODO Auto-generated method stub

		// Extract the newly generated tuples
		return null;
	}

	protected boolean compareValueTupels(List<E> list, List<E> list2, List<Integer> c) {
		for (Integer ind : c) {
			if (ind != 0)
				if (!list.get(ind).equals(list.get(ind)))
					return false;
		}
		return true;
	}

	private List<List<Integer>> getRelevantTuples(List<List<Integer>> nMinus1prefixes, int i) {
		// TODO Auto-generated method stub

		// gets a subset of nMinus1prefixes, which contains only tuples in which
		// the lower i parameters don't appear.
		return null;
	}

	private int getExpectedNumberOfTuples(int i) {
		// TODO Auto-generated method stub

		// Should be reuseable from super classes
		return 0;
	}

	private List<List<Integer>> generateNminus1ParamCombinations() {
		// TODO Auto-generated method stub

		// Should generate all parameter combinations of size N-1
		// TODO think of the format.
		return null;
	}

	public List<List<E>> getFirstNTupels() throws GeneratorException {

		List<List<E>> startingList = new ArrayList<List<E>>();
		List<List<E>> inp = getInput();

		for (int i = 0; i < N; i++) {
			startingList.add(inp.get(inputs.get(inputs.size() - i - 1).index));
			System.out.println(inp.get(inputs.get(inputs.size() - i - 1).index).size());
		}

		CartesianProductAlgorithm<E> alg = new CartesianProductAlgorithm<>();
		alg.initialize(startingList, myConstraints);

		List<List<E>> allCombs = new ArrayList<>();
		List<E> comb = alg.getNext();
		while (comb != null) {
			allCombs.add(comb);
			comb = alg.getNext();
		}

		return allCombs;
	}

}

class Parameter implements Comparable<Parameter> {

	int size = -1;
	int index = -1;

	public Parameter(int ind, int s) {
		this.size = s;
		this.index = ind;
	}

	@Override
	public int compareTo(Parameter o) {
		return this.size - o.size;
	}

}
