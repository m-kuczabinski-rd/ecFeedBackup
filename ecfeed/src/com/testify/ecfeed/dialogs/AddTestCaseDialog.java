package com.testify.ecfeed.dialogs;

import java.util.Vector;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.EcModelUtils;

public class AddTestCaseDialog extends TitleAreaDialog implements ISetValueListener {

	private MethodNode fMethod;
	private Vector<PartitionNode> fTestData;
	private String fTestSuiteName;
	private Combo fTestSuiteCombo;
	private Button fOkButton;
	private TableViewer fTestDataViewer;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddTestCaseDialog(Shell parentShell, MethodNode method) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		fTestData = new Vector<PartitionNode>();
		Vector<CategoryNode> categories = method.getCategories();
		for(CategoryNode category : categories){
			fTestData.add((PartitionNode)category.getChildren().elementAt(0));
		}
		fMethod = method;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(DialogStrings.DIALOG_ADD_TEST_CASE_TITLE);
		setMessage(DialogStrings.DIALOG_ADD_TEST_CASE_MESSAGE);
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
		categoryColumn.setText("Category");
		categoryViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override 
			public String getText(Object element){
				return ((PartitionNode)element).getParent().toString();
			}
		});
		
		TableViewerColumn partitionViewerColumn = new TableViewerColumn(fTestDataViewer, SWT.NONE);
		TableColumn partitionColumn = partitionViewerColumn.getColumn();
		partitionColumn.setWidth(150);
		partitionColumn.setText("Partition");
		partitionViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override 
			public String getText(Object element){
				return ((PartitionNode)element).getName();
			}
		});
		partitionViewerColumn.setEditingSupport(new TestCasePartitionEditingSupport(fTestDataViewer, fTestData, this));

		TableViewerColumn valueViewerColumn = new TableViewerColumn(fTestDataViewer, SWT.NONE);
		TableColumn valueColumn = valueViewerColumn.getColumn();
		valueColumn.setWidth(150);
		valueColumn.setText("Value");
		valueViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override 
			public String getText(Object element){
				return ((PartitionNode)element).getValueString();
			}
		});

		fTestDataViewer.setContentProvider(new ArrayContentProvider());
		fTestDataViewer.setInput(fTestData);
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
		if(!EcModelUtils.validateTestSuiteName(fTestSuiteCombo.getText())){
			setErrorMessage(DialogStrings.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
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
	public void setValue(Vector<PartitionNode> testData) {
		fTestDataViewer.refresh();
	}
	
	public String getTestSuite(){
		return fTestSuiteName;
	}
	
	public Vector<PartitionNode> getTestData(){
		return fTestData;
	}
}
