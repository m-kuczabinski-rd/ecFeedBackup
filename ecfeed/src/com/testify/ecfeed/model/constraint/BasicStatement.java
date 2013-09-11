package com.testify.ecfeed.model.constraint;

import java.util.ArrayList;

import com.testify.ecfeed.model.PartitionNode;

public class BasicStatement implements IStatement {
	BasicStatement fParent = null;
	private static int fLastId = 0;
	private final int fId;
	
	public BasicStatement(){
		fId = fLastId++;
	}
	
	@Override
	public boolean evaluate(ArrayList<PartitionNode> values) {
		return false;
	}

	public int getId(){
		return fId;
	}
	
	public BasicStatement getParent() {
		return fParent;
	}

	void setParent(BasicStatement parent) {
		fParent = parent;
	}

	public ArrayList<BasicStatement> getChildren(){
		return null;
	}

	public void replaceChild(BasicStatement oldStatement, 
			BasicStatement newStatement) {
		ArrayList<BasicStatement> children = getChildren();
		if(children != null){
			int index = children.indexOf(oldStatement);
			if(index != -1){
				newStatement.setParent(this);
				children.set(index, newStatement);
			}
		}
	}

	public void removeChild(BasicStatement child) {
		ArrayList<BasicStatement> children = getChildren();
		if(children != null){
			children.remove(child);
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof BasicStatement == false){
			return false;
		}
		return fId == ((BasicStatement)obj).getId();
	}

	public boolean mentions(PartitionNode partition) {
		return false;
	}
}
