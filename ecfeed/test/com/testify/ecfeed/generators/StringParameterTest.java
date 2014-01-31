package com.testify.ecfeed.generators;

import static org.junit.Assert.*;

import org.junit.Test;

import com.testify.ecfeed.generators.api.GeneratorException;

public class StringParameterTest {

	@Test
	public void testTest() {
		try {
			StringParameter p1 = new StringParameter("parameter", true, "default", new String[]{"default", "value"});
			assertTrue(p1.test("default"));
			assertTrue(p1.test("value"));
			assertFalse(p1.test("other"));
			assertFalse(p1.test(5));
			StringParameter p2 = new StringParameter("parameter", true, "default");
			assertTrue(p2.test("default"));
			assertTrue(p2.test("value"));
			assertTrue(p2.test("other"));
			assertFalse(p2.test(5));
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void constructorTest() {
		try {
			StringParameter parameter = new StringParameter("parameter", true, "default", new String[]{"default", "value"});
			assertEquals("default", parameter.defaultValue());
			assertEquals("parameter", parameter.getName());
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			new StringParameter("parameter", true, "default", new String[]{"any", "value"});
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

}