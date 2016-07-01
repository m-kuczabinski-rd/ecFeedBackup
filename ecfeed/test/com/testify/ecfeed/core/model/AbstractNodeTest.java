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

package com.testify.ecfeed.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;

public class AbstractNodeTest{

	private class AbstractNodeImpl extends AbstractNode{

		public AbstractNodeImpl(String name) {
			super(name);
		}

		@Override
		public AbstractNode getCopy() {
			return null;
		}

		@Override
		public Object accept(IModelVisitor visitor) {
			return null;
		}

	}

	@Test
	public void testId(){
		AbstractNode node1 = new AbstractNodeImpl("name");
		AbstractNode node2 = new AbstractNodeImpl("name");

		assertNotEquals(node1.getId(), node2.getId());
	}

	@Test
	public void testName() {
		AbstractNode node = new AbstractNodeImpl("name");
		assertEquals("name", node.getName());
		node.setName("new name");
		assertEquals("new name", node.getName());
	}

	@Test
	public void testParent(){
		AbstractNode parent = new AbstractNodeImpl("parent");
		AbstractNode child = new AbstractNodeImpl("child");

		assertEquals(null, child.getParent());
		child.setParent(parent);
		assertEquals(parent, child.getParent());
	}

	@Test
	public void testHasChildren(){
		AbstractNode node = new AbstractNodeImpl("name");
		assertFalse(node.hasChildren());
	}

	@Test
	public void testGetRoot(){
		RootNode root = new RootNode("root", ModelVersionDistributor.getCurrentVersion());
		ClassNode classNode = new ClassNode("class");
		MethodNode method = new MethodNode("method");
		MethodParameterNode parameter = new MethodParameterNode("name", "type", "0", false);
		MethodParameterNode expCat = new MethodParameterNode("name", "type", "0", true);
		expCat.setDefaultValueString("0");
		ConstraintNode constraint = new ConstraintNode("name", new Constraint(new StaticStatement(true), new StaticStatement(false)));
		TestCaseNode testCase = new TestCaseNode("name", new ArrayList<ChoiceNode>());
		ChoiceNode choice = new ChoiceNode("name", "0");

		parameter.addChoice(choice);
		method.addParameter(parameter);
		method.addParameter(expCat);
		method.addConstraint(constraint);
		method.addTestCase(testCase);
		classNode.addMethod(method);
		root.addClass(classNode);

		assertEquals(root, root.getRoot());
		assertEquals(root, classNode.getRoot());
		assertEquals(root, method.getRoot());
		assertEquals(root, parameter.getRoot());
		assertEquals(root, expCat.getRoot());
		assertEquals(root, constraint.getRoot());
		assertEquals(root, testCase.getRoot());
		assertEquals(root, choice.getRoot());
	}

	@Test
	public void testEquals(){
		AbstractNode node1 = new AbstractNodeImpl("name");
		AbstractNode node2 = new AbstractNodeImpl("name");

		assertNotEquals(node1, node2);
		assertEquals(node1, node1);
	}

	//	@Test
	//	public void testMoveChild(){
	//		RootNode root = new RootNode("name");
	//		ClassNode class1 = new ClassNode("name");
	//		ClassNode class2 = new ClassNode("name");
	//		ClassNode class3 = new ClassNode("name");
	//		
	//		root.addClass(class1);
	//		root.addClass(class2);
	//		root.addClass(class3);
	//		
	//		int index = root.getClasses().indexOf(class2);
	//		root.moveChild(class2, true);
	//		assertTrue(root.getClasses().indexOf(class2) == index - 1);
	//		root.moveChild(class2, false);
	//		assertTrue(root.getClasses().indexOf(class2) == index);
	//	}

