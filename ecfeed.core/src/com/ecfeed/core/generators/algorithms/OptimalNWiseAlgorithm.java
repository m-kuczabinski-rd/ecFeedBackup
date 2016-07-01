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

package com.ecfeed.core.generators.algorithms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.generators.api.GeneratorException;

public class OptimalNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E>{

	private int K;
	private Set<List<E>> fGeneratedTuples;

	public OptimalNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);
	}

	@Override
	public List<E> getNext() throws GeneratorException {
		while (fCancel != true
				&& (K != 0 && fGeneratedTuples.size() < tuplesToGenerate())) {

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
		K = maxTuples(getInput(), N);
		fGeneratedTuples = new HashSet<List<E>>();
		super.reset();
	}
	

	private Set<List<E>> originalTuples(List<E> next) {
		Set<List<E>> originalTuples = getTuples(next);
		originalTuples.removeAll(fGeneratedTuples);
		return originalTuples;
	}
}
