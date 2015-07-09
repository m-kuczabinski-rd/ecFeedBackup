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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.common.TestCasesViewerContentProvider;
import com.testify.ecfeed.ui.common.TestCasesViewerLabelProvider;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class TestCasesViewer extends CheckboxTreeViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private IFileInfoProvider fFileInfoProvider;
	private TestCasesViewerLabelProvider fLabelProvider;
	private TestCasesViewerContentProvider fContentProvider;
	private Button fExecuteSelectedButton;
	private Button fGenerateSuiteButton;
	private MethodInterface fMethodIf;
	private MethodNode fParentMethod;

	private class AddTestCaseAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.addTestCase();
		}
	}

	private class GenerateTestSuiteAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.generateTestSuite();
		}
	}

	private class ExecuteStaticTestAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.executeStaticTests(getCheckedTestCases(), fFileInfoProvider);
		}
	}

	private class RenameSuiteAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.renameSuite();
		}
	}

	private class RemoveSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.removeTestCases(getCheckedTestCases());
		}
	}

	private class CalculateCoverageAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodIf.openCoverageDialog(getCheckedElements(), getGrayedElements());
		}
	}

	public TestCasesViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, STYLE);
		fFileInfoProvider = fileInfoProvider;
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		fMethodIf = new MethodInterface(this, fileInfoProvider);

		getCheckboxViewer().addCheckStateListener(new TreeCheckStateListener(getCheckboxViewer()));

		getSection().setText("Test cases");

		addButton("Add test case", new AddTestCaseAdapter());
		addButton("Rename suite", new RenameSuiteAdapter());
		fGenerateSuiteButton = addButton("Generate test suite", new GenerateTestSuiteAdapter());
		addButton("Calculate coverage", new CalculateCoverageAdapter());
		addButton("Remove selected", new RemoveSelectedAdapter());
		fExecuteSelectedButton = addButton("Execute selected", new ExecuteStaticTestAdapter());

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
	}

	@Override
	public void refresh() {
		fGenerateSuiteButton.setEnabled(getSelectedMethod().getParameters().size() > 0);
		fExecuteSelectedButton.setEnabled(executionEnabled());
		fLabelProvider.refresh();
	}

	public void setInput(MethodNode method){
		fParentMethod = method;
		fMethodIf.setTarget(method);
		fLabelProvider.setMethod(method);
		fContentProvider.setMethod(method);
		super.setInput(method);
	}

	protected Collection<TestCaseNode> getCheckedTestCases() {
		Collection<TestCaseNode> result = new HashSet<TestCaseNode>();
		for(Object o : getCheckedElements()){
			if(o instanceof TestCaseNode){
				result.add((TestCaseNode)o);
			}
			if(o instanceof String && getCheckboxViewer().getGrayed(o) == false){
				result.addAll(getSelectedMethod().getTestCases((String)o));
			}
		}
		return result;
	}

	@Override
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		TreeViewer treeViewer = super.createTreeViewer(parent, style);
		final Tree tree = treeViewer.getTree();
		tree.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					fExecuteSelectedButton.setEnabled(executionEnabled());
				}
			}
		});
		return treeViewer;
	}

	@Override
	//Put buttons next to the viewer instead below
	protected int buttonsPosition(){
		return BUTTONS_ASIDE;
	}

	@Override
	protected IContentProvider viewerContentProvider() {
		if(fContentProvider == null){
			fContentProvider = new TestCasesViewerContentProvider();
		}
		return fContentProvider;
	}

	@Override
	protected IBaseLabelProvider viewerLabelProvider() {
		if(fLabelProvider == null){
			fLabelProvider = new TestCasesViewerLabelProvider();
		}
		return fLabelProvider;
	}

	@Override
	protected int viewerStyle(){
		return VIEWER_STYLE;
	}

	private MethodNode getSelectedMethod(){
		return fParentMethod;
	}

	private boolean executionEnabled(){
		Collection<TestCaseNode> checked = getCheckedTestCases();
		if(checked.size() == 0) return false;
		if(fMethodIf.getImplementationStatus() == EImplementationStatus.NOT_IMPLEMENTED) return false;
		for(TestCaseNode tc : checked){
			if(fMethodIf.getImplementationStatus(tc) != EImplementationStatus.IMPLEMENTED) return false;
		}
		return true;
	}

}
