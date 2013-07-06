package com.testify.ecfeed.editor;

import java.io.ByteArrayOutputStream;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextEditor;

import com.testify.ecfeed.editors.EcMultiPageEditor;
import com.testify.ecfeed.editors.IModelUpdateListener;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.EcWriter;

public class EcSourceViewer extends TextEditor implements IModelUpdateListener{
	
	private ColorManager fColorManager;
	public EcSourceViewer(EcMultiPageEditor editor){
		super();
		fColorManager = new ColorManager();
		setSourceViewerConfiguration(new EcViewerConfiguration(fColorManager));
		setDocumentProvider(new EcDocumentProvider());
		if(editor != null){
			editor.registerModelUpdateListener(this);
		}
	}
	
	public void dispose() {
		fColorManager.dispose();
		super.dispose();
	}
	
	public IDocument getDocument(){
		return getSourceViewer().getDocument();
	}

//	public void updateModel(RootNode model) {
//		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//		EcWriter writer = new EcWriter(ostream);
//		writer.writeXmlDocument(model);
//		getDocument().set(ostream.toString());
//	}
//
	@Override
	public boolean isEditable(){
		return false;
	}

	@Override
	public void modelUpdated(RootNode model) {
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		EcWriter writer = new EcWriter(ostream);
		writer.writeXmlDocument(model);
		getDocument().set(ostream.toString());
	}
}
