/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ecfeed.core.generators.IntegerParameter;
import com.ecfeed.core.generators.api.GeneratorException;

public class IntegerParameterTest {
	@Test
	public void constructorWithAllowedValuesTest() {
		try {
			@SuppressWarnings("unused")
			IntegerParameter parameter = new IntegerParameter("parameter", true, 0, new Integer[]{-1, 0, 1});
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			@SuppressWarnings("unused")
			IntegerParameter parameter = new IntegerParameter("parameter", true, 5, new Integer[]{-1, 0, 1});
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	@Test
	@SuppressWarnings("unused")
	public void constructorWithBoundsTest() {
		try {
			IntegerParameter parameter = new IntegerParameter("parameter", true, 0, -1, 1);
			parameter = new IntegerParameter("parameter", true, -1, -1, 1);
			parameter = new IntegerParameter("parameter", true, 1, -1, 1);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			IntegerParameter parameter = new IntegerParameter("parameter", true, 2, -1, 1);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	@Test
	public void allowedValuesTest(){
		try {
			IntegerParameter parameter = new IntegerParameter("parameter", true, 0, -1, 1);
			assertArrayEquals(null, parameter.allowedValues());
			Integer[] allowed = new Integer[]{0, 1, 2};
			parameter = new IntegerParameter("parameter", true, 0, allowed);
			assertArrayEquals(allowed, parameter.allowedValues());
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void testTest(){
		try {
			IntegerParameter boundedParameter;
			boundedParameter = new IntegerParameter("parameter", true, 0, -1, 1);
			assertTrue(boundedParameter.test(0));
			assertTrue(boundedParameter.test(-1));
			assertTrue(boundedParameter.test(1));
			assertFalse(boundedParameter.test(3));
			assertFalse(boundedParameter.test(1.0));
			
			IntegerParameter parameter = new IntegerParameter("parameter", true, 0, new Integer[]{-1, 0, 1});
			assertTrue(parameter.test(0));
			assertFalse(parameter.test(5));
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}
}
