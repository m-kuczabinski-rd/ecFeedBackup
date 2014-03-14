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

import org.eclipse.swt.widgets.Composite;

import com.testify.ecfeed.model.CategoryNode;

public class CategoryDetailsPage extends BasicDetailsPage {

	private CategoryNode fSelectedCategory;
	private CategoryChildrenViewer fPartitionsViewer;

	public CategoryDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		addForm(fPartitionsViewer = new CategoryChildrenViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}
	
	@Override
	public void refresh(){
		if(getSelectedElement() instanceof CategoryNode){
			fSelectedCategory = (CategoryNode)getSelectedElement();
		}
		if(fSelectedCategory != null){
			getMainSection().setText(fSelectedCategory.toString());
			fPartitionsViewer.setInput(fSelectedCategory);
		}
	}
}
