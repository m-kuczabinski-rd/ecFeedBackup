package com.testify.ecfeed.editors;

import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.dialogs.ISetValueListener;
import com.testify.ecfeed.dialogs.TestCasePartitionEditingSupport;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.utils.EcModelUtils;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.layout.RowLayout;

public class TestCaseNodeDetailsPage extends GenericNodeDetailsPage implements ISetValueListener{
	private TestCaseNode fSelectedNode;
	private Section fMainSection;
	private MethodNode fParent;
	private Combo fTestSuiteNameCombo;
	private TableViewer fTestDataViewer;
	private TableViewerColumn fPartitionViewerColumn;

	/**
	 * Create the details page.
	 */
	public TestCaseNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
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
		Table testDataTable = fTestDataViewer.getTable();
		testDataTable.setHeaderVisible(true);
		testDataTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(testDataTable);
		
		createTableViewerColumn(fTestDataViewer, "Parameter", 155, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				CategoryNode parent = (CategoryNode)testValue.getParent();
				return parent.toString();
			}
		});
		
		fPartitionViewerColumn = createTableViewerColumn(fTestDataViewer, "Partition", 110, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				return testValue.getName();
			}
		}); 
				
		createTableViewerColumn(fTestDataViewer, "Represented value", 100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				return testValue.getValueString();
			}
		});
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
				fParent.removeChild(fSelectedNode);
				getParentBlock().selectNode(fParent);
				fSelectedNode = null;
				updateModel(fParent);
			}
		});
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.getFirstElement() instanceof TestCaseNode){
			fSelectedNode = (TestCaseNode)structuredSelection.getFirstElement();
			refresh();
		}
	}

	public void refresh() {
		if(fSelectedNode == null){
			return;
		}
		fParent = (MethodNode)fSelectedNode.getParent();
		fMainSection.setText(fSelectedNode.toString());
		fTestSuiteNameCombo.setItems(fParent.getTestSuites().toArray(new String[]{}));
		fTestSuiteNameCombo.setText(fSelectedNode.getName());
		fTestDataViewer.setInput(fSelectedNode.getTestData());
		EditingSupport editingSupport = new TestCasePartitionEditingSupport(fTestDataViewer, fSelectedNode.getTestData(), this);
		fPartitionViewerColumn.setEditingSupport(editingSupport);
	}

	private void renameTestCase() {
		String newName = fTestSuiteNameCombo.getText();
		if(EcModelUtils.validateTestSuiteName(newName)){
			fSelectedNode.setName(newName);
			updateModel(fSelectedNode);
		}
		else{
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					DialogStrings.DIALOG_TEST_SUITE_NAME_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					DialogStrings.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
			fTestSuiteNameCombo.setText(fSelectedNode.getName());
		}
	}

	@Override
	public void setValue(Vector<PartitionNode> testData) {
		updateModel(fSelectedNode);
	}

}
