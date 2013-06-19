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
	
	//TODO unit tests
	protected void addChild(GenericNode child){
		addChild(fChildren.size(), child);
	}
	
	//Should not be used explicitly
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
		if(getChildren() != null){
			return (getChildren().size() > 0);
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
		if(obj instanceof GenericNode == false){
			return false;
		}
		GenericNode node = (GenericNode)obj;
		if (!fName.equals(node.getName())){
			return false;
		}
		int childrenCount = getChildren().size();
		int nodeChildrenCount = node.getChildren().size();
		if(childrenCount != nodeChildrenCount){
			return false;
		}
		
		Vector<GenericNode> nodeChildren = node.getChildren();
		for(int i = 0; i < childrenCount; i++){
			GenericNode thisChild = getChildren().elementAt(i);
			GenericNode nodeChild = nodeChildren.elementAt(i);
			if(!thisChild.equals(nodeChild)){
				return false;
			}
		}
		return true;
	}

	public boolean removeChild(GenericNode child) {
		boolean result = getChildren().remove(child);
		if(result){
			child.setParent(null);
		}
		return result;
	}
	
	protected boolean isParent(GenericNode potentialChild){
		for(GenericNode child : getChildren()){
			if(child == potentialChild){
				return true;
			}
		}
		return false;
	}

	//TODO unit tests
	public GenericNode getChild(String name) {
		for(GenericNode child : getChildren()){
			if (name.equals(child.getName())){
				return child;
			}
		}
		return null;
	}

	//TODO Unit tests
	public void addChild(int index, GenericNode child) {
		if(!isParent(child)){
			fChildren.add(index, child);
			child.setParent(this);
		}
	}
}
