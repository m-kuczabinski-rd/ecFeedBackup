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

import static com.testify.ecfeed.testutils.Constants.SUPPORTED_TYPES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.testify.ecfeed.testutils.RandomModelGenerator;

public class ParameterNodeTest{
	
	@Test
	public void addPartitionTest() {
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		
		assertEquals(0, parameter.getPartitions().size());
		
		ChoiceNode partition = new ChoiceNode("partition", "0"); 
		parameter.addPartition(partition);

		assertEquals(1, parameter.getPartitions().size());
	}
	
	@Test
	public void getPartitionTest(){
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		ChoiceNode p = new ChoiceNode("p", "0");
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p11 = new ChoiceNode("p11", "0");
		parameter.addPartition(p);
		p.addPartition(p1);
		p1.addPartition(p11);
		
		assertEquals(p, parameter.getPartition("p"));
		assertEquals(p1, parameter.getPartition("p:p1"));
		assertEquals(p11, parameter.getPartition("p:p1:p11"));
		assertEquals(null, parameter.getPartition("p1"));
		assertEquals(null, parameter.getPartition("p11"));
		assertEquals(null, parameter.getPartition("something"));
	}

	@Test
	public void getPartitionsTest() {
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		ChoiceNode partition1 = new ChoiceNode("partition1", "0"); 
		ChoiceNode partition2 = new ChoiceNode("partition2", "0"); 
		parameter.addPartition(partition1);
		parameter.addPartition(partition2);
		
		List<ChoiceNode> partitions = parameter.getPartitions();
		assertEquals(2, partitions.size());
		assertTrue(partitions.contains(partition1));
		assertTrue(partitions.contains(partition2));
	}
	
	@Test
	public void getChildrenTest() {
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		ChoiceNode partition1 = new ChoiceNode("partition1", "0"); 
		ChoiceNode partition2 = new ChoiceNode("partition2", "0"); 
		parameter.addPartition(partition1);
		parameter.addPartition(partition2);
		
		List<? extends GenericNode> children = parameter.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(partition1));
		assertTrue(children.contains(partition2));
	}

	@Test
	public void getPartitionNames() {
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		ChoiceNode partition1 = new ChoiceNode("partition1", "0"); 
		ChoiceNode partition2 = new ChoiceNode("partition2", "0"); 
		parameter.addPartition(partition1);
		parameter.addPartition(partition2);
		
		Set<String> partitionNames = parameter.getPartitionNames();
		assertTrue(partitionNames.contains("partition1"));
		assertTrue(partitionNames.contains("partition2"));
	}

	@Test
	public void getLeafPartitionNamesTest() {
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		ChoiceNode p1 = new ChoiceNode("p1", "0"); 
		ChoiceNode p11 = new ChoiceNode("p11", "0"); 
		ChoiceNode p12 = new ChoiceNode("p12", "0"); 
		ChoiceNode p2 = new ChoiceNode("p2", "0");
		p1.addPartition(p11);
		p1.addPartition(p12);
		parameter.addPartition(p1);
		parameter.addPartition(p2);
		
		Set<String> leafNames = parameter.getLeafPartitionNames();
		assertTrue(leafNames.contains("p1:p11"));
		assertTrue(leafNames.contains("p1:p12"));
		assertTrue(leafNames.contains("p2"));
		assertFalse(leafNames.contains("p1"));
	}

	@Test
	public void getAllPartitionNamesTest(){
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		ChoiceNode p1 = new ChoiceNode("p1", "0"); 
		ChoiceNode p11 = new ChoiceNode("p11", "0"); 
		ChoiceNode p12 = new ChoiceNode("p12", "0"); 
		ChoiceNode p2 = new ChoiceNode("p2", "0");
		p1.addPartition(p11);
		p1.addPartition(p12);
		parameter.addPartition(p1);
		parameter.addPartition(p2);
		
		Set<String> names = parameter.getAllPartitionNames();
		
		assertTrue(names.contains("p1"));
		assertTrue(names.contains("p1:p11"));
		assertTrue(names.contains("p1:p12"));
		assertTrue(names.contains("p2"));
	}
	
	
	@Test
	public void getMethodTest() {
		MethodNode method = new MethodNode("method");
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		method.addParameter(parameter);
		
		assertEquals(method, parameter.getMethod());
	}

	@Test
	public void getLeafPartitionsTest(){
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		
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
		
		parameter.addPartition(p1);
		parameter.addPartition(p2);
		parameter.addPartition(p3);
		p2.addPartition(p21);
		p2.addPartition(p22);
		p2.addPartition(p23);
		p3.addPartition(p31);
		p3.addPartition(p32);
		p3.addPartition(p33);
		p32.addPartition(p321);
		p32.addPartition(p322);
		p32.addPartition(p323);
		
		assertTrue(parameter.getLeafPartitions().contains(p1));
		assertTrue(parameter.getLeafPartitions().contains(p21));
		assertTrue(parameter.getLeafPartitions().contains(p22));
		assertTrue(parameter.getLeafPartitions().contains(p23));
		assertTrue(parameter.getLeafPartitions().contains(p31));
		assertTrue(parameter.getLeafPartitions().contains(p321));
		assertTrue(parameter.getLeafPartitions().contains(p322));
		assertTrue(parameter.getLeafPartitions().contains(p323));
		assertTrue(parameter.getLeafPartitions().contains(p33));
		
		assertFalse(parameter.getLeafPartitions().contains(p2));
		assertFalse(parameter.getLeafPartitions().contains(p3));
		assertFalse(parameter.getLeafPartitions().contains(p32));
	}
	
	@Test
	public void getAllLabelsTest(){
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		p1.addLabel("l11");
		p1.addLabel("l12");
		ChoiceNode p2 = new ChoiceNode("p2", "0");
		p2.addLabel("l21");
		p2.addLabel("l22");
		ChoiceNode p11 = new ChoiceNode("p11", "0");
		p11.addLabel("l111");
		p11.addLabel("l112");
		ChoiceNode p12 = new ChoiceNode("p12", "0");
		p12.addLabel("l121");
		p12.addLabel("l122");
		ChoiceNode p21 = new ChoiceNode("p21", "0");
		p21.addLabel("l211");
		p21.addLabel("l212");
		ChoiceNode p22 = new ChoiceNode("p22", "0");
		p22.addLabel("l221");
		p22.addLabel("l222");

		p1.addPartition(p11);
		p1.addPartition(p12);
		p2.addPartition(p21);
		p2.addPartition(p22);
		
		ParameterNode c = new ParameterNode("c", "type", "0", false);
		c.addPartition(p1);
		c.addPartition(p2);
		
		Set<String> labels = c.getLeafLabels();
		
		assertTrue(labels.contains("l11"));
		assertTrue(labels.contains("l12"));
		assertTrue(labels.contains("l21"));
		assertTrue(labels.contains("l21"));
		assertTrue(labels.contains("l111"));
		assertTrue(labels.contains("l112"));
		assertTrue(labels.contains("l121"));
		assertTrue(labels.contains("l121"));
		assertTrue(labels.contains("l211"));
		assertTrue(labels.contains("l211"));
		assertTrue(labels.contains("l221"));
		assertTrue(labels.contains("l221"));
		
	}
	
	/*****************compare()************************/
