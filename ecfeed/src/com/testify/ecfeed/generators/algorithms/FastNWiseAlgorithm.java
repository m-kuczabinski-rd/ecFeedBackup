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

package com.testify.ecfeed.generators.algorithms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.generators.api.GeneratorException;

public class FastNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {
	
	private Set<List<E>> fCoveredTuples;
	
	public FastNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);
	}
	
	@Override
	public List<E> getNext() throws GeneratorException{
		List<E> next;
		while((next = cartesianNext()) != null){
			Set<List<E>> originalTuples = originalTuples(next);
			if(originalTuples.size() > 0){
				fCoveredTuples.addAll(originalTuples);
				progress(originalTuples.size());
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
