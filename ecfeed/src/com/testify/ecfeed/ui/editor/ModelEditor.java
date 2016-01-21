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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.testify.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.core.adapter.ModelOperationManager;
import com.testify.ecfeed.core.model.ModelConverter;
import com.testify.ecfeed.core.model.ModelVersionDistributor;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.serialization.IModelParser;
import com.testify.ecfeed.core.serialization.IModelSerializer;
import com.testify.ecfeed.core.serialization.ParserException;
import com.testify.ecfeed.core.serialization.ect.EctParser;
import com.testify.ecfeed.core.serialization.ect.EctSerializer;
import com.testify.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.editor.utils.ExceptionCatchDialog;

public class ModelEditor extends FormEditor implements IFileInfoProvider{

	private RootNode fModel;
	private ModelPage fModelPage;
	private ModelOperationManager fModelManager;
	private ObjectUndoContext fUndoContext;
	private ModelSourceEditor fSourcePageEditor;
	private int fSourcePageIndex = -1;

	public class SourceEditorInput implements IEditorInput{

		@Override
		@SuppressWarnings({ "rawtypes" })
		public Object getAdapter(Class adapter) {
			return null;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		@Override
		public String getName() {
			return "source";
		}

		@Override
		public IPersistableElement getPersistable() {
			return null;
		}

		@Override
		public String getToolTipText() {
			return "XML view of model";
		}
	}

	private class ResourceChangeReporter implements IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
			case IResourceChangeEvent.POST_BUILD:
				try {
					event.getDelta().accept(new ResourceDeltaVisitor());
				} catch (CoreException e) {
					SystemLogger.logCatch(e.getMessage());
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
				if (!Display.getDefault().isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							CachedImplementationStatusResolver.clearCache();
							if(fModelPage.getMasterBlock().getMasterSection() != null){
								fModelPage.getMasterBlock().getMasterSection().refresh();
							}
							if(fModelPage.getMasterBlock().getCurrentPage() != null){
								fModelPage.getMasterBlock().getCurrentPage().refresh();
							}
						}
					});
				}
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
		fUndoContext = new ObjectUndoContext(fModelManager);
	}

	public RootNode getModel(){
		if (fModel == null){
			fModel = createModel();
		}
		return fModel;
	}

	private RootNode createModel() {
		IEditorInput input = getEditorInput();
		if(!(input instanceof FileEditorInput)) {
			return null;
		}

		IFile file = ((FileEditorInput)input).getFile();
		try {
			IModelParser parser = new EctParser();
			InputStream iStream = file.getContents();
			return ModelConverter.convertToCurrentVersion(parser.parseModel(iStream));

		} catch (CoreException | ParserException e) {
			ExceptionCatchDialog.display("Can not parse model.", e.getMessage());
			return null;
		}
	}

	@Override
	protected void addPages() {
		try {
			setPartName(getEditorInput().getName());
			addPage(fModelPage = new ModelPage(this, this));

			addSourcePage();

		} catch (PartInitException e) {
			ExceptionCatchDialog.display("Can not add page.", e.getMessage());
		}
	}

	private void addSourcePage() throws PartInitException {
		IEditorInput editorInput = new SourceEditorInput();
		setPartName(getEditorInput().getName());
		fSourcePageEditor = new ModelSourceEditor(getSite().getShell());

		fSourcePageIndex = addPage(fSourcePageEditor, editorInput);
		setPageText(fSourcePageIndex, fSourcePageEditor.getTitle());
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		if (newPageIndex != fSourcePageIndex)
			return;

		if (fModel.subtreeSize() <= Constants.SOURCE_VIEWER_MAX_SUBTREE_SIZE) {
			refreshSourceViewer();
		}
		else {
			fSourcePageEditor.refreshContent(Messages.MODEL_SOURCE_SIZE_EXCEEDED(Constants.SOURCE_VIEWER_MAX_SUBTREE_SIZE));
		}
	}

	private void refreshSourceViewer()
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IModelSerializer serializer = 
				new EctSerializer(outputStream, ModelVersionDistributor.getCurrentVersion());
		try{
			serializer.serialize(fModel);
		}
		catch(Exception e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", "Could not serialize the file:" + e.getMessage());
		}

		fSourcePageEditor.refreshContent(outputStream.toString());
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
			FileOutputStream outputStream = new FileOutputStream(file.getLocation().toOSString());
			IModelSerializer serializer = 
					new EctSerializer(outputStream, ModelVersionDistributor.getCurrentVersion());
			serializer.serialize(fModel);
			refreshWorkspace(monitor);
			commitPages(true);
			firePropertyChange(PROP_DIRTY);
		}
		catch(Exception e){
			ExceptionCatchDialog.display("Can not save editor file.", e.getMessage());
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
		ModelMasterDetailsBlock masterBlock = fModelPage.getMasterBlock();
		if (masterBlock == null) {
			return;
		}

		masterBlock.refreshToolBarActions();
		masterBlock.getMasterSection().refresh();

		BasicDetailsPage page = masterBlock.getCurrentPage();
		if (page == null){
			return;
		}

		page.refresh();
	}

	@Override
	public IFile getFile(){
		IEditorInput input = getEditorInput();
		if (input instanceof FileEditorInput){
			return ((FileEditorInput)input).getFile();
		}
		return null;
	}

	@Override
	public boolean isProjectAvailable() {
		return true; // false for standalone app, true for IDE plugin
	}

	@Override
	public IProject getProject() {
		if (!isProjectAvailable()) {
			return null;
		}
		IFile file = getFile();
		if (file != null){
			return file.getProject();
		}
		return null;
	}

	@Override
	public IPath getPath(){
		IFile file = getFile();
		if (file != null){
			IPath path = file.getFullPath();
			return path;
		}
		return null;
	}

	@Override
	public IPackageFragmentRoot getPackageFragmentRoot() {
		if (!isProjectAvailable()) {
			return null;
		}		
		try {
			if(getProject().hasNature(JavaCore.NATURE_ID)){
				IJavaProject javaProject = JavaCore.create(getProject());
				IPath path = getPath();
				if(javaProject != null){
					for(IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()){
						if(root.getPath().isPrefixOf(path)){
							return root;
						}
					}
				}
			}
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return null;
	}

	public ModelOperationManager getModelOperationManager(){
		return fModelManager;
	}

	public IUndoContext getUndoContext() {
		return fUndoContext;
	}
}
