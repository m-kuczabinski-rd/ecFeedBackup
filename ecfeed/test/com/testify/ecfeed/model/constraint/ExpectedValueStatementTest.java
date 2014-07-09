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
package com.testify.ecfeed.model.constraint;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class ExpectedValueStatementTest{
	
	private static MethodNode fMethod;
	private static CategoryNode fExpCategory1;
	private static CategoryNode fPartCategory1;
	private static CategoryNode fPartCategory2;
	private static String fExpectedValue1;

	
	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method");
		fExpectedValue1 = "value1";

		fExpCategory1 = new CategoryNode("category", "type", true);
		fExpCategory1.setDefaultValueString(fExpectedValue1);
		fPartCategory1 = new CategoryNode("category", "type", false);
		fPartCategory2 = new CategoryNode("category", "type", false);

		fMethod.addCategory(fPartCategory1);
		fMethod.addCategory(fExpCategory1);
		fMethod.addCategory(fPartCategory2);
		
	}
	
	@Test
	public void testAdapt(){
		PartitionNode partition1 = new PartitionNode("partition1", "");
		PartitionNode statementPartition = new PartitionNode("exp_partition", "statement expected value");
		ExpectedValueStatement testStatement = new ExpectedValueStatement(fExpCategory1, statementPartition);
		
		List<PartitionNode> testData = new ArrayList<>();
		testData.add(partition1);	
		testData.add(fExpCategory1.getDefaultValuePartition());
		testData.add(partition1);
		
		testStatement.adapt(testData);
		
		assertTrue(testData.get(1).getValueString().equals(statementPartition.getValueString()));
	}
	
	@Test
	public void compareTest(){
		CategoryNode c1 = new CategoryNode("c", "type", true);
		CategoryNode c2 = new CategoryNode("c", "type", true);
		
		PartitionNode p1 = new PartitionNode("name", "value");
		PartitionNode p2 = new PartitionNode("name", "value");
		
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
