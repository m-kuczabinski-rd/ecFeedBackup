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
	
	public GenericNode getRoot(){
		if(getParent() == null){
			return this;
		}
		return getParent().getRoot();
	}

	@Override
	public String toString(){
		return getName();
	}
	
	@Override
	public boolean equals(Object obj){
		return super.equals(obj);
	}

	public boolean removeChild(GenericNode child) {
		return fChildren.remove(child); 
	}
}
