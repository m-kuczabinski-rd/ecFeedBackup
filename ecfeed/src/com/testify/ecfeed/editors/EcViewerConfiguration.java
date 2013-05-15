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

import com.testify.ecfeed.scanners.CDataScanner;
import com.testify.ecfeed.scanners.XmlPartitionScanner;
import com.testify.ecfeed.scanners.XmlScanner;
import com.testify.ecfeed.scanners.XmlTagScanner;
import com.testify.ecfeed.scanners.XmlTextScanner;

public class EcViewerConfiguration extends SourceViewerConfiguration {
	
	private ColorManager fColorManager;
	private XmlDoubleClickStrategy fDoubleClickStrategy;

	private XmlScanner fXmlScanner;
	private XmlTagScanner fXmlTagScanner;
	private XmlTextScanner fTextScanner;
	private CDataScanner fCdataScanner;
	
	public EcViewerConfiguration(ColorManager colorManager) {
		fColorManager = colorManager;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] 
		{
				IDocument.DEFAULT_CONTENT_TYPE,
				XmlPartitionScanner.XML_START_TAG, 
				XmlPartitionScanner.XML_PI,
				XmlPartitionScanner.XML_DOCTYPE,
				XmlPartitionScanner.XML_END_TAG, 
				XmlPartitionScanner.XML_TEXT,
				XmlPartitionScanner.XML_CDATA,
				XmlPartitionScanner.XML_COMMENT
		};
	}
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer,
			String contentType) {
		if (fDoubleClickStrategy == null)
			fDoubleClickStrategy = new XmlDoubleClickStrategy();
		return fDoubleClickStrategy;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    PresentationReconciler reconciler = new PresentationReconciler();
	
	    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXmlTagScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_START_TAG);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_START_TAG);
	
	    dr = new DefaultDamagerRepairer(getXmlTagScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_END_TAG);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_END_TAG);
	
	    dr = new DefaultDamagerRepairer(getXmlScanner());
	    reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
	    reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	
	    dr = new DefaultDamagerRepairer(getXmlScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_DOCTYPE);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_DOCTYPE);
	
	    dr = new DefaultDamagerRepairer(getXmlScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_PI);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_PI);
	
	    dr = new DefaultDamagerRepairer(getXmlTextScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_TEXT);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_TEXT);
	
	    dr = new DefaultDamagerRepairer(getCDataScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_CDATA);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_CDATA);
	
	    TextAttribute textAttribute = new TextAttribute(fColorManager.getColor(IXmlColorConstants.XML_COMMENT));
	    NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(textAttribute);
	    reconciler.setDamager(ndr, XmlPartitionScanner.XML_COMMENT);
	    reconciler.setRepairer(ndr, XmlPartitionScanner.XML_COMMENT);
	
	    return reconciler;
	}

	protected XmlScanner getXmlScanner() {
		if (fXmlScanner == null) {
			fXmlScanner = new XmlScanner(fColorManager);
			fXmlScanner.setDefaultReturnToken(
					new Token(new TextAttribute(fColorManager.getColor(IXmlColorConstants.DEFAULT))));
		}
		return fXmlScanner;
	}

	protected XmlTagScanner getXmlTagScanner() {
		if (fXmlTagScanner == null) {
			fXmlTagScanner = new XmlTagScanner(fColorManager);
			fXmlTagScanner.setDefaultReturnToken(
					new Token(new TextAttribute(fColorManager.getColor(IXmlColorConstants.TAG))));
		}
		return fXmlTagScanner;
	}

	protected XmlTextScanner getXmlTextScanner() {
	    if (fTextScanner == null)
	    {
	        fTextScanner = new XmlTextScanner(fColorManager);
	        fTextScanner.setDefaultReturnToken(new Token(new TextAttribute(fColorManager
	                .getColor(IXmlColorConstants.DEFAULT))));
	    }
	    return fTextScanner;
	}

	protected CDataScanner getCDataScanner() {
	    if (fCdataScanner == null)
	    {
	        fCdataScanner = new CDataScanner(fColorManager);
	        fCdataScanner.setDefaultReturnToken(new Token(new TextAttribute(fColorManager
	                .getColor(IXmlColorConstants.CDATA_TEXT))));
	    }
	    return fCdataScanner;
	}
}
