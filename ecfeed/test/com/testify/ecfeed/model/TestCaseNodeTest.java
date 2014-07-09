package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.testutils.RandomModelGenerator;

public class TestCaseNodeTest {

	@Test
	public void compare(){
		PartitionNode p1 = new PartitionNode("name", "value");
		PartitionNode p2 = new PartitionNode("name", "value");
		
		List<PartitionNode> td1 = new ArrayList<PartitionNode>();
		td1.add(p1);
		List<PartitionNode> td2 = new ArrayList<PartitionNode>();
		td2.add(p2);
		List<PartitionNode> td3 = new ArrayList<PartitionNode>();
		
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
	
	@Test
	public void compareSmokeTest(){
		for(int i = 0; i < 5; i++){
			RandomModelGenerator gen = new RandomModelGenerator();
			MethodNode m = gen.generateMethod(5, 0, 0);
			TestCaseNode t = gen.generateTestCase(m);

			assertTrue(t.compare(t));
		}
	}
}
