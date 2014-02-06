package com.testify.ecfeed.model.constraint;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class StatementArrayTest {

	private static MethodNode fMethod;
	private static CategoryNode fCategory1;
	private static PartitionNode fPartition11;
	private static PartitionNode fPartition12;
	private static PartitionNode fPartition13;
	private static CategoryNode fCategory2;
	private static PartitionNode fPartition21;
	private static PartitionNode fPartition22;
	private static PartitionNode fPartition23;

	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method");
		fCategory1 = new CategoryNode("category", "type");
		fPartition11 = new PartitionNode("partition11", null);
		fPartition12 = new PartitionNode("partition12", null);
		fPartition13 = new PartitionNode("partition13", null);
		fCategory1.addPartition(fPartition11);
		fCategory1.addPartition(fPartition12);
		fCategory1.addPartition(fPartition13);
		fCategory2 = new CategoryNode("category", "type");
		fPartition21 = new PartitionNode("partition21", null);
		fPartition22 = new PartitionNode("partition22", null);
		fPartition23 = new PartitionNode("partition23", null);
		fCategory2.addPartition(fPartition21);
		fCategory2.addPartition(fPartition22);
		fCategory2.addPartition(fPartition23);
		fMethod.addCategory(fCategory1);
		fMethod.addCategory(fCategory2);
	}
	

	@Test
	public void testEvaluate() {
		StatementArray arrayOr = new StatementArray(Operator.OR);
		StatementArray arrayAnd = new StatementArray(Operator.AND);
		PartitionStatement statement1 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition11);
		PartitionStatement statement2 = new PartitionStatement(fCategory2, Relation.EQUAL, fPartition21);
		arrayOr.addStatement(statement1);
		arrayOr.addStatement(statement2);
		arrayAnd.addStatement(statement1);
		arrayAnd.addStatement(statement2);
		
		List<PartitionNode> bothFulfill = new ArrayList<PartitionNode>();
		bothFulfill.add(fPartition11);
		bothFulfill.add(fPartition21);
		assertTrue(arrayOr.evaluate(bothFulfill));
		assertTrue(arrayAnd.evaluate(bothFulfill));

		List<PartitionNode> oneFulfills = new ArrayList<PartitionNode>();
		oneFulfills.add(fPartition12);
		oneFulfills.add(fPartition21);
		assertTrue(arrayOr.evaluate(oneFulfills));
		assertFalse(arrayAnd.evaluate(oneFulfills));

		List<PartitionNode> noneFulfills = new ArrayList<PartitionNode>();
		noneFulfills.add(fPartition12);
		noneFulfills.add(fPartition22);
		assertFalse(arrayOr.evaluate(noneFulfills));
		assertFalse(arrayAnd.evaluate(noneFulfills));
	}

	@Test
	public void testGetChildren() {
		StatementArray array = new StatementArray(Operator.OR);
		PartitionStatement statement1 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition11);
		PartitionStatement statement2 = new PartitionStatement(fCategory2, Relation.EQUAL, fPartition21);
		PartitionStatement statement3 = new PartitionStatement(fCategory2, Relation.EQUAL, fPartition21);
		array.addStatement(statement1);
		array.addStatement(statement2);
		
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		assertFalse(array.getChildren().contains(statement3));
	}

	@Test
	public void testMentionsPartitionNode() {
		StatementArray array = new StatementArray(Operator.OR);
		PartitionStatement statement1 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition11);
		PartitionStatement statement2 = new PartitionStatement(fCategory2, Relation.EQUAL, fPartition21);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertTrue(array.mentions(fPartition11));
		assertFalse(array.mentions(fPartition13));
	}

	@Test
	public void testMentionsCategoryNode() {
		StatementArray array = new StatementArray(Operator.OR);
		PartitionStatement statement1 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition11);
		array.addStatement(statement1);
		assertTrue(array.mentions(fPartition11.getCategory()));
		assertFalse(array.mentions(fPartition21.getCategory()));
	}

	@Test
	public void testSetOperator() {
		StatementArray array = new StatementArray(Operator.OR);
		PartitionStatement statement1 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition11);
		PartitionStatement statement2 = new PartitionStatement(fCategory2, Relation.EQUAL, fPartition21);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(Operator.OR, array.getOperator());
		array.setOperator(Operator.AND);
		assertEquals(Operator.AND, array.getOperator());
		//check that children statements were not changed
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
	}

	@Test
	public void testReplaceChild() {
		StatementArray array = new StatementArray(Operator.OR);
		PartitionStatement statement1 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition11);
		PartitionStatement statement2 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition12);
		PartitionStatement statement3 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition13);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		assertFalse(array.getChildren().contains(statement3));
		
		array.replaceChild(statement2, statement3);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertFalse(array.getChildren().contains(statement2));
		assertTrue(array.getChildren().contains(statement3));
	}

	@Test
	public void testRemoveChild() {
		StatementArray array = new StatementArray(Operator.OR);
		PartitionStatement statement1 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition11);
		PartitionStatement statement2 = new PartitionStatement(fCategory1, Relation.EQUAL, fPartition12);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		
		array.removeChild(statement2);
		assertEquals(1, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertFalse(array.getChildren().contains(statement2));
	}

}
