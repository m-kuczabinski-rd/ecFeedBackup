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
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.category.CategoryOperationShift;
import com.testify.ecfeed.modelif.java.classx.ClassOperationAddMethod;
import com.testify.ecfeed.modelif.java.classx.ClassOperationRemoveMethod;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddConstraint;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddParameter;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddTestCase;
import com.testify.ecfeed.modelif.java.method.MethodOperationRemoveConstraint;
import com.testify.ecfeed.modelif.java.method.MethodOperationRemoveParameter;
import com.testify.ecfeed.modelif.java.method.MethodOperationRemoveTestCase;
import com.testify.ecfeed.modelif.java.root.RootOperationAddNewClass;
import com.testify.ecfeed.modelif.java.root.RootOperationRemoveClass;

public class GenericMoveOperation extends BulkOperation {
	
	private class AddChildOperationProvider implements IModelVisitor{

		private GenericNode fChild;
		private int fIndex;

		public AddChildOperationProvider(GenericNode child, int index) {
			fChild = child;
			fIndex = index;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			if(fChild instanceof ClassNode){
				return new RootOperationAddNewClass(node, (ClassNode)fChild, fIndex);
			}
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fChild instanceof MethodNode){
				return new ClassOperationAddMethod(node, (MethodNode)fChild, fIndex);
			}
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fChild instanceof CategoryNode){
				return new MethodOperationAddParameter(node, (CategoryNode)fChild, fIndex);
			}
			if(fChild instanceof ConstraintNode){
				return new MethodOperationAddConstraint(node, (ConstraintNode)fChild, fIndex);
			}
			if(fChild instanceof TestCaseNode){
				return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fIndex);
			}
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			if(fChild instanceof PartitionNode){
//				return new CategoryOperationAddPartition(node, (PartitionNode)fChild, fIndex);
			}
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(fChild instanceof PartitionNode){
//				return new PartitionOperationAddPartition(node, (PartitionNode)fChild, fIndex);
			}
			return null;
		}
		
	}

	private class RemoveChildOperationProvider implements IModelVisitor{

		private GenericNode fChild;

		public RemoveChildOperationProvider(GenericNode child) {
			fChild = child;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			if(fChild instanceof ClassNode){
				return new RootOperationRemoveClass(node, (ClassNode)fChild);
			}
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fChild instanceof MethodNode){
				return new ClassOperationRemoveMethod(node, (MethodNode)fChild);
			}
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fChild instanceof CategoryNode){
				return new MethodOperationRemoveParameter(node, (CategoryNode)fChild);
			}
			if(fChild instanceof ConstraintNode){
				return new MethodOperationRemoveConstraint(node, (ConstraintNode)fChild);
			}
			if(fChild instanceof TestCaseNode){
				return new MethodOperationRemoveTestCase(node, (TestCaseNode)fChild);
			}
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			if(fChild instanceof PartitionNode){
//				return new CategoryOperationRemovePartition(node, (PartitionNode)fChild, fIndex);
			}
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(fChild instanceof PartitionNode){
//				return new PartitionOperationRemovePartition(node, (PartitionNode)fChild, fIndex);
			}
			return null;
		}
		
	}
	
	private class SwapNodesOperationProvider implements IModelVisitor{

		private GenericNode fChild;
		private int fNewIndex;

		public SwapNodesOperationProvider(GenericNode target, int newIndex) {
			fChild = target;
			fNewIndex = newIndex;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			if(fChild instanceof ClassNode){
				return new GenericShiftOperation(node.getClasses(), fChild.getIndex(), fNewIndex);
			}
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fChild instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fChild.getIndex(), fNewIndex);
			}
			return null;
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
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			if(fChild instanceof PartitionNode){
				return new GenericShiftOperation(node.getPartitions(), fChild.getIndex(), fNewIndex);
			}
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(fChild instanceof PartitionNode){
				return new GenericShiftOperation(node.getPartitions(), fChild.getIndex(), fNewIndex);
			}
			return null;
		}
	}

	public GenericMoveOperation(GenericNode target, GenericNode newParent, int newIndex) throws ModelIfException {
		super(false);
		try {
			if(target.getParent() == newParent){
				addOperation((IModelOperation)newParent.accept(new SwapNodesOperationProvider(target, newIndex)));
			}
			else{
				addOperation((IModelOperation)newParent.accept(new RemoveChildOperationProvider(target)));
				addOperation((IModelOperation)target.getParent().accept(new AddChildOperationProvider(target, newIndex)));
			}
		} catch (Exception e) {
			throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}	
}
