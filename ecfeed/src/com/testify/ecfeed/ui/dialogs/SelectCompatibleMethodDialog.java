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

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.ui.common.Messages;

public class SelectCompatibleMethodDialog extends TitleAreaDialog {
	private MethodNode fSelectedMethod = null;
	private TableViewer fMethodViewer;
	private Button fOkButton;
	private List<MethodNode> fCompatibleMethods;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectCompatibleMethodDialog(Shell parentShell, List<MethodNode> compatibleMethods) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		setHelpAvailable(false);
		fCompatibleMethods = compatibleMethods;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.DIALOG_RENAME_METHOD_TITLE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Text infoText = new Text(container, SWT.READ_ONLY | SWT.WRAP);
		infoText.setText(Messages.DIALOG_RENAME_METHOD_MESSAGE);
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
		fMethodViewer.setInput(fCompatibleMethods);
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

//	private List<MethodNode> getCompatibleMethods(){
//		List<MethodNode> compatibleMethods = new ArrayList<MethodNode>();
//		for(MethodNode m : ClassInterface.getOtherMethods(fRenamedMethod.getClassNode())){
//			if(m.getParametersTypes().equals(fRenamedMethod.getParametersTypes())){
//				compatibleMethods.add(m);
//			}
//		}
//		return compatibleMethods;
//	}
}
