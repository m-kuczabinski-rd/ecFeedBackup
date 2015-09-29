/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Composite;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterDetailsPage extends AbstractParameterDetailsPage {

	private IFileInfoProvider fFileInfoProvider;
	private GlobalParameterInterface fParameterIf;
	private LinkingMethodsViewer fLinkingMethodsViewer;

	public GlobalParameterDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		getParameterIf();
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		addForm(fLinkingMethodsViewer = new LinkingMethodsViewer(this, this, fFileInfoProvider));
	}


	@Override
	protected AbstractParameterInterface getParameterIf() {
		if(fParameterIf == null){
			fParameterIf = new GlobalParameterInterface(this, fFileInfoProvider);
		}
		return fParameterIf;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof GlobalParameterNode){
			GlobalParameterNode parameter = (GlobalParameterNode)getSelectedElement();
			fParameterIf.setTarget(parameter);
			getMainSection().setText(parameter.getQualifiedName() + ": " + parameter.getType());
			fLinkingMethodsViewer.setInput(parameter);
			fLinkingMethodsViewer.setVisible(fParameterIf.getLinkers().size() > 0);

			getMainSection().layout();

		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return GlobalParameterNode.class;
	}

	@Override
	protected AbstractParameterCommentsSection getParameterCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext) {
		return new GlobalParameterCommentsSection(sectionContext, updateContext, fFileInfoProvider);
	}
}
