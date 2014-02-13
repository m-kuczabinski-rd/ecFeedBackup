/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.modeleditor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.ParameterizedMethod;
import com.testify.ecfeed.ui.common.Messages;

public class ExecuteStaticTestAdapter extends ExecuteTestAdapter {

	public ExecuteStaticTestAdapter(MethodNodeDetailsPage page) {
		super(page);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void widgetSelected(SelectionEvent event){
		Class testClass = loadTestClass();
		Method testMethod = getTestMethod(testClass, getPage().getSelectedMethod());
		if(testMethod == null){
			new MessageDialog(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_MESSAGE(getPage().getSelectedMethod().toString()),
					MessageDialog.ERROR, 
					new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID).open();
		}
		Collection<TestCaseNode> selectedTestCases = getSelectedTestCases();
		ParameterizedMethod frameworkMethod = new ParameterizedMethod(testMethod, selectedTestCases);
		try {
			frameworkMethod.invokeExplosively(testClass.newInstance(), new Object[]{});
		} catch (Throwable e) {
			new MessageDialog(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_MESSAGE(getPage().getSelectedMethod().toString(), e.getMessage()),
					MessageDialog.ERROR, 
					new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID).open();
		} 
	}

	protected Collection<TestCaseNode> getSelectedTestCases() {
		Collection<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
		for(Object element : getPage().getTestCaseViewer().getCheckedElements()){
			if(element instanceof TestCaseNode){
				testCases.add((TestCaseNode)element);
			}
		}
		return testCases;
	}


}
