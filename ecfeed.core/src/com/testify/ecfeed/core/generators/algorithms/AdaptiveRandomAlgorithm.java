/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.testify.ecfeed.core.generators.api.GeneratorException;
import com.testify.ecfeed.core.generators.api.IConstraint;

public class AdaptiveRandomAlgorithm<E> extends AbstractAlgorithm<E> implements IAlgorithm<E> {

	private final int fDepth;
	private final int fCandidatesSize;
	private final int fLength;
	private final boolean fDuplicates;
	
	private CartesianProductAlgorithm<E> fCartesianAlgorithm;

	private List<List<E>> fHistory;

	protected class BlackList implements IConstraint<E>{
		Collection<List<E>> fBlackList;
		
		public BlackList(Collection<List<E>> blackList){
			fBlackList = blackList;
		}

		@Override
		public boolean evaluate(List<E> values) {
			return !fBlackList.contains(values);
		}
		
		@Override
		public boolean adapt(List<E> values) {
			return false;
		}
		
		public void setBlackList(Collection<List<E>> newBlackList){
			fBlackList = newBlackList;
		}
		
		public Collection<List<E>> getBlackList(){
			return fBlackList;
		}
	}
	
	public AdaptiveRandomAlgorithm(int depth, int candidatesSize, 
			int length, boolean duplicates) {
		if(depth == -1) depth = Integer.MAX_VALUE;
		fDepth = depth;
		fCandidatesSize = candidatesSize;
		fLength = length;
		fDuplicates = duplicates;
		
		fHistory = new ArrayList<List<E>>();
		fCartesianAlgorithm = new CartesianProductAlgorithm<E>();
	}
	
	@Override
	public void initialize(List<List<E>> input,
			Collection<IConstraint<E>> constraints)
			throws GeneratorException {
		if(fDuplicates == false){
			constraints.add(new BlackList(fHistory));
		}
		fCartesianAlgorithm.initialize(input, constraints);
		setTotalWork(fLength);
		super.initialize(input, constraints);
	}
	
	@Override
	public List<E> getNext() throws GeneratorException {
		if(fHistory.size() >= fLength){
			return null;
		}
		List<List<E>> candidates = getCandidates();
		List<E> optimalCandidate = getOptimalCandidate(candidates, 
				fHistory.subList(Math.max(fHistory.size() - fDepth, 0), fHistory.size()));
		fHistory.add(optimalCandidate);
		progress(1);
		return optimalCandidate;
	}

	@Override
	public void reset(){
		fHistory.clear();
		super.reset();
	}

	public int getLength(){
		return fLength;
	}

	public boolean getDuplicates(){
		return fDuplicates;
	}

	public int getHistorySize(){
		return fDepth;
	}

	public int getCandidatesSize(){
		return fCandidatesSize;
	}

	public List<List<E>> getHistory(){
		return fHistory;
	}

	protected List<List<E>> getCandidates() throws GeneratorException {
		Set<List<E>> candidates = new HashSet<List<E>>();
		BlackList candidatesBlackList = new BlackList(candidates);
		while(candidates.size() < fCandidatesSize){
			List<E> candidate = getCandidate(candidatesBlackList);
			if(candidate == null){
				break;
			}
			candidates.add(candidate);
		}
		return new ArrayList<List<E>>(candidates);
	}

	protected List<E> getCandidate(BlackList blackList) throws GeneratorException{
		fCartesianAlgorithm.addConstraint(blackList);
		List<Integer> random = randomVector(getInput());
		List<E> result = fCartesianAlgorithm.getNext(instance(random));
		if(result == null){
			result = fCartesianAlgorithm.getNext(null);
		};
		fCartesianAlgorithm.removeConstraint(blackList);
		return result;
	}

	protected List<E> getOptimalCandidate(List<List<E>> candidates, List<List<E>> history) {
		if(candidates.size() == 0) return null;
		
		List<E> optimalCandidate = null;
		int optimalCandidateMinDistance = 0;
		for(List<E> candidate : candidates){
			int candidateMinDistance = Integer.MAX_VALUE;
			for(List<E> event : history){
				int distance = distance(candidate, event);
				candidateMinDistance = Math.min(distance, candidateMinDistance);
			}
			if(candidateMinDistance >= optimalCandidateMinDistance){
				optimalCandidate = candidate;
				optimalCandidateMinDistance = candidateMinDistance;
			}
		}
		return optimalCandidate;
	}

	protected int distance(List<E> vector1, List<E> vector2) {
		if(vector1.size() != vector2.size()){
			return Integer.MAX_VALUE;
		}
		int distance = 0;
		for(int i = 0; i < vector1.size(); i++){
			if(!vector1.get(i).equals(vector2.get(i))){
				++distance;
			}
		}
		return distance;
	}

	protected List<Integer> randomVector(List<? extends List<E>> input) {
		List<Integer> result = new ArrayList<Integer>();
		Random random = new Random();
		for(int i = 0; i < input.size(); i++){
			result.add(random.nextInt(input.get(i).size()));
		}
		return result;
	}
}
