package com.testify.ecfeed.test.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.runner.EcFeeder;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.TestSuites;

@RunWith(EcFeeder.class)
public class EcFeederTest{

	public EcFeederTest() throws Throwable {
	}
	
	@EcModel
	public static String[] getModel(){
		return new String[]{"test/com/testify/ecfeed/runner/ecModel.ect"};
	}
	
	@TestSuites
	public static String[] testSuites(){
		return new String[]{"dupa", "default suite"};
	}
	
	@Test
	public void exampleTestFunction(int intParameter, String stringParameter){
		System.out.println("exampleTestFunction(" + intParameter + ", " + stringParameter + ")");
	}

}
