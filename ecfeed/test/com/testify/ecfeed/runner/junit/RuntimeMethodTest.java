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

package com.testify.ecfeed.runner.junit;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.testify.ecfeed.abstraction.java.ModelClassLoader;
import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.runner.junit.RuntimeMethod;

public class RuntimeMethodTest {

	private Set<List<Integer>> fExecuted;
	private final int MAX_PARTITIONS = 10;
	
	private final Collection<IConstraint<PartitionNode>> EMPTY_CONSTRAINTS = 
			new ArrayList<IConstraint<PartitionNode>>();
	
	public void functionUnderTest(int arg1, int arg2){
		List<Integer> parameters = new ArrayList<Integer>();
		parameters.add(arg1);
		parameters.add(arg2);
		fExecuted.add(parameters);
	}

	@Test
	public void conformanceTest(){
		for(int j = 1; j <= MAX_PARTITIONS; ++j){
			test(2, j);
		}
	}
	
	public void test(int categories, int partitionsPerCategory) {
		List<List<PartitionNode>> input = generateInput(categories, partitionsPerCategory);
		IGenerator<PartitionNode> generator = new CartesianProductGenerator<PartitionNode>();
		try {
			Method methodUnterTest = this.getClass().getMethod("functionUnderTest", int.class, int.class);
			generator.initialize(input, EMPTY_CONSTRAINTS, null);
			RuntimeMethod testedMethod = new RuntimeMethod(methodUnterTest, generator, new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader()));
			fExecuted = new HashSet<List<Integer>>();
			testedMethod.invokeExplosively(this, (Object[])null);
			assertEquals(referenceResult(input), fExecuted);
		} catch (Throwable e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	private Set<List<Integer>> referenceResult(List<List<PartitionNode>> input) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		CartesianProductGenerator<PartitionNode> referenceGenerator = new CartesianProductGenerator<PartitionNode>();
		try {
			referenceGenerator.initialize(input, EMPTY_CONSTRAINTS, null);
			List<PartitionNode> next;
			while((next = referenceGenerator.next()) != null){
				List<Integer> testCase = new ArrayList<Integer>();
				for(PartitionNode parameter : next){
					testCase.add(Integer.valueOf(parameter.getValueString()));
				}
				result.add(testCase);
			}
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
		return result;
	}

	private List<List<PartitionNode>> generateInput(int categories,
			int partitions) {
		List<List<PartitionNode>> input = new ArrayList<List<PartitionNode>>();
		for(int i = 0; i < categories; ++i){
			input.add(generateCategory(partitions));
		}
		return input;
	}

	private List<PartitionNode> generateCategory(int partitions) {
		CategoryNode parent = new CategoryNode("Category", "int","0",  false);
		List<PartitionNode> category = new ArrayList<PartitionNode>();
		for(int i = 0; i < partitions; i++){
			PartitionNode partition = new PartitionNode(String.valueOf(i), String.valueOf(i));
			partition.setParent(parent);
			category.add(partition);
		}
		return category;
	}

}
