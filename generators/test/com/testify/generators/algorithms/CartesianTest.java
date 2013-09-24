package com.testify.generators.algorithms;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.ArrayList;

import org.junit.Test;

import com.testify.ecfeed.api.IConstraint;

public class CartesianTest {

	Cartesian fGenerator;
	private IConstraint[] fConstraints;
	
	public CartesianTest() {
		fGenerator = new Cartesian();
		fConstraints = new IConstraint[]{};
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testEmptyInput(){
		ArrayList[] input = new ArrayList[]{};
		assertArrayEquals(new ArrayList[]{}, fGenerator.generate(input, null));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSingleVectorInput(){
		ArrayList[] input = new ArrayList[]{new ArrayList<String>(Arrays.asList(new String[]{"a", "b", "c"}))};
		
		ArrayList[] expectedResult = new ArrayList[3];
		expectedResult[0] = new ArrayList();
		expectedResult[1] = new ArrayList();
		expectedResult[2] = new ArrayList();
		
		expectedResult[0].add("a");
		expectedResult[1].add("b");
		expectedResult[2].add("c");
		
		ArrayList[] result = fGenerator.generate(input, fConstraints);
		assertArrayEquals(expectedResult, result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testThreeVectorsInput() {
		String[] A = new String[]{"a1", "a2", "a3"};
		String[] B = new String[]{"b1", "b2", "b3"};
		String[] C = new String[]{"c1", "c2", "c3"};
		
		ArrayList<String> a = new ArrayList<String>(Arrays.asList(A));
		ArrayList<String> b = new ArrayList<String>(Arrays.asList(B));
		ArrayList<String> c = new ArrayList<String>(Arrays.asList(C));
		ArrayList[] input = new ArrayList[]{a, b, c};

		ArrayList[] expectedResult = new ArrayList[]
				{
				new ArrayList(Arrays.asList(new String[]{"a1", "b1", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a1", "b1", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a1", "b1", "c3"})),

				new ArrayList(Arrays.asList(new String[]{"a1", "b2", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a1", "b2", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a1", "b2", "c3"})),

				new ArrayList(Arrays.asList(new String[]{"a1", "b3", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a1", "b3", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a1", "b3", "c3"})),

				
				new ArrayList(Arrays.asList(new String[]{"a2", "b1", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a2", "b1", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a2", "b1", "c3"})),

				new ArrayList(Arrays.asList(new String[]{"a2", "b2", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a2", "b2", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a2", "b2", "c3"})),

				new ArrayList(Arrays.asList(new String[]{"a2", "b3", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a2", "b3", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a2", "b3", "c3"})),


				new ArrayList(Arrays.asList(new String[]{"a3", "b1", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a3", "b1", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a3", "b1", "c3"})),

				new ArrayList(Arrays.asList(new String[]{"a3", "b2", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a3", "b2", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a3", "b2", "c3"})),

				new ArrayList(Arrays.asList(new String[]{"a3", "b3", "c1"})),
				new ArrayList(Arrays.asList(new String[]{"a3", "b3", "c2"})),
				new ArrayList(Arrays.asList(new String[]{"a3", "b3", "c3"})),
				};
		
		assertArrayEquals(expectedResult, fGenerator.generate(input, fConstraints));
	}

}