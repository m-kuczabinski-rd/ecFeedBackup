package com.testify.ecfeed.adapter.operations;

import java.util.List;

import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class FactoryShiftOperation{

	private static class MoveUpDownOperationProvider implements IModelVisitor{

		private List<? extends AbstractNode> fShifted;
		private boolean fUp;

		public MoveUpDownOperationProvider(List<? extends AbstractNode> shifted, boolean up){
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
			if(fShifted.get(0) instanceof MethodParameterNode){
				return new MethodParameterShiftOperation(node.getMethodParameters(), fShifted, fUp);
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
		public Object visit(MethodParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp);
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
		public Object visit(ChoiceNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}

	private static class ShiftToIndexOperationProvider implements IModelVisitor{

		private List<? extends AbstractNode> fShifted;
		private int fShift;

		public ShiftToIndexOperationProvider(List<? extends AbstractNode> shifted, int index){
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
			if(fShifted.get(0) instanceof MethodParameterNode){
				return new MethodParameterShiftOperation(node.getMethodParameters(), fShifted, fShift);
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
		public Object visit(MethodParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift);
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
		public Object visit(ChoiceNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift);
			}
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}

	public static GenericShiftOperation getShiftOperation(List<? extends AbstractNode> shifted, boolean up) throws ModelOperationException{
		AbstractNode parent = getParent(shifted);
		return getShiftOperation(parent, shifted, new MoveUpDownOperationProvider(shifted, up));
	}

	public static GenericShiftOperation getShiftOperation(List<? extends AbstractNode> shifted, int newIndex) throws ModelOperationException{
		AbstractNode parent = getParent(shifted);
		return getShiftOperation(parent, shifted, new ShiftToIndexOperationProvider(shifted, newIndex));
	}


	private static GenericShiftOperation getShiftOperation(AbstractNode parent, List<? extends AbstractNode> shifted, IModelVisitor provider) throws ModelOperationException{
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

	private static int calculateShift(List<? extends AbstractNode> shifted, int newIndex) {
		int shift = newIndex - minIndexNode(shifted).getIndex();
		if(minIndexNode(shifted).getIndex() < newIndex){
			shift -= 1;
		}
		return shift;
	}

	private static AbstractNode minIndexNode(List<? extends AbstractNode> nodes){
		AbstractNode minIndexNode = nodes.get(0);
		for(AbstractNode node : nodes){
			minIndexNode = node.getIndex() < minIndexNode.getIndex() ? node : minIndexNode;
		}
		return minIndexNode;
	}

	private static boolean haveTheSameType(List<? extends AbstractNode> shifted) {
		if(shifted.size() == 0){
			return false;
		}
		Class<?> _class = shifted.get(0).getClass();
		for(AbstractNode node : shifted){
			if(node.getClass().equals(_class) == false){
				return false;
			}
		}
		return true;
	}

	private static AbstractNode getParent(List<? extends AbstractNode> nodes) {
		if(nodes.size() == 0){
			return null;
		}
		AbstractNode parent = nodes.get(0).getParent();
		if(parent == null){
			return null;
		}

		for(AbstractNode node : nodes){
			if(node.getParent() != parent){
				return null;
			}
		}
		return parent;
	}
}
