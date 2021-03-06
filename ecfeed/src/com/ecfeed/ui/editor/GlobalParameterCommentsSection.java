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

import org.eclipse.swt.events.SelectionListener;

import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.GlobalParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterCommentsSection extends AbstractParameterCommentsSection {

	private IFileInfoProvider fFileInfoProvider;
	private GlobalParameterInterface fTargetIf;

	public GlobalParameterCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		getExportButton().setText("Export type comments");
		getImportButton().setText("Import type comments");
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new GlobalParameterInterface(getUpdateContext(), fFileInfoProvider);
		}
		return fTargetIf;
	}

	@Override
	protected SelectionListener createExportButtonSelectionListener(){
		return new ExportFullTypeSelectionAdapter();
	}

	@Override
	protected SelectionListener createImportButtonSelectionListener(){
		return new ImportFullTypeSelectionAdapter();
	}

}
