package com.testify.ecfeed.model;

import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class MethodNode extends GenericNode {
	private Vector<CategoryNode> fCategories;
	private Multimap<String, TestCaseNode> fTestCases;
	
	public MethodNode(String name){
		super(name);
		fCategories = new Vector<CategoryNode>();
		fTestCases = HashMultimap.create();
	}
	
	//TODO Unit tests 
	public void addCategory(CategoryNode category){
		fCategories.add(category);
		super.addChild(fCategories.size() - 1, category);
	}

	//TODO unit tests 
	public void addTestCase(TestCaseNode testCase){
		fTestCases.put(testCase.getName(), testCase);
		super.addChild(testCase);
	}
	
	public Vector<CategoryNode> getCategories(){
		return fCategories;
	}

	public Multimap<String, TestCaseNode> getTestCases(){
		return fTestCases;
	}

	//TODO unit tests
	public Set<String> getTestSuites(){
		return fTestCases.keySet();
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

	//TODO unit tests
	public boolean removeChild(TestCaseNode testCase){
		fTestCases.remove(testCase.getName(), testCase);
		return super.removeChild(testCase);
	}
	
	//TODO unit tests
	public boolean removeChild(CategoryNode category){
		fCategories.remove(category);
		return super.removeChild(category);
	}
	
	private Vector<String> getParameterTypes() {
		Vector<String> types = new Vector<String>();
		for(CategoryNode category : getCategories()){
			types.add(category.getType());
		}
		return types;
	}

	//TODO unit tests
	public Collection<TestCaseNode> getTestCases(String testSuite) {
		return fTestCases.get(testSuite);
	}

	//TODO unit tests
	public Collection<TestCaseNode> removeTestSuite(String oldName) {
		Collection<TestCaseNode> testCases = fTestCases.removeAll(oldName);
		super.removeChildren(testCases);
		return testCases;
	}
	
}
