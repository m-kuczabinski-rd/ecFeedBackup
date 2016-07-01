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

import static com.testify.ecfeed.testutils.Constants.SUPPORTED_TYPES;
import static com.testify.ecfeed.testutils.ModelTestUtils.assertElementsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class ChoiceNodeTest{
	@Test
	public void testValue() {
		ChoiceNode choice = new ChoiceNode("name", "value");
		assertEquals("value", choice.getValueString());
		choice.setValueString("new value");
		assertEquals("new value", choice.getValueString());
	}

	
	@Test
	public void testGetParameter(){
		MethodNode method = new MethodNode("method");
		MethodParameterNode parameter = new MethodParameterNode("name", "type","0",  false);
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p11 = new ChoiceNode("p11", "0");
		ChoiceNode p111 = new ChoiceNode("p111", "0");
		
		method.addParameter(parameter);
		parameter.addChoice(p1);
		p1.addChoice(p11);
		p11.addChoice(p111);
		
		assertEquals(p11, p111.getParent());
		assertEquals(p1, p11.getParent());
		assertEquals(parameter, p1.getParent());
		assertEquals(method, parameter.getParent());
		
		assertEquals(parameter, p1.getParameter());
		assertEquals(parameter, p11.getParameter());
		assertEquals(parameter, p111.getParameter());
	}
	
	
	@Test
	public void testLevel(){
		MethodNode method = new MethodNode("method");
		MethodParameterNode parameter = new MethodParameterNode("name", "type", "0", false);
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p11 = new ChoiceNode("p11", "0");
		ChoiceNode p111 = new ChoiceNode("p111", "0");
		
		method.addParameter(parameter);
		parameter.addChoice(p1);
		p1.addChoice(p11);
		p11.addChoice(p111);
		
		assertEquals(0, p1.level());
		assertEquals(1, p11.level());
		assertEquals(2, p111.level());
	}
	
//	@Test
//	public void isAncestorTest(){
//		ChoiceNode p = new ChoiceNode("p", "0");
//		ChoiceNode p1 = new ChoiceNode("p1", "0");
//		ChoiceNode p2 = new ChoiceNode("p2", "0");
//		ChoiceNode p11 = new ChoiceNode("p1", "0");
//		ChoiceNode p12 = new ChoiceNode("p2", "0");
//		ChoiceNode p21 = new ChoiceNode("p1", "0");
//		ChoiceNode p22 = new ChoiceNode("p2", "0");
//		
//		p.addChoice(p1);
//		p.addChoice(p2);
//		p1.addChoice(p11);
//		p1.addChoice(p12);
//		p2.addChoice(p21);
//		p2.addChoice(p22);
//		
//		assertTrue(p.isAncestor(p1));
//		assertTrue(p.isAncestor(p2));
//		assertTrue(p.isAncestor(p11));
//		assertTrue(p.isAncestor(p12));
//		assertTrue(p.isAncestor(p21));
//		assertTrue(p.isAncestor(p22));
//		assertFalse(p.isAncestor(p));
//		
//		assertTrue(p1.isAncestor(p11));
//		assertTrue(p1.isAncestor(p12));
//		assertFalse(p1.isAncestor(p21));
//		assertFalse(p1.isAncestor(p21));
//		assertFalse(p1.isAncestor(p));
//		assertFalse(p1.isAncestor(p1));
//		assertFalse(p1.isAncestor(p2));
//
//		assertTrue(p2.isAncestor(p21));
//		assertTrue(p2.isAncestor(p22));
//		assertFalse(p2.isAncestor(p11));
//		assertFalse(p2.isAncestor(p11));
//		assertFalse(p2.isAncestor(p1));
//		assertFalse(p2.isAncestor(p2));
//		assertFalse(p2.isAncestor(p));
//	}
	
//	@Test
//	public void isDescendantTest(){
//		ChoiceNode p = new ChoiceNode("p", "0");
//		ChoiceNode p1 = new ChoiceNode("p1", "0");
//		ChoiceNode p2 = new ChoiceNode("p2", "0");
//		ChoiceNode p11 = new ChoiceNode("p1", "0");
//		ChoiceNode p12 = new ChoiceNode("p2", "0");
//		ChoiceNode p21 = new ChoiceNode("p1", "0");
//		ChoiceNode p22 = new ChoiceNode("p2", "0");
//		
//		p.addChoice(p1);
//		p.addChoice(p2);
//		p1.addChoice(p11);
//		p1.addChoice(p12);
//		p2.addChoice(p21);
//		p2.addChoice(p22);
//		
//		assertTrue(p11.isDescendant(p1));
//		assertTrue(p11.isDescendant(p));
//		assertTrue(p12.isDescendant(p1));
//		assertTrue(p12.isDescendant(p));
//		assertFalse(p11.isDescendant(p2));
//		assertFalse(p11.isDescendant(p11));
//	}
	
	@Test
	public void getLeafsTest(){
		ChoiceNode p = new ChoiceNode("p", "0");
		
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p2 = new ChoiceNode("p1", "0");
		ChoiceNode p3 = new ChoiceNode("p1", "0");
		
		ChoiceNode p21 = new ChoiceNode("p21", "0");
		ChoiceNode p22 = new ChoiceNode("p22", "0");
		ChoiceNode p23 = new ChoiceNode("p23", "0");

		ChoiceNode p31 = new ChoiceNode("p31", "0");
		ChoiceNode p32 = new ChoiceNode("p32", "0");
		ChoiceNode p33 = new ChoiceNode("p33", "0");

		ChoiceNode p321 = new ChoiceNode("p321", "0");
		ChoiceNode p322 = new ChoiceNode("p322", "0");
		ChoiceNode p323 = new ChoiceNode("p323", "0");
		
		p.addChoice(p1);
		p.addChoice(p2);
		p.addChoice(p3);
		p2.addChoice(p21);
		p2.addChoice(p22);
		p2.addChoice(p23);
		p3.addChoice(p31);
		p3.addChoice(p32);
		p3.addChoice(p33);
		p32.addChoice(p321);
		p32.addChoice(p322);
		p32.addChoice(p323);
		
		assertTrue(p.getLeafChoices().contains(p1));
		assertTrue(p.getLeafChoices().contains(p21));
		assertTrue(p.getLeafChoices().contains(p22));
		assertTrue(p.getLeafChoices().contains(p23));
		assertTrue(p.getLeafChoices().contains(p31));
		assertTrue(p.getLeafChoices().contains(p321));
		assertTrue(p.getLeafChoices().contains(p322));
		assertTrue(p.getLeafChoices().contains(p323));
		assertTrue(p.getLeafChoices().contains(p33));
		
		assertFalse(p.getLeafChoices().contains(p2));
		assertFalse(p.getLeafChoices().contains(p3));
		assertFalse(p.getLeafChoices().contains(p32));
	}
	
	@Test
	public void getQualifiedNameTest(){
		ChoiceNode p = new ChoiceNode("p", "0");
		ChoiceNode q = new ChoiceNode("q", "0");
		
		p.addChoice(q);
		
		assertEquals(p.getName() + ":" + q.getName(), q.getQualifiedName());
	}
	
	@Test
	public void getAllDescendantsTest(){
		ChoiceNode p = new ChoiceNode("p", "0");
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p11 = new ChoiceNode("p11", "0");
		ChoiceNode p12 = new ChoiceNode("p12", "0");
		ChoiceNode p111 = new ChoiceNode("p111", "0");
		ChoiceNode p112 = new ChoiceNode("p112", "0");
		ChoiceNode p2 = new ChoiceNode("p2", "0");
		
		p.addChoice(p1);
		p.addChoice(p2);
		p1.addChoice(p11);
		p1.addChoice(p12);
		p11.addChoice(p111);
		p11.addChoice(p112);
		
		Set<String> names = p.getAllChoiceNames();
		assertTrue(names.contains("p:p1"));
		assertTrue(names.contains("p:p1:p11"));
		assertTrue(names.contains("p:p1:p12"));
		assertTrue(names.contains("p:p1:p11:p111"));
		assertTrue(names.contains("p:p1:p11:p112"));
		assertTrue(names.contains("p:p2"));
		
	}
	
	@Test 
	public void labelTest(){
		ChoiceNode p = new ChoiceNode("p", "0");
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p2 = new ChoiceNode("p2", "0");
		ChoiceNode p12 = new ChoiceNode("p12", "0");
		ChoiceNode p11 = new ChoiceNode("p11", "0");
		
		p.addChoice(p1);
		p.addChoice(p2);
		p1.addChoice(p11);
		p1.addChoice(p12);
		
		assertTrue(p.addLabel("pLabel.0"));
		assertTrue(p.addLabel("pLabel.1"));
		assertTrue(p1.addLabel("p1Label.0"));
		assertTrue(p1.addLabel("p1Label.1"));
		assertTrue(p2.addLabel("p2Label.0"));
		assertTrue(p2.addLabel("p2Label.1"));
		assertTrue(p11.addLabel("p11Label.0"));
		assertTrue(p11.addLabel("p11Label.1"));
		assertTrue(p12.addLabel("p12Label.0"));
		assertTrue(p12.addLabel("p12Label.1"));
		
//		assertFalse(p.addLabel("pLabel.0"));
//		assertFalse(p12.addLabel("pLabel.0"));

		
		//getLabels
		assertTrue(p12.getLabels().contains("p12Label.0"));
		assertTrue(p12.getLabels().contains("p12Label.1"));
		assertFalse(p12.getLabels().contains("p1Label.0"));
		assertFalse(p12.getLabels().contains("p1Label.1"));

		//getAllLabels
		assertTrue(p12.getAllLabels().contains("p12Label.0"));
		assertTrue(p12.getAllLabels().contains("p12Label.1"));
		assertTrue(p12.getAllLabels().contains("p1Label.0"));
		assertTrue(p12.getAllLabels().contains("p1Label.1"));
		assertTrue(p12.getAllLabels().contains("pLabel.0"));
		assertTrue(p12.getAllLabels().contains("pLabel.1"));
		
		assertFalse(p12.getAllLabels().contains("p11Label.0"));
		assertFalse(p12.getAllLabels().contains("p11Label.1"));
		assertFalse(p12.getAllLabels().contains("p2Label.0"));
		assertFalse(p12.getAllLabels().contains("p2Label.1"));
		
		//getInheritedLabels test
		assertFalse(p12.getInheritedLabels().contains("p12Label.0"));
		assertFalse(p12.getInheritedLabels().contains("p12Label.1"));
		assertTrue(p12.getInheritedLabels().contains("p1Label.0"));
		assertTrue(p12.getInheritedLabels().contains("p1Label.1"));
		assertTrue(p12.getInheritedLabels().contains("pLabel.0"));
		assertTrue(p12.getInheritedLabels().contains("pLabel.1"));
		
//		//getDescendingLabels test
//		assertTrue(p.getAllDescendingLabels().contains("pLabel.0"));
//		assertTrue(p.getAllDescendingLabels().contains("pLabel.1"));
//		assertTrue(p.getAllDescendingLabels().contains("p1Label.0"));
//		assertTrue(p.getAllDescendingLabels().contains("p1Label.1"));
//		assertTrue(p.getAllDescendingLabels().contains("p2Label.0"));
//		assertTrue(p.getAllDescendingLabels().contains("p2Label.1"));
//		assertTrue(p.getAllDescendingLabels().contains("p11Label.0"));
//		assertTrue(p.getAllDescendingLabels().contains("p11Label.1"));
//		assertTrue(p.getAllDescendingLabels().contains("p12Label.0"));
//		assertTrue(p.getAllDescendingLabels().contains("p12Label.1"));
//
//		assertFalse(p1.getAllDescendingLabels().contains("pLabel.0"));
//		assertFalse(p1.getAllDescendingLabels().contains("pLabel.1"));
//		assertTrue(p1.getAllDescendingLabels().contains("p1Label.0"));
//		assertTrue(p1.getAllDescendingLabels().contains("p1Label.1"));
//		assertFalse(p1.getAllDescendingLabels().contains("p2Label.0"));
//		assertFalse(p1.getAllDescendingLabels().contains("p2Label.1"));
//		assertTrue(p1.getAllDescendingLabels().contains("p11Label.0"));
//		assertTrue(p1.getAllDescendingLabels().contains("p11Label.1"));
//		assertTrue(p1.getAllDescendingLabels().contains("p12Label.0"));
//		assertTrue(p1.getAllDescendingLabels().contains("p12Label.1"));
//		
	}
	
//	@Test
	public void compareSmokeTest(){
		for(String type : SUPPORTED_TYPES){
			
			ChoiceNode p = new RandomModelGenerator().generateChoice(3, 3, 3, type);
			assertElementsEqual(p, p);
		}
	}
	
	/*******************compare()***************************/
	@Test
	public void compareNameTest(){
		ChoiceNode p1 = new ChoiceNode("p", "VALUE");
		ChoiceNode p2 = new ChoiceNode("p", "VALUE");
		
		assertTrue(p1.compare(p2));
		
		p1.setName("p1");
		assertFalse(p1.compare(p2));
		p2.setName("p1");
		assertTrue(p1.compare(p2));
	}
	
	@Test
	public void compareValueTest(){
		ChoiceNode p1 = new ChoiceNode("p", "VALUE");
		ChoiceNode p2 = new ChoiceNode("p", "VALUE");
		
		assertTrue(p1.compare(p2));
		
		p1.setValueString("NEW VALUE");
		assertFalse(p1.compare(p2));
		p2.setValueString("NEW VALUE");
		assertTrue(p1.compare(p2));
	}
	
	@Test
	public void compareLabelsTest(){
		ChoiceNode p1 = new ChoiceNode("p", "VALUE");
		ChoiceNode p2 = new ChoiceNode("p", "VALUE");
		
		assertTrue(p1.compare(p2));

		p1.addLabel("label");
		assertFalse(p1.compare(p2));
		p2.addLabel("label");
		assertTrue(p1.compare(p2));

		p1.addLabel("label1");
		assertFalse(p1.compare(p2));
		p2.addLabel("label1");
		assertTrue(p1.compare(p2));
	}
	
	@Test
	public void compareChildrenTest(){
		ChoiceNode p1 = new ChoiceNode("p", "VALUE");
		ChoiceNode p2 = new ChoiceNode("p", "VALUE");

		ChoiceNode p11 = new ChoiceNode("p", "VALUE");
		ChoiceNode p21 = new ChoiceNode("p", "VALUE");

		assertTrue(p1.compare(p2));

		p1.addChoice(p11);
		assertFalse(p1.compare(p2));
		p2.addChoice(p21);
		assertTrue(p1.compare(p2));
		
		p11.setName("p11");
		assertFalse(p1.compare(p2));
		p21.setName("p11");
		assertTrue(p1.compare(p2));
	}
	
}
