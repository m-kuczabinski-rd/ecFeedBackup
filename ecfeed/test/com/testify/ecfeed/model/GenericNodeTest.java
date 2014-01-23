/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StaticStatement;

public class GenericNodeTest{
	
	@Test
	public void testId(){
		GenericNode node1 = new GenericNode("name");
		GenericNode node2 = new GenericNode("name");
		
		assertNotEquals(node1.getId(), node2.getId());
	}
	
	@Test
	public void testName() {
		GenericNode node = new GenericNode("name");
		assertEquals("name", node.getName());
		node.setName("new name");
		assertEquals("new name", node.getName());
	}

	@Test
	public void testParent(){
		GenericNode parent = new GenericNode("parent");
		GenericNode child = new GenericNode("child");
		
		assertEquals(null, child.getParent());
		child.setParent(parent);
		assertEquals(parent, child.getParent());
	}
		
	@Test
	public void testHasChildren(){
		GenericNode node = new GenericNode("name");
		assertFalse(node.hasChildren());
	}
	
	@Test
	public void testGetRoot(){
		RootNode root = new RootNode("root");
		ClassNode classNode = new ClassNode("class");
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("name", "type");
		ExpectedValueCategoryNode expCat = new ExpectedValueCategoryNode("name", "type", 0);
		ConstraintNode constraint = new ConstraintNode("name", new Constraint(new StaticStatement(true), new StaticStatement(false)));
		TestCaseNode testCase = new TestCaseNode("name", new ArrayList<PartitionNode>());
		PartitionNode partition = new PartitionNode("name", 0);
		
		category.addPartition(partition);
		method.addCategory(category);
		method.addCategory(expCat);
		method.addConstraint(constraint);
		method.addTestCase(testCase);
		classNode.addMethod(method);
		root.addClass(classNode);
		
		assertEquals(root, root.getRoot());
		assertEquals(root, classNode.getRoot());
		assertEquals(root, method.getRoot());
		assertEquals(root, category.getRoot());
		assertEquals(root, expCat.getRoot());
		assertEquals(root, constraint.getRoot());
		assertEquals(root, testCase.getRoot());
		assertEquals(root, partition.getRoot());
	}

	@Test
	public void testEquals(){
		GenericNode node1 = new GenericNode("name");
		GenericNode node2 = new GenericNode("name");
		
		assertNotEquals(node1, node2);
		assertEquals(node1, node1);
	}

	@Test
	public void testMoveChild(){
		RootNode root = new RootNode("name");
		ClassNode class1 = new ClassNode("name");
		ClassNode class2 = new ClassNode("name");
		ClassNode class3 = new ClassNode("name");
		
		root.addClass(class1);
		root.addClass(class2);
		root.addClass(class3);
		
		int index = root.getClasses().indexOf(class2);
		root.moveChild(class2, true);
		assertTrue(root.getClasses().indexOf(class2) == index - 1);
		root.moveChild(class2, false);
		assertTrue(root.getClasses().indexOf(class2) == index);
	}
	
	@Test
	public void testSubtreeSize(){
		RootNode root = new RootNode("root");
		ClassNode classNode = new ClassNode("class");
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("name", "type");
		ExpectedValueCategoryNode expCat = new ExpectedValueCategoryNode("name", "type", 0);
		ConstraintNode constraint = new ConstraintNode("name", new Constraint(new StaticStatement(true), new StaticStatement(false)));
		TestCaseNode testCase = new TestCaseNode("name", new ArrayList<PartitionNode>());
		PartitionNode partition = new PartitionNode("name", 0);
		
		category.addPartition(partition);
		method.addCategory(category);
		method.addCategory(expCat);
		method.addConstraint(constraint);
		method.addTestCase(testCase);
		classNode.addMethod(method);
		root.addClass(classNode);

		assertEquals(8, root.subtreeSize());
		assertEquals(7, classNode.subtreeSize());
		assertEquals(6, method.subtreeSize());
		assertEquals(1, constraint.subtreeSize());
		assertEquals(1, testCase.subtreeSize());
		assertEquals(1, expCat.subtreeSize());
		assertEquals(2, category.subtreeSize());
		assertEquals(1, partition.subtreeSize());
	}
}