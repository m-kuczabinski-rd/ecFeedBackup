package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

public class SelectAllAction extends Action {

	private TableViewer fTableViewer;
	private TreeViewer fTreeViewer;
	private boolean fSelectRoot;
	
	public SelectAllAction(TreeViewer viewer, boolean selectRoot){
		fTreeViewer = viewer;
		fSelectRoot = selectRoot;
	}
	
	public SelectAllAction(TableViewer viewer){
		fTableViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
	
	@Override
	public void run(){
		if(fTreeViewer != null){
			fTreeViewer.expandAll();
			fTreeViewer.getTree().selectAll();
			if(fSelectRoot == false){
				fTreeViewer.getTree().deselect(fTreeViewer.getTree().getTopItem());
			}
		}
		if(fTableViewer != null){
			fTableViewer.getTable().selectAll();
		}
	}
}
