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
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public class MenuOperationCut extends ModelModyfingOperation{

	private MenuOperationCopy fCopyOperation;
	private MenuOperationDelete fDeleteOperation;
	
	public MenuOperationCut(List<GenericNode> nodes, ModelOperationManager operationManager, AbstractFormPart source, IModelUpdateListener updateListener) {
		super("Cut", nodes, operationManager, source, updateListener);
		
		fCopyOperation = new MenuOperationCopy(nodes);
		fDeleteOperation = new MenuOperationDelete(nodes, operationManager, source, updateListener);
	}

	@Override
	public Object execute(){
		fCopyOperation.execute();
		fDeleteOperation.execute();
		return null;
	}
	
	@Override 
	public boolean isEnabled(){
		return fCopyOperation.isEnabled() && fDeleteOperation.isEnabled();
	}
}
