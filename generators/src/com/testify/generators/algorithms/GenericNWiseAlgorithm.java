package com.testify.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.google.common.collect.Sets;
import com.testify.ecfeed.api.IAlgorithm;
import com.testify.ecfeed.api.IConstraint;

public class GenericNWiseAlgorithm<E> implements IAlgorithm<E> {
	private int N;

	public GenericNWiseAlgorithm(int n) {
		N = n;
	}
	
	@Override
	public Set<List<E>> generate(List<List<E>> input,
			Collection<IConstraint<E>> constraints){

		TupleGenerator<E> tupleGenerator = new TupleGenerator<E>();
		Set<List<E>> nTuples = tupleGenerator.getNTuples(input, N);
		Set<List<E>> result = cartesianProduct(input);
		result = applyConstraints(result, constraints);
		result = selectTuplesRepresentation(nTuples, result);
		
		return result;
	}

	private Set<List<E>> applyConstraints(Set<List<E>> input, Collection<IConstraint<E>> constraints) {
		Set<List<E>> result = new HashSet<List<E>>(input);
		for(IConstraint<E> constraint : constraints){
			Iterator<List<E>> it = result.iterator();
			while(it.hasNext()){
				if(constraint.evaluate(it.next()) == false){
					it.remove();
				}
			}
		}
		return result;
	}

	private Set<List<E>> cartesianProduct(List<List<E>> input) {
		List<Set<E>> cartesianProductInput = new ArrayList<Set<E>>();
		for(List<E> axis : input){
			cartesianProductInput.add(new LinkedHashSet<E>(axis));
		}
		return Sets.cartesianProduct(cartesianProductInput);
	}

	private Set<List<E>> selectTuplesRepresentation(Set<List<E>> nTuples,
			Set<List<E>> input) {
		Set<List<E>> result = new HashSet<List<E>>();
		int elementSize = elementSize(input);
		int maxTuples = combinations(elementSize, N);
		for(int t = maxTuples; t > 0; t--){
			for(List<E> vector : input){
				Set<List<E>> usedTuples = getUsedTuples(vector, nTuples);
				if(usedTuples.size() == t){
					result.add(vector);
					nTuples.removeAll(usedTuples);
				}
			}
		}
		return result;
	}

	private int combinations(int n, int k) {
		//return n!/(k!*(n-k)!);
		int coefficient = 1;
		for(int i = n - k + 1; i <= n; i++){
			coefficient *= i;
		}
		for(int i = 1; i <= k; i++){
			coefficient /= i;
		}
		return coefficient;
	}

	private int elementSize(Set<List<E>> input) {
		for(List<E> element : input){
			return element.size();
		}
		return 0;
	}

	private Set<List<E>> getUsedTuples(List<E> vector, Set<List<E>> tuples) {
		Set<List<E>> usedTuples = new HashSet<List<E>>();
		for(List<E> tuple : tuples){
			if(vector.containsAll(tuple)){
				usedTuples.add(tuple);
			}
		}
		return usedTuples;
	}
}
