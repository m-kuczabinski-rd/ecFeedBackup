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

package com.ecfeed.core.adapter.operations;

import java.util.List;

import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;

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
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fUp);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fUp);
			}
			if(fShifted.get(0) instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fShifted, fUp);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodParameterNode){
				return new MethodParameterShiftOperation(node.getParameters(), fShifted, fUp);
			}
			if(fShifted.get(0) instanceof ConstraintNode){
				return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fUp);
			}
			if(fShifted.get(0) instanceof TestCaseNode){
				return new GenericShiftOperation(node.getTestCases(), fShifted, fUp);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
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
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fShift);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fShifted, fShift);
			}
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fShift);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodParameterNode){
				return new MethodParameterShiftOperation(node.getParameters(), fShifted, fShift);
			}
			if(fShifted.get(0) instanceof ConstraintNode){
				return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fShift);
			}
			if(fShifted.get(0) instanceof TestCaseNode){
				return new GenericShiftOperation(node.getTestCases(), fShifted, fShift);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift);
			}
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
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
		if(parent == null || haveTheSameType(shifted) == false){
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
		try{
			return (GenericShiftOperation)parent.accept(provider);
		}
		catch(Exception e){
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
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
