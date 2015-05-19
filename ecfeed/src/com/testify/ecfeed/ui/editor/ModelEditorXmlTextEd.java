package com.testify.ecfeed.ui.editor;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;


public class ModelEditorXmlTextEd extends TextEditor {

	ModelEditorXmlTextEd()
	{
//		ModelEditorXmlDocumentProvider modelEditorXmlDocumentProvider = new ModelEditorXmlDocumentProvider();
//		IDocumentProvider provider = (IDocumentProvider)modelEditorXmlDocumentProvider;
		
		IDocumentProvider provider = (IDocumentProvider)new ModelEditorXmlDocumentProvider();; 
		super.setDocumentProvider(provider);
	}
}
