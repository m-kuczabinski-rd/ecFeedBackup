/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.editor.sourceviewer;

import java.io.ByteArrayOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;

import com.testify.ecfeed.editor.ColorManager;
import com.testify.ecfeed.editor.EcMultiPageEditor;
import com.testify.ecfeed.editor.IModelUpdateListener;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.EcWriter;

public class SourceViewer extends TextEditor implements IModelUpdateListener{
	
	private ColorManager fColorManager;
	private EcMultiPageEditor fEditor;
	private boolean fNeedRefresh;
	
	public class DocumentProvider extends FileDocumentProvider{
		@Override
		protected IDocument createDocument(Object element) throws CoreException{
			IDocument document = super.createDocument(element);
			if(document != null){
				
				IPartitionTokenScanner scanner = new XmlPartitionScanner();
				String[] legalContentTypes = new String[]
				{
						XmlPartitionScanner.XML_START_TAG,
						XmlPartitionScanner.XML_PI,
						XmlPartitionScanner.XML_END_TAG,
				};
				
				IDocumentPartitioner partitioner = new FastPartitioner(scanner , legalContentTypes);
				partitioner.connect(document);
				document.setDocumentPartitioner(partitioner);
			}
			return document;
		}
		
	}

	public SourceViewer(EcMultiPageEditor editor){
		super();
		fColorManager = new ColorManager();
		fEditor = editor;
		setSourceViewerConfiguration(new ViewerConfiguration(fColorManager));
		setDocumentProvider(new DocumentProvider());
		if(editor != null){
			editor.registerModelUpdateListener(this);
		}
	}
	
	@Override
	public void doSave(IProgressMonitor progressMonitor){
		refresh();
		super.doSave(progressMonitor);
	}
	
	@Override
	public void doSaveAs(){
		refresh();
		super.doSaveAs();
	}
	
	@Override
	public void dispose() {
		fColorManager.dispose();
		super.dispose();
	}
	
	public IDocument getDocument(){
		return getSourceViewer().getDocument();
	}

	@Override
	public boolean isEditable(){
		return false;
	}

	@Override
	public void modelUpdated(RootNode model) {
		fNeedRefresh = true;
	}
	
	private void refreshSourceText(){
		RootNode model = fEditor.getModel();
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		EcWriter writer = new EcWriter(ostream);
		writer.writeXmlDocument(model);
		getDocument().set(ostream.toString());
	}

	public void refresh() {
		if(fNeedRefresh){
			refreshSourceText();
		}
		fNeedRefresh = false;
	}
}
