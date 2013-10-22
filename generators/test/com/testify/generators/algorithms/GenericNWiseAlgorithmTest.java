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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.api.IAlgorithmInput;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.runner.EcFeeder;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.expected;

@RunWith(EcFeeder.class)
@EcModel("test/algorithms.ect")
public class GenericNWiseAlgorithmTest extends GenericNWiseAlgorithm<String>{
	public GenericNWiseAlgorithmTest() {
		super(0);
	}

	private static class DummyProgressMonitor implements IProgressMonitor{
		@Override
		public void beginTask(String name, int totalWork) {
		}
		@Override
		public void done() {
		}
		@Override
		public void internalWorked(double work) {
		}
		@Override
		public boolean isCanceled() {
			return false;
		}
		@Override
		public void setCanceled(boolean value) {
		}
		@Override
		public void setTaskName(String name) {
		}
		@Override
		public void subTask(String name) {
		}
		@Override
		public void worked(int work) {
		}
	}

	@Test
	public void testCombinations(@expected int result, int n, int k){
		if(combinations(n, k) != result){
			System.out.println("combinations(" + n + ", " + k + ") = " + combinations(n, k) + " != " + result);
		}
		assertEquals(result, combinations(n, k));
	}
	
	@Test
	public void testGeneratorNoConstraints() {
		String x1 = "x1";
		String x2 = "x2";
		String x3 = "x3";

		String y1 = "y1";
		String y2 = "y2";
		String y3 = "y3";

		String z1 = "z1";
		String z2 = "z2";
		String z3 = "z3";

		String u1 = "u1";
		String u2 = "u2";
		String u3 = "u3";

		ArrayList<String> x = new ArrayList<String>(Arrays.asList(new String[]{x1, x2, x3}));
		ArrayList<String> y = new ArrayList<String>(Arrays.asList(new String[]{y1, y2, y3}));
		ArrayList<String> z = new ArrayList<String>(Arrays.asList(new String[]{z1, z2, z3}));
		ArrayList<String> u = new ArrayList<String>(Arrays.asList(new String[]{u1, u2, u3}));
		
		final List<List<String>> input = new ArrayList<List<String>>();
		input.add(x);
		input.add(y);
		input.add(z);
		input.add(u);
		
		GenericNWiseAlgorithm<String> oneWiseAlgorithm = new GenericNWiseAlgorithm<>(1);
		GenericNWiseAlgorithm<String> twoWiseAlgorithm = new GenericNWiseAlgorithm<>(2);
		GenericNWiseAlgorithm<String> threeWiseAlgorithm = new GenericNWiseAlgorithm<>(3);
		GenericNWiseAlgorithm<String> fourWiseAlgorithm = new GenericNWiseAlgorithm<>(4);
		
		final Collection<IConstraint<String>> constraints = new HashSet<IConstraint<String>>();
		DummyProgressMonitor monitor = new DummyProgressMonitor(); 
		
		IAlgorithmInput<String> algInput = new IAlgorithmInput<String>() {

			@Override
			public List<List<String>> getInput() {
				return input;
			}

			@Override
			public Collection<IConstraint<String>> getConstraints() {
				return constraints;
			}
		};
		
		Set<List<String>> oneWiseSet = oneWiseAlgorithm.generate(algInput, monitor);
		Set<List<String>> twoWiseSet = twoWiseAlgorithm.generate(algInput, monitor);
		Set<List<String>> threeWiseSet = threeWiseAlgorithm.generate(algInput, monitor);
		Set<List<String>> fourWiseSet = fourWiseAlgorithm.generate(algInput, monitor);

		TupleGenerator<String> tupleGenerator = new TupleGenerator<String>();
		Set<List<String>> oneTuples = tupleGenerator.getNTuples(input, 1);
		Set<List<String>> twoTuples = tupleGenerator.getNTuples(input, 2);
		Set<List<String>> threeTuples = tupleGenerator.getNTuples(input, 3);
		Set<List<String>> fourTuples = tupleGenerator.getNTuples(input, 4);

		assertTrue(containsAllTuples(oneWiseSet, oneTuples));

		assertTrue(containsAllTuples(twoWiseSet, twoTuples));

		assertTrue(containsAllTuples(threeWiseSet, threeTuples));

		assertTrue(containsAllTuples(fourWiseSet, fourTuples));

	}
	
	private boolean containsAllTuples(Set<List<String>> set, Set<List<String>> tuples) {
		for(List<String> vector : set){
			Iterator<List<String>> it = tuples.iterator();
			while(it.hasNext()){
				List<String> tuple = it.next();
				if(vector.containsAll(tuple)){
					it.remove();
				}
			}
		}
		return tuples.size() == 0;
	}
}
