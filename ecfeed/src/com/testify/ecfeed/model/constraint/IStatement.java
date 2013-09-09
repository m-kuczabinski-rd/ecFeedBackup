package com.testify.ecfeed.model.constraint;

import java.util.Vector;

import com.testify.ecfeed.model.PartitionNode;

public interface IStatement{
	public boolean evaluate(Vector<PartitionNode> values);
}