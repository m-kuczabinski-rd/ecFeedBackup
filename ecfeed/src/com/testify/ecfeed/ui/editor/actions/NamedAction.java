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

package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

public class NamedAction extends Action {

	public static final String COPY_ACTION_ID = ActionFactory.COPY.getId();
	public static final String CUT_ACTION_ID = ActionFactory.CUT.getId();
	public static final String PASTE_ACTION_ID = ActionFactory.PASTE.getId();
	public static final String DELETE_ACTION_ID = ActionFactory.DELETE.getId();
	public static final String SELECT_ALL_ACTION_ID = ActionFactory.SELECT_ALL.getId();
	public static final String UNDO_ACTION_ID = ActionFactory.UNDO.getId();
	public static final String REDO_ACTION_ID = ActionFactory.REDO.getId();
	public static final String MOVE_UP_ACTION_ID = "moveUp";
	public static final String MOVE_DOWN_ACTION_ID = "moveDown";
	public static final String EXPAND_ACTION_ID = "expand";
	public static final String COLLAPSE_ACTION_ID = "collapse";
	public static final String EXPAND_COLLAPSE_ACTION_ID = "expand/collapse";
	public static final String JAVADOC_EXPORT_ACTION_ID = "javadoc.export";
	public static final String JAVADOC_IMPORT_ACTION_ID = "javadoc.import";
	public static final String JAVADOC_EXPORT_TYPE_ACTION_ID = "javadoc.exportType";
	public static final String JAVADOC_IMPORT_TYPE_ACTION_ID = "javadoc.importType";

	public static final String COPY_ACTION_NAME = "Copy\tCtrl+C";
	public static final String CUT_ACTION_NAME = "Cut\tCtrl+X";
	public static final String PASTE_ACTION_NAME = "Paste\tCtrl+V";
	public static final String DELETE_ACTION_NAME = "Delete\tDEL";
	public static final String SELECT_ALL_ACTION_NAME = "Select All\tCtrl+A";
	public static final String UNDO_ACTION_NAME = "Undo\tCtrl+Z";
	public static final String REDO_ACTION_NAME = "Redo\tCtrl+Shift+Z";
	public static final String MOVE_UP_ACTION_NAME = "Move Up\tAlt+Up";
	public static final String MOVE_DOWN_ACTION_NAME = "Move Down\tAlt+Down";
	public static final String EXPAND_ACTION_NAME = "Expand\tCtrl+Shift+E";
	public static final String COLLAPSE_ACTION_NAME = "Collapse\tCtrl+Shift+W";
	public static final String EXPAND_COLLAPSE_ACTION_NAME = "Expand/Collapse\tSpace";
	public static final String JAVADOC_EXPORT_ACTION_NAME = "Export javadoc";
	public static final String JAVADOC_IMPORT_ACTION_NAME = "Import javadoc";
	public static final String JAVADOC_EXPORT_TYPE_ACTION_NAME = "javadoc.exportType";
	public static final String JAVADOC_IMPORT_TYPE_ACTION_NAME = "javadoc.importType";

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

	@Override
	public String getId(){
		return fId;
	}
}
