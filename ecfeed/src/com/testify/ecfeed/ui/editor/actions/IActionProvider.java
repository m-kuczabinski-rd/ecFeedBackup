package com.testify.ecfeed.ui.editor.actions;

import java.util.Set;

import org.eclipse.jface.action.Action;

public interface IActionProvider {
	public Action getAction(String actionId);
	public Set<String> getGroups();
	public String getActionName(String id);
	public Set<String> getActions(String groupId);
}
