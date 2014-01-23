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

import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;

public class RootNodeTest{
	
	@Test
	public void tesGetClass(){
		RootNode root = new RootNode("name");
		ClassNode classNode1 = new ClassNode("name");
		ClassNode classNode2 = new ClassNode("name");
		assertEquals(0,  root.getClasses().size());
		assertEquals(0,  root.getChildren().size());
		root.addClass(classNode1);
		root.addClass(classNode2);
		assertEquals(2,  root.getClasses().size());
		assertEquals(2,  root.getChildren().size());
		assertTrue(root.getClasses().contains(classNode1));
		assertTrue(root.getChildren().contains(classNode1));
		assertTrue(root.getClasses().contains(classNode2));
		assertTrue(root.getChildren().contains(classNode2));
		
		root.removeClass(classNode1);
		assertEquals(1,  root.getClasses().size());
		assertEquals(1,  root.getChildren().size());
		assertFalse(root.getClasses().contains(classNode1));
		assertFalse(root.getChildren().contains(classNode1));
		assertTrue(root.getClasses().contains(classNode2));
		assertTrue(root.getChildren().contains(classNode2));
	}
	

	@Test
	public void testGetClassModel(){
		RootNode root = new RootNode("name");
		ClassNode class1 = new ClassNode("com.example.class1");
		ClassNode class2 = new ClassNode("com.example.class2");
		ClassNode class3 = new ClassNode("class1");
		
		root.addClass(class1);
		root.addClass(class2);
		root.addClass(class3);
		
		assertEquals(class1, root.getClassModel("com.example.class1"));
	}
}