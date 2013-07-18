package com.testify.ecfeed.editors;

import java.util.Collection;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.dialogs.RemoveTestSuiteDialog;
import com.testify.ecfeed.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.dialogs.TestCaseSettingsDialog;
import com.testify.ecfeed.model.CategoryNode;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.wb.swt.TableViewerColumnSorter;
import org.eclipse.jface.window.Window;

public class MethodNodeDetailsPage extends GenericNodeDetailsPage{
	private Label fMethodNameLabel;
	private MethodNode fSelectedNode;
	private CheckboxTableViewer fTestCasesViewer;
	private TableViewer fParametersViewer;
	private Section fMainSection;

	public MethodNodeDetailsPage(ModelMasterDetailsBlock parentBlock){
		super(parentBlock);
	}
	/**
	 * Create contents of the details page.
	 * @param parent
	 */
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
		
		createMethodameComposite(mainComposite);
		
		createParametersSection(mainComposite);

		createTestCasesSection(mainComposite);
	}
	private void createMethodameComposite(Composite composite) {
		Composite methodNameComposite = new Composite(composite, SWT.NONE);
		methodNameComposite.setLayout(new GridLayout(2, false));
		methodNameComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().adapt(methodNameComposite);
		getToolkit().paintBordersFor(methodNameComposite);
		
		fMethodNameLabel = new Label(methodNameComposite, SWT.NONE);
		fMethodNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().adapt(fMethodNameLabel, true, true);
		
		Button changeButton = getToolkit().createButton(methodNameComposite, "Change", SWT.NONE);
		changeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
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
		TableViewerColumn parameterNameViewerColumn = new TableViewerColumn(fParametersViewer, SWT.NONE);
		TableColumn parameterNameColumn = parameterNameViewerColumn.getColumn();
		parameterNameColumn.setWidth(100);
		parameterNameColumn.setText("Name");
		parameterNameViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getName();
			}
		});
		
		TableViewerColumn parameterTypeViewerColumn = new TableViewerColumn(fParametersViewer, SWT.NONE);
		TableColumn parameterTypeColumn = parameterTypeViewerColumn.getColumn();
		parameterTypeColumn.setWidth(100);
		parameterTypeColumn.setText("Type");
		parameterTypeViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
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
		
		TableViewerColumn testSuiteViewerColumn = new TableViewerColumn(fTestCasesViewer, SWT.NONE);
		new TableViewerColumnSorter(testSuiteViewerColumn) {
			protected Object getValue(Object o) {
				return ((TestCaseNode)o).getName();
			}
		};
		testSuiteViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((TestCaseNode)element).getName();
			}
		});
		TableColumn testSuiteColumn = testSuiteViewerColumn.getColumn();
		testSuiteColumn.setWidth(130);
		testSuiteColumn.setText("Test Suite");
		
		TableViewerColumn valuesViewerColumn = new TableViewerColumn(fTestCasesViewer, SWT.NONE);
		valuesViewerColumn.setLabelProvider(new ColumnLabelProvider(){
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
		TableColumn valuesColumn = valuesViewerColumn.getColumn();
		valuesColumn.setWidth(100);
		valuesColumn.setText("Parameter Values");
		
		
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
		Button addTestCaseButton = getToolkit().createButton(testCasesButonsComposite, "Add Test Case", SWT.NONE);
		addTestCaseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		addTestCaseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				TestCaseSettingsDialog dialog = 
						new TestCaseSettingsDialog(Display.getDefault().getActiveShell(), fSelectedNode, null);
				dialog.create();
				if (dialog.open() == Window.OK) {
					TestCaseNode testCase = dialog.getTestCase();
					fSelectedNode.addTestCase(testCase);
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}
		});
	}
	
	private void createRenameSuiteButton(Composite testCasesButonsComposite) {
		Button renameSuiteButton = getToolkit().createButton(testCasesButonsComposite, "Rename suite...", SWT.NONE);
		renameSuiteButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		renameSuiteButton.addSelectionListener(new SelectionAdapter() {
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
	}
	
	private void createGenerateTestSuiteButton(
			Composite testCasesButonsComposite) {
		Button generateTestSuiteButton = new Button(testCasesButonsComposite, SWT.NONE);
		generateTestSuiteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		getToolkit().adapt(generateTestSuiteButton, true, true);
		generateTestSuiteButton.setText("Generate Test Suite");
	}
	
	private void createRemoveSelectedButton(Composite testCasesButonsComposite) {
		Button removeSelectedButton = new Button(testCasesButonsComposite, SWT.NONE);
		removeSelectedButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		getToolkit().adapt(removeSelectedButton, true, true);
		removeSelectedButton.setText("Remove Selected");
		removeSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog infoDialog = new MessageDialog(Display.getDefault().getActiveShell(), 
						DialogStrings.DIALOG_REMOVE_TEST_CASES_TITLE, Display.getDefault().getSystemImage(SWT.ICON_QUESTION), 
						DialogStrings.DIALOG_REMOVE_TEST_CASES_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, new String[] {"OK", "Cancel"}, 0);
				if(infoDialog.open() == 0){
					removeTestCases(fTestCasesViewer.getCheckedElements());
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}

			private void removeTestCases(Object[] checkedElements) {
				for(Object testCase : checkedElements){
					fSelectedNode.removeChild((TestCaseNode)testCase);
				}
			}
		});

	}
	
	private void createRemoveTestSuitesButton(Composite testCasesButonsComposite) {
		Button removeTestSuiteButton = new Button(testCasesButonsComposite, SWT.NONE);
		removeTestSuiteButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		getToolkit().adapt(removeTestSuiteButton, true, true);
		removeTestSuiteButton.setText("Remove Suites...");
		removeTestSuiteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RemoveTestSuiteDialog dialog = new RemoveTestSuiteDialog(Display.getDefault().getActiveShell(), fSelectedNode.getTestSuites());
				if(dialog.open() == IDialogConstants.OK_ID){
					removeTestSuites(dialog.getCheckedElements());
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}

			private void removeTestSuites(Object[] suites) {
				for(Object suite : suites){
					fSelectedNode.removeTestSuite((String)suite);
				}
				
			}
		});
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
		fTestCasesViewer.setInput(fSelectedNode.getTestCases());
	}
}
