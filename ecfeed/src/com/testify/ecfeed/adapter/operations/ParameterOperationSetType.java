package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentNode;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.ChoicesParentStatement.ChoiceCondition;
import com.testify.ecfeed.model.ChoicesParentStatement.LabelCondition;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.IStatementVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;

public class ParameterOperationSetType extends BulkOperation{

	private class SetTypeOperation extends AbstractModelOperation{

		private MethodParameterNode fTarget;
		private String fNewType;
		private String fCurrentType;
		private String fOriginalDefaultValue;
		private List<ChoiceNode> fOriginalChoices;
		private Map<ChoiceNode, String> fOriginalValues;
		private List<TestCaseNode> fOriginalTestCases;
		private List<ConstraintNode> fOriginalConstraints;
		private Map<AbstractStatement, String> fOriginalStatementValues;

		private ITypeAdapterProvider fAdapterProvider;

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
				ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
				String newValue = adapter.convert(statement.getCondition().getValueString());
				fOriginalStatementValues.put(statement, statement.getCondition().getValueString());
				statement.getCondition().setValueString(newValue);
				if(JavaUtils.isUserType(fNewType)){
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

		private class ReverseOperation extends AbstractModelOperation{

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
						}catch(Exception e){}
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

			public ReverseOperation() {
				super(ParameterOperationSetType.this.getName());
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.setType(fCurrentType);
				fTarget.setDefaultValueString(fOriginalDefaultValue);
				fTarget.replaceChoices(fOriginalChoices);
				revertChoiceValues(fTarget.getChoices());
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				restoreConstraintValues();
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetTypeOperation(fTarget, fNewType, fAdapterProvider);
			}

			private void restoreConstraintValues() {
				IStatementVisitor valueRestorer = new StatementValueRestorer();
				for(ConstraintNode constraint : fTarget.getMethod().getConstraintNodes()){
					try{
						constraint.getConstraint().getPremise().accept(valueRestorer);
						constraint.getConstraint().getConsequence().accept(valueRestorer);
					}catch(Exception e){}
				}
			}

			private void revertChoiceValues(List<ChoiceNode> choices) {
				for(ChoiceNode choice : choices){
					if(fOriginalValues.containsKey(choice)){
						choice.setValueString(fOriginalValues.get(choice));
					}
					revertChoiceValues(choice.getChoices());
				}
			}

		}

		public SetTypeOperation(MethodParameterNode target, String newType, ITypeAdapterProvider adapterProvider) {
			super(OperationNames.SET_TYPE);
			fTarget = target;
			fNewType = newType;
			fCurrentType = target.getType();
			fAdapterProvider = adapterProvider;
		}

		@Override
		public void execute() throws ModelOperationException {
			fOriginalDefaultValue = fTarget.getDefaultValue();
			fOriginalChoices = new ArrayList<ChoiceNode>(fTarget.getChoices());
			fOriginalValues = new HashMap<>();
			fOriginalTestCases = new ArrayList<>(fTarget.getMethod().getTestCases());
			fOriginalConstraints = new ArrayList<>(fTarget.getMethod().getConstraintNodes());
			fOriginalStatementValues = new HashMap<>();

			if(JavaUtils.isValidTypeName(fNewType) == false){
				throw new ModelOperationException(Messages.CATEGORY_TYPE_REGEX_PROBLEM);
			}
			MethodNode method = fTarget.getMethod();
			List<String> parameterTypes = method.getParametersTypes();
			parameterTypes.set(fTarget.getIndex(), fNewType);
			if(method.getClassNode().getMethod(method.getName(), parameterTypes) != null){
				throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
			}

			ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
			fTarget.setType(fNewType);

			convertChoiceValues(fTarget, adapter);
			removeDeadChoices(fTarget);

			String defaultValue = adapter.convert(fTarget.getDefaultValue());
			if(defaultValue == null){
				if(fTarget.getLeafChoices().size() > 0){
					defaultValue = fTarget.getLeafChoices().toArray(new ChoiceNode[]{})[0].getValueString();
				}
				else{
					defaultValue = adapter.defaultValue();
				}
			}
			if(JavaUtils.isUserType(fNewType)){
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
			if(fTarget.isExpected()){
				adaptTestCases();
				adaptConstraints();
			}

			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation(){
			return new ReverseOperation();
		}

		private void convertChoiceValues(ChoicesParentNode parent, ITypeAdapter adapter) {
			for(ChoiceNode p : parent.getChoices()){
				convertChoiceValue(p, adapter);
				convertChoiceValues(p, adapter);
			}
		}

		private void convertChoiceValue(ChoiceNode p, ITypeAdapter adapter) {
			fOriginalValues.put(p, p.getValueString());
			String newValue = adapter.convert(p.getValueString());
			p.setValueString(newValue);
		}

		private void removeDeadChoices(ChoicesParentNode parent) {
			List<ChoiceNode> toRemove = new ArrayList<ChoiceNode>();
			for(ChoiceNode p : parent.getChoices()){
				if(isDead(p)){
					toRemove.add(p);
				}
				else{
					removeDeadChoices(p);
				}
			}
			for(ChoiceNode removed : toRemove){
				parent.removeChoice(removed);
			}
		}

		private void adaptTestCases() {
			MethodNode method = fTarget.getMethod();
			if(method != null){
				Iterator<TestCaseNode> tcIt = method.getTestCases().iterator();
				ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
				while(tcIt.hasNext()){
					ChoiceNode expectedValue = tcIt.next().getTestData().get(fTarget.getIndex());
					String newValue = adapter.convert(expectedValue.getValueString());
					if(JavaUtils.isUserType(fNewType)){
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
			fOriginalStatementValues.clear();
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

		private boolean isDead(ChoiceNode p) {
			if(p.isAbstract() == false){
				return p.getValueString() == null;
			}
			boolean allChildrenDead = true;
			for(ChoiceNode child : p.getChoices()){
				if(isDead(child) == false){
					allChildrenDead = false;
					break;
				}
			}
			return allChildrenDead;
		}
	}

	public ParameterOperationSetType(MethodParameterNode target, String newType, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_TYPE, true);
		addOperation(new SetTypeOperation(target, newType, adapterProvider));
		if(target.getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getMethod()));
		}
	}
}
