package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.common.JavaDocSupport;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterCommentsSection extends JavaDocCommentsSection {

	private TabItem fParameterCommentsTab;

	protected class ImportTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().importTypeJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getTypeCommentsTab()));
		}
	}

	protected class ImportFullTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().importFullTypeJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getTypeCommentsTab()));
		}
	}

	protected class ExportTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().exportTypeJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getTypeJavadocTab()));
		}
	}

	protected class ExportFullTypeSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().exportFullTypeJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getTypeJavadocTab()));
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

	public AbstractParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fParameterCommentsTab = addTextTab("Parameter", 0);
		getTypeCommentsTab().setText("Type");
		getTypeJavadocTab().setText("Type javadoc");

		addEditListener(new EditButtonListener());
	}

	@Override
	protected void createExportMenuItems() {
		MenuItem exportAllItem = new MenuItem(getExportButtonMenu(), SWT.NONE);
		exportAllItem.setText("Export type comments with choices");
		exportAllItem.addSelectionListener(new ExportFullTypeSelectionAdapter());
		MenuItem exportTypeItem = new MenuItem(getExportButtonMenu(), SWT.NONE);
		exportTypeItem.setText("Export only type comments");
		exportTypeItem.addSelectionListener(new ExportTypeSelectionAdapter());
	}

	@Override
	protected void createImportMenuItems() {
		MenuItem importAllItem = new MenuItem(getImportButtonMenu(), SWT.NONE);
		importAllItem.setText("Import type and choices comments");
		importAllItem.addSelectionListener(new ImportFullTypeSelectionAdapter());
		MenuItem importTypeItem = new MenuItem(getImportButtonMenu(), SWT.NONE);
		importTypeItem.setText("Import only type comments");
		importTypeItem.addSelectionListener(new ImportTypeSelectionAdapter());
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
		return getCommentsItem();
	}

	protected TabItem getTypeJavadocTab(){
		return getJavaDocItem();
	}

	@Override
	protected abstract AbstractParameterInterface getTargetIf();

	@Override
	protected boolean commentsExportable(){
		return true;
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
	}
}
