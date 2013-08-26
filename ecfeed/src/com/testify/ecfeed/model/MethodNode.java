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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class MethodNode extends GenericNode {
	private Vector<CategoryNode> fCategories;
	private Vector<TestCaseNode> fTestCases;
	
	public MethodNode(String name){
		super(name);
		fCategories = new Vector<CategoryNode>();
		fTestCases = new Vector<TestCaseNode>();
	}
	
	//TODO Unit tests 
	public void addCategory(CategoryNode category){
		fCategories.add(category);
		super.addChild(category);
	}

	//TODO unit tests 
	public void addTestCase(TestCaseNode testCase){
		fTestCases.add(testCase);
		super.addChild(testCase);
	}
	
	public Vector<CategoryNode> getCategories(){
		return fCategories;
	}

	public Vector<TestCaseNode> getTestCases(){
		return fTestCases;
	}
	
	//TODO unit tests
	public Set<String> getTestSuites(){
		Set<String> testSuites = new HashSet<String>();
		for(TestCaseNode testCase : getTestCases()){
			testSuites.add(testCase.getName());
		}
		return testSuites;
	}
	
	@Override
	public String toString(){
		String result = new String(getName()) + "(";
		Vector<String> types = getParameterTypes();
		Vector<String> names = getParameterNames();
		for(int i = 0; i < types.size(); i++){
			result += types.elementAt(i);
			result += " ";
			result += names.elementAt(i);
			if(i < types.size() - 1) result += ", ";
		}
		result += ")";
		return result;
	}

	@Override 
	public boolean equals(Object obj){
		if(obj instanceof MethodNode != true){
			return false;
		}
		return super.equals((MethodNode)obj);
	}

	@Override
	public Vector<GenericNode> getChildren(){
		Vector<GenericNode> children = new Vector<GenericNode>();
		children.addAll(fCategories);
		children.addAll(fTestCases);
		return children;
	}
	
	//TODO unit tests
	public boolean removeChild(TestCaseNode testCase){
		fTestCases.remove(testCase);
		return super.removeChild(testCase);
	}
	
	//TODO unit tests
	public boolean removeChild(CategoryNode category){
		fCategories.remove(category);
		return super.removeChild(category);
	}

	public Vector<String> getParameterTypes() {
		Vector<String> types = new Vector<String>();
		for(CategoryNode category : getCategories()){
			types.add(category.getType());
		}
		return types;
	}

	public Vector<String> getParameterNames() {
		Vector<String> types = new Vector<String>();
		for(CategoryNode category : getCategories()){
			types.add(category.getName());
		}
		return types;
	}

	//TODO unit tests
	public Collection<TestCaseNode> getTestCases(String testSuite) {
		Vector<TestCaseNode> testCases = new Vector<TestCaseNode>();
		for(TestCaseNode testCase : getTestCases()){
			if(testSuite.equals(testCase.getName())){
				testCases.add(testCase);
			}
		}
		return testCases;
	}

	//TODO unit tests
	public Collection<TestCaseNode> removeTestSuite(String suiteName) {
		Collection<TestCaseNode> testCases = getTestCases(suiteName);
		fTestCases.removeAll(testCases);
		super.removeChildren(testCases);
		return testCases;
	}	
	
	//TODO unit tests
	@SuppressWarnings("rawtypes")
	public void moveChild(GenericNode child, boolean moveUp){
		Vector childrenArray = null;
		if(child instanceof CategoryNode){
			childrenArray = fCategories;
		}
		if(child instanceof TestCaseNode){
			childrenArray = fTestCases;
		}
		if(childrenArray == null){
			return;
		}
		
		int childIndex = childrenArray.indexOf(child);
		if(moveUp && childIndex > 0){
			Collections.swap(childrenArray, childIndex, childIndex - 1);
		}
		if(!moveUp && childIndex < childrenArray.size() - 1){
			Collections.swap(childrenArray, childIndex, childIndex + 1);
		}
	}
}
