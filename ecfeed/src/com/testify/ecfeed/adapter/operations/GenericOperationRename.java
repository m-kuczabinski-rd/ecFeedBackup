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

package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.Constants;
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
import com.testify.ecfeed.utils.SystemLogger;

public class GenericOperationRename extends AbstractModelOperation {

	private AbstractNode fTarget;
	private String fNewName;
	private String fOriginalName;
	private String fNameRegex;

	private class RegexProblemMessageProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Messages.MODEL_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Messages.CLASS_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Messages.METHOD_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return Messages.CATEGORY_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return Messages.CATEGORY_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Messages.TEST_CASE_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Messages.CONSTRAINT_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return Messages.PARTITION_NAME_REGEX_PROBLEM;
		}
	}

	private class NameRegexProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Constants.REGEX_ROOT_NODE_NAME;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Constants.REGEX_CLASS_NODE_NAME;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Constants.REGEX_METHOD_NODE_NAME;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return Constants.REGEX_CATEGORY_NODE_NAME;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return Constants.REGEX_CATEGORY_NODE_NAME;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Constants.REGEX_TEST_CASE_NODE_NAME;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Constants.REGEX_CONSTRAINT_NODE_NAME;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return Constants.REGEX_PARTITION_NODE_NAME;
		}
	}

	public GenericOperationRename(AbstractNode target, String newName){
		super(OperationNames.RENAME);
		fTarget = target;
		fNewName = newName;
		fOriginalName = target.getName();
		fNameRegex = getNameRegex(target);
	}

	@Override
	public void execute() throws ModelOperationException{
		verifyNameWithRegex();
		verifyNewName(fNewName);
		fTarget.setName(fNewName);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericOperationRename(getTarget(), getOriginalName());
	}

	protected AbstractNode getTarget(){
		return fTarget;
	}

	protected String getOriginalName(){
		return fOriginalName;
	}

	protected String getNewName(){
		return fNewName;
	}

	protected void verifyNewName(String newName) throws ModelOperationException{
	}

	protected void verifyNameWithRegex() throws ModelOperationException{
		if(fNewName.matches(fNameRegex) == false){
			throw new ModelOperationException(getRegexProblemMessage());
		}
	}

	private String getNameRegex(AbstractNode target) {
		try{
			return (String)fTarget.accept(new NameRegexProvider());
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		return "*";
	}

	private String getRegexProblemMessage(){
		try{
			return (String)fTarget.accept(new RegexProblemMessageProvider());
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		return "";
	}
}
