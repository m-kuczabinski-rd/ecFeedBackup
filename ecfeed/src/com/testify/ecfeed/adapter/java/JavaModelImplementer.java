package com.testify.ecfeed.adapter.java;

import com.testify.ecfeed.adapter.AbstractModelImplementer;
import com.testify.ecfeed.adapter.IImplementationStatusResolver;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public abstract class JavaModelImplementer extends AbstractModelImplementer{

	public JavaModelImplementer(IImplementationStatusResolver statusResolver) {
		super(statusResolver);
	}
	
	protected boolean implementable(RootNode node){
		return super.implementable(node);
	}
	
	protected boolean implementable(ClassNode node){
		return super.implementable(node);
	}
	
	protected boolean implementable(MethodNode node){
		return super.implementable(node);
	}
	
	protected boolean implementable(CategoryNode node){
		return super.implementable(node);
	}
	protected boolean implementable(PartitionNode node){
		return super.implementable(node);
	}
	
	protected boolean implementable(TestCaseNode node){
		return super.implementable(node);
	}

	
//	private IImplementationStatusResolver fImplementationStatusResolver;
//	private class ImplementableVisitor implements IModelVisitor{
//
//		@Override
//		public Object visit(RootNode node) throws Exception {
//			return true;
//		}
//
//		@Override
//		public Object visit(ClassNode node) throws Exception {
//			return true;
//		}
//
//		@Override
//		public Object visit(MethodNode node) throws Exception {
//			return true;
//		}
//
//		@Override
//		public Object visit(CategoryNode node) throws Exception {
//			if(JavaUtils.isUserType(node.getType())){
//				return true;
//			}
//			return false;
//		}
//
//		@Override
//		public Object visit(TestCaseNode node) throws Exception {
//			return true;
//		}
//
//		@Override
//		public Object visit(ConstraintNode node) throws Exception {
//			return false;
//		}
//
//		@Override
//		public Object visit(PartitionNode node) throws Exception {
//			if(node.getCategory() != null && JavaUtils.isUserType(node.getCategory().getType())){
//				return true;
//			}
//			return false;
//		}
//	}
//
//	public JavaModelImplementer(IImplementationStatusResolver resolver){
//		fImplementationStatusResolver = resolver;
//	}
//	
//	public boolean implementable(GenericNode node){
//		try{
//			return (boolean)node.accept(new ImplementableVisitor());
//		}catch (Exception e){}
//		return false;
//	}
//	
//	public boolean implementable(Class<? extends GenericNode> type){
//		if(type.equals(RootNode.class) ||
//			(type.equals(ClassNode.class))||
//			(type.equals(MethodNode.class))||
//			(type.equals(CategoryNode.class))||
//			(type.equals(TestCaseNode.class))||
//			(type.equals(PartitionNode.class))
//		){
//			return true;
//		}
//		return false;
//			
//	}
//
//	public EImplementationStatus getImplementationStatus(GenericNode node){
//		return fImplementationStatusResolver.getImplementationStatus(node);
//	}
//	
//	protected abstract void implement(RootNode node);
//	protected abstract void implement(ClassNode node);
//	protected abstract void implement(MethodNode node);
//	protected abstract void implement(CategoryNode node);
//	protected abstract void implement(PartitionNode node);
//	protected abstract void implement(TestCaseNode node);
}