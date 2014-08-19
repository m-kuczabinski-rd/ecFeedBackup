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

package com.testify.ecfeed.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TestDataEditorListener;
import com.testify.ecfeed.ui.common.TestDataValueEditingSupport;
import com.testify.ecfeed.utils.Constants;

public class AddTestCaseDialog extends TitleAreaDialog implements TestDataEditorListener {

	private MethodNode fMethod;
	private ArrayList<PartitionNode> fTestData;
	private String fTestSuiteName;
	private Combo fTestSuiteCombo;
	private Button fOkButton;
	private TableViewer fTestDataViewer;
	private ColorManager fColorManager;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddTestCaseDialog(Shell parentShell, MethodNode method) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		fTestData = new ArrayList<PartitionNode>();
		List<CategoryNode> categories = method.getCategories();
		for(CategoryNode category : categories){
			if(category.isExpected()){
				fTestData.add(createAnonymuousPartition(category));
			}
			else{

				PartitionNode testValue = category.getPartitions().get(0);
				while(testValue.isAbstract()){
					testValue = testValue.getPartitions().get(0);
				}
				fTestData.add(testValue);
			}
			
		}
		fMethod = method;
		fColorManager = new ColorManager();
	}

	private PartitionNode createAnonymuousPartition(CategoryNode parent) {
		PartitionNode partition = new PartitionNode("@expected", parent.getDefaultValueString()); 
		partition.setParent(parent);
		return partition;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.DIALOG_ADD_TEST_CASE_TITLE);
		setMessage(Messages.DIALOG_ADD_TEST_CASE_MESSAGE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTestSuiteComposite(container);
		
		createTestDataViewer(container);

		return area;
	}

	private void createTestDataViewer(Composite container) {
		fTestDataViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = fTestDataViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn categoryViewerColumn = new TableViewerColumn(fTestDataViewer, SWT.NONE);
		TableColumn categoryColumn = categoryViewerColumn.getColumn();
		categoryColumn.setWidth(200);
		categoryColumn.setText("Parameter");
		categoryViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override 
			public String getText(Object element){
				return ((PartitionNode)element).getParent().toString();
			}
			@Override 
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		
		TableViewerColumn partitionViewerColumn = new TableViewerColumn(fTestDataViewer, SWT.NONE);
		TableColumn partitionColumn = partitionViewerColumn.getColumn();
		partitionColumn.setWidth(150);
		partitionColumn.setText("Partition");
		partitionViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				if(testValue.getCategory().isExpected()){
					return testValue.getValueString();
				}
				return testValue.toString();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		partitionViewerColumn.setEditingSupport(new TestDataValueEditingSupport(fTestDataViewer, fTestData, this));

		fTestDataViewer.setContentProvider(new ArrayContentProvider());
		fTestDataViewer.setInput(fTestData);
	}

	private Color getColor(Object element){
		PartitionNode partition = (PartitionNode)element;
		if(partition.getCategory().isExpected()){
			return fColorManager.getColor(ColorConstants.EXPECTED_VALUE_CATEGORY);
		}
		return null;
	}
	
	private void createTestSuiteComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label testSuiteLabel = new Label(composite, SWT.NONE);
		testSuiteLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		testSuiteLabel.setText("Test suite");
		
		ComboViewer testSuiteViewer = new ComboViewer(composite, SWT.NONE);
		fTestSuiteCombo = testSuiteViewer.getCombo();
		fTestSuiteCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fTestSuiteCombo.setItems(fMethod.getTestSuites().toArray(new String[]{}));
		fTestSuiteCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validateTestSuiteName();
			}
		});
		fTestSuiteCombo.setText(Constants.DEFAULT_TEST_SUITE_NAME);
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	public void okPressed(){
		fTestSuiteName = fTestSuiteCombo.getText();
		super.okPressed();
	}
	
	private void validateTestSuiteName() {
		if(!TestCaseNode.validateTestSuiteName(fTestSuiteCombo.getText())){
			setErrorMessage(Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
			setOkButton(false);
		}
		else{
			setErrorMessage(null);
			setOkButton(true);
		}
	}

	private void setOkButton(boolean enabled) {
		if(fOkButton != null && !fOkButton.isDisposed()){
			fOkButton.setEnabled(enabled);
		}
	}

	@Override
	public void testDataChanged(int index, PartitionNode newValue) {
		fTestData.set(index, newValue);
		fTestDataViewer.refresh();
	}
	
	public String getTestSuite(){
		return fTestSuiteName;
	}
	
	public ArrayList<PartitionNode> getTestData(){
		return fTestData;
	}
}
