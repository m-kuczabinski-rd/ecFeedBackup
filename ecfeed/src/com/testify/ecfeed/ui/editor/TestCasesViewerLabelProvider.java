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
import org.eclipse.swt.graphics.Color;
import org.eclipse.jface.viewers.IColorProvider;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.utils.ModelUtils;
import com.testify.ecfeed.utils.Constants;

public class TestCasesViewerLabelProvider extends LabelProvider implements IColorProvider {
	private MethodNode fMethod;
	private ColorManager fColorManager;
	
	public TestCasesViewerLabelProvider(MethodNode method){
		fMethod = method;
		fColorManager = new ColorManager();
	}
	
	public void setMethod(MethodNode method){
		fMethod = method;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			int testCasesCount = fMethod.getTestCases((String) element).size();
			
			if(testCasesCount > Constants.MAX_DISPLAYED_CHILDREN_PER_NODE*2){
				return (String) element + " [" + testCasesCount + " test cases]";
			}			

			int executableCount = 0;
			for (TestCaseNode testCase : fMethod.getTestCases((String) element)) {
				if (ModelUtils.isTestCaseImplemented(testCase) && ModelUtils.methodDefinitionImplemented(fMethod)) {
					++executableCount;
				}
			}
			return (String) element +
					" [" + testCasesCount + " test case" + (testCasesCount == 1 ? "" : "s") +
					", " + executableCount + " executable" + "]";
		} else if (element instanceof TestCaseNode) {
			return fMethod.getName() + "(" + ((TestCaseNode) element).testDataString() + ")";
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof TestCaseNode) {
			if (ModelUtils.isTestCaseImplemented((TestCaseNode)element) && ModelUtils.methodDefinitionImplemented(fMethod)) {
				return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
		} else if (element instanceof String) {
			if (ModelUtils.isTestSuiteImplemented(fMethod, (String)element) && ModelUtils.methodDefinitionImplemented(fMethod)) {
				return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}
}
