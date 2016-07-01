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

package com.ecfeed.ui.common;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public class TestCasesViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
	public final Object[] EMPTY_ARRAY = new Object[] {};

	private MethodNode fMethodNode;

	public TestCasesViewerContentProvider(){
	}

	public TestCasesViewerContentProvider(MethodNode target){
		fMethodNode = target;
	}

	@Override
	public Object[] getElements(Object inputElement){
		if(inputElement instanceof MethodNode){
			return ((MethodNode)inputElement).getTestSuites().toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement){
		if(parentElement instanceof String){
			Collection<TestCaseNode> testCases = fMethodNode.getTestCases((String)parentElement);
			if(testCases.size() <= Constants.MAX_DISPLAYED_TEST_CASES_PER_SUITE){
				return testCases.toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element){
		if(element instanceof TestCaseNode){
			return ((TestCaseNode)element).getName();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element){
		return getChildren(element).length > 0;
	}

	public void setMethod(MethodNode method){
		fMethodNode = method;
	}
}
