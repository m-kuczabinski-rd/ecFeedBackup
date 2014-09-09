package com.testify.ecfeed.modelif.java.common;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.category.CategoryOperationShift;

public class SwapOperationFactory implements IModelVisitor{

	private GenericNode fChild;
	private int fNewIndex;

	public SwapOperationFactory(GenericNode target, int newIndex) {
		fChild = target;
		fNewIndex = newIndex;
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fChild instanceof ClassNode){
			return new GenericShiftOperation(node.getClasses(), fChild.getIndex(), fNewIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fChild instanceof MethodNode){
			return new GenericShiftOperation(node.getMethods(), fChild.getIndex(), fNewIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof CategoryNode){
			return new CategoryOperationShift((CategoryNode)fChild, fNewIndex);
		}
		if(fChild instanceof ConstraintNode){
			return new GenericShiftOperation(node.getConstraintNodes(), fChild.getIndex(), fNewIndex);
		}
		if(fChild instanceof TestCaseNode){
			return new GenericShiftOperation(node.getTestCases(), fChild.getIndex(), fNewIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(CategoryNode node) throws Exception {
		if(fChild instanceof PartitionNode){
			return new GenericShiftOperation(node.getPartitions(), fChild.getIndex(), fNewIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(PartitionNode node) throws Exception {
		if(fChild instanceof PartitionNode){
			return new GenericShiftOperation(node.getPartitions(), fChild.getIndex(), fNewIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}
}

