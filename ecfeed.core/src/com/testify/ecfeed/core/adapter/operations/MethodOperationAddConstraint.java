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

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.adapter.java.Constants;
import com.testify.ecfeed.core.adapter.java.Messages;

public class MethodOperationAddConstraint extends AbstractModelOperation {

	private MethodNode fTarget;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationAddConstraint(MethodNode target, ConstraintNode constraint, int index){
		super(OperationNames.ADD_CONSTRAINT);
		fTarget = target;
		fConstraint = constraint;
		fIndex = index;
	}

	public MethodOperationAddConstraint(MethodNode target, ConstraintNode constraint){
		this(target, constraint, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fIndex == -1){
			fIndex = fTarget.getConstraintNodes().size();
		}
		if(fConstraint.getName().matches(Constants.REGEX_CONSTRAINT_NODE_NAME) == false){
			ModelOperationException.report(Messages.CONSTRAINT_NAME_REGEX_PROBLEM);
		}
		if(fConstraint.updateReferences(fTarget) == false){
			ModelOperationException.report(Messages.INCOMPATIBLE_CONSTRAINT_PROBLEM);
		}
		fTarget.addConstraint(fConstraint, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveConstraint(fTarget, fConstraint);
	}

}
