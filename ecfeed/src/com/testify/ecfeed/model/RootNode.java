package com.testify.ecfeed.model;

import java.util.Vector;

public class RootNode extends GenericNode {

	public RootNode(String name) {
		super(name);
	}
	
	public void addClass(ClassNode node){
		addChild(node);
	}

	public Vector<ClassNode> getClasses() {
		Vector<ClassNode> classes = new Vector<ClassNode>();
		for(GenericNode child : getChildren()){
			if(child instanceof ClassNode){
				classes.add((ClassNode)child);
			}
		}
		return classes;
	}

	@Override 
	public boolean equals(Object obj){
		if(obj instanceof RootNode != true){
			return false;
		}
		return super.equals((RootNode)obj);
	}

	//TODO unit tests
	public ClassNode getClassModel(String name) {
		for(ClassNode childClass : getClasses()){
			if(childClass.getQualifiedName().equals(name)){
				return childClass;
			}
		}
		return null;
	}
}
