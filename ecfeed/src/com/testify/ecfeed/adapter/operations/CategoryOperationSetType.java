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
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.IStatementVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedCategoryStatement;
import com.testify.ecfeed.model.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.PartitionedCategoryStatement.PartitionCondition;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;

public class CategoryOperationSetType extends BulkOperation{

	private class SetTypeOperation extends AbstractModelOperation{

		private CategoryNode fTarget;
		private String fNewType;
		private String fCurrentType;
		private String fOriginalDefaultValue;
		private List<PartitionNode> fOriginalPartitions;
		private Map<PartitionNode, String> fOriginalValues;
		private List<TestCaseNode> fOriginalTestCases;
		private List<ConstraintNode> fOriginalConstraints;

		private ITypeAdapterProvider fAdapterProvider;

		private class StatementAdapter implements IStatementVisitor{

			@Override
			public Object visit(StaticStatement statement) throws Exception {
				return true;
			}

			@Override
			public Object visit(StatementArray statement) throws Exception {
				return true;
			}

			@Override
			public Object visit(ExpectedValueStatement statement) throws Exception {
				ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
				String newValue = adapter.convert(statement.getCondition().getValueString());
				statement.getCondition().setValueString(newValue);
				return newValue != null && fTarget.getLeafPartitionValues().contains(newValue);
			}

			@Override
			public Object visit(PartitionedCategoryStatement statement)
					throws Exception {
				return true;
			}

			@Override
			public Object visit(LabelCondition condition) throws Exception {
				return true;
			}

			@Override
			public Object visit(PartitionCondition condition) throws Exception {
				return true;
			}
		}

		private class ReverseOperation extends AbstractModelOperation{

			public ReverseOperation() {
				super(CategoryOperationSetType.this.getName());
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.setType(fCurrentType);
				fTarget.setDefaultValueString(fOriginalDefaultValue);
				fTarget.replacePartitions(fOriginalPartitions);
				revertPartitionValues(fTarget.getPartitions());
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetTypeOperation(fTarget, fNewType, fAdapterProvider);
			}

			private void revertPartitionValues(List<PartitionNode> choices) {
				for(PartitionNode choice : choices){
					if(fOriginalValues.containsKey(choice)){
						choice.setValueString(fOriginalValues.get(choice));
					}
					revertPartitionValues(choice.getPartitions());
				}
			}

		}

		public SetTypeOperation(CategoryNode target, String newType, ITypeAdapterProvider adapterProvider) {
			super(OperationNames.SET_TYPE);
			fTarget = target;
			fNewType = newType;
			fCurrentType = target.getType();
			fAdapterProvider = adapterProvider;
		}

		@Override
		public void execute() throws ModelOperationException {
			fOriginalDefaultValue = fTarget.getDefaultValue();
			fOriginalPartitions = new ArrayList<PartitionNode>(fTarget.getPartitions());
			fOriginalValues = new HashMap<>();
			fOriginalTestCases = new ArrayList<>(fTarget.getMethod().getTestCases());
			fOriginalConstraints = new ArrayList<>(fTarget.getMethod().getConstraintNodes());

			if(JavaUtils.isValidTypeName(fNewType) == false){
				throw new ModelOperationException(Messages.CATEGORY_TYPE_REGEX_PROBLEM);
			}
			MethodNode method = fTarget.getMethod();
			List<String> parameterTypes = method.getCategoriesTypes();
			parameterTypes.set(fTarget.getIndex(), fNewType);
			if(method.getClassNode().getMethod(method.getName(), parameterTypes) != null){
				throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
			}

			ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
			fTarget.setType(fNewType);

			convertPartitionValues(fTarget, adapter);
			removeDeadPartitions(fTarget);

			String defaultValue = adapter.convert(fTarget.getDefaultValue());
			if(defaultValue == null){
				if(fTarget.getLeafPartitions().size() > 0){
					defaultValue = fTarget.getLeafPartitions().toArray(new PartitionNode[]{})[0].getValueString();
				}
				else{
					defaultValue = adapter.defaultValue();
				}
			}
			if(JavaUtils.isUserType(fNewType)){
				if(fTarget.getLeafPartitions().size() > 0){
					if(fTarget.getLeafPartitionValues().contains(defaultValue) == false){
						defaultValue = fTarget.getLeafPartitionValues().toArray(new String[]{})[0];
					}
				}
				else{
					fTarget.addPartition(new PartitionNode(defaultValue.toLowerCase(), defaultValue));
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

		private void convertPartitionValues(PartitionedNode parent, ITypeAdapter adapter) {
			for(PartitionNode p : parent.getPartitions()){
				convertPartitionValue(p, adapter);
				convertPartitionValues(p, adapter);
			}
		}

		private void convertPartitionValue(PartitionNode p, ITypeAdapter adapter) {
			fOriginalValues.put(p, p.getValueString());
			String newValue = adapter.convert(p.getValueString());
			p.setValueString(newValue);
		}

		private void removeDeadPartitions(PartitionedNode parent) {
			List<PartitionNode> toRemove = new ArrayList<PartitionNode>();
			for(PartitionNode p : parent.getPartitions()){
				if(isDead(p)){
					toRemove.add(p);
				}
				else{
					removeDeadPartitions(p);
				}
			}
			for(PartitionNode removed : toRemove){
				parent.removePartition(removed);
			}
		}

		private void adaptTestCases() {
			MethodNode method = fTarget.getMethod();
			if(method != null){
				Iterator<TestCaseNode> tcIt = method.getTestCases().iterator();
				ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
				while(tcIt.hasNext()){
					PartitionNode expectedValue = tcIt.next().getTestData().get(fTarget.getIndex());
					String newValue = adapter.convert(expectedValue.getValueString());
					if(JavaUtils.isUserType(fNewType)){
						if(fTarget.getLeafPartitionValues().contains(newValue) == false){
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
			// TODO Auto-generated method stub
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

		private boolean isDead(PartitionNode p) {
			if(p.isAbstract() == false){
				return p.getValueString() == null;
			}
			boolean allChildrenDead = true;
			for(PartitionNode child : p.getPartitions()){
				if(isDead(child) == false){
					allChildrenDead = false;
					break;
				}
			}
			return allChildrenDead;
		}
	}

	public CategoryOperationSetType(CategoryNode target, String newType, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_TYPE, true);
		addOperation(new SetTypeOperation(target, newType, adapterProvider));
		if(target.getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getMethod()));
		}
	}
}
