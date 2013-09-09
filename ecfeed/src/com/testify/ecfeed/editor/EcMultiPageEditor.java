/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.editor;


import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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

import com.testify.ecfeed.editor.modeleditor.ModelPage;
import com.testify.ecfeed.editor.sourceviewer.SourceViewer;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.EcParser;

public class EcMultiPageEditor extends FormEditor{
	
	public static String ID = "com.testify.ecfeed.editors.EcMultiPageEditor";

	private SourceViewer fSourceViewer;
	private RootNode fModel;
	private Set<IModelUpdateListener> fModelUpdateListeners;

	private int fSourceViewerIndex;

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
		fModelUpdateListeners = new HashSet<IModelUpdateListener>();
	}
	
	@Override
	protected void addPages() {
		try {
			fSourceViewer = new SourceViewer(this);
			fSourceViewer.init(getEditorSite(), getEditorInput());

			setPartName(getEditorInput().getName());
			
			ModelPage treeEditorPage = new ModelPage(this, getModel());
			addPage(treeEditorPage);

			fSourceViewerIndex = addPage(fSourceViewer, getEditorInput());
			setPageText(fSourceViewerIndex, "source");
			
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

	public SourceViewer getSourceViewer() {
		return fSourceViewer;
	}
	
	@Override
	protected void pageChange(int newPageIndex) {
		if(newPageIndex == fSourceViewerIndex){
			fSourceViewer.refresh();
		}
		super.pageChange(newPageIndex);
	}
}