//	@Test
	public void compareSmokeTest(){
		for(String type : SUPPORTED_TYPES){
			for(Boolean expected : new Boolean[]{true, false}){
				ParameterNode c = new RandomModelGenerator().generateParameter(type, expected, 3, 3, 3);
				
				assertTrue(c.compare(c));
			}
		}
	}
	
	@Test
	public void compareNameTest(){
		ParameterNode c1 = new ParameterNode("c", "type", "0", true);
		ParameterNode c2 = new ParameterNode("c", "type", "0", true);
		
		assertTrue(c1.compare(c2));
		
		c1.setName("c1");
		assertFalse(c1.compare(c2));
		c2.setName("c1");
		assertTrue(c1.compare(c2));
	}
	
	@Test
	public void compareTypeTest(){
		ParameterNode c1 = new ParameterNode("c", "type", "0", true);
		ParameterNode c2 = new ParameterNode("c", "type", "0", true);
		
		assertTrue(c1.compare(c2));
		
		c1.setType("type1");
		assertFalse(c1.compare(c2));
		c2.setType("type1");
		assertTrue(c1.compare(c2));
	}
	
	@Test
	public void compareExpectedTest(){
		ParameterNode c1 = new ParameterNode("c", "type", "0", true);
		ParameterNode c2 = new ParameterNode("c", "type", "0", true);
		
		assertTrue(c1.compare(c2));

		c1.setExpected(false);
		assertFalse(c1.compare(c2));
		c2.setExpected(false);
		assertTrue(c1.compare(c2));
	}
	
	@Test
	public void compareDefaultValueTest(){
		ParameterNode c1 = new ParameterNode("c", "type", "0", true);
		ParameterNode c2 = new ParameterNode("c", "type", "0", true);
		
		assertTrue(c1.compare(c2));

		c1.setDefaultValueString("new default value");
		assertFalse(c1.compare(c2));
		c2.setDefaultValueString("new default value");
		assertTrue(c1.compare(c2));
	}
	
	@Test
	public void comparePartitionsTest(){
		ParameterNode c1 = new ParameterNode("c", "type", "0", true);
		ParameterNode c2 = new ParameterNode("c", "type", "0", true);
		
		assertTrue(c1.compare(c2));

		ChoiceNode p1 = new ChoiceNode("p", "value");
		ChoiceNode p2 = new ChoiceNode("p", "value");
		
		c1.addPartition(p1);
		assertFalse(c1.compare(c2));
		c2.addPartition(p2);
		assertTrue(c1.compare(c2));
		
		p1.setName("p1");
		assertFalse(c1.compare(c2));
		p2.setName("p1");
		assertTrue(c1.compare(c2));
	}
	
	
	@Test
	public void compareTest(){
		assertTrue(new ParameterNode("c", "int", "0", true).compare(new ParameterNode("c", "int", "0", true)));
		assertTrue(new ParameterNode("c", "int", "0", false).compare(new ParameterNode("c", "int", "0", false)));

		assertFalse(new ParameterNode("c1", "int", "0", false).compare(new ParameterNode("c", "int","0",  false)));
		assertFalse(new ParameterNode("c", "boolean", "0", false).compare(new ParameterNode("c", "int", "0", false)));
		assertFalse(new ParameterNode("c", "int", "0", true).compare(new ParameterNode("c", "int", "0", false)));

		ParameterNode c1 = new ParameterNode("c", "int", "0", false);
		ParameterNode c2 = new ParameterNode("c", "int", "0", false);
		assertTrue(c1.compare(c2));

		c1.setDefaultValueString("cc");
		assertFalse(c1.compare(c2));
		c2.setDefaultValueString("cc");
		assertTrue(c1.compare(c2));

		c1.addPartition(new ChoiceNode("p", "x"));
		c1.setDefaultValueString("cc");
		assertFalse(c1.compare(c2));
		c2.addPartition(new ChoiceNode("p1", "x"));
		assertFalse(c1.compare(c2));
		c2.getPartition("p1").setName("p");
		assertTrue(c1.compare(c2));
	}
}
