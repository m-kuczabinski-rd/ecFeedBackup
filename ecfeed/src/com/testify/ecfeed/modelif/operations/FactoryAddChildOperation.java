package com.testify.ecfeed.modelif.operations;

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

public class FactoryAddChildOperation implements IModelVisitor{

	private GenericNode fChild;
	private int fIndex;

	public FactoryAddChildOperation(GenericNode child, int index) {
		fChild = child;
		fIndex = index;
	}

	public FactoryAddChildOperation(GenericNode child) {
		this(child, -1);
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fChild instanceof ClassNode){
			if(fIndex == -1){
				return new RootOperationAddNewClass(node, (ClassNode)fChild);
			}
			return new RootOperationAddNewClass(node, (ClassNode)fChild, fIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fChild instanceof MethodNode){
			if(fIndex == -1){
				return new ClassOperationAddMethod(node, (MethodNode)fChild);
			}
			return new ClassOperationAddMethod(node, (MethodNode)fChild, fIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof CategoryNode){
			if(fIndex == -1){
				return new MethodOperationAddParameter(node, (CategoryNode)fChild);
			}
			return new MethodOperationAddParameter(node, (CategoryNode)fChild, fIndex);
		}
		if(fChild instanceof ConstraintNode){
			if(fIndex == -1){
				return new MethodOperationAddConstraint(node, (ConstraintNode)fChild);
			}
			return new MethodOperationAddConstraint(node, (ConstraintNode)fChild, fIndex);
		}
		if(fChild instanceof TestCaseNode){
			if(fIndex == -1){
				return new MethodOperationAddTestCase(node, (TestCaseNode)fChild);
			}
			return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(CategoryNode node) throws Exception {
		if(fChild instanceof PartitionNode){
			if(fIndex == -1){
				return new GenericOperationAddPartition(node, (PartitionNode)fChild);
			}
			return new GenericOperationAddPartition(node, (PartitionNode)fChild, fIndex);
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
			if(fIndex == -1){
				return new GenericOperationAddPartition(node, (PartitionNode)fChild);
			}
			return new GenericOperationAddPartition(node, (PartitionNode)fChild, fIndex);
		}
		throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}
	
}
