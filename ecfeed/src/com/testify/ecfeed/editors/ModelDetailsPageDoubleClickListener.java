package com.testify.ecfeed.editors;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import com.testify.ecfeed.model.GenericNode;

public class ModelDetailsPageDoubleClickListener implements
		IDoubleClickListener {
	private ModelMasterDetailsBlock fParentBlock;

	ModelDetailsPageDoubleClickListener(ModelMasterDetailsBlock parentBlock){
		fParentBlock = parentBlock;
	}
	
	@Override
	public void doubleClick(DoubleClickEvent event) {
		if(event.getSource() instanceof StructuredViewer){
			StructuredViewer sourceViewer = (StructuredViewer)event.getSource();
			IStructuredSelection selection = (IStructuredSelection) sourceViewer.getSelection();
			fParentBlock.selectNode((GenericNode)selection.getFirstElement());
		}
	}

}
