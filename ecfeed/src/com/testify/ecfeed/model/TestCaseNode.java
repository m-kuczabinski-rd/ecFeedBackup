package com.testify.ecfeed.model;

import java.util.Vector;

public class TestCaseNode extends GenericNode {
	Vector<PartitionNode> fTestData;
	
	public TestCaseNode(String name, Vector<PartitionNode> testData) {
		super(name);
		fTestData = testData;
	}

	public Vector<PartitionNode> getTestData(){
		return fTestData;
	}
	
	//TODO unit tests
	public String toString(){
		String methodName = null;
		if (getParent() != null){
			methodName = getParent().getName();
		}
		String result = "[" + getName() + "]";
				
		if(methodName != null){
			result += ": " + methodName + "(";
			for(int i = 0; i < fTestData.size(); i++){
				result += fTestData.elementAt(i).getName();
				if(i < fTestData.size() - 1){
					result += ", ";
				}
			}
			result += ")";
		}
		
		return result;
	}
}
