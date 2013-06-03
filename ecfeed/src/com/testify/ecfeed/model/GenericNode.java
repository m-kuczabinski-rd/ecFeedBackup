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
		if(!isParent(child)){
			fChildren.add(child);
			child.setParent(this);
		}
	}
	
	//Should not be used 
	void setParent(GenericNode newParent) {
		fParent = newParent;
		if(newParent != null){
			newParent.addChild(this);
		}
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
		boolean result = fChildren.remove(child);
		if(result){
			child.setParent(null);
		}
		return result;
	}
	
	private boolean isParent(GenericNode potentialChild){
		for(GenericNode child : getChildren()){
			if(child == potentialChild){
				return true;
			}
		}
		return false;
	}
}
