package com.testify.ecfeed.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class EcEditor extends TextEditor{
	public EcEditor(){
		super();
		setDocumentProvider(new EcDocumentProvider());
	}
}
