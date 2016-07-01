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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class TabFolderSection extends ButtonsCompositeSection {

	private TabFolder fTabFolder;

	public TabFolderSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider,
			int style) {
		super(sectionContext, updateContext, fileInfoProvider, style);
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
