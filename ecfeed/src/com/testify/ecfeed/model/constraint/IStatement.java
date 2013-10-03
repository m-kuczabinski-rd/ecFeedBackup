package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.model.PartitionNode;

public interface IStatement{
	public boolean evaluate(List<PartitionNode> values);
}