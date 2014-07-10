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
import java.util.HashSet;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.utils.ModelUtils;

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
			Collection<TestCaseNode> testSuite = fMethod.getTestCases((String)element);
			
			int testCasesCount = testSuite.size();		
			
			int executableCount = testCasesCount;
			
			HashSet<PartitionNode> unimplemented = new HashSet<>();
			for(CategoryNode category: fMethod.getCategories(false)){
				for(PartitionNode partition: category.getLeafPartitions()){
					if(!ModelUtils.isPartitionImplemented(partition)){
						unimplemented.add(partition);
					}
				}
			}
			// if all partitions are implemented - no unimplemented testcases possible
			if(unimplemented.isEmpty()){
				return (String) element +
				" [" + testCasesCount + " test case" + (testCasesCount == 1 ? "" : "s") +
				", " + testCasesCount + " executable" + "]";
			}
			// count unimplemented
			for (TestCaseNode testCase : testSuite) {
				for(PartitionNode partition : testCase.getTestData()){
					if(unimplemented.contains(partition)){
						executableCount--;
						break;
					}
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
