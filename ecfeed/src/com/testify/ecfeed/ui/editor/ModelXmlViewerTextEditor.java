package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;


public class ModelXmlViewerTextEditor extends TextEditor {

	private ModelXmlViewerDocumentProvider fDocumentProvider;
	
	ModelXmlViewerTextEditor(Shell shell)
	{
		fDocumentProvider = new ModelXmlViewerDocumentProvider(shell);
		super.setDocumentProvider((IDocumentProvider)fDocumentProvider);
	}
	
	public void refreshContent(String newContent) {
		fDocumentProvider.refreshDocument(newContent);
	}
	
}
