/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.generators.NWiseGenerator;
import com.ecfeed.core.generators.algorithms.Tuples;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.runner.RunnerException;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.junit.annotations.GeneratorParameter;

public class OnlineRunnerTest extends StaticRunnerTest{

	public OnlineRunnerTest() throws InitializationError {
		super();
		fExecuted = new HashSet<List<String>>();
	}

	protected final static String MODEL_PATH = "test/com/testify/ecfeed/junit/OnlineRunnerTest.ect";

	protected final static String OVERRIDING_CONSTRAINT_NAME = "constraint";

	private static Set<List<String>> fExecuted;

	private final Collection<IConstraint<ChoiceNode>> EMPTY_CONSTRAINTS =
			new ArrayList<IConstraint<ChoiceNode>>();

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
	
	public static Set<Map<Integer,String>> getCoveredNTuples(Set<List<String>> tests, int n){
		Set<Map<Integer,String>> nTuples = new HashSet<>();
		
		List<Integer> indices = new ArrayList<>();
		for(int i=0; i< tests.iterator().next().size(); i++)
			indices.add(i);
		Tuples<Integer> combinationGenerator = new Tuples<>(indices, n);
		Set<List<Integer>> allCombs = combinationGenerator.getAll();
		
		for(List<String> test : tests) 
			for(List<Integer> comb : allCombs) {
				Map<Integer, String> nTuple = new HashMap<>();
				for(Integer ind : comb)
					nTuple.put(ind, test.get(ind));
				nTuples.add(nTuple);
			}
			
		
		return nTuples;
	}

	@RunWith(OnlineRunner.class)
	@Generator(NWiseGenerator.class)
	@GeneratorParameter(name = "N", value = "2")
	@EcModel(MODEL_PATH)
	public static class GlobalGeneratorTestClass{
		@Test
		public void testWithGlobalGenerator(String arg1, String arg2, String arg3, String arg4){
			executeTest(arg1, arg2, arg3, arg4);
		}
	}

	@RunWith(OnlineRunner.class)
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

	@RunWith(OnlineRunner.class)
	@Generator(CartesianProductGenerator.class)
	@EcModel(MODEL_PATH)
	@Constraints(Constraints.ALL)
	public static class GlobalConstraintsTestClass{
		@Test
		public void testWithConstraints(String arg1, String arg2, String arg3, String arg4){
			executeTest(arg1, arg2, arg3, arg4);
		}
	}

	@RunWith(OnlineRunner.class)
	@Generator(CartesianProductGenerator.class)
	@EcModel(MODEL_PATH)
	@Constraints(Constraints.ALL)
	public static class OverridenConstraintsTestClass{
		@Test
		@Constraints("constraint")
		public void testWithConstraints(String arg1, String arg2, String arg3, String arg4){
			executeTest(arg1, arg2, arg3, arg4);
		}
	}

