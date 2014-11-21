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

package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;

public class ClassNodeTest extends ClassNode {
	public ClassNodeTest(){
		super("com.testify.ecfeed.model.ClassNodeTest");
	}

	@Test
	public void getChildrenTest(){
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method1");
		MethodNode method2 = new MethodNode("method1");
		
		classNode.addMethod(method1);
		classNode.addMethod(method2);
		
		List<? extends AbstractNode> children = classNode.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(method1));
		assertTrue(children.contains(method2));
	}
	
	@Test
	public void getQualifiedNameTest() {
		ClassNode classNode = new ClassNode("com.example.ClassName");
		assertEquals("com.example.ClassName", classNode.getQualifiedName());
		assertEquals("com.example.ClassName", classNode.getName());
	}
	
	@Test
	public void getLocalNameTest(){
		ClassNode classNode = new ClassNode("com.example.ClassName");
		assertEquals("ClassName", classNode.getLocalName());
	}

	@Test
	public void getMethodTest() {
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method");
		MethodNode method2 = new MethodNode("method");
		
		List<String> method1Types = new ArrayList<String>();
		method1Types.add("int");
		method1Types.add("double");
		
		List<String> method2Types = new ArrayList<String>();
		method2Types.add("int");
		method2Types.add("int");

		for(String type : method1Types){
			method1.addParameter(new ParameterNode("parameter", type,"0",  false));
		}

		for(String type : method2Types){
			method2.addParameter(new ParameterNode("parameter", type,"0",  false));
		}
		
		classNode.addMethod(method1);
		classNode.addMethod(method2);
		
		assertEquals(method1, classNode.getMethod("method", method1Types));
		assertEquals(method2, classNode.getMethod("method", method2Types));
	}

	@Test
	public void getMethodsTest() {
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method");
		MethodNode method2 = new MethodNode("method");
		classNode.addMethod(method1);
		classNode.addMethod(method2);
		
		assertTrue(classNode.getMethods().contains(method1));
		assertTrue(classNode.getMethods().contains(method2));
	}
	
	@Test
	public void getTestSuitesTest(){
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method");
		MethodNode method2 = new MethodNode("method");

		method1.addTestCase(new TestCaseNode("suite 1", null));
		method1.addTestCase(new TestCaseNode("suite 2", null));
		method1.addTestCase(new TestCaseNode("suite 2", null));
		method1.addTestCase(new TestCaseNode("suite 3", null));

		method2.addTestCase(new TestCaseNode("suite 1", null));
		method2.addTestCase(new TestCaseNode("suite 4", null));
		method2.addTestCase(new TestCaseNode("suite 2", null));
		method2.addTestCase(new TestCaseNode("suite 3", null));
		
		classNode.addMethod(method1);
		classNode.addMethod(method2);

		assertEquals(4, classNode.getTestSuites().size());
		assertTrue(classNode.getTestSuites().contains("suite 1"));
		assertTrue(classNode.getTestSuites().contains("suite 2"));
		assertTrue(classNode.getTestSuites().contains("suite 3"));
		assertTrue(classNode.getTestSuites().contains("suite 4"));
		assertFalse(classNode.getTestSuites().contains("unused test suite"));
	}
	
	@Test
	public void compareTest(){
		ClassNode c1 = new ClassNode("c1");
		ClassNode c2 = new ClassNode("c2");
		
		assertFalse(c1.compare(c2));
		
		c2.setName("c1");
		assertTrue(c1.compare(c2));
		
		MethodNode m1 = new MethodNode("m1");
		MethodNode m2 = new MethodNode("m2");
		
		c1.addMethod(m1);
		assertFalse(c1.compare(c2));
		
		c2.addMethod(m2);
		assertFalse(c1.compare(c2));

		m2.setName("m1");
		assertTrue(c1.compare(c2));
	}
}
