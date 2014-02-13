package com.testify.ecfeed.runner;

import static org.junit.Assert.*;

//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import com.testify.ecfeed.runner.annotations.EcModel;

//@RunWith(StaticRunner.class)
//@EcModel("test/com/testify/ecfeed/runner/testModel.ect")
public class StaticTestClass {

//	@Test
	public void testFunction1(int arg1, int arg2){
		fail("dupa");
	}
	
//	@Test
	public void testFunction2(int arg1, int arg2){
		assertEquals(2, 1);
	}

//	@Test
	public void testFunction3(int arg1, int arg2){
		assertTrue(false);
	}
}
