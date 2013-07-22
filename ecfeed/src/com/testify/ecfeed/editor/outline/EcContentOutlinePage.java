package com.testify.ecfeed.editor.outline;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.testify.ecfeed.editor.EcMultiPageEditor;
import com.testify.ecfeed.editor.IModelUpdateListener;
import com.testify.ecfeed.model.RootNode;

public class EcContentOutlinePage extends ContentOutlinePage implements IModelUpdateListener{
	
	private TreeViewer fTreeViewer;
	private EcMultiPageEditor fEditor; 

	public EcContentOutlinePage(EcMultiPageEditor editor) {
		super();
		this.fEditor = editor;
		editor.registerModelUpdateListener(this);
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

	public void setEditor(EcMultiPageEditor editor){
		fEditor = editor;
	}
	
	public EcMultiPageEditor getEditor() {
		return fEditor;
	}

	@Override
	public void modelUpdated(RootNode model) {
		refreshTree();
	}

	public void refreshTree() {
    	Display.getDefault().asyncExec(new Runnable() {

			public void run() {
		    	fTreeViewer.refresh();
			}
    	});
	}
}
