package com.testify.ecfeed.model;

import java.util.Vector;

public class ClassNode extends GenericNode {
	private Vector<MethodNode> fMethods;

	public ClassNode(String qualifiedName) {
		super(qualifiedName);
		fMethods = new Vector<MethodNode>();
	}

	public String getQualifiedName() {
		return super.getName();
	}
	
	public String getLocalName(){
		return getLocalName(getName());
	}

	//TODO unit tests
	public void addMethod(MethodNode method) {
		super.addChild(method);
		fMethods.add(method);
	}
	
	//TODO unit tests
	public boolean removeChild(MethodNode method){
		fMethods.remove(method);
		return super.removeChild(method);
	}
	
	private String getLocalName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}
	
	public String toString(){
		return getLocalName();
	}

	@Override 
	public boolean equals(Object obj){
		if(obj instanceof ClassNode != true){
			return false;
		}
		return super.equals((ClassNode)obj);
	}

	//TODO unit tests
	public MethodNode getMethod(String name, Vector<String> argTypes) {
		for(MethodNode methodNode : getMethods()){
			Vector<String> args = new Vector<String>();
			for(CategoryNode arg : methodNode.getCategories()){
				args.add(arg.getType());
			}
			if(methodNode.getName().equals(name) && args.equals(argTypes)){
				return methodNode;
			}
		}
		return null;
	}

	//TODO unit tests
	private Vector<MethodNode> getMethods() {
		return fMethods;
	}
}
