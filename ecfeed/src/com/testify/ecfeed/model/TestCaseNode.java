/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import java.util.List;

public class TestCaseNode extends GenericNode {
	List<PartitionNode> fTestData;
	
	public TestCaseNode(String name, List<PartitionNode> testData) {
		super(name);
		fTestData = testData;
	}

	public MethodNode getMethod() {
		return (MethodNode)getParent();
	}

	public List<PartitionNode> getTestData(){
		return fTestData;
	}
	
	public void replaceValue(int index, PartitionNode newValue) {
		fTestData.set(index, newValue);
	}

	public boolean mentions(PartitionNode partition) {
		for(PartitionNode p : fTestData){
			if(p == partition || p.isDescendant(partition)){
				return true;
			}
		}
		return false;
	}

	public String testDataString(){
		String result = new String();
		
		for(int i = 0; i < fTestData.size(); i++){
			PartitionNode partition = fTestData.get(i);
			if(partition.getCategory().isExpected()){
				result += "[e]" + partition.getValueString();
			}
			else{
				result += partition.getQualifiedName();
			}
			if(i < fTestData.size() - 1){
				result += ", ";
			}
		}
		return result;
	}
	
	public static boolean validateTestSuiteName(String newName) {
		if(newName.length() < 1 || newName.length() > 64) return false;
		if(newName.matches("[ ]+.*")) return false;
		return true;
	}

	public String toString(){
		String methodName = null;
		if (getParent() != null){
			methodName = getParent().getName();
		}
		String result = "[" + getName() + "]";
				
		if(methodName != null){
			result += ": " + methodName + "(";
			result += testDataString();
			result += ")";
		}
		
		return result;
	}
}
