package com.testify.ecfeed.generators;

import static org.junit.Assert.*;

import org.junit.Test;

import com.testify.ecfeed.generators.api.GeneratorException;

public class DoubleParameterTest {
	@Test
	public void constructorWithAllowedValuesTest() {
		try {
			@SuppressWarnings("unused")
			DoubleParameter parameter = new DoubleParameter("parameter", true, 0.0f, new Double[]{-1.0, 0.0, 1.0});
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			@SuppressWarnings("unused")
			DoubleParameter parameter = new DoubleParameter("parameter", true, 5.0f, new Double[]{-1.0, 0.0, 1.0});
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	@Test
	public void constructorWithBoundsTest() {
		try {
			@SuppressWarnings("unused")
			DoubleParameter parameter = new DoubleParameter("parameter", true, 0.0, -1.0, 1.0);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			@SuppressWarnings("unused")
			DoubleParameter parameter = new DoubleParameter("parameter", true, 2.0, -1.0, 1.0);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	@Test
	public void allowedValuesTest(){
		try {
			DoubleParameter parameter = new DoubleParameter("parameter", true, 0.0, -1.0, 1.0);
			assertArrayEquals(null, parameter.allowedValues());
			Double[] allowed = new Double[]{0.0, 1.0, 2.0};
			parameter = new DoubleParameter("parameter", true, 0.0, allowed);
			assertArrayEquals(allowed, parameter.allowedValues());
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void testTest(){
		DoubleParameter boundedParameter;
		try {
			boundedParameter = new DoubleParameter("parameter", true, 0.0, -1.0, 1.0);
			assertTrue(boundedParameter.test(0.00));
			assertTrue(boundedParameter.test(-1.00));
			assertTrue(boundedParameter.test(1.00));
			assertFalse(boundedParameter.test(3.0));

			boundedParameter = new DoubleParameter("parameter", true, 0.0, new Double[]{-1.0, 0.0, 1.0});
			assertTrue(boundedParameter.test(0.0));
			assertTrue(boundedParameter.test(-1.0));
			assertTrue(boundedParameter.test(1.0));
			assertFalse(boundedParameter.test(3.0));
			assertFalse(boundedParameter.test(3.0f));
			assertFalse(boundedParameter.test("string"));
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}
}
