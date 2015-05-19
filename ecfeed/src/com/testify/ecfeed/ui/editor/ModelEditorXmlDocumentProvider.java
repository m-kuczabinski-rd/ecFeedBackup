package com.testify.ecfeed.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

public class ModelEditorXmlDocumentProvider extends AbstractDocumentProvider {

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		return new Document("ABCDEFGH");
	}

	@Override
	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
