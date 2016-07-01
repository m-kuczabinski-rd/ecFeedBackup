package com.testify.ecfeed.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.ModelOperationManager;
import com.ecfeed.core.adapter.operations.AbstractParameterOperationSetType;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IChoicesParentVisitor;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;


@RunWith(OnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@EcModel("test/com.testify.ecfeed.adapter.operations.ect")
@Constraints(Constraints.ALL)
public class AbstractParameterOperationSetTypeTest{

	private ModelOperationManager fOperationManager;
	private ITypeAdapterProvider fAdapterProvider = new EclipseTypeAdapterProvider();

	private class AbstractParameterNodeImp extends AbstractParameterNode{

		public AbstractParameterNodeImp(String name, String type) {
			super(name, type);
		}

		@Override
		public AbstractParameterNode getParameter() {
			return this;
		}

		@Override
		public AbstractNode getCopy() {
			return null;
		}

		@Override
		public Object accept(IModelVisitor visitor) throws Exception {
			return null;
		}

		@Override
		public List<MethodNode> getMethods() {
			return null;
		}

		@Override
		public Object accept(IParameterVisitor visitor) throws Exception {
			return null;
		}

		@Override
		public Object accept(IChoicesParentVisitor visitor) throws Exception {
			return null;
		}

		@Override
		public Set<ConstraintNode> mentioningConstraints() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<ConstraintNode> mentioningConstraints(String label) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Test
	public void typeNameTest(String currentTypeName, String newTypeName, boolean exceptionExpected){
		AbstractParameterNode parameter = new AbstractParameterNodeImp("parameter", currentTypeName);
		fOperationManager = new ModelOperationManager();
		IModelOperation operation = new AbstractParameterOperationSetType(parameter, newTypeName, fAdapterProvider);
		try{
			fOperationManager.execute(operation);
			if(exceptionExpected){
				fail("Exception expected");
			}
			assertEquals(newTypeName, parameter.getType());

			fOperationManager.undo();
			assertEquals(currentTypeName, parameter.getType());
			fOperationManager.redo();
			assertEquals(newTypeName, parameter.getType());
			fOperationManager.undo();
			assertEquals(currentTypeName, parameter.getType());
			fOperationManager.redo();
			assertEquals(newTypeName, parameter.getType());
		}catch(ModelOperationException e){
			if(exceptionExpected == false){
				fail("Unexception exception: " + e.getMessage());
			}
			assertEquals(currentTypeName, parameter.getType());
		}
	}

	@Test
	public void choicesConsistanceTest(){
		fOperationManager = new ModelOperationManager();
		String convertableValue = "0";
		String notConvertableValue = "not convertable";

		AbstractParameterNode parameter = new AbstractParameterNodeImp("parameter", "String");
		ChoiceNode choice1 = new ChoiceNode("choice1", convertableValue);
		ChoiceNode choice11 = new ChoiceNode("choice1.1", convertableValue);
		ChoiceNode choice12 = new ChoiceNode("choice1.2", notConvertableValue);
		ChoiceNode choice121 = new ChoiceNode("choice1.2.1", notConvertableValue);
		ChoiceNode choice122 = new ChoiceNode("choice1.2.1", convertableValue);
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice12.addChoice(choice121);
		choice12.addChoice(choice122);
		ChoiceNode choice2 = new ChoiceNode("choice2", convertableValue);
		ChoiceNode choice21 = new ChoiceNode("choice2.1", notConvertableValue);
		ChoiceNode choice22 = new ChoiceNode("choice2.2", notConvertableValue);
		choice2.addChoice(choice21);
		choice2.addChoice(choice22);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		IModelOperation operation = new AbstractParameterOperationSetType(parameter, "int", fAdapterProvider);

		try{
			fOperationManager.execute(operation);
			assertEquals("int", parameter.getType());
			assertTrue(parameter.getChoices().contains(choice1));
			assertEquals(convertableValue, choice11.getValueString());
			assertFalse(parameter.getChoices().contains(choice2));
			assertTrue(choice1.getChoices().contains(choice11));
			assertTrue(choice1.getChoices().contains(choice12));
			assertTrue(choice12.getValueString() != null);

			fOperationManager.undo();
			assertEquals("String", parameter.getType());
			assertTrue(parameter.getChoices().contains(choice1));
			assertTrue(parameter.getChoices().contains(choice2));
			assertTrue(choice1.getChoices().contains(choice11));
			assertTrue(choice1.getChoices().contains(choice12));
			assertTrue(choice2.getChoices().contains(choice21));
			assertTrue(choice2.getChoices().contains(choice22));
			assertEquals(convertableValue, choice1.getValueString());
			assertEquals(convertableValue, choice11.getValueString());
			assertEquals(notConvertableValue, choice12.getValueString());
			assertEquals(convertableValue, choice2.getValueString());
			assertEquals(notConvertableValue, choice21.getValueString());
			assertEquals(notConvertableValue, choice22.getValueString());

			fOperationManager.redo();
			assertEquals("int", parameter.getType());
			assertTrue(parameter.getChoices().contains(choice1));
			assertEquals(convertableValue, choice11.getValueString());
			assertFalse(parameter.getChoices().contains(choice2));
			assertTrue(choice1.getChoices().contains(choice11));
			assertTrue(choice1.getChoices().contains(choice12));
		}catch(ModelOperationException e){
			fail("Unexpected exception: " + e.getMessage());
		}

	}

}

