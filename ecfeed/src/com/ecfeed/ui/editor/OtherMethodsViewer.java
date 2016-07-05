/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class OtherMethodsViewer extends CheckboxTableViewerSection {

	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private Button fAddSelectedButton;
	private ClassInterface fClassIf;

	private class AddSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				fClassIf.addMethods(getSelectedMethods());
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add selected items.", e.getMessage());
			}
		}
	}

	public OtherMethodsViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);
		fClassIf = new ClassInterface(this, fileInfoProvider);
		fAddSelectedButton = addButton("Add selected", new AddSelectedAdapter());
	}

	public void setInput(ClassNode classNode){
		fClassIf.setTarget(classNode);
		setText("Other methods in " + JavaUtils.getLocalName(classNode));
		super.setInput(fClassIf.getOtherMethods());
	}

	public int getItemsCount(){
		return fClassIf.getOtherMethods().size();
	}

	public List<MethodNode> getSelectedMethods() {
		List<MethodNode> methods = new ArrayList<MethodNode>();
		for(Object object : getCheckboxViewer().getCheckedElements()){
			if(object instanceof MethodNode){
				methods.add((MethodNode)object);
			}
		}
		return methods;
	}

	@Override
	protected void createTableColumns() {
	}

	@Override
	protected int viewerStyle(){
		return VIEWER_STYLE;
	}

	protected void onSelectionChanged() {
		refresh();
	}

	@Override
	public void refresh() {
		fAddSelectedButton.setEnabled(isAddSelectedButtonEnabled());
	}

	private boolean isAddSelectedButtonEnabled(){
		List<MethodNode> methods = getSelectedMethods();
		if(methods.isEmpty()) {
			return false;
		}
		return true;
	}	
}
