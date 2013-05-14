package com.testify.ecfeed.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class EcConfiguration extends SourceViewerConfiguration {
	
	private ColorManager fColorManager;
	private EcDoubleClickStrategy fDoubleClickStrategy;
	private XmlScanner fXmlScanner;
	private XmlTagScanner fXmlTagScanner;
	
	public EcConfiguration(ColorManager colorManager) {
		fColorManager = colorManager;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {IDocument.DEFAULT_CONTENT_TYPE,
			XmlPartitionScanner.XML_COMMENT,
			XmlPartitionScanner.XML_TAG };
	}
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer,
			String contentType) {
		if (fDoubleClickStrategy == null)
			fDoubleClickStrategy = new EcDoubleClickStrategy();
		return fDoubleClickStrategy;
	}

	protected XmlScanner getXmlScanner() {
		if (fXmlScanner == null) {
			fXmlScanner = new XmlScanner(fColorManager);
			fXmlScanner.setDefaultReturnToken(
					new Token(new TextAttribute(fColorManager.getColor(IXmlColorConstants.DEFAULT))));
		}
		return fXmlScanner;
	}

	protected XmlTagScanner getXMLTagScanner() {
		if (fXmlTagScanner == null) {
			fXmlTagScanner = new XmlTagScanner(fColorManager);
			fXmlTagScanner.setDefaultReturnToken(
					new Token(new TextAttribute(fColorManager.getColor(IXmlColorConstants.TAG))));
		}
		return fXmlTagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =	new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, XmlPartitionScanner.XML_TAG);
		reconciler.setRepairer(dr, XmlPartitionScanner.XML_TAG);

		dr = new DefaultDamagerRepairer(getXmlScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
				new NonRuleBasedDamagerRepairer(
						new TextAttribute(
								fColorManager.getColor(IXmlColorConstants.XML_COMMENT)));
		reconciler.setDamager(ndr, XmlPartitionScanner.XML_COMMENT);
		reconciler.setRepairer(ndr, XmlPartitionScanner.XML_COMMENT);

		return reconciler;
	}
}
