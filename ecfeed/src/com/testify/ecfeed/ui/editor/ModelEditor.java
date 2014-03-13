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

import com.testify.ecfeed.model.IModelWrapper;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.ParserException;
import com.testify.ecfeed.parsers.xml.XmlModelParser;
import com.testify.ecfeed.parsers.xml.XmlModelSerializer;

public class ModelEditor extends FormEditor implements IModelWrapper{
	
	public static String ID = "com.testify.ecfeed.ui.editors.EcMultiPageEditor";

	private RootNode fModel;
	private ModelPage fModelPage;

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
				XmlModelParser parser = new XmlModelParser();
				iStream = file.getContents();
				root = parser.parseModel(iStream);
			} catch (CoreException | ParserException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return root;
	}

	public ModelEditor() {
		super();
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
			XmlModelSerializer writer = new XmlModelSerializer(fout);
			writer.writeXmlDocument(fModel);
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
}
