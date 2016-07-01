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

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ModelOperationException;

public class MethodOperationMakeConsistent extends AbstractModelOperation {

	private MethodNode fTarget;
	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(OperationNames.MAKE_CONSISTENT);
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.replaceTestCases(fOriginalTestCases);
			fTarget.replaceConstraints(fOriginalConstraints);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationMakeConsistent(fTarget);
		}

	}

	public MethodOperationMakeConsistent(MethodNode target){
		super(OperationNames.MAKE_CONSISTENT);
		fTarget = target;
		fOriginalConstraints = new ArrayList<ConstraintNode>(target.getConstraintNodes());
		fOriginalTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
	}

	@Override
	public void execute() throws ModelOperationException {
		boolean modelUpdated = false;
		Iterator<TestCaseNode> tcIt = fTarget.getTestCases().iterator();
		while(tcIt.hasNext()){
			if(tcIt.next().isConsistent() == false){
				tcIt.remove();
				modelUpdated = true;
			}
		}
		Iterator<ConstraintNode> cIt = fTarget.getConstraintNodes().iterator();
		while(cIt.hasNext()){
			if(cIt.next().isConsistent() == false){
				cIt.remove();
				modelUpdated = true;
			}
		}
		if(modelUpdated){
			markModelUpdated();
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

}
