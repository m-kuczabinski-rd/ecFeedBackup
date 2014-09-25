package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class MoveUpDownAction extends ModelModifyingAction {

	private boolean fUp;

	public MoveUpDownAction(boolean up, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(chooseId(up), chooseName(up), selectionProvider, updateContext);
		fUp = up;
	}
	
	@Override
	public boolean isEnabled(){
		return getSelectionInterface().moveUpDownEnabed(fUp);
	}
	
	@Override 
	public void run(){
		getSelectionInterface().moveUpDown(fUp);
	}

	private static String chooseId(boolean up){
		return up?GlobalActions.MOVE_UP.getId():GlobalActions.MOVE_DOWN.getId();
	}

	private static String chooseName(boolean up){
		return up?GlobalActions.MOVE_UP.getName():GlobalActions.MOVE_DOWN.getName();
	}
}
