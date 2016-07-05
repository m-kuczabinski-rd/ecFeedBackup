/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.modelif.ConstraintInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.ModelNodesTransfer;

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
			fConstraintIf.setName(newName);
		}
	}

	private class AddConstraintAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			ConstraintNode constraint = fMethodInterface.addNewConstraint();
			if(constraint != null){
				selectElement(constraint);
				fNameColumn.getViewer().editElement(constraint, 0);
			}
		}
	}

	public ConstraintsListViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider){
		super(sectionContext, updateContext, fileInfoProvider, STYLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		getSection().setText("Constraints");

		fMethodInterface = new MethodInterface(this, fileInfoProvider);
		fConstraintIf = new ConstraintInterface(this, fileInfoProvider);

		fNameColumn.setEditingSupport(new ConstraintNameEditingSupport());

		addButton("Add constraint", new AddConstraintAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
		setActionProvider(new ModelViewerActionProvider(getTableViewer(), updateContext, fileInfoProvider));
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
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
