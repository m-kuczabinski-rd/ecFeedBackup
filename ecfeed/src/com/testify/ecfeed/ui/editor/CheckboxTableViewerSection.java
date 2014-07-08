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
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class CheckboxTableViewerSection extends TableViewerSection {

	public CheckboxTableViewerSection(Composite parent, FormToolkit toolkit, int style, IModelUpdateListener updateListener) {
		super(parent, toolkit, style, updateListener);
	}
	
	public Object[] getCheckedElements(){
		return getCheckboxViewer().getCheckedElements();
	}

	@Override
	protected Table createTable(Composite parent, int style){
		return new Table(parent, style | SWT.CHECK);
	}
	
	@Override
	protected StructuredViewer createViewer(Composite parent, int style){
		Table table = createTable(parent, style);
		table.setLayoutData(viewerLayoutData());
		return new CheckboxTableViewer(table);
	}
	
	protected CheckboxTableViewer getCheckboxViewer(){
		return (CheckboxTableViewer)getViewer();
	}

}
