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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IChoicesParentVisitor;
import com.ecfeed.core.model.IStatementVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.ChoicesParentStatement.ChoiceCondition;
import com.ecfeed.core.model.ChoicesParentStatement.LabelCondition;
import com.ecfeed.core.utils.SystemLogger;

public class MethodParameterOperationSetType extends BulkOperation {

	private class SetTypeOperation extends AbstractParameterOperationSetType{

		private String fOriginalDefaultValue;
		private Map<AbstractStatement, String> fOriginalStatementValues;
		private ArrayList<TestCaseNode> fOriginalTestCases;
		private ArrayList<ConstraintNode> fOriginalConstraints;

		private class RealChoicesProvider implements IChoicesParentVisitor{

			@Override
			public Object visit(MethodParameterNode node) throws Exception {
				return node.getRealChoices();
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				return node.getChoices();
			}

			@Override
			public Object visit(ChoiceNode node) throws Exception {
				return node.getChoices();
			}

		}

		private class StatementAdapter implements IStatementVisitor{

			@Override
			public Object visit(StaticStatement statement) throws Exception {
				return true;
			}

			@Override
			public Object visit(StatementArray statement) throws Exception {
				boolean success = true;
				for(AbstractStatement child : statement.getChildren()){
					try{
						success &= (boolean)child.accept(this);
					}catch(Exception e){
						success = false;
					}
				}
				return success;
			}

			@Override
			public Object visit(ExpectedValueStatement statement) throws Exception {
				boolean success = true;
				ITypeAdapter adapter = getAdapterProvider().getAdapter(getNewType());
				String newValue = adapter.convert(statement.getCondition().getValueString());
				fOriginalStatementValues.put(statement, statement.getCondition().getValueString());
				statement.getCondition().setValueString(newValue);
				if(JavaUtils.isUserType(getNewType())){
					success = newValue != null && fTarget.getLeafChoiceValues().contains(newValue);
				}
				else{
					success = newValue != null;
				}
				return success;
			}

			@Override
			public Object visit(ChoicesParentStatement statement)
					throws Exception {
				return true;
			}

			@Override
			public Object visit(LabelCondition condition) throws Exception {
				return true;
			}

			@Override
			public Object visit(ChoiceCondition condition) throws Exception {
				return true;
			}
		}

		private class ReverseSetTypeOperation extends AbstractParameterOperationSetType.ReverseOperation{

			private class StatementValueRestorer implements IStatementVisitor{

				@Override
				public Object visit(StaticStatement statement) throws Exception {
					return null;
				}

				@Override
				public Object visit(StatementArray statement) throws Exception {
					for(AbstractStatement child : statement.getChildren()){
						try{
							child.accept(this);
						}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
					}
					return null;
				}

				@Override
				public Object visit(ExpectedValueStatement statement)
						throws Exception {
					if(fOriginalStatementValues.containsKey(statement)){
						statement.getCondition().setValueString(fOriginalStatementValues.get(statement));
					}
					return null;
				}

				@Override
				public Object visit(ChoicesParentStatement statement)
						throws Exception {
					return null;
				}

				@Override
				public Object visit(LabelCondition condition) throws Exception {
					return null;
				}

				@Override
				public Object visit(ChoiceCondition condition)
						throws Exception {
					return null;
				}
			}

			@Override
			public void execute() throws ModelOperationException{
				super.execute();
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				fTarget.setDefaultValueString(fOriginalDefaultValue);
				restoreStatementValues();
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation(){
				return new SetTypeOperation(fTarget, getNewType(), getAdapterProvider());
			}

			private void restoreStatementValues() {
				IStatementVisitor valueRestorer = new StatementValueRestorer();
				for(ConstraintNode constraint : fTarget.getMethod().getConstraintNodes()){
					try{
						constraint.getConstraint().getPremise().accept(valueRestorer);
						constraint.getConstraint().getConsequence().accept(valueRestorer);
					}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
				}
			}

		}

