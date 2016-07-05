/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.GlobalParameterInterface;
import com.ecfeed.ui.modelif.GlobalParametersParentInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.ModelNodesTransfer;
import com.ecfeed.ui.modelif.ParametersParentInterface;

public class GlobalParametersViewer extends AbstractParametersViewer {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private GlobalParametersParentInterface fParentIf;
	private GlobalParameterInterface fParameterIf;

	public GlobalParametersViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);

		getSection().setText("Global parameters");
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
	}

	@Override
	protected ParametersParentInterface getParametersParentInterface() {
		return getGlobalParametersParentIf();
	}

	protected ParametersParentInterface getGlobalParametersParentIf() {
		if(fParentIf == null){
			fParentIf = new GlobalParametersParentInterface(this, getFileInfoProvider());
		}
		return fParentIf;
	}

	public void setInput(GlobalParametersParentNode input){
		fParentIf.setTarget(input);
		super.setInput(input);
	}

	@Override
	protected AbstractParameterInterface getParameterInterface() {
		if(fParameterIf == null){
			fParameterIf = new GlobalParameterInterface(this, getFileInfoProvider());
		}
		return fParameterIf;
	}

}
