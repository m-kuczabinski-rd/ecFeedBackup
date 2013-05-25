package com.testify.ecfeed.outline;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
}
