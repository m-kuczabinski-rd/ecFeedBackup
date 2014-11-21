package com.testify.ecfeed.adapter.operations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.ModelOperationManager;
import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.testutils.ModelTestUtils;

@RunWith(OnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@EcModel("test/com.testify.ecfeed.adapter.operations.ect")
@Constraints(Constraints.ALL)
public class MethodOperationRemoveParameterTest{

//	@Test
	public void signatureCheckTest(boolean sameParameterNames, boolean sameParameterTypes, boolean sameMethodName, boolean duplicate){
		ClassNode classNode = new ClassNode("TestClass");
		String similarMethodName = "testMethod";
		String targetMethodName = sameMethodName?similarMethodName:"otherTestMethod";
		MethodNode similarMethod = new MethodNode(similarMethodName);
		MethodNode targetMethod = new MethodNode(targetMethodName);
		classNode.addMethod(targetMethod);
		classNode.addMethod(similarMethod);

		ModelOperationManager operationManager = new ModelOperationManager();

		for(int i = 0; i < 3; i++){
			similarMethod.addParameter(new ParameterNode("arg" + i, "int", "0", false));
			if(i < 2){
				targetMethod.addParameter(new ParameterNode("arg" + i, "int", "0", false));
			}
			else{
				String type = sameParameterTypes?"int":"float";
				String name = sameParameterNames?"arg"+i:"differentName";
				targetMethod.addParameter(new ParameterNode(name, type, "0", false));
			}

		}

		ParameterNode removed = new ParameterNode("removed", "String", "xxx", false);
		targetMethod.addParameter(removed);

		try{
			IModelOperation operation = new MethodOperationRemoveParameter(targetMethod, removed);
			operationManager.execute(operation);
			if(duplicate){
				fail("Exception expected");
			}
		}catch(ModelOperationException e){
			if(duplicate == false){
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void consistenceCheckTest(boolean removedParameterExpected){
		ClassNode classNode = new ClassNode("TestClass");
		MethodNode target = new MethodNode("target");
		classNode.addMethod(target);

		ParameterNode removedParameter = new ParameterNode("removed", "int", "0", removedParameterExpected);
		ChoiceNode removedChoice = new ChoiceNode("removed1", "5");
		removedParameter.addChoice(removedChoice);
		target.addParameter(removedParameter);

		ParameterNode notRemovedParameter = new ParameterNode("notRemoved", "int", "0", false);
		ChoiceNode notRemovedChoice = new ChoiceNode("choice", "5");
		notRemovedParameter.addChoice(notRemovedChoice);
		target.addParameter(notRemovedParameter);

		//add test cases
		List<ChoiceNode> testData1 = new ArrayList<>();
		if(removedParameterExpected){
			ChoiceNode expected = new ChoiceNode("removed", "1");
			expected.setParent(removedParameter);
			testData1.add(expected);
		}
		else{
			testData1.add(removedChoice);
		}
		testData1.add(notRemovedChoice);

		TestCaseNode tc1 = new TestCaseNode("default", testData1);
		target.addTestCase(tc1);

		ConstraintNode dummyConstraint = new ConstraintNode("dummy", new Constraint(new StaticStatement(true), new StaticStatement(true)));
		AbstractStatement consequence;
		if(removedParameterExpected){
			ChoiceNode condition = new ChoiceNode("int", "7");
			condition.setParent(removedParameter);
			consequence = new ExpectedValueStatement(removedParameter, condition);
		}
		else{
			consequence = new ChoicesParentStatement(removedParameter, EStatementRelation.EQUAL, removedChoice);
		}
		ConstraintNode removedConstraint = new ConstraintNode("removed constraint", new Constraint(new StaticStatement(true), consequence));
		target.addConstraint(dummyConstraint);
		target.addConstraint(removedConstraint);


		//Actual tests
		ModelOperationManager operationManager = new ModelOperationManager();
		IModelOperation operation = new MethodOperationRemoveParameter(target, removedParameter);
		try{
			List<TestCaseNode> testCasesCopy = new ArrayList<TestCaseNode>();
			for(TestCaseNode tc : target.getTestCases()){
				testCasesCopy.add(tc.getCopy(target));
			}
			operationManager.execute(operation);
			assertFalse(target.getParameters().contains(removedParameter));

			//make sure that the parameter's choice was removed from test case
			assertFalse(tc1.getTestData().contains(removedChoice));
			//make sure that only the constraint that mentions the test case was removed
			assertTrue(target.getConstraintNodes().contains(dummyConstraint));
			assertFalse(target.getConstraintNodes().contains(removedConstraint));

			//undo
			operationManager.undo();
			ModelTestUtils.assertCollectionsEqual(testCasesCopy, target.getTestCases());
			assertTrue(target.getConstraintNodes().contains(dummyConstraint));
			assertTrue(target.getConstraintNodes().contains(removedConstraint));

			//redo
			operationManager.redo();
			assertFalse(target.getParameters().contains(removedParameter));
			assertTrue(target.getConstraintNodes().contains(dummyConstraint));
			assertFalse(target.getConstraintNodes().contains(removedConstraint));

			//undo
			operationManager.undo();
			ModelTestUtils.assertCollectionsEqual(testCasesCopy, target.getTestCases());
			assertTrue(target.getConstraintNodes().contains(dummyConstraint));
			assertTrue(target.getConstraintNodes().contains(removedConstraint));

			//redo
			operationManager.redo();
			assertFalse(target.getParameters().contains(removedParameter));
			assertTrue(target.getConstraintNodes().contains(dummyConstraint));
			assertFalse(target.getConstraintNodes().contains(removedConstraint));

		}catch(ModelOperationException e){
			fail("Unexpected exception: " + e.getMessage());
		}

	}
}

