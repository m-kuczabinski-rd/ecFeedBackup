/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;

public class ModelPage extends FormPage {
	private static final String ID = "com.testify.ecfeed.pages.refactored";
	private static final String TITLE = "model";

	private ModelMasterDetailsBlock fBlock;
	private ModelEditor fEditor;

	public ModelPage(ModelEditor editor, IFileInfoProvider fileInfoProvider) {
		super(editor, ID, TITLE);
		fEditor = editor;
		fBlock = new ModelMasterDetailsBlock(this, fileInfoProvider);
	}

	public void commitMasterPart(boolean onSave){
		if(fBlock.getMasterSection() != null && fBlock.getMasterSection().isDirty()){
			fBlock.getMasterSection().commit(onSave);
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		fBlock.createContent(managedForm);
	}

	@Override
	public boolean isDirty(){
		boolean masterSectionDirty = fBlock.getMasterSection() == null ? false : fBlock.getMasterSection().isDirty();
		return super.isDirty() || masterSectionDirty;
	}

	public RootNode getModel() throws ModelOperationException{
		return fEditor.getModel();
	}

	public ModelMasterDetailsBlock getMasterBlock() {
		return fBlock;
	}

	public ModelEditor getEditor(){
		return fEditor;
	}
}
