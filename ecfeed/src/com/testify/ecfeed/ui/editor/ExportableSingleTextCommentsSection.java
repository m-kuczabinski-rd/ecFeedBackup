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
import org.eclipse.swt.widgets.Button;

import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ExportableSingleTextCommentsSection extends SingleTextCommentsSection {

	private class ExportAllSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportAllComments();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not export.", e.getMessage());
			}
		}
	}

	private class ImportAllSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importAllJavadocComments();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not import.", e.getMessage());
			}
		}
	}

	private Button fExportButton;
	private Button fImportButton;

	public ExportableSingleTextCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider);
	}

	@Override
	protected void createCommentsButtons() {
		super.createCommentsButtons();
		fExportButton = addButton("Export all", null);
		fExportButton.setToolTipText(Messages.TOOLTIP_EXPORT_SUBTREE_COMMENTS_TO_JAVADOC);
		fExportButton.addSelectionListener(new ExportAllSelectionAdapter());
		fImportButton = addButton("Import all", null);
		fImportButton.setToolTipText(Messages.TOOLTIP_IMPORT_SUBTREE_COMMENTS_FROM_JAVADOC);
		fImportButton.addSelectionListener(new ImportAllSelectionAdapter());
	}

}
