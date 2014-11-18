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

package com.testify.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.ITestDataEditorListener;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.common.TestDataValueEditingSupport;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.TestCaseInterface;

public class TestDataViewer extends TableViewerSection implements ITestDataEditorListener{

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private TestCaseInterface fTestCaseIf;

	public TestDataViewer(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, STYLE);
		fTestCaseIf = new TestCaseInterface(this);
		getSection().setText("Test data");
	}

	@Override
	protected void createTableColumns() {
		addColumn("Parameter", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				ChoiceNode testValue = (ChoiceNode)element;
				ParameterNode parent = testValue.getParameter();
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
				if(testValue.getParameter().isExpected()){
					return testValue.getValueString();
				}
				return testValue.toString();
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});

		valueColumn.setEditingSupport(new TestDataValueEditingSupport(getTableViewer(), null, this));
	}

	public void setInput(TestCaseNode testCase){
		List<ChoiceNode> testData = testCase.getTestData();
		super.setInput(testData);
		fTestCaseIf.setTarget(testCase);
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
		if(element instanceof ChoiceNode){
			ChoiceNode choice = (ChoiceNode)element;
			if(fTestCaseIf.getImplementationStatus(choice) == EImplementationStatus.IMPLEMENTED){
				return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
		}
		return null;
	}
}
