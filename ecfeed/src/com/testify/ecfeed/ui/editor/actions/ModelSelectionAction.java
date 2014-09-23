package com.testify.ecfeed.ui.editor.actions;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.common.NodeSelectionUtils;

public abstract class ModelSelectionAction extends NamedAction {
	
	private NodeSelectionUtils fSelectionUtils;

	public ModelSelectionAction(String id, String name, ISelectionProvider selectionProvider){
		super(id, name);
		fSelectionUtils = new NodeSelectionUtils(selectionProvider);
	}
	
	protected List<GenericNode> getSelectedNodes(){
		return getSelectionUtils().getSelectedNodes();
	}

	protected boolean isSelectionSibling(){
		return getSelectionUtils().isSelectionSibling();
	}
	
	protected boolean isSelectionSingleType(){
		return getSelectionUtils().isSelectionSingleType();
	}
	
	protected NodeSelectionUtils getSelectionUtils(){
		return fSelectionUtils;
	}

}
