package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.databene.feed4junit.Feeder;

@RunWith(Feeder.class)
public class NodeTest extends GenericNode {

	public NodeTest() {
		super("dummy");
	}

	@Test
	public void test(String newName) {
		GenericNode node = new GenericNode("node");
		
		assertEquals("node", node.getName());
		
		node.setName(newName);
		assertEquals(newName, node.getName());
	}

}
