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

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.ModelViewerActionFactory;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.PartitionInterface;
import com.testify.ecfeed.ui.modelif.PartitionedNodeInterface;

public class PartitionsViewer extends TableViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	
	private PartitionedNodeInterface fParentIf;
	private PartitionInterface fTableItemIf;
	
	private TableViewerColumn fNameColumn;
	private TableViewerColumn fValueColumn;
	
	private class PartitionNameEditingSupport extends EditingSupport{

		private TextCellEditor fNameCellEditor;

		public PartitionNameEditingSupport() {
			super(getTableViewer());
			fNameCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fNameCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((PartitionNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String newName = (String)value;
			PartitionNode partition = (PartitionNode)element;
			
			if(newName.equals(partition.getName()) == false){
				fTableItemIf.setTarget(partition);
				fTableItemIf.setName(newName, PartitionsViewer.this);
			}
		}
	}

	private class PartitionNameLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element){
			if(element instanceof PartitionNode){
				return ((PartitionNode)element).getName();
			}
			return "";
		}

		@Override
		public Color getForeground(Object element){
			if(element instanceof PartitionNode){
				PartitionNode partition = (PartitionNode)element;
				if(partition.isAbstract()){
					return ColorManager.getColor(ColorConstants.ABSTRACT_PARTITION);
				} else if (fTableItemIf.getImplementationStatus(partition) == ImplementationStatus.IMPLEMENTED) {
					return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
				}
			}
			return null;
		}
	}
	
	private class PartitionValueEditingSupport extends EditingSupport {
		private ComboBoxViewerCellEditor fCellEditor;
		
		public PartitionValueEditingSupport(TableViewerSection viewer) {
			super(viewer.getTableViewer());
			fCellEditor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.TRAIL);
			fCellEditor.setLabelProvider(new LabelProvider());
			fCellEditor.setContentProvider(new ArrayContentProvider());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			PartitionNode node = (PartitionNode)element;
			if(CategoryInterface.hasLimitedValuesSet(node.getCategory())){
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			} else {
				fCellEditor.setActivationStyle(SWT.NONE);
			}
			List<String> items = CategoryInterface.getSpecialValues(node.getCategory().getType());
			if(items.contains(node.getValueString()) == false){
				items.add(node.getValueString());
			}
			fCellEditor.setInput(items);
			fCellEditor.getViewer().getCCombo().setEditable(CategoryInterface.isBoolean(node.getCategory().getType()) == false);
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return ((PartitionNode)element).isAbstract() == false;
		}

		@Override
		protected Object getValue(Object element) {
			return ((PartitionNode)element).getValueString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String valueString = null;
			if(value instanceof String){
				valueString = (String)value;
			} else if(value == null){
				valueString = fCellEditor.getViewer().getCCombo().getText();
			}
			fTableItemIf.setTarget((PartitionNode)element);
			fTableItemIf.setValue(valueString, PartitionsViewer.this);
		}
	}

	private class PartitionValueLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element){
			if(element instanceof PartitionNode){
				PartitionNode partition = (PartitionNode)element;
				return partition.isAbstract()?"[ABSTRACT]":partition.getValueString();
			}
			return "";
		}

		@Override
		public Color getForeground(Object element){
			if(element instanceof PartitionNode){
				PartitionNode partition = (PartitionNode)element;
				if(partition.isAbstract()){
					return ColorManager.getColor(ColorConstants.ABSTRACT_PARTITION);
				} else if (fTableItemIf.getImplementationStatus(partition) == ImplementationStatus.IMPLEMENTED) {
					return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
				}
			}
			return null;
		}

	}

	private class AddPartitionAdapter extends SelectionAdapter{
		
		@Override
		public void widgetSelected(SelectionEvent e){
			PartitionNode added = fParentIf.addNewPartition(PartitionsViewer.this);
			if(added != null){
				getTable().setSelection(added.getIndex());
			}
		}
	}
	
	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new PartitionNameLabelProvider());
		fValueColumn = addColumn("Value", 150, new PartitionValueLabelProvider());
	}
	
	public PartitionsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent, parent.getOperationManager());
		
		fParentIf = new CategoryInterface();
		fTableItemIf = new PartitionInterface();

		fNameColumn.setEditingSupport(new PartitionNameEditingSupport());
		fValueColumn.setEditingSupport(new PartitionValueEditingSupport(this));

		getSection().setText("Partitions");
		addButton("Add partition", new AddPartitionAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this)));
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		setActionProvider(new ModelViewerActionFactory(getTableViewer(), this));
	}

	public void setInput(PartitionedNode parent){
		super.setInput(parent.getPartitions());
		fParentIf.setTarget(parent);
	}
	
	public void setVisible(boolean visible){
		this.getSection().setVisible(visible);
	}
}
