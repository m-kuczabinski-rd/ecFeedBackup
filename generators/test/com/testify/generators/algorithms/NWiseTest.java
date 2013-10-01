package com.testify.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class NWiseTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void test() {
		ArrayList x = new ArrayList(Arrays.asList(new String[]{"x1", "x2", "x3"}));
		ArrayList y = new ArrayList(Arrays.asList(new String[]{"y1", "y2", "y3"}));
		ArrayList z = new ArrayList(Arrays.asList(new String[]{"z1", "z2", "z3"}));
		
		ArrayList[] input = new ArrayList[]{x, y, z};
		
		ArrayList[] pairwise = new NWise(2).generate(input, null);
		
		for(int i = 0; i < pairwise.length; i++){
			System.out.println(pairwise[i]);
		}
	}

}