	@RunWith(OnlineRunner.class)
	@Generator(CartesianProductGenerator.class)
	@EcModel(MODEL_PATH)
	public static class NoConstraintsTestClass{
		@Test
		public void testWithNoConstraints(String arg1, String arg2, String arg3, String arg4){
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
				List<List<ChoiceNode>> input = referenceInput(getModel(MODEL_PATH), method);
				Set<List<String>> referenceResult = computeReferenceResult(referenceNWiseGenerator(input, EMPTY_CONSTRAINTS, 2));
				method.invokeExplosively(testClass.newInstance(), (Object[])null);
				// The following assertion assumes that the expected coverage is 100, and we are looking for 2-wise combinations
				assertEquals(getCoveredNTuples(referenceResult, 2), getCoveredNTuples(fExecuted, 2));
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
				List<List<ChoiceNode>> input = referenceInput(getModel(MODEL_PATH), method);
				Set<List<String>> referenceResult = computeReferenceResult(referenceNWiseGenerator(input, EMPTY_CONSTRAINTS, 3));
				method.invokeExplosively(testClass.newInstance(), (Object[])null);
				// The following assertion assumes that the expected coverage is 100, and we are looking for 3-wise combinations
				assertEquals(getCoveredNTuples(referenceResult, 3), getCoveredNTuples(fExecuted, 3));
			}
		}
		catch(Throwable e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGlobalConstraints(){
		try{
			Class<GlobalConstraintsTestClass> testClass = GlobalConstraintsTestClass.class;
			OnlineRunner runner = new OnlineRunner(testClass);
			for(FrameworkMethod method : runner.computeTestMethods()){
				List<List<ChoiceNode>> input = referenceInput(runner.getModel(), method);
				Collection<IConstraint<ChoiceNode>> constraints = getConstraints(runner.getModel(), method);
				Set<List<String>> referenceResult = computeReferenceResult(referenceCartesianGenerator(input, constraints));
				method.invokeExplosively(testClass.newInstance(), (Object[])null);
				assertEquals(referenceResult, fExecuted);
			}
		}
		catch(Throwable e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testOverridenConstraints(){
		try{
			Class<OverridenConstraintsTestClass> testClass = OverridenConstraintsTestClass.class;
			OnlineRunner runner = new OnlineRunner(testClass);
			for(FrameworkMethod method : runner.computeTestMethods()){
				List<List<ChoiceNode>> input = referenceInput(runner.getModel(), method);
				Collection<IConstraint<ChoiceNode>> constraints = getConstraints(runner.getModel(), method,
						OVERRIDING_CONSTRAINT_NAME);
				Set<List<String>> referenceResult = computeReferenceResult(referenceCartesianGenerator(input, constraints));
				method.invokeExplosively(testClass.newInstance(), (Object[])null);
				assertEquals(referenceResult, fExecuted);
			}
		}
		catch(Throwable e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testNoConstraints(){
		try{
			Class<NoConstraintsTestClass> testClass = NoConstraintsTestClass.class;
			OnlineRunner runner = new OnlineRunner(testClass);
			for(FrameworkMethod method : runner.computeTestMethods()){
				List<List<ChoiceNode>> input = referenceInput(runner.getModel(), method);
				Collection<IConstraint<ChoiceNode>> constraints = EMPTY_CONSTRAINTS;
				Set<List<String>> referenceResult = computeReferenceResult(referenceCartesianGenerator(input, constraints));
				method.invokeExplosively(testClass.newInstance(), (Object[])null);
				assertEquals(referenceResult, fExecuted);
			}
		}
		catch(Throwable e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	private Collection<IConstraint<ChoiceNode>> getConstraints(
			RootNode model, FrameworkMethod method,
			String name) throws RunnerException {
		return getMethodModel(model, method).getConstraints(name);
	}

	protected Collection<IConstraint<ChoiceNode>> getConstraints(
			RootNode model, FrameworkMethod method) throws RunnerException {
		Collection<IConstraint<ChoiceNode>> result = new ArrayList<IConstraint<ChoiceNode>>();
		MethodNode methodModel = getMethodModel(model, method);
		for(ConstraintNode node : methodModel.getConstraintNodes()){
			result.add(node.getConstraint());
		}
		return result;
	}

	protected Set<List<String>> computeReferenceResult(
			IGenerator<ChoiceNode> initializedGenerator) throws GeneratorException {
		Set<List<String>> result = new HashSet<List<String>>();
		List<ChoiceNode> next;
		while((next = initializedGenerator.next()) != null){
			List<String> sample = new ArrayList<String>();
			for(ChoiceNode choice : next){
				sample.add(choice.getValueString());
			}
			result.add(sample);
		}
		return result;
	}

	private IGenerator<ChoiceNode> referenceCartesianGenerator(
			List<List<ChoiceNode>> input,
			Collection<IConstraint<ChoiceNode>> constraints) throws GeneratorException {
		IGenerator<ChoiceNode> generator = new CartesianProductGenerator<ChoiceNode>();
		generator.initialize(input, constraints, null);
		return generator;
	}

	private NWiseGenerator<ChoiceNode> referenceNWiseGenerator(
			List<List<ChoiceNode>> input,
			Collection<IConstraint<ChoiceNode>> constraints,
			int n) throws GeneratorException {
		NWiseGenerator<ChoiceNode> result = new NWiseGenerator<ChoiceNode>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("N", n);
		result.initialize(input, constraints, parameters);
		return result;
	}

	private List<List<ChoiceNode>> referenceInput(RootNode model, FrameworkMethod method) throws RunnerException {
		List<List<ChoiceNode>> result = new ArrayList<List<ChoiceNode>>();
		MethodNode methodModel = getMethodModel(model, method);
		for(MethodParameterNode parameter : methodModel.getMethodParameters()){
			result.add(parameter.getChoices());
		}
		return result;
	}

}
