/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import java.util.ArrayList;

public class TestCaseNode extends GenericNode {
	ArrayList<PartitionNode> fTestData;
	
	public TestCaseNode(String name, ArrayList<PartitionNode> testData) {
		super(name);
		fTestData = testData;
	}

	public ArrayList<PartitionNode> getTestData(){
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
			result += testDataString();
			result += ")";
		}
		
		return result;
	}
	
	public String testDataString(){
		String result = new String();
		
		for(int i = 0; i < fTestData.size(); i++){
			result += fTestData.get(i).getName();
			if(i < fTestData.size() - 1){
				result += ", ";
			}
		}
		return result;
	}
	
	public boolean mentions(PartitionNode partition) {
		for(PartitionNode p : fTestData){
			if(p == partition){
				return true;
			}
		}
		return false;
	}
}
