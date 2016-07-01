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

package com.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabItem;

import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.common.JavaDocSupport;
import com.testify.ecfeed.ui.common.Messages;

public abstract class AbstractParameterCommentsSection extends JavaDocCommentsSection {

	private TabItem fParameterCommentsTab;

	protected class ImportTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importTypeJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getTypeCommentsTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

	protected class ImportFullTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importFullTypeJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getTypeCommentsTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

	protected class ExportTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportTypeJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getTypeJavadocTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_EXPORT, e.getMessage());
			}
		}
	}

	protected class ExportFullTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportFullTypeJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getTypeJavadocTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_EXPORT, e.getMessage());
			}
		}
	}

	protected class EditButtonListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getActiveItem() == fParameterCommentsTab){
				getTargetIf().editComments();
			}
			else if(getActiveItem() == getTypeCommentsTab() || getActiveItem() == getTypeJavadocTab()){
				getTargetIf().editTypeComments();
			}
		}
	}

	public AbstractParameterCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider);

		fParameterCommentsTab = addTextTab("Parameter", 0);
		getTypeCommentsTab().setText("Type");
		getTypeJavadocTab().setText("Type javadoc");
	}

	@Override
	public void refresh(){
		super.refresh();

		String javadoc = JavaDocSupport.getTypeJavadoc(getTarget());
		getTextFromTabItem(getTypeJavadocTab()).setText(javadoc != null ? javadoc : "");

		if(getTargetIf().getComments() != null){
			getTextFromTabItem(fParameterCommentsTab).setText(getTargetIf().getComments());
		}else{
			getTextFromTabItem(fParameterCommentsTab).setText("");
		}
		if(getTargetIf().getTypeComments() != null){
			getTextFromTabItem(getTypeCommentsTab()).setText(getTargetIf().getTypeComments());
		}else{
			getTextFromTabItem(getTypeCommentsTab()).setText("");
		}

		boolean importExportEnabled = getTargetIf().commentsImportExportEnabled();
		getExportButton().setEnabled(importExportEnabled);
		getImportButton().setEnabled(importExportEnabled);
	}

	@Override
	public AbstractParameterNode getTarget(){
		return (AbstractParameterNode)super.getTarget();
	}

	public void setInput(AbstractParameterNode input){
		super.setInput(input);
		getTargetIf().setTarget(input);
	}

	@Override
	protected void refreshEditButton() {
		TabItem activeItem = getActiveItem();
		boolean enabled = true;
		if(activeItem == getTypeCommentsTab() || activeItem == getTypeJavadocTab()){
			if(JavaUtils.isPrimitive(getTarget().getType())){
				enabled = false;
			}
		}
		getEditButton().setEnabled(enabled);

		AbstractParameterInterface targetIf = getTargetIf();
		String editButtonText;
		if(getActiveItem() == getTypeCommentsTab() || getActiveItem() == getTypeJavadocTab()){
			if(targetIf.getTypeComments() != null && targetIf.getTypeComments().length() > 0){
				editButtonText = "Edit type comments";
			}else{
				editButtonText = "Add type comments";
			}
		}else{
			if(targetIf.getComments() != null && targetIf.getComments().length() > 0){
				editButtonText = "Edit comments";
			}else{
				editButtonText = "Add comments";
			}
		}
		getEditButton().setText(editButtonText);
		getButtonsComposite().layout();
	}

	@Override
	protected SelectionAdapter createEditButtonSelectionAdapter(){
		return new EditButtonListener();
	}

	protected TabItem getParameterCommentsTab(){
		return fParameterCommentsTab;
	}

	protected TabItem getTypeCommentsTab(){
		return getCommentsItem();
	}

	protected TabItem getTypeJavadocTab(){
		return getJavaDocItem();
	}

	@Override
	protected abstract AbstractParameterInterface getTargetIf();

}
