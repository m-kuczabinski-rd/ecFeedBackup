package com.testify.ecfeed.ui.common;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

public class TreeCheckStateListener implements ICheckStateListener{
	TreeNodeContentProvider fContentProvider;
	CheckboxTreeViewer fViewer;
	
	public TreeCheckStateListener(CheckboxTreeViewer treeViewer){
		fViewer = treeViewer;
		fContentProvider = (TreeNodeContentProvider)treeViewer.getContentProvider();
	}
	
	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		Object element = event.getElement();
		boolean checked = event.getChecked();
		
		fViewer.setSubtreeChecked(element, checked);
		setParentGreyed(element);
	}
	
	private void setParentGreyed(Object element) {
		Object parent = fContentProvider.getParent(element);
		if(parent == null) return;
		Object[] children = fContentProvider.getChildren(parent);
		int checkedChildrenCount = getCheckedChildrenCount(parent);
		
		if(checkedChildrenCount == 0){
			fViewer.setGrayChecked(parent, false);
		}
		else if(checkedChildrenCount < children.length){
			fViewer.setGrayChecked(parent, true);
		}
		else{
			fViewer.setGrayed(parent, false);
			fViewer.setChecked(parent, true);
		}
		setParentGreyed(parent);
	}

	private int getCheckedChildrenCount(Object parent) {
		int checkedChildrenCount = 0;
		for(Object element : fViewer.getCheckedElements()){
			if(fContentProvider.getParent(element) == parent){
				checkedChildrenCount++;
			}
		}
		return checkedChildrenCount;
	}
}