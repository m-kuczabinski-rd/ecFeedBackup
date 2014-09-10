package com.testify.ecfeed.modelif.operations;

import java.util.Arrays;
import java.util.List;

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

public class ShiftOperationFactory implements IModelVisitor {

	private List<? extends GenericNode> fShifted;
	private boolean fUp;

	public ShiftOperationFactory(List<? extends GenericNode> shifted, boolean up) {
		fShifted = shifted;
		fUp = up;
	}

	public ShiftOperationFactory(GenericNode shifted, boolean up) {
		fShifted = Arrays.asList(new GenericNode[]{shifted});
		fUp = up;
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fShifted.get(0) instanceof ClassNode){
			return new GenericShiftOperation(node.getClasses(), fShifted, fUp);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fShifted.get(0) instanceof MethodNode){
			return new GenericShiftOperation(node.getMethods(), fShifted, fUp);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fShifted.get(0) instanceof CategoryNode){
			return new CategoryShiftOperation(node.getCategories(), fShifted, fUp);
		}
		if(fShifted.get(0) instanceof ConstraintNode){
			return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fUp);
		}
		if(fShifted.get(0) instanceof TestCaseNode){
			return new GenericShiftOperation(node.getTestCases(), fShifted, fUp);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(CategoryNode node) throws Exception {
		if(fShifted.get(0) instanceof PartitionNode){
			return new GenericShiftOperation(node.getPartitions(), fShifted, fUp);
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
		if(fShifted.get(0) instanceof PartitionNode){
			return new GenericShiftOperation(node.getPartitions(), fShifted, fUp);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}
}
