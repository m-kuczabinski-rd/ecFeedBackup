package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

public class NamedAction extends Action {

	private static final String COPY_ACTION_ID = ActionFactory.COPY.getId();
	private static final String CUT_ACTION_ID = ActionFactory.CUT.getId();
	private static final String PASTE_ACTION_ID = ActionFactory.PASTE.getId();
	private static final String DELETE_ACTION_ID = ActionFactory.DELETE.getId();
	private static final String SELECT_ALL_ACTION_ID = ActionFactory.SELECT_ALL.getId();
	private static final String UNDO_ACTION_ID = ActionFactory.UNDO.getId();
	private static final String REDO_ACTION_ID = ActionFactory.REDO.getId();
	private static final String MOVE_UP_ACTION_ID = "moveUp";
	private static final String MOVE_DOWN_ACTION_ID = "moveDown";
	private static final String EXPAND_ACTION_ID = "expand";
	private static final String COLLAPSE_ACTION_ID = "collapse";

	private static final String COPY_ACTION_NAME = "Copy\tCtrl+c";
	private static final String CUT_ACTION_NAME = "Cut\tCtrl+x";
	private static final String PASTE_ACTION_NAME = "Paste\tCtrl+x";
	private static final String DELETE_ACTION_NAME = "Delete\tDEL";
	private static final String SELECT_ALL_ACTION_NAME = "Select All\tCtrl+a";
	private static final String UNDO_ACTION_NAME = "Undo\tCtrl+z";
	private static final String REDO_ACTION_NAME = "Redo\tCtrl+Shift+z";
	private static final String MOVE_UP_ACTION_NAME = "Move Up\tAlt+Up";
	private static final String MOVE_DOWN_ACTION_NAME = "Move Up\tAlt+Down";
	private static final String EXPAND_ACTION_NAME = "Expand\tCtrl+Shift+e";
	private static final String COLLAPSE_ACTION_NAME = "Collapse\tCtrl+Shift+w";
	
	protected enum GlobalActions{
		COPY(COPY_ACTION_ID, COPY_ACTION_NAME),
		CUT(CUT_ACTION_ID, CUT_ACTION_NAME),
		PASTE(PASTE_ACTION_ID, PASTE_ACTION_NAME),
		DELETE(DELETE_ACTION_ID, DELETE_ACTION_NAME),
		SELECT_ALL(SELECT_ALL_ACTION_ID, SELECT_ALL_ACTION_NAME),
		UNDO(UNDO_ACTION_ID, UNDO_ACTION_NAME),
		REDO(REDO_ACTION_ID, REDO_ACTION_NAME),
		MOVE_UP(MOVE_UP_ACTION_ID, MOVE_UP_ACTION_NAME),
		MOVE_DOWN(MOVE_DOWN_ACTION_ID, MOVE_DOWN_ACTION_NAME),
		EXPAND(EXPAND_ACTION_ID, EXPAND_ACTION_NAME),
		COLLAPSE(COLLAPSE_ACTION_ID, COLLAPSE_ACTION_NAME);
		
		private String fActionId;
		private String fActionName;

		GlobalActions(String actionId, String actionName){
			fActionId = actionId; 
			fActionName = actionName;
		}
		
		public String getName(){
			return fActionName;
		}
		
		public String getId(){
			return fActionId;
		}
	}
	
	private final String fName;
	private final String fId;

	public NamedAction(String id, String name){
		fId = id;
		fName = name;
	}
	
	public String getName(){
		return fName;
	}

	public String getId(){
		return fId;
	}
}
