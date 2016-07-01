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

package com.ecfeed.ui.editor.actions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;

import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.ChoicesParentInterface;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.GlobalParametersParentInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.RootInterface;

public class AddChildActionProvider {

	private StructuredViewer fViewer;
	private IModelUpdateContext fContext;
	private IFileInfoProvider fFileInfoProvider;

	private void reportExceptionInvalidNodeType() {
		final String MSG = "Invalid type of selected node.";
		ExceptionHelper.reportRuntimeException(MSG);
	}

	private AbstractNode getOneNode(List<AbstractNode> nodes) {
		if(nodes.size() != 1) {
			final String MSG = "Too many nodes selected for action.";
			ExceptionHelper.reportRuntimeException(MSG);
		}
		return nodes.get(0); 
	}

	private void setTargetNode(AbstractNode abstractNode, AbstractNodeInterface abstractNodeInterface) {
		if (abstractNodeInterface == null ) {
			final String MSG = "Invalid parent interface.";
			ExceptionHelper.reportRuntimeException(MSG);
		}
		abstractNodeInterface.setTarget(abstractNode);
	}

	private class AddGlobalParameterAction extends AbstractAddChildAction{
		private GlobalParametersParentInterface fParentIf;

		public AddGlobalParameterAction() {
			super(ADD_GLOBAL_PARAMETER_ACTION_ID, ADD_GLOBAL_PARAMETER_ACTION_NAME, fViewer, fContext);
			fParentIf = new GlobalParametersParentInterface(fContext, fFileInfoProvider);
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof GlobalParametersParentNode)) {
				reportExceptionInvalidNodeType();
			}

