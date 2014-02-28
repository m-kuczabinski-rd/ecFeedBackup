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

package com.testify.ecfeed.ui.editor.modeleditor.obsolete;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.editor.EcMultiPageEditor;

public class ObsoleteModelPage extends FormPage{
	
	private final static String ID = "com.testify.ecfeed.ui.editor.modeleditor";
	private final static String TITLE = "model";
	
	private ObsoleteModelMasterDetailsBlock fBlock;
	private RootNode fModel;
	
	/**
	 * Create the form page.
	 * @param rootNode 
	 * @param id
	 * @param title
	 */
	public ObsoleteModelPage(EcMultiPageEditor editor, RootNode model) {
		super(editor, ID, TITLE);
		fModel = model;
		fBlock = new ObsoleteModelMasterDetailsBlock(editor, model);
	}

	public RootNode getModel(){
		return fModel;
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Equivalence class model");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);

		fBlock.createContent(managedForm);
	}
}
