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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.ParametersParentInterface;

public abstract class AbstractParametersViewer extends TableViewerSection {

	private final String BROWSE_PARAMETER_TYPE_STRING = "Browse...";

	private TableViewerColumn fNameColumn;
	private TableViewerColumn fTypeColumn;
	private ParametersParentInterface fParentIf;

	protected class ParameterTypeEditingSupport extends EditingSupport {

		private ComboBoxCellEditor fCellEditor;

		public ParameterTypeEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if(fCellEditor == null){
				List<String> items = new ArrayList<String>(Arrays.asList(AbstractParameterInterface.supportedPrimitiveTypes()));
				items.add(BROWSE_PARAMETER_TYPE_STRING);
				fCellEditor = new ComboBoxCellEditor(getTable(), items.toArray(new String[]{}));
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_KEY_ACTIVATION);
			}
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			AbstractParameterNode node = (AbstractParameterNode)element;
			String [] items = fCellEditor.getItems();
			ArrayList<String> newItems = new ArrayList<String>();

			for (int i = 0; i < items.length; ++i) {
				newItems.add(items[i]);
				if (items[i].equals(node.getType())) {
					return i;
				}
			}

			newItems.add(newItems.size() - 1, node.getType());
			fCellEditor.setItems(newItems.toArray(items));
			return (newItems.size() - 2);
		}

		@Override
		protected void setValue(Object element, Object value) {
			AbstractParameterNode node = (AbstractParameterNode)element;
			String newType = null;
			int index = (int)value;

			if (index >= 0) {
				newType = fCellEditor.getItems()[index];
			} else {
				newType = ((CCombo)fCellEditor.getControl()).getText();
			}
			if(newType.equals(BROWSE_PARAMETER_TYPE_STRING)){
				getParameterInterface().setTarget(node);
				getParameterInterface().importType();
			}
			else{
				getParameterInterface().setTarget(node);
				getParameterInterface().setType(newType);
			}

			fCellEditor.setFocus();
		}
	}


	private class ParameterNameEditingSupport extends EditingSupport {

		private TextCellEditor fNameCellEditor;

		public ParameterNameEditingSupport() {
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
			return ((AbstractParameterNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			getParameterInterface().setTarget((AbstractParameterNode)element);
			getParameterInterface().setName((String)value);
		}
	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				AbstractParameterNode addedParameter = fParentIf.addNewParameter();
				if(addedParameter != null){
					selectElement(addedParameter);
					fNameColumn.getViewer().editElement(addedParameter, 0);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not create parameter.", e.getMessage());
			}
		}
	}

	public AbstractParametersViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider,
			int STYLE) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);
		fParentIf = getParametersParentInterface();

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		addButton("New parameter", new AddNewParameterAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		fNameColumn.setEditingSupport(new ParameterNameEditingSupport());
		fTypeColumn.setEditingSupport(getParameterTypeEditingSupport());

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
		setActionProvider(new ModelViewerActionProvider(getTableViewer(), this, fileInfoProvider));
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 100, new NodeNameColumnLabelProvider());
		fTypeColumn = addColumn("Type", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((AbstractParameterNode)element).getType();
			}
		});
	}

	public void setInput(ParametersParentNode parent){
		fParentIf.setTarget(parent);
		super.setInput(parent.getParameters());
	}

	protected EditingSupport getParameterTypeEditingSupport() {
		return new ParameterTypeEditingSupport();
	}

	protected abstract ParametersParentInterface getParametersParentInterface();
	protected abstract AbstractParameterInterface getParameterInterface();
}
