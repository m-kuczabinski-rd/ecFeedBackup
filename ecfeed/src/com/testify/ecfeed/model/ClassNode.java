package com.testify.ecfeed.model;

public class ClassNode extends GenericNode {

	public ClassNode(String qualifiedName) {
		super(qualifiedName);
	}

	public String getQualifiedName() {
		return super.getName();
	}
	
	public String getLocalName(){
		return getLocalName(getName());
	}

	public void addMethod(MethodNode method) {
		super.addChild(method);
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
}
