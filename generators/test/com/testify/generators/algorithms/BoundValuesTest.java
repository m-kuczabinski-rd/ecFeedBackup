package com.testify.generators.algorithms;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

public class BoundValuesTest {

	BoundValues fGenerator;
	
	public BoundValuesTest() {
		fGenerator = new BoundValues();
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testEmptyInput() {
		Vector[] input = new Vector[]{};
		Vector[] expectedResult = new Vector[]{};
		
		assertArrayEquals(expectedResult, input);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSingleInput() {
		
		Vector[] input = new Vector[]{new Vector<String>(Arrays.asList(new String[]{"a", "b", "c", "d"}))};
		
		
		Vector[] expectedResult = new Vector[2];
		expectedResult[0] = new Vector();
		expectedResult[1] = new Vector();

		expectedResult[0].add("a");
		expectedResult[1].add("d");
		
		assertArrayEquals(expectedResult, fGenerator.generate(input));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testMultipleInput(){
		Vector[] input = new Vector[]{
				new Vector<String>(Arrays.asList(new String[]{"a1", "a2", "a3", "a4"})),
				new Vector<String>(Arrays.asList(new String[]{"b1", "b2", "b3", "b4"})),
				new Vector<String>(Arrays.asList(new String[]{"c1", "c2", "c3", "c4"}))
		};
		
		Vector[] expectedResult = new Vector[]
				{
				new Vector(Arrays.asList(new String[]{"a1", "b1", "c1"})),
				new Vector(Arrays.asList(new String[]{"a1", "b1", "c4"})),

				new Vector(Arrays.asList(new String[]{"a1", "b4", "c1"})),
				new Vector(Arrays.asList(new String[]{"a1", "b4", "c4"})),

				new Vector(Arrays.asList(new String[]{"a4", "b1", "c1"})),
				new Vector(Arrays.asList(new String[]{"a4", "b1", "c4"})),

				new Vector(Arrays.asList(new String[]{"a4", "b4", "c1"})),
				new Vector(Arrays.asList(new String[]{"a4", "b4", "c4"})),
				};
		
		assertArrayEquals(expectedResult, fGenerator.generate(input));
	}

}
