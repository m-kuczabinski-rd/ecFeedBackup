/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.adapter.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.adapter.java.JavaUtils;

public class ParameterOperationSetExpected extends AbstractModelOperation {
	
	private MethodParameterNode fTarget;
	private boolean fExpected;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	private List<ChoiceNode> fOriginalChoices;
	private String fOriginalDefaultValue;
	
	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(ParameterOperationSetExpected.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.setExpected(!fExpected);
			if(fTarget.getMethod() != null){
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
			}
			fTarget.replaceChoices(fOriginalChoices);
			fTarget.setDefaultValueString(fOriginalDefaultValue);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ParameterOperationSetExpected(fTarget, fExpected);
		}
		
	}
	
	public ParameterOperationSetExpected(MethodParameterNode target, boolean expected){
		super(OperationNames.SET_EXPECTED_STATUS);
		fTarget = target;
		fExpected = expected;
		
		MethodNode method = target.getMethod(); 
		if(method != null){
			fOriginalTestCases = new ArrayList<TestCaseNode>();
			fOriginalTestCases.addAll(method.getTestCases());
			fOriginalConstraints = new ArrayList<ConstraintNode>();
			fOriginalConstraints.addAll(method.getConstraintNodes());
		}
		fOriginalChoices = new ArrayList<ChoiceNode>();
		fOriginalChoices.addAll(fTarget.getChoices());
		fOriginalDefaultValue = fTarget.getDefaultValue();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setExpected(fExpected);
		String type = fTarget.getType();
		if(fExpected && JavaUtils.hasLimitedValuesSet(type)){
			boolean validDefaultValue = false;
			String currentDefaultValue = fTarget.getDefaultValue();
			for(ChoiceNode leaf : fTarget.getLeafChoices()){
				if(currentDefaultValue.equals(leaf.getValueString())){
					validDefaultValue = true;
					break;
				}
			}
			if(validDefaultValue == false){
				if(fTarget.getLeafChoices().size() > 0){
					fTarget.setDefaultValueString(fTarget.getLeafChoices().toArray(new ChoiceNode[]{})[0].getValueString());
				}
				else{
					fTarget.addChoice(new ChoiceNode("choice", currentDefaultValue));
				}
			}
		}
		
		MethodNode method = fTarget.getMethod(); 
		if(method != null){
			int index = fTarget.getIndex();
			ListIterator<TestCaseNode> tcIt = method.getTestCases().listIterator();
			while(tcIt.hasNext()){
				TestCaseNode testCase = tcIt.next();
				if(fExpected){
					ChoiceNode p = new ChoiceNode("expected", fTarget.getDefaultValue());
					p.setParent(fTarget);
					TestCaseNode newTestCase = testCase.getCopy();
					newTestCase.setParent(method);
					newTestCase.getTestData().set(index, p.getCopy());
					tcIt.set(newTestCase);
				}
				else{
					tcIt.remove();
				}
			}
			Iterator<ConstraintNode> cIt = method.getConstraintNodes().iterator();
			while(cIt.hasNext()){
				if(cIt.next().mentions(fTarget)){
					cIt.remove();
				}
			}
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}
	
	protected MethodParameterNode getTarget(){
		return fTarget;
	}
	
	protected boolean getExpected(){
		return fExpected;
	}
	
}
