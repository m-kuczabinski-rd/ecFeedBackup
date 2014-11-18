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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.testify.ecfeed.ui.modelif.ParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;
import com.testify.ecfeed.ui.modelif.ModelNodesTransfer;

public class ParametersViewer extends TableViewerSection{

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";

	private MethodNode fSelectedMethod;
	
	private TableViewerColumn fNameColumn;
	private TableViewerColumn fTypeColumn;
	private TableViewerColumn fExpectedColumn;
	private TableViewerColumn fDefaultValueColumn;

	private ParameterInterface fParameterIf;
	private MethodInterface fMethodIf;
	
	private class ParameterTypeEditingSupport extends EditingSupport {

		private ComboBoxCellEditor fCellEditor;

		public ParameterTypeEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if(fCellEditor == null){
				fCellEditor = new ComboBoxCellEditor(getTable(), ParameterInterface.supportedPrimitiveTypes());
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
			ParameterNode node = (ParameterNode)element;
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
			ParameterNode node = (ParameterNode)element;
			String newType = null;
			int index = (int)value;

			if (index >= 0) {
				newType = fCellEditor.getItems()[index];
			} else {
				newType = ((CCombo)fCellEditor.getControl()).getText();
			}
			fParameterIf.setTarget(node);
			fParameterIf.setType(newType);

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
			return ((ParameterNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			fParameterIf.setTarget((ParameterNode)element);
			fParameterIf.setName((String)value);
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
			ParameterNode node = (ParameterNode)element;
			return (node.isExpected() ? 0 : 1);
		}

		@Override
		protected void setValue(Object element, Object value) {
			ParameterNode node = (ParameterNode)element;
			boolean expected = ((int)value == 0) ? true : false;
			fParameterIf.setTarget(node);
			fParameterIf.setExpected(expected);
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
			ParameterNode parameter = (ParameterNode)element;
			ArrayList<String> expectedValues = new ArrayList<String>();
			for(String value : ParameterInterface.getSpecialValues(parameter.getType())){
				expectedValues.add(value);
			}
			if(expectedValues.contains(parameter.getDefaultValue()) == false){
				expectedValues.add(parameter.getDefaultValue());
			}
			for(ChoiceNode leaf : parameter.getLeafChoices()){
				if(!expectedValues.contains(leaf.getValueString())){
					expectedValues.add(leaf.getValueString());
				}
			}

			fComboCellEditor.setInput(expectedValues);
			fComboCellEditor.setValue(parameter.getDefaultValue());

			fParameterIf.setTarget(parameter);
			if(fParameterIf.hasLimitedValuesSet()){
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
			return (element instanceof ParameterNode && ((ParameterNode)element).isExpected());
		}

		@Override
		protected Object getValue(Object element) {
			return ((ParameterNode)element).getDefaultValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			ParameterNode parameter = (ParameterNode)element;
			String valueString = null;
			if(value instanceof String){
				valueString = (String)value;
			} else if(value == null){
				valueString = fComboCellEditor.getViewer().getCCombo().getText();
			}
			fParameterIf.setTarget(parameter);
			fParameterIf.setDefaultValue(valueString);
		}

	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ParameterNode addedParameter = fMethodIf.addNewParameter();
			if(addedParameter != null){
				selectElement(addedParameter);
				fNameColumn.getViewer().editElement(addedParameter, 0);			
			}
		}
	}

	public ParametersViewer(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, STYLE);
		fParameterIf = new ParameterInterface(this);
		fMethodIf = new MethodInterface(this);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		getSection().setText("Parameters");
		addButton("New parameter", new AddNewParameterAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this)));

		fNameColumn.setEditingSupport(new ParameterNameEditingSupport());
		fTypeColumn.setEditingSupport(new ParameterTypeEditingSupport());
		fExpectedColumn.setEditingSupport(new ExpectedValueEditingSupport());
		fDefaultValueColumn.setEditingSupport(new DefaultValueEditingSupport());

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
		setActionProvider(new ModelViewerActionProvider(getTableViewer(), this));
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
		getViewer().addDropSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDropListener(getViewer(), this));
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new NodeNameColumnLabelProvider());
		
		fTypeColumn = addColumn("Type", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ParameterNode)element).getType();
			}
		});
		
		fExpectedColumn = addColumn("Expected", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ParameterNode node = (ParameterNode)element;
				return (node.isExpected() ? "Yes" : "No");
			}
		});

		fDefaultValueColumn = addColumn("Default value", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof ParameterNode && ((ParameterNode)element).isExpected()){
					ParameterNode parameter = (ParameterNode)element;
					return parameter.getDefaultValue();
				}
				return EMPTY_STRING ;
			}
		});
	}
		
	public void setInput(MethodNode method){
		fMethodIf.setTarget(method);
		fSelectedMethod = method;
		showDefaultValueColumn(fSelectedMethod.getParametersNames(true).size() == 0);
		super.setInput(method.getParameters());
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
