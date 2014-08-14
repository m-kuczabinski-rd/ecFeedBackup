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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;

public class TestCasesViewerLabelProvider extends LabelProvider implements IColorProvider {
	private MethodNode fMethod;
	private ColorManager fColorManager;
	private GenericNodeInterface fNodeIf;
	private Map<String, Integer> fExecutableTestSuites;
	private Map<TestCaseNode, Boolean> fTestCasesStatusMap;
	
	public TestCasesViewerLabelProvider(MethodNode method){
		fMethod = method;
		fColorManager = new ColorManager();
		fNodeIf = new GenericNodeInterface(null);
		fExecutableTestSuites = new HashMap<String, Integer>();
		fTestCasesStatusMap = new HashMap<TestCaseNode, Boolean>();
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String suiteName = (String)element;
			int executable = 0;
			if(fExecutableTestSuites.containsKey(suiteName)){
				executable = fExecutableTestSuites.get(suiteName);
			}
			Collection<TestCaseNode> testCases = fMethod.getTestCases(suiteName);
			String plural = testCases.size() != 1 ? "s" : "";
			return suiteName + " [" + testCases.size() + " test case" + plural + ", " + executable + " executable]";   
		}
		else if(element instanceof TestCaseNode){
			return fMethod.getName() + "(" + ((TestCaseNode)element).testDataString() + ")";
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		Color executableColor = fColorManager.getColor(ColorConstants.TEST_CASE_EXECUTABLE);
		if (element instanceof TestCaseNode) {
			TestCaseNode tc = (TestCaseNode)element;
			if(fTestCasesStatusMap.containsKey(tc) && fTestCasesStatusMap.get(tc) == true){
				return executableColor;
			}
			return null;
		}
		if (element instanceof String) {
			String name = (String)element;
			if(fExecutableTestSuites.containsKey(name)){
				boolean suiteExecutable = (fExecutableTestSuites.get(name) == fMethod.getTestCases(name).size());
				return suiteExecutable ? executableColor : null;
			}
			return null;
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	public void setMethod(MethodNode method){
		fMethod = method;
		refresh();
	}

	public void refresh(){
		updateExecutableTable();
	}

	private void updateExecutableTable() {
		Map<PartitionNode, ImplementationStatus> partitionStatusMap = new HashMap<PartitionNode, ImplementationStatus>();
		fExecutableTestSuites.clear();
		fTestCasesStatusMap.clear();
		for(String testSuite : fMethod.getTestSuites()){
			fExecutableTestSuites.put(testSuite, 0);
		}
		if(fNodeIf.implementationStatus(fMethod) != ImplementationStatus.NOT_IMPLEMENTED){
			for(TestCaseNode tc : fMethod.getTestCases()){
				boolean executable = true;
				String name = tc.getName();
				if(fExecutableTestSuites.containsKey(name) == false){
					fExecutableTestSuites.put(name, 0);
				}
				for(PartitionNode p : tc.getTestData()){
					ImplementationStatus status = partitionStatusMap.get(p);
					if(status == null){
						status = fNodeIf.implementationStatus(p);
						partitionStatusMap.put(p, status);
					}
					if(status != ImplementationStatus.IMPLEMENTED){
						executable = false;
						break;
					}
				}
				if(executable){
					int current = fExecutableTestSuites.get(name);
					fExecutableTestSuites.put(name, current + 1);
				}
				fTestCasesStatusMap.put(tc, executable);
			}
		}
	}
}
