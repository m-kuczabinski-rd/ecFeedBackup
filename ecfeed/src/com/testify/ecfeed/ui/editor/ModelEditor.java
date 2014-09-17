/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.testify.ecfeed.abstraction.ModelOperationManager;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.serialization.IModelParser;
import com.testify.ecfeed.serialization.IModelSerializer;
import com.testify.ecfeed.serialization.ParserException;
import com.testify.ecfeed.serialization.ect.EctParser;
import com.testify.ecfeed.serialization.ect.EctSerializer;

public class ModelEditor extends FormEditor{
	
	public static String ID = "com.testify.ecfeed.ui.editors.EcMultiPageEditor";

	private RootNode fModel;
	private ModelPage fModelPage;
	private boolean fResourceChange = false;
	private ModelOperationManager fModelManager;

	private class ResourceChangeReporter implements IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			switch (event.getType()) {
				case IResourceChangeEvent.POST_CHANGE:
					try {
						event.getDelta().accept(new ResourceDeltaVisitor());
					} catch (CoreException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	}

	private class ResourceDeltaVisitor implements IResourceDeltaVisitor {
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			switch (delta.getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.REMOVED:
				case IResourceDelta.CHANGED:
					fResourceChange = true;
					break;
				default:
					break;
			}
			return false;
		}
	}

	public ModelEditor() {
		super();
		ResourceChangeReporter listener = new ResourceChangeReporter();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
		fModelManager = new ModelOperationManager();
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
				IModelParser parser = new EctParser();
				iStream = file.getContents();
				root = parser.parseModel(iStream);
			} catch (CoreException | ParserException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return root;
	}

	@Override
	protected void addPages() {
		try {
			setPartName(getEditorInput().getName());
			addPage(fModelPage = new ModelPage(this));

		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor",
					null, e.getStatus());
		}
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile file = ((FileEditorInput)getEditorInput()).getFile();
		saveEditor(file, monitor);
	}
	
	@Override
	public void doSaveAs() {
		SaveAsDialog dialog = new SaveAsDialog(Display.getDefault().getActiveShell());

		IFile original = ((FileEditorInput)getEditorInput()).getFile();
		dialog.setOriginalFile(original);
		dialog.create();

		if (dialog.open() != Window.CANCEL) {
			IPath path = dialog.getResult();
			IWorkspace workspace= ResourcesPlugin.getWorkspace();
			IFile file = workspace.getRoot().getFile(path);
			saveEditor(file, null);
			setInput(new FileEditorInput(file));
			setPartName(file.getName());
		}
	}
	
	private void saveEditor(IFile file, IProgressMonitor monitor){
		try{
			FileOutputStream fout = new FileOutputStream(file.getLocation().toOSString());
			IModelSerializer serializer = new EctSerializer(fout);
			serializer.serialize(fModel);
//			XmlModelSerializer writer = new XmlModelSerializer(fout);
//			writer.writeXmlDocument(fModel);
			refreshWorkspace(monitor);
			commitPages(true);
			firePropertyChange(PROP_DIRTY);
		}
		catch(Exception e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					"Error", "Couldn't write the file:" + e.getMessage());
		}
	}
	
	private void refreshWorkspace(IProgressMonitor monitor) throws CoreException {
		for(IResource resource : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
			resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	@Override
	public void commitPages(boolean onSave){
		super.commitPages(onSave);
		fModelPage.commitMasterPart(onSave);
	}

	@Override
	public void setFocus() {
		if (fResourceChange) {
			fModelPage.getMasterBlock().getMasterSection().refresh();
			fModelPage.getMasterBlock().getCurrentPage().refresh();
			fResourceChange = false;
		}
		super.setFocus();
	}
	
	public ModelOperationManager getModelOperationManager(){
		return fModelManager;
	}
}
