package com.testify.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.core.generators.api.GeneratorException;
import com.testify.ecfeed.core.generators.api.IConstraint;

public class RBOptimalFullCoverageNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

	protected ArrayList<Parameter> inputs = new ArrayList<>();
	protected Collection<IConstraint<E>> myConstraints;

	private List<List<E>> all = null;
	private int next = -1;

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

		Collections.sort(inputs);
		/* Parameters with smaller indices have larger domain sizes. */
		Collections.reverse(inputs);
	}

	@Override
	public List<E> getNext() throws GeneratorException {
		if (all == null) {
			all = generateAll();
			next = 0;
		}
		return all.get(next++);
	}

	/**
	 * Generates all tuples that together cover all N-Wise combinations of input
	 * values. The generated set of tuples is intended to be optimal (i.e., the
	 * returned set contains the minimum number of tuples that can provide the
	 * required coverage of N-Wise combinations.)
	 * 
	 * @return
	 * @throws GeneratorException
	 */
	public List<List<E>> generateAll() throws GeneratorException {
		List<List<E>> firstNTupels = getFirstNTupels();
		List<List<E>> originalIn = getInput();

		int cnt = inputs.size();
		Set<List<Boolean>> nM1paramIndCombs = generateNm1ParamIndexCombinations();

		nextColumn: for (int i = N; i < cnt; i++) {
			Set<List<Boolean>> filteredIndCombs = getSubset(nM1paramIndCombs, i);
			int expected = getExpectedNumberOfTuples(filteredIndCombs, i);
			Set<List<E>> foundTuples = new HashSet<>();
			
			for (int j = 0; j < firstNTupels.size(); j++) {
				if (firstNTupels.get(j).size() == i) {
					firstNTupels.get(j).add(originalIn.get(inputs.get(i).index).get(0));
				}
				else if (firstNTupels.get(j).size() < i || firstNTupels.get(j).size() > i + 1)
					throw new RuntimeException(
							"Unexpected number of elements in the tuple. N: " + N + ", Expected tuple size: " + i
									+ " or " + (i + 1) + ", Actual tuple size: " + firstNTupels.get(j).size());

				for (List<Boolean> c : filteredIndCombs) {
					List<E> remainingVals = new ArrayList<>(originalIn.get(inputs.get(i).index));
					
					for (int l = 0; l < firstNTupels.size(); l++) {
						if (compareValuesAtIndices(firstNTupels.get(j), firstNTupels.get(l), c)) {
							if (firstNTupels.get(l).size() == i) {
								E val = remainingVals.remove(0);
								firstNTupels.get(l).add(val);
								foundTuples.addAll(getTuplesAtIndices(firstNTupels.get(l), filteredIndCombs, i));
								if (foundTuples.size() >= expected) {
									// TODO fill the rest of this column with
									// appropriate values
									continue nextColumn; // Continue to the next
															// column
								}
							} else if (firstNTupels.get(l).size() == i + 1)
								remainingVals.remove(firstNTupels.get(l).get(i));
							else
								throw new RuntimeException("Too few elements in the tuple. N: " + N
										+ ", Expected tuple size: " + i + " or " + (i + 1) + ", Actual tuple size: "
										+ firstNTupels.get(l).size());
							if (remainingVals.size() == 0)
								break; // Continue to the next c
						}
					}
					if (remainingVals.size() != 0)
						createNewFullTuples(firstNTupels, j, c, i, remainingVals);
				}
			}
		}

		return firstNTupels;
	}

	/**
	 * 
	 * @param list
	 * @param indCombs
	 * @param lastInd
	 * @return
	 */
	private List<List<E>> getTuplesAtIndices(List<E> list, Set<List<Boolean>> indCombs, int lastInd) {
		List<List<E>> tuples = new ArrayList<>();
		for (List<Boolean> c : indCombs) {
			List<E> tuple = new ArrayList<>();
			for (int ind = 0; ind < c.size(); ind++) {
				if (!c.get(ind))
					tuple.add(null);
				else
					tuple.add(list.get(ind));
			}
			tuple.add(list.get(lastInd));
			tuples.add(tuple);
		}
		return tuples;
	}

	/**
	 * Returns a subset of the input set in which only indices smaller than i
	 * are set to true.
	 * 
	 * @param set
	 * @param i
	 * @return
	 */
	/*
	 * I have intentionally used no access modifier for this method to make it
	 * (package-)private to make accessible in the test class.
	 */
	Set<List<Boolean>> getSubset(Set<List<Boolean>> set, int i) {
		Set<List<Boolean>> selection = new HashSet<>();

		for (List<Boolean> comb : set) {
			int indCnt = 0;
			for (int ind = 0; ind < i; ind++) {
				if (comb.get(ind))
					indCnt++;
			}
			if (indCnt == N - 1)
				selection.add(comb);
		}
		return selection;
	}

	/**
	 * 
	 * @param indCombs
	 * @param i
	 * @return
	 */
	/*
	 * I have intentionally used no access modifier for this method to make it
	 * (package-)private to make accessible in the test class.
	 */
	int getExpectedNumberOfTuples(Set<List<Boolean>> indCombs, int i) {
		int val = 0;
		for (List<Boolean> l : indCombs) {
			int num = 1;
			for (int ind = 0; ind < l.size(); ind++) {
				if (l.get(ind))
					num *= inputs.get(ind).size;
			}
			val += num;
		}
		return val*inputs.get(i).size;
	}

	/**
	 * 
	 * @param list
	 * @param list2
	 * @param c
	 * @return
	 */
	protected boolean compareValuesAtIndices(List<E> list, List<E> list2, List<Boolean> c) {
		
		for (int ind = 0; ind < c.size(); ind++) {
			if (c.get(ind))
				if (!(list.get(ind).equals(list2.get(ind))))
					return false;
		}
		return true;
	}

	public Set<List<Boolean>> generateNm1ParamIndexCombinations() {

		List<Integer> tempInput = new ArrayList<>();
		for (int i = 0; i < inputs.size(); i++)
			tempInput.add(i);

		Tuples<Integer> tupleGen = new Tuples<>(tempInput, N - 1);
		Set<List<Integer>> intermediateRes = tupleGen.getAll();

		Set<List<Boolean>> res = new HashSet<>();

		for (List<Integer> x : intermediateRes) {
			List<Boolean> tup = new ArrayList<>();
			for (int i = 0; i < inputs.size(); i++) {
				if (x.contains(i))
					tup.add(true);
				else
					tup.add(false);
			}
			res.add(tup);
		}
		return res;
	}

	public List<List<E>> getFirstNTupels() throws GeneratorException {

		List<List<E>> startingList = new ArrayList<List<E>>();
		List<List<E>> inp = getInput();

		for (int i = 0; i < N; i++) {
			startingList.add(inp.get(inputs.get(i).index));
			//System.out.println(inp.get(inputs.get(inputs.size() - i - 1).index).size());
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

	private void createNewFullTuples(List<List<E>> firstNTupels, int j, List<Boolean> c, int ind,
			List<E> remainingVals) {
		// TODO Auto-generated method stub

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
