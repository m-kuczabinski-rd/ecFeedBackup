/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class MenuOperationCut extends ModelModyfingOperation{

	private MenuOperationCopy fCopyOperation;
	private RootInterface fModelIf;
	
	public MenuOperationCut(List<GenericNode> nodes, ModelOperationManager operationManager, AbstractFormPart source, IModelUpdateListener updateListener) {
		super("Cut", nodes, operationManager, source, updateListener);
		fModelIf = new RootInterface(operationManager);
		fModelIf.setTarget((RootNode)nodes.get(0).getRoot());
		
		fCopyOperation = new MenuOperationCopy(nodes);
	}

	@Override
	public Object execute(){
		fCopyOperation.execute();
		return fModelIf.removeNodes(getSelectedNodes(), getSource(), getUpdateListener());
	}
	
	@Override 
	public boolean isEnabled(){
		return fCopyOperation.isEnabled();
	}
}
