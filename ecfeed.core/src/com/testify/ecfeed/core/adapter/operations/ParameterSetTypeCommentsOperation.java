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
import com.ecfeed.core.model.AbstractParameterNode;

public class ParameterSetTypeCommentsOperation extends AbstractModelOperation {

	private String fComments;
	private AbstractParameterNode fTarget;
	private String fCurrentComments;

	public ParameterSetTypeCommentsOperation(AbstractParameterNode target, String comments) {
		super(OperationNames.SET_COMMENTS);
		fTarget = target;
		fComments = comments;
	}

	@Override
	public void execute() throws ModelOperationException {
		fCurrentComments = fTarget.getTypeComments() != null ? fTarget.getTypeComments() : "";
		fTarget.setTypeComments(fComments);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ParameterSetTypeCommentsOperation(fTarget, fCurrentComments);
	}

}
