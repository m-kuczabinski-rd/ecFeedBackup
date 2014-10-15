package com.testify.ecfeed.adapter;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public abstract class AbstractImplementationStatusResolver implements
		IImplementationStatusResolver {

	private StatusResolver fStatusResolver;
	
	private class StatusResolver implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			return implementationStatus(node);
		}
	}
	
	public AbstractImplementationStatusResolver() {
		fStatusResolver = new StatusResolver();
	}
	
	@Override
	public EImplementationStatus getImplementationStatus(GenericNode node) {
		try{
			EImplementationStatus status = (EImplementationStatus)node.accept(fStatusResolver); 
			return status;
		}
		catch(Exception e){}
		return EImplementationStatus.NOT_IMPLEMENTED;
	}

	protected abstract EImplementationStatus implementationStatus(RootNode node);
	protected abstract EImplementationStatus implementationStatus(ClassNode node);
	protected abstract EImplementationStatus implementationStatus(MethodNode node);
	protected abstract EImplementationStatus implementationStatus(CategoryNode node);
	protected abstract EImplementationStatus implementationStatus(TestCaseNode node);
	protected abstract EImplementationStatus implementationStatus(ConstraintNode node);
	protected abstract EImplementationStatus implementationStatus(PartitionNode node);

}