		private MethodParameterNode fTarget;

		public SetTypeOperation(MethodParameterNode target, String newType, ITypeAdapterProvider adapterProvider) {
			super(target, newType, adapterProvider);
			fTarget = target;
			fOriginalStatementValues = new HashMap<>();
		}

		@Override
		public void execute() throws ModelOperationException{
			MethodNode method = fTarget.getMethod();
			List<String> types = method.getParametersTypes();
			types.set(fTarget.getIndex(), getNewType());
			if(method.getClassNode().getMethod(method.getName(), types) != null && method.getClassNode().getMethod(method.getName(), types) != method){
				ModelOperationException.report(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
			}
			
			super.execute();
			fOriginalTestCases = new ArrayList<>(fTarget.getMethod().getTestCases());
			fOriginalConstraints = new ArrayList<>(fTarget.getMethod().getConstraintNodes());
			adaptDefaultValue();
			if(fTarget.isExpected()){
				adaptTestCases();
				adaptConstraints();
			}

			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation(){
			return new ReverseSetTypeOperation();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<ChoiceNode> getChoices(ChoicesParentNode parent){
			try{
				return (List<ChoiceNode>)parent.accept(new RealChoicesProvider());
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
			return null;
		}

		private void adaptDefaultValue() {
			fOriginalDefaultValue = fTarget.getDefaultValue();
			ITypeAdapter adapter = getAdapterProvider().getAdapter(getNewType());
			String defaultValue = adapter.convert(fTarget.getDefaultValue());
			if(defaultValue == null){
				if(fTarget.getLeafChoices().size() > 0){
					defaultValue = fTarget.getLeafChoices().toArray(new ChoiceNode[]{})[0].getValueString();
				}
				else{
					defaultValue = adapter.defaultValue();
				}
			}
			if(JavaUtils.isUserType(getNewType())){
				if(fTarget.getLeafChoices().size() > 0){
					if(fTarget.getLeafChoiceValues().contains(defaultValue) == false){
						defaultValue = fTarget.getLeafChoiceValues().toArray(new String[]{})[0];
					}
				}
				else{
					fTarget.addChoice(new ChoiceNode(defaultValue.toLowerCase(), defaultValue));
				}
			}
			fTarget.setDefaultValueString(defaultValue);
		}

		private void adaptTestCases() {
			MethodNode method = fTarget.getMethod();
			if(method != null){
				Iterator<TestCaseNode> tcIt = method.getTestCases().iterator();
				ITypeAdapter adapter = getAdapterProvider().getAdapter(getNewType());
				while(tcIt.hasNext()){
					ChoiceNode expectedValue = tcIt.next().getTestData().get(fTarget.getIndex());
					String newValue = adapter.convert(expectedValue.getValueString());
					if(JavaUtils.isUserType(getNewType())){
						if(fTarget.getLeafChoiceValues().contains(newValue) == false){
							tcIt.remove();
							continue;
						}
					}
					if(newValue == null && adapter.isNullAllowed() == false){
						tcIt.remove();
						continue;
					}
					else{
						if(expectedValue.getValueString().equals(newValue) == false){
							expectedValue.setValueString(newValue);
						}
					}
				}
			}
		}

		private void adaptConstraints() {
			Iterator<ConstraintNode> it = fTarget.getMethod().getConstraintNodes().iterator();
			while(it.hasNext()){
				Constraint constraint = it.next().getConstraint();
				IStatementVisitor statementAdapter = new StatementAdapter();
				try{
				if((boolean)constraint.getPremise().accept(statementAdapter) == false ||
						(boolean)constraint.getConsequence().accept(statementAdapter) == false){
					it.remove();
				}
				}catch(Exception e){
					it.remove();
				}
			}
		}
}

	public MethodParameterOperationSetType(MethodParameterNode target, String newType, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_TYPE, true);
		addOperation(new SetTypeOperation(target, newType, adapterProvider));
		if(target.getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getMethod()));
		}
	}
}
