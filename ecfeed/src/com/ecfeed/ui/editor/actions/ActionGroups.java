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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public abstract class ActionGroups implements IActionProvider {

	private boolean fEnabled;

	private class GrouppedAction {
		public GrouppedAction(String group, NamedAction action){
			fGroupId = group;
			fAction = action;
		}

		private String fGroupId;
		private NamedAction fAction;
	}

	private Set<GrouppedAction> fActions;

	public ActionGroups(){
		fActions = new LinkedHashSet<>();
		fEnabled = true;
	}

	protected void addAction(String group, NamedAction action){
		Iterator<GrouppedAction> iterator = fActions.iterator();
		while(iterator.hasNext()){
			if(iterator.next().fAction.getId().equals(action.getId())){
				iterator.remove();
			}
		}
		fActions.add(new GrouppedAction(group, action));
	}

	@Override
	public Set<String> getGroups(){
		Set<String> result = new LinkedHashSet<>();
		if(fEnabled){
			for(GrouppedAction record : fActions){
				result.add(record.fGroupId);
			}
		}
		return result;
	}

	@Override
	public Set<NamedAction> getActions(String groupId){
		Set<NamedAction> result = new LinkedHashSet<>();
		for(GrouppedAction record : fActions){
			if(record.fGroupId.equals(groupId)){
				result.add(record.fAction);
			}
		}
		return result;
	}

	@Override
	public NamedAction getAction(String actionId){
		for(GrouppedAction record : fActions){
			if(record.fAction.getId().equals(actionId)){
				return record.fAction;
			}
		}
		return null;
	}

	public void setEnabled(boolean enabled){
		fEnabled = enabled;
	}
}
