package com.testify.ecfeed.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.ModelOperationManager;
import com.ecfeed.core.adapter.operations.GenericOperationRemoveParameter;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParametersParentNode;
import com.testify.ecfeed.core.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.testutils.ENodeType;
import com.testify.ecfeed.testutils.ModelTestUtils;

@RunWith(OnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@EcModel("test/com.testify.ecfeed.adapter.operations.ect")
@Constraints(Constraints.ALL)
public class GenericOperationRemoveParameterTest{

	@Test
	public void seriesTest(ENodeType parentType){
		Random rand = new Random();
		ParametersParentNode parent = (ParametersParentNode)ModelTestUtils.getNode(parentType, "parent");
		int numOfOperations = 10;
		List<AbstractParameterNode> removedParameters = new ArrayList<>();
		List<Integer> removedIndices = new ArrayList<>();
		ModelOperationManager operationManager = new ModelOperationManager();

		for(int i = 0; i < numOfOperations; ++i){
			MethodParameterNode parameter = new MethodParameterNode("arg"+i, "int", "0", false);
			parent.addParameter(parameter);
		}

		try{
			for(int i = 0; i < numOfOperations; ++i){
				int index = rand.nextInt(parent.getParameters().size());
				AbstractParameterNode removed = parent.getParameters().get(index);
				removedParameters.add(removed);
				removedIndices.add(index);
				IModelOperation operation = new GenericOperationRemoveParameter(parent, removed);

				operationManager.execute(operation);
				assertFalse(parent.getParameters().contains(removed));
			}
			for(int i = numOfOperations -1; i >= 0; --i){
				assertTrue(operationManager.undoEnabled());
				operationManager.undo();
				assertTrue(parent.getParameters().contains(removedParameters.get(i)));
				assertTrue(parent.getParameters().contains(removedParameters.get(i)));
				assertEquals((int)removedIndices.get(i), parent.getParameters().indexOf(removedParameters.get(i)));
			}
			for(int i = 0; i < numOfOperations; ++i){
				assertTrue(operationManager.redoEnabled());
				operationManager.redo();
				assertFalse(parent.getParameters().contains(removedParameters.get(i)));
			}

		}catch(ModelOperationException e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}

