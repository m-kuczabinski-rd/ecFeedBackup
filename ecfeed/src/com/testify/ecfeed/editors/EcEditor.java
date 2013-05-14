package com.testify.ecfeed.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class EcEditor extends TextEditor{
	
	private ColorManager fColorManager;
	
	public EcEditor(){
		super();
		fColorManager = new ColorManager();
		setSourceViewerConfiguration(new EcConfiguration(fColorManager));
		setDocumentProvider(new EcDocumentProvider());
	}
	
	public void dispose() {
		fColorManager.dispose();
		super.dispose();
	}
}
