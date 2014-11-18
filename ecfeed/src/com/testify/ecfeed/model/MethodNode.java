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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.generators.api.IConstraint;

public class MethodNode extends GenericNode {
	private List<ParameterNode> fParameters;
	private List<TestCaseNode> fTestCases;
	private List<ConstraintNode> fConstraints;

	public MethodNode(String name){
		super(name);
		fParameters = new ArrayList<ParameterNode>();
		fTestCases = new ArrayList<TestCaseNode>();
		fConstraints = new ArrayList<ConstraintNode>();
	}

	@Override
	public String toString(){
		String result = new String(getName()) + "(";
		List<String> types = getParametersShortTypes();
		List<String> names = getParametersNames();
		for(int i = 0; i < types.size(); i++){
			if(getParameters().get(i).isExpected()){
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

	@Override
	public List<? extends GenericNode> getChildren(){
		List<GenericNode> children = new ArrayList<GenericNode>();
		children.addAll(fParameters);
		children.addAll(fConstraints);
		children.addAll(fTestCases);

		return children;
	}

	@Override
	public boolean hasChildren(){
		return(fParameters.size() != 0 || fConstraints.size() != 0 || fTestCases.size() != 0);
	}

	@Override
	public MethodNode getCopy(){
		MethodNode copy = new MethodNode(this.getName());

		for(ParameterNode parameter : fParameters){
			copy.addParameter(parameter.getCopy());
		}

		for(TestCaseNode testcase : fTestCases){
			TestCaseNode tcase = testcase.getCopy(copy);
			if(tcase != null)
				copy.addTestCase(tcase);
		}

		for(ConstraintNode constraint : fConstraints){
			constraint = constraint.getCopy(copy);
			if(constraint != null)
				copy.addConstraint(constraint);
		}

		copy.setParent(getParent());
		return copy;
	}

	public void addParameter(ParameterNode parameter){
		addParameter(parameter, fParameters.size());
	}

	public void addParameter(ParameterNode parameter, int index) {
		fParameters.add(index, parameter);
		parameter.setParent(this);
	}

	public void addConstraint(ConstraintNode constraint) {
		addConstraint(constraint, fConstraints.size());
	}

	public void addConstraint(ConstraintNode constraint, int index) {
		constraint.setParent(this);
		fConstraints.add(index, constraint);
	}

	public void addTestCase(TestCaseNode testCase){
		addTestCase(testCase, fTestCases.size());
	}

	public void addTestCase(TestCaseNode testCase, int index){
		fTestCases.add(index, testCase);
		testCase.setParent(this);
	}

	public ClassNode getClassNode() {
		return (ClassNode)getParent();
	}

	public List<ParameterNode> getParameters(){
		return fParameters;
	}

	public ParameterNode getParameter(String parameterName) {
		for(ParameterNode parameter : fParameters){
			if(parameter.getName().equals(parameterName)){
				return parameter;
			}
		}
		return null;
	}

	public List<ParameterNode> getParameters(boolean expected) {
		ArrayList<ParameterNode> parameters = new ArrayList<>();
		for(ParameterNode parameter : fParameters){
			if(parameter.isExpected() == expected){
				parameters.add(parameter);
			}
		}
		return parameters;
	}

	public List<String> getParametersTypes() {
		List<String> types = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			types.add(parameter.getType());
		}
		return types;
	}

	public List<String> getParametersShortTypes() {
		List<String> types = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			types.add(parameter.getShortType());
		}
		return types;
	}

	public List<String> getParametersNames() {
		List<String> names = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			names.add(parameter.getName());
		}
		return names;
	}

