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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.ui.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.ui.dialogs.CalculateCoverageDialog;
import com.testify.ecfeed.ui.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.utils.ModelUtils;

public class TestCasesViewer extends CheckboxTreeViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private MethodNode fSelectedMethod;
	private TestCasesViewerLabelProvider fLabelProvider;
	private TestCasesViewerContentProvider fContentProvider;
	private Button fExecuteSeletedButton;
	private Button fGenerateSuiteButton;

	@Override
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		TreeViewer treeViewer = super.createTreeViewer(parent, style);
		final Tree tree = treeViewer.getTree();
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					tree.setRedraw(false);
					TreeItem item = (TreeItem)event.item;
					if (item.getData() instanceof TestCaseNode) {
						if (!ModelUtils.isTestCaseImplemented((TestCaseNode)item.getData())) {
							item.setChecked(false);
						}
					} else if (item.getData() instanceof String) {
						for(int i = 0; i < item.getItemCount(); ++i) {
							TreeItem subItem = item.getItem(i);
							if (!ModelUtils.isTestCaseImplemented((TestCaseNode)subItem.getData())) {
								subItem.setChecked(false);
							}
						}
					}
					tree.setRedraw(true);
				}
			}
		});
		return treeViewer;
	}

	private class AddTestCaseAdapter extends SelectionAdapter{
		public void widgetSelected(SelectionEvent e){
			
			for(CategoryNode category : fSelectedMethod.getCategories()){
				if(!category.isExpected() && category.getOrdinaryPartitions().isEmpty()){
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Warning", "Imput partitions cannot be null");
					return;
				}
			}
			
			AddTestCaseDialog dialog = new AddTestCaseDialog(getActiveShell(), fSelectedMethod);
			dialog.create();
			if (dialog.open() == IDialogConstants.OK_ID) {
				String testSuite = dialog.getTestSuite();
				ArrayList<PartitionNode> testData = dialog.getTestData();
				fSelectedMethod.addTestCase(new TestCaseNode(testSuite, testData));
				modelUpdated();
			}
		}
	}
	
	private class RenameSuiteAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			RenameTestSuiteDialog dialog = 
					new RenameTestSuiteDialog(Display.getDefault().getActiveShell(), fSelectedMethod.getTestSuites());
			dialog.create();
			if (dialog.open() == Window.OK) {
				String oldName = dialog.getRenamedTestSuite();
				String newName = dialog.getNewName();
				Collection<TestCaseNode> testSuite = fSelectedMethod.getTestCases(oldName);
				for(TestCaseNode testCase : testSuite){
					testCase.setName(newName);
				}
				modelUpdated();
			}
		}
	}
	
	private class RemoveSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if(!fSelectedMethod.getTestCases().isEmpty()){
				if(MessageDialog.openConfirm(getActiveShell(), 
						Messages.DIALOG_REMOVE_TEST_CASES_TITLE,
						Messages.DIALOG_REMOVE_TEST_CASES_MESSAGE)){
					removeCheckedTestSuites();
					removeCheckedTestCases();
					
					for(String testSuite : fSelectedMethod.getTestSuites()){
						getCheckboxViewer().setGrayChecked(testSuite, false);
					}
					modelUpdated();
				}
			}
		}

		private void removeCheckedTestSuites() {
			for(String testSuite : fSelectedMethod.getTestSuites()){
				if(getCheckboxViewer().getChecked(testSuite) && !getCheckboxViewer().getGrayed(testSuite)){
					fSelectedMethod.removeTestSuite(testSuite);
				}
			}
		}

		private void removeCheckedTestCases() {
			for(Object element : getCheckboxViewer().getCheckedElements()){
				if(element instanceof TestCaseNode){
					fSelectedMethod.removeTestCase((TestCaseNode)element);
				}
			}
		}
	}
	
	private class CalculateCoverageAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			CalculateCoverageDialog dialog = new CalculateCoverageDialog(getActiveShell(), 
					fSelectedMethod, getCheckedElements(), getGrayedElements());
			dialog.open();
		}

	}
	
	public TestCasesViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		getCheckboxViewer().addCheckStateListener(new TreeCheckStateListener(getCheckboxViewer()));

		getSection().setText("Test cases");
		
		addButton("Add test case", new AddTestCaseAdapter());
		addButton("Rename suite", new RenameSuiteAdapter());
		fGenerateSuiteButton = addButton("Generate test suite", new GenerateTestSuiteAdapter(this));
		addButton("Calculate coverage", new CalculateCoverageAdapter());
		addButton("Remove selected", new RemoveSelectedAdapter());
		fExecuteSeletedButton = addButton("Execute selected", new ExecuteStaticTestAdapter(this));

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}
	
	@Override
	//Put buttons next to the viewer instead below
	protected int buttonsPosition(){
		return BUTTONS_ASIDE;
	}
	
	@Override
	protected IContentProvider viewerContentProvider() {
		if(fContentProvider == null){
			fContentProvider = new TestCasesViewerContentProvider(fSelectedMethod);	
		}
		return fContentProvider;
	}

	@Override
	protected IBaseLabelProvider viewerLabelProvider() {
		if(fLabelProvider == null){
			fLabelProvider = new TestCasesViewerLabelProvider(fSelectedMethod);
		}
		return fLabelProvider;
	}

	public void setInput(MethodNode method){
		fSelectedMethod = method;
		fContentProvider.setMethod(method);
		fLabelProvider.setMethod(method);
		super.setInput(method);
	}
	
	public MethodNode getSelectedMethod(){
		return fSelectedMethod;
	}

	@Override
	public void refresh() {
		super.refresh();
		fExecuteSeletedButton.setEnabled(ModelUtils.isMethodImplemented(fSelectedMethod) || ModelUtils.isMethodPartiallyImplemented(fSelectedMethod));
		fGenerateSuiteButton.setEnabled(ModelUtils.isMethodWithParameters(fSelectedMethod));
	}
}
