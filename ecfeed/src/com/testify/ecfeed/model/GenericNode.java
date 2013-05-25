package com.testify.ecfeed.model;

import java.util.Vector;

public class GenericNode {
	private String fName;
	private Vector<GenericNode> fChildren;
	private GenericNode fParent;

	public GenericNode(String name){
		this.fName = name;
		this.fChildren = new Vector<GenericNode>();
	}
	
	public String getName() {
		return fName;
	}

	public void setName(String name) {
		this.fName = name;
	}
	
	protected void addChild(GenericNode child){
		child.setParent(this);
		fChildren.add(child);
	}
	
	//Available only inside the package 
	void setParent(GenericNode node) {
		fParent = node;
	}

	public Vector<GenericNode> getChildren() {
		return fChildren;
	}
	
	public boolean hasChildren(){
		if(fChildren != null){
			return (fChildren.size() > 0);
		}
		return false;
	}
	
	public GenericNode getParent(){
		return fParent;
	}

	public int getChildIndex(GenericNode child){
		for(int i = 0; i < fChildren.size(); i++){
			if(fChildren.elementAt(i).equals(child)){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
	@Override
	public boolean equals(Object obj){
		return super.equals(obj);
	}
}
