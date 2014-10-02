package com.testify.ecfeed.modeladp.operations;

import java.util.ArrayList;
import java.util.List;

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
import com.testify.ecfeed.modeladp.java.JavaUtils;

public class FactoryRenameOperation {

	private static class ClassOperationRename extends GenericOperationRename {

		public ClassOperationRename(GenericNode target, String newName) {
			super(target, newName);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ClassOperationRename(getTarget(), getOriginalName());
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			for(String token : getNewName().split("\\.")){
				if(JavaUtils.isJavaKeyword(token)){
					throw new ModelOperationException(Messages.CLASS_NAME_CONTAINS_KEYWORD_PROBLEM);
				}
			}
			if(getTarget().getSibling(getNewName()) != null){
				throw new ModelOperationException(Messages.CLASS_NAME_DUPLICATE_PROBLEM);
			}
		}
	}
	
	private static class MethodOperationRename extends GenericOperationRename {
		
		public MethodOperationRename(MethodNode target, String newName){
			super(target, newName);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationRename((MethodNode)getTarget(), getOriginalName());
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			List<String> problems = new ArrayList<String>();
			MethodNode target = (MethodNode)getTarget();
			if(JavaUtils.validateNewMethodSignature(target.getClassNode(), getNewName(), target.getCategoriesTypes(), problems) == false){
				throw new ModelOperationException(JavaUtils.consolidate(problems));
			}
		}
	}

	private static class CategoryOperationRename extends GenericOperationRename {

		public CategoryOperationRename(GenericNode target, String newName) {
			super(target, newName);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new CategoryOperationRename(getTarget(), getOriginalName());
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			CategoryNode target = (CategoryNode)getTarget();
			if(JavaUtils.isJavaKeyword(newName)){
				throw new ModelOperationException(Messages.CATEGORY_NAME_REGEX_PROBLEM);
			}
			if(target.getMethod().getCategory(newName) != null){
				throw new ModelOperationException(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
			}
		}
	}
	
	private static class PartitionOperationRename extends GenericOperationRename {

		public PartitionOperationRename(PartitionNode target, String newName){
			super(target, newName);
		}
		
		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationRename((PartitionNode)getTarget(), getOriginalName());
		}
		
		@Override
		protected void verifyNewName(String newName)throws ModelOperationException{
			if(getTarget().getSibling(getNewName()) != null){
				throw new ModelOperationException(Messages.PARTITION_NAME_NOT_UNIQUE_PROBLEM);
			}
		}
	}
	
	private static class RenameOperationProvider implements IModelVisitor{

		private String fNewName;

		public RenameOperationProvider(String newName) {
			fNewName = newName;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return new GenericOperationRename(node, fNewName);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new ClassOperationRename(node, fNewName);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new MethodOperationRename(node, fNewName);
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return new CategoryOperationRename(node, fNewName);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new GenericOperationRename(node, fNewName);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return new GenericOperationRename(node, fNewName);
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			return new PartitionOperationRename(node, fNewName);
		}
	}
	
	public static IModelOperation getRenameOperation(GenericNode target, String newName){
		try{
			return (IModelOperation)target.accept(new RenameOperationProvider(newName));
		}catch(Exception e){}
		return null;
	}
}
