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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractCommentsSection extends TabFolderSection {

	private final static int STYLE = Section.TITLE_BAR | Section.COMPACT | Section.TWISTIE;
	private final static String SECTION_TITLE = "Comments";

	private class TabFolderSelectionListsner extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			refreshEditButton();
		}
	}

	private class EditCommentsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				getTargetIf().editComments();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not edit comments", e.getMessage());
			}
		}
	}

	private IFileInfoProvider fFileInfoProvider;

	private Button fEditButton;

	private AbstractNode fTarget;
	private AbstractNodeInterface fTargetIf;

	private Map<TabItem, Text> fTextItems;

	public AbstractCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);
		fFileInfoProvider = fileInfoProvider;

		if(getTabFolder() != null){
			getTabFolder().addSelectionListener(new TabFolderSelectionListsner());
		}

		fTextItems = new HashMap<TabItem, Text>();

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		getSection().setLayoutData(gd);
		getSection().setText(SECTION_TITLE);
		getSection().layout();
	}

	@Override
	public void refresh(){
		refreshEditButton();
	}

	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		createCommentsButtons();
		return client;
	}

	@Override
	protected int buttonsPosition() {
		return BUTTONS_BELOW;
	}

	protected void createCommentsButtons() {
		fEditButton = addButton("Edit comments", null);
		fEditButton.setToolTipText(Messages.TOOLTIP_EDIT_COMMENTS);
		fEditButton.addSelectionListener(createEditButtonSelectionAdapter());
	}

	protected void setInput(AbstractNode input){
		fTarget = input;
		getTargetIf().setTarget(input);
		refresh();
	}

	protected void refreshEditButton(){
		if(getTargetIf() != null && getTargetIf().getComments() != null && getTargetIf().getComments().length() > 0){
			getEditButton().setText("Edit comments");
		}else{
			getEditButton().setText("Add comments");
		}
	}

	protected AbstractNode getTarget(){
		return fTarget;
	}

	protected Button getEditButton(){
		return fEditButton;
	}

	protected AbstractNodeInterface getTargetIf(){
		if(fTargetIf == null){
			fTargetIf = new AbstractNodeInterface(getUpdateContext(), fFileInfoProvider);
		}
		return fTargetIf;
	}

	protected SelectionListener createEditButtonSelectionAdapter(){
		return new EditCommentsAdapter();
	}

	protected TabItem addTextTab(String title){
		return addTextTab(title, getTabFolder().getItemCount());
	}

	protected TabItem addTextTab(String title, int index){
		Text text = getToolkit().createText(getTabFolder(), "", SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
		text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		TabItem item = addTabItem(text, title, index);
		fTextItems.put(item, text);
		return item;
	}

	protected Text getTextFromTabItem(TabItem item){
		return fTextItems.get(item);
	}
}
