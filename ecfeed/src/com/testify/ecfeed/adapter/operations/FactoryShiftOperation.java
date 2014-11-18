package com.testify.ecfeed.adapter.operations;

import java.util.List;

import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class FactoryShiftOperation{

	private static class MoveUpDownOperationProvider implements IModelVisitor{
		
		private List<? extends GenericNode> fShifted;
		private boolean fUp;

		public MoveUpDownOperationProvider(List<? extends GenericNode> shifted, boolean up){
			fShifted = shifted;
			fUp = up;
		}
		
		@Override
		public Object visit(RootNode node) throws Exception {
			if(fShifted.get(0) instanceof ClassNode){
				return new GenericShiftOperation(node.getClasses(), fShifted, fUp);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fShifted, fUp);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fShifted.get(0) instanceof ParameterNode){
				return new ParameterShiftOperation(node.getParameters(), fShifted, fUp);
			}
			if(fShifted.get(0) instanceof ConstraintNode){
				return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fUp);
			}
			if(fShifted.get(0) instanceof TestCaseNode){
				return new GenericShiftOperation(node.getTestCases(), fShifted, fUp);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(ParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof PartitionNode){
				return new GenericShiftOperation(node.getPartitions(), fShifted, fUp);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(fShifted.get(0) instanceof PartitionNode){
				return new GenericShiftOperation(node.getPartitions(), fShifted, fUp);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}

	private static class ShiftToIndexOperationProvider implements IModelVisitor{
		
		private List<? extends GenericNode> fShifted;
		private int fShift;

		public ShiftToIndexOperationProvider(List<? extends GenericNode> shifted, int index){
			fShifted = shifted;
			fShift = calculateShift(shifted, index);
		}
		
		@Override
		public Object visit(RootNode node) throws Exception {
			if(fShifted.get(0) instanceof ClassNode){
				return new GenericShiftOperation(node.getClasses(), fShifted, fShift);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fShifted, fShift);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fShifted.get(0) instanceof ParameterNode){
				return new ParameterShiftOperation(node.getParameters(), fShifted, fShift);
			}
			if(fShifted.get(0) instanceof ConstraintNode){
				return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fShift);
			}
			if(fShifted.get(0) instanceof TestCaseNode){
				return new GenericShiftOperation(node.getTestCases(), fShifted, fShift);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(ParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof PartitionNode){
				return new GenericShiftOperation(node.getPartitions(), fShifted, fShift);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(fShifted.get(0) instanceof PartitionNode){
				return new GenericShiftOperation(node.getPartitions(), fShifted, fShift);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}

	public static GenericShiftOperation getShiftOperation(List<? extends GenericNode> shifted, boolean up) throws ModelOperationException{
		GenericNode parent = getParent(shifted);
		return getShiftOperation(parent, shifted, new MoveUpDownOperationProvider(shifted, up));
	}

	public static GenericShiftOperation getShiftOperation(List<? extends GenericNode> shifted, int newIndex) throws ModelOperationException{
		GenericNode parent = getParent(shifted);
		return getShiftOperation(parent, shifted, new ShiftToIndexOperationProvider(shifted, newIndex));
	}

	
	private static GenericShiftOperation getShiftOperation(GenericNode parent, List<? extends GenericNode> shifted, IModelVisitor provider) throws ModelOperationException{
		if(parent == null && haveTheSameType(shifted) == false){
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
		try{
			return (GenericShiftOperation)parent.accept(provider);
		}
		catch(Exception e){
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}

	private static int calculateShift(List<? extends GenericNode> shifted, int newIndex) {
		int shift = newIndex - minIndexNode(shifted).getIndex(); 
		if(minIndexNode(shifted).getIndex() < newIndex){
			shift -= 1;
		}
		return shift;
	}

	private static GenericNode minIndexNode(List<? extends GenericNode> nodes){
		GenericNode minIndexNode = nodes.get(0);
		for(GenericNode node : nodes){
			minIndexNode = node.getIndex() < minIndexNode.getIndex() ? node : minIndexNode; 
		}
		return minIndexNode;
	}

	private static boolean haveTheSameType(List<? extends GenericNode> shifted) {
		if(shifted.size() == 0){
			return false;
		}
		Class<?> _class = shifted.get(0).getClass();
		for(GenericNode node : shifted){
			if(node.getClass().equals(_class) == false){
				return false;
			}
		}
		return true;
	}

	private static GenericNode getParent(List<? extends GenericNode> nodes) {
		if(nodes.size() == 0){
			return null;
		}
		GenericNode parent = nodes.get(0).getParent();
		if(parent == null){
			return null;
		}
		
		for(GenericNode node : nodes){
			if(node.getParent() != parent){
				return null;
			}
		}
		return parent;
	}
}
