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

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedCategoryNode;

import static org.junit.Assert.*;

public class ExpectedValueStatementTest{
	
	private static MethodNode fMethod;
	private static ExpectedCategoryNode fExpCategory1;
	private static PartitionedCategoryNode fPartCategory1;
	private static PartitionedCategoryNode fPartCategory2;
	private static String fExpectedValue1;

	
	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method");
		fExpectedValue1 = "value1";

		fExpCategory1 = new ExpectedCategoryNode("category", "type", fExpectedValue1);
		fPartCategory1 = new PartitionedCategoryNode("category", "type");
		fPartCategory2 = new PartitionedCategoryNode("category", "type");

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
		
		assertTrue(testData.get(1).getValue().equals(statementPartition.getValue()));
	}
}
