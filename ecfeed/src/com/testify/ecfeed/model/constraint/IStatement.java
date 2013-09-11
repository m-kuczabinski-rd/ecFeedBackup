package com.testify.ecfeed.model.constraint;

import java.util.ArrayList;

import com.testify.ecfeed.model.PartitionNode;

public interface IStatement{
	public boolean evaluate(ArrayList<PartitionNode> values);
}