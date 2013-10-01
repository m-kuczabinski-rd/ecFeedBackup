package com.testify.generators.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.ITestGenAlgorithm;

public class NWise implements ITestGenAlgorithm {

	int fN;
	
	public NWise(int n) {
		fN = n;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList[] generate(ArrayList[] input, IConstraint[] constraints) {
		ArrayList[] cartesian = new Cartesian().generate(input, constraints);
		Set<ArrayList> result = new HashSet<ArrayList>();
		Set<List> tuples = new TupleGenerator().getNTuples(input, fN);
		
		int maxTuples = sum(fN);
		for(int t = maxTuples; t > 0; t--){
			for(ArrayList vector : cartesian){
				Set<List> usedTuples = getUsedTuples(vector, tuples);
				if(usedTuples.size() == t){
					result.add(vector);
					tuples.removeAll(usedTuples);
					System.out.println("Vector " + vector + " added. Used tuples:" + usedTuples);
				}
			}
		}
		return result.toArray(new ArrayList[]{});
}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Set<List> getUsedTuples(ArrayList vector, Set<List> tuples) {
		Set<List> usedTuples = new HashSet<List>();
		for(List tuple : tuples){
			if(vector.containsAll(tuple)){
				usedTuples.add(tuple);
			}
		}
		return usedTuples;
	}

	private int sum(int n) {
		if(n <= 1) return n;
		return n + sum(n - 1);
	}

}
