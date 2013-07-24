package com.testify.ecfeed.model;

import java.util.Vector;

public class CategoryNode extends GenericNode {
	
	private final String fType;	
	
	public CategoryNode(String name, String type) {
		super(name);
		fType = type;
	}

	public String getType() {
		return fType;
	}

	//TODO unit tests
	public void addPartition(PartitionNode partition) {
		if(getPartition(partition.getName()) == null){
			super.addChild(partition);
		}
	}
	
	//TODO unit tests
	public PartitionNode getPartition(String name){
		return (PartitionNode) super.getChild(name);
	}

	public String toString(){
		return new String(getName() + ": " + getType());
	}

	public boolean removeChild(PartitionNode partition) {
		return super.removeChild(partition);
	}

	@Override 
	public boolean equals(Object obj){
		if(obj instanceof CategoryNode != true){
			return false;
		}
		CategoryNode category = (CategoryNode)obj;
		if(!fType.equals(category.getType())){
			return false;
		}
		return super.equals(category);
	}

	//TODO unit tests
	public Vector<PartitionNode> getPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		for(GenericNode child : getChildren()){
			if(child instanceof PartitionNode){
				partitions.add((PartitionNode)child);
			}
		}
		return partitions;
	}
}
