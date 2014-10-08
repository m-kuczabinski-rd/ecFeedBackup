package com.testify.ecfeed.ui.editor.actions;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.junit.StaticRunner;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.expected;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.testutils.ENodeType;
import com.testify.ecfeed.testutils.RandomModelGenerator;

@RunWith(StaticRunner.class)
@EcModel("test/com.testify.ecfeed.ui.editor.actions.ect")
public class AddChildActionTest {

	@Test
	public void isEnabledTest(ENodeType parentType, @expected boolean enabled){
		System.out.println("isEnabledTest(" + parentType + ", " + enabled + ")");
		RandomModelGenerator modelGenerator = new RandomModelGenerator();
		GenericNode node = modelGenerator.generateNode(parentType);
		//special case - expected parameters of primitive type are not enabled 
		if(node instanceof CategoryNode){
			if(((CategoryNode)node).isExpected() && JavaUtils.isPrimitive(((CategoryNode) node).getType())){
				enabled = false;
			}
			else{
				enabled = true;
			}
		}
		
		List<AbstractAddChildAction> addChildActions = new AddChildActionFactory(null, null).getPossibleActions(node);
		for(AbstractAddChildAction action : addChildActions){
			boolean isEnabled = action.isEnabled();
			assertEquals(enabled, isEnabled);
		}
	}
}
