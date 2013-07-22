package com.testify.ecfeed.dialogs;

import java.util.Vector;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import org.eclipse.swt.widgets.Combo;

public class TestCaseSettingsDialog extends TitleAreaDialog implements ISetValueListener {

	private TestCaseNode fTestCase;
	private MethodNode fParentMethod;
	private TableViewer fViewer;
	private boolean fNewTestCase;
	private Button fOkButton;
	private String fErrorMessage;
	private Combo fTestSuiteNameCombo;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TestCaseSettingsDialog(Shell parentShell, MethodNode parentMethod, TestCaseNode testCase) {
		super(parentShell);
		setHelpAvailable(false);
		fTestCase = testCase;
		fNewTestCase = (fTestCase == null);
		if(fNewTestCase){
			Vector<PartitionNode> testParameters = new Vector<PartitionNode>();
			Vector<CategoryNode> categories = parentMethod.getCategories();
			for(CategoryNode category : categories){
				testParameters.add((PartitionNode)category.getChildren().elementAt(0));
			}
			fTestCase = new TestCaseNode(Constants.DEFAULT_TEST_SUITE_NAME, testParameters);
		}
		fParentMethod = parentMethod;
	}

	@Override
	public void create(){
		super.create();
		setTitle(fNewTestCase?"New test case":"Edit test case");
		setMessage("Set test suite name and edit test data");
	}
	
	@Override
	public Control createDialogArea(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 15;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setFont(parent.getFont());

		createTitleBarSeparator(composite);
		
		createLabel(composite, "Test suite: ");
		createSuiteNameCombo(composite);
		createTitleBarSeparator(composite);
		createTestDataViewer(composite);
		new Label(composite, SWT.NONE);

		return composite;
	}

	public TestCaseNode getTestCase() {
		return fTestCase;
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

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(491, 374);
	}

	private void createSuiteNameCombo(Composite composite) {
		fTestSuiteNameCombo = new Combo(composite, SWT.NONE);
		fTestSuiteNameCombo.setItems(fParentMethod.getTestSuites().toArray(new String[0]));
		fTestSuiteNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fTestSuiteNameCombo.setText(fTestCase.getName());
		fTestSuiteNameCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				boolean inputValid = verifyInput();
				if(inputValid){
					fTestCase.setName(fTestSuiteNameCombo.getText());
					setErrorMessage(null);
				}
			}
		});
	}

	private boolean verifyInput(){
		boolean result = true;
		result &= verifyTestSuiteName();
		if(result){
			fOkButton.setEnabled(true);
		}
		else{
			setErrorMessage(fErrorMessage);
			fOkButton.setEnabled(false);
		}
		return result;
	}
	
	private boolean verifyTestSuiteName() {
		String testSuiteName = fTestSuiteNameCombo.getText();
		if(testSuiteName.length() == 0 || testSuiteName.length() > 64){
			fErrorMessage = DialogStrings.DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE;
			return false;
		}
		return true;
	}

	private void createLabel(Composite composite, String labelText) {
		Label label = new Label(composite, SWT.NONE);
		GridData gdLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gdLabel.widthHint = 97;
		label.setLayoutData(gdLabel);
		label.setText(labelText);
	}

	private void createTitleBarSeparator(Composite composite) {
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
				| SWT.SEPARATOR);
		GridData gd_titleBarSeparator = new GridData(GridData.FILL_HORIZONTAL);
		gd_titleBarSeparator.horizontalSpan = 2;
		titleBarSeparator.setLayoutData(gd_titleBarSeparator);
	}

	private void createTestDataViewer(Composite composite) {
		fViewer = new TableViewer(composite, SWT.SINGLE|SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION|SWT.BORDER|SWT.FULL_SELECTION);
		createColumns(composite, fViewer);
		final Table table = fViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		fViewer.setContentProvider(new ArrayContentProvider());
		fViewer.setInput(fTestCase.getTestData());
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		fViewer.getControl().setLayoutData(gridData);
	}

	private void createColumns(Composite composite, TableViewer tableViwer) {
		TableViewerColumn col = createTableViewerColumn("Category", 150, 0);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((PartitionNode)element).getParent().getName();
			}
		});
		
		col = createTableViewerColumn("Partition", 150, 0);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((PartitionNode)element).getName();
			}
		});
		col.setEditingSupport(new TestCasePartitionEditingSupport(fViewer, fTestCase.getTestData(), this));

		col = createTableViewerColumn("Value", 150, 0);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				Object value = ((PartitionNode)element).getValue();
				if(value == null){
					return "null";
				}
				return value.toString();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int width, final int columnNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(fViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(true);
		
		return viewerColumn;
	}

	@Override
	public void setValue(Vector<PartitionNode> testData) {
	}
}
