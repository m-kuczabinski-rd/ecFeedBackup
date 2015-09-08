/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.ui.editor.actions.IActionProvider;
import com.testify.ecfeed.ui.editor.actions.NamedAction;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class TreeViewerSection extends ViewerSection {

	public TreeViewerSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider,
			int style) {
		super(sectionContext, updateContext, fileInfoProvider, style);
	}

	@Override
	protected StructuredViewer createViewer(Composite viewerComposite, int style) {
		return createTreeViewer(viewerComposite, style);
	}

	@Override
	protected void createViewerColumns(){
	}

	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree = new Tree(parent, style);
		tree.setLayoutData(viewerLayoutData());
		TreeViewer treeViewer = new TreeViewer(tree);
		return treeViewer;
	}

	protected Tree getTree(){
		return getTreeViewer().getTree();
	}

	protected TreeViewer getTreeViewer(){
		return (TreeViewer)getViewer();
	}

	@Override
	protected void setActionProvider(IActionProvider provider){
		super.setActionProvider(provider);
		if(provider.getAction(NamedAction.EXPAND_COLLAPSE_ACTION_ID) != null){
			addKeyListener(SWT.SPACE, SWT.NONE, provider.getAction(NamedAction.EXPAND_COLLAPSE_ACTION_ID));
		}
	}
}
