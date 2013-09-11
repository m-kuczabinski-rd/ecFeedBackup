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
import com.testify.ecfeed.model.RootNode;

public class RootNodeTest extends RootNode {

	public RootNodeTest() {
		super("RootNodeTest");
	}

	@Test
	public void test() {
		RootNode root = new RootNode("root");
		ClassNode klass = new ClassNode("com.test.ClassName");
		assertEquals(false, root.hasChildren());
		assertEquals(0, root.getChildren().size());
		assertEquals(0, root.getClasses().size());
		assertEquals(null, klass.getParent());

		root.addClass(klass);
		assertEquals(true, root.hasChildren());
		assertEquals(1, root.getChildren().size());
		assertEquals(1, root.getClasses().size());
		assertEquals(root, klass.getParent());
		assertEquals(klass, root.getChildren().get(0));
	}
	
	@Test
	public void testEquals(){
		RootNode root = new RootNode("root");
		ClassNode cl1 = new ClassNode("com.test.Class1");
		ClassNode cl2 = new ClassNode("com.test.Class2");
		root.addClass(cl1);
		root.addClass(cl2);

		RootNode rootCopy = new RootNode("root");
		ClassNode cl1Copy = new ClassNode("com.test.Class1");
		ClassNode cl2Copy = new ClassNode("com.test.Class2");
		rootCopy.addClass(cl1Copy);
		rootCopy.addClass(cl2Copy);
		
		assertTrue(root.equals(rootCopy));
		cl2Copy.setName("com.test.Class2Copy");
		assertFalse(root.equals(rootCopy));
	}
}
