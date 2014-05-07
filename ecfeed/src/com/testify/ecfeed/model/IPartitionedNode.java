package com.testify.ecfeed.model;

import java.util.List;

public interface IPartitionedNode extends IGenericNode{
	public AbstractCategoryNode getCategory();
	public List<PartitionNode> getPartitions();
	public void addPartition(PartitionNode partition);
	public PartitionNode getPartition(String name);
	public boolean removePartition(PartitionNode partition);
	public boolean removePartition(String name);
	public List<PartitionNode> getLeafPartitions();
	public List<String> getAllPartitionNames();
	public void partitionRemoved(PartitionNode partition);
}
