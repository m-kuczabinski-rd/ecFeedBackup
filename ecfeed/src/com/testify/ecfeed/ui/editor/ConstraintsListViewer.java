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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.ModelViewerGlobalActionFactory;
import com.testify.ecfeed.ui.modelif.ConstraintInterface;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class ConstraintsListViewer extends TableViewerSection {
	
	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private TableViewerColumn fNameColumn;
	private MethodInterface fMethodInterface;
	private ConstraintInterface fConstraintIf;

	public class ConstraintNameEditingSupport extends EditingSupport{

		private CellEditor fNameCellEditor;

		public ConstraintNameEditingSupport(){
			super(getTableViewer());
			fNameCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element){
			return fNameCellEditor;
		}

		@Override
		protected boolean canEdit(Object element){
			return true;
		}

		@Override
		protected Object getValue(Object element){
			return ((ConstraintNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value){
			String newName = (String)value;
			ConstraintNode constraint = (ConstraintNode)element;
			fConstraintIf.setTarget(constraint);
			fConstraintIf.setName(newName, ConstraintsListViewer.this);
		}
	}

	private class AddConstraintAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			ConstraintNode constraint = fMethodInterface.addNewConstraint(ConstraintsListViewer.this);
			if(constraint != null){
				selectElement(constraint);
				fNameColumn.getViewer().editElement(constraint, 0);
			}
		}
	}
	
	public ConstraintsListViewer(BasicDetailsPage parent, FormToolkit toolkit){
		super(parent.getMainComposite(), toolkit, STYLE, parent, parent.getOperationManager());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);
		
		getSection().setText("Constraints");
		
		fMethodInterface = new MethodInterface();
		fConstraintIf = new ConstraintInterface();
		
		fNameColumn.setEditingSupport(new ConstraintNameEditingSupport());

		addButton("Add constraint", new AddConstraintAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this)));
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		addKeyListener(SWT.DEL, new DeleteAction(getViewer(), this));
		setActionProvider(new ModelViewerGlobalActionFactory(getTableViewer(), this));
	}
	
	public void setInput(MethodNode method){
		super.setInput(method.getConstraintNodes());
		fMethodInterface.setTarget(method);
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new NodeNameColumnLabelProvider());
		
		addColumn("Definition", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getConstraint().toString();
			}
		});
	}
	
}
