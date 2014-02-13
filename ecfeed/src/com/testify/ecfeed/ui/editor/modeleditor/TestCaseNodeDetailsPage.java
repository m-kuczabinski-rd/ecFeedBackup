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

package com.testify.ecfeed.ui.editor.modeleditor;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.InputChangedListener;
import com.testify.ecfeed.ui.common.TestCasePartitionEditingSupport;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.layout.RowLayout;

public class TestCaseNodeDetailsPage extends GenericNodeDetailsPage implements InputChangedListener{
	private TestCaseNode fSelectedTestCase;
	private Section fMainSection;
	private MethodNode fParent;
	private Combo fTestSuiteNameCombo;
	private TableViewer fTestDataViewer;
	private TableViewerColumn fPartitionViewerColumn;
	private ColorManager fColorManager;

	/**
	 * Create the details page.
	 */
	public TestCaseNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
		fColorManager = new ColorManager();
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		//		
		fMainSection = getToolkit().createSection(parent, Section.TITLE_BAR);
		getToolkit().paintBordersFor(fMainSection);
		fMainSection.setText("New Section");
		fMainSection.setExpanded(true);
		
		Composite mainContainer = new Composite(fMainSection, SWT.NONE);
		getToolkit().adapt(mainContainer);
		getToolkit().paintBordersFor(mainContainer);
		fMainSection.setClient(mainContainer);
		mainContainer.setLayout(new GridLayout(1, false));
		
		createTestSuiteNameComposite(mainContainer);
		
		createTestDataComposite(mainContainer);
		
		createTextClientComposite();

	}

	private void createTestSuiteNameComposite(Composite mainContainer) {
		Composite testSuiteNameComposite = new Composite(mainContainer, SWT.NONE);
		testSuiteNameComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		getToolkit().adapt(testSuiteNameComposite);
		getToolkit().paintBordersFor(testSuiteNameComposite);
		testSuiteNameComposite.setLayout(new GridLayout(3, false));
		
		Label testSuiteNameLabel = new Label(testSuiteNameComposite, SWT.NONE);
		testSuiteNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		getToolkit().adapt(testSuiteNameLabel, true, true);
		testSuiteNameLabel.setText("Test suite");
		
		ComboViewer comboViewer = new ComboViewer(testSuiteNameComposite, SWT.NONE);
		fTestSuiteNameCombo = comboViewer.getCombo();
		fTestSuiteNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().paintBordersFor(fTestSuiteNameCombo);
		fTestSuiteNameCombo.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					renameTestCase();
				}
			}
		});
		getToolkit().adapt(fTestSuiteNameCombo, true, true);
		
		createButton(testSuiteNameComposite, "Change", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				renameTestCase();
			}
		});
	}

	private void createTestDataComposite(Composite parentComposite) {
		Composite testDataComposite = new Composite(parentComposite, SWT.NONE);
		testDataComposite.setLayout(new GridLayout(1, false));
		testDataComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().adapt(testDataComposite);
		getToolkit().paintBordersFor(testDataComposite);
		
		Label testDataLabel = new Label(testDataComposite, SWT.NONE);
		testDataLabel.setSize(62, 17);
		getToolkit().adapt(testDataLabel, true, true);
		testDataLabel.setText("Test data");
		
		fTestDataViewer = new TableViewer(testDataComposite, SWT.BORDER | SWT.FULL_SELECTION);
		fTestDataViewer.setContentProvider(new ArrayContentProvider());
		fTestDataViewer.getTable().setHeaderVisible(true);
		fTestDataViewer.getTable().setLayoutData(VIEWERS_GRID_DATA);
		
		createTableViewerColumn(fTestDataViewer, "Parameter", 155, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				CategoryNode parent = testValue.getCategory();
				return parent.toString();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		
		fPartitionViewerColumn = createTableViewerColumn(fTestDataViewer, "Value", 110, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				if(testValue.getCategory() instanceof ExpectedValueCategoryNode){
					return testValue.getValueString();
				}
				return testValue.toString();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		}); 
	}

	private Color getColor(Object element){
		PartitionNode partition = (PartitionNode)element;
		if(partition.getCategory() instanceof ExpectedValueCategoryNode){
			return fColorManager.getColor(ColorConstants.EXPECTED_VALUE_CATEGORY);
		}
		return null;
	}
	
	private void createTextClientComposite() {
		Composite textClientComposite = new Composite(fMainSection, SWT.NONE);
		getToolkit().adapt(textClientComposite);
		getToolkit().paintBordersFor(textClientComposite);
		fMainSection.setTextClient(textClientComposite);
		textClientComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		createButton(textClientComposite, "Remove", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				fParent.removeTestCase(fSelectedTestCase);
				getParentBlock().selectNode(fParent);
				fSelectedTestCase = null;
				updateModel(fParent);
			}
		});
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedTestCase = (TestCaseNode)fSelectedNode;
		refresh();
	}

	public void refresh() {
		if(fSelectedTestCase == null || fSelectedTestCase.getParent() == null){
			return;
		}
		fParent = (MethodNode)fSelectedTestCase.getParent();
		fMainSection.setText(fSelectedTestCase.toString());
		fTestSuiteNameCombo.setItems(fParent.getTestSuites().toArray(new String[]{}));
		fTestSuiteNameCombo.setText(fSelectedTestCase.getName());
		List<PartitionNode> testData = fSelectedTestCase.getTestData();
		fTestDataViewer.setInput(testData);
		fPartitionViewerColumn.setEditingSupport(new TestCasePartitionEditingSupport(fTestDataViewer, testData, this));
	}

	private void renameTestCase() {
		String newName = fTestSuiteNameCombo.getText();
		if(TestCaseNode.validateTestSuiteName(newName)){
			fSelectedTestCase.setName(newName);
			updateModel(fSelectedTestCase);
		}
		else{
			MessageDialog.openError(getActiveShell(), 
					Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_TITLE, 
					Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
			fTestSuiteNameCombo.setText(fSelectedTestCase.getName());
		}
	}

	@Override
	public void inputChanged() {
		updateModel(fSelectedTestCase);
	}
}
