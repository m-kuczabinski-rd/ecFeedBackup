/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

public class TupleGenerator<E> {
	
	public Set<List<E>> getNTuples(List<List<E>> input, int n){
		
		Set<Collection<E>> powerSetInput = new LinkedHashSet<Collection<E>>(input);
		
		Set<Set<Collection<E>>> powerSet = Sets.powerSet(powerSetInput);
		Set<Set<Collection<E>>> kPremutation = new HashSet<Set<Collection<E>>>();
		
		for(Set<Collection<E>> permutation : powerSet){
			if(permutation.size() == n){
				kPremutation.add(permutation);
			}
		}

		Set<List<E>> result = new HashSet<List<E>>();
		for(Set<Collection<E>> axes : kPremutation){
			result.addAll(cartesianProduct(axes));
		}
		
		return result;
	}
	
	private Set<List<E>> cartesianProduct(Set<Collection<E>> axes) {
		List<Set<E>> input = new ArrayList<Set<E>>();
		
		for(Collection<E> axis : axes){
			input.add(new HashSet<E>(axis));
		}
		
		Set<List<E>> product = Sets.cartesianProduct(input);
		return product;
	}
}
