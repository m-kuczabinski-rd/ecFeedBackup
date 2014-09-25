package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public class ModelModifyingAction extends ModelSelectionAction {

	private IModelUpdateContext fUPdateContext;

	public ModelModifyingAction(String id, String name, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(id, name, selectionProvider);
		fUPdateContext = updateContext;
	}

	protected IModelUpdateContext getUpdateContext(){
		return fUPdateContext;
	}
	
	protected SelectionInterface getSelectionInterface(){
		return getSelectionUtils().getSelectionInterface(fUPdateContext);
	}
}
