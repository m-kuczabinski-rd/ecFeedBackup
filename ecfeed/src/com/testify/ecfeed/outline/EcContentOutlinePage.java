package com.testify.ecfeed.outline;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.testify.ecfeed.editors.EcEditor;

public class EcContentOutlinePage extends ContentOutlinePage {
	
	private TreeViewer fTreeViewer;
	private ITextEditor fEditor; 

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

	public void setTextEditor(ITextEditor editor){
		fEditor = editor;
	}

//	private void update() {
//		if(fTreeViewer != null){
//			Control control = fTreeViewer.getControl();
//			if((control != null) && !(control.isDisposed())){
//				control.setRedraw(false);
//				fTreeViewer.setInput(fEditorInput);
//				fTreeViewer.expandAll();
//				control.setRedraw(true);
//			}			
//		}
//	}
}
