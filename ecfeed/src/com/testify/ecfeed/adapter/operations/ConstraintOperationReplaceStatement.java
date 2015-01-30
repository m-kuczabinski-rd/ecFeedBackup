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

package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;

public class ConstraintOperationReplaceStatement extends AbstractModelOperation{

	private AbstractStatement fNewStatement;
	private AbstractStatement fCurrentStatement;
	private ConstraintNode fTarget;

	public ConstraintOperationReplaceStatement(ConstraintNode target, AbstractStatement current, AbstractStatement newStatement) {
		super(OperationNames.REPLACE_STATEMENT);
		fTarget = target;
		fCurrentStatement = current;
		fNewStatement = newStatement;
	}

	@Override
	public void execute() throws ModelOperationException {
		Constraint constraint = fTarget.getConstraint();
		if(constraint.getPremise() == fCurrentStatement){
			constraint.setPremise(fNewStatement);
		}
		else if(constraint.getConsequence() == fCurrentStatement){
			constraint.setConsequence(fNewStatement);
		}
		else{
			throw new ModelOperationException(Messages.TARGET_STATEMENT_NOT_FOUND_PROBLEM);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationReplaceStatement(fTarget, fNewStatement, fCurrentStatement);
	}

}
