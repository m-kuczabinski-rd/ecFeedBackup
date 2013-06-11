package com.testify.ecfeed.dialogs;

import java.util.Vector;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import org.eclipse.swt.widgets.Combo;

public class EditTestCaseDialog extends TitleAreaDialog {

	private TestCaseNode fTestCase;
	private MethodNode fParentMethod;
	private TableViewer fViewer;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EditTestCaseDialog(Shell parentShell, MethodNode parentMethod, TestCaseNode testCase) {
		super(parentShell);
		fTestCase = testCase;
		if(fTestCase == null){
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

		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
				| SWT.SEPARATOR);
		GridData gd_titleBarSeparator = new GridData(GridData.FILL_HORIZONTAL);
		gd_titleBarSeparator.horizontalSpan = 2;
		titleBarSeparator.setLayoutData(gd_titleBarSeparator);
		
		Label testSuiteLabel = new Label(composite, SWT.NONE);
		GridData gd_testSuiteLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_testSuiteLabel.widthHint = 97;
		testSuiteLabel.setLayoutData(gd_testSuiteLabel);
		testSuiteLabel.setText("Test suite: ");
		
		Combo testSuiteNameCombo = new Combo(composite, SWT.NONE);
		testSuiteNameCombo.setItems(fParentMethod.getTestSuiteNames().toArray(new String[0]));
		testSuiteNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		testSuiteNameCombo.setText(fTestCase.getName());
				
		Label titleBarSeparator2 = new Label(composite, SWT.HORIZONTAL
				| SWT.SEPARATOR);
		GridData gd_titleBarSeparator2 = new GridData(GridData.FILL_HORIZONTAL);
		gd_titleBarSeparator2.horizontalSpan = 2;
		titleBarSeparator2.setLayoutData(gd_titleBarSeparator2);
		
		createTestDataViewer(composite);

		return composite;
	}

	private void createTestDataViewer(Composite composite) {
		fViewer = new TableViewer(composite, SWT.SINGLE|SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION|SWT.BORDER);
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
		col.setEditingSupport(new TestCasePartitionEditingSupport(fViewer, fTestCase.getTestData()));
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

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public TestCaseNode getTestCase() {
		return fTestCase;
	}
}
