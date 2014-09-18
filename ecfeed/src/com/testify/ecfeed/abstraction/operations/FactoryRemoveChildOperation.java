package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class FactoryRemoveChildOperation implements IModelVisitor{

	private GenericNode fChild;
	private boolean fValidate;

	public FactoryRemoveChildOperation(GenericNode child, boolean validate) {
		fChild = child;
		fValidate = validate;
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
			return new GenericOperationRemovePartition(node, (PartitionNode)fChild, fValidate);
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
			return new GenericOperationRemovePartition(node, (PartitionNode)fChild, fValidate);
		}
		return null;
	}
	
}
