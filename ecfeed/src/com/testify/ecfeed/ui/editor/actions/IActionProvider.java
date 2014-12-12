package com.testify.ecfeed.ui.editor.actions;

import java.util.Set;

public interface IActionProvider {
	public Set<String> getGroups();
	public Set<NamedAction> getActions(String groupId);
	public NamedAction getAction(String actionId);
	public void setEnabled(boolean enabled);
}
