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
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof TestCaseNode == false) return false;
		TestCaseNode testCase = (TestCaseNode)obj;
		if (getTestData().equals(testCase.getTestData()) == false) return false;
		return super.equals(obj);
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
