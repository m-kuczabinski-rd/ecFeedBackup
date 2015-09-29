/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.common.JavaDocSupport;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.editor.utils.ExceptionCatchDialog;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterCommentsSection extends AbstractParameterCommentsSection {

	private IFileInfoProvider fFileInfoProvider;
	private MethodParameterInterface fTargetIf;

	private TabItem fParameterJavadocTab;

	private MenuItem fExportAllItem;
	private MenuItem fExportParameterCommentsItem;
	private MenuItem fExportTypeCommentsItem;
	private MenuItem fImportAllItem;
	private MenuItem fImportParameterCommentsItem;
	private MenuItem fImportTypeCommentsItem;

	protected class ImportParameterCommentsSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getParameterCommentsTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.display(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

	protected class ExportParameterCommentsSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportCommentsToJavadoc(getTarget().getDescription());
				getTabFolder().setSelection(getTabFolder().indexOf(fParameterJavadocTab));
			} catch (Exception e) {
				ExceptionCatchDialog.display(Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS, e.getMessage());
			}
		}
	}

	protected class ExportAllParameterCommentsSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportAllComments();
				getTabFolder().setSelection(getTabFolder().indexOf(fParameterJavadocTab));
			} catch (Exception e) {
				ExceptionCatchDialog.display(Messages.EXCEPTION_CAN_NOT_EXPORT, e.getMessage());
			}
		}
	}

	protected class ImportAllParameterCommentsSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().importAllJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getParameterCommentsTab()));
		}
	}


	public MethodParameterCommentsSection(
			ISectionContext sectionContext,
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;

		int typeJavadocTabIndex = Arrays.asList(getTabFolder().getItems()).indexOf(getTypeJavadocTab());
		fParameterJavadocTab = addTextTab("Parameter javadoc", typeJavadocTabIndex);
	}

	@Override
	public MethodParameterNode getTarget(){
		return (MethodParameterNode)super.getTarget();
	}

	@Override
	public void refresh(){
		super.refresh();
		if(JavaDocSupport.getJavadoc(getTarget())!= null){
			getTextFromTabItem(fParameterJavadocTab).setText(JavaDocSupport.getJavadoc(getTarget()));
		}else{
			getTextFromTabItem(fParameterJavadocTab).setText("");
		}
		if(JavaDocSupport.getTypeJavadoc(getTarget()) != null){
			getTextFromTabItem(getTypeJavadocTab()).setText(JavaDocSupport.getTypeJavadoc(getTarget()));
		}else{
			getTextFromTabItem(getTypeJavadocTab()).setText("");
		}
		refreshMenuItems();
	}

	@Override
	protected MethodParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new MethodParameterInterface(getUpdateContext(), fFileInfoProvider);
		}
		return fTargetIf;
	}

	@Override
	protected void createExportMenuItems() {
		fExportAllItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 0);
		fExportAllItem.setText("Export all");
		fExportAllItem.addSelectionListener(new ExportAllParameterCommentsSelectionAdapter()); 
		fExportParameterCommentsItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 1);
		fExportParameterCommentsItem.setText("Export method comments");
		fExportParameterCommentsItem.addSelectionListener(new ExportParameterCommentsSelectionAdapter());
		fExportTypeCommentsItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 1);
		fExportTypeCommentsItem.setText("Export only type comments");
		fExportTypeCommentsItem.addSelectionListener(new ExportFullTypeSelectionAdapter());
	}

	@Override
	protected void createImportMenuItems() {
		fImportAllItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 0);
		fImportAllItem.setText("Import all");
		fImportAllItem.addSelectionListener(new ImportAllParameterCommentsSelectionAdapter());
		fImportParameterCommentsItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 1);
		fImportParameterCommentsItem.setText("Import only parameter comments");
		fImportParameterCommentsItem.addSelectionListener(new ImportParameterCommentsSelectionAdapter());
		fImportTypeCommentsItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 1);
		fImportTypeCommentsItem.setText("Import only type comments");
		fImportTypeCommentsItem.addSelectionListener(new ImportFullTypeSelectionAdapter());
	}

	private void refreshMenuItems() {
		EImplementationStatus methodStatus = getTargetIf().getImplementationStatus(getTarget().getMethod());
		boolean parameterCommentsExportEnabled = methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
		boolean typeCommentsExportEnabled = JavaUtils.isUserType(getTarget().getType()) && getTargetIf().getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED && getTarget().isLinked() == false;
		fExportParameterCommentsItem.setEnabled(parameterCommentsExportEnabled);
		fImportParameterCommentsItem.setEnabled(parameterCommentsExportEnabled);
		fExportTypeCommentsItem.setEnabled(typeCommentsExportEnabled);
		fImportTypeCommentsItem.setEnabled(typeCommentsExportEnabled);
		fExportAllItem.setEnabled(typeCommentsExportEnabled && parameterCommentsExportEnabled);
		fImportAllItem.setEnabled(typeCommentsExportEnabled && parameterCommentsExportEnabled);
	}

	@Override
	protected void refreshEditButton() {
		super.refreshEditButton();
		TabItem item = getActiveItem();
		if(item == getTypeCommentsTab() || item == getTypeJavadocTab()){
			if(getTarget().isLinked()){
				getEditButton().setEnabled(false);
			}
		}
	}

}
