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

		TestCaseNode testCase1 = new TestCaseNode("test1", null);
		TestCaseNode testCase2 = new TestCaseNode("test2", null);
		
		method.addCategory(cat1);
		method.addCategory(cat2);
		method.addCategory(cat3);
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);

		assertEquals("method(int, boolean, short)", method.toString());
	}
	
	@Test
	public void testEquals(){
		MethodNode method = new MethodNode("method");
		CategoryNode cat1 = new CategoryNode("cat1", "int");
		CategoryNode cat2 = new CategoryNode("cat2", "String");
		TestCaseNode testCase1 = new TestCaseNode("test1", null);
		TestCaseNode testCase2 = new TestCaseNode("test2", null);
		method.addCategory(cat1);
		method.addCategory(cat2);
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);
		
		MethodNode methodCopy = new MethodNode("method");
		CategoryNode cat1Copy = new CategoryNode("cat1", "int");
		CategoryNode cat2Copy = new CategoryNode("cat2", "String");
		TestCaseNode testCase1Copy = new TestCaseNode("test1", null);
		TestCaseNode testCase2Copy = new TestCaseNode("test2", null);
		methodCopy.addCategory(cat1Copy);
		methodCopy.addCategory(cat2Copy);
		methodCopy.addTestCase(testCase1Copy);
		methodCopy.addTestCase(testCase2Copy);
		
		assertTrue(method.equals(methodCopy));
		CategoryNode cat2Mod = new CategoryNode("cat2", "int");
		methodCopy.removeChild(cat2Copy);
		methodCopy.addCategory(cat2Mod);
		assertFalse(method.equals(methodCopy));
		methodCopy.removeChild(cat2Mod);
		methodCopy.addCategory(cat2Copy);
		assertTrue(method.equals(methodCopy));
		method.removeChild(testCase2);
		assertFalse(method.equals(methodCopy));
		method.addTestCase(testCase2);
		assertTrue(method.equals(methodCopy));
	}
	
}
