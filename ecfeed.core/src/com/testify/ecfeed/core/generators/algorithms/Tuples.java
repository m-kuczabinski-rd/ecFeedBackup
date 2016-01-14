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
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tuples<E> {
	private List<E> fInput;
	private int N;
	private List<Integer> fLast;
	private Map<Integer, E> fInputMap;
	private List<Integer> keys;

	public Tuples(List<E> input, int n){
		fLast = null;
		fInput = input;
		/*
		 * We are mapping values with unique integers, because otherwise algorithm would have no means to
		 * differentiate between exact same value despite it's position, and position is crucial here.
		 * Introducing Global Parameters brought this problem - two or more parameters linking same target
		 * were breaking it. Change is transparent - methods used to obtain results are just mapping values back.
		 */
		fInputMap = new HashMap<Integer, E>();
		for(int i = 0; i < fInput.size(); i++){
			fInputMap.put(i, fInput.get(i));
		}
		keys = new ArrayList<Integer>(fInputMap.keySet());
		N = n;
	}
	
	public List<E> next(){
		if(fLast == null){
			fLast = first();
			return reverseMap(fLast);
		}
		fLast = increment(fLast);
		return reverseMap(fLast);
	}
	
	public boolean hasNext(){
		if(fLast == null) return (N > 0 && fInput.size() > 0 && N <= fInput.size());
		BitSet bitSet = bits(fLast);
		int tail = tailLength(bitSet);
		return  tail != N; 
	}

	public Set<List<E>> getAll(){
		fLast = null;
		Set<List<E>> result = new HashSet<List<E>>();
		while(hasNext()){
			result.add(next());
		}
		return result;
	}
	
	/*
	 * Map original values back using this method.
	 */
	public List<E> reverseMap(List<Integer> indexes){
		ArrayList<E> tuple = new ArrayList<E>();
		for(Integer key: indexes){
			tuple.add(fInputMap.get(key));
		}	
		return tuple;
	}
	
	private List<Integer> increment(List<Integer> last) {
		BitSet bitSet = bits(last);
		int lastClear = bitSet.previousClearBit(fInput.size() - 1);
		if(lastClear == -1) return null;
		int tail = tailLength(bitSet);
		int toMove = bitSet.previousSetBit(lastClear);
		if(toMove == -1) return null;
		bitSet.clear(toMove, fInput.size());
		bitSet.set(toMove + 1, toMove + 2 + tail); 
		return instance(bitSet);
	}
	
	private int tailLength(BitSet bitSet){
		int lastClear = bitSet.previousClearBit(fInput.size() - 1);
		return fInput.size() - 1 - lastClear;
	}

	private BitSet bits(List<Integer> input) {
		BitSet bitSet = new BitSet(fInput.size());
		for(Integer element : input){
			bitSet.set(keys.indexOf(element));
		}
		return bitSet;
	}

	private List<Integer> first() {
		BitSet bitSet = new BitSet(fInput.size());
		for(int i = 0; i < N; i++){
			bitSet.set(i);
		}
		return instance(bitSet);
	}

	private List<Integer> instance(BitSet bitSet) {
		List<Integer> instance = new ArrayList<Integer>();
		//iterate over set bits
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1)) {
			instance.add(keys.get(i));
		}
		return instance;
	}
}
