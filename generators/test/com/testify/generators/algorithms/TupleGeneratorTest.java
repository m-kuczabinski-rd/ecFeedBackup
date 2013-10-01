package com.testify.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TupleGeneratorTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() {
		ArrayList x = new ArrayList(Arrays.asList(new String[]{"x1", "x2", "x3"}));
		ArrayList y = new ArrayList(Arrays.asList(new String[]{"y1", "y2", "y3"}));
		ArrayList z = new ArrayList(Arrays.asList(new String[]{"z1", "z2", "z3"}));
		
		TupleGenerator gen = new TupleGenerator();
		Set<List> pairs = gen.getNTuples(new ArrayList[]{x, y,z}, 2);
		
		for(List pair : pairs){
			System.out.println(pair);
		}
		
	}

}
