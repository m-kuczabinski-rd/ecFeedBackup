/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.ecfeed.ui.modelif.NodeDnDBuffer;
import com.ecfeed.ui.modelif.NodeSelectionUtils;

public class ModelNodeDragListener implements DragSourceListener {

	private NodeSelectionUtils fSelectionToolbox;
	private NodeDnDBuffer fDnDBuffer;
	private boolean fEnabled;

	ModelNodeDragListener(ISelectionProvider selectionProvider){
		fSelectionToolbox = new NodeSelectionUtils(selectionProvider);
		fDnDBuffer = NodeDnDBuffer.getInstance();
		fEnabled = true;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		if((fEnabled == false) || (fSelectionToolbox.isSelectionSingleType() == false)){
			event.doit = false;
		}
		else{
			fDnDBuffer.setDraggedNodes(fSelectionToolbox.getSelectedNodes());
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		fDnDBuffer.clear();
	}

	public void setEnabled(boolean enabled){
		fEnabled = enabled;
	}
}
