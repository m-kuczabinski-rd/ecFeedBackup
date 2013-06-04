package com.testify.ecfeed.model;

import java.util.Vector;

public class MethodNode extends GenericNode {
	public MethodNode(String name){
		super(name);
	}
	
	public void addCategory(CategoryNode category){
		super.addChild(category);
	}
	
	public String toString(){
		String result = new String(getName()) + "(";
		Vector<String> types = getParameterTypes();
		for(int i = 0; i < types.size(); i++){
			result += types.elementAt(i);
			if(i < types.size() - 1) result += ", ";
		}
		result += ")";
		return result;
	}

	private Vector<String> getParameterTypes() {
		Vector<String> types = new Vector<String>();
		for(GenericNode child : getChildren()){
			if (child instanceof CategoryNode){
				types.add(((CategoryNode)child).getType());
			}
		}
		return types;
	}
	
	@Override 
	public boolean equals(Object obj){
		if(obj instanceof MethodNode != true){
			return false;
		}
		return super.equals((MethodNode)obj);
	}

}
