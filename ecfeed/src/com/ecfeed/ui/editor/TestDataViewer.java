/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.ui.common.ColorConstants;
import com.ecfeed.ui.common.ColorManager;
import com.ecfeed.ui.common.ITestDataEditorListener;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.TestDataValueEditingSupport;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.TestCaseInterface;

public class TestDataViewer extends TableViewerSection implements ITestDataEditorListener{

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private IFileInfoProvider fFileInfoProvider;
	private TestCaseInterface fTestCaseIf;
	private TestDataValueEditingSupport fValueEditingSupport;

	public TestDataViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);
		fFileInfoProvider = fileInfoProvider;
		getTestCaseInterface();
		getSection().setText("Test data");
	}

	@Override
	protected void createTableColumns() {
		addColumn("Parameter", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				ChoiceNode testValue = (ChoiceNode)element;
				AbstractParameterNode parent = testValue.getParameter();
				return parent.toString();
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});

		TableViewerColumn valueColumn = addColumn("Value", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				ChoiceNode testValue = (ChoiceNode)element;
				if(fTestCaseIf.isExpected(testValue)){
					return testValue.getValueString();
				}
				return testValue.toString();
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});

		fValueEditingSupport = new TestDataValueEditingSupport(null, getTableViewer(), this);
		valueColumn.setEditingSupport(fValueEditingSupport);
	}

	protected TestCaseInterface getTestCaseInterface() {
		if(fTestCaseIf == null){
			fTestCaseIf = new TestCaseInterface(this, fFileInfoProvider);
		}
		return fTestCaseIf;
	}

	public void setInput(TestCaseNode testCase){
		List<ChoiceNode> testData = testCase.getTestData();
		fValueEditingSupport.setMethod(testCase.getMethod());
		fTestCaseIf.setTarget(testCase);
		//target and data support must be updated prior to calling super
		super.setInput(testData);
	}

	@Override
	public void testDataChanged(int index, ChoiceNode value) {
		fTestCaseIf.updateTestData(index, value);
	}

	@Override
	protected int viewerStyle(){
		return VIEWER_STYLE;
	}

	private Color getColor(Object element) {
		if (!(element instanceof ChoiceNode)) {
			return null;
		}
		if (!fFileInfoProvider.isProjectAvailable()) {
			return null;
		}		
		ChoiceNode choice = (ChoiceNode)element;
		if(fTestCaseIf.getImplementationStatus(choice) == EImplementationStatus.IMPLEMENTED){
			return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
		}
		return null;
	}
}
