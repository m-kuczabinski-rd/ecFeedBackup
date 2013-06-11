package com.testify.ecfeed.model;

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
	
	public void addCategory(CategoryNode category){
		if(!isParent(category)){
			fCategories.add(category);
			category.setParent(this);
		}
	}
	
	public void addTestCase(TestCaseNode testCase){
		if(!isParent(testCase)){
			fTestCases.add(testCase);
			testCase.setParent(this);
		}
	}
	
	public Vector<CategoryNode> getCategories(){
		return fCategories;
	}
	
	public Vector<TestCaseNode> getTestCases(){
		return fTestCases;
	}
	
	public Set<String> getTestSuiteNames(){
		Set<String> testSuiteNames = new HashSet<String>();
		for(TestCaseNode testCase : fTestCases){
			testSuiteNames.add(testCase.getName());
		}
		return testSuiteNames;
	}
	
	@Override
	public Vector<GenericNode> getChildren(){
		Vector<GenericNode> children = new Vector<GenericNode>();
		children.addAll(fCategories);
		children.addAll(fTestCases);
		return children;
	}

	@Override
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

	@Override 
	public boolean equals(Object obj){
		if(obj instanceof MethodNode != true){
			return false;
		}
		return super.equals((MethodNode)obj);
	}

	@Override
	public boolean removeChild(GenericNode child) {
		boolean result = fTestCases.remove(child) || fCategories.remove(child) ;
		if(result){
			child.setParent(null);
		}
		return result;
	}
	
	private Vector<String> getParameterTypes() {
		Vector<String> types = new Vector<String>();
		for(CategoryNode category : getCategories()){
			types.add(category.getType());
		}
		return types;
	}
	
}
