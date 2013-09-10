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

package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.utils.EcModelUtils;

import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ColumnPixelData;

public class TestMethodRenameDialog extends TitleAreaDialog {
	private MethodNode fRenamedMethod;
	private MethodNode fSelectedMethod = null;
	private TableViewer fMethodViewer;
	private Button fOkButton;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TestMethodRenameDialog(Shell parentShell, MethodNode method) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		setHelpAvailable(false);
		fRenamedMethod = method;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(DialogStrings.DIALOG_RENAME_METHOD_TITLE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Text infoText = new Text(container, SWT.READ_ONLY | SWT.WRAP);
		infoText.setText(DialogStrings.DIALOG_RENAME_METHOD_MESSAGE);
		infoText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		infoText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Composite tableComposite = new Composite(container, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		TableColumnLayout tableCompositeLayout = new TableColumnLayout();
		tableComposite.setLayout(tableCompositeLayout);
		
		fMethodViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		Table methodsTable = fMethodViewer.getTable();
		methodsTable.setHeaderVisible(true);
		methodsTable.setLinesVisible(true);
		
		TableViewerColumn methodViewerColumn = new TableViewerColumn(fMethodViewer, SWT.NONE);
		TableColumn methodColumn = methodViewerColumn.getColumn();
		tableCompositeLayout.setColumnData(methodColumn, new ColumnPixelData(150, true, true));
		methodColumn.setText("Method");
		fMethodViewer.setContentProvider(new ArrayContentProvider());
		fMethodViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				return ((MethodNode)element).toString();
			}
		});
		fMethodViewer.setInput(EcModelUtils.getCompatibleMethods(fRenamedMethod));
		fMethodViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)fMethodViewer.getSelection();
				fSelectedMethod = (MethodNode)selection.getFirstElement();
				fOkButton.setEnabled(true);
			}
		});
		fMethodViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection)fMethodViewer.getSelection();
				fSelectedMethod = (MethodNode)selection.getFirstElement();
				okPressed();
			}
		});

		return area;
	}
	
	public MethodNode getSelectedMethod(){
		return fSelectedMethod;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		fOkButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

}
