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

import com.testify.ecfeed.model.PartitionNode;

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
	
	@Test
	public void testEquals(){
		PartitionNode booleanPartition = new PartitionNode("boolean", true);
		PartitionNode booleanPartitionCopy = new PartitionNode("boolean", true);
		assertTrue(booleanPartition.equals(booleanPartitionCopy));
		booleanPartitionCopy.setValue(false);
		assertFalse(booleanPartition.equals(booleanPartitionCopy));

		PartitionNode bytePartition     = new PartitionNode("byte", (byte)0);
		PartitionNode bytePartitionCopy = new PartitionNode("byte", (byte)0);
		assertTrue(bytePartition.equals(bytePartitionCopy));
		bytePartitionCopy.setValue((byte)1);
		assertFalse(bytePartition.equals(bytePartitionCopy));

		PartitionNode charPartition     = new PartitionNode("char", 'a');
		PartitionNode charPartitionCopy = new PartitionNode("char", 'a');
		assertTrue(charPartition.equals(charPartitionCopy));
		charPartitionCopy.setValue('b');
		assertFalse(charPartition.equals(charPartitionCopy));

		PartitionNode intPartition     = new PartitionNode("int", (int)0);
		PartitionNode intPartitionCopy = new PartitionNode("int", (int)0);
		assertTrue(intPartition.equals(intPartitionCopy));
		intPartitionCopy.setValue((long)0);
		assertFalse(intPartition.equals(intPartitionCopy));
		intPartitionCopy.setValue((int)1);
		assertFalse(intPartition.equals(intPartitionCopy));
		

		PartitionNode longPartition     = new PartitionNode("long", (long)0);
		PartitionNode longPartitionCopy = new PartitionNode("long", (long)0);
		assertTrue(longPartition.equals(longPartitionCopy));
		longPartitionCopy.setValue((long)1);
		assertFalse(longPartition.equals(longPartitionCopy));

		PartitionNode shortPartition     = new PartitionNode("short", (short)0);
		PartitionNode shortPartitionCopy = new PartitionNode("short", (short)0);
		assertTrue(shortPartition.equals(shortPartitionCopy));
		shortPartitionCopy.setValue((short)1);
		assertFalse(shortPartition.equals(shortPartitionCopy));

		PartitionNode floatPartition     = new PartitionNode("float", (float)0);
		PartitionNode floatPartitionCopy = new PartitionNode("float", (float)0);
		assertTrue(floatPartition.equals(floatPartitionCopy));
		floatPartitionCopy.setValue((float)0.1);
		assertFalse(floatPartition.equals(floatPartitionCopy));

		PartitionNode doublePartition     = new PartitionNode("double", (double)0);
		PartitionNode doublePartitionCopy = new PartitionNode("double", (double)0);
		assertTrue(doublePartition.equals(doublePartitionCopy));
		doublePartitionCopy.setValue((double)1.0);
		assertFalse(doublePartition.equals(doublePartitionCopy));

		PartitionNode stringPartition     = new PartitionNode("string", "string");
		PartitionNode stringPartitionCopy = new PartitionNode("string", "string");
		assertTrue(stringPartition.equals(stringPartitionCopy));
		stringPartitionCopy.setValue("new string");
		assertFalse(stringPartition.equals(stringPartitionCopy));

		PartitionNode nullStringPartition     = new PartitionNode("string", null);
		PartitionNode nullStringPartitionCopy = new PartitionNode("string", null);
		assertTrue(nullStringPartition.equals(nullStringPartitionCopy));

		PartitionNode emptyStringPartition     = new PartitionNode("emptyString", "");
		PartitionNode emptyStringPartitionCopy = new PartitionNode("emptyString", "");
		assertTrue(emptyStringPartition.equals(emptyStringPartitionCopy));
	}

}
