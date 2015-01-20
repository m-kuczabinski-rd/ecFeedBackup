package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceCommentsSection extends JavaDocCommentsSection {

	private Button fImportButton;
	private Button fExportButton;

	protected class ChoiceImportSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getTarget().getChoices().size() > 0){
				getTargetIf().importAllJavadocComments();
			}else{
				getTargetIf().importJavadocComments();
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
			}
		}
	}

	public ChoiceCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected boolean commentsExportable(){
		return false;
	}

	@Override
	protected void createCommentsButtons(boolean exportable) {
		super.createCommentsButtons(exportable);
		fExportButton = addButton("Export", null);
		fExportButton.addSelectionListener(new ChoiceExportSelectionAdapter());
		fImportButton = addButton("Import", null);
		fImportButton.addSelectionListener(new ChoiceImportSelectionAdapter());
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
		fExportButton.setEnabled(importExportEnabled);
		fImportButton.setEnabled(importExportEnabled);
		getJavaDocText().setEnabled(importExportEnabled);
//		removeSelectionListeners(fExportButton);
//		removeSelectionListeners(fImportButton);
		if(getTarget().isAbstract()){
			fExportButton.setText("Export all");
			fExportButton.setToolTipText(Messages.TOOLTIP_EXPORT_CHOICE_SUBTREE_COMMENTS_TO_JAVADOC);
//			fExportButton.addSelectionListener(fExportAllSelectionAdapter);
			fImportButton.setText("Import all");
			fImportButton.setToolTipText(Messages.TOOLTIP_IMPORT_CHOICE_SUBTREE_COMMENTS_FROM_JAVADOC);
//			fImportButton.addSelectionListener(fImportAllSelectionAdapter);
		}else{
			fExportButton.setText("Export");
			fExportButton.setToolTipText(Messages.TOOLTIP_EXPORT_CHOICE_COMMENTS_TO_JAVADOC);
//			fExportButton.addSelectionListener(fExportSelectionAdapter);
			fImportButton.setText("Import");
			fImportButton.setToolTipText(Messages.TOOLTIP_IMPORT_CHOICE_COMMENTS_FROM_JAVADOC);
//			fImportButton.addSelectionListener(fImportSelectionAdapter);
		}
	}
//
//	private void removeSelectionListeners(Button button) {
//		for(Listener listener : button.getListeners(SWT.Selection)){
//			button.removeListener(SWT.Selection, listener);
//		}
//	}
}
