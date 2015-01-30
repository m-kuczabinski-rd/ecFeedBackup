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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.adapter.IModelImplementer;
import com.testify.ecfeed.ui.common.EclipseModelImplementer;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;


public class ModelViewerActionProvider extends ActionGroups {

	public ModelViewerActionProvider(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot) {
		this(viewer, context, null, selectRoot);
	}

	public ModelViewerActionProvider(TreeViewer viewer, IModelUpdateContext context, IFileInfoProvider infoProfider, boolean selectRoot) {
		addEditActions(viewer, context);
		if(infoProfider != null){
			addImplementationActions(viewer, context, infoProfider);
		}
		addViewerActions(viewer, context, selectRoot);
		addMoveActions(viewer, context);
	}

	public ModelViewerActionProvider(TableViewer viewer, IModelUpdateContext context) {
		this(viewer, context, null);
	}

	public ModelViewerActionProvider(TableViewer viewer, IModelUpdateContext context, IFileInfoProvider infoProvider) {
		addEditActions(viewer, context);
		if(infoProvider != null){
			addImplementationActions(viewer, context, infoProvider);
		}
		addViewerActions(viewer);
		addMoveActions(viewer, context);
	}

	private void addEditActions(ISelectionProvider selectionProvider, IModelUpdateContext context){
		DeleteAction deleteAction = new DeleteAction(selectionProvider, context);
		addAction("edit", new CopyAction(selectionProvider));
		addAction("edit", new CutAction(new CopyAction(selectionProvider), deleteAction));
		addAction("edit", new PasteAction(selectionProvider, context));
		addAction("edit", deleteAction);
	}

	private void addImplementationActions(StructuredViewer viewer, IModelUpdateContext context, IFileInfoProvider fileInfoProvider) {
		IModelImplementer implementer = new EclipseModelImplementer(fileInfoProvider);
		addAction("implement", new ImplementAction(viewer, context, implementer));
		addAction("implement", new GoToImplementationAction(viewer));
	}

	private void addMoveActions(ISelectionProvider selectionProvider, IModelUpdateContext context){
		addAction("move", new MoveUpDownAction(true, selectionProvider, context));
		addAction("move", new MoveUpDownAction(false, selectionProvider, context));
	}

	private void addViewerActions(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot){
		addAction("viewer", new SelectAllAction(viewer, selectRoot));
		addAction("viewer", new ExpandCollapseAction(viewer));
	}

	private void addViewerActions(TableViewer viewer){
		addAction("viewer", new SelectAllAction(viewer));
	}

}
