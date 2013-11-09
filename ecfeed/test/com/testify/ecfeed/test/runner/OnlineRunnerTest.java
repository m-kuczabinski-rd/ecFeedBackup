package com.testify.ecfeed.test.runner;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.runner.OnlineRunner;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.runner.annotations.Constraints;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.Generator;
import com.testify.ecfeed.runner.annotations.GeneratorParameter;
import com.testify.generators.NWiseGenerator;

public class OnlineRunnerTest extends StaticRunnerTest{

	public OnlineRunnerTest() throws InitializationError {
		super();
		fExecuted = new HashSet<List<String>>();
	}

	protected final static String MODEL_PATH = "test/com/testify/ecfeed/test/runner/OnlineRunnerTest.ect";

	private static Set<List<String>> fExecuted;

	public static void executeTest(String arg1, String arg2, String arg3, String arg4){
		if(fExecuted != null){
			List<String> executed = new ArrayList<String>();
			executed.add(arg1);
			executed.add(arg2);
			executed.add(arg3);
			executed.add(arg4);
			fExecuted.add(executed);
		}
	}

	
	@Generator(NWiseGenerator.class)
	@GeneratorParameter(name = "N", value = "2")
	@EcModel(MODEL_PATH)
	public static class GlobalGeneratorTestClass{
		@Test
		public void testWithGlobalGenerator(String arg1, String arg2, String arg3, String arg4){
			executeTest(arg1, arg2, arg3, arg4);
		}
	}
	
	@Generator(NWiseGenerator.class)
	@GeneratorParameter(name = "N", value = "2")
	@EcModel(MODEL_PATH)
	public static class OverridenGeneratorParameterTestClass{

		@Test
		@GeneratorParameter(name = "N", value = "3")
		public void testWithLocalGeneratorParameter(String arg1, String arg2, String arg3, String arg4){
			executeTest(arg1, arg2, arg3, arg4);
		}
	}
	
	@Generator(NWiseGenerator.class)
	@GeneratorParameter(name = "N", value = "2")
	@EcModel(MODEL_PATH)
	@Constraints(Constraints.ALL)
	public static class GlobalConstraintsTestClass{
		@Test
		public void testWithConstraints(String arg1, String arg2, String arg3, String arg4){
			executeTest(arg1, arg2, arg3, arg4);
		}
	}
	
	@Generator(NWiseGenerator.class)
	@GeneratorParameter(name = "N", value = "2")
	@EcModel(MODEL_PATH)
	@Constraints(Constraints.NONE)
	public static class OverridenConstraintsTestClass{
		@Test
		@Constraints("constraint")
		public void testWithConstraints(String arg1, String arg2, String arg3, String arg4){
			executeTest(arg1, arg2, arg3, arg4);
		}
	}
	
	@Before
	public void clearResult(){
		fExecuted.clear();
	}
	
	@Test
	public void testGlobalGenerator() {
		try{
			Class<GlobalGeneratorTestClass> testClass = GlobalGeneratorTestClass.class;
			OnlineRunner runner = new OnlineRunner(testClass);
			for(FrameworkMethod method : runner.computeTestMethods()){
				List<List<PartitionNode>> input = referenceInput(getModel(MODEL_PATH), method);
				Set<List<String>> referenceResult = computeReferenceResult(referenceNWiseGenerator(input, null, 2));
				method.invokeExplosively(testClass.newInstance(), (Object[])null);
				assertEquals(referenceResult, fExecuted);
			}
		}
		catch(Throwable e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testOverridenParameters(){
		try{
			Class<OverridenGeneratorParameterTestClass> testClass = OverridenGeneratorParameterTestClass.class;
			OnlineRunner runner = new OnlineRunner(testClass);
			for(FrameworkMethod method : runner.computeTestMethods()){
				List<List<PartitionNode>> input = referenceInput(getModel(MODEL_PATH), method);
				Set<List<String>> referenceResult = computeReferenceResult(referenceNWiseGenerator(input, null, 3));
				method.invokeExplosively(testClass.newInstance(), (Object[])null);
				assertEquals(referenceResult, fExecuted);
			}
		}
		catch(Throwable e){
			fail("Unexpected exception: " + e.getMessage());
		}

	}

	protected Set<List<String>> computeReferenceResult(
			NWiseGenerator<PartitionNode> initializedGenerator) throws GeneratorException {
		Set<List<String>> result = new HashSet<List<String>>();
		List<PartitionNode> next;
		while((next = initializedGenerator.next()) != null){
			List<String> sample = new ArrayList<String>();
			for(PartitionNode partition : next){
				sample.add((String)partition.getValue());
			}
			result.add(sample);
		}
		return result;
	}

	private NWiseGenerator<PartitionNode> referenceNWiseGenerator(
			List<List<PartitionNode>> input, 
			Collection<? extends IConstraint<PartitionNode>> constraints, 
			int n) throws GeneratorException {
		NWiseGenerator<PartitionNode> result = new NWiseGenerator<PartitionNode>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("N", n);
		result.initialize(input, null, parameters, null);
		return result;
	}

	private List<List<PartitionNode>> referenceInput(RootNode model, FrameworkMethod method) throws RunnerException {
		List<List<PartitionNode>> result = new ArrayList<List<PartitionNode>>();
		MethodNode methodModel = getMethodModel(model, method);
		for(CategoryNode category : methodModel.getCategories()){
			result.add(category.getPartitions());
		}
		return result;
	}

}
