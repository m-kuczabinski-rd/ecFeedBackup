package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.action.Action;

public class CutAction extends Action {

	private Action fCopyAction;
	private Action fDeleteAction;

	public CutAction(Action copyAction, Action deleteAction) {
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
