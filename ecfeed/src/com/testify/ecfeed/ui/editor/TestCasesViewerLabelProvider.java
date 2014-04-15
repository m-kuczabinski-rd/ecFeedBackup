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

import org.eclipse.jface.viewers.LabelProvider;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class TestCasesViewerLabelProvider extends LabelProvider {
	MethodNode fMethod;
	
	public TestCasesViewerLabelProvider(MethodNode method){
		fMethod = method;
	}
	
	public void setMethod(MethodNode method){
		fMethod = method;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			int testCasesCount = fMethod.getTestCases((String) element).size();
			return (String) element + " [" + testCasesCount + " test case" + (testCasesCount == 1 ? "" : "s") + "]";
		} else if (element instanceof TestCaseNode) {
			return fMethod.getName() + "(" + ((TestCaseNode) element).testDataString() + ")";
		}
		return null;
	}
}
