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

	@Override
	protected Composite createMainControlComposite(Composite parent) {
		Composite composite = super.createMainControlComposite(parent);
		fTabFolder = new TabFolder(getMainControlComposite(), SWT.BOTTOM);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		fTabFolder.setLayoutData(gd);
		return composite;
	}

	protected TabFolder getTabFolder(){
		return fTabFolder;
	}

	protected TabItem addTabItem(Control control, String title){
		return addTabItem(control, title, fTabFolder.getItems().length);
	}

	protected TabItem addTabItem(Control control, String title, int index){
		TabItem item = new TabItem(fTabFolder, SWT.NONE, index);
		item.setText(title);
		item.setControl(control);
		return item;
	}

	protected TabItem getActiveItem(){
		return fTabFolder.getItem(fTabFolder.getSelectionIndex());
	}
}
