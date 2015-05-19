package com.testify.ecfeed.ui.editor;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;


public class ModelXmlViewerTextEditor extends TextEditor {

	ModelXmlViewerTextEditor()
	{
		super.setDocumentProvider((IDocumentProvider)new ModelXmlViewerDocumentProvider());
	}
}
