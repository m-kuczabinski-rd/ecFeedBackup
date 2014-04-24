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

package com.testify.ecfeed.ui.editor.modeleditor.obsolete;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.testify.ecfeed.utils.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.DefaultValueEditingSupport;
import com.testify.ecfeed.ui.common.TestDataEditorListener;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.ui.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.ui.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.ui.dialogs.TestMethodRenameDialog;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.wb.swt.TableViewerColumnSorter;
import org.eclipse.jface.window.Window;

public class ObsoleteMethodNodeDetailsPage extends ObsoleteGenericNodeDetailsPage implements TestDataEditorListener{
	private Label fMethodNameLabel;
	private MethodNode fSelectedMethod;
	private CheckboxTreeViewer fTestCasesViewer;
	private CheckboxTableViewer fConstraintsViewer;
	private TableViewer fParametersViewer;
	private Section fMainSection;
	private Section fConstraintsSection;
	private ColorManager fColorManager;
	private Composite fParametersComposite;
	private String EMPTY_STRING = "";
	private Section fParametersSection;
	private Section fTestCasesSection;

	private class TestCaseViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
		public final Object[] EMPTY_ARRAY = new Object[]{};

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof MethodNode){
				return ((MethodNode)inputElement).getTestSuites().toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof String){
				Collection<TestCaseNode> testCases = fSelectedMethod.getTestCases((String)parentElement);
				if(testCases.size() < Constants.MAX_DISPLAYED_TEST_CASES_PER_SUITE){
					return testCases.toArray();
				}
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element) {
			if(element instanceof TestCaseNode){
				return ((TestCaseNode)element).getName();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
		
	}
	
	public ObsoleteMethodNodeDetailsPage(ObsoleteModelMasterDetailsBlock parentBlock){
		super(parentBlock);
		fColorManager = new ColorManager();
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
				TestMethodRenameDialog dialog = new TestMethodRenameDialog(getActiveShell(), fSelectedMethod);
				if(dialog.open() == IDialogConstants.OK_ID){
					MethodNode selectedMethod = dialog.getSelectedMethod();
					fSelectedMethod.setName(selectedMethod.getName());
					updateParemeters(selectedMethod);
					updateModel(fSelectedMethod);
				}
			}

			private void updateParemeters(MethodNode newMethod) {
				List<CategoryNode> srcParameters = newMethod.getCategories();
				for(int i = 0; i < srcParameters.size(); i++){
					updateParameter(i, srcParameters.get(i));
				}
			}
		});
	}
	
	private void updateParameter(int index, CategoryNode newCategory){
		boolean isOriginalCategoryExpected = fSelectedMethod.getCategories().get(index) 
				instanceof ExpectedValueCategoryNode;
		boolean isNewCategoryExpected = newCategory instanceof ExpectedValueCategoryNode;
		if(isOriginalCategoryExpected == isNewCategoryExpected){
			fSelectedMethod.getCategories().get(index).setName(newCategory.getName());
		}
		else{
			fSelectedMethod.replaceCategory(index, newCategory);
		}
	}

	private void createParametersSection(Composite composite) {
		fParametersSection = getToolkit().createSection(composite, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		fParametersSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		getToolkit().paintBordersFor(fParametersSection);
		fParametersSection.setText("Parameters");
		fParametersSection.setExpanded(false);
	}

	private void redrawParametersComposite(Section section, List<CategoryNode> input) {
		if(fParametersComposite != null && !fParametersComposite.isDisposed()){
			fParametersComposite.dispose();
		}
		fParametersComposite = new Composite(section, SWT.NONE);
		getToolkit().adapt(fParametersComposite);
		getToolkit().paintBordersFor(fParametersComposite);
		section.setClient(fParametersComposite);
		fParametersComposite.setLayout(new GridLayout(1, false));
		fParametersComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
		createParametersViewer(fParametersComposite);
		fParametersViewer.setInput(input);
		fParametersSection.layout();
	}
	
	private void createParametersViewer(Composite composite) {
		fParametersViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fParametersViewer.setContentProvider(new ArrayContentProvider());
		fParametersViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		fParametersViewer.getTable().setLayoutData(VIEWERS_GRID_DATA);
		fParametersViewer.getTable().setHeaderVisible(true);
		
		createParametersColumns();
	}
	
	private void createParametersColumns() {
		createTableViewerColumn(fParametersViewer, "Name", 100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				String result = new String();
				if(element instanceof ExpectedValueCategoryNode){
					result += "[e]";
				}
				result += ((CategoryNode)element).getName();
				return result;
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		createTableViewerColumn(fParametersViewer, "Type", 100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		if(fSelectedMethod.getExpectedCategoriesNames().size() > 0){
			TableViewerColumn defaultValueColumn = 
					createTableViewerColumn(fParametersViewer, "Default value", 100, new ColumnLabelProvider(){
				@Override
				public String getText(Object element){
					if(element instanceof ExpectedValueCategoryNode){
						ExpectedValueCategoryNode category = (ExpectedValueCategoryNode)element;
						return category.getDefaultValuePartition().getValueString();
					}
					return EMPTY_STRING ;
				}
				@Override
				public Color getForeground(Object element){
					return getColor(element);
				}
			});
			defaultValueColumn.setEditingSupport(new DefaultValueEditingSupport(fParametersViewer, this));
		}
	}
	
	private Color getColor(Object element){
		if(element instanceof ExpectedValueCategoryNode){
			return fColorManager.getColor(ColorConstants.EXPECTED_VALUE_CATEGORY);
		}
		return null;
	}
	
	private void createConstraintsSection(Composite composite) {
		fConstraintsSection = getToolkit().createSection(composite, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		fConstraintsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		getToolkit().paintBordersFor(fConstraintsSection);
		fConstraintsSection.setText("Constraints");
		fConstraintsSection.setExpanded(false);
		
		Composite constraintsComposite = new Composite(fConstraintsSection, SWT.NONE);
		getToolkit().adapt(constraintsComposite);
		getToolkit().paintBordersFor(constraintsComposite);
		fConstraintsSection.setClient(constraintsComposite);
		constraintsComposite.setLayout(new GridLayout(1, false));
		constraintsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createConstraintsViewer(constraintsComposite);
		
		createConstraintsButtons(constraintsComposite);

	}
	
	private void createConstraintsViewer(Composite composite) {
		fConstraintsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fConstraintsViewer.setContentProvider(new ArrayContentProvider());
		fConstraintsViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		fConstraintsViewer.getTable().setHeaderVisible(true);
		fConstraintsViewer.getTable().setLayoutData(VIEWERS_GRID_DATA);
		
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
				fSelectedMethod.addConstraint(new ConstraintNode(name, new Constraint(premise, consequence)));
				updateModel(fSelectedMethod);
				fConstraintsSection.layout();
				fMainSection.layout();
			}
		});
	}
	
	private void createRemoveSelectedConstraintsButton(Composite composite) {
		createButton(composite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if(MessageDialog.openConfirm(getActiveShell(), 
						Messages.DIALOG_REMOVE_CONSTRAINTS_TITLE, 
						Messages.DIALOG_REMOVE_CONSTRAINTS_MESSAGE)){
					for(Object constraint : fConstraintsViewer.getCheckedElements()){
						fSelectedMethod.removeConstraint((ConstraintNode)constraint);
					}
					updateModel((RootNode)fSelectedMethod.getRoot());
				}
			}
		});
	}

	private void createTestCasesSection(Composite mainComposite) {
		fTestCasesSection = getToolkit().createSection(mainComposite, Section.TITLE_BAR);
		fTestCasesSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(fTestCasesSection);
		fTestCasesSection.setText("Test Cases");
		
		Composite testCasesComposite = new Composite(fTestCasesSection, SWT.NONE);
		getToolkit().adapt(testCasesComposite);
		getToolkit().paintBordersFor(testCasesComposite);
		fTestCasesSection.setClient(testCasesComposite);
		testCasesComposite.setLayout(new GridLayout(2, false));
		
		createTestCasesViewer(testCasesComposite);
		
		createTestCasesSectionButtons(testCasesComposite);

	}
	private void createTestCasesViewer(Composite composite) {
		fTestCasesViewer = new CheckboxTreeViewer(composite);
		fTestCasesViewer.setContentProvider(new TestCaseViewerContentProvider());
		fTestCasesViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof String){
					int testCasesCount = fSelectedMethod.getTestCases((String)element).size();
					return (String)element + " [" +  testCasesCount + " test case" + 
							(testCasesCount == 1?"":"s") + "]";
				}
				else if(element instanceof TestCaseNode){
					return fSelectedMethod.getName() + "(" + ((TestCaseNode)element).testDataString() + ")";
				}
				return null;
			}
		});
		fTestCasesViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		fTestCasesViewer.addCheckStateListener(new TreeCheckStateListener(fTestCasesViewer));
		fTestCasesViewer.getTree().setLayoutData(VIEWERS_GRID_DATA);
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

		createExecuteSelectedButton(testCasesButonsComposite);

		createExecuteOnlineButton(testCasesButonsComposite);
	}
	
	private void createAddTestCaseButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Add Test Case", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				AddTestCaseDialog dialog = new AddTestCaseDialog(getActiveShell(), fSelectedMethod);
				dialog.create();
				if (dialog.open() == IDialogConstants.OK_ID) {
					String testSuite = dialog.getTestSuite();
					ArrayList<PartitionNode> testData = dialog.getTestData();
					fSelectedMethod.addTestCase(new TestCaseNode(testSuite, testData));
					updateModel(fSelectedMethod);
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
						new RenameTestSuiteDialog(Display.getDefault().getActiveShell(), fSelectedMethod.getTestSuites());
				dialog.create();
				if (dialog.open() == Window.OK) {
					String oldName = dialog.getRenamedTestSuite();
					String newName = dialog.getNewName();
					Collection<TestCaseNode> testSuite = fSelectedMethod.getTestCases(oldName);
					for(TestCaseNode testCase : testSuite){
						testCase.setName(newName);
					}
					updateModel((RootNode)fSelectedMethod.getRoot());
				}
			}
		});
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createGenerateTestSuiteButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Generate Test Suite", 
				new ObsoleteGenerateTestSuiteAdapter(this));
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createRemoveSelectedButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(MessageDialog.openConfirm(getActiveShell(), 
						Messages.DIALOG_REMOVE_TEST_CASES_TITLE,
						Messages.DIALOG_REMOVE_TEST_CASES_MESSAGE)){
					removeCheckedTestSuites();
					removeCheckedTestCases();
					
					for(String testSuite : fSelectedMethod.getTestSuites()){
						fTestCasesViewer.setGrayChecked(testSuite, false);
					}
					updateModel((RootNode)fSelectedMethod.getRoot());
				}
			}

			private void removeCheckedTestSuites() {
				for(String testSuite : fSelectedMethod.getTestSuites()){
					if(fTestCasesViewer.getChecked(testSuite) && !fTestCasesViewer.getGrayed(testSuite)){
						fSelectedMethod.removeTestSuite(testSuite);
					}
				}
			}

			private void removeCheckedTestCases() {
				for(Object element : fTestCasesViewer.getCheckedElements()){
					if(element instanceof TestCaseNode){
						fSelectedMethod.removeTestCase((TestCaseNode)element);
					}
				}
			}
		});
		
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createExecuteSelectedButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Execute selected", 
				new ObsoleteExecuteStaticTestAdapter(this));
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	private void createExecuteOnlineButton(Composite testCasesButonsComposite) {
		Button button = createButton(testCasesButonsComposite, "Execute online", 
				new ObsoleteExecuteOnlineTestAdapter(this));
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedMethod = (MethodNode)fSelectedNode;
		redrawParametersComposite(fParametersSection, fSelectedMethod.getCategories());
		fConstraintsViewer.setInput(fSelectedMethod.getConstraintNodes());
		fTestCasesViewer.setInput(fSelectedMethod);
		refresh();
	}

	public void refresh() {
		if(fSelectedMethod == null){
			return;
		}
		fMainSection.setText(fSelectedMethod.toString());
		fMethodNameLabel.setText("Method name: " + fSelectedMethod.toString());
		fParametersViewer.refresh();
		fConstraintsViewer.refresh();
		fTestCasesViewer.refresh();
		fConstraintsSection.layout();
		fTestCasesSection.layout();
		fMainSection.layout();
	}

	@Override
	public void testDataChanged() {
		updateModel(fSelectedMethod);
	}
	
	public MethodNode getSelectedMethod(){
		return fSelectedMethod;
	}

	public CheckboxTreeViewer getTestCaseViewer() {
		return fTestCasesViewer;
	}
}
