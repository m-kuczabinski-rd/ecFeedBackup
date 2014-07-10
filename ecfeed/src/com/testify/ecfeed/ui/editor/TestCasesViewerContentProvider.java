/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.utils.Constants;

public class TestCasesViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
	public final Object[] EMPTY_ARRAY = new Object[] {};
	MethodNode fMethod;

	public TestCasesViewerContentProvider(MethodNode method){
		fMethod = method;
	}
	
	public void setMethod(MethodNode method){
		fMethod = method;
	}

	@Override
	public Object[] getElements(Object inputElement){
		if(fMethod!= null && inputElement instanceof MethodNode){
			return ((MethodNode)inputElement).getTestSuites().toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement){
		if(fMethod != null && parentElement instanceof String){
			Collection<TestCaseNode> testCases = fMethod.getTestCases((String)parentElement);
			if(testCases.size() <= Constants.MAX_DISPLAYED_TEST_CASES_PER_SUITE){
				return testCases.toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element){
		if(fMethod!= null && element instanceof TestCaseNode){
			return ((TestCaseNode)element).getName();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element){
		return getChildren(element).length > 0;
	}
}