			setTargetNode(selectedNode, fParentIf);
			select(fParentIf.addNewParameter());
		}

		@Override
		protected GlobalParametersParentInterface getParentInterface(){
			return fParentIf;
		}

	}

	private class AddClassAction extends AbstractAddChildAction{
		private RootInterface fParentIf;

		public AddClassAction(){
			super(ADD_CLASS_ACTION_ID, ADD_CLASS_ACTION_NAME, fViewer, fContext);
			fParentIf = new RootInterface(fContext, fFileInfoProvider);
		}

		@Override
		protected RootInterface getParentInterface(){
			return fParentIf;
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (selectedNode instanceof RootNode == false) {
				reportExceptionInvalidNodeType();
			}

			setTargetNode(selectedNode, fParentIf);
			select(getParentInterface().addNewClass());
		}
	}

	private class AddMethodAction extends AbstractAddChildAction{
		private ClassInterface fParentIf;

		public AddMethodAction(){
			super(ADD_METHOD_ACTION_ID, ADD_METHOD_ACTION_NAME, fViewer, fContext);
			fParentIf = new ClassInterface(fContext, fFileInfoProvider);
		}

		@Override
		protected ClassInterface getParentInterface(){
			return fParentIf;
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof ClassNode)) {
				reportExceptionInvalidNodeType();
			}

			setTargetNode(selectedNode, fParentIf);
			select(getParentInterface().addNewMethod());
		}
	}

	private abstract class AddMethodChildAction extends AbstractAddChildAction{
		private MethodInterface fParentIf;

		public AddMethodChildAction(String id, String name) {
			super(id, name, fViewer, fContext);
			fParentIf = new MethodInterface(fContext, fFileInfoProvider);
		}

		@Override
		protected MethodInterface getParentInterface(){
			return fParentIf;
		}

		protected void prepareRun() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof MethodNode)) {
				reportExceptionInvalidNodeType();
			}

			setTargetNode(selectedNode, fParentIf);
		}
	}

	private class AddMethodParameterAction extends AddMethodChildAction{
		public AddMethodParameterAction() {
			super(ADD_METHOD_PARAMETER_ACTION_ID, ADD_METHOD_PARAMETER_ACTION_NAME);
		}

		@Override
		public void run() {
			prepareRun();
			select(getParentInterface().addNewParameter());
		}
	}

	private class AddConstraintAction extends AddMethodChildAction{
		public AddConstraintAction() {
			super(ADD_CONSTRAINT_ACTION_ID, ADD_CONSTRAINT_ACTION_NAME);
		}

		@Override
		public void run() {
			prepareRun();
			select(getParentInterface().addNewConstraint());
		}
	}

	private class AddTestCaseAction extends AddMethodChildAction{
		public AddTestCaseAction() {
			super(ADD_TEST_CASE_ACTION_ID, ADD_TEST_CASE_ACTION_NAME);
		}

		@Override
		public void run() {
			prepareRun();
			select(getParentInterface().addTestCase());
		}
	}

	private class AddTestSuiteAction extends AddMethodChildAction{
		public AddTestSuiteAction() {
			super(ADD_TEST_SUITE_ACTION_ID, ADD_TEST_SUITE_ACTION_NAME);
		}

		@Override
		public void run() {
			prepareRun();
			getParentInterface().generateTestSuite();
		}
	}

	private class AddChoiceAction extends AbstractAddChildAction{
		private ChoicesParentInterface fParentIf;

		private class EnableVisitor implements IParameterVisitor{

			@Override
			public Object visit(MethodParameterNode node) throws Exception {
				return (node.isLinked() == false) && (node.isExpected() == false || JavaUtils.isUserType(node.getType()));
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				return true;
			}

		}

		public AddChoiceAction(IFileInfoProvider fileInfoProvider){
			super(ADD_PARTITION_ACTION_ID, ADD_PARTITION_ACTION_NAME, fViewer, fContext);
			fParentIf = new ChoicesParentInterface(fContext, fileInfoProvider);
		}

		@Override
		protected ChoicesParentInterface getParentInterface() {
			return fParentIf;
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof ChoicesParentNode)) {
				reportExceptionInvalidNodeType();
			}

			setTargetNode(selectedNode, fParentIf);
			select(fParentIf.addNewChoice());
		}

		@Override
		public boolean isEnabled(){
			if(super.isEnabled() == false) 
				return false;

			ChoicesParentNode target = (ChoicesParentNode)getSelectedNodes().get(0);

			AbstractParameterNode parameter = target.getParameter();
			try {
				return (boolean)parameter.accept(new EnableVisitor());
			} catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
			}
			return false;
		}
	}

	private class AddNewChilActionProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddClassAction(),
					new AddGlobalParameterAction()
			});
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodAction(),
					new AddGlobalParameterAction()
			});
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodParameterAction(),
					new AddConstraintAction(),
					new AddTestCaseAction(),
					new AddTestSuiteAction()
			});
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddChoiceAction(fFileInfoProvider)
			});
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddChoiceAction(fFileInfoProvider)
			});
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{});
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{});
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddChoiceAction(fFileInfoProvider)
			});
		}
	}

	public AddChildActionProvider(
			StructuredViewer viewer, 
			IModelUpdateContext context, 
			IFileInfoProvider fileInfoProvider) {
		fFileInfoProvider = fileInfoProvider;
		fContext = context;
		fViewer = viewer;
	}


	@SuppressWarnings("unchecked")
	public List<AbstractAddChildAction> getPossibleActions(AbstractNode parent){
		try {
			return (List<AbstractAddChildAction>)parent.accept(new AddNewChilActionProvider());
		} catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return null;
	}

	private class GetMainInsertActionProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return new AddClassAction();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new AddMethodAction();
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new AddMethodParameterAction();
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return new AddChoiceAction(fFileInfoProvider);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new AddChoiceAction(fFileInfoProvider);
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
		public Object visit(ChoiceNode node) throws Exception {
			return new AddChoiceAction(fFileInfoProvider);
		}
	}

	public AbstractAddChildAction getMainInsertAction(AbstractNode parent) {
		try {
			return (AbstractAddChildAction)parent.accept(new GetMainInsertActionProvider());
		} catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return null;
	}

}
