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

import java.util.ArrayList;
import java.util.List;


public class TestCaseNode extends GenericNode {
	List<PartitionNode> fTestData;
	
	@Override
	public int getIndex(){
		if(getMethod() == null){
			return -1;
		}
		return getMethod().getTestCases().indexOf(this);
	}

	@Override
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
	
	@Override
	public TestCaseNode getCopy(){
		List<PartitionNode> testdata = new ArrayList<>();
		for(PartitionNode partition : fTestData){
			testdata.add(partition);
		}
		return new TestCaseNode(this.getName(), testdata);
	}
	
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
			if(p.is(partition)){
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

	public TestCaseNode getCopy(MethodNode method){
		TestCaseNode tcase = getCopy();
		if(tcase.updateReferences(method))
			return tcase;
		else
			return null;
	}

	public boolean updateReferences(MethodNode method){
		List<CategoryNode> categories = method.getCategories();
		if(categories.size() != getTestData().size())
			return false;

		for(int i = 0; i < categories.size(); i++){
			CategoryNode category = categories.get(i);
			if(category.isExpected()){

			} else{
				PartitionNode original = getTestData().get(i);
				PartitionNode newReference = category.getPartition(original.getQualifiedName());
				if(newReference == null){
					return false;
				}
				getTestData().set(i, newReference);
			}
		}
		return true;
	}
	
	@Override
	public boolean compare(GenericNode node){
		if(node instanceof TestCaseNode == false){
			return false;
		}
		
		TestCaseNode compared = (TestCaseNode)node;
		
		if(getTestData().size() != compared.getTestData().size()){
			return false;
		}
		
		for(int i = 0; i < getTestData().size(); i++){
			if(getTestData().get(i).compare(compared.getTestData().get(i)) == false){
				return false;
			}
		}
		
		return super.compare(node);
	}
	
	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	public boolean isConsistent() {
		for(PartitionNode p : getTestData()){
			CategoryNode category = p.getCategory();
			if(category == null || (category.isExpected() == false && category.getPartition(p.getQualifiedName()) == null)){
				return false;
			}
		}
		return true;
	}
	
	@Override 
	public int getMaxIndex(){
		if(getMethod() != null){
			return getMethod().getTestCases().size();
		}
		return -1;
	}

}
