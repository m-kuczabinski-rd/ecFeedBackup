package com.testify.ecfeed.ui.editor.actions;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.common.NodeSelectionToolbox;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public abstract class ModelSelectionAction extends NamedAction {
	
	private NodeSelectionToolbox fSelectionToolbox;

	public ModelSelectionAction(String id, String name, ISelectionProvider selectionProvider){
		super(id, name);
		fSelectionToolbox = new NodeSelectionToolbox(selectionProvider);
	}
	
	protected List<GenericNode> getSelectedNodes(){
		return fSelectionToolbox.getSelectedNodes();
	}

	protected SelectionInterface getSelectionInterface(){
		return fSelectionToolbox.getSelectionInterface();
	}
	
	protected boolean isSelectionSibling(){
		return fSelectionToolbox.isSelectionSibling();
	}
	
	protected boolean isSelectionSingleType(){
		return fSelectionToolbox.isSelectionSingleType();
	}

}
