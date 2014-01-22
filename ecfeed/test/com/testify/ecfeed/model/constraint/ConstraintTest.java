package com.testify.ecfeed.model.constraint;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class ConstraintTest {
	private MethodNode fMethod = new MethodNode("method");
	private CategoryNode fCategory = new CategoryNode("category", "type");

	private PartitionNode fP1 = new PartitionNode("p1", 0);
	private PartitionNode fP2 = new PartitionNode("p2", 0);
	private PartitionNode fP3 = new PartitionNode("p3", 0);

	private PartitionNode fP11 = new PartitionNode("p11", 0);
	private PartitionNode fP12 = new PartitionNode("p12", 0);
	private PartitionNode fP13 = new PartitionNode("p13", 0);

	private PartitionNode fP21 = new PartitionNode("p21", 0);
	private PartitionNode fP22 = new PartitionNode("p22", 0);
	private PartitionNode fP23 = new PartitionNode("p23", 0);

	private PartitionNode fP221 = new PartitionNode("p21", 0);
	private PartitionNode fP222 = new PartitionNode("p22", 0);
	private PartitionNode fP223 = new PartitionNode("p23", 0);

	private PartitionNode fP31 = new PartitionNode("p31", 0);
	private PartitionNode fP32 = new PartitionNode("p32", 0);
	private PartitionNode fP33 = new PartitionNode("p33", 0);

	@Before
	public void prepareStructure(){
		fP1.addPartition(fP11);
		fP1.addPartition(fP12);
		fP1.addPartition(fP13);

		fP2.addPartition(fP21);
		fP2.addPartition(fP22);
		fP2.addPartition(fP23);

		fP22.addPartition(fP221);
		fP22.addPartition(fP222);
		fP22.addPartition(fP223);

		fP3.addPartition(fP31);
		fP3.addPartition(fP32);
		fP3.addPartition(fP33);

		fCategory.addPartition(fP1);
		fCategory.addPartition(fP2);
		fCategory.addPartition(fP3);
		
		fMethod.addCategory(fCategory);
	}

	@Test
	public void equalsTest(){
		Statement statement = new Statement(fP22, Relation.EQUAL);
		
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP221})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP222})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP22})));

		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP2})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP1})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP3})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP13})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP33})));
	}

	@Test 
	public void notEqualsTest(){
		Statement statement = new Statement(fP22, Relation.NOT);
		
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP221})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP222})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP22})));

		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP2})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP1})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP3})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP13})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP33})));
	}
}
