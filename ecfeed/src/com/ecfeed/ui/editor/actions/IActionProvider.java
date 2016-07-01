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

import java.util.Set;

public interface IActionProvider {
	public Set<String> getGroups();
	public Set<NamedAction> getActions(String groupId);
	public NamedAction getAction(String actionId);
	public void setEnabled(boolean enabled);
}
