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

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionChildrenViewer extends CheckboxTableViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	
	private PartitionInterface fPartitionIf;
	private PartitionInterface fTableItemIf;

	private TableViewerColumn fNameColumn;
	private TableViewerColumn fValueColumn;

	private Button fMoveUpButton;

	private class AddPartitionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			PartitionNode added = fPartitionIf.addNewPartition(PartitionChildrenViewer.this, getUpdateListener());
			if(added != null){
				getTable().setSelection(added.getIndex());
			}
		}
	}

	private class RemovePartitionsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fPartitionIf.removePartitions(getCheckedPartitions(), PartitionChildrenViewer.this, getUpdateListener());
		}
	}
	
	private class MoveUpDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if(getSelectedPartition() != null){
				fTableItemIf.setTarget(getSelectedPartition());
				fTableItemIf.moveUpDown(e.getSource() == fMoveUpButton, PartitionChildrenViewer.this, getUpdateListener());
			}
		}
	}
	
	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new PartitionNameLabelProvider());
		fValueColumn = addColumn("Value", 150, new PartitionValueLabelProvider());
	}

	public PartitionChildrenViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		
		fPartitionIf = new PartitionInterface(operationManager);
		fTableItemIf = new PartitionInterface(operationManager);
		
		fNameColumn.setEditingSupport(new PartitionNameEditingSupport(this, operationManager));
		fValueColumn.setEditingSupport(new PartitionValueEditingSupport(this, operationManager));

		getSection().setText("Partitions");
		addButton("Add partition", new AddPartitionAdapter());
		addButton("Remove selected", new RemovePartitionsAdapter());
		fMoveUpButton = addButton("Move Up", new MoveUpDownAdapter());
		addButton("Move Down", new MoveUpDownAdapter());

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	public void setInput(PartitionNode parent){
		super.setInput(parent.getPartitions());
		fPartitionIf.setTarget(parent);
	}
	
	protected Collection<PartitionNode> getCheckedPartitions(){
		Collection<PartitionNode> result = new ArrayList<PartitionNode>();
		for(Object element : getCheckedElements()){
			if(element instanceof PartitionNode){
				result.add((PartitionNode)element);
			}
		}
		return result;
	}

	private PartitionNode getSelectedPartition() {
		if(getSelectedElement() != null && getSelectedElement() instanceof PartitionNode){
			return (PartitionNode)getSelectedElement();
		}
		return null;
	}
}
