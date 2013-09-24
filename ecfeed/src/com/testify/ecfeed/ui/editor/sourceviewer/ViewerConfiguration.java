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

package com.testify.ecfeed.ui.editor.sourceviewer;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;

public class ViewerConfiguration extends SourceViewerConfiguration {
	
	private ColorManager fColorManager;
	private XmlTagScanner fXmlTagScanner;
	
	public class XmlTagScanner extends RuleBasedScanner {
		public XmlTagScanner(ColorManager manager) {
			IToken string =	new Token(new TextAttribute(manager.getColor(ColorConstants.STRING)));

			setRules(new IRule[] {new SingleLineRule("\"", "\"", string, '\\') });
		}
	}
	
	public ViewerConfiguration(ColorManager colorManager) {
		fColorManager = colorManager;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] 
		{
				IDocument.DEFAULT_CONTENT_TYPE,
				XmlPartitionScanner.XML_START_TAG, 
				XmlPartitionScanner.XML_PI,
				XmlPartitionScanner.XML_END_TAG, 
		};
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    PresentationReconciler reconciler = new PresentationReconciler();
	
	    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXmlTagScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_START_TAG);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_START_TAG);
	
	    dr = new DefaultDamagerRepairer(getXmlTagScanner());
	    reconciler.setDamager(dr, XmlPartitionScanner.XML_END_TAG);
	    reconciler.setRepairer(dr, XmlPartitionScanner.XML_END_TAG);
	
	    return reconciler;
	}

	protected XmlTagScanner getXmlTagScanner() {
		if (fXmlTagScanner == null) {
			fXmlTagScanner = new XmlTagScanner(fColorManager);
			fXmlTagScanner.setDefaultReturnToken(
					new Token(new TextAttribute(fColorManager.getColor(ColorConstants.TAG))));
		}
		return fXmlTagScanner;
	}
}
