package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.testify.ecfeed.ui.modelif.NodeDnDBuffer;
import com.testify.ecfeed.ui.modelif.NodeSelectionUtils;

public class ModelNodeDragListener implements DragSourceListener {

	private NodeSelectionUtils fSelectionToolbox;
	private NodeDnDBuffer fDnDBuffer;
	
	ModelNodeDragListener(ISelectionProvider selectionProvider){
		fSelectionToolbox = new NodeSelectionUtils(selectionProvider);
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
