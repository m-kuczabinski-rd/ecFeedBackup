/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;

public class ExpectedValueStatementTest{
	
	private static MethodNode fMethod;
	private static ParameterNode fExpParameter1;
	private static ParameterNode fPartParameter1;
	private static ParameterNode fPartParameter2;
	private static String fExpectedValue1;

	
	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method");
		fExpectedValue1 = "value1";

		fExpParameter1 = new ParameterNode("parameter", "type","0",  true);
		fExpParameter1.setDefaultValueString(fExpectedValue1);
		fPartParameter1 = new ParameterNode("parameter", "type","0",  false);
		fPartParameter2 = new ParameterNode("parameter", "type", "0", false);

		fMethod.addParameter(fPartParameter1);
		fMethod.addParameter(fExpParameter1);
		fMethod.addParameter(fPartParameter2);
		
	}
	
	@Test
	public void testAdapt(){
		ChoiceNode partition1 = new ChoiceNode("partition1", "");
		ChoiceNode statementPartition = new ChoiceNode("exp_partition", "statement expected value");
		ExpectedValueStatement testStatement = new ExpectedValueStatement(fExpParameter1, statementPartition);
		
		List<ChoiceNode> testData = new ArrayList<>();
		testData.add(partition1);	
		testData.add(new ChoiceNode("", fExpParameter1.getDefaultValue()));
		testData.add(partition1);
		
		testStatement.adapt(testData);
		
		assertTrue(testData.get(1).getValueString().equals(statementPartition.getValueString()));
	}
	
	@Test
	public void compareTest(){
		ParameterNode c1 = new ParameterNode("c", "type", "0", true);
		ParameterNode c2 = new ParameterNode("c", "type", "0", true);
		
		ChoiceNode p1 = new ChoiceNode("name", "value");
		ChoiceNode p2 = new ChoiceNode("name", "value");
		
		ExpectedValueStatement s1 = new ExpectedValueStatement(c1, p1);
		ExpectedValueStatement s2 = new ExpectedValueStatement(c2, p2);
		
		assertTrue(s1.compare(s2));
		c1.setName("c1");
		assertFalse(s1.compare(s2));
		c2.setName("c1");
		assertTrue(s1.compare(s2));

		s1.getCondition().setValueString("v1");
		assertFalse(s1.compare(s2));
		s2.getCondition().setValueString("v1");
		assertTrue(s1.compare(s2));

	}
}
