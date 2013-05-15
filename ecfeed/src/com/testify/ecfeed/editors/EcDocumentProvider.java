package com.testify.ecfeed.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class EcDocumentProvider extends FileDocumentProvider{
	@Override
	protected IDocument createDocument(Object element) throws CoreException{
		IDocument document = super.createDocument(element);
		if(document != null){
			
			IPartitionTokenScanner scanner = new XmlPartitionScanner();
			String[] legalContentTypes = new String[]
			{
					XmlPartitionScanner.XML_START_TAG,
					XmlPartitionScanner.XML_PI,
					XmlPartitionScanner.XML_DOCTYPE,
					XmlPartitionScanner.XML_END_TAG,
					XmlPartitionScanner.XML_TEXT,
					XmlPartitionScanner.XML_CDATA,
					XmlPartitionScanner.XML_COMMENT
			};
			
			IDocumentPartitioner partitioner = new FastPartitioner(scanner , legalContentTypes);
//			IDocumentPartitioner partitioner = new EcPartitioner(scanner , legalContentTypes);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
	
}
