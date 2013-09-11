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
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.testify.ecfeed.api.IConstraint;

public class MethodNode extends GenericNode {
	private Vector<CategoryNode> fCategories;
	private Vector<TestCaseNode> fTestCases;
	private Vector<ConstraintNode> fConstraints;
	
	public MethodNode(String name){
		super(name);
		fCategories = new Vector<CategoryNode>();
		fTestCases = new Vector<TestCaseNode>();
		fConstraints = new Vector<ConstraintNode>();
	}
	
	//TODO Unit tests 
	public void addCategory(CategoryNode category){
		fCategories.add(category);
		category.setParent(this);
	}

	//TODO unit tests 
	public void addConstraint(ConstraintNode constraint) {
		fConstraints.add(constraint);
		constraint.setParent(this);
	}
	
	//TODO unit tests 
	public void addTestCase(TestCaseNode testCase){
		fTestCases.add(testCase);
		testCase.setParent(this);
	}
	
	public Vector<CategoryNode> getCategories(){
		return fCategories;
	}

	public CategoryNode getCategory(String categoryName) {
		for(CategoryNode category : getCategories()){
			if(category.getName().equals(categoryName)){
				return category;
			}
		}
		return null;
	}

	public Vector<String> getCategoriesTypes() {
		Vector<String> types = new Vector<String>();
		for(CategoryNode category : getCategories()){
			types.add(category.getType());
		}
		return types;
	}

	public Vector<String> getCategoriesNames() {
		Vector<String> names = new Vector<String>();
		for(CategoryNode category : getCategories()){
			names.add(category.getName());
		}
		return names;
	}

	public Vector<ConstraintNode> getConstraints(){
		return fConstraints;
	}

	public Vector<IConstraint> getConstraints(String name) {
		Vector<IConstraint> constraints = new Vector<IConstraint>();
		for(ConstraintNode node : fConstraints){
			if(node.getName().equals(name)){
				constraints.add(node);
			}
		}
		return constraints;
	}

	public Set<String> getConstraintsNames() {
		Set<String> names = new HashSet<String>();
		for(ConstraintNode constraint : fConstraints){
			names.add(constraint.getName());
		}
		return names;
	}

	public Vector<TestCaseNode> getTestCases(){
		return fTestCases;
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
	public Set<String> getTestSuites(){
		Set<String> testSuites = new HashSet<String>();
		for(TestCaseNode testCase : getTestCases()){
			testSuites.add(testCase.getName());
		}
		return testSuites;
	}

	@Override
	public Vector<? extends IGenericNode> getChildren(){
		Vector<IGenericNode> children = new Vector<IGenericNode>();
		children.addAll(fCategories);
		children.addAll(fConstraints);
		children.addAll(fTestCases);
		
		return children;
	}
	
	@Override
	public boolean hasChildren(){
		return(fCategories.size() != 0 || fConstraints.size() != 0 || fTestCases.size() != 0);
	}
	
	@Override
	public String toString(){
		String result = new String(getName()) + "(";
		Vector<String> types = getCategoriesTypes();
		Vector<String> names = getCategoriesNames();
		for(int i = 0; i < types.size(); i++){
			result += types.elementAt(i);
			result += " ";
			result += names.elementAt(i);
			if(i < types.size() - 1) result += ", ";
		}
		result += ")";
		return result;
	}

	//TODO unit tests
	public boolean removeChild(TestCaseNode testCase){
		testCase.setParent(null);
		return fTestCases.remove(testCase);
	}
	
	//TODO unit tests
	public boolean removeChild(CategoryNode category){
		category.setParent(null);
		return fCategories.remove(category);
	}
	
	//TODO unit tests
	public boolean removeChild(ConstraintNode constraint){
		constraint.setParent(null);
		return fConstraints.remove(constraint);
	}

	public void removeConstraint(ConstraintNode constraint) {
		fConstraints.remove(constraint);
	}

	//TODO unit tests
	public void removeTestSuite(String suiteName) {
		Iterator<TestCaseNode> iterator = getTestCases().iterator();
		while(iterator.hasNext()){
			TestCaseNode testCase = iterator.next();
			if(testCase.getName().equals(suiteName)){
				iterator.remove();
			}
		}
	}

	//TODO unit tests
	@SuppressWarnings("rawtypes")
	@Override
	public void moveChild(IGenericNode child, boolean moveUp){
		Vector childrenArray = null;
		if(child instanceof CategoryNode){
			childrenArray = fCategories;
		}
		if(child instanceof ConstraintNode){
			childrenArray = fConstraints;
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
