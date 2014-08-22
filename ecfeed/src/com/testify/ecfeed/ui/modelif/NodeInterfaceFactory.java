package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ModelOperationManager;

public class NodeInterfaceFactory{

	private ModelOperationManager fOperationManager;

	public NodeInterfaceFactory(ModelOperationManager operationManager) {
		fOperationManager = operationManager;
	}

	private class InterfaceProvider  implements IModelVisitor {
		@Override
		public Object visit(RootNode node) throws Exception {
			RootInterface nodeIf = new RootInterface(fOperationManager);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			ClassInterface nodeIf = new ClassInterface(fOperationManager);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			MethodInterface nodeIf = new MethodInterface(fOperationManager);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			CategoryInterface nodeIf = new CategoryInterface(fOperationManager);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			TestCaseInterface nodeIf = new TestCaseInterface(fOperationManager);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ConstraintInterface nodeIf = new ConstraintInterface(fOperationManager);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			PartitionInterface nodeIf = new PartitionInterface(fOperationManager);
			nodeIf.setTarget(node);
			return nodeIf;
		}
	}
	
	public GenericNodeInterface getNodeInterface(GenericNode node){
		try{
			return (GenericNodeInterface)node.accept(new InterfaceProvider());
		}
		catch(Exception e){}
		GenericNodeInterface nodeIf = new GenericNodeInterface(fOperationManager);
		nodeIf.setTarget(node);
		return nodeIf;
	}
}
