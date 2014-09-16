package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.action.Action;

public class CutAction extends NamedAction {

	private Action fCopyAction;
	private Action fDeleteAction;

	public CutAction(Action copyAction, Action deleteAction) {
		super(GlobalActions.CUT.getId(), GlobalActions.CUT.getName());
		fCopyAction = copyAction;
		fDeleteAction = deleteAction;
	}

	@Override
	public boolean isEnabled(){
		return fCopyAction.isEnabled() && fDeleteAction.isEnabled();
	}
	
	@Override
	public void run(){
		fCopyAction.run();
		fDeleteAction.run();
	}
}
