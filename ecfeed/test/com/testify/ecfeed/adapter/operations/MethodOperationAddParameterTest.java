package com.testify.ecfeed.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.adapter.ModelOperationManager;
import com.testify.ecfeed.core.adapter.operations.MethodOperationAddParameter;
import com.testify.ecfeed.core.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;


@RunWith(OnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@EcModel("test/com.testify.ecfeed.adapter.operations.ect")
@Constraints(Constraints.ALL)
public class MethodOperationAddParameterTest{

	@Test
	public void testMethodWithTestCases(){
		ClassNode classNode = new ClassNode("TestClass");
		MethodNode parentMethod = new MethodNode("testMethod");
		MethodParameterNode normalParameter = new MethodParameterNode("normalParameter", "int", "0", false);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0");
		ChoiceNode choice2 = new ChoiceNode("choice2", "5");
		normalParameter.addChoice(choice1);
		normalParameter.addChoice(choice2);
		MethodParameterNode expectedParameter = new MethodParameterNode("expectedParameter", "int", "0", false);
		ChoiceNode expectedChoice1 = new ChoiceNode("choice1", "4");
		ChoiceNode expectedChoice2 = new ChoiceNode("choice1", "7");
		expectedChoice1.setParent(expectedParameter);
		expectedChoice2.setParent(expectedParameter);
		parentMethod.addParameter(normalParameter);
		parentMethod.addParameter(expectedParameter);
		TestCaseNode testCase1 = new TestCaseNode("default", Arrays.asList(new ChoiceNode[]{choice1, expectedChoice1}));
		TestCaseNode testCase2 = new TestCaseNode("default", Arrays.asList(new ChoiceNode[]{choice1, expectedChoice2}));
		TestCaseNode testCase3 = new TestCaseNode("default", Arrays.asList(new ChoiceNode[]{choice2, expectedChoice1}));
		TestCaseNode testCase4 = new TestCaseNode("default", Arrays.asList(new ChoiceNode[]{choice2, expectedChoice2}));
		parentMethod.addTestCase(testCase1);
		parentMethod.addTestCase(testCase2);
		parentMethod.addTestCase(testCase3);
		parentMethod.addTestCase(testCase4);
		classNode.addMethod(parentMethod);

		MethodParameterNode parameter = new MethodParameterNode("parameter", "int", "0", false);
		ModelOperationManager operationManager = new ModelOperationManager();
		IModelOperation operation = new MethodOperationAddParameter(parentMethod, parameter, 1);

		try{
			assertEquals(4, parentMethod.getTestCases().size());
			assertEquals(0, parentMethod.getTestCases().indexOf(testCase1));
			assertEquals(1, parentMethod.getTestCases().indexOf(testCase2));
			assertEquals(2, parentMethod.getTestCases().indexOf(testCase3));
			assertEquals(3, parentMethod.getTestCases().indexOf(testCase4));

			operationManager.execute(operation);
			assertTrue(parentMethod.getParameters().contains(parameter));
			assertEquals(1, parentMethod.getParameters().indexOf(parameter));
			assertTrue(parentMethod.getTestCases().size() == 0);
			assertTrue(operationManager.undoEnabled());

			operationManager.undo();
			assertFalse(parentMethod.getParameters().contains(parameter));
			assertEquals(4, parentMethod.getTestCases().size());
			assertEquals(0, parentMethod.getTestCases().indexOf(testCase1));
			assertEquals(1, parentMethod.getTestCases().indexOf(testCase2));
			assertEquals(2, parentMethod.getTestCases().indexOf(testCase3));
			assertEquals(3, parentMethod.getTestCases().indexOf(testCase4));
			assertTrue(operationManager.redoEnabled());

			operationManager.redo();
			assertTrue(parentMethod.getParameters().contains(parameter));
			assertEquals(1, parentMethod.getParameters().indexOf(parameter));
			assertTrue(parentMethod.getTestCases().size() == 0);
			assertTrue(operationManager.undoEnabled());
		}catch(ModelOperationException e){
			fail("Unexpected exception: " + e.getMessage());
		}

	}

	@Test
	public void methodSignatureTest(boolean sameArgNames, boolean sameArgTypes, boolean sameName, boolean duplicate){
//		sameArgNames = true;
//		sameArgTypes = false;
//		sameName = true;
//		duplicate = true;

		String existingMethodName = "existingMethod";
		String[] existingParameterNames = new String[]{"existingArg1", "existingArg2", "existingArg3"};
		String[] existingParameterTypes = new String[]{"int", "boolean", "double"};
		ClassNode classNode = new ClassNode("TestClass");
		MethodNode existingMethod = new MethodNode(existingMethodName);
		for(int i = 0; i < existingParameterNames.length; ++i){
			existingMethod.addParameter(new MethodParameterNode(existingParameterNames[i], existingParameterTypes[i], "0", false));
		}
		classNode.addMethod(existingMethod);

		String methodName = sameName?existingMethodName:"notExistingMethodName";
		MethodNode targetMethod = new MethodNode(methodName);
		for(int i = 0; i < existingParameterNames.length - 1; ++i){
			targetMethod.addParameter(new MethodParameterNode(existingParameterNames[i], existingParameterTypes[i], "0", false));
		}
		classNode.addMethod(targetMethod);

		String newParameterName = sameArgNames?existingParameterNames[existingParameterNames.length - 1]:"notExistingArgName";
		String newParameterType = sameArgTypes?existingParameterTypes[existingParameterTypes.length - 1]:"float";
		MethodParameterNode newArg = new MethodParameterNode(newParameterName, newParameterType, "0", false);

		ModelOperationManager operationManager = new ModelOperationManager();
		IModelOperation operation = new MethodOperationAddParameter(targetMethod, newArg);
		try{
			operationManager.execute(operation);
			if(duplicate){
				fail("Exception expected");
			}
			assertTrue(targetMethod.getParameters().contains(newArg));
			assertEquals(targetMethod.getParameters().size() - 1, targetMethod.getParameters().indexOf(newArg));
		}catch(ModelOperationException e){
			if(duplicate == false){
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

}

