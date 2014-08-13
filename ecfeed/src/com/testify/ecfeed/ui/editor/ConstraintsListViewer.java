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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class ConstraintsListViewer extends CheckboxTableViewerSection {
	
	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private TableViewerColumn fNameColumn;
	private MethodInterface fMethodInterface;

	private class AddConstraintAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			ConstraintNode constraint = fMethodInterface.addNewConstraint(ConstraintsListViewer.this, getUpdateListener());
			if(constraint != null){
				selectElement(constraint);
				fNameColumn.getViewer().editElement(constraint, 0);
			}
		}
	}
	
	private class RemoveSelectedConstraintsAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			fMethodInterface.removeConstraints(getCheckedConstrainst(), ConstraintsListViewer.this, getUpdateListener());
		}
	}

	public ConstraintsListViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager){
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);
		
		getSection().setText("Constraints");
		
		fMethodInterface = new MethodInterface(operationManager);
		fNameColumn.setEditingSupport(new ConstraintNameEditingSupport(this, operationManager));

		addButton("Add constraint", new AddConstraintAdapter());
		addButton("Remove selected", new RemoveSelectedConstraintsAdapter());
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}
	
	public Collection<ConstraintNode> getCheckedConstrainst() {
		List<ConstraintNode> result = new ArrayList<ConstraintNode>();
		for(Object object : getCheckedElements()){
			result.add((ConstraintNode)object);
		}
		return result;
	}

	public void setInput(MethodNode method){
		super.setInput(method.getConstraintNodes());
		fMethodInterface.setTarget(method);
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getName();
			}
		});
		
		addColumn("Definition", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getConstraint().toString();
			}
		});
	}
	
}
