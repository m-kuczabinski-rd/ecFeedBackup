package com.testify.ecfeed.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

public class ModelXmlViewerDocumentProvider extends AbstractDocumentProvider {

	private Shell fShell;
	private Object fDocumentElement;
	private String fContent;
	
	
	ModelXmlViewerDocumentProvider(Shell shell) {
		fShell = shell;
	}
	
	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		fDocumentElement = element;
		return new Document(fContent);
	}

	@Override
	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {
		return null;
	}

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
	}

	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}
	
	public void refreshDocument(String newContent) {
		try {
			fContent = newContent;
			super.doResetDocument(fDocumentElement, null);
		} catch (CoreException e) {
			ErrorDialog.openError(fShell,
					"Error while refreshing XML page.",
					null, e.getStatus());			
		}
	}
}
