/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.testutils.RandomModelGenerator;

public class TestCaseNodeTest {

	@Test
	public void compare(){
		ChoiceNode p1 = new ChoiceNode("name", "value");
		ChoiceNode p2 = new ChoiceNode("name", "value");

		List<ChoiceNode> td1 = new ArrayList<ChoiceNode>();
		td1.add(p1);
		List<ChoiceNode> td2 = new ArrayList<ChoiceNode>();
		td2.add(p2);
		List<ChoiceNode> td3 = new ArrayList<ChoiceNode>();

		TestCaseNode tc1 = new TestCaseNode("name", td1);
		TestCaseNode tc2 = new TestCaseNode("name", td2);
		TestCaseNode tc3 = new TestCaseNode("name", td3);

		assertTrue(tc1.compare(tc2));
		assertFalse(tc1.compare(tc3));

		tc1.setName("tc1");
		assertFalse(tc1.compare(tc2));
		tc2.setName("tc1");
		assertTrue(tc1.compare(tc2));

		p1.setName("p1");
		assertFalse(tc1.compare(tc2));
		p2.setName("p1");
		assertTrue(tc1.compare(tc2));
	}

//	@Test
	public void compareSmokeTest(){
		for(int i = 0; i < 5; i++){
			RandomModelGenerator gen = new RandomModelGenerator();
			MethodNode m = gen.generateMethod(5, 0, 0);
			TestCaseNode t = gen.generateTestCase(m);

			assertTrue(t.compare(t));
		}
	}
}
