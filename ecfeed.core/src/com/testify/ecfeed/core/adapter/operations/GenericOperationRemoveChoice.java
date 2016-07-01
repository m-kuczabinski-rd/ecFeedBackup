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

import java.util.Set;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.adapter.java.Messages;

public class GenericOperationRemoveChoice extends BulkOperation {

	private class RemoveChoiceOperation extends AbstractModelOperation{

		private ChoicesParentNode fTarget;
		private ChoiceNode fChoice;
		private String fOriginalDefaultValue;
		private int fOriginalIndex;
		private ITypeAdapterProvider fAdapterProvider;

		private class ReverseOperation extends AbstractModelOperation{

			private class ReverseParameterAdapter implements IParameterVisitor{

				@Override
				public Object visit(MethodParameterNode node) throws Exception {
					node.setDefaultValueString(fOriginalDefaultValue);
					return null;
				}

				@Override
				public Object visit(GlobalParameterNode node) throws Exception {
					return null;
				}

			}

			public ReverseOperation() {
				super(RemoveChoiceOperation.this.getName());
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.addChoice(fChoice, fOriginalIndex);
				reverseAdaptParameter();
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new RemoveChoiceOperation(fTarget, fChoice, fAdapterProvider);
			}

			private void reverseAdaptParameter() {
				try{
					fTarget.getParameter().accept(new ReverseParameterAdapter());
				}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
			}

		}

		private class OperationValidator implements IParameterVisitor{

			@Override
			public Object visit(MethodParameterNode parameter) throws Exception {
				if(parameter.isExpected() && JavaUtils.isPrimitive(parameter.getType()) == false && parameter.getChoices().size() == 1 && parameter.getChoices().get(0) == fChoice){
					// We are removing the only choice of expected parameter.
					// The last parameter must represent the default expected value
					ModelOperationException.report(Messages.EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM);
				}
				return null;
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				return null;
			}
		}

		private class ParameterAdapter implements IParameterVisitor{

			@Override
			public Object visit(MethodParameterNode parameter) throws Exception {
				fOriginalDefaultValue = parameter.getDefaultValue();
				if(parameter.isExpected() && fChoice.getValueString().equals(parameter.getDefaultValue())){
					// the value of removed choice is the same as default expected value
					// Check if there are leaf choices with the same value. If not, update the default value
					Set<String> leafValues = parameter.getLeafChoiceValues();
					if(leafValues.contains(parameter.getDefaultValue()) == false){
						if(leafValues.size() > 0){
							parameter.setDefaultValueString(leafValues.toArray(new String[]{})[0]);
						}
						else{
							ModelOperationException.report(Messages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
						}
					}
				}
				return null;
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				return null;
			}

		}

		public RemoveChoiceOperation(ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider){
			super(OperationNames.REMOVE_PARTITION);
			fAdapterProvider = adapterProvider;
			fTarget = target;
			fChoice = choice;
			fOriginalIndex = fChoice.getIndex();
		}

		@Override
		public void execute() throws ModelOperationException {
			fOriginalIndex = fChoice.getIndex();
			validateOperation();
			fTarget.removeChoice(fChoice);
			adaptParameter();
			if(fChoice.getParent() instanceof ChoiceNode){
				adaptParentChoice((ChoiceNode)fChoice.getParent());
			}
		}

		private void adaptParentChoice(ChoiceNode parent) {
			if(parent.isAbstract() == false){
				ITypeAdapter adapter = fAdapterProvider.getAdapter(parent.getParameter().getType());
				String newValue = adapter.convert(parent.getValueString());
				if(newValue == null){
					newValue = adapter.defaultValue();
				}
				parent.setValueString(newValue);
			}
		}

		private void adaptParameter() {
			try{
				fTarget.getParameter().accept(new ParameterAdapter());
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		}

		private void validateOperation() throws ModelOperationException {
			try{
				if(fTarget.getParameter() != null){
					fTarget.getParameter().accept(new OperationValidator());
				}
			}catch(Exception e){
				throw (ModelOperationException)e;
			}
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public GenericOperationRemoveChoice(ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, boolean validate) {
		super(OperationNames.REMOVE_PARTITION, true);
		addOperation(new RemoveChoiceOperation(target, choice, adapterProvider));
		if(validate){
			for(MethodNode method : target.getParameter().getMethods()){
				addOperation(new MethodOperationMakeConsistent(method));
			}
		}
	}
}
