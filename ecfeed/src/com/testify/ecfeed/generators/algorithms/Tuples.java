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

package com.testify.ecfeed.generators.algorithms;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tuples<E> {
	private List<? extends E> fInput;
	private int N;
	private List<E> fLast;

	public Tuples(List<? extends E> input, int n){
		fLast = null;
		fInput = input;
		N = n;
	}
	
	public List<E> next(){
		if(fLast == null){
			fLast = first();
			return fLast;
		}
		fLast = increment(fLast);
		return fLast;
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
	
	private List<E> increment(List<E> last) {
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

	private BitSet bits(List<E> input) {
		BitSet bitSet = new BitSet(fInput.size());
		for(E element : input){
			bitSet.set(fInput.indexOf(element));
		}
		return bitSet;
	}

	private List<E> first() {
		BitSet bitSet = new BitSet(fInput.size());
		for(int i = 0; i < N; i++){
			bitSet.set(i);
		}
		return instance(bitSet);
	}

	private List<E> instance(BitSet bitSet) {
		List<E> instance = new ArrayList<E>();
		//iterate over set bits
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1)) {
			instance.add(fInput.get(i));
		}
		return instance;
	}
}
