package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Button;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelCommentsSection extends SingleTextCommentsSection {

	private Button fExportButton;
	private Button fImportButton;

	public ModelCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected void createCommentsButtons(boolean exportable) {
		super.createCommentsButtons(exportable);
		fExportButton = addButton("Export all", null);
		fExportButton.setToolTipText(Messages.TOOLTIP_EXPORT_SUBTREE_COMMENTS_TO_JAVADOC);
		fExportButton.addSelectionListener(new ExportAllSelectionAdapter());
		fImportButton = addButton("Import all", null);
		fImportButton.setToolTipText(Messages.TOOLTIP_IMPORT_SUBTREE_COMMENTS_FROM_JAVADOC);
		fImportButton.addSelectionListener(new ImportAllSelectionAdapter());
	}

}
