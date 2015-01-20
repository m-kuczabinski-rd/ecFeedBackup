package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterCommentsSection extends TabFolderCommentsSection {

	private TabItem fParameterCommentsTab;
	private TabItem fTypeCommentsTab;

	protected class ImportTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().importTypeJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(fTypeCommentsTab));
		}
	}

	protected class ExportTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().exportTypeJavadocComments();
		}
	}

	protected class EditButtonListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getActiveItem() == fParameterCommentsTab){
				getTargetIf().editComments();
			}
			else if(getActiveItem() == fTypeCommentsTab){
				getTargetIf().editTypeComments();
			}
		}
	}

	public AbstractParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fParameterCommentsTab = addTextTab("Parameter", true);
		fTypeCommentsTab = addTextTab("Type", true);

		addEditListener(new EditButtonListener());
	}

	@Override
	protected void createExportMenuItems() {
		MenuItem exportTypeItem = new MenuItem(getExportButtonMenu(), SWT.NONE);
		exportTypeItem.setText("Export type comments");
		exportTypeItem.addSelectionListener(new ExportTypeSelectionAdapter());
		MenuItem exportAllItem = new MenuItem(getExportButtonMenu(), SWT.NONE);
		exportAllItem.setText("Export type and choices comments");
	}

	@Override
	protected void createImportMenuItems() {
		MenuItem importTypeItem = new MenuItem(getImportButtonMenu(), SWT.NONE);
		importTypeItem.setText("Import type comments");
		importTypeItem.addSelectionListener(new ImportTypeSelectionAdapter());
		MenuItem importAllItem = new MenuItem(getImportButtonMenu(), SWT.NONE);
		importAllItem.setText("Import type and choices comments");
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getTargetIf().getComments() != null){
			getTextFromTabItem(fParameterCommentsTab).setText(getTargetIf().getComments());
		}else{
			getTextFromTabItem(fParameterCommentsTab).setText("");
		}
		if(getTargetIf().getTypeComments() != null){
			getTextFromTabItem(fTypeCommentsTab).setText(getTargetIf().getTypeComments());
		}else{
			getTextFromTabItem(fTypeCommentsTab).setText("");
		}

		boolean importExportEnabled = getTargetIf().commentsExportable();
		getExportButton().setEnabled(importExportEnabled);
		getImportButton().setEnabled(importExportEnabled);
	}

	public void setInput(AbstractParameterNode input){
		super.setInput(input);
		getTargetIf().setTarget(input);
	}

	@Override
	public AbstractParameterNode getTarget(){
		return (AbstractParameterNode)super.getTarget();
	}

	protected TabItem getParameterCommentsTab(){
		return fParameterCommentsTab;
	}

	protected TabItem getTypeCommentsTab(){
		return fTypeCommentsTab;
	}

	@Override
	protected abstract AbstractParameterInterface getTargetIf();

	@Override
	protected boolean commentsExportable(){
		return true;
	}

}
