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
import com.testify.ecfeed.adapter.java.Messages;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ParametersParentNode;

public class GenericOperationAddParameter extends AbstractModelOperation {

	private ParametersParentNode fTarget;
	private AbstractParameterNode fParameter;
	private int fNewIndex;

	protected class ReverseOperation extends AbstractModelOperation{

		private int fOriginalIndex;
		private AbstractParameterNode fReversedParameter;
		private ParametersParentNode fReversedTarget;

		public ReverseOperation(ParametersParentNode target, AbstractParameterNode parameter) {
			super("reverse " + OperationNames.ADD_PARAMETER);
			fReversedTarget = target;
			fReversedParameter = parameter;
		}

		@Override
		public void execute() throws ModelOperationException {
			fOriginalIndex = fReversedParameter.getIndex();
			fReversedTarget.removeParameter(fReversedParameter);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GenericOperationAddParameter(fReversedTarget, fReversedParameter, fOriginalIndex);
		}
	}

	public GenericOperationAddParameter(ParametersParentNode target, AbstractParameterNode parameter, int index) {
		super(OperationNames.ADD_PARAMETER);
		fTarget = target;
		fParameter = parameter;
		fNewIndex = (index == -1)? target.getParameters().size() : index;
	}

	public GenericOperationAddParameter(ParametersParentNode target, AbstractParameterNode parameter) {
		this(target, parameter, -1);
	}


	@Override
	public void execute() throws ModelOperationException {
		String parameterName = fParameter.getName();
		if(fNewIndex < 0){
			ModelOperationException.report(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fTarget.getParameters().size()){
			ModelOperationException.report(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		if(fTarget.getParameter(parameterName) != null){
			ModelOperationException.report(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}
		fTarget.addParameter(fParameter, fNewIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation(fTarget, fParameter);
	}

}
