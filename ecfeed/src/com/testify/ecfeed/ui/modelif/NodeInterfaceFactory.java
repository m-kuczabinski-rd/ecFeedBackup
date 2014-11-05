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
		
		private IModelUpdateContext fContext;

		public InterfaceProvider(IModelUpdateContext context) {
			fContext = context;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			RootInterface nodeIf = new RootInterface(fContext);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			ClassInterface nodeIf = new ClassInterface(fContext);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			MethodInterface nodeIf = new MethodInterface(fContext);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			CategoryInterface nodeIf = new CategoryInterface(fContext);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			TestCaseInterface nodeIf = new TestCaseInterface(fContext);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ConstraintInterface nodeIf = new ConstraintInterface(fContext);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			ChoiceInterface nodeIf = new ChoiceInterface(fContext);
			nodeIf.setTarget(node);
			return nodeIf;
		}
	}
	
	public static GenericNodeInterface getNodeInterface(GenericNode node, IModelUpdateContext context){
		try{
			return (GenericNodeInterface)node.accept(new InterfaceProvider(context));
		}
		catch(Exception e){}
		GenericNodeInterface nodeIf = new GenericNodeInterface(context);
		nodeIf.setTarget(node);
		return nodeIf;
	}
}
