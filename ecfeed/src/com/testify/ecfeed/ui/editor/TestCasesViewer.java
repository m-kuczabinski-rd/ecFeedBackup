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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.ui.dialogs.CalculateCoverageDialog;
import com.testify.ecfeed.ui.modelif.MethodInterface;
import com.testify.ecfeed.utils.ModelUtils;

public class TestCasesViewer extends CheckboxTreeViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	
	private MethodNode fSelectedMethod;
	private TestCasesViewerLabelProvider fLabelProvider;
	private TestCasesViewerContentProvider fContentProvider;
	private Button fExecuteSelectedButton;
	private Button fGenerateSuiteButton;
	private boolean fIsExecutable;
	private MethodInterface fMethodIf;
	
	
	private class AddTestCaseAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.addTestCase(TestCasesViewer.this, getUpdateListener());
		}
	}
	
	private class RenameSuiteAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.renameSuite(TestCasesViewer.this, getUpdateListener());
		}
	}
	
	private class RemoveSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.removeTestCases(getCheckedTestCases(), TestCasesViewer.this, getUpdateListener());
		}
	}
	
	private class CalculateCoverageAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			new CalculateCoverageDialog(getActiveShell(), 
					fMethodIf.getTarget(), getCheckedElements(), getGrayedElements()).open();
		}
	}
	
	public TestCasesViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);
		
		fMethodIf = new MethodInterface(operationManager);

		getCheckboxViewer().addCheckStateListener(new TreeCheckStateListener(getCheckboxViewer()));

		getSection().setText("Test cases");
		
		addButton("Add test case", new AddTestCaseAdapter());
		addButton("Rename suite", new RenameSuiteAdapter());
		fGenerateSuiteButton = addButton("Generate test suite", new GenerateTestSuiteAdapter(this));
		addButton("Calculate coverage", new CalculateCoverageAdapter());
		addButton("Remove selected", new RemoveSelectedAdapter());
		fExecuteSelectedButton = addButton("Execute selected", new ExecuteStaticTestAdapter(this));

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}
	
	protected Collection<TestCaseNode> getCheckedTestCases() {
		Collection<TestCaseNode> result = new ArrayList<TestCaseNode>();
		for(Object o : getCheckedElements()){
			if(o instanceof TestCaseNode){
				result.add((TestCaseNode)o);
			}
		}
		return result;
	}

	@Override
	public void refresh() {
		//super.refresh();
		fIsExecutable = ModelUtils.isMethodImplemented(fSelectedMethod) || ModelUtils.isMethodPartiallyImplemented(fSelectedMethod);
		setExecuteEnabled(true);
		fGenerateSuiteButton.setEnabled(ModelUtils.isMethodWithParameters(fSelectedMethod));
	}

	public void setInput(MethodNode method){
		fSelectedMethod = method;
		fMethodIf.setTarget(method);
		fContentProvider.setMethod(method);
		fLabelProvider.setMethod(method);
		super.setInput(method);
	}
	
	public MethodNode getSelectedMethod(){
		return fSelectedMethod;
	}
	
	@Override
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		TreeViewer treeViewer = super.createTreeViewer(parent, style);
		final Tree tree = treeViewer.getTree();
		tree.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					isSelectionExecutable();
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
	
	private void setExecuteEnabled(boolean enabled){
			fExecuteSelectedButton.setEnabled(enabled && fIsExecutable);
	}
	
	private void isSelectionExecutable(){
		if(fIsExecutable){
			for(Object element: getCheckboxViewer().getCheckedElements()){
				if(element instanceof TestCaseNode){
					if (!ModelUtils.isTestCaseImplemented((TestCaseNode)element)){
						setExecuteEnabled(false);
						return;
					}
				}
			}
		}
		setExecuteEnabled(true);
	}
	
}
