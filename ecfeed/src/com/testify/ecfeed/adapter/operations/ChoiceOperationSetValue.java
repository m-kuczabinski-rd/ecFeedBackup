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
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IParameterVisitor;
import com.testify.ecfeed.model.MethodParameterNode;

public class ChoiceOperationSetValue extends AbstractModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private ChoiceNode fTarget;

	private ITypeAdapterProvider fAdapterProvider;

	private class ParameterAdapter implements IParameterVisitor{

		@Override
		public Object visit(MethodParameterNode parameter) throws Exception {
			fOriginalDefaultValue = parameter.getDefaultValue();
			if(parameter != null && JavaUtils.isUserType(parameter.getType())){
				if(parameter.getLeafChoiceValues().contains(parameter.getDefaultValue()) == false){
					parameter.setDefaultValueString(fNewValue);
				}
			}
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

	}

	private class ReverseOperation extends AbstractModelOperation{

		private class ReverseParameterAdapter implements IParameterVisitor{

			@Override
			public Object visit(MethodParameterNode parameter) throws Exception {
				parameter.setDefaultValueString(fOriginalDefaultValue);
				return null;
			}

			@Override
			public Object visit(GlobalParameterNode parameter) throws Exception {
				return null;
			}

		}

		public ReverseOperation() {
			super(ChoiceOperationSetValue.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.setValueString(fOriginalValue);
			adaptParameter(fTarget.getParameter());
			markModelUpdated();
		}

		private void adaptParameter(AbstractParameterNode parameter) {
			try{
				parameter.accept(new ReverseParameterAdapter());
			}catch(Exception e){}
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ChoiceOperationSetValue(fTarget, fNewValue, fAdapterProvider);
		}
	}

	public ChoiceOperationSetValue(ChoiceNode target, String newValue, ITypeAdapterProvider adapterProvider){
		super(OperationNames.SET_PARTITION_VALUE);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = fTarget.getValueString();
		fAdapterProvider = adapterProvider;
	}

	@Override
	public void execute() throws ModelOperationException {
		String convertedValue = validateChoiceValue(fTarget.getParameter().getType(), fNewValue);
		if(convertedValue == null){
			throw new ModelOperationException(Messages.PARTITION_VALUE_PROBLEM(fNewValue));
		}
		fTarget.setValueString(convertedValue);
		adaptParameter(fTarget.getParameter());
		markModelUpdated();
	}

	private void adaptParameter(AbstractParameterNode parameter) {
		try{
			parameter.accept(new ParameterAdapter());
		}catch(Exception e){}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

	@Override
	public String toString(){
		return "setValue[" + fTarget + "](" + fNewValue + ")";
	}

	private String validateChoiceValue(String type, String value) {
		if (value.length() > Constants.MAX_PARTITION_VALUE_STRING_LENGTH) return null;

		return fAdapterProvider.getAdapter(type).convert(value);
	}
}
