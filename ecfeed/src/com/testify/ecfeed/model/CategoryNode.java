package com.testify.ecfeed.model;

public class CategoryNode extends GenericNode {
	
	private final String fTypeSignature;	
	
	public CategoryNode(String name, String typeSignature) {
		super(name);
		fTypeSignature = typeSignature;
	}

	public String getTypeSignature() {
		return fTypeSignature;
	}

	public void addPartition(PartitionNode partition) {
		super.addChild(partition);
	}

}
