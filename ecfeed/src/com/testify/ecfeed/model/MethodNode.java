package com.testify.ecfeed.model;

import java.util.Collection;
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
		super.addChild(fCategories.size() - 1, category);
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
		for(TestCaseNode testCase : fTestCases){
			testSuites.add(testCase.getName());
		}
		return testSuites;
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
		fTestCases.remove(testCase);
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
		Vector<TestCaseNode> testCases = new Vector<TestCaseNode>();
		for(TestCaseNode testCase : fTestCases){
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
	
}
