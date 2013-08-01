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

package com.testify.ecfeed.test.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;

public class ClassNodeTest extends ClassNode {
	public ClassNodeTest(){
		super("com.testify.ecfeed.model.ClassNodeTest");
	}

	@Test
	public void testGetName() {
		ClassNode testNode = new ClassNode("com.testify.ecfeed.model.TestClassNode");
		assertEquals("com.testify.ecfeed.model.TestClassNode", testNode.getName());
		assertEquals("TestClassNode", testNode.getLocalName());
		assertEquals("com.testify.ecfeed.model.TestClassNode", testNode.getQualifiedName());

		ClassNode testNode1 = new ClassNode("TestClassNode");
		assertEquals("TestClassNode", testNode1.getName());
		assertEquals("TestClassNode", testNode1.getLocalName());
		assertEquals("TestClassNode", testNode1.getQualifiedName());
		
		ClassNode testNode2 = new ClassNode("");
		assertEquals("", testNode2.getName());
		assertEquals("", testNode2.getLocalName());
		assertEquals("", testNode2.getQualifiedName());

		ClassNode testNode3 = new ClassNode(".");
		assertEquals(".", testNode3.getName());
		assertEquals("", testNode3.getLocalName());
		assertEquals(".", testNode3.getQualifiedName());
	}
	
	@Test
	public void addMethod(){
		ClassNode classNode = new ClassNode("com.testify.ecfeed.model.TestClassNode");
		MethodNode method = new MethodNode("testMethod");
		assertEquals(false, classNode.hasChildren());
		assertEquals(0, classNode.getChildren().size());
		assertEquals(null, method.getParent());

		classNode.addMethod(method);
		assertEquals(true, classNode.hasChildren());
		assertEquals(1, classNode.getChildren().size());
		assertEquals(classNode, method.getParent());
		assertEquals(method, classNode.getChildren().elementAt(0));
	}
	
	@Test
	public void testEquals(){
		ClassNode classNode = new ClassNode("com.test.classNode");
		MethodNode method1 = new MethodNode("method1");
		MethodNode method2 = new MethodNode("method2");
		classNode.addMethod(method1);
		classNode.addMethod(method2);

		ClassNode classNodeCopy = new ClassNode("com.test.classNode");
		MethodNode method1Copy = new MethodNode("method1");
		MethodNode method2Copy = new MethodNode("method2");
		classNodeCopy.addMethod(method1Copy);
		classNodeCopy.addMethod(method2Copy);
		
		assertTrue(classNode.equals(classNodeCopy));
		method2Copy.setName("name");
		assertFalse(classNode.equals(classNodeCopy));
	}
}
