package com.testify.ecfeed.generators.algorithms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.generators.api.GeneratorException;

public class OptimalNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E>{

	CartesianProductGenerator<E> fCartesianGenerator;
	private int K;
	private Set<List<E>> fGeneratedTuples;
	
	public OptimalNWiseAlgorithm(int n) {
		super(n);
	}
	
	@Override
	public List<E> getNext() throws GeneratorException{
		while(K != 0 && fGeneratedTuples.size() < tuplesToGenerate()){
			List<E> next = cartesianNext();
			if(next == null){
				--K;
				cartesianReset();
				continue;
			}
			Set<List<E>> originalTuples = originalTuples(next); 
			if(originalTuples.size() == K){
				fGeneratedTuples.addAll(originalTuples);
				progress(originalTuples.size());
				return next;
			}
		}
		return null;
	}

	@Override
	public void reset(){
		super.reset();
		K = maxTuples(getInput(), N);
		fGeneratedTuples = new HashSet<List<E>>();
	}
	

	private Set<List<E>> originalTuples(List<E> next) {
		Set<List<E>> originalTuples = getTuples(next);
		originalTuples.removeAll(fGeneratedTuples);
		return originalTuples;
	}

}