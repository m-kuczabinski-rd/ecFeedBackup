package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PartitionedCategoryNode extends AbstractCategoryNode{
	protected final List<PartitionNode> fPartitions;

	public PartitionedCategoryNode(String name, String type) {
		super(name, type);
		fPartitions = new ArrayList<PartitionNode>();
	}

	public void addPartition(PartitionNode partition) {
		fPartitions.add(partition);
		partition.setParent(this);
	}
	
	@Override
	public PartitionedCategoryNode getCategory() {
		return this;
	}

	public PartitionNode getPartition(String qualifiedName){
		return (PartitionNode)getChild(qualifiedName);
	}
	
	public List<PartitionNode> getPartitions() {
		return fPartitions;
	}


	public List<PartitionNode> getLeafPartitions(){
		List<PartitionNode> leafs = new ArrayList<PartitionNode>();
		for(PartitionNode child : fPartitions){
			leafs.addAll(child.getLeafPartitions());
		}
		return leafs;
	}
	
	public List<String> getAllPartitionNames(){
		List<String> names = new ArrayList<String>();
		for(PartitionNode child : getPartitions()){
			names.add(child.getQualifiedName());
			names.addAll(child.getAllPartitionNames());
		}
		return names;
	}

	public boolean removePartition(PartitionNode partition){
		if(fPartitions.contains(partition) && fPartitions.remove(partition)){
			MethodNode parentMethod = getMethod();
			if(parentMethod != null){
				parentMethod.partitionRemoved(partition);
			}
		}
		return false;
	}

	@Override
	public boolean removePartition(String qualifiedName) {
		return removePartition(getPartition(qualifiedName));
	}

	public List<? extends IGenericNode> getChildren(){
		return fPartitions;
	}

	public List<String> getPartitionNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(PartitionNode partition : getPartitions()){
			names.add(partition.getName());
		}
		return names;
	}

	public List<String> getLeafPartitionNames(){
		List<PartitionNode> leafPartitions = getLeafPartitions();
		List<String> names = new ArrayList<String>();
		for(PartitionNode leaf : leafPartitions){
			names.add(leaf.getQualifiedName());
		}
		return names;
	}
	
	public Set<String> getAllPartitionLabels(){
		Set<String> labels = new LinkedHashSet<String>();
		for(PartitionNode p : getPartitions()){
			labels.addAll(p.getAllDescendingLabels());
		}
		return labels;
	}
}
