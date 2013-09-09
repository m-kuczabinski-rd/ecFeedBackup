/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.editor.modeleditor;

import java.util.Collection;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.ITestGenAlgorithm;
import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.dialogs.GenerateTestSuiteDialog;
import com.testify.ecfeed.dialogs.RemoveTestSuiteDialog;
import com.testify.ecfeed.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.dialogs.TestMethodRenameDialog;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.IStatement;
import com.testify.ecfeed.model.constraint.StaticStatement;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.wb.swt.TableViewerColumnSorter;
import org.eclipse.jface.window.Window;

public class MethodNodeDetailsPage extends GenericNodeDetailsPage{
	private Label fMethodNameLabel;
	private MethodNode fSelectedNode;
	private CheckboxTableViewer fTestCasesViewer;
	private CheckboxTableViewer fConstraintsViewer;
	private TableViewer fParametersViewer;
	private Section fMainSection;

	public MethodNodeDetailsPage(ModelMasterDetailsBlock parentBlock){
		super(parentBlock);
	}

	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		fMainSection = getToolkit().createSection(parent, Section.TITLE_BAR);
		getToolkit().paintBordersFor(fMainSection);
		fMainSection.setText("Method");
		
		Composite mainComposite = new Composite(fMainSection, SWT.NONE);
		getToolkit().adapt(mainComposite);
		getToolkit().paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);
		mainComposite.setLayout(new GridLayout(1, false));
		
		createMethodNameComposite(mainComposite);
		
		createParametersSection(mainComposite);
		
		createConstraintsSection(mainComposite);

		createTestCasesSection(mainComposite);
	}

	private void createMethodNameComposite(Composite composite) {
		Composite methodNameComposite = new Composite(composite, SWT.NONE);
		methodNameComposite.setLayout(new GridLayout(2, false));
		methodNameComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().adapt(methodNameComposite);
		getToolkit().paintBordersFor(methodNameComposite);
		
		fMethodNameLabel = new Label(methodNameComposite, SWT.NONE);
		fMethodNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().adapt(fMethodNameLabel, true, true);
		
		createButton(methodNameComposite, "Change", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				TestMethodRenameDialog dialog = new TestMethodRenameDialog(getActiveShell(), fSelectedNode);
				if(dialog.open() == IDialogConstants.OK_ID){
					MethodNode selectedMethod = dialog.getSelectedMethod();
					fSelectedNode.setName(selectedMethod.getName());
					Vector<CategoryNode> parameters = fSelectedNode.getCategories();
					Vector<CategoryNode> newParameters = selectedMethod.getCategories();
					for(int i = 0; i < parameters.size(); i++){
						parameters.elementAt(i).setName(newParameters.elementAt(i).getName());
					}
					updateModel(fSelectedNode);
				}
			}
		});
	}

	private void createParametersSection(Composite composite) {
		Section parametersSection = getToolkit().createSection(composite, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		parametersSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		getToolkit().paintBordersFor(parametersSection);
		parametersSection.setText("Parameters");
		parametersSection.setExpanded(false);
		
		Composite parametersComposite = new Composite(parametersSection, SWT.NONE);
		getToolkit().adapt(parametersComposite);
		getToolkit().paintBordersFor(parametersComposite);
		parametersSection.setClient(parametersComposite);
		
		createParametersViewer(parametersComposite);
	}
	
	private void createParametersViewer(Composite composite) {
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
		fParametersViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fParametersViewer.setContentProvider(new ArrayContentProvider());
		fParametersViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		Table parametersTable = fParametersViewer.getTable();
		parametersTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		parametersTable.setHeaderVisible(true);
		getToolkit().paintBordersFor(parametersTable);
		
		createParametersColumns();
	}
	
	private void createParametersColumns() {
		createTableViewerColumn(fParametersViewer, "Name", 100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getName();
			}
		});
		createTableViewerColumn(fParametersViewer, "Type", 100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
			}
		});
	}
	
	private void createConstraintsSection(Composite composite) {
		Section constraintsSection = getToolkit().createSection(composite, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		constraintsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		getToolkit().paintBordersFor(constraintsSection);
		constraintsSection.setText("Constraints");
		constraintsSection.setExpanded(false);
		
		Composite constraintsComposite = new Composite(constraintsSection, SWT.NONE);
		getToolkit().adapt(constraintsComposite);
		getToolkit().paintBordersFor(constraintsComposite);
		constraintsSection.setClient(constraintsComposite);
		constraintsComposite.setLayout(new GridLayout(1, false));
		constraintsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createConstraintsViewer(constraintsComposite);
		
		createConstraintsButtons(constraintsComposite);

	}
	

	
	private void createConstraintsViewer(Composite composite) {
		fConstraintsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fConstraintsViewer.setContentProvider(new ArrayContentProvider());
		fConstraintsViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		Table constraintsTable = fConstraintsViewer.getTable();
		constraintsTable.setHeaderVisible(true);
		constraintsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		getToolkit().paintBordersFor(constraintsTable);
		
		TableViewerColumn constraintNameViewerColumn = createTableViewerColumn(fConstraintsViewer, "Name", 
				130, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getName();
			}
		});
		
		new TableViewerColumnSorter(constraintNameViewerColumn) {
			protected Object getValue(Object o) {
				return ((ConstraintNode)o).getName();
			}
		};

		createTableViewerColumn(fConstraintsViewer, "Definition", 100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getConstraint().toString();
			}
		});
	}

	private void createConstraintsButtons(Composite composite) {
		Composite constraintsButtonsComposite = new Composite(composite, SWT.NONE);
		constraintsButtonsComposite.setLayout(new RowLayout());

		createAddConstraintButton(constraintsButtonsComposite);
		createRemoveSelectedConstraintsButton(constraintsButtonsComposite);
	}

	private void createAddConstraintButton(Composite composite) {
		createButton(composite, "Add Constraint", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String name = Constants.DEFAULT_CONSTRAINT_NAME;
				BasicStatement premise = new StaticStatement(true);
				BasicStatement consequence = new StaticStatement(true);
				fSelectedNode.addConstraint(new ConstraintNode(name, new Constraint(premise, consequence)));
				updateModel(fSelectedNode);
			}
		});
	}
	private void createRemoveSelectedConstraintsButton(Composite composite) {
		createButton(composite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				MessageDialog infoDialog = new MessageDialog(Display.getDefault().getActiveShell(), 
						DialogStrings.DIALOG_REMOVE_CONSTRAINTS_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_QUESTION), 
						DialogStrings.DIALOG_REMOVE_CONSTRAINTS_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, 
						new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, IDialogConstants.OK_ID);
				if(infoDialog.open() == IDialogConstants.OK_ID){
					for(Object constraint : fConstraintsViewer.getCheckedElements()){
						fSelectedNode.removeConstraint((ConstraintNode)constraint);
					}
					fTestCasesViewer.setAllChecked(false);
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}
		});
	}

	private void createTestCasesSection(Composite mainComposite) {
		Section testCasesSection = getToolkit().createSection(mainComposite, Section.TITLE_BAR);
		testCasesSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(testCasesSection);
		testCasesSection.setText("Test Cases");
		
		Composite testCasesComposite = new Composite(testCasesSection, SWT.NONE);
		getToolkit().adapt(testCasesComposite);
		getToolkit().paintBordersFor(testCasesComposite);
		testCasesSection.setClient(testCasesComposite);
		testCasesComposite.setLayout(new GridLayout(2, false));
		
		createTestCasesViewer(testCasesComposite);
		
		createTestCasesSectionButtons(testCasesComposite);

	}
	private void createTestCasesViewer(Composite composite) {
		fTestCasesViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fTestCasesViewer.setContentProvider(new ArrayContentProvider());
		fTestCasesViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		Table testCasesTable = fTestCasesViewer.getTable();
		testCasesTable.setHeaderVisible(true);
		testCasesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(testCasesTable);
		
		TableViewerColumn testSuiteViewerColumn = createTableViewerColumn(fTestCasesViewer, "Test Suite", 
				130, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((TestCaseNode)element).getName();
			}
		});
		
		new TableViewerColumnSorter(testSuiteViewerColumn) {
			protected Object getValue(Object o) {
				return ((TestCaseNode)o).getName();
			}
		};

		createTableViewerColumn(fTestCasesViewer, "Parameter Values", 100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				String result = "[";
				TestCaseNode testCase = (TestCaseNode)element;
				Vector<PartitionNode> testData = testCase.getTestData();
				for(int i = 0; i < testData.size(); i++){
					result += testData.elementAt(i).getName();
					if(i < testData.size() - 1){
						result += ", ";
					}
				}
				result += "]";
				return result;
			}
		});
	}
	
	private void createTestCasesSectionButtons(Composite testCasesComposite) {
		Composite testCasesButonsComposite = new Composite(testCasesComposite, SWT.NONE);
		testCasesButonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		getToolkit().adapt(testCasesButonsComposite);
		getToolkit().paintBordersFor(testCasesButonsComposite);
		testCasesButonsComposite.setLayout(new GridLayout(1, false));
		
		createAddTestCaseButton(testCasesButonsComposite);

		createRenameSuiteButton(testCasesButonsComposite);
		
		createGenerateTestSuiteButton(testCasesButonsComposite);
		
		createRemoveSelectedButton(testCasesButonsComposite);
		
		createRemoveTestSuitesButton(testCasesButonsComposite);
	}
	
	private void createAddTestCaseButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Add Test Case", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				AddTestCaseDialog dialog = new AddTestCaseDialog(getActiveShell(), fSelectedNode);
				dialog.create();
				if (dialog.open() == IDialogConstants.OK_ID) {
					String testSuite = dialog.getTestSuite();
					Vector<PartitionNode> testData = dialog.getTestData();
					fSelectedNode.addTestCase(new TestCaseNode(testSuite, testData));
					updateModel(fSelectedNode);
				}
			}
		});
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createRenameSuiteButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Rename Suite...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				RenameTestSuiteDialog dialog = 
						new RenameTestSuiteDialog(Display.getDefault().getActiveShell(), fSelectedNode.getTestSuites());
				dialog.create();
				if (dialog.open() == Window.OK) {
					String oldName = dialog.getRenamedTestSuite();
					String newName = dialog.getNewName();
					Collection<TestCaseNode> testSuite = fSelectedNode.removeTestSuite(oldName);
					for(TestCaseNode testCase : testSuite){
						testCase.setName(newName);
						fSelectedNode.addTestCase(testCase);
					}
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}
		});
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createGenerateTestSuiteButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Generate Test Suite", new SelectionAdapter() {
			@SuppressWarnings({"rawtypes", "unchecked"})
			@Override
			public void widgetSelected(SelectionEvent e){
				GenerateTestSuiteDialog dialog = new GenerateTestSuiteDialog(getActiveShell(), fSelectedNode);
				if(dialog.open() == IDialogConstants.OK_ID){
					ITestGenAlgorithm selectedAlgorithm = dialog.getSelectedAlgorithm();
					Vector[] algorithmInput = dialog.getAlgorithmInput();
					IConstraint[] constraints = dialog.getConstraints();
					
					long startTime = System.currentTimeMillis();
					Vector[] generatedData = selectedAlgorithm.generate(algorithmInput, constraints);
					System.out.println("Data generated in " + (System.currentTimeMillis() - startTime) + "ms");
					if((generatedData != null) && (generatedData.length > 0)){
						for(Vector testCase : generatedData){
							Vector<PartitionNode> testData = (Vector<PartitionNode>)testCase;
							TestCaseNode testCaseNode = new TestCaseNode(dialog.getTestSuiteName(), testData);
							fSelectedNode.addTestCase(testCaseNode);
						}
						startTime = System.currentTimeMillis();
						updateModel(fSelectedNode);
						System.out.println("Model updated in " + (System.currentTimeMillis() - startTime) + "ms");
					}
					else{
						new MessageDialog(Display.getDefault().getActiveShell(), 
								DialogStrings.DIALOG_EMPTY_TEST_SUITE_GENERATED_TITLE, 
								Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 
								DialogStrings.DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE,
								MessageDialog.INFORMATION, 
								new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID).open();

					}
					
				}
			}

			@SuppressWarnings("rawtypes")
			private Vector[] getAlgorithmInput(MethodNode method) {
				Vector<CategoryNode> categories = method.getCategories();
				Vector[] result = new Vector[categories.size()];
				
				for(int i = 0; i < categories.size(); i++){
					result[i] = categories.elementAt(i).getPartitions();
				}
				return result;
			}
		});
		
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createRemoveSelectedButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog infoDialog = new MessageDialog(Display.getDefault().getActiveShell(), 
						DialogStrings.DIALOG_REMOVE_TEST_CASES_TITLE, Display.getDefault().getSystemImage(SWT.ICON_QUESTION), 
						DialogStrings.DIALOG_REMOVE_TEST_CASES_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, 
						new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, IDialogConstants.OK_ID);
				if(infoDialog.open() == IDialogConstants.OK_ID){
					for(Object testCase : fTestCasesViewer.getCheckedElements()){
						fSelectedNode.removeChild((TestCaseNode)testCase);
						fTestCasesViewer.setAllChecked(false);
					}
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}
		});
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createRemoveTestSuitesButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Remove Suites...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RemoveTestSuiteDialog dialog = new RemoveTestSuiteDialog(Display.getDefault().getActiveShell(), fSelectedNode.getTestSuites());
				if(dialog.open() == IDialogConstants.OK_ID){
					for(Object suite : dialog.getCheckedElements()){
						fSelectedNode.removeTestSuite((String)suite);
					}
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}
		});
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.getFirstElement() instanceof MethodNode){
			fSelectedNode = (MethodNode)structuredSelection.getFirstElement();
		}
		refresh();
	}

	public void refresh() {
		if(fSelectedNode == null){
			return;
		}
		fMainSection.setText(fSelectedNode.toString());
		fMethodNameLabel.setText("Method name: " + fSelectedNode.toString());
		fParametersViewer.setInput(fSelectedNode.getCategories());
		fConstraintsViewer.setInput(fSelectedNode.getConstraints());
		fTestCasesViewer.setInput(fSelectedNode.getTestCases());
	}
}
