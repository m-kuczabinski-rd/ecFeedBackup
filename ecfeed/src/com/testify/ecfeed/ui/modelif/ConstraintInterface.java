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

package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.ConstraintOperationReplaceStatement;
import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;

public class ConstraintInterface extends AbstractNodeInterface {

	public ConstraintInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
	}

	@Override
	protected ConstraintNode getTarget(){
		return (ConstraintNode)super.getTarget();
	}

	public boolean replaceStatement(AbstractStatement current, AbstractStatement newStatement) {
		if(current != newStatement){
			IModelOperation operation = new ConstraintOperationReplaceStatement(getTarget(), current, newStatement);
			return execute(operation, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean goToImplementationEnabled(){
		return false;
	}
}
