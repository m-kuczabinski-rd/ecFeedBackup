package com.testify.generators.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

public class TupleGenerator {
	
	@SuppressWarnings({ "rawtypes" })
	public Set<List> getNTuples(Collection[] input, int n){

		Set<Collection> hashedInput = new HashSet<Collection>(Arrays.asList(input));
		Set<Set<Collection>> powerSet = Sets.powerSet(hashedInput);
		Set<Set<Collection>> kPremutation = new HashSet<Set<Collection>>();
		
		for(Set<Collection> permutation : powerSet){
			if(permutation.size() == n){
				kPremutation.add(permutation);
			}
		}

		Set<List> result = new HashSet<List>();
		for(Set<Collection> axes : kPremutation){
			result.addAll(cartesianProduct(axes));
		}
		
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Set<List<Object>> cartesianProduct(Set<Collection> axes) {
		List<Set<Object>> input = new ArrayList<Set<Object>>();
		
		for(Collection axis : axes){
			input.add(new HashSet(axis));
		}
		
		Set<List<Object>> product = Sets.cartesianProduct(input);
		return product;
	}
}
