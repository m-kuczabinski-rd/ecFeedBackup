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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class ParametersViewer extends TableViewerSection{

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";

	private MethodNode fSelectedMethod;
	
	private Button fMoveUpButton;

	private TableViewerColumn fNameColumn;
	private TableViewerColumn fTypeColumn;
	private TableViewerColumn fExpectedColumn;
	private TableViewerColumn fDefaultValueColumn;

	private CategoryInterface fCategoryIf;
	private MethodInterface fMethodIf;
	
	private class CategoryTypeEditingSupport extends EditingSupport {

		private ComboBoxCellEditor fCellEditor;

		public CategoryTypeEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if(fCellEditor == null){
				fCellEditor = new ComboBoxCellEditor(getTable(), CategoryInterface.supportedPrimitiveTypes());
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			}
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			CategoryNode node = (CategoryNode)element;
			String [] items = fCellEditor.getItems();
			ArrayList<String> newItems = new ArrayList<String>();

			for (int i = 0; i < items.length; ++i) {
				newItems.add(items[i]);
				if (items[i].equals(node.getType())) {
					return i;
				}
			}

			newItems.add(node.getType());
			fCellEditor.setItems(newItems.toArray(items));
			return (newItems.size() - 1);
		}

		@Override
		protected void setValue(Object element, Object value) {
			CategoryNode node = (CategoryNode)element;
			String newType = null;
			int index = (int)value;

			if (index >= 0) {
				newType = fCellEditor.getItems()[index];
			} else {
				newType = ((CCombo)fCellEditor.getControl()).getText();
			}
			fCategoryIf.setTarget(node);
			fCategoryIf.setType(newType, ParametersViewer.this);

			fCellEditor.setFocus();
		}
	}

	
	private class CategoryNameEditingSupport extends EditingSupport {

		private TextCellEditor fNameCellEditor;

		public CategoryNameEditingSupport() {
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
			return ((CategoryNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			fCategoryIf.setTarget((CategoryNode)element);
			fCategoryIf.setName((String)value, ParametersViewer.this);
		}
	}

	private class ExpectedValueEditingSupport extends EditingSupport {

		private final String[] EDITOR_ITEMS = {"Yes", "No"};
		private ComboBoxCellEditor fCellEditor;

		public ExpectedValueEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if(fCellEditor == null){
				fCellEditor = new ComboBoxCellEditor(getTable(), EDITOR_ITEMS, SWT.READ_ONLY);
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			}
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			CategoryNode node = (CategoryNode)element;
			return (node.isExpected() ? 0 : 1);
		}

		@Override
		protected void setValue(Object element, Object value) {
			CategoryNode node = (CategoryNode)element;
			boolean expected = ((int)value == 0) ? true : false;
			fCategoryIf.setTarget(node);
			fCategoryIf.setExpected(expected, ParametersViewer.this);
			fCellEditor.setFocus();
		}
	}
	
	private class DefaultValueEditingSupport extends EditingSupport {
		private ComboBoxViewerCellEditor fComboCellEditor;

		public DefaultValueEditingSupport() {
			super(getTableViewer());
			fComboCellEditor = new ComboBoxViewerCellEditor(getTable(), SWT.TRAIL);
			fComboCellEditor.setLabelProvider(new LabelProvider());
			fComboCellEditor.setContentProvider(new ArrayContentProvider());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			CategoryNode category = (CategoryNode)element;
			ArrayList<String> expectedValues = new ArrayList<String>();
			for(String value : CategoryInterface.getSpecialValues(category.getType())){
				expectedValues.add(value);
			}
			if(expectedValues.contains(category.getDefaultValue()) == false){
				expectedValues.add(category.getDefaultValue());
			}
			for(PartitionNode leaf : category.getLeafPartitions()){
				if(!expectedValues.contains(leaf.getValueString())){
					expectedValues.add(leaf.getValueString());
				}
			}

			fComboCellEditor.setInput(expectedValues);
			fComboCellEditor.setValue(category.getDefaultValue());

			fCategoryIf.setTarget(category);
			if(fCategoryIf.hasLimitedValuesSet()){
				fComboCellEditor.getViewer().getCCombo().setEditable(false);
			}
			else{
				fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION
						| ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
				fComboCellEditor.getViewer().getCCombo().setEditable(true);
			}
			return fComboCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return (element instanceof CategoryNode && ((CategoryNode)element).isExpected());
		}

		@Override
		protected Object getValue(Object element) {
			return ((CategoryNode)element).getDefaultValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			CategoryNode category = (CategoryNode)element;
			String valueString = null;
			if(value instanceof String){
				valueString = (String)value;
			} else if(value == null){
				valueString = fComboCellEditor.getViewer().getCCombo().getText();
			}
			fCategoryIf.setTarget(category);
			fCategoryIf.setDefaultValue(valueString, ParametersViewer.this);
		}

	}

	private class MoveUpDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			boolean up = (e.getSource() == fMoveUpButton);
			fCategoryIf.setTarget((CategoryNode)getSelectedElement());
			fCategoryIf.moveUpDown(up, ParametersViewer.this);
		}
	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			CategoryNode addedParameter = fMethodIf.addNewParameter(ParametersViewer.this);
			if(addedParameter != null){
				selectElement(addedParameter);
				fNameColumn.getViewer().editElement(addedParameter, 0);			
			}
		}
	}

	public ParametersViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent, parent.getOperationManager());
		fCategoryIf = new CategoryInterface();
		fMethodIf = new MethodInterface();

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		getSection().setText("Parameters");
		addButton("New parameter", new AddNewParameterAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this)));
		MoveUpDownAdapter adapter = new MoveUpDownAdapter();
		fMoveUpButton = addButton("Move Up", adapter);
		addButton("Move Down", adapter);

		fNameColumn.setEditingSupport(new CategoryNameEditingSupport());
		fTypeColumn.setEditingSupport(new CategoryTypeEditingSupport());
		fExpectedColumn.setEditingSupport(new ExpectedValueEditingSupport());
		fDefaultValueColumn.setEditingSupport(new DefaultValueEditingSupport());

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		addKeyListener(SWT.DEL, new DeleteAction(getViewer(), this));
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new NodeNameColumnLabelProvider());
		
		fTypeColumn = addColumn("Type", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
			}
		});
		
		fExpectedColumn = addColumn("Expected", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				CategoryNode node = (CategoryNode)element;
				return (node.isExpected() ? "Yes" : "No");
			}
		});

		fDefaultValueColumn = addColumn("Default value", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof CategoryNode && ((CategoryNode)element).isExpected()){
					CategoryNode category = (CategoryNode)element;
					return category.getDefaultValue();
				}
				return EMPTY_STRING ;
			}
		});
	}
		
	public void setInput(MethodNode method){
		fMethodIf.setTarget(method);
		fSelectedMethod = method;
		showDefaultValueColumn(fSelectedMethod.getCategoriesNames(true).size() == 0);
		super.setInput(method.getCategories());
	}

	private void showDefaultValueColumn(boolean show) {
		if(show){
			fDefaultValueColumn.getColumn().setWidth(0);
			fDefaultValueColumn.getColumn().setResizable(false);
		}
		else{
			fDefaultValueColumn.getColumn().setWidth(150);
			fDefaultValueColumn.getColumn().setResizable(true);
		}
	}
}
