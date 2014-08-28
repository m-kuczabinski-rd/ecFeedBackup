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

package com.testify.ecfeed.ui.common;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.TableViewerSection;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.EclipseModelBuilder;

public class DefaultValueEditingSupport extends EditingSupport {
	private final TableViewerSection fSection;
	private ComboBoxViewerCellEditor fComboCellEditor = null;
	private CategoryInterface fCategoryIf;
	

	public DefaultValueEditingSupport(TableViewerSection section, ModelOperationManager operationManager) {
		super(section.getTableViewer());
		fSection = section;
		fCategoryIf = new CategoryInterface(operationManager);

		fComboCellEditor = new ComboBoxViewerCellEditor(fSection.getTable(), SWT.TRAIL);
		fComboCellEditor.setLabelProvider(new LabelProvider());
		fComboCellEditor.setContentProvider(new ArrayContentProvider());
}
	
	@Override
	protected CellEditor getCellEditor(Object element) {
		CategoryNode category = (CategoryNode)element;
		ArrayList<String> expectedValues = new ArrayList<String>();
		for(PartitionNode node : new EclipseModelBuilder().defaultPartitions(category.getType())){
			expectedValues.add(node.getValueString());
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
		fCategoryIf.setDefaultValue(valueString, fSection, fSection.getUpdateListener());
	}

}
