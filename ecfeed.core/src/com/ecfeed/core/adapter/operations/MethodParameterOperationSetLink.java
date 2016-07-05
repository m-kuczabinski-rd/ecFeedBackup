/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

public class MethodParameterOperationSetLink extends BulkOperation {

	private class SetLinkOperation extends AbstractModelOperation{
		private MethodParameterNode fTarget;
		private GlobalParameterNode fNewLink;
		private GlobalParameterNode fCurrentLink;

		private class ReverseOperation extends AbstractReverseOperation{

			public ReverseOperation() {
				super(MethodParameterOperationSetLink.this);
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.setLink(fCurrentLink);
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetLinkOperation(fTarget, fNewLink);
			}
		}

		public SetLinkOperation(MethodParameterNode target, GlobalParameterNode link) {
			super(OperationNames.SET_LINK);
			fTarget = target;
			fNewLink = link;
		}

		@Override
		public void execute() throws ModelOperationException {
			MethodNode method = fTarget.getMethod();
			List<String> types = method.getParametersTypes();
			types.set(fTarget.getIndex(), fNewLink.getType());
			if(method.checkDuplicate(fTarget.getIndex(), fNewLink.getType())){
				ModelOperationException.report(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
			}

			fCurrentLink = fTarget.getLink();
			fTarget.setLink(fNewLink);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public MethodParameterOperationSetLink(MethodParameterNode target, GlobalParameterNode link) {
		super(OperationNames.SET_LINK, true);
		addOperation(new SetLinkOperation(target, link));
		addOperation(new MethodOperationMakeConsistent(target.getMethod()));
	}
}
