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
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.operations.ClassOperationAddMethod;
import com.testify.ecfeed.adapter.operations.ClassOperationAddMethods;
import com.testify.ecfeed.adapter.operations.ClassOperationRemoveMethod;
import com.testify.ecfeed.adapter.operations.ClassOperationSetAndroidBaseRunner;
import com.testify.ecfeed.adapter.operations.ClassOperationSetRunOnAndroid;
import com.testify.ecfeed.adapter.operations.FactoryRenameOperation;
import com.testify.ecfeed.android.DefaulAndroidRunner;
import com.testify.ecfeed.android.project.AndroidManifestAccessor;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;

public class ClassInterface extends GlobalParametersParentInterface {

	public ClassInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
	}

	public static String getQualifiedName(ClassNode classNode){
		return classNode.getName();
	}

	public static String getQualifiedName(String packageName, String localName){
		return packageName + "." + localName;
	}

	public String getQualifiedName(){
		return getQualifiedName(getTarget());
	}

	public String getLocalName(){
		return getLocalName(getTarget());
	}

	public static String getLocalName(ClassNode classNode){
		return JavaUtils.getLocalName(classNode.getName());
	}

	public static String getPackageName(ClassNode classNode){
		return JavaUtils.getPackageName(classNode.getName());
	}

	public String getPackageName(){
		return getPackageName(getTarget());
	}

	public boolean getRunOnAndroid(){
		return getTarget().getRunOnAndroid();
	}

	public String getAndroidRunner(){
		return getTarget().getAndroidBaseRunner();
	}

	@Override
	public boolean setName(String newName) {
		return setQualifiedName(newName);
	}

	public boolean setQualifiedName(String newName){
		if(newName.equals(getQualifiedName())){
			return false;
		}
		if(getImplementationStatus(getTarget()) != EImplementationStatus.NOT_IMPLEMENTED){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE,
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE) == false){
				return false;
			}
		}
		return execute(FactoryRenameOperation.getRenameOperation(getTarget(), newName), Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE);
	}

	public boolean setLocalName(String newLocalName){
		String newQualifiedName = getPackageName() + "." + newLocalName;
		return setQualifiedName(newQualifiedName);
	}

	public boolean setPackageName(String newPackageName){
		String newQualifiedName = newPackageName + "." + getLocalName();
		return setQualifiedName(newQualifiedName);
	}

	public boolean setRunOnAndroid(boolean runOnAndroid) {
		IModelOperation operation = new ClassOperationSetRunOnAndroid(getTarget(), runOnAndroid);
		return execute(operation, Messages.DIALOG_ANDROID_RUNNER_SET_PROBLEM_TITLE);
	}

	public boolean setAndroidRunner(String androidRunner) {
		IModelOperation operation = new ClassOperationSetAndroidBaseRunner(getTarget(), androidRunner);
		return execute(operation, Messages.DIALOG_ANDROID_RUNNER_SET_PROBLEM_TITLE);
	}

	public MethodNode addNewMethod(){
		return addNewMethod(generateNewMethodName());
	}

	public MethodNode addNewMethod(String name){
		MethodNode method = new MethodNode(name);
		if(addMethod(method)){
			return method;
		}
		return null;
	}

	public boolean addMethods(Collection<MethodNode> methods){
		IModelOperation operation = new ClassOperationAddMethods(getTarget(), methods, getTarget().getMethods().size());
		return execute(operation, Messages.DIALOG_ADD_METHODS_PROBLEM_TITLE);
	}

	public boolean addMethod(MethodNode method){
		IModelOperation operation = new ClassOperationAddMethod(getTarget(), method, getTarget().getMethods().size());
		return execute(operation, Messages.DIALOG_ADD_METHOD_PROBLEM_TITLE);
	}

	public boolean removeMethod(MethodNode method){
		if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_REMOVE_METHOD_TITLE,
				Messages.DIALOG_REMOVE_METHOD_MESSAGE)){
			IModelOperation operation = new ClassOperationRemoveMethod(getTarget(), method);
			return execute(operation, Messages.DIALOG_REMOVE_METHOD_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean removeMethods(Collection<MethodNode> methods){
		if(methods.size() == 0){
			return false;
		}
		if(methods.size() == 1){
			return removeMethod(new ArrayList<MethodNode>(methods).get(0));
		}
		else if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_REMOVE_METHODS_TITLE,
				Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
			return removeChildren(methods, Messages.DIALOG_REMOVE_METHODS_PROBLEM_TITLE);
		}
		return false;
	}

	public static List<MethodNode> getOtherMethods(ClassNode target){
		List<MethodNode> otherMethods = new ArrayList<MethodNode>();
		EclipseModelBuilder builder = new EclipseModelBuilder();
		try{
			ClassNode completeModel = builder.buildClassModel(JavaUtils.getQualifiedName(target), false);
			for(MethodNode method : completeModel.getMethods()){
				if(target.getMethod(method.getName(), method.getParametersTypes()) == null){
					otherMethods.add(method);
				}
			}
		}catch (ModelOperationException e){}
		return otherMethods;
	}


	public List<MethodNode> getOtherMethods(){
		return getOtherMethods(getTarget());
	}

	public void reassignClass() {
		TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());

		if (dialog.open() == IDialogConstants.OK_ID) {
			IType selectedClass = (IType)dialog.getFirstResult();
			String qualifiedName = selectedClass.getFullyQualifiedName();
			setQualifiedName(qualifiedName);
		}
	}

	public List<String> createRunnerList(String projectPath) {

		List<String> runnerList = new AndroidManifestAccessor(projectPath).getRunnerNames();
		addDefaultRunner(runnerList);
		return runnerList;
	}

	private void addDefaultRunner(List<String> runnerList) {

		String runner = DefaulAndroidRunner.getName();

		if (runnerList.contains(runner)) {
			return;
		}

		runnerList.add(0, runner);
	}

	@Override
	public void goToImplementation(){
		IType type = JavaModelAnalyser.getIType(getQualifiedName());
		if(type != null){
			try{
				JavaUI.openInEditor(type);
			}catch(Exception e){}
		}
	}

	@Override
	protected ClassNode getTarget(){
		return (ClassNode)super.getTarget();
	}

	private String generateNewMethodName() {
		String name = Constants.DEFAULT_NEW_METHOD_NAME;
		int i = 0;
		while(getTarget().getMethod(name, new ArrayList<String>()) != null){
			name = Constants.DEFAULT_NEW_METHOD_NAME + i++;
		}
		return name;
	}
}
