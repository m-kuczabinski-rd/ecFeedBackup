package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.testify.ecfeed.ui.common.NodeSelectionToolbox;
import com.testify.ecfeed.ui.modelif.NodeDnDBuffer;

public class ModelNodeDragListener implements DragSourceListener {

	private NodeSelectionToolbox fSelectionToolbox;
	private NodeDnDBuffer fDnDBuffer;
	
	ModelNodeDragListener(ISelectionProvider selectionProvider){
		fSelectionToolbox = new NodeSelectionToolbox(selectionProvider);
		fDnDBuffer = NodeDnDBuffer.getInstance();
	}
	
	@Override
	public void dragStart(DragSourceEvent event) {
		if(fSelectionToolbox.isSelectionSingleType() == false){
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
}
