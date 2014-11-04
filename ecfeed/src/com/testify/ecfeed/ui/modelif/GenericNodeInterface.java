package com.testify.ecfeed.ui.modelif;

import java.util.Arrays;
import java.util.Collection;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.operations.FactoryRemoveOperation;
import com.testify.ecfeed.adapter.operations.FactoryRenameOperation;
import com.testify.ecfeed.adapter.operations.FactoryShiftOperation;
import com.testify.ecfeed.adapter.operations.GenericAddChildrenOperation;
import com.testify.ecfeed.adapter.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.adapter.operations.GenericShiftOperation;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;

public class GenericNodeInterface extends OperationExecuter{

	private GenericNode fTarget;
	private EclipseImplementationStatusResolver fStatusResolver;
	private ITypeAdapterProvider fAdapterProvider;

	private class RenameParameterProblemTitleProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Messages.DIALOG_RENAME_MODEL_PROBLEM_TITLE;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Messages.DIALOG_RENAME_CONSTRAINT_PROBLEM_TITLE;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			return Messages.DIALOG_RENAME_PARTITION_PROBLEM_TITLE;
		}

	}

	public GenericNodeInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fStatusResolver = new EclipseImplementationStatusResolver();
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	public void setTarget(GenericNode target){
		fTarget = target;
	}

	public EImplementationStatus getImplementationStatus(GenericNode node){
		return fStatusResolver.getImplementationStatus(node);
	}

	public EImplementationStatus getImplementationStatus(){
		return getImplementationStatus(fTarget);
	}

	static public boolean validateName(String name){
		return true;
	}

	public String getName(){
		return fTarget.getName();
	}

	public boolean setName(String newName){
		if(newName.equals(getName())){
			return false;
		}
		String problemTitle = "";
		try{
			problemTitle = (String)fTarget.accept(new RenameParameterProblemTitleProvider());
		}catch(Exception e){}
		return execute(FactoryRenameOperation.getRenameOperation(fTarget, newName), problemTitle);
	}

	public boolean remove(){
		return execute(FactoryRemoveOperation.getRemoveOperation(fTarget, true), Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
	}

	public boolean removeChildren(Collection<? extends GenericNode> children, String message){
		if(children == null || children.size() == 0) return false;
		for(GenericNode node : children){
			if(node.getParent() != fTarget) return false;
		}
		return execute(new GenericRemoveNodesOperation(children, true), message);
	}

	public boolean addChildren(Collection<? extends GenericNode> children){
		IModelOperation operation = new GenericAddChildrenOperation(fTarget, children, fAdapterProvider, true);
		return execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}

	public boolean addChildren(Collection<? extends GenericNode> children, int index){
		IModelOperation operation;
		if(index == -1){
			operation = new GenericAddChildrenOperation(fTarget, children, fAdapterProvider, true);
		}
		else{
			operation = new GenericAddChildrenOperation(fTarget, children, index, fAdapterProvider, true);
		}
		return execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}

	public boolean pasteEnabled(Collection<? extends GenericNode> pasted){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted, fAdapterProvider, true);
		return operation.enabled();
	}

	public boolean pasteEnabled(Collection<? extends GenericNode> pasted, int index){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted, index, fAdapterProvider, true);
		return operation.enabled();
	}

	public boolean moveUpDown(boolean up) {
		try{
			GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(Arrays.asList(new GenericNode[]{fTarget}), up);
			if(operation.getShift() > 0){
				return executeMoveOperation(operation);
			}
		}catch(Exception e){}
		return false;
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation) {
		return execute(moveOperation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
	}
}
