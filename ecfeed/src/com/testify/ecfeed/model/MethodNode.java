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
import java.util.ArrayList;

import com.testify.ecfeed.api.IConstraint;

public class MethodNode extends GenericNode {
	private ArrayList<CategoryNode> fCategories;
	private ArrayList<TestCaseNode> fTestCases;
	private ArrayList<ConstraintNode> fConstraints;
	
	public MethodNode(String name){
		super(name);
		fCategories = new ArrayList<CategoryNode>();
		fTestCases = new ArrayList<TestCaseNode>();
		fConstraints = new ArrayList<ConstraintNode>();
	}
	
	//TODO Unit tests 
	public void addCategory(CategoryNode category){
		fCategories.add(category);
		category.setParent(this);
	}

	//TODO Unit tests 
	public void addCategory(ExpectedValueCategoryNode category){
		addCategory((CategoryNode)category);
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
	
	public ArrayList<CategoryNode> getCategories(){
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

	public ArrayList<String> getCategoriesTypes() {
		ArrayList<String> types = new ArrayList<String>();
		for(CategoryNode category : getCategories()){
			types.add(category.getType());
		}
		return types;
	}

	public ArrayList<String> getCategoriesNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(CategoryNode category : getCategories()){
			names.add(category.getName());
		}
		return names;
	}

	public ArrayList<String> getExpectedCategoriesNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(CategoryNode category : getCategories()){
			if(category.isExpected()){
				names.add(category.getName());
			}
		}
		return names;
	}

	public ArrayList<String> getOrdinaryCategoriesNames() {
		ArrayList<String> allNames = getCategoriesNames();
		ArrayList<String> expectedNames = getExpectedCategoriesNames();
		allNames.removeAll(expectedNames);
		return allNames;
	}

	public ArrayList<ConstraintNode> getConstraints(){
		return fConstraints;
	}

	public ArrayList<IConstraint> getConstraints(String name) {
		ArrayList<IConstraint> constraints = new ArrayList<IConstraint>();
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

	public ArrayList<TestCaseNode> getTestCases(){
		return fTestCases;
	}
	
	//TODO unit tests
	public Collection<TestCaseNode> getTestCases(String testSuite) {
		ArrayList<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
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
	public ArrayList<? extends IGenericNode> getChildren(){
		ArrayList<IGenericNode> children = new ArrayList<IGenericNode>();
		children.addAll(fCategories);
		children.addAll(fConstraints);
		children.addAll(fTestCases);
		
		return children;
	}
	
	@Override
	public boolean hasChildren(){
		return(fCategories.size() != 0 || fConstraints.size() != 0 || fTestCases.size() != 0);
	}
	
	//TODO unit tests
	@SuppressWarnings("rawtypes")
	@Override
	public void moveChild(IGenericNode child, boolean moveUp){
		ArrayList childrenArray = null;
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
	public boolean removeChild(ExpectedValueCategoryNode category){
		return removeChild((CategoryNode)category);
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

	public void replaceCategory(int index, CategoryNode newCategory){
		newCategory.setParent(this);
		CategoryNode originalCategory = fCategories.get(index);
		fCategories.set(index, newCategory);

		if(newCategory instanceof ExpectedValueCategoryNode){
			if (!originalCategory.isExpected()){
				ExpectedValueCategoryNode expectedCategory = (ExpectedValueCategoryNode)newCategory;
				for(TestCaseNode testCase : fTestCases){
					testCase.replaceValue(index, expectedCategory.getDefaultValuePartition().getCopy());
				}
				Iterator<ConstraintNode> iterator = fConstraints.iterator();
				while(iterator.hasNext()){
					if(iterator.next().mentions(originalCategory)){
						iterator.remove();
					}
				}
			}
		}
		else{
			if(originalCategory.isExpected()){
				fTestCases.clear();
			}
		}
	}

	@Override
	public String toString(){
		String result = new String(getName()) + "(";
		ArrayList<String> types = getCategoriesTypes();
		ArrayList<String> names = getCategoriesNames();
		for(int i = 0; i < types.size(); i++){
			if(getCategories().get(i).isExpected()){
				result += "[e]";
			}
			result += types.get(i);
			result += " ";
			result += names.get(i);
			if(i < types.size() - 1) result += ", ";
		}
		result += ")";
		return result;
	}
}
