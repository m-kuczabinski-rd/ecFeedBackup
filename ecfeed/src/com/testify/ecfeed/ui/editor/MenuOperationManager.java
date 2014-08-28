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

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.GenericNode;

public class MenuOperationManager{

	private NodeClipboard fClipboard;
	private ModelMasterSection fModel;

	public List<MenuOperation> getOperations(GenericNode target){
		ArrayList<MenuOperation> operations = new ArrayList<>();

		operations.add(new MenuCopyOperation(target, fClipboard));
		if(fClipboard.getClipboardNode() != null){
			operations.add(new MenuPasteOperation(target, fClipboard.getClipboardNode().getCopy(), fModel));
		} else{
			operations.add(new MenuPasteOperation(target, null, fModel));
		}
		operations.add(new MenuCutOperation(target, fClipboard, fModel));

		return operations;
	}

	public MenuOperationManager(ModelMasterSection model){
		fClipboard = new NodeClipboard();
		fModel = model;
	}

	public void setClipboard(NodeClipboard clipboard){
		fClipboard = clipboard;
	}

}
