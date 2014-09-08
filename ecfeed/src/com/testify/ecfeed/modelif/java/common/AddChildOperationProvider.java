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
import com.testify.ecfeed.modelif.java.classx.ClassOperationAddMethod;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddConstraint;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddParameter;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddTestCase;
import com.testify.ecfeed.modelif.java.root.RootOperationAddNewClass;

public class AddChildOperationProvider implements IModelVisitor{

	private GenericNode fChild;
	private int fIndex;

	public AddChildOperationProvider(GenericNode child, int index) {
		fChild = child;
		fIndex = index;
	}

	public AddChildOperationProvider(GenericNode child) {
		this(child, -1);
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fChild instanceof ClassNode){
			if(fIndex == -1){
				fIndex = node.getClasses().size();
			}
			return new RootOperationAddNewClass(node, (ClassNode)fChild, fIndex);
		}
		return null;
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fChild instanceof MethodNode){
			if(fIndex == -1){
				fIndex = node.getMethods().size();
			}
			return new ClassOperationAddMethod(node, (MethodNode)fChild, fIndex);
		}
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof CategoryNode){
			if(fIndex == -1){
				fIndex = node.getCategories().size();
			}
			return new MethodOperationAddParameter(node, (CategoryNode)fChild, fIndex);
		}
		if(fChild instanceof ConstraintNode){
			if(fIndex == -1){
				fIndex = node.getConstraintNodes().size();
			}
			return new MethodOperationAddConstraint(node, (ConstraintNode)fChild, fIndex);
		}
		if(fChild instanceof TestCaseNode){
			if(fIndex == -1){
				fIndex = node.getTestCases().size();
			}
			return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fIndex);
		}
		return null;
	}

	@Override
	public Object visit(CategoryNode node) throws Exception {
		if(fChild instanceof PartitionNode){
			return new GenericOperationAddPartition(node, (PartitionNode)fChild, fIndex);
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
			return new GenericOperationAddPartition(node, (PartitionNode)fChild, fIndex);
		}
		return null;
	}
	
}