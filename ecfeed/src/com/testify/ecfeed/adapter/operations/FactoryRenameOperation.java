package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class FactoryRenameOperation {

	private static class ClassOperationRename extends GenericOperationRename {

		public ClassOperationRename(AbstractNode target, String newName) {
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
			if(JavaUtils.validateNewMethodSignature(target.getClassNode(), getNewName(), target.getParametersTypes(), problems) == false){
				throw new ModelOperationException(JavaUtils.consolidate(problems));
			}
		}
	}

	private static class GlobalParameterOperationRename extends GenericOperationRename {

		public GlobalParameterOperationRename(AbstractNode target, String newName) {
			super(target, newName);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GlobalParameterOperationRename(getTarget(), getOriginalName());
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			if(JavaUtils.isJavaKeyword(newName)){
				throw new ModelOperationException(Messages.CATEGORY_NAME_REGEX_PROBLEM);
			}
		}
	}

	private static class MethodParameterOperationRename extends GenericOperationRename {

		public MethodParameterOperationRename(AbstractNode target, String newName) {
			super(target, newName);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodParameterOperationRename(getTarget(), getOriginalName());
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			MethodParameterNode target = (MethodParameterNode)getTarget();
			if(JavaUtils.isJavaKeyword(newName)){
				throw new ModelOperationException(Messages.CATEGORY_NAME_REGEX_PROBLEM);
			}
			if(target.getMethod().getParameter(newName) != null){
				throw new ModelOperationException(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class ChoiceOperationRename extends GenericOperationRename {

		public ChoiceOperationRename(ChoiceNode target, String newName){
			super(target, newName);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ChoiceOperationRename((ChoiceNode)getTarget(), getOriginalName());
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
		public Object visit(MethodParameterNode node) throws Exception {
			return new MethodParameterOperationRename(node, fNewName);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new GlobalParameterOperationRename(node, fNewName);
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
		public Object visit(ChoiceNode node) throws Exception {
			return new ChoiceOperationRename(node, fNewName);
		}
	}

	public static IModelOperation getRenameOperation(AbstractNode target, String newName){
		try{
			return (IModelOperation)target.accept(new RenameOperationProvider(newName));
		}catch(Exception e){}
		return null;
	}
}
