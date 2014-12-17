package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.StructuredViewer;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IModelImplementer;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ImplementAction extends ModelModifyingAction {

	private IModelImplementer fImplementer;

	public ImplementAction(StructuredViewer viewer, IModelUpdateContext context, IModelImplementer implementer) {
		super("implement", "Implement", viewer, context);
		fImplementer = implementer;
	}

	@Override
	public void run(){
		for(AbstractNode node : getSelectedNodes()){
			fImplementer.implement(node);
		}
	}

	@Override
	public boolean isEnabled(){
		for(AbstractNode node : getSelectedNodes()){
			if(fImplementer.implementable(node) == true && fImplementer.getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
				return true;
			}
		}
		return false;
	}

}
