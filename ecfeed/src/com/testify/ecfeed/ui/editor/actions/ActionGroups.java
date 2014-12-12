package com.testify.ecfeed.ui.editor.actions;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public abstract class ActionGroups implements IActionProvider {

	private boolean fEnabled;

	private class ActionRecord{
		public ActionRecord(String group, NamedAction action){
			GROUP_ID = group;
			ACTION = action;
		}

		public String GROUP_ID;
		public NamedAction ACTION;
	}

	private Set<ActionRecord> fActions;

	public ActionGroups(){
		fActions = new LinkedHashSet<>();
		fEnabled = true;
	}

	protected void addAction(String group, NamedAction action){
		Iterator<ActionRecord> iterator = fActions.iterator();
		while(iterator.hasNext()){
			if(iterator.next().ACTION.getId().equals(action.getId())){
				iterator.remove();
			}
		}
		fActions.add(new ActionRecord(group, action));
	}

	@Override
	public Set<String> getGroups(){
		Set<String> result = new LinkedHashSet<>();
		if(fEnabled){
			for(ActionRecord record : fActions){
				result.add(record.GROUP_ID);
			}
		}
		return result;
	}

	@Override
	public Set<NamedAction> getActions(String groupId){
		Set<NamedAction> result = new LinkedHashSet<>();
		for(ActionRecord record : fActions){
			if(record.GROUP_ID.equals(groupId)){
				result.add(record.ACTION);
			}
		}
		return result;
	}

	@Override
	public NamedAction getAction(String actionId){
		for(ActionRecord record : fActions){
			if(record.ACTION.getId().equals(actionId)){
				return record.ACTION;
			}
		}
		return null;
	}

	public void setEnabled(boolean enabled){
		fEnabled = enabled;
	}
}
