/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.ui.editor.actions;

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
