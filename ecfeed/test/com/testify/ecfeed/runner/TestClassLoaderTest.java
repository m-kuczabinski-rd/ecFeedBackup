package com.testify.ecfeed.runner;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestClassLoaderTest {
	
	@Test
	public void loadClassTest(){
		TestClassLoader loader = new TestClassLoader(this.getClass().getClassLoader(), "dupa");
		try {
			loader.loadClass("com.testify.ecfeed.runner.OnlineRunner");
		} catch (ClassNotFoundException e) {
			fail("ClassNotFoundException: " + e.getMessage());
		}
	}
}
