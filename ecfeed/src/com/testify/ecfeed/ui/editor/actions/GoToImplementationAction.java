package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;

public class GoToImplementationAction extends ModelSelectionAction {

	public GoToImplementationAction(ISelectionProvider selectionProvider) {
		super("goToImpl", "Go to implementation", selectionProvider);
	}

	@Override
	public void run(){
		if(getSelectedNodes().size() != 1){
			return;
		}
		AbstractNode node = getSelectedNodes().get(0);
		AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, null);
		nodeIf.goToImplementation();
	}

	@Override
	public boolean isEnabled(){
		if(getSelectedNodes().size() != 1){
			return false;
		}
		AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null);
		return nodeIf.goToImplementationEnabled();
	}

}
