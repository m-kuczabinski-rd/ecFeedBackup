package com.testify.ecfeed.editors;


import java.io.InputStream;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.testify.ecfeed.editor.EcSourceViewer;
import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.EcParser;

public class EcMultiPageEditor extends FormEditor{

	private EcSourceViewer fSourceViewer;
	private EcContentOutlinePage fContentOutline;
	private RootNode fModel;
	private Vector<IModelUpdateListener> fModelUpdateListeners;

	public void registerModelUpdateListener(IModelUpdateListener listener){
		fModelUpdateListeners.add(listener);
	}
	
	public RootNode getModel(){
		if (fModel == null){
			fModel = createModel();
		}
		return fModel;
	}
	
	private RootNode createModel() {
		RootNode root = null;
		IEditorInput input = getEditorInput();
		if(input instanceof FileEditorInput){
			IFile file = ((FileEditorInput)input).getFile();
			InputStream iStream;
			try {
				EcParser parser = new EcParser();
				iStream = file.getContents();
				root = parser.parseEctFile(iStream);
			} catch (CoreException e) {
				System.out.println("Exception: " + e.getMessage());
			}
		}
		return root;
	}

	public void updateModel(RootNode model){
		fModel = model;
		updateListeners(model);
	}

	private void updateListeners(RootNode model) {
		for(IModelUpdateListener listener : fModelUpdateListeners){
			listener.modelUpdated(model);
		}
	}

	public EcMultiPageEditor() {
		super();
		fModelUpdateListeners = new Vector<IModelUpdateListener>();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class required){
		if(IContentOutlinePage.class.equals(required)){
			if(fContentOutline == null){
				fContentOutline = new EcContentOutlinePage(this);
			}
			return fContentOutline;
		}
		return super.getAdapter(required);
	}

	@Override
	protected void addPages() {
		int index;
		try {
			fSourceViewer = new EcSourceViewer(this);
			fSourceViewer.init(getEditorSite(), getEditorInput());

			setPartName(getEditorInput().getName());
			
			ModelPage treeEditorPage = new ModelPage(this, getModel());
			addPage(treeEditorPage);

			index = addPage(fSourceViewer, getEditorInput());
			setPageText(index, "source");
			
		} catch (PartInitException e) {
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating nested text editor",
					null,
					e.getStatus());
		}
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		fSourceViewer.doSave(monitor);
	}
	
	@Override
	public void doSaveAs() {
		fSourceViewer.doSaveAs();
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	public EcSourceViewer getSourceViewer() {
		return fSourceViewer;
	}
}
