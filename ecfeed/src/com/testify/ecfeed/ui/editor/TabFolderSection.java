package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class TabFolderSection extends ButtonsCompositeSection {

	private TabFolder fTabFolder;

	public TabFolderSection(ISectionContext sectionContext, IModelUpdateContext updateContext, int style) {
		super(sectionContext, updateContext, style);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		getSection().setLayoutData(gd);
	}

	protected TabFolder getTabFolder(){
		return fTabFolder;
	}

	protected void addTabItem(Control control, String title){
		TabItem item = new TabItem(fTabFolder, SWT.NONE);
		item.setText(title);
		item.setControl(control);
	}

	protected TabItem addTabItem(Control control, String title, int index){
		TabItem item = new TabItem(fTabFolder, SWT.NONE, index);
		item.setText(title);
		item.setControl(control);
		return item;
	}

	@Override
	protected Composite createMainControlComposite(Composite parent) {
		Composite composite = super.createMainControlComposite(parent);
		fTabFolder = new TabFolder(getMainControlComposite(), SWT.BOTTOM);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		fTabFolder.setLayoutData(gd);
		return composite;
	}

	protected TabItem getActiveItem(){
		return fTabFolder.getItem(fTabFolder.getSelectionIndex());
	}
}
