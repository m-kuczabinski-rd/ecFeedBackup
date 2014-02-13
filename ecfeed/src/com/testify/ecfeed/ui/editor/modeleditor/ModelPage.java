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

package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.editor.EcMultiPageEditor;

public class ModelPage extends FormPage {
	
	private final static String ID = "model";
	private final static String TITLE = "model";
	
	private ModelMasterDetailsBlock fBlock;
	private RootNode fModel;
	private EcMultiPageEditor fEditor;
	
	/**
	 * Create the form page.
	 * @param rootNode 
	 * @param id
	 * @param title
	 */
	public ModelPage(EcMultiPageEditor editor, RootNode model) {
		super(editor, ID, TITLE);
		fModel = model;
		fEditor = editor;
		fBlock = new ModelMasterDetailsBlock(editor, model);
	}

	public RootNode getModel(){
		return fModel;
	}
	
	public EcMultiPageEditor getEditor(){
		return fEditor;
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
