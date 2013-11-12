package com.testify.generators.algorithms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.api.GeneratorException;

public class FastNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {
	private Set<List<E>> fCoveredTuples;
	
	@Override
	public List<E> getNext() throws GeneratorException{
		List<E> next;
		while((next = cartesianNext()) != null){
			Set<List<E>> originalTuples = originalTuples(next);
			if(originalTuples.size() > 0){
				fCoveredTuples.addAll(originalTuples);
				progressMonitor().worked(originalTuples.size());
				return next;
			}
		}
		return null;
	}
	
	@Override
	public void reset(){
		fCoveredTuples = new HashSet<List<E>>();
		super.reset();
	}
	
	protected Set<List<E>> originalTuples(List<E> vector){
		Set<List<E>> tuples = getTuples(vector);
		tuples.removeAll(fCoveredTuples);
		return tuples;
	}
}