	@Test
	public void testSubtreeSize(){
		RootNode root = new RootNode("root", ModelVersionDistributor.getCurrentVersion());
		ClassNode classNode = new ClassNode("class");
		MethodNode method = new MethodNode("method");
		MethodParameterNode parameter = new MethodParameterNode("name", "type", "0", false);
		MethodParameterNode expCat = new MethodParameterNode("name", "type", "0", true);
		expCat.setDefaultValueString("0");
		ConstraintNode constraint = new ConstraintNode("name", new Constraint(new StaticStatement(true), new StaticStatement(false)));
		TestCaseNode testCase = new TestCaseNode("name", new ArrayList<ChoiceNode>());
		ChoiceNode choice = new ChoiceNode("name", "0");

		parameter.addChoice(choice);
		method.addParameter(parameter);
		method.addParameter(expCat);
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
		assertEquals(2, parameter.subtreeSize());
		assertEquals(1, choice.subtreeSize());
	}

	@Test
	public void getChildTest(){
		RootNode root = new RootNode("root", ModelVersionDistributor.getCurrentVersion());
		ClassNode classNode = new ClassNode("class");
		MethodNode method = new MethodNode("method");
		MethodParameterNode parameter = new MethodParameterNode("parameter", "type", "0", false);
		MethodParameterNode expCat = new MethodParameterNode("expCat", "type", "0", true);
		expCat.setDefaultValueString("0");
		ConstraintNode constraint = new ConstraintNode("constraint", new Constraint(new StaticStatement(true), new StaticStatement(false)));
		TestCaseNode testCase = new TestCaseNode("testCase", new ArrayList<ChoiceNode>());
		ChoiceNode p = new ChoiceNode("p", "0");
		ChoiceNode p1 = new ChoiceNode("p1", "0");

		p.addChoice(p1);
		parameter.addChoice(p);
		method.addParameter(parameter);
		method.addParameter(expCat);
		method.addConstraint(constraint);
		method.addTestCase(testCase);
		classNode.addMethod(method);
		root.addClass(classNode);

		assertEquals(classNode, root.getChild("class"));
		assertEquals(method, root.getChild("class:method"));
		assertEquals(method, classNode.getChild("method"));
		assertEquals(parameter, root.getChild("class:method:parameter"));
		assertEquals(parameter, classNode.getChild("method:parameter"));
		assertEquals(parameter, method.getChild("parameter"));
		assertEquals(expCat, root.getChild("class:method:expCat"));
		assertEquals(expCat, classNode.getChild("method:expCat"));
		assertEquals(expCat, method.getChild("expCat"));
		assertEquals(constraint, root.getChild("class:method:constraint"));
		assertEquals(constraint, classNode.getChild("method:constraint"));
		assertEquals(constraint, method.getChild("constraint"));
		assertEquals(testCase, root.getChild("class:method:testCase"));
		assertEquals(testCase, classNode.getChild("method:testCase"));
		assertEquals(testCase, method.getChild("testCase"));
		assertEquals(p, root.getChild("class:method:parameter:p"));
		assertEquals(p, classNode.getChild("method:parameter:p"));
		assertEquals(p, method.getChild("parameter:p"));
		assertEquals(p, parameter.getChild("p"));
		assertEquals(p1, root.getChild("class:method:parameter:p:p1"));
		assertEquals(p1, classNode.getChild("method:parameter:p:p1"));
		assertEquals(p1, method.getChild("parameter:p:p1"));
		assertEquals(p1, parameter.getChild("p:p1"));
		assertEquals(p1, p.getChild("p1"));
	}

	@Test
	public void getSiblingTest(){
		MethodParameterNode cat = new MethodParameterNode("cat", "type", "0", false);
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p2 = new ChoiceNode("p2", "0");

		cat.addChoice(p1);
		cat.addChoice(p2);

		assertTrue(p1.hasSibling("p2"));
		assertFalse(p1.hasSibling("p1"));

		assertEquals(p2, p1.getSibling("p2"));
		assertEquals(null, p1.getSibling("p1"));
		assertEquals(null, p1.getSibling("some string"));

	}

	@Test
	public void compareTest(){
		AbstractNode n1 = new AbstractNodeImpl("n");
		AbstractNode n2 = new AbstractNodeImpl("n");

		assertTrue(n1.compare(n2));

		n2.setName("nn");
		assertFalse(n1.compare(n2));

		n1.setName("nn");
		assertTrue(n1.compare(n2));

	}
}
