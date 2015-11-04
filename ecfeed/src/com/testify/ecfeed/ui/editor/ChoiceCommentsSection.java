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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceCommentsSection extends JavaDocCommentsSection {

	protected class ChoiceImportSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getTarget().getChoices().size() > 0){
				getTargetIf().importAllJavadocComments();
			}else{
				getTargetIf().importJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getCommentsItem()));
			}
		}
	}

	protected class ChoiceExportSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getTarget().getChoices().size() > 0){
				getTargetIf().exportAllComments();
			}else{
				getTargetIf().exportCommentsToJavadoc(getTarget().getDescription());
				getTabFolder().setSelection(getTabFolder().indexOf(getJavaDocItem()));
			}
		}
	}

	public ChoiceCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider);
	}

	@Override
	protected void createCommentsButtons() {
		super.createCommentsButtons();
	}

	@Override
	protected SelectionListener createExportButtonSelectionListener(){
		return new ChoiceExportSelectionAdapter();
	}

	@Override
	protected SelectionListener createImportButtonSelectionListener(){
		return new ChoiceImportSelectionAdapter();
	}

	@Override
	public void refresh(){
		super.refresh();
		updateExportImportButtons();
	}

	@Override
	protected ChoiceNode getTarget(){
		return (ChoiceNode)super.getTarget();
	}

	private void updateExportImportButtons() {
		boolean importExportEnabled = getTargetIf().commentsImportExportEnabled();
		getExportButton().setEnabled(importExportEnabled);
		getImportButton().setEnabled(importExportEnabled);
		getJavaDocText().setEnabled(importExportEnabled);
		if(getTarget().isAbstract()){
			getExportButton().setText("Export all");
			getExportButton().setToolTipText(Messages.TOOLTIP_EXPORT_CHOICE_SUBTREE_COMMENTS_TO_JAVADOC);
			getImportButton().setText("Import all");
			getImportButton().setToolTipText(Messages.TOOLTIP_IMPORT_CHOICE_SUBTREE_COMMENTS_FROM_JAVADOC);
		}else{
			getExportButton().setText("Export");
			getExportButton().setToolTipText(Messages.TOOLTIP_EXPORT_CHOICE_COMMENTS_TO_JAVADOC);
			getImportButton().setText("Import");
			getImportButton().setToolTipText(Messages.TOOLTIP_IMPORT_CHOICE_COMMENTS_FROM_JAVADOC);
		}
	}
}