	public ArrayList<String> getParametersNames(boolean expected) {
		ArrayList<String> names = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			if(parameter.isExpected() == expected){
				names.add(parameter.getName());
			}
		}
		return names;
	}

	public List<ConstraintNode> getConstraintNodes(){
		return fConstraints;
	}

	public List<IConstraint<PartitionNode>> getAllConstraints(){
		List<IConstraint<PartitionNode>> constraints = new ArrayList<IConstraint<PartitionNode>>();
		for(ConstraintNode node : fConstraints){
			constraints.add(node.getConstraint());
		}
		return constraints;
	}

	public List<IConstraint<PartitionNode>> getConstraints(String name) {
		List<IConstraint<PartitionNode>> constraints = new ArrayList<IConstraint<PartitionNode>>();
		for(ConstraintNode node : fConstraints){
			if(node.getName().equals(name)){
				constraints.add(node.getConstraint());
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

	public List<TestCaseNode> getTestCases(){
		return fTestCases;
	}

	public Collection<TestCaseNode> getTestCases(String testSuite) {
		ArrayList<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : getTestCases()){
			if(testSuite.equals(testCase.getName())){
				testCases.add(testCase);
			}
		}
		return testCases;
	}

	public Set<String> getTestSuites(){
		Set<String> testSuites = new HashSet<String>();
		for(TestCaseNode testCase : getTestCases()){
			testSuites.add(testCase.getName());
		}
		return testSuites;
	}

	public boolean removeParameter(ParameterNode parameter){
		parameter.setParent(null);
		return fParameters.remove(parameter);
	}

	public boolean removeTestCase(TestCaseNode testCase){
		testCase.setParent(null);
		return fTestCases.remove(testCase);
	}

	public void removeTestCases(){
		fTestCases.clear();
	}

	public boolean removeConstraint(ConstraintNode constraint) {
		constraint.setParent(null);
		return fConstraints.remove(constraint);
	}

	public void removeTestSuite(String suiteName) {
		Iterator<TestCaseNode> iterator = getTestCases().iterator();
		while(iterator.hasNext()){
			TestCaseNode testCase = iterator.next();
			if(testCase.getName().equals(suiteName)){
				iterator.remove();
			}
		}
	}

	public boolean isPartitionMentioned(PartitionNode partition){
		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(partition)){
				return true;
			}
		}
		for(TestCaseNode testCase: fTestCases){
			if(testCase.mentions(partition)){
				return true;
			}
		}
		return false;
	}

	public Set<ConstraintNode> mentioningConstraints(Collection<ParameterNode> parameters){
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(ParameterNode parameter : parameters){
			result.addAll(mentioningConstraints(parameter));
		}
		return result;
	}

	public Set<ConstraintNode> mentioningConstraints(ParameterNode parameter){
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(parameter)){
				result.add(constraint);
			}
		}
		return result;
	}

	public Set<ConstraintNode> mentioningConstraints(ParameterNode parameter, String label){
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(parameter, label)){
				result.add(constraint);
			}
		}
		return result;
	}

	public Set<ConstraintNode> mentioningConstraints(PartitionNode partition){
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(partition)){
				result.add(constraint);
			}
		}
		return result;
	}

	public List<TestCaseNode> mentioningTestCases(PartitionNode partition){
		List<TestCaseNode> result = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : fTestCases){
			if(testCase.getTestData().contains(partition)){
				result.add(testCase);
			}
		}
		return result;
	}

	public boolean isParameterMentioned(ParameterNode parameter){
		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(parameter)){
				return true;
			}
		}
		if(fTestCases.isEmpty()){
			return false;
		}
		return true;
	}

	public void clearTestCases(){
		fTestCases.clear();
	}

	public void replaceParameters(List<ParameterNode> parameters) {
		fParameters.clear();
		fParameters.addAll(parameters);
	}

	public void replaceTestCases(List<TestCaseNode> testCases){
		fTestCases.clear();
		fTestCases.addAll(testCases);
	}

	public void replaceConstraints(List<ConstraintNode> constraints){
		fConstraints.clear();
		fConstraints.addAll(constraints);
	}

	public void clearConstraints(){
		fConstraints.clear();
	}

	@Override
	public int getMaxChildIndex(GenericNode potentialChild){
		if(potentialChild instanceof ParameterNode) return getParameters().size();
		if(potentialChild instanceof ConstraintNode) return getConstraintNodes().size();
		if(potentialChild instanceof TestCaseNode) return getTestCases().size();
		return super.getMaxChildIndex(potentialChild);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	@Override
	public boolean compare(GenericNode node){
		if(node instanceof MethodNode == false){
			return false;
		}

		MethodNode comparedMethod = (MethodNode)node;

		int parametersCount = getParameters().size();
		int testCasesCount = getTestCases().size();
		int constraintsCount = getConstraintNodes().size();

		if(parametersCount != comparedMethod.getParameters().size() ||
				testCasesCount != comparedMethod.getTestCases().size() ||
				constraintsCount != comparedMethod.getConstraintNodes().size()){
			return false;
		}

		for(int i = 0; i < parametersCount; i++){
			if(getParameters().get(i).compare(comparedMethod.getParameters().get(i)) == false){
				return false;
			}
		}

		for(int i = 0; i < testCasesCount; i++){
			if(getTestCases().get(i).compare(comparedMethod.getTestCases().get(i)) == false){
				return false;
			}
		}

		for(int i = 0; i < constraintsCount; i++){
			if(getConstraintNodes().get(i).compare(comparedMethod.getConstraintNodes().get(i)) == false){
				return false;
			}
		}

		return super.compare(node);
	}
}
