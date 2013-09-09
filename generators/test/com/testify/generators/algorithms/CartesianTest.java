package com.testify.generators.algorithms;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Vector;

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
		Vector[] input = new Vector[]{};
		assertArrayEquals(new Vector[]{}, fGenerator.generate(input, null));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSingleVectorInput(){
		Vector[] input = new Vector[]{new Vector<String>(Arrays.asList(new String[]{"a", "b", "c"}))};
		
		Vector[] expectedResult = new Vector[3];
		expectedResult[0] = new Vector();
		expectedResult[1] = new Vector();
		expectedResult[2] = new Vector();
		
		expectedResult[0].add("a");
		expectedResult[1].add("b");
		expectedResult[2].add("c");
		
		Vector[] result = fGenerator.generate(input, fConstraints);
		assertArrayEquals(expectedResult, result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testThreeVectorsInput() {
		String[] A = new String[]{"a1", "a2", "a3"};
		String[] B = new String[]{"b1", "b2", "b3"};
		String[] C = new String[]{"c1", "c2", "c3"};
		
		Vector<String> a = new Vector<String>(Arrays.asList(A));
		Vector<String> b = new Vector<String>(Arrays.asList(B));
		Vector<String> c = new Vector<String>(Arrays.asList(C));
		Vector[] input = new Vector[]{a, b, c};

		Vector[] expectedResult = new Vector[]
				{
				new Vector(Arrays.asList(new String[]{"a1", "b1", "c1"})),
				new Vector(Arrays.asList(new String[]{"a1", "b1", "c2"})),
				new Vector(Arrays.asList(new String[]{"a1", "b1", "c3"})),

				new Vector(Arrays.asList(new String[]{"a1", "b2", "c1"})),
				new Vector(Arrays.asList(new String[]{"a1", "b2", "c2"})),
				new Vector(Arrays.asList(new String[]{"a1", "b2", "c3"})),

				new Vector(Arrays.asList(new String[]{"a1", "b3", "c1"})),
				new Vector(Arrays.asList(new String[]{"a1", "b3", "c2"})),
				new Vector(Arrays.asList(new String[]{"a1", "b3", "c3"})),

				
				new Vector(Arrays.asList(new String[]{"a2", "b1", "c1"})),
				new Vector(Arrays.asList(new String[]{"a2", "b1", "c2"})),
				new Vector(Arrays.asList(new String[]{"a2", "b1", "c3"})),

				new Vector(Arrays.asList(new String[]{"a2", "b2", "c1"})),
				new Vector(Arrays.asList(new String[]{"a2", "b2", "c2"})),
				new Vector(Arrays.asList(new String[]{"a2", "b2", "c3"})),

				new Vector(Arrays.asList(new String[]{"a2", "b3", "c1"})),
				new Vector(Arrays.asList(new String[]{"a2", "b3", "c2"})),
				new Vector(Arrays.asList(new String[]{"a2", "b3", "c3"})),

				
				new Vector(Arrays.asList(new String[]{"a3", "b1", "c1"})),
				new Vector(Arrays.asList(new String[]{"a3", "b1", "c2"})),
				new Vector(Arrays.asList(new String[]{"a3", "b1", "c3"})),

				new Vector(Arrays.asList(new String[]{"a3", "b2", "c1"})),
				new Vector(Arrays.asList(new String[]{"a3", "b2", "c2"})),
				new Vector(Arrays.asList(new String[]{"a3", "b2", "c3"})),

				new Vector(Arrays.asList(new String[]{"a3", "b3", "c1"})),
				new Vector(Arrays.asList(new String[]{"a3", "b3", "c2"})),
				new Vector(Arrays.asList(new String[]{"a3", "b3", "c3"})),
				};
		
		assertArrayEquals(expectedResult, fGenerator.generate(input, fConstraints));
	}

}
