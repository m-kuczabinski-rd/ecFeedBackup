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
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.adapter.operations.AbstractParameterOperationSetType;
import com.testify.ecfeed.core.adapter.operations.BulkOperation;
import com.testify.ecfeed.core.adapter.operations.ParameterSetTypeCommentsOperation;
import com.testify.ecfeed.core.adapter.operations.ReplaceChoicesOperation;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.JavaDocSupport;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.ui.dialogs.TextAreaDialog;
import com.testify.ecfeed.ui.dialogs.UserTypeSelectionDialog;

public abstract class AbstractParameterInterface extends ChoicesParentInterface {
	
	IFileInfoProvider fFileInfoProvider;

	public AbstractParameterInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
	}

	public String getType() {
		return getTarget().getType();
	}

	public String getTypeComments() {
		return getTarget().getTypeComments() != null ? getTarget().getTypeComments() : "";
	}

	public boolean editTypeComments() {
		TextAreaDialog dialog = new TextAreaDialog(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_EDIT_COMMENTS_TITLE, Messages.DIALOG_EDIT_COMMENTS_MESSAGE, getTypeComments());
		if(dialog.open() == IDialogConstants.OK_ID){
			return execute(new ParameterSetTypeCommentsOperation(getTarget(), dialog.getText()), Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean setTypeComments(String comments){
		if(comments != null && comments.equals(getTarget().getTypeComments()) == false){
			return execute(new ParameterSetTypeCommentsOperation(getTarget(), comments), Messages.DIALOG_EDIT_COMMENTS_TITLE);
		}
		return false;
	}

	public boolean importType(){
		TestClassSelectionDialog dialog = new UserTypeSelectionDialog(Display.getDefault().getActiveShell());

		if (dialog.open() == IDialogConstants.OK_ID) {
			IType selectedEnum = (IType)dialog.getFirstResult();
			String newType = selectedEnum.getFullyQualifiedName().replace('$', '.');
			IModelOperation operation = setTypeOperation(newType);
			return execute(operation, Messages.DIALOG_SET_PARAMETER_TYPE_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean resetChoicesToDefault(){
		String type = getTarget().getType();
		List<ChoiceNode> defaultChoices = new EclipseModelBuilder().defaultChoices(type);
		IModelOperation operation = new ReplaceChoicesOperation(getTarget(), defaultChoices, getAdapterProvider());
		return execute(operation, Messages.DIALOG_RESET_CHOICES_PROBLEM_TITLE);
	}

	public static boolean hasLimitedValuesSet(String type) {
		return !isPrimitive(type) || isBoolean(type);
	}

	public static boolean hasLimitedValuesSet(AbstractParameterNode parameter) {
		return hasLimitedValuesSet(parameter.getType());
	}

	public static boolean isPrimitive(String type) {
		return Arrays.asList(JavaUtils.supportedPrimitiveTypes()).contains(type);
	}

	public static boolean isUserType(String type) {
		return !isPrimitive(type);
	}

	public static boolean isBoolean(String type){
		return type.equals(JavaUtils.getBooleanTypeName());
	}

	public static List<String> getSpecialValues(String type) {
		return new EclipseModelBuilder().getSpecialValues(type);
	}

	public static String[] supportedPrimitiveTypes() {
		return JavaUtils.supportedPrimitiveTypes();
	}

	@Override
	public boolean goToImplementationEnabled(){
		if(JavaUtils.isUserType(getTarget().getType()) == false){
			return false;
		}
		return super.goToImplementationEnabled();
	}

	@Override
	public void goToImplementation(){
		if(JavaUtils.isUserType(getTarget().getType())){
			IType type = JavaModelAnalyser.getIType(getType());
			if(type != null){
				try {
					JavaUI.openInEditor(type);
				} catch (Exception e) {SystemLogger.logCatch(e.getMessage());}
			}
		}
	}

	public boolean setType(String newType) {
		if(newType.equals(getTarget().getType())){
			return false;
		}
		return execute(setTypeOperation(newType), Messages.DIALOG_SET_PARAMETER_TYPE_PROBLEM_TITLE);
	}

	@Override
	protected AbstractParameterNode getTarget(){
		return (AbstractParameterNode)super.getTarget();
	}

	protected IModelOperation setTypeOperation(String type) {
		return new AbstractParameterOperationSetType(getTarget(), type, getAdapterProvider());
	}

	public boolean importTypeJavadocComments() {
		return setTypeComments(JavaDocSupport.importTypeJavadoc(getTarget()));
	}

	public void importFullTypeJavadocComments() {
		IModelOperation operation = new BulkOperation("Import javadoc", getFullTypeImportOperations(), false);
		execute(operation, Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
	}

	protected List<IModelOperation> getFullTypeImportOperations(){
		List<IModelOperation> result = new ArrayList<IModelOperation>();
		String typeJavadoc = JavaDocSupport.importTypeJavadoc(getTarget());
		result.add(new ParameterSetTypeCommentsOperation(getTarget(), typeJavadoc));
		for(ChoiceNode choice : getTarget().getChoices()){
			AbstractNodeInterface nodeIf = 
					NodeInterfaceFactory.getNodeInterface(choice, getUpdateContext(), fFileInfoProvider);
			result.addAll(nodeIf.getImportAllJavadocCommentsOperations());
		}
		return result;
	}

	public void exportTypeJavadocComments() {
		JavaDocSupport.exportTypeJavadoc(getTarget());
	}

	public void exportFullTypeJavadocComments() {
		exportTypeJavadocComments();
		for(ChoiceNode choice : getTarget().getChoices()){
			ChoiceInterface choiceIf = 
					(ChoiceInterface)NodeInterfaceFactory.getNodeInterface(
							choice, getUpdateContext(), fFileInfoProvider);
			choiceIf.exportAllComments();
		}
	}

	@Override
	protected List<IModelOperation> getImportAllJavadocCommentsOperations(){
		List<IModelOperation> result = super.getImportAllJavadocCommentsOperations();
		String typeJavadoc = JavaDocSupport.importTypeJavadoc(getTarget());
		if(typeJavadoc != null && typeJavadoc.equals(getTypeComments()) == false && importTypeCommentsEnabled()){
			result.add(new ParameterSetTypeCommentsOperation(getTarget(), typeJavadoc));
		}
		return result;
	}

	public abstract boolean importTypeCommentsEnabled();
}
