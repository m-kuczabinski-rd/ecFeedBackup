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
}
