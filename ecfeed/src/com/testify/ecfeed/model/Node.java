package com.testify.ecfeed.model;

import java.util.Vector;

public class Node {
	private String fName;
	private Vector<Node> fChildren;
	private Node fParent;

	public Node(String name){
		this.fName = name;
	}
	
	public String getName() {
		return fName;
	}

	public void setName(String name) {
		this.fName = name;
	}
	
	public Vector<Node> getChildren() {
		return fChildren;
	}
	
	public boolean hasChildren(){
		return (fChildren.size() > 0);
	}
	
	public Node getParent(){
		return fParent;
	}

	@Override
	public String toString(){
		return "Node " + getName();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		if(obj instanceof Node){
			boolean result = true;
			Node node = (Node) obj;
			result &= node.getName().equals(this.getName()); 
			return result;
		}
		else{
			return false;
		}
	}
}
