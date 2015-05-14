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

public class ModelXmlPage extends FormPage {
	private static final String ID = "com.testify.ecfeed.pages.refactored";
	private static final String TITLE = "XML";

	public ModelXmlPage(ModelEditor editor) {
		super(editor, ID, TITLE);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
	}
	
	@Override
	public boolean isDirty(){
		return false;
	}
}
