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

package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.model.AbstractNode;

public class GenericSetCommentsOperation extends AbstractModelOperation {

	private String fComments;
	private AbstractNode fTarget;
	private String fCurrentComments;

	public GenericSetCommentsOperation(AbstractNode target, String comments) {
		super(OperationNames.SET_COMMENTS);
		fTarget = target;
		fComments = comments;
	}

	@Override
	public void execute() throws ModelOperationException {
		fCurrentComments = fTarget.getDescription() != null ? fTarget.getDescription() : "";
		fTarget.setDescription(fComments);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericSetCommentsOperation(fTarget, fCurrentComments);
	}

}
