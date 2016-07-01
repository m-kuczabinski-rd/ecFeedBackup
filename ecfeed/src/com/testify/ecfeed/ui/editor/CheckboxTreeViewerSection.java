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

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class CheckboxTreeViewerSection extends TreeViewerSection {

	public CheckboxTreeViewerSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider,
			int style) {
		super(sectionContext, updateContext, fileInfoProvider, style);
	}

	@Override
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree = new Tree(parent, style | SWT.CHECK);
		tree.setLayoutData(viewerLayoutData());
		CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(tree);
		return treeViewer;
	}

	protected CheckboxTreeViewer getCheckboxViewer(){
		return (CheckboxTreeViewer)getViewer();
	}

	public Object[] getCheckedElements(){
		return getCheckboxViewer().getCheckedElements();
	}

	public Object[] getGrayedElements(){
		return getCheckboxViewer().getGrayedElements();
	}
}
