package com.testify.ecfeed.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.runner.annotations.Constraints;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.Generator;
import com.testify.ecfeed.runner.annotations.GeneratorParameter;
import com.testify.ecfeed.runner.annotations.GeneratorParameterNames;
import com.testify.ecfeed.runner.annotations.GeneratorParameterValues;
import com.testify.generators.CartesianProductGenerator;
import com.testify.generators.NWiseGenerator;
import com.testify.generators.RandomGenerator;

@RunWith(OnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@Constraints("constraint")
@EcModel("test/com/testify/ecfeed/runner/OnlineRunnerTest.ect")
public class OnlineRunnerTestClass {

	@Test
	@Generator(NWiseGenerator.class)
	@GeneratorParameter(name = "N", value = "2")
	public void nWiseTest(int a, int b, int c, int d) {
		System.out.println("test(" + a + ", " + b + ", " + c + ", " + d + ")");
	}

	@Test
	@Generator(CartesianProductGenerator.class)
	@Constraints("other constraint")
	public void cartesianTest(int a, int b, int c, int d) {
		System.out.println("test(" + a + ", " + b + ", " + c + ", " + d + ")");
	}

	@Test
	@Generator(RandomGenerator.class)
	@GeneratorParameterNames({"Test suite size", "Duplicates"})
	@GeneratorParameterValues({"100", "false"})
	public void randomTest(int a, int b, int c, int d) {
		System.out.println("test(" + a + ", " + b + ", " + c + ", " + d + ")");
	}


}
