package com.testify.ecfeed.model;

public class MethodNode extends GenericNode {
	public MethodNode(String name){
		super(name);
	}
	
	public void addCategory(CategoryNode category){
		super.addChild(category);
	}
}
