package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PartitionNodeTest extends PartitionNode {

	public PartitionNodeTest() {
		super("test", null);
	}

	@Test
	public void testValue() {
		PartitionNode partition = new PartitionNode("test partition", null);
		assertEquals(null, partition.getValue());
		
		partition.setValue((int)0);
		assertEquals((int)0, partition.getValue());

		partition.setValue((int)Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, partition.getValue());

		partition.setValue((int)Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, partition.getValue());
	}

}
