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

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class CheckboxTableViewerSection extends TableViewerSection {

	public CheckboxTableViewerSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider,
			int style) {
		super(sectionContext, updateContext, fileInfoProvider, style);
	}

	@Override
	protected Table createTable(Composite parent, int style){
		Table tab = new Table(parent, style | SWT.CHECK);

		tab.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				onSelectionChanged();
			}
		}); 
		return tab; 		
	}

	protected abstract void onSelectionChanged();

	@Override
	protected StructuredViewer createViewer(Composite parent, int style){
		Table table = createTable(parent, style);
		table.setLayoutData(viewerLayoutData());
		return new CheckboxTableViewer(table);
	}

	protected CheckboxTableViewer getCheckboxViewer(){
		return (CheckboxTableViewer)getViewer();
	}

	public Object[] getCheckedElements(){
		return getCheckboxViewer().getCheckedElements();
	}

}
