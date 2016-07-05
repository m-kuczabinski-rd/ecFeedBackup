/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

public class TreeCheckStateListener implements ICheckStateListener{
	private TreeNodeContentProvider fContentProvider;
	private CheckboxTreeViewer fViewer;

	public TreeCheckStateListener(CheckboxTreeViewer treeViewer){
		fViewer = treeViewer;
		fContentProvider = (TreeNodeContentProvider)treeViewer.getContentProvider();
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		Object element = event.getElement();
		boolean checked = event.getChecked();

		if (checked) {
			setAllCheckedState(element);
		} else {
			setNotCheckedState(element);
		}

		fViewer.setSubtreeChecked(element, checked);
		setParentState(element);
	}

	protected CheckboxTreeViewer getViewer(){
		return fViewer;
	}

	protected TreeNodeContentProvider getContentProvider(){
		return fContentProvider;
	}

	protected void setParentState(Object element) {
		Object parent = fContentProvider.getParent(element);
		if(parent == null) {
			return;
		}
		setElementState(parent);
		setParentState(parent);
	}

	private void setElementState(Object element) {
		int checkedChildrenCount = getCheckedChildrenCount(element);
		if (checkedChildrenCount == 0) {
			setNotCheckedState(element);
			return;
		}

		int allChildrenCount = fContentProvider.getChildren(element).length;
		if (checkedChildrenCount < allChildrenCount) {
			setPartlyCheckedState(element);
			return;
		}

		setAllCheckedState(element);
	}

	private void setNotCheckedState(Object element) {
		fViewer.setGrayed(element, false);
		fViewer.setChecked(element, false);
	}

	private void setAllCheckedState(Object element) {
		fViewer.setGrayed(element, false);
		fViewer.setChecked(element, true);
	}

	private void setPartlyCheckedState(Object element) {
		fViewer.setGrayed(element, true);
		fViewer.setChecked(element, true);
	}

	private int getCheckedChildrenCount(Object parent) {
		int checkedChildrenCount = 0;
		for(Object element : fViewer.getCheckedElements()){
			if(parent.equals(fContentProvider.getParent(element))){
				checkedChildrenCount++;
			}
		}
		return checkedChildrenCount;
	}
}
