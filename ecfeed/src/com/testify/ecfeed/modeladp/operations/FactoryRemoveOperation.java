package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

public class FactoryRemoveOperation {
	
	private static class UnsupportedModelOperation implements IModelOperation{
		@Override
		public void execute() throws ModelOperationException {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new UnsupportedModelOperation();
		}

		@Override
		public boolean modelUpdated() {
			return false;
		}

		@Override
		public String getName() {
			return "";
		}
	}
	
	private static class RemoveOperationVisitor implements IModelVisitor{

		private boolean fValidate;
		
		public RemoveOperationVisitor(boolean validate){
			fValidate = validate;
		}
		
		@Override
		public Object visit(RootNode node) throws Exception {
			return new UnsupportedModelOperation();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new RootOperationRemoveClass(node.getRoot(), node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new ClassOperationRemoveMethod(node.getClassNode(), node);
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return new MethodOperationRemoveParameter(node.getMethod(), node, fValidate);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new MethodOperationRemoveTestCase(node.getMethod(), node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return new MethodOperationRemoveConstraint(node.getMethod(), node);
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			return new GenericOperationRemovePartition(node.getParent(), node, fValidate);
		}
	}
	
	public static IModelOperation getRemoveOperation(GenericNode node, boolean validate){
		try {
			return (IModelOperation)node.accept(new RemoveOperationVisitor(validate));
		} catch (Exception e) {
			return new UnsupportedModelOperation();
		}
	}
}
