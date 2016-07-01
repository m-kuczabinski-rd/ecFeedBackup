/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.core.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;

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
					ModelOperationException.report(Messages.CLASS_NAME_CONTAINS_KEYWORD_PROBLEM);
				}
			}
			if(getTarget().getSibling(getNewName()) != null){
				ModelOperationException.report(Messages.CLASS_NAME_DUPLICATE_PROBLEM);
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
				ModelOperationException.report(JavaUtils.consolidate(problems));
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
			GlobalParameterNode target = (GlobalParameterNode) getTarget();
			if(JavaUtils.isJavaKeyword(newName)){
				ModelOperationException.report(Messages.CATEGORY_NAME_REGEX_PROBLEM);
			}
			if(target.getParametersParent().getParameter(newName) != null){
				ModelOperationException.report(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
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
				ModelOperationException.report(Messages.CATEGORY_NAME_REGEX_PROBLEM);
			}
			if(target.getMethod().getParameter(newName) != null){
				ModelOperationException.report(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
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
				ModelOperationException.report(Messages.PARTITION_NAME_NOT_UNIQUE_PROBLEM);
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
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		return null;
	}
}
