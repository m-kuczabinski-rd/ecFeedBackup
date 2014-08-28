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

package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.GenericNode;

public class MenuCopyOperation extends MenuOperation{
	private NodeClipboard fSource;
	private GenericNode fTarget;

	@Override
	public void execute(){
		fSource.setClipboardNode(fTarget);
	}

	@Override
	public boolean isEnabled(){
		return (fTarget != null);
	}
	
	public MenuCopyOperation(GenericNode target, NodeClipboard source){
		super("Copy");
		fTarget = target;
		fSource = source;
	}

}
