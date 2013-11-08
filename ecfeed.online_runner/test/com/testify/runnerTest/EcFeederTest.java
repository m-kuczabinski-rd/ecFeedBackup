package com.testify.runnerTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.runner.annotations.Constraints;
import com.testify.ecfeed.runner.annotations.GeneratorParameter;
import com.testify.ecfeed.runner.annotations.Model;
import com.testify.ecfeed.runner.annotations.OnlineRandomGenerator;
import com.testify.ecfeed.runner.annotations.RuntimeGenerator;
import com.testify.runner.RuntimeFeeder;

@RunWith(RuntimeFeeder.class)
@Model("test/New.ect")
@RuntimeGenerator(OnlineRandomGenerator.class)
@GeneratorParameter(Repetitions = false, TestRuns = "OnlineRandomGenerator.FOREVER")
@Constraints({ "constraint1", "constraint2" })
//@GeneratorParameter(TestRuns="OnlineRandomGenerator.FOREVER", Repetitions = true)
public class EcFeederTest {
	public String string;

//	@TestSuites
	public static String[] testSuites(){
		return new String[]{"default suite", "other suite"};
	}
	
	@Test
	public void exampleTestFunction(int intParameter){
		System.out.println("exampleTestFunction(" + intParameter + ")");
	}

	
	
	
	
}
