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

public class NodeInterfaceFactory{

	private static class InterfaceProvider  implements IModelVisitor {
		@Override
		public Object visit(RootNode node) throws Exception {
			RootInterface nodeIf = new RootInterface();
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			ClassInterface nodeIf = new ClassInterface();
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			MethodInterface nodeIf = new MethodInterface();
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			CategoryInterface nodeIf = new CategoryInterface();
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			TestCaseInterface nodeIf = new TestCaseInterface();
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ConstraintInterface nodeIf = new ConstraintInterface();
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			PartitionInterface nodeIf = new PartitionInterface();
			nodeIf.setTarget(node);
			return nodeIf;
		}
	}
	
	public static GenericNodeInterface getNodeInterface(GenericNode node){
		try{
			return (GenericNodeInterface)node.accept(new InterfaceProvider());
		}
		catch(Exception e){}
		GenericNodeInterface nodeIf = new GenericNodeInterface();
		nodeIf.setTarget(node);
		return nodeIf;
	}
}
