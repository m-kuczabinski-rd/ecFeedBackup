package com.testify.ecfeed.model;

public class Node {
	private String fName;

	public Node(String name){
		this.fName = name;
	}
	
	public String getName() {
		return fName;
	}

	public void setName(String name) {
		this.fName = name;
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
