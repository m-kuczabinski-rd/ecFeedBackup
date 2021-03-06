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

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.TestCasesViewerContentProvider;
import com.ecfeed.ui.common.TestCasesViewerLabelProvider;
import com.ecfeed.ui.common.TreeCheckStateListener;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;

public class TestCasesViewer extends CheckboxTreeViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private TestCasesViewerLabelProvider fLabelProvider;
	private TestCasesViewerContentProvider fContentProvider;
	private Button fExecuteSelectedButton;
	private Button fExportTestCasesButton;
	private Button fGenerateSuiteButton;
	private MethodInterface fMethodIf;
	private MethodNode fParentMethod;

	private class ExportTestCasesAdapter extends SelectionAdapter{ 
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.exportTestCases(getCheckedTestCases());
		}
	}

	private class AddTestCaseAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				fMethodIf.addTestCase();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add test case.", e.getMessage());
			}
		}
	}

	private class GenerateTestSuiteAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				fMethodIf.generateTestSuite();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not generate test suite.", e.getMessage());
			}
		}
	}

	private class ExecuteStaticTestAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent event) {
			try {
				fMethodIf.executeStaticTests(getCheckedTestCases(), getFileInfoProvider());
			}
			catch (Exception e){
				ExceptionCatchDialog.open("Can not execute static tests.", e.getMessage());
			}
		}
	}

	private class RenameSuiteAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				fMethodIf.renameSuite();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not rename suite.", e.getMessage());
			}
		}
	}

	private class RemoveSelectedAdapter extends SelectionAdapter {

		String fDescriptionWhenError;

		RemoveSelectedAdapter(String descriptionWhenError) {
			fDescriptionWhenError = descriptionWhenError;
		}

		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				fMethodIf.removeTestCases(getCheckedTestCases());
			} catch (Exception e) {
				ExceptionCatchDialog.open(fDescriptionWhenError, e.getMessage());
			}
		}
	}

	private class CalculateCoverageAdapter extends SelectionAdapter {

		private IFileInfoProvider fFileInfoProvider;

		CalculateCoverageAdapter(IFileInfoProvider fileInfoProvider) {
			fFileInfoProvider = fileInfoProvider;
		}
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				fMethodIf.openCoverageDialog(getCheckedElements(), getGrayedElements(), fFileInfoProvider);
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not calculate coverage.", e.getMessage());
			}
		}
	}

	public TestCasesViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		fMethodIf = new MethodInterface(this, fileInfoProvider);

		getCheckboxViewer().addCheckStateListener(new TreeCheckStateListener(getCheckboxViewer()));

		getSection().setText("Test cases");

		addButton("Add test case", new AddTestCaseAdapter());
		addButton("Rename suite", new RenameSuiteAdapter());
		fGenerateSuiteButton = addButton("Generate test suite", new GenerateTestSuiteAdapter());
		addButton("Calculate coverage", new CalculateCoverageAdapter(fileInfoProvider));
		addButton("Remove selected", new RemoveSelectedAdapter(Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		if (getFileInfoProvider().isProjectAvailable()) {
			fExecuteSelectedButton = addButton("Execute selected", new ExecuteStaticTestAdapter());
		}

		fExportTestCasesButton = addButton("Export selected", new ExportTestCasesAdapter());

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
	}

	@Override
	public void refresh() {
		fGenerateSuiteButton.setEnabled(getSelectedMethod().getParameters().size() > 0);

		if (getFileInfoProvider().isProjectAvailable()) {
			fExecuteSelectedButton.setEnabled(executionEnabled());
		}

		fExportTestCasesButton.setEnabled(anyTestCaseSelected());
		fLabelProvider.refresh();
	}

	private boolean anyTestCaseSelected() {
		if (getCheckedTestCases().isEmpty())
			return false;

		return true;
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
				if (event.detail == SWT.CHECK && getFileInfoProvider().isProjectAvailable()) {
					fExecuteSelectedButton.setEnabled(executionEnabled());
				}
				fExportTestCasesButton.setEnabled(anyTestCaseSelected());
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
			IFileInfoProvider fileInfoProvider = getFileInfoProvider();
			fLabelProvider = new TestCasesViewerLabelProvider(fileInfoProvider);
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

	private boolean executionEnabled() {

		if (!getFileInfoProvider().isProjectAvailable()) {
			return false;
		}

		if (fMethodIf.getImplementationStatus() == EImplementationStatus.NOT_IMPLEMENTED) { 
			return false;
		}

		Collection<TestCaseNode> checked = getCheckedTestCases();
		if (checked.size() == 0) {
			return false;
		}

		for (TestCaseNode tc : checked) {
			if (fMethodIf.getImplementationStatus(tc) != EImplementationStatus.IMPLEMENTED) {
				return false;
			}
		}

		return true;
	}

}
