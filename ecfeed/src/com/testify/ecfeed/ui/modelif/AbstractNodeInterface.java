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

package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
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
import com.testify.ecfeed.core.adapter.operations.BulkOperation;
import com.testify.ecfeed.core.adapter.operations.FactoryRemoveOperation;
import com.testify.ecfeed.core.adapter.operations.FactoryRenameOperation;
import com.testify.ecfeed.core.adapter.operations.FactoryShiftOperation;
import com.testify.ecfeed.core.adapter.operations.GenericAddChildrenOperation;
import com.testify.ecfeed.core.adapter.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.core.adapter.operations.GenericSetCommentsOperation;
import com.testify.ecfeed.core.adapter.operations.GenericShiftOperation;
import com.testify.ecfeed.core.adapter.operations.OperationNames;
import com.testify.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.JavaDocSupport;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.TextAreaDialog;

public class AbstractNodeInterface extends OperationExecuter{

	private IFileInfoProvider fFileInfoProvider;
	private AbstractNode fTarget;
	private EclipseImplementationStatusResolver fStatusResolver;
	private ITypeAdapterProvider fAdapterProvider;

	private class RenameParameterProblemTitleProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Messages.DIALOG_RENAME_MODEL_PROBLEM_TITLE;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Messages.DIALOG_RENAME_CONSTRAINT_PROBLEM_TITLE;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return Messages.DIALOG_RENAME_CHOICE_PROBLEM_TITLE;
		}

	}

	public AbstractNodeInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext);
		fFileInfoProvider = fileInfoProvider;
		fStatusResolver = new EclipseImplementationStatusResolver(fileInfoProvider);
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	public void setTarget(AbstractNode target){
		fTarget = target;
	}

	public EImplementationStatus getImplementationStatus(AbstractNode node){
		return fStatusResolver.getImplementationStatus(node);
	}

	public EImplementationStatus getImplementationStatus(){
		return getImplementationStatus(fTarget);
	}

	static public boolean validateName(String name){
		return true;
	}

	public String getName(){
		return fTarget.getName();
	}

	protected IFileInfoProvider getFileInfoProvider() {
		return fFileInfoProvider;
	}

	public boolean setName(String newName){
		if(newName.equals(getName())){
			return false;
		}
		String problemTitle = "";
		try{
			problemTitle = (String)fTarget.accept(new RenameParameterProblemTitleProvider());
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		return execute(FactoryRenameOperation.getRenameOperation(fTarget, newName), problemTitle);
	}

	public boolean editComments() {
		TextAreaDialog dialog = new TextAreaDialog(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_EDIT_COMMENTS_TITLE, Messages.DIALOG_EDIT_COMMENTS_MESSAGE, getComments());
		if(dialog.open() == IDialogConstants.OK_ID){
			return setComments(dialog.getText());
		}
		return false;
	}

	public boolean setComments(String comments){
		if(comments.equals(getComments())){
			return false;
		}
		return execute(new GenericSetCommentsOperation(fTarget, comments), Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
	}

	public String getComments() {
		if(fTarget != null && fTarget.getDescription() != null){
			return fTarget.getDescription();
		}
		return "";
	}

	public boolean remove(){
		return execute(FactoryRemoveOperation.getRemoveOperation(fTarget, fAdapterProvider, true), Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
	}

	public boolean removeChildren(Collection<? extends AbstractNode> children, String message){
		if(children == null || children.size() == 0) { 
			return false;
		}

		for(AbstractNode node : children){
			if(node.getParent() != fTarget) { 
				return false;
			}
		}
		return execute(new GenericRemoveNodesOperation(children, fAdapterProvider, true), message);
	}

	public String canAddChildren(Collection<? extends AbstractNode> children) {
		return null; // error message if can not
	}

	public boolean addChildren(Collection<? extends AbstractNode> children){
		IModelOperation operation = new GenericAddChildrenOperation(fTarget, children, fAdapterProvider, true);
		return execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}

	public boolean addChildren(Collection<? extends AbstractNode> children, int index){
		IModelOperation operation;
		if(index == -1){
			operation = new GenericAddChildrenOperation(fTarget, children, fAdapterProvider, true);
		}
		else{
			operation = new GenericAddChildrenOperation(fTarget, children, index, fAdapterProvider, true);
		}
		return execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}

	public boolean pasteEnabled(Collection<? extends AbstractNode> pasted){
		return pasteEnabled(pasted, -1);
	}

	public boolean pasteEnabled(Collection<? extends AbstractNode> pasted, int index){
		GenericAddChildrenOperation operation;
		if(index == -1){
			operation = new GenericAddChildrenOperation(fTarget, pasted, fAdapterProvider, true);
		}else{
			operation = new GenericAddChildrenOperation(fTarget, pasted, index, fAdapterProvider, true);
		}
		return operation.enabled();
	}

	public boolean moveUpDown(boolean up) {
		try{
			GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(Arrays.asList(new AbstractNode[]{fTarget}), up);
			if(operation.getShift() > 0){
				return executeMoveOperation(operation);
			}
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation) {
		return execute(moveOperation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
	}

	protected ITypeAdapterProvider getAdapterProvider(){
		return fAdapterProvider;
	}

	protected AbstractNode getTarget(){
		return fTarget;
	}

	public boolean goToImplementationEnabled(){
		return getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED;
	}

	public void goToImplementation(){

	}

	public boolean importJavadocComments() {
		String comments = JavaDocSupport.importJavadoc(getTarget());
		if(comments != null){
			return setComments(comments);
		}
		return false;
	}

	public void exportCommentsToJavadoc(String comments) {
		JavaDocSupport.exportJavadoc(getTarget());
	}

	public boolean importAllJavadocComments() {
		List<IModelOperation> operations = getImportAllJavadocCommentsOperations();
		if(operations.size() > 0){
			IModelOperation operation = new BulkOperation(OperationNames.SET_COMMENTS, operations, false);
			return execute(operation, Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean exportAllComments() {
		exportCommentsToJavadoc(getComments());
		for(AbstractNode child : getTarget().getChildren()){
			AbstractNodeInterface nodeIf = 
					NodeInterfaceFactory.getNodeInterface(child, getUpdateContext(), fFileInfoProvider);
			nodeIf.exportAllComments();
		}
		return true;
	}

	protected List<IModelOperation> getImportAllJavadocCommentsOperations(){
		List<IModelOperation> result = new ArrayList<IModelOperation>();
		String javadoc = JavaDocSupport.importJavadoc(getTarget());
		if(javadoc != null && getComments() != javadoc){
			result.add(new GenericSetCommentsOperation(fTarget, javadoc));
		}
		for(AbstractNode child : getTarget().getChildren()){
			AbstractNodeInterface childIf = 
					NodeInterfaceFactory.getNodeInterface(child, getUpdateContext(), fFileInfoProvider);
			result.addAll(childIf.getImportAllJavadocCommentsOperations());
		}
		return result;
	}

	public boolean commentsImportExportEnabled(){
		return getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED;
	}
}
