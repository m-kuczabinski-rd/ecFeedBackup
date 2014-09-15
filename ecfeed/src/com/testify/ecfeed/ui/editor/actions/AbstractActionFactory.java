package com.testify.ecfeed.ui.editor.actions;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;


public abstract class AbstractActionFactory implements IActionProvider {

	private class ActionRecord{
		public ActionRecord(String id, String group, String name, Action action){
			NAME = name;
			GROUP_ID = group;
			ACTION_ID = id;
			ACTION = action;
		}
		
		public String NAME;
		public String GROUP_ID;
		public String ACTION_ID;
		public Action ACTION;
	}
	
	private Set<ActionRecord> fActions;
	
	public AbstractActionFactory(){
		fActions = new LinkedHashSet<>();
	}
	
	protected void addAction(String group, String id, String name, Action action){
		Iterator<ActionRecord> iterator = fActions.iterator();
		while(iterator.hasNext()){
			if(iterator.next().ACTION_ID.equals(id)){
				iterator.remove();
			}
		}
		fActions.add(new ActionRecord(id, group, name, action));
	}
	
	public String getActionName(String id){
		for(ActionRecord record : fActions){
			if(record.ACTION_ID.equals(id)){
				return record.NAME;
			}
		}
		return null;
	}

	public Action getAction(String id){
		for(ActionRecord record : fActions){
			if(record.ACTION_ID.equals(id)){
				return record.ACTION;
			}
		}
		return null;
	}
	
	public Set<String> getGroups(){
		Set<String> result = new LinkedHashSet<>();
		for(ActionRecord record : fActions){
			result.add(record.GROUP_ID);
		}
		return result;
	}
	
	public Set<String> getActions(String groupId){
		Set<String> result = new LinkedHashSet<>();
		for(ActionRecord record : fActions){
			if(record.GROUP_ID.equals(groupId)){
				result.add(record.ACTION_ID);
			}
		}
		return result;
	}
}
