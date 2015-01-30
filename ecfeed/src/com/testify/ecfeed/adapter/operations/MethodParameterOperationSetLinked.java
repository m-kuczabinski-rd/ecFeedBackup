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

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodParameterOperationSetLinked extends BulkOperation{

	private class SetLinkedOperation extends AbstractModelOperation {

		private MethodParameterNode fTarget;
		private boolean fLinked;
		private List<TestCaseNode> fOriginalTestCases;

		private class ReverseSetLinkedOperation extends AbstractReverseOperation{

			public ReverseSetLinkedOperation() {
				super(SetLinkedOperation.this);
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
				fTarget.setLinked(!fLinked);
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetLinkedOperation(fTarget, fLinked);
			}

		}

		public SetLinkedOperation(MethodParameterNode target, boolean linked) {
			super(OperationNames.SET_LINKED);
			fTarget = target;
			fLinked = linked;
		}

		@Override
		public void execute() throws ModelOperationException {
			MethodNode method = fTarget.getMethod();
			String newType;
			if(fLinked){
				if(fTarget.getLink() == null){
					throw new ModelOperationException(Messages.LINK_NOT_SET_PROBLEM);
				}
				newType = fTarget.getLink().getType();
			}
			else{
				newType = fTarget.getRealType();
			}

			if(method.checkDuplicate(fTarget.getIndex(), newType)){
				throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
			}
//			if(fLinked){
//				GlobalParameterNode link = fTarget.getLink();
//				if(link == null || method.checkDuplicate(fTarget.getIndex(), link.getType())){
//					GlobalParameterNode newLink = setNewLink();
//					if(newLink == null){
//						throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
//					}
//					fTarget.setLink(newLink);
//				}
//			}
//
//
//				//check if the link is still part of the model
//				GlobalParametersParentNode parent = link.getParametersParent();
//				if(link == null || parent == null || parent.getParameters().contains(link) == false){
//					GlobalParameterNode newLink = null;
//					for(GlobalParameterNode newLinkCandidate : fTarget.getMethod().getAvailableGlobalParameters()){
//						if(checkSignatureConflict(method, fTarget.getIndex(), newLinkCandidate.getType()) == false){
//							newLink = newLinkCandidate;
//							break;
//						}
//					}
//					System.out.println("Dupa");
//				}
//				newType = link.getType();
//			}else{
//				newType = fTarget.getType();
//			}
//
//			List<String> types = method.getParametersTypes();
//			types.set(fTarget.getIndex(), newType);
//			if(method.getClassNode().getMethod(method.getName(), types) != null && method.getClassNode().getMethod(method.getName(), types) != method){
//				throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
//			}
			fTarget.setLinked(fLinked);
			fOriginalTestCases = new ArrayList<>(method.getTestCases());
			method.removeTestCases();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ReverseSetLinkedOperation();
		}
	}

	public MethodParameterOperationSetLinked(MethodParameterNode target, boolean linked) {
		super(OperationNames.SET_LINKED, true);
		addOperation(new SetLinkedOperation(target, linked));
		addOperation(new MethodOperationMakeConsistent(target.getMethod()));
	}

	public void addOperation(int index, IModelOperation operation){
		operations().add(index, operation);
	}

}
