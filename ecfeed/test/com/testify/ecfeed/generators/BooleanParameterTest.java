package com.testify.ecfeed.generators;

import static org.junit.Assert.*;

import org.junit.Test;

public class BooleanParameterTest {

	@Test
	public void testTest() {
		BooleanParameter parameter = new BooleanParameter("parameter", true, false);
		assertTrue(parameter.test(true));
		assertTrue(parameter.test(false));
		assertFalse(parameter.test(8));
	}

}
