package com.testify.ecfeed.dialogs;

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;

import com.testify.ecfeed.constants.DialogStrings;

public class RemoveTestSuiteDialog extends TitleAreaDialog {
	private Table testSuitesTable;
	private Set<String> fTestSuites;
	private Object[] fSelectedSuites;
	private CheckboxTableViewer fTestSuitesViewer;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RemoveTestSuiteDialog(Shell parentShell, Set<String> testSuites) {
		super(parentShell);
		setHelpAvailable(false);
		fTestSuites = testSuites;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(DialogStrings.DIALOG_REMOVE_TEST_SUITES_TITLE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblSelectTestSuites = new Label(container, SWT.NONE);
		lblSelectTestSuites.setText(DialogStrings.DIALOG_REMOVE_TEST_SUITES_MESSAGE);
		
		fTestSuitesViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		testSuitesTable = fTestSuitesViewer.getTable();
		testSuitesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		testSuitesTable.setHeaderVisible(true);
		fTestSuitesViewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn testSuiteViewerColumn = new TableViewerColumn(fTestSuitesViewer, SWT.NONE);
		TableColumn testSuiteColumn = testSuiteViewerColumn.getColumn();
		testSuiteViewerColumn.setLabelProvider(new ColumnLabelProvider());
		testSuiteColumn.setWidth(100);
		testSuiteColumn.setText("Test suite");
		
		fTestSuitesViewer.setInput(fTestSuites);

		return area;
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
		return new Point(450, 500);
	}
	
	@Override
	public void okPressed(){
		fSelectedSuites = fTestSuitesViewer.getCheckedElements();
		super.okPressed();
	}

	public Object[] getCheckedElements() {
		return fSelectedSuites;
	}

}
