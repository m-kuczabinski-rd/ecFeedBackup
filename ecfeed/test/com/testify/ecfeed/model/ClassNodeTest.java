package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;

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
}
