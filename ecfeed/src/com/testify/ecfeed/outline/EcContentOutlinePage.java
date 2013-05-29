package com.testify.ecfeed.outline;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.testify.ecfeed.editors.EcEditor;

public class EcContentOutlinePage extends ContentOutlinePage {
	
	private TreeViewer fTreeViewer;
	private EcEditor fEditor; 

	public EcContentOutlinePage(EcEditor editor) {
		super();
		this.fEditor = editor;
	}
	
	@Override
	public void createControl(Composite parent){
		super.createControl(parent);
		
		fTreeViewer = getTreeViewer();
		fTreeViewer.setContentProvider(new EcContentProvider());
		fTreeViewer.setLabelProvider(new EcLabelProvider());
		
		fTreeViewer.setInput(fEditor);

		createContextMenu(fTreeViewer);
	}

	private void createContextMenu(TreeViewer treeViewer) {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		Menu menu = menuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu("com.testify.ecfeed.outline.menu.context", menuManager, treeViewer);
	}

	public void setEditor(EcEditor editor){
		fEditor = editor;
	}
	
	public void refreshTree() {
    	Display.getDefault().asyncExec(new Runnable() {

			public void run() {
		    	fTreeViewer.refresh();
			}
    	});
	}

	public EcEditor getEditor() {
		return fEditor;
	}
}
