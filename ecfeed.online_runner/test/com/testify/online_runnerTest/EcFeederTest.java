package com.testify.online_runnerTest;

import org.databene.feed4junit.Feeder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.api.IRuntimeGenerator;
import com.testify.ecfeed.runner.annotations.*;
import com.testify.ecfeed.runner.EcFeeder;
import com.testify.ecfeed.runner.annotations.TestSuites;
import com.testify.online_runner.RuntimeFeeder;

@RunWith(RuntimeFeeder.class)
@Model("test/New.ect")
@RuntimeGenerator(OnlineRandomGenerator.class)
@GeneratorParameter(Repetitions = false, TestRuns = "OnlineRandomGenerator.FOREVER")
@Constraints({ "constraint1", "constraint2" })
//@GeneratorParameter(TestRuns="OnlineRandomGenerator.FOREVER", Repetitions = true)
public class EcFeederTest{
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
