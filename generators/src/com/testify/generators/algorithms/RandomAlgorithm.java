package com.testify.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;

public class RandomAlgorithm<E> extends CartesianProductAlgorithm<E> {
	
	private int fLength;
	private boolean fDuplicates;

	private List<? extends List<E>> fInput;
	private int fGeneratedCount;
	private BlackList fBlackList;
	private IProgressMonitor fProgressMonitor;

	protected class BlackList implements IConstraint<E>{

		private Set<List<E>> fBlackList;
		
		public BlackList() {
			fBlackList = new HashSet<List<E>>();
		}
		
		@Override
		public boolean evaluate(List<E> values) {
			return !fBlackList.contains(values);
		}
		
		public void add(List<E> element){
			fBlackList.add(element);
		}
		
		public void clear(){
			fBlackList.clear();
		}
		
		public void remove(List<E> element){
			fBlackList.remove(element);
		}
		
	}
	
	public RandomAlgorithm(int length, boolean duplicates) {
		fLength = length;
		fDuplicates = duplicates;
	}

	public RandomAlgorithm(boolean duplicates) {
		fLength = Integer.MAX_VALUE;
		fDuplicates = duplicates;
	}

	public RandomAlgorithm() {
		fLength = Integer.MAX_VALUE;
		fDuplicates = false;
	}


	@Override
	public void initialize(List<? extends List<E>> input,
			Collection<? extends IConstraint<E>> constraints,
			IProgressMonitor progressMonitor) throws GeneratorException {
		fInput = input;
		fBlackList = new BlackList();
		fProgressMonitor = progressMonitor;
		fProgressMonitor.beginTask("\nGenerating " + fLength + " random combinations", fLength);
		if(constraints == null){
			constraints = new HashSet<IConstraint<E>>();
		}
		Collection<IConstraint<E>> newConstraints = new HashSet<IConstraint<E>>(constraints);
		if(!fDuplicates){
			newConstraints.add(fBlackList);
		}
		super.initialize(input, newConstraints, null);
	}

	@Override
	public List<E> getNext() throws GeneratorException {
		if(fGeneratedCount >= fLength) return null;
		List<Integer> random = randomVector();
		List<E> result = getNext(instance(random));
		if(result == null){
			result = getNext(null);
		};
		if(result != null){
			++fGeneratedCount;
			fProgressMonitor.worked(1);
			if(fDuplicates){
				fBlackList.add(result);
			}
		}
		else{
			fProgressMonitor.done();
		}
		return result;
	}

	protected List<Integer> randomVector() {
		List<Integer> result = new ArrayList<Integer>();
		Random random = new Random();
		for(int i = 0; i < fInput.size(); i++){
			result.add(random.nextInt(fInput.get(i).size()));
		}
		return result;
	}

	@Override
	public void reset() {
		fGeneratedCount = 0;
		fBlackList.clear();
	}

	@Override
	protected int totalWork(){
		return fLength;
	}
}
