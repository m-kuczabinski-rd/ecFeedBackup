package com.testify.ecfeed.model.constraint;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.PartitionNode;

public class StaticStatementTest {

	@Test
	public void testEvaluate() {
		List<PartitionNode> list = new ArrayList<PartitionNode>();
		
		StaticStatement trueStatement = new StaticStatement(true);
		assertTrue(trueStatement.evaluate(list));
		StaticStatement falseStatement = new StaticStatement(false);
		assertFalse(falseStatement.evaluate(list));
	}

	@Test
	public void testSetValue() {
		List<PartitionNode> list = new ArrayList<PartitionNode>();
		
		StaticStatement statement = new StaticStatement(true);
		assertTrue(statement.evaluate(list));
		
		statement.setValue(false);
		assertFalse(statement.evaluate(list));
	}

}
