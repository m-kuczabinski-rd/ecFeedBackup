package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class MethodNodeTest {

	@Test
	public void testToString() {
		MethodNode method = new MethodNode("method");
		CategoryNode cat1 = new CategoryNode("cat1", "int");
		CategoryNode cat2 = new CategoryNode("cat1", "boolean");
		CategoryNode cat3 = new CategoryNode("cat1", "short");
		
		method.addCategory(cat1);
		method.addCategory(cat2);
		method.addCategory(cat3);

		assertEquals("method(int, boolean, short)", method.toString());
	}

}
